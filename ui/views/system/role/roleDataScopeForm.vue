<template>
  <!-- 分配角色数据权限抽屉 -->
  <el-drawer v-model="visibleProxy" title="分配数据权限" size="55%" append-to-body :close-on-click-modal="false" @closed="resetForm">
    <el-card shadow="always" class="box-card" style="padding-top: 10px">
      <el-form ref="dataScopeRef" :model="form" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="form.roleKey" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限范围">
          <el-select v-model="form.dataScope" @change="dataScopeSelectChange">
            <el-option v-for="item in dataScopeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-show="form.dataScope === '2'" label="数据权限">
          <el-checkbox v-model="deptExpand" @change="handleCheckedTreeExpand($event)">展开/折叠</el-checkbox>
          <el-checkbox v-model="deptNodeAll" @change="handleCheckedTreeNodeAll($event)">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.deptCheckStrictly" @change="handleCheckedTreeConnect($event)">父子联动</el-checkbox>
          <el-tree
            ref="deptRef"
            class="tree-border"
            :data="deptOptions"
            show-checkbox
            default-expand-all
            node-key="id"
            :check-strictly="!form.deptCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          ></el-tree>
        </el-form-item>
      </el-form>
    </el-card>
    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitDataScope">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { getRole, dataScope, deptTreeSelect } from '@/api/system/role';
import { RoleForm, RoleVO, DeptTreeOption } from '@/api/system/role/types';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  roleId: {
    type: [Number, String],
    default: ''
  }
});

const emit = defineEmits(['update:visible', 'save']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 按钮loading
const buttonLoading = ref(false);

// 表单ref
const dataScopeRef = ref<ElFormInstance>();
const deptRef = ref<ElTreeInstance>();

// 部门树选项
const deptOptions = ref<DeptTreeOption[]>([]);
const deptExpand = ref(true);
const deptNodeAll = ref(false);

// 数据范围选项
const dataScopeOptions = ref([
  { value: '1', label: '全部数据权限' },
  { value: '2', label: '自定数据权限' },
  { value: '3', label: '本部门数据权限' },
  { value: '4', label: '本部门及以下数据权限' },
  { value: '5', label: '仅本人数据权限' },
  { value: '6', label: '部门及以下或本人数据权限' }
]);

// 初始化表单数据
const initFormData: RoleForm = {
  roleId: undefined,
  roleSort: 1,
  status: '0',
  roleName: '',
  roleKey: '',
  menuCheckStrictly: true,
  deptCheckStrictly: true,
  remark: '',
  dataScope: '1',
  menuIds: [],
  deptIds: []
};

// 表单数据
const form = ref<RoleForm>({ ...initFormData });

/** 取消按钮 */
const cancel = () => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = () => {
  form.value = { ...initFormData };
  deptRef.value?.setCheckedKeys([]);
  deptExpand.value = true;
  deptNodeAll.value = false;
  dataScopeRef.value?.resetFields();
};

/** 选择角色权限范围触发 */
const dataScopeSelectChange = (value: string) => {
  if (value !== '2') {
    deptRef.value?.setCheckedKeys([]);
  }
};

/** 树权限（展开/折叠）*/
const handleCheckedTreeExpand = (value: boolean) => {
  let treeList = deptOptions.value;
  for (let i = 0; i < treeList.length; i++) {
    if (deptRef.value) {
      deptRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  }
};

/** 树权限（全选/全不选）*/
const handleCheckedTreeNodeAll = (value: any) => {
  deptRef.value?.setCheckedNodes(value ? (deptOptions.value as any) : []);
};

/** 树权限（父子联动）*/
const handleCheckedTreeConnect = (value: any) => {
  form.value.deptCheckStrictly = value;
};

/** 所有部门节点数据 */
const getDeptAllCheckedKeys = (): any => {
  // 目前被选中的部门节点
  let checkedKeys = deptRef.value?.getCheckedKeys();
  // 半选中的部门节点
  let halfCheckedKeys = deptRef.value?.getHalfCheckedKeys();
  if (halfCheckedKeys) {
    checkedKeys?.unshift.apply(checkedKeys, halfCheckedKeys);
  }
  return checkedKeys;
};

/** 根据角色ID查询部门树结构 */
const getRoleDeptTreeSelect = async (roleId: string | number) => {
  const res = await deptTreeSelect(roleId);
  deptOptions.value = res.data.depts;
  return res.data;
};

/** 提交按钮（数据权限）*/
const submitDataScope = async () => {
  buttonLoading.value = true;
  try {
    if (form.value.roleId) {
      form.value.deptIds = getDeptAllCheckedKeys();
      await dataScope(form.value);
      proxy?.$modal.msgSuccess('修改成功');
      emit('save');
      visibleProxy.value = false;
    }
  } finally {
    buttonLoading.value = false;
  }
};

// 监听抽屉可见性和角色ID变化
watch(
  [() => props.visible, () => props.roleId],
  async ([visible, roleId]) => {
    if (visible && roleId) {
      resetForm();

      // 获取角色信息
      const response = await getRole(roleId);
      Object.assign(form.value, response.data);

      // 获取角色关联的部门权限
      const res = await getRoleDeptTreeSelect(roleId);

      // 等待DOM更新后设置选中的部门节点
      await nextTick(() => {
        deptRef.value?.setCheckedKeys(res.checkedKeys);
      });
    }
  },
  { immediate: true }
);
</script>

<style scoped>
:deep(.tree-border) {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #fff;
  border-radius: 4px;
  padding: 10px;
  height: 300px;
  overflow: auto;
}
:deep(.el-select) {
  width: 100%;
}
</style>
