<template>
  <el-card class="notification-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span>通知中心</span>
        <div class="header-content">
          <div class="tab-container">
            <!-- 待办标签 -->
            <div class="tab-item" :class="{ 'active-tab': activeTab === 'todo' }" @click="changeTab('todo')">
              待办
              <span v-if="getTabCount('todo') > 0" class="count">{{ getTabCount('todo') }}</span>
            </div>

            <!-- 消息标签 -->
            <div class="tab-item" :class="{ 'active-tab': activeTab === 'message' }" @click="changeTab('message')">
              消息
              <span v-if="getTabCount('message') > 0" class="count">{{ getTabCount('message') }}</span>
            </div>

            <!-- 公告标签 -->
            <div class="tab-item" :class="{ 'active-tab': activeTab === 'announcement' }" @click="changeTab('announcement')">
              公告
              <span v-if="getTabCount('announcement') > 0" class="count">{{ getTabCount('announcement') }}</span>
            </div>
          </div>
          <!-- 强制显示所有按钮，移除任何条件 -->
          <div class="header-actions">
            <!-- 标记全部已读 -->
            <el-tooltip :content="markAllReadTooltip" effect="dark" placement="top">
              <div class="action-wrapper" @click="canMarkAllRead && markAllAsRead" :class="{ 'disabled': !canMarkAllRead }">
                <el-icon>
                  <Check />
                </el-icon>
              </div>
            </el-tooltip>

            <!-- 刷新通知 -->
            <el-tooltip content="刷新通知" effect="dark" placement="top">
              <div class="action-wrapper" @click="refreshNotifications">
                <el-icon :class="{ 'is-loading': loading }">
                  <Refresh />
                </el-icon>
              </div>
            </el-tooltip>
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
        <el-table
          :data="filteredNotifications"
          style="width: 100%"
          size="small"
          @row-click="handleNotificationClick"
          :row-class-name="getRowClassName"
        >
          <!-- 标记 -->
          <el-table-column width="40">
            <template #default="scope">
              <el-tooltip v-if="!scope.row.read" content="未读" effect="dark" placement="top">
                <div class="unread-dot"></div>
              </el-tooltip>
            </template>
          </el-table-column>

          <!-- 类型 -->
          <el-table-column label="类型" width="70">
            <template #default="scope">
              <el-tag size="small" :type="getTagType(scope.row.type)" effect="plain">
                {{ getTypeName(scope.row.type) }}
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
          <el-table-column label="内容" min-width="180">
            <template #default="scope">
              <span class="text-sm text-gray-500">{{ getNotificationContent(scope.row) }}</span>
            </template>
          </el-table-column>

          <!-- 日期 -->
          <el-table-column prop="createTime" label="时间" width="100" align="right">
            <template #default="scope">
              {{ formatTime(scope.row.createTime) }}
            </template>
          </el-table-column>

          <!-- 操作 -->
          <el-table-column width="60" fixed="right">
            <template #default="scope">
              <el-dropdown trigger="click" @command="(command) => handleCommand(command, scope.row)">
                <span class="el-dropdown-link">
                  <i class="el-icon-more"></i>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="scope.row.read ? 'markUnread' : 'markRead'">
                      {{ scope.row.read ? '标为未读' : '标为已读' }}
                    </el-dropdown-item>
                    <el-dropdown-item command="view">查看详情</el-dropdown-item>
                    <el-dropdown-item v-if="scope.row.type === 'todo'" command="process">处理</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="filteredNotifications.length === 0" class="empty-state">暂无通知内容</div>

        <!-- 分页 -->
        <div class="pagination-outer-wrapper" v-if="filteredNotifications.length > 0 && activeTab !== 'todo'" style="margin-bottom: -30px !important">
          <div class="pagination-inner-container">
            <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              background
            />
          </div>
        </div>
      </template>
    </div>
    <!-- 通知详情对话框 -->
    <el-dialog
      title="通知详情"
      v-model="detailDialogVisible"
      width="600px"
      :append-to-body="true"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
    >
      <div v-if="selectedNotification" class="notification-detail">
        <div class="detail-header">
          <el-tag :type="getTagType(selectedNotification.type)" effect="plain" size="small">
            {{ getTypeName(selectedNotification.type) }}
          </el-tag>
          <h3 :style="getPriorityStyle(selectedNotification.priority)">{{ selectedNotification.title }}</h3>
          <div class="detail-meta">
            <span>{{ formatDate(selectedNotification.createTime) }}</span>
            <span v-if="selectedNotification.read"> 已读于 {{ formatDate(selectedNotification.readTime) }} </span>
            <span v-else>未读</span>
          </div>
        </div>

        <div class="detail-content" v-html="selectedNotification.content"></div>

        <div class="detail-actions" v-if="selectedNotification.actions">
          <h4>可执行操作</h4>
          <div class="action-buttons">
            <template v-for="(action, index) in parseActions(selectedNotification.actions)" :key="index">
              <el-button :type="action.style || 'primary'" size="small" @click="executeAction(action)">
                {{ action.name }}
              </el-button>
            </template>
          </div>
        </div>

        <div class="detail-attachments" v-if="selectedNotification.attachments">
          <h4>附件</h4>
          <div class="attachment-list">
            <template v-for="(attachment, index) in parseAttachments(selectedNotification.attachments)" :key="index">
              <div class="attachment-item">
                <i class="el-icon-document"></i>
                <span>{{ attachment.name }}</span>
                <el-button type="text" size="small" @click="downloadAttachment(attachment)">下载</el-button>
              </div>
            </template>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button v-if="selectedNotification && selectedNotification.type === 'todo'" type="primary" @click="processTodo(selectedNotification)">
            处理
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import useNoticeStore from '@/store/modules/notice';
import { getNotifications, getNotificationSettings, updateNotificationSettings } from '@/api/system/notification';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';

dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

const router = useRouter();
const noticeStore = useNoticeStore();

// 标签页定义
interface TabItem {
  label: string;
  value: string;
}

const tabs: TabItem[] = [
  { label: '待办', value: 'todo' },
  { label: '消息', value: 'message' },
  { label: '公告', value: 'announcement' }
];

const activeTab = ref<string>('todo');
const loading = ref<boolean>(false);
const error = ref<string>('');
const notificationList = ref<any[]>([]);
const detailDialogVisible = ref<boolean>(false);
const selectedNotification = ref<any>(null);

// 分页信息
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
});

// 通知设置
const notificationSettings = ref({
  todoNotifySystem: true,
  todoNotifySms: false,
  todoNotifyEmail: false,
  alertNotifySystem: true,
  alertNotifySms: false,
  alertNotifyEmail: false,
  announceNotifySystem: true,
  announceNotifyEmail: false,
  doNotDisturbStart: null,
  doNotDisturbEnd: null
});

const canMarkAllRead = computed(() => {
  // 只有在"消息"或"公告"标签，并且有未读消息时才可用
  if (activeTab.value === 'message' || activeTab.value === 'announcement') {
    // 获取当前选中标签的未读数
    const count = getTabCount(activeTab.value);
    return count > 0;
  }
  return false;
});

// 标记全部已读按钮的提示文本
const markAllReadTooltip = computed(() => {
  if (!canMarkAllRead.value) {
    if (activeTab.value === 'todo') {
      return '待办不支持标记全部已读';
    } else {
      return '没有未读' + getTypeName(activeTab.value);
    }
  }
  return '标记全部已读';
});

// 加载通知设置
const loadNotificationSettings = async () => {
  try {
    const { data } = await getNotificationSettings();
    if (data && data.code === 200) {
      const settings = data.data;

      // 转换复选框值
      notificationSettings.value = {
        todoNotifySystem: settings.todoNotifySystem === '1',
        todoNotifySms: settings.todoNotifySms === '1',
        todoNotifyEmail: settings.todoNotifyEmail === '1',
        alertNotifySystem: settings.alertNotifySystem === '1',
        alertNotifySms: settings.alertNotifySms === '1',
        alertNotifyEmail: settings.alertNotifyEmail === '1',
        announceNotifySystem: settings.announceNotifySystem === '1',
        announceNotifyEmail: settings.announceNotifyEmail === '1',
        doNotDisturbStart: settings.doNotDisturbStart ? dayjs(settings.doNotDisturbStart, 'HH:mm').toDate() : null,
        doNotDisturbEnd: settings.doNotDisturbEnd ? dayjs(settings.doNotDisturbEnd, 'HH:mm').toDate() : null
      };
    }
  } catch (error) {
    console.error('加载通知设置失败:', error);
  }
};

// 根据当前标签筛选通知
const filteredNotifications = computed(() => {
  if (activeTab.value === 'todo') {
    // 待办标签不分页，显示所有待办
    return notificationList.value.filter((notice) => notice.type === 'todo');
  } else {
    // 消息和公告标签保持分页功能
    return notificationList.value.filter((notice) => notice.type === activeTab.value);
  }
});

