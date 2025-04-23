// 导出配置类型定义

export interface ExportConfigVO {
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
   * 文件名称
   */
  filename?: string;

  /**
   * 路径
   */
  path?: string;

  /**
   * 类型 0-生成方式 1-模板方式
   */
  type: string;

  /**
   * 渠道
   */
  branchType: string;

  /**
   * 创建人
   */
  operator?: string;

  /**
   * 创建日期
   */
  makedate?: string;

  /**
   * 创建时间
   */
  maketime?: string;

  /**
   * 修改人
   */
  modifyoperator?: string;

  /**
   * 修改日期
   */
  modifydate?: string;

  /**
   * 修改时间
   */
  modifytime?: string;
}

export interface ExportConfigForm {
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
   * 文件名称
   */
  filename?: string;

  /**
   * 路径
   */
  path?: string;

  /**
   * 类型 0-生成方式 1-模板方式
   */
  type?: string;

  /**
   * 渠道
   */
  branchType?: string;
}

export interface ExportConfigQuery extends PageQuery {
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
   * 类型
   */
  type?: string;
}

// Sheet配置接口
export interface SheetConfig {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 配置id
   */
  configId?: string | number;

  /**
   * sheet索引
   */
  sheetIndex?: number;

  /**
   * 标题名称
   */
  titleName: string;

  /**
   * sheet名称
   */
  sheetName: string;

  /**
   * 起始行
   */
  beginRow?: string;

  /**
   * 起始列
   */
  beginCol?: string;

  /**
   * 查询sql字段
   */
  sqlField: string;

  /**
   * sql条件
   */
  sqlConditions: string;

  /**
   * 汇总字段
   */
  sqlGroup?: string;

  /**
   * 排序字段
   */
  sqlOrder?: string;

  /**
   * 字段配置列表
   */
  items?: ExportConfigItemVO[];
}

export interface ExportConfigItemVO {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * sheet索引
   */
  sheetId?: string | number;

  /**
   * 配置id
   */
  configId?: string | number;

  /**
   * 字段
   */
  field: string;

  /**
   * 名称
   */
  name: string;

  /**
   * 字段类型 0-字符串,1-数字(整形),2-数字(2位),3-数字(4位),4-日期,9-序号
   */
  type: number;

  /**
   * 显示长度
   */
  dispLength: number;

  /**
   * 格式
   */
  format?: string;

  /**
   * 排序
   */
  sort: number;
}

// 通用分页查询接口
export interface PageQuery {
  pageNum: number;
  pageSize: number;
}

// 导出配置提交数据结构
export interface ExportConfigSubmitData {
  /**
   * 基本信息
   */
  basicInfo: ExportConfigForm;

  /**
   * Sheet配置信息
   */
  sheetInfo: SheetConfig[];
}

// 预览数据类型
export type PreviewData = Array<Array<Record<string, any>>>;
