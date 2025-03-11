<template>
  <el-select
    v-model="selectVal"
    :placeholder="placeholder"
    :disabled="isDisabled"
    :style="{ width: wid }"
    :clearable="isClearable"
    filterable
    :filter-method="dataFilter"
    multiple
    collapse-tags
    collapse-tags-tooltip
    @change="doSelect"
    @focus="doFocus"
  >
    <template #header>
      <el-button size="small" @click="selectAll">全选</el-button>
      <el-button size="small" @click="invertSelect">反选</el-button>
    </template>
    <el-option v-for="item in options" :key="item.value" :label="item.value + '-' + item.label" :value="item.value">
      <div style="display: flex">
        <span v-if="item.value" style="margin-right: 8px">{{ item.value }}</span>
        <span v-if="item.label" style="color: purple">{{ item.label }}</span>
      </div>
    </el-option>
  </el-select>
</template>

<script setup lang="ts">
import { getDicts } from '@/api/system/dict/data';
import { queryBaseLabel } from '@/api/common/syscommon';
import { LabelShowVo } from '@/api/common/syscommon/types';
import { PropType } from 'vue';

// 定义组件的属性
const props = defineProps({
  //请求路径
  url: { type: String, default: null },
  //字典类型
  dictType: { type: String, default: null },
  //值
  modelValue: { type: Array as PropType<(string | number)[]> },
  //宽度
  wid: { type: String },
  //原数据
  optionsData: { type: Array as PropType<LabelShowVo[]>, default: null },
  //是否启用
  isDisabled: { type: Boolean, default: false },
  //提示
  placeholder: { type: String, default: '请选择' },
  //是否加载数据
  loadData: { type: Boolean, default: true },
  //是否允许清空
  isClearable: { type: Boolean, default: true }
});
//选项内容
const options = ref([] as LabelShowVo[]);
//备份
const copyOptions = ref([] as LabelShowVo[]);
//事件
const emits = defineEmits(['update:modelValue', 'change', 'focus']);
//监听数据
watch(
  () => props.optionsData,
  (newOptionsData) => {
    if (!props.url && !props.dictType) {
      options.value = newOptionsData;
      copyOptions.value = newOptionsData;
    }
  },
  { immediate: true }
);

//监听url变化
watch(
  () => props.url,
  async (newUrl) => {
    if (newUrl) {
      await getData(); // 重新加载数据
    }
  }
);
//选择
const selectVal = computed({
  get() {
    return props.modelValue;
  },
  set(val) {
    emits('update:modelValue', val);
  }
});

//选中事件
const doSelect = (val: any) => {
  //如果有值
  if (val) {
    var optionVal = options.value?.filter((v) => v.value == val)[0];
    emits('change', val, optionVal);
  }
};

//获取焦点事件
const doFocus = () => {
  emits('focus');
};

//索引方法
const dataFilter = (val?: string) => {
  // 自定义查询方法
  if (val) {
    const lVal = val.toUpperCase();
    options.value = copyOptions.value?.filter((item) => {
      if (
        item.value.includes(val) ||
        item.value.toUpperCase().includes(lVal) ||
        item.label.includes(val) ||
        item.label.toUpperCase().includes(lVal)
      ) {
        return true;
      }
    });
  } else {
    options.value = copyOptions.value;
  }
};

//获取数据方法
const getData = async () => {
  //如果不加载数据
  if (!props.loadData) {
    return;
  }
  //如果是请求路径
  if (props.url) {
    const res = await queryBaseLabel(props.url);
    if (res) {
      options.value = res.data;
      copyOptions.value = res.data;
    }
  } else if (props.dictType) {
    //如果是请求字典类型
    let dictType = props.dictType;
    const res = await getDicts(dictType);
    //循环数据
    res.data.map((p) => {
      let Item = { label: p.dictLabel, value: p.dictValue, otherData: undefined };
      options.value.push(Item);
      copyOptions.value.push(Item);
    });
  }
};

// 全选逻辑
const selectAll = () => {
  selectVal.value = options.value.map((option) => option.value);
};

// 反选逻辑
const invertSelect = () => {
  const selectedValues = selectVal.value;
  const allValues = options.value.map((option) => option.value);
  const unselectedValues = allValues.filter((value) => !selectedValues.includes(value));
  selectVal.value = unselectedValues;
};

onMounted(() => {
  getData();
});

//获取选项长度
const getOptionLength = () => {
  return copyOptions.value?.length;
};

//清空选项
const clearOptions = () => {
  options.value = [];
  copyOptions.value = [];
};

defineExpose({
  getOptionLength,
  clearOptions
});
</script>

<style lang="scss" scoped></style>