// 获取每个标签对应的通知数量
const getTabCount = (tabValue: string): number => {
  if (tabValue === 'todo') {
    return noticeStore.unreadCount.todo || 0;
  } else if (tabValue === 'message') {
    return noticeStore.unreadCount.message || 0;
  } else if (tabValue === 'announcement') {
    return noticeStore.unreadCount.announcement || 0;
  }
  return 0;
};

// 获取未读通知总数
const getUnreadCount = (): number => {
  return notificationList.value.filter((notice) => !notice.read).length;
};

// 根据优先级获取样式
const getPriorityStyle = (priority: string): Record<string, string> => {
  switch (priority) {
    case 'high':
      return { color: '#F56C6C' };
    case 'medium':
      return { color: '#E6A23C' };
    default:
      return {};
  }
};

// 根据分类获取标签类型
const getTagType = (type: string): string => {
  switch (type) {
    case 'todo':
      return 'primary';
    case 'alert':
      return 'warning';
    case 'announcement':
      return 'success';
    case 'message':
      return 'info';
    default:
      return 'info';
  }
};

// 获取类型名称
const getTypeName = (type: string): string => {
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
      return '其他';
  }
};

// 获取通知内容（去除HTML标签）
const getNotificationContent = (notification: any): string => {
  let content = notification.content || '';
  content = content.replace(/<[^>]+>/g, '');
  if (content.length > 50) {
    content = content.substring(0, 50) + '...';
  }
  return content;
};

// 格式化时间为相对时间
const formatTime = (time: string): string => {
  return dayjs(time).fromNow();
};

// 格式化日期时间
const formatDate = (time: string): string => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

// 解析通知操作
const parseActions = (actionsJson: string): any[] => {
  try {
    return JSON.parse(actionsJson) || [];
  } catch (error) {
    console.error('解析通知操作失败:', error);
    return [];
  }
};

// 解析通知附件
const parseAttachments = (attachmentsJson: string): any[] => {
  try {
    return JSON.parse(attachmentsJson) || [];
  } catch (error) {
    console.error('解析通知附件失败:', error);
    return [];
  }
};

// 执行操作
const executeAction = (action: any) => {
  if (action.url) {
    // 如果有确认信息，先询问用户
    if (action.confirmMessage) {
      ElMessageBox.confirm(action.confirmMessage, '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 确认后执行操作
          router.push({ path: action.url });
          detailDialogVisible.value = false;
        })
        .catch(() => {
          // 用户取消操作
        });
    } else {
      // 没有确认信息，直接执行
      router.push({ path: action.url });
      detailDialogVisible.value = false;
    }
  }
};

// 下载附件
const downloadAttachment = (attachment: any) => {
  if (attachment.url) {
    window.open(attachment.url, '_blank');
  } else {
    ElMessage.warning('附件链接不可用');
  }
};

// 处理待办任务
const processTodo = (notification: any) => {
  try {
    // 解析业务数据
    const businessData = notification.businessData ? JSON.parse(notification.businessData) : {};
    if (businessData.businessUrl) {
      // 关闭对话框
      detailDialogVisible.value = false;
      // 跳转到业务页面
      router.push({ path: businessData.businessUrl });
    } else {
      ElMessage.warning('没有可处理的业务链接');
    }
  } catch (error) {
    console.error('处理待办任务失败:', error);
    ElMessage.error('处理待办任务失败');
  }
};

// 处理通知点击
const handleNotificationClick = (row: any) => {
  selectedNotification.value = row;
  detailDialogVisible.value = true;

  // 标记为已读
  if (!row.read) {
    noticeStore.markAsRead(row.notificationId);
  }
};

// 获取行样式
const getRowClassName = ({ row }: { row: any }) => {
  return row.read ? '' : 'unread-row';
};

// 处理下拉菜单命令
const handleCommand = (command: string, row: any) => {
  switch (command) {
    case 'markRead':
      noticeStore.markAsRead(row.notificationId);
      break;
    case 'markUnread':
      // 暂时不支持标记为未读，这里可以添加相应接口
      ElMessage.info('标记为未读功能暂未实现');
      break;
    case 'view':
      selectedNotification.value = row;
      detailDialogVisible.value = true;
      break;
    case 'process':
      processTodo(row);
      break;
  }
};

// 标记所有通知为已读
const markAllAsRead = async () => {
  const type = activeTab.value === 'all' ? undefined : activeTab.value;
  await noticeStore.markAllAsRead(type);
  await fetchNotifications();
};

// 切换标签页
const changeTab = (tab: string) => {
  activeTab.value = tab;
  // 切换到待办标签时，取消分页限制，显示全部待办
  if (tab === 'todo') {
    // 可以在这里设置特殊处理，比如设置较大的页面大小
    pagination.value.size = 100;
  } else {
    // 其他标签恢复正常分页大小
    pagination.value.size = 10;
  }
  pagination.value.current = 1;
  fetchNotifications();
};

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  pagination.value.size = size;
  fetchNotifications();
};

