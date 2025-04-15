<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <!-- 总员工数卡片 -->
      <div class="stats-card bg-gradient-to-br from-blue-50 to-white">
        <div class="icon-container bg-blue-100">
          <svg class="text-blue-600" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
        </div>
        <div class="stats-content">
          <div class="stats-title">总员工数</div>
          <div class="stats-value">{{ formatNumber(data.baseData.userNum) }}</div>
          <div class="stats-trend" v-if="data.baseData.employeeChange">
            <span :class="trendClass(data.baseData.employeeChange)">
              {{ trendIcon(data.baseData.employeeChange) }} {{ Math.abs(data.baseData.employeeChange) }}%
            </span>
            <span class="text-xs text-gray-500 ml-1">较上月</span>
          </div>
        </div>
      </div>
      
      <!-- 活跃心理咨询师卡片 -->
      <div class="stats-card bg-gradient-to-br from-green-50 to-white">
        <div class="icon-container bg-green-100">
          <svg class="text-green-600" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="8.5" cy="7" r="4"></circle>
            <line x1="20" y1="8" x2="20" y2="14"></line>
            <line x1="23" y1="11" x2="17" y2="11"></line>
          </svg>
        </div>
        <div class="stats-content">
          <div class="stats-title">心理咨询师</div>
          <div class="stats-value">{{ formatNumber(data.baseData.doctorNum) }}</div>
          <div class="stats-trend" v-if="data.baseData.doctorChange">
            <span :class="trendClass(data.baseData.doctorChange)">
              {{ trendIcon(data.baseData.doctorChange) }} {{ Math.abs(data.baseData.doctorChange) }}%
            </span>
            <span class="text-xs text-gray-500 ml-1">较上月</span>
          </div>
        </div>
      </div>
      
      <!-- 试卷数量卡片 -->
      <div class="stats-card bg-gradient-to-br from-purple-50 to-white">
        <div class="icon-container bg-purple-100">
          <svg class="text-purple-600" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
        </div>
        <div class="stats-content">
          <div class="stats-title">试卷数量</div>
          <div class="stats-value">{{ formatNumber(data.baseData.testPaperNum) }}</div>
          <div class="stats-trend" v-if="data.baseData.testPaperChange">
            <span :class="trendClass(data.baseData.testPaperChange)">
              {{ trendIcon(data.baseData.testPaperChange) }} {{ Math.abs(data.baseData.testPaperChange) }}%
            </span>
            <span class="text-xs text-gray-500 ml-1">较上月</span>
          </div>
        </div>
      </div>
      
      <!-- 心理宣传数量卡片 -->
      <div class="stats-card bg-gradient-to-br from-amber-50 to-white">
        <div class="icon-container bg-amber-100">
          <svg class="text-amber-600" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
          </svg>
        </div>
        <div class="stats-content">
          <div class="stats-title">心理宣传数量</div>
          <div class="stats-value">{{ formatNumber(data.baseData.propagateNum) }}</div>
          <div class="stats-trend" v-if="data.baseData.propagateChange">
            <span :class="trendClass(data.baseData.propagateChange)">
              {{ trendIcon(data.baseData.propagateChange) }} {{ Math.abs(data.baseData.propagateChange) }}%
            </span>
            <span class="text-xs text-gray-500 ml-1">较上月</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="grid grid-cols-1 gap-6">
      <!-- 心理健康测试趋势 -->
      <div class="dashboard-card">
        <div class="card-header">
          <h3 class="card-title">心理健康测试趋势</h3>
          <div class="time-filter">
            <button class="time-filter-btn" :class="{ active: trendTimeRange === 'week' }" @click="changeTrendTimeRange('week')">周</button>
            <button class="time-filter-btn" :class="{ active: trendTimeRange === 'month' }" @click="changeTrendTimeRange('month')">月</button>
            <button class="time-filter-btn" :class="{ active: trendTimeRange === 'quarter' }" @click="changeTrendTimeRange('quarter')">季</button>
          </div>
        </div>
        <div class="chart-container" ref="lineChartRef"></div>
      </div>
      
      <!-- 部门健康和高风险员工 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 部门心理健康状况 -->
        <div class="dashboard-card">
          <div class="card-header">
            <h3 class="card-title">部门心理健康状况</h3>
            <div class="health-legend">
              <span class="health-legend-item"><span class="health-dot bg-green-500"></span>健康</span>
              <span class="health-legend-item"><span class="health-dot bg-yellow-500"></span>亚健康</span>
              <span class="health-legend-item"><span class="health-dot bg-red-500"></span>不健康</span>
            </div>
          </div>
          <div class="chart-container" ref="departmentChartRef"></div>
        </div>
        
        <!-- 最新评估完成情况 -->
        <div class="dashboard-card">
          <div class="card-header">
            <h3 class="card-title">最新评估完成情况</h3>
            <span class="text-xs text-gray-500">最近7天</span>
          </div>
          <div class="h-[350px] overflow-y-auto space-y-3 p-2">
            <div
              v-for="(item, index) in data.recentAssessments"
              :key="index"
              class="flex items-center gap-3 p-2 bg-gray-50 rounded-lg"
            >
              <div class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden">
                <img
                  v-if="item.avatar"
                  :src="item.avatar"
                  alt="User avatar"
                  class="object-cover"
                />
                <span v-else>{{ getInitials(item.name) }}</span>
              </div>
              <div class="flex-1">
                <div class="flex justify-between">
                  <span class="text-sm font-medium">{{ item.name }}</span>
                  <span class="text-xs text-gray-500">{{ formatTimeAgo(item.time) }}</span>
                </div>
                <div class="text-xs text-gray-500">完成了{{ item.assessmentName }}</div>
              </div>
            </div>
            <div v-if="!data.recentAssessments || data.recentAssessments.length === 0" class="text-center text-gray-500 py-4">
              暂无评估完成记录
            </div>
          </div>
        </div>
      </div>
      
      <!-- 高风险员工列表 -->
      <div class="dashboard-card">
        <div class="card-header">
          <h3 class="card-title">高风险员工列表</h3>
          <span class="text-xs text-gray-500">最近30天</span>
        </div>
        <div class="overflow-auto max-h-[400px]">
          <table class="employee-table">
            <thead>
              <tr>
                <th class="employee-th">姓名</th>
                <th class="employee-th">部门</th>
                <th class="employee-th">分数</th>
                <th class="employee-th">时间</th>
              </tr>
            </thead>
            <tbody>
              <tr 
                v-for="(employee, index) in data.highRiskEmployees" 
                :key="index"
                class="employee-tr"
              >
                <td class="employee-td">
                  <div class="flex items-center">
                    <div class="avatar-container">
                      {{ getInitials(employee.name) }}
                    </div>
                    <span>{{ employee.name }}</span>
                  </div>
                </td>
                <td class="employee-td">{{ employee.department }}</td>
                <td class="employee-td">
                  <span class="score-badge" :class="getScoreClass(employee.score)">
                    {{ employee.score }}
                  </span>
                </td>
                <td class="employee-td">{{ formatDate(employee.testTime) }}</td>
              </tr>
              <tr v-if="data.highRiskEmployees.length === 0">
                <td colspan="4" class="employee-td text-center">暂无高风险员工</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import * as echarts from "echarts";

