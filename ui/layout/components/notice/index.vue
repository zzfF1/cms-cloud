<template>
  <div class="notification-dropdown">
    <el-tabs v-model="activeTab" class="notification-tabs">
      <el-tab-pane label="通知" name="notification">
        <div class="notification-header">
          <span class="title">通知</span>
          <span class="actions">
            <el-button type="primary" link size="small" @click="handleReadAll">全部已读</el-button>
            <el-button type="primary" link size="small" @click="goToNotificationCenter"> 查看更多 </el-button>
          </span>
        </div>

        <el-scrollbar max-height="350px">
          <div v-if="loading" class="notification-loading">
            <el-skeleton :rows="3" animated />
          </div>

          <template v-else>
            <div v-if="notifications.length === 0" class="notification-empty">
              <el-empty description="暂无通知" :image-size="60"></el-empty>
            </div>

            <div v-else class="notification-list">
              <div
                v-for="item in notifications"
                :key="item.notificationId"
                class="notification-item"
                :class="{ unread: !item.read }"
                @click="handleReadNotice(item)"
              >
                <div class="notification-icon" :class="getNotificationIconClass(item)">
                  <i :class="getNotificationIcon(item)"></i>
                </div>
                <div class="notification-content">
                  <div class="notification-title" :class="{ 'high-priority': item.priority === 'high' }">
                    {{ item.title }}
                  </div>
                  <div class="notification-desc">{{ getNotificationContent(item) }}</div>
                  <div class="notification-time">{{ formatTime(item.createTime) }}</div>
                </div>
              </div>
            </div>
          </template>
        </el-scrollbar>
      </el-tab-pane>

      <el-tab-pane label="待办" name="todo">
        <div class="notification-header">
          <span class="title">待办事项</span>
          <span class="actions">
            <el-button type="primary" link size="small" @click="refreshTodoList">刷新</el-button>
          </span>
        </div>

        <el-scrollbar max-height="350px">
          <div v-if="todoLoading" class="notification-loading">
            <el-skeleton :rows="3" animated />
          </div>

          <template v-else>
            <div v-if="todos.length === 0" class="notification-empty">
              <el-empty description="暂无待办事项" :image-size="60"></el-empty>
            </div>

            <div v-else class="todo-list">
              <div v-for="item in todos" :key="item.ruleId" class="todo-item" @click="goToTodoDetail(item)">
                <div class="todo-icon">
                  <i class="el-icon-tickets"></i>
                </div>
                <div class="todo-content">
                  <div class="todo-title">{{ item.ruleName }}</div>
                  <div class="todo-desc">
                    <span class="todo-count">{{ item.count }}条待处理</span>
                  </div>
                  <div class="todo-time">{{ formatTime(item.createTime) }}</div>
                </div>
              </div>
            </div>
          </template>
        </el-scrollbar>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import useNoticeStore from '@/store/modules/notice';
import { storeToRefs } from 'pinia';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';

dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

const router = useRouter();
const noticeStore = useNoticeStore();
const { notices: storeNotices, todos: storeTodos, loading: storeLoading } = storeToRefs(noticeStore);

const activeTab = ref('notification');
const notifications = ref([]);
const todos = ref([]);
const loading = ref(false);
const todoLoading = ref(false);

// 监听store中的通知变化
watch(
  () => storeNotices.value,
  (newVal) => {
    notifications.value = newVal.slice(0, 5); // 只显示前5条通知
  }
);

// 监听store中的待办任务变化
watch(
  () => storeTodos.value,
  (newVal) => {
    todos.value = newVal;
  }
);

// 监听store中的加载状态
watch(
  () => storeLoading.value,
  (newVal) => {
    loading.value = newVal;
  }
);

// 格式化时间显示
const formatTime = (time: string) => {
  if (!time) return '';
  return dayjs(time).fromNow();
};

// 获取通知图标
const getNotificationIcon = (notification: any) => {
  const typeIconMap = {
    todo: 'el-icon-tickets',
    alert: 'el-icon-warning',
    announcement: 'el-icon-bell',
    message: 'el-icon-message'
  };

  return typeIconMap[notification.type] || 'el-icon-message';
};

// 获取通知图标样式类
const getNotificationIconClass = (notification: any) => {
  const typeClassMap = {
    todo: 'icon-todo',
    alert: 'icon-alert',
    announcement: 'icon-announcement',
    message: 'icon-message'
  };

  // 如果是高优先级，添加高优先级样式
  if (notification.priority === 'high') {
    return `${typeClassMap[notification.type] || 'icon-message'} high-priority`;
  }

  return typeClassMap[notification.type] || 'icon-message';
};

