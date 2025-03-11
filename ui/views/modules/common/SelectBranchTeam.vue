<template>
  <el-input v-model="inputValue" placeholder="请选择">
    <template #append>
      <el-button @click="openDialog">选择</el-button>
    </template>
  </el-input>
  <m-dialog v-model="dialogVisible" title="选择销售机构" dialog-widht="60%" @saveSubmit="confirm">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div class="search" v-show="showSearch">
        <el-form :model="queryParams" ref="queryParams" :inline="true" label-width="100px">
          <el-form-item label="机构编码" prop="branchattr">
            <el-input v-model="queryParams.branchattr" placeholder="请输入机构编码" clearable />
          </el-form-item>
          <el-form-item label="机构名称" prop="name">
            <el-input v-model="queryParams.name" placeholder="请输入机构名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </transition>
    <el-card shadow="never">
      <el-table
        ref="tableRef"
        v-loading="loading"
        border
        :data="branchGroupList"
        :height="tableHeight"
        highlight-current-row
        @current-change="handleCurrentChange"
      >
        <el-table-column label="机构主键" align="center" prop="agentGroup" v-if="false" />
        <el-table-column label="管理机构" align="left" prop="manageCom" width="260">
          <template #default="scope">
            <div>{{ scope.row.manageCom }}-{{ scope.row.manageComName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="销售机构" align="left" prop="branchAttr" width="280">
          <template #default="scope">
            <div>{{ scope.row.branchAttr }}-{{ scope.row.name }}</div>
          </template>
        </el-table-column>
        <el-table-column label="成立日期" align="center" prop="foundDate" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.foundDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="主管" align="center" prop="branchManagerName" width="110" />
        <el-table-column label="级别" align="center" prop="branchLevelName" width="60" />
        <el-table-column label="状态" align="center" prop="endFlagName" width="70" />
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
  </m-dialog>
</template>

<script setup lang="ts">
import { listBaseBranch } from '@/api/common/branch/index';
import MDialog from '@/views/modules/common/MDialog.vue';
import { BaseBranchShowVO, BranchQuery } from '@/api/common/branch/types';
const { proxy } = getCurrentInstance() as ComponentInternalInstance;

// 定义组件的属性
const props = defineProps({
  //显示级别
  showLevel: { type: Number, default: 4 },
  //结果值
  modelValue: { type: String, default: '' },
  //已经选择的管理机构
  selManageCom: { type: String, default: '' },
  //渠道
  branchType: { type: String, required: true, default: '0' },
  //级别
  branchLevel: { type: String, default: '' }
});
//数据
const branchGroupList = ref<BaseBranchShowVO[]>([]);
//加载状态
const loading = ref(false);
//总条数
const total = ref(0);
//是否显示搜索
const showSearch = ref(true);
//表格ref
const tableRef = ref(null);
//查询表单ref
const queryFormRef = ref<ElFormInstance>();

const queryParams = reactive<BranchQuery>({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  branchattr: undefined,
  managecom: undefined,
  branchtype: undefined,
  branchlevels: undefined
});

// table高度
const tableHeight = ref('500');
//显示状态
const dialogVisible = ref(false);
//文本框值
const inputValue = ref('');
//选中值
const selectedValue = ref(props.modelValue);
//选中行
const currentRow = ref();
//组件实例
const emit = defineEmits(['update:modelValue', 'callback']);

const getList = async () => {
  loading.value = true;
  //查询条件
  const res = await listBaseBranch(queryParams);
  branchGroupList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  //加载数据
  nextTick(async () => {
    getList();
  });
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

//显示弹窗
const openDialog = () => {
  let result = true;
  emit(`callback`, currentRow.value, (val: boolean) => (result = val));
  //如果校验不通过则不关闭弹窗
  if (!result) {
    return;
  }
  //初始化数据
  getList();
  dialogVisible.value = true;
};

//关闭弹窗
const closeDialog = () => {
  dialogVisible.value = false;
};

//确定
const confirm = () => {
  //如果没有选中值
  if (currentRow.value === undefined) {
    ElMessage({ message: '请选择销售机构!', type: 'warning' });
    return;
  }
  inputValue.value = (currentRow.value.branchAttr + '-' + currentRow.value.name).toString();
  selectedValue.value = currentRow.value.agentGroup;
  //更新结果
  emit('update:modelValue', selectedValue.value);
  //关闭对话框
  closeDialog();
};

const handleCurrentChange = (val: BaseBranchShowVO | undefined) => {
  currentRow.value = val;
};
onMounted(() => {
  // 设置表格初始高度为innerHeight-offsetTop-表格底部与浏览器底部距离85
  tableHeight.value = String(window.innerHeight - 415);
  // 监听浏览器高度变化
  window.onresize = () => {
    tableHeight.value = String(window.innerHeight - 415);
  };
});
</script>

<style scoped lang="scss"></style>
