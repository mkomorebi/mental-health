<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>
<template>
  <div class="bg-gray-50 py-10 min-h-screen">
    <div class="max-w-6xl mx-auto px-4">
      <!-- 标题和描述 -->
      <div class="mb-8 text-center">
        <h1 class="text-3xl font-bold text-[#2A5C8A] mb-2">心理测试</h1>
        <p class="text-gray-600">选择适合您的心理测试，了解自己的心理健康状况</p>
      </div>
      
      <!-- 测试类型筛选 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-4 mb-6">
        <div class="flex flex-wrap gap-2">
          <button 
            @click="loadTestPaper(null)" 
            class="px-4 py-2 rounded-md transition-all duration-200 text-sm font-medium transform hover:-translate-y-0.5"
            :class="data.typeName === null ? 
              'bg-[#2A5C8A] text-white' : 
              'bg-gray-100 text-gray-700 hover:bg-gray-200'"
          >
            全部
          </button>
          <button 
            v-for="typeName in allTypeNames" 
            :key="typeName"
            @click="loadTestPaper(typeName)" 
            class="px-4 py-2 rounded-md transition-colors text-sm font-medium"
            :class="data.typeName === typeName ? 
              'bg-[#2A5C8A] text-white' : 
              'bg-gray-100 text-gray-700 hover:bg-gray-200'"
          >
            {{ typeName }}
          </button>
        </div>
      </div>
      
      <!-- 测试列表 -->
      <div v-if="data.testPaperData.length === 0" class="text-center py-10">
        <div class="max-w-md mx-auto">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 mx-auto text-gray-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
          <h3 class="text-lg font-medium text-gray-700 mb-1">暂无测试</h3>
          <p class="text-gray-500 mb-4">当前分类下暂无心理测试内容</p>
        </div>
      </div>
      
      <div class="space-y-6">
        <div 
          v-for="item in data.testPaperData" 
          :key="item.id" 
          class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-all duration-300 hover:border-[#2A5C8A]/30"
        >
          <div class="p-6 flex flex-col md:flex-row gap-6">
            <!-- 测试内容 -->
            <div class="flex-1">
              <h2 class="text-xl font-semibold text-gray-800 hover:text-[#2A5C8A] transition-colors">
                {{ item.title }}
              </h2>
              <p class="mt-3 text-gray-600 line-clamp-3">{{ item.content }}</p>
              
              <div class="mt-4 flex flex-wrap items-center text-sm gap-y-2">
                <div class="flex items-center mr-4">
                  <img 
                    :src="item.doctorAvatar" 
                    alt="" 
                    class="w-6 h-6 rounded-full border border-gray-200"
                  >
                  <span class="ml-2 text-gray-800">{{ item.doctorName }}</span>
                </div>
                
                <div class="flex items-center mr-4 text-gray-500">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                  </svg>
                  <span>{{ item.testNum }} 人测试</span>
                </div>
                
                <div class="flex items-center mr-4 text-gray-500">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                  </svg>
                  <span>{{ item.time }} </span>
                </div>
                
                <div class="flex items-center text-gray-500">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
                    <line x1="7" y1="7" x2="7.01" y2="7"></line>
                  </svg>
                  <span>{{ item.typeName }}</span>
                </div>
              </div>
            </div>
            
            <!-- 测试图片和按钮 -->
            <div class="md:w-64 flex flex-col items-center">
              <div class="w-full h-40 overflow-hidden rounded-lg mb-4">
                <img 
                  :src="item.img" 
                  alt="" 
                  class="w-full h-full object-cover transition-transform duration-500 hover:scale-105"
                >
              </div>
              <button 
                @click="navTo('/employee/testPaperDetail?id=' + item.id)" 
                class="w-full py-2 bg-[#2A5C8A] text-white rounded-md hover:bg-[#1e4266] transition-all duration-200 transform hover:-translate-y-0.5 shadow-sm flex items-center justify-center"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="9 11 12 14 22 4"></polyline>
                  <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>
                </svg>
                开始测试
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const data = reactive({
  testPaperData: [],
  typeName: null
});

// 存储所有可用的类型名称
const allTypeNames = ref([]);

// 初始加载所有测试数据和类型
const loadInitialData = () => {
  // 加载所有测试数据
  request.get('/testPaper/selectAll').then(res => {
    if (res.code === '200') {
      data.testPaperData = res.data;
      
      // 提取所有不重复的类型名称
      const typeNames = data.testPaperData
        .map(paper => paper.typeName)
        .filter(name => name);
      allTypeNames.value = [...new Set(typeNames)];
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const loadTestPaper = (typeName) => {
  data.typeName = typeName;
  request.get('/testPaper/selectAll', {
    params: {
      typeName: typeName
    }
  }).then(res => {
    if (res.code === '200') {
      data.testPaperData = res.data;
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const navTo = (url) => {
  location.href = url;
};

onMounted(() => {
  loadInitialData();
});
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>