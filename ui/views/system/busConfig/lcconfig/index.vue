<template>
  <div class="app-container">
    <!-- 左侧流程类型树 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <div class="sidebar-title">流程类型</div>
        <el-button type="primary" size="small" @click="handleTypeOperation()"> 添加类型 </el-button>
      </div>
      <el-tree :data="flowTypes" node-key="serialno" :props="{ label: 'name' }" @node-click="(data) => !data.children && selectType(data)">
        <template #default="{ node, data }">
          <div class="custom-tree-node">
            <span>{{ data.serialno }}-{{ node.label }}</span>
            <span v-if="!data.children" class="operation">
              <el-button link type="primary" @click.stop="handleTypeOperation(data)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button link type="danger" @click.stop="handleTypeDelete(node, data)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </span>
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 右侧内容区 -->
    <div v-if="currentType" class="main-content">
      <div class="content-header">
        <h3>{{ currentType.name }}</h3>
        <el-button type="primary" size="small" @click="handleNodeOperation()"> 添加节点 </el-button>
        <el-button type="primary" size="small" @click="toggleAllNodesExpand"> 展开/折叠 </el-button>
        <el-button type="success" size="small" :disabled="!hasChanges" @click="handleSaveAll"> 保存修改 </el-button>
      </div>

      <!-- 节点表格 -->
      <el-table :data="nodes" border style="width: 100%" row-key="id">
        <!-- 展开行 -->
        <el-table-column type="expand">
          <template #default="scope">
            <div class="expand-content">
              <el-tabs>
                <!-- 动作配置 -->
                <el-tab-pane>
                  <template #label>
                    <div style="display: flex; align-items: center; gap: 8px">
                      <span>动作配置</span>
                      <el-button type="primary" size="small" @click.stop="handleActionOperation(scope.row)"> 添加 </el-button>
                    </div>
                  </template>
                  <el-table :data="scope.row.actions || []" border size="small">
                    <el-table-column prop="name" label="动作名称" width="150" />
                    <el-table-column prop="type" label="类型" width="120">
                      <template #default="{ row }">
                        {{ actionTypes[row.type] }}
                      </template>
                    </el-table-column>
                    <el-table-column prop="dzType" label="动作类型" width="120">
                      <template #default="{ row }">
                        {{ dzTypes[row.dzType] }}
                      </template>
                    </el-table-column>
                    <el-table-column prop="dz" label="动作内容" show-overflow-tooltip />
                    <el-table-column label="操作" width="150" fixed="right">
                      <template #default="{ row }">
                        <el-button-group>
                          <el-button link type="primary" @click="handleActionOperation(scope.row, row)"> 编辑 </el-button>
                          <el-button link type="danger" @click="handleActionDelete(scope.row, row)"> 删除 </el-button>
                        </el-button-group>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>

                <!-- 检查配置 -->
                <el-tab-pane>
                  <template #label>
                    <div style="display: flex; align-items: center; gap: 8px">
                      <span>检查配置</span>
                      <el-button type="primary" size="small" @click.stop="handleCheckOperation(scope.row)"> 添加 </el-button>
                    </div>
                  </template>
                  <el-table :data="scope.row.checks || []" border size="small">
                    <el-table-column prop="name" label="检查名称" width="150" />
                    <el-table-column prop="type" label="类型" width="120">
                      <template #default="{ row }">
                        {{ actionTypes[row.type] }}
                      </template>
                    </el-table-column>
                    <el-table-column prop="checkTj" label="检查条件" show-overflow-tooltip />
                    <el-table-column prop="checkMsg" label="提示信息" show-overflow-tooltip />
                    <el-table-column label="操作" width="150" fixed="right">
                      <template #default="{ row }">
                        <el-button-group>
                          <el-button link type="primary" @click="handleCheckOperation(scope.row, row)"> 编辑 </el-button>
                          <el-button link type="danger" @click="handleCheckDelete(scope.row, row)"> 删除 </el-button>
                        </el-button-group>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>

                <!-- 跳转配置 -->
                <el-tab-pane>
                  <template #label>
                    <div style="display: flex; align-items: center; gap: 8px">
                      <span>跳转配置</span>
                      <el-button type="primary" size="small" @click.stop="handleJumpOperation(scope.row)"> 添加 </el-button>
                    </div>
                  </template>
                  <el-table :data="scope.row.jumps || []" border size="small">
                    <el-table-column prop="recno" label="顺序号" width="80" />
                    <el-table-column prop="nextNodeName" label="下一节点" width="150" />
                    <el-table-column prop="tzTj" label="跳转条件" show-overflow-tooltip />
                    <el-table-column prop="sm" label="说明" show-overflow-tooltip />
                    <el-table-column label="操作" width="150" fixed="right">
                      <template #default="{ row }">
                        <el-button-group>
                          <el-button link type="primary" @click="handleJumpOperation(scope.row, row)"> 编辑 </el-button>
                          <el-button link type="danger" @click="handleJumpDelete(scope.row, row)"> 删除 </el-button>
                        </el-button-group>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
              </el-tabs>
            </div>
          </template>
        </el-table-column>

        <!-- 主表格列 -->
        <el-table-column type="index" width="60" align="center" label="序号" />
        <el-table-column prop="name" label="节点名称" width="200" />
        <el-table-column prop="recno" label="顺序号" width="80" />
        <el-table-column label="动作数量" align="center" width="100">
          <template #default="{ row }">
            {{ (row.actions || []).length }}
          </template>
        </el-table-column>
        <el-table-column label="检查数量" align="center" width="100">
          <template #default="{ row }">
            {{ (row.checks || []).length }}
          </template>
        </el-table-column>
        <el-table-column label="跳转数量" align="center" width="100">
          <template #default="{ row }">
            {{ (row.jumps || []).length }}
          </template>
        </el-table-column>
        <el-table-column prop="nextNode" label="下一节点" width="150" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row, $index }">
            <el-button-group>
              <el-button link type="primary" @click="handleNodeOperation(row)">编辑</el-button>
              <el-button link type="danger" @click="handleNodeDelete($index)">删除</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-empty v-else description="请选择流程类型" />

    <!-- 对话框组件 -->
    <NodeDialog v-model:visible="dialogs.node.visible" :nodes="nodes" :current-node="currentNode" @save="handleNodeSave" />
    <ActionDialog
      v-model:visible="dialogs.action.visible"
      :action-types="actionTypes"
      :dz-types="dzTypes"
      :current-action="currentAction"
      :current-node="currentNode"
    />
    <CheckDialog v-model:visible="dialogs.check.visible" :action-types="actionTypes" :current-check="currentCheck" :current-node="currentNode" />
    <JumpDialog v-model:visible="dialogs.jump.visible" :nodes="nodes" :current-jump="currentJump" :current-node="currentNode" />
    <TypeDialog v-model:visible="dialogs.type.visible" :current-type="currentEditType" @save="handleTypeSave" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Edit, Delete } from '@element-plus/icons-vue';
