<template>
  <!-- 添加或修改角色抽屉 -->
  <el-drawer v-model="visibleProxy" :title="title" size="45%" append-to-body :close-on-click-modal="false" @closed="resetForm">
    <div class="drawer-content-wrapper" v-loading="drawerLoading" element-loading-text="数据加载中...">
      <el-card v-if="!drawerLoading" shadow="hover" class="box-card">
        <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="100px">
          <!-- 基本信息区域 -->
          <div class="form-section">
            <div class="section-header">
              <el-icon class="header-icon"><document /></el-icon>
              <span class="section-title">基本信息</span>
            </div>
            <el-divider />
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="角色名称" prop="roleName">
                  <el-input v-model="form.roleName" placeholder="请输入角色名称" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="权限字符" prop="roleKey">
                  <el-tooltip content="控制器中定义的权限字符，如：@SaCheckRole('admin')" placement="top">
                    <el-input v-model="form.roleKey" placeholder="请输入权限字符" clearable>
                      <template #suffix>
                        <el-icon class="input-icon"><question-filled /></el-icon>
                      </template>
                    </el-input>
                  </el-tooltip>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="角色顺序" prop="roleSort">
                  <el-input-number v-model="form.roleSort" controls-position="right" :min="0" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态">
                  <el-radio-group v-model="form.status">
                    <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row>
              <el-col :span="24">
                <el-form-item label="备注">
                  <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" :rows="2" maxlength="500" show-word-limit></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 菜单权限区域 -->
          <div class="form-section menu-section">
            <div class="section-header">
              <el-icon class="header-icon"><menu /></el-icon>
              <span class="section-title">菜单权限</span>
              <el-tag type="primary" size="small" effect="plain" class="selected-count-tag">已选中: {{ selectedCount }}</el-tag>
            </div>
            <el-divider />

            <!-- 菜单操作工具栏 -->
            <div class="menu-toolbar">
              <div class="menu-actions">
                <el-button size="small" :type="menuExpand ? 'success' : 'default'" @click="toggleExpand">
                  <el-icon><expand /></el-icon>
                  {{ menuExpand ? '折叠' : '展开' }}
                </el-button>
                <el-button size="small" :type="menuNodeAll ? 'warning' : 'default'" @click="toggleAllSelect">
                  {{ menuNodeAll ? '全不选' : '全选' }}
                </el-button>
                <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect" class="parent-linked">父子联动</el-checkbox>
              </div>
              <div class="menu-actions">
                <el-button size="small" type="danger" :disabled="selectedCount === 0" @click="clearAllSelection">
                  <el-icon><delete /></el-icon>
                  清空选择
                </el-button>
              </div>
            </div>

            <!-- 搜索过滤区域 -->
            <div class="search-container">
              <el-input
                v-model="filterText"
                placeholder="输入关键字过滤菜单"
                clearable
                class="search-input"
              >
                <template #prefix>
                  <el-icon><search /></el-icon>
                </template>
              </el-input>
            </div>

            <!-- 菜单树区域 -->
            <el-tree
              ref="menuRef"
              :data="menuOptions"
              show-checkbox
              node-key="id"
              :default-expand-all="menuExpand"
              :check-strictly="!form.menuCheckStrictly"
              empty-text="加载中，请稍候"
              :props="{ label: 'label', children: 'children' }"
              :filter-node-method="filterNode"
              @check="handleNodeCheck"
              class="menu-tree"
              highlight-current
              v-loading="treeLoading"
              element-loading-text="菜单加载中..."
            >
              <template #default="{ node, data }">
                <span class="custom-tree-node">
                  <!-- 节点图标和标签 -->
                  <span class="node-info">
                    <el-icon v-if="data.menuType === 'M'" class="directory-icon"><folder /></el-icon>
                    <el-icon v-else-if="data.menuType === 'C'" class="menu-icon"><menu /></el-icon>
                    <el-icon v-else-if="data.menuType === 'F'" class="button-icon"><operation /></el-icon>
                    <span :class="getNodeClass(data.menuType)">{{ node.label }}</span>
                  </span>

                  <!-- 节点标签 -->
                  <span class="node-tag">
                    <el-tag v-if="data.menuType === 'M'" type="success" size="small">目录</el-tag>
                    <el-tag v-else-if="data.menuType === 'C'" type="primary" size="small">菜单</el-tag>
                    <el-tag v-else-if="data.menuType === 'F'" type="warning" size="small">按钮</el-tag>
                  </span>
                </span>
              </template>
            </el-tree>
          </div>
        </el-form>
      </el-card>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { addRole, getRole, updateRole } from '@/api/system/role';
