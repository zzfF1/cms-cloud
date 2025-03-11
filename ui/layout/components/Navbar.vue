<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="appStore.sidebar.opened" class="hamburger-container" @toggle-click="toggleSideBar" />
    <breadcrumb v-if="!settingsStore.topNav" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="settingsStore.topNav" id="topmenu-container" class="topmenu-container" />

    <div class="right-menu flex align-center">
      <template v-if="appStore.device !== 'mobile'">
        <!-- <header-search id="header-search" class="right-menu-item" /> -->
        <search-menu ref="searchMenuRef" />
        <el-tooltip content="搜索" effect="dark" placement="bottom">
          <div class="right-menu-item hover-effect icon-container" @click="openSearchMenu">
            <svg-icon class-name="menu-icon search-icon" icon-class="search" />
          </div>
        </el-tooltip>
        <!-- 消息 -->
        <el-tooltip :content="$t('navbar.message')" effect="dark" placement="bottom">
          <div class="icon-container">
            <el-popover placement="bottom" trigger="click" transition="el-zoom-in-top" :width="350" :persistent="false">
              <template #reference>
                <el-badge :value="unreadTotal > 0 ? unreadTotal : ''" :max="99" class="badge-item">
                  <svg-icon icon-class="message" class="menu-icon" />
                </el-badge>
              </template>
              <template #default>
                <notice></notice>
              </template>
            </el-popover>
          </div>
        </el-tooltip>

        <el-tooltip :content="$t('navbar.full')" effect="dark" placement="bottom">
          <screenfull id="screenfull" class="right-menu-item hover-effect icon-container" />
        </el-tooltip>

        <el-tooltip :content="$t('navbar.language')" effect="dark" placement="bottom">
          <lang-select id="lang-select" class="right-menu-item hover-effect icon-container" />
        </el-tooltip>

        <el-tooltip :content="$t('navbar.layoutSize')" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect icon-container" />
        </el-tooltip>
      </template>
      <div class="avatar-container">
        <el-dropdown class="right-menu-item hover-effect" trigger="click" @command="handleCommand">
          <div class="avatar-wrapper">
            <img :src="userStore.avatar" class="user-avatar" />
            <el-icon class="dropdown-icon"><caret-bottom /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <router-link v-if="!dynamic" to="/user/profile">
                <el-dropdown-item>{{ $t('navbar.personalCenter') }}</el-dropdown-item>
              </router-link>
              <el-dropdown-item v-if="settingsStore.showSettings" command="setLayout">
                <span>{{ $t('navbar.layoutSetting') }}</span>
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <span>{{ $t('navbar.logout') }}</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import SearchMenu from './TopBar/search.vue';
import useAppStore from '@/store/modules/app';
import useUserStore from '@/store/modules/user';
import useSettingsStore from '@/store/modules/settings';
import useNoticeStore from '@/store/modules/notice';
import { getTenantList } from '@/api/login';
import { dynamicClear, dynamicTenant } from '@/api/system/tenant';
import { TenantVO } from '@/api/types';
import notice from './notice/index.vue';
import router from '@/router';

const appStore = useAppStore();
const userStore = useUserStore();
const settingsStore = useSettingsStore();
const noticeStore = useNoticeStore();

// 使用 ref 存储未读消息数量
const unreadTotal = ref(0);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const userId = ref(userStore.userId);
const companyName = ref(undefined);
const tenantList = ref<TenantVO[]>([]);
// 是否切换了租户
const dynamic = ref(false);
// 租户开关
const tenantEnabled = ref(true);
// 搜索菜单
const searchMenuRef = ref<InstanceType<typeof SearchMenu>>();

const openSearchMenu = () => {
  searchMenuRef.value?.openSearch();
};

// 动态切换
const dynamicTenantEvent = async (tenantId: string) => {
  if (companyName.value != null && companyName.value !== '') {
    await dynamicTenant(tenantId);
    dynamic.value = true;
    proxy?.$tab.closeAllPage();
    proxy?.$router.push('/');
    proxy?.$tab.refreshPage();
  }
};

const dynamicClearEvent = async () => {
  await dynamicClear();
  dynamic.value = false;
  proxy?.$tab.closeAllPage();
  proxy?.$router.push('/');
  proxy?.$tab.refreshPage();
};

/** 租户列表 */
const initTenantList = async () => {
  const { data } = await getTenantList(true);
  tenantEnabled.value = data.tenantEnabled === undefined ? true : data.tenantEnabled;
  if (tenantEnabled.value) {
    tenantList.value = data.voList;
  }
};

