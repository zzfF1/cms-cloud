package com.sinosoft.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.system.domain.bo.SysDeptBo;
import com.sinosoft.system.domain.bo.SysDeptLdcomBo;
import com.sinosoft.system.domain.bo.SysDeptQuery;
import com.sinosoft.system.domain.vo.SysDeptLdcomVo;
import com.sinosoft.system.domain.vo.SysDeptVo;

import java.util.List;

/**
 * 部门管理 服务层
 *
 * @author zzf
 */
public interface ISysDeptService {

    /**
     * 分页查询部门管理数据
     *
     * @param dept      部门信息
     * @param pageQuery 分页对象
     * @return 部门信息集合
     */
    TableDataInfo<SysDeptVo> selectPageDeptList(SysDeptQuery dept, PageQuery pageQuery);

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDeptVo> selectDeptList(SysDeptQuery dept);

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    List<Tree<Long>> selectDeptTreeList(SysDeptQuery dept);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    List<Tree<Long>> buildDeptTreeSelect(List<SysDeptVo> depts);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDeptVo selectDeptById(Long deptId);

    /**
     * 通过部门ID串查询部门
     *
     * @param deptIds 部门id串
     * @return 部门列表信息
     */
    List<SysDeptVo> selectDeptByIds(List<Long> deptIds);

    /**
     * 通过部门ID查询部门名称
     *
     * @param deptIds 部门ID串逗号分隔
     * @return 部门名称串逗号分隔
     */
    String selectDeptNameByIds(String deptIds);

    /**
     * 根据ID查询所有子部门数（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    long selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在部门子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    boolean checkDeptNameUnique(SysDeptBo dept);


    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    void checkDeptDataScope(Long deptId);

    /**
     * 新增保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    int insertDept(SysDeptBo bo);

    /**
     * 修改保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    int updateDept(SysDeptBo bo);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    int deleteDeptById(Long deptId);

    /**
     * 查询部门(简单查询)
     *
     * @return 部门列表
     */
    List<SysDeptVo> selectDeptsSimple();

    /**
     * 分页查询部门管理数据（集成Ldcom）
     *
     * @param dept 部门信息
     * @param pageQuery 分页对象
     * @return 部门信息集合
     */
    TableDataInfo<SysDeptLdcomVo> selectPageDeptLdcomList(SysDeptQuery dept, PageQuery pageQuery);

    /**
     * 查询部门管理数据（集成Ldcom）
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDeptLdcomVo> selectDeptLdcomList(SysDeptQuery dept);

    /**
     * 根据部门ID查询信息（集成Ldcom）
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDeptLdcomVo selectDeptLdcomById(Long deptId);

    /**
     * 新增保存部门信息（集成Ldcom）
     *
     * @param bo 部门信息
     * @return 结果
     */
    int insertDeptLdcom(SysDeptLdcomBo bo);

    /**
     * 修改保存部门信息（集成Ldcom）
     *
     * @param bo 部门信息
     * @return 结果
     */
    int updateDeptLdcom(SysDeptLdcomBo bo);
}
