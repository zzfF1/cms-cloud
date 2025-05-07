export interface LawVersionVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 渠道
   */
  branchType: string;

  /**
   * 佣金类型
   */
  indexCalType: string;

  /**
   * 启用状态
   */
  status: number;

  /**
   * 计算人员实现类
   */
  calculateClass: string;

  /**
   * 计算完成处理队列
   */
  handleQueue: string;
}

export interface LawVersionForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 渠道
   */
  branchType?: string;

  /**
   * 佣金类型
   */
  indexCalType?: string;

  /**
   * 启用状态
   */
  status?: number;

  /**
   * 计算人员实现类
   */
  calculateClass?: string;

  /**
   * 计算完成处理队列
   */
  handleQueue?: string;
}

export interface LawVersionQuery extends PageQuery {
  /**
   * 渠道
   */
  branchType?: string;

  /**
   * 佣金类型
   */
  indexCalType?: string;

  /**
   * 启用状态
   */
  status?: number;

  /**
   * 计算人员实现类
   */
  calculateClass?: string;

  /**
   * 计算完成处理队列
   */
  handleQueue?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}

/**
 * 佣金计算定义
 */
export interface WageCalDefineVo {
  /**
   * 主键
   */
  seriesNo?: string;
  /**
   *计算代码
   */
  calCode?: string;
  /**
   *佣金项名称
   */
  calName?: string;
  /**
   *数据存储字段
   */
  tableColname?: string;
  /**
   *计算分组名称
   */
  calGroupName?: string;
  /**
   *计算过程
   */
  calProcessElem?: string;
  /**
   *数据类型
   */
  dataType?: string;
  /**
   *计算类型
   */
  wageType?: string;
  /**
   * 计算频率
   */
  calPeriod?: string;
  /**
   * 特殊频率参数
   */
  calElements?: string;
  /**
   *计算排序字段
   */
  calOrder?: string;
  /**
   *输出佣金单
   */
  outExcel?: string;
  /**
   *佣金单排序
   */
  outOrder?: string;
  /**
   *计算处理类
   */
  handlerClass?: string;
  /**
   *构造参数
   */
  constructionParm?: string;
  /**
   *说明
   */
  remark?: string;
}

export interface BaseLawVerFormVo {
  /**
   * 主键
   */
  id?: string | number;
  /**
   * 基本法名称
   */
  name: string;
  /**
   * 渠道
   */
  branchType: string;
  /**
   * 佣金类型
   */
  indexCalType: string;
}

export interface WageCalDefinitionVo {
  /**
   * 序列号
   */
  seriesNo: string | number;
  /**
   * 计算代码
   */
  calCode: string;
  /**
   * 佣金项说明
   */
  calName: string;
  /**
   * 佣金结果列
   */
  tableColname: string;
  /**
   * 计算分组名称 多个佣金项 保存一个过程id 佣金单使用该名称展示title
   */
  calGroupName: string;
  /**
   * 计算过程存储元素 ,号分割多个过程id
   */
  calProcessElem: string;
  /**
   * 0 字符串 1数字 2比例
   */
  dataType: string | number;
  /**
   * 佣金类型 0佣金计算 1合并计税 wagetypeenum
   */
  wageType: string;
  /**
   * 计算频率
   */
  calPeriod: string;
  /**
   * 特殊频率参数
   */
  calElements: string;
  /**
   * 排序字段
   */
  calOrder: string | number;
  /**
   * 是否输出excel 佣金单展示
   */
  outExcel: number;
  /**
   * 佣金单排序字段
   */
  outOrder: number;
  /**
   * 计算处理类
   */
  handlerClass: string;
  /**
   * 实现类的构造参数,多个参数用,号分割,string类型参数
   */
  constructionParm: string;
  /**
   * 规则说明
   */
  remark: string;
}

export interface WageCalEleVo {
  /**
   * 主键
   */
  seriesNo: string | number;
  /**
   * 计算元素id
   */
  calElements: string;
  /**
   * 存储序号
   */
  rowIndex: number;
  /**
   * 标题
   */
  title: string;
  /**
   * 列宽度
   */
  colWidth: number;
  /**
   * 0 字符串 1数字 2比例
   */
  dataType: number;
  /**
   * 排序字段
   */
  elemOrder: number;
}

export interface WageCalGradeRelationVo {
  /**
   * 主键
   */
  seriesNo: string | number;
  /**
   * 职级
   */
  agentGrade: string;
  /**
   * 计算代码
   */
  calCode: string;
}

export interface BaseLawVerDetailsVo {
  lawVerFormVo?: BaseLawVerFormVo;
  calDefinList?: Array<WageCalDefinitionVo>;
  elementsList?: Array<WageCalEleVo>;
  gradeRelationVoList?: Array<WageCalGradeRelationVo>;
}
