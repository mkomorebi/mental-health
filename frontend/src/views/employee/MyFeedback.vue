<template>
  <div class="bg-gray-50 py-10 min-h-screen">
    <div class="max-w-6xl mx-auto px-4">
      <!-- 标题区域 -->
      <div class="mb-8 text-center">
        <h1 class="text-3xl font-bold text-[#2A5C8A] mb-2">我的反馈记录</h1>
        <p class="text-gray-600">您可以在这里查看所有提交的反馈及处理进度</p>
      </div>

      <!-- 搜索筛选区域 -->
      <div class="mb-8">
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-100">
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
            <!-- 反馈问题搜索 -->
            <div class="md:col-span-2">
              <label class="block text-sm font-medium text-gray-700 mb-1">搜索反馈问题</label>
              <div class="relative">
                <input
                  v-model="data.question"
                  placeholder="输入关键词搜索反馈问题..."
                  class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
                />
                <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                  <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </span>
              </div>
            </div>

            <!-- 反馈类型筛选 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">反馈类型</label>
              <div class="relative">
                <select
                  v-model="data.type"
                  class="block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-[#2A5C8A] focus:border-[#2A5C8A] rounded-md appearance-none"
                >
                  <option :value="null">所有类型</option>
                  <option value="feature">功能建议</option>
                  <option value="bug">问题反馈</option>
                  <option value="question">使用咨询</option>
                  <option value="other">其他</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                  <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </div>
              </div>
            </div>

            <!-- 反馈状态筛选 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">反馈状态</label>
              <div class="relative">
                <select
                  v-model="data.status"
                  class="block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-[#2A5C8A] focus:border-[#2A5C8A] rounded-md appearance-none"
                >
                  <option :value="null">所有状态</option>
                  <option value="待回复">待回复</option>
                  <option value="已回复">已回复</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                  <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                  </svg>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="flex md:col-span-4 justify-end mt-2 gap-4">
              <button
                @click="load"
                class="inline-flex items-center px-5 py-2.5 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#4a9be6] hover:bg-[#7c97ea] transition-all duration-200 transform hover:-translate-y-0.5"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="11" cy="11" r="8"></circle>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
                查询
              </button>
              <button
                @click="reset"
                class="inline-flex items-center px-5 py-2.5 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-white bg-[#4a9be6] hover:bg-[#7c97ea] transition-all duration-200 transform hover:-translate-y-0.5"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
                  <path d="M3 3v5h5"></path>
                  <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"></path>
                  <path d="M16 21h5v-5"></path>
                </svg>
                重置
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 反馈列表 -->
      <div v-if="data.loading" class="flex justify-center my-4">
        <svg class="animate-spin h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
        </svg>
      </div>
      <div class="space-y-4 mb-6 transition-opacity duration-300" :class="{ 'opacity-0': data.loading, 'opacity-100': !data.loading }">
        <!-- 空状态 -->
        <div v-if="data.tableData.length === 0" class="bg-white rounded-lg shadow-md p-8 text-center border border-gray-100">
          <h3 class="mt-2 text-lg font-medium text-gray-900">暂无反馈记录</h3>
          <p class="mt-1 text-sm text-gray-500">您还没有提交过反馈，或者没有符合筛选条件的反馈。请尝试更改筛选条件或提交新的反馈。</p>
          <button
            @click="reset"
            type="button"
            class="mt-6 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#2A5C8A] hover:bg-[#1e4266] transition-colors"
          >
            查看全部反馈
          </button>
        </div>

        <!-- 反馈卡片 -->
        <div v-for="item in data.tableData" :key="item.id" class="bg-white rounded-lg shadow-md border border-gray-100 overflow-hidden transition-all duration-300 hover:shadow-lg">
          <div class="p-6">
            <!-- 反馈头部 -->
            <div class="flex items-start justify-between">
              <div class="flex items-start">
                <!-- 反馈类型图标 -->
                <div :class="{
                  'flex-shrink-0 h-10 w-10 rounded-full flex items-center justify-center mr-3': true,
                  'bg-blue-100 text-blue-600': item.type === 'feature',
                  'bg-red-100 text-red-600': item.type === 'bug',
                  'bg-yellow-100 text-yellow-600': item.type === 'question',
                  'bg-gray-100 text-gray-600': item.type === 'other' || !item.type
                }">
                  <svg v-if="item.type === 'feature'" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                  </svg>
                  <svg v-else-if="item.type === 'bug'" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                  </svg>
                  <svg v-else-if="item.type === 'question'" class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <svg v-else class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z" />
                  </svg>
                </div>

                <div>
                  <h3 class="text-lg font-semibold text-gray-800">{{ item.question }}</h3>
                  <div class="flex items-center mt-1">
                    <span class="text-sm text-gray-500">{{ formatDate(item.time) }}</span>
                    <span class="mx-2 text-gray-300">•</span>
                    <span :class="{
                      'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium': true,
                      'bg-yellow-100 text-yellow-800': item.status === '待回复',
                      'bg-green-100 text-green-800': item.status === '已回复'
                    }">
                      {{ item.status }}
                    </span>
                    <span v-if="item.urgency" class="ml-2 flex items-center text-xs">
                      <svg class="mr-1 h-3 w-3" :class="{
                        'text-green-500': item.urgency < 40,
                        'text-yellow-500': item.urgency >= 40 && item.urgency < 70,
                        'text-red-500': item.urgency >= 70
                      }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      <span :class="{
                        'text-gray-500': true,
                        'text-green-600': item.urgency < 40,
                        'text-yellow-600': item.urgency >= 40 && item.urgency < 70,
                        'text-red-600': item.urgency >= 70
                      }">紧急度 {{ item.urgency }}%</span>
                    </span>
                  </div>
                </div>
              </div>

              <button
                @click="del(item.id)"
                class="text-gray-400 hover:text-red-500 transition-colors duration-150 p-1 rounded-full hover:bg-red-50"
                title="删除反馈"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </button>
            </div>

            <!-- 反馈内容 -->
            <div class="mt-4 pl-13">
              <div class="bg-gray-50 rounded-lg p-4">
                <h4 class="text-sm font-medium text-gray-700 mb-2">我的想法:</h4>
                <p class="text-gray-700 whitespace-pre-line">{{ item.content }}</p>
              </div>
            </div>

            <!-- 反馈图片 -->
            <div v-if="item.imageUrls && item.imageUrls.length > 0" class="mt-4 pl-13">
              <h4 class="text-sm font-medium text-gray-700 mb-2">相关截图:</h4>
              <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-2">
                <div v-for="(img, index) in item.imageUrls" :key="index" class="relative group">
                  <img
                    :src="img"
                    @click="previewImage(img)"
                    class="w-full h-24 object-cover rounded-lg border border-gray-200 cursor-pointer hover:scale-105 transition-transform duration-150"
                  />
                </div>
              </div>
            </div>

            <!-- 回复内容 -->
            <div v-if="item.status === '已回复'" class="mt-6 pl-13">
              <div class="border-t border-gray-200 pt-4">
                <div class="flex items-start">
                  <div class="flex-shrink-0 h-8 w-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center mr-3">
                    <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
                    </svg>
                  </div>
                  <div>
                    <div class="flex items-center">
                      <span class="text-sm font-medium text-gray-900">{{ item.replyName || '管理员' }}</span>
                      <span class="mx-2 text-xs text-gray-400">•</span>
                      <span class="text-xs text-gray-500">{{ formatDate(item.replyTime) }}</span>
                    </div>
                    <div class="mt-2 bg-gray-50 rounded-lg p-4">
                      <p class="text-gray-700 whitespace-pre-line">{{ item.replyContent }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页控件 -->
      <div v-if="data.total > 0" class="mt-10">
        <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-4 flex flex-col items-center">
          <!-- 自定义分页控件 -->
          <div class="flex items-center justify-center space-x-1">
            <!-- 每页显示数量选择器 -->
            <div class="mr-4">
              <select 
                v-model="data.pageSize" 
                @change="handleSizeChange(data.pageSize)"
                class="border border-gray-300 rounded-md text-sm py-1 px-2 focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
              >
                <option v-for="size in [5, 10, 20, 50]" :key="size" :value="size">{{ size }}条/页</option>
              </select>
            </div>
            
            <!-- 上一页按钮 -->
            <button 
              @click="prevPage" 
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
              @click="nextPage" 
              :disabled="data.pageNum >= totalPages"
              class="w-9 h-9 flex items-center justify-center rounded-md border"
              :class="data.pageNum >= totalPages ? 'border-gray-200 text-gray-400 cursor-not-allowed' : 'border-gray-300 text-gray-700 hover:bg-gray-50'"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="9 18 15 12 9 6"></polyline>
              </svg>
            </button>
            
            <!-- 跳转到指定页 -->
            <div class="ml-4 flex items-center">
              <span class="text-sm text-gray-600 mr-2">跳至</span>
              <input 
                v-model="jumpToPage" 
                type="number" 
                min="1" 
                :max="totalPages" 
                class="w-12 border border-gray-300 rounded-md text-sm py-1 px-2 text-center focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
                @keyup.enter="handleJumpToPage"
              />
              <span class="text-sm text-gray-600 ml-2">页</span>
            </div>
            
            <!-- 总条数显示 -->
            <div class="ml-4 text-sm text-gray-600">
              共 {{ data.total }} 条
            </div>
          </div>
          
          <!-- 当前页信息 -->
          <div class="mt-2 text-sm text-gray-500">
            当前显示 {{ (data.pageNum - 1) * data.pageSize + 1 }} - {{ Math.min(data.pageNum * data.pageSize, data.total) }} 条
          </div>
        </div>
      </div>

      <!-- 图片预览模态框 -->
      <div v-if="previewVisible" class="fixed inset-0 z-50 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
        <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
          <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true" @click="closePreview"></div>
          <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
          <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full">
            <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
              <div class="sm:flex sm:items-start">
                <div class="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left w-full">
                  <div class="flex justify-between items-center mb-4">
                    <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-title">图片预览</h3>
                    <button
                      type="button"
                      @click="closePreview"
                      class="bg-white rounded-md text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                    >
                      <span class="sr-only">关闭</span>
                      <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                  <div class="mt-2">
                    <img :src="previewImageUrl" class="w-full h-auto max-h-[70vh] object-contain" />
                  </div>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
              <button
                type="button"
                @click="closePreview"
                class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
              >
                关闭
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";

const data = reactive({
  status: null,
  type: null,
  question: null,
  pageNum: 1,
  pageSize: 10,
  total: 0,
  tableData: [],
  loading: false
});

const previewVisible = ref(false);
const previewImageUrl = ref('');

const jumpToPage = ref(1);

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

// 处理页码变化
const handlePageChange = (page) => {
  if (page < 1 || page > totalPages.value) return;
  data.pageNum = page;
  jumpToPage.value = page;
  window.scrollTo({ top: 0, behavior: 'smooth' });
  load();
};

// 处理每页显示数量变化
const handleSizeChange = (size) => {
  data.pageSize = size;
  data.pageNum = 1; // 重置到第一页
  jumpToPage.value = 1;
  load();
};

// 处理跳转到指定页
const handleJumpToPage = () => {
  let page = parseInt(jumpToPage.value);
  if (isNaN(page)) {
    page = 1;
  } else if (page < 1) {
    page = 1;
  } else if (page > totalPages.value) {
    page = totalPages.value;
  }
  
  jumpToPage.value = page;
  handlePageChange(page);
};

// 格式化日期显示
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-');
};

