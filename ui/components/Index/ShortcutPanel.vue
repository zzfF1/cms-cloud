<template>
  <div class="module-container">
    <el-card shadow="hover" class="shortcuts-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            快捷入口
          </div>
          <div class="header-right">
            <el-button type="text" size="small" @click="showAddShortcutDialog">添加</el-button>
            <el-button type="text" size="small" @click="showManageDialog">管理</el-button>
          </div>
        </div>
      </template>

      <div class="shortcuts-container">
        <div v-for="item in shortcuts" :key="item.id" class="shortcut-item" @click="handleShortcutClick(item.route)">
          <div class="shortcut-content">
            <div class="shortcut-icon" :class="item.iconColor">
              <i :class="item.icon"></i>
            </div>
            <div class="shortcut-info">
              <div class="shortcut-label">{{ item.label }}</div>
            </div>
          </div>
          <div class="shortcut-actions">
            <i class="el-icon-delete" title="删除" @click.stop="confirmDeleteShortcut(item)"></i>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 添加快捷入口对话框 -->
    <el-dialog title="添加快捷入口" v-model="addDialogVisible" width="30%" :before-close="handleCloseDialog" :append-to-body="true" :modal-append-to-body="false" :close-on-click-modal="false" center>
      <el-form :model="newShortcut" label-position="top" :rules="shortcutRules" ref="shortcutFormRef" class="add-shortcut-form">
        <el-form-item label="名称" prop="label" required>
          <el-input v-model="newShortcut.label" placeholder="请输入名称"></el-input>
        </el-form-item>
        <el-form-item label="路由" prop="route" required>
          <el-input v-model="newShortcut.route" placeholder="请输入路由"></el-input>
        </el-form-item>
        <el-form-item label="图标颜色">
          <el-select v-model="newShortcut.iconColor" placeholder="请选择图标颜色" style="width: 100%">
            <el-option label="蓝色" value="blue"></el-option>
            <el-option label="绿色" value="green"></el-option>
            <el-option label="橙色" value="orange"></el-option>
            <el-option label="灰色" value="gray"></el-option>
            <el-option label="红色" value="red"></el-option>
            <el-option label="紫色" value="purple"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图标">
          <el-select v-model="newShortcut.icon" placeholder="请选择图标" style="width: 100%">
            <el-option label="添加" value="el-icon-plus"></el-option>
            <el-option label="用户" value="el-icon-user"></el-option>
            <el-option label="日历" value="el-icon-date"></el-option>
            <el-option label="文档" value="el-icon-document"></el-option>
            <el-option label="设置" value="el-icon-setting"></el-option>
            <el-option label="电话" value="el-icon-phone"></el-option>
            <el-option label="消息" value="el-icon-message"></el-option>
            <el-option label="图表" value="el-icon-data-analysis"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addShortcut">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 管理快捷入口对话框 -->
    <el-dialog title="管理快捷入口" v-model="manageDialogVisible" width="50%" :append-to-body="true" :modal-append-to-body="false" :close-on-click-modal="false" center>
      <el-table :data="shortcuts" style="width: 100%">
        <el-table-column label="图标" width="80">
          <template #default="scope">
            <div class="shortcut-icon-small" :class="scope.row.iconColor">
              <i :class="scope.row.icon"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="label" label="名称"></el-table-column>
        <el-table-column prop="route" label="路由"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="text" size="small" @click="editShortcut(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="confirmDeleteShortcut(scope.row)" class="delete-btn">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="manageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="showAddShortcutDialog">添加</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑快捷入口对话框 -->
    <el-dialog title="编辑快捷入口" v-model="editDialogVisible" width="30%" :append-to-body="true" :modal-append-to-body="false" :close-on-click-modal="false" center>
      <el-form :model="editingShortcut" label-position="top" :rules="shortcutRules" ref="editFormRef" class="add-shortcut-form">
        <el-form-item label="名称" prop="label" required>
          <el-input v-model="editingShortcut.label" placeholder="请输入名称"></el-input>
        </el-form-item>
        <el-form-item label="路由" prop="route" required>
          <el-input v-model="editingShortcut.route" placeholder="请输入路由"></el-input>
        </el-form-item>
        <el-form-item label="图标颜色">
          <el-select v-model="editingShortcut.iconColor" placeholder="请选择图标颜色" style="width: 100%">
            <el-option label="蓝色" value="blue"></el-option>
            <el-option label="绿色" value="green"></el-option>
            <el-option label="橙色" value="orange"></el-option>
            <el-option label="灰色" value="gray"></el-option>
            <el-option label="红色" value="red"></el-option>
            <el-option label="紫色" value="purple"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="图标">
          <el-select v-model="editingShortcut.icon" placeholder="请选择图标" style="width: 100%">
            <el-option label="添加" value="el-icon-plus"></el-option>
            <el-option label="用户" value="el-icon-user"></el-option>
            <el-option label="日历" value="el-icon-date"></el-option>
            <el-option label="文档" value="el-icon-document"></el-option>
            <el-option label="设置" value="el-icon-setting"></el-option>
            <el-option label="电话" value="el-icon-phone"></el-option>
            <el-option label="消息" value="el-icon-message"></el-option>
            <el-option label="图表" value="el-icon-data-analysis"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateShortcut">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog title="确认删除" v-model="deleteDialogVisible" width="30%" :append-to-body="true" :modal-append-to-body="false" :close-on-click-modal="false" center>
      <p>确定要删除"{{ shortcutToDelete?.label }}"吗？</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="deleteShortcut">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { defineEmits, defineProps, ref } from 'vue';

