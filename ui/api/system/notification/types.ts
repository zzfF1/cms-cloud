/**
 * 通知类型定义
 */
export interface NotificationParams {
  userId?: number;
  type?: string; // 通知类型：todo, alert, message, announcement
  isRead?: boolean; // 是否已读
  startTime?: string; // 开始时间
  endTime?: string; // 结束时间
  keyword?: string; // 关键词搜索
  page?: number; // 页码
  size?: number; // 每页大小
}

/**
 * 通知对象定义
 */
export interface Notification {
  id: number | string;
  notificationId: number;
  type: string; // 通知类型：todo, alert, message, announcement
  title: string; // 标题
  content: string; // 内容
  sourceType?: string; // 来源类型
  sourceId?: string; // 来源ID
  priority: string; // 优先级：high, medium, low
  read: boolean; // 是否已读
  readTime?: string; // 阅读时间
  createTime: string; // 创建时间
  expirationDate?: string; // 过期时间
  businessData?: string; // 业务数据，JSON格式
  actions?: string; // 可执行操作，JSON格式
  attachments?: string; // 附件信息，JSON格式
  category?: string; // 前端分类：business, system, team
}

/**
 * 待办任务定义
 */
export interface TodoTask {
  count: number; // 待办数量
  ruleId: number; // 规则ID
  ruleName: string; // 规则名称
  menuId: number; // 菜单ID
  menuUrl: string; // 菜单URL
  templateId: number; // 模板ID
  templateCode: string; // 模板代码
  templateName: string; // 模板名称
  createTime: string; // 创建时间
}

/**
 * 通知设置定义
 */
export interface NotificationSetting {
  todoNotifySystem: string; // 待办-系统通知
  todoNotifySms: string; // 待办-短信通知
  todoNotifyEmail: string; // 待办-邮件通知
  alertNotifySystem: string; // 预警-系统通知
  alertNotifySms: string; // 预警-短信通知
  alertNotifyEmail: string; // 预警-邮件通知
  announceNotifySystem: string; // 公告-系统通知
  announceNotifyEmail: string; // 公告-邮件通知
  doNotDisturbStart: string; // 免打扰开始时间
  doNotDisturbEnd: string; // 免打扰结束时间
}
