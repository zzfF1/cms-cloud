<template>
  <div>
    <el-dialog v-model="dialogVisible" :close-on-click-modal="false" style="height: 500px;" title="错误明细"
               width="900px">
      <el-table ref="tableRef" :data="errorMsgList" default-expand-all height="350px" row-key="msg" size="small">
        <el-table-column align="center" label="序号" type="index" width="50" />
        <el-table-column align="left" label="错误说明" prop="msg" />
      </el-table>
      <template #footer>
        <el-button type="primary" @click="closeDialog">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
//驳回对话框
const dialogVisible = ref(false);
//流程数据
const errorMsgList = ref<any[]>([]);
/**
 * 显示
 * @param errorMsg 错误信息
 */
const show = (errorMsg: any) => {
  dialogVisible.value = true;
  errorMsgList.value = errorMsg.data;
}
/**
 * 关闭
 */
const closeDialog = () => {
  errorMsgList.value = [];
  dialogVisible.value = false;
}
//暴露方法
defineExpose({ show });
</script>

<style lang="scss" scoped></style>
