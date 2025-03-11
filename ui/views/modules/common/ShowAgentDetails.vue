<template>
  <div>
    <el-drawer :title="dialog.title" v-model="dialog.visible" size="70%" append-to-body>
      <el-descriptions title="基础信息" :column="3" style="width: 100%" border>
        <el-descriptions-item labelStyle="width: 150px" contentStyle="width: 250px">
          <template #label>
            <div class="cell-item">工号</div>
          </template>
          {{ agentInfoForm.agentFormVo?.agentCode }}
        </el-descriptions-item>
        <el-descriptions-item labelStyle="width: 150px" contentStyle="width: 250px">
          <template #label>
            <div class="cell-item">姓名</div>
          </template>
          {{ agentInfoForm.agentFormVo?.name }}
        </el-descriptions-item>
        <el-descriptions-item labelStyle="width: 150px" contentStyle="width: 250px">
          <template #label>
            <div class="cell-item">年龄</div>
          </template>
          {{ agentInfoForm.agentFormVo?.age }}
        </el-descriptions-item>
        <el-descriptions-item labelStyle="width: 150px" contentStyle="width: 250px">
          <!--          <template #label>-->
          <!--            <div class="cell-item">证件类型</div>-->
          <!--          </template>-->
          {{ agentInfoForm.agentFormVo?.idNoType }}
        </el-descriptions-item>
        <el-descriptions-item labelStyle="width: 150px" contentStyle="width: 250px">
          <!--          <template #label>-->
          <!--            <div class="cell-item">证件号</div>-->
          <!--          </template>-->
          {{ agentInfoForm.agentFormVo?.idNo }}
        </el-descriptions-item>
        <el-descriptions-item labelStyle="width: 150px" contentStyle="width: 250px">
          <!--          <template #label>-->
          <!--            <div class="cell-item">出生日期</div>-->
          <!--          </template>-->
          {{ agentInfoForm.agentFormVo?.birthday }}
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions title="Vertical list with border" direction="vertical" :column="3" border>
        <el-descriptions-item label="Username">123</el-descriptions-item>
        <el-descriptions-item label="Telephone">123</el-descriptions-item>
        <el-descriptions-item label="Place">123</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
//对话框显示状态
import { AgentDetailInfo } from "@/api/ind/indagent/agentApply/types";
import { getAgentTemp } from "@/api/ind/indagent/agentApply";

const dialog = reactive<DialogOption>({
  visible: false,
  title: ""
});
//参数
const agentInfoForm = reactive<AgentDetailInfo>({
  agentFormVo: undefined,
  treeFormVo: undefined,
  qualifications: [],
  relatives: []
});

/** 表单重置 */
const reset = () => {
  //重置
  agentInfoForm.agentFormVo = {};
  agentInfoForm.treeFormVo = {};
  agentInfoForm.relatives = [];
  agentInfoForm.qualifications = [];
};

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
};

/** 显示 */
const showDialog = async (agentCode: any) => {
  reset();
  dialog.title = "用户详情";
  const res = await getAgentTemp(agentCode);
  Object.assign(agentInfoForm, res.data);
  dialog.visible = true;
};
//暴露方法
defineExpose({ showDialog });
</script>

<style scoped lang="scss">
.cell-item {
  display: flex;
  align-items: center;
}

:deep(.el-drawer__body) {
  padding-top: 0px !important;
}
</style>
