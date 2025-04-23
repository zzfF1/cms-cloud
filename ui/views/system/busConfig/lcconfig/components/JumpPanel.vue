<!-- JumpPanel.vue -->
<template>
  <div class="jump-panel">
    <div class="panel-header">
      <h3>跳转配置</h3>
      <el-button type="primary" size="small" @click="handleAddJump"> 添加跳转 </el-button>
    </div>

    <div class="panel-content">
      <!-- 左侧跳转列表 -->
      <div class="jump-list">
        <el-table :data="jumps" @current-change="handleSelectJump" highlight-current-row style="width: 100%">
          <el-table-column prop="recno" label="顺序号" width="80" align="center" />
          <el-table-column prop="nextNodeName" label="下一节点" />
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button link type="danger" @click.stop="handleDeleteJump(row)"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 右侧编辑区 -->
      <div v-if="editingJump" class="jump-editor">
        <el-form :model="editingJump" label-width="100px" :rules="rules">
          <el-form-item label="顺序号" prop="recno">
            <el-input-number v-model="editingJump.recno" :min="1" />
          </el-form-item>

          <el-form-item label="下一节点" prop="lcNextId">
            <el-select v-model="editingJump.lcNextId" placeholder="请选择下一节点" @change="handleNextNodeChange" clearable>
              <el-option v-for="node in availableNodes" :key="node.id" :label="node.name" :value="node.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="跳转条件" prop="tzTj">
            <el-input v-model="editingJump.tzTj" type="textarea" :rows="4" placeholder="请输入跳转条件" />
          </el-form-item>

          <el-form-item label="说明" prop="sm">
            <el-input v-model="editingJump.sm" placeholder="请输入跳转说明" />
          </el-form-item>

          <!-- 流程可视化 -->
          <div v-if="editingJump.lcNextId" class="jump-visualization">
            <div class="viz-title">跳转流程图示</div>
            <div class="viz-content">
              <div class="viz-node current">{{ props.node.name }}</div>
              <div class="viz-arrow">↓</div>
              <div class="viz-condition">条件: {{ editingJump.tzTj || '1=1' }}</div>
              <div class="viz-arrow">↓</div>
              <div class="viz-node target">{{ editingJump.nextNodeName }}</div>
            </div>
          </div>

          <div class="form-actions">
            <el-button @click="handleCancelEdit">取消</el-button>
            <el-button type="primary" @click="handleSaveJump">保存</el-button>
          </div>
        </el-form>
      </div>

      <el-empty v-else description="请选择跳转或添加新跳转" :image-size="100" class="empty-state" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { VALIDATION_RULES } from '../utils/constants';
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

// 跳转列表
const jumps = ref(props.node.jumps || []);

// 当前编辑的跳转
const selectedJump = ref(null);
const editingJump = ref(null);

// 表单验证规则
const rules = VALIDATION_RULES.jump;

// 可选的下一节点（排除自己）
const availableNodes = computed(() => {
  return props.nodes.filter((node) => node.id !== props.node.id);
});

// 选择跳转进行编辑
const handleSelectJump = (jump) => {
  if (!jump) return;
  selectedJump.value = jump;
  editingJump.value = deepClone(jump);
};

// 添加新跳转
const handleAddJump = () => {
  editingJump.value = {
    recno: jumps.value.length + 1,
    lcNextId: '',
    nextNodeName: '',
    tzTj: '',
    sm: ''
  };
};

// 处理下一节点变更
const handleNextNodeChange = (nextId) => {
  if (!editingJump.value) return;

  // 更新nextNodeName字段
  const nextNode = props.nodes.find((node) => node.id === nextId);
  editingJump.value.nextNodeName = nextNode ? nextNode.name : '';
};

// 保存跳转
const handleSaveJump = () => {
  if (!editingJump.value) return;

  const jumpToSave = deepClone(editingJump.value);

  // 添加临时ID
  if (!jumpToSave.serialno) {
    jumpToSave.serialno = Date.now();
  }

  const updatedJumps = [...jumps.value];
  const index = updatedJumps.findIndex((j) => j.serialno === jumpToSave.serialno);

  if (index !== -1) {
    updatedJumps[index] = jumpToSave;
  } else {
    updatedJumps.push(jumpToSave);
  }

  // 更新本地列表
  jumps.value = updatedJumps;

  // 更新父组件数据
  const updatedNode = {
    ...props.node,
    jumps: updatedJumps
  };

  emit('update', updatedNode);

  // 更新选中状态
  selectedJump.value = jumpToSave;
};

// 删除跳转
const handleDeleteJump = async (jump) => {
  try {
    await ElMessageBox.confirm('确定要删除该跳转吗？', '提示');

    const updatedJumps = jumps.value.filter((j) => j.serialno !== jump.serialno);
    jumps.value = updatedJumps;

    // 如果删除的是当前编辑的跳转，清空编辑状态
    if (selectedJump.value?.serialno === jump.serialno) {
      selectedJump.value = null;
      editingJump.value = null;
    }

    // 更新父组件数据
    const updatedNode = {
      ...props.node,
      jumps: updatedJumps
    };

    emit('update', updatedNode);
  } catch (error) {
    // 用户取消删除
  }
};

// 取消编辑
const handleCancelEdit = () => {
  if (selectedJump.value) {
    editingJump.value = deepClone(selectedJump.value);
  } else {
    editingJump.value = null;
  }
};
</script>

<style scoped>
.jump-panel {
  padding: 16px 0;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

h3 {
  margin: 0;
}

.panel-content {
  display: flex;
  gap: 24px;
}

.jump-list {
  width: 40%;
  max-height: 400px;
  overflow: auto;
}

.jump-editor {
  width: 60%;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.empty-state {
  width: 60%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 4px;
  padding: 32px 0;
}

.jump-visualization {
  margin-top: 20px;
  padding: 16px;
  background-color: #f0f0f0;
  border-radius: 4px;
}

.viz-title {
  font-weight: bold;
  margin-bottom: 12px;
}

.viz-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.viz-node {
  width: 100%;
  padding: 8px 0;
  text-align: center;
  border-radius: 4px;
}

.viz-node.current {
  background-color: #bbdefb;
  font-weight: bold;
}

.viz-node.target {
  background-color: #e0e0e0;
}

.viz-condition {
  width: 100%;
  padding: 8px;
  background-color: #e0f7fa;
  border-radius: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  gap: 12px;
}
</style>
