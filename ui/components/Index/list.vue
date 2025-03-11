<template>
  <div class="app-container">
    <el-card class="notification-list-card">
      <template #header>
        <div class="card-header">
          <span>通知列表</span>
          <div class="header-actions">
            <el-tooltip content="标记全部已读" effect="dark" placement="top">
              <el-button type="primary" plain size="small" icon="el-icon-check" @click="markAllAsRead" :disabled="getUnreadCount() === 0">
                全部已读
              </el-button>
            </el-tooltip>
            <el-tooltip content="刷新列表" effect="dark" placement="top">
              <el-button type="primary" plain size="small" icon="el-icon-refresh" @click="refreshList" :loading="loading"> 刷新 </el-button>
            </el-tooltip>
          </div>
        </div>
      </template>

      <!-- 通知筛选表单 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="notification-query-form">
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="queryParams.type" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="全部类型" value="" />
            <el-option label="待办通知" value="todo" />
            <el-option label="预警通知" value="alert" />
            <el-option label="公告通知" value="announcement" />
            <el-option label="消息通知" value="message" />
          </el-select>
        </el-form-item>
        <el-form-item label="阅读状态" prop="isRead">
          <el-select v-model="queryParams.isRead" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="全部状态" value="" />
            <el-option label="未读" :value="false" />
            <el-option label="已读" :value="true" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词" prop="keyword">
          <el-input v-model="queryParams.keyword" placeholder="标题/内容关键词" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="通知时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 通知表格 -->
      <el-table
        v-loading="loading"
        :data="notificationList"
        style="width: 100%"
        row-key="notificationId"
        :row-class-name="getRowClassName"
        @row-click="handleRowClick"
      >
        <!-- 未读标记 -->
        <el-table-column width="50" align="center">
          <template #default="scope">
            <el-tooltip content="未读" effect="dark" placement="top" v-if="!scope.row.read">
              <div class="unread-dot"></div>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 通知类型 -->
        <el-table-column label="类型" align="center" width="90">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.type)" size="small">
              {{ getTypeName(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 优先级 -->
        <el-table-column label="优先级" align="center" width="90">
          <template #default="scope">
            <el-tag :type="getPriorityTagType(scope.row.priority)" size="small" effect="plain">
              {{ getPriorityName(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 标题 -->
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <span :class="{ 'font-bold': !scope.row.read }">{{ scope.row.title }}</span>
          </template>
        </el-table-column>

        <!-- 内容摘要 -->
        <el-table-column prop="content" label="内容摘要" min-width="250" show-overflow-tooltip>
          <template #default="scope">
            {{ getNotificationContent(scope.row) }}
          </template>
        </el-table-column>

        <!-- 创建时间 -->
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>

        <!-- 阅读状态 -->
        <el-table-column label="阅读状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.read" type="success" size="small">已读</el-tag>
            <el-tag v-else type="warning" size="small">未读</el-tag>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" align="center" width="150">
          <template #default="scope">
            <el-button type="text" size="small" @click.stop="viewNotificationDetail(scope.row)"> 查看 </el-button>
            <el-button v-if="!scope.row.read" type="text" size="small" @click.stop="markAsRead(scope.row)"> 标为已读 </el-button>
            <el-button v-if="scope.row.type === 'todo'" type="text" size="small" @click.stop="processTodo(scope.row)"> 处理 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />
    </el-card>

    <!-- 通知详情对话框 -->
    <el-dialog title="通知详情" v-model="detailDialogVisible" width="650px" append-to-body destroy-on-close>
      <div v-if="selectedNotification" class="notification-detail">
        <div class="detail-header">
          <div class="detail-header-top">
            <el-tag :type="getTagType(selectedNotification.type)" size="small">
              {{ getTypeName(selectedNotification.type) }}
            </el-tag>
            <el-tag :type="getPriorityTagType(selectedNotification.priority)" size="small" effect="plain" style="margin-left: 8px">
              {{ getPriorityName(selectedNotification.priority) }}
            </el-tag>
          </div>

          <h3>{{ selectedNotification.title }}</h3>

          <div class="detail-meta">
            <span>创建时间: {{ formatDateTime(selectedNotification.createTime) }}</span>
            <span v-if="selectedNotification.expirationDate"> 过期时间: {{ formatDateTime(selectedNotification.expirationDate) }} </span>
            <span v-if="selectedNotification.read"> 已读时间: {{ formatDateTime(selectedNotification.readTime) }} </span>
          </div>
        </div>

        <el-divider content-position="left">通知内容</el-divider>

        <div class="detail-content" v-html="selectedNotification.content"></div>

        <div v-if="actions.length > 0" class="detail-actions">
          <el-divider content-position="left">可执行操作</el-divider>
          <div class="action-buttons">
            <el-button
              v-for="(action, index) in actions"
              :key="index"
              :type="getActionType(action.style)"
              size="small"
              @click="executeAction(action)"
            >
              {{ action.name }}
            </el-button>
          </div>
        </div>

        <div v-if="attachments.length > 0" class="detail-attachments">
          <el-divider content-position="left">附件</el-divider>
          <el-table :data="attachments" style="width: 100%" size="small">
            <el-table-column prop="name" label="附件名称" min-width="200" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="size" label="大小" width="100">
              <template #default="scope">
                {{ formatFileSize(scope.row.size) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="scope">
                <el-button type="primary" link size="small" @click="downloadAttachment(scope.row)"> 下载 </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-if="businessData" class="detail-business-data">
          <el-divider content-position="left">相关业务</el-divider>
          <div class="business-info">
            <template v-if="businessData.businessName">
              <div class="business-item">
                <span class="label">业务名称:</span>
                <span class="value">{{ businessData.businessName }}</span>
              </div>
            </template>
            <template v-if="businessData.businessCode">
              <div class="business-item">
                <span class="label">业务编码:</span>
                <span class="value">{{ businessData.businessCode }}</span>
              </div>
            </template>
            <template v-if="businessData.businessType">
              <div class="business-item">
                <span class="label">业务类型:</span>
                <span class="value">{{ businessData.businessType }}</span>
              </div>
            </template>
            <template v-if="businessData.businessTime">
              <div class="business-item">
                <span class="label">业务时间:</span>
                <span class="value">{{ formatDateTime(businessData.businessTime) }}</span>
              </div>
            </template>
            <template v-if="businessData.businessStatus">
              <div class="business-item">
                <span class="label">业务状态:</span>
                <span class="value">{{ businessData.businessStatus }}</span>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { getNotifications, markNotificationRead, markAllRead } from '@/api/system/notification';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';
import Pagination from '@/components/Pagination/index.vue';

const router = useRouter();

// 数据对象
const loading = ref(false);
const notificationList = ref([]);
const total = ref(0);
const dateRange = ref([]);
const queryParams = reactive({
  page: 1,
  size: 10,
  type: '',
  isRead: '',
  keyword: '',
  startTime: '',
  endTime: ''
});

// 详情对话框
const detailDialogVisible = ref(false);
const selectedNotification = ref(null);
const actions = ref([]);
const attachments = ref([]);
const businessData = ref(null);

// 获取通知列表
const getList = async () => {
  loading.value = true;

  try {
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startTime = dateRange.value[0];
      queryParams.endTime = dateRange.value[1];
    } else {
      queryParams.startTime = '';
      queryParams.endTime = '';
    }

    const { data } = await getNotifications(queryParams);

    if (data && data.code === 200) {
      notificationList.value = data.rows || [];
      total.value = data.total || 0;
    } else {
      ElMessage.error(data?.msg || '获取通知列表失败');
    }
  } catch (error) {
    console.error('获取通知列表异常:', error);
    ElMessage.error('获取通知列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 获取未读通知数量
const getUnreadCount = () => {
  return notificationList.value.filter((item) => !item.read).length;
};

// 标记全部已读
const markAllAsRead = async () => {
  if (getUnreadCount() === 0) {
    return;
  }

  try {
    const { data } = await markAllRead();

    if (data && data.code === 200) {
      // 更新本地状态
      notificationList.value.forEach((notice) => {
        notice.read = true;
        notice.readTime = new Date().toISOString();
      });

      ElMessage.success('所有通知已标记为已读');
    } else {
      ElMessage.error(data?.msg || '标记全部已读失败');
    }
  } catch (error) {
    console.error('标记全部已读异常:', error);
    ElMessage.error('标记全部已读失败，请稍后重试');
  }
};

// 标记单条通知为已读
const markAsRead = async (notification) => {
  if (notification.read) {
    return;
  }

  try {
    const { data } = await markNotificationRead(notification.notificationId);

    if (data && data.code === 200) {
      // 更新本地状态
      notification.read = true;
      notification.readTime = new Date().toISOString();

      ElMessage.success('已标记为已读');
    } else {
      ElMessage.error(data?.msg || '标记已读失败');
    }
  } catch (error) {
    console.error('标记已读异常:', error);
    ElMessage.error('标记已读失败，请稍后重试');
  }
};

// 查看通知详情
const viewNotificationDetail = (notification) => {
  selectedNotification.value = notification;

  // 解析操作和附件
  parseNotificationDetails(notification);

  // 显示详情对话框
  detailDialogVisible.value = true;

  // 标记为已读
  if (!notification.read) {
    markAsRead(notification);
  }
};

// 解析通知详情
const parseNotificationDetails = (notification) => {
  // 解析操作
  if (notification.actions) {
    try {
      actions.value = JSON.parse(notification.actions) || [];
    } catch (e) {
      console.error('解析通知操作失败:', e);
      actions.value = [];
    }
  } else {
    actions.value = [];
  }

  // 解析附件
  if (notification.attachments) {
    try {
      attachments.value = JSON.parse(notification.attachments) || [];
    } catch (e) {
      console.error('解析通知附件失败:', e);
      attachments.value = [];
    }
  } else {
    attachments.value = [];
  }

  // 解析业务数据
  if (notification.businessData) {
    try {
      businessData.value = JSON.parse(notification.businessData) || null;
    } catch (e) {
      console.error('解析通知业务数据失败:', e);
      businessData.value = null;
    }
  } else {
    businessData.value = null;
  }
};

// 执行操作
const executeAction = (action) => {
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
const downloadAttachment = (attachment) => {
  if (attachment.url) {
    window.open(attachment.url, '_blank');
  } else {
    ElMessage.warning('附件链接不可用');
  }
};

// 处理待办任务
const processTodo = (notification) => {
  try {
    // 解析业务数据
    const data = notification.businessData ? JSON.parse(notification.businessData) : {};
    if (data.businessUrl) {
      // 关闭对话框
      detailDialogVisible.value = false;
      // 跳转到业务页面
      router.push({ path: data.businessUrl });
    } else {
      ElMessage.warning('没有可处理的业务链接');
    }
  } catch (error) {
    console.error('处理待办任务失败:', error);
    ElMessage.error('处理待办任务失败');
  }
};

// 处理行点击
const handleRowClick = (row) => {
  viewNotificationDetail(row);
};

// 获取行样式
const getRowClassName = ({ row }) => {
  return row.read ? '' : 'unread-row';
};

// 搜索
const handleQuery = () => {
  queryParams.page = 1;
  getList();
};

// 重置搜索
const resetQuery = () => {
  dateRange.value = [];
  queryParams.type = '';
  queryParams.isRead = '';
  queryParams.keyword = '';
  queryParams.startTime = '';
  queryParams.endTime = '';
  queryParams.page = 1;
  getList();
};

// 刷新列表
const refreshList = () => {
  getList();
};

// 获取通知类型标签样式
const getTagType = (type) => {
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

// 获取通知类型名称
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
      return '其他';
  }
};

// 获取优先级标签样式
const getPriorityTagType = (priority) => {
  switch (priority) {
    case 'high':
      return 'danger';
    case 'medium':
      return 'warning';
    case 'low':
      return 'info';
    default:
      return 'info';
  }
};

// 获取优先级名称
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

// 获取操作按钮类型
const getActionType = (style) => {
  switch (style) {
    case 'primary':
      return 'primary';
    case 'danger':
      return 'danger';
    case 'warning':
      return 'warning';
    case 'success':
      return 'success';
    case 'info':
      return 'info';
    default:
      return 'primary';
  }
};

// 获取通知内容（去除HTML标签）
const getNotificationContent = (notification) => {
  let content = notification.content || '';
  content = content.replace(/<[^>]+>/g, '');
  if (content.length > 50) {
    content = content.substring(0, 50) + '...';
  }
  return content;
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss');
};

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '0 B';

  const units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB'];
  let i = 0;
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024;
    i++;
  }

  return size.toFixed(2) + ' ' + units[i];
};

// 监听查询参数变化
watch(
  () => [queryParams.type, queryParams.isRead],
  () => {
    handleQuery();
  }
);

// 初始化
onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.notification-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.notification-query-form {
  margin-bottom: 20px;
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

.font-bold {
  font-weight: bold;
}

/* 通知详情样式 */
.notification-detail {
  .detail-header {
    margin-bottom: 16px;

    .detail-header-top {
      margin-bottom: 8px;
    }

    h3 {
      margin: 10px 0;
      font-size: 18px;
      font-weight: 500;
    }

    .detail-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      color: #909399;
      font-size: 13px;
    }
  }

  .detail-content {
    margin: 16px 0;
    line-height: 1.8;
    color: #606266;
  }

  .action-buttons {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    margin: 10px 0;
  }

  .business-info {
    .business-item {
      display: flex;
      margin-bottom: 8px;

      .label {
        width: 100px;
        color: #909399;
      }

      .value {
        flex: 1;
        color: #303133;
      }
    }
  }
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .notification-query-form {
    :deep(.el-form-item) {
      margin-right: 0;
      margin-bottom: 10px;
      display: flex;
      flex-direction: column;
      align-items: flex-start;

      .el-form-item__label {
        padding-bottom: 4px;
      }

      .el-form-item__content {
        margin-left: 0 !important;
        width: 100%;
      }
    }
  }
}
</style>
