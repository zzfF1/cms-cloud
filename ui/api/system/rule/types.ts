export interface ElMainVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 类型
   */
  type: number;

  /**
   * 计算代码
   */
  calCode: string;

  /**
   * 说明
   */
  message: string;

  /**
   * 版本号
   */
  calVersion: number;

  /**
   * 创建时间
   */
  createDate: string;
}

export interface ElMainForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 类型
   */
  type?: number;

  /**
   * 计算代码
   */
  calCode?: string;

  /**
   * 说明
   */
  message?: string;

  /**
   * 版本号
   */
  calVersion?: number;

  /**
   * 创建时间
   */
  createDate?: string;
}

export interface ElMainQuery extends PageQuery {
  /**
   * 类型
   */
  type?: number;

  /**
   * 计算代码
   */
  calCode?: string;

  /**
   * 说明
   */
  message?: string;
}
