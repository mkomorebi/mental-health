<template>
  <div class="min-h-screen flex flex-col bg-gray-50">
    <!-- Header -->
    <header class="bg-[#2A5C8A] shadow-md fixed top-0 left-0 right-0 z-50">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center h-16">
          <!-- Logo and Title -->
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m8 3 4 8 5-5 5 15H2L8 3z"/>
            </svg>
            <span class="ml-2 font-bold text-xl text-white">心理健康管理系统</span>
          </div>
          
          <!-- Navigation Menu -->
          <nav class="hidden lg:flex flex-1 justify-center" style="height: 64px;">
            <ul class="flex h-full">
              <li v-for="(item, index) in navigationItems" :key="index" class="flex h-full">
                <router-link 
                  :to="item.path" 
                  class="text-sm font-medium transition-all duration-200"
                  :class="[
                    router.currentRoute.value.path === item.path 
                      ? 'bg-[#1e4266] text-white font-semibold' 
                      : 'text-blue-100 hover:bg-[#3a6d9a] hover:text-white'
                  ]"
                  style="display: flex; align-items: center; height: 100%; padding: 0 1rem; position: relative;"
                >
                  {{ item.name }}
                  <div 
                    v-if="router.currentRoute.value.path === item.path"
                    class="absolute bottom-0 left-0 w-full h-1 bg-white"
                    style="opacity: 0.7;"
                  ></div>
                </router-link>
              </li>
            </ul>
          </nav>
          
          <!-- User Actions -->
          <div class="flex items-center">
            <template v-if="!data.user.id">
              <button 
                @click="router.push('/login')"
                class="ml-2 px-4 py-2 text-sm font-medium text-[#2A5C8A] bg-white rounded-md hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#2A5C8A]"
              >
                登录
              </button>
              <button 
                @click="router.push('/register')"
                class="ml-2 px-4 py-2 text-sm font-medium text-white bg-[#1e4266] rounded-md hover:bg-[#163452] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#2A5C8A]"
              >
                注册
              </button>
            </template>
            
            <div v-else class="relative user-menu">
              <div 
                @click="toggleUserMenu"
                class="flex items-center px-3 py-2 text-sm text-white rounded-md cursor-pointer hover:bg-[#3a6d9a] transition-colors duration-200"
              >
                <img 
                  :src="data.user.avatar"
                  alt="用户头像"
                  class="w-8 h-8 rounded-full object-cover border-2 border-white"
                >
                <span class="ml-2 hidden sm:inline">{{ data.user.name }}</span>
                <svg 
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-4 w-4 ml-1 transition-transform duration-200"
                  :class="{ 'rotate-180': userMenuOpen }"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <polyline points="6 9 12 15 18 9"></polyline>
                </svg>
              </div>
              
              <!-- Dropdown Menu -->
              <div 
                v-show="userMenuOpen" 
                class="absolute right-0 mt-2 w-56 bg-white rounded-md shadow-lg py-1 z-10 border border-gray-200 transform origin-top-right transition-all duration-200"
              >
                <div class="px-4 py-3 border-b border-gray-100">
                  <div class="flex items-center">
                    <img 
                      :src="data.user.avatar"
                      alt="用户头像"
                      class="w-10 h-10 rounded-full object-cover border border-gray-200"
                    >
                    <div class="ml-3">
                      <p class="text-sm font-medium text-gray-900">{{ data.user.name }}</p>
                      <p class="text-xs text-gray-500 truncate">{{ data.user.email || '未设置邮箱' }}</p>
                    </div>
                  </div>
                </div>
                
                <div class="py-1">
                  <a
                    v-for="(item, index) in userMenuItems" 
                    :key="index"
                    @click="handleUserMenuItem(item.action)"
                    class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer transition-colors duration-150"
                    :class="{'border-t border-gray-100': item.divider}"
                  >
                    <span v-if="item.icon" class="mr-3 text-gray-500">
                      <svg v-if="item.icon === 'DocumentTextIcon'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </svg>
                      <svg v-else-if="item.icon === 'ChatAlt2Icon'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z" />
                      </svg>
                      <svg v-else-if="item.icon === 'AnnotationIcon'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                      </svg>
                      <svg v-else-if="item.icon === 'KeyIcon'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                      </svg>
                      <svg v-else-if="item.icon === 'LogoutIcon'" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                      </svg>
                    </span>
                    {{ item.name }}
                  </a>
                </div>
              </div>
            </div>
            
            <!-- Mobile Menu Button -->
            <button 
              @click="mobileMenuOpen = !mobileMenuOpen"
              class="lg:hidden ml-2 inline-flex items-center justify-center p-2 rounded-md text-white hover:bg-[#3a6d9a] focus:outline-none"
            >
              <svg 
                xmlns="http://www.w3.org/2000/svg" 
                class="h-6 w-6" 
                fill="none" 
                viewBox="0 0 24 24" 
                stroke="currentColor"
              >
                <path 
                  v-if="!mobileMenuOpen" 
                  stroke-linecap="round" 
                  stroke-linejoin="round" 
                  stroke-width="2" 
                  d="M4 6h16M4 12h16M4 18h16"
                />
                <path 
                  v-else 
                  stroke-linecap="round" 
                  stroke-linejoin="round" 
                  stroke-width="2" 
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>
      
      <!-- Mobile Navigation Menu -->
      <div 
        v-show="mobileMenuOpen" 
        class="lg:hidden bg-[#1e4266] pb-3 px-4"
      >
        <div class="space-y-1">
          <router-link 
            v-for="(item, index) in navigationItems" 
            :key="index"
            :to="item.path" 
            class="block px-3 py-2 rounded-md text-base font-medium"
            :class="[
              router.currentRoute.value.path === item.path 
                ? 'bg-[#3a6d9a] text-white' 
                : 'text-blue-100 hover:bg-[#3a6d9a] hover:text-white'
            ]"
            @click="mobileMenuOpen = false"
          >
            {{ item.name }}
          </router-link>
        </div>
      </div>
    </header>
    
    <!-- Main Content -->
    <main class="flex-1 pt-16">
      <div class="container mx-auto px-4 py-6 min-h-\[calc\(100vh-64px-56px\)\]">
        <RouterView @updateUser="updateUser" />
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
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import router from "@/router/index.ts";
import request from "@/utils/request.js";

