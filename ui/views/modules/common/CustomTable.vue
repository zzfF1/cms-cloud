<template>
  <el-table
    ref="tableRef"
    v-loading="getLoadFlag"
    :height="tableHeight"
    :data="tableData"
    size="small"
    border
    :scrollbar-always-on="true"
    @selection-change="handleSelectionChange"
  >
    <el-table-column
      v-for="(column, index) in columns"
      :key="index"
      :label="column.label"
      :prop="column.prop"
      :align="column.align ? column.align : 'left'"
      :width="column.width ? column.width : null"
      :fixed="column.fixed ? column.fixed : null"
      :type="column.type ? column.type : null"
      :show-overflow-tooltip="column.tooltip ? true : false"
    >
      <template v-if="column.slot" #default="{ row }">
        <slot :name="column.slot" :row="row" />
      </template>
      <template v-else-if="column.code" #default="{ row }">
        <span>{{ formatCodeName(row, column) }}</span>
      </template>
      <template v-else-if="column.formatter" #default="{ row }">
        {{ column.formatter(row) }}
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup lang="ts">
import { SysPageConfigTabVo } from '@/api/common/syscommon/types';
import { queryPageTable } from '@/api/common/syscommon';
//属性
const props = defineProps({
  //请求路径
  pageCode: { type: String, default: null },
  //加载状态
  loading: { type: Boolean, default: true },
  /** 表格数据 */
  tableData: { type: Array, required: true },
  /** 列对象 */
  tableColumns: { type: Array<SysPageConfigTabVo>, required: false },
  /** 底部高度 */
  bottomHeight: { type: Number, default: 150 }
});
const columns = ref<SysPageConfigTabVo[]>([]);
// table高度
const tableHeight = ref();
// table元素
const tableRef = ref(null);
//加载状态
const localLoading = ref(true);
//加载状态
const getLoadFlag = computed(() => props.loading || localLoading.value);
//事件
const emits = defineEmits(['selection-change', 'row-click', 'row-dblclick']);

//格式化代码与名称
const formatCodeName = (row?: any, column?: any) => {
  return row[column.code] ? row[column.code] + '-' + row[column.name] : '';
};

//选择事件
const handleSelectionChange = (selection: any) => {
  emits('selection-change', selection);
};

//初始化列对象
const initColumns = async () => {
  localLoading.value = true;
  if (props.pageCode) {
    let res = await queryPageTable(props.pageCode);
    columns.value = res.data;
    localLoading.value = false;
  } else {
    columns.value = props.tableColumns || [];
    localLoading.value = false;
  }
};

onMounted(() => {
  initColumns();
  // 设置表格初始高度为innerHeight-offsetTop-表格底部与浏览器底部距离85
  tableHeight.value = window.innerHeight - (tableRef.value as any).$el.offsetTop - props.bottomHeight;
  // 监听浏览器高度变化
  window.onresize = () => {
    tableHeight.value = window.innerHeight - (tableRef.value as any).$el.offsetTop - props.bottomHeight;
  };
});
</script>