import { roleMenuTreeselect, treeselect } from '@/api/system/menu/index';
import { RoleForm, RoleVO } from '@/api/system/role/types';
import { MenuTreeOption, RoleMenuTree } from '@/api/system/menu/types';
import { ref, computed, watch, nextTick } from 'vue';
import { ComponentInternalInstance, PropType } from 'vue';
import {
  QuestionFilled,
  Folder,
  Menu,
  Operation,
  Search,
  Document,
  Select,
  Delete,
  Expand
} from '@element-plus/icons-vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  editType: {
    type: String,
    default: 'add'
  },
  rowData: {
    type: Object as PropType<RoleVO | null>,
    default: null
  }
});

const emit = defineEmits(['update:visible', 'save']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = proxy?.useDict('sys_normal_disable');

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 根据编辑类型决定标题
const title = computed(() => (props.editType === 'add' ? '添加角色' : '修改角色'));

// 加载状态
const drawerLoading = ref(false);
const treeLoading = ref(false);
const buttonLoading = ref(false);

// 表单ref
const roleFormRef = ref();
const menuRef = ref();

// 树形菜单数据
const menuOptions = ref<MenuTreeOption[]>([]);

// 展开/全选状态
const menuExpand = ref(false);
const menuNodeAll = ref(false);

// 选中的数量
const selectedCount = ref(0);

// 搜索过滤
const filterText = ref('');

// 已选中的菜单列表
const selectedMenus = ref<any[]>([]);

// 根据菜单类型分类的计算属性
const directoryMenus = computed(() => {
  return selectedMenus.value.filter(menu => menu.menuType === 'M');
});

const normalMenus = computed(() => {
  return selectedMenus.value.filter(menu => menu.menuType === 'C');
});

const buttonMenus = computed(() => {
  return selectedMenus.value.filter(menu => menu.menuType === 'F');
});

// 初始化表单数据
const initFormData: RoleForm = {
  roleId: undefined,
  roleSort: 1,
  status: '0',
  roleName: '',
  roleKey: '',
  menuCheckStrictly: true,
  deptCheckStrictly: true,
  remark: '',
  dataScope: '1',
  menuIds: [],
  deptIds: [],
  roleType: 'N'
};

// 表单数据
const form = ref<RoleForm>({ ...initFormData });

// 表单校验规则
const rules = ref({
  roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
  roleKey: [{ required: true, message: '权限字符不能为空', trigger: 'blur' }],
  roleSort: [{ required: true, message: '角色顺序不能为空', trigger: 'blur' }]
});

/**
 * 过滤节点方法
 */
const filterNode = (value: string, data: any) => {
  if (!value) return true;
  return data.label.toLowerCase().includes(value.toLowerCase());
};

/**
 * 获取节点样式类
 */
const getNodeClass = (menuType: string): string => {
  switch (menuType) {
    case 'M': return 'directory-label';
    case 'C': return 'menu-label';
    case 'F': return 'button-label';
    default: return '';
  }
};

/**
 * 更新已选菜单列表
 */
const updateSelectedMenus = () => {
  if (!menuRef.value) {
    selectedMenus.value = [];
    selectedCount.value = 0;
    return;
  }

  try {
    const checkedNodes = menuRef.value.getCheckedNodes() || [];
    selectedMenus.value = checkedNodes;
    selectedCount.value = checkedNodes.length;
  } catch (error) {
    console.error('更新已选菜单失败:', error);
    selectedMenus.value = [];
    selectedCount.value = 0;
  }
};

/**
 * 处理节点选中状态变更
 */
const handleNodeCheck = () => {
  if (menuRef.value) {
    updateSelectedMenus();
  }
};

/**
 * 清空所有选择
 */
const clearAllSelection = () => {
  if (menuRef.value) {
    menuRef.value.setCheckedKeys([]);
    updateSelectedMenus();
  }
};

/**
 * 切换展开状态
 */
const toggleExpand = () => {
  menuExpand.value = !menuExpand.value;
  handleCheckedTreeExpand(menuExpand.value);
};

/**
 * 切换全选状态
 */
const toggleAllSelect = () => {
  menuNodeAll.value = !menuNodeAll.value;
  handleCheckedTreeNodeAll(menuNodeAll.value);
};

/** 取消按钮 */
const cancel = (): void => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = (): void => {
  form.value = { ...initFormData };
  selectedCount.value = 0;
  menuExpand.value = false;
  menuNodeAll.value = false;
  filterText.value = '';
  selectedMenus.value = [];
  roleFormRef.value?.resetFields();
  // 添加空值检查
  if (menuRef.value) {
    menuRef.value.setCheckedKeys([]);
  }
};

/**
 * 递归展开或折叠树节点
 */
const expandAllNodes = (expand: boolean, nodes: any[]) => {
  if (!nodes || !nodes.length) return;

  nodes.forEach(node => {
    if (menuRef.value?.store.nodesMap[node.id]) {
      menuRef.value.store.nodesMap[node.id].expanded = expand;
    }

    if (node.children && node.children.length) {
      expandAllNodes(expand, node.children);
    }
  });
};

/** 树权限（展开/折叠）*/
const handleCheckedTreeExpand = (value: boolean): void => {
  menuExpand.value = value;
  expandAllNodes(value, menuOptions.value);
};

/** 树权限（全选/全不选） */
const handleCheckedTreeNodeAll = (value: boolean): void => {
  // 添加空值检查
  if (menuRef.value) {
    menuRef.value.setCheckedNodes(value ? menuOptions.value : []);
    // 更新已选中菜单列表
    updateSelectedMenus();
  }
};

/** 树权限（父子联动） */
const handleCheckedTreeConnect = (value: boolean): void => {
  form.value.menuCheckStrictly = value;
};

/** 所有菜单节点数据 */
const getMenuAllCheckedKeys = (): (string | number)[] => {
  // 添加空值检查
  if (!menuRef.value) return [];

  // 目前被选中的菜单节点
  let checkedKeys = menuRef.value.getCheckedKeys() || [];
  // 半选中的菜单节点
  let halfCheckedKeys = menuRef.value.getHalfCheckedKeys() || [];
  if (halfCheckedKeys && halfCheckedKeys.length) {
    checkedKeys = [...checkedKeys, ...halfCheckedKeys];
  }
  return checkedKeys;
};

/**
 * 获取菜单树数据
 */
const getMenuTreeselect = async (): Promise<void> => {
  treeLoading.value = true;
  try {
    const res = await treeselect();
    menuOptions.value = res.data;
  } catch (error) {
    console.error('获取菜单树数据失败:', error);
  } finally {
    treeLoading.value = false;
  }
};

/** 根据角色ID查询菜单树结构 */
const getRoleMenuTreeselect = async (roleId: string | number) => {
  return roleMenuTreeselect(roleId).then((res): RoleMenuTree => {
    menuOptions.value = res.data.menus;
    return res.data;
  });
};

/**
 * 提交表单
 */
const submitForm = (): void => {
  roleFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        form.value.menuIds = getMenuAllCheckedKeys();
        if (form.value.roleId) {
          await updateRole(form.value);
        } else {
          await addRole(form.value);
        }
        proxy?.$modal.msgSuccess('操作成功');
        emit('save');
        visibleProxy.value = false;
      } catch (error) {
        console.error('提交表单失败:', error);
      } finally {
        buttonLoading.value = false;
      }
    }
  });
};

