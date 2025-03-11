// src/api/dashboard.ts
import request from '@/utils/request';
import {
  NotificationParams,
  ApiResponse,
  Notification,
  PersonalData,
  CalendarEvent,
  ShortcutItem,
  DashboardComponent,
  CommissionResult,
  PageData
} from '@/api/system/dashboard/types';
import { AxiosPromise } from 'axios';

// 是否使用测试数据
let useTestData = true;

/**
 * 设置是否使用测试数据
 * @param value true=使用测试数据, false=调用真实接口
 */
export function setUseTestData(value: boolean): void {
  useTestData = value;
}

/**
 * 获取当前使用的数据模式
 * @returns {boolean} true=使用测试数据, false=调用真实接口
 */
export function isUsingTestData(): boolean {
  return useTestData;
}

// 测试数据
const testData = {
  // 通知列表测试数据
  notifications: {
    code: 200,
    message: '操作成功',
    data: {
      records: [
        {
          id: 1001,
          title: '张先生的重疾险保单即将到期',
          description: '需要在3天内联系客户确认是否续保',
          time: '10分钟前',
          category: 'business',
          priority: 'urgent',
          date: '2023-03-05',
          read: false
        },
        {
          id: 1002,
          title: '您有3位客户本周需要回访',
          description: '王女士、李先生和赵先生的季度回访计划',
          time: '30分钟前',
          category: 'business',
          priority: 'important',
          date: '2023-03-05',
          read: false
        },
        {
          id: 1003,
          title: '产品培训：新增智慧人生2025寿险产品',
          description: '明天下午2点将举行线上产品培训会议',
          time: '1小时前',
          category: 'team',
          priority: 'normal',
          date: '2023-03-05',
          read: true
        },
        {
          id: 1004,
          title: '完成月度销售报表提交',
          description: '请在本周五前完成本月销售数据汇总',
          time: '昨天 15:30',
          category: 'system',
          priority: 'important',
          date: '2023-03-04',
          read: true
        },
        {
          id: 1005,
          title: '客户刘先生提交的理赔申请已通过初审',
          description: '请联系客户补充剩余材料',
          time: '昨天 10:15',
          category: 'business',
          priority: 'normal',
          date: '2023-03-04',
          read: true
        }
      ],
      total: 15,
      size: 10,
      current: 1,
      pages: 2
    }
  },

  // 个人数据概览测试数据
  personalData: {
    code: 200,
    message: '操作成功',
    data: {
      workload: { today: 12, todayTrend: 8.3, week: 58, weekTrend: 3.5, month: 235, monthTrend: -2.1 },
      sales: { today: 45800, todayTrend: 15.2, week: 198600, weekTrend: 5.8, month: 856000, monthTrend: 12.5 },
      performance: { rank: 3, percentage: 92.5, target: 85 },
      statistics: { newCustomers: 8, policies: 12, renewals: 5, claims: 2 }
    }
  },

  // 日历事件测试数据
  calendarEvents: {
    code: 200,
    message: '操作成功',
    data: [
      {
        id: 1,
        title: '客户回访 - 张先生',
        date: '2023-03-05',
        time: '10:30',
        type: 'client',
        notes: '讨论年金险续保事宜',
        color: '#409EFF',
        isAllDay: false
      },
      {
        id: 2,
        title: '产品培训',
        date: '2023-03-05',
        time: '14:00',
        type: 'training',
        notes: '2025智慧人生产品介绍培训',
        color: '#67C23A',
        isAllDay: false
      },
      { id: 3, title: '团队周会', date: '2023-03-06', time: '09:30', type: 'meeting', notes: '销售部门周例会', color: '#E6A23C', isAllDay: false },
      {
        id: 4,
        title: '新客户洽谈 - 李女士',
        date: '2023-03-07',
        time: '15:00',
        type: 'client',
        notes: '重疾险产品推荐',
        color: '#409EFF',
        isAllDay: false
      },
      {
        id: 5,
        title: '月度总结会议',
        date: '2023-03-10',
        time: '10:00',
        type: 'meeting',
        notes: '回顾2月销售业绩',
        color: '#E6A23C',
        isAllDay: false
      }
    ]
  },

  // 快捷入口测试数据
  shortcuts: {
    code: 200,
    message: '操作成功',
    data: [
      { id: 'P1001', label: '新建保单', icon: 'el-icon-plus', iconColor: 'blue', route: 'new-policy' },
      { id: 'P1002', label: '客户管理', icon: 'el-icon-user', iconColor: 'green', route: 'customer' },
      { id: 'P1003', label: '日程安排', icon: 'el-icon-date', iconColor: 'orange', route: 'schedule' },
      { id: 'P1004', label: '销售报表', icon: 'el-icon-document', iconColor: 'gray', route: 'report' }
    ]
  },

  // 仪表板组件测试数据
  dashboardComponents: {
    code: 200,
    message: '操作成功',
    data: [
      { id: 'notification', name: '通知', description: '显示业务通知、系统提醒和团队活动' },
      { id: 'shortcuts', name: '快捷入口', description: '快速访问常用功能' },
      { id: 'data-overview', name: '个人数据概览', description: '显示工作量和销售数据统计' },
      { id: 'calendar', name: '日历', description: '显示日程安排' }
    ]
  },

  // 佣金数据测试数据
  commissionData: {
    code: 200,
    message: '操作成功',
    data: {
      wageList: [
        {
          policyId: 'P2023030001',
          policyNumber: 'LI-2023-0012345',
          productName: '智盈人生终身寿险',
          customerName: '张三',
          premium: 12500.0,
          paymentPeriod: '20年',
          commission: 2625.0,
          commissionRate: 21,
          settleDate: '2023-03-15',
          status: '已结算'
        },
        {
          policyId: 'P2023030002',
          policyNumber: 'HI-2023-0023456',
          productName: '安心百万医疗险',
          customerName: '李四',
          premium: 3600.0,
          paymentPeriod: '1年',
          commission: 648.0,
          commissionRate: 18,
          settleDate: '2023-03-15',
          status: '已结算'
        },
        {
          policyId: 'P2023030003',
          policyNumber: 'CI-2023-0034567',
          productName: '超级玛丽重疾险',
          customerName: '王五',
          premium: 8800.0,
          paymentPeriod: '30年',
          commission: 2112.0,
          commissionRate: 24,
          settleDate: '2023-03-20',
          status: '待结算'
        }
      ],
      summary: {
        totalCommission: 5385.0,
        settledCommission: 3273.0,
        pendingCommission: 2112.0,
        policyCount: 3,
        avgCommissionRate: 21
      }
    }
  }
};

