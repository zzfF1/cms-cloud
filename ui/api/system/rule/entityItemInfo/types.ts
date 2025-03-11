export interface EntityItemInfoVO {
  /**
   * 主键
   */
  itemId: string | number;

  /**
   * 实体id
   */
  entityId: string | number;

  /**
   * 字段名称
   */
  itemName: string;

  /**
   * 字段标识
   */
  itemIdentify: string | number;

  /**
   * 属性描述
   */
  itemDesc: string;

  /**
   * 是否有效
   */
  isEffect: number;

  /**
   * 备注
   */
  remark: string;

}

export interface EntityItemInfoForm extends BaseEntity {
  /**
   * 主键
   */
  itemId?: string | number;

  /**
   * 实体id
   */
  entityId?: string | number;

  /**
   * 字段名称
   */
  itemName?: string;

  /**
   * 字段标识
   */
  itemIdentify?: string | number;

  /**
   * 属性描述
   */
  itemDesc?: string;

  /**
   * 是否有效
   */
  isEffect?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface EntityItemInfoQuery extends PageQuery {

  /**
   * 实体id
   */
  entityId?: string | number;

  /**
   * 字段名称
   */
  itemName?: string;

  /**
   * 字段标识
   */
  itemIdentify?: string | number;

  /**
   * 属性描述
   */
  itemDesc?: string;

  /**
   * 是否有效
   */
  isEffect?: number;

  /**
   * 日期范围参数
   */
  params?: any;
}



