<template>
  <div class="department-stats-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="search-row">
        <div class="search-item">
          <el-input v-model="searchParams.departmentName" placeholder="部门名称" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="search-item">
          <el-input v-model="searchParams.paperTitle" placeholder="问卷名称" clearable>
            <template #prefix>
              <el-icon><Document /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="search-item">
          <el-date-picker
            v-model="searchParams.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="loadData" :icon="Search">查询</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
          <el-button type="success" @click="exportData" :icon="Download">导出</el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card" v-for="card in statCards" :key="card.title" :style="{borderLeft: `4px solid ${card.color}`}">
        <div class="stat-icon" :style="{backgroundColor: `${card.color}20`}">
          <component :is="card.icon" :style="{color: card.color}" />
        </div>
        <div class="stat-content">
          <div class="stat-title">{{ card.title }}</div>
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-trend" v-if="card.trend !== undefined">
            <span :style="{color: card.trend >= 0 ? '#f56c6c' : '#67c23a'}">
              {{ card.trend >= 0 ? '↑' : '↓' }} {{ Math.abs(card.trend) }}%
            </span>
            较上月
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-area">
      <div class="chart-card">
        <div class="chart-title">部门健康分布</div>
        <div ref="healthChart" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <div class="chart-title">高风险用户分布</div>
        <div ref="riskChart" class="chart-container"></div>
      </div>
    </div>

    <!-- 部门表格 -->
    <div class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
        @row-click="handleRowClick"
      >
        <el-table-column type="expand">
          <template #default="{row}">
            <div class="questionnaire-table">
              <el-table :data="row.testPapers" border>
                <el-table-column prop="title" label="问卷名称" min-width="180">
                  <template #default="{row}">
                    <div class="paper-title">
                      <el-icon><Document /></el-icon>
                      <span>{{ row.title }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="publishTime" label="发布时间" width="150" align="center">
                  <template #default="{row}">
                    {{ formatDate(row.publishTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="completionRate" label="完成率" width="120" align="center">
                  <template #default="{row}">
                    <el-progress 
                      :percentage="row.completionRate * 100" 
                      :color="getRateColor(row.completionRate)"
                      :show-text="false"
                    />
                    <span class="rate-text">{{ (row.completionRate * 100).toFixed(1) }}%</span>
                  </template>
                </el-table-column>
                <el-table-column prop="avgScore" label="平均分" width="120" align="center">
                  <template #default="{row}">
                    <span :style="{color: getScoreColor(row.avgScore, row.totalScore)}">
                      {{ row.avgScore.toFixed(1) }}
                    </span>
                    / {{ row.totalScore }}
                  </template>
                </el-table-column>
                <el-table-column prop="highRiskCount" label="高风险" width="100" align="center">
                  <template #default="{row}">
                    <el-tag type="danger" v-if="row.highRiskCount > 0">
                      {{ row.highRiskCount }}
                    </el-tag>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="name" label="部门名称" min-width="180">
          <template #default="{row}">
            <div class="department-name">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="userCount" label="用户数" width="120" align="center">
          <template #default="{row}">
            <div class="user-count">
              <el-icon><User /></el-icon>
              <span>{{ row.userCount }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="testPaperCount" label="问卷数" width="120" align="center">
          <template #default="{row}">
            <div class="paper-count">
              <el-icon><Document /></el-icon>
              <span>{{ row.testPaperCount }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="completionRate" label="完成率" width="150" align="center">
          <template #default="{row}">
            <el-progress 
              :percentage="row.completionRate * 100" 
              :color="getRateColor(row.completionRate)"
            />
          </template>
        </el-table-column>
        
        <el-table-column prop="avgScore" label="平均分" width="120" align="center">
          <template #default="{row}">
            <span :style="{color: getScoreColor(row.avgScore, 100)}">
              {{ row.avgScore.toFixed(1) }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column prop="highRiskRate" label="高风险率" width="150" align="center">
          <template #default="{row}">
            <el-progress 
              :percentage="row.highRiskRate * 100" 
              :color="getRiskColor(row.highRiskRate)"
              :show-text="false"
            />
            <span class="rate-text">{{ (row.highRiskRate * 100).toFixed(1) }}%</span>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 空状态 -->
      <div v-if="!loading && tableData.length === 0" class="empty-state">
        <el-icon><DataLine /></el-icon>
        <div>暂无数据</div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { 
  Search, Refresh, Download, Document, 
  OfficeBuilding, User, DataLine 
} from '@element-plus/icons-vue'
import request from '@/utils/request'

// 搜索参数
const searchParams = reactive({
  departmentName: '',
  paperTitle: '',
  timeRange: []
})

// 分页参数
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 统计卡片数据
const statCards = reactive([
  { title: '总用户数', value: 0, icon: 'User', color: '#409EFF', trend: 0 },
  { title: '部门数量', value: 0, icon: 'OfficeBuilding', color: '#67C23A' },
  { title: '问卷总数', value: 0, icon: 'Document', color: '#9B59B6' },
  { title: '高风险用户', value: 0, icon: 'Warning', color: '#F56C6C', trend: 0 }
])

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 图表引用
const healthChart = ref(null)
const riskChart = ref(null)
let healthChartInstance = null
let riskChartInstance = null

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 获取统计数据
    const statsRes = await request.post('/department/stats', {
      ...searchParams,
      pageNum: pagination.current,
      pageSize: pagination.size,
      startTime: searchParams.timeRange?.[0],
      endTime: searchParams.timeRange?.[1]
    })
    
    if (statsRes.code === 200) {
      const data = statsRes.data
      statCards[0].value = data.totalUsers
      statCards[1].value = data.departmentCount
      statCards[2].value = data.totalTestPapers
      statCards[3].value = data.highRiskUsers
      
      // 更新表格数据
      tableData.value = data.list.map(dept => ({
        ...dept,
        completionRate: dept.completedCount / dept.userCount || 0,
        highRiskRate: dept.highRiskCount / dept.userCount || 0
      }))
      pagination.total = data.total
      
      // 初始化图表
      initCharts(data.chartData)
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化图表
const initCharts = (chartData) => {
  nextTick(() => {
    if (!healthChartInstance && healthChart.value) {
      healthChartInstance = echarts.init(healthChart.value)
    }
    if (!riskChartInstance && riskChart.value) {
      riskChartInstance = echarts.init(riskChart.value)
    }
    
    // 部门健康雷达图
    if (healthChartInstance) {
      const option = {
        tooltip: { trigger: 'item' },
        radar: {
          indicator: chartData.healthIndicators,
          radius: '65%'
        },
        series: [{
          type: 'radar',
          data: chartData.healthData,
          areaStyle: { opacity: 0.3 }
        }]
      }
      healthChartInstance.setOption(option)
    }
    
    // 高风险用户饼图
    if (riskChartInstance) {
      const option = {
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', right: 10, top: 'center' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          data: chartData.riskDistribution,
          emphasis: { itemStyle: { shadowBlur: 10 } }
        }]
      }
      riskChartInstance.setOption(option)
    }
  })
}

// 重置搜索
const resetSearch = () => {
  searchParams.departmentName = ''
  searchParams.paperTitle = ''
  searchParams.timeRange = []
  pagination.current = 1
  loadData()
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadData()
}

// 导出数据
const exportData = async () => {
  try {
    const res = await request.post('/department/export', {
      ...searchParams,
      startTime: searchParams.timeRange?.[0],
      endTime: searchParams.timeRange?.[1]
    }, { responseType: 'blob' })
    
    const url = window.URL.createObjectURL(new Blob([res]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '部门统计数据.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString()
}

// 获取分数颜色
const getScoreColor = (score, total) => {
  const percent = score / total
  if (percent < 0.6) return '#F56C6C'
  if (percent < 0.8) return '#E6A23C'
  return '#67C23A'
}

// 获取完成率颜色
const getRateColor = (rate) => {
  if (rate < 0.5) return '#F56C6C'
  if (rate < 0.8) return '#E6A23C'
  return '#67C23A'
}

// 获取风险率颜色
const getRiskColor = (rate) => {
  if (rate > 0.2) return '#F56C6C'
  if (rate > 0.1) return '#E6A23C'
  return '#67C23A'
}

// 行点击事件
const handleRowClick = (row) => {
  console.log('查看部门详情:', row.id)
}

// 初始化加载
onMounted(() => {
  loadData()
})

// 组件卸载时销毁图表
onUnmounted(() => {
  if (healthChartInstance) {
    healthChartInstance.dispose()
    healthChartInstance = null
  }
  if (riskChartInstance) {
    riskChartInstance.dispose()
    riskChartInstance = null
  }
})
</script>

<style scoped>
.department-stats-container {
  padding: 20px;
  background-color: #f5f7fa;
}

.search-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.search-item {
  flex: 1;
  min-width: 200px;
}

.search-actions {
  display: flex;
  gap: 10px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.stat-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-trend {
  font-size: 12px;
  color: #909399;
}

.chart-area {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

@media (max-width: 1200px) {
  .chart-area {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .chart-area {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
}

.chart-container {
  height: 300px;
}

.table-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.questionnaire-table {
  padding: 10px;
}

.paper-title, .department-name, .user-count, .paper-count {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rate-text {
  margin-left: 8px;
  font-size: 14px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #909399;
}

.empty-state svg {
  width: 60px;
  height: 60px;
  margin-bottom: 10px;
}

.pagination {
  background-color: #fff;
  border-radius: 4px;
  padding: 15px 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

:deep(.el-progress-bar) {
  padding-right: 0;
  margin-right: 0;
}

.health-details-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2A5C8A;
}

.health-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.health-stat-item {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-desc {
  font-size: 12px;
  color: #909399;
}

.text-success {
  color: #67C23A;
}

.text-warning {
  color: #E6A23C;
}

.text-danger {
  color: #F56C6C;
}

.health-distribution {
  margin-bottom: 20px;
}

.distribution-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #2A5C8A;
}

.distribution-chart {
  height: 300px;
}

.health-recommendations {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 20px;
}

.recommendations-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #2A5C8A;
}

.recommendations-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

@media (max-width: 768px) {
  .recommendations-content {
    grid-template-columns: 1fr;
  }
  
  .health-stats {
    grid-template-columns: 1fr;
  }
}

.recommendation-item {
  display: flex;
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.recommendation-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: #ecf5ff;
  border-radius: 8px;
  margin-right: 15px;
  color: #409EFF;
}

.recommendation-title {
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
}

.recommendation-desc {
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
}
</style>