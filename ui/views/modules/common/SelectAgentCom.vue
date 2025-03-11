<template>
  <el-input v-model="inputShowVal" :style="{ width: wid }" placeholder="请选择" readonly>
    <template #append>
      <el-button :disabled="props.isEnabled" @click="show">选择</el-button>
    </template>
  </el-input>
  <el-row>
    <el-dialog v-model="visible" title="选择销售机构" width="60%" top="2vh" append-to-body>
      <el-form ref="queryFormRef" :model="queryParams" label-width="80px" size="small" :inline="true">
        <el-form-item label="机构代码" prop="branchattr">
          <el-input v-model="queryParams.branchattr" placeholder="" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="机构名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="主管代码" prop="branchManager">
          <el-input v-model="queryParams.branchmanager" placeholder="" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="主管姓名" prop="branchManagerName">
          <el-input v-model="queryParams.branchmanagername" placeholder="" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showEndFlag" label="是否停业" prop="endFlag">
          <i-select v-model="queryParams.endflag" dict-type="sys_yes_no"></i-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="branchGroupList"
        border
        height="400px"
        show-overflow-tooltip
        size="small"
        @select="clickRow"
      >
        <el-table-column type="selection" width="35">
          <template #header>
            <el-input size="small" placeholder=" " />
          </template>
        </el-table-column>
        <el-table-column v-if="false" label="机构主键" align="center" prop="agentGroup" />
        <el-table-column label="管理机构" prop="manageComName" width="250">
          <template #default="scope">
            <div>{{ scope.row.managecom }}-{{ scope.row.managecomname }}</div>
          </template>
        </el-table-column>
        <el-table-column label="销售机构" prop="branchattr" width="300">
          <template #default="scope">
            <div>{{ scope.row.branchattr }}-{{ scope.row.name }}</div>
          </template>
        </el-table-column>
        <el-table-column label="级别" align="center" prop="branchlevelname" width="100" />
        <el-table-column label="主管" align="center" prop="branchmanagername" width="120" />
        <el-table-column label="状态" align="center" prop="endflagname" width="80" />
        <el-table-column label="操作" align="center" fixed="right" width="90">
          <template #default="scope">
            <el-button link type="primary" icon="Select" @click="handleSel(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-if="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSelectBranch">确 定</el-button>
          <el-button @click="visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </el-row>
</template>

<script setup lang="ts">
// 保持原有的 script 部分不变
import { listBaseBranch } from '@/api/common/branch';
import { BaseBranchShowVO, BranchQuery } from '@/api/common/branch/types';
import ISelect from '@/views/modules/common/ISelect.vue';

const props = defineProps({
  modelValue: { type: [Number, String], default: '' },
  showVal: { type: String, default: '' },
  endFlagState: { type: String, default: '' },
  branchLevels: { type: String, default: '' },
  selManageCom: { type: String, default: '' },
  branchType: { type: String, required: true, default: '0' },
  isEnabled: { type: Boolean, default: false },
  wid: { type: String },
  showHaveAgent: { type: Boolean, default: false }
});

const inputShowVal = ref(props.showVal);
const selectedValue = ref(props.modelValue);
const currentRow = ref();
const showEndFlag = ref(props.endFlagState !== undefined);
const branchGroupList = ref<BaseBranchShowVO[]>([]);
const visible = ref(false);
const total = ref(0);
const loading = ref(true);

const queryParams = reactive<BranchQuery>({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  branchattr: undefined,
  managecom: undefined,
  branchtype: props.branchType,
  branchlevels: undefined,
  endflag: 'N',
  branchmanager: undefined,
  branchmanagername: undefined
});

const tableRef = ref<ElTableInstance>();
const queryFormRef = ref<ElFormInstance>();
const emit = defineEmits(['update:modelValue', 'callback', 'setSelBranch']);

watch(
  () => props.showVal,
  (newVal) => {
    inputShowVal.value = newVal.toString();
  }
);

watch(
  () => props.modelValue,
  (newVal) => {
    if (!newVal) {
      inputShowVal.value = '';
    }
  }
);

const show = () => {
  let result = true;
  emit(`callback`, currentRow.value, (val: boolean) => (result = val));
  if (!result) return;
  getList();
  visible.value = true;
};

const clickRow = (selection: any, row: any) => {
  tableRef.value?.clearSelection();
  if (selection.length === 0) return;
  if (row) {
    currentRow.value = row;
    tableRef.value?.toggleRowSelection(row, true);
  }
};

const getList = async () => {
  loading.value = true;
  queryParams.managecom = props.selManageCom;
  queryParams.branchlevels = props.branchLevels;
  if (props.showHaveAgent) {
    queryParams.showhaveagent = props.showHaveAgent;
  }
  if (props.endFlagState) {
    queryParams.endflag = props.endFlagState;
  }
  const res = await listBaseBranch(queryParams);
  branchGroupList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

const resetQuery = () => {
  queryFormRef.value?.resetFields();
  getList();
};

const handleSelectBranch = async () => {
  if (currentRow.value === undefined) {
    ElMessage({ message: '请选择销售机构!', type: 'warning' });
    return;
  }
  inputShowVal.value = currentRow.value.branchattr + '-' + currentRow.value.name;
  selectedValue.value = currentRow.value.agentgroup;
  emit('update:modelValue', selectedValue.value);
  emit(`setSelBranch`, currentRow.value);
  visible.value = false;
};

const handleSel = (row: BaseBranchShowVO) => {
  currentRow.value = row;
  inputShowVal.value = row.branchattr + '-' + row.name;
  selectedValue.value = row.agentgroup;
  emit('update:modelValue', selectedValue.value);
  emit(`setSelBranch`, row);
  visible.value = false;
};
</script>

<style scoped>
:deep(.el-table__header-wrapper .el-checkbox) {
  display: none;
}
.el-form--inline .el-form-item {
  margin-right: 8px;
  margin-bottom: 8px;
}
</style>
