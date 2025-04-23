<template>
  <!-- 添加或修改角色抽屉 -->
  <el-drawer v-model="visibleProxy" :title="title" size="55%" append-to-body :close-on-click-modal="false" @closed="resetForm">
    <el-card shadow="always" class="box-card" style="padding-top: 10px">
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item prop="roleKey">
          <template #label>
            <span>
              <el-tooltip content="控制器中定义的权限字符，如：@SaCheckRole('admin')" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              权限字符
            </span>
          </template>
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event)">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event)">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect($event)">父子联动</el-checkbox>
          <el-tree
            ref="menuRef"
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :check-strictly="!form.menuCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          ></el-tree>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item>
      </el-form>
    </el-card>
    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { addRole, getRole, updateRole } from '@/api/system/role';
import { roleMenuTreeselect, treeselect as menuTreeselect } from '@/api/system/menu/index';
import { RoleForm, RoleVO } from '@/api/system/role/types';
import { MenuTreeOption } from '@/api/system/menu/types';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  editType: {
    type: String,
    default: 'add'
  },
  rowData: {
    type: Object as PropType<RoleVO | null>,
    default: null
  }
});

const emit = defineEmits(['update:visible', 'save']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable } = toRefs<any>(proxy?.useDict('sys_normal_disable'));

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 根据编辑类型决定标题
const title = computed(() => (props.editType === 'add' ? '添加角色' : '修改角色'));

// 按钮loading
const buttonLoading = ref(false);

// 表单ref
const roleFormRef = ref<ElFormInstance>();
const menuRef = ref<ElTreeInstance>();

// 菜单树选项
const menuOptions = ref<MenuTreeOption[]>([]);
const menuExpand = ref(false);
const menuNodeAll = ref(false);

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

// 表单校验规则
const rules = ref({
  roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
  roleKey: [{ required: true, message: '权限字符不能为空', trigger: 'blur' }],
  roleSort: [{ required: true, message: '角色顺序不能为空', trigger: 'blur' }]
});

/** 取消按钮 */
const cancel = () => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = () => {
  form.value = { ...initFormData };
  menuRef.value?.setCheckedKeys([]);
  menuExpand.value = false;
  menuNodeAll.value = false;
  roleFormRef.value?.resetFields();
};

/** 树权限（展开/折叠）*/
const handleCheckedTreeExpand = (value: boolean) => {
  let treeList = menuOptions.value;
  for (let i = 0; i < treeList.length; i++) {
    if (menuRef.value) {
      menuRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  }
};

/** 树权限（全选/全不选）*/
const handleCheckedTreeNodeAll = (value: any) => {
  menuRef.value?.setCheckedNodes(value ? (menuOptions.value as any) : []);
};

/** 树权限（父子联动）*/
const handleCheckedTreeConnect = (value: any) => {
  form.value.menuCheckStrictly = value;
};

/** 所有菜单节点数据 */
const getMenuAllCheckedKeys = (): any => {
  // 目前被选中的菜单节点
  let checkedKeys = menuRef.value?.getCheckedKeys();
  // 半选中的菜单节点
  let halfCheckedKeys = menuRef.value?.getHalfCheckedKeys();
  if (halfCheckedKeys) {
    checkedKeys?.unshift.apply(checkedKeys, halfCheckedKeys);
  }
  return checkedKeys;
};

/** 查询菜单树结构 */
const getMenuTreeselect = async () => {
  const res = await menuTreeselect();
  menuOptions.value = res.data;
};

/** 根据角色ID查询菜单树结构 */
const getRoleMenuTreeselect = async (roleId: string | number) => {
  const res = await roleMenuTreeselect(roleId);
  menuOptions.value = res.data.menus;
  return res.data;
};

/** 提交按钮 */
const submitForm = () => {
  roleFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        form.value.menuIds = getMenuAllCheckedKeys();
        if (form.value.roleId) {
          await updateRole(form.value);
        } else {
          await addRole(form.value);
        }
        proxy?.$modal.msgSuccess('操作成功');
        emit('save');
        visibleProxy.value = false;
      } finally {
        buttonLoading.value = false;
      }
    }
  });
};

// 监听抽屉可见性、编辑类型和行数据变化
watch(
  [() => props.visible, () => props.editType, () => props.rowData],
  async ([visible, type, data]) => {
    if (visible) {
      resetForm();

      // 获取菜单树数据
      await getMenuTreeselect();

      if (type === 'edit' && data) {
        // 修改操作
        const res = await getRole(data.roleId);
        Object.assign(form.value, res.data);
        form.value.roleSort = Number(form.value.roleSort);

        // 获取角色关联的菜单权限
        const menuRes = await getRoleMenuTreeselect(data.roleId);
        menuRes.checkedKeys.forEach((v) => {
          nextTick(() => {
            menuRef.value?.setChecked(v, true, false);
          });
        });
      }
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
</style>
