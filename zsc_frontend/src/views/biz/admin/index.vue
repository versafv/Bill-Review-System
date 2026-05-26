<template>
  <div class="app-container dashboard">
    <el-row :gutter="20" class="dashboard-stats">
      <el-col :xs="12" :sm="8" :md="span6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon total-icon"><el-icon :size="36"><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="span6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon user-icon"><el-icon :size="36"><UserFilled /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">普通用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="span6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon reviewer-icon"><el-icon :size="36"><Checked /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.reviewerCount }}</div>
              <div class="stat-label">审核员</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="span6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon admin-icon"><el-icon :size="36"><Setting /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.adminCount }}</div>
              <div class="stat-label">管理员</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-stats">
      <el-col :xs="12" :sm="8" :md="span6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon bill-icon"><el-icon :size="36"><Document /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalBills }}</div>
              <div class="stat-label">总票据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8" :md="span6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon pending-icon"><el-icon :size="36"><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingBills }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, UserFilled, Checked, Setting, Document, Clock } from '@element-plus/icons-vue'
import { getAdminStats } from '@/api/biz/admin'

const span6 = 4  // 24 / 6
const stats = ref({ totalUsers: 0, userCount: 0, reviewerCount: 0, adminCount: 0, totalBills: 0, pendingBills: 0 })

onMounted(() => {
  getAdminStats().then(res => { stats.value = res.data })
})
</script>

<style scoped lang="scss">
.dashboard { padding: 20px; }
.dashboard-stats {
  margin-bottom: 20px;
  .el-col { margin-bottom: 16px; }
  .stat-card {
    transition: all 0.3s;
    &:hover { transform: translateY(-5px); }
    .stat-content {
      display: flex; align-items: center; justify-content: space-around; padding: 10px 0;
      .stat-icon {
        width: 70px; height: 70px; border-radius: 50%;
        display: flex; align-items: center; justify-content: center;
        &.total-icon    { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; }
        &.user-icon     { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: #fff; }
        &.reviewer-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: #fff; }
        &.admin-icon    { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); color: #fff; }
        &.bill-icon     { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: #fff; }
        &.pending-icon  { background: linear-gradient(135deg, #f7971e 0%, #ffd200 100%); color: #fff; }
      }
      .stat-info {
        text-align: center;
        .stat-value { font-size: 32px; font-weight: bold; color: var(--el-text-color-primary); margin-bottom: 5px; }
        .stat-label { font-size: 14px; color: var(--el-text-color-secondary); }
      }
    }
  }
}
</style>
