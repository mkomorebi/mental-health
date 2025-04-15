<template>
  <div class="doctor-layout min-h-screen flex flex-col bg-gray-50">
    <!-- 顶部导航栏 -->
    <header class="bg-[#2A5C8A] shadow-md fixed top-0 left-0 right-0 z-50">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center h-16">
          <!-- Logo和系统名称 -->
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m8 3 4 8 5-5 5 15H2L8 3z"/>
            </svg>
            <span class="ml-2 font-bold text-xl text-white" v-show="!isCollapse">心理健康管理系统</span>
          </div>
          
          <!-- 用户操作区 -->
          <div class="flex items-center space-x-4">
            <!-- 通知图标 -->
            <!--<div class="relative cursor-pointer" @click="showNotifications">
              <el-badge :value="notificationCount" :max="99" class="cursor-pointer">
                <el-icon :size="20" color="#fff"><Bell /></el-icon>
              </el-badge>
            </div>-->
            
            <!-- 用户菜单 -->
            <div class="relative">
              <div 
                @click="userMenuOpen = !userMenuOpen"
                class="flex items-center px-3 py-2 text-sm text-white rounded-md cursor-pointer hover:bg-[#3a6d9a] transition-colors"
              >
                <img 
                  :src="userInfo.avatar || '/default-avatar.png'"
                  alt="用户头像"
                  class="w-8 h-8 rounded-full object-cover border-2 border-white"
                >
                <span class="ml-2">{{ userInfo.name || '医生' }}</span>
                <el-icon :class="{ 'rotate-180': userMenuOpen }" class="transition-transform">
                  <ArrowDown />
                </el-icon>
              </div>
              
              <!-- 用户下拉菜单 -->
              <transition name="el-zoom-in-top">
                <div 
                  v-show="userMenuOpen" 
                  class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 divide-y divide-gray-100"
                  ref="userMenuRef"
                >
                  <div class="px-4 py-3 text-sm text-gray-900">
                    <div class="font-medium">{{ userInfo.name }}</div>
                    <div class="truncate text-gray-500">{{ userInfo.roleName || '心理医生' }}</div>
                  </div>
                  <!--<div class="py-1">
                    <a 
                      @click="navigateTo('/doctor/person')"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer flex items-center"
                    >
                      <el-icon class="mr-2"><User /></el-icon>个人资料
                    </a>
                    <a 
                      @click="navigateTo('/doctor/doctorAuthentication')"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer flex items-center"
                    >
                      <el-icon class="mr-2"><Document /></el-icon>资格认证
                    </a>
                    <a 
                      @click="navigateTo('/doctor/password')"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer flex items-center"
                    >
                      <el-icon class="mr-2"><Lock /></el-icon>修改密码
                    </a>
                  </div>-->
                  <div class="py-1">
                    <a 
                      @click="logout"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer flex items-center"
                    >
                      <el-icon class="mr-2"><SwitchButton /></el-icon>退出登录
                    </a>
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>
    </header>
    
    <!-- 主体内容区 -->
    <div class="flex flex-1 overflow-hidden pt-16">
      <!-- 左侧菜单栏 -->
      <div class="h-screen bg-white shadow-md flex-shrink-0 transition-all duration-300 ease-in-out overflow-hidden fixed top-16 left-0 bottom-0 z-40" 
           :class="isCollapse ? 'w-[65px]' : 'w-64'">
        <div class="h-full flex flex-col">
          <!-- 导航菜单 -->
          <el-menu
            :default-active="activeMenu"
            class="border-r-0 flex-1 overflow-y-auto h-full"
            @select="handleMenuSelect"
            :collapse="isCollapse"
            :collapse-transition="false"
            :unique-opened="true"
          >
            <!-- 首页 -->
            <el-menu-item index="/doctor/doctorHome">
              <el-icon><HomeFilled /></el-icon>
              <template #title>工作台</template>
            </el-menu-item>
            
            <!-- 内容管理 -->
            <el-sub-menu index="content">
              <template #title>
                <el-icon><Files /></el-icon>
                <span>内容管理</span>
              </template>
              <el-menu-item 
                index="/doctor/propagate"
                v-if="userInfo.status === '审批通过'"
              >
                宣传资料
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 测试管理 -->
            <el-sub-menu index="test">
              <template #title>
                <el-icon><Document /></el-icon>
                <span>测试管理</span>
              </template>
              <el-menu-item
                index="/doctor/topic"
                v-if="userInfo.status === '审批通过'"
              >
                题库
              </el-menu-item>
              <el-menu-item 
                index="/doctor/testPaper"
                v-if="userInfo.status === '审批通过'"
              >
                试卷
              </el-menu-item>
              <el-menu-item 
                index="/doctor/testRecord"
                v-if="userInfo.status === '审批通过'"
              >
                测试记录
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 服务管理 -->
            <el-sub-menu index="service">
              <template #title>
                <el-icon><Service /></el-icon>
                <span>服务管理</span>
              </template>
              <el-menu-item index="/doctor/reservation">预约记录</el-menu-item>
              <el-menu-item index="/doctor/diagnosis">咨询记录</el-menu-item>
            </el-sub-menu>
            
            <!-- 个人中心 -->
            <el-sub-menu index="personal">
              <template #title>
                <el-icon><User /></el-icon>
                <span>个人中心</span>
              </template>
              <el-menu-item index="/doctor/person">个人资料</el-menu-item>
              <el-menu-item index="/doctor/doctorAuthentication">资格认证</el-menu-item>
              <el-menu-item index="/doctor/password">修改密码</el-menu-item>
            </el-sub-menu>
          </el-menu>
          
          <!-- 折叠按钮 -->
          <div class="p-2 flex justify-center bg-white border-t border-gray-100 sticky bottom-0">
            <el-button 
              @click="toggleCollapse" 
              type="primary"
              text
              :icon="isCollapse ? Expand : Fold" 
              class="w-full flex items-center justify-center py-2 hover:bg-blue-50 transition-colors"
            >
              <span v-if="!isCollapse">折叠菜单</span>
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 右侧内容区 -->
      <div class="flex-1 overflow-auto bg-gray-50" :class="isCollapse ? 'ml-[65px]' : 'ml-64'">
        <div class="container mx-auto px-4 py-6">
          <router-view @update-dashboard="fetchDashboardData"></router-view>
        </div>
      </div>
    </div>
    
    <!-- 移动端菜单按钮 -->
    <div class="lg:hidden fixed bottom-4 right-4 z-50">
      <el-button 
        @click="mobileMenuVisible = !mobileMenuVisible"
        type="primary"
        size="large"
        :icon="mobileMenuVisible ? 'Close' : 'Menu'"
        circle
      />
    </div>
    
    <!-- 移动端菜单抽屉 -->
    <el-drawer
      v-model="mobileMenuVisible"
      title="菜单导航"
      direction="ltr"
      size="280px"
      class="lg:hidden"
    >
      <!-- 移动端数据看板 -->
      <div class="p-4 border-b">
        <div class="text-sm font-medium text-gray-500 mb-2">今日数据</div>
        <div class="grid grid-cols-2 gap-2">
          <div class="bg-blue-50 p-2 rounded">
            <div class="text-xs text-gray-500">预约数</div>
            <div class="text-lg font-bold text-blue-600">{{ dashboardData.reservations }}</div>
          </div>
          <div class="bg-green-50 p-2 rounded">
            <div class="text-xs text-gray-500">测试量</div>
            <div class="text-lg font-bold text-green-600">{{ dashboardData.testsToday }}</div>
          </div>
        </div>
      </div>
      
      <!-- 移动端菜单 -->
      <el-menu
        :default-active="activeMenu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/doctor/home">
          <el-icon><HomeFilled /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>
        
        <el-sub-menu index="test">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>测试管理</span>
          </template>
          <el-menu-item 
            index="/doctor/topic"
            v-if="userInfo.status === '审批通过'"
          >
            题库
          </el-menu-item>
          <el-menu-item 
            index="/doctor/testPaper"
            v-if="userInfo.status === '审批通过'"
          >
            试卷
          </el-menu-item>
          <el-menu-item 
            index="/doctor/testRecord"
            v-if="userInfo.status === '审批通过'"
          >
            测试记录
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="service">
          <template #title>
            <el-icon><Service /></el-icon>
            <span>服务管理</span>
          </template>
          <el-menu-item index="/doctor/reservation">预约记录</el-menu-item>
          <el-menu-item index="/doctor/consultation">咨询记录</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  HomeFilled,
  Files,
  Document,
  Service,
  User,
  Bell,
  ArrowDown,
  Lock,
  SwitchButton,
  Expand,
  Fold,
  Menu,
  Close
} from '@element-plus/icons-vue';
import { onClickOutside } from '@vueuse/core';
import axios from 'axios';

