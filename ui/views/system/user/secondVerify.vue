<template>
  <div>
    <!-- 二次验证对话框 -->
    <el-dialog v-model="visibleProxy" :title="title" width="500px" append-to-body :close-on-click-modal="false" @closed="resetForm">
      <el-form ref="verifyFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="验证方式" prop="verifyType">
          <el-radio-group v-model="form.verifyType">
            <el-radio v-if="userInfo.phonenumber" :label="1">短信验证</el-radio>
            <el-radio v-if="userInfo.email" :label="2">邮件验证</el-radio>
            <el-radio :label="3">密码验证</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 短信验证 -->
        <template v-if="form.verifyType === 1">
          <el-form-item label="手机号码">
            <el-input v-model="maskedPhone" disabled />
          </el-form-item>
          <el-form-item label="验证码" prop="smsCode">
            <div class="verify-code-container">
              <el-input v-model="form.smsCode" placeholder="请输入验证码" maxlength="6" />
              <el-button :disabled="smsTimer > 0" @click="sendSmsCode">
                {{ smsTimer > 0 ? `重新获取(${smsTimer}s)` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
        </template>

        <!-- 邮件验证 -->
        <template v-if="form.verifyType === 2">
          <el-form-item label="邮箱">
            <el-input v-model="maskedEmail" disabled />
          </el-form-item>
          <el-form-item label="验证码" prop="emailCode">
            <div class="verify-code-container">
              <el-input v-model="form.emailCode" placeholder="请输入验证码" maxlength="6" />
              <el-button :disabled="emailTimer > 0" @click="sendEmailCode">
                {{ emailTimer > 0 ? `重新获取(${emailTimer}s)` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
        </template>

        <!-- 密码验证 -->
        <template v-if="form.verifyType === 3">
          <el-form-item label="用户名">
            <el-input v-model="userInfo.userName" disabled />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, watch } from 'vue';
import { UserVO } from '@/api/system/user/types';
import { ElMessage } from 'element-plus';
import { sendAuthCode, verifyAuthCode, verifyPassword, VerifyResult } from './index';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userInfo: {
    type: Object as PropType<Partial<UserVO>>,
    default: () => ({})
  },
  title: {
    type: String,
    default: '二次验证'
  },
  operationType: {
    type: String,
    default: 'default' // 可以是 'edit', 'delete', 'sensitive' 等
  }
});

const emit = defineEmits(['update:visible', 'verify-success', 'verify-cancel']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

// 表单实例
const verifyFormRef = ref<ElFormInstance>();

// 验证表单数据
const form = reactive({
  verifyType: props.userInfo.phonenumber ? 1 : props.userInfo.email ? 2 : 3, // 默认选择可用的验证方式
  smsCode: '',
  emailCode: '',
  password: ''
});

// 验证倒计时
const smsTimer = ref(0);
const emailTimer = ref(0);
const buttonLoading = ref(false);

// 手机号码掩码处理
const maskedPhone = computed(() => {
  const phone = props.userInfo.phonenumber || '';
  if (phone.length >= 11) {
    return phone.substring(0, 3) + '****' + phone.substring(7);
  }
  return phone;
});

// 邮箱掩码处理
const maskedEmail = computed(() => {
  const email = props.userInfo.email || '';
  if (email.includes('@')) {
    const [username, domain] = email.split('@');
    if (username.length > 2) {
      return username.substring(0, 2) + '****@' + domain;
    }
    return username + '****@' + domain;
  }
  return email;
});

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 表单验证规则
const rules = reactive({
  verifyType: [{ required: true, message: '请选择验证方式', trigger: 'change' }],
  smsCode: [
    { required: true, message: '请输入短信验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码必须是6位数字', trigger: 'blur' }
  ],
  emailCode: [
    { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码必须是6位数字', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
});

// 发送短信验证码
const sendSmsCode = async () => {
  if (!props.userInfo.userId) {
    ElMessage.error('用户信息不完整，无法发送验证码');
    return;
  }

  try {
    const res = await sendAuthCode(props.userInfo.userId, 1); // 1 表示短信验证码

    if (res.code === 200) {
      ElMessage.success('验证码发送成功，请注意查收');
      smsTimer.value = 60;
      const interval = setInterval(() => {
        smsTimer.value--;
        if (smsTimer.value <= 0) {
          clearInterval(interval);
        }
      }, 1000);
    } else {
      ElMessage.error(res.msg || '验证码发送失败');
    }
  } catch (error) {
    console.error('发送短信验证码失败:', error);
    ElMessage.error('验证码发送失败，请稍后重试');
  }
};

// 发送邮件验证码
const sendEmailCode = async () => {
  if (!props.userInfo.userId) {
    ElMessage.error('用户信息不完整，无法发送验证码');
    return;
  }

  try {
    const res = await sendAuthCode(props.userInfo.userId, 2); // 2 表示邮件验证码

    if (res.code === 200) {
      ElMessage.success('验证码发送成功，请注意查收');
      emailTimer.value = 60;
      const interval = setInterval(() => {
        emailTimer.value--;
        if (emailTimer.value <= 0) {
          clearInterval(interval);
        }
      }, 1000);
    } else {
      ElMessage.error(res.msg || '验证码发送失败');
    }
  } catch (error) {
    console.error('发送邮件验证码失败:', error);
    ElMessage.error('验证码发送失败，请稍后重试');
  }
};

// 提交验证
const submitForm = () => {
  verifyFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;

      try {
        // 根据验证方式执行不同的验证逻辑
        let verifyResult = false;
        let verifyResponse;

        if (form.verifyType === 1) { // 短信验证
          verifyResponse = await verifyAuthCode(props.userInfo.userId as string | number, 1, form.smsCode);
          verifyResult = verifyResponse.code === 200;
        } else if (form.verifyType === 2) { // 邮件验证
          verifyResponse = await verifyAuthCode(props.userInfo.userId as string | number, 2, form.emailCode);
          verifyResult = verifyResponse.code === 200;
        } else if (form.verifyType === 3) { // 密码验证
          verifyResponse = await verifyPassword(props.userInfo.userName as string, form.password);
          verifyResult = verifyResponse.code === 200;
        }

        if (verifyResult) {
          ElMessage.success('验证成功');
          emit('verify-success', {
            operationType: props.operationType,
            verifyType: form.verifyType,
            success: true
          } as VerifyResult);
          visibleProxy.value = false;
        } else {
          ElMessage.error(verifyResponse?.msg || '验证失败，请检查输入并重试');
        }
      } catch (error) {
        console.error('验证失败:', error);
        ElMessage.error('验证过程发生错误，请稍后重试');
      } finally {
        buttonLoading.value = false;
      }
    }
  });
};

// 取消按钮
const cancel = () => {
  emit('verify-cancel');
  visibleProxy.value = false;
};

// 重置表单
const resetForm = () => {
  form.smsCode = '';
  form.emailCode = '';
  form.password = '';
  verifyFormRef.value?.resetFields();
};

// 监听用户信息变化，更新默认验证方式
watch(
  () => props.userInfo,
  (newVal) => {
    if (newVal) {
      form.verifyType = newVal.phonenumber ? 1 : newVal.email ? 2 : 3;
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.verify-code-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.verify-code-container .el-input {
  flex-grow: 1;
}

.verify-code-container .el-button {
  flex-shrink: 0;
  width: 110px;
}
</style>
