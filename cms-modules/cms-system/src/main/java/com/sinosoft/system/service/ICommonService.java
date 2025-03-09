package com.sinosoft.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.sinosoft.common.domain.bo.AgentQueryBo;
import com.sinosoft.common.domain.bo.BranchGroupQueryBo;
import com.sinosoft.common.domain.bo.LaComQueryBo;
import com.sinosoft.common.domain.bo.LdComQueryBo;
import com.sinosoft.common.domain.vo.LcProcessShowVo;
import com.sinosoft.common.mybatis.core.page.PageQuery;
import com.sinosoft.common.mybatis.core.page.TableDataInfo;
import com.sinosoft.common.schema.commission.domain.Lmriskapp;
import com.sinosoft.common.schema.common.domain.LaQualifyCode;
import com.sinosoft.common.schema.common.domain.vo.LabelShowVo;
import com.sinosoft.common.schema.common.domain.vo.SysPageConfigTabVo;
import com.sinosoft.common.schema.team.domain.vo.BranchGroupShowVo;

import java.util.List;

/**
 * 公共接口
 */
public interface ICommonService {
    /**
     * 查询流程轨迹
     *
     * @param dataId     业务数据id
     * @param lcSerialNo 流程类型
     * @return 流程轨迹
     */
    List<LcProcessShowVo> queryProcess(String dataId, Integer lcSerialNo);

    /**
     * 查询管理机构
     *
     * @param queryBo 查询条件
     * @return 管理机构
     */
    List<LabelShowVo> queryComLabel(LdComQueryBo queryBo);


    /**
     * 查询资质
     *
     * @param queryBo 查询条件
     * @return 资质
     */
    List<LabelShowVo> queryQualifyLabel(LaQualifyCode queryBo);

    /**
     * 查询管理机构
     *
     * @param queryBo 查询条件
     * @return 中介机构
     */
    List<LabelShowVo> queryLaComLabel(LaComQueryBo queryBo);

    /**
     * 查询流程集合
     *
     * @param lcSerialno 流程类型
     * @return 管理机构
     */
    List<LabelShowVo> queryLcLabel(Integer lcSerialno);

    /**
     * 人员查询集合
     *
     * @param agentQuery 查询条件
     * @return 人员集合
     */
    List<LabelShowVo> queryAgentLabel(AgentQueryBo agentQuery);

    /**
     * 查询销售机构管理分页列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页对象
     * @return 销售机构管理分页数据
     */
    TableDataInfo<BranchGroupShowVo> queryBranchPageList(BranchGroupQueryBo bo, PageQuery pageQuery);

    /**
     * 查询管理机构树
     *
     * @param queryBo 查询条件
     * @return 管理机构树
     */
    List<Tree<String>> selectManageTreeList(LdComQueryBo queryBo);

    /**
     * 查询险种编码
     *
     * @return 险种编码树
     */
    List<LabelShowVo> queryRiskCode(Lmriskapp lmriskapp);

    /**
     * 查询职级
     */
    List<LabelShowVo> queryLaAgentGradeLabel();

    /**
     * 查询所选管理机构的上级管理机构
     *
     * @param bo 查询条件
     * @return 管理机构
     */
    List<LabelShowVo> queryUpLaComLabel(LaComQueryBo bo);

    /**
     * 查询页面表格配置
     *
     * @param pageCode 页面编码
     * @return 表格配置
     */
    List<SysPageConfigTabVo> queryPageTableConfig(String pageCode);
}
