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
                <div class="stat-title">配置总数</div>
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
                <el-icon><Plus /></el-icon> 新增配置
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 搜索区域 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="名称">
          <el-input v-model="queryParams.name" placeholder="请输入名称" clearable prefix-icon="Search" />
        </el-form-item>
        <el-form-item label="代码">
          <el-input v-model="queryParams.code" placeholder="请输入代码" clearable prefix-icon="Search" />
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="queryParams.branchType" placeholder="请选择渠道" clearable>
            <el-option label="银保" value="3" />
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

        <el-table-column prop="name" label="名称" min-width="180" show-overflow-tooltip />

        <el-table-column prop="branchType" label="渠道" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getBranchTypeTag(row.branchType)">
              {{ getBranchTypeName(row.branchType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '0' ? 'success' : 'warning'">
              {{ row.type === '0' ? '生成方式' : '模板方式' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Sheet信息" min-width="200">
          <template #default="{ row }">
            <div class="sheet-info">
              <div class="sheet-count" v-if="row.sheets && row.sheets.length > 0">
                <el-badge :value="row.sheets.length" class="sheet-badge">
                  <el-icon class="sheet-icon"><Files /></el-icon>
                </el-badge>
              </div>
              <div class="sheet-names" v-if="row.sheets && row.sheets.length > 0">
                <el-tag v-for="(sheet, index) in row.sheets" :key="index" type="info" effect="plain" class="sheet-tag">
                  {{ sheet.sheetName || 'Sheet' + (index + 1) }}
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
              <div v-if="hasFields(row)" class="field-names">
                <el-tag v-for="(item, index) in getFieldSample(row)" :key="index" type="success" effect="plain" class="field-tag">
                  {{ item.name }}
                </el-tag>
                <el-tag v-if="getTotalFieldCount(row) > 3" type="info"> +{{ getTotalFieldCount(row) - 3 }}个 </el-tag>
              </div>
              <div v-else class="no-field">
                <el-tag type="info" effect="plain">未配置字段</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>

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

    <!-- 引入配置抽屉 -->
    <export-config-drawer v-model="drawerVisible" :title="drawerTitle" :data="formData" @submit="handleSubmit" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import ExportConfigDrawer from './components/ExportConfigDrawer.vue';
import { Search, Plus, Edit, Delete, Download, Refresh, Document, Connection, Grid, Files } from '@element-plus/icons-vue';
import {
  listExportConfig,
  getFullExportConfig,
  addExportConfig,
  updateExportConfig,
  delExportConfig,
  downloadExportTemplate
} from '@/api/system/exportConfig';
import { ExportConfigVO, ExportConfigItemVO } from '@/api/system/exportConfig/types';

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  code: '',
  branchType: ''
});

// 表格数据
const loading = ref(false);
const tableData = ref<ExportConfigVO[]>([]);
const total = ref(0);

// 抽屉控制
const drawerVisible = ref(false);
const drawerTitle = ref('');

// 当前操作的数据
const formData = ref<Partial<ExportConfigVO>>({});

// 获取渠道名称
const getBranchTypeName = (type: string) => {
  const map: Record<string, string> = {
    '3': '银保'
  };
  return map[type] || type;
};

// 获取渠道标签类型
const getBranchTypeTag = (type: string) => {
  const map: Record<string, string> = {
    '3': 'primary'
  };
  return map[type] || 'info';
};

// 检查是否有字段配置
const hasFields = (row: ExportConfigVO) => {
  if (!row.sheets || row.sheets.length === 0) return false;

  for (const sheet of row.sheets) {
    if (sheet.items && sheet.items.length > 0) {
      return true;
    }
  }
  return false;
};

// 获取字段样本（前3个）
const getFieldSample = (row: ExportConfigVO) => {
  if (!row.sheets || row.sheets.length === 0) return [];

  // 收集所有字段
  const allFields: ExportConfigItemVO[] = [];
  for (const sheet of row.sheets) {
    if (sheet.items && sheet.items.length > 0) {
      allFields.push(...sheet.items);
    }
  }

  // 按排序字段排序
  allFields.sort((a, b) => (a.sort || 0) - (b.sort || 0));

  // 返回前3个
  return allFields.slice(0, 3);
};

// 获取总字段数
const getTotalFieldCount = (row: ExportConfigVO) => {
  if (!row.sheets || row.sheets.length === 0) return 0;

  let count = 0;
  for (const sheet of row.sheets) {
    if (sheet.items && sheet.items.length > 0) {
      count += sheet.items.length;
    }
  }
  return count;
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
    if (item.sheets && item.sheets.length > 0) {
      totalSheets += item.sheets.length;
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
    const response = await listExportConfig(queryParams);
    tableData.value = response.rows;
    total.value = response.total;
  } catch (error) {
    console.error('获取列表失败', error);
    ElMessage.error('获取列表失败');
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
  queryParams.code = '';
  queryParams.branchType = '';
  handleQuery();
};

// 新增
const handleAdd = () => {
  drawerTitle.value = '新增导出配置';
  formData.value = {
    branchType: '3',
    type: '0',
    sheets: [
      {
        sheetIndex: 0,
        titleName: '',
        sheetName: '',
        beginRow: '0',
        beginCol: '0',
        sqlField: '',
        sqlConditions: '',
        sqlGroup: '',
        sqlOrder: '',
        items: []
      }
    ]
  };
  drawerVisible.value = true;
};

// 编辑
const handleEdit = async (row: ExportConfigVO) => {
  try {
    loading.value = true;
    drawerTitle.value = '编辑导出配置';

    // 一次性获取配置的完整信息
    const response = await getFullExportConfig(row.id);

    // 设置配置信息
    formData.value = response.data.basicInfo;

    // 设置Sheet配置信息
    if (response.data.sheetInfo && response.data.sheetInfo.length > 0) {
      formData.value.sheets = response.data.sheetInfo;
    }

    // 显示抽屉
    drawerVisible.value = true;
  } catch (error) {
    console.error('获取详情失败', error);
    ElMessage.error('获取详情失败');
  } finally {
    loading.value = false;
  }
};

// 删除
const handleDelete = (row: ExportConfigVO) => {
  ElMessageBox.confirm(`确认删除配置"${row.name}"吗？删除后无法恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    draggable: true
  })
    .then(async () => {
      try {
        await delExportConfig(row.id);
        ElMessage.success('删除成功');
        getList();
      } catch (error) {
        console.error('删除失败', error);
        ElMessage.error('删除失败');
      }
    })
    .catch(() => {});
};

// 下载
const handleDownload = async (row: ExportConfigVO) => {
  try {
    loading.value = true;
    ElMessage.info('正在生成Excel文件，请稍候...');

    const response = await downloadExportTemplate(row.id);
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
const handleSubmit = async (data: { basicInfo: ExportConfigVO, sheetInfo: any[] }) => {
  try {
    loading.value = true;
    const { basicInfo, sheetInfo } = data;

    if (basicInfo.id) {
      // 编辑模式
      await updateExportConfig({ basicInfo, sheetInfo });
      ElMessage.success('修改成功');
    } else {
      // 新增模式
      await addExportConfig({ basicInfo, sheetInfo });
      ElMessage.success('新增成功');
    }

    drawerVisible.value = false;
    getList();
  } catch (error) {
    console.error('保存失败', error);
    ElMessage.error('保存失败');
  } finally {
    loading.value = false;
  }
};

// 分页
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
