<template>
  <!-- 添加或修改规则配置对话框 -->
  <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
    <el-form ref="elMainFormRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="规则代码" prop="calCode">
        <el-input v-model="form.calCode" placeholder="请输入" :disabled="form.id !== undefined" />
      </el-form-item>
      <el-form-item label="规则说明" prop="message">
        <el-input v-model="form.message" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="版本号" prop="calVersion">
        <el-input v-model="form.calVersion" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-input v-model="form.type" placeholder="请输入" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ElMainForm } from "@/api/system/rule/types";
import { addElMain, getElMain, updateElMain } from "@/api/system/rule";

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
//加载状态
const buttonLoading = ref(false);
const initFormData: ElMainForm = {
  id: undefined,
  type: undefined,
  calCode: undefined,
  message: undefined,
  calVersion: undefined,
  createDate: undefined
};
//表单数据
const form = ref(initFormData);
// 表单校验
const rules = ref({
  calCode: [{ required: true, message: "计算代码不能为空", trigger: "blur" }],
  message: [{ required: true, message: "说明不能为空", trigger: "blur" }],
  calVersion: [{ required: true, message: "版本号不能为空", trigger: "blur" }]
});
// 表单ref
const elMainFormRef = ref<ElFormInstance>();
//回调函数
const emit = defineEmits(["getList"]);

//对话框显示状态
const dialog = reactive<DialogOption>({
  visible: false,
  title: ""
});

const cancel = () => {
  reset();
  dialog.visible = false;
};

const reset = () => {
  form.value = { ...initFormData };
  elMainFormRef.value?.resetFields();
};

/** 提交按钮 */
const submitForm = () => {
  elMainFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateElMain(form.value).finally(() => buttonLoading.value = false);
      } else {
        await addElMain(form.value).finally(() => buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("保存成功");
      dialog.visible = false;
      //调用父组件方法
      emit("getList");
    }
  });
};

const showDialog = async (id: any) => {
  reset();
  if (!id) {
    dialog.title = "添加规则";
  } else {
    dialog.title = "修改规则";
    const res = await getElMain(id);
    Object.assign(form.value, res.data);
  }
  dialog.visible = true;
};
//暴露方法
defineExpose({ showDialog });
</script>

<style scoped lang="scss"></style>
