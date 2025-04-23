<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>
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
        </div>
      </div>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="grid grid-cols-1 gap-6">
      <!-- 心理健康测试趋势 -->
      <div class="dashboard-card">
        <div class="card-header">
          <h3 class="card-title">心理健康测试趋势</h3>
          <div class="flex items-center gap-4">
            <!-- 添加健康状态图例 -->
            <div class="health-trend-legend hidden md:flex items-center space-x-3">
              <span class="health-legend-item"><span class="health-dot bg-green-500"></span>优秀/良好 (≥75)</span>
              <span class="health-legend-item"><span class="health-dot bg-yellow-500"></span>一般 (65-74)</span>
              <span class="health-legend-item"><span class="health-dot bg-orange-500"></span>需改善 (50-64)</span>
              <span class="health-legend-item"><span class="health-dot bg-red-500"></span>需关注 (<50)</span>
            </div>
            <div class="time-filter">
              <button class="time-filter-btn" :class="{ active: trendTimeRange === 'week' }" @click="changeTrendTimeRange('week')">周</button>
              <button class="time-filter-btn" :class="{ active: trendTimeRange === 'month' }" @click="changeTrendTimeRange('month')">月</button>
              <button class="time-filter-btn" :class="{ active: trendTimeRange === 'quarter' }" @click="changeTrendTimeRange('quarter')">季</button>
            </div>
          </div>
        </div>
        <div class="chart-container relative" ref="lineChartRef">
          <div v-if="chartLoading" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
            <div class="flex flex-col items-center">
              <div class="loading-spinner mb-2"></div>
              <span class="text-sm text-gray-500">加载中...</span>
            </div>
          </div>
        </div>
        <!-- 移动端显示的图例 -->
        <div class="health-trend-legend flex md:hidden flex-wrap justify-center mt-4 gap-2">
          <span class="health-legend-item"><span class="health-dot bg-green-500"></span>优秀/良好 (≥75)</span>
          <span class="health-legend-item"><span class="health-dot bg-yellow-500"></span>一般 (65-74)</span>
          <span class="health-legend-item"><span class="health-dot bg-orange-500"></span>需改善 (50-64)</span>
          <span class="health-legend-item"><span class="health-dot bg-red-500"></span>需关注 (<50)</span>
        </div>
      </div>
      
      <!-- 部门健康和高风险员工 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- 部门心理健康状况 -->
        <div class="dashboard-card">
          <div class="card-header">
            <h3 class="card-title">部门心理健康状况</h3>
            <div class="health-legend">
              <span class="health-legend-item"><span class="health-dot bg-green-500"></span>优秀/良好</span>
              <span class="health-legend-item"><span class="health-dot bg-yellow-500"></span>一般</span>
              <span class="health-legend-item"><span class="health-dot bg-orange-500"></span>需改善</span>
              <span class="health-legend-item"><span class="health-dot bg-red-500"></span>需关注</span>
            </div>
          </div>
          <div class="chart-container relative" ref="departmentChartRef" style="height: 400px;">
            <div v-if="departmentChartLoading" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-70 z-10">
              <div class="flex flex-col items-center">
                <div class="loading-spinner mb-2"></div>
                <span class="text-sm text-gray-500">加载中...</span>
              </div>
            </div>
          </div>
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
          <div class="flex items-center gap-2">
            <span class="text-xs text-gray-500">最近30天</span>
            <button 
              @click="exportHighRiskEmployees" 
              class="p-1.5 bg-blue-50 text-blue-600 rounded-md hover:bg-blue-100 transition-colors"
              title="导出Excel"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="7 10 12 15 17 10"></polyline>
                <line x1="12" y1="15" x2="12" y2="3"></line>
              </svg>
            </button>
          </div>
        </div>
        <div class="overflow-auto max-h-[400px]">
          <table class="employee-table">
            <thead>
              <tr>
                <th class="employee-th">姓名</th>
                <th class="employee-th">部门</th>
                <th class="employee-th">综合评分</th>
                <th class="employee-th">健康状态</th>
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
                  <span class="score-badge" :class="getScoreClass(employee.combinedScore || employee.score)">
                    {{ employee.combinedScore || employee.score }}
                  </span>
                </td>
                <td class="employee-td">
                  <span class="health-status-badge" :class="getHealthStatusClass(employee.combinedScore || employee.score)">
                    {{ getHealthStatus(employee.combinedScore || employee.score) }}
                  </span>
                </td>
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
import { reactive, ref, onMounted, onBeforeUnmount, nextTick, onUnmounted, watch } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import * as echarts from "echarts";
import * as XLSX from 'xlsx'; // 需要安装: npm install xlsx

