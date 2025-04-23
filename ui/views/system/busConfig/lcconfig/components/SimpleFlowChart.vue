<!-- SimpleFlowChart.vue -->
<template>
  <div class="flow-chart">
    <div class="flow-nodes">
      <template v-if="prevNode">
        <div class="node prev-node" @click="$emit('select-node', prevNode)">
          {{ prevNode.name }}
        </div>
        <div class="arrow">→</div>
      </template>

      <div class="node current-node">
        {{ currentNode.name }}
      </div>

      <template v-if="nextNode">
        <div class="arrow">→</div>
        <div class="node next-node" @click="$emit('select-node', nextNode)">
          {{ nextNode.name }}
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  currentNode: {
    type: Object,
    required: true
  },
  prevNode: Object,
  nextNode: Object
});

defineEmits(['select-node']);
</script>

<style scoped>
.flow-chart {
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.flow-nodes {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.node {
  padding: 8px 16px;
  border-radius: 4px;
}

.prev-node,
.next-node {
  background-color: #e0e0e0;
  cursor: pointer;
}

.prev-node:hover,
.next-node:hover {
  background-color: #d0d0d0;
}

.current-node {
  padding: 8px 24px;
  background-color: #bbdefb;
  border: 1px solid #2196f3;
  font-weight: bold;
}

.arrow {
  color: #757575;
}
</style>
