<template>
  <div class="menu-management-container">
    <el-row class="full-height-row" :gutter="10">
      <!-- 左侧树形菜单 -->
      <el-col :span="6" class="full-height-col">
        <el-card shadow="hover" class="full-height-card">
          <template #header>
            <div class="card-header">
              <span>菜单结构</span>
              <div class="header-actions">
                <el-tooltip content="展开/收缩" placement="top">
                  <el-button icon="Sort" circle size="small" @click="toggleExpandAll"></el-button>
                </el-tooltip>
                <el-tooltip content="刷新" placement="top">
                  <el-button icon="Refresh" circle size="small" @click="refreshTreeData"></el-button>
                </el-tooltip>
              </div>
            </div>
          </template>

          <div class="search-container">
            <el-input
              v-model="treeSearchText"
              placeholder="搜索菜单"
              clearable
              @keyup.enter="searchTree"
              @clear="refreshTreeData"
            >
              <template #suffix>
                <el-button :icon="Search" circle @click="searchTree"></el-button>
              </template>
            </el-input>
          </div>

          <!-- 修复树容器高度问题 -->
          <div class="tree-scroll-container">
            <el-tree
              ref="menuTreeRef"
              :data="treeData"
              :props="defaultProps"
              :default-expand-all="isExpandAll"
              node-key="menuId"
              highlight-current
              :expand-on-click-node="false"
              @node-click="handleNodeClick"
            >
              <!-- 自定义树节点内容 -->
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <!-- 图标根据菜单类型显示不同 -->
                  <div class="node-left">
                    <el-icon v-if="data.menuType === 'M'" class="tree-icon"><Folder /></el-icon>
                    <el-icon v-else class="tree-icon"><Menu /></el-icon>
                    <span class="node-label">{{ node.label }}</span>
                  </div>

                  <!-- 统计信息 -->
                  <div class="node-statistics">
                    <el-tag v-if="data.menuCount > 0" size="small" type="success" class="stat-tag">
                      {{ data.menuCount }}菜单
                    </el-tag>
                    <el-tag v-if="data.buttonCount > 0" size="small" type="warning" class="stat-tag">
                      {{ data.buttonCount }}按钮
                    </el-tag>
                  </div>
                </div>
              </template>
            </el-tree>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧表格 -->
      <el-col :span="18" class="full-height-col">
        <el-card shadow="hover" class="full-height-card">
          <template #header>
            <div class="table-header">
              <div class="header-title">
                <span>{{ currentNodeName || '根目录' }}</span>
                <el-tag size="small" type="info" class="ml-2">共 {{ total }} 条</el-tag>
              </div>
              <div class="header-actions">
                <el-button
                  v-hasPermi="['system:menu:add']"
                  type="primary"
                  plain
                  icon="Plus"
                  @click="handleAdd(currentNode)"
                >
                  新增
                </el-button>
                <el-button icon="Search" plain @click="showSearch = !showSearch">
                  {{ showSearch ? '隐藏' : '显示' }}搜索
                </el-button>
                <el-button icon="Refresh" @click="refreshTableData">刷新</el-button>
              </div>
            </div>
          </template>

          <!-- 搜索区域 -->
          <el-collapse-transition>
            <div v-show="showSearch" class="search-form-container">
              <el-form ref="queryFormRef" :model="queryParams" :inline="true">
                <el-form-item label="菜单名称" prop="menuName">
                  <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                  <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
                    <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-collapse-transition>

          <!-- 改进表格容器以填充满空间 -->
          <div class="full-height-table-container">
            <el-table
              v-loading="tableLoading"
              :data="tableData"
              row-key="menuId"
              stripe
              border
              fit
              highlight-current-row
              @selection-change="handleSelectionChange"
              class="fill-height-table"
            >
              <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="160" />
              <el-table-column prop="icon" label="图标" align="center" width="80">
                <template #default="scope">
                  <svg-icon :icon-class="scope.row.icon" />
                </template>
              </el-table-column>
              <el-table-column prop="menuType" label="类型" align="center" width="80">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.menuType === 'M' ? 'primary' : scope.row.menuType === 'C' ? 'success' : 'warning'"
                  >
                    {{ scope.row.menuType === 'M' ? '目录' : scope.row.menuType === 'C' ? '菜单' : '按钮' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="orderNum" label="排序" width="80" align="center" />
              <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true" width="150" />
              <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true" width="180" />
              <el-table-column prop="status" label="状态" width="80" align="center">
                <template #default="scope">
                  <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
                </template>
              </el-table-column>
              <el-table-column prop="visible" label="可见" width="80" align="center">
                <template #default="scope">
                  <el-icon v-if="scope.row.visible === '0'" class="text-green-500"><View /></el-icon>
                  <el-icon v-else class="text-red-500"><Hide /></el-icon>
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" width="180" align="center">
                <template #default="scope">
                  <el-tooltip content="修改" placement="top">
                    <el-button v-hasPermi="['system:menu:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" />
                  </el-tooltip>
                  <el-tooltip content="新增" placement="top">
                    <el-button v-hasPermi="['system:menu:add']" link type="primary" icon="Plus" @click="handleAdd(scope.row)" />
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <el-button v-hasPermi="['system:menu:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)" />
                  </el-tooltip>
                </template>
              </el-table-column>

              <!-- 空数据填充行 -->
              <template #empty>
                <div class="empty-data">
                  <el-empty description="暂无数据" />
                </div>
              </template>
            </el-table>
            <el-pagination
              v-if="total > 0"
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 引入菜单表单组件 -->
    <menu-form v-model:visible="formVisible" :edit-type="editType" :row-data="currentRow" @save="handleSave"></menu-form>
  </div>
</template>

<script setup name="Menu" lang="ts">
import { delMenu, listMenu } from '@/api/system/menu';
import { MenuQuery, MenuVO } from '@/api/system/menu/types';
import MenuForm from './menuForm.vue';
import { View, Hide, Folder, Menu, Search, Sort } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_show_hide, sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_show_hide', 'sys_normal_disable'));

// 表单控制状态
const formVisible = ref(false);
const editType = ref('');
const currentRow = ref<MenuVO | null>(null);

// 左侧树控制
const treeSearchText = ref('');
const treeData = ref<MenuVO & { menuCount: number; buttonCount: number }[]>([]);
const treeLoading = ref(false);
const currentNode = ref<MenuVO | null>(null);
const currentNodeName = ref('根目录');
const isExpandAll = ref(false); // 控制树的展开/收缩状态
const defaultProps = {
  children: 'children',
  label: 'menuName'
};

// 右侧表格控制
const tableData = ref<MenuVO[]>([]);
const tableLoading = ref(false);
const total = ref(0);
const multipleSelection = ref<MenuVO[]>([]);
const showSearch = ref(true);

// 查询表单ref
const queryFormRef = ref<ElFormInstance>();
const menuTreeRef = ref<ElTreeInstance>();

// 查询参数
const queryParams = reactive<MenuQuery & { pageNum: number; pageSize: number }>({
  menuName: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10
});

/**
 * 切换树展开/收缩状态
 */
const toggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    // 获取所有节点
    const allNodes = menuTreeRef.value?.store.nodesMap;
    if (allNodes) {
      Object.values(allNodes).forEach(node => {
        node.expanded = isExpandAll.value;
      });
    }
  });
};

