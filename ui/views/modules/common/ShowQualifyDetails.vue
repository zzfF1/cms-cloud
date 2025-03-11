<template>
  <div>
    <el-dialog title="险种授权计算详情" v-model="dialogVisible" width="1000px" style="height: 500px;" :close-on-click-modal="false">
      <el-table ref="tableRef" :data="dataList" height="350px" v-loading="loading" size="small" :row-class-name="tableRowClassName">
        <el-table-column label="序号" type="index" width="50" align="center" />
        <el-table-column label="资质代码" align="center" prop="code" width="80" />
        <el-table-column label="资质名称" align="center" prop="name" width="200" :show-overflow-tooltip="true" />
        <el-table-column label="管理机构要求" align="center">
          <el-table-column label="适用范围" align="center" prop="manageComRange" width="110" :show-overflow-tooltip="true" />
          <el-table-column label="值" align="center" prop="manageComVal" width="110" />
          <el-table-column label="结果" align="center" prop="manageState" width="100" />
        </el-table-column>
        <el-table-column label="资质要求" align="center">
          <el-table-column label="适用范围" align="center" prop="qualifyRange" width="110" />
          <el-table-column label="值" align="center" prop="qualifyVal" width="110" />
          <el-table-column label="结果" align="center" prop="qualifyValState" width="100" />
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="closeDialog">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">

import { agentRiskCalProcess } from '@/api/busgrp/agent/agentRiskQuery'
import { AgentRiskCodeVo } from '@/api/busgrp/agent/agentRiskQuery/types'

//加载
const loading = ref(false);
//驳回对话框
const dialogVisible = ref(false);
//流程数据
const dataList = ref<AgentRiskCodeVo[]>([]);

const tableRowClassName = ({row, rowIndex }: {row:AgentRiskCodeVo ; rowIndex: number }) => {
  if (row.state === 2) {
    return 'warning-row'
  } else if (row.state === 1) {
    return 'success-row'
  }
  return '';
}

/**
 * 显示资质计算详情
 * @param agentCode 工号
 * @param riskCode 险种
 */
const getList = async (agentCode: string, riskCode: string) => {
  loading.value = true;
  const res = await agentRiskCalProcess(agentCode, riskCode);
  dataList.value = res.rows;
  loading.value = false;
}

/**
 * 显示
 * @param agentCode 工号
 * @param riskCode 险种
 */
const showDialog = async (agentCode: string, riskCode: string) => {
  dialogVisible.value = true;
  await getList(agentCode, riskCode);
}
/**
 * 关闭
 */
const closeDialog = () => {
  dataList.value = [];
  dialogVisible.value = false;
}

//暴露方法
defineExpose({ showDialog });
</script>

<style scoped lang="scss">
:deep(.el-table .warning-row) {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
:deep(.el-table .success-row) {
  --el-table-tr-bg-color: var(--el-color-success-light-9);
}
</style>