const lineChartRef = ref(null);
const departmentChartRef = ref(null);
const propagandaChartRef = ref(null);
const assessmentChartRef = ref(null);

const trendTimeRange = ref('month'); // week/month/quarter

const data = reactive({
  baseData: {
    userNum: 0,
    doctorNum: 0,
    testPaperNum: 0,
    propagateNum: 0
  },
  highRiskEmployees: [],
  recentAssessments: [],
  pageNum: 1,
  pageSize: 5,
  total: 0
});

const charts = reactive({
  line: null,
  department: null,
  propaganda: null,
  assessment: null
});

const chartLoading = ref(false);
const departmentChartLoading = ref(false);
const highRiskLoading = ref(false);

// 添加组件挂载状态标志
const isMounted = ref(false);
const isInitialized = ref(false);

// 添加图表初始化状态跟踪
const chartInitStatus = reactive({
  line: false,
  department: false
});

// 添加一个全局错误处理函数
const handleChartError = (operation, chartName, error) => {
  console.error(`图表操作失败 [${chartName}] [${operation}]:`, error);
  // 不向用户显示技术错误，避免用户体验受影响
};

// 安全的图表操作函数
const safeChartOperation = (chart, operation, params = []) => {
  if (!chart) return false;
  
  try {
    return chart[operation](...params);
  } catch (error) {
    handleChartError(operation, chart.__chartName || 'unknown', error);
    return false;
  }
};

// 修改图表初始化逻辑
const initCharts = () => {
  // 清除任何现有的初始化计时器
  if (window.__chartInitTimers) {
    Object.values(window.__chartInitTimers).forEach(timer => clearTimeout(timer));
  }
  window.__chartInitTimers = {};
  
  // 确保组件已挂载
  if (!isMounted.value) return;
  
  // 使用一个函数来安全地初始化单个图表
  const initSingleChart = (chartRef, chartKey, options, delay = 0) => {
    window.__chartInitTimers[chartKey] = setTimeout(() => {
      if (!isMounted.value) return;
      
      try {
        // 检查DOM元素
        if (!chartRef.value) {
          console.log(`${chartKey}图表DOM元素不存在，跳过初始化`);
          return;
        }
        
        // 销毁现有实例
        if (charts[chartKey]) {
          safeChartOperation(charts[chartKey], 'dispose');
          charts[chartKey] = null;
        }
        
        // 创建新实例
        charts[chartKey] = echarts.init(chartRef.value);
        charts[chartKey].__chartName = chartKey;
        chartInitStatus[chartKey] = true;
        
        // 设置基本选项
        safeChartOperation(charts[chartKey], 'setOption', [options]);
        
        // 显示加载状态
        safeChartOperation(charts[chartKey], 'showLoading', [{
          text: '加载中...',
          maskColor: 'rgba(255, 255, 255, 0.8)',
          textColor: '#666'
        }]);
        
        // 加载数据
        if (chartKey === 'line') {
          loadLineData();
        } else if (chartKey === 'department') {
          loadDepartmentData();
        }
        
        console.log(`${chartKey}图表初始化成功`);
      } catch (err) {
        console.error(`${chartKey}图表初始化失败:`, err);
        chartInitStatus[chartKey] = false;
      }
    }, delay);
  };
  
  // 初始化趋势图表
  const lineOptions = {
    grid: { left: '3%', right: '4%', bottom: '10%', top: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: [] },
    yAxis: { type: 'value', name: '人数' },
    series: [{ name: '数据', type: 'line', data: [] }]
  };
  
  // 初始化部门图表
  const departmentOptions = {
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value' },
    series: [{ name: '健康评分', type: 'bar', data: [] }]
  };
  
  // 按顺序初始化图表，避免并发操作
  initSingleChart(lineChartRef, 'line', lineOptions, 100);
  initSingleChart(departmentChartRef, 'department', departmentOptions, 800);
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
  if (!score && score !== 0) return '';
  if (score >= 85) return 'score-excellent';
  if (score >= 75) return 'score-good';
  if (score >= 65) return 'score-normal';
  if (score >= 50) return 'score-warning';
  return 'score-critical';
};