// 响应式状态
const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
});

const mobileMenuOpen = ref(false);
const userMenuOpen = ref(false);

// 导航菜单项
const navigationItems = [
  { name: '首页', path: '/employee/Home' },
  { name: '心理测试', path: '/employee/testPaper' },
  { name: '心理健康科普', path: '/employee/propagate' },
  { name: 'Ai心理咨询', path: '/employee/chat/ai' },
  { name: '心理医生预约', path: '/employee/reservation' },
  { name: '个人档案', path: '/employee/personalFile' },
  { name: '个人中心', path: '/employee/person' },
];

// 用户菜单项
const userMenuItems = [
  { 
    name: '测试记录', 
    action: '/employee/testRecord',
    icon: 'DocumentTextIcon'
  },
  { 
    name: '反馈与建议', 
    action: '/employee/feedback',
    icon: 'ChatAlt2Icon'
  },
  { 
    name: '我的反馈', 
    action: '/employee/myFeedback',
    icon: 'AnnotationIcon'
  },
  { 
    name: '修改密码', 
    action: '/employee/password',
    icon: 'KeyIcon',
    divider: true
  },
  { 
    name: '退出登录', 
    action: 'logout',
    icon: 'LogoutIcon'
  },
];

// 处理用户菜单项点击
const handleUserMenuItem = (action) => {
  userMenuOpen.value = false;
  
  if (action === 'logout') {
    logout();
  } else {
    router.push(action);
  }
};

// 切换用户菜单显示状态
const toggleUserMenu = (event) => {
  event.stopPropagation();
  userMenuOpen.value = !userMenuOpen.value;
};

// 导航到指定URL
const navTo = (url) => {
  location.href = url;
};

// 更新用户信息
const updateUser = () => {
  try {
    const userData = localStorage.getItem('xm-user');
    console.log('从localStorage获取的用户数据:', userData);
    
    if (userData) {
      data.user = JSON.parse(userData);
      console.log('解析后的用户数据:', data.user);
      
      // 检查token是否存在
      if (!data.user.token) {
        console.warn('用户数据中缺少token字段');
      }
    } else {
      console.warn('localStorage中未找到用户数据');
      data.user = {};
    }
  } catch (error) {
    console.error('解析用户数据时出错:', error);
    data.user = {};
  }
};

// 退出登录
const logout = () => {
  console.log('执行退出登录');
  localStorage.removeItem('xm-user');
  data.user = {};
  router.push('/');
};

// 点击外部关闭下拉菜单
const handleClickOutside = (event) => {
  if (userMenuOpen.value && !event.target.closest('.user-menu')) {
    userMenuOpen.value = false;
  }
};

// 生命周期钩子
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  updateUser(); // 确保在组件挂载时更新用户信息
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style>
/* 全局样式可以在这里添加 */

/* 添加滚动行为样式 */
main {
  overflow-y: auto;
}

/* 确保内容区域最小高度正确，减去头部和底部的高度 */
.min-h-\[calc\(100vh-64px-56px\)\] {
  min-height: calc(100vh - 64px - 56px);
}

/* 用户菜单项过渡效果 */
.user-menu-enter-active,
.user-menu-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.user-menu-enter-from,
.user-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 添加菜单项悬停效果 */
.user-menu a:hover svg {
  color: #2A5C8A;
}

/* 移动菜单过渡效果 */
.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: max-height 0.3s ease-in-out;
  overflow: hidden;
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  max-height: 0;
}
</style>