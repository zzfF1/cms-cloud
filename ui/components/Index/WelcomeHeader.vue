<template>
  <div class="welcome-section">
    <div class="welcome-content">
      <div class="welcome-row">
        <div class="left-content">
          <div class="title">工作台</div>
          <div class="greeting">你好，{{ nickname }}，祝你开心每一天！</div>
          <div class="info">当前日期：{{ curDate }} &nbsp;&nbsp;&nbsp;登录IP：{{ loginIp }}</div>
        </div>
        <div class="right-content">
          <el-button type="primary" size="small" @click="showComponentManagement">
            <i class="el-icon-setting"></i>
            管理组件
          </el-button>
        </div>
      </div>
    </div>

    <!-- 组件管理对话框 -->
    <el-dialog
      title="首页组件管理"
      v-model="componentDialogVisible"
      width="70%"
      :append-to-body="true"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      center
    >
      <div class="component-management">
        <div class="divider"></div>

        <!-- 可配置组件区域 -->
        <div class="configurable-components-section">
          <div class="section-title">可配置组件</div>
          <el-table :data="configurableComponents" style="width: 100%">
            <el-table-column prop="name" label="组件名称" width="160"></el-table-column>
            <el-table-column prop="description" label="描述"></el-table-column>
            <el-table-column label="显示" width="80">
              <template #default="scope">
                <el-switch v-model="scope.row.enabled" active-color="#13ce66" inactive-color="#dcdfe6"></el-switch>
              </template>
            </el-table-column>
            <el-table-column label="宽度" width="120">
              <template #default="scope">
                <el-select v-model="scope.row.width" placeholder="选择宽度" size="small" :disabled="!scope.row.enabled">
                  <el-option label="1/3" value="third"></el-option>
                  <el-option label="1/2" value="half"></el-option>
                  <el-option label="2/3" value="two-thirds"></el-option>
                  <el-option label="全宽" value="full"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="排序" width="120">
              <template #default="scope">
                <div class="sort-actions" v-if="scope.row.enabled">
                  <el-button type="text" @click="moveComponent(scope.row, 'up')" :disabled="!canMoveUp(scope.row)">
                    <i class="el-icon-top"></i>
                  </el-button>
                  <el-button type="text" @click="moveComponent(scope.row, 'down')" :disabled="!canMoveDown(scope.row)">
                    <i class="el-icon-bottom"></i>
                  </el-button>
                </div>
                <div v-else class="sort-actions-disabled">
                  <i class="el-icon-minus"></i>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 布局预览区域 -->
      <div class="layout-preview">
        <div class="preview-title">布局预览</div>
        <div class="preview-container">
          <div class="preview-header">工作台</div>
          <div class="preview-body">
            <div class="preview-left">通知 (2/3宽度)</div>
            <div class="preview-right">快捷入口 (1/3宽度)</div>
          </div>
          <div class="preview-dynamic">
            <div v-for="comp in previewComponents" :key="comp.id" :class="['preview-component', `preview-${comp.width || 'full'}`]">
              {{ comp.name }} ({{ getWidthLabel(comp.width) }})
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="componentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveComponentSettings">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted } from 'vue';
import dayjs from 'dayjs';

// 组件接收props
const props = defineProps<{
  nickname: string;
  loginIp: string;
}>();

// 定义事件
const emit = defineEmits(['update:components']);

// 当前日期
const curDate = computed(() => dayjs(new Date()).format('YYYY-MM-DD'));

// 组件管理相关
const componentDialogVisible = ref(false);

// 所有可配置组件列表（不包含固定组件）
const configurableComponents = ref([
  { id: 'data-overview', name: '个人数据概览', description: '显示工作量和销售数据统计', enabled: true, order: 3, width: 'full' },
  { id: 'calendar', name: '日历', description: '显示本月日程安排', enabled: true, order: 4, width: 'two-thirds' }
]);

// 固定组件列表（这些组件不可配置）
const fixedComponents = ref([
  { id: 'welcome', name: '工作台', description: '显示欢迎信息和用户登录信息', enabled: true, order: 0, fixed: true, position: 'top' },
  {
    id: 'notification',
    name: '通知',
    description: '显示业务通知、系统提醒和团队活动',
    enabled: true,
    order: 1,
    fixed: true,
    position: 'left',
    width: 'two-thirds'
  },
  { id: 'shortcuts', name: '快捷入口', description: '快速访问常用功能', enabled: true, order: 2, fixed: true, position: 'right', width: 'third' }
]);

// 显示组件管理对话框
const showComponentManagement = () => {
  componentDialogVisible.value = true;
};

// 获取宽度标签
const getWidthLabel = (width) => {
  switch (width) {
    case 'third':
      return '1/3宽度';
    case 'half':
      return '1/2宽度';
    case 'two-thirds':
      return '2/3宽度';
    case 'full':
      return '全宽';
    default:
      return '全宽';
  }
};

// 用于预览的组件列表
const previewComponents = computed(() => {
  return configurableComponents.value.filter((comp) => comp.enabled).sort((a, b) => a.order - b.order);
});

// 检查组件是否可以上移
const canMoveUp = (component) => {
  if (!component.enabled) return false;
  const index = getEnabledIndex(component);
  return index > 0;
};

// 检查组件是否可以下移
const canMoveDown = (component) => {
  if (!component.enabled) return false;
  const index = getEnabledIndex(component);
  const enabledConfigurable = configurableComponents.value.filter((comp) => comp.enabled);
  return index < enabledConfigurable.length - 1 && index !== -1;
};

// 获取组件在已启用组件中的索引（不包括固定组件）
const getEnabledIndex = (component) => {
  // 只获取可配置组件中已启用的组件
  const enabledConfigurable = configurableComponents.value.filter((comp) => comp.enabled).sort((a, b) => a.order - b.order);

  return enabledConfigurable.findIndex((comp) => comp.id === component.id);
};

