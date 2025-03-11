<template>
  <el-card shadow="hover">
    <template #header>
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="Edit">配置过程</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-tooltip placement="top">
            <template #content> 从excel复制数据,粘贴生成数据!</template>
            <el-button type="warning" plain icon="DocumentCopy">粘贴</el-button>
          </el-tooltip>
        </el-col>
      </el-row>
    </template>
    <el-form ref="dataFormRef" :model="baseLawConfig.calDefinList" :rules="rules" :inline="true">
      <el-table ref="dragTable" :data="baseLawConfig.calDefinList" row-key="columnId" size="small"
                :max-height="tableHeight" border>
        <el-table-column label="序号" type="index" width="65px" />
        <el-table-column label="计算代码" align="center" width="100px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.calCode'" :rules="rules.calCode" style="margin-bottom: 0;">
              <el-input v-model="scope.row.calCode" style="width: 80px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="工资项名称" align="center" width="170px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.calName'" :rules="rules.calCode" style="margin-bottom: 0;">
              <el-input v-model="scope.row.calName" style="width: 150px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="结果字段" align="center" width="100px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.tableColname'" :rules="rules.tableColname" style="margin-bottom: 0;">
              <el-input v-model="scope.row.tableColname" style="width: 80px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="计算分组名称" align="center" width="200px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.calGroupName'" style="margin-bottom: 0;">
              <el-input v-model="scope.row.calGroupName" style="width: 185px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="计算过程元素" align="center" width="200px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.calProcessElem'" style="margin-bottom: 0;">
              <el-input v-model="scope.row.calProcessElem" style="width: 185px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="数据类型" align="center" width="90px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.dataType'" :rules="rules.dataType" style="margin-bottom: 0;">
              <!--              <i-select v-model="scope.row.dataType" wid="70px" placeholder=" "></i-select>-->
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="计算类型" align="center" width="90px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.wageType'" :rules="rules.wageType" style="margin-bottom: 0;">
              <el-input v-model="scope.row.wageType" style="width: 70px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="计算频率" align="center" width="90px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.calPeriod'" :rules="rules.calPeriod" style="margin-bottom: 0;">
              <!--              <i-select v-model="scope.row.calPeriod" wid="70px" placeholder=" "></i-select>-->
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="特殊频率参数" align="center" width="150px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.calElements'" style="margin-bottom: 0;">
              <el-input v-model="scope.row.calElements" style="width: 130px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="计算顺序" align="center" width="120px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.calOrder'" :rules="rules.calOrder" style="margin-bottom: 0;">
              <el-input v-model="scope.row.calOrder" style="width: 100px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="输出工资单" align="center" width="80px">
          <template #default="scope">
            <el-checkbox :true-label="1" :false-label="0" v-model="scope.row.outExcel"></el-checkbox>
          </template>
        </el-table-column>
        <el-table-column label="工资单输出顺序" align="center" width="100px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.outOrder'" :rules="rules.outOrder" style="margin-bottom: 0;">
              <el-input v-model="scope.row.calOrder" style="width: 80px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="计算处理类" width="220px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.handlerClass'" :rules="rules.handlerClass" style="margin-bottom: 0;">
              <el-input v-model="scope.row.handlerClass" style="width: 200px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="规则说明" width="200px">
          <template #default="scope">
            <el-form-item :prop="scope.$index + '.remark'" style="margin-bottom: 0;">
              <el-input v-model="scope.row.remark" style="width: 180px;"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" fixed="right" class-name="small-padding fixed-width" width="130">
          <template #default="scope">
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDel(scope.$index)">删除</el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align: center;margin-left:-100px;margin-top:10px;">
        <el-button type="primary" @click="submitForm()">提交</el-button>
        <el-button @click="close()">关闭</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script setup name="OssConfig1" lang="ts">

import {
  BaseLawVerDetailsVo,
  BaseLawVerFormVo,
  WageCalDefineVo,
  WageCalDefinitionVo, WageCalEleVo, WageCalGradeRelationVo
} from "@/api/system/lawVersion/types";
import ISelect from "@/views/modules/common/ISelect.vue";
import { getBaseLawConfig } from "@/api/system/lawVersion";

const route = useRoute();
const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const tableHeight = ref(document.documentElement.scrollHeight - 245 + "px");
//工资计算定义
const wageCalDef = ref<WageCalDefineVo[]>([]);
//基本法版本ID
const lawId = route.params && route.params.lawId as string;
//配置对象
const baseLawConfig = reactive<BaseLawVerDetailsVo>({
  lawVerFormVo: undefined,
  calDefinList: [],
  elementsList: [],
  gradeRelationVoList: []
});
//校验规则
const rules = reactive({
  calCode: [{ required: true, message: "请输入", trigger: "blur" }],
  calName: [{ required: true, message: "请输入", trigger: "blur" }],
  tableColname: [{ required: true, message: "请输入", trigger: "blur" }],
  dataType: [{ required: true, message: "请输入", trigger: "blur" }],
  wageType: [{ required: true, message: "请输入", trigger: "blur" }],
  calPeriod: [{ required: true, message: "请输入", trigger: "blur" }],
  calOrder: [{ required: true, message: "请输入", trigger: "blur" }],
  outExcel: [{ required: true, message: "请输入", trigger: "blur" }],
  outOrder: [{ required: true, message: "请输入", trigger: "blur" }],
  handlerClass: [{ required: true, message: "请输入", trigger: "blur" }]
});

const getConfig = async () => {
  const res = await getBaseLawConfig(lawId);
  if (res) {
    Object.assign(baseLawConfig, res.data);
    console.log(baseLawConfig);
  }
};

/**添加 */
const handleAdd = () => {
  wageCalDef.value.push({});
};

/**删除 */
const handleDel = (index?: number) => {
  if (index !== undefined) {
    wageCalDef.value.splice(index, 1);
  }
};

const submitForm = () => {
  console.log("submitForm");
};

const close = () => {
  proxy?.$tab.closePage();
};

onMounted(() => {
  //加载配置
  getConfig();
});
</script>

<style scoped lang="scss">
:deep(.el-form-item__error) {
  right: 0;
  top: 50%;
  padding-right: 25px;
  transform: translateY(-50%);
  white-space: nowrap;
  text-align: right;
  pointer-events: none;
}

:deep(.el-table__body) {
  margin-bottom: 10px;
}
</style>
