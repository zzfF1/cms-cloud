<template>
  <div class="app-container">
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span>通知设置</span>
        </div>
      </template>

      <el-form :model="settings" label-width="120px" :rules="rules" ref="settingsForm">
        <h3 class="setting-section-title">通知接收设置</h3>

        <el-form-item label="待办通知">
          <el-checkbox v-model="settings.todoNotifySystem" label="系统内通知" />
          <el-checkbox v-model="settings.todoNotifySms" label="短信通知" />
          <el-checkbox v-model="settings.todoNotifyEmail" label="邮件通知" />
        </el-form-item>

        <el-form-item label="预警通知">
          <el-checkbox v-model="settings.alertNotifySystem" label="系统内通知" />
          <el-checkbox v-model="settings.alertNotifySms" label="短信通知" />
          <el-checkbox v-model="settings.alertNotifyEmail" label="邮件通知" />
        </el-form-item>

        <el-form-item label="公告通知">
          <el-checkbox v-model="settings.announceNotifySystem" label="系统内通知" />
          <el-checkbox v-model="settings.announceNotifyEmail" label="邮件通知" />
        </el-form-item>

        <el-divider />

        <h3 class="setting-section-title">免打扰时间</h3>
        <p class="setting-desc">在免打扰时间内，只会收到系统内通知，不会收到短信和邮件通知</p>

        <el-form-item label="是否启用免打扰">
          <el-switch v-model="settings.enableDoNotDisturb" />
        </el-form-item>

        <el-form-item label="免打扰时间段" v-if="settings.enableDoNotDisturb">
          <el-time-picker v-model="settings.doNotDisturbStart" format="HH:mm" placeholder="开始时间" style="width: 140px; margin-right: 10px" />
          <span>至</span>
          <el-time-picker v-model="settings.doNotDisturbEnd" format="HH:mm" placeholder="结束时间" style="width: 140px; margin-left: 10px" />
        </el-form-item>

        <el-divider />

        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
          <el-button @click="resetSettings">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getNotificationSettings, updateNotificationSettings } from '@/api/system/notification';
import { ElMessage } from 'element-plus';
import type { FormInstance } from 'element-plus';
import dayjs from 'dayjs';

const settingsForm = ref<FormInstance>();
const loading = ref(false);

// 表单数据
const settings = ref({
  todoNotifySystem: true,
  todoNotifySms: false,
  todoNotifyEmail: false,
  alertNotifySystem: true,
  alertNotifySms: false,
  alertNotifyEmail: false,
  announceNotifySystem: true,
  announceNotifyEmail: false,
  enableDoNotDisturb: false,
  doNotDisturbStart: null,
  doNotDisturbEnd: null
});

// 表单校验规则
const rules = {
  doNotDisturbStart: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  doNotDisturbEnd: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
};

// 加载通知设置
const loadSettings = async () => {
  loading.value = true;
  try {
    const { data } = await getNotificationSettings();
    if (data && data.code === 200) {
      const userSettings = data.data;

      // 转换字符串值到布尔值
      settings.value = {
        todoNotifySystem: userSettings.todoNotifySystem === '1',
        todoNotifySms: userSettings.todoNotifySms === '1',
        todoNotifyEmail: userSettings.todoNotifyEmail === '1',
        alertNotifySystem: userSettings.alertNotifySystem === '1',
        alertNotifySms: userSettings.alertNotifySms === '1',
        alertNotifyEmail: userSettings.alertNotifyEmail === '1',
        announceNotifySystem: userSettings.announceNotifySystem === '1',
        announceNotifyEmail: userSettings.announceNotifyEmail === '1',

        // 处理免打扰时间
        enableDoNotDisturb: userSettings.doNotDisturbStart !== null && userSettings.doNotDisturbEnd !== null,

        doNotDisturbStart: userSettings.doNotDisturbStart ? dayjs(`2025-01-01 ${userSettings.doNotDisturbStart}`).toDate() : null,

        doNotDisturbEnd: userSettings.doNotDisturbEnd ? dayjs(`2025-01-01 ${userSettings.doNotDisturbEnd}`).toDate() : null
      };
    } else {
      ElMessage.error(data?.msg || '获取通知设置失败');
    }
  } catch (error) {
    console.error('获取通知设置出错:', error);
    ElMessage.error('获取通知设置时发生错误');
  } finally {
    loading.value = false;
  }
};

// 保存设置
const saveSettings = async () => {
  await settingsForm.value?.validate(async (valid) => {
    if (!valid) {
      return false;
    }

    if (settings.value.enableDoNotDisturb && (!settings.value.doNotDisturbStart || !settings.value.doNotDisturbEnd)) {
      ElMessage.warning('启用免打扰时间后，必须设置时间段');
      return false;
    }

    loading.value = true;
    try {
      // 转换为API需要的格式
      const apiSettings = {
        todoNotifySystem: settings.value.todoNotifySystem ? '1' : '0',
        todoNotifySms: settings.value.todoNotifySms ? '1' : '0',
        todoNotifyEmail: settings.value.todoNotifyEmail ? '1' : '0',
        alertNotifySystem: settings.value.alertNotifySystem ? '1' : '0',
        alertNotifySms: settings.value.alertNotifySms ? '1' : '0',
        alertNotifyEmail: settings.value.alertNotifyEmail ? '1' : '0',
        announceNotifySystem: settings.value.announceNotifySystem ? '1' : '0',
        announceNotifyEmail: settings.value.announceNotifyEmail ? '1' : '0',

        // 处理免打扰时间
        doNotDisturbStart:
          settings.value.enableDoNotDisturb && settings.value.doNotDisturbStart ? dayjs(settings.value.doNotDisturbStart).format('HH:mm') : null,

        doNotDisturbEnd:
          settings.value.enableDoNotDisturb && settings.value.doNotDisturbEnd ? dayjs(settings.value.doNotDisturbEnd).format('HH:mm') : null
      };

      const { data } = await updateNotificationSettings(apiSettings);
      if (data && data.code === 200) {
        ElMessage.success('通知设置保存成功');
      } else {
        ElMessage.error(data?.msg || '保存设置失败');
      }
    } catch (error) {
      console.error('保存通知设置出错:', error);
      ElMessage.error('保存通知设置时发生错误');
    } finally {
      loading.value = false;
    }
  });
};

// 重置设置
const resetSettings = () => {
  loadSettings();
};

// 初始化
onMounted(() => {
  loadSettings();
});
</script>

<style lang="scss" scoped>
.settings-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.setting-section-title {
  font-size: 16px;
  font-weight: 500;
  margin: 16px 0;
  color: #303133;
}

.setting-desc {
  font-size: 14px;
  color: #909399;
  margin-bottom: 16px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-checkbox) {
  margin-right: 20px;
}

:deep(.el-divider) {
  margin: 24px 0;
}

@media (max-width: 768px) {
  :deep(.el-form-item__label) {
    width: 100% !important;
    text-align: left;
    margin-bottom: 8px;
  }

  :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }
}
</style>