/**
 * 获取首页通知列表
 * @param params 查询参数
 */
export const getNotifications = (params?: NotificationParams): Promise<ApiResponse<Notification>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve(testData.notifications);
  } else {
    // 调用真实接口
    return request({
      url: '/notification/list',
      method: 'get',
      params
    });
  }
};

/**
 * 标记通知为已读
 * @param id 通知ID
 */
export const markNotificationRead = (id: number): Promise<ApiResponse<boolean>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve({
      code: 200,
      message: '操作成功',
      data: true
    });
  } else {
    // 调用真实接口
    return request({
      url: `/notification/read/${id}`,
      method: 'put'
    });
  }
};

/**
 * 获取个人数据概览
 */
export const getPersonalDataOverview = (): Promise<ApiResponse<PersonalData>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve(testData.personalData);
  } else {
    // 调用真实接口
    return request({
      url: '/dashboard/personal-data',
      method: 'get'
    });
  }
};

/**
 * 获取日历日程列表
 * @param params 查询参数
 */
export const getCalendarEvents = (params?: any): Promise<ApiResponse<CalendarEvent[]>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve(testData.calendarEvents);
  } else {
    // 调用真实接口
    return request({
      url: '/calendar/events',
      method: 'get',
      params
    });
  }
};

/**
 * 添加日历日程
 * @param data 日程数据
 */
export const addCalendarEvent = (data: Partial<CalendarEvent>): Promise<ApiResponse<number>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve({
      code: 200,
      message: '操作成功',
      data: 11 // 新创建的事件ID
    });
  } else {
    // 调用真实接口
    return request({
      url: '/calendar/events',
      method: 'post',
      data
    });
  }
};

/**
 * 更新日历日程
 * @param id 日程ID
 * @param data 日程数据
 */
export const updateCalendarEvent = (id: number, data: Partial<CalendarEvent>): Promise<ApiResponse<boolean>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve({
      code: 200,
      message: '操作成功',
      data: true
    });
  } else {
    // 调用真实接口
    return request({
      url: `/calendar/events/${id}`,
      method: 'put',
      data
    });
  }
};

/**
 * 删除日历日程
 * @param id 日程ID
 */
export const deleteCalendarEvent = (id: number): Promise<ApiResponse<boolean>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve({
      code: 200,
      message: '操作成功',
      data: true
    });
  } else {
    // 调用真实接口
    return request({
      url: `/calendar/events/${id}`,
      method: 'delete'
    });
  }
};

/**
 * 获取快捷入口配置
 */
export const getShortcuts = (): Promise<ApiResponse<ShortcutItem[]>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve(testData.shortcuts);
  } else {
    // 调用真实接口
    return request({
      url: '/dashboard/shortcuts',
      method: 'get'
    });
  }
};

/**
 * 保存快捷入口配置
 * @param data 快捷入口数据
 */
export const saveShortcuts = (data: ShortcutItem[]): Promise<ApiResponse<boolean>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve({
      code: 200,
      message: '操作成功',
      data: true
    });
  } else {
    // 调用真实接口
    return request({
      url: '/dashboard/shortcuts',
      method: 'post',
      data
    });
  }
};

/**
 * 保存仪表板组件配置
 * @param data 组件配置数据
 */
export const saveDashboardComponents = (data: DashboardComponent[]): Promise<ApiResponse<boolean>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve({
      code: 200,
      message: '操作成功',
      data: true
    });
  } else {
    // 调用真实接口
    return request({
      url: '/dashboard/components',
      method: 'post',
      data
    });
  }
};

/**
 * 获取仪表板组件配置
 */
export const getDashboardComponents = (): Promise<ApiResponse<DashboardComponent[]>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve(testData.dashboardComponents);
  } else {
    // 调用真实接口
    return request({
      url: '/dashboard/components',
      method: 'get'
    });
  }
};

/**
 * 佣金计算
 * @param data 计算对象
 */
export const calculateBkWage = (data: any): Promise<ApiResponse<CommissionResult>> => {
  if (useTestData) {
    // 返回测试数据
    return Promise.resolve(testData.commissionData);
  } else {
    // 调用真实接口
    return request({
      url: '/business-bank/bkcommission/wageList',
      method: 'post',
      data
    });
  }
};
