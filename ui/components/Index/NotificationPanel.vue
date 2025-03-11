<template>
  <el-card class="notification-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span>通知中心</span>
        <div class="header-content">
          <div class="tab-container">
            <div
              v-for="tab in tabs"
              :key="tab.value"
              class="tab-item"
              :class="{ 'active-tab': activeTab === tab.value }"
              @click="changeTab(tab.value)"
            >
              {{ tab.label }}
              <span v-if="getTabCount(tab.value) > 0" class="count">{{ getTabCount(tab.value) }}</span>
            </div>
          </div>
          <div class="header-actions">
            <el-tooltip content="标记全部已读" effect="dark" placement="top">
              <i class="el-icon-check" @click="markAllAsRead" v-if="getUnreadCount() > 0"></i>
            </el-tooltip>
            <el-tooltip content="刷新通知" effect="dark" placement="top">
              <i class="el-icon-refresh" @click="refreshNotifications" :class="{ 'loading': loading }"></i>
            </el-tooltip>
            <el-tooltip content="通知设置" effect="dark" placement="top">
              <i class="el-icon-setting" @click="openNotificationSettings"></i>
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
        <div class="pagination-container" v-if="filteredNotifications.length > 0">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </template>
    </div>

    <!-- 通知设置对话框 -->
    <el-dialog title="通知设置" v-model="settingsDialogVisible" width="500px" destroy-on-close>
      <el-form :model="notificationSettings" label-width="120px">
        <el-form-item label="待办通知">
          <el-checkbox v-model="notificationSettings.todoNotifySystem">系统通知</el-checkbox>
          <el-checkbox v-model="notificationSettings.todoNotifySms">短信通知</el-checkbox>
          <el-checkbox v-model="notificationSettings.todoNotifyEmail">邮件通知</el-checkbox>
        </el-form-item>

        <el-form-item label="预警通知">
          <el-checkbox v-model="notificationSettings.alertNotifySystem">系统通知</el-checkbox>
          <el-checkbox v-model="notificationSettings.alertNotifySms">短信通知</el-checkbox>
          <el-checkbox v-model="notificationSettings.alertNotifyEmail">邮件通知</el-checkbox>
        </el-form-item>

        <el-form-item label="公告通知">
          <el-checkbox v-model="notificationSettings.announceNotifySystem">系统通知</el-checkbox>
          <el-checkbox v-model="notificationSettings.announceNotifyEmail">邮件通知</el-checkbox>
        </el-form-item>

        <el-form-item label="免打扰时间">
          <el-time-picker
            v-model="notificationSettings.doNotDisturbStart"
            format="HH:mm"
            placeholder="开始时间"
            style="width: 120px; margin-right: 10px"
          />
          <span>至</span>
          <el-time-picker
            v-model="notificationSettings.doNotDisturbEnd"
            format="HH:mm"
            placeholder="结束时间"
            style="width: 120px; margin-left: 10px"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="settingsDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveNotificationSettings">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 通知详情对话框 -->
    <el-dialog title="通知详情" v-model="detailDialogVisible" width="600px" destroy-on-close>
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
  { label: '全部', value: 'all' },
  { label: '待办', value: 'todo' },
  { label: '预警', value: 'alert' },
  { label: '消息', value: 'message' },
  { label: '公告', value: 'announcement' }
];

const activeTab = ref<string>('all');
const loading = ref<boolean>(false);
const error = ref<string>('');
const notificationList = ref<any[]>([]);
const settingsDialogVisible = ref<boolean>(false);
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

// 保存通知设置
const saveNotificationSettings = async () => {
  try {
    // 转换为后端需要的格式
    const settings = {
      todoNotifySystem: notificationSettings.value.todoNotifySystem ? '1' : '0',
      todoNotifySms: notificationSettings.value.todoNotifySms ? '1' : '0',
      todoNotifyEmail: notificationSettings.value.todoNotifyEmail ? '1' : '0',
      alertNotifySystem: notificationSettings.value.alertNotifySystem ? '1' : '0',
      alertNotifySms: notificationSettings.value.alertNotifySms ? '1' : '0',
      alertNotifyEmail: notificationSettings.value.alertNotifyEmail ? '1' : '0',
      announceNotifySystem: notificationSettings.value.announceNotifySystem ? '1' : '0',
      announceNotifyEmail: notificationSettings.value.announceNotifyEmail ? '1' : '0',
      doNotDisturbStart: notificationSettings.value.doNotDisturbStart ? dayjs(notificationSettings.value.doNotDisturbStart).format('HH:mm') : null,
      doNotDisturbEnd: notificationSettings.value.doNotDisturbEnd ? dayjs(notificationSettings.value.doNotDisturbEnd).format('HH:mm') : null
    };

    const { data } = await updateNotificationSettings(settings);
    if (data && data.code === 200) {
      ElMessage.success('通知设置保存成功');
      settingsDialogVisible.value = false;
    } else {
      ElMessage.error(data?.msg || '保存失败');
    }
  } catch (error) {
    console.error('保存通知设置失败:', error);
    ElMessage.error('保存通知设置失败');
  }
};

// 打开通知设置对话框
const openNotificationSettings = () => {
  loadNotificationSettings();
  settingsDialogVisible.value = true;
};

// 根据当前标签筛选通知
const filteredNotifications = computed(() => {
  if (activeTab.value === 'all') {
    return notificationList.value;
  }
  return notificationList.value.filter((notice) => notice.type === activeTab.value);
});

// 获取每个标签对应的通知数量
const getTabCount = (tabValue: string): number => {
  if (tabValue === 'all') {
    return notificationList.value.filter((notice) => !notice.read).length;
  }
  return notificationList.value.filter((notice) => notice.type === tabValue && !notice.read).length;
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
      page: pagination.value.current,
      size: pagination.value.size
    };

    if (activeTab.value !== 'all') {
      params.type = activeTab.value;
    }

    const { data } = await getNotifications(params);

    if (data && data.code === 200) {
      notificationList.value = data.rows || [];
      pagination.value.total = data.total || 0;

      // 同时更新未读统计
      await noticeStore.getUnreadCount();
    } else {
      error.value = data?.msg || '获取通知数据失败';
    }
  } catch (err) {
    console.error('获取通知列表出错:', err);
    error.value = '获取通知时发生错误，请稍后重试';
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
  margin-bottom: 12px;
  min-height: 500px;
}

.notification-content {
  min-height: 440px;
  position: relative;
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

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  padding-right: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 12px;
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

.header-actions i {
  cursor: pointer;
  font-size: 16px;
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

:deep(.unread-row) {
  background-color: #f0f7ff;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
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
