import { defineStore } from 'pinia';
import { getNotifications, getUnreadCount, markNotificationRead, batchMarkRead, markAllRead, getTodoList } from '@/api/system/notification';
import useUserStore from './user';
import { ElMessage } from 'element-plus';

interface NoticeState {
  notices: any[];
  todos: any[];
  unreadCount: {
    total: number;
    todo: number;
    alert: number;
    message: number;
    announcement: number;
  };
  loading: boolean;
  defaultActiveTab: string;
}

const useNoticeStore = defineStore('notice', {
  state: (): NoticeState => ({
    notices: [],
    todos: [],
    unreadCount: {
      total: 0,
      todo: 0,
      alert: 0,
      message: 0,
      announcement: 0
    },
    loading: false,
    defaultActiveTab: 'all'
  }),

  actions: {
    // 获取通知列表
    async getNotifications(params: any = {}) {
      try {
        this.loading = true;
        const userStore = useUserStore();
        const userId = userStore.userId;

        const mergedParams = {
          userId,
          pageNum: 1,
          pageSize: 20,
          ...params
        };

        const { data } = await getNotifications(mergedParams);

        if (data && data.code === 200) {
          this.notices = data.rows || [];
          return data;
        } else {
          console.error('获取通知失败:', data?.msg || '未知错误');
          return null;
        }
      } catch (error) {
        console.error('获取通知异常:', error);
        return null;
      } finally {
        this.loading = false;
      }
    },

    // 获取未读通知数量
    async getUnreadCount() {
      try {
        const { data } = await getUnreadCount();
        if (data && data.code === 200) {
          this.unreadCount = data.data || {
            total: 0,
            todo: 0,
            alert: 0,
            message: 0,
            announcement: 0
          };
          return data.data;
        } else {
          console.error('获取未读通知数量失败:', data?.msg || '未知错误');
          return null;
        }
      } catch (error) {
        console.error('获取未读通知数量异常:', error);
        return null;
      }
    },

    // 获取待办任务列表
    async getTodoList() {
      try {
        const { data } = await getTodoList();
        if (data && data.code === 200) {
          this.todos = data.data || [];
          return data.data;
        } else {
          console.error('获取待办任务失败:', data?.msg || '未知错误');
          return [];
        }
      } catch (error) {
        console.error('获取待办任务异常:', error);
        return [];
      }
    },

    // 标记通知为已读
    async markAsRead(notificationId: number | string) {
      try {
        const { data } = await markNotificationRead(notificationId);
        if (data && data.code === 200) {
          // 更新本地通知状态
          const notice = this.notices.find((n) => n.notificationId === notificationId);
          if (notice) {
            notice.read = true;
            notice.readTime = new Date().toISOString();
          }
          // 更新未读计数
          this.getUnreadCount();
          return true;
        } else {
          console.error('标记通知已读失败:', data?.msg || '未知错误');
          return false;
        }
      } catch (error) {
        console.error('标记通知已读异常:', error);
        return false;
      }
    },

    // 批量标记通知为已读
    async batchMarkAsRead(notificationIds: (number | string)[]) {
      if (!notificationIds || notificationIds.length === 0) return false;

      try {
        const { data } = await batchMarkRead(notificationIds);
        if (data && data.code === 200) {
          // 更新本地通知状态
          notificationIds.forEach((id) => {
            const notice = this.notices.find((n) => n.notificationId === id);
            if (notice) {
              notice.read = true;
              notice.readTime = new Date().toISOString();
            }
          });
          // 更新未读计数
          this.getUnreadCount();
          return true;
        } else {
          console.error('批量标记通知已读失败:', data?.msg || '未知错误');
          return false;
        }
      } catch (error) {
        console.error('批量标记通知已读异常:', error);
        return false;
      }
    },

    // 标记所有通知为已读
    async markAllAsRead(type?: string) {
      try {
        const { data } = await markAllRead(type);
        if (data && data.code === 200) {
          // 更新本地通知状态
          this.notices.forEach((notice) => {
            if (!type || notice.type === type) {
              notice.read = true;
              notice.readTime = new Date().toISOString();
            }
          });
          // 更新未读计数
          this.getUnreadCount();
          ElMessage.success('已全部标为已读');
          return true;
        } else {
          console.error('标记全部已读失败:', data?.msg || '未知错误');
          return false;
        }
      } catch (error) {
        console.error('标记全部已读异常:', error);
        return false;
      }
    },

    // 设置默认激活的标签
    setDefaultActiveTab(tab: string) {
      this.defaultActiveTab = tab;
    },

    // 刷新通知列表
    async refreshNotifications(params: any = {}) {
      await this.getNotifications(params);
      await this.getUnreadCount();
      return this.notices;
    }
  }
});

export default useNoticeStore;
