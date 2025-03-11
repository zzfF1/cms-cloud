<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="mb-4">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="模板名称">
          <el-input v-model="queryParams.name" placeholder="请输入模板名称" clearable />
        </el-form-item>
        <el-form-item label="代码">
          <el-input v-model="queryParams.code" placeholder="请输入代码" clearable />
        </el-form-item>
        <el-form-item label="渠道">
          <el-select v-model="queryParams.branchType" placeholder="请选择渠道" clearable>
            <el-option label="银保" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="primary" @click="handleAdd">新增配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card>
      <el-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="code" label="代码" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="branchType" label="渠道">
          <template #default="{ row }">
            {{ row.branchType === '3' ? '银保' : row.branchType }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型">
          <template #default="{ row }">
            {{ row.type === '0' ? '生成方式' : '模板方式' }}
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="创建人" />
        <el-table-column prop="makedate" label="创建时间" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" @click="handleConfig(row)">配置</el-button>
            <el-button type="text" @click="handleDownload(row)">下载</el-button>
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

    <!-- 引入配置弹窗 -->
    <config-dialog v-model="configDialog.visible" :title="configDialog.title" :data="currentItem" @submit="handleConfigSubmit" />
    <!-- 引入编辑弹窗 -->
    <edit-dialog v-model="editDialog.visible" :title="editDialog.title" :data="currentItem" @submit="handleEditSubmit" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import ConfigDialog from '@/views/system/busConfig/excelExport/ConfigDialog.vue';
import EditDialog from '@/views/system/busConfig/excelExport/EditDialog.vue';

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  code: '',
  branchType: ''
});

// 加载状态
const loading = ref(false);
const total = ref(0);

// 表格数据
const tableData = ref([
  {
    id: '103',
    code: 'BANK_AGENT_ADD',
    name: '银保入司清单',
    type: '0',
    branchType: '3',
    operator: 'admin',
    makedate: '2024-01-04',
    sheets: [
      {
        id: '103',
        configId: '103',
        sheetIndex: '0',
        titleName: '入司人员信息清单',
        sheetName: '入司人员信息',
        beginRow: '0',
        beginCol: '0',
        sqlField: '1 num, agent.managecom...',
        sqlConditions: 'laagenttemp agent left join...',
        sqlGroup: '',
        sqlOrder: 'agent.employdate,agent.managecom',
        items: [
          {
            id: '427',
            field: 'num',
            name: '序号',
            type: 9,
            dispLength: 5000,
            format: '',
            sort: 1
          }
        ]
      }
    ]
  }
]);

// 配置弹窗
const configDialog = reactive({ visible: false, title: '' });
// 编辑弹窗
const editDialog = reactive({ visible: false, title: '' });

// 当前操作的数据
const currentItem = ref(null);
const formRef = ref(null);

// 表单数据
const formData = reactive({
  id: undefined,
  code: '',
  name: '',
  branchType: '3',
  type: '0',
  path: '',
  filename: ''
});

// 查询列表
const handleQuery = () => {
  loading.value = true;
  // TODO: 调用查询接口
  setTimeout(() => {
    loading.value = false;
  }, 500);
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
  editDialog.title = '新增配置';
  currentItem.value = {
    branchType: '3',
    type: '0'
  };
  editDialog.visible = true;
};

// 编辑
const handleEdit = (row) => {
  editDialog.title = '编辑配置';
  currentItem.value = { ...row };
  editDialog.visible = true;
};

// 配置
const handleConfig = (row) => {
  configDialog.title = row.name;
  currentItem.value = row;
  configDialog.visible = true;
};

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该配置吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    // TODO: 调用删除接口
    ElMessage.success('删除成功');
    handleQuery();
  });
};

// 下载
const handleDownload = async (row) => {
  // TODO: 调用下载接口
  ElMessage.success('下载成功');
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    if (formData.type === '1' && !formData.path) {
      ElMessage.warning('请上传模板文件');
      return;
    }

    // TODO: 调用保存接口
    ElMessage.success('保存成功');
    editDialog.visible = false;
    handleQuery();
  } catch (error) {
    console.error(error);
    ElMessage.error('表单校验失败，请检查必填项');
  }
};

// 提交配置
const handleConfigSubmit = async (data) => {
  try {
    // TODO: 调用保存接口
    ElMessage.success('保存成功');
    configDialog.visible = false;
    handleQuery();
  } catch (error) {
    ElMessage.error('保存失败: ' + error.message);
  }
};

// 分页
const handleSizeChange = (val) => {
  queryParams.pageSize = val;
  handleQuery();
};

const handleCurrentChange = (val) => {
  queryParams.pageNum = val;
  handleQuery();
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.upload-demo {
  text-align: center;
}

.el-upload {
  width: 100%;
}
</style>
