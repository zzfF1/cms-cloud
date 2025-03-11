<!-- components/export/ConfigDialog.vue -->
<template>
  <el-drawer v-model="visible" :title="'配置 - ' + title" size="90%" append-to-body destroy-on-close>
    <!-- 工具栏 -->
    <div class="mb-4 flex justify-between">
      <div>
        <el-button type="primary" @click="handleAddSheet">新增Sheet</el-button>
      </div>
      <div>
        <el-button @click="handlePreview">预览数据</el-button>
        <el-button type="primary" @click="handleSubmit">保存配置</el-button>
      </div>
    </div>

    <!-- Sheets配置区域 -->
    <el-tabs v-model="activeTab" type="border-card" closable @tab-remove="handleRemoveSheet">
      <el-tab-pane v-for="(sheet, index) in sheets" :key="sheet.id" :label="sheet.sheetName || 'Sheet' + (index + 1)" :name="String(index)">
        <el-form :model="sheet" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="标题名称">
                <el-input v-model="sheet.titleName" placeholder="请输入标题名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Sheet名称">
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
            <div class="mb-2 text-right">
              <el-button type="primary" @click="handleAddField(index)">新增字段</el-button>
            </div>
            <el-table :data="sheet.items" border size="small">
              <el-table-column type="index" label="序号" width="60" />
              <el-table-column prop="field" label="字段名">
                <template #default="{ row }">
                  <el-input v-model="row.field" placeholder="请输入字段名" />
                </template>
              </el-table-column>
              <el-table-column prop="name" label="显示名称">
                <template #default="{ row }">
                  <el-input v-model="row.name" placeholder="请输入显示名称" />
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="150">
                <template #default="{ row }">
                  <el-select v-model="row.type">
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
                  <el-input-number v-model="row.dispLength" :min="1000" :max="10000" :step="500" />
                </template>
              </el-table-column>
              <el-table-column prop="format" label="格式">
                <template #default="{ row }">
                  <el-input v-model="row.format" placeholder="请输入格式" />
                </template>
              </el-table-column>
              <el-table-column prop="sort" label="排序" width="100">
                <template #default="{ row }">
                  <el-input-number v-model="row.sort" :min="1" :max="99" :precision="2" :step="0.01" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" fixed="right">
                <template #default="{ $index }">
                  <el-button type="text" style="color: red" @click="handleRemoveField(index, $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-drawer>
</template>

<script setup>
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  data: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:modelValue', 'submit']);

// 弹窗显示控制
const visible = ref(false);
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val;
  }
);
watch(
  () => visible.value,
  (val) => {
    emit('update:modelValue', val);
  }
);

// Sheet页签
const activeTab = ref('0');
const sheets = ref([]);

// 监听数据变化
watch(
  () => props.data,
  (newVal) => {
    if (newVal?.sheets) {
      sheets.value = JSON.parse(JSON.stringify(newVal.sheets));
    }
  },
  { immediate: true }
);

// 新增Sheet
const handleAddSheet = () => {
  sheets.value.push({
    id: Date.now(),
    configId: props.data?.id,
    sheetIndex: sheets.value.length,
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
  activeTab.value = String(sheets.value.length - 1);
};

// 删除Sheet
const handleRemoveSheet = (targetName) => {
  if (sheets.value.length <= 1) {
    ElMessage.warning('至少需要保留一个Sheet');
    return;
  }

  const index = Number(targetName);
  sheets.value.splice(index, 1);

  if (activeTab.value === targetName) {
    activeTab.value = index === sheets.value.length ? String(index - 1) : String(index);
  }
};

// 新增字段
const handleAddField = (sheetIndex) => {
  sheets.value[sheetIndex].items.push({
    id: Date.now(),
    field: '',
    name: '',
    type: 0,
    dispLength: 5000,
    format: '',
    sort: sheets.value[sheetIndex].items.length + 1
  });
};

// 删除字段
const handleRemoveField = (sheetIndex, fieldIndex) => {
  sheets.value[sheetIndex].items.splice(fieldIndex, 1);
};

// 预览数据
const handlePreview = () => {
  // TODO: 实现预览功能
};

// 提交配置
const handleSubmit = () => {
  // 校验数据
  if (!validateConfig()) return;

  emit('submit', sheets.value);
};

// 配置校验
const validateConfig = () => {
  for (const [index, sheet] of sheets.value.entries()) {
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
    if (sheet.items.length === 0) {
      ElMessage.warning(`Sheet${index + 1} 至少需要配置一个字段`);
      return false;
    }
  }
  return true;
};
</script>
