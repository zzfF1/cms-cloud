# TypeDialog.vue
<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="流程类型名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入流程类型名称" />
      </el-form-item>
      <el-form-item label="顺序号" prop="recno">
        <el-input-number v-model="formData.recno" :min="1" :step="1" />
      </el-form-item>
      <el-form-item label="业务表" prop="lcTable">
        <el-input v-model="formData.lcTable" placeholder="请输入业务表名" />
      </el-form-item>
      <el-form-item label="流程字段" prop="lcField">
        <el-input v-model="formData.lcField" placeholder="请输入流程字段名" />
      </el-form-item>
      <el-form-item label="业务表主键" prop="idField">
        <el-input v-model="formData.idField" placeholder="请输入主键字段名" />
      </el-form-item>
      <el-form-item label="驳回字段" prop="rejectField">
        <el-input v-model="formData.rejectField" placeholder="请输入驳回字段名" />
      </el-form-item>
      <el-form-item label="驳回原因字段" prop="rejectField2">
        <el-input v-model="formData.rejectField2" placeholder="请输入驳回原因字段名" />
      </el-form-item>
      <el-form-item label="是否多主键" prop="mulkey">
        <el-radio-group v-model="formData.mulkey">
          <el-radio :label="0">单主键</el-radio>
          <el-radio :label="1">多主键</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance } from 'element-plus';

interface TypeForm {
  serialno?: number | null;
  name: string;
  recno: number;
  lcTable: string;
  lcField: string;
  idField: string;
  rejectField: string;
  rejectField2: string;
  mulkey: number;
}

interface Props {
  visible: boolean;
  currentType?: TypeForm | null;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:visible', 'save']);

// 表单与对话框状态
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
const formData = ref<TypeForm>({
  name: '',
  recno: 1,
  lcTable: '',
  lcField: '',
  idField: '',
  rejectField: '',
  rejectField2: '',
  mulkey: 0
});

// 计算属性
const dialogTitle = computed(() => {
  return formData.value.serialno ? '编辑流程类型' : '新增流程类型';
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入流程类型名称', trigger: 'blur' }],
  lcTable: [{ required: true, message: '请输入业务表名', trigger: 'blur' }],
  lcField: [{ required: true, message: '请输入流程字段名', trigger: 'blur' }],
  idField: [{ required: true, message: '请输入主键字段名', trigger: 'blur' }],
  rejectField: [{ required: true, message: '请输入驳回字段名', trigger: 'blur' }],
  rejectField2: [{ required: true, message: '请输入驳回原因字段名', trigger: 'blur' }]
};

// 监听visible变化
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val;
    if (val && props.currentType) {
      // 编辑模式,复制当前类型数据
      formData.value = {
        ...formData.value,
        ...props.currentType,
        // 转换字段名称为驼峰
        lcTable: props.currentType.lcTable || props.currentType.lc_table || '',
        lcField: props.currentType.lcField || props.currentType.lc_field || '',
        idField: props.currentType.idField || props.currentType.id_field || '',
        rejectField: props.currentType.rejectField || props.currentType.reject_field || '',
        rejectField2: props.currentType.rejectField2 || props.currentType.reject_field2 || ''
      };
    } else {
      // 新增模式,重置表单
      resetForm();
    }
  }
);

// 监听dialogVisible变化
watch(dialogVisible, (val) => {
  emit('update:visible', val);
});

// 重置表单
const resetForm = () => {
  formData.value = {
    name: '',
    recno: 1,
    lcTable: '',
    lcField: '',
    idField: '',
    rejectField: '',
    rejectField2: '',
    mulkey: 0
  };
  formRef.value?.resetFields();
};

// 关闭对话框
const handleClose = () => {
  resetForm();
  emit('update:visible', false);
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    // 转换回下划线格式
    const submitData = {
      ...formData.value,
      lc_table: formData.value.lcTable,
      lc_field: formData.value.lcField,
      id_field: formData.value.idField,
      reject_field: formData.value.rejectField,
      reject_field2: formData.value.rejectField2
    };

    emit('save', submitData);
    handleClose();
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};
</script>
