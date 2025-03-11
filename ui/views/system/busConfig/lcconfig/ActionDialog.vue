# ActionDialog.vue
<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" status-icon>
      <el-form-item label="动作名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入动作名称" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择类型">
          <el-option v-for="(name, value) in actionTypes" :key="value" :label="name" :value="Number(value)" />
        </el-select>
      </el-form-item>
      <el-form-item label="动作类型" prop="dzType">
        <el-select v-model="formData.dzType" placeholder="请选择动作类型">
          <el-option v-for="(name, value) in dzTypes" :key="value" :label="name" :value="Number(value)" />
        </el-select>
      </el-form-item>
      <el-form-item label="动作内容" prop="dz">
        <el-input v-model="formData.dz" type="textarea" :rows="4" :placeholder="formData.dzType === 0 ? '请输入SQL语句' : '请输入类名'" />
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
import type { LcDefineVo, LcDzVo } from '@/api/system/lcconfig/types';
interface ActionForm {
  serialno?: number;
  name: string;
  type: number;
  dzType: number;
  dz: string;
  recno: number;
}

const props = defineProps({
  visible: { type: Boolean, default: false },
  actionTypes: { type: Object, default: () => ({}) },
  dzTypes: { type: Object, default: () => ({}) },
  currentAction: { type: Object as PropType<LcDzVo>, default: null },
  currentNode: { type: Object as PropType<LcDefineVo>, default: null }
});
const emit = defineEmits(['update:visible', 'save']);

// 表单与对话框状态
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
const formData = ref<ActionForm>({
  name: '',
  type: 0,
  dzType: 0,
  dz: '',
  recno: 1
});

// 计算属性
const dialogTitle = computed(() => {
  return formData.value.serialno ? '编辑动作' : '新增动作';
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入动作名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  dz: [{ required: true, message: '请输入动作内容', trigger: 'blur' }],
  dzType: [{ required: true, message: '请选择动作类型', trigger: 'change' }]
};

// 监听visible变化
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val;
    if (val && props.currentAction) {
      formData.value = { ...props.currentAction };
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
    dzType: 0,
    dz: '',
    recno: 1
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
    // 确保 actions 数组存在
    if (!props.currentNode.actions) {
      // eslint-disable-next-line vue/no-mutating-props
      props.currentNode.actions = [];
    }
    if (formData.value.serialno) {
      const index = props.currentNode.actions.findIndex((a) => a.serialno === formData.value.serialno);
      if (index !== -1) {
        // eslint-disable-next-line vue/no-mutating-props
        props.currentNode.actions[index] = { ...formData.value };
      }
    } else {
      const newAction = {
        ...formData.value
      };
      // eslint-disable-next-line vue/no-mutating-props
      props.currentNode.actions.push(newAction);
    }
    handleClose();
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};
</script>
