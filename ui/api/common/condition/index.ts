import { ConditionBase, CustomCondition } from '@/api/common/condition/types';
import { FieldTypeEnum } from '@/enums/FieldTypeEnum';
import { ConditionEnum } from '@/enums/ConditionEnum';

/**
 * 添加动态条件
 * @param fieldType 字段类型
 * @param fieldPrefix 字段前缀
 * @param fieldName 字段名
 * @param condOperator 字段条件
 * @param vals 结果
 * @param queryOrder 查询顺序
 * @param specialSqlCode 特殊sql代码
 */
export function addCondition(
  fieldType: FieldTypeEnum,
  fieldPrefix: string,
  fieldName: string,
  condOperator: ConditionEnum,
  alias: string,
  vals: string | string[],
  queryOrder: number,
  specialSqlCode: string
): CustomCondition {
  // 如果 val 是 string[]，则转换为逗号分隔的字符串
  const value = Array.isArray(vals) ? vals.join(',') : vals;
  if (value) {
    return {
      fieldType: fieldType,
      fieldPrefix: fieldPrefix,
      fieldName: fieldName,
      condOperator: condOperator,
      alias: alias,
      val: value,
      queryOrder: queryOrder,
      specialSqlCode: specialSqlCode
    };
  } else {
    return null;
  }
}

/**
 * 添加动态条件
 * @param alias 别名
 * @param val 结果
 */
export function addConditionA(alias: string, val: string): CustomCondition {
  return addCondition(FieldTypeEnum.STR, '', '', undefined, alias, val, 1, '');
}

/**
 * 添加动态条件
 * @param alias 别名
 * @param val 结果
 */
export function addConditionB(alias: string, val: string[]): CustomCondition {
  return addCondition(FieldTypeEnum.STR, '', '', undefined, alias, val, 1, '');
}

/**
 * 添加动态条件
 * @param fieldPrefix 字段前缀
 * @param fieldName 字段名
 * @param condOperator 字段条件
 * @param val 结果
 * @param queryOrder 查询顺序
 */
export function addCondition1(fieldPrefix: string, fieldName: string, condOperator: ConditionEnum, val: string, queryOrder: number): CustomCondition {
  return addCondition(FieldTypeEnum.STR, fieldPrefix, fieldName, condOperator, '', val, queryOrder, '');
}

/**
 * 添加动态条件
 * @param fieldName 字段名
 * @param condOperator 字段条件
 * @param val 结果
 * @param queryOrder 查询顺序
 */
export function addCondition2(fieldName: string, condOperator: ConditionEnum, val: string, queryOrder: number): CustomCondition {
  return addCondition(FieldTypeEnum.STR, '', fieldName, condOperator, '', val, queryOrder, '');
}

/**
 * 添加动态条件
 * @param fieldPrefix 字段前缀
 * @param fieldName 字段名
 * @param condOperator 字段条件
 * @param val 结果
 * @param queryOrder 查询顺序
 */
export function addCondition3(
  fieldPrefix: string,
  fieldName: string,
  condOperator: ConditionEnum,
  val: string[],
  queryOrder: number
): CustomCondition {
  return addCondition(FieldTypeEnum.STR, fieldPrefix, fieldName, condOperator, '', val, queryOrder, '');
}

/**
 *
 * @param condition 结果
 * @param conditions 集合
 */
export function addToConditions(condition: any, conditions: any) {
  if (condition) {
    conditions.push(condition);
  }
}

/**
 * 将参数转换为 ConditionBase 格式
 * @param params 查询参数对象
 * @returns ConditionBase
 */
export function transformToConditionBase(params: Record<string, any>): ConditionBase {
  const conditions: CustomCondition[] = Object.keys(params)
    .filter((key) => {
      const value = params[key];
      // 排除无效值：undefined, null, 空字符串，以及空数组
      if (Array.isArray(value)) {
        return value.length > 0; // 保留非空数组
      }
      return value !== undefined && value !== null && value !== '' && key !== 'pageNum' && key !== 'pageSize';
    })
    .map((key) => {
      const value = params[key];
      return {
        alias: key,
        val: Array.isArray(value) ? value.join(',') : value // 数组转换为逗号分隔字符串
      };
    });

  return {
    conditions,
    pageNum: params.pageNum,
    pageSize: params.pageSize
  };
}
