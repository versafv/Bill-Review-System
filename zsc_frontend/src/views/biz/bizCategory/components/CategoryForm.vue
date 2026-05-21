<template>
  <el-dialog :title="title" v-model="open" width="500px" append-to-body>
    <el-form ref="categoryFormRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="类别名称" prop="categoryName">
        <el-input v-model="form.categoryName" placeholder="请输入类别名称" />
      </el-form-item>
      <el-form-item label="排序号" prop="sortOrder">
        <el-input-number 
          v-model="form.sortOrder" 
          :min="0" 
          :max="9999" 
          controls-position="right"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :value="dict.value"
          >{{ dict.label }}</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="BizCategoryForm">
import { addBizCategory, updateBizCategory } from "@/api/biz/bizCategory"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ""
  },
  formData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const categoryFormRef = ref(null)

const open = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const form = ref({})
const rules = reactive({
  categoryName: [{ required: true, message: "类别名称不能为空", trigger: "blur" }],
  sortOrder: [{ required: true, message: "排序号不能为空", trigger: "blur" }],
  status: [{ required: true, message: "状态不能为空", trigger: "change" }]
})

watch(() => props.formData, (newVal) => {
  form.value = { ...newVal }
}, { immediate: true, deep: true })

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    categoryId: undefined,
    categoryName: undefined,
    sortOrder: 1,
    status: "0"
  }
  proxy.resetForm("categoryFormRef")
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["categoryFormRef"].validate(valid => {
    if (valid) {
      if (form.value.categoryId != undefined) {
        updateBizCategory(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          emit('success')
        })
      } else {
        addBizCategory(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          emit('success')
        })
      }
    }
  })
}

defineExpose({
  categoryFormRef,
  reset
})
</script>
