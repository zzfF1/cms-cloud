<template>
  <div>
    <el-dialog v-model="dialog.visible" :title="dialog.title" append-to-body width="500px">
      <el-form ref="entityInfoFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标识" prop="entityIdentify">
          <el-input v-model="form.entityIdentify" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="名称" prop="entityName">
          <el-input v-model="form.entityName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="包路径" prop="pkgName">
          <el-input v-model="form.pkgName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="描述" prop="entityDesc">
          <el-input v-model="form.entityDesc" placeholder="请输入" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { EntityInfoForm, EntityInfoQuery } from '@/api/system/rule/entityInfo/types'
import { addEntityInfo, getEntityInfo, updateEntityInfo } from '@/api/system/rule/entityInfo'

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const entityInfoFormRef = ref<ElFormInstance>();
//加载状态
const buttonLoading = ref(false);
const initFormData: EntityInfoForm = {
  entityId: undefined,
  entityName: undefined,
  entityDesc: undefined,
  entityIdentify: undefined,
  pkgName: undefined,
  isEffect: undefined,
  remark: undefined
}
const data = reactive<PageData<EntityInfoForm, EntityInfoQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1, // 默认页码
    pageSize: 10 // 默认每页条数
  },
  rules: {
    entityName: [
      { required: true, message: "名称不能为空", trigger: "blur" }
    ],
    entityIdentify: [
      { required: true, message: "标识不能为空", trigger: "blur" }
    ],
    pkgName: [
      { required: true, message: "包路径不能为空", trigger: "blur" }
    ]
  }
});
const { form, rules } = toRefs(data);
//对话框显示状态
const dialog = reactive<DialogOption>({
  visible: false,
  title: ""
});
//回调函数
const emit = defineEmits(["getList"]);
/** 提交按钮 */
const submitForm = () => {
  entityInfoFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.entityId) {
        await updateEntityInfo(form.value).finally(() => buttonLoading.value = false);
      } else {
        await addEntityInfo(form.value).finally(() => buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("修改成功");
      dialog.visible = false;
      //调用父组件方法
      emit("getList");
    }
  });
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  entityInfoFormRef.value?.resetFields();
}

const showDialog = async (entityId: any) => {
  if (!entityId) {
    dialog.title = "添加变量";
  } else {
    dialog.title = "修改变量";
    const res = await getEntityInfo(entityId);
    Object.assign(form.value, res.data);
  }
  dialog.visible = true;
}

//暴露方法
defineExpose({ showDialog });
</script>

<style lang="scss" scoped></style>
