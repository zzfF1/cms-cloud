<template>
  <el-drawer
    v-model="visible"
    :title="title"
    size="95%"
    append-to-body
    :close-on-click-modal="false"
    @closed="handleClosed"
    :destroy-on-close="true"
    class="template-drawer"
  >
    <!-- 全局加载遮罩 -->
    <el-loading v-model:full-screen="fullScreenLoading" :text="loadingText" background="rgba(255, 255, 255, 0.7)" />

    <div class="drawer-container">
      <!-- 固定在顶部的表单区域 -->
      <div class="template-header">
        <el-card shadow="hover" class="header-card">
          <template #header>
            <div class="card-header">
              <span><i class="el-icon-document"></i> 模板基本信息</span>
            </div>
          </template>

          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" class="base-form">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="模板名称" prop="name">
                  <el-input v-model="formData.name" placeholder="请输入模板名称" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="代码" prop="code">
                  <el-input v-model="formData.code" placeholder="请输入代码" :disabled="Boolean(formData.id)" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="渠道" prop="branchType">
                  <el-select v-model="formData.branchType" placeholder="请选择渠道" style="width: 100%" clearable>
                    <el-option label="渠道1" value="01" />
                    <el-option label="渠道2" value="02" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="说明" prop="remark">
              <el-input v-model="formData.remark" type="textarea" rows="2" placeholder="请输入说明" />
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- Sheet配置部分 -->
      <div class="template-content">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span><i class="el-icon-s-grid"></i> Sheet配置</span>
              <el-button type="primary" size="small" @click="handleAddSheet" plain>
                <el-icon><Plus /></el-icon> 新增Sheet
              </el-button>
            </div>
          </template>

          <!-- Sheet选项卡 -->
          <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick" class="sheet-tabs">
            <el-tab-pane v-for="(sheet, index) in sheetList" :key="index" :name="String(index)">
              <template #label>
                <div class="custom-tab-label">
                  <span>{{ sheet.name || 'Sheet' + (index + 1) }}</span>
                  <el-button v-if="sheetList.length > 1" type="danger" size="small" @click.stop="handleRemoveSheet(index)" circle plain>
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
              </template>

              <div class="sheet-config">
                <div class="sheet-header">
                  <el-input v-model="sheet.name" placeholder="请输入Sheet名称" style="width: 200px" clearable prefix-icon="el-icon-document" />
                  <el-button type="primary" @click="handleAddField(index)" plain>
                    <el-icon><Plus /></el-icon> 新增字段
                  </el-button>
                </div>

                <!-- 字段表格 -->
                <el-table
                  :data="sheet.fields"
                  border
                  stripe
                  highlight-current-row
                  class="field-table"
                  size="default"
                  :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
                >
                  <el-table-column type="index" label="序号" width="60" align="center" />

                  <el-table-column prop="title" label="标题" min-width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.title" placeholder="请输入标题" clearable />
                    </template>
                  </el-table-column>

                  <el-table-column prop="fieldRequired" label="必填" width="80" align="center">
                    <template #default="{ row }">
                      <el-switch v-model="row.fieldRequired" :active-value="1" :inactive-value="0" />
                    </template>
                  </el-table-column>

                  <el-table-column prop="dataType" label="字段类型" width="120">
                    <template #default="{ row }">
                      <el-select v-model="row.dataType" placeholder="请选择类型" style="width: 100%">
                        <el-option label="字符串" value="0" />
                        <el-option label="整数" value="1" />
                        <el-option label="2位小数" value="2" />
                        <el-option label="4位小数" value="3" />
                        <el-option label="日期" value="4" />
                        <el-option label="序号" value="9" />
                      </el-select>
                    </template>
                  </el-table-column>

                  <el-table-column prop="fillSm" label="填写说明" min-width="120">
                    <template #default="{ row }">
                      <el-input v-model="row.fillSm" placeholder="请输入填写说明" clearable />
                    </template>
                  </el-table-column>

                  <el-table-column prop="width" label="列宽" width="120">
                    <template #default="{ row }">
                      <el-input-number v-model="row.width" :min="1000" :max="10000" :step="500" style="width: 100%" controls-position="right" />
                    </template>
                  </el-table-column>

                  <el-table-column label="下拉配置" min-width="200">
                    <template #default="{ row }">
                      <el-row :gutter="5">
                        <el-col :span="12">
                          <el-input v-model="row.downSelHandler" placeholder="处理类" size="small" />
                        </el-col>
                        <el-col :span="12">
                          <el-input v-model="row.parameter" placeholder="参数" size="small" />
                        </el-col>
                      </el-row>
                    </template>
                  </el-table-column>

                  <el-table-column prop="sort" label="排序" width="100">
                    <template #default="{ row }">
                      <el-input-number
                        v-model="row.sort"
                        :min="1"
                        :max="99"
                        :precision="2"
                        :step="0.01"
                        style="width: 100%"
                        controls-position="right"
                      />
                    </template>
                  </el-table-column>

                  <el-table-column label="操作" width="80" align="center" fixed="right">
                    <template #default="{ $index }">
                      <el-button type="danger" size="small" @click="handleRemoveField(Number(activeTab), $index)" circle>
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>

    <!-- 抽屉底部操作栏 -->
    <template #footer>
      <div class="drawer-footer">
        <el-button @click="cancel" plain>取 消</el-button>
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive, defineProps, defineEmits, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { SheetConfig, ImportConfigItemVO, ImportConfigVO } from '@/api/system/importConfig/types';
import { Plus, Delete, Close } from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '模板配置'
  },
  data: {
    type: Object as () => Partial<ImportConfigVO>,
    default: () => ({})
  },
  sheetData: {
    type: Array as () => SheetConfig[],
    default: () => []
  }
});

