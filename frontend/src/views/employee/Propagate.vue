<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>
<template>
  <div class="bg-gray-50 py-10 min-h-screen">
    <div class="max-w-6xl mx-auto px-4">
      <!-- 标题和描述 -->
      <div class="mb-8 text-center">
        <h1 class="text-3xl font-bold text-[#2A5C8A] mb-2">健康科普</h1>
        <p class="text-gray-600">了解最新的心理健康知识和医疗资讯</p>
      </div>
      
      <!-- 搜索区域 - 重新设计 -->
      <div class="mb-8">
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-100">
          <!-- 搜索标题输入框 -->
          <div class="mb-4">
            <label for="search-title" class="block text-sm font-medium text-gray-700 mb-1">文章标题</label>
            <div class="relative">
              <input
                id="search-title"
                v-model="data.searchTitle"
                type="text"
                placeholder="搜索文章标题"
                class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
                @keyup.enter="handleSearch"
              />
              <span class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="11" cy="11" r="8"></circle>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
              </span>
              <button 
                v-if="data.searchTitle" 
                @click="handleClearSearch" 
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>
          </div>
          
          <!-- 筛选和排序选项 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
            <!-- 标签筛选 -->
            <div>
              <label for="tag-filter" class="block text-sm font-medium text-gray-700 mb-1">标签筛选</label>
              <div class="relative">
                <select
                  id="tag-filter"
                  v-model="data.searchTag"
                  class="block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-[#2A5C8A] focus:border-[#2A5C8A] rounded-md appearance-none"
                >
                  <option value="">全部标签</option>
                  <option v-for="tag in availableTags" :key="tag" :value="tag">{{ tag }}</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"></polyline>
                  </svg>
                </div>
              </div>
            </div>
            
            <!-- 排序方式 -->
            <div>
              <label for="sort-by" class="block text-sm font-medium text-gray-700 mb-1">排序方式</label>
              <div class="relative">
                <select
                  id="sort-by"
                  v-model="data.sortBy"
                  class="block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-[#2A5C8A] focus:border-[#2A5C8A] rounded-md appearance-none"
                >
                  <option value="timeDesc">最新发布</option>
                  <option value="timeAsc">最早发布</option>
                  <option value="numDesc">浏览最多</option>
                </select>
                <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"></polyline>
                  </svg>
                </div>
              </div>
            </div>
            
            <!-- 搜索按钮 -->
            <div class="flex items-end">
              <button 
                @click="handleSearch" 
                class="w-full py-2 bg-[#4a9be6] text-white rounded-md hover:bg-[#7c97ea] transition-all duration-200 transform hover:-translate-y-0.5 flex items-center justify-center"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="11" cy="11" r="8"></circle>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
                搜索
              </button>
            </div>
          </div>
          
          <!-- 搜索结果提示 -->
          <div v-if="isSearchActive" class="flex items-center justify-between border-t border-gray-100 pt-4 mt-2">
            <span class="text-sm text-gray-600">搜索结果: {{ data.total }} 条</span>
            <button 
              @click="resetSearch" 
              class="text-[#2A5C8A] hover:text-[#1e4266] flex items-center text-sm font-medium transition-all duration-200 hover:-translate-y-0.5"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
                <path d="M3 3v5h5"></path>
                <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"></path>
                <path d="M16 21h5v-5"></path>
              </svg>
              重置筛选
            </button>
          </div>
        </div>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center py-10">
        <div class="space-y-4 w-full max-w-3xl">
          <div v-for="i in 3" :key="i" class="bg-white rounded-lg p-4 shadow-sm animate-pulse">
            <div class="flex flex-col md:flex-row">
              <div class="w-full md:w-48 h-48 md:h-32 bg-gray-200 rounded-md mb-4 md:mb-0"></div>
              <div class="md:ml-4 flex-1">
                <div class="h-6 bg-gray-200 rounded w-3/4 mb-3"></div>
                <div class="h-4 bg-gray-200 rounded w-full mb-2"></div>
                <div class="h-4 bg-gray-200 rounded w-full mb-2"></div>
                <div class="h-4 bg-gray-200 rounded w-2/3 mb-4"></div>
                <div class="flex flex-wrap gap-2">
                  <div class="h-6 bg-gray-200 rounded w-20"></div>
                  <div class="h-6 bg-gray-200 rounded w-24"></div>
                  <div class="h-6 bg-gray-200 rounded w-32"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 内容列表 -->
      <div v-else>
        <div v-if="data.propagateData.length === 0" class="text-center py-10">
          <div class="max-w-md mx-auto">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-gray-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M8 15h8M9 9h.01M15 9h.01"></path>
            </svg>
            <h3 class="text-lg font-medium text-gray-700 mb-1">暂无内容</h3>
            <p class="text-gray-500 mb-4">{{ isSearchActive ? '没有找到符合条件的文章' : '暂无健康科普内容' }}</p>
            <button 
              v-if="isSearchActive" 
              @click="resetSearch"
              class="px-4 py-2 bg-[#2A5C8A] text-white rounded-md hover:bg-[#1e4266] transition-colors"
            >
              重置筛选
            </button>
          </div>
        </div>
        
        <transition-group 
          name="list" 
          tag="div" 
          class="space-y-6"
        >
          <div 
            v-for="item in data.propagateData" 
            :key="item.id" 
            class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-all duration-300 hover:border-[#2A5C8A]/30 group"
            @click="navTo('/employee/propagateDetail?id=' + item.id)"
          >
            <div class="flex flex-col md:flex-row cursor-pointer">
              <div class="md:w-48 flex-shrink-0 overflow-hidden">
                <img 
                  :src="item.img || defaultImage" 
                  alt="" 
                  class="w-full h-48 md:h-full object-cover transition-transform duration-500 group-hover:scale-105"
                >
              </div>
              <div class="flex-1 p-5">
                <h2 class="text-xl font-semibold text-gray-800 group-hover:text-[#2A5C8A] transition-colors">{{ item.title }}</h2>
                <p class="mt-2 text-gray-600 line-clamp-3">{{ item.content }}</p>
                <div class="mt-4 flex flex-wrap items-center text-sm">
                  <div class="flex items-center mr-4 mb-2">
                    <img 
                      :src="item.doctorAvatar || defaultAvatar" 
                      alt="" 
                      class="w-6 h-6 rounded-full border border-gray-200"
                    >
                    <span class="ml-2 text-gray-800">{{ item.doctorName }}</span>
                  </div>
                  <div class="flex items-center mr-4 mb-2 text-gray-500">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"></path>
                      <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                    <span>{{ item.num }} 次浏览</span>
                  </div>
                  <div class="flex items-center mr-4 mb-2 text-gray-500">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                      <line x1="16" y1="2" x2="16" y2="6"></line>
                      <line x1="8" y1="2" x2="8" y2="6"></line>
                      <line x1="3" y1="10" x2="21" y2="10"></line>
                    </svg>
                    <span>{{ formatDate(item.time) }}</span>
                  </div>
                  <div v-if="item.tag" class="flex items-center flex-wrap mb-2">
                    <span 
                      v-for="tag in item.tag.split(',').map(tag => tag.trim())" 
                      :key="tag" 
                      class="px-2 py-0.5 text-xs rounded-full bg-[#2A5C8A]/10 text-[#2A5C8A] hover:bg-[#2A5C8A]/20 transition-colors cursor-pointer mr-1 mb-1"
                      @click.stop="applyTagFilter(tag)"
                    >
                      {{ tag }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </transition-group>
        
        <!-- 分页 - 重新设计 -->
        <div class="mt-10" v-if="data.total">
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
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed, watch } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const loading = ref(true);
const defaultAvatar = '/src/assets/default-avatar.png'; // 默认头像路径
const defaultImage = '/src/assets/default-image.jpg'; // 默认图片路径
const availableTags = ref([]);
const jumpToPage = ref(1);

const data = reactive({
  pageNum: 1,
  pageSize: 5,
  total: 0,
  propagateData: [],
  searchTitle: '',
  searchTag: '',
  sortBy: 'timeDesc', // 默认按最新发布排序
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

// 从URL参数中获取初始搜索条件
const initFromQuery = () => {
  const query = route.query;
  if (query.title) data.searchTitle = query.title;
  if (query.tag) data.searchTag = query.tag;
  if (query.sortBy) data.sortBy = query.sortBy;
  if (query.page) data.pageNum = parseInt(query.page);
  if (query.pageSize) data.pageSize = parseInt(query.pageSize);
  
  // 同步跳转页输入框
  jumpToPage.value = data.pageNum;
};

// 更新URL参数以反映当前搜索状态
const updateQueryParams = () => {
  const query = {};
  if (data.searchTitle) query.title = data.searchTitle;
  if (data.searchTag) query.tag = data.searchTag;
  if (data.sortBy !== 'timeDesc') query.sortBy = data.sortBy;
  if (data.pageNum > 1) query.page = data.pageNum;
  if (data.pageSize !== 5) query.pageSize = data.pageSize;
  
  router.replace({ query });
};

// 判断是否有搜索条件
const isSearchActive = computed(() => {
  return data.searchTitle || data.searchTag || data.sortBy !== 'timeDesc';
});

// 加载所有可用标签
const loadTags = () => {
  request.get('/propagate/getAllTags').then(res => {
    if (res.code === '200' && res.data) {
      availableTags.value = res.data;
    }
  }).catch(err => {
    console.error('获取标签失败', err);
  });
};

const loadPropagate = () => {
  loading.value = true;
  
  // 构建查询参数
  const params = {
    pageNum: data.pageNum,
    pageSize: data.pageSize,
  };
  
  // 添加搜索条件
  if (data.searchTitle) {
    params.title = data.searchTitle;
  }
  
  if (data.searchTag) {
    params.tag = data.searchTag;
  }
  
  // 添加排序条件
  if (data.sortBy) {
    switch (data.sortBy) {
      case 'timeDesc':
        params.orderBy = 'time';
        params.orderType = 'desc';
        break;
      case 'timeAsc':
        params.orderBy = 'time';
        params.orderType = 'asc';
        break;
      case 'numDesc':
        params.orderBy = 'num';
        params.orderType = 'desc';
        break;
    }
  }
  
  // 添加延迟以确保加载状态显示
  setTimeout(() => {
    request.get('/propagate/selectPageFront', { params })
      .then(res => {
        if (res.code === '200') {
          data.propagateData = res.data.list;
          data.total = res.data.total;
          
          // 处理内容，移除HTML标签以便显示纯文本
          data.propagateData.forEach(item => {
            if (item.content) {
              item.content = item.content.replace(/<[^>]+>/g, '');
            }
          });
          
          // 更新跳转页输入框
          jumpToPage.value = data.pageNum;
        } else {
          ElMessage.error(res.msg);
        }
      })
      .catch(err => {
        ElMessage.error('获取数据失败，请稍后重试');
        console.error(err);
      })
      .finally(() => {
        loading.value = false;
        updateQueryParams();
      });
  }, 300); // 添加短暂延迟以确保加载状态可见
};

// 处理搜索
const handleSearch = () => {
  data.pageNum = 1; // 重置到第一页
  loadPropagate();
};

// 处理清除搜索
const handleClearSearch = () => {
  data.searchTitle = '';
  loadPropagate();
};

// 处理清除标签筛选
const handleClearTag = () => {
  data.searchTag = '';
  loadPropagate();
};

// 处理标签变化
const handleTagChange = () => {
  // 不立即触发搜索，等用户点击搜索按钮
};

// 处理排序变化
const handleSortChange = () => {
  // 不立即触发搜索，等用户点击搜索按钮
};

// 应用标签筛选
const applyTagFilter = (tag) => {
  data.searchTag = tag;
  data.pageNum = 1; // 重置到第一页
  loadPropagate();
};

// 重置所有搜索条件
const resetSearch = () => {
  data.searchTitle = '';
  data.searchTag = '';
  data.sortBy = 'timeDesc';
  data.pageNum = 1;
  loadPropagate();
};

// 处理页码变化
const handlePageChange = (page) => {
  if (page < 1 || page > totalPages.value) return;
  data.pageNum = page;
  jumpToPage.value = page;
  window.scrollTo({ top: 0, behavior: 'smooth' });
  loadPropagate();
};

// 处理每页显示数量变化
const handleSizeChange = (size) => {
  data.pageSize = size;
  data.pageNum = 1; // 重置到第一页
  jumpToPage.value = 1;
  loadPropagate();
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

const navTo = (url) => {
  router.push(url);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' });
};

// 监听路由变化，以便在用户使用浏览器的前进/后退按钮时更新内容
watch(() => route.query, (newQuery) => {
  if (Object.keys(newQuery).length > 0) {
    initFromQuery();
    loadPropagate();
  }
}, { deep: true });

onMounted(() => {
  initFromQuery(); // 从URL参数初始化搜索条件
  loadTags(); // 加载所有可用标签
  loadPropagate();
});
</script>

<style>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
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

/* 禁用输入框的箭头 */
input[type=number]::-webkit-inner-spin-button, 
input[type=number]::-webkit-outer-spin-button { 
  -webkit-appearance: none; 
  margin: 0; 
}
input[type=number] {
  -moz-appearance: textfield;
}
</style>