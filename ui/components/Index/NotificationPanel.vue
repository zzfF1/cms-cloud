<template>
  <el-card class="notification-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span>通知</span>
        <div class="header-content">
          <div class="tab-container">
            <div
              v-for="tab in tabs"
              :key="tab.value"
              class="tab-item"
              :class="{ 'active-tab': activeTab === tab.value }"
              @click="activeTab = tab.value"
            >
              {{ tab.label }}
              <span v-if="getTabCount(tab.value) > 0" class="count">{{ getTabCount(tab.value) }}</span>
            </div>
          </div>
          <div class="header-actions">
            <i class="el-icon-refresh" @click="refreshNotifications" :class="{ 'loading': loading }"></i>
            <i class="el-icon-more"></i>
          </div>
        </div>
      </div>
    </template>

    <div class="notification-content">
      <!-- 加载中状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-state">
        <i class="el-icon-warning"></i>
        <p>{{ error }}</p>
        <el-button type="primary" size="small" @click="refreshNotifications">重试</el-button>
      </div>

      <!-- 通知表格 -->
      <template v-else>
        <el-table :data="filteredNotifications" style="width: 100%" size="small" @row-click="handleNotificationClick">
          <!-- 类型 -->
          <el-table-column label="类型" width="70">
            <template #default="scope">
              <el-tag size="small" :type="getTagType(scope.row.category)" effect="plain">
                {{ getCategoryName(scope.row.category) }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 标题 -->
          <el-table-column label="标题" min-width="140">
            <template #default="scope">
              <span :class="{ 'font-bold': !scope.row.read }" :style="getPriorityStyle(scope.row.priority)">
                {{ scope.row.title }}
              </span>
            </template>
          </el-table-column>

          <!-- 通知内容 -->
          <el-table-column label="通知内容" min-width="180">
            <template #default="scope">
              <span class="text-sm text-gray-500">{{ scope.row.description }}</span>
            </template>
          </el-table-column>

          <!-- 日期 -->
          <el-table-column prop="time" label="日期" width="90" align="right"> </el-table-column>
        </el-table>

        <div v-if="filteredNotifications.length === 0" class="empty-state">暂无通知内容</div>
      </template>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted, watch } from 'vue';
import { getNotifications, markNotificationRead, Notification, NotificationParams } from '@/api/system/dashboard';
import { ElMessage } from 'element-plus';

// 标签页定义
interface TabItem {
  label: string;
  value: string;
}

const tabs: TabItem[] = [
  { label: '全部', value: 'all' },
  { label: '业务相关', value: 'business' },
  { label: '系统提醒', value: 'system' },
  { label: '团队活动', value: 'team' }
];

const activeTab = ref<string>('all');
const loading = ref<boolean>(false);
const error = ref<string>('');

// 分页信息
interface PaginationInfo {
  current: number;
  size: number;
  total: number;
  pages: number;
}

const pagination = ref<PaginationInfo>({
  current: 1,
  size: 10,
  total: 0,
  pages: 1
});

// 通知数据
const notifications = ref<Notification[]>([]);

// 监听标签页变化，切换标签时重置分页
watch(activeTab, () => {
  pagination.value.current = 1;
  if (activeTab.value !== 'all') {
    fetchNotifications({ category: activeTab.value });
  } else {
    fetchNotifications();
  }
});

// 暴露给父组件的方法
defineExpose({
  notifications,
  refreshNotifications
});

// 根据当前标签筛选通知
const filteredNotifications = computed<Notification[]>(() => {
  if (activeTab.value === 'all') {
    return notifications.value;
  }
  return notifications.value.filter((notification) => notification.category === activeTab.value);
});

// 获取每个标签对应的通知数量
const getTabCount = (tabValue: string): number => {
  if (tabValue === 'all') {
    return notifications.value.length;
  }
  return notifications.value.filter((notification) => notification.category === tabValue).length;
};

// 根据优先级获取样式
const getPriorityStyle = (priority: string): Record<string, string> => {
  switch (priority) {
    case 'urgent':
      return { color: '#F56C6C' };
    case 'important':
      return { color: '#E6A23C' };
    default:
      return {};
  }
};

