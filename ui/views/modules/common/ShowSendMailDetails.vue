<template>
  <div>
    <el-dialog title="邮件预警详情" v-model="dialogVisible" width="1000px" :close-on-click-modal="false">
      <el-table ref="tableRef" :data="dataList" height="350px" v-loading="loading" size="small">
        <el-table-column label="序号" type="index" width="50" align="center" />
        <el-table-column label="收件人姓名" align="left" prop="userName" width="120" />
        <el-table-column label="收件人邮箱" align="left" prop="recipient" width="180" :show-overflow-tooltip="true" />
        <el-table-column label="主题" align="left" prop="subject" width="200" :show-overflow-tooltip="true" />
        <el-table-column label="内容" align="left" prop="body" width="280" :show-overflow-tooltip="true" />
        <el-table-column label="发送日期" align="left" prop="sendTime" width="130" :show-overflow-tooltip="true" />
      </el-table>
      <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      <template #footer>
        <el-button type="primary" @click="closeDialog">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">

import { ComEmailSendWarnVo } from '@/api/busgrp/intermediary/LaComInput/types'
import { listComMailWarn } from '@/api/busgrp/intermediary/LaComInput'

const total = ref(0);
//加载
const loading = ref(false);
//驳回对话框
const dialogVisible = ref(false);
//流程数据
const dataList = ref<ComEmailSendWarnVo[]>([]);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  mailCode: ''
});
/**
 * 显示资质计算详情
 * @param mailCode 工号
 */
const getList = async (mailCode: string) => {
  loading.value = true;
  const res = await listComMailWarn(queryParams);
  dataList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/**
 * 显示
 * @param mailCode 邮件编号
 */
const showDialog = async (mailCode: string) => {
  dialogVisible.value = true;
  await getList(mailCode);
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
