# JumpDialog.vue
<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" status-icon>
      <el-form-item label="顺序号" prop="recno">
        <el-input-number v-model="formData.recno" :min="1" />
      </el-form-item>
      <el-form-item label="下一节点" prop="lcNextId">
        <el-select v-model="formData.lcNextId" placeholder="请选择下一节点">
          <el-option v-for="node in nodes" :key="node.id" :label="node.name" :value="node.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="跳转条件" prop="tzTj">
        <el-input v-model="formData.tzTj" type="textarea" :rows="4" placeholder="请输入跳转条件" />
      </el-form-item>
      <el-form-item label="说明" prop="sm">
        <el-input v-model="formData.sm" placeholder="请输入跳转说明" />
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
import type { LcDefineVo, LcTzVo } from '@/api/system/lcconfig/types';

interface JumpForm {
  serialno?: number;
  recno: number;
  lcNextId?: number;
  tzTj: string;
  sm: string;
  nextNodeName?: string;
}
const props = defineProps({
  visible: { type: Boolean, default: false },
  nodes: { type: Array, default: () => [] },
  currentJump: { type: Object as PropType<LcTzVo>, default: null },
  currentNode: { type: Object as PropType<LcDefineVo>, default: null }
});
const emit = defineEmits(['update:visible', 'save']);

// 表单与对话框状态
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
const formData = ref<JumpForm>({
  recno: 1,
  tzTj: '',
  sm: ''
});

// 计算属性
const dialogTitle = computed(() => {
  return formData.value.serialno ? '编辑跳转' : '新增跳转';
});

// 表单验证规则
const rules = {
  recno: [{ required: true, message: '请输入顺序号', trigger: 'blur' }],
  lcNextId: [{ required: true, message: '请选择下一节点', trigger: 'change' }],
  tzTj: [{ required: true, message: '请输入跳转条件', trigger: 'blur' }],
  sm: [{ required: true, message: '请输入说明', trigger: 'blur' }]
};

// 监听visible变化
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val;
    if (val && props.currentJump) {
      formData.value = { ...props.currentJump };
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
    recno: 1,
    tzTj: '',
    sm: ''
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
    // 确保 jumps 数组存在
    if (!props.currentNode.jumps) {
      // eslint-disable-next-line vue/no-mutating-props
      props.currentNode.jumps = [];
    }
    const nextNode = props.nodes.find((n) => n.id === formData.value.lcNextId);
    const saveData = {
      ...formData.value,
      nextNodeName: nextNode?.name || ''
    };
    if (formData.value.serialno) {
      const index = props.currentNode.jumps.findIndex((j) => j.serialno === formData.value.serialno);
      if (index !== -1) {
        // eslint-disable-next-line vue/no-mutating-props
        props.currentNode.jumps[index] = saveData;
      }
    } else {
      const newJump = {
        ...saveData
      };
      // eslint-disable-next-line vue/no-mutating-props
      props.currentNode.jumps.push(newJump);
    }
    handleClose();
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};
</script>
