package com.sinosoft.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.core.utils.*;
import com.sinosoft.common.schema.common.domain.Ldcom;
import com.sinosoft.common.schema.common.mapper.LdcomMapper;
import com.sinosoft.system.domain.bo.SysDeptLdcomBo;
import com.sinosoft.system.domain.bo.SysDeptQuery;
import com.sinosoft.system.domain.vo.SysDeptLdcomVo;
import lombok.RequiredArgsConstructor;
import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.constant.SystemConstants;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.mybatis.helper.DataBaseHelper;
import com.sinosoft.common.redis.utils.CacheUtils;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.system.domain.SysDept;
import com.sinosoft.system.domain.SysRole;
import com.sinosoft.system.domain.SysUser;
import com.sinosoft.system.domain.bo.SysDeptBo;
import com.sinosoft.system.domain.vo.SysDeptVo;
import com.sinosoft.system.mapper.SysDeptMapper;
import com.sinosoft.system.mapper.SysRoleMapper;
import com.sinosoft.system.mapper.SysUserMapper;
import com.sinosoft.system.service.ISysDeptService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 部门管理 服务实现
 *
 * @author zzf
 */
@RequiredArgsConstructor
@Service
public class SysDeptServiceImpl implements ISysDeptService {

    private final SysDeptMapper baseMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;
    private final LdcomMapper ldcomMapper; // 新增LdcomMapper


    /**
     * 分页查询部门管理数据
     *
     * @param dept      部门信息
     * @param pageQuery 分页对象
     * @return 部门信息集合
     */
    @Override
    public TableDataInfo<SysDeptVo> selectPageDeptList(SysDeptQuery dept, PageQuery pageQuery) {
        Page<SysDeptVo> page = baseMapper.selectPageDeptList(pageQuery.build(), buildQueryWrapper(dept));
        return TableDataInfo.build(page);
    }

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<SysDeptVo> selectDeptList(SysDeptQuery dept) {
        LambdaQueryWrapper<SysDept> lqw = buildQueryWrapper(dept);
        return baseMapper.selectDeptList(lqw);
    }

