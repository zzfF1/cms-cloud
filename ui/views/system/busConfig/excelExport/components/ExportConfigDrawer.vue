<template>
  <el-drawer
    v-model="visible"
    :title="title"
    size="95%"
    append-to-body
    :close-on-click-modal="false"
    @closed="handleClosed"
    :destroy-on-close="true"
    class="export-drawer"
  >
    <!-- 全局加载遮罩 -->
    <el-loading v-model:full-screen="fullScreenLoading" :text="loadingText" background="rgba(255, 255, 255, 0.7)" />

    <div class="drawer-container">
      <!-- 固定在顶部的表单区域 -->
      <div class="export-header">
        <el-card shadow="hover" class="header-card">
          <template #header>
            <div class="card-header">
              <span><i class="el-icon-document"></i> 导出配置基本信息</span>
            </div>
          </template>

          <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px" class="base-form">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="名称" prop="name">
                  <el-input v-model="formData.name" placeholder="请输入名称" clearable />
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
                    <el-option label="银保" value="3" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="类型" prop="type">
                  <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
                    <el-option label="生成方式" value="0" />
                    <el-option label="模板方式" value="1" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="16" v-if="formData.type === '1'">
                <el-form-item label="模板路径">
                  <el-input v-model="formData.path" placeholder="请上传模板">
                    <template #append>
                      <el-upload
                        :show-file-list="false"
                        :before-upload="beforeTemplateUpload"
                        :http-request="handleTemplateUpload"
                        accept=".xlsx,.xls"
                      >
                        <el-button>上传</el-button>
                      </el-upload>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>
      </div>

      <!-- Sheet配置部分 -->
      <div class="export-content">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span><i class="el-icon-s-grid"></i> Sheet配置</span>
              <div>
                <el-button type="primary" size="small" @click="handleAddSheet" plain>
                  <el-icon><Plus /></el-icon> 新增Sheet
                </el-button>
                <el-button size="small" @click="handlePreview" plain>
                  <el-icon><View /></el-icon> 预览数据
                </el-button>
              </div>
            </div>
          </template>

          <!-- Sheet选项卡 -->
          <el-tabs v-model="activeTab" type="card" class="sheet-tabs">
            <el-tab-pane
              v-for="(sheet, index) in sheetList"
              :key="sheet.id || index"
              :name="String(index)"
            >
              <template #label>
                <div class="custom-tab-label">
                  <span>{{ sheet.sheetName || 'Sheet' + (index + 1) }}</span>
                  <el-button
                    v-if="sheetList.length > 1"
                    type="danger"
                    size="small"
                    @click.stop="handleRemoveSheet(index)"
                    circle
                    plain
                  >
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
              </template>

              <div class="sheet-config">
                <el-form :model="sheet" label-width="100px">
                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="标题名称" prop="titleName">
                        <el-input v-model="sheet.titleName" placeholder="请输入标题名称" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="Sheet名称" prop="sheetName">
                        <el-input v-model="sheet.sheetName" placeholder="请输入Sheet名称" />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-form-item label="SQL字段">
                    <el-input v-model="sheet.sqlField" type="textarea" :rows="3" placeholder="请输入SQL字段" />
                  </el-form-item>

                  <el-form-item label="SQL条件">
                    <el-input v-model="sheet.sqlConditions" type="textarea" :rows="2" placeholder="请输入SQL条件" />
                  </el-form-item>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="分组字段">
                        <el-input v-model="sheet.sqlGroup" placeholder="请输入分组字段" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="排序字段">
                        <el-input v-model="sheet.sqlOrder" placeholder="请输入排序字段" />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <!-- 字段配置 -->
                  <el-form-item label="字段配置">
                    <div class="table-header">
                      <h4>字段列表</h4>
                      <el-button type="primary" size="small" @click="handleAddField(index)">
                        <el-icon><Plus /></el-icon> 新增字段
                      </el-button>
                    </div>
                    <el-table
                      :data="sheet.items"
                      border
                      stripe
                      highlight-current-row
                      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
                      class="field-table"
                    >
                      <el-table-column type="index" label="序号" width="60" align="center" />
                      <el-table-column prop="field" label="字段名" min-width="120">
                        <template #default="{ row }">
                          <el-input v-model="row.field" placeholder="请输入字段名" />
                        </template>
                      </el-table-column>
                      <el-table-column prop="name" label="显示名称" min-width="120">
                        <template #default="{ row }">
                          <el-input v-model="row.name" placeholder="请输入显示名称" />
                        </template>
                      </el-table-column>
                      <el-table-column prop="type" label="类型" width="120">
                        <template #default="{ row }">
                          <el-select v-model="row.type" style="width: 100%">
                            <el-option label="字符串" :value="0" />
                            <el-option label="整数" :value="1" />
                            <el-option label="2位小数" :value="2" />
                            <el-option label="4位小数" :value="3" />
                            <el-option label="日期" :value="4" />
                            <el-option label="序号" :value="9" />
                          </el-select>
                        </template>
                      </el-table-column>
                      <el-table-column prop="dispLength" label="长度" width="120">
                        <template #default="{ row }">
                          <el-input-number
                            v-model="row.dispLength"
                            :min="1000"
                            :max="10000"
                            :step="500"
                            style="width: 100%"
                            controls-position="right"
                          />
                        </template>
                      </el-table-column>
                      <el-table-column prop="format" label="格式" min-width="120">
                        <template #default="{ row }">
                          <el-input v-model="row.format" placeholder="请输入格式" />
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
                      <el-table-column label="操作" width="80" fixed="right" align="center">
                        <template #default="{ $index }">
                          <el-button type="danger" size="small" @click="handleRemoveField(index, $index)" circle>
                            <el-icon><Delete /></el-icon>
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-form-item>
                </el-form>
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

    <!-- 数据预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="数据预览" width="90%" append-to-body>
      <el-tabs type="border-card">
        <el-tab-pane v-for="(sheet, index) in sheetList" :key="index" :label="sheet.sheetName || 'Sheet' + (index + 1)">
          <el-table v-loading="previewLoading" :data="previewData[index] || []" border stripe>
            <el-table-column v-for="(item, itemIndex) in sheet.items" :key="itemIndex" :prop="item.field" :label="item.name" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关 闭</el-button>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive, defineProps, defineEmits, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { Plus, Delete, Close, View } from '@element-plus/icons-vue';
