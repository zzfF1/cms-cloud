package com.sinosoft.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sinosoft.common.core.utils.TreeBuildUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sinosoft.common.core.constant.CacheNames;
import com.sinosoft.common.core.exception.ServiceException;
import com.sinosoft.common.core.service.IBaseCommonTranslation;
import com.sinosoft.common.core.utils.StringUtils;
import com.sinosoft.common.domain.LcDefine;
import com.sinosoft.common.domain.bo.AgentQueryBo;
import com.sinosoft.common.domain.bo.BranchGroupQueryBo;
import com.sinosoft.common.domain.bo.LaComQueryBo;
import com.sinosoft.common.domain.bo.LdComQueryBo;
import com.sinosoft.common.domain.vo.LcProcessShowVo;
import com.sinosoft.common.mapper.LcDefineMapper;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.satoken.utils.LoginHelper;
import com.sinosoft.common.schema.agent.domain.Laagent;
import com.sinosoft.common.schema.agent.domain.Laagentgrade;
import com.sinosoft.common.schema.agent.mapper.LaagentMapper;
import com.sinosoft.common.schema.agent.mapper.LaagentgradeMapper;
import com.sinosoft.common.schema.commission.domain.Lmriskapp;
import com.sinosoft.common.schema.commission.mapper.LmriskappMapper;
import com.sinosoft.common.schema.common.domain.LaQualifyCode;
import com.sinosoft.common.schema.common.domain.Ldcom;
import com.sinosoft.common.schema.common.domain.SysPageConfig;
import com.sinosoft.common.schema.common.domain.vo.LabelShowVo;
import com.sinosoft.common.schema.common.domain.vo.SysPageConfigTabVo;
import com.sinosoft.common.schema.common.mapper.LaQualifyCodeMapper;
import com.sinosoft.common.schema.common.mapper.LdcomMapper;
import com.sinosoft.common.schema.inermediary.domain.Lacom;
import com.sinosoft.common.schema.inermediary.mapper.LacomMapper;
import com.sinosoft.common.schema.team.domain.Labranchgroup;
import com.sinosoft.common.schema.team.domain.vo.BranchGroupShowVo;
import com.sinosoft.common.schema.team.mapper.CommonBranchGroupMapper;
import com.sinosoft.common.schema.team.mapper.LabranchgroupMapper;
import com.sinosoft.common.service.ICmsCommonService;
import com.sinosoft.system.api.model.LoginUser;
import com.sinosoft.system.mapper.CommonMapper;
import com.sinosoft.system.service.ICommonService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: cms6
 * @description: 公共接口
 * @author: zzf
 * @create: 2023-10-02 10:17
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommonServiceImpl implements ICommonService, IBaseCommonTranslation {

    private final CommonMapper commonMapper;
    private final LdcomMapper ldComMapper;
    private final LaQualifyCodeMapper laQualifyCodeMapper;
    private final LacomMapper laComMapper;
    private final LcDefineMapper lcDefineMapper;
    private final LaagentMapper laAgentMapper;
    private final LabranchgroupMapper laBranchGroupMapper;
    private final CommonBranchGroupMapper branchGroupMapper;
    private final LaagentgradeMapper laAgentGradeMapper;
    private final LmriskappMapper lmRiskAppMapper;
    private final ICmsCommonService cmsCommonService;

    @Override
    public TableDataInfo<BranchGroupShowVo> queryBranchPageList(BranchGroupQueryBo bo, PageQuery pageQuery) {
        QueryWrapper<Labranchgroup> lqw = buildQueryWrapper(bo);
        Page<BranchGroupShowVo> pageShow = branchGroupMapper.selectBaseBranchPageShowList(pageQuery.build(), lqw);
        return TableDataInfo.build(pageShow);
    }

    private QueryWrapper<Labranchgroup> buildQueryWrapper(BranchGroupQueryBo bo) {
        QueryWrapper<Labranchgroup> lqw = Wrappers.query();
        //登录用户管理机构限制
        lqw.likeRight(ObjectUtils.isNotNull(LoginHelper.getDeptId()), "managecom", LoginHelper.getDeptId());
        lqw.likeRight(StringUtils.isNotBlank(bo.getManagecom()), "managecom", bo.getManagecom());
        //外部代码
        lqw.likeRight(StringUtils.isNotBlank(bo.getBranchattr()), "a.branchattr", bo.getBranchattr());
        //机构名称
        lqw.like(StringUtils.isNotBlank(bo.getName()), "a.name", bo.getName());
        //主管代码
        lqw.like(StringUtils.isNotBlank(bo.getBranchmanager()), "a.branchmanager", bo.getBranchmanager());
        //主管姓名
        lqw.like(StringUtils.isNotBlank(bo.getBranchmanagername()), "a.branchmanagername", bo.getBranchmanagername());
        //渠道
        lqw.eq(StringUtils.isNotBlank(bo.getBranchtype()), "branchtype", bo.getBranchtype());
        //级别
        if (StringUtils.isNotBlank(bo.getBranchlevels())) {
            lqw.in("a.branchlevel", List.of(StringUtils.split(bo.getBranchlevels(), ",")));
        }
        //停业
        lqw.eq(StringUtils.isNotBlank(bo.getEndflag()), "a.endflag", bo.getEndflag());
        //只显示有代理人的机构
        lqw.exists(StringUtils.isNotBlank(bo.getShowhaveagent()), "select 1 from laagent b where b.agentgroup = a.agentgroup and b.agentstate<'04' limit 1");
        //管理机构与外部代码排序
        lqw.orderByAsc("managecom", "branchattr");
        return lqw;
    }

    @Override
    public List<LcProcessShowVo> queryProcess(String dataId, Integer lcSerialNo) {
        return commonMapper.queryProcess(dataId, lcSerialNo);
    }

    @Override
    public List<LabelShowVo> queryComLabel(LdComQueryBo queryBo) {
        LambdaQueryWrapper<Ldcom> lqw = Wrappers.lambdaQuery();
        lqw.likeRight(StringUtils.isNotBlank(queryBo.getComCode()), Ldcom::getComcode, queryBo.getComCode());
        lqw.like(StringUtils.isNotBlank(queryBo.getName()), Ldcom::getShortname, queryBo.getName());
        lqw.in(queryBo.getComGrades() != null && !queryBo.getComGrades().isEmpty(), Ldcom::getComgrade, queryBo.getComGrades());
        lqw.likeRight(Ldcom::getComcode, queryBo.getCurUserManageCom());
        lqw.orderByAsc(Ldcom::getComcode);
        List<Ldcom> dataList = ldComMapper.selectList(lqw);
        return dataList.stream().map(ldCom -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(ldCom.getShortname());
            labelShowVo.setValue(ldCom.getComcode());
            Map<String, Object> oMap = new HashMap<>();
            oMap.put("comGrade", ldCom.getComgrade());
            labelShowVo.setOtherData(oMap);
            return labelShowVo;
        }).toList();
    }

    //资质信息下拉
    @Override
    public List<LabelShowVo> queryQualifyLabel(LaQualifyCode queryBo) {
        LambdaQueryWrapper<LaQualifyCode> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(LaQualifyCode::getQualifyCode);
        List<LaQualifyCode> dataList = laQualifyCodeMapper.selectList(lqw);
        return dataList.stream().map(ldCom -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(ldCom.getQualifyName());
            labelShowVo.setValue(ldCom.getQualifyCode());
            return labelShowVo;
        }).toList();
    }

    //中介下拉
    @Override
    public List<LabelShowVo> queryLaComLabel(LaComQueryBo queryBo) {
        LambdaQueryWrapper<Lacom> lqw = Wrappers.lambdaQuery();
        lqw.select(Lacom::getAgentcom, Lacom::getName, Lacom::getBranchtype, Lacom::getManagecom, Lacom::getActype);
        lqw.likeRight(StringUtils.isNotBlank(queryBo.getAgentCom()), Lacom::getAgentcom, queryBo.getAgentCom());
        lqw.likeRight(StringUtils.isNotBlank(queryBo.getManageCom()), Lacom::getManagecom, queryBo.getManageCom());
        lqw.likeRight(StringUtils.isNotBlank(queryBo.getBranchType()), Lacom::getBranchtype, queryBo.getBranchType());
        if (!LoginHelper.isSuperAdmin()) {
            LoginUser loginUser = LoginHelper.getLoginUser();
            lqw.likeRight(Lacom::getManagecom, Objects.requireNonNull(loginUser).getDeptId());
        }
        lqw.orderByAsc(Lacom::getAgentcom);
        List<Lacom> dataList = laComMapper.selectList(lqw);
        return dataList.stream().map(LaCom -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(LaCom.getName());
            labelShowVo.setValue(LaCom.getAgentcom());
            Map<String, Object> oMap = new HashMap<>();
            //oMap.put("comGrade", LaCom.getBankType());
            labelShowVo.setOtherData(oMap);
            return labelShowVo;
        }).toList();
    }

    @Override
    public List<LabelShowVo> queryLcLabel(Integer lcSerialno) {
        LambdaQueryWrapper<LcDefine> lqw = Wrappers.lambdaQuery();
        lqw.select(LcDefine::getId, LcDefine::getName, LcDefine::getRecno);
        lqw.eq(LcDefine::getLcSerialno, lcSerialno);
        lqw.orderByAsc(LcDefine::getRecno);
        List<LcDefine> dataList = lcDefineMapper.selectList(lqw);
        return dataList.stream().map(lcDefine -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(lcDefine.getName());
            labelShowVo.setValue(lcDefine.getId().toString());
            return labelShowVo;
        }).toList();
    }

    /**
     * 根据工号查询业务员姓名
     *
     * @param agentCode 工号
     * @return 业务员姓名
     */
    @Cacheable(cacheNames = CacheNames.AGENT_NAME, key = "#agentCode")
    @Override
    public String selectAgentNameByAgentCode(String agentCode) {
        LambdaQueryWrapper<Laagent> lqw = Wrappers.lambdaQuery();
        lqw.select(Laagent::getName);
        lqw.eq(Laagent::getAgentcode, agentCode);
        Optional<Laagent> laAgentOptional = Optional.ofNullable(laAgentMapper.selectOne(lqw));
        return laAgentOptional.map(Laagent::getName).orElse(null);
    }

    /**
     * 职级代码查询职级名称
     *
     * @param gradeCode 职级代码
     * @return 职级名称
     */
    @Cacheable(cacheNames = CacheNames.GRADE_NAME, key = "#gradeCode")
    @Override
    public String selectGradeNameByAgentCode(String gradeCode) {
        LambdaQueryWrapper<Laagentgrade> lqw = Wrappers.lambdaQuery();
        lqw.select(Laagentgrade::getGradename);
        lqw.eq(Laagentgrade::getGradecode, gradeCode);
        Optional<Laagentgrade> laAgentGradeOptional = Optional.ofNullable(laAgentGradeMapper.selectOne(lqw));
        return laAgentGradeOptional.map(Laagentgrade::getGradename).orElse(null);
    }

    @Override
    public List<LabelShowVo> queryAgentLabel(AgentQueryBo agentQuery) {
        LambdaQueryWrapper<Laagent> lqw = Wrappers.lambdaQuery();
        lqw.select(Laagent::getAgentcode, Laagent::getName);
        lqw.eq(StringUtils.isNotBlank(agentQuery.getBranchType()), Laagent::getBranchtype, agentQuery.getBranchType());
        lqw.eq(StringUtils.isNotBlank(agentQuery.getAgentState()), Laagent::getAgentstate, agentQuery.getAgentState());
        List<Laagent> dataList = laAgentMapper.selectList(lqw);
        return dataList.stream().map(laAgent -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(laAgent.getName());
            labelShowVo.setValue(laAgent.getAgentcode());
            return labelShowVo;
        }).toList();
    }

    /**
     * 销售机构ID查询代码+名称
     *
     * @param agentGroup 工号
     * @return 业务员姓名
     */
    @Cacheable(cacheNames = CacheNames.BRANCH, key = "#agentGroup")
    @Override
    public String selectBranchById(String agentGroup) {
        LambdaQueryWrapper<Labranchgroup> lqw = Wrappers.lambdaQuery();
        lqw.select(Labranchgroup::getBranchattr, Labranchgroup::getName);
        lqw.eq(Labranchgroup::getAgentgroup, agentGroup);
        Optional<Labranchgroup> branchOptional = Optional.ofNullable(laBranchGroupMapper.selectOne(lqw));
        return branchOptional.map(branchGroup -> branchGroup.getBranchattr() + "-" + branchGroup.getName()).orElse(null);
    }

    /**
     * 管理机构名称查询
     *
     * @param manageCom 管理机构ID
     * @return 管理机构名称
     */
    @Cacheable(cacheNames = CacheNames.MANAGE_NAME, key = "#manageCom")
    @Override
    public String selectManageNameById(String manageCom) {
        LambdaQueryWrapper<Ldcom> lqw = Wrappers.lambdaQuery();
        lqw.select(Ldcom::getShortname);
        lqw.eq(Ldcom::getComcode, manageCom);
        Optional<Ldcom> manageOptional = Optional.ofNullable(ldComMapper.selectOne(lqw));
        return manageOptional.map(Ldcom::getShortname).orElse(null);
    }

    /**
     * 中介销售机构
     *
     * @param agentCom 中介机构编码
     * @return 中介机构名称
     */
    @Cacheable(cacheNames = CacheNames.AGENT_COM_NAME, key = "#agentCom")
    @Override
    public String selectAgentComNameById(String agentCom) {
        LambdaQueryWrapper<Lacom> lqw = Wrappers.lambdaQuery();
        lqw.select(Lacom::getName);
        lqw.eq(Lacom::getAgentcom, agentCom);
        Optional<Lacom> manageOptional = Optional.ofNullable(laComMapper.selectOne(lqw));
        return manageOptional.map(Lacom::getName).orElse(null);
    }

    @Override
    public List<Tree<String>> selectManageTreeList(LdComQueryBo queryBo) {
        QueryWrapper<Ldcom> lqw = Wrappers.query();
        lqw.select("comcode", "upcomcode", "shortname", "name", "comgrade");
        if (!LoginHelper.isSuperAdmin()) {
            lqw.likeRight("comcode", LoginHelper.getDeptId() + "");
        }
        if (StringUtils.isNotBlank(queryBo.getLevel())) {
            lqw.apply(" length(comcode) <= " + queryBo.getLevel());
        } else {
            lqw.apply(" length(comcode) <= 6 ");
        }
        List<Ldcom> ldComList = ldComMapper.selectList(lqw);
        return buildDeptTreeSelect(ldComList);
    }

    /**
     * 险种编码下拉
     *
     * @return
     */
    @Override
    ///@DS("slave")
    public List<LabelShowVo> queryRiskCode(Lmriskapp lmriskapp) {
        QueryWrapper<Lmriskapp> queryWrapper = Wrappers.query();
        queryWrapper.eq(StringUtils.isNotBlank(lmriskapp.getRiskprop()), "riskprop", lmriskapp.getRiskprop());
        queryWrapper.orderByAsc("riskcode");
        List<Lmriskapp> dataList = lmRiskAppMapper.selectList(queryWrapper);
        return dataList.stream().map(Lmriskapp -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(Lmriskapp.getRiskname());
            labelShowVo.setValue(Lmriskapp.getRiskcode());
            return labelShowVo;
        }).collect(Collectors.toList());
    }


    @Cacheable(cacheNames = CacheNames.RISK_NAME, key = "#riskCode")
    @Override
    public String selectRiskNameById(String riskCode) {
        LambdaQueryWrapper<Lmriskapp> lqw = Wrappers.lambdaQuery();
        lqw.select(Lmriskapp::getRiskname);
        lqw.eq(Lmriskapp::getRiskcode, riskCode);
        Optional<Lmriskapp> riskOptional = Optional.ofNullable(lmRiskAppMapper.selectOne(lqw));
        return riskOptional.map(Lmriskapp::getRiskname).orElse(null);
    }


    /*职级下拉*/
    @Override
    public List<LabelShowVo> queryLaAgentGradeLabel() {
        QueryWrapper<Laagentgrade> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("gradecode", "B1").or().likeRight("grade_code", "B5");
        List<Laagentgrade> laAgentGrades = laAgentGradeMapper.selectList(queryWrapper);
        return laAgentGrades.stream().map(LaAgentGrade -> {
            LabelShowVo labelShowVo = new LabelShowVo();
            labelShowVo.setLabel(LaAgentGrade.getGradename());
            labelShowVo.setValue(LaAgentGrade.getGradecode());
            Map<String, Object> oMap = new HashMap<>();
            labelShowVo.setOtherData(oMap);
            return labelShowVo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询所选管理机构的上级管理机构
     *
     * @param queryBo
     * @return
     */
    @Override
    public List<LabelShowVo> queryUpLaComLabel(LaComQueryBo queryBo) {
        LambdaQueryWrapper<Lacom> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(queryBo.getManageCom()), Lacom::getManagecom, queryBo.getManageCom());
        lqw.eq(StringUtils.isNotBlank(queryBo.getBranchType()), Lacom::getBranchtype, queryBo.getManageCom());
        lqw.likeRight(StringUtils.isNotBlank(queryBo.getUpAgentCom()), Lacom::getUpagentcom, queryBo.getUpAgentCom());
        lqw.likeRight(StringUtils.isNotBlank(queryBo.getAgentCom()), Lacom::getAgentcom, queryBo.getAgentCom());
        if (!LoginHelper.isSuperAdmin()) {
            LoginUser loginUser = LoginHelper.getLoginUser();
            lqw.likeRight(Lacom::getManagecom, Objects.requireNonNull(loginUser).getDeptId());
        }
        lqw.orderByAsc(Lacom::getAgentcom);
        List<Lacom> dataList = laComMapper.selectList(lqw);
        List<LabelShowVo> labelShowList = new ArrayList();
        dataList.forEach(Lacom -> {
            if (StringUtils.isNotBlank(Lacom.getUpagentcom())) {
                LabelShowVo labelShowVo = new LabelShowVo();
                labelShowVo.setValue(Lacom.getUpagentcom());
                labelShowVo.setLabel(Objects.requireNonNull(laComMapper.selectById(Lacom.getUpagentcom()), "查询上级中介机构失败!").getName());
                Map<String, Object> oMap = new HashMap<>();
                labelShowVo.setOtherData(oMap);
                labelShowList.add(labelShowVo);
            }
        });
        return labelShowList;
    }

    public List<Tree<String>> buildDeptTreeSelect(List<Ldcom> ldComs) {
        if (CollUtil.isEmpty(ldComs)) {
            return CollUtil.newArrayList();
        }
        // 直接使用 TreeUtil.build 方法，传入 null 作为根节点ID
        List<Tree<String>> treeList = TreeUtil.build(ldComs, "0", TreeBuildUtils.DEFAULT_CONFIG, (com, tree) -> {
            tree.setId(com.getComcode());
            tree.setParentId(com.getUpcomcode());
            tree.setName(com.getShortname());
            tree.putExtra("manageCom", com.getComcode());
        });
        return treeList;

    }

    /**
     * 查询界面表格配置
     *
     * @param pageCode 界面编码
     */
    @Override
    public List<SysPageConfigTabVo> queryPageTableConfig(String pageCode) {
        SysPageConfig sysPageConfig = cmsCommonService.selectPageConfigByCode(pageCode);
        if (sysPageConfig == null) {
            log.warn("[{}]界面配置不存在", pageCode);
            throw new ServiceException("[" + pageCode + "]界面配置不存在");
        }
        return cmsCommonService.selectPageConfigTabByPageId(sysPageConfig.getId());
    }
}
