<template>
  <div>
    <!-- 导入数据 -->
    <el-dialog v-model="upload.open" :title="upload.title" append-to-body width="400px">
      <el-upload
        ref="uploadRef"
        v-model:file-list="uploadFile"
        :action="upload.url"
        :auto-upload="false"
        :disabled="upload.isUploading"
        :headers="upload.headers"
        :limit="1"
        accept=".xlsx, .xls"
        drag
      >
        <el-icon class="el-icon--upload">
          <i-ep-upload-filled />
        </el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="text-center el-upload__tip">
            <span>仅允许导入xls、xlsx格式文件。</span>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
      <show-batch-error-msg ref="batchErrorRef"></show-batch-error-msg>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { globalHeaders } from '@/utils/request';
import { UploadUserFile } from 'element-plus';
import { importDataFile } from '@/api/common/syscommon'
import ShowBatchErrorMsg from '@/views/modules/common/ShowBatchErrorMsg.vue'

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
//属性
const props = defineProps({
  //上传地址
  upUrl: { type: String, default: '' }
});
const uploadRef = ref<ElUploadInstance>();
//上付附件
const uploadFile = ref<UploadUserFile[]>([]);
//批量错误信息
const batchErrorRef = ref<InstanceType<typeof ShowBatchErrorMsg>>();
/*** 导入参数 */
const upload = reactive<ImportOption>({
  // 是否显示弹出层
  open: false,
  // 弹出层标题
  title: '',
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: globalHeaders(),
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + props.upUrl
});
const emit = defineEmits(['ok']);
/** 查询参数列表 */
const show = () => {
  upload.open = true;
  upload.title = '导入数据';
};
/** 取消 */
const cancel = () => {
  upload.open = false;
  uploadRef.value?.clearFiles();
  uploadFile.value = [];
};

/** 提交上传文件 */
const submitFileForm = async () => {
  //如果有附件
  if (uploadFile.value?.length === 0) {
    proxy?.$modal.msgError('请选择要传的文件!');
    return;
  }
  //是否有附件
  let fileItem: UploadUserFile | undefined = undefined;
  fileItem = uploadFile.value[0];
  if (fileItem.raw) {
    proxy?.$modal.loading('正在上传文件，请稍候...');
    try {
      const formData = new FormData();
      formData.append('file', fileItem.raw);
      await importDataFile(props.upUrl, formData);
      proxy?.$modal.msgSuccess("导入成功");
      proxy?.$modal.closeLoading();
    } catch (e) {
      proxy?.$modal.closeLoading();
      proxy?.$modal.msgError('导入存在以下错误,请核对!');
      batchErrorRef.value?.show(e);
      return;
    }
  }
  emit('ok');
  cancel();
}

//暴露方法
defineExpose({
  show
});
</script>

<style lang="scss" scoped></style>