// 用这个改进版本替换现有的watch处理程序
watch(
  [() => props.visible, () => props.editType, () => props.rowData],
  async ([visible, type, data]) => {
    if (visible) {
      // 显示加载状态
      drawerLoading.value = true;
      resetForm();
      try {
        // 先获取菜单树数据
        await getMenuTreeselect();

        if (type === 'edit' && data) {
          // 修改操作
          try {
            const res = await getRole(data.roleId);
            Object.assign(form.value, res.data);
            form.value.roleSort = Number(form.value.roleSort);
            // 获取角色菜单数据
            const roleMenuData = await getRoleMenuTreeselect(data.roleId);
            // 重要：使用setTimeout确保树已完全渲染
            setTimeout(() => {
              if (menuRef.value && roleMenuData.checkedKeys.length > 0) {
                // 使用setCheckedKeys而不是单独的setChecked调用
                menuRef.value.setCheckedKeys(roleMenuData.checkedKeys);
                // 设置选中键后更新选中计数
                updateSelectedMenus();
              }
            }, 200); // 短暂延迟以确保树准备就绪
          } catch (error) {
            console.error('获取角色数据失败:', error);
          }
        }
      } finally {
        // 隐藏加载状态
        drawerLoading.value = false;
      }
    }
  }
);

// 监听过滤文本变化
watch(filterText, (val) => {
  menuRef.value?.filter(val);
});
</script>

