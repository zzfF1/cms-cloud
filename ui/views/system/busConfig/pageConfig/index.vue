<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="search">
        <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="85px">
          <el-form-item label="代码" prop="code">
            <el-input v-model="queryParams.code" placeholder="请输入代码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="名称" prop="name">
            <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="渠道" prop="branchType">
            <el-select v-model="queryParams.branchType" placeholder="请选择渠道" clearable>
              <el-option label="银保渠道" value="3" />
              <el-option label="团险渠道" value="2" />
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
            <el-button v-hasPermi="['system:pageConfig:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:pageConfig:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">
              修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:pageConfig:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">
              删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:pageConfig:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" :data="pageConfigList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" />
        <el-table-column label="代码" align="center" prop="code" />
        <el-table-column label="名称" align="center" prop="name" />
        <el-table-column label="渠道" align="center" prop="branchType" />
        <el-table-column label="排序" align="center" prop="sort" />
        <el-table-column label="说明" align="center" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:pageConfig:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:pageConfig:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <!-- 引入界面配置表单组件 -->
    <page-config-form v-model:visible="formVisible" :edit-type="editType" :row-data="currentRow" @save="handleSave"> </page-config-form>
  </div>
</template>

<script setup name="PageConfig" lang="ts">
import { listPageConfig, delPageConfig } from '@/api/system/pageConfig';
import { PageConfigQuery, PageConfigVO } from '@/api/system/pageConfig/types';
import PageConfigForm from './pageConfigForm.vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

// 表单控制状态
const formVisible = ref(false);
const editType = ref('');
const currentRow = ref<PageConfigVO | null>(null);

// 列表数据
const pageConfigList = ref<PageConfigVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

// 查询表单ref
const queryFormRef = ref<ElFormInstance>();

// 查询参数
const queryParams = reactive<PageConfigQuery>({
  pageNum: 1,
  pageSize: 10,
  code: undefined,
  name: undefined,
  branchType: undefined
});

/** 查询界面配置列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listPageConfig(queryParams);
    pageConfigList.value = res.rows;
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
const handleSelectionChange = (selection: PageConfigVO[]) => {
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
const handleUpdate = (row?: PageConfigVO) => {
  if (row) {
    currentRow.value = row;
  } else {
    const selectedConfig = pageConfigList.value.find((item) => item.id === ids.value[0]);
    currentRow.value = selectedConfig || null;
  }
  editType.value = 'edit';
  formVisible.value = true;
};

/** 保存成功后的处理 */
const handleSave = () => {
  getList();
};

/** 删除按钮操作 */
const handleDelete = async (row?: PageConfigVO) => {
  const _ids = row?.id || ids.value;
  try {
    await proxy?.$modal.confirm('是否确认删除界面配置编号为"' + _ids + '"的数据项？');
    await delPageConfig(_ids);
    proxy?.$modal.msgSuccess('删除成功');
    await getList();
  } catch (error) {
    console.error('删除操作取消或失败', error);
  }
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/pageConfig/export',
    {
      ...queryParams
    },
    `pageConfig_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getList();
});
</script>
