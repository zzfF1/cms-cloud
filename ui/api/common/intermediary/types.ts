/**
 * 代理机构查询
 */
export interface AgentComQuery extends PageQuery {
  /**
   * 机构名称
   */
  name?: string;
  /**
   * 机构代码
   */
  agentcom?: string;
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
  banktype?: string;
  /**
   * 停业
   */
  endflag?: string;
}

/**
 * 部门类型
 */
export interface BaseAgentComShowVO extends BaseEntity {
  /**
   * 展业机构代码
   */
  agentcom: string;
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
}