const lineChartRef = ref(null);
const departmentChartRef = ref(null);
const propagandaChartRef = ref(null);
const assessmentChartRef = ref(null);

const trendTimeRange = ref('month'); // week/month/quarter

const data = reactive({
  baseData: {
    totalEmployees: 0,
    activeDoctors: 0,
    testPaperNum: 0,
    propagateNum: 0,
    employeeChange: 0,
    doctorChange: 0,
    testPaperChange: 0,
    propagateChange: 0
  },
  highRiskEmployees: [],
  recentAssessments: []
});

let charts = {
  line: null,
  department: null,
  propaganda: null,
  assessment: null
};

// 格式化数字
const formatNumber = (num) => {
  return num ? num.toLocaleString() : '0';
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}/${date.getDate()}`;
};

// 获取姓名首字母
const getInitials = (name) => {
  if (!name) return '';
  return name.slice(0, 1);
};

// 获取分数样式类
const getScoreClass = (score) => {
  if (score < 40) return 'score-critical';
  if (score < 60) return 'score-warning';
  return 'score-normal';
};

// 趋势变化样式
const trendClass = (change) => {
  return change > 0 ? 'text-green-600' : change < 0 ? 'text-red-600' : 'text-gray-500';
};

// 趋势变化图标
const trendIcon = (change) => {
  return change > 0 ? '↑' : change < 0 ? '↓' : '→';
};

// 更改趋势时间范围
const changeTrendTimeRange = (range) => {
  trendTimeRange.value = range;
  loadLineData();
};

// 格式化相对时间
const formatTimeAgo = (dateStr) => {
  if (!dateStr) return '';
  
  const now = new Date();
  const date = new Date(dateStr);
  const diffMs = now - date;
  const diffSec = Math.floor(diffMs / 1000);
  const diffMin = Math.floor(diffSec / 60);
  const diffHour = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHour / 24);
  
  if (diffDay > 0) {
    return `${diffDay}天前`;
  } else if (diffHour > 0) {
    return `${diffHour}小时前`;
  } else if (diffMin > 0) {
    return `${diffMin}分钟前`;
  } else {
    return '刚刚';
  }
};

// 加载基础数据
const loadBaseData = async () => {
  try {
    const res = await request.get('/statistics/base');
    if (res.code === '200') {
      data.baseData = {
        ...data.baseData,
        ...res.data
      };
    } else {
      ElMessage.error(res.msg || '加载基础数据失败');
    }
  } catch (err) {
    console.error('Failed to load base data:', err);
    ElMessage.error('加载基础数据失败');
  }
};

// 加载高风险员工数据
const loadHighRiskEmployees = async () => {
  try {
    const res = await request.get('/statistics/high-risk-employees');
    if (res.code === '200') {
      data.highRiskEmployees = res.data || [];
    } else {
      ElMessage.error(res.msg || '加载高风险员工数据失败');
    }
  } catch (err) {
    console.error('Failed to load high risk employees:', err);
    ElMessage.error('加载高风险员工数据失败');
  }
};

// 加载最新评估完成情况数据
const loadRecentAssessments = async () => {
  try {
    const res = await request.get('/statistics/recent-assessments');
    if (res.code === '200') {
      data.recentAssessments = res.data || [];
    } else {
      ElMessage.error(res.msg || '加载最新评估完成情况失败');
    }
  } catch (err) {
    console.error('Failed to load recent assessments:', err);
    ElMessage.error('加载最新评估完成情况失败');
  }
};

// 初始化折线图 - 心理健康趋势
const initLineChart = () => {
  if (!lineChartRef.value) return;
  
  nextTick(() => {
    if (charts.line) {
      charts.line.dispose();
    }
    
    charts.line = echarts.init(lineChartRef.value);
    charts.line.showLoading();
    
    const options = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: params => {
          let result = `${params[0].axisValue}<br>`;
          params.forEach(item => {
            result += `${item.marker} ${item.seriesName}: <strong>${item.value}</strong> 人<br>`;
          });
          return result;
        }
      },
      legend: {
        data: ['健康', '亚健康', '不健康'],
        bottom: 0,
        textStyle: {
          color: '#666'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: [],
        axisLine: {
          lineStyle: {
            color: '#ddd'
          }
        },
        axisLabel: {
          color: '#666'
        }
      },
      yAxis: {
        type: 'value',
        name: '人数',
        nameTextStyle: {
          color: '#666'
        },
        axisLine: {
          show: false
        },
        axisLabel: {
          color: '#666'
        },
        splitLine: {
          lineStyle: {
            color: '#eee'
          }
        }
      },
      series: [
        {
          name: '健康',
          type: 'line',
          data: [],
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          itemStyle: {
            color: '#52c41a'
          },
          lineStyle: {
            width: 3
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
              { offset: 1, color: 'rgba(82, 196, 26, 0.1)' }
            ])
          }
        },
        {
          name: '亚健康',
          type: 'line',
          data: [],
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          itemStyle: {
            color: '#faad14'
          },
          lineStyle: {
            width: 3
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(250, 173, 20, 0.3)' },
              { offset: 1, color: 'rgba(250, 173, 20, 0.1)' }
            ])
          }
        },
        {
          name: '不健康',
          type: 'line',
          data: [],
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          itemStyle: {
            color: '#f5222d'
          },
          lineStyle: {
            width: 3
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(245, 34, 45, 0.3)' },
              { offset: 1, color: 'rgba(245, 34, 45, 0.1)' }
            ])
          }
        }
      ]
    };
    
    charts.line.setOption(options);
    loadLineData();
  });
};

// 初始化部门健康图表
const initDepartmentChart = () => {
  if (!departmentChartRef.value) return;
  
  nextTick(() => {
    if (charts.department) {
      charts.department.dispose();
    }
    
    charts.department = echarts.init(departmentChartRef.value);
    charts.department.showLoading();
    
    const options = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: params => {
          const value = params[0].value;
          let status = '';
          if (value >= 80) status = '健康';
          else if (value >= 60) status = '亚健康';
          else status = '不健康';
          
          return `${params[0].axisValue}<br>平均分数: <strong>${value}</strong><br>状态: <strong>${status}</strong>`;
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: [],
        axisLabel: {
          interval: 0,
          rotate: -45,
          color: '#666',
          fontSize: 12
        },
        axisLine: {
          lineStyle: {
            color: '#ddd'
          }
        }
      },
      yAxis: {
        type: 'value',
        name: '平均分数',
        nameTextStyle: {
          color: '#666'
        },
        axisLine: {
          show: false
        },
        axisLabel: {
          color: '#666'
        },
        splitLine: {
          lineStyle: {
            color: '#eee'
          }
        },
        max: 100,
        min: 0
      },
      series: [
        {
          type: 'bar',
          data: [],
          barWidth: '50%',
          itemStyle: {
            color: function(params) {
              const value = params.value;
              if (value >= 80) return '#52c41a'; // 健康
              if (value >= 60) return '#faad14'; // 亚健康
              return '#f5222d'; // 不健康
            },
            borderRadius: [4, 4, 0, 0]
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}',
            color: '#666'
          }
        }
      ]
    };
    
    charts.department.setOption(options);
    loadDepartmentData();
  });
};

// 加载折线图数据
const loadLineData = async () => {
  try {
    if (!charts.line) return;
    
    charts.line.showLoading();
    const res = await request.get('/statistics/health-trend', {
      params: { range: trendTimeRange.value }
    });
    
    if (res.code === '200') {
      charts.line.hideLoading();
      charts.line.setOption({
        xAxis: {
          data: res.data.xAxis || []
        },
        series: [
          { data: res.data.healthy || [] },
          { data: res.data.subHealthy || [] },
          { data: res.data.unhealthy || [] }
        ]
      });
    } else {
      ElMessage.error(res.msg || '加载心理健康趋势数据失败');
      charts.line.hideLoading();
    }
  } catch (err) {
    console.error('Failed to load line chart data:', err);
    ElMessage.error('加载心理健康趋势数据失败');
    if (charts.line) {
      charts.line.hideLoading();
    }
  }
};

// 加载部门健康数据
const loadDepartmentData = async () => {
  try {
    if (!charts.department) return;
    
    charts.department.showLoading();
    const res = await request.get('/statistics/department-health');
    
    if (res.code === '200') {
      charts.department.hideLoading();
      charts.department.setOption({
        xAxis: {
          data: res.data.departments || []
        },
        series: [{
          data: res.data.scores || []
        }]
      });
    } else {
      ElMessage.error(res.msg || '加载部门健康数据失败');
      charts.department.hideLoading();
    }
  } catch (err) {
    console.error('Failed to load department health data:', err);
    ElMessage.error('加载部门健康数据失败');
    if (charts.department) {
      charts.department.hideLoading();
    }
  }
};

// 处理窗口大小变化
const handleResize = () => {
  Object.values(charts).forEach(chart => {
    if (chart) chart.resize();
  });
};

// 组件挂载
onMounted(async () => {
  await Promise.all([
    loadBaseData(),
    loadHighRiskEmployees(),
    loadRecentAssessments()
  ]);
  
  initLineChart();
  initDepartmentChart();
  
  window.addEventListener('resize', handleResize);
});

// 组件卸载
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  
  Object.values(charts).forEach(chart => {
    if (chart) chart.dispose();
  });
  
  charts = { line: null, department: null };
});
</script>

<style scoped>
.dashboard-container {
  @apply w-full h-full overflow-y-auto p-4 space-y-6 bg-gray-50;
}

/* 统计卡片样式 */
.stats-card {
  @apply rounded-lg shadow-sm p-4 flex items-center transition-all hover:shadow-md border border-gray-100;
}

.icon-container {
  @apply flex-shrink-0 p-3 rounded-lg mr-4;
}

.stats-content {
  @apply flex-1;
}

.stats-title {
  @apply text-sm font-medium text-gray-500;
}

.stats-value {
  @apply text-2xl font-bold text-gray-800 my-1;
}

.stats-trend {
  @apply text-xs flex items-center;
}

/* 仪表盘卡片通用样式 */
.dashboard-card {
  @apply bg-white rounded-lg shadow-sm p-5 border border-gray-100;
}

.card-header {
  @apply flex justify-between items-center mb-4;
}

.card-title {
  @apply text-lg font-semibold text-gray-800;
}

/* 时间筛选按钮 */
.time-filter {
  @apply flex space-x-2;
}

.time-filter-btn {
  @apply px-3 py-1 text-xs rounded-md border border-gray-300 text-gray-600 hover:bg-gray-100 transition-colors;
}

.time-filter-btn.active {
  @apply bg-blue-50 border-blue-200 text-blue-600;
}

/* 健康状态图例 */
.health-legend {
  @apply flex space-x-4;
}

.health-legend-item {
  @apply flex items-center text-xs text-gray-600;
}

.health-dot {
  @apply w-2 h-2 rounded-full mr-1;
}

/* 员工表格样式 */
.employee-table {
  @apply min-w-full divide-y divide-gray-200;
}

.employee-th {
  @apply px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider;
}

.employee-tr {
  @apply hover:bg-gray-50 transition-colors;
}

.employee-td {
  @apply px-4 py-3 whitespace-nowrap text-sm;
}

/* 头像样式 */
.avatar-container {
  @apply w-6 h-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center mr-2 text-xs font-medium;
}

/* 分数徽章 */
.score-badge {
  @apply px-2 py-1 rounded-full text-xs font-semibold;
}

.score-critical {
  @apply bg-red-100 text-red-800;
}

.score-warning {
  @apply bg-yellow-100 text-yellow-800;
}

.score-normal {
  @apply bg-green-100 text-green-800;
}

/* 图表容器 */
.chart-container {
  @apply w-full h-[350px];
}

/* 响应式调整 */
@media (max-width: 640px) {
  .dashboard-container {
    @apply p-2;
  }
  
  .chart-container {
    height: 250px;
  }
  
  .card-header {
    @apply flex-col items-start space-y-2;
  }
  
  .time-filter {
    @apply self-end;
  }
}
</style>