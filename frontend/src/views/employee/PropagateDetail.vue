<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>
<template>
  <div class="bg-gray-50 py-10 min-h-screen">
    <div class="max-w-4xl mx-auto px-4">
      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center py-10">
        <el-skeleton :rows="10" animated />
      </div>
      
      <!-- 错误状态 -->
      <div v-else-if="error" class="bg-white rounded-lg shadow-md p-6 md:p-8 text-center">
        <el-icon class="text-red-500 text-5xl mb-4"><WarningFilled /></el-icon>
        <h2 class="text-2xl font-bold mb-4">{{ errorMessage }}</h2>
        <p class="text-gray-600 mb-6">请返回列表页查看其他内容</p>
        <button @click="goBack" class="px-6 py-3 bg-[#2A5C8A] text-white rounded-md hover:bg-[#1e4266] transition-colors text-lg flex items-center mx-auto">
          <el-icon class="mr-1"><Back /></el-icon> 返回列表
        </button>
      </div>
      
      <!-- 文章内容 -->
      <div v-else>
        <div class="bg-white rounded-lg shadow-md p-6 md:p-8">
          <div class="mb-6">
            <div class="text-3xl font-bold text-center mb-4 text-[#2A5C8A]">{{ data.propagateData.title }}</div>
            <div class="flex flex-wrap justify-center items-center text-gray-600 mb-2 text-sm">
              <div class="flex items-center mr-4 mb-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                <span>{{ formatDate(data.propagateData.time) }}</span>
              </div>
              <div class="flex items-center mr-4 mb-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <span>{{ data.propagateData.doctorName }}</span>
              </div>
              <div class="flex items-center mb-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
                <span>{{ data.propagateData.num }} 次浏览</span>
              </div>
            </div>
            <div class="border-t border-gray-200 my-4"></div>
          </div>
          
          <!-- 文章图片 -->
          <!--<div v-if="data.propagateData.img" class="mb-6 flex justify-center">
            <img :src="data.propagateData.img" alt="文章图片" class="rounded-lg max-h-80 object-contain">
          </div>-->
          
          <!-- 文章内容 -->
          <div class="mt-6 article-content" v-html="data.propagateData.content"></div>
          
          <!-- 文章标签 -->
          <div v-if="data.propagateData.tag" class="mt-4 flex items-center flex-wrap">
            <span class="text-sm text-gray-600 mr-2">标签:</span>
            <span 
              v-for="tag in parseTags" 
              :key="tag" 
              class="px-2 py-0.5 text-xs rounded-full bg-[#2A5C8A]/10 text-[#2A5C8A] hover:bg-[#2A5C8A]/20 transition-colors mr-1 mb-1"
            >
              {{ tag }}
            </span>
          </div>
          
          <!-- 返回按钮 -->
          <div class="mt-10 flex justify-center">
            <button @click="goBack" class="px-6 py-3 bg-[#2A5C8A] text-white rounded-md hover:bg-[#1e4266] transition-all duration-200 transform hover:-translate-y-0.5 shadow-sm flex items-center justify-center">
              <el-icon class="mr-1"><Back /></el-icon> 返回列表
            </button>
          </div>
        </div>
        
        <!-- 相关文章 -->
        <div v-if="relatedArticles.length > 0" class="mt-8 bg-white rounded-lg shadow-md p-6 md:p-8">
          <h3 class="text-xl font-bold mb-4 text-[#2A5C8A]">相关文章</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div v-for="article in relatedArticles" :key="article.id" 
                 class="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors hover:border-[#2A5C8A]/30"
                 @click="navToArticle(article.id)">
              <div class="font-medium text-[#2A5C8A] hover:text-[#1e4266] transition-colors">{{ article.title }}</div>
              <div class="mt-2 text-sm text-gray-500 flex justify-between">
                <span>{{ article.doctorName }}</span>
                <span>{{ formatDate(article.time) }}</span>
              </div>
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
import { Calendar, User, View, Back, WarningFilled } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const loading = ref(true);
const error = ref(false);
const errorMessage = ref('获取文章失败');
const relatedArticles = ref([]);

const data = reactive({
  propagateId: null,
  propagateData: {}
})

const parseTags = computed(() => {
  if (!data.propagateData.tag) return [];
  return data.propagateData.tag.split(',').filter(tag => tag.trim());
});

const loadPropagate = () => {
  loading.value = true;
  error.value = false;
  data.propagateId = route.query.id;
  
  if (!data.propagateId) {
    error.value = true;
    errorMessage.value = '文章ID不存在';
    loading.value = false;
    return;
  }
  
  request.get('/propagate/selectById/' + data.propagateId).then(res => {
    if (res.code === '200') {
      data.propagateData = res.data;
      // 使用专门的接口增加浏览量
      request.put('/propagate/incrementViews/' + data.propagateId);
      
      // 加载相关文章
      loadRelatedArticles();
    } else {
      error.value = true;
      errorMessage.value = res.msg || '获取文章详情失败';
    }
  }).catch(err => {
    error.value = true;
    errorMessage.value = '获取文章详情失败';
    console.error(err);
  }).finally(() => {
    loading.value = false;
  });
}

const loadRelatedArticles = () => {
  // 基于当前文章的标签获取相关文章
  const params = {
    pageNum: 1,
    pageSize: 4,
    orderBy: 'time',
    orderType: 'desc'
  };
  
  // 如果有标签，添加标签筛选条件
  if (data.propagateData.tag) {
    params.tag = data.propagateData.tag;
  }
  
  request.get('/propagate/selectPageFront', { params }).then(res => {
    if (res.code === '200') {
      // 过滤掉当前文章
      relatedArticles.value = res.data.list
        .filter(article => article.id !== data.propagateId)
        .slice(0, 4); // 最多显示4篇相关文章
    }
  }).catch(err => {
    console.error('获取相关文章失败', err);
  });
}

const goBack = () => {
  router.push('/employee/propagate');
}

const navToArticle = (id) => {
  router.push(`/employee/propagateDetail?id=${id}`);
}

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
}

// 监听路由变化，当id变化时重新加载文章
watch(() => route.query.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    loadPropagate();
  }
}, { immediate: false });

onMounted(() => {
  loadPropagate();
})
</script>

<style scoped>
.article-content {
  line-height: 1.8;
  color: #333;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3),
.article-content :deep(h4),
.article-content :deep(h5),
.article-content :deep(h6) {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
  font-weight: 600;
  color: #2A5C8A;
}

.article-content :deep(p) {
  margin-bottom: 1em;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 0.5rem;
  margin: 1rem 0;
}

.article-content :deep(a) {
  color: #2A5C8A;
  text-decoration: underline;
}

.article-content :deep(ul),
.article-content :deep(ol) {
  margin-left: 1.5rem;
  margin-bottom: 1rem;
}

.article-content :deep(blockquote) {
  border-left: 4px solid #2A5C8A;
  padding-left: 1rem;
  color: #666;
  font-style: italic;
  margin: 1rem 0;
}

.article-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
}

.article-content :deep(th),
.article-content :deep(td) {
  border: 1px solid #ddd;
  padding: 0.5rem;
}

.article-content :deep(th) {
  background-color: #f5f5f5;
  font-weight: bold;
}

.article-content :deep(pre) {
  background-color: #f5f5f5;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1rem 0;
}

.article-content :deep(code) {
  font-family: monospace;
  background-color: #f5f5f5;
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
}
</style>