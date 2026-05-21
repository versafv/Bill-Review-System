<template>
  <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
    <el-form-item label="类别名称" prop="categoryName">
      <el-input
        v-model="queryParams.categoryName"
        placeholder="请输入类别名称"
        clearable
        style="width: 240px"
        @keyup.enter="handleQuery"
      />
    </el-form-item>
    <el-form-item label="状态" prop="status">
      <el-select
        v-model="queryParams.status"
        placeholder="请选择状态"
        clearable
        style="width: 240px"
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
      <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
      <el-button icon="Refresh" @click="resetQuery">重置</el-button>
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
  },
  showSearch: {
    type: Boolean,
    default: true
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
