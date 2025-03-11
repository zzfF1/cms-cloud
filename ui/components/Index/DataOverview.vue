<template>
  <el-card class="data-overview-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <span>个人数据概览</span>
        <div class="header-actions">
          <i class="el-icon-refresh" @click="refreshData"></i>
        </div>
      </div>
    </template>

    <div class="data-overview">
      <div v-for="(item, index) in dataItems" :key="index" class="data-item">
        <div class="data-label">{{ item.label }}</div>
        <div class="data-value">{{ item.value }}</div>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { defineExpose, ref } from 'vue';

interface DataItem {
  label: string;
  value: number | string;
}

// 个人数据项
const dataItems = ref<DataItem[]>([
  { label: '今日工作量', value: 0 },
  { label: '本周工作量', value: 0 },
  { label: '本月工作量', value: 0 },
  { label: '今日销售', value: 0 },
  { label: '本周销售', value: 0 },
  { label: '本月销售', value: 0 }
]);

// 刷新数据
const refreshData = () => {
  // 这里可以添加从后端获取最新数据的逻辑
  console.log('刷新数据概览');
};

// 暴露给父组件的方法
defineExpose({
  dataItems,
  refreshData
});
</script>

<style scoped>
.data-overview-card {
  margin-bottom: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.header-actions i {
  cursor: pointer;
  font-size: 14px;
  color: #909399;
  transition: color 0.2s;
}

.header-actions i:hover {
  color: #409EFF;
}

.data-overview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  padding: 8px 0;
}

.data-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 6px 0;
}

.data-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
}

.data-value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style>