const router = useRouter();
const route = useRoute();

const userInfo = ref({
  name: '医生',
  avatar: '',
  role: 'DOCTOR',
  roleName: '心理医生',
  status: '待审批'
});

const dashboardData = ref({
  reservations: 0,
  testsToday: 0,
  consultations: 0,
  completionRate: 0
});

const userMenuOpen = ref(false);
const activeMenu = ref('');
const isCollapse = ref(false);
const mobileMenuVisible = ref(false);
const notificationCount = ref(2);
const userMenuRef = ref(null);

const breadcrumbTitle = computed(() => {
  const routeMap = {
    'content': '内容管理',
    'test': '测试管理',
    'service': '服务管理',
    'personal': '个人中心'
  };
  return routeMap[route.meta?.menu] || '工作台';
});

const handleMenuSelect = (index) => {
  router.push(index);
  mobileMenuVisible.value = false;
};

const navigateTo = (path) => {
  userMenuOpen.value = false;
  router.push(path);
};

const logout = () => {
  localStorage.removeItem('xm-user');
  router.push('/');
};

const showNotifications = () => {
  // 显示通知面板
};

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
  localStorage.setItem('doctor-menu-collapse', isCollapse.value.toString());
  
  // 添加一个短暂延迟以确保DOM更新
  nextTick(() => {
    window.dispatchEvent(new Event('resize'));
  });
};

