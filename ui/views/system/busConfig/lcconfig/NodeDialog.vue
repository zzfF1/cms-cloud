# NodeDialog.vue
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
        <el-select v-model="formData.next_id" placeholder="选择下一节点">
          <el-option v-for="node in nodes" :key="node.id" :label="node.name" :value="node.id" />
        </el-select>
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
import { ElMessage } from 'element-plus';
import type { FormInstance } from 'element-plus';

interface NodeForm {
  id?: number;
  name: string;
  recno: number;
  next_id?: number;
  actions: any[];
  checks: any[];
  jumps: any[];
}

interface Props {
  visible: boolean;
  nodes: any[];
  currentNode?: NodeForm;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:visible', 'save']);

// 表单与对话框状态
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
const formData = ref<NodeForm>({
  name: '',
  recno: 1,
  actions: [],
  checks: [],
  jumps: []
});

// 计算属性
const dialogTitle = computed(() => {
  return formData.value.id ? '编辑节点' : '新增节点';
});

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入节点名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  recno: [{ required: true, message: '请输入顺序号', trigger: 'blur' }]
};

// 监听visible变化
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val;
    if (val && props.currentNode) {
      // 编辑模式,复制当前节点数据
      formData.value = { ...props.currentNode };
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
    const saveData = {
      ...formData.value,
      nextNode: props.nodes.find((n) => n.id === formData.value.next_id)?.name || ''
    };

    emit('save', saveData);
    handleClose();
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};
</script>