// 上移或下移组件
const moveComponent = (component, direction) => {
  // 获取可配置组件中已启用的组件
  const enabledConfigurable = configurableComponents.value.filter((comp) => comp.enabled).sort((a, b) => a.order - b.order);

  // 获取组件在已启用的可配置组件中的索引
  const index = enabledConfigurable.findIndex((comp) => comp.id === component.id);

  if (index === -1) return;

  if (direction === 'up' && index > 0) {
    // 交换当前组件和上一个组件的order值
    const currentOrder = component.order;
    component.order = enabledConfigurable[index - 1].order;
    enabledConfigurable[index - 1].order = currentOrder;
  } else if (direction === 'down' && index < enabledConfigurable.length - 1) {
    // 交换当前组件和下一个组件的order值
    const currentOrder = component.order;
    component.order = enabledConfigurable[index + 1].order;
    enabledConfigurable[index + 1].order = currentOrder;
  }

  // 重新排序所有组件
  updateComponentOrders();
};

// 更新所有组件的顺序
const updateComponentOrders = () => {
  // 首先按照现有顺序排序已启用的可配置组件
  const enabledConfigurable = configurableComponents.value.filter((comp) => comp.enabled).sort((a, b) => a.order - b.order);

  // 然后重新分配order值，从固定组件数量开始
  const startOrder = fixedComponents.value.length;
  enabledConfigurable.forEach((comp, index) => {
    comp.order = startOrder + index;
  });

  // 未启用的组件设置为-1
  configurableComponents.value
    .filter((comp) => !comp.enabled)
    .forEach((comp) => {
      comp.order = -1;
    });
};

// 保存组件设置
const saveComponentSettings = () => {
  // 更新组件顺序
  updateComponentOrders();

  // 创建要发送给父组件的组件数组（固定组件+已启用的可配置组件）
  const configEnabled = configurableComponents.value
    .filter((comp) => comp.enabled)
    .map((comp) => ({
      id: comp.id,
      name: comp.name,
      description: comp.description,
      width: comp.width || 'third' // 确保有宽度属性
    }));

  const fixedComponentsData = fixedComponents.value.map((comp) => ({
    id: comp.id,
    name: comp.name,
    description: comp.description,
    fixed: true,
    position: comp.position,
    width: comp.width
  }));

  // 通知父组件更新组件配置
  emit('update:components', [...fixedComponentsData, ...configEnabled]);
  componentDialogVisible.value = false;
};

// 初始化
onMounted(() => {
  // 确保组件顺序正确
  updateComponentOrders();

  // 设置正确的排序值，使可配置组件的order从固定组件数量开始
  const startOrder = fixedComponents.value.length;
  configurableComponents.value.forEach((comp, index) => {
    if (comp.enabled) {
      comp.order = startOrder + index;
    }
  });
});
</script>

<style scoped>
.welcome-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid rgba(230, 230, 230, 0.5);
}

.welcome-content {
  padding: 0 15px;
}

.welcome-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-content {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.right-content {
  display: flex;
  align-items: center;
}

.title {
  font-weight: bold;
  font-size: 16px;
  color: #1890ff;
  margin-right: 15px;
}

.greeting {
  font-size: 14px;
  color: #262626;
  margin-right: 15px;
}

.info {
  font-size: 12px;
  color: #8c8c8c;
}

.component-management {
  max-height: 300px;
  overflow-y: auto;
}

.section-title {
  font-weight: 500;
  font-size: 15px;
  margin-bottom: 10px;
  color: #303133;
}

.fixed-components-section,
.configurable-components-section {
  margin-bottom: 20px;
}

.divider {
  height: 1px;
  background-color: #ebeef5;
  margin: 15px 0;
}

.sort-actions {
  display: flex;
  justify-content: center;
}

.sort-actions-disabled {
  display: flex;
  justify-content: center;
  color: #c0c4cc;
}

/* 布局预览样式 */
.layout-preview {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.preview-title {
  font-weight: 500;
  font-size: 15px;
  margin-bottom: 10px;
  color: #303133;
}

.preview-container {
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  background-color: #f5f7fa;
  padding: 10px;
}

.preview-header {
  background-color: #e0f2fe;
  color: #0284c7;
  padding: 8px;
  text-align: center;
  border-radius: 4px;
  font-weight: bold;
  margin-bottom: 10px;
}

.preview-body {
  display: flex;
  gap: 10px;
  min-height: 60px;
  margin-bottom: 10px;
}

.preview-left {
  width: 66.666%;
  background-color: #dbeafe;
  color: #2563eb;
  padding: 8px;
  border-radius: 4px;
  text-align: center;
  font-weight: 500;
}

.preview-right {
  width: 33.333%;
  background-color: #dbeafe;
  color: #2563eb;
  padding: 8px;
  border-radius: 4px;
  text-align: center;
  font-weight: 500;
}

.preview-dynamic {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.preview-component {
  background-color: #d1fae5;
  color: #047857;
  padding: 8px;
  text-align: center;
  border-radius: 4px;
  font-weight: 500;
  margin-bottom: 10px;
}

.preview-third {
  width: calc(33.333% - 7px);
}

.preview-half {
  width: calc(50% - 5px);
}

.preview-two-thirds {
  width: calc(66.666% - 3px);
}

.preview-full {
  width: 100%;
}

@media (max-width: 992px) {
  .welcome-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .left-content {
    margin-bottom: 10px;
  }

  .title,
  .greeting,
  .info {
    margin-bottom: 5px;
  }

  .preview-body {
    flex-direction: column;
  }

  .preview-left,
  .preview-right {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>
