import request from '@/utils/request';
import { NotificationParams } from './types';
import { AxiosPromise } from 'axios';
// 查询公告列表
/**
 * 获取通知列表
 * @param params 查询参数
 */
export function getNotifications(params: NotificationParams): AxiosPromise<any> {
  return request({
    url: '/system/notice/notifications',
    method: 'get',
    params
  });
}

/**
 * 获取未读通知统计
 */
export function getUnreadCount(): AxiosPromise<any> {
  return request({
    url: '/system/notice/unread/count',
    method: 'get'
  });
}

/**
 * 标记通知为已读
 * @param notificationId 通知ID
 */
export function markNotificationRead(notificationId: number | string): AxiosPromise<any> {
  return request({
    url: `/system/notice/read/${notificationId}`,
    method: 'put'
  });
}

/**
 * 批量标记通知为已读
 * @param notificationIds 通知ID数组
 */
export function batchMarkRead(notificationIds: (number | string)[]): AxiosPromise<any> {
  return request({
    url: '/system/notice/read/batch',
    method: 'put',
    data: { notificationIds }
  });
}

/**
 * 标记所有通知为已读
 * @param type 通知类型（可选）
 */
export function markAllRead(type?: string): AxiosPromise<any> {
  return request({
    url: '/system/notice/read/all',
    method: 'put',
    params: { type }
  });
}

/**
 * 获取用户待办任务列表
 */
export function getTodoList(): AxiosPromise<any> {
  return request({
    url: '/system/notice/todo/list',
    method: 'get'
  });
}

/**
 * 获取用户通知设置
 */
export function getNotificationSettings(): AxiosPromise<any> {
  return request({
    url: '/system/notice/settings',
    method: 'get'
  });
}

/**
 * 更新用户通知设置
 * @param settings 设置信息
 */
export function updateNotificationSettings(settings: any): AxiosPromise<any> {
  return request({
    url: '/system/notice/settings',
    method: 'put',
    data: settings
  });
}
