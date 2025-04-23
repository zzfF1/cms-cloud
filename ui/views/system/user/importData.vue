<template>
  <!-- 用户导入对话框 -->
  <el-dialog v-model="visibleProxy" :title="upload.title" width="400px" append-to-body>
    <el-upload
      ref="uploadRef"
      :limit="1"
      accept=".xlsx, .xls"
      :headers="upload.headers"
      :action="upload.url + '?updateSupport=' + upload.updateSupport"
      :disabled="upload.isUploading"
      :on-progress="handleFileUploadProgress"
      :on-success="handleFileSuccess"
      :auto-upload="false"
      drag
    >
      <el-icon class="el-icon--upload">
        <i-ep-upload-filled />
      </el-icon>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <template #tip>
        <div class="text-center el-upload__tip">
          <div class="el-upload__tip"><el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据</div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
        </div>
      </template>
    </el-upload>
    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="upload.isUploading" type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="handleCancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { globalHeaders } from '@/utils/request';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  // 可以添加更多props，如uploadUrl等，这里使用默认值
  uploadUrl: {
    type: String,
    default: '/system/user/importData'
  },
  title: {
    type: String,
    default: '用户导入'
  }
});

const emit = defineEmits(['update:visible', 'save', 'template-download']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const uploadRef = ref<ElUploadInstance>();

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 导入参数
const upload = reactive<ImportOption>({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: props.title,
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: globalHeaders(),
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + props.uploadUrl
});

// 监听props变化
watch(
  () => props.title,
  (val) => {
    upload.title = val;
  }
);

// 监听props变化
watch(
  () => props.uploadUrl,
  (val) => {
    upload.url = import.meta.env.VITE_APP_BASE_API + val;
  }
);

/**文件上传中处理 */
const handleFileUploadProgress = () => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: UploadFile) => {
  upload.isUploading = false;
  uploadRef.value?.handleRemove(file);
  visibleProxy.value = false;
  ElMessageBox.alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + '</div>', '导入结果', {
    dangerouslyUseHTMLString: true
  });
  emit('save');
};

/** 提交上传文件 */
const submitFileForm = () => {
  uploadRef.value?.submit();
};

/** 下载模板操作 */
const importTemplate = () => {
  proxy?.download('system/user/importTemplate', {}, `user_template_${new Date().getTime()}.xlsx`);
  emit('template-download');
};

/** 取消导入 */
const handleCancel = () => {
  visibleProxy.value = false;
};

// 在组件显示时重置状态
watch(
  () => props.visible,
  (val) => {
    if (val) {
      upload.isUploading = false;
      upload.updateSupport = 0;
    }
  }
);
</script>