// 获取通知内容
const getNotificationContent = (notification: any) => {
  // 移除HTML标签并截取前50个字符
  let content = notification.content || '';
  content = content.replace(/<[^>]+>/g, '');
  if (content.length > 50) {
    content = content.substring(0, 50) + '...';
  }
  return content;
};

// 处理已读通知
const handleReadNotice = async (notification: any) => {
  if (!notification.read) {
    await noticeStore.markAsRead(notification.notificationId);
  }

  // 处理通知跳转
  if (notification.type === 'todo') {
    // 跳转到待办页面
    router.push({ path: '/todo' });
  } else if (notification.businessData) {
    try {
      // 尝试解析业务数据
      const businessData = JSON.parse(notification.businessData);
      if (businessData.businessUrl) {
        router.push({ path: businessData.businessUrl });
      }
    } catch (error) {
      console.error('解析业务数据异常:', error);
    }
  } else {
    // 默认跳转到通知中心
    router.push({ path: '/notice/list' });
  }
};

// 全部标记为已读
const handleReadAll = async () => {
  await noticeStore.markAllAsRead();
};

// 刷新待办列表
const refreshTodoList = async () => {
  todoLoading.value = true;
  try {
    await noticeStore.getTodoList();
  } finally {
    todoLoading.value = false;
  }
};

// 跳转到通知中心
const goToNotificationCenter = () => {
  router.push({ path: '/notice/list' });
};

// 跳转到待办详情
const goToTodoDetail = (todo: any) => {
  if (todo.menuUrl) {
    router.push({ path: todo.menuUrl });
  }
};

// 初始化
onMounted(async () => {
  loading.value = true;
  todoLoading.value = true;

  try {
    // 加载通知数据
    await noticeStore.getNotifications({ page: 1, size: 5 });

    // 加载待办数据
    await noticeStore.getTodoList();

    // 加载未读计数
    await noticeStore.getUnreadCount();
  } finally {
    loading.value = false;
    todoLoading.value = false;
  }
});
</script>

<style lang="scss" scoped>
.notification-dropdown {
  width: 350px;
  font-size: 14px;
}

.notification-tabs :deep(.el-tabs__header) {
  margin-bottom: 10px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 5px 8px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 8px;

  .title {
    font-weight: 500;
    color: #303133;
  }

  .actions {
    display: flex;
    gap: 8px;
  }
}

.notification-loading {
  padding: 10px;
}

.notification-empty {
  padding: 20px 0;
  display: flex;
  justify-content: center;
}

.notification-list,
.todo-list {
  .notification-item,
  .todo-item {
    display: flex;
    padding: 10px;
    border-bottom: 1px solid #f0f2f5;
    cursor: pointer;
    transition: background-color 0.3s;

    &:hover {
      background-color: #f5f7fa;
    }

    &.unread {
      background-color: #f0f7ff;

      &:hover {
        background-color: #e6f1ff;
      }

      .notification-title {
        font-weight: 600;
      }
    }

    .notification-icon,
    .todo-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 10px;
      flex-shrink: 0;

      i {
        font-size: 18px;
        color: white;
      }
    }

    .notification-content,
    .todo-content {
      flex: 1;
      overflow: hidden;

      .notification-title,
      .todo-title {
        color: #303133;
        font-size: 14px;
        margin-bottom: 3px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        &.high-priority {
          color: #f56c6c;
        }
      }

      .notification-desc,
      .todo-desc {
        color: #606266;
        font-size: 12px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-bottom: 3px;
      }

      .notification-time,
      .todo-time {
        color: #909399;
        font-size: 12px;
      }

      .todo-count {
        background-color: #f0f2f5;
        padding: 2px 6px;
        border-radius: 10px;
      }
    }
  }
}

// 通知图标样式
.notification-icon {
  &.icon-todo {
    background-color: #409eff;
  }

  &.icon-alert {
    background-color: #e6a23c;
  }

  &.icon-announcement {
    background-color: #67c23a;
  }

  &.icon-message {
    background-color: #909399;
  }

  &.high-priority {
    background-color: #f56c6c;
  }
}

.todo-icon {
  background-color: #409eff;
}
</style>
