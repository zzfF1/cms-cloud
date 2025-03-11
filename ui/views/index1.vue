<template>
  <div class="content">
    <el-card class="mb-2" shadow="hover">
      <div class="py-4 font-bold">工作台</div>
      <el-row>
        <el-col :md="16" :xs="24">
          <div class="flex items-center">
            <div class="pr-4 flex items-center"></div>
            <div>
              <div class="text-xl">你好，{{ userStore.nickname }} ，祝你开心每一天！</div>
              <div class="text-sm text-gray-400 pt-2">当前日期 : {{ curDate }} &nbsp;&nbsp;&nbsp;&nbsp;登录IP : {{ userStore.loginIp }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="15">
      <!--      <el-col :lg="12" :md="24">-->
      <!--        <el-card class="mb-2" shadow="hover" style="height: 350px;">-->
      <!--          <template #header>-->
      <!--            <div class="card-header flex justify-between items-center">-->
      <!--              <span>公告</span>-->
      <!--            </div>-->
      <!--          </template>-->
      <!--        </el-card>-->
      <!--      </el-col>-->
      <el-col :lg="18" :md="24">
        <el-card class="mb-2" shadow="hover" style="height: 350px">
          <template #header>
            <div class="card-header flex justify-between items-center">
              <span>待办</span>
            </div>
          </template>
          <!--  <p v-hasPermi="['system:daiban:sale']"  class="text item">业务员28600036-春暖花开于202403考核清退，请及时办理离职手续</p>
                <p v-hasPermi="['system:daiban:sale']"  class="text item">业务员28600033-国泰民安的执业证即将到期，请及时续签</p>
                <p v-hasPermi="['system:daiban:branch']"  class="text item">中介机构1000001-长安经纪北京分公司的营业执照即将到期，请及时续签</p>
               -->
          <p v-hasPermi="['system:daiban:sale']" v-for="item in options" :key="item.value" class="text item">{{ item.label }}</p>
          <p v-hasPermi="['system:daiban:branch']" v-for="item in options1" :key="item.value" class="text item">{{ item.label }}</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import useUserStore from '@/store/modules/user';
import dayjs from 'dayjs';
import { initWebSocket } from '@/utils/websocket';

const userStore = useUserStore();
//当前日期
let curDate = dayjs(new Date()).format('YYYY-MM-DD');
onMounted(() => {
  let protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
  initWebSocket(protocol + window.location.host + import.meta.env.VITE_APP_BASE_API + '/resource/websocket');
});

const options = [
  {
    value: '1',
    label: '业务员28600036-春暖花开于202403考核清退，请及时办理离职手续'
  },
  { value: '2', label: '业务员28600033-国泰民安的执业证即将到期，请及时续签' }
];

const options1 = [
  {
    value: '1',
    label: '中介机构1000001-长安经纪北京分公司的营业执照即将到期，请及时续签'
  }
];
</script>

<style lang="scss" scoped>
::v-deep(.el-card__header) {
  margin-bottom: -1px;
}

::v-deep(.el-card__body) {
  padding: 0;
}

.c-list-card-body {
  transition: all 0.3s;
  position: relative;
  padding: 15px;
  box-shadow:
    1px 0 0 0 #f0f0f0,
    0 1px 0 0 #f0f0f0,
    1px 1px 0 0 #f0f0f0,
    inset 1px 0 0 0 #f0f0f0,
    inset 0 1px 0 0 #f0f0f0;
}

.c-list-card-body:hover {
  z-index: 1;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
}
</style>
