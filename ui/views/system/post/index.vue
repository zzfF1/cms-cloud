<template>
  <div class="p-2">
    <el-row :gutter="20">
      <!-- 部门树 -->
      <el-col :lg="4" :xs="24" style="">
        <el-card shadow="hover">
          <el-input v-model="deptName" placeholder="请输入部门名称" prefix-icon="Search" clearable />
          <el-tree
            ref="deptTreeRef"
            class="mt-2"
            node-key="id"
            :data="deptOptions"
            :props="{ label: 'label', children: 'children' } as any"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            highlight-current
            default-expand-all
            @node-click="handleNodeClick"
          />
        </el-card>
      </el-col>
      <el-col :lg="20" :xs="24">
        <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
          <div v-show="showSearch" class="mb-[10px]">
            <el-card shadow="hover">
              <el-form ref="queryFormRef" :model="queryParams" :inline="true">
                <el-form-item label="岗位编码" prop="postCode">
                  <el-input v-model="queryParams.postCode" placeholder="请输入岗位编码" clearable @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="类别编码" prop="postCategory">
                  <el-input
                    v-model="queryParams.postCategory"
                    placeholder="请输入类别编码"
                    clearable
                    style="width: 200px"
                    @keyup.enter="handleQuery"
                  />
                </el-form-item>
                <el-form-item label="岗位名称" prop="postName">
                  <el-input v-model="queryParams.postName" placeholder="请输入岗位名称" clearable @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="部门" prop="deptId">
                  <el-tree-select
                    v-model="queryParams.deptId"
                    :data="deptOptions"
                    :props="{ value: 'id', label: 'label', children: 'children' } as any"
                    value-key="id"
                    placeholder="请选择部门"
                    check-strictly
                  />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                  <el-select v-model="queryParams.status" placeholder="岗位状态" clearable>
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
            <el-row :gutter="10" class="mb8">
              <el-col :span="1.5">
                <el-button v-hasPermi="['system:post:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button v-hasPermi="['system:post:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()"
                  >修改</el-button
                >
              </el-col>
              <el-col :span="1.5">
                <el-button v-hasPermi="['system:post:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">
                  删除
                </el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button v-hasPermi="['system:post:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
              </el-col>
              <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
            </el-row>
          </template>
          <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column v-if="false" label="岗位编号" align="center" prop="postId" />
            <el-table-column label="岗位编码" align="center" prop="postCode" />
            <el-table-column label="类别编码" align="center" prop="postCategory" />
            <el-table-column label="岗位名称" align="center" prop="postName" />
            <el-table-column label="部门" align="center" prop="deptName" />
            <el-table-column label="排序" align="center" prop="postSort" />
            <el-table-column label="状态" align="center" prop="status">
              <template #default="scope">
                <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="180">
              <template #default="scope">
                <span>{{ proxy.parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-tooltip content="修改" placement="top">
                  <el-button v-hasPermi="['system:post:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button v-hasPermi="['system:post:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            :total="total"
            @pagination="getList"
          />
        </el-card>

        <!-- 引入岗位表单组件 -->
        <post-form v-model:visible="formVisible" :edit-type="editType" :row-data="currentRow" :dept-options="deptOptions" @save="handleSave">
        </post-form>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Post" lang="ts">
import { delPost, listPost } from '@/api/system/post';
import { PostQuery, PostVO } from '@/api/system/post/types';
import { DeptTreeVO, DeptVO } from '@/api/system/dept/types';
import api from '@/api/system/user';
import PostForm from './postForm.vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

// 表单控制状态
const formVisible = ref(false);
const editType = ref('');
const currentRow = ref<PostVO | null>(null);

const postList = ref<PostVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<number | string>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const deptName = ref('');
const deptOptions = ref<DeptTreeVO[]>([]);
const deptTreeRef = ref<ElTreeInstance>();
const queryFormRef = ref<ElFormInstance>();

// 查询参数
const queryParams = reactive<PostQuery>({
  pageNum: 1,
  pageSize: 10,
  postCode: '',
  postName: '',
  postCategory: '',
  status: '',
  deptId: undefined,
  belongDeptId: undefined
});

/** 通过条件过滤节点  */
const filterNode = (value: string, data: any) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};

/** 根据名称筛选部门树 */
watchEffect(
  () => {
    deptTreeRef.value?.filter(deptName.value);
  },
  {
    flush: 'post' // watchEffect会在DOM挂载或者更新之前就会触发，此属性控制在DOM元素更新后运行
  }
);

/** 查询部门下拉树结构 */
const getTreeSelect = async () => {
  const res = await api.deptTreeSelect();
  deptOptions.value = res.data;
};

/** 节点单击事件 */
const handleNodeClick = (data: DeptVO) => {
  queryParams.belongDeptId = data.id;
  queryParams.deptId = undefined;
  handleQuery();
};

/** 查询岗位列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listPost(queryParams);
    postList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  if (queryParams.deptId) {
    queryParams.belongDeptId = undefined;
  }
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.pageNum = 1;
  queryParams.deptId = undefined;
  deptTreeRef.value?.setCurrentKey(undefined);
  /** 清空左边部门树选中值 */
  queryParams.belongDeptId = undefined;
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: PostVO[]) => {
  ids.value = selection.map((item) => item.postId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  currentRow.value = null;
  editType.value = 'add';
  formVisible.value = true;
};

/** 修改按钮操作 */
const handleUpdate = (row?: PostVO) => {
  if (row) {
    currentRow.value = row;
  } else {
    const selectedPost = postList.value.find((item) => item.postId === ids.value[0]);
    currentRow.value = selectedPost || null;
  }
  editType.value = 'edit';
  formVisible.value = true;
};

/** 保存成功后的处理 */
const handleSave = () => {
  getList();
};

/** 删除按钮操作 */
const handleDelete = async (row?: PostVO) => {
  const postIds = row?.postId || ids.value;
  await proxy?.$modal.confirm('是否确认删除岗位编号为"' + postIds + '"的数据项？');
  await delPost(postIds);
  await getList();
  proxy?.$modal.msgSuccess('删除成功');
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/post/export',
    {
      ...queryParams
    },
    `post_${new Date().getTime()}.xlsx`
  );
};

onMounted(() => {
  getTreeSelect(); // 初始化部门数据
  getList();
});
</script>
