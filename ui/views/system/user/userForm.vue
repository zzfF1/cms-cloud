<template>
  <!-- 添加或修改用户信息抽屉 -->
  <el-drawer v-model="visibleProxy" :title="title" size="55%" append-to-body :close-on-click-modal="false" @closed="resetForm">
    <el-card shadow="always" class="box-card" style="padding-top: 10px">
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="80px">
        <!-- 基本信息 -->
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="props.enabledDeptTree"
                :props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                placeholder="请选择归属部门"
                check-strictly
                @change="handleDeptChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="渠道类型" prop="branchType">
              <el-select v-model="form.branchType" placeholder="请选择渠道类型">
                <el-option v-for="dict in branch_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="证件号码" prop="idNo">
              <el-input v-model="form.idNo" placeholder="请输入证件号码" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="绑定IP" prop="bindIp">
              <el-input v-model="form.bindIp" placeholder="请输入IP地址" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="账户类型" prop="accType">
              <el-radio-group v-model="form.accType">
                <el-radio :value="0">长期用户</el-radio>
                <el-radio :value="1">临时用户</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="系统内置" prop="sysType">
              <el-radio-group v-model="form.sysType">
                <el-radio value="Y">是</el-radio>
                <el-radio value="N">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="访问时间" prop="accessTime">
              <el-time-picker
                v-model="form.accessTime"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                value-format="HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-if="form.accType === 1">
          <el-col :span="24">
            <el-form-item label="账户有效期" prop="validDate">
              <el-date-picker
                v-model="form.validDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择">
                <el-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :label="item.postName"
                  :value="item.postId"
                  :disabled="item.status == '1'"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleIds">
              <el-select v-model="form.roleIds" filterable multiple placeholder="请选择">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId"
                  :disabled="item.status == '1'"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import api from '@/api/system/user';
import { UserForm, UserVO } from '@/api/system/user/types';
import { DeptTreeVO } from '@/api/system/dept/types';
import { RoleVO } from '@/api/system/role/types';
import { PostVO } from '@/api/system/post/types';
import { optionselect } from '@/api/system/post';

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
    type: Object as PropType<UserVO | null>,
    default: null
  },
  enabledDeptTree: {
    type: Array as PropType<DeptTreeVO[]>,
    default: () => []
  }
});

const emit = defineEmits(['update:visible', 'save']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable, sys_user_sex, branch_type } = toRefs<any>(
  proxy?.useDict('sys_normal_disable', 'sys_user_sex', 'branch_type')
);

// 表单相关数据
const userFormRef = ref<ElFormInstance>();
const postOptions = ref<PostVO[]>([]);
const roleOptions = ref<RoleVO[]>([]);
const initPassword = ref<string>('');

// 初始化表单数据
const initFormData: UserForm = {
  userId: undefined,
  deptId: undefined,
  userName: '',
  nickName: undefined,
  password: '',
  phonenumber: undefined,
  email: undefined,
  sex: undefined,
  status: '0',
  remark: '',
  postIds: [],
  roleIds: [],
  branchType: '',
  idNo: '',
  bindIp: '',
  accessTime: undefined,
  accType: 0,
  validDate: undefined,
  realName: '',
  sysType: 'N'
};

// 表单数据
const form = ref<UserForm>({ ...initFormData });

// 表单校验规则
const rules = ref({
  userName: [
    { required: true, message: '用户名称不能为空', trigger: 'blur' },
    {
      min: 2,
      max: 20,
      message: '用户名称长度必须介于 2 和 20 之间',
      trigger: 'blur'
    }
  ],
  nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
  password: [
    { required: true, message: '用户密码不能为空', trigger: 'blur' },
    {
      min: 5,
      max: 20,
      message: '用户密码长度必须介于 5 和 20 之间',
      trigger: 'blur'
    }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  phonenumber: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  roleIds: [{ required: true, message: '用户角色不能为空', trigger: 'blur' }],
  branchType: [{ required: true, message: '请选择渠道类型', trigger: 'change' }],
  bindIp: [
    {
      pattern: /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/,
      message: '请输入正确的IP地址',
      trigger: 'blur'
    }
  ],
  idNo: [
    {
      pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
      message: '请输入正确的身份证号码',
      trigger: 'blur'
    }
  ],
  validDate: [
    {
      validator: (rule, value, callback) => {
        if (form.value.accType === 1 && (!value || value.length < 2)) {
          callback(new Error('临时用户必须设置有效期'));
        } else if (value && value.length === 2) {
          const diff = new Date(value[1]).getTime() - new Date(value[0]).getTime();
          if (diff < 90 * 24 * 60 * 60 * 1000) {
            callback(new Error('账户有效期间隔至少90天'));
          } else {
            callback();
          }
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ]
});

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 根据编辑类型决定标题
const title = computed(() => (props.editType === 'add' ? '添加用户' : '修改用户'));

// 按钮loading状态
const buttonLoading = ref(false);

/** 取消按钮 */
const cancel = () => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = () => {
  form.value = { ...initFormData };
  userFormRef.value?.resetFields();
};

/** 提交按钮 */
const submitForm = () => {
  userFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        if (form.value.userId) {
          await api.updateUser(form.value);
        } else {
          await api.addUser(form.value);
        }
        proxy?.$modal.msgSuccess('操作成功');
        emit('save');
        visibleProxy.value = false;
      } finally {
        buttonLoading.value = false;
      }
    }
  });
};

// 部门变更事件
const handleDeptChange = async (value: number | string) => {
  const response = await optionselect(value);
  postOptions.value = response.data;
  form.value.postIds = [];

  // 查找选中的部门对象，获取管理机构信息
  const selectedDept = findDeptById(props.enabledDeptTree, value);
  if (selectedDept) {
    form.value.manageCom = selectedDept.manageCom; // 自动设置管理机构
  }
};

// 递归查找部门对象
const findDeptById = (deptTree: DeptTreeVO[], id: string | number): DeptTreeVO | null => {
  for (const dept of deptTree) {
    if (dept.id === id) {
      return dept;
    }
    if (dept.children && dept.children.length > 0) {
      const found = findDeptById(dept.children, id);
      if (found) return found;
    }
  }
  return null;
};

// 监听抽屉可见性、编辑类型和行数据变化
watch(
  [() => props.visible, () => props.editType, () => props.rowData],
  async ([visible, type, data]) => {
    if (visible) {
      resetForm();
      if (type === 'add') {
        // 新增操作
        const { data: userData } = await api.getUser();
        postOptions.value = userData.posts;
        roleOptions.value = userData.roles;
        form.value.password = initPassword.value;
      } else if (type === 'edit' && data) {
        // 修改操作
        const { data: userData } = await api.getUser(data.userId);
        Object.assign(form.value, userData.user);

        // 处理时间范围字段
        if (userData.user.accessStartTime && userData.user.accessEndTime) {
          form.value.accessTime = [userData.user.accessStartTime, userData.user.accessEndTime];
        }
        if (userData.user.validStartDate && userData.user.validEndDate) {
          form.value.validDate = [userData.user.validStartDate, userData.user.validEndDate];
        }

        postOptions.value = userData.posts;
        roleOptions.value = userData.roles;
        form.value.postIds = userData.postIds;
        form.value.roleIds = userData.roleIds;
      }
    }
  },
  { immediate: true }
);

// 初始化获取默认密码
onMounted(() => {
  proxy?.getConfigKey('sys.user.initPassword').then((response) => {
    initPassword.value = response.data;
  });
});
</script>

<style scoped>
:deep(.el-select) {
  width: 100%;
}
</style>
