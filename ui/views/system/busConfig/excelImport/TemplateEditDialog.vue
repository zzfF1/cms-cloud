<!-- TemplateEditDialog.vue -->
<template>
  <el-dialog v-model="visible" :title="title" width="800px" append-to-body destroy-on-close @closed="handleClosed">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入模板名称" />
      </el-form-item>
      <el-form-item label="代码" prop="code">
        <el-input v-model="formData.code" placeholder="请输入代码" />
      </el-form-item>
      <el-form-item label="渠道" prop="branchType">
        <el-select v-model="formData.branchType" placeholder="请选择渠道">
          <el-option label="渠道1" value="01" />
          <el-option label="渠道2" value="02" />
        </el-select>
      </el-form-item>
      <el-form-item label="说明" prop="remark">
        <el-input v-model="formData.remark" type="textarea" placeholder="请输入说明" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, defineProps, defineEmits, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '新增模板'
  },
  data: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:modelValue', 'submit']);

// 表单ref
const formRef = ref(null);

// 弹窗显示控制
const visible = ref(false);

// 表单数据
const formData = reactive({
  id: undefined,
  name: '',
  code: '',
  branchType: '',
  remark: ''
});

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入代码', trigger: 'blur' }],
  branchType: [{ required: true, message: '请选择渠道', trigger: 'change' }]
};

// 监听父组件传入的显示状态
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val;
  }
);

// 监听本地显示状态
watch(
  () => visible.value,
  (val) => {
    emit('update:modelValue', val);
  }
);

// 监听编辑数据
watch(
  () => props.data,
  (val) => {
    if (val) {
      Object.assign(formData, val);
    }
  },
  { deep: true }
);

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();
  emit('submit', { ...formData });
  visible.value = false;
};

// 弹窗关闭后重置表单
const handleClosed = () => {
  formRef.value?.resetFields();
  Object.assign(formData, {
    id: undefined,
    name: '',
    code: '',
    branchType: '',
    remark: ''
  });
};
</script>