// 获取健康状态
const getHealthStatus = (score) => {
  if (!score && score !== 0) return '暂无评级';
  if (score >= 85) return '优秀';
  if (score >= 75) return '良好';
  if (score >= 65) return '一般';
  if (score >= 50) return '需改善';
  return '需关注';
};

// 获取健康状态样式类
const getHealthStatusClass = (score) => {
  if (!score && score !== 0) return 'bg-gray-100 text-gray-600';
  if (score >= 85) return 'bg-green-100 text-green-800';
  if (score >= 75) return 'bg-green-100 text-green-800';
  if (score >= 65) return 'bg-yellow-100 text-yellow-800';
  if (score >= 50) return 'bg-orange-100 text-orange-800';
  return 'bg-red-100 text-red-800';
};

// 切换趋势时间范围
const changeTrendTimeRange = (range) => {
  if (trendTimeRange.value === range) return;
  
  trendTimeRange.value = range;
  console.log('切换趋势时间范围:', range);
  
  // 如果图表已初始化，重新加载数据
  if (charts.line) {
    loadLineData();
  }
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

// 格式化日期显示
const formatChartDate = (dateStr, range) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  
  if (range === 'week') {
    return `${date.getMonth() + 1}/${date.getDate()}`;
  } else if (range === 'quarter') {
    return `${date.getMonth() + 1}月`;
  } else {
    return `${date.getMonth() + 1}/${date.getDate()}`;
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
      console.log("基础数据加载成功:", data.baseData);
    } else {
      console.error("加载基础数据失败:", res.msg);
      ElMessage.error(res.msg || '加载基础数据失败');
    }
  } catch (err) {
    console.error('Failed to load base data:', err);
    ElMessage.error('加载基础数据失败: ' + (err.message || err));
  }
};

