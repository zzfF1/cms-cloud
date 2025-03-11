<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
          <el-form-item label="计算代码" prop="calCode">
            <el-input v-model="queryParams.calCode" placeholder="请输入计算代码" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="说明" prop="message">
            <el-input v-model="queryParams.message" placeholder="请输入说明" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['elmain:main:add']">新增 </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['elmain:main:edit']"
              >修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['elmain:main:export']"> 导出 </el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table ref="tableRef" :height="tableHeight" v-loading="loading" :data="elMainList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键" align="center" prop="id" v-if="false" />
        <el-table-column label="计算代码" align="left" prop="calCode" width="300" />
        <el-table-column label="说明" align="left" prop="message" width="450" />
        <el-table-column label="类型" align="center" prop="type" />
        <el-table-column label="版本号" align="center" prop="calVersion" />
        <el-table-column label="创建时间" align="center" prop="createDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createDate, "{y}-{m}-{d}") }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['elmain:main:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="配置" placement="top">
              <el-button link type="primary" icon="Setting" v-hasPermi="['elmain:main:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <add-e-form ref="rmainFormRef" @getList="getList"></add-e-form>
  </div>
</template>

<script setup name="ElMain" lang="ts">
import { listElMain } from "@/api/system/rule";
import { ElMainForm, ElMainQuery, ElMainVO } from "@/api/system/rule/types";
import AddEForm from "./addEForm.vue";

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const elMainList = ref<ElMainVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const rmainFormRef =  ref<InstanceType<typeof AddEForm>>();
// table元素
const tableRef = ref(null);
// table高度
const tableHeight = ref();
const dynamicHeight = ref("100px");

const data = reactive<PageData<ElMainForm, ElMainQuery>>({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    type: undefined,
    calCode: undefined,
    message: undefined
  },
  rules: {}
});

const { queryParams } = toRefs(data);

/** 查询规则配置列表 */
const getList = async () => {
  loading.value = true;
  const res = await listElMain(queryParams.value);
  elMainList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: ElMainVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  rmainFormRef.value?.showDialog(undefined);
};

/** 修改按钮操作 */
const handleUpdate = async (row?: ElMainVO) => {
  const _id = row?.id || ids.value[0];
  rmainFormRef.value?.showDialog(_id);
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download("elmain/elMain/export", {
    ...queryParams.value
  }, `elMain_${new Date().getTime()}.xlsx`);
};

onMounted(() => {
  getList();
  // 设置表格初始高度为innerHeight-offsetTop-表格底部与浏览器底部距离85
  tableHeight.value = window.innerHeight - (tableRef.value as any).$el.offsetTop - 200;
  dynamicHeight.value = ((window.innerHeight - 150) + "px");
  // 监听浏览器高度变化
  window.onresize = () => {
    tableHeight.value = window.innerHeight - (tableRef.value as any).$el.offsetTop - 200;
    dynamicHeight.value = ((window.innerHeight - 150) + "px");
  };
});
</script>
