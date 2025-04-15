<template>
  <div class="min-h-screen bg-gradient-to-b from-white to-blue-50 flex flex-col">
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
        <div class="bg-white rounded-xl shadow-lg overflow-hidden">
          <!-- Card Header -->
          <div class="bg-[#2A5C8A] p-6 text-white">
            <h1 class="text-2xl font-bold text-center">员工入口</h1>
            <p class="text-blue-100 text-center mt-2">登录您的账号以访问心理健康服务</p>
          </div>
          
          <!-- Login Form -->
          <div class="p-6">
            <form @submit.prevent="handleLogin">
              <!-- Alert for errors -->
              <div v-if="errorMessage" class="mb-4 p-3 bg-red-50 border border-red-200 text-red-600 rounded-md text-sm">
                {{ errorMessage }}
              </div>
              
              <!-- Email/Username Field -->
              <div class="mb-4">
                <label for="username" class="block text-sm font-medium text-gray-700 mb-1">工号/用户名</label>
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
                    placeholder="请输入您的工号或用户名"
                    required
                    autocomplete="username"
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
                    placeholder="请输入您的密码"
                    required
                    autocomplete="current-password"
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
            
          </div>
        </div>
        
        <!-- Wellness Tip -->
        <div class="mt-6 bg-blue-50 rounded-lg p-4 border border-blue-100">
          <div class="flex">
            <div class="flex-shrink-0">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="16" x2="12" y2="12"></line>
                <line x1="12" y1="8" x2="12.01" y2="8"></line>
              </svg>
            </div>
            <div class="ml-3">
              <h3 class="text-sm font-medium text-blue-800">今日心理小贴士</h3>
              <div class="mt-2 text-sm text-blue-700">
                <p>深呼吸是缓解压力的有效方法。试着吸气4秒，屏息4秒，呼气6秒，重复5次。</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    
    <!-- Footer -->
    <footer class="bg-white py-4 border-t">
      <div class="container mx-auto px-4">
        <p class="text-center text-sm text-gray-500">
          © {{ new Date().getFullYear() }} 心理健康管理系统. 保留所有权利.
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';


// 表单数据
const username = ref('');
const password = ref('');
const rememberMe = ref(false);
const showPassword = ref(false);
const isLoading = ref(false);
const errorMessage = ref('');

// 登录处理
const handleLogin = async () => {
  errorMessage.value = '';
  
  if (!username.value || !password.value) {
    errorMessage.value = '请输入用户名和密码';
    return;
  }
  
  try {
    isLoading.value = true;
    
    const loginUrl = `${import.meta.env.VITE_APP_BASE_API}/login`;
    console.log('登录请求URL:', loginUrl);
    console.log('登录请求数据:', {
      username: username.value,
      password: password.value,
      role: 'USER'
    });
    
    const response = await axios.post(loginUrl, {
      username: username.value,
      password: password.value,
      role: 'USER'
    });
    
    console.log('登录响应:', response.data);
    const { code, msg, data } = response.data;

    if (code === "200") {
      // 检查响应中是否包含token
      console.log('登录成功，用户数据:', data);
      console.log('token值:', data.token);
      console.log('token类型:', typeof data.token);
      console.log('token长度:', data.token ? data.token.length : 0);
      
      // 确保data中包含token字段
      if (!data || !data.token) {
        console.error('登录响应中缺少token字段');
        throw new Error('登录成功但未获取到授权信息');
      }
      
      // 存储用户信息，确保包含token
      localStorage.setItem('xm-user', JSON.stringify(data));
      console.log('存储到localStorage的数据:', JSON.stringify(data));
      
      // 使用Vue Router跳转
      router.push('/employee/Home');
    } else {
      throw new Error(msg || '登录失败，请稍后再试');
    }
    
  } catch (error) {
    console.error('登录失败:', error);
    errorMessage.value = error.message || '登录失败，请稍后再试';
  } finally {
    isLoading.value = false;
  }
};

const router = useRouter();
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