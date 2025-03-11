<template>
  <div class="app-container">
    <el-card class="todo-list-card">
      <template #header>
        <div class="card-header">
          <span>待办任务</span>
          <div class="header-actions">
            <el-tooltip content="刷新列表" effect="dark" placement="top">
              <el-button type="primary" plain size="small" icon="el-icon-refresh" @click="refreshList" :loading="loading"> 刷新 </el-button>
            </el-tooltip>
          </div>
        </div>
      </template>

      <!-- 任务分组展示 -->
      <div class="todo-groups">
        <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
          <el-tab-pane name="all" label="全部待办"></el-tab-pane>
          <el-tab-pane name="urgent" label="紧急待办"></el-tab-pane>
          <el-tab-pane v-for="group in todoGroups" :key="group.templateId" :name="group.templateId.toString()">
            <template #label>
              <span>{{ group.templateName }}</span>
              <el-badge :value="group.count" :max="99" type="primary" class="task-count-badge" />
            </template>
          </el-tab-pane>
        </el-tabs>

        <!-- 任务筛选表单 -->
        <el-form :model="queryParams" ref="queryForm" :inline="true" class="todo-query-form">
          <el-form-item label="关键词" prop="keyword">
            <el-input v-model="queryParams.keyword" placeholder="规则名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 待办任务列表 -->
      <el-table v-loading="loading" :data="todoList" style="width: 100%" row-key="ruleId" @row-click="handleRowClick">
        <!-- 任务图标 -->
        <el-table-column width="60" align="center">
          <template #default="scope">
            <div class="todo-icon" :class="{ 'urgent-task': scope.row.priority === 'high' }">
              <i class="el-icon-tickets"></i>
            </div>
          </template>
        </el-table-column>

        <!-- 任务名称 -->
        <el-table-column prop="ruleName" label="任务名称" min-width="200">
          <template #default="scope">
            <span :class="{ 'urgent-task-text': scope.row.priority === 'high' }">
              {{ scope.row.ruleName }}
            </span>
          </template>
        </el-table-column>

        <!-- 任务数量 -->
        <el-table-column prop="count" label="待处理数量" width="120" align="center">
          <template #default="scope">
            <el-badge :value="scope.row.count" :max="99" type="primary" />
          </template>
        </el-table-column>

        <!-- 任务类型 -->
        <el-table-column prop="templateName" label="任务类型" min-width="150">
          <template #default="scope">
            {{ scope.row.templateName }}
          </template>
        </el-table-column>

        <!-- 创建时间 -->
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" align="center" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" plain @click.stop="processTodo(scope.row)"> 处理 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.page" v-model:limit="queryParams.size" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { getTodoList } from '@/api/system/notice';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';
import Pagination from '@/components/Pagination/index.vue';

const router = useRouter();

// 数据对象
const loading = ref(false);
const todoList = ref([]);
const total = ref(0);
const activeTab = ref('all');
const todoGroups = ref([]);
const queryParams = reactive({
  page: 1,
  size: 10,
  templateId: null,
  keyword: '',
  priority: null
});

// 获取待办任务列表
const getList = async () => {
  loading.value = true;

  try {
    // 设置过滤条件
    if (activeTab.value === 'all') {
      queryParams.templateId = null;
      queryParams.priority = null;
    } else if (activeTab.value === 'urgent') {
      queryParams.templateId = null;
      queryParams.priority = 'high';
    } else {
      queryParams.templateId = parseInt(activeTab.value);
      queryParams.priority = null;
    }

    const { data } = await getTodoList(queryParams);

    if (data && data.code === 200) {
      todoList.value = data.data || [];
      total.value = data.total || 0;

      // 提取任务分组信息
      const groups = {};
      todoList.value.forEach((todo) => {
        if (!groups[todo.templateId]) {
          groups[todo.templateId] = {
            templateId: todo.templateId,
            templateName: todo.templateName,
            count: todo.count
          };
        } else {
          groups[todo.templateId].count += todo.count;
        }
      });

      todoGroups.value = Object.values(groups);
    } else {
      ElMessage.error(data?.msg || '获取待办任务列表失败');
    }
  } catch (error) {
    console.error('获取待办任务列表异常:', error);
    ElMessage.error('获取待办任务列表失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 处理待办任务
const processTodo = (todo) => {
  if (todo.menuUrl) {
    router.push({ path: todo.menuUrl });
  } else {
    ElMessage.warning('没有配置处理路径');
  }
};

// 处理行点击
const handleRowClick = (row) => {
  processTodo(row);
};

// 处理标签页切换
const handleTabClick = () => {
  queryParams.page = 1;
  getList();
};

// 搜索
const handleQuery = () => {
  queryParams.page = 1;
  getList();
};

// 重置搜索
const resetQuery = () => {
  queryParams.keyword = '';
  queryParams.page = 1;
  getList();
};

// 刷新列表
const refreshList = () => {
  getList();
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss');
};

// 监听查询参数变化
watch(
  () => activeTab.value,
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
.todo-list-card {
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

.todo-query-form {
  margin: 16px 0;
}

.todo-groups {
  margin-bottom: 20px;
}

.task-count-badge {
  margin-left: 6px;
}

.todo-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;

  i {
    color: white;
    font-size: 18px;
  }

  &.urgent-task {
    background-color: #f56c6c;
  }
}

.urgent-task-text {
  color: #f56c6c;
  font-weight: bold;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .todo-query-form {
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
