<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="机构名称" prop="deptName">
              <el-input v-model="queryParams.deptName" placeholder="请输入机构名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="机构级别" prop="comgrade">
              <el-select v-model="queryParams.comgrade" placeholder="请选择机构级别" clearable>
                <el-option label="总公司" value="0" />
                <el-option label="省级分公司" value="1" />
                <el-option label="地市级中支公司" value="2" />
                <el-option label="县支公司" value="3" />
                <el-option label="营销服务部" value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="部门状态" clearable>
                <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10">
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:dept:add']" type="primary" plain icon="Plus" @click="handleAdd()">新增 </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="info" plain icon="Sort" @click="handleToggleExpandAll">展开/折叠</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table
        ref="deptTableRef"
        v-loading="loading"
        :data="deptList"
        row-key="deptId"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="isExpandAll"
      >
        <el-table-column prop="deptName" label="机构名称" width="260"></el-table-column>
        <el-table-column prop="manageCom" label="机构代码" width="120"></el-table-column>
        <el-table-column prop="shortname" align="center" label="机构简称" width="150"></el-table-column>
        <el-table-column prop="comgrade" align="center" label="机构级别" width="130">
          <template #default="scope">
            <span v-if="scope.row.comgrade === '0'">总公司</span>
            <span v-else-if="scope.row.comgrade === '1'">省级分公司</span>
            <span v-else-if="scope.row.comgrade === '2'">地市级中支公司</span>
            <span v-else-if="scope.row.comgrade === '3'">县支公司</span>
            <span v-else-if="scope.row.comgrade === '4'">营销服务部</span>
            <span v-else>{{ scope.row.comgrade }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="leaderName" align="center" label="负责人" width="120"></el-table-column>
        <el-table-column prop="orderNum" align="center" label="排序" width="80"></el-table-column>
        <el-table-column prop="status" align="center" label="状态" width="100">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ proxy.parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" align="center" label="操作">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button v-hasPermi="['system:dept:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" />
            </el-tooltip>
            <el-tooltip content="新增" placement="top">
              <el-button v-hasPermi="['system:dept:add']" link type="primary" icon="Plus" @click="handleAdd(scope.row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button v-hasPermi="['system:dept:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 引入部门表单组件 -->
    <dept-form v-model:visible="formVisible" :edit-type="editType" :row-data="currentRow" @save="handleSave"> </dept-form>
  </div>
</template>

<script setup name="Dept" lang="ts">
import { delDept, listDept, listDeptLdcom } from '@/api/system/dept';
import { DeptQuery, DeptVO } from '@/api/system/dept/types';
import DeptForm from './deptForm.vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

// 表单控制状态
const formVisible = ref(false);
const editType = ref('');
const currentRow = ref<DeptVO | null>(null);

// 列表数据
const deptList = ref<DeptVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const isExpandAll = ref(true);

// 是否使用集成Ldcom API
const useLdcomApi = ref(true);

// 查询表单ref
const queryFormRef = ref<ElFormInstance>();
const deptTableRef = ref<ElTableInstance>();

// 查询参数
const queryParams = reactive<DeptQuery>({
  pageNum: 1,
  pageSize: 10,
  deptName: undefined,
  comgrade: undefined,
  status: undefined
});

/** 查询部门列表 */
const getList = async () => {
  loading.value = true;
  try {
    let res;
    if (useLdcomApi.value) {
      // 使用集成Ldcom的API
      res = await listDeptLdcom(queryParams);
    } else {
      // 使用原始API
      res = await listDept(queryParams);
    }
    const data = proxy?.handleTree<DeptVO>(res.data, 'deptId');
    if (data) {
      deptList.value = data;
    }
  } finally {
    loading.value = false;
  }
};

/** 搜索按钮操作 */
const handleQuery = () => {
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};

/** 展开/折叠操作 */
const handleToggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value;
  toggleExpandAll(deptList.value, isExpandAll.value);
};

/** 展开/折叠所有 */
const toggleExpandAll = (data: DeptVO[], status: boolean) => {
  data.forEach((item) => {
    deptTableRef.value?.toggleRowExpansion(item, status);
    if (item.children && item.children.length > 0) toggleExpandAll(item.children, status);
  });
};

/** 新增按钮操作 */
const handleAdd = (row?: DeptVO) => {
  currentRow.value = row || null;
  editType.value = 'add';
  formVisible.value = true;
};

/** 修改按钮操作 */
const handleUpdate = (row: DeptVO) => {
  currentRow.value = row;
  editType.value = 'edit';
  formVisible.value = true;
};

/** 保存成功后的处理 */
const handleSave = () => {
  getList();
};

/** 删除按钮操作 */
const handleDelete = async (row: DeptVO) => {
  await proxy?.$modal.confirm('是否确认删除名称为"' + row.deptName + '"的数据项?');
  await delDept(row.deptId);
  await getList();
  proxy?.$modal.msgSuccess('删除成功');
};

onMounted(() => {
  getList();
});
</script>