// 加载高风险员工数据
const loadHighRiskEmployees = async () => {
  try {
    highRiskLoading.value = true;
    const res = await request.get('/statistics/high-risk-employees');
    console.log('原始高风险员工数据:', res.data);
    
    if (res.code === '200') {
      // 处理高风险员工数据
      const highRiskData = res.data.map(item => {
        // 检查是否缺少部门信息
        if (!item.department) {
          console.log('发现没有部门的员工数据:', item);
          item.department = '未分配部门'; // 为缺少部门的员工添加默认部门
        }
        
        // 检查是否缺少ID
        if (!item.id) {
          console.log('发现没有ID的员工数据:', item);
        }
        
        return {
          id: item.id || `temp-${Math.random().toString(36).substr(2, 9)}`, // 为缺少ID的员工生成临时ID
          name: item.name || '未知员工',
          department: item.department || '未分配部门',
          score: item.score,
          testTime: item.testTime,
          // 添加其他可能需要的字段
        };
      });
      
      // 按分数升序排序，分数越低风险越高
      highRiskData.sort((a, b) => a.score - b.score);
      
      // 如果同一员工有多条记录，只保留分数最低的一条
      const uniqueEmployees = {};
      highRiskData.forEach(item => {
        const key = item.name;
        if (!uniqueEmployees[key] || item.score < uniqueEmployees[key].score) {
          uniqueEmployees[key] = item;
        }
      });
      
      data.highRiskEmployees = Object.values(uniqueEmployees);
      console.log('处理后的高风险员工数据:', data.highRiskEmployees);
    }
  } catch (err) {
    console.error('Failed to load high risk employees:', err);
    ElMessage.error('加载高风险员工数据失败');
  } finally {
    highRiskLoading.value = false;
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

// 加载健康趋势数据
const loadLineData = async () => {
  try {
    // 检查组件是否仍然挂载
    if (!isMounted.value) {
      console.log('组件已卸载，取消加载趋势数据');
      return;
    }
    
    // 检查图表实例是否存在
    if (!charts.line || !chartInitStatus.line) {
      console.error('趋势图表未初始化');
      return;
    }
    
    chartLoading.value = true;
    
    // 安全地显示加载状态
    safeChartOperation(charts.line, 'showLoading', [{
      text: '加载中...',
      maskColor: 'rgba(255, 255, 255, 0.8)',
      textColor: '#666'
    }]);
    
    console.log('发送健康趋势数据请求, 时间范围:', trendTimeRange.value);
    
    const res = await request.get('/statistics/health-trend', {
      params: { range: trendTimeRange.value }
    });
    
    console.log('收到健康趋势数据响应:', res);
    
    // 再次检查组件是否仍然挂载
    if (!isMounted.value || !charts.line) {
      console.log('组件已卸载或图表已销毁，取消处理趋势数据');
      return;
    }
    
    // 安全地隐藏加载状态
    safeChartOperation(charts.line, 'hideLoading');
    
    if (res.code === '200' && res.data) {
      const xAxis = res.data.xAxis || [];
      const healthy = res.data.healthy || [];
      const subHealthy = res.data.subHealthy || [];
      const unhealthy = res.data.unhealthy || [];
      const avgScores = res.data.avgScores || [];
      const testCounts = res.data.testCounts || [];
      
      // 如果没有数据，显示空状态
      if (xAxis.length === 0) {
        safeChartOperation(charts.line, 'setOption', [{
          title: {
            text: '暂无趋势数据',
            left: 'center',
            top: 'center',
            textStyle: {
              color: '#999',
              fontSize: 16,
              fontWeight: 'normal'
            }
          },
          xAxis: { type: 'category', data: [] },
          yAxis: { type: 'value' },
          series: [{ name: '数据', type: 'line', data: [] }]
        }]);
        chartLoading.value = false;
        return;
      }
      
      // 格式化X轴日期显示
      const formattedXAxis = xAxis.map(date => formatChartDate(date, trendTimeRange.value));
      
      // 使用简化的tooltip配置，确保基本功能正常
      const options = {
        tooltip: {
          show: true,
          trigger: 'axis',
          confine: true, // 确保tooltip不会超出容器边界
          enterable: true, // 允许鼠标进入tooltip
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e2e8f0',
          borderWidth: 1,
          borderRadius: 4,
          padding: [12, 16],
          textStyle: {
            color: '#4b5563',
            fontSize: 13
          },
          formatter: function(params) {
            if (!params || params.length === 0) return '数据不可用';
            
            // 确保索引有效
            const dataIndex = params[0].dataIndex;
            if (dataIndex >= xAxis.length) return '数据不可用';
            
            const date = xAxis[dataIndex];
            let healthyCount = 0;
            let subHealthyCount = 0;
            let unhealthyCount = 0;
            
            // 获取各类别数据
            params.forEach(param => {
              if (param.seriesName === '优秀/良好') {
                healthyCount = param.value || 0;
              } else if (param.seriesName === '一般') {
                subHealthyCount = param.value || 0;
              } else if (param.seriesName === '需改善/需关注') {
                unhealthyCount = param.value || 0;
              }
            });
            
            // 计算总数和百分比
            const totalCount = healthyCount + subHealthyCount + unhealthyCount;
            const healthyPercent = totalCount > 0 ? (healthyCount / totalCount * 100).toFixed(1) : 0;
            const subHealthyPercent = totalCount > 0 ? (subHealthyCount / totalCount * 100).toFixed(1) : 0;
            const unhealthyPercent = totalCount > 0 ? (unhealthyCount / totalCount * 100).toFixed(1) : 0;
            
            // 获取平均分和测试次数
            const avgScore = avgScores && avgScores[dataIndex] !== undefined ? avgScores[dataIndex].toFixed(1) : '暂无数据';
            const testCount = testCounts && testCounts[dataIndex] !== undefined ? testCounts[dataIndex] : '暂无数据';
            
            // 使用简单的HTML结构，避免复杂布局可能导致的问题
            return `
              <div style="font-weight:bold;margin-bottom:8px;font-size:14px;">${date}</div>
              <div>优秀/良好: <b>${healthyCount}</b> (${healthyPercent}%)</div>
              <div>一般: <b>${subHealthyCount}</b> (${subHealthyPercent}%)</div>
              <div>需改善/需关注: <b>${unhealthyCount}</b> (${unhealthyPercent}%)</div>
              <div style="margin-top:5px;border-top:1px solid #eee;padding-top:5px;">
                <div>总测试人数: <b>${totalCount}</b></div>
                <div>平均健康评分: <b>${avgScore}</b></div>
                <div>测试总次数: <b>${testCount}</b></div>
              </div>
            `;
          },
          axisPointer: {
            type: 'line',
            lineStyle: {
              color: 'rgba(0, 0, 0, 0.2)',
              width: 1,
              type: 'solid'
            }
          }
        },
        legend: {
          data: ['优秀/良好', '一般', '需改善/需关注'],
          bottom: 0,
          selectedMode: true,
          textStyle: {
            color: '#666'
          },
          icon: 'roundRect',
          itemWidth: 12,
          itemHeight: 12,
          itemGap: 15
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          top: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: formattedXAxis,
          axisLabel: {
            interval: formattedXAxis.length > 10 ? 'auto' : 0,
            fontSize: 12,
            color: '#666'
          },
          axisLine: {
            lineStyle: {
              color: '#ddd'
            }
          },
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value',
          name: '人数',
          nameTextStyle: {
            color: '#666'
          },
          axisLabel: {
            formatter: '{value}',
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
            name: '优秀/良好',
            type: 'line',
            stack: '总量',
            smooth: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(82, 196, 26, 0.6)' },
                { offset: 1, color: 'rgba(82, 196, 26, 0.1)' }
              ])
            },
            emphasis: {
              focus: 'series'
            },
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#52c41a'
            },
            lineStyle: {
              width: 2,
              color: '#52c41a'
            },
            data: healthy
          },
          {
            name: '一般',
            type: 'line',
            stack: '总量',
            smooth: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(250, 173, 20, 0.6)' },
                { offset: 1, color: 'rgba(250, 173, 20, 0.1)' }
              ])
            },
            emphasis: {
              focus: 'series'
            },
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#faad14'
            },
            lineStyle: {
              width: 2,
              color: '#faad14'
            },
            data: subHealthy
          },
          {
            name: '需改善/需关注',
            type: 'line',
            stack: '总量',
            smooth: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(245, 34, 45, 0.6)' },
                { offset: 1, color: 'rgba(245, 34, 45, 0.1)' }
              ])
            },
            emphasis: {
              focus: 'series'
            },
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#f5222d'
            },
            lineStyle: {
              width: 2,
              color: '#f5222d'
            },
            data: unhealthy
          }
        ]
      };
      
      // 安全地设置图表选项
      safeChartOperation(charts.line, 'setOption', [options, true]);
      
      // 添加额外的事件监听，确保tooltip正常工作
      safeChartOperation(charts.line, 'off', ['mouseover']);
      safeChartOperation(charts.line, 'on', ['mouseover', 'series', function(params) {
        console.log('鼠标悬停在图表上:', params);
      }]);
      
    } else {
      // 设置错误状态
      safeChartOperation(charts.line, 'setOption', [{
        title: {
          text: '加载数据失败',
          left: 'center',
          top: 'center',
          textStyle: {
            color: '#f56c6c',
            fontSize: 16,
            fontWeight: 'normal'
          }
        },
        xAxis: { type: 'category', data: [] },
        yAxis: { type: 'value' },
        series: [{ name: '数据', type: 'line', data: [] }]
      }]);
      
      ElMessage.error(res.msg || '加载健康趋势数据失败');
    }
  } catch (err) {
    console.error('Failed to load trend data:', err);
    
    // 安全地隐藏加载状态和设置错误状态
    if (isMounted.value && charts.line) {
      safeChartOperation(charts.line, 'hideLoading');
      safeChartOperation(charts.line, 'setOption', [{
        title: {
          text: '加载数据失败',
          left: 'center',
          top: 'center',
          textStyle: {
            color: '#f56c6c',
            fontSize: 16,
            fontWeight: 'normal'
          }
        },
        xAxis: { type: 'category', data: [] },
        yAxis: { type: 'value' },
        series: [{ name: '数据', type: 'line', data: [] }]
      }]);
    }
    
    ElMessage.error('加载健康趋势数据失败');
  } finally {
    chartLoading.value = false;
  }
};