// 处理页码变化
const handleCurrentChange = (page: number) => {
  pagination.value.current = page;
  fetchNotifications();
};

// 刷新通知
const refreshNotifications = () => {
  fetchNotifications();
};

// 获取通知数据
const fetchNotifications = async () => {
  loading.value = true;
  error.value = '';
  try {
    const params: any = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size
    };
    // 设置筛选类型
    if (activeTab.value !== 'all') {
      params.type = activeTab.value;
    }
    // 为待办类型设置特殊处理 - 可以设置一个较大的pageSize
    if (activeTab.value === 'todo') {
      params.pageSize = 100; // 设置一个较大的值以显示所有待办
    }
    const response = await getNotifications(params);
    if (response && response.code === 200) {
      notificationList.value = response.rows || [];
      pagination.value.total = response.total || 0;
      // 同时更新未读统计
      await noticeStore.getUnreadCount();
    } else {
      error.value = response?.msg || '获取通知数据失败';
    }
  } catch (err) {
    console.error('获取通知列表出错:', err);
    error.value = '获取通知时发生错误，请稍后重试';
    notificationList.value = [];
  } finally {
    loading.value = false;
  }
};

// 页面初始化
onMounted(() => {
  refreshNotifications();
});
</script>

<style lang="scss" scoped>
.notification-card {
  margin-bottom: 0;
  min-height: 500px;
}

.notification-content {
  min-height: 440px;
  position: relative;
  padding-bottom: 50px; /* 为分页组件留出空间 */
}

.loading-state,
.error-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 340px;
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
  gap: 12px;
  margin-left: 16px;
}

.action-icon {
  cursor: pointer;
  font-size: 18px;
  color: #909399;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 4px;
}

.action-icon:hover {
  color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
}

.action-icon.loading {
  animation: rotate 1s linear infinite;
}

.action-wrapper {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 4px;
  transition: all 0.3s;
}

.action-wrapper:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.action-wrapper .el-icon {
  font-size: 18px;
  color: #909399;
}

.action-wrapper:hover .el-icon {
  color: #409eff;
}

.action-wrapper.disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.action-wrapper.disabled:hover {
  background-color: transparent;
}

.action-wrapper.disabled .el-icon {
  color: #909399;
}

.el-icon.is-loading {
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

.font-bold {
  font-weight: bold;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #409eff;
  display: inline-block;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
}

/* 分页容器样式 */
.pagination-outer-wrapper {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  padding: 8px 0;
  border-top: 1px solid #ebeef5;
  background-color: #f8f8f8;
  z-index: 10;
}

.pagination-inner-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

/* 通知详情样式 */
.notification-detail {
  padding: 0 10px;

  .detail-header {
    margin-bottom: 16px;

    h3 {
      margin: 8px 0;
      font-size: 16px;
    }

    .detail-meta {
      display: flex;
      justify-content: space-between;
      color: #909399;
      font-size: 12px;
    }
  }

  .detail-content {
    margin-bottom: 16px;
    line-height: 1.6;
    color: #606266;
  }

  .detail-actions,
  .detail-attachments {
    border-top: 1px solid #ebeef5;
    padding-top: 10px;
    margin-top: 10px;

    h4 {
      margin-bottom: 10px;
      font-size: 14px;
      font-weight: 500;
    }

    .action-buttons {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }

    .attachment-list {
      .attachment-item {
        display: flex;
        align-items: center;
        padding: 6px 0;

        i {
          margin-right: 6px;
          color: #909399;
        }

        span {
          flex: 1;
        }
      }
    }
  }
}

/* 深度选择器样式 */
:deep(.unread-row) {
  background-color: #f0f7ff;
}

:deep(.el-pagination) {
  padding: 0;
  font-weight: normal;
  justify-content: center !important;
}

:deep(.el-pagination .el-pagination__total) {
  font-size: 13px;
}

:deep(.el-pagination .el-pagination__jump) {
  margin-left: 10px;
}

:deep(.el-pagination .btn-prev, .el-pagination .btn-next) {
  padding: 0 10px;
}

:deep(.el-pagination .el-pagination__sizes) {
  margin-right: 15px;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .tab-container {
    flex-wrap: wrap;
  }

  .tab-item {
    padding: 4px 8px;
    margin-bottom: 4px;
  }

  .header-actions {
    margin-left: 8px;
  }
}
</style>