const emit = defineEmits(['update:modelValue', 'submit']);

// 表单ref
const formRef = ref<FormInstance>();

// 抽屉显示控制
const visible = ref(false);

// 按钮loading
const buttonLoading = ref(false);
// 表单加载状态
const formLoading = ref(false);
// 全屏加载状态
const fullScreenLoading = ref(false);
// 加载文本
const loadingText = ref('数据加载中，请稍候...');

// 当前激活的标签页
const activeTab = ref('0');

// 表单数据 - 模板基本信息
const formData = reactive<Partial<ImportConfigVO>>({
  id: undefined,
  name: '',
  code: '',
  branchType: '',
  remark: ''
});

// 表单校验规则
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入代码', trigger: 'blur' }],
  branchType: [{ required: true, message: '请选择渠道', trigger: 'change' }]
});

// sheet列表数据
const sheetList = ref<SheetConfig[]>([
  {
    name: '',
    fields: []
  }
]);

// 监听父组件传入的显示状态
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val;
    if (val) {
      // 显示加载状态
      formLoading.value = true;
      fullScreenLoading.value = true;

      // 模拟加载延迟
      setTimeout(() => {
        formLoading.value = false;
        fullScreenLoading.value = false;
      }, 500);
    }
  }
);

// 监听本地显示状态
watch(
  () => visible.value,
  (val) => {
    emit('update:modelValue', val);
  }
);

// 监听编辑数据 - 模板基本信息
watch(
  () => props.data,
  (val) => {
    if (val) {
      Object.assign(formData, val);
    }
  },
  { deep: true, immediate: true }
);

// 监听配置数据 - Sheet信息
watch(
  () => props.sheetData,
  (val) => {
    if (val && val.length > 0) {
      // 深拷贝数据
      sheetList.value = JSON.parse(JSON.stringify(val));
    }
  },
  { deep: true, immediate: true }
);

// 新增Sheet
const handleAddSheet = () => {
  sheetList.value.push({
    name: '',
    fields: []
  });
  activeTab.value = String(sheetList.value.length - 1);
};

