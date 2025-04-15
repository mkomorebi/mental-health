<template>
    <div class="bg-gray-50 py-10 min-h-screen">
      <div class="max-w-3xl mx-auto px-4">
        <!-- 标题 -->
        <div class="mb-8 text-center">
          <h1 class="text-3xl font-bold text-[#2A5C8A] mb-2">个人中心</h1>
          <p class="text-gray-600">管理您的个人信息和账户设置</p>
        </div>
        
        <!-- 个人信息卡片 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
          <!-- 头像上传区域 -->
          <div class="flex flex-col items-center mb-6">
            <el-upload
              :action="baseUrl + '/files/upload'"
              :on-success="handleFileUpload"
              :show-file-list="false"
              class="avatar-uploader"
            >
              <div class="relative group">
                <img 
                  v-if="data.user.avatar" 
                  :src="data.user.avatar" 
                  class="w-32 h-32 rounded-full border-4 border-white shadow-md object-cover"
                />
                <div 
                  v-else 
                  class="w-32 h-32 rounded-full border-4 border-white shadow-md bg-gray-100 flex items-center justify-center"
                >
                  <svg 
                    xmlns="http://www.w3.org/2000/svg" 
                    class="h-12 w-12 text-gray-400" 
                    viewBox="0 0 24 24" 
                    fill="none" 
                    stroke="currentColor" 
                    stroke-width="2" 
                    stroke-linecap="round" 
                    stroke-linejoin="round"
                  >
                    <path d="M12 5v14M5 12h14"/>
                  </svg>
                </div>
                <div class="absolute inset-0 bg-black bg-opacity-30 rounded-full opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                  <svg 
                    xmlns="http://www.w3.org/2000/svg" 
                    class="h-8 w-8 text-white" 
                    viewBox="0 0 24 24" 
                    fill="none" 
                    stroke="currentColor" 
                    stroke-width="2" 
                    stroke-linecap="round" 
                    stroke-linejoin="round"
                  >
                    <path d="M12 5v14M5 12h14"/>
                  </svg>
                </div>
              </div>
            </el-upload>
            <p class="mt-3 text-sm text-gray-500">点击头像上传新照片</p>
          </div>
          
          <!-- 个人信息表单 -->
          <div class="space-y-4">
            <!-- 用户名 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
              <div class="relative">
                <input
                  v-model="data.user.username"
                  type="text"
                  disabled
                  class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent bg-gray-100 text-gray-600"
                />
                <span class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                  </svg>
                </span>
              </div>
            </div>
            
            <!-- 姓名 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">姓名</label>
              <input
                v-model="data.user.name"
                type="text"
                placeholder="请输入您的姓名"
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
              />
            </div>
            
            <!-- 电话 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">电话</label>
              <input
                v-model="data.user.phone"
                type="tel"
                placeholder="请输入您的电话号码"
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
              />
            </div>
            
            <!-- 邮箱 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
              <input
                v-model="data.user.email"
                type="email"
                placeholder="请输入您的邮箱地址"
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
              />
            </div>
            
            <!-- 保存按钮 -->
            <div class="pt-4 flex justify-center">
              <button
                @click="update"
                class="py-3 px-6 bg-[#4a9be6] text-white rounded-md hover:bg-[#7c97ea] transition-all duration-200 shadow-lg transform hover:-translate-y-0.5 flex items-center justify-center"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                  <polyline points="17 21 17 13 7 13 7 21"/>
                  <polyline points="7 3 7 8 15 8"/>
                </svg>
                保存更改
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { reactive } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage } from "element-plus";
  
  const baseUrl = import.meta.env.VITE_BASE_URL;
  
  const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}')
  });
  
  const handleFileUpload = (res) => {
    if (res.code === '200') {
      data.user.avatar = res.data;
      ElMessage.success('头像上传成功');
    } else {
      ElMessage.error(res.msg || '头像上传失败');
    }
  };
  
  const emit = defineEmits(['updateUser']);
  
  const update = () => {
    if (data.user.role === 'USER') {
      request.put('/user/update', data.user).then(res => {
        if (res.code === '200') {
          ElMessage.success('个人信息更新成功');
          localStorage.setItem('xm-user', JSON.stringify(data.user));
          emit('updateUser');
        } else {
          ElMessage.error(res.msg);
        }
      }).catch(err => {
        ElMessage.error('更新失败，请稍后重试');
        console.error(err);
      });
    }
  };
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
  
  /* 头像上传区域过渡效果 */
  .avatar-uploader .el-upload {
    transition: all 0.3s ease;
  }
  
  .avatar-uploader .el-upload:hover {
    transform: scale(1.05);
  }
  </style>