// 加载部门健康数据
const loadDepartmentData = async () => {
  try {
    // 检查组件是否仍然挂载
    if (!isMounted.value) {
      console.log('组件已卸载，取消加载部门数据');
      return;
    }
    
    // 检查图表实例是否存在
    if (!charts.department) {
      console.error('Charts.department is not initialized');
      return;
    }
    
    departmentChartLoading.value = true;
    
    console.log('发送部门健康数据请求');
    
    const res = await request.get('/statistics/department-health');
    
    // 再次检查组件是否仍然挂载
    if (!isMounted.value) {
      console.log('组件已卸载，取消处理部门数据');
      return;
    }
    
    console.log('收到部门健康数据响应:', JSON.stringify(res));
    
    if (res.code === '200' && res.data) {
      // 确保所有数据都存在，如果不存在则使用空数组
      const departments = res.data.departments || [];
      const scores = res.data.scores || [];
      const healthStatus = res.data.healthStatus || [];
      const userNums = res.data.userNums || [];
      const testedUserNums = res.data.testedUserNums || [];
      const participationRates = res.data.participationRates || [];
      const testsPerUser = res.data.testsPerUser || [];
      const highRiskRatios = res.data.highRiskRatios || [];
      
      // 保存处理后的数据
      const processedData = {
        departments,
        scores,
        healthStatus,
        userNums,
        testedUserNums,
        participationRates,
        testsPerUser,
        highRiskRatios,
        departmentsCount: departments.length
      };
      
      console.log('处理后的部门健康数据:', processedData);
      
      // 再次检查组件和图表是否仍然存在
      if (!isMounted.value || !charts.department) {
        console.log('组件或图表已不存在，取消更新图表');
        return;
      }
      
      // 安全地隐藏加载状态
      safeChartOperation(charts.department, 'hideLoading');
      
      // 如果没有部门数据，显示空状态
      if (departments.length === 0) {
        safeChartOperation(charts.department, 'setOption', [{
          title: {
            text: '暂无部门数据',
            left: 'center',
            top: 'center',
            textStyle: {
              color: '#999',
              fontSize: 16,
              fontWeight: 'normal'
            }
          },
          xAxis: {
            type: 'category',
            data: []
          },
          yAxis: {
            type: 'value'
          },
          series: [{
            type: 'bar',
            data: []
          }]
        }]);
        departmentChartLoading.value = false;
        return;
      }
      
      // 根据部门数量调整X轴标签旋转角度
      const rotateAngle = departments.length > 5 ? 45 : 0;
      
      // 准备图表数据 - 使用简单的柱状图而不是极坐标图
      const seriesData = scores.map((score, index) => {
        return {
          value: score,
          itemStyle: {
            color: getScoreColor(score)
          }
        };
      });
      
      // 设置图表选项 - 使用简单的柱状图配置
      const options = {
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          borderColor: '#e2e8f0',
          borderWidth: 1,
          padding: [10, 15],
          textStyle: {
            color: '#4b5563',
            fontSize: 12
          },
          formatter: function(params) {
            // 确保索引存在且有效
            if (!params || !departments || params.dataIndex >= departments.length) {
              return '数据不可用';
            }
            
            const index = params.dataIndex;
            
            // 确保所有数据都存在，如果不存在则使用默认值
            const department = departments[index] || '未知部门';
            const score = scores[index] !== undefined ? scores[index] : '暂无数据';
            const status = healthStatus[index] || '暂无数据';
            const userNum = userNums[index] !== undefined ? userNums[index] : 0;
            const testedUserNum = testedUserNums[index] !== undefined ? testedUserNums[index] : 0;
            const participationRate = participationRates[index] !== undefined ? 
              (participationRates[index] * 100).toFixed(1) + '%' : '0%';
            const testsPerUserVal = testsPerUser[index] !== undefined ? 
              testsPerUser[index].toFixed(1) : '0';
            const highRiskRatio = highRiskRatios[index] !== undefined ? 
              (highRiskRatios[index] * 100).toFixed(1) + '%' : '0%';
            
            return `
              <div style="font-weight:bold;margin-bottom:8px;font-size:14px;">${department}</div>
              <div style="display:flex;justify-content:space-between;margin:5px 0;">
                <span>健康评分:</span>
                <span style="font-weight:bold;margin-left:10px;">${score}</span>
              </div>
              <div style="display:flex;justify-content:space-between;margin:5px 0;">
                <span>健康状态:</span>
                <span style="font-weight:bold;margin-left:10px;">${status}</span>
              </div>
              <div style="display:flex;justify-content:space-between;margin:5px 0;">
                <span>部门总人数:</span>
                <span style="font-weight:bold;margin-left:10px;">${userNum}</span>
              </div>
              <div style="display:flex;justify-content:space-between;margin:5px 0;">
                <span>参测人数:</span>
                <span style="font-weight:bold;margin-left:10px;">${testedUserNum}</span>
              </div>
              <div style="display:flex;justify-content:space-between;margin:5px 0;">
                <span>参与率:</span>
                <span style="font-weight:bold;margin-left:10px;">${participationRate}</span>
              </div>
              <div style="display:flex;justify-content:space-between;margin:5px 0;">
                <span>人均测试次数:</span>
                <span style="font-weight:bold;margin-left:10px;">${testsPerUserVal}</span>
              </div>
              <div style="display:flex;justify-content:space-between;margin:5px 0;">
                <span>高风险员工比例:</span>
                <span style="font-weight:bold;margin-left:10px;">${highRiskRatio}</span>
              </div>
            `;
          },
          extraCssText: 'box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: departments,
          axisLabel: {
            interval: 0,
            rotate: rotateAngle,
            fontSize: 12
          }
        },
        yAxis: {
          type: 'value',
          name: '健康评分',
          min: 0,
          max: 100,
          interval: 20,
          axisLabel: {
            formatter: '{value}'
          }
        },
        series: [
          {
            name: '健康评分',
            type: 'bar',
            data: seriesData,
            label: {
              show: true,
              position: 'top',
              formatter: '{c}'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      
      // 安全地设置图表选项
      safeChartOperation(charts.department, 'setOption', [options, true]);
      
    } else {
      // 安全地隐藏加载状态
      safeChartOperation(charts.department, 'hideLoading');
      
      // 设置空状态
      safeChartOperation(charts.department, 'setOption', [{
        title: {
          text: '加载数据失败',
          left: 'center',
          top: 'center',
          textStyle: {
            color: '#f56c6c',
            fontSize: 16,
            fontWeight: 'normal'
          }
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          type: 'bar',
          data: []
        }]
      }]);
      
      ElMessage.error(res.msg || '加载部门健康数据失败');
    }
  } catch (err) {
    console.error('Failed to load department data:', err);
    
    // 安全地隐藏加载状态
    if (isMounted.value && charts.department) {
      safeChartOperation(charts.department, 'hideLoading');
      
      // 设置错误状态
      safeChartOperation(charts.department, 'setOption', [{
        title: {
          text: '加载数据失败',
          left: 'center',
          top: 'center',
          textStyle: {
            color: '#f56c6c',
            fontSize: 16,
            fontWeight: 'normal'
          }
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          type: 'bar',
          data: []
        }]
      }]);
    }
    
    ElMessage.error('加载部门健康数据失败');
  } finally {
    departmentChartLoading.value = false;
  }
};

// 获取分数对应的颜色
const getScoreColor = (score) => {
  if (score >= 85) return '#52c41a'; // 优秀
  if (score >= 75) return '#85d13b'; // 良好
  if (score >= 65) return '#faad14'; // 一般
  if (score >= 50) return '#fa8c16'; // 需改善
  return '#f5222d'; // 需关注
};

// 修改窗口大小变化处理函数
const handleResize = () => {
  // 检查组件是否仍然挂载
  if (!isMounted.value) return;
  
  // 安全地调整图表大小
  Object.keys(charts).forEach(key => {
    const chart = charts[key];
    if (chart && typeof chart.resize === 'function' && chartInitStatus[key]) {
      try {
        chart.resize();
      } catch (err) {
        console.error(`调整图表 ${key} 大小失败:`, err);
      }
    }
  });
};

// 清理所有图表实例
const disposeAllCharts = () => {
  Object.keys(charts).forEach(key => {
    if (charts[key]) {
      try {
        charts[key].dispose();
        charts[key] = null;
      } catch (err) {
        console.error(`销毁图表 ${key} 失败:`, err);
      }
    }
  });
};

// 监听组件挂载状态变化
watch(isMounted, (newVal, oldVal) => {
  console.log(`组件挂载状态变化: ${oldVal} -> ${newVal}`);
});

// 修改组件挂载函数
onMounted(async () => {
  try {
    // 设置挂载标志
    isMounted.value = true;
    
    // 加载基础数据
    await Promise.all([
      loadBaseData(),
      loadHighRiskEmployees(),
      loadRecentAssessments()
    ]);
    
    // 检查组件是否仍然挂载
    if (!isMounted.value) {
      console.log('组件已卸载，取消初始化图表');
      return;
    }
    
    // 延迟初始化图表，确保DOM已完全渲染
    setTimeout(() => {
      if (isMounted.value) {
        initCharts();
      }
    }, 500);
    
    // 添加窗口大小变化监听
    window.addEventListener('resize', handleResize);
  } catch (err) {
    console.error('组件挂载时出错:', err);
    ElMessage.error('加载数据失败');
  }
});

// 修改组件卸载前函数
onBeforeUnmount(() => {
  // 设置挂载标志为 false
  isMounted.value = false;
  console.log('组件即将卸载，设置挂载标志为 false');
  
  // 清除所有初始化计时器
  if (window.__chartInitTimers) {
    Object.values(window.__chartInitTimers).forEach(timer => clearTimeout(timer));
    window.__chartInitTimers = {};
  }
  
  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize);
  
  // 立即销毁所有图表，避免后续操作
  disposeAllCharts();
  
  // 重置图表初始化状态
  Object.keys(chartInitStatus).forEach(key => {
    chartInitStatus[key] = false;
  });
});

