<template>
  <div class="test-record-container">
    <!-- Search and Action Card -->
    <div class="bg-white rounded-lg shadow-md p-4 mb-4">
      <!-- Search Row -->
      <div class="flex flex-wrap items-center gap-3 mb-4 pb-3 border-b border-gray-100">
        <div class="relative flex-grow max-w-[180px]">
          <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </span>
          <el-input 
            v-model="data.testPaperName" 
            placeholder="试卷名称" 
            class="pl-10"
            clearable
          />
        </div>
        
        <div class="relative flex-grow max-w-[180px]">
          <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </span>
          <el-input 
            v-model="data.userName" 
            placeholder="用户姓名" 
            class="pl-10"
            clearable
          />
        </div>
        
        <div class="flex-grow max-w-[220px]">
          <el-select 
            v-model="data.departmentId" 
            placeholder="请选择部门" 
            clearable
            class="w-full"
          >
            <el-option 
              v-for="item in data.departmentList" 
              :key="item.id" 
              :label="item.name" 
              :value="item.id"
            ></el-option>
          </el-select>
        </div>
        
        <div class="flex-grow max-w-[220px]">
          <el-select 
            v-model="data.scoreRange" 
            placeholder="请选择分数段" 
            clearable
            class="w-full"
          >
            <el-option label="0-60分" value="0-60"></el-option>
            <el-option label="60-70分" value="60-70"></el-option>
            <el-option label="70-80分" value="70-80"></el-option>
            <el-option label="80-90分" value="80-90"></el-option>
            <el-option label="90-100分" value="90-100"></el-option>
          </el-select>
        </div>
      </div>

      <!-- Action Buttons Row -->
      <div class="flex flex-wrap items-center gap-3">
        <el-button 
          type="primary" 
          @click="load"
          class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          查询
        </el-button>
        <el-button 
          @click="reset"
          class="border-gray-300 text-gray-700"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
            <path d="M3 3v5h5"></path>
          </svg>
          重置
        </el-button>
        <!-- <el-button 
          type="danger" 
          @click="delBatch"
          :disabled="!data.ids.length"
          class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600 text-white disabled:bg-red-300 disabled:border-red-300"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 6h18"></path>
            <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
            <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
            <line x1="10" y1="11" x2="10" y2="17"></line>
            <line x1="14" y1="11" x2="14" y2="17"></line>
          </svg>
          批量删除
        </el-button> -->
        <el-button 
          type="success" 
          @click="exportData"
          :disabled="!data.tableData.length"
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

    <!-- Table Card -->
    <div class="bg-white rounded-lg shadow-md p-4 mb-4 overflow-hidden">
      <el-table 
        :data="data.tableData" 
        @selection-change="handleSelectionChange"
        stripe
        border
        class="w-full"
        v-loading="data.loading"
        element-loading-text="加载中..."
        element-loading-background="rgba(255, 255, 255, 0.8)"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="testPaperName" label="试卷名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户姓名" min-width="120" />
        <el-table-column v-if="data.user.role === 'ADMIN'" prop="doctorName" label="医生姓名" min-width="120" />
        <el-table-column prop="departmentName" label="所属部门" min-width="120" />
        <el-table-column prop="score" label="测试分数" min-width="100">
          <template #default="scope">
            <el-tag 
              :type="getScoreTagType(scope.row.score)" 
              effect="plain"
            >
              {{ scope.row.score }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="测试结果" min-width="150" show-overflow-tooltip />
        <el-table-column prop="time" label="测试时间" min-width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <div class="flex items-center space-x-2">
              <el-button 
                type="danger" 
                circle 
                @click="del(scope.row.id)"
                class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M3 6h18"></path>
                  <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                  <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                </svg>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- Empty State -->
      <div v-if="!data.loading && data.tableData.length === 0" class="py-8 text-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
          <polyline points="14 2 14 8 20 8"></polyline>
          <line x1="16" y1="13" x2="8" y2="13"></line>
          <line x1="16" y1="17" x2="8" y2="17"></line>
          <polyline points="10 9 9 9 8 9"></polyline>
        </svg>
        <p class="mt-2 text-gray-500">暂无测试记录数据</p>
      </div>
    </div>

    <!-- Pagination Card -->
    <div v-if="data.total > 0" class="bg-white rounded-lg shadow-md p-4 flex justify-center">
      <el-pagination 
        @current-change="handlePageChange" 
        @size-change="handleSizeChange"
        background 
        layout="total, sizes, prev, pager, next, jumper" 
        :page-sizes="[10, 20, 50, 100]"
        :page-size="data.pageSize" 
        v-model:current-page="data.pageNum" 
        :total="data.total" 
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete } from "@element-plus/icons-vue";

const data = reactive({
  user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  testPaperName: null,
  userName: null,
  departmentId: null,
  scoreRange: null,
  ids: [],
  departmentList: [],
  loading: false
});

const getScoreTagType = (score) => {
  if (score === undefined || score === null) return 'info';
  score = Number(score);
  if (score < 60) return 'danger';
  if (score < 70) return 'warning';
  if (score < 80) return 'info';
  if (score < 90) return 'success';
  return 'primary';
};