// 定义快捷入口数据结构
interface Shortcut {
  id: string;
  label: string;
  icon: string;
  iconColor: string;
  route: string;
}

// 接收props或使用默认值
const props = defineProps({
  shortcuts: {
    type: Array as () => Shortcut[],
    default: () => []
  }
});

// 定义事件
const emit = defineEmits(['navigate', 'update:shortcuts']);

// 本地状态
const shortcuts = ref<Shortcut[]>(props.shortcuts);
const addDialogVisible = ref(false);
const manageDialogVisible = ref(false);
const editDialogVisible = ref(false);
const deleteDialogVisible = ref(false);
const shortcutToDelete = ref<Shortcut | null>(null);
const editingShortcut = ref<Shortcut | null>(null);
const shortcutFormRef = ref(null);
const editFormRef = ref(null);

// 表单验证规则
const shortcutRules = {
  label: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { min: 2, max: 10, message: '长度在 2 到 10 个字符之间', trigger: 'blur' }
  ],
  route: [{ required: true, message: '请输入路由', trigger: 'blur' }]
};

// 可用的路由选项
const availableRoutes = [
  { label: '新建保单', value: 'new-policy' },
  { label: '客户管理', value: 'customer' },
  { label: '日程安排', value: 'schedule' },
  { label: '销售报表', value: 'report' },
  { label: '理赔管理', value: 'claims' },
  { label: '业绩分析', value: 'performance' },
  { label: '产品中心', value: 'products' },
  { label: '知识库', value: 'knowledge' }
];

const newShortcut = ref<Partial<Shortcut>>({
  label: '',
  route: '',
  icon: 'el-icon-plus',
  iconColor: 'blue'
});

// 处理快捷方式点击
const handleShortcutClick = (route: string) => {
  emit('navigate', route);
};

// 显示添加对话框
const showAddShortcutDialog = () => {
  newShortcut.value = {
    label: '',
    route: '',
    icon: 'el-icon-plus',
    iconColor: 'blue'
  };
  addDialogVisible.value = true;
  manageDialogVisible.value = false;
};

// 显示管理对话框
const showManageDialog = () => {
  manageDialogVisible.value = true;
};

// 关闭对话框
const handleCloseDialog = () => {
  addDialogVisible.value = false;
};

// 添加快捷入口
const addShortcut = () => {
  if (!newShortcut.value.label || !newShortcut.value.route) {
    return;
  }

  const nextId = `P${1000 + shortcuts.value.length + 1}`;

  const shortcutToAdd: Shortcut = {
    id: nextId,
    label: newShortcut.value.label || '',
    icon: newShortcut.value.icon || 'el-icon-plus',
    iconColor: newShortcut.value.iconColor || 'blue',
    route: newShortcut.value.route || ''
  };

  shortcuts.value.push(shortcutToAdd);
  emit('update:shortcuts', shortcuts.value);
  addDialogVisible.value = false;
};

