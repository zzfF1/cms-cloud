/**
 * 流程类型
 */
export interface LcMainVo {
  /**
   * 流水号
   */
  serialno: number;

  /**
   * 流程类型名称
   */
  name: string;

  /**
   * 顺序号
   */
  recno: number;

  /**
   * 业务表
   */
  lcTable: string;

  /**
   * 流程字段
   */
  lcField: string;

  /**
   * 业务表主键
   */
  idField: string;

  /**
   * 业务表是不是多主键 1多主键 0单主键
   */
  mulkey: number;
}

/**
 * 流程定义接口
 */
export interface LcDefineVo {
  id: number;

  /**
   * 流程名称
   */
  name: string;

  /**
   * 顺序号
   */
  recno: number;

  /**
   * 下一个流程节点
   */
  nextId: number;

  /**
   * 流程动作
   */
  actions: LcDzVo[];

  /**
   * 流程检查
   */
  checks: LcCheckVo[];

  /**
   * 流程跳转
   */
  jumps: LcTzVo[];
}

/**
 * 流程检查接口
 */
export interface LcCheckVo {
  /**
   * 主键
   */
  serialno: number;

  /**
   * 检查说明
   */
  name: string;

  /**
   * 顺序号
   */
  recno: number;

  /**
   * 类型 0-提交进入时 1-退回时 2-提交时 3-退回进入时
   */
  type: number;

  /**
   * 检查条件
   */
  checkTj: string;

  /**
   * 检查类型
   */
  checkType: number;

  /**
   * 检查提示说明
   */
  checkMsg: string;
}

/**
 * 流程动作接口
 */
export interface LcDzVo {
  /**
   * 主键
   */
  serialno: number;

  /**
   * 检查说明
   */
  name: string;

  /**
   * 顺序号
   */
  recno: number;

  /**
   * 类型 0-提交进入时 1-退回时 2-提交时 3-退回进入时
   */
  type: number;

  /**
   * 动作条件
   */
  dz: string;

  /**
   * 动作类型
   */
  dzType: number;
}

/**
 * 流程跳转接口
 */
export interface LcTzVo {
  serialno: number;

  /**
   * 顺序号
   */
  recno: number;

  /**
   * 下一个流程节点
   */
  lcNextId: number;

  /**
   * 跳转条件
   */
  tzTj: string;

  /**
   * 跳转说明
   */
  sm: string;
}
