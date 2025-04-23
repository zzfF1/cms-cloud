<template>
  <!-- 添加或修改公告抽屉 -->
  <el-drawer v-model="visibleProxy" :title="title" size="55%" append-to-body :close-on-click-modal="false" @closed="resetForm">
    <el-card shadow="always" class="box-card" style="padding-top: 10px">
      <el-form ref="noticeFormRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择">
                <el-option v-for="dict in sys_notice_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_notice_status" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="内容">
              <editor v-model="form.noticeContent" :min-height="192" />
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
import { getNotice, addNotice, updateNotice } from '@/api/system/notice';
import { NoticeForm, NoticeVO } from '@/api/system/notice/types';

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
    type: Object as PropType<NoticeVO | null>,
    default: null
  }
});

const emit = defineEmits(['update:visible', 'save']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_notice_status, sys_notice_type } = toRefs<any>(proxy?.useDict('sys_notice_status', 'sys_notice_type'));

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 根据编辑类型决定标题
const title = computed(() => (props.editType === 'add' ? '添加公告' : '修改公告'));

// 按钮loading
const buttonLoading = ref(false);

// 表单ref
const noticeFormRef = ref<ElFormInstance>();

// 初始化表单数据
const initFormData: NoticeForm = {
  noticeId: undefined,
  noticeTitle: '',
  noticeType: '',
  noticeContent: '',
  status: '0',
  remark: '',
  createByName: ''
};

// 表单数据
const form = ref<NoticeForm>({ ...initFormData });

// 表单校验规则
const rules = ref({
  noticeTitle: [{ required: true, message: '公告标题不能为空', trigger: 'blur' }],
  noticeType: [{ required: true, message: '公告类型不能为空', trigger: 'change' }]
});

/** 取消按钮 */
const cancel = () => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = () => {
  form.value = { ...initFormData };
  noticeFormRef.value?.resetFields();
};

/** 提交按钮 */
const submitForm = () => {
  noticeFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        if (form.value.noticeId) {
          await updateNotice(form.value);
        } else {
          await addNotice(form.value);
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

      if (type === 'edit' && data) {
        // 修改操作
        const res = await getNotice(data.noticeId);
        Object.assign(form.value, res.data);
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
</style>