// 更新未读消息计数
const updateUnreadCount = () => {
  // 尝试从各种可能的位置获取未读计数
  try {
    // 首先尝试使用新API
    if (typeof noticeStore.getUnreadNoticeCount === 'function') {
      noticeStore.getUnreadNoticeCount();
    }

    // 然后从各种可能的位置获取数据
    if (noticeStore.state?.unreadCount?.total !== undefined) {
      // 如果新的结构可用
      unreadTotal.value = noticeStore.state.unreadCount.total;
    } else if (noticeStore.state?.notices) {
      // 如果在state下
      unreadTotal.value = noticeStore.state.notices.filter(notice => !notice.read).length;
    } else if (Array.isArray(noticeStore.notices)) {
      // 如果直接在根对象上
      unreadTotal.value = noticeStore.notices.filter(notice => !notice.read).length;
    } else {
      // 尝试旧的store结构
      const oldNotices = noticeStore?.state?.notices || [];
      if (Array.isArray(oldNotices)) {
        unreadTotal.value = oldNotices.filter(notice => !notice.read).length;
      }
    }
  } catch (e) {
    console.error('更新未读消息计数出错:', e);
    unreadTotal.value = 0; // 出错时使用默认值
  }
};

// 初始化通知
const initNotifications = async () => {
  try {
    // 尝试调用新的API
    if (typeof noticeStore.getUnreadNoticeCount === 'function') {
      await noticeStore.getUnreadNoticeCount();
    }

    // 更新未读计数
    updateUnreadCount();
  } catch (e) {
    console.error('初始化通知出错:', e);
  }
};

defineExpose({
  initTenantList
});

const toggleSideBar = () => {
  appStore.toggleSideBar(false);
};

const logout = async () => {
  await ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });
  userStore.logout().then(() => {
    router.replace({
      path: '/login',
      query: {
        redirect: encodeURIComponent(router.currentRoute.value.fullPath || '/')
      }
    });
  });
};

const emits = defineEmits(['setLayout']);
const setLayout = () => {
  emits('setLayout');
};
// 定义Command方法对象 通过key直接调用方法
const commandMap: { [key: string]: any } = {
  setLayout,
  logout
};
const handleCommand = (command: string) => {
  // 判断是否存在该方法
  if (commandMap[command]) {
    commandMap[command]();
  }
};

// 安全地监听通知变化，使用深度监听整个store
watch(
  () => noticeStore,
  () => {
    updateUnreadCount();
  },
  { deep: true }
);

// 页面加载时初始化
onMounted(() => {
  initNotifications();
});
</script>

<style lang="scss" scoped>
:deep(.el-select .el-input__wrapper) {
  height: 30px;
  border-radius: 6px;
}

:deep(.el-badge__content.is-fixed) {
  top: 12px;
  right: 4px;
  height: 18px;
  line-height: 18px;
  padding: 0 6px;
  border-radius: 9px;
  font-size: 12px;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.flex {
  display: flex;
}

.align-center {
  align-items: center;
}

.navbar {
  height: 54px;
  overflow: hidden;
  position: relative;
  background: linear-gradient(135deg, #f8faff 0%, #e6eeff 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border-bottom: 1px solid rgba(230, 230, 230, 0.5);
  transition: all 0.3s ease;

  .hamburger-container {
    line-height: 54px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    padding: 0 16px;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
    margin-left: 10px;
    line-height: 54px;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
    line-height: 54px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .tenant-select {
    margin-right: 8px;
    :deep(.el-input__wrapper) {
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    }
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 54px;
    display: flex;
    margin-right: 8px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 10px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
          color: #409EFF;
        }
      }
    }

    .icon-container {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 12px;

      .menu-icon {
        font-size: 20px;
        transition: all 0.3s;
        &:hover {
          transform: scale(1.1);
          color: #409EFF;
        }
      }
    }

    .badge-item {
      cursor: pointer;
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;
        display: flex;
        align-items: center;
        padding: 0 10px;
        border-radius: 8px;
        transition: all 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }

        .user-avatar {
          cursor: pointer;
          width: 36px;
          height: 36px;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
          transition: all 0.3s;

          &:hover {
            transform: scale(1.05);
          }
        }

        .dropdown-icon {
          cursor: pointer;
          position: absolute;
          right: -10px;
          margin-left: 4px;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}

/* 添加深色模式支持 */
.dark-mode {
  .navbar {
    background: linear-gradient(to right, #1a1a1a, #2c2c2c);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    border-bottom: 1px solid #333;

    .right-menu-item {
      color: #e0e0e0;
    }

    .avatar-wrapper {
      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .navbar {
    .right-menu {
      .right-menu-item {
        padding: 0 6px;
      }

      .icon-container {
        padding: 0 6px;
      }

      .avatar-container {
        margin-right: 15px;
      }
    }
  }
}
</style>
