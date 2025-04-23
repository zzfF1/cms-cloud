<!-- TypeTree.vue -->
<template>
  <div class="type-tree">
    <div class="type-header">
      <h3 class="title">流程类型</h3>
      <el-button type="primary" size="small" @click="$emit('add-type')"> 添加类型 </el-button>
    </div>

    <div class="type-content">
      <el-tree
        :data="types"
        node-key="serialno"
        :props="{ label: 'name' }"
        @node-click="(data) => !data.children && $emit('select-type', data)"
        :highlight-current="true"
        :current-node-key="currentType?.serialno"
      >
        <template #default="{ node, data }">
          <div class="custom-tree-node">
            <span>{{ data.serialno }}-{{ node.label }}</span>
            <span v-if="!data.children" class="operations">
              <el-button link type="primary" @click.stop="$emit('edit-type', data)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button link type="danger" @click.stop="$emit('delete-type', data)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </span>
          </div>
        </template>
      </el-tree>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Edit, Delete } from '@element-plus/icons-vue';

defineProps({
  types: {
    type: Array,
    default: () => []
  },
  currentType: {
    type: Object,
    default: null
  }
});

defineEmits(['select-type', 'add-type', 'edit-type', 'delete-type']);
</script>

<style scoped>
.type-tree {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.type-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.type-content {
  flex: 1;
  overflow: auto;
  padding: 8px 0;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.operations {
  display: none;
}

:deep(.el-tree-node__content:hover) .operations {
  display: inline-flex;
}
</style>
