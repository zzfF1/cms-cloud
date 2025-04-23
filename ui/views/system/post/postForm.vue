<template>
  <!-- 添加或修改岗位抽屉 -->
  <el-drawer v-model="visibleProxy" :title="title" size="55%" append-to-body :close-on-click-modal="false" @closed="resetForm">
    <el-card shadow="always" class="box-card" style="padding-top: 10px">
      <el-form ref="postFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select
            v-model="form.deptId"
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' }"
            value-key="id"
            placeholder="请选择部门"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入编码名称" />
        </el-form-item>
        <el-form-item label="类别编码" prop="postCategory">
          <el-input v-model="form.postCategory" placeholder="请输入类别编码" />
        </el-form-item>
        <el-form-item label="岗位顺序" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
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
import { getPost, addPost, updatePost } from '@/api/system/post';
import { PostForm, PostVO } from '@/api/system/post/types';
import { DeptVO, DeptTreeVO } from '@/api/system/dept/types';

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
    type: Object as PropType<PostVO | null>,
    default: null
  },
  deptOptions: {
    type: Array as PropType<DeptTreeVO[]>,
    default: () => []
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
const title = computed(() => (props.editType === 'add' ? '添加岗位' : '修改岗位'));

// 按钮loading
const buttonLoading = ref(false);

// 表单ref
const postFormRef = ref<ElFormInstance>();

// 初始化表单数据
const initFormData: PostForm = {
  postId: undefined,
  deptId: undefined,
  postCode: '',
  postName: '',
  postCategory: '',
  postSort: 0,
  status: '0',
  remark: ''
};

// 表单数据
const form = ref<PostForm>({ ...initFormData });

// 表单校验规则
const rules = ref({
  postName: [{ required: true, message: '岗位名称不能为空', trigger: 'blur' }],
  postCode: [{ required: true, message: '岗位编码不能为空', trigger: 'blur' }],
  deptId: [{ required: true, message: '部门不能为空', trigger: 'blur' }],
  postSort: [{ required: true, message: '岗位顺序不能为空', trigger: 'blur' }]
});

/** 取消按钮 */
const cancel = () => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = () => {
  form.value = { ...initFormData };
  postFormRef.value?.resetFields();
};

/** 提交按钮 */
const submitForm = () => {
  postFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        if (form.value.postId) {
          await updatePost(form.value);
        } else {
          await addPost(form.value);
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
        const res = await getPost(data.postId);
        Object.assign(form.value, res.data);
      }
    }
  },
  { immediate: true }
);
</script>

<style scoped>
:deep(.el-tree-select) {
  width: 100%;
}
</style>
