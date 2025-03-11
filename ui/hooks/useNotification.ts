import { computed, onMounted, ref } from 'vue';
import useNoticeStore from '@/store/modules/notice';
import { ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';

/**
 * 通知钩子函数
 * 提供通知相关功能的统一接口
 */
export default function useNotification() {
  const noticeStore = useNoticeStore();
  const router = useRouter();

  // 加载状态
  const loading = ref(false);

  // 获取未读通知统计
  const unreadCount = computed(() => {
    return {
      total: noticeStore.unreadCount.total || 0,
      todo: noticeStore.unreadCount.todo || 0,
      alert: noticeStore.unreadCount.alert || 0,
      message: noticeStore.unreadCount.message || 0,
      announcement: noticeStore.unreadCount.announcement || 0
    };
  });

  // 获取通知列表
  const notifications = computed(() => noticeStore.notices || []);

  // 获取待办任务列表
  const todos = computed(() => noticeStore.todos || []);

  // 刷新通知数据
  const refreshNotifications = async (params = {}) => {
    loading.value = true;
    try {
      await noticeStore.getNotifications(params);
      await noticeStore.getUnreadCount();
    } finally {
      loading.value = false;
    }
  };

  // 刷新待办数据
  const refreshTodos = async () => {
    loading.value = true;
    try {
      await noticeStore.getTodoList();
    } finally {
      loading.value = false;
    }
  };

  // 标记通知为已读
  const markAsRead = async (notificationId) => {
    return await noticeStore.markAsRead(notificationId);
  };

  // 批量标记通知为已读
  const batchMarkAsRead = async (notificationIds) => {
    return await noticeStore.batchMarkAsRead(notificationIds);
  };

  // 标记所有通知为已读
  const markAllAsRead = async (type) => {
    const confirmMessage = type
      ? `确定要将所有${getTypeName(type)}通知标记为已读吗？`
      : '确定要将所有通知标记为已读吗？';

    try {
      await ElMessageBox.confirm(confirmMessage, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });

      return await noticeStore.markAllAsRead(type);
    } catch (error) {
      // 用户取消操作
      return false;
    }
  };

  // 处理通知点击
  const handleNotificationClick = (notification) => {
    if (!notification.read) {
      markAsRead(notification.notificationId);
    }

    navigateToNotificationTarget(notification);
  };

  // 处理待办任务点击
  const handleTodoClick = (todo) => {
    if (todo.menuUrl) {
      router.push(todo.menuUrl);
    }
  };

  // 导航到通知目标页面
  const navigateToNotificationTarget = (notification) => {
    if (notification.type === 'todo') {
      router.push('/todo');
      return;
    }

    // 尝试从业务数据中获取URL
    try {
      if (notification.businessData) {
        const businessData = JSON.parse(notification.businessData);
        if (businessData.businessUrl) {
          router.push(businessData.businessUrl);
          return;
        }
      }
    } catch (e) {
      console.error('解析业务数据失败', e);
    }

    // 默认导航到通知列表
    router.push('/notice/list');
  };

  // 获取通知类型显示名称
  const getTypeName = (type) => {
    switch (type) {
      case 'todo':
        return '待办';
      case 'alert':
        return '预警';
      case 'announcement':
        return '公告';
      case 'message':
        return '消息';
      default:
        return '通知';
    }
  };

  // 获取通知优先级显示名称
  const getPriorityName = (priority) => {
    switch (priority) {
      case 'high':
        return '高';
      case 'medium':
        return '中';
      case 'low':
        return '低';
      default:
        return '中';
    }
  };

  // 初始化
  onMounted(async () => {
    await noticeStore.getUnreadCount();
  });

  return {
    loading,
    unreadCount,
    notifications,
    todos,
    refreshNotifications,
    refreshTodos,
    markAsRead,
    batchMarkAsRead,
    markAllAsRead,
    handleNotificationClick,
    handleTodoClick,
    navigateToNotificationTarget,
    getTypeName,
    getPriorityName
  };
}
