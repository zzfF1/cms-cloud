<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="权限字符" prop="roleKey">
              <el-input v-model="queryParams.roleKey" placeholder="请输入权限字符" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="角色状态" clearable>
                <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="创建时间" style="width: 308px">
              <el-date-picker
                v-model="dateRange"
                value-format="YYYY-MM-DD HH:mm:ss"
                type="daterange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
              ></el-date-picker>
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
            <el-button v-hasPermi="['system:role:add']" type="primary" plain icon="Plus" @click="handleAdd()">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:role:edit']" type="success" plain :disabled="single" icon="Edit" @click="handleUpdate()">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:role:delete']" type="danger" plain :disabled="ids.length === 0" @click="handleDelete()">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:role:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table ref="roleTableRef" v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="false" label="角色编号" prop="roleId" width="120" />
        <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" width="150" />
        <el-table-column label="权限字符" prop="roleKey" :show-overflow-tooltip="true" width="200" />
        <el-table-column label="显示顺序" prop="roleSort" width="100" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime">
          <template #default="scope">
            <span>{{ proxy.parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
            <el-tooltip v-if="scope.row.roleId !== 1" content="修改" placement="top">
              <el-button v-hasPermi="['system:role:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip v-if="scope.row.roleId !== 1" content="删除" placement="top">
              <el-button v-hasPermi="['system:role:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip v-if="scope.row.roleId !== 1" content="数据权限" placement="top">
              <el-button v-hasPermi="['system:role:edit']" link type="primary" icon="CircleCheck" @click="handleDataScope(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip v-if="scope.row.roleId !== 1" content="分配用户" placement="top">
              <el-button v-hasPermi="['system:role:edit']" link type="primary" icon="User" @click="handleAuthUser(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 引入角色表单组件 -->
    <role-form v-model:visible="formVisible" :edit-type="editType" :row-data="currentRow" @save="handleSave"> </role-form>

    <!-- 引入数据权限表单组件 -->
    <role-data-scope-form v-model:visible="dataScopeVisible" :role-id="currentRoleId" @save="handleSave"> </role-data-scope-form>
  </div>
</template>

<script setup name="Role" lang="ts">
import { changeRoleStatus, delRole, listRole } from '@/api/system/role';
import { RoleQuery, RoleVO } from '@/api/system/role/types';
import RoleForm from './roleForm.vue';
import RoleDataScopeForm from './roleDataScopeForm.vue';

const router = useRouter();
const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

// 表单控制状态
const formVisible = ref(false);
const dataScopeVisible = ref(false);
const editType = ref('');
const currentRow = ref<RoleVO | null>(null);
const currentRoleId = ref<string | number>('');

const roleList = ref<RoleVO[]>();
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref<[DateModelType, DateModelType]>(['', '']);

const queryFormRef = ref<ElFormInstance>();

// 查询参数
const queryParams = reactive<RoleQuery>({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: '',
  status: ''
});

/**
 * 查询角色列表
 */
const getList = () => {
  loading.value = true;
  listRole(proxy?.addDateRange(queryParams, dateRange.value)).then((res) => {
    roleList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
};

/**
 * 搜索按钮操作
 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

/** 重置 */
const resetQuery = () => {
  dateRange.value = ['', ''];
  queryFormRef.value?.resetFields();
  handleQuery();
};

/**删除按钮操作 */
const handleDelete = async (row?: RoleVO) => {
  const roleids = row?.roleId || ids.value;
  try {
    await proxy?.$modal.confirm('是否确认删除角色数据？');
    await delRole(roleids);
    getList();
    proxy?.$modal.msgSuccess('删除成功');
  } catch (error) {
    console.error('删除操作取消或失败', error);
  }
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/role/export',
    {
      ...queryParams
    },
    `role_${new Date().getTime()}.xlsx`
  );
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: RoleVO[]) => {
  ids.value = selection.map((item: RoleVO) => item.roleId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 角色状态修改 */
const handleStatusChange = async (row: RoleVO) => {
  const text = row.status === '0' ? '启用' : '停用';
  try {
    await proxy?.$modal.confirm('确认要"' + text + '""' + row.roleName + '"角色吗?');
    await changeRoleStatus(row.roleId, row.status);
    proxy?.$modal.msgSuccess(text + '成功');
  } catch {
    row.status = row.status === '0' ? '1' : '0';
  }
};

/** 分配用户 */
const handleAuthUser = (row: RoleVO) => {
  router.push('/system/role-auth/user/' + row.roleId);
};

/** 新增按钮操作 */
const handleAdd = () => {
  currentRow.value = null;
  editType.value = 'add';
  formVisible.value = true;
};

/** 修改按钮操作 */
const handleUpdate = (row?: RoleVO) => {
  if (row) {
    currentRow.value = row;
  } else {
    const selectedRole = roleList.value?.find((item) => item.roleId === ids.value[0]);
    currentRow.value = selectedRole || null;
  }
  editType.value = 'edit';
  formVisible.value = true;
};

/** 分配数据权限操作 */
const handleDataScope = (row: RoleVO) => {
  currentRoleId.value = row.roleId;
  dataScopeVisible.value = true;
};

/** 保存成功后的处理 */
const handleSave = () => {
  getList();
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.el-card {
  margin-bottom: 10px;
}
</style>
