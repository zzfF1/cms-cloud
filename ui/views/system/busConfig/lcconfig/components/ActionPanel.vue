<!-- ActionPanel.vue -->
<template>
  <div class="action-panel">
    <div class="panel-header">
      <h3>动作配置</h3>
      <el-button type="primary" size="small" @click="handleAddAction"> 添加动作 </el-button>
    </div>

    <div class="panel-content">
      <!-- 左侧动作列表 -->
      <div class="action-list">
        <el-table :data="actions" @current-change="handleSelectAction" highlight-current-row style="width: 100%">
          <el-table-column prop="name" label="动作名称" min-width="120" />
          <el-table-column prop="type" label="类型" min-width="120">
            <template #default="{ row }">
              {{ ACTION_TYPES[row.type] }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button link type="danger" @click.stop="handleDeleteAction(row)"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 右侧编辑区 -->
      <div v-if="editingAction" class="action-editor">
        <el-form :model="editingAction" label-width="100px" :rules="rules">
          <el-form-item label="动作名称" prop="name">
            <el-input v-model="editingAction.name" />
          </el-form-item>

          <el-form-item label="类型" prop="type">
            <el-select v-model="editingAction.type" placeholder="请选择类型">
              <el-option v-for="(name, value) in ACTION_TYPES" :key="value" :label="name" :value="Number(value)" />
            </el-select>
          </el-form-item>

          <el-form-item label="动作类型" prop="dzType">
            <el-select v-model="editingAction.dzType" placeholder="请选择动作类型">
              <el-option v-for="(name, value) in DZ_TYPES" :key="value" :label="name" :value="Number(value)" />
            </el-select>
          </el-form-item>

          <el-form-item label="动作内容" prop="dz">
            <el-input v-model="editingAction.dz" type="textarea" :rows="5" :placeholder="editingAction.dzType === 0 ? 'SQL语句' : '类名'" />
          </el-form-item>

          <div class="form-actions">
            <el-button @click="handleCancelEdit">取消</el-button>
            <el-button type="primary" @click="handleSaveAction">保存</el-button>
          </div>
        </el-form>
      </div>

      <el-empty v-else description="请选择动作或添加新动作" :image-size="100" class="empty-state" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ACTION_TYPES, DZ_TYPES, VALIDATION_RULES } from '../utils/constants';
import { deepClone } from '../utils/utils';

const props = defineProps({
  node: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['update']);

// 动作列表
const actions = ref(props.node.actions || []);

// 当前编辑的动作
const selectedAction = ref(null);
const editingAction = ref(null);

// 表单验证规则
const rules = VALIDATION_RULES.action;

// 选择动作进行编辑
const handleSelectAction = (action) => {
  if (!action) return;
  selectedAction.value = action;
  editingAction.value = deepClone(action);
};

// 添加新动作
const handleAddAction = () => {
  editingAction.value = {
    name: '',
    type: 0,
    dzType: 0,
    dz: '',
    recno: actions.value.length + 1
  };
};

// 保存动作
const handleSaveAction = () => {
  if (!editingAction.value) return;

  const actionToSave = deepClone(editingAction.value);

  // 添加临时ID
  if (!actionToSave.serialno) {
    actionToSave.serialno = Date.now();
  }

  const updatedActions = [...actions.value];
  const index = updatedActions.findIndex((a) => a.serialno === actionToSave.serialno);

  if (index !== -1) {
    updatedActions[index] = actionToSave;
  } else {
    updatedActions.push(actionToSave);
  }

  // 更新本地列表
  actions.value = updatedActions;

  // 更新父组件数据
  const updatedNode = {
    ...props.node,
    actions: updatedActions
  };

  emit('update', updatedNode);

  // 更新选中状态
  selectedAction.value = actionToSave;
};

// 删除动作
const handleDeleteAction = async (action) => {
  try {
    await ElMessageBox.confirm('确定要删除该动作吗？', '提示');

    const updatedActions = actions.value.filter((a) => a.serialno !== action.serialno);
    actions.value = updatedActions;

    // 如果删除的是当前编辑的动作，清空编辑状态
    if (selectedAction.value?.serialno === action.serialno) {
      selectedAction.value = null;
      editingAction.value = null;
    }

    // 更新父组件数据
    const updatedNode = {
      ...props.node,
      actions: updatedActions
    };

    emit('update', updatedNode);
  } catch (error) {
    // 用户取消删除
  }
};

// 取消编辑
const handleCancelEdit = () => {
  if (selectedAction.value) {
    editingAction.value = deepClone(selectedAction.value);
  } else {
    editingAction.value = null;
  }
};
</script>

<style scoped>
.action-panel {
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

.action-list {
  width: 40%;
  max-height: 400px;
  overflow: auto;
}

.action-editor {
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

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  gap: 12px;
}
</style>
