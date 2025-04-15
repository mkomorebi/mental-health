<template>
    <div class="dashboard-container">
      <!-- Welcome Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4">
        <div class="text-lg font-medium text-gray-800">您好！{{ data.user?.name }}，欢迎使用本系统！</div>
      </div>
  
      <!-- Content Area -->
      <div class="flex flex-col lg:flex-row gap-4">
        <!-- Announcements Card -->
        <div class="bg-white rounded-lg shadow-md p-4 flex-1">
          <div class="text-xl font-bold text-gray-800 mb-6 pb-2 border-b border-gray-100">系统公告</div>
          
          <!-- 加载状态 -->
          <div v-if="data.loading" class="py-8 text-center">
            <el-spinner size="medium"></el-spinner>
            <p class="mt-2 text-gray-500">加载中...</p>
          </div>
          
          <!-- 公告内容 -->
          <el-timeline v-else>
            <el-timeline-item
              v-for="(item, index) in data.noticeData"
              :key="index"
              :timestamp="item.time"
              :hide-timestamp="false"
              class="mb-4"
            >
              <div class="bg-gray-50 rounded-lg p-3 shadow-sm">
                <div class="text-gray-700">{{ item.content }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
          
          <!-- Empty State -->
          <div v-if="!data.loading && !data.noticeData.length" class="py-8 text-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
            <p class="mt-2 text-gray-500">暂无公告</p>
          </div>
        </div>
  
        <!-- Empty Space for Future Content -->
        <div class="flex-1 hidden lg:block"></div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { reactive, onMounted } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage } from "element-plus";
  
  const data = reactive({
    user: {},
    noticeData: [],
    loading: true
  })
  
  // 安全地解析用户数据
  try {
    data.user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  } catch (error) {
    console.error('Failed to parse user data:', error);
    ElMessage.error('获取用户信息失败');
  }
  
  const loadNotice = () => {
    data.loading = true;
    request.get('/notice/selectAll')
      .then(res => {
        if (res.code === '200') {
          data.noticeData = res.data.slice(0, 4); // 直接限制为最多4条
        } else {
          ElMessage.error(res.msg || '获取公告失败');
        }
      })
      .catch(err => {
        console.error('Failed to load notices:', err);
        ElMessage.error('网络错误，获取公告失败');
      })
      .finally(() => {
        data.loading = false;
      });
  }
  
  onMounted(() => {
    loadNotice();
  });
  </script>
  
  <style scoped>
  .dashboard-container {
    width: 100%;
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    padding: 1rem;
    background-color: #f5f7fa;
  }
  
  :deep(.el-timeline) {
    padding-left: 10px;
  }
  
  :deep(.el-timeline-item__timestamp) {
    color: #6b7280;
    font-size: 0.875rem;
    margin-bottom: 0.5rem;
  }
  
  :deep(.el-timeline-item__node) {
    background-color: #2A5C8A;
    width: 12px;
    height: 12px;
  }
  
  :deep(.el-timeline-item__wrapper) {
    padding-left: 20px;
  }
  </style>