    /**
     * 查询部门树结构信息
     *
     * @param bo 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<Tree<Long>> selectDeptTreeList(SysDeptQuery bo) {
        LambdaQueryWrapper<SysDept> lqw = buildQueryWrapper(bo);
        List<SysDeptVo> depts = baseMapper.selectDeptList(lqw);
        return buildDeptTreeSelect(depts);
    }

    private LambdaQueryWrapper<SysDept> buildQueryWrapper(SysDeptQuery bo) {
        LambdaQueryWrapper<SysDept> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysDept::getDelFlag, SystemConstants.NORMAL);
        lqw.eq(ObjectUtil.isNotNull(bo.getDeptId()), SysDept::getDeptId, bo.getDeptId());
        lqw.eq(ObjectUtil.isNotNull(bo.getParentId()), SysDept::getParentId, bo.getParentId());
        lqw.like(StringUtils.isNotBlank(bo.getDeptName()), SysDept::getDeptName, bo.getDeptName());
        lqw.like(StringUtils.isNotBlank(bo.getDeptCategory()), SysDept::getDeptCategory, bo.getDeptCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysDept::getStatus, bo.getStatus());
        lqw.orderByAsc(SysDept::getAncestors);
        lqw.orderByAsc(SysDept::getParentId);
        lqw.orderByAsc(SysDept::getOrderNum);
        lqw.orderByAsc(SysDept::getDeptId);
        if (ObjectUtil.isNotNull(bo.getBelongDeptId())) {
            //部门树搜索
            lqw.and(x -> {
                Long parentId = bo.getBelongDeptId();
                List<SysDept> deptList = baseMapper.selectListByParentId(parentId);
                List<Long> deptIds = StreamUtils.toList(deptList, SysDept::getDeptId);
                deptIds.add(parentId);
                x.in(SysDept::getDeptId, deptIds);
            });
        }
        return lqw;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<Tree<Long>> buildDeptTreeSelect(List<SysDeptVo> depts) {
        if (CollUtil.isEmpty(depts)) {
            return CollUtil.newArrayList();
        }
        // 获取当前列表中每一个节点的parentId，然后在列表中查找是否有id与其parentId对应，若无对应，则表明此时节点列表中，该节点在当前列表中属于顶级节点
        List<Tree<Long>> treeList = CollUtil.newArrayList();
        for (SysDeptVo d : depts) {
            Long parentId = d.getParentId();
            SysDeptVo sysDeptVo = depts.stream().filter(it -> it.getDeptId().longValue() == parentId).findFirst().orElse(null);
            if (sysDeptVo == null) {
                List<Tree<Long>> trees = TreeBuildUtils.build(depts, parentId, (dept, tree) -> tree.setId(dept.getDeptId()).setParentId(dept.getParentId()).setName(dept.getDeptName()).setWeight(dept.getOrderNum()).putExtra("disabled", SystemConstants.DISABLE.equals(dept.getStatus())));
                Tree<Long> tree = StreamUtils.findFirst(trees, it -> it.getId().longValue() == d.getDeptId());
                treeList.add(tree);
            }
        }
        return treeList;
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        return baseMapper.selectDeptListByRoleId(roleId, role.getDeptCheckStrictly());
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Cacheable(cacheNames = CacheNames.SYS_DEPT, key = "#deptId")
    @Override
    public SysDeptVo selectDeptById(Long deptId) {
        SysDeptVo dept = baseMapper.selectVoById(deptId);
        if (ObjectUtil.isNull(dept)) {
            return null;
        }
        SysDeptVo parentDept = baseMapper.selectVoOne(new LambdaQueryWrapper<SysDept>().select(SysDept::getDeptName).eq(SysDept::getDeptId, dept.getParentId()));
        dept.setParentName(ObjectUtils.notNullGetter(parentDept, SysDeptVo::getDeptName));
        return dept;
    }

    /**
     * 通过部门ID串查询部门
     *
     * @param deptIds 部门id串
     * @return 部门列表信息
     */
    @Override
    public List<SysDeptVo> selectDeptByIds(List<Long> deptIds) {
        return baseMapper.selectDeptList(new LambdaQueryWrapper<SysDept>().select(SysDept::getDeptId, SysDept::getDeptName, SysDept::getLeader).eq(SysDept::getStatus, SystemConstants.NORMAL).in(CollUtil.isNotEmpty(deptIds), SysDept::getDeptId, deptIds));
    }

    /**
     * 通过部门ID查询部门名称
     *
     * @param deptIds 部门ID串逗号分隔
     * @return 部门名称串逗号分隔
     */
    @Override
    public String selectDeptNameByIds(String deptIds) {
        List<String> list = new ArrayList<>();
        for (Long id : StringUtils.splitTo(deptIds, Convert::toLong)) {
            SysDeptVo vo = SpringUtils.getAopProxy(this).selectDeptById(id);
            if (ObjectUtil.isNotNull(vo)) {
                list.add(vo.getDeptName());
            }
        }
        return String.join(StringUtils.SEPARATOR, list);
    }

