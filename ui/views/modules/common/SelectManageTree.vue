<template>
  <el-input v-model="inputShowVal" placeholder="请选择" readonly>
    <template #append>
      <el-button :disabled="props.isEnabled" @click="openDialog">选择</el-button>
    </template>
  </el-input>
  <m-dialog v-model="dialogVisible" title="选择管理机构" dialog-widht="450px" @save-submit="confirm">
    <el-tree
      ref="deptTreeRef"
      :data="deptOptions"
      :props="defaultProps"
      :expand-on-click-node="false"
      :default-expand-all="true"
      @node-click="handleNodeClick"
    ></el-tree>
  </m-dialog>
</template>

<script setup lang="ts">
import { deptTreeSelect } from '@/api/system/user';
import { DeptTreeVO } from '@/api/system/dept/types';
import MDialog from '@/views/modules/common/MDialog.vue';

// 定义组件的属性
const props = defineProps({
  //显示级别
  showLevel: { type: Number, default: 4 },
  //结果值
  modelValue: { type: String, default: '' },
  //结果值
  showVal: { type: String, default: '' },
  //是否允许选择上级节点
  parentSelect: { type: Boolean, default: true },
  //是否启用
  isEnabled: { type: Boolean, default: false }
});

//显示状态
const dialogVisible = ref(false);
//文本框值
const inputShowVal = ref(props.showVal);
const selectedValue = ref(props.modelValue);
const defaultProps = { children: 'children', label: 'label' };
const emit = defineEmits(['update:modelValue']);
const deptOptions = ref<DeptTreeVO[]>([]);
const deptTreeRef = ref<ElTreeInstance>();

//监听数据修改
watch(
  () => props.showVal,
  (newVal) => {
    inputShowVal.value = newVal.toString();
  }
);
watch(
  () => props.modelValue,
  (newVal) => {
    if (!newVal) {
      inputShowVal.value = '';
    }
  }
);

/** 查询部门下拉树结构 */
const getTreeSelect = async () => {
  const res = await deptTreeSelect();
  deptOptions.value = res.data;
};

//显示弹窗
const openDialog = () => {
  dialogVisible.value = true;
};

//关闭弹窗
const closeDialog = () => {
  dialogVisible.value = false;
};

//选择事件
const handleNodeClick = (data: any) => {
  selectedValue.value = data.id.toString();
};

//确定
const confirm = () => {
  //获取当前选中节点
  let node = deptTreeRef.value?.getCurrentNode();
  //如果为空
  if (!node) {
    ElMessage({ message: '请选择管理机构!', type: 'warning' });
    return;
  }
  //如果不允许选择上级节点
  if (!props.parentSelect) {
    if (!(!node.children || node.children.length === 0)) {
      ElMessage({ message: '请选择下级管理机构!', type: 'warning' });
      return;
    }
  }
  //展示值
  inputShowVal.value = node.label;
  //设置选中值
  selectedValue.value = node.manageCom;
  //更新结果
  emit('update:modelValue', selectedValue.value);
  //关闭对话框
  closeDialog();
};
//开始加载
onMounted(() => {
  getTreeSelect();
});
</script>

<style lang="scss" scoped></style>
