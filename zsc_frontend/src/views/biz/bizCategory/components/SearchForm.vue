<template>
  <el-form :model="queryParams" :inline="true" class="search-form">
    <el-form-item prop="categoryName">
      <template #label><svg-icon icon-class="search" /></template>
      <el-input
        v-model="queryParams.categoryName"
        placeholder="类别名称"
        clearable
        style="width: 160px"
        @keyup.enter="handleQuery"
      />
    </el-form-item>
    <el-form-item prop="status">
      <template #label><svg-icon icon-class="switch" /></template>
      <el-select
        v-model="queryParams.status"
        placeholder="状态"
        clearable
        style="width: 100px"
      >
        <el-option
          v-for="dict in sys_normal_disable"
          :key="dict.value"
          :label="dict.label"
          :value="dict.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" icon="Search" circle @click="handleQuery" />
      <el-button type="primary" circle @click="resetQuery"><svg-icon icon-class="reset" /></el-button>
    </el-form-item>
  </el-form>
</template>

<script setup name="BizCategorySearch">
const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const props = defineProps({
  queryParams: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['query', 'reset'])

const queryFormRef = ref(null)

/** 搜索按钮操作 */
function handleQuery() {
  emit('query')
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryFormRef")
  emit('reset')
}

defineExpose({
  queryFormRef
})
</script>

<style scoped lang="scss">
.search-form {
  margin-bottom: 16px;
  :deep(.el-form-item) { margin-right: 8px; margin-bottom: 0; }
  :deep(.el-form-item__label) { display: flex; align-items: center; }
}
</style>
