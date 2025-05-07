<template>
  <!-- 添加或修改部门抽屉 -->
  <el-drawer v-model="visibleProxy" :title="title" size="55%" append-to-body :close-on-click-modal="false" @closed="resetForm">
    <el-card shadow="always" class="box-card" style="padding-top: 10px">
      <el-form ref="deptFormRef" :model="form" :rules="rules" label-width="100px">
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-row>
          <el-col v-if="form.parentId !== 0" :span="24">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                value-key="deptId"
                placeholder="选择上级部门"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构代码" prop="manageCom">
              <el-input v-model="form.manageCom" placeholder="请输入机构代码" maxlength="8" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入机构名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构简称" prop="shortname">
              <el-input v-model="form.shortname" placeholder="请输入机构简称" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构级别" prop="comgrade">
              <el-select v-model="form.comgrade" placeholder="请选择机构级别">
                <el-option label="总公司" value="0" />
                <el-option label="省级分公司" value="1" />
                <el-option label="地市级中支公司" value="2" />
                <el-option label="县支公司" value="3" />
                <el-option label="营销服务部" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 机构信息 -->
        <el-divider content-position="left">机构信息</el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="对外机构码" prop="outcomcode">
              <el-input v-model="form.outcomcode" placeholder="请输入对外机构代码" maxlength="10" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="行政区划码" prop="regionalismcode">
              <el-input v-model="form.regionalismcode" placeholder="请输入行政区划代码" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="机构地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入机构地址" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构邮编" prop="zipcode">
              <el-input v-model="form.zipcode" placeholder="请输入机构邮编" maxlength="10" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人姓名" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构电话" prop="comPhone">
              <el-input v-model="form.comPhone" placeholder="请输入机构电话" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构传真" prop="fax">
              <el-input v-model="form.fax" placeholder="请输入机构传真" maxlength="50" />
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
import { addDeptLdcom, getDeptLdcom, listDept, listDeptExcludeChild, updateDeptLdcom } from '@/api/system/dept';
import { DeptForm, DeptVO } from '@/api/system/dept/types';

interface DeptOptionsType {
  deptId: number | string;
  deptName: string;
  children: DeptOptionsType[];
}

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
    type: Object as PropType<DeptVO | null>,
    default: null
  }
});

const emit = defineEmits(['update:visible', 'save']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 根据编辑类型决定标题
const title = computed(() => (props.editType === 'add' ? '添加部门' : '修改部门'));
// 按钮loading
const buttonLoading = ref(false);
// 表单ref
const deptFormRef = ref<ElFormInstance>();
// 部门树选项
const deptOptions = ref<DeptOptionsType[]>([]);

// 初始化表单数据
const initFormData: DeptForm = {
  deptId: undefined,
  parentId: undefined,
  manageCom: '',
  deptName: undefined,
  orderNum: 0,
  leader: undefined,
  phone: undefined,
  email: undefined,
  status: '0',

  // Ldcom相关字段
  outcomcode: undefined,
  name: undefined,
  shortname: undefined,
  address: undefined,
  zipcode: undefined,
  comPhone: undefined,
  fax: undefined,
  comgrade: undefined,
  regionalismcode: undefined
};

// 表单数据
const form = ref<DeptForm>({ ...initFormData });

// 表单校验规则
const rules = ref({
  parentId: [{ required: true, message: '上级部门不能为空', trigger: 'blur' }],
  deptName: [{ required: true, message: '机构名称不能为空', trigger: 'blur' }],
  manageCom: [{ required: true, message: '机构代码不能为空', trigger: 'blur' }],
  orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
  comgrade: [{ required: true, message: '机构级别不能为空', trigger: 'change' }],
  leader: [{ max: 50, message: '负责人姓名长度不能超过50个字符', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
  phone: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }],

  // Ldcom相关字段校验规则
  outcomcode: [{ max: 10, message: '对外机构代码长度不能超过10个字符', trigger: 'blur' }],
  name: [{ max: 100, message: '机构名称长度不能超过100个字符', trigger: 'blur' }],
  shortname: [{ max: 50, message: '机构简称长度不能超过50个字符', trigger: 'blur' }],
  address: [{ max: 200, message: '机构地址长度不能超过200个字符', trigger: 'blur' }],
  zipcode: [{ max: 10, message: '机构邮编长度不能超过10个字符', trigger: 'blur' }],
  comPhone: [{ max: 50, message: '机构电话长度不能超过50个字符', trigger: 'blur' }],
  fax: [{ max: 50, message: '机构传真长度不能超过50个字符', trigger: 'blur' }],
  regionalismcode: [{ max: 20, message: '行政区划代码长度不能超过20个字符', trigger: 'blur' }]
});

/** 取消按钮 */
const cancel = () => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = () => {
  form.value = { ...initFormData };
  deptFormRef.value?.resetFields();
};

/** 查询当前部门的所有用户 - 不再需要，保留方法以兼容现有代码 */
async function getDeptAllUser(deptId: any) {
  // 由于负责人改为直接输入，不再需要查询用户列表
  return;
}

/** 提交按钮 */
const submitForm = () => {
  deptFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        // 如果用户填写了机构名称但没有填写name字段，自动复制过去
        if (form.value.deptName && !form.value.name) {
          form.value.name = form.value.deptName;
        }
        if (form.value.deptId) {
          await updateDeptLdcom(form.value);
        } else {
          await addDeptLdcom(form.value);
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

// 监听抽屉可见性、编辑类型和行数据变化
watch(
  [() => props.visible, () => props.editType, () => props.rowData],
  async ([visible, type, data]) => {
    if (visible) {
      resetForm();

      if (type === 'add') {
        // 新增操作
        const res = await listDept();
        const treeData = proxy?.handleTree<DeptOptionsType>(res.data, 'deptId');
        if (treeData) {
          deptOptions.value = treeData;
          if (data && data.deptId) {
            form.value.parentId = data.deptId;
          }
        }
      } else if (type === 'edit' && data) {
        // 修改操作
        const res = await getDeptLdcom(data.deptId);
        Object.assign(form.value, res.data);

        const response = await listDeptExcludeChild(data.deptId);
        const treeData = proxy?.handleTree<DeptOptionsType>(response.data, 'deptId');
        if (treeData) {
          deptOptions.value = treeData;
          if (treeData.length === 0) {
            const noResultsOptions: DeptOptionsType = {
              deptId: form.value.parentId || 0,
              deptName: form.value.parentName || '',
              children: []
            };
            deptOptions.value.push(noResultsOptions);
          }
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
:deep(.el-tree-select) {
  width: 100%;
}
:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: bold;
  color: #409EFF;
}
:deep(.el-divider--horizontal) {
  margin: 20px 0;
}
</style>
