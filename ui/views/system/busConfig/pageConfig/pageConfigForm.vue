<template>
  <!-- 添加或修改界面配置抽屉 -->
  <el-drawer
    v-model="visibleProxy"
    :title="title"
    size="95%"
    append-to-body
    :close-on-click-modal="false"
    @closed="resetForm"
    :destroy-on-close="true"
  >
    <!-- 添加全局加载遮罩 -->
    <el-loading v-model:full-screen="fullScreenLoading" :text="loadingText" background="rgba(255, 255, 255, 0.7)" />

    <!-- 使用flex布局使卡片填充整个抽屉高度 -->
    <div class="drawer-container">
      <el-card shadow="always" class="box-card" v-loading="formLoading" element-loading-text="数据加载中...">
        <el-form ref="pageConfigFormRef" :model="form" :rules="rules" label-width="120px" class="form-container">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="代码" prop="code">
                <el-input v-model="form.code" :disabled="form.id != null" placeholder="请输入代码" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入名称" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="渠道" prop="branchType">
                <el-select v-model="form.branchType" placeholder="请选择渠道" style="width: 100%">
                  <el-option label="银保渠道" value="3" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="排序" prop="sort">
                <el-input-number v-model="form.sort" :precision="2" :step="0.01" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="说明" prop="remark">
            <el-input v-model="form.remark" type="textarea" rows="2" placeholder="请输入说明" />
          </el-form-item>

          <!-- 配置选项卡，使用flex布局让选项卡内容填充剩余空间 -->
          <el-tabs v-model="activeTab" type="border-card" class="flex-tabs">
            <!-- 查询配置 -->
            <el-tab-pane label="查询配置" name="query" class="tab-content">
              <div class="table-container">
                <div class="table-actions">
                  <el-button type="primary" @click="handleAddQuery">新增查询条件</el-button>
                </div>
                <!-- 移除max-height限制，让表格自动填充 -->
                <el-table :data="form.queryList" border size="small" class="flex-table">
                  <el-table-column type="index" label="序号" width="60" align="center" fixed />
                  <el-table-column prop="fieldType" label="类型" width="100" fixed>
                    <template #default="{ row }">
                      <el-select v-model="row.fieldType" placeholder="请选择类型" size="small">
                        <el-option label="字符串" value="STR" />
                        <el-option label="数字" value="NUM" />
                        <el-option label="日期" value="DATE" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column prop="fieldPrefix" label="字段前缀" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.fieldPrefix" placeholder="请输入字段前缀" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="alias" label="别名" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.alias" placeholder="请输入别名" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="fieldName" label="字段名" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.fieldName" placeholder="请输入字段名" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="condOperator" label="条件" width="120">
                    <template #default="{ row }">
                      <el-select v-model="row.condOperator" placeholder="请选择条件" size="small">
                        <el-option label="等于" value="EQ" />
                        <el-option label="包含" value="LIKE" />
                        <el-option label="右包含" value="LIKE_RIGHT" />
                        <el-option label="左包含" value="LIKE_LEFT" />
                        <el-option label="在...之中" value="IN" />
                        <el-option label="大于" value="GT" />
                        <el-option label="小于" value="LT" />
                        <el-option label="大于等于" value="GE" />
                        <el-option label="小于等于" value="LE" />
                        <el-option label="不等于" value="NE" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column prop="queryOrder" label="排序" width="100">
                    <template #default="{ row }">
                      <el-input-number v-model="row.queryOrder" :precision="0" :min="0" controls-position="right" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="specialCode" label="特殊代码" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.specialCode" placeholder="请输入特殊代码" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="remark" label="说明" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.remark" placeholder="请输入说明" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="type" label="类型" width="120">
                    <template #default="{ row }">
                      <el-select v-model="row.type" placeholder="请选择类型" size="small">
                        <el-option label="前台查询条件" value="0" />
                        <el-option label="后台追加条件" value="1" />
                        <el-option label="排序字段" value="2" />
                        <el-option label="高级查询" value="3" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column prop="defaultValue" label="默认值" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.defaultValue" placeholder="请输入默认值" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="componentType" label="组件类型" width="120">
                    <template #default="{ row }">
                      <el-select v-model="row.componentType" placeholder="请选择组件类型" size="small">
                        <el-option label="文本输入框" value="input" />
                        <el-option label="数字输入框" value="number" />
                        <el-option label="单选下拉框" value="select" />
                        <el-option label="多选下拉框" value="multiSelect" />
                        <el-option label="日期选择器" value="date" />
                        <el-option label="弹窗选择器" value="popup" />
                        <el-option label="树形选择器" value="tree" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column prop="dictType" label="字典类型" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.dictType" placeholder="请输入字典类型" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="dataSource" label="数据源类型" width="120">
                    <template #default="{ row }">
                      <el-select v-model="row.dataSource" placeholder="请选择数据源类型" size="small">
                        <el-option label="字典" value="dict" />
                        <el-option label="自定义" value="custom" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column prop="beanName" label="Bean名称" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.beanName" placeholder="请输入Bean名称" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="dependencyField" label="依赖字段" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.dependencyField" placeholder="请输入依赖字段" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="placeholder" label="占位提示文字" width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.placeholder" placeholder="请输入占位提示文字" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="optionsConfig" label="选项配置" width="120">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="handleEditOptions(row)">编辑选项</el-button>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="120" align="center" fixed="right">
                    <template #default="{ $index }">
                      <el-button type="danger" link @click="removeQuery($index)"> 删除 </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>

            <!-- 表格配置 -->
            <el-tab-pane label="表格配置" name="table" class="tab-content">
              <div class="table-container">
                <div class="table-actions">
                  <el-button type="primary" @click="handleAddTable">新增表格列</el-button>
                </div>
                <el-table :data="form.tableList" border class="flex-table">
                  <el-table-column type="index" label="序号" width="60" align="center" />
                  <el-table-column prop="label" label="标题">
                    <template #default="{ row }">
                      <el-input v-model="row.label" placeholder="请输入标题" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="prop" label="字段名">
                    <template #default="{ row }">
                      <el-input v-model="row.prop" placeholder="请输入字段名" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="width" label="列宽">
                    <template #default="{ row }">
                      <el-input v-model="row.width" placeholder="请输入列宽" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="sort" label="排序">
                    <template #default="{ row }">
                      <el-input-number v-model="row.sort" :precision="2" :step="0.01" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="120" align="center">
                    <template #default="{ $index }">
                      <el-button type="danger" link @click="removeTable($index)"> 删除 </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-form>
      </el-card>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-drawer>

  <!-- 选项配置编辑对话框 -->
  <el-dialog v-model="optionsDialogVisible" title="编辑选项配置" width="600px" append-to-body>
    <el-form label-width="100px">
      <el-form-item label="选项配置">
        <el-input v-model="currentOptionsConfig" type="textarea" :rows="15" placeholder="请输入JSON格式的选项配置" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="optionsDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveOptionsConfig">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { getPageConfig, addPageConfig, updatePageConfig } from '@/api/system/pageConfig';
