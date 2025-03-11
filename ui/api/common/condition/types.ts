import { FieldTypeEnum } from '@/enums/FieldTypeEnum';
import { ConditionEnum } from '@/enums/ConditionEnum';

/**
 * 自定义查询条件
 */
export interface CustomCondition {
  /**
   * 字段类型
   */
  fieldType?: FieldTypeEnum;
  /**
   * 字段前缀
   */
  fieldPrefix?: string;
  /**
   * 字段名
   */
  fieldName?: string;
  /**
   * 条件
   */
  condOperator?: ConditionEnum;
  /**
   * 别名
   */
  alias?: string;
  /**
   * 结果
   */
  val?: string;
  /**
   * 字段条件
   */
  queryOrder?: number;
  /**
   * 特殊sql代码
   */
  specialSqlCode?: string;
}

/**
 * 查询条件基类
 */
export interface ConditionBase {
  conditions?: CustomCondition[];
  pageSize?: number;
  pageNum?: number;
}
