<template>
  <section class="app-main">
    <el-watermark :content="waterMsg">
      <router-view v-slot="{ Component, route }">
        <transition v-if="!route.meta.noCache" :enter-active-class="animante" mode="out-in">
          <keep-alive v-if="!route.meta.noCache" :include="tagsViewStore.cachedViews">
            <component :is="Component" v-if="!route.meta.link" :key="route.path" />
          </keep-alive>
        </transition>
        <transition v-if="route.meta.noCache" :enter-active-class="animante" mode="out-in">
          <component :is="Component" v-if="!route.meta.link && route.meta.noCache" :key="route.path" />
        </transition>
      </router-view>
      <iframe-toggle />
    </el-watermark>
  </section>
</template>

<script setup name="AppMain" lang="ts">
import useSettingsStore from '@/store/modules/settings';
import useUserStore from '@/store/modules/user';
import useTagsViewStore from '@/store/modules/tagsView';
import IframeToggle from './IframeToggle/index.vue';
import { ComponentInternalInstance } from 'vue';
import dayjs from 'dayjs';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const route = useRoute();
const tagsViewStore = useTagsViewStore();
const userStore = useUserStore();
//当前日期
let curDate = dayjs(new Date()).format('YYYY-MM-DD');
//水印信息
const waterMsg = ref('BR_' + curDate + '_' + userStore.nickname);

// 随机动画集合
const animante = ref<string>('');
const animationEnable = ref(useSettingsStore().animationEnable);
watch(
  () => useSettingsStore().animationEnable,
  (val: boolean) => {
    animationEnable.value = val;
    if (val) {
      animante.value = proxy?.animate.animateList[Math.round(Math.random() * proxy?.animate.animateList.length)] as string;
    } else {
      animante.value = proxy?.animate.defaultAnimate as string;
    }
  },
  { immediate: true }
);

onMounted(() => {
  addIframe();
});

watchEffect((route) => {
  addIframe();
});

function addIframe() {
  if (route.meta.link) {
    useTagsViewStore().addIframeView(route);
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  /* 50= navbar  50  */
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    /* 84 = navbar + tags-view = 50 + 34 */
    min-height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}
</style>
<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 6px;
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background-color: #c0c0c0;
  border-radius: 3px;
}
</style>
