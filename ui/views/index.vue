<template>
  <div class="dashboard-container">
    <!-- 欢迎信息组件 (固定在顶部) -->
    <WelcomeHeader :nickname="userStore.nickname" :login-ip="userStore.loginIp" @update:components="updateDashboardComponents" />

    <div class="dashboard-layout">
      <!-- 左侧固定区域 (通知组件) - 占据2/3 -->
      <div class="left-fixed-area">
        <NotificationPanel ref="notificationPanel" class="dashboard-component notification-panel" />
      </div>

      <!-- 右侧固定区域 (快捷入口组件) - 占据1/3 -->
      <div class="right-fixed-area">
        <ShortcutPanel
          ref="shortcutPanel"
          :shortcuts="shortcutItems"
          @navigate="navigateTo"
          @update:shortcuts="updateShortcuts"
          class="dashboard-component shortcut-panel"
        />
      </div>
    </div>

    <!-- 动态组件区域 -->
    <div class="dynamic-components-area">
      <!-- 动态加载组件 (除了固定组件) -->
      <template v-for="(component, index) in dynamicComponents" :key="component.id">
        <component
          v-if="getComponentByName(component.id)"
          :is="getComponentByName(component.id)"
          :class="['dashboard-component', `width-${component.width || 'full'}`]"
        />
      </template>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { markRaw, onMounted, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import useUserStore from '@/store/modules/user';
import { ElMessage } from 'element-plus';

// 导入组件
import WelcomeHeader from '@/components/Index/WelcomeHeader.vue';
import NotificationPanel from '@/components/Index/NotificationPanel.vue';
import ShortcutPanel from '@/components/Index/ShortcutPanel.vue';
import DataOverview from '@/components/Index/DataOverview.vue';
import CalendarComponent from '@/components/Index/CalendarComponent.vue';

// 获取路由和用户状态
const router = useRouter();
const userStore = useUserStore();

// 组件引用
const notificationPanel = ref(null);
const shortcutPanel = ref(null);

// 快捷入口配置
const shortcutItems = ref([
  { id: 'P1001', label: '新建保单', icon: 'el-icon-plus', iconColor: 'blue', route: 'new-policy' }, { id: 'P1002', label: '客户管理', icon: 'el-icon-user', iconColor: 'green', route: 'customer' },
  { id: 'P1003', label: '日程安排', icon: 'el-icon-date', iconColor: 'orange', route: 'schedule' }, { id: 'P1004', label: '销售报表', icon: 'el-icon-document', iconColor: 'gray', route: 'report' }
]);

// 导航方法
const navigateTo = (route: string) => {
  router.push({ name: route });
};

// 更新快捷入口
const updateShortcuts = (newShortcuts: any[]) => {
  shortcutItems.value = newShortcuts;
  // 可以在这里添加保存到localStorage或发送到服务器的逻辑
  localStorage.setItem('shortcutItems', JSON.stringify(newShortcuts));
};

// 组件注册表
const componentRegistry = {
  'notification': markRaw(NotificationPanel),
  'shortcuts': markRaw(ShortcutPanel),
  'data-overview': markRaw(DataOverview),
  'calendar': markRaw(CalendarComponent)
  // 可以在这里添加更多动态组件
};

// 根据组件ID获取组件
const getComponentByName = (componentId) => {
  return componentRegistry[componentId] || null;
};

// 所有组件列表（包括固定和动态组件）
const allComponents = ref([
  { id: 'welcome', name: '工作台', description: '显示欢迎信息和用户登录信息', fixed: true, position: 'top' },
  { id: 'notification', name: '通知', description: '显示业务通知、系统提醒和团队活动', fixed: true, position: 'left', width: 'two-thirds' },
  { id: 'shortcuts', name: '快捷入口', description: '快速访问常用功能', fixed: true, position: 'right', width: 'third' },
  { id: 'data-overview', name: '个人数据概览', description: '显示工作量和销售数据统计', width: 'full' },
  { id: 'calendar', name: '日历', description: '显示日程安排', width: 'two-thirds' }
]);

// 只获取动态组件（非固定组件）
const dynamicComponents = computed(() => {
  return allComponents.value.filter((comp) => !comp.fixed);
});

// 更新仪表盘组件配置
const updateDashboardComponents = (updatedComponents) => {
  // 保留固定组件
  const fixedComps = allComponents.value.filter((comp) => comp.fixed);

  // 合并更新的动态组件，确保每个组件都有width属性
  const newDynamicComps = updatedComponents
    .filter((comp) => !comp.fixed)
    .map((comp) => ({
      ...comp,
      width: comp.width || 'third' // 默认为1/3宽度
    }));

  // 更新组件列表
  allComponents.value = [...fixedComps, ...newDynamicComps];

  // 保存到本地存储
  localStorage.setItem('dashboardComponents', JSON.stringify(allComponents.value));

  // 显示消息通知
  ElMessage({
    message: '组件设置已保存',
    type: 'success',
    duration: 2000
  });
};

// 加载用户保存的组件配置
onMounted(() => {
  // 加载保存的快捷入口
  const savedShortcuts = localStorage.getItem('shortcutItems');
  if (savedShortcuts) {
    try {
      shortcutItems.value = JSON.parse(savedShortcuts);
    } catch (e) {
      console.error('Failed to parse saved shortcuts', e);
    }
  }

  // 加载用户保存的组件配置
  const savedComponents = localStorage.getItem('dashboardComponents');
  if (savedComponents) {
    try {
      const parsedComponents = JSON.parse(savedComponents);
      // 确保固定组件保持不变
      const fixedComps = allComponents.value
        .filter((comp) => comp.fixed)
        .map((comp) => {
          // 保留固定组件的原始宽度设置
          const original = allComponents.value.find((o) => o.id === comp.id);
          return { ...comp, width: original.width };
        });
      const savedDynamicComps = parsedComponents.filter((comp) => !comp.fixed);
      allComponents.value = [...fixedComps, ...savedDynamicComps];
    } catch (e) {
      console.error('Failed to parse saved dashboard components', e);
    }
  }

  // 加载仪表板数据
  loadDashboardData();
});

// 加载仪表板数据
const loadDashboardData = () => {
  // 这里可以添加从API获取数据的逻辑
  console.log('加载仪表板数据');
};
</script>

<style scoped>
@import '@/assets/styles/dashboard-theme.css';

.dashboard-container {
  background: linear-gradient(135deg, #f8faff 0%, #e6eeff 100%);
  min-height: calc(100vh - 64px);
  padding: 20px;
  font-size: 14px;
  color: #333;
}

.dashboard-layout {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

/* 左侧固定区域 - 通知组件 */
.left-fixed-area {
  width: 66.666%;
  min-width: 450px;
}

/* 右侧固定区域 - 快捷入口组件 */
.right-fixed-area {
  width: 33.333%;
  min-width: 250px;
}

/* 动态组件区域 */
.dynamic-components-area {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

/* 组件宽度类 */
.dashboard-component {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 10px;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  overflow: hidden;
  border: 1px solid rgba(230, 230, 230, 0.5);
}

.dashboard-component:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

/* 固定组件样式 */
.notification-panel {
  height: 100%;
  min-height: 500px;
}

.shortcut-panel {
  height: 100%;
  min-height: 400px;
}

/* 组件宽度类 */
.width-third {
  width: calc(33.333% - 7px);
  min-width: 280px;
}

.width-half {
  width: calc(50% - 5px);
  min-width: 400px;
}

.width-two-thirds {
  width: calc(66.666% - 3px);
  min-width: 500px;
}

.width-full {
  width: 100%;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .dashboard-layout {
    flex-direction: column;
  }

  .left-fixed-area,
  .right-fixed-area {
    width: 100%;
  }

  .width-third,
  .width-half,
  .width-two-thirds {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .dashboard-layout {
    gap: 10px;
  }
}
</style>
