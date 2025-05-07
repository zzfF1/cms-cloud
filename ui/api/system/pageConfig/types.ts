export interface PageConfigVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 代码
   */
  code: string;

  /**
   * 名称
   */
  name: string;

  /**
   * 渠道
   */
  branchType: string;

  /**
   * 排序
   */
  sort: number;

  /**
   * 说明
   */
  remark: string;

  /**
   * 涉及数据表
   */
  involveTab: string;

  /**
   * 查询条件列表
   */
  queryList?: PageConfigQueryVO[];

  /**
   * 表格列表
   */
  tableList?: PageConfigTabVO[];
}

export interface PageConfigForm {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 代码
   */
  code?: string;

  /**
   * 名称
   */
  name?: string;

  /**
   * 渠道
   */
  branchType?: string;

  /**
   * 排序
   */
  sort?: number;

  /**
   * 说明
   */
  remark?: string;

  /**
   * 涉及数据表
   */
  involveTab?: string;

  /**
   * 查询条件列表
   */
  queryList?: PageConfigQueryForm[];

  /**
   * 表格列表
   */
  tableList?: PageConfigTabForm[];
}

export interface PageConfigQuery extends PageQuery {
  /**
   * 代码
   */
  code?: string;

  /**
   * 名称
   */
  name?: string;

  /**
   * 渠道
   */
  branchType?: string;
}

export interface PageConfigQueryVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 配置id
   */
  pageId: string | number;

  /**
   * 类型
   */
  fieldType: string;

  /**
   * 字段前缀
   */
  fieldPrefix: string;

  /**
   * 别名
   */
  alias: string;

  /**
   * 字段名
   */
  fieldName: string;

  /**
   * 条件
   */
  condOperator: string;

  /**
   * 排序字段
   */
  queryOrder: number;

  /**
   * 特殊代码
   */
  specialCode: string;

  /**
   * 说明
   */
  remark: string;

  /**
   * 类型 0:前台查询条件 1:后台追加条件 2:排序字段 3:高级查询
   */
  type: string;

  /**
   * 默认值
   */
  defaultValue: string;

  /**
   * 组件类型
   */
  componentType: string;

  /**
   * 字典类型编码
   */
  dictType: string;

  /**
   * 数据源类型
   */
  dataSource: string;

  /**
   * Bean
   */
  beanName: string;

  /**
   * 依赖字段
   */
  dependencyField: string;

  /**
   * 选项配置
   */
  optionsConfig: string;

  /**
   * 占位提示文字
   */
  placeholder: string;
}

export interface PageConfigQueryForm {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 配置id
   */
  pageId?: string | number;

  /**
   * 类型
   */
  fieldType?: string;

  /**
   * 字段前缀
   */
  fieldPrefix?: string;

  /**
   * 别名
   */
  alias?: string;

  /**
   * 字段名
   */
  fieldName?: string;

  /**
   * 条件
   */
  condOperator?: string;

  /**
   * 排序字段
   */
  queryOrder?: number;

  /**
   * 特殊代码
   */
  specialCode?: string;

  /**
   * 说明
   */
  remark?: string;

  /**
   * 类型 0:前台查询条件 1:后台追加条件 2:排序字段 3:高级查询
   */
  type?: string;

  /**
   * 默认值
   */
  defaultValue?: string;

  /**
   * 组件类型
   */
  componentType?: string;

  /**
   * 字典类型编码
   */
  dictType?: string;

  /**
   * 数据源类型
   */
  dataSource?: string;

  /**
   * Bean
   */
  beanName?: string;

  /**
   * 依赖字段
   */
  dependencyField?: string;

  /**
   * 选项配置
   */
  optionsConfig?: string;

  /**
   * 占位提示文字
   */
  placeholder?: string;
}

export interface PageConfigTabVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 配置id
   */
  pageId: string | number;

  /**
   * 标题
   */
  label: string;

  /**
   * 字段名称
   */
  prop: string;

  /**
   * 列宽
   */
  width: string;

  /**
   * 排序
   */
  sort: number;
}

export interface PageConfigTabForm {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 配置id
   */
  pageId?: string | number;

  /**
   * 标题
   */
  label?: string;

  /**
   * 字段名称
   */
  prop?: string;

  /**
   * 列宽
   */
  width?: string;

  /**
   * 排序
   */
  sort?: number;
}