<style scoped>
.drawer-content-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.box-card {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  padding: 0;
  background-color: #fcfcfc;
}

:deep(.el-form) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 15px 20px;
}

:deep(.el-drawer__body) {
  padding: 0;
  overflow: hidden;
  height: calc(100% - 60px); /* 根据头部高度调整 */
}

.form-section {
  margin-bottom: 20px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 15px;
}

.section-header {
  display: flex;
  align-items: center;
  padding: 5px 0;
}

.header-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #409eff;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-right: 10px;
}

.selected-count-tag {
  margin-left: 5px;
  font-weight: bold;
}

.menu-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-bottom: 0;
  overflow: hidden;
}

.menu-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px; /* 从15px减小到10px */
  padding: 6px; /* 从10px减小到6px */
  background-color: #f6f8fa;
  border-radius: 6px;
  border: 1px solid #eaeefb;
}

.menu-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-container {
  margin-bottom: 10px; /* 从15px减小到10px */
}

.search-input :deep(.el-input__inner) {
  border-color: #dcdfe6;
}

.search-input :deep(.el-input__inner:focus) {
  border-color: #409eff;
}

.menu-tree {
  height: calc(100vh - 350px);
  min-height: 300px;
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  padding: 6px; /* 从10px减小到6px */
  overflow: auto;
  background-color: #fff;
}

.menu-tree :deep(.el-tree-node__content) {
  height: 30px; /* 从36px减小到30px */
  margin: 0; /* 从2px 0减小到0 */
  border-radius: 4px;
}

.menu-tree :deep(.el-tree-node__content:hover) {
  background-color: #f0f7ff;
}

.menu-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
}

.menu-tree :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #409eff;
  border-color: #409eff;
}

.menu-tree :deep(.el-tree-node__expand-icon) {
  color: #67c23a;
  padding: 4px;
  margin-right: 2px;
}

/* 缩小节点之间的缩进 */
.menu-tree :deep(.el-tree-node__children) {
  padding-left: 16px; /* 默认通常是24px或更多 */
}

.custom-tree-node {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 28px; /* 从32px减小到28px */
  line-height: 28px;
}

.node-info {
  display: flex;
  align-items: center;
}

.node-tag {
  margin-left: auto;
}

.node-tag :deep(.el-tag) {
  padding: 0 4px; /* 减小标签内边距 */
  height: 20px; /* 减小标签高度 */
  line-height: 20px; /* 调整行高匹配高度 */
}

.directory-icon {
  color: #e6a23c;
  margin-right: 4px; /* 从8px减小到4px */
  font-size: 16px;
}

.menu-icon {
  color: #409eff;
  margin-right: 4px; /* 从8px减小到4px */
  font-size: 16px;
}

.button-icon {
  color: #f56c6c;
  margin-right: 4px; /* 从8px减小到4px */
  font-size: 16px;
}

.directory-label {
  font-weight: bold;
  color: #303133;
}

.menu-label {
  font-weight: normal;
  color: #2c3e50;
}

.button-label {
  font-weight: normal;
  color: #606266;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 10px;
}

:deep(.el-card__body) {
  padding: 10px;
  height: 100%;
  overflow: auto;
}

:deep(.el-divider--horizontal) {
  margin: 12px 0;
}

.parent-linked {
  color: #409eff;
  font-weight: 500;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #409eff;
}

/* 按钮样式增强 */
:deep(.el-button--primary) {
  background-color: #409eff;
}

:deep(.el-button--success) {
  background-color: #67c23a;
}

:deep(.el-button--warning) {
  background-color: #e6a23c;
}

:deep(.el-button--danger) {
  background-color: #f56c6c;
}

:deep(.el-button--default:hover) {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}
</style>
