<template>
  <el-dialog v-model="visible" :title="title" width="600px" append-to-body destroy-on-close>
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
      <el-form-item label="代码" prop="code">
        <el-input v-model="formData.code" placeholder="请输入代码" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入名称" />
      </el-form-item>
      <el-form-item label="渠道" prop="branchType">
        <el-select v-model="formData.branchType" placeholder="请选择渠道" class="w-full">
          <el-option label="银保" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择类型" class="w-full">
          <el-option label="生成方式" value="0" />
          <el-option label="模板方式" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="formData.type === '1'" label="模板路径">
        <el-input v-model="formData.path" placeholder="请上传模板">
          <template #append>
            <el-button @click="handleUpload">上传</el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  data: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:modelValue', 'submit']);

// 弹窗显示控制
const visible = ref(false);
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val;
  }
);
watch(
  () => visible.value,
  (val) => {
    emit('update:modelValue', val);
  }
);

// 表单ref
const formRef = ref(null);

// 表单数据
const formData = reactive({
  id: undefined,
  code: '',
  name: '',
  branchType: '3',
  type: '0',
  path: '',
  filename: ''
});

// 上传弹窗数据
const uploadDialog = reactive({
  visible: false,
  file: null
});

// 表单校验规则
const formRules = {
  code: [
    { required: true, message: '请输入代码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '代码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  branchType: [{ required: true, message: '请选择渠道', trigger: 'change' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
};

// 监听编辑数据
watch(
  () => props.data,
  (newVal) => {
    if (newVal) {
      Object.assign(formData, newVal);
    }
  },
  { deep: true }
);

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    if (formData.type === '1' && !formData.path) {
      ElMessage.warning('请上传模板文件');
      return;
    }

    emit('submit', { ...formData });
    visible.value = false;
  } catch (error) {
    ElMessage.error('表单校验失败，请检查必填项');
  }
};

// 弹窗关闭时重置表单
const handleReset = () => {
  formRef.value?.resetFields();
  Object.assign(formData, {
    id: undefined,
    code: '',
    name: '',
    branchType: '3',
    type: '0',
    path: '',
    filename: ''
  });
  uploadDialog.file = null;
};
</script>

<style scoped></style>