// 编辑快捷入口
const editShortcut = (shortcut: Shortcut) => {
  editingShortcut.value = { ...shortcut };
  editDialogVisible.value = true;
};

// 更新快捷入口
const updateShortcut = () => {
  if (!editingShortcut.value) return;

  const index = shortcuts.value.findIndex((item) => item.id === editingShortcut.value?.id);
  if (index > -1) {
    shortcuts.value[index] = { ...editingShortcut.value };
    emit('update:shortcuts', shortcuts.value);
    editDialogVisible.value = false;
  }
};

// 确认删除快捷入口
const confirmDeleteShortcut = (shortcut: Shortcut) => {
  shortcutToDelete.value = shortcut;
  deleteDialogVisible.value = true;
};

// 删除快捷入口
const deleteShortcut = () => {
  if (!shortcutToDelete.value) return;

  shortcuts.value = shortcuts.value.filter((item) => item.id !== shortcutToDelete.value?.id);
  emit('update:shortcuts', shortcuts.value);
  deleteDialogVisible.value = false;
};

// 刷新快捷入口
const refreshShortcuts = () => {
  console.log('刷新快捷入口');
  // 这里可以添加从后端获取最新快捷入口的逻辑
};

// 暴露方法给父组件
defineExpose({
  shortcuts
});
</script>

<style scoped>
.module-container {
  margin-bottom: 16px;
}

.shortcuts-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 14px;
}

.header-right {
  display: flex;
  gap: 8px;
}

.header-right :deep(.el-button) {
  padding: 4px 8px;
  font-size: 12px;
}

.expand-icon {
  margin-right: 4px;
  color: #909399;
}

.shortcuts-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.shortcut-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  border: 1px solid #ebeef5;
}

.shortcut-item:hover {
  background-color: #f5f7fa;
}

.shortcut-content {
  display: flex;
  align-items: center;
}

.shortcut-icon,
.shortcut-icon-small {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  color: white;
}

.shortcut-icon {
  width: 32px;
  height: 32px;
  margin-right: 10px;
  font-size: 16px;
}

.shortcut-icon-small {
  width: 24px;
  height: 24px;
  font-size: 14px;
}

.shortcut-icon.blue,
.shortcut-icon-small.blue {
  background-color: #409eff;
}

.shortcut-icon.green,
.shortcut-icon-small.green {
  background-color: #67c23a;
}

.shortcut-icon.orange,
.shortcut-icon-small.orange {
  background-color: #e6a23c;
}

.shortcut-icon.gray,
.shortcut-icon-small.gray {
  background-color: #909399;
}

.shortcut-icon.red,
.shortcut-icon-small.red {
  background-color: #f56c6c;
}

.shortcut-icon.purple,
.shortcut-icon-small.purple {
  background-color: #9254de;
}

.shortcut-info {
  display: flex;
  flex-direction: column;
}

.shortcut-label {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.shortcut-id {
  font-size: 12px;
  color: #909399;
}

.shortcut-actions {
  opacity: 0;
  transition: opacity 0.2s;
}

.shortcut-item:hover .shortcut-actions {
  opacity: 1;
}

.shortcut-actions i {
  font-size: 16px;
  color: #909399;
  cursor: pointer;
}

.shortcut-actions i:hover {
  color: #f56c6c;
}

.delete-btn {
  color: #f56c6c;
}

/* 添加快捷入口表单样式 */
.add-shortcut-form {
  padding: 0 10px;
}

.add-shortcut-form :deep(.el-form-item__label) {
  padding-bottom: 4px;
  font-size: 14px;
}

.add-shortcut-form :deep(.el-input),
.add-shortcut-form :deep(.el-select) {
  width: 100%;
}

/* 对话框样式覆盖 */
:deep(.el-dialog__header) {
  text-align: left;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  margin-right: 0;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-dialog__headerbtn) {
  top: 15px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 10px 20px;
  border-top: 1px solid #ebeef5;
}
</style>