import NodeDialog from './NodeDialog.vue';
import ActionDialog from './ActionDialog.vue';
import CheckDialog from './CheckDialog.vue';
import JumpDialog from './JumpDialog.vue';
import TypeDialog from './TypeDialog.vue';

import { listType, getType, addType, updateType, delType, listNode, getNode, addNode, updateNode, delNode } from '@/api/system/lcconfig';

import type { LcDefineVo, LcMainVo, LcDzVo, LcCheckVo, LcTzVo } from '@/api/system/lcconfig/types';

// 对话框状态管理
const dialogs = ref({
  node: { visible: false },
  action: { visible: false },
  check: { visible: false },
  jump: { visible: false },
  type: { visible: false }
});

// 当前操作的数据
const currentType = ref<LcMainVo | null>(null);
const currentEditType = ref<LcMainVo | null>(null);
const currentNode = ref<LcDefineVo | null>(null);
const currentAction = ref<LcDzVo | null>(null);
const currentCheck = ref<LcCheckVo | null>(null);
const currentJump = ref<LcTzVo | null>(null);

// 数据列表
const flowTypes = ref<LcMainVo[]>([]);
const nodes = ref<LcDefineVo[]>([]);
// 原始数据存储,用于判断是否有修改
const originalData = ref<{
  nodes: LcDefineVo[];
  currentType: LcMainVo | null;
} | null>(null);
// 枚举值
const actionTypes = { 0: '提交进入时', 1: '退回时', 2: '提交时', 3: '退回进入时' };
const dzTypes = { 0: 'SQL', 3: '实现类' };

// 是否有未保存的修改
const hasChanges = computed(() => {
  if (!originalData.value || !currentType.value) return false;
  const currentData = {
    nodes: nodes.value,
    currentType: currentType.value
  };
  return JSON.stringify(currentData) !== JSON.stringify(originalData.value);
});

