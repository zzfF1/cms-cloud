<template>
  <!-- 添加或修改客户端管理抽屉 -->
  <el-drawer v-model="visibleProxy" :title="title" size="65%" append-to-body :close-on-click-modal="false" @closed="resetForm">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="basic">
        <el-card shadow="always" class="box-card" style="padding-top: 10px">
          <el-form ref="clientFormRef" :model="form" :rules="rules" label-width="160px">
            <el-form-item label="客户端key" prop="clientKey">
              <el-input v-model="form.clientKey" :disabled="form.id != null" placeholder="请输入客户端key" />
            </el-form-item>
            <el-form-item label="客户端秘钥" prop="clientSecret">
              <el-input v-model="form.clientSecret" :disabled="form.id != null" placeholder="请输入客户端秘钥" />
            </el-form-item>
            <el-form-item label="授权类型" prop="grantTypeList">
              <el-select v-model="form.grantTypeList" multiple placeholder="请输入授权类型">
                <el-option v-for="dict in sys_grant_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="设备类型" prop="deviceType">
              <el-select v-model="form.deviceType" placeholder="请输入设备类型">
                <el-option v-for="dict in sys_device_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item prop="activeTimeout" label-width="auto">
              <template #label>
                <span>
                  <el-tooltip content="指定时间无操作则过期（单位：秒），默认30分钟（1800秒）" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  Token活跃超时时间
                </span>
              </template>
              <el-input v-model="form.activeTimeout" placeholder="请输入Token活跃超时时间" />
            </el-form-item>
            <el-form-item prop="timeout" label-width="auto">
              <template #label>
                <span>
                  <el-tooltip content="指定时间必定过期（单位：秒），默认七天（604800秒）" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  Token固定超时时间
                </span>
              </template>
              <el-input v-model="form.timeout" placeholder="请输入Token固定超时时间" />
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 安全配置选项卡 -->
      <el-tab-pane label="安全配置" name="security">
        <el-card shadow="always" class="box-card" style="padding-top: 10px">
          <el-form ref="securityFormRef" :model="securityConfig" label-width="160px">
            <el-form-item label="加密方式" prop="encryptionMode">
              <el-select v-model="securityConfig.encryptionMode" placeholder="请选择加密方式">
                <el-option label="SM2" value="SM2"></el-option>
                <el-option label="SM4" value="SM4"></el-option>
                <el-option label="AES" value="AES"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="访问密钥(AK)" prop="ak">
              <el-input v-model="securityConfig.ak" placeholder="访问密钥将自动生成">
                <template #append>
                  <el-button @click="generateAk">生成</el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="安全密钥(SK)" prop="sk">
              <el-input v-model="securityConfig.sk" type="password" placeholder="安全密钥将自动生成" show-password>
                <template #append>
                  <el-button @click="generateSk">生成</el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-divider content-position="center">SM2密钥对</el-divider>

            <el-form-item label="SM2公钥" prop="sm2PublicKey">
              <el-input v-model="securityConfig.sm2PublicKey" type="textarea" :rows="3" placeholder="SM2公钥" />
            </el-form-item>

            <el-form-item label="SM2私钥" prop="sm2PrivateKey">
              <el-input v-model="securityConfig.sm2PrivateKey" type="textarea" :rows="3" placeholder="SM2私钥" show-password />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="generateSm2KeyPair" :loading="keyPairLoading">生成SM2密钥对</el-button>
            </el-form-item>

            <el-divider content-position="center">服务配置</el-divider>

            <el-form-item label="服务地址" prop="serverPath">
              <el-input v-model="securityConfig.serverPath" placeholder="请输入服务地址" />
            </el-form-item>

            <el-form-item label="端口" prop="port">
              <el-input v-model="securityConfig.port" placeholder="请输入服务端口" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- API权限选项卡 -->
      <el-tab-pane label="API权限" name="api">
        <el-card shadow="always" class="box-card" style="padding-top: 10px">
          <div style="margin-bottom: 10px">
            <el-button type="primary" @click="addApiPermission">添加API</el-button>
          </div>

          <el-table :data="apiPermissions" style="width: 100%" border>
            <el-table-column label="序号" type="index" width="50" align="center" />
            <el-table-column label="API路径" prop="api">
              <template #default="scope">
                <el-input v-model="scope.row.api" placeholder="请输入API路径，如 /api/v1/users" />
              </template>
            </el-table-column>
            <el-table-column label="请求方法" prop="method" width="150">
              <template #default="scope">
                <el-select v-model="scope.row.method" placeholder="请选择请求方法">
                  <el-option label="GET" value="GET"></el-option>
                  <el-option label="POST" value="POST"></el-option>
                  <el-option label="PUT" value="PUT"></el-option>
                  <el-option label="DELETE" value="DELETE"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="scope">
                <el-button type="danger" link @click="removeApiPermission(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import {
  getClient,
  addClient,
  updateClient,
  generateSm2KeyPair as getSm2KeyPair,
  generateAccessKey,
  generateSecretKey
} from '@/api/system/client';
import { ClientForm, ClientVO, ClientConfig, ApiPermission, KeyPair } from '@/api/system/client/types';
import type { PropType } from 'vue';
import type { ComponentInternalInstance } from 'vue';

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
    type: Object as PropType<ClientVO | null>,
    default: null
  }
});

const emit = defineEmits(['update:visible', 'save']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));
const { sys_grant_type } = toRefs<any>(proxy?.useDict('sys_grant_type'));
const { sys_device_type } = toRefs<any>(proxy?.useDict('sys_device_type'));

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 活动标签页
const activeTab = ref('basic');

// 根据编辑类型决定标题
const title = computed(() => (props.editType === 'add' ? '添加客户端' : '修改客户端'));

