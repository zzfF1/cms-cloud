/**
 * 管理机构查询
 */
export interface LdComQueryBo {
  /**
   * 机构编码
   */
  comCode?: string;
  /**
   * 名称
   */
  name?: string;
  /**
   * 机构级别
   */
  comGrades?: string[];
  /**
   * 显示级别
   */
  level?: string;
}

export interface LabelShowVo {
  /**
   * 标签
   */
  label?: string;
  /**
   * 值
   */
  value?: string;
  /**
   * 其他值
   */
  otherData?: object;
}

export interface HelpDocShowVo {
  /**
   * 标题
   */
  title: string;
  /**
   * 内容
   */
  content: string;
}

/**
 * 管理机构
 */
export interface ManageComVo {
  id: number | string;
  parentId: number | string;
  children: ManageComVo[];
  label: string;
  manageCom: string;
}

/**
 * 流程查询
 */
export interface LcProcessShowVo {
  /**
   * 最后标志
   */
  lastFlag: number;
  /**
   * 操作类型
   */
  czType: number;
  /**
   * 流程值
   */
  lcName: string;
  /**
   * 操作人
   */
  operator: number;
  /**
   * 姓名
   */
  operatorName: string;
  /**
   * 操作时间
   */
  makeDate: Date;
  /**
   * 意见
   */
  yj: string;
}

/**
 * 自定义列对象
 */
export interface SysPageConfigTabVo {
  label: string;
  prop: string;
  align?: string;
  width?: string;
  fixed?: boolean | string;
  type?: string;
  slot?: string;
  code?: string;
  name?: string;
  tooltip?: string;
  formatter?: (row: any) => string;
}

/**
 * 附件材料
 */
export interface AttachFileVo {
  /**
   * 主键
   */
  uid?: number;
  /**
   * 业务类型
   */
  busCode?: string;
  /**
   * 业务类型
   */
  busDataType?: string;
  /**
   * 附件ID
   */
  attId?: number;
  /**
   * 业务数据ID
   */
  busDataId?: string;
  /**
   * 文件名称
   */
  originalName?: string;
  /**
   * 文件路径
   */
  url?: string;
  /**
   * 文件
   */
  file?: File;
  /**
   * 创建人
   */
  operator?: string;
  /**
   * 创建日期
   */
  makedate?: string;
  /**
   * 是否删除
   */
  markedForDeletion?: boolean;
  /**
   * 是否修改
   */
  markedForUpdate?: boolean;
  /**
   * 是否新增
   */
  markedForAdd?: boolean;
}
