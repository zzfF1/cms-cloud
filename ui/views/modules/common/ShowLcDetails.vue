<template>
  <div>
    <el-dialog v-model="dialogVisible" title="流程轨迹" width="900px" style="height: 500px" :close-on-click-modal="false">
      <el-table ref="tableRef" v-loading="loading" :data="dataList" size="small" height="350px">
        <el-table-column label="序号" type="index" width="50" align="center" />
        <el-table-column label="操作类型" align="center" prop="czType" width="100">
          <template #default="scope">
            <el-tag :type="getDirectTagType(scope.row.czType)">
              {{ getDirectTagText(scope.row.czType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="流程节点" align="left" prop="lcName" width="150" />
        <el-table-column label="操作人" align="left" prop="operatorName" width="110" />
        <el-table-column label="操作时间" align="left" prop="makeDate" width="160" />
        <el-table-column label="说明" align="left" prop="yj" width="300" />
      </el-table>
      <template #footer>
        <el-button type="primary" @click="closeDialog">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { queryLcProcess } from '@/api/common/syscommon';
import { LcProcessShowVo } from '@/api/common/syscommon/types';

//属性
const props = defineProps({
  //流程类型
  lcSerialNo: { type: Number, default: 0 }
});
//加载
const loading = ref(false);
//驳回对话框
const dialogVisible = ref(false);
//流程数据
const dataList = ref<LcProcessShowVo[]>([]);
//状态
const state = reactive({
  directTagMap: {
    '0': { text: '保存', type: 'success' },
    '1': { text: '提交', type: 'default' },
    '-1': { text: '退回', type: 'danger' },
    '-2': { text: '一键撤回', type: 'danger' }
  }
});
/**
 * 显示流程轨迹
 * @param id
 */
const getList = async (id: string) => {
  loading.value = true;
  const res = await queryLcProcess({ dataId: id, lcSerialNo: props.lcSerialNo });
  dataList.value = res.rows;
  loading.value = false;
};

/**
 * 显示
 * @param id 流程ID
 */
const showDialog = async (id: any) => {
  dialogVisible.value = true;
  await getList(id);
};
/**
 * 关闭
 */
const closeDialog = () => {
  dataList.value = [];
  dialogVisible.value = false;
};

const getDirectTagType = (status: string) => {
  return state.directTagMap[status]?.type || 'default';
};

const getDirectTagText = (status: string) => {
  return state.directTagMap[status]?.text || '';
};

//暴露方法
defineExpose({ showDialog });
</script>

<style scoped lang="scss"></style>
