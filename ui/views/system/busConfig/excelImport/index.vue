<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <div class="statistics-panel">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card total-card">
            <div class="stat-card-content">
              <div class="stat-icon">
                <el-icon class="icon"><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">模板总数</div>
                <div class="stat-value">{{ total }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover" class="stat-card channel-card">
            <div class="stat-card-content">
              <div class="stat-icon">
                <el-icon class="icon"><Connection /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">渠道统计</div>
                <div class="stat-value">{{ getChannelCount() }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover" class="stat-card sheet-card">
            <div class="stat-card-content">
              <div class="stat-icon">
                <el-icon class="icon"><Grid /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">平均Sheet数</div>
                <div class="stat-value">{{ getAverageSheetCount() }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover" class="stat-card action-card">
            <div class="stat-card-content action-content">
              <el-button type="primary" @click="handleAdd" class="action-button">
                <el-icon><Plus /></el-icon> 新增模板
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索区域 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="模板名称">
          <el-input v-model="queryParams.name" placeholder="请输入模板名称" clearable prefix-icon="Search" />
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="queryParams.branchType" placeholder="请选择渠道" clearable>
            <el-option label="渠道1" value="01" />
            <el-option label="渠道2" value="02" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card shadow="hover" class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        border
        highlight-current-row
        class="custom-table"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column type="index" label="序号" width="70" align="center" />

        <el-table-column prop="code" label="代码" min-width="120" />

        <el-table-column prop="name" label="模板名称" min-width="180" show-overflow-tooltip />

        <el-table-column prop="branchType" label="渠道" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getBranchTypeTag(row.branchType)">
              {{ getBranchTypeName(row.branchType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Sheet信息" min-width="200">
          <template #default="{ row }">
            <div class="sheet-info">
              <div class="sheet-count">
                <el-badge :value="getSheetCount(row)" class="sheet-badge">
                  <el-icon class="sheet-icon"><Files /></el-icon>
                </el-badge>
              </div>
              <div class="sheet-names" v-if="row.sheetNames">
                <el-tag v-for="(sheet, index) in row.sheetNames.split(',')" :key="index" type="info" effect="plain" class="sheet-tag">
                  {{ sheet }}
                </el-tag>
              </div>
              <div v-else class="no-sheet">
                <el-tag type="info" effect="plain">未配置Sheet</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="字段信息" min-width="200">
          <template #default="{ row }">
            <div class="field-info">
              <div v-if="row.titleNames" class="field-names">
                <el-tag v-for="(title, index) in getFirstFewTitles(row.titleNames)" :key="index" type="success" effect="plain" class="field-tag">
                  {{ title }}
                </el-tag>
                <el-tag v-if="getTitleCount(row) > 3" type="info"> +{{ getTitleCount(row) - 3 }}个 </el-tag>
              </div>
              <div v-else class="no-field">
                <el-tag type="info" effect="plain">未配置字段</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="说明" min-width="150" show-overflow-tooltip />

        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button-group class="action-group">
              <el-button type="primary" @click="handleEdit(row)" size="small">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button type="success" @click="handleDownload(row)" size="small">
                <el-icon><Download /></el-icon>
              </el-button>
              <el-button type="danger" @click="handleDelete(row)" size="small">
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 引入模板配置抽屉 -->
    <template-drawer v-model="drawerVisible" :title="dialogTitle" :data="formData" :sheetData="sheetData" @submit="handleSubmit" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import TemplateDrawer from './components/TemplateDrawer.vue';
import { Search, Plus, Edit, Delete, Download, Refresh, Document, Connection, Grid, Files } from '@element-plus/icons-vue';
import {
  listImportConfig,
  getFullImportConfig,
  addImportConfig,
  updateImportConfig,
  delImportConfig,
  saveImportConfigItems,
  downloadImportTemplate
} from '@/api/system/importConfig';
import { ImportConfigVO, SheetConfig } from '@/api/system/importConfig/types';

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  branchType: ''
});

// 表格数据
const loading = ref(false);
const tableData = ref<ImportConfigVO[]>([]);
const total = ref(0);

// 抽屉控制
const drawerVisible = ref(false);
const dialogTitle = ref('');

// 表单数据
const formData = ref<Partial<ImportConfigVO>>({});
const sheetData = ref<SheetConfig[]>([]);

// 获取渠道名称
const getBranchTypeName = (type: string) => {
  const map: Record<string, string> = {
    '01': '渠道1',
    '02': '渠道2'
  };
  return map[type] || type;
};

// 获取渠道标签类型
const getBranchTypeTag = (type: string) => {
  const map: Record<string, string> = {
    '01': 'primary',
    '02': 'success'
  };
  return map[type] || 'info';
};

// 获取Sheet数量
const getSheetCount = (row: ImportConfigVO) => {
  if (!row.sheetNames) return 0;
  return row.sheetNames.split(',').length;
};

// 获取标题数量
const getTitleCount = (row: ImportConfigVO) => {
  if (!row.titleNames) return 0;
  return row.titleNames.split(',').length;
};

// 获取前几个标题
const getFirstFewTitles = (titleNames: string) => {
  if (!titleNames) return [];
  const titles = titleNames.split(',');
  return titles.slice(0, 3); // 只取前3个
};

// 获取渠道数量统计
const getChannelCount = () => {
  if (!tableData.value || tableData.value.length === 0) return 0;

  const channels = new Set();
  tableData.value.forEach((item) => {
    if (item.branchType) {
      channels.add(item.branchType);
    }
  });

  return channels.size;
};

// 获取平均Sheet数
const getAverageSheetCount = () => {
  if (!tableData.value || tableData.value.length === 0) return '0';

  let totalSheets = 0;
  let validItems = 0;

  tableData.value.forEach((item) => {
    if (item.sheetNames) {
      totalSheets += item.sheetNames.split(',').length;
      validItems++;
    }
  });

  if (validItems === 0) return '0';
  return (totalSheets / validItems).toFixed(1);
};

// 查询列表
const getList = async () => {
  loading.value = true;
  try {
    const response = await listImportConfig(queryParams);
    tableData.value = response.rows;
    total.value = response.total;
  } catch (error) {
    console.error('获取列表失败', error);
  } finally {
    loading.value = false;
  }
};

// 查询按钮点击
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置查询
const resetQuery = () => {
  queryParams.name = '';
  queryParams.branchType = '';
  handleQuery();
};

// 新增按钮点击
const handleAdd = () => {
  dialogTitle.value = '新增模板';
  formData.value = {};
  sheetData.value = [{ name: '', fields: [] }];
  drawerVisible.value = true;
};

// 编辑按钮点击
const handleEdit = async (row: ImportConfigVO) => {
  try {
    loading.value = true;
    dialogTitle.value = '编辑模板';

    // 一次性获取模板的完整信息
    const response = await getFullImportConfig(row.id);

    // 设置模板基本信息
    formData.value = response.data.basicInfo;

    // 设置Sheet配置信息
    sheetData.value = response.data.sheetInfo;

    // 显示抽屉
    drawerVisible.value = true;
  } catch (error) {
    console.error('获取详情失败', error);
    ElMessage.error('获取详情失败');
  } finally {
    loading.value = false;
  }
};

// 删除按钮点击
const handleDelete = (row: ImportConfigVO) => {
  ElMessageBox.confirm(`确认删除模板"${row.name}"吗？删除后无法恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    draggable: true
  })
    .then(async () => {
      try {
        await delImportConfig(row.id);
        ElMessage.success('删除成功');
        getList();
      } catch (error) {
        console.error('删除失败', error);
        ElMessage.error('删除失败');
      }
    })
    .catch(() => {});
};

// 下载模板
const handleDownload = async (row: ImportConfigVO) => {
  try {
    loading.value = true;
    ElMessage.info('正在生成Excel模板，请稍候...');

    const response = await downloadImportTemplate(row.id);
    const blob = new Blob([response.data]);
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `${row.name}.xlsx`;
    link.click();
    URL.revokeObjectURL(link.href);

    ElMessage.success('下载成功');
  } catch (error) {
    console.error('下载失败', error);
    ElMessage.error('下载失败');
  } finally {
    loading.value = false;
  }
};

// 抽屉提交处理
const handleSubmit = async (data: { formData: ImportConfigVO; sheetData: SheetConfig[] }) => {
  try {
    loading.value = true;
    const { formData: submitFormData, sheetData: submitSheetData } = data;

    // 1. 保存模板基本信息
    if (submitFormData.id) {
      // 编辑模式
      await updateImportConfig(submitFormData);
    } else {
      // 新增模式
      const response = await addImportConfig(submitFormData);
      // 获取新创建的模板ID
      submitFormData.id = response.data;
    }

    // 2. 保存Sheet配置
    if (submitFormData.id) {
      await saveImportConfigItems(submitFormData.id, submitSheetData);
    }

    ElMessage.success('保存成功');
    getList();
  } catch (error) {
    console.error('保存失败', error);
    ElMessage.error('保存失败');
  } finally {
    loading.value = false;
  }
};

// 分页相关方法
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val;
  getList();
};

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val;
  getList();
};

// 初始化
onMounted(() => {
  getList();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

/* 统计面板 */
.statistics-panel {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.stat-card-content {
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
}

.total-card .stat-icon {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.channel-card .stat-icon {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.sheet-card .stat-icon {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-icon .icon {
  font-size: 28px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.action-content {
  justify-content: center;
}

.action-button {
  height: 40px;
  width: 100%;
  font-size: 16px;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-form {
  padding: 10px 0;
}

/* 表格卡片 */
.table-card {
  border-radius: 8px;
}

.custom-table {
  margin: 10px 0;
  border-radius: 4px;
  overflow: hidden;
}

/* Sheet信息样式 */
.sheet-info {
  display: flex;
  align-items: center;
}

.sheet-count {
  margin-right: 15px;
}

.sheet-badge {
  margin-right: 10px;
}

.sheet-icon {
  font-size: 20px;
  color: #409eff;
}

.sheet-names {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.sheet-tag {
  margin-right: 5px;
}

.no-sheet,
.no-field {
  color: #909399;
  font-style: italic;
}

/* 字段信息样式 */
.field-info {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.field-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

/* 操作按钮组 */
.action-group {
  display: flex;
  justify-content: center;
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 10px 0;
}
</style>
