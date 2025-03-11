/**
 * 销售机构查询条件
 */
export interface BranchQuery extends PageQuery {
  /**
   * 机构名称
   */
  name?: string;
  /**
   * 机构代码
   */
  branchattr?: string;
  /**
   * 管理机构
   */
  managecom?: string;
  /**
   * 渠道
   */
  branchtype?: string;
  /**
   * 级别 ,分割
   */
  branchlevels?: string;
  /**
   * 停业
   */
  endflag?: string;
  /**
   * 主管代码
   */
  branchmanager?: string;
  /**
   * 主管姓名
   */
  branchmanagername?: string;
  /**
   * 是否显示有人的机构
   */
  showhaveagent?: boolean;
}

/**
 * 部门类型
 */
export interface BaseBranchShowVO extends BaseEntity {
  /**
   * 展业机构代码
   */
  agentgroup: string;
  /**
   * 展业机构名称
   */
  name: string;
  /**
   * 管理机构
   */
  managecom: string;
  /**
   * 管理机构名称
   */
  managecomname: string;
  /**
   * 展业机构外部编码
   */
  branchattr: string;
  /**
   * 展业类型
   */
  branchtype: string;
  /**
   * 展业机构级别
   */
  branchlevel: string;
  /**
   * 展业机构级别名称
   */
  branchlevelname: string;
  /**
   * 主管代码
   */
  branchmanager: string;
  /**
   * 主管名称
   */
  branchmanagername: string;
  /**
   * 成立日期
   */
  founddate: string;
  /**
   * 停业日期
   */
  enddate: string;
  /**
   * 停业
   */
  endflag: string;
  /**
   * 状态名称
   */
  endflagname: string;
}