import { previewExportData, uploadExportTemplate } from '@/api/system/exportConfig';
import { ExportConfigVO, ExportConfigForm, SheetConfig, ExportConfigItemVO } from '@/api/system/exportConfig/types';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '导出配置'
  },
  data: {
    type: Object as () => Partial<ExportConfigVO>,
    default: () => ({})
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

// 表单数据 - 基本信息
const formData = reactive<ExportConfigForm>({
  id: undefined,
  code: '',
  name: '',
  branchType: '3',
  type: '0',
  path: '',
  filename: ''
});

// 表单校验规则
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  code: [
    { required: true, message: '请输入代码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '代码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  branchType: [{ required: true, message: '请选择渠道', trigger: 'change' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
});

// sheet列表数据
const sheetList = ref<SheetConfig[]>([
  {
    id: undefined,
    configId: undefined,
    sheetIndex: 0,
    titleName: '',
    sheetName: '',
    beginRow: '0',
    beginCol: '0',
    sqlField: '',
    sqlConditions: '',
    sqlGroup: '',
    sqlOrder: '',
    items: []
  }
]);

// 数据预览相关
const previewDialogVisible = ref(false);
const previewLoading = ref(false);
const previewData = ref<any[]>([]);

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

// 监听编辑数据
watch(
  () => props.data,
  (val) => {
    if (val) {
      // 设置基本信息
      Object.keys(formData).forEach(key => {
        if (val[key as keyof ExportConfigVO] !== undefined) {
          // @ts-ignore
          formData[key] = val[key];
        }
      });

      // 设置sheets
      if (val.sheets && val.sheets.length > 0) {
        sheetList.value = JSON.parse(JSON.stringify(val.sheets));
      } else {
        // 如果没有sheets，创建一个空sheet
        sheetList.value = [
          {
            id: undefined,
            configId: val.id,
            sheetIndex: 0,
            titleName: '',
            sheetName: '',
            beginRow: '0',
            beginCol: '0',
            sqlField: '',
            sqlConditions: '',
            sqlGroup: '',
            sqlOrder: '',
            items: []
          }
        ];
      }
    }
  },
  { deep: true, immediate: true }
);

// 上传模板前校验
const beforeTemplateUpload = (file: File) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
    file.type === 'application/vnd.ms-excel';
  if (!isExcel) {
    ElMessage.error('只能上传Excel文件！');
    return false;
  }
  return true;
};

// 处理模板上传
const handleTemplateUpload = async (options: any) => {
  const { file } = options;
  try {
    const response = await uploadExportTemplate(file);
    formData.path = response.data;
    formData.filename = file.name;
    ElMessage.success('上传成功');
  } catch (error) {
    console.error('上传失败:', error);
    ElMessage.error('上传失败');
  }
};

// 新增Sheet
const handleAddSheet = () => {
  sheetList.value.push({
    id: undefined,
    configId: formData.id,
    sheetIndex: sheetList.value.length,
    titleName: '',
    sheetName: '',
    beginRow: '0',
    beginCol: '0',
    sqlField: '',
    sqlConditions: '',
    sqlGroup: '',
    sqlOrder: '',
    items: []
  });
  activeTab.value = String(sheetList.value.length - 1);

  ElMessage.success('已添加新Sheet');
};

// 删除Sheet
const handleRemoveSheet = (index: number) => {
  if (sheetList.value.length <= 1) {
    ElMessage.warning('至少需要保留一个Sheet');
    return;
  }

  ElMessage.success(`已删除 ${sheetList.value[index].sheetName || 'Sheet' + (index + 1)}`);
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
  if (!sheetList.value[sheetIndex].items) {
    sheetList.value[sheetIndex].items = [];
  }

  const newField: ExportConfigItemVO = {
    id: undefined,
    field: '',
    name: '',
    type: 0,
    dispLength: 5000,
    format: '',
    sort: (sheetList.value[sheetIndex].items?.length || 0) + 1
  };

  sheetList.value[sheetIndex].items?.push(newField);

  ElMessage.success('已添加新字段');
};

// 删除字段
const handleRemoveField = (sheetIndex: number, fieldIndex: number) => {
  if (!sheetList.value[sheetIndex].items) return;

  const fieldName = sheetList.value[sheetIndex].items?.[fieldIndex]?.name || '未命名字段';
  sheetList.value[sheetIndex].items?.splice(fieldIndex, 1);

  ElMessage.success(`已删除字段: ${fieldName}`);
};

// 预览数据
const handlePreview = async () => {
  // 校验必填项
  if (!validateSheets()) {
    return;
  }

  previewLoading.value = true;
  previewDialogVisible.value = true;

  try {
    const response = await previewExportData({
      basicInfo: { ...formData },
      sheetInfo: sheetList.value.map((sheet, index) => ({
        ...sheet,
        sheetIndex: index
      }))
    });

    previewData.value = response.data || [];

    // 如果没有数据，填充一些假数据进行展示
    if (!previewData.value || previewData.value.length === 0) {
      previewData.value = sheetList.value.map(sheet => {
        return [
          generateSampleRow(sheet.items, 1),
          generateSampleRow(sheet.items, 2),
          generateSampleRow(sheet.items, 3)
        ];
      });
    }
  } catch (error) {
    console.error('预览失败:', error);
    ElMessage.error('预览失败');
  } finally {
    previewLoading.value = false;
  }
};

// 生成示例行数据
const generateSampleRow = (items?: ExportConfigItemVO[], rowNum = 1) => {
  const row: Record<string, any> = {};
  if (!items) return row;

  items.forEach(item => {
    const field = item.field || 'field';

    switch (item.type) {
      case 0: // 字符串
        row[field] = `示例文本${rowNum}`;
        break;
      case 1: // 整数
        row[field] = rowNum * 100;
        break;
      case 2: // 2位小数
        row[field] = (rowNum * 10.25).toFixed(2);
        break;
      case 3: // 4位小数
        row[field] = (rowNum * 10.1234).toFixed(4);
        break;
      case 4: // 日期
        row[field] = new Date().toLocaleDateString();
        break;
      case 9: // 序号
        row[field] = rowNum;
        break;
      default:
        row[field] = '';
    }
  });

  return row;
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

    // 校验Sheets配置
    if (!validateSheets()) {
      buttonLoading.value = false;
      return;
    }

    if (formData.type === '1' && !formData.path) {
      ElMessage.warning('请上传模板文件');
      buttonLoading.value = false;
      return;
    }

    // 提交数据
    const basicInfo = { ...formData };
    const sheetInfo = sheetList.value.map((sheet, index) => ({
      ...sheet,
      sheetIndex: index
    }));

    emit('submit', { basicInfo, sheetInfo });
  } catch (error) {
    console.error('表单验证失败', error);
    ElMessage.error('请检查表单必填项');
  } finally {
    buttonLoading.value = false;
  }
};

// 校验Sheets配置
const validateSheets = () => {
  for (const [index, sheet] of sheetList.value.entries()) {
    if (!sheet.titleName) {
      ElMessage.warning(`Sheet${index + 1} 标题名称不能为空`);
      return false;
    }
    if (!sheet.sheetName) {
      ElMessage.warning(`Sheet${index + 1} Sheet名称不能为空`);
      return false;
    }
    if (!sheet.sqlField) {
      ElMessage.warning(`Sheet${index + 1} SQL字段不能为空`);
      return false;
    }
    if (!sheet.sqlConditions) {
      ElMessage.warning(`Sheet${index + 1} SQL条件不能为空`);
      return false;
    }
    if (!sheet.items || sheet.items.length === 0) {
      ElMessage.warning(`Sheet${index + 1} 至少需要配置一个字段`);
      return false;
    }

    // 检查字段配置
    for (const [fieldIndex, field] of (sheet.items || []).entries()) {
      if (!field.field) {
        ElMessage.warning(`Sheet${index + 1} 第${fieldIndex + 1}行字段名不能为空`);
        return false;
      }
      if (!field.name) {
        ElMessage.warning(`Sheet${index + 1} 第${fieldIndex + 1}行显示名称不能为空`);
        return false;
      }
    }
  }
  return true;
};

// 抽屉关闭后重置数据
const handleClosed = () => {
  formRef.value?.resetFields();

  // 重置表单数据
  Object.assign(formData, {
    id: undefined,
    code: '',
    name: '',
    branchType: '3',
    type: '0',
    path: '',
    filename: ''
  });

  // 重置sheets
  sheetList.value = [
    {
      id: undefined,
      configId: undefined,
      sheetIndex: 0,
      titleName: '',
      sheetName: '',
      beginRow: '0',
      beginCol: '0',
      sqlField: '',
      sqlConditions: '',
      sqlGroup: '',
      sqlOrder: '',
      items: []
    }
  ];

  activeTab.value = '0';
  previewData.value = [];
};
</script>

<style scoped>
.export-drawer :deep(.el-drawer__body) {
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

.export-header {
  margin-bottom: 16px;
}

.export-content {
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
  color: #409EFF;
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
  padding: 10px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.table-header h4 {
  margin: 0;
  color: #606266;
}

.field-table {
  margin-bottom: 20px;
}

.drawer-footer {
  padding: 10px 20px;
  text-align: right;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}
</style>