/**
 * 加载树数据并计算子菜单和按钮统计信息
 */
const loadTreeData = async () => {
  treeLoading.value = true;
  try {
    const params = treeSearchText.value ? { menuName: treeSearchText.value } : undefined;
    const res = await listMenu(params);
    const data = proxy?.handleTree<MenuVO>(res.data, 'menuId');
    if (data) {
      // 添加统计信息
      const enhancedData = addMenuStatistics(data);
      // 过滤掉按钮类型
      treeData.value = filterTreeData(enhancedData);
    }
  } finally {
    treeLoading.value = false;
  }
};

/**
 * 为菜单树添加统计信息
 */
const addMenuStatistics = (data: MenuVO[]): (MenuVO & { menuCount: number; buttonCount: number })[] => {
  return data.map(item => {
    // 默认统计值
    let menuCount = 0;
    let buttonCount = 0;

    // 如果有子菜单，递归处理并累加统计
    if (item.children && item.children.length > 0) {
      const enhancedChildren = addMenuStatistics(item.children);

      // 统计直接子菜单中的菜单和按钮数量
      item.children.forEach(child => {
        if (child.menuType === 'C') menuCount++;
        if (child.menuType === 'F') buttonCount++;
      });

      // 更新子菜单
      item.children = enhancedChildren;
    }

    // 返回带有统计信息的菜单项
    return {
      ...item,
      menuCount,
      buttonCount
    };
  });
};

/**
 * 过滤树形数据，只保留目录和菜单类型
 */
const filterTreeData = (data: (MenuVO & { menuCount: number; buttonCount: number })[]) => {
  return data
    .filter(item => item.menuType !== 'F')
    .map(item => ({
      ...item,
      children: item.children ? filterTreeData(item.children) : []
    }));
};

/**
 * 根据父ID查询菜单列表（包含分页）
 */
const listMenuByParentId = async (parentId: string | number, params: MenuQuery & { pageNum: number; pageSize: number }) => {
  // 如果后端没有直接提供按父ID查询的API，这里先用模拟实现
  const res = await listMenu({
    ...params,
    parentId: parentId.toString()
  });

  // 模拟对后端返回数据的处理
  const allData = res.data;
  const filteredData = allData.filter(item => {
    const matchParent = item.parentId.toString() === parentId.toString();
    const matchName = !params.menuName || item.menuName.includes(params.menuName);
    const matchStatus = !params.status || item.status === params.status;
    return matchParent && matchName && matchStatus;
  });

  // 计算分页
  const total = filteredData.length;
  const start = (params.pageNum - 1) * params.pageSize;
  const end = Math.min(start + params.pageSize, total);
  const pageData = filteredData.slice(start, end);

  return {
    rows: pageData,
    total
  };
};