// 组件卸载
onUnmounted(() => {
  console.log('组件已卸载，清理资源');
});

// 导出高风险员工列表为Excel
const exportHighRiskEmployees = () => {
  if (data.highRiskEmployees.length === 0) {
    ElMessage.warning('暂无高风险员工数据可导出');
    return;
  }
  
  try {
    // 准备导出数据
    const exportData = data.highRiskEmployees.map(employee => ({
      '姓名': employee.name,
      '部门': employee.department,
      '综合评分': employee.combinedScore || employee.score,
      '健康状态': getHealthStatus(employee.combinedScore || employee.score)
    }));
    
    // 创建工作簿和工作表
    const wb = XLSX.utils.book_new();
    const ws = XLSX.utils.json_to_sheet(exportData);
    
    // 设置列宽
    const colWidths = [
      { wch: 10 }, // 姓名
      { wch: 15 }, // 部门
      { wch: 10 }, // 综合评分
      { wch: 10 }  // 健康状态
    ];
    ws['!cols'] = colWidths;
    
    // 添加工作表到工作簿
    XLSX.utils.book_append_sheet(wb, ws, '高风险员工列表');
    
    // 生成文件名
    const now = new Date();
    const fileName = `高风险员工列表_${now.getFullYear()}${(now.getMonth()+1).toString().padStart(2, '0')}${now.getDate().toString().padStart(2, '0')}.xlsx`;
    
    // 导出Excel文件
    XLSX.writeFile(wb, fileName);
    
    ElMessage.success('高风险员工列表已成功导出');
  } catch (error) {
    console.error('导出Excel失败:', error);
    ElMessage.error('导出Excel失败，请稍后重试');
  }
};
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
.health-trend-legend {
  @apply text-xs text-gray-600;
}