// 按钮loading
const buttonLoading = ref(false);
const keyPairLoading = ref(false);

// 表单ref
const clientFormRef = ref<ElFormInstance>();
const securityFormRef = ref<ElFormInstance>();

// 安全配置
const securityConfig = ref<ClientConfig>({
  ak: '',
  sk: '',
  encryptionMode: 'SM2',
  sm2PublicKey: '',
  sm2PrivateKey: '',
  serverPath: '',
  port: ''
});

// API权限列表
const apiPermissions = ref<ApiPermission[]>([]);

// 初始化表单数据
const initFormData: ClientForm = {
  id: undefined,
  clientId: undefined,
  clientKey: undefined,
  clientSecret: undefined,
  grantTypeList: undefined,
  deviceType: undefined,
  activeTimeout: undefined,
  timeout: undefined,
  status: '0'
};

// 表单数据
const form = ref<ClientForm>({ ...initFormData });

// 表单校验规则
const rules = ref({
  id: [{ required: true, message: 'id不能为空', trigger: 'blur' }],
  clientId: [{ required: true, message: '客户端id不能为空', trigger: 'blur' }],
  clientKey: [{ required: true, message: '客户端key不能为空', trigger: 'blur' }],
  clientSecret: [{ required: true, message: '客户端秘钥不能为空', trigger: 'blur' }],
  grantTypeList: [{ required: true, message: '授权类型不能为空', trigger: 'change' }],
  deviceType: [{ required: true, message: '设备类型不能为空', trigger: 'change' }]
});

/**
 * 生成访问密钥(AK)
 */
const generateAk = async () => {
  try {
    const res = await generateAccessKey();
    securityConfig.value.ak = res.data;
    proxy?.$modal.msgSuccess('AK生成成功');
  } catch (error) {
    console.error('生成AK失败:', error);
    // 前端备用生成方法
    const hexString = await generateHexString(64);
    securityConfig.value.ak = hexString;
  }
};

/**
 * 生成安全密钥(SK)
 */
const generateSk = async () => {
  try {
    const res = await generateSecretKey();
    securityConfig.value.sk = res.data;
    proxy?.$modal.msgSuccess('SK生成成功');
  } catch (error) {
    console.error('生成SK失败:', error);
    // 前端备用生成方法
    const hexString = await generateHexString(128);
    securityConfig.value.sk = hexString;
  }
};

/**
 * 生成SM2密钥对 (调用后端Hutool)
 */
const generateSm2KeyPair = async () => {
  try {
    await proxy?.$modal.confirm('确认要生成新的密钥对？这将覆盖当前的密钥');
    keyPairLoading.value = true;
    const res = await getSm2KeyPair();
    securityConfig.value.sm2PrivateKey = res.data.privateKey;
    securityConfig.value.sm2PublicKey = res.data.publicKey;
    proxy?.$modal.msgSuccess('SM2密钥对生成成功');
  } catch (error) {
    console.error('生成SM2密钥对失败:', error);
  } finally {
    keyPairLoading.value = false;
  }
};

/**
 * 生成十六进制字符串（备用方法）
 */
const generateHexString = async (length: number): Promise<string> => {
  const bytes = new Uint8Array(Math.ceil(length / 2));
  window.crypto.getRandomValues(bytes);
  return Array.from(bytes).map(b => b.toString(16).padStart(2, '0')).join('').slice(0, length);
};

/**
 * 添加API权限
 */
const addApiPermission = () => {
  apiPermissions.value.push({
    api: '',
    method: 'GET'
  });
};

/**
 * 删除API权限
 * @param index 索引
 */
const removeApiPermission = (index: number) => {
  apiPermissions.value.splice(index, 1);
};

/** 取消按钮 */
const cancel = () => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = () => {
  form.value = { ...initFormData };
  securityConfig.value = {
    ak: '',
    sk: '',
    encryptionMode: 'SM2',
    sm2PublicKey: '',
    sm2PrivateKey: '',
    serverPath: '',
    port: ''
  };
  apiPermissions.value = [];
  clientFormRef.value?.resetFields();
  if (securityFormRef.value) {
    securityFormRef.value.resetFields();
  }
  activeTab.value = 'basic';
};

/** 提交按钮 */
const submitForm = () => {
  clientFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        // 构建完整的表单数据，直接使用对象类型而非字符串
        const formData = {
          ...form.value,
          // 直接使用安全配置对象
          config: securityConfig.value,
          // 直接使用API权限数组
          apiPermissions: apiPermissions.value
        };

        if (form.value.id) {
          await updateClient(formData);
        } else {
          await addClient(formData);
        }
        proxy?.$modal.msgSuccess('操作成功');
        emit('save');
        visibleProxy.value = false;
      } catch (error) {
        console.error('提交表单失败:', error);
      } finally {
        buttonLoading.value = false;
      }
    } else {
      activeTab.value = 'basic';
      proxy?.$modal.msgError('请填写必填项');
    }
  });
};

// 监听抽屉可见性、编辑类型和行数据变化
watch(
  [() => props.visible, () => props.editType, () => props.rowData],
  async ([visible, type, data]) => {
    if (visible) {
      resetForm();

      if (type === 'edit' && data) {
        // 修改操作
        const res = await getClient(data.id);
        Object.assign(form.value, res.data);

        // 直接使用对象类型数据
        if (res.data.config) {
          securityConfig.value = res.data.config;
        }

        if (res.data.apiPermissions) {
          apiPermissions.value = res.data.apiPermissions;
        }
      }
    }
  },
  { immediate: true }
);
</script>

<style scoped>
:deep(.el-select) {
  width: 100%;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}

.el-divider {
  margin: 24px 0;
}
</style>
