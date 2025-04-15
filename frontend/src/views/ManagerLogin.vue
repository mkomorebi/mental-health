<template>
  <div class="min-h-screen bg-gradient-to-b from-gray-50 to-white flex flex-col">
    <!-- Header -->
    <header class="bg-white/90 backdrop-blur-sm border-b h-16">
      <div class="container mx-auto h-full flex items-center px-4">
        <div class="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m8 3 4 8 5-5 5 15H2L8 3z"/>
          </svg>
          <span class="ml-2 font-bold text-xl text-[#2A5C8A]">心理健康管理系统</span>
        </div>
      </div>
    </header>
    
    <!-- Main Content -->
    <main class="flex-1 flex items-center justify-center p-4">
      <div class="w-full max-w-md">
        <!-- Login Card -->
        <div class="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100">
          <!-- Card Header -->
          <div class="bg-[#2A5C8A] p-6 text-white">
            <h1 class="text-2xl font-bold text-center">系统登录</h1>
            <p class="text-blue-100 text-center mt-2">请输入您的账号密码登录系统</p>
          </div>
          
          <!-- Login Form -->
          <div class="p-6">
            <form @submit.prevent="handleLogin">
              <!-- Alert for errors -->
              <div v-if="errorMessage" class="mb-4 p-3 bg-red-50 border border-red-200 text-red-600 rounded-md text-sm">
                {{ errorMessage }}
              </div>
              
              <!-- Role Selection -->
              <div class="mb-4">
                <label class="block text-sm font-medium text-gray-700 mb-1">登录身份</label>
                <div class="grid grid-cols-2 gap-2">
                  <button
                    type="button"
                    @click="role = 'ADMIN'"
                    :class="{
                      'bg-[#2A5C8A] text-white': role === 'ADMIN',
                      'bg-gray-100 text-gray-700 hover:bg-gray-200': role !== 'ADMIN'
                    }"
                    class="py-2 px-4 rounded-md text-sm font-medium transition-colors"
                  >
                    管理员
                  </button>
                  <button
                    type="button"
                    @click="role = 'DOCTOR'"
                    :class="{
                      'bg-[#2A5C8A] text-white': role === 'DOCTOR',
                      'bg-gray-100 text-gray-700 hover:bg-gray-200': role !== 'DOCTOR'
                    }"
                    class="py-2 px-4 rounded-md text-sm font-medium transition-colors"
                  >
                    医生
                  </button>
                </div>
              </div>
              
              <!-- Username Field -->
              <div class="mb-4">
                <label for="username" class="block text-sm font-medium text-gray-700 mb-1">账号</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                      <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                  </div>
                  <input 
                    v-model="username"
                    type="text" 
                    id="username" 
                    class="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#2A5C8A] focus:border-[#2A5C8A]" 
                    placeholder="请输入账号"
                    required
                  />
                </div>
              </div>
              
              <!-- Password Field -->
              <div class="mb-6">
                <label for="password" class="block text-sm font-medium text-gray-700 mb-1">密码</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                      <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                    </svg>
                  </div>
                  <input 
                    v-model="password"
                    :type="showPassword ? 'text' : 'password'" 
                    id="password" 
                    class="block w-full pl-10 pr-10 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#2A5C8A] focus:border-[#2A5C8A]" 
                    placeholder="请输入密码"
                    required
                  />
                  <button 
                    type="button"
                    class="absolute inset-y-0 right-0 pr-3 flex items-center"
                    @click="showPassword = !showPassword"
                  >
                    <svg v-if="showPassword" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                      <line x1="1" y1="1" x2="23" y2="23"></line>
                    </svg>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                      <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                  </button>
                </div>
              </div>
              
              <!-- Remember Me & Forgot Password -->
              <div class="flex items-center justify-between mb-6">
                <div class="flex items-center">
                  <input 
                    v-model="rememberMe"
                    id="remember-me" 
                    type="checkbox" 
                    class="h-4 w-4 text-[#2A5C8A] focus:ring-[#2A5C8A] border-gray-300 rounded" 
                  />
                  <label for="remember-me" class="ml-2 block text-sm text-gray-700">
                    记住我
                  </label>
                </div>
                <div class="text-sm">
                  <a href="#" class="text-[#2A5C8A] hover:underline">
                    忘记密码?
                  </a>
                </div>
              </div>
              
              <!-- Login Button -->
              <div>
                <button 
                  type="submit" 
                  class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#2A5C8A] hover:bg-[#1e4266] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#2A5C8A]"
                  :disabled="isLoading"
                >
                  <svg v-if="isLoading" class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  {{ isLoading ? '登录中...' : '登录' }}
                </button>
              </div>
            </form>
            
            <!-- Security Notice -->
            <div class="mt-6 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
              </svg>
              <p class="ml-2 text-xs text-gray-500">
                系统入口受到安全保护，未授权访问将被记录并追责
              </p>
            </div>
          </div>
        </div>
      </div>
    </main>
    
    <!-- Footer -->
    <footer class="bg-white py-4 border-t">
      <div class="container mx-auto px-4">
        <div class="flex flex-col md:flex-row justify-between items-center">
          <p class="text-sm text-gray-500">
            © {{ new Date().getFullYear() }} 心理健康管理系统. 保留所有权利.
          </p>
          <div class="flex space-x-4 mt-2 md:mt-0">
            <a href="#" class="text-sm text-gray-500 hover:text-gray-700">隐私政策</a>
            <a href="#" class="text-sm text-gray-500 hover:text-gray-700">使用条款</a>
            <a href="#" class="text-sm text-gray-500 hover:text-gray-700">联系我们</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

