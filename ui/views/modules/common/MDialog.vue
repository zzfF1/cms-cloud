<template>
  <!-- 模板部分 -->
  <el-dialog
    v-model="dialogVisible"
    :width="width"
    :fullscreen="fullscreen"
    draggable
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    style="padding-bottom: 0px !important"
  >
    <!-- 弹窗头部 -->
    <template #header>
      <div class="custom-header">
        <span>{{ title }}</span>
        <div class="right-btn">
          <!-- 全屏按钮 -->
          <el-icon @click="changeScreen">
            <FullScreen />
          </el-icon>
          <!-- 关闭按钮 -->
          <el-icon @click="close">
            <Close />
          </el-icon>
        </div>
      </div>
    </template>
    <!-- 弹窗中间body -->
    <el-scrollbar>
      <div :class="formBody">
        <!-- 这个一定要加 -->
        <slot />
      </div>
    </el-scrollbar>
    <!-- 弹窗底部 -->
    <template v-if="footer" #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="saveSubmit">确定</el-button>
        <el-button @click="close">取消</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
let props = defineProps({
  //是否显示弹窗
  modelValue: { type: Boolean, require: true, default: false },
  //弹窗标题
  title: { type: String, default: '' },
  //是否显示底部按钮
  footer: { type: Boolean, default: true },
  //确认事件
  saveSubmit: {
    type: Function,
    default: () => {
      return Function;
    }
  },
  //弹窗宽度
  dialogWidht: { type: String, default: '500px' }
});
//显示弹窗
const dialogVisible = ref(props.modelValue);
//弹窗标题
const title = ref(props.title);
//是否全屏
const fullscreen = ref(false);
//弹窗body部分css
const formBody = ref('form-body');
//宽度
const width = ref(props.dialogWidht);

let emits = defineEmits(['update:modelValue', 'saveSubmit']);
//全屏
const changeScreen = () => {
  if (fullscreen.value == true) {
    fullscreen.value = false;
    formBody.value = 'form-body';
  } else {
    fullscreen.value = true;
    formBody.value = 'form-body-new';
  }
};
//销毁
const close = () => {
  dialogVisible.value = false;
  emits('update:modelValue', false);
};
//保存表单方法
const saveSubmit = () => {
  emits('saveSubmit', props.saveSubmit);
};

//监听value
watch(
  () => props.modelValue,
  (value, o) => {
    if (value == true) {
      fullscreen.value = false;
    }
    dialogVisible.value = value;
  }
);
</script>

<style scoped lang="scss">
.custom-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  height: 20px;
}

.custom-header .right-btn {
  height: 40px;
  cursor: pointer;
}

.custom-header .right-btn .el-icon {
  margin-right: 10px;
  height: 100%;
}

.custom-header .right-btn .el-icon:hover {
  color: #fff000;
}

//
.form-body {
  max-height: 70vh;
  overflow: auto;
}

.form-body-new {
  max-height: 70vh;
  overflow: auto;
}

.el-scrollbar {
  margin-bottom: 30px;
}
</style>