    /**
     * 根据ID查询所有子部门数（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public long selectNormalChildrenDeptById(Long deptId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysDept>().eq(SysDept::getStatus, SystemConstants.NORMAL).apply(DataBaseHelper.findInSet(deptId, "ancestors")));
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        return baseMapper.exists(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, deptId));
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        return userMapper.exists(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, deptId));
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDeptBo dept) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeptName, dept.getDeptName()).eq(SysDept::getParentId, dept.getParentId()).ne(ObjectUtil.isNotNull(dept.getDeptId()), SysDept::getDeptId, dept.getDeptId()));
        return !exist;
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId) {
        if (ObjectUtil.isNull(deptId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        if (baseMapper.countDeptById(deptId) == 0) {
            throw new ServiceException("没有权限访问部门数据！");
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    @CacheEvict(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, allEntries = true)
    @Override
    public int insertDept(SysDeptBo bo) {
        SysDept info = baseMapper.selectById(bo.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!SystemConstants.NORMAL.equals(info.getStatus())) {
            throw new ServiceException("部门停用，不允许新增");
        }
        SysDept dept = MapstructUtils.convert(bo, SysDept.class);
        dept.setAncestors(info.getAncestors() + StringUtils.SEPARATOR + dept.getParentId());
        return baseMapper.insert(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    @Caching(evict = {@CacheEvict(cacheNames = CacheNames.SYS_DEPT, key = "#bo.deptId"), @CacheEvict(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, allEntries = true)})
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDept(SysDeptBo bo) {
        SysDept dept = MapstructUtils.convert(bo, SysDept.class);
        SysDept oldDept = baseMapper.selectById(dept.getDeptId());
        if (!oldDept.getParentId().equals(dept.getParentId())) {
            // 如果是新父部门 则校验是否具有新父部门权限 避免越权
            this.checkDeptDataScope(dept.getParentId());
            SysDept newParentDept = baseMapper.selectById(dept.getParentId());
            if (ObjectUtil.isNotNull(newParentDept) && ObjectUtil.isNotNull(oldDept)) {
                String newAncestors = newParentDept.getAncestors() + StringUtils.SEPARATOR + newParentDept.getDeptId();
                String oldAncestors = oldDept.getAncestors();
                dept.setAncestors(newAncestors);
                updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
            }
        } else {
            dept.setAncestors(oldDept.getAncestors());
        }
        int result = baseMapper.updateById(dept);
        // 如果部门状态为启用，且部门祖级列表不为空，且部门祖级列表不等于根部门祖级列表（如果部门祖级列表不等于根部门祖级列表，则说明存在上级部门）
        if (SystemConstants.NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors()) && !StringUtils.equals(SystemConstants.ROOT_DEPT_ANCESTORS, dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        baseMapper.update(null, new LambdaUpdateWrapper<SysDept>().set(SysDept::getStatus, SystemConstants.NORMAL).in(SysDept::getDeptId, Arrays.asList(deptIds)));
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = baseMapper.selectList(new LambdaQueryWrapper<SysDept>().apply(DataBaseHelper.findInSet(deptId, "ancestors")));
        List<SysDept> list = new ArrayList<>();
        for (SysDept child : children) {
            SysDept dept = new SysDept();
            dept.setDeptId(child.getDeptId());
            dept.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            list.add(dept);
        }
        if (CollUtil.isNotEmpty(list)) {
            if (baseMapper.updateBatchById(list)) {
                list.forEach(dept -> CacheUtils.evict(CacheNames.SYS_DEPT, dept.getDeptId()));
            }
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Caching(evict = {@CacheEvict(cacheNames = CacheNames.SYS_DEPT, key = "#deptId"), @CacheEvict(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, key = "#deptId")})
    @Override
    public int deleteDeptById(Long deptId) {
        return baseMapper.deleteById(deptId);
    }

    /**
     * 查询部门(简单查询)
     *
     * @return 部门列表
     */
    @Override
    public List<SysDeptVo> selectDeptsSimple() {
        return baseMapper.selectDeptList(new LambdaQueryWrapper<SysDept>().select(SysDept::getDeptId, SysDept::getDeptName, SysDept::getParentId).eq(SysDept::getStatus, SystemConstants.NORMAL));
    }

    /**
     * 分页查询部门管理数据（集成Ldcom）
     *
     * @param dept      部门信息
     * @param pageQuery 分页对象
     * @return 部门信息集合
     */
    @Override
    public TableDataInfo<SysDeptLdcomVo> selectPageDeptLdcomList(SysDeptQuery dept, PageQuery pageQuery) {
        Page<SysDeptLdcomVo> page = baseMapper.selectPageDeptLdcomList(pageQuery.build(), buildLdcomQueryWrapper(dept));
        return TableDataInfo.build(page);
    }

    /**
     * 查询部门管理数据（集成Ldcom）
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<SysDeptLdcomVo> selectDeptLdcomList(SysDeptQuery dept) {
        return baseMapper.selectDeptLdcomList(buildLdcomQueryWrapper(dept));
    }

    /**
     * 构建集成Ldcom的查询条件
     */
    private LambdaQueryWrapper<SysDept> buildLdcomQueryWrapper(SysDeptQuery bo) {
        LambdaQueryWrapper<SysDept> lqw = buildQueryWrapper(bo);
        return lqw;
    }