// 删除Sheet
const handleRemoveSheet = (index: number) => {
  if (sheetList.value.length <= 1) {
    return;
  }

  ElMessage.success(`已删除 ${sheetList.value[index].name || 'Sheet' + (index + 1)}`);
  sheetList.value.splice(index, 1);

  // 如果删除的是当前选中的标签页，切换到第一个标签页
  if (Number(activeTab.value) === index) {
    activeTab.value = '0';
  } else if (Number(activeTab.value) > index) {
    // 如果删除的标签页在当前选中标签页前面，当前选中标签页索引减1
    activeTab.value = String(Number(activeTab.value) - 1);
  }
};

// 新增字段
const handleAddField = (sheetIndex: number) => {
  const newField: ImportConfigItemVO = {
    id: '',
    configId: '',
    sheetIndex: sheetIndex,
    title: '',
    fieldRequired: 0,
    dataType: '0',
    fillSm: '',
    width: 5000,
    downSelHandler: '',
    parameter: '',
    sort: sheetList.value[sheetIndex].fields.length + 1,
    remark: ''
  };

  sheetList.value[sheetIndex].fields.push(newField);
  ElMessage.success('已添加新字段');
};

// 删除字段
const handleRemoveField = (sheetIndex: number, fieldIndex: number) => {
  const fieldTitle = sheetList.value[sheetIndex].fields[fieldIndex].title || '未命名字段';
  sheetList.value[sheetIndex].fields.splice(fieldIndex, 1);
  ElMessage.success(`已删除字段: ${fieldTitle}`);
};

// 标签页切换
const handleTabClick = () => {
  // 标签页切换逻辑
};

// 取消按钮
const cancel = () => {
  visible.value = false;
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  try {
    buttonLoading.value = true;
    await formRef.value.validate();

    // 检查字段配置是否为空
    const hasEmptySheet = sheetList.value.some((sheet) => sheet.fields.length === 0);
    if (hasEmptySheet) {
      ElMessage.warning('存在没有配置字段的Sheet页，请添加字段');
      buttonLoading.value = false;
      return;
    }

    // 提交模板基本信息和Sheet配置数据
    emit('submit', {
      formData: { ...formData },
      sheetData: sheetList.value
    });

    visible.value = false;
  } catch (error) {
    console.error('表单验证失败', error);
    ElMessage.error('请检查表单必填项');
  } finally {
    buttonLoading.value = false;
  }
};

// 抽屉关闭后重置数据
const handleClosed = () => {
  formRef.value?.resetFields();
  Object.assign(formData, {
    id: undefined,
    name: '',
    code: '',
    branchType: '',
    remark: ''
  });

  sheetList.value = [
    {
      name: '',
      fields: []
    }
  ];
  activeTab.value = '0';
};
</script>

<style scoped>
.template-drawer :deep(.el-drawer__body) {
  padding: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.drawer-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 16px;
  overflow: hidden;
}

.template-header {
  margin-bottom: 16px;
}

.template-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.header-card,
.content-card {
  width: 100%;
  border-radius: 8px;
  transition: all 0.3s;
}

.content-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-card :deep(.el-card__body) {
  flex: 1;
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
  color: #409eff;
}

.base-form {
  padding: 10px;
}

.sheet-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sheet-tabs :deep(.el-tabs__content) {
  flex: 1;
  padding: 16px;
  overflow: auto;
}

.sheet-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
  padding: 10px 16px 0;
  background-color: #f5f7fa;
}

.custom-tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.custom-tab-label button {
  opacity: 0.7;
  transition: opacity 0.3s;
}

.custom-tab-label:hover button {
  opacity: 1;
}

.sheet-config {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.field-table {
  flex: 1;
  border-radius: 4px;
  overflow: hidden;
}

.field-table :deep(.el-table__inner-wrapper) {
  height: 100%;
}

.field-table :deep(.el-input-number) {
  width: 100%;
}

.drawer-footer {
  padding: 10px 20px;
  text-align: right;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}
</style>
