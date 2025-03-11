export interface EntityInfoVO {
  /**
   * 主键
   */
  entityId: string | number;

  /**
   * 名称
   */
  entityName: string;

  /**
   * 描述
   */
  entityDesc: string;

  /**
   * 标识
   */
  entityIdentify: string | number;

  /**
   * 包路径
   */
  pkgName: string;

  /**
   * 是否有效(1是0否)
   */
  isEffect: number;

  /**
   * 备注
   */
  remark: string;
}

export interface EntityInfoForm extends BaseEntity {
  /**
   * 主键
   */
  entityId?: string | number;

  /**
   * 名称
   */
  entityName?: string;

  /**
   * 描述
   */
  entityDesc?: string;

  /**
   * 标识
   */
  entityIdentify?: string | number;

  /**
   * 包路径
   */
  pkgName?: string;

  /**
   * 是否有效(1是0否)
   */
  isEffect?: number;

  /**
   * 备注
   */
  remark?: string;
}

export interface EntityInfoQuery extends PageQuery {
  /**
   * 名称
   */
  entityName?: string;

  /**
   * 描述
   */
  entityDesc?: string;

  /**
   * 标识
   */
  entityIdentify?: string | number;

  /**
   * 包路径
   */
  pkgName?: string;

  /**
   * 是否有效(1是0否)
   */
  isEffect?: number;

  /**
   * 日期范围参数
   */
  params?: any;
}