// 表单数据
const username = ref('');
const password = ref('');
const role = ref('ADMIN'); // 默认选择管理员
const rememberMe = ref(false);
const showPassword = ref(false);
const isLoading = ref(false);
const errorMessage = ref('');

// 根据角色跳转到不同页面
const redirectByRole = (role) => {
  switch(role) {
    case 'ADMIN':
      return '/admin';
    case 'DOCTOR':
      return '/doctor';
    default:
      return '/';
  }
};

// 登录处理
const handleLogin = async () => {
  // 重置错误信息
  errorMessage.value = '';
  
  // 表单验证
  if (!username.value || !password.value) {
    errorMessage.value = '请输入用户名和密码';
    return;
  }
  
  try {
    // 显示加载状态
    isLoading.value = true;
    
    // 使用 import.meta.env 访问环境变量
    const loginUrl = `${import.meta.env.VITE_APP_BASE_API}/login`;
    console.log('请求的登录 URL:', loginUrl);
    
    // 发送登录请求，包含角色信息
    const response = await axios.post(loginUrl, {
      username: username.value,
      password: password.value,
      role: role.value // 将前端选择的角色发送到后端
    });
    
    // 检查响应状态
    console.log('响应数据:', response.data);
    const { code, msg, data } = response.data;

    if (code === "200") {
      console.log('登录成功', {
        username: username.value,
        role: role.value,
        rememberMe: rememberMe.value
      });
      
      // 确保data中包含token字段
      if (!data || !data.token) {
        console.error('登录响应中缺少token字段');
        throw new Error('登录成功但未获取到授权信息');
      }
      
      // 存储用户信息，包含角色
      localStorage.setItem('xm-user', JSON.stringify({
        ...data,
        role: role.value // 确保角色信息被存储
      }));
      console.log('存储到localStorage的数据:', JSON.stringify(data));
      
      // 登录成功后重定向到对应角色的主页
      window.location.href = redirectByRole(role.value);
    } else {
      throw new Error(msg || '登录失败，请稍后再试');
    }
    
  } catch (error) {
    // 处理登录错误
    console.error('登录失败:', error);
    errorMessage.value = error.message || '登录失败，请稍后再试';
  } finally {
    // 无论成功或失败，都关闭加载状态
    isLoading.value = false;
  }
};
</script>

<style>
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>