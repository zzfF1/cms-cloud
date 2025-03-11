<template>
  <div class="page-config-container">
    <!-- 顶部搜索区 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="代码">
          <el-input v-model="searchForm.code" placeholder="请输入代码" clearable />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="searchForm.name" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="searchForm.branchType" placeholder="请选择渠道" clearable>
            <el-option label="银保渠道" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="code" label="代码" min-width="120" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="branchType" label="渠道" min-width="100">
          <template #default="{ row }">
            {{ row.branchType === '3' ? '银保渠道' : row.branchType }}
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="remark" label="说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <PageConfigDialog v-model="dialogVisible" :config-data="currentConfigData" @success="handleDialogSuccess" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import PageConfigDialog from '@/views/system/busConfig/pageConfig/PageConfigDialog.vue';

// 搜索表单
const searchForm = reactive({
  code: '',
  name: '',
  branchType: ''
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 对话框控制
const dialogVisible = ref(false);
const currentConfigData = ref(null);

// 搜索方法
const handleSearch = () => {
  fetchData();
};

const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = '';
  });
  handleSearch();
};

// 获取数据方法
const fetchData = async () => {
  loading.value = true;
  try {
    // 这里调用后端API
    // const res = await api.getPageConfigList({
    //   ...searchForm,
    //   page: currentPage.value,
    //   pageSize: pageSize.value
    // })
    // tableData.value = res.data.list
    // total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

// 分页方法
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchData();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchData();
};

// 新增方法
const handleAdd = () => {
  currentConfigData.value = null;
  dialogVisible.value = true;
};

// 编辑方法
const handleEdit = (row) => {
  currentConfigData.value = { ...row };
  dialogVisible.value = true;
};

// 删除方法
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该配置?', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      // await api.deletePageConfig(row.id)
      ElMessage.success('删除成功');
      fetchData();
    } catch (error) {
      ElMessage.error('删除失败');
    }
  });
};

// 对话框成功回调
const handleDialogSuccess = () => {
  dialogVisible.value = false;
  fetchData();
};
</script>

<style scoped>
.page-config-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