const fetchDashboardData = async () => {
  try {
    // 从localStorage获取用户ID
    const userData = localStorage.getItem('xm-user');
    if (userData) {
      const parsedData = JSON.parse(userData);
      const doctorId = parsedData.id;
      
      // 调用后端接口获取仪表盘数据
      const response = await axios.get(`/doctor/dashboard/${doctorId}`);
      if (response.data.code === 200) {
        dashboardData.value = response.data.data;
      }
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
    // 使用默认数据作为后备
    dashboardData.value = {
      reservations: 0,
      testsToday: 0,
      consultations: 0,
      completionRate: 0
    };
  }
};

const fetchDoctorInfo = async () => {
  try {
    // 从localStorage获取用户ID
    const userData = localStorage.getItem('xm-user');
    if (userData) {
      const parsedData = JSON.parse(userData);
      const doctorId = parsedData.id;
      
      // 调用后端接口获取最新信息
      const response = await axios.get(`/doctor/selectById/${doctorId}`);
      if (response.data.code === 200) {
        // 更新本地状态
        userInfo.value = response.data.data;
        // 更新localStorage
        localStorage.setItem('xm-user', JSON.stringify(response.data.data));
      }
    }
  } catch (error) {
    console.error('获取医生信息失败:', error);
  }
};

onMounted(() => {
  // 初始化用户信息
  const userData = localStorage.getItem('xm-user');
  if (userData) {
    userInfo.value = JSON.parse(userData);
  }
  
  // 获取最新的医生信息
  fetchDoctorInfo();
  console.log(userInfo.value.name);
  console.log(userInfo.value.avatar);
  console.log(userInfo.value.role);
  console.log(userInfo.value.roleName);
  console.log(userInfo.value.status);
  
  // 设置当前活动菜单
  activeMenu.value = route.path;
  
  // 获取菜单折叠状态
  const collapseState = localStorage.getItem('doctor-menu-collapse');
  if (collapseState !== null) {
    isCollapse.value = collapseState === 'true';
  }
  
  // 获取仪表盘数据
  fetchDashboardData();
  
  // 设置点击外部关闭用户菜单
  nextTick(() => {
    if (userMenuRef.value) {
      onClickOutside(userMenuRef, () => {
        userMenuOpen.value = false;
      });
    }
  });
});
</script>

<style scoped>
.doctor-layout {
  --el-menu-item-height: 48px;
  --el-menu-sub-item-height: 40px;
}

.el-menu {
  border-right: none;
}

.el-menu-item {
  height: var(--el-menu-item-height);
  line-height: var(--el-menu-item-height);
}

.el-sub-menu .el-menu-item {
  height: var(--el-menu-sub-item-height);
  line-height: var(--el-menu-sub-item-height);
}

/* 菜单折叠时的样式 */
.el-menu--collapse {
  width: 64px;
}

.el-menu--collapse .el-sub-menu__title span,
.el-menu--collapse .el-menu-item span {
  display: none;
}

.el-menu--collapse .el-sub-menu__title .el-sub-menu__icon-arrow {
  display: none;
}

/* 添加平滑过渡效果 */
.el-menu--collapse .el-sub-menu > .el-sub-menu__title .el-sub-menu__icon-arrow {
  display: none;
}

/* 优化折叠状态下的图标间距 */
.el-menu--collapse .el-menu-item .el-icon,
.el-menu--collapse .el-sub-menu__title .el-icon {
  margin: 0 auto;
}

/* 移动端优化 */
@media (max-width: 1023px) {
  .el-menu {
    --el-menu-item-height: 44px;
    --el-menu-sub-item-height: 36px;
  }
}

/* 优化折叠按钮样式 */
.el-button.is-text.is-primary {
  color: var(--el-color-primary);
}

.el-button.is-text.is-primary:hover {
  background-color: var(--el-color-primary-light-9);
}

/* 确保内容区域有正确的滚动行为 */
.flex-1.overflow-auto {
  height: calc(100vh - 64px);
  overflow-y: auto;
}

/* 菜单滚动条样式 */
.el-menu.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.el-menu.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.el-menu.overflow-y-auto::-webkit-scrollbar-track {
  background-color: #f5f7fa;
}
</style>