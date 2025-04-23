<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="search">
        <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="85px">
          <el-form-item label="客户端key" prop="clientKey">
            <el-input v-model="queryParams.clientKey" placeholder="请输入客户端key" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="客户端秘钥" prop="clientSecret">
            <el-input v-model="queryParams.clientSecret" placeholder="请输入客户端秘钥" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="状态" clearable>
              <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:client:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:client:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">
              修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:client:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">
              删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:client:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="clientList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="true" label="id" align="center" prop="id" />
        <el-table-column label="客户端id" align="center" prop="clientId" />
        <el-table-column label="客户端key" align="center" prop="clientKey" />
        <el-table-column label="客户端秘钥" align="center" prop="clientSecret" />
        <el-table-column label="授权类型" align="center">
          <template #default="scope">
            <dict-tag :options="sys_grant_type" :value="scope.row.grantTypeList" />
          </template>
        </el-table-column>
        <el-table-column label="设备类型" align="center">
          <template #default="scope">
            <dict-tag :options="sys_device_type" :value="scope.row.deviceType" />
          </template>
        </el-table-column>
        <el-table-column label="Token活跃超时时间" align="center" prop="activeTimeout" />
        <el-table-column label="Token固定超时时间" align="center" prop="timeout" />
        <el-table-column key="status" label="状态" align="center">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:client:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:client:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <!-- 引入客户端表单组件 -->
    <client-form v-model:visible="formVisible" :edit-type="editType" :row-data="currentRow" @save="handleSave"> </client-form>
  </div>
</template>

<script setup name="Client" lang="ts">
import { changeStatus, delClient, listClient } from '@/api/system/client';
import { ClientQuery, ClientVO } from '@/api/system/client/types';
import ClientForm from './clientForm.vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));
const { sys_grant_type } = toRefs<any>(proxy?.useDict('sys_grant_type'));
const { sys_device_type } = toRefs<any>(proxy?.useDict('sys_device_type'));

// 表单控制状态
const formVisible = ref(false);
const editType = ref('');
const currentRow = ref<ClientVO | null>(null);

// 列表数据
const clientList = ref<ClientVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

// 查询表单ref
const queryFormRef = ref<ElFormInstance>();

// 查询参数
const queryParams = reactive<ClientQuery>({
  pageNum: 1,
  pageSize: 10,
  clientId: undefined,
  clientKey: undefined,
  clientSecret: undefined,
  grantType: undefined,
  deviceType: undefined,
  activeTimeout: undefined,
  timeout: undefined,
  status: undefined
});

/** 查询客户端管理列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listClient(queryParams);
    clientList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: ClientVO[]) => {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  currentRow.value = null;
  editType.value = 'add';
  formVisible.value = true;
};

/** 修改按钮操作 */
const handleUpdate = (row?: ClientVO) => {
  if (row) {
    currentRow.value = row;
  } else {
    const selectedClient = clientList.value.find((item) => item.id === ids.value[0]);
    currentRow.value = selectedClient || null;
  }
  editType.value = 'edit';
  formVisible.value = true;
};

/** 保存成功后的处理 */
const handleSave = () => {
  getList();
};

/** 删除按钮操作 */
const handleDelete = async (row?: ClientVO) => {
  const _ids = row?.id || ids.value;
  try {
    await proxy?.$modal.confirm('是否确认删除客户端管理编号为"' + _ids + '"的数据项？');
    await delClient(_ids);
    proxy?.$modal.msgSuccess('删除成功');
    await getList();
  } catch (error) {
    console.error('删除操作取消或失败', error);
  }
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/client/export',
    {
      ...queryParams
    },
    `client_${new Date().getTime()}.xlsx`
  );
};

/** 状态修改  */
const handleStatusChange = async (row: ClientVO) => {
  const text = row.status === '0' ? '启用' : '停用';
  try {
    await proxy?.$modal.confirm('确认要"' + text + '"吗?');
    await changeStatus(row.clientId, row.status);
    proxy?.$modal.msgSuccess(text + '成功');
  } catch (err) {
    row.status = row.status === '0' ? '1' : '0';
  }
};

onMounted(() => {
  getList();
});
</script>