/**
 * 搜索树节点
 */
const searchTree = () => {
  loadTreeData();
};

/**
 * 刷新树数据
 */
const refreshTreeData = () => {
  treeSearchText.value = '';
  loadTreeData();
};

/**
 * 树节点点击事件
 */
const handleNodeClick = (data: MenuVO) => {
  currentNode.value = data;
  currentNodeName.value = data.menuName;
  // 重置分页并加载表格数据
  queryParams.pageNum = 1;
  loadTableData();
};

/**
 * 加载表格数据
 */
const loadTableData = async () => {
  tableLoading.value = true;
  try {
    const parentId = currentNode.value?.menuId || '0';
    const res = await listMenuByParentId(parentId, queryParams);
    tableData.value = res.rows;
    total.value = res.total;
  } finally {
    tableLoading.value = false;
  }
};

/**
 * 刷新表格数据
 */
const refreshTableData = () => {
  loadTableData();
};

/**
 * 搜索按钮操作
 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  loadTableData();
};

/**
 * 重置查询参数
 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/**
 * 分页大小变化
 */
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val;
  loadTableData();
};

/**
 * 页码变化
 */
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val;
  loadTableData();
};

/**
 * 表格多选框选中数据
 */
const handleSelectionChange = (selection: MenuVO[]) => {
  multipleSelection.value = selection;
};

/**
 * 新增菜单
 */
const handleAdd = (row?: MenuVO) => {
  currentRow.value = row || currentNode.value;
  editType.value = 'add';
  formVisible.value = true;
};

/**
 * 修改菜单
 */
const handleUpdate = (row: MenuVO) => {
  currentRow.value = row;
  editType.value = 'edit';
  formVisible.value = true;
};

/**
 * 保存成功后的处理
 */
const handleSave = () => {
  // 刷新树和表格数据
  loadTreeData();
  loadTableData();
};

/**
 * 删除菜单
 */
const handleDelete = async (row: MenuVO) => {
  await proxy?.$modal.confirm('是否确认删除名称为"' + row.menuName + '"的数据项?');
  await delMenu(row.menuId);
  // 刷新表格数据
  loadTableData();
  proxy?.$modal.msgSuccess('删除成功');
};

// 初始化
onMounted(() => {
  loadTreeData();
  loadTableData();
});
</script>

<style scoped>
/* 关键样式修复 */
.menu-management-container {
  height: calc(100vh - 100px);
  padding: 10px;
  box-sizing: border-box;
}

.full-height-row {
  height: 100%;
}

.full-height-col {
  height: 100%;
}

.full-height-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 修复左侧树高度 */
.tree-scroll-container {
  flex: 1;
  overflow: auto;
  padding: 0;
  min-height: 200px; /* 确保最小高度 */

  /* 强制显示滚动条，不依赖内容高度 */
  position: relative;
  overflow-y: auto;
  height: calc(100% - 60px); /* 减去搜索框高度 */
}

/* 修复表格高度 */
.full-height-table-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  /* 关键：设置最小高度 */
  min-height: calc(100vh - 280px);
  position: relative;
}

.fill-height-table {
  flex: 1;
  width: 100%;
}

/* 空数据时填充整个表格区域 */
.empty-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  min-height: 300px;
}

/* 其他样式保持不变 */
.card-header, .table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
}

.header-title {
  display: flex;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 6px;
}

.search-container {
  padding: 10px 15px;
  border-bottom: 1px solid #ebeef5;
}

.custom-tree-node {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
}

.node-left {
  display: flex;
  align-items: center;
}

.tree-icon {
  margin-right: 6px;
}

.node-label {
  flex: 1;
}

.node-statistics {
  display: flex;
  align-items: center;
}

.stat-tag {
  margin-left: 4px;
  font-size: 10px;
  padding: 0 4px;
  height: 18px;
  line-height: 18px;
}

.search-form-container {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
}

.pagination-container {
  padding: 10px 15px;
  text-align: right;
  border-top: 1px solid #ebeef5;
}

/* 特别处理el-card的body部分 */
:deep(.el-card__body) {
  flex: 1;
  overflow: hidden;
  padding: 0;
  display: flex;
  flex-direction: column;
}

/* 确保表格自身和表体都正确占据空间 */
:deep(.el-table) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.el-table__body-wrapper) {
  flex: 1;
  overflow-y: auto;
}

/* 强制表格占据空间 */
:deep(.el-table__empty-block) {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .stat-tag {
    display: none;
  }
}
</style>
