<!-- NodeBasicInfo.vue -->
<template>
  <div class="node-basic-info">
    <h3>基本信息</h3>

    <el-form :model="formData" label-width="100px" class="form-container">
      <el-form-item label="节点名称" prop="name">
        <el-input v-model="formData.name" />
      </el-form-item>

      <el-form-item label="顺序号" prop="recno">
        <el-input-number v-model="formData.recno" :min="1" />
      </el-form-item>

      <el-form-item label="下一节点">
        <el-select v-model="formData.nextId" placeholder="选择下一节点" @change="handleNextNodeChange" clearable>
          <el-option v-for="node in availableNodes" :key="node.id" :label="node.name" :value="node.id" />
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { deepClone } from '../utils/utils';

const props = defineProps({
  node: {
    type: Object,
    required: true
  },
  nodes: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update']);

// 表单数据
const formData = ref(deepClone(props.node));

// 监听props变化
watch(
  () => props.node,
  (newVal) => {
    formData.value = deepClone(newVal);
  },
  { deep: true }
);

// 可选的下一节点（排除自己）
const availableNodes = computed(() => {
  return props.nodes.filter((node) => node.id !== props.node.id);
});

// 处理下一节点变更
const handleNextNodeChange = (nextId) => {
  // 更新nextNode字段
  const nextNode = props.nodes.find((node) => node.id === nextId);
  formData.value.nextNode = nextNode ? nextNode.name : '';

  // 通知父组件更新
  emit('update', deepClone(formData.value));
};

// 监听表单数据变化
watch(
  () => formData.value.name,
  () => emit('update', deepClone(formData.value))
);

watch(
  () => formData.value.recno,
  () => emit('update', deepClone(formData.value))
);
</script>

<style scoped>
.node-basic-info {
  padding: 16px 0;
}

h3 {
  margin-top: 0;
  margin-bottom: 20px;
}

.form-container {
  max-width: 500px;
}
</style>
