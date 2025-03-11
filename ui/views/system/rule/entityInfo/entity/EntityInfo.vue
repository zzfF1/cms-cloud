<template>
  <div>
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter"
                :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="search" style="padding-bottom: 0px;">
        <el-form ref="queryFormRef" :inline="true" :model="queryParams" label-width="75px" size="small">
          <el-row>
            <el-col :span="8">
              <el-form-item label="名称" prop="entityName">
                <el-input v-model="queryParams.entityName" clearable placeholder="请输入" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="包路径" prop="pkgName">
                <el-input v-model="queryParams.pkgName" clearable placeholder="请输入" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item>
                <el-button icon="Search" type="primary" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </transition>
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb5">
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:entityInfo:add']" plain type="primary" @click="handleAdd"> 新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:entityInfo:remove']" :disabled="multiple" plain type="danger"
                       @click="handleDelete()"> 删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:entityInfo:export']" plain type="warning" @click="handleExport"> 导出
            </el-button>
          </el-col>
        </el-row>
      </template>
      <div>
        <el-table ref="tableRef" v-loading="loading" :data="entityList" :height="tableHeight" border
                  @row-click="handleRowClick" @selection-change="handleSelectionChange">
          <el-table-column align="center" type="selection" width="55" />
          <el-table-column align="left" label="名称" min-width="30%" prop="entityName" />
          <el-table-column align="left" label="包路径" min-width="50%" prop="pkgName" />
          <el-table-column align="left" label="描述" min-width="20%" prop="entityDesc" />
          <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="100px;">
            <template #default="scope">
              <el-tooltip content="修改" placement="top">
                <el-button v-hasPermi="['system:entityInfo:edit']" icon="Edit" link type="primary"
                           @click="handleUpdate(scope.row)"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button v-hasPermi="['system:entityInfo:edit']" icon="Delete" link type="primary"
                           @click="handleDelete(scope.row)"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNum"
                    :total="total" />
      </div>
    </el-card>
    <EntityInfoModal ref="entityInfoModalRef" @getList="getList"></EntityInfoModal>
  </div>
</template>

<script lang="ts" setup>
import { EntityInfoQuery, EntityInfoVO } from '@/api/system/rule/entityInfo/types'
import { delEntityInfo, listEntityInfo } from '@/api/system/rule/entityInfo'
import EntityInfoModal from './EntityInfoModal.vue'

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const loading = ref(false);
const total = ref(0);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
// table高度
const tableRef = ref(null);
const tableHeight = ref();
const entityInfoModalRef = ref<InstanceType<typeof EntityInfoModal>>();
//数据
const entityList = ref<EntityInfoVO[]>([]);
const queryFormRef = ref<ElFormInstance>();
const queryParams = reactive<EntityInfoQuery>({
  pageNum: 1,
  pageSize: 10,
  entityName: undefined,
  entityDesc: undefined,
  entityIdentify: undefined,
  pkgName: undefined,
  isEffect: undefined
})
const emit = defineEmits<{
  (e: 'getEntityId', entityId: string): void;
  (e: 'getEntityName', entityName: string): void;
}>();

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/**
 * 查询方法
 */
const getList = async () => {
  loading.value = true;
  const res = await listEntityInfo(queryParams);
  entityList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/**
 * 单击事件
 * @param row 选择的行
 */
function handleRowClick(row: EntityInfoVO) {
  emit('getEntityId', row.entityId as string);
  emit('getEntityName', row.entityName as string);
}

const handleSelectionChange = (selection: EntityInfoVO[]) => {
  ids.value = (selection.map((item) => item.entityId).join(',') as any);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/**
 * 新增
 */
const handleAdd = () => {
  entityInfoModalRef.value?.showDialog(undefined);
}
/**
 * 修改
 * @param row
 */
const handleUpdate = (row?: EntityInfoVO) => {
  entityInfoModalRef.value?.showDialog(row?.entityId);
}

/** 删除按钮操作 */
const handleDelete = async (row?: EntityInfoVO) => {
  const _entityIds = row?.entityId || ids.value;
  await proxy?.$modal.confirm('是否确认删除数据？').finally(() => loading.value = false);
  await delEntityInfo(_entityIds);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('system/entityInfo/export', {
    ...queryParams
  }, `entityInfo_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
  // 设置表格初始高度为innerHeight-offsetTop-表格底部与浏览器底部距离85
  tableHeight.value = window.innerHeight - (tableRef.value as any).$el.offsetTop - 135;
  // 监听浏览器高度变化
  window.onresize = () => {
    tableHeight.value = window.innerHeight - (tableRef.value as any).$el.offsetTop - 135;
  };
})
</script>

<style lang="scss" scoped>
::-webkit-scrollbar-corner {
  background: transparent;
}
</style>