const load = async () => {
  data.loading = true;
  try {
    // 解析分数范围
    let minScore, maxScore;
    if (data.scoreRange) {
      [minScore, maxScore] = data.scoreRange.split('-').map(Number);
    }

    // 根据角色使用不同的API端点
    const endpoint = data.user.role === 'ADMIN' 
      ? "/testRecord/selectPage" 
      : "/testRecord/selectDoctorPage";

    const res = await request.get(endpoint, {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        testPaperName: data.testPaperName || undefined,
        userName: data.userName || undefined,
        departmentId: data.departmentId || undefined,
        minScore: minScore || undefined,
        maxScore: maxScore || undefined,
        doctorId: data.user.role === 'DOCTOR' ? data.user.id : undefined // 医生角色时传入医生ID
      },
    });

    if (res.code === "200") {
      data.tableData = res.data?.list || [];
      data.total = res.data?.total || 0;
    } else {
      ElMessage.error(res.msg || '加载测试记录失败');
    }
  } catch (error) {
    console.error('Failed to load data:', error);
    ElMessage.error('加载数据失败，请检查网络连接');
  } finally {
    data.loading = false;
  }
};

const handlePageChange = (page) => {
  data.pageNum = page;
  load();
};

const handleSizeChange = (size) => {
  data.pageSize = size;
  data.pageNum = 1; // 切换每页显示数量时，重置为第一页
  load();
};

const del = (id) => {
  ElMessageBox.confirm(
    "删除后数据无法恢复，您确定删除吗？", 
    "删除确认", 
    {
      type: "warning",
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      confirmButtonClass: 'el-button--danger'
    }
  )
    .then(() => {
      request.delete("/testRecord/delete/" + id).then((res) => {
        if (res.code === "200") {
          ElMessage.success("删除成功");
          // 如果当前页只有一条数据且不是第一页，删除后跳转到上一页
          if (data.tableData.length === 1 && data.pageNum > 1) {
            data.pageNum--;
          }
          load();
        } else {
          ElMessage.error(res.msg || '删除失败');
        }
      });
    })
    .catch(() => {});
};

const delBatch = () => {
  if (!data.ids.length) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${data.ids.length} 条记录吗？删除后数据无法恢复。`, 
    "批量删除确认", 
    {
      type: "warning",
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      confirmButtonClass: 'el-button--danger'
    }
  )
    .then(() => {
      request.delete("/testRecord/delete/batch", { data: data.ids }).then((res) => {
        if (res.code === "200") {
          ElMessage.success(`成功删除 ${data.ids.length} 条记录`);
          // 如果删除了当前页所有数据，跳转到第一页
          if (data.ids.length >= data.tableData.length) {
            data.pageNum = 1;
          }
          load();
        } else {
          ElMessage.error(res.msg || '批量删除失败');
        }
      });
    })
    .catch(() => {});
};

const handleSelectionChange = (rows) => {
  data.ids = rows.map((v) => v.id);
};

const reset = () => {
  data.testPaperName = null;
  data.userName = null;
  data.departmentId = null;
  data.scoreRange = null;
  data.pageNum = 1;
  load();
};

// 获取部门列表
const loadDepartments = async () => {
  try {
    const res = await request.get('/departments/selectAll');
    if (res.code === '200') {
      data.departmentList = res.data?.list || [];
    }
  } catch (error) {
    console.error('Failed to load departments:', error);
  }
};

// 导出数据功能
const exportData = () => {
  if (!data.tableData.length) {
    ElMessage.warning("没有可导出的数据");
    return;
  }

  try {
    // 创建工作表数据
    const exportData = data.tableData.map(item => {
      const row = {
        '试卷名称': item.testPaperName,
        '用户姓名': item.userName,
        '所属部门': item.departmentName,
        '测试分数': item.score,
        '测试结果': item.result,
        '测试时间': item.time
      };
      
      // 如果是管理员，添加医生姓名列
      if (data.user.role === 'ADMIN') {
        row['医生姓名'] = item.doctorName;
      }
      
      return row;
    });

    // 创建CSV内容
    const headers = Object.keys(exportData[0]);
    let csvContent = headers.join(',') + '\n';
    
    exportData.forEach(row => {
      const values = headers.map(header => {
        const value = row[header] || '';
        // 处理包含逗号的字段，用双引号包裹
        return value.toString().includes(',') ? `"${value}"` : value;
      });
      csvContent += values.join(',') + '\n';
    });

    // 创建Blob对象
    const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' });
    
    // 创建下载链接
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    // 设置文件名
    const date = new Date();
    const fileName = `测试记录_${date.getFullYear()}${(date.getMonth()+1).toString().padStart(2, '0')}${date.getDate().toString().padStart(2, '0')}.csv`;
    
    // 触发下载
    link.href = url;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    ElMessage.success('数据导出成功');
  } catch (error) {
    console.error('导出数据失败:', error);
    ElMessage.error('导出数据失败，请稍后重试');
  }
};

onMounted(() => {
  loadDepartments();
  load();
});
</script>

<style scoped>
.test-record-container {
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
  