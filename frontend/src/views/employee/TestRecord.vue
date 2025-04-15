<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>
<template>
  <div class="bg-gray-50 py-10 min-h-screen">
    <div class="max-w-6xl mx-auto px-4">
      <!-- 标题和描述 -->
      <div class="mb-8 text-center">
        <h1 class="text-3xl font-bold text-[#2A5C8A]">心理测试记录</h1>
        <p class="text-gray-600 mt-2">查看您的历史测试结果和分析</p>
      </div>
      
      <!-- 搜索区域 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5 mb-6">
        <div class="flex flex-wrap gap-4 items-center">
          <div class="relative flex-grow max-w-md">
            <input
              v-model="data.testPaperName"
              type="text"
              placeholder="请输入试卷名称查询"
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
              @keyup.enter="search"
            />
            <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </span>
          </div>
          
          <button 
            @click="search" 
            class="px-4 py-2 bg-[#4a9be6] text-white rounded-md hover:bg-[#7c97ea] transition-all duration-200 transform hover:-translate-y-0.5 shadow-sm flex items-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            查询
          </button>
          
          <button 
            @click="reset" 
            class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-all duration-200 transform hover:-translate-y-0.5 flex items-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
              <path d="M3 3v5h5"></path>
              <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"></path>
              <path d="M16 21h5v-5"></path>
            </svg>
            重置
          </button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
        <!-- 空状态 -->
        <div v-if="data.tableData.length === 0" class="text-center py-16">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
          <h3 class="text-lg font-medium text-gray-700 mb-1">暂无测试记录</h3>
          <p class="text-gray-500 mb-4">您还没有完成任何心理测试</p>
          <button 
            @click="navTo('/employee/testPaper')" 
            class="px-4 py-2 bg-[#2A5C8A] text-white rounded-md hover:bg-[#1e4266] transition-all duration-200 transform hover:-translate-y-0.5 shadow-sm"
          >
            去测试
          </button>
        </div>
        
        <!-- 表格内容 -->
        <div v-else class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  试卷名称
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  用户姓名
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  测试分数
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  测试结果
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  测试时间
                </th>
                <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  操作
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="item in data.tableData" :key="item.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <a 
                    :href="'/employee/testPaperDetail?id=' + item.testPaperId" 
                    target="_blank"
                    class="text-[#2A5C8A] hover:underline font-medium"
                  >
                    {{ item.testPaperName }}
                  </a>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                  {{ item.userName }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 py-1 text-sm rounded-full bg-[#2A5C8A]/10 text-[#2A5C8A] font-medium">
                    {{ item.score }} 分
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                  {{ item.result }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ item.time }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button 
                    @click="del(item.id)" 
                    class="text-red-600 hover:text-red-800 p-1 rounded-full hover:bg-red-50 transition-all duration-200 transform hover:-translate-y-0.5"
                    title="删除"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="3 6 5 6 21 6"></polyline>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      <line x1="10" y1="11" x2="10" y2="17"></line>
                      <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="mt-6" v-if="data.total">
        <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-4 flex justify-center">
          <div class="flex items-center space-x-1">
            <!-- 上一页按钮 -->
            <button 
              @click="handlePageChange(data.pageNum - 1)" 
              :disabled="data.pageNum <= 1"
              class="w-9 h-9 flex items-center justify-center rounded-md border"
              :class="data.pageNum <= 1 ? 'border-gray-200 text-gray-400 cursor-not-allowed' : 'border-gray-300 text-gray-700 hover:bg-gray-50'"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="15 18 9 12 15 6"></polyline>
              </svg>
            </button>
            
            <!-- 页码按钮 -->
            <div class="flex space-x-1">
              <template v-for="page in displayedPages" :key="page">
                <button 
                  v-if="page !== '...'"
                  @click="handlePageChange(page)" 
                  class="w-9 h-9 flex items-center justify-center rounded-md text-sm"
                  :class="page === data.pageNum ? 'bg-[#2A5C8A] text-white' : 'border border-gray-300 text-gray-700 hover:bg-gray-50'"
                >
                  {{ page }}
                </button>
                <span 
                  v-else
                  class="w-9 h-9 flex items-center justify-center text-gray-500"
                >
                  ...
                </span>
              </template>
            </div>
            
            <!-- 下一页按钮 -->
            <button 
              @click="handlePageChange(data.pageNum + 1)" 
              :disabled="data.pageNum >= totalPages"
              class="w-9 h-9 flex items-center justify-center rounded-md border"
              :class="data.pageNum >= totalPages ? 'border-gray-200 text-gray-400 cursor-not-allowed' : 'border-gray-300 text-gray-700 hover:bg-gray-50'"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="9 18 15 12 9 6"></polyline>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, markRaw } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import 'element-plus/dist/index.css';
const data = reactive({
  testPaperName: null,
  pageNum: 1,
  pageSize: 5,
  total: 0,
  tableData: [],
});

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(data.total / data.pageSize);
});

// 计算要显示的页码
const displayedPages = computed(() => {
  const current = data.pageNum;
  const total = totalPages.value;
  const pages = [];
  
  if (total <= 7) {
    // 如果总页数小于等于7，显示所有页码
    for (let i = 1; i <= total; i++) {
      pages.push(i);
    }
  } else {
    // 总是显示第一页
    pages.push(1);
    
    // 如果当前页接近开始
    if (current <= 3) {
      pages.push(2, 3, 4, '...', total);
    } 
    // 如果当前页接近结束
    else if (current >= total - 2) {
      pages.push('...', total - 3, total - 2, total - 1, total);
    } 
    // 如果当前页在中间
    else {
      pages.push('...', current - 1, current, current + 1, '...', total);
    }
  }
  
  return pages;
});

const load = (isSearch = false) => {
  // 如果是搜索操作且重置页码为1
  if (isSearch) {
    data.pageNum = 1;
  }
  
  request.get('/testRecord/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      testPaperName: data.testPaperName
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data.list;
      data.total = res.data.total;
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const del = (id) => {
  ElMessageBox.confirm(
    '确定要删除这条测试记录吗？删除后将无法恢复。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    }
  )
    .then(() => {
      request.delete('/testRecord/delete/' + id)
        .then(res => {
          if (res.code === '200') {
            ElMessage({
              type: 'success',
              message: '删除成功'
            });
            load();
          } else {
            ElMessage({
              type: 'error',
              message: res.msg || '删除失败'
            });
          }
        })
        .catch(err => {
          console.error('删除请求错误:', err);
          ElMessage({
            type: 'error',
            message: '网络错误，请稍后重试'
          });
        });
    })
    .catch(() => {
    });
};

const reset = () => {
  data.testPaperName = null;
  // 重置后执行搜索
  load(true);
};

const handlePageChange = (page) => {
  if (page < 1 || page > totalPages.value) return;
  data.pageNum = page;
  load(false); // 翻页不重置页码
};

const navTo = (url) => {
  location.href = url;
};

const search = () => {
  // 执行搜索并重置到第一页
  load(true);
};

// 初始加载数据
load(false);
</script>

<style scoped>
/* 自定义表格样式 */
table {
  border-collapse: separate;
  border-spacing: 0;
}

/* 表格悬停效果 */
tr:hover {
  background-color: rgba(42, 92, 138, 0.05);
}
</style>