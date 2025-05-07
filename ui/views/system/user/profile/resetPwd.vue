<template>
  <el-form ref="pwdRef" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="旧密码" prop="oldPassword">
      <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password />
    </el-form-item>

    <el-form-item label="新密码" prop="newPassword">
      <el-input
        v-model="user.newPassword"
        placeholder="请输入新密码"
        type="password"
        show-password
        @input="checkPasswordStrength"
      />

      <div class="password-strength-box">
        <div class="password-strength-header">
          <span>密码强度：</span>
          <div class="strength-meter">
            <div
              class="strength-meter-fill"
              :style="{ width: strengthScore + '%', backgroundColor: strengthColor }"
            ></div>
          </div>
          <span :style="{ color: strengthColor }">{{ strengthLevel }}</span>
        </div>

        <div class="password-rules">
          <div class="rule-item" :class="{ 'rule-met': lengthValid }">
            <i class="el-icon" :class="lengthValid ? 'el-icon-check' : 'el-icon-close'"></i>
            <span>长度至少8位</span>
          </div>

          <div class="rule-item" :class="{ 'rule-met': uppercaseValid }">
            <i class="el-icon" :class="uppercaseValid ? 'el-icon-check' : 'el-icon-close'"></i>
            <span>包含大写字母</span>
          </div>

          <div class="rule-item" :class="{ 'rule-met': lowercaseValid }">
            <i class="el-icon" :class="lowercaseValid ? 'el-icon-check' : 'el-icon-close'"></i>
            <span>包含小写字母</span>
          </div>

          <div class="rule-item" :class="{ 'rule-met': numberValid }">
            <i class="el-icon" :class="numberValid ? 'el-icon-check' : 'el-icon-close'"></i>
            <span>包含数字</span>
          </div>

          <div class="rule-item" :class="{ 'rule-met': specialCharValid }">
            <i class="el-icon" :class="specialCharValid ? 'el-icon-check' : 'el-icon-close'"></i>
            <span>包含特殊字符</span>
          </div>

          <div class="rule-item" :class="{ 'rule-met': minTypesMet }">
            <i class="el-icon" :class="minTypesMet ? 'el-icon-check' : 'el-icon-close'"></i>
            <span>至少满足上述三种字符要求</span>
          </div>

          <div class="rule-item" :class="{ 'rule-met': !containsUsername }">
            <i class="el-icon" :class="!containsUsername ? 'el-icon-check' : 'el-icon-close'"></i>
            <span>不能包含用户名</span>
          </div>
        </div>
      </div>
    </el-form-item>

    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="submit" :disabled="!allRulesMet">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { updateUserPwd } from '@/api/system/user';
import type { ResetPwdForm } from '@/api/system/user/types';
import { ref, computed, watch } from 'vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const pwdRef = ref<ElFormInstance>();
const user = ref<ResetPwdForm>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 用户名，实际应用中应该从用户上下文或存储中获取
const username = 'admin'; // 示例值，实际应用应获取真实用户名

// 密码规则校验状态
const lengthValid = ref(false);
const uppercaseValid = ref(false);
const lowercaseValid = ref(false);
const numberValid = ref(false);
const specialCharValid = ref(false);
const containsUsername = ref(false);

// 计算至少满足三种字符类型
const minTypesMet = computed(() => {
  let count = 0;
  if (uppercaseValid.value) count++;
  if (lowercaseValid.value) count++;
  if (numberValid.value) count++;
  if (specialCharValid.value) count++;
  return count >= 3;
});

// 所有规则是否满足
const allRulesMet = computed(() => {
  return lengthValid.value &&
    minTypesMet.value &&
    !containsUsername.value;
});

// 密码强度评分 (0-100)
const strengthScore = computed(() => {
  if (!user.value.newPassword) return 0;

  let score = 0;
  if (lengthValid.value) score += 20;
  if (uppercaseValid.value) score += 15;
  if (lowercaseValid.value) score += 15;
  if (numberValid.value) score += 15;
  if (specialCharValid.value) score += 15;
  if (!containsUsername.value) score += 20;

  return Math.min(score, 100);
});

// 密码强度等级
const strengthLevel = computed(() => {
  if (strengthScore.value < 40) return '弱';
  if (strengthScore.value < 70) return '中';
  return '强';
});

// 密码强度颜色
const strengthColor = computed(() => {
  if (strengthScore.value < 40) return '#F56C6C'; // 红色
  if (strengthScore.value < 70) return '#E6A23C'; // 黄色
  return '#67C23A'; // 绿色
});

// 检查密码强度
const checkPasswordStrength = () => {
  const pwd = user.value.newPassword;

  // 长度检查
  lengthValid.value = pwd.length >= 8;

  // 字符类型检查
  uppercaseValid.value = /[A-Z]/.test(pwd);
  lowercaseValid.value = /[a-z]/.test(pwd);
  numberValid.value = /[0-9]/.test(pwd);
  specialCharValid.value = /[^A-Za-z0-9]/.test(pwd);

  // 用户名检查
  containsUsername.value = username ? pwd.toLowerCase().includes(username.toLowerCase()) : false;
};

// 监听密码变化
watch(() => user.value.newPassword, () => {
  checkPasswordStrength();
});

const equalToPassword = (rule: any, value: string, callback: any) => {
  if (user.value.newPassword !== value) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const rules = ref({
  oldPassword: [{ required: true, message: '旧密码不能为空', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '新密码不能为空', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        checkPasswordStrength();
        if (allRulesMet.value) {
          callback();
        } else {
          callback(new Error('密码不符合安全要求'));
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '确认密码不能为空', trigger: 'blur' },
    {
      validator: equalToPassword,
      trigger: 'blur'
    }
  ]
});

/** 提交按钮 */
const submit = () => {
  pwdRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        await updateUserPwd(user.value.oldPassword, user.value.newPassword);
        proxy?.$modal.msgSuccess('密码修改成功');
      } catch (error: any) {
        proxy?.$modal.msgError(error.message || '密码修改失败');
      }
    }
  });
};

/** 关闭按钮 */
const close = () => {
  proxy?.$tab.closePage();
};
</script>

<style scoped>
.password-strength-box {
  margin-top: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
  padding: 12px 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.password-strength-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.strength-meter {
  flex: 1;
  height: 6px;
  background-color: #e9ecef;
  border-radius: 3px;
  margin: 0 10px;
  overflow: hidden;
}

.strength-meter-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease, background-color 0.3s ease;
}

.password-rules {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.rule-item {
  display: flex;
  align-items: center;
  color: #909399;
  font-size: 13px;
  transition: color 0.3s ease;
}

.rule-item .el-icon {
  margin-right: 8px;
  font-size: 16px;
  flex-shrink: 0;
}

.rule-item .el-icon-close {
  color: #F56C6C;
}

.rule-met {
  color: #67C23A;
}

.rule-met .el-icon-check {
  color: #67C23A;
}

@media (max-width: 768px) {
  .password-rules {
    grid-template-columns: 1fr;
  }
}
</style>
