# CheckDialog.vue
<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" status-icon>
      <el-form-item label="检查名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入检查名称" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择类型">
          <el-option v-for="(name, value) in actionTypes" :key="value" :label="name" :value="Number(value)" />
        </el-select>
      </el-form-item>
      <el-form-item label="检查条件" prop="checkTj">
        <el-input v-model="formData.checkTj" type="textarea" :rows="4" placeholder="请输入SQL语句或实现类" />
      </el-form-item>
      <el-form-item label="提示信息" prop="checkMsg">
        <el-input v-model="formData.checkMsg" type="textarea" :rows="2" placeholder="请输入检查不通过时的提示信息" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import type { FormInstance } from 'element-plus';
import type { LcDefineVo, LcCheckVo } from '@/api/system/lcconfig/types';

interface CheckForm {
  serialno?: number;
  name: string;
  type: number;
  checkTj: string;
  checkMsg: string;
  recno: number;
  checkType: number;
}
const props = defineProps({
  visible: { type: Boolean, default: false },
  actionTypes: { type: Object, default: () => ({}) },
  currentCheck: { type: Object as PropType<LcCheckVo>, default: null },
  currentNode: { type: Object as PropType<LcDefineVo>, default: null }
});
const emit = defineEmits(['update:visible', 'save']);

// 表单与对话框状态
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
const formData = ref<CheckForm>({
  name: '',
  type: 0,
  checkTj: '',
  checkMsg: '',
  recno: 1,
  checkType: 0
});

// 计算属性
const dialogTitle = computed(() => {
  return formData.value.serialno ? '编辑检查' : '新增检查';
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入检查名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  checkTj: [{ required: true, message: '请输入检查条件', trigger: 'blur' }],
  checkMsg: [{ required: true, message: '请输入提示信息', trigger: 'blur' }]
};

// 监听visible变化
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val;
    if (val && props.currentCheck) {
      formData.value = { ...props.currentCheck };
    } else {
      resetForm();
    }
  }
);

// 监听dialogVisible变化
watch(dialogVisible, (val) => {
  emit('update:visible', val);
});

// 方法定义
const resetForm = () => {
  formData.value = {
    name: '',
    type: 0,
    checkTj: '',
    checkMsg: '',
    recno: 1,
    checkType: 0
  };
};

const handleClose = () => {
  formRef.value?.resetFields();
  emit('update:visible', false);
};

const handleSubmit = async () => {
  if (!formRef.value || !props.currentNode) return;
  try {
    await formRef.value.validate();
    // 确保 checks 数组存在
    if (!props.currentNode.checks) {
      // eslint-disable-next-line vue/no-mutating-props
      props.currentNode.checks = [];
    }
    if (formData.value.serialno) {
      const index = props.currentNode.checks.findIndex((c) => c.serialno === formData.value.serialno);
      if (index !== -1) {
        // eslint-disable-next-line vue/no-mutating-props
        props.currentNode.checks[index] = { ...formData.value };
      }
    } else {
      const newCheck = {
        ...formData.value
      };
      // eslint-disable-next-line vue/no-mutating-props
      props.currentNode.checks.push(newCheck);
    }
    handleClose();
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};
</script>