// 类型相关操作
const handleTypeOperation = (type: LcMainVo | null = null) => {
  // 如果有未保存的修改,提示用户
  if (hasChanges.value) {
    ElMessageBox.confirm('有未保存的修改，是否继续？', '提示', {
      confirmButtonText: '继续',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(() => {
        currentEditType.value = type;
        dialogs.value.type.visible = true;
      })
      .catch(() => {});
  } else {
    currentEditType.value = type;
    dialogs.value.type.visible = true;
  }
};

const handleTypeSave = async (typeData: LcMainVo) => {
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

const handleTypeDelete = async (node: any, type: LcMainVo) => {
  try {
    await ElMessageBox.confirm('确定要删除该流程类型吗？', '提示');
    await delType(type.serialno);
    ElMessage.success('删除成功');
    await loadFlowTypes();
  } catch (error) {
    console.error('删除失败:', error);
  }
};

// 节点相关操作
const handleNodeOperation = (node: LcDefineVo | null = null) => {
  currentNode.value = node;
  dialogs.value.node.visible = true;
};

const handleNodeSave = (nodeData: LcDefineVo) => {
  if (nodeData.id) {
    const index = nodes.value.findIndex((n) => n.id === nodeData.id);
    if (index !== -1) {
      nodes.value[index] = nodeData;
    }
  } else {
    nodeData.id = Date.now(); // 临时ID
    nodes.value.push(nodeData);
  }
};

const handleNodeDelete = async (index: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该节点吗？', '提示');
    nodes.value.splice(index, 1);
  } catch (error) {
    console.error('删除失败:', error);
  }
};

// 动作相关操作
const handleActionOperation = (node: LcDefineVo, action: LcDzVo | null = null) => {
  currentNode.value = node;
  currentAction.value = action;
  dialogs.value.action.visible = true;
};

const handleActionDelete = async (node: LcDefineVo, action: LcDzVo) => {
  try {
    await ElMessageBox.confirm('确定要删除该动作吗？', '提示');
    const index = node.actions.findIndex((a) => a.serialno === action.serialno);
    if (index !== -1) {
      node.actions.splice(index, 1);
    }
  } catch (error) {
    console.error('删除失败:', error);
  }
};

// 检查相关操作
const handleCheckOperation = (node: LcDefineVo, check: LcCheckVo | null = null) => {
  currentNode.value = node;
  currentCheck.value = check;
  dialogs.value.check.visible = true;
};

const handleCheckDelete = async (node: LcDefineVo, check: LcCheckVo) => {
  try {
    await ElMessageBox.confirm('确定要删除该检查吗？', '提示');
    const index = node.checks.findIndex((c) => c.serialno === check.serialno);
    if (index !== -1) {
      node.checks.splice(index, 1);
    }
  } catch (error) {
    console.error('删除失败:', error);
  }
};

// 跳转相关操作
const handleJumpOperation = (node: LcDefineVo, jump: LcTzVo | null = null) => {
  currentNode.value = node;
  currentJump.value = jump;
  dialogs.value.jump.visible = true;
};

const handleJumpDelete = async (node: LcDefineVo, jump: LcTzVo) => {
  try {
    await ElMessageBox.confirm('确定要删除该跳转吗？', '提示');
    const index = node.jumps.findIndex((j) => j.serialno === jump.serialno);
    if (index !== -1) {
      node.jumps.splice(index, 1);
    }
  } catch (error) {
    console.error('删除失败:', error);
  }
};

// 统一保存所有修改
const handleSaveAll = async () => {
  try {
    if (!currentType.value) return;
    await ElMessageBox.confirm('确认保存所有修改吗？', '提示');
    // 保存流程类型
    if (currentType.value.serialno) {
      await updateType(currentType.value);
    }
    // 保存节点配置
    for (const node of nodes.value) {
      if (node.id) {
        await updateNode(node);
      } else {
        const res = await addNode({
          ...node,
          lcSerialno: currentType.value.serialno
        });
        node.id = res.data.id;
      }
    }
    // 更新原始数据
    originalData.value = {
      nodes: JSON.parse(JSON.stringify(nodes.value)),
      currentType: JSON.parse(JSON.stringify(currentType.value))
    };

    ElMessage.success('保存成功');
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败');
  }
};

// 流程类型选择
const selectType = async (type: LcMainVo) => {
  // 如果有未保存的修改,提示用户
  if (hasChanges.value) {
    try {
      await ElMessageBox.confirm('有未保存的修改，是否继续？', '提示', {
        confirmButtonText: '继续',
        cancelButtonText: '取消',
        type: 'warning'
      });
      await switchType(type);
    } catch (error) {
      // 用户取消切换
    }
  } else {
    await switchType(type);
  }
};

// 切换流程类型
const switchType = async (type: LcMainVo) => {
  currentType.value = type;
  if (type.serialno) {
    await loadNodes(type.serialno);
    // 保存原始数据
    originalData.value = {
      nodes: JSON.parse(JSON.stringify(nodes.value)),
      currentType: JSON.parse(JSON.stringify(type))
    };
  }
};

// 数据加载方法
const loadFlowTypes = async () => {
  const res = await listType();
  flowTypes.value = res.data;
};

const loadNodes = async (lcSerialno: number) => {
  const res = await listNode(lcSerialno);
  nodes.value = res.data;
};

// 生命周期钩子
onMounted(() => {
  loadFlowTypes();
});
</script>

<style scoped lang="scss">
.app-container {
  display: flex;
  min-height: 100vh;
  padding: 20px;
  gap: 20px;
  background-color: #f0f2f5;
}

.sidebar {
  width: 250px;
  background: white;
  padding: 16px;
  border-radius: 4px;
  height: fit-content;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.main-content {
  flex: 1;
  background: white;
  padding: 16px;
  border-radius: 4px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.operation {
  display: none;
}

.el-tree-node__content:hover .operation {
  display: inline-flex;
}

:deep(.el-table__row) {
  height: 50px;
}

:deep(.el-tabs__header) {
  margin-bottom: 2px !important;
}

:deep(.el-table__expand-icon) {
  margin-right: 10px;
}

.expand-content {
  padding: 2px 2px 2px 2px;
  background-color: #d1f6e2;
  border-radius: 4px;
  margin: 0 8px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

:deep(.el-table__expanded-cell) {
  padding: 2px 2px 2px 2px !important;
  background-color: #d1f6e2 !important;
}

:deep(.el-tabs__content) {
  padding: 2px 2px 2px 2px;
}
</style>