// 根据分类获取标签类型
const getTagType = (category: string): string => {
  switch (category) {
    case 'business':
      return 'primary';
    case 'team':
      return 'success';
    case 'system':
      return 'warning';
    default:
      return 'info';
  }
};

// 获取分类名称
const getCategoryName = (category: string): string => {
  switch (category) {
    case 'business':
      return '业务';
    case 'team':
      return '团队';
    case 'system':
      return '系统';
    default:
      return '其他';
  }
};

// 处理通知点击
const handleNotificationClick = async (row: Notification): Promise<void> => {
  const notification = notifications.value.find((n) => n.id === row.id);
  if (notification && !notification.read) {
    try {
      const response = await markNotificationRead(notification.id);
      if (response.code === 200) {
        notification.read = true;
      } else {
        ElMessage.warning(response.message || '标记已读失败');
      }
    } catch (err) {
      console.error('标记通知已读出错:', err);
      ElMessage.error('标记已读时发生错误');
    }
  }
};

// 处理分页变化
const handlePageChange = (page: number): void => {
  pagination.value.current = page;
  const params: NotificationParams = {
    page,
    size: pagination.value.size
  };

  if (activeTab.value !== 'all') {
    params.category = activeTab.value;
  }

  fetchNotifications(params);
};

// 获取通知数据
const fetchNotifications = async (params?: NotificationParams): Promise<void> => {
  loading.value = true;
  error.value = '';

  try {
    const defaultParams: NotificationParams = {
      page: pagination.value.current,
      size: pagination.value.size
    };

    const mergedParams: NotificationParams = { ...defaultParams, ...params };

    const response = await getNotifications(mergedParams);

    if (response.code === 200) {
      notifications.value = response.data.records;

      // 更新分页信息
      pagination.value = {
        current: response.data.current,
        size: response.data.size,
        total: response.data.total,
        pages: response.data.pages
      };
    } else {
      error.value = response.message || '获取通知数据失败';
    }
  } catch (err) {
    console.error('获取通知列表出错:', err);
    error.value = '获取通知时发生错误，请稍后重试';
  } finally {
    loading.value = false;
  }
};

// 刷新通知
function refreshNotifications(): void {
  const params: NotificationParams = {
    page: pagination.value.current,
    size: pagination.value.size
  };

  if (activeTab.value !== 'all') {
    params.category = activeTab.value;
  }

  fetchNotifications(params);
}

// 页面初始化
onMounted(() => {
  refreshNotifications();
});
</script>

<style scoped>
.notification-card {
  margin-bottom: 12px;
  /* 这里设置最小高度 */
  min-height: 400px;
}

.notification-content {
  /* 确保内容区域也有最小高度，减去头部的高度 */
  min-height: 340px;
  position: relative;
}

.loading-state,
.error-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 340px;
  width: 100%;
}

.error-state {
  color: #f56c6c;
}

.error-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.loading-state {
  padding: 20px;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  padding-right: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 12px;
}

:deep(.el-pagination) {
  margin: 0;
  padding: 0;
}

:deep(.el-pagination .el-pager li) {
  min-width: 24px;
  height: 24px;
  line-height: 24px;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  padding: 0 6px;
  min-width: 24px;
  height: 24px;
  line-height: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-left: auto;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 16px;
}

.header-actions i {
  cursor: pointer;
  font-size: 14px;
  color: #909399;
  transition: color 0.2s;
}

.header-actions i:hover {
  color: #409eff;
}

.header-actions i.loading {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.tab-container {
  display: flex;
}

.tab-item {
  padding: 0 12px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  position: relative;
}

.tab-item:hover {
  color: #409eff;
}

.active-tab {
  color: #409eff;
  font-weight: bold;
}

.count {
  display: inline-block;
  background-color: #f0f2f5;
  color: #606266;
  border-radius: 10px;
  padding: 0 6px;
  font-size: 12px;
  margin-left: 4px;
}

.empty-state {
  padding: 20px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
}

.font-bold {
  font-weight: bold;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 500;
  font-size: 13px;
  padding: 6px 0;
}

:deep(.el-table .el-table__row) {
  font-size: 13px;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 14px;
}
</style>