import { PageConfigForm, PageConfigVO, PageConfigQueryForm, PageConfigTabForm } from '@/api/system/pageConfig/types';

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
    type: Object as PropType<PageConfigVO | null>,
    default: null
  }
});

const emit = defineEmits(['update:visible', 'save']);

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

// 可见性的双向绑定
const visibleProxy = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

// 根据编辑类型决定标题
const title = computed(() => (props.editType === 'add' ? '添加界面配置' : '修改界面配置'));

// 按钮loading
const buttonLoading = ref(false);
// 表单加载状态
const formLoading = ref(false);
// 全屏加载状态
const fullScreenLoading = ref(false);
// 加载文本
const loadingText = ref('数据加载中，请稍候...');

// 当前激活的选项卡
const activeTab = ref('query');

// 表单ref
const pageConfigFormRef = ref<ElFormInstance>();

// 初始化表单数据
const initFormData: PageConfigForm = {
  id: undefined,
  code: undefined,
  name: undefined,
  branchType: undefined,
  sort: 0,
  remark: undefined,
  queryList: [],
  tableList: []
};

// 表单数据
const form = ref<PageConfigForm>({ ...initFormData });

// 表单校验规则
const rules = ref({
  code: [{ required: true, message: '代码不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
  branchType: [{ required: true, message: '渠道不能为空', trigger: 'change' }],
  sort: [{ required: true, message: '排序不能为空', trigger: 'blur' }]
});

/** 取消按钮 */
const cancel = () => {
  visibleProxy.value = false;
};

/** 表单重置 */
const resetForm = () => {
  form.value = { ...initFormData };
  pageConfigFormRef.value?.resetFields();
};

// 选项配置对话框控制
const optionsDialogVisible = ref(false);
const currentOptionsConfig = ref('');
const currentQueryRow = ref<any>(null);

// 打开选项配置编辑对话框
const handleEditOptions = (row: any) => {
  currentQueryRow.value = row;
  currentOptionsConfig.value = row.optionsConfig || '[]';
  optionsDialogVisible.value = true;
};

// 保存选项配置
const saveOptionsConfig = () => {
  try {
    // 尝试解析JSON，验证格式是否正确
    JSON.parse(currentOptionsConfig.value);
    if (currentQueryRow.value) {
      currentQueryRow.value.optionsConfig = currentOptionsConfig.value;
    }
    optionsDialogVisible.value = false;
  } catch (error) {
    proxy?.$modal.msgError('选项配置JSON格式错误，请检查');
  }
};

// 新增查询条件
const handleAddQuery = () => {
  if (!form.value.queryList) {
    form.value.queryList = [];
  }
  const newQuery: PageConfigQueryForm = {
    fieldType: 'STR',
    fieldPrefix: '',
    alias: '',
    fieldName: '',
    condOperator: 'EQ',
    queryOrder: form.value.queryList.length + 1,
    specialCode: '',
    remark: '',
    type: '0',
    defaultValue: '',
    componentType: 'input',
    dictType: '',
    dataSource: '',
    beanName: '',
    dependencyField: '',
    optionsConfig: '',
    placeholder: ''
  };
  form.value.queryList.push(newQuery);
};

// 删除查询条件
const removeQuery = (index: number) => {
  if (form.value.queryList) {
    form.value.queryList.splice(index, 1);
  }
};

// 新增表格列
const handleAddTable = () => {
  if (!form.value.tableList) {
    form.value.tableList = [];
  }
  const newTable: PageConfigTabForm = {
    label: '',
    prop: '',
    width: '',
    sort: form.value.tableList.length + 1
  };
  form.value.tableList.push(newTable);
};

// 删除表格列
const removeTable = (index: number) => {
  if (form.value.tableList) {
    form.value.tableList.splice(index, 1);
  }
};

/** 提交按钮 */
const submitForm = () => {
  pageConfigFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        if (form.value.id) {
          await updatePageConfig(form.value);
        } else {
          await addPageConfig(form.value);
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

      if (type === 'edit' && data) {
        // 修改操作时显示加载中
        formLoading.value = true;
        fullScreenLoading.value = true;
        try {
          // 修改操作
          const res = await getPageConfig(data.id);
          Object.assign(form.value, res.data);
        } catch (error) {
          console.error('获取数据失败:', error);
          proxy?.$modal.msgError('获取数据失败');
        } finally {
          // 数据加载完成后隐藏加载中状态
          setTimeout(() => {
            formLoading.value = false;
            fullScreenLoading.value = false;
          }, 500); // 添加略微延迟以确保DOM更新
        }
      }
    }
  },
  { immediate: true }
);
</script>

<style scoped>
:deep(.el-select) {
  width: 100%;
}

.table-actions {
  margin-bottom: 15px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-tabs__content) {
  padding: 20px;
}

:deep(.el-table) {
  margin-bottom: 20px;
}

:deep(.el-table .cell) {
  padding-left: 8px;
  padding-right: 8px;
}

:deep(.el-table--border th:first-child .cell, .el-table--border td:first-child .cell) {
  padding-left: 8px;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-dialog .el-textarea) {
  font-family: monospace;
  font-size: 14px;
}
</style>
