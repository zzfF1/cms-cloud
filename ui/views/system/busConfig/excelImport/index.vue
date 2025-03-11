<template>
  <div class="app-container">
    <el-card>
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="模板名称">
          <el-input v-model="queryParams.name" placeholder="请输入模板名称" clearable />
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="queryParams.branchType" placeholder="请选择渠道" clearable>
            <el-option label="渠道1" value="01" />
            <el-option label="渠道2" value="02" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增模板</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column prop="code" label="代码" />
        <el-table-column prop="name" label="模板名称" />
        <el-table-column prop="branchType" label="渠道">
          <template #default="{ row }">
            {{ getBranchTypeName(row.branchType) }}
          </template>
        </el-table-column>
        <el-table-column prop="titleNames" label="标题名称" show-overflow-tooltip />
        <el-table-column prop="sheetNames" label="Sheet名称" show-overflow-tooltip />
        <el-table-column prop="remark" label="说明" show-overflow-tooltip />
        <el-table-column prop="operator" label="创建人" width="100" />
        <el-table-column prop="makedate" label="创建日期" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" @click="handleConfig(row)">配置</el-button>
            <el-button type="text" @click="handleDownload(row)">下载模板</el-button>
            <el-button type="text" style="color: red" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 引入模板编辑弹窗 -->
    <template-edit-dialog v-model="editDialogVisible" :title="dialogTitle" :data="editData" @submit="handleEditSubmit" />
    <!-- 引入字段配置弹窗 -->
    <template-config-dialog v-model="configDialogVisible" :data="configData" @submit="handleConfigSubmit" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import TemplateConfigDialog from '@/views/system/busConfig/excelImport/TemplateConfigDialog.vue';
import TemplateEditDialog from '@/views/system/busConfig/excelImport/TemplateEditDialog.vue';
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  branchType: ''
});

// 表格数据
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

// 弹窗控制
const editDialogVisible = ref(false);
const configDialogVisible = ref(false);
const dialogTitle = ref('');

// 编辑数据
const editData = ref({});
const configData = ref([]);

// 获取渠道名称
const getBranchTypeName = (type) => {
  const map = {
    '01': '渠道1',
    '02': '渠道2'
  };
  return map[type] || type;
};

// 查询列表
const getList = async () => {
  loading.value = true;
  try {
    // TODO: 调用后端API
    // const response = await fetchList(queryParams);
    // tableData.value = response.data;
    // total.value = response.total;
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
  editData.value = {};
  editDialogVisible.value = true;
};

// 编辑按钮点击
const handleEdit = (row) => {
  dialogTitle.value = '编辑模板';
  editData.value = { ...row };
  editDialogVisible.value = true;
};

// 配置按钮点击
const handleConfig = async (row) => {
  // TODO: 调用后端API获取配置详情
  // const response = await fetchConfigDetail(row.id);
  // configData.value = response.data;
  configDialogVisible.value = true;
};

// 删除按钮点击
const handleDelete = (row) => {
  // ElMessageBox.confirm('确认删除该模板配置吗？', '警告', {
  //   confirmButtonText: '确定',
  //   cancelButtonText: '取消',
  //   type: 'warning'
  // }).then(async () => {
  //   await deleteConfig(row.id);
  //   ElMessage.success('删除成功');
  //   getList();
  // });
};

// 下载模板
const handleDownload = async (row) => {
  // TODO: 调用后端下载API
};

// 编辑弹窗提交
const handleEditSubmit = async (data) => {
  // TODO: 调用后端保存API
  // await saveConfig(data);
  // ElMessage.success('保存成功');
  getList();
};

// 配置弹窗提交
const handleConfigSubmit = async (data) => {
  // TODO: 调用后端保存API
  // await saveConfigItems(data);
  // ElMessage.success('保存成功');
  getList();
};

// 分页相关方法
const handleSizeChange = (val) => {
  queryParams.pageSize = val;
  getList();
};

const handleCurrentChange = (val) => {
  queryParams.pageNum = val;
  getList();
};

// 初始化
onMounted(() => {
  // getList();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
