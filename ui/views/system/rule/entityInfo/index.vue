<template>
  <div>
    <el-row :gutter="5">
      <el-col :lg="12" :xs="24" style="">
        <EntityInfo @get-entity-id="getEntityId" @get-entity-name="getEntityName" />
      </el-col>
    </el-row>
  </div>
  <div></div>
</template>

<script lang="ts" setup>
import EntityInfo from './entity/EntityInfo.vue'

const entityIdRef = ref<string>('');
const entityNameRef = ref<string>('');

function getEntityId(entityId: string) {
  entityIdRef.value = entityId;
}

function getEntityName(entityName: string) {
  entityNameRef.value = entityName;
}
</script>

<style lang="scss" scoped></style>
