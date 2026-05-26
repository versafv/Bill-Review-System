<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item prop="userName">
        <template #label><svg-icon icon-class="search" /></template>
        <el-input v-model="queryParams.userName" placeholder="用户名" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item prop="roleKey">
        <template #label><svg-icon icon-class="peoples" /></template>
        <el-select v-model="queryParams.roleKey" placeholder="角色" clearable style="width: 120px">
          <el-option label="普通用户" value="user" />
          <el-option label="审核员" value="reviewer" />
        </el-select>
      </el-form-item>
      <el-form-item prop="status">
        <template #label><svg-icon icon-class="switch" /></template>
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 100px">
          <el-option v-for="d in sys_normal_disable" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" circle native-type="button" @click="handleQuery" />
        <el-button type="primary" circle native-type="button" @click="resetQuery"><svg-icon icon-class="reset" /></el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="userList">
      <el-table-column label="用户名" align="center" prop="userName" min-width="120" />
      <el-table-column label="邮箱" align="center" prop="email" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="手机" align="center" prop="phonenumber" width="130" />
      <el-table-column label="角色" align="center" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.roles?.[0]?.roleKey === 'reviewer' ? 'success' : ''" size="small">
            {{ scope.row.roles?.[0]?.roleKey === 'reviewer' ? '审核员' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160">
        <template #default="scope">
          <el-tooltip content="重置密码" placement="top"><el-button link type="primary" icon="Lock" @click="handleResetPwd(scope.row)" v-hasPermi="['system:user:resetPwd']" /></el-tooltip>
          <el-tooltip :content="scope.row.status === '0' ? '停用' : '启用'" placement="top"><el-button link :type="scope.row.status === '0' ? 'warning' : 'success'" :icon="scope.row.status === '0' ? 'VideoPause' : 'VideoPlay'" @click="handleStatus(scope.row)" v-hasPermi="['system:user:edit']" /></el-tooltip>
          <el-tooltip content="删除" placement="top"><el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:user:remove']" /></el-tooltip>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listUser, resetUserPwd, changeUserStatus } from '@/api/biz/admin'
import { delUser } from '@/api/system/user'

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict('sys_normal_disable')

const userList = ref([])
const loading = ref(false)

const queryParams = ref({ userName: undefined, roleKey: undefined, status: undefined })

function parseTime(t) { return t ? proxy.parseTime(t) : '-' }

function getList() {
  loading.value = true
  const raw = queryParams.value
  const params = {}
  Object.keys(raw).forEach(k => {
    const v = raw[k]
    if (v !== undefined && v !== null && v !== '') {
      params[k] = v
    }
  })
  listUser(params).then(res => {
    userList.value = res.data || []
  }).finally(() => { loading.value = false })
}

function handleQuery() { getList() }
function resetQuery() {
  queryParams.value = { userName: undefined, roleKey: undefined, status: undefined }
  handleQuery()
}

function handleResetPwd(row) {
  proxy.$prompt(`请输入「${row.userName}」的新密码`, '重置密码', { confirmButtonText: '确定', cancelButtonText: '取消' }).then(({ value }) => {
    resetUserPwd(row.userId, value).then(() => proxy.$modal.msgSuccess('密码重置成功'))
  })
}

function handleStatus(row) {
  const newStatus = row.status === '0' ? '1' : '0'
  const text = newStatus === '1' ? '停用' : '启用'
  proxy.$modal.confirm(`确认${text}用户「${row.userName}」？`).then(() => {
    changeUserStatus(row.userId, newStatus).then(() => { proxy.$modal.msgSuccess(text + '成功'); getList() })
  })
}

function handleDelete(row) {
  proxy.$modal.confirm(`确认删除用户「${row.userName}」？`).then(() => {
    delUser(row.userId).then(() => { proxy.$modal.msgSuccess('删除成功'); getList() })
  })
}

onMounted(() => getList())
</script>

<style scoped lang="scss">
.search-form {
  margin-bottom: 16px;
  :deep(.el-form-item) { margin-right: 8px; margin-bottom: 0; }
  :deep(.el-form-item__label) { display: flex; align-items: center; }
}
</style>
