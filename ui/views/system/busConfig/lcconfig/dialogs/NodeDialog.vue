<!-- NodeDialog.vue -->
<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" status-icon>
      <el-form-item label="节点名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入节点名称" />
      </el-form-item>
      <el-form-item label="顺序号" prop="recno">
        <el-input-number v-model="formData.recno" :min="1" />
      </el-form-item>
      <el-form-item label="下一节点">
        <el-select v-model="formData.next_id" placeholder="选择下一节点" clearable>
          <el-option
            v-for="node in availableNodes"
            :key="node.id"
            :label="node.name"
            :value="node.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { VALIDATION_RULES } from '../utils/constants';
import { deepClone } from '../utils/utils';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  nodes: {
    type: Array,
    default: () => []
  },
  currentNode: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['update:visible', 'save']);

// 表单与对话框状态
const dialogVisible = ref(false);
const formRef = ref();
const formData = ref({
  name: '',
  recno: 1,
  next_id: undefined,
  actions: [],
  checks: [],
  jumps: []
});

// 计算属性
const dialogTitle = computed(() => {
  return formData.value.id ? '编辑节点' : '新增节点';
});

// 可选的下一节点（排除自己）
const availableNodes = computed(() => {
  if (!formData.value.id) {
    return props.nodes;
  }
  return props.nodes.filter(node => node.id !== formData.value.id);
});

// 表单验证规则
const rules = VALIDATION_RULES.node;

// 监听visible变化
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val;
    if (val && props.currentNode) {
      // 编辑模式,复制当前节点数据
      formData.value = deepClone(props.currentNode);
      // 兼容字段名差异
      if (props.currentNode.nextId !== undefined) {
        formData.value.next_id = props.currentNode.nextId;
      }
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

// 方法定义
const resetForm = () => {
  formData.value = {
    name: '',
    recno: 1,
    next_id: undefined,
    actions: [],
    checks: [],
    jumps: []
  };
};

const handleClose = () => {
  formRef.value?.resetFields();
  emit('update:visible', false);
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    // 构建保存的数据
    const saveData = deepClone(formData.value);

    // 处理字段名一致性
    if (saveData.next_id !== undefined) {
      saveData.nextId = saveData.next_id;

      // 设置nextNode名称
      const nextNode = props.nodes.find(n => n.id === saveData.next_id);
      saveData.nextNode = nextNode ? nextNode.name : '';
    }

    emit('save', saveData);
    handleClose();
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};
</script>

<style scoped>
/* 可以添加样式 */
:deep(.el-form-item__label) {
  font-weight: 500;
}
</style>
