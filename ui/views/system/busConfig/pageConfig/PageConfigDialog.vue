<template>
  <el-drawer :title="props.configData ? '编辑配置' : '新增配置'" v-model="dialogVisible" size="80%" :close-on-click-modal="false">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" class="config-form">
      <el-form-item label="代码" prop="code">
        <el-input v-model="formData.code" placeholder="请输入代码" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入名称" />
      </el-form-item>
      <el-form-item label="渠道" prop="branchType">
        <el-select v-model="formData.branchType" placeholder="请选择渠道">
          <el-option label="银保渠道" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="formData.sort" :precision="2" :step="0.01" />
      </el-form-item>
      <el-form-item label="说明" prop="remark">
        <el-input v-model="formData.remark" type="textarea" rows="3" placeholder="请输入说明" />
      </el-form-item>

      <!-- 配置选项卡 -->
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 查询配置 -->
        <el-tab-pane label="查询配置" name="query">
          <div class="table-actions">
            <el-button type="primary" @click="handleAddQuery">新增查询条件</el-button>
          </div>
          <el-table :data="formData.queryList" border>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="fieldType" label="类型" width="120">
              <template #default="{ row }">
                <el-select v-model="row.fieldType" placeholder="请选择类型">
                  <el-option label="字符串" value="STR" />
                  <el-option label="数字" value="NUM" />
                  <el-option label="日期" value="DATE" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="fieldPrefix" label="字段前缀">
              <template #default="{ row }">
                <el-input v-model="row.fieldPrefix" placeholder="请输入字段前缀" />
              </template>
            </el-table-column>
            <el-table-column prop="fieldName" label="字段名">
              <template #default="{ row }">
                <el-input v-model="row.fieldName" placeholder="请输入字段名" />
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="说明">
              <template #default="{ row }">
                <el-input v-model="row.remark" placeholder="请输入说明" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ $index }">
                <el-button type="danger" link @click="removeQuery($index)"> 删除 </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 表格配置 -->
        <el-tab-pane label="表格配置" name="table">
          <div class="table-actions">
            <el-button type="primary" @click="handleAddTable">新增表格列</el-button>
          </div>
          <el-table :data="formData.tableList" border>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="label" label="标题">
              <template #default="{ row }">
                <el-input v-model="row.label" placeholder="请输入标题" />
              </template>
            </el-table-column>
            <el-table-column prop="prop" label="字段名">
              <template #default="{ row }">
                <el-input v-model="row.prop" placeholder="请输入字段名" />
              </template>
            </el-table-column>
            <el-table-column prop="width" label="列宽">
              <template #default="{ row }">
                <el-input v-model="row.width" placeholder="请输入列宽" />
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序">
              <template #default="{ row }">
                <el-input-number v-model="row.sort" :precision="2" :step="0.01" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ $index }">
                <el-button type="danger" link @click="removeTable($index)"> 删除 </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading"> 确定 </el-button>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  modelValue: Boolean,
  configData: Object
});

const emit = defineEmits(['update:modelValue', 'success']);

// 表单ref
const formRef = ref(null);
const loading = ref(false);
const activeTab = ref('query');

// 表单数据
const formData = reactive({
  code: '',
  name: '',
  branchType: '',
  sort: 0,
  remark: '',
  queryList: [],
  tableList: []
});

// 表单校验规则
const rules = {
  code: [{ required: true, message: '请输入代码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  branchType: [{ required: true, message: '请选择渠道', trigger: 'change' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
};

// 对话框可见性双向绑定
const dialogVisible = ref(props.modelValue);
watch(
  () => props.modelValue,
  (val) => {
    dialogVisible.value = val;
  }
);
watch(dialogVisible, (val) => {
  emit('update:modelValue', val);
});

// 监听编辑数据变化
watch(
  () => props.configData,
  (val) => {
    if (val) {
      Object.keys(formData).forEach((key) => {
        formData[key] = val[key] || formData[key];
      });
    } else {
      Object.keys(formData).forEach((key) => {
        formData[key] = Array.isArray(formData[key]) ? [] : '';
      });
      formData.sort = 0;
    }
  },
  { immediate: true }
);

// 新增查询条件
const handleAddQuery = () => {
  formData.queryList.push({
    fieldType: 'STR',
    fieldPrefix: '',
    fieldName: '',
    remark: ''
  });
};

// 删除查询条件
const removeQuery = (index) => {
  formData.queryList.splice(index, 1);
};

// 新增表格列
const handleAddTable = () => {
  formData.tableList.push({
    label: '',
    prop: '',
    width: '',
    sort: formData.tableList.length + 1,
    align: 'left'
  });
};

// 删除表格列
const removeTable = (index) => {
  formData.tableList.splice(index, 1);
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    loading.value = true;

    // 构建提交数据
    const submitData = {
      ...formData,
      // 添加其他必要字段
      operator: 'admin', // 这里应该是当前登录用户
      makedate: new Date().toISOString().split('T')[0],
      maketime: new Date().toTimeString().slice(0, 8)
    };

    // 处理查询配置数据
    if (formData.queryList.length > 0) {
      submitData.queryList = formData.queryList.map((item, index) => ({
        ...item,
        query_order: index + 1,
        operator: submitData.operator,
        makedate: submitData.makedate,
        maketime: submitData.maketime
      }));
    }

    // 处理表格配置数据
    if (formData.tableList.length > 0) {
      submitData.tableList = formData.tableList.map((item) => ({
        ...item,
        operator: submitData.operator,
        makedate: submitData.makedate,
        maketime: submitData.maketime
      }));
    }

    // 如果是编辑模式，需要添加ID
    if (props.configData) {
      submitData.id = props.configData.id;
    }

    // 调用API保存数据
    // const res = await (props.configData
    //   ? api.updatePageConfig(submitData)
    //   : api.createPageConfig(submitData))

    ElMessage.success(props.configData ? '更新成功' : '创建成功');
    emit('success');
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error('提交失败，请检查表单');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.config-form {
  overflow-y: auto;
  padding: 0 20px;
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

.el-form-item {
  margin-bottom: 18px;
}
</style>