    /**
     * 根据部门ID查询信息（集成Ldcom）
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDeptLdcomVo selectDeptLdcomById(Long deptId) {
        // 查询部门基础信息
        SysDeptVo deptVo = this.selectDeptById(deptId);
        if (deptVo == null) {
            return null;
        }

        // 转换为集成Ldcom的视图对象
        SysDeptLdcomVo deptLdcomVo = MapstructUtils.convert(deptVo, SysDeptLdcomVo.class);

        // 查询关联的Ldcom信息
        if (StringUtils.isNotEmpty(deptVo.getManageCom())) {
            Ldcom ldcom = ldcomMapper.selectById(deptVo.getManageCom());
            if (ldcom != null) {
                // 填充Ldcom信息
                deptLdcomVo.setOutcomcode(ldcom.getOutcomcode());
                deptLdcomVo.setName(ldcom.getName());
                deptLdcomVo.setShortname(ldcom.getShortname());
                deptLdcomVo.setAddress(ldcom.getAddress());
                deptLdcomVo.setZipcode(ldcom.getZipcode());
                deptLdcomVo.setComPhone(ldcom.getPhone());
                deptLdcomVo.setFax(ldcom.getFax());
                deptLdcomVo.setComgrade(ldcom.getComgrade());
                deptLdcomVo.setRegionalismcode(ldcom.getRegionalismcode());
                deptLdcomVo.setUpcomcode(ldcom.getUpcomcode());
            }
        }

        return deptLdcomVo;
    }

    /**
     * 新增保存部门信息（集成Ldcom）
     *
     * @param bo 部门信息
     * @return 结果
     */
    @CacheEvict(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDeptLdcom(SysDeptLdcomBo bo) {
        // 保存部门基础信息
        SysDeptBo deptBo = MapstructUtils.convert(bo, SysDeptBo.class);
        int result = this.insertDept(deptBo);

        // 保存Ldcom信息
        if (result > 0 && StringUtils.isNotEmpty(bo.getManageCom())) {
            saveLdcomInfo(bo);
        }

        return result;
    }

    /**
     * 修改保存部门信息（集成Ldcom）
     *
     * @param bo 部门信息
     * @return 结果
     */
    @Caching(evict = {@CacheEvict(cacheNames = CacheNames.SYS_DEPT, key = "#bo.deptId"), @CacheEvict(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, allEntries = true)})
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDeptLdcom(SysDeptLdcomBo bo) {
        // 更新部门基础信息
        SysDeptBo deptBo = MapstructUtils.convert(bo, SysDeptBo.class);
        int result = this.updateDept(deptBo);

        // 更新Ldcom信息
        if (result > 0 && StringUtils.isNotEmpty(bo.getManageCom())) {
            saveLdcomInfo(bo);
        }

        return result;
    }

    /**
     * 保存或更新Ldcom信息
     */
    private void saveLdcomInfo(SysDeptLdcomBo bo) {
        // 查询是否已存在Ldcom记录
        Ldcom ldcom = ldcomMapper.selectById(bo.getManageCom());

        if (ldcom == null) {
            // 不存在则新增
            ldcom = new Ldcom();
            ldcom.setComcode(bo.getManageCom());
            setLdcomFields(ldcom, bo);
            ldcomMapper.insert(ldcom);
        } else {
            // 存在则更新
            setLdcomFields(ldcom, bo);
            ldcomMapper.updateById(ldcom);
        }
    }

    /**
     * 设置Ldcom字段值
     */
    private void setLdcomFields(Ldcom ldcom, SysDeptLdcomBo bo) {
        ldcom.setOutcomcode(bo.getOutcomcode());
        ldcom.setName(StringUtils.isNotEmpty(bo.getName()) ? bo.getName() : bo.getDeptName());
        ldcom.setShortname(bo.getShortname());
        ldcom.setAddress(bo.getAddress());
        ldcom.setZipcode(bo.getZipcode());
        ldcom.setPhone(bo.getComPhone());
        ldcom.setFax(bo.getFax());
        ldcom.setComgrade(bo.getComgrade());
        ldcom.setRegionalismcode(bo.getRegionalismcode());
        ldcom.setUpcomcode(bo.getUpcomcode());

        // 设置父部门对应的上级机构代码
        if (ObjectUtil.isNotNull(bo.getParentId()) && !bo.getParentId().equals(0L)) {
            SysDeptVo parentDept = this.selectDeptById(bo.getParentId());
            if (parentDept != null) {
                ldcom.setUpcomcode(parentDept.getManageCom());
            }
        }
    }
}
