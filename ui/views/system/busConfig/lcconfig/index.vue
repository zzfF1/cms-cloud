<!-- index.vue -->
<template>
  <div class="app-container">
    <!-- 左侧流程类型树 -->
    <div class="sidebar">
      <type-tree
        :types="flowTypes"
        :current-type="currentType"
        @select-type="selectType"
        @add-type="openTypeDialog()"
        @edit-type="openTypeDialog($event)"
        @delete-type="handleTypeDelete"
      />
    </div>

    <!-- 中间节点列表面板 -->
    <div v-if="currentType" class="node-list-panel">
      <div class="panel-header">
        <h3>节点列表</h3>
        <el-button type="primary" size="small" @click="handleNodeAdd">添加节点</el-button>
      </div>

      <!-- 节点表格 -->
      <el-table :data="nodes" @current-change="handleNodeSelect" highlight-current-row class="node-table">
        <el-table-column prop="name" label="节点名称" />
        <el-table-column prop="recno" label="顺序号" width="80" align="center" />
        <el-table-column prop="nextNode" label="下一节点" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row, $index }">
            <el-button link type="primary" @click.stop="handleNodeEdit(row)">编辑</el-button>
            <el-button link type="danger" @click.stop="handleNodeDelete($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 右侧详情配置区 -->
    <div v-if="currentNode" class="node-detail-panel">
      <!-- 详情面板头部 -->
      <div class="panel-header">
        <h3>{{ currentNode.name }} - 详细配置</h3>
        <div v-if="hasChanges" class="unsaved-changes">
          <span class="change-indicator">●</span>
          <span>有未保存的更改</span>
          <el-button type="success" size="small" @click="saveCurrentNode"> 保存更改 </el-button>
        </div>
      </div>

      <!-- 简易流程图 -->
      <simple-flow-chart :current-node="currentNode" :prev-node="prevNode" :next-node="nextNode" @select-node="handleNodeSelect" />

      <!-- 配置选项卡 -->
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <node-basic-info :node="currentNode" :nodes="nodes" @update="updateNodeData" />
        </el-tab-pane>

        <el-tab-pane :label="`动作配置 (${currentNode.actions?.length || 0})`" name="action">
          <action-panel :node="currentNode" @update="updateNodeData" />
        </el-tab-pane>

        <el-tab-pane :label="`检查配置 (${currentNode.checks?.length || 0})`" name="check">
          <check-panel :node="currentNode" @update="updateNodeData" />
        </el-tab-pane>

        <el-tab-pane :label="`跳转配置 (${currentNode.jumps?.length || 0})`" name="jump">
          <jump-panel :node="currentNode" :nodes="nodes" @update="updateNodeData" />
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-empty v-else-if="currentType" description="请选择节点" />
    <el-empty v-else description="请选择流程类型" />

    <!-- 对话框 -->
    <type-dialog v-model:visible="dialogs.type.visible" :current-type="currentEditType" @save="handleTypeSave" />

    <node-dialog v-model:visible="dialogs.node.visible" :nodes="nodes" :current-node="editingNode" @save="handleNodeSave" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue';

// 组件导入
import TypeTree from './components/TypeTree.vue';
import SimpleFlowChart from './components/SimpleFlowChart.vue';
import NodeBasicInfo from './components/NodeBasicInfo.vue';
import ActionPanel from './components/ActionPanel.vue';
import CheckPanel from './components/CheckPanel.vue';
import JumpPanel from './components/JumpPanel.vue';
import TypeDialog from './dialogs/TypeDialog.vue';
import NodeDialog from './dialogs/NodeDialog.vue';

// API和工具函数导入
import { listType, getType, addType, updateType, delType, listNode, getNode, addNode, updateNode, delNode } from '@/api/system/lcconfig';
import { deepClone, confirmDialog, confirmIfHasChanges } from './utils/utils';

// 数据状态
const flowTypes = ref([]);
const currentType = ref(null);
const currentEditType = ref(null);
const nodes = ref([]);
const originalNodes = ref([]);
const currentNode = ref(null);
const editingNode = ref(null);
const hasChanges = ref(false);
const activeTab = ref('basic');

// 对话框状态
const dialogs = ref({
  type: { visible: false },
  node: { visible: false }
});

// 计算属性
// 计算当前节点的前序节点
const prevNode = computed(() => {
  if (!currentNode.value) return null;

  // 查找以当前节点为下一节点的节点
  return nodes.value.find(
    (node) => node.nextId === currentNode.value.id || (node.jumps && node.jumps.some((jump) => jump.lcNextId === currentNode.value.id))
  );
});

// 计算当前节点的后续节点
const nextNode = computed(() => {
  if (!currentNode.value || !currentNode.value.nextId) return null;

  // 查找当前节点的下一节点
  return nodes.value.find((node) => node.id === currentNode.value.nextId);
});

// 流程类型相关方法
const loadFlowTypes = async () => {
  try {
    const res = await listType();
    flowTypes.value = res.data;
  } catch (error) {
    console.error('加载流程类型失败:', error);
    ElMessage.error('加载流程类型失败');
  }
};

const selectType = (type) => {
  confirmIfHasChanges(hasChanges.value, async () => {
    currentType.value = type;
    currentNode.value = null;
    hasChanges.value = false;

    if (type.serialno) {
      await loadNodes(type.serialno);
    }
  });
};

const openTypeDialog = (type = null) => {
  confirmIfHasChanges(hasChanges.value, () => {
    currentEditType.value = type;
    dialogs.value.type.visible = true;
  });
};

const handleTypeSave = async (typeData) => {
  try {
    if (typeData.serialno) {
      await updateType(typeData);
      ElMessage.success('更新成功');
    } else {
      await addType(typeData);
      ElMessage.success('添加成功');
    }
    await loadFlowTypes();
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败');
  }
};

