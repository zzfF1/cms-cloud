// 导入模板类型定义

export interface ImportConfigVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 代码
   */
  code: string;

  /**
   * 模板名称
   */
  name: string;

  /**
   * 标题名称（多个sheet标题使用,分割）
   */
  titleNames: string;

  /**
   * Sheet名称（多个sheet使用,分割）
   */
  sheetNames: string;

  /**
   * 渠道
   */
  branchType: string;

  /**
   * 说明
   */
  remark: string;

  /**
   * 创建人
   */
  operator: string;

  /**
   * 创建日期
   */
  makedate: string;

  /**
   * 创建时间
   */
  maketime: string;

  /**
   * 修改人
   */
  modifyoperator: string;

  /**
   * 修改日期
   */
  modifydate: string;

  /**
   * 修改时间
   */
  modifytime: string;

  /**
   * 配置项列表
   */
  configItems?: ImportConfigItemVO[];
}

export interface ImportConfigItemVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 配置id
   */
  configId: string | number;

  /**
   * sheet索引
   */
  sheetIndex: number;

  /**
   * 标题
   */
  title: string;

  /**
   * 字段必录 0-否 1-是
   */
  fieldRequired: number;

  /**
   * 字段类型 0-字符串,1-数字(整形),2-数字(2位),3-数字(4位),4-日期,9-序号
   */
  dataType: string;

  /**
   * 填写说明
   */
  fillSm: string;

  /**
   * 列宽
   */
  width: number;

  /**
   * 下拉处理类
   */
  downSelHandler: string;

  /**
   * 参数
   */
  parameter: string;

  /**
   * 排序
   */
  sort: number;

  /**
   * 备注
   */
  remark: string;
}

export interface ImportConfigForm {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 代码
   */
  code?: string;

  /**
   * 模板名称
   */
  name?: string;

  /**
   * 渠道
   */
  branchType?: string;

  /**
   * 说明
   */
  remark?: string;
}

export interface ImportConfigQuery extends PageQuery {
  /**
   * 代码
   */
  code?: string;

  /**
   * 模板名称
   */
  name?: string;

  /**
   * 渠道
   */
  branchType?: string;
}

// 定义Sheet配置接口，用于模板配置
export interface SheetConfig {
  /**
   * Sheet名称
   */
  name: string;

  /**
   * 字段配置列表
   */
  fields: ImportConfigItemVO[];
}

// 通用分页查询接口
export interface PageQuery {
  pageNum: number;
  pageSize: number;
}
