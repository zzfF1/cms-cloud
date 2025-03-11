<template>
  <div>
    <!--  弹窗录入驳回原因  -->
    <el-dialog v-model="dialog.visible" :close-on-click-modal="false" :title="dialog.title" width="35%">
      <el-form ref="formRef" :model="formReas" :rules="rules" label-width="80px">
        <el-form-item :label="reasonLabel" prop="reason">
          <el-input v-model="formReas.reason" :rows="4" placeholder="请输入" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
//对话框显示状态
const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});
const emit = defineEmits(['ok']);
//驳回原因
const formReas = ref({ reason: undefined, type: undefined });
//表单ref
const formRef = ref<ElFormInstance>();
// 表单校验
const rules = ref({
  reason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }]
});
/** 显示对话框 */
const showDialog = async (type: any) => {
  reset();
  dialog.title = '审批意见';
  dialog.visible = true;
  formReas.value.type = type; // 保存 type 到 formReas 中
};
/** 表单重置 */
const reset = () => {
  formRef.value?.resetFields();
};
/** 取消 */
const cancel = () => {
  dialog.visible = false;
  reset();
};
/**
 * 确定
 */
const submitForm = () => {
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      emit('ok', { reason: formReas.value.reason, type: formReas.value.type });
      dialog.visible = false;
    }
  });
};

const reasonLabel = computed(() => {
  const type = formReas.value.type;
  if (type === 1 || type === 2) {
    return '通过说明';
  } else if (type === -1 || type === -2) {
    return '驳回说明';
  } else {
    return '原因';
  }
});
//暴露方法
defineExpose({
  showDialog
});
</script>

<style lang="scss" scoped></style>
