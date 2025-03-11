<template>
  <div>
    <el-drawer :title="dialog.title" v-model="dialog.visible" size="80%" append-to-body :close-on-click-modal="false">
      <v-md-preview :text="text"></v-md-preview>
<!--      &lt;!&ndash; 图片 &ndash;&gt;-->
<!--      <div>-->
<!--        <el-image v-for="url in urls" :key="url" :src="url" lazy v-if="showImage" />-->
<!--      </div>-->
      <!-- 视频 -->
<!--      <video ref="videoRef" :src="videoUrl" width="650" height="380" autoplay controls v-if="showVideo"></video>-->
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">关 闭</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { queryHelpDoc } from "@/api/common/syscommon";
import { queryVideoUrlByPath } from '@/api/busgrp/video';
import { AttachBusCodeConstant } from '@/enums/AttachBusCodeConstant';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
//对话框显示状态
const dialog = reactive<DialogOption>({
  visible: false,
  title: ""
});
const text = ref("");
const videoUrl = ref("");//视频地址
const showImage = ref(false);
const showVideo = ref(false);

//关闭
const cancel = () => {
  dialog.visible = false;
};
//显示
const showDialog = async (busCode: string,path?: string) => {
  //没有值加载 有值不重新加载
  if (text.value === '') {
    const res = await queryHelpDoc(busCode);
    if (res) {
      text.value = res.data.content;
      dialog.title = res.data.title;
    }
  }
  //有视频,查询视频地址
  // if(path){
  //   showVideo.value = true;
  //   const formData = new FormData();
  //   formData.append('path',path);
  //   formData.append('busCode',AttachBusCodeConstant.VIDEO_FILE_UPLOAD.code);
  //   const urlData = await queryVideoUrlByPath(formData);
  //   if(urlData){
  //     videoUrl.value = urlData.data;
  //   }
  // }
  dialog.visible = true;
};

// const urls = any[];

//暴露方法
defineExpose({ showDialog });
</script>

<style scoped lang="scss"></style>