const handleTypeDelete = async (type) => {
  try {
    await confirmDialog('确定要删除该流程类型吗？');
    await delType(type.serialno);
    ElMessage.success('删除成功');

    // 如果删除的是当前选中的类型，清空选择
    if (currentType.value?.serialno === type.serialno) {
      currentType.value = null;
      currentNode.value = null;
      nodes.value = [];
    }

    await loadFlowTypes();
  } catch (error) {
    // 用户取消删除或API错误
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 节点相关方法
const loadNodes = async (lcSerialno) => {
  try {
    const res = await listNode(lcSerialno);
    nodes.value = res.data;
    originalNodes.value = deepClone(res.data);
  } catch (error) {
    console.error('加载节点失败:', error);
    ElMessage.error('加载节点失败');
  }
};

const handleNodeSelect = (node) => {
  // 如果是通过表格选择，node是直接传入的
  // 如果是通过流程图选择，node是事件
  const selectedNode = node?.row || node;
  if (!selectedNode) return;
  currentNode.value = deepClone(selectedNode);
};

const handleNodeAdd = () => {
  editingNode.value = null;
  dialogs.value.node.visible = true;
};

const handleNodeEdit = (node) => {
  editingNode.value = deepClone(node);
  dialogs.value.node.visible = true;
};

const handleNodeSave = (nodeData) => {
  // 临时ID用于前端标识
  if (!nodeData.id) {
    nodeData.id = -Date.now(); // 使用负数，避免与后端ID冲突
  }

  const index = nodes.value.findIndex((n) => n.id === nodeData.id);

  if (index !== -1) {
    // 更新现有节点
    nodes.value[index] = nodeData;
  } else {
    // 添加新节点
    nodes.value.push(nodeData);
  }

  // 如果当前节点就是编辑的节点，更新当前节点
  if (currentNode.value && currentNode.value.id === nodeData.id) {
    currentNode.value = deepClone(nodeData);
  }

  hasChanges.value = true;
};

const handleNodeDelete = async (index) => {
  try {
    await confirmDialog('确定要删除该节点吗？');

    const nodeToDelete = nodes.value[index];

    // 如果节点已保存到后端，先删除后端数据
    if (nodeToDelete.id > 0) {
      await delNode(nodeToDelete.id);
    }

    // 从前端列表中删除
    nodes.value.splice(index, 1);

    // 如果删除的是当前选中的节点，清空选择
    if (currentNode.value && currentNode.value.id === nodeToDelete.id) {
      currentNode.value = null;
    }

    hasChanges.value = true;
    ElMessage.success('删除成功');
  } catch (error) {
    // 用户取消删除或API错误
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 节点数据更新
const updateNodeData = (updatedNode) => {
  currentNode.value = updatedNode;
  hasChanges.value = true;
};

// 保存当前节点更改
const saveCurrentNode = async () => {
  try {
    if (!currentNode.value) return;

    // 更新本地节点列表
    const index = nodes.value.findIndex((n) => n.id === currentNode.value.id);
    if (index !== -1) {
      nodes.value[index] = deepClone(currentNode.value);
    }

    // 根据ID判断是新增还是更新
    if (currentNode.value.id > 0) {
      // 已有ID，更新
      await updateNode(currentNode.value);
    } else if (currentNode.value.id < 0) {
      // 临时ID，新增
      const saveData = deepClone(currentNode.value);
      delete saveData.id; // 删除前端临时ID

      // 添加lcSerialno字段
      saveData.lcSerialno = currentType.value.serialno;

      const res = await addNode(saveData);

      // 更新ID为后端返回的ID
      currentNode.value.id = res.data.id;
      nodes.value[index].id = res.data.id;
    }

    // 更新原始数据
    originalNodes.value = deepClone(nodes.value);

    hasChanges.value = false;
    ElMessage.success('保存成功');
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败');
  }
};

// 保存所有修改
const handleSaveAll = async () => {
  try {
    if (!currentType.value) return;

    await confirmDialog('确认保存所有修改吗？');

    // 保存流程类型
    if (currentType.value.serialno) {
      await updateType(currentType.value);
    }

    // 保存节点配置
    for (const node of nodes.value) {
      if (node.id > 0) {
        // 更新现有节点
        await updateNode(node);
      } else if (node.id < 0) {
        // 添加新节点
        const saveData = { ...node };
        delete saveData.id; // 删除前端临时ID
        saveData.lcSerialno = currentType.value.serialno;

        const res = await addNode(saveData);
        node.id = res.data.id;
      }
    }

    // 更新原始数据
    originalNodes.value = deepClone(nodes.value);
    hasChanges.value = false;

    ElMessage.success('保存成功');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('保存失败:', error);
      ElMessage.error('保存失败');
    }
  }
};

// 生命周期钩子
onMounted(() => {
  loadFlowTypes();
});
</script>

<style scoped>
.app-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f0f2f5;
}

.sidebar {
  width: 250px;
  background: white;
  border-right: 1px solid #dcdfe6;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.node-list-panel {
  width: 350px;
  background: white;
  border-right: 1px solid #dcdfe6;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.node-detail-panel {
  flex: 1;
  background: white;
  padding: 16px;
  overflow: auto;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.node-table {
  flex: 1;
  overflow: auto;
}

.unsaved-changes {
  display: flex;
  align-items: center;
  gap: 8px;
}

.change-indicator {
  color: #f56c6c;
  font-size: 18px;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .app-container {
    flex-direction: column;
    height: auto;
    min-height: 100vh;
  }

  .sidebar,
  .node-list-panel,
  .node-detail-panel {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #dcdfe6;
  }

  .sidebar,
  .node-list-panel {
    height: auto;
    max-height: 400px;
  }
}
</style>
