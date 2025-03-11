<!-- TemplateConfigDialog.vue -->
<template>
  <el-dialog v-model="visible" title="字段配置" width="1200px" append-to-body destroy-on-close @closed="handleClosed">
    <div class="config-container">
      <div class="sheet-actions">
        <el-button type="primary" @click="handleAddSheet">新增Sheet</el-button>
      </div>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane v-for="(sheet, index) in sheetList" :key="index" :label="sheet.name || 'Sheet' + (index + 1)" :name="String(index)">
          <div class="sheet-header">
            <el-input v-model="sheet.name" placeholder="请输入Sheet名称" style="width: 200px" />
            <div class="sheet-actions">
              <el-button type="primary" @click="handleAddField(index)">新增字段</el-button>
              <el-button type="danger" @click="handleRemoveSheet(index)" :disabled="sheetList.length <= 1">删除Sheet</el-button>
            </div>
          </div>

          <el-table :data="sheet.fields" style="margin-top: 15px">
            <el-table-column type="index" label="序号" width="50" />
            <el-table-column prop="title" label="标题">
              <template #default="{ row }">
                <el-input v-model="row.title" placeholder="请输入标题" />
              </template>
            </el-table-column>
            <el-table-column prop="fieldRequired" label="必填" width="80">
              <template #default="{ row }">
                <el-switch v-model="row.fieldRequired" :active-value="1" :inactive-value="0" />
              </template>
            </el-table-column>
            <el-table-column prop="dataType" label="字段类型" width="150">
              <template #default="{ row }">
                <el-select v-model="row.dataType">
                  <el-option label="字符串" value="0" />
                  <el-option label="整数" value="1" />
                  <el-option label="2位小数" value="2" />
                  <el-option label="4位小数" value="3" />
                  <el-option label="日期" value="4" />
                  <el-option label="序号" value="9" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="fillSm" label="填写说明">
              <template #default="{ row }">
                <el-input v-model="row.fillSm" placeholder="请输入填写说明" />
              </template>
            </el-table-column>
            <el-table-column prop="width" label="列宽" width="100">
              <template #default="{ row }">
                <el-input-number v-model="row.width" :min="1000" :max="10000" :step="500" />
              </template>
            </el-table-column>
            <el-table-column prop="sort" label="排序" width="100">
              <template #default="{ row }">
                <el-input-number v-model="row.sort" :min="1" :max="99" :precision="2" :step="0.01" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ $index }">
                <el-button type="text" style="color: red" @click="handleRemoveField(index, $index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, defineProps, defineEmits, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:modelValue', 'submit']);

// 弹窗显示控制
const visible = ref(false);

// 当前激活的标签页
const activeTab = ref('0');

// sheet列表数据
const sheetList = ref([
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
  }
);

// 监听本地显示状态
watch(
  () => visible.value,
  (val) => {
    emit('update:modelValue', val);
  }
);

// 监听配置数据
watch(
  () => props.data,
  (val) => {
    if (val && val.length > 0) {
      sheetList.value = JSON.parse(JSON.stringify(val));
    }
  },
  { deep: true }
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
const handleRemoveSheet = (index) => {
  sheetList.value.splice(index, 1);
  activeTab.value = '0';
};

// 新增字段
const handleAddField = (sheetIndex) => {
  sheetList.value[sheetIndex].fields.push({
    title: '',
    fieldRequired: 0,
    dataType: '0',
    fillSm: '',
    width: 5000,
    sort: sheetList.value[sheetIndex].fields.length + 1
  });
};

// 删除字段
const handleRemoveField = (sheetIndex, fieldIndex) => {
  sheetList.value[sheetIndex].fields.splice(fieldIndex, 1);
};

// 标签页切换
const handleTabClick = () => {
  // 可以在这里处理标签页切换逻辑
};

// 提交表单
const submitForm = () => {
  emit('submit', sheetList.value);
  visible.value = false;
};

// 弹窗关闭后重置数据
const handleClosed = () => {
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
.config-container {
  height: 600px;
  overflow-y: auto;
}

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.sheet-actions {
  margin-bottom: 15px;
}
</style>
