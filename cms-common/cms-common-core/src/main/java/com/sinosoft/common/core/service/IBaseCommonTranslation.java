package com.sinosoft.common.core.service;

/**
 * 通用翻译实现
 *
 * @author zzf
 */
public interface IBaseCommonTranslation {


    /**
     * 工号查询姓名
     *
     * @param agentCode 工号
     * @return 姓名
     */
    String selectAgentNameByAgentCode(String agentCode);

    /**
     * 职级名称查询
     *
     * @param gradeCode 工号
     * @return 姓名
     */
    String selectGradeNameByAgentCode(String gradeCode);


    /**
     * 销售机构ID查询代码+名称
     *
     * @param agentGroup 销售机构ID
     * @return 代码+名称
     */
    String selectBranchById(String agentGroup);

    /**
     * 管理机构名称查询
     *
     * @param manageCom 管理机构ID
     * @return 管理机构名称
     */
    String selectManageNameById(String manageCom);

    /**
     * 中介机构名称查询
     *
     * @param agentCom 中介机构代码
     * @return 中介机构名称
     */
    String selectAgentComNameById(String agentCom);

    /**
     * 产品名称查询
     * @param riskCode 产品代码
     * @return 产品名称
     */
    String selectRiskNameById(String riskCode);

}
