<template>
  <div class="app-container dashboard">
    <el-row :gutter="20" class="dashboard-stats">
      <el-col :xs="12" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon book-icon">
              <el-icon :size="36"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalBooks }}</div>
              <div class="stat-label">总图书数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon available-icon">
              <el-icon :size="36"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.availableBooks }}</div>
              <div class="stat-label">可借图书</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon borrowed-icon">
              <el-icon :size="36"><DocumentChecked /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.borrowedBooks }}</div>
              <div class="stat-label">已借出</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon category-icon">
              <el-icon :size="36"><Menu /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalCategories }}</div>
              <div class="stat-label">分类数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-charts">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>图书分类统计</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>本周借阅趋势</span>
            </div>
          </template>
          <div ref="borrowTrendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>借阅排行榜</span>
            </div>
          </template>
          <div class="rank-list">
            <div v-for="(book, index) in hotBooks" :key="index" class="rank-item">
              <span :class="['rank-number', { top: index < 3 }]">{{ index + 1 }}</span>
              <span class="rank-title">{{ book.title }}</span>
              <span class="rank-count">{{ book.borrowCount }}次</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-tables">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="table-card">
          <template #header>
            <div class="card-header">
              <span>热门图书</span>
              <el-button type="primary" link @click="viewAllBooks">查看全部</el-button>
            </div>
          </template>
          <el-table :data="hotBooks" style="width: 100%" max-height="300">
            <el-table-column label="排名" width="70" align="center">
              <template #default="scope">
                <el-tag :type="scope.$index < 3 ? 'danger' : 'info'" effect="dark">
                  {{ scope.$index + 1 }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="书名" />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="borrowCount" label="借阅次数" width="100" align="center" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card class="table-card">
          <template #header>
            <div class="card-header">
              <span>最新入库</span>
              <el-button type="primary" link @click="viewNewBooks">查看全部</el-button>
            </div>
          </template>
          <el-table :data="newBooks" style="width: 100%" max-height="300">
            <el-table-column prop="title" label="书名" />
            <el-table-column prop="author" label="作者" width="100" />
            <el-table-column prop="categoryName" label="分类" width="120" />
            <el-table-column prop="createdTime" label="入库时间" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import { ref, onMounted, nextTick } from 'vue'
import { Reading, CircleCheck, DocumentChecked, Menu } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { useRouter } from 'vue-router'

const router = useRouter()
const categoryChartRef = ref(null)
const borrowTrendChartRef = ref(null)

const version = ref('1.0.0')

const statistics = ref({
  totalBooks: 0,
  availableBooks: 0,
  borrowedBooks: 0,
  totalCategories: 0
})

const hotBooks = ref([
  { title: '活着', author: '余华', borrowCount: 156 },
  { title: '三体', author: '刘慈欣', borrowCount: 142 },
  { title: '百年孤独', author: '加西亚·马尔克斯', borrowCount: 128 },
  { title: '追风筝的人', author: '卡勒德·胡赛尼', borrowCount: 115 },
  { title: '解忧杂货店', author: '东野圭吾', borrowCount: 98 },
  { title: '白夜行', author: '东野圭吾', borrowCount: 87 },
  { title: '人类简史', author: '尤瓦尔·赫拉利', borrowCount: 76 },
  { title: '平凡的世界', author: '路遥', borrowCount: 65 },
  { title: '小王子', author: '圣埃克苏佩里', borrowCount: 54 },
  { title: '围城', author: '钱钟书', borrowCount: 43 }
])

const newBooks = ref([
  { title: '行舟绿水中', author: '李凤利', categoryName: '文学小说', createdTime: '2025-04-24' },
  { title: '迟到的春天', author: '张三', categoryName: '历史传记', createdTime: '2025-05-01' },
  { title: 'AI 人工智能', author: '王五', categoryName: '计算机科学', createdTime: '2025-05-02' },
  { title: '深度学习导论', author: '赵六', categoryName: '计算机科学', createdTime: '2025-05-06' }
])

function goTarget(href) {
  window.open(href, '_blank')
}

function initStatistics() {
  statistics.value = {
    totalBooks: 172,
    availableBooks: 145,
    borrowedBooks: 27,
    totalCategories: 12
  }
}

function initCategoryChart() {
  if (!categoryChartRef.value) return
  const chart = echarts.init(categoryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle'
    },
    series: [
      {
        name: '图书分类',
        type: 'pie',
        radius: '60%',
        center: ['60%', '50%'],
        data: [
          { value: 45, name: '文学小说' },
          { value: 32, name: '计算机科学' },
          { value: 28, name: '历史传记' },
          { value: 25, name: '自然科学' },
          { value: 22, name: '工程技术' },
          { value: 20, name: '经济管理' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

function initBorrowTrendChart() {
  if (!borrowTrendChartRef.value) return
  const chart = echarts.init(borrowTrendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '借阅次数',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210],
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: 'rgba(64, 158, 255, 0.5)'
            },
            {
              offset: 1,
              color: 'rgba(64, 158, 255, 0.01)'
            }
          ])
        }
      }
    ]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

function viewAllBooks() {
  router.push('/book/book')
}

function viewNewBooks() {
  router.push('/book/book')
}

onMounted(() => {
  initStatistics()
  nextTick(() => {
    initCategoryChart()
    initBorrowTrendChart()
  })
})
</script>

<style scoped lang="scss">
.dashboard {
  padding: 20px;

  .dashboard-header {
    margin-bottom: 20px;

    h2 {
      margin-top: 0;
      color: #303133;
    }

    p {
      color: #606266;
      line-height: 1.8;
      margin-bottom: 10px;
    }
  }

  .dashboard-stats {
    margin-bottom: 20px;

    .stat-card {
      transition: all 0.3s;

      &:hover {
        transform: translateY(-5px);
      }

      .stat-content {
        display: flex;
        align-items: center;
        justify-content: space-around;
        padding: 10px 0;

        .stat-icon {
          width: 70px;
          height: 70px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;

          &.book-icon {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: #fff;
          }

          &.available-icon {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: #fff;
          }

          &.borrowed-icon {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: #fff;
          }

          &.category-icon {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            color: #fff;
          }
        }

        .stat-info {
          text-align: center;

          .stat-value {
            font-size: 32px;
            font-weight: bold;
            color: #303133;
            margin-bottom: 5px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }
      }
    }
  }

  .dashboard-charts {
    margin-bottom: 20px;

    .chart-card {
      height: 100%;

      .chart-container {
        height: 280px;
        width: 100%;
      }

      .rank-list {
        height: 280px;
        overflow-y: auto;

        .rank-item {
          display: flex;
          align-items: center;
          padding: 10px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .rank-number {
            width: 24px;
            height: 24px;
            border-radius: 50%;
            background: #e0e0e0;
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            margin-right: 12px;

            &.top {
              background: #f56c6c;
            }
          }

          .rank-title {
            flex: 1;
            color: #303133;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .rank-count {
            color: #909399;
            font-size: 12px;
            margin-left: 10px;
          }
        }
      }
    }
  }

  .dashboard-tables {
    .table-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: bold;
        font-size: 16px;
      }
    }
  }
}

:deep(.el-card__header) {
  padding: 15px 20px;
}
</style>
