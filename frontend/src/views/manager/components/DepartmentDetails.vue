<template>
  <div class="department-stats-container">
    <!-- Search and Action Card -->
    <div class="bg-white rounded-lg shadow-md p-4 mb-4">
      <!-- Search Row -->
      <div class="flex flex-wrap items-center gap-3">
        <div class="relative flex-grow max-w-xs">
          <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </span>
          <el-input 
            v-model="data.departmentName" 
            placeholder="请输入部门名称" 
            class="pl-10"
          />
        </div>
        
        <div class="relative flex-grow max-w-xs">
          <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
          </span>
          <el-input 
            v-model="data.paperTitle" 
            placeholder="请输入问卷名称" 
            class="pl-10"
          />
        </div>
        
        <el-date-picker
          v-model="data.publishTime"
          type="date"
          placeholder="选择发布时间"
          value-format="YYYY-MM-DD"
          class="flex-grow max-w-xs"
        />
        
        <div class="flex items-center gap-2">
          <el-button 
            type="primary" 
            @click="load"
            class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            查询
          </el-button>
          
          <el-button 
            @click="reset"
            class="border-gray-300 text-gray-700"
          >
            重置
          </el-button>
          
          <el-button 
            type="success" 
            @click="downLoad"
            class="bg-green-500 hover:bg-green-600 border-green-500 hover:border-green-600 text-white disabled:bg-green-300 disabled:border-green-300"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7 10 12 15 17 10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            导出
          </el-button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div v-if="flattenedTableData.length > 0" class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
      <div class="bg-white rounded-lg shadow-md p-4">
        <div class="flex items-center">
          <div class="flex-shrink-0 bg-blue-50 p-3 rounded-lg mr-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
          </div>
          <div>
            <div class="text-gray-500 text-sm">总用户数</div>
            <div class="text-xl font-bold">{{ totalUsers }}</div>
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow-md p-4">
        <div class="flex items-center">
          <div class="flex-shrink-0 bg-purple-50 p-3 rounded-lg mr-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-purple-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
          </div>
          <div>
            <div class="text-gray-500 text-sm">问卷总数</div>
            <div class="text-xl font-bold">{{ totalTestPapers }}</div>
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-lg shadow-md p-4">
        <div class="flex items-center">
          <div class="flex-shrink-0 bg-green-50 p-3 rounded-lg mr-4">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-green-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
              <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
            </svg>
          </div>
          <div>
            <div class="text-gray-500 text-sm">部门数量</div>
            <div class="text-xl font-bold">{{ departmentCount }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Table Card -->
    <div class="bg-white rounded-lg shadow-md p-4 mb-4 overflow-hidden">
      <el-table 
        :data="flattenedTableData" 
        stripe
        border
        class="w-full"
        v-loading="data.loading"
        element-loading-text="加载中..."
        element-loading-background="rgba(255, 255, 255, 0.8)"
      >
        <el-table-column prop="departmentName" label="部门名称" min-width="150">
          <template #default="scope">
            <div class="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
              </svg>
              <span class="font-medium">{{ scope.row.departmentName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userNum" label="用户数" width="120" align="center">
          <template #default="scope">
            <div class="flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
              </svg>
              <span class="font-medium">{{ scope.row.userNum || 0 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="paperTitle" label="问卷名称" min-width="180">
          <template #default="scope">
            <div class="flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-purple-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
              <span class="font-medium">{{ scope.row.paperTitle }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" align="center">
          <template #default="scope">
            <div class="flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-gray-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
              <span class="font-medium">{{ formatDate(scope.row.publishTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="completionRate" label="完成率" width="120" align="center">
          <template #default="scope">
            <div class="flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-green-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
              <span class="font-medium">{{ formatPercentage(scope.row.completionRate) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="avgScore" label="平均分" width="120" align="center">
          <template #default="scope">
            <div class="flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-amber-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
              <span class="font-medium">{{ formatScore(scope.row.avgScore) }}</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- Empty State -->
      <div v-if="!data.loading && flattenedTableData.length === 0" class="py-8 text-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p class="mt-2 text-gray-500">暂无部门统计数据</p>
      </div>
    </div>

    <!-- 添加分页组件 -->
    <div v-if="flattenedTableData.length > 0" class="bg-white rounded-lg shadow-md p-4 mt-4">
      <el-pagination
        v-model:current-page="data.pageNum"
        v-model:page-size="data.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="data.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes, prev, pager, next, jumper"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { saveAs } from 'file-saver';
import { reactive, computed } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const data = reactive({
  tableData: [],
  loading: false,
  departmentName: '',
  paperTitle: '',
  publishTime: '',
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// 扁平化数据，将每个部门的问卷数据展开为单独的行
const flattenedTableData = computed(() => {
  return data.tableData.reduce((acc, department) => {
    if (department.testPapers && department.testPapers.length > 0) {
      department.testPapers.forEach(paper => {
        acc.push({
          departmentId: department.departmentId,
          departmentName: department.departmentName,
          userNum: department.userNum,
          paperId: paper.paperId,
          paperTitle: paper.paperTitle,
          publishTime: paper.publishTime,
          completionRate: paper.completionRate,
          avgScore: paper.avgScore
        });
      });
    } else {
      // 如果没有问卷数据，至少显示部门信息
      acc.push({
        departmentId: department.departmentId,
        departmentName: department.departmentName,
        userNum: department.userNum,
        paperId: null,
        paperTitle: '暂无问卷数据',
        publishTime: '',
        completionRate: 0,
        avgScore: 0
      });
    }
    return acc;
  }, []);
});

// 计算总用户数
const totalUsers = computed(() => {
  return data.tableData.reduce((sum, dept) => sum + (dept.userNum || 0), 0);
});

// 计算问卷总数
const totalTestPapers = computed(() => {
  return data.tableData.reduce((sum, dept) => {
    return sum + (dept.testPapers ? dept.testPapers.length : 0);
  }, 0);
});

// 计算部门数量
const departmentCount = computed(() => {
  return data.tableData.length;
});

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-');
};

// 格式化分数，保留一位小数
const formatScore = (score) => {
  if (score === undefined || score === null) return '0.0';
  return parseFloat(score).toFixed(1);
};

// 格式化百分比，显示为百分比形式
const formatPercentage = (rate) => {
  if (rate === undefined || rate === null) return '0%';
  return `${(parseFloat(rate) * 100).toFixed(1)}%`;
};

// 修改加载数据方法
const load = async () => {
  data.loading = true;
  try {
    const res = await request.get('/testRecord/metric', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        departmentName: data.departmentName,
        paperTitle: data.paperTitle,
        publishTime: data.publishTime || ''
      }
    });
    
    if (res.code === '200') {
      data.tableData = res.data?.list || [];
      data.total = res.data?.total || 0;
    } else {
      ElMessage.error(res.msg || '加载部门统计数据失败');
    }
  } catch (error) {
    console.error('Failed to load data:', error);
    ElMessage.error('加载数据失败，请检查网络连接');
  } finally {
    data.loading = false;
  }
};

// 添加分页处理方法
const handleSizeChange = (val) => {
  data.pageSize = val;
  data.pageNum = 1; // 切换每页显示数量时重置为第一页
  load();
};

const handleCurrentChange = (val) => {
  data.pageNum = val;
  load();
};

// 修改重置方法
const reset = () => {
  data.departmentName = '';
  data.paperTitle = '';
  data.publishTime = '';
  data.pageNum = 1;
  load();
};

// 修改导出方法，添加分页参数
const downLoad = async () => {
  try {
    ElMessage.info('正在导出数据，请稍候...');
    
    const response = await request.get('/testRecord/export', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        departmentName: data.departmentName,
        paperTitle: data.paperTitle,
        publishTime: data.publishTime || ''
      },
      responseType: 'blob'
    });
    
    const blob = new Blob([response]);
    saveAs(blob, '部门统计数据.xlsx');
    
    ElMessage.success('导出成功');
  } catch (error) {
    console.error('Export error:', error);
    ElMessage.error('导出失败，请重试');
  }
};

// 初始化加载
load();
</script>

<style scoped>
.department-stats-container {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 1rem;
  background-color: #f5f7fa;
}

/* 统一按钮样式 */
:deep(.el-button) {
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
}

:deep(.el-button:hover:not(:disabled)) {
  transform: translateY(-2px);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

:deep(.el-button--primary) {
  --el-button-bg-color: #2A5C8A;
  --el-button-border-color: #2A5C8A;
  --el-button-hover-bg-color: #1e4266;
  --el-button-hover-border-color: #1e4266;
}

:deep(.el-button--success) {
  --el-button-bg-color: #10b981;
  --el-button-border-color: #10b981;
  --el-button-hover-bg-color: #059669;
  --el-button-hover-border-color: #059669;
}

:deep(.el-button--danger) {
  --el-button-bg-color: #ef4444;
  --el-button-border-color: #ef4444;
  --el-button-hover-bg-color: #dc2626;
  --el-button-hover-border-color: #dc2626;
}

:deep(.el-button--default) {
  --el-button-hover-bg-color: #f3f4f6;
  --el-button-hover-border-color: #d1d5db;
}

:deep(.el-button.is-disabled) {
  opacity: 0.6;
  cursor: not-allowed;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #2A5C8A;
}

:deep(.el-table .el-table__header-wrapper th) {
  background-color: #f5f7fa;
  font-weight: 600;
}

/* Ensure content area doesn't affect sidebar */
:deep(.el-main) {
  overflow-y: hidden;
}

/* Card hover effects */
.bg-white {
  transition: all 0.3s ease;
}

.bg-white:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Table row hover effect */
:deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

:deep(.el-table__row:hover) {
  background-color: #f0f7ff !important;
}
</style>