// 加载反馈数据
const load = async () => {
  try {
    data.loading = true;
    const res = await request.get('/feedback/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        status: data.status,
        type: data.type,
        question: data.question
      }
    });

    if (res.code === '200') {
      data.tableData = res.data.list.map(item => {
        return {
          ...item,
          imageUrls: item.imageUrls ? item.imageUrls.split(',') : []
        };
      });
      data.total = res.data.total;
    } else {
      ElMessage.error(res.msg || '加载反馈数据失败');
    }
  } catch (error) {
    console.error('加载反馈数据出错:', error);
    ElMessage.error('加载反馈数据失败，请稍后重试');
  } finally {
    data.loading = false;
  }
};

// 重置筛选条件
const reset = () => {
  data.status = null;
  data.type = null;
  data.question = null;
  data.pageNum = 1;
  load();
};

// 删除反馈
const del = (id) => {
  ElMessageBox.confirm('确定要删除这条反馈吗？删除后将无法恢复。', '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'delete-confirm-dialog'
  }).then(() => {
    request.delete('/feedback/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('反馈删除成功');
        load();
      } else {
        ElMessage.error(res.msg || '删除反馈失败');
      }
    }).catch(error => {
      console.error('删除反馈出错:', error);
      ElMessage.error('删除反馈失败，请稍后重试');
    });
  }).catch(() => {
    // 用户取消删除
  });
};

// 上一页
const prevPage = () => {
  if (data.pageNum > 1) {
    data.pageNum--;
    load();
  }
};

// 下一页
const nextPage = () => {
  if (data.pageNum < Math.ceil(data.total / data.pageSize)) {
    data.pageNum++;
    load();
  }
};

// 预览图片
const previewImage = (url) => {
  previewImageUrl.value = url;
  previewVisible.value = true;
};

// 关闭预览
const closePreview = () => {
  previewVisible.value = false;
};

// 初始化加载
load();
</script>

<style>
/* 禁用输入框的箭头 */
input[type=number]::-webkit-inner-spin-button, 
input[type=number]::-webkit-outer-spin-button { 
  -webkit-appearance: none; 
  margin: 0; 
}
input[type=number] {
  -moz-appearance: textfield;
}

/* 列表动画 */
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

/* 自定义加载动画 */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.button-container {
  display: flex;
  justify-content: flex-end;
}

.button-container > button:not(:last-child) {
  margin-right: 1rem;
}
</style>