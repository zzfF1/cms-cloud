<!-- CheckPanel.vue -->
<template>
  <div class="check-panel">
    <div class="panel-header">
      <h3>检查配置</h3>
      <el-button type="primary" size="small" @click="handleAddCheck"> 添加检查 </el-button>
    </div>

    <div class="panel-content">
      <!-- 左侧检查列表 -->
      <div class="check-list">
        <el-table :data="checks" @current-change="handleSelectCheck" highlight-current-row style="width: 100%">
          <el-table-column prop="name" label="检查名称" min-width="120" />
          <el-table-column prop="type" label="类型" min-width="120">
            <template #default="{ row }">
              {{ ACTION_TYPES[row.type] }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button link type="danger" @click.stop="handleDeleteCheck(row)"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 右侧编辑区 -->
      <div v-if="editingCheck" class="check-editor">
        <el-form :model="editingCheck" label-width="100px" :rules="rules">
          <el-form-item label="检查名称" prop="name">
            <el-input v-model="editingCheck.name" />
          </el-form-item>

          <el-form-item label="类型" prop="type">
            <el-select v-model="editingCheck.type" placeholder="请选择类型">
              <el-option v-for="(name, value) in ACTION_TYPES" :key="value" :label="name" :value="Number(value)" />
            </el-select>
          </el-form-item>

          <el-form-item label="检查条件" prop="checkTj">
            <el-input v-model="editingCheck.checkTj" type="textarea" :rows="5" placeholder="请输入SQL语句或实现类" />
          </el-form-item>

          <el-form-item label="提示信息" prop="checkMsg">
            <el-input v-model="editingCheck.checkMsg" type="textarea" :rows="3" placeholder="请输入检查不通过时的提示信息" />
          </el-form-item>

          <div class="form-actions">
            <el-button @click="handleCancelEdit">取消</el-button>
            <el-button type="primary" @click="handleSaveCheck">保存</el-button>
          </div>
        </el-form>
      </div>

      <el-empty v-else description="请选择检查或添加新检查" :image-size="100" class="empty-state" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ACTION_TYPES, VALIDATION_RULES } from '../utils/constants';
import { deepClone } from '../utils/utils';

const props = defineProps({
  node: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['update']);

// 检查列表
const checks = ref(props.node.checks || []);

// 当前编辑的检查
const selectedCheck = ref(null);
const editingCheck = ref(null);

// 表单验证规则
const rules = VALIDATION_RULES.check;

// 选择检查进行编辑
const handleSelectCheck = (check) => {
  if (!check) return;
  selectedCheck.value = check;
  editingCheck.value = deepClone(check);
};

// 添加新检查
const handleAddCheck = () => {
  editingCheck.value = {
    name: '',
    type: 0,
    checkTj: '',
    checkMsg: '',
    recno: checks.value.length + 1,
    checkType: 0
  };
};

// 保存检查
const handleSaveCheck = () => {
  if (!editingCheck.value) return;

  const checkToSave = deepClone(editingCheck.value);

  // 添加临时ID
  if (!checkToSave.serialno) {
    checkToSave.serialno = Date.now();
  }

  const updatedChecks = [...checks.value];
  const index = updatedChecks.findIndex((c) => c.serialno === checkToSave.serialno);

  if (index !== -1) {
    updatedChecks[index] = checkToSave;
  } else {
    updatedChecks.push(checkToSave);
  }

  // 更新本地列表
  checks.value = updatedChecks;

  // 更新父组件数据
  const updatedNode = {
    ...props.node,
    checks: updatedChecks
  };

  emit('update', updatedNode);

  // 更新选中状态
  selectedCheck.value = checkToSave;
};

// 删除检查
const handleDeleteCheck = async (check) => {
  try {
    await ElMessageBox.confirm('确定要删除该检查吗？', '提示');

    const updatedChecks = checks.value.filter((c) => c.serialno !== check.serialno);
    checks.value = updatedChecks;

    // 如果删除的是当前编辑的检查，清空编辑状态
    if (selectedCheck.value?.serialno === check.serialno) {
      selectedCheck.value = null;
      editingCheck.value = null;
    }

    // 更新父组件数据
    const updatedNode = {
      ...props.node,
      checks: updatedChecks
    };

    emit('update', updatedNode);
  } catch (error) {
    // 用户取消删除
  }
};

// 取消编辑
const handleCancelEdit = () => {
  if (selectedCheck.value) {
    editingCheck.value = deepClone(selectedCheck.value);
  } else {
    editingCheck.value = null;
  }
};
</script>

<style scoped>
.check-panel {
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

.check-list {
  width: 40%;
  max-height: 400px;
  overflow: auto;
}

.check-editor {
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