.health-legend-item {
  @apply flex items-center whitespace-nowrap;
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

/* 健康状态徽章 */
.health-status-badge {
  @apply px-2 py-1 rounded-full text-xs font-semibold whitespace-nowrap;
}

/* 更新分数徽章样式，使其与健康档案一致 */
.score-badge {
  @apply px-2 py-1 rounded-full text-xs font-semibold;
}

.score-critical {
  @apply bg-red-100 text-red-800;
}

.score-warning {
  @apply bg-orange-100 text-orange-800;
}

.score-normal {
  @apply bg-yellow-100 text-yellow-800;
}

.score-good {
  @apply bg-green-100 text-green-800;
}

.score-excellent {
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

.loading-spinner {
  @apply w-8 h-8 border-4 border-blue-200 border-t-blue-500 rounded-full animate-spin;
}

.relative {
  position: relative;
}

.absolute {
  position: absolute;
}

.inset-0 {
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}

.z-10 {
  z-index: 10;
}

.bg-opacity-70 {
  --tw-bg-opacity: 0.7;
}

/* 添加一些新的样式 */
.tooltip-title {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
  font-size: 14px;
}

.tooltip-item {
  display: flex;
  justify-content: space-between;
  margin: 3px 0;
  font-size: 12px;
}

.tooltip-value {
  font-weight: bold;
  margin-left: 8px;
}

/* 更新健康状态图例样式 */
.health-legend {
  @apply flex items-center space-x-3 text-xs text-gray-600;
}
</style>