<template>
  <div class="min-h-screen bg-gradient-to-b from-white to-blue-50">
    <!-- Navigation -->
    <header
        class="fixed top-0 left-0 right-0 z-50 transition-all duration-300 bg-white/90 backdrop-blur-sm border-b"
        :class="scrolled ? 'h-16' : 'h-20'"
    >
      <div class="container mx-auto h-full flex items-center justify-between px-4">
        <div class="flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m8 3 4 8 5-5 5 15H2L8 3z"/>
          </svg>
          <span class="ml-2 font-bold text-xl text-[#2A5C8A]">心理健康管理系统</span>
        </div>

        <!-- Desktop Navigation -->
        <nav class="hidden md:flex items-center space-x-8">
          <a href="#features" class="text-gray-700 hover:text-[#2A5C8A] transition-colors">
            功能介绍
          </a>
          <a href="#data" class="text-gray-700 hover:text-[#2A5C8A] transition-colors">
            数据展示
          </a>
          <a href="#resources" class="text-gray-700 hover:text-[#2A5C8A] transition-colors">
            资源中心
          </a>
          <a href="#about" class="text-gray-700 hover:text-[#2A5C8A] transition-colors">
            关于我们
          </a>
        </nav>

        <div class="flex items-center space-x-3">
          <router-link to="/employee-login">
            <button class="px-4 py-2 rounded-md bg-amber-200 border border-orange-400 text-orange-500 hover:bg-orange-50 transition-colors">
              员工入口
            </button>
          </router-link>
          <router-link to="/manager-login">
            <button class="px-4 py-2 rounded-md bg-[#7cb6ef] border border-[#0c457e] text-[#092948] hover:bg-[#b1c7dd] transition-colors">
            管理端入口
          </button>
          </router-link>
        </div>

        <!-- Mobile menu button -->
        <button
            class="md:hidden p-2 rounded-md border border-gray-200"
            @click="mobileMenuOpen = !mobileMenuOpen"
            aria-label="打开菜单"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="3" y1="12" x2="21" y2="12"></line>
            <line x1="3" y1="6" x2="21" y2="6"></line>
            <line x1="3" y1="18" x2="21" y2="18"></line>
          </svg>
        </button>
      </div>

      <!-- Mobile Navigation Menu -->
      <div
          v-if="mobileMenuOpen"
          class="md:hidden absolute top-full left-0 right-0 bg-white border-b shadow-lg"
      >
        <nav class="container mx-auto py-4 px-4 flex flex-col space-y-4">
          <a
              href="#features"
              class="text-gray-700 hover:text-[#2A5C8A] transition-colors py-2"
              @click="mobileMenuOpen = false"
          >
            功能介绍
          </a>
          <a
              href="#data"
              class="text-gray-700 hover:text-[#2A5C8A] transition-colors py-2"
              @click="mobileMenuOpen = false"
          >
            数据展示
          </a>
          <a
              href="#resources"
              class="text-gray-700 hover:text-[#2A5C8A] transition-colors py-2"
              @click="mobileMenuOpen = false"
          >
            资源中心
          </a>
          <a
              href="#about"
              class="text-gray-700 hover:text-[#2A5C8A] transition-colors py-2"
              @click="mobileMenuOpen = false"
          >
            关于我们
          </a>
          <div class="flex flex-col space-y-2 pt-2 border-t">
            <button class="w-full px-4 py-2 rounded-md border border-orange-400 text-orange-500 hover:bg-orange-50 transition-colors">
              员工入口
            </button>
            <button class="w-full px-4 py-2 rounded-md bg-[#2A5C8A] text-white hover:bg-[#1e4266] transition-colors">
              管理端入口
            </button>
          </div>
        </nav>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="pt-32 pb-16 container mx-auto px-4">
      <div class="grid md:grid-cols-2 gap-12 items-center">
        <div class="space-y-6">
          <h1 class="text-4xl md:text-5xl font-bold text-[#2A5C8A] leading-tight">
            构建健康职场生态
          </h1>
          <div class="h-8">
            <p class="text-xl text-gray-600">
              专业的
              <span class="text-[#2A5C8A] font-medium">
                {{ typedText }}
                <span class="animate-blink">|</span>
              </span>
              解决方案
            </p>
          </div>
          <p class="text-gray-600">
            为企业提供全方位的员工心理健康管理服务，打造积极健康的职场环境，
            提升组织效能与员工幸福感。
          </p>
          <div class="flex flex-wrap gap-4">
            <button class="px-4 py-2 rounded-md bg-[#2A5C8A] text-white hover:bg-[#1e4266] transition-colors">
              立即体验
            </button>
            <button class="px-4 py-2 rounded-md border border-gray-300 flex items-center gap-2 hover:bg-gray-50 transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="5 3 19 12 5 21 5 3"></polygon>
              </svg>
              观看演示视频
            </button>
          </div>
        </div>

        <div class="relative">
          <div class="bg-white rounded-xl shadow-lg p-6 border border-blue-100">
            <div class="h-48 w-full bg-gradient-to-b from-blue-50 to-white rounded-lg p-4 relative overflow-hidden group">
  <!-- 波纹背景（修正版） -->
  <div class="absolute inset-0 opacity-10 overflow-hidden">
    <div class="absolute top-0 left-0 w-full h-full animate-ripple">
      <div class="absolute top-0 left-0 w-full h-1/3 bg-[#2A5C8A] opacity-20"></div>
      <div class="absolute top-1/3 left-0 w-full h-1/3 bg-[#2A5C8A] opacity-10"></div>
      <div class="absolute top-2/3 left-0 w-full h-1/3 bg-[#2A5C8A] opacity-5"></div>
    </div>
  </div>

  <!-- 轮播主体（带实际动效） -->
  <div class="relative h-full flex flex-col">
    <!-- 标题栏 -->
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-lg font-semibold text-[#2A5C8A]">
        📌 职场能量补给站
      </h3>
      <div class="flex space-x-2">
        <button class="p-1 rounded-full transition-all hover:bg-blue-100 hover:scale-110">
          ←
        </button>
        <button class="p-1 rounded-full transition-all hover:bg-blue-100 hover:scale-110">
          →
        </button>
      </div>
    </div>

    <!-- 轮播内容（实际滑动效果） -->
    <div class="flex-1 relative overflow-hidden">
      <div class="absolute inset-0 flex transition-transform duration-500 ease-in-out" style="width: 300%;">
        <!-- 贴士1 -->
        <div class="w-1/3 p-2 flex items-center space-x-4" v-if="currentTipIndex === 0">
          <div class="flex-shrink-0 w-12 h-12 bg-blue-500 rounded-lg flex items-center justify-center text-white">
            ⏳
          </div>
          <div>
            <h4 class="font-medium">20-20-20法则</h4>
            <p class="text-sm text-gray-600 mt-1">
              每工作20分钟，远眺20英尺外物体<br>
              持续20秒缓解视觉疲劳
            </p>
          </div>
        </div>

        <!-- 贴士2 -->
        <div class="w-1/3 p-2 flex items-center space-x-4" v-if="currentTipIndex === 1">
          <div class="flex-shrink-0 w-12 h-12 bg-orange-500 rounded-lg flex items-center justify-center text-white">
            🧘
          </div>
          <div>
            <h4 class="font-medium">座椅瑜伽</h4>
            <p class="text-sm text-gray-600 mt-1">
              坐姿脊柱扭转保持15秒<br>
              有效缓解肩颈僵硬
            </p>
          </div>
        </div>

        <!-- 贴士3 -->
        <div class="w-1/3 p-2 flex items-center space-x-4" v-if="currentTipIndex === 2">
          <div class="flex-shrink-0 w-12 h-12 bg-green-500 rounded-lg flex items-center justify-center text-white">
            💡
          </div>
          <div>
            <h4 class="font-medium">积极心理锚点</h4>
            <p class="text-sm text-gray-600 mt-1">
              在显示器旁贴笑脸便签<br>
              视觉暗示提升工作愉悦感
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 进度指示（动态激活效果） -->
    <div class="flex justify-center space-x-1 mt-2">
      <div class="w-8 h-1 bg-blue-200 rounded-full cursor-pointer hover:bg-blue-300 active-indicator"></div>
      <div class="w-8 h-1 bg-blue-200 rounded-full cursor-pointer hover:bg-blue-300 active-indicator"></div>
      <div class="w-8 h-1 bg-blue-200 rounded-full cursor-pointer hover:bg-blue-300 active-indicator"></div>
    </div>
  </div>
</div>
          </div>

          <!-- AI Assistant -->
          <div
              class="absolute -bottom-10 -right-10 bg-white rounded-full shadow-lg p-3 border-2 border-[#2A5C8A] animate-bounce-slow cursor-pointer"
              @click="showAIAssistant = true"
              aria-label="AI助手"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path>
              <line x1="12" y1="17" x2="12.01" y2="17"></line>
            </svg>
          </div>

          <!-- AI Assistant Dialog -->
          <div
              v-if="showAIAssistant"
              class="fixed inset-0 bg-black/30 z-50 flex items-end sm:items-center justify-center p-4"
              @click.self="showAIAssistant = false"
          >
            <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-auto overflow-hidden">
              <div class="p-4 bg-[#2A5C8A] text-white flex justify-between items-center">
                <div class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path>
                    <line x1="12" y1="17" x2="12.01" y2="17"></line>
                  </svg>
                  <span class="font-medium">AI心理助手</span>
                </div>
                <button
                    @click="showAIAssistant = false"
                    class="text-white/80 hover:text-white"
                    aria-label="关闭对话框"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="18" y1="6" x2="6" y2="18"></line>
                    <line x1="6" y1="6" x2="18" y2="18"></line>
                  </svg>
                </button>
              </div>
              <div class="p-4 max-h-96 overflow-y-auto">
                <div class="flex gap-3 mb-4">
                  <div class="w-8 h-8 rounded-full bg-[#2A5C8A] flex items-center justify-center text-white flex-shrink-0">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path>
                      <line x1="12" y1="17" x2="12.01" y2="17"></line>
                    </svg>
                  </div>
                  <div class="bg-gray-100 rounded-lg p-3 text-gray-700">
                    <p>您好！我是您的AI心理健康助手。有什么我可以帮助您的吗？</p>
                    <p class="text-sm text-gray-500 mt-1">您可以询问我关于情绪管理、压力缓解或心理健康的任何问题。</p>
                  </div>
                </div>

                <div v-for="(message, index) in aiMessages" :key="index" class="flex gap-3 mb-4">
                  <template v-if="message.type === 'user'">
                    <div class="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center text-gray-600 ml-auto order-2 flex-shrink-0">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                      </svg>
                    </div>
                    <div class="bg-blue-500 text-white rounded-lg p-3 order-1 ml-auto">
                      <p>{{ message.text }}</p>
                    </div>
                  </template>
                  <template v-else>
                    <div class="w-8 h-8 rounded-full bg-[#2A5C8A] flex items-center justify-center text-white flex-shrink-0">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path>
                        <line x1="12" y1="17" x2="12.01" y2="17"></line>
                      </svg>
                    </div>
                    <div class="bg-gray-100 rounded-lg p-3 text-gray-700">
                      <p>{{ message.text }}</p>
                    </div>
                  </template>
                </div>
              </div>
              <div class="p-4 border-t">
                <form @submit.prevent="sendAIMessage" class="flex gap-2">
                  <input
                      v-model="aiInput"
                      type="text"
                      placeholder="输入您的问题..."
                      class="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A]"
                  />
                  <button
                      type="submit"
                      class="bg-[#2A5C8A] text-white px-4 py-2 rounded-md hover:bg-[#1e4266] transition-colors"
                      :disabled="!aiInput.trim()"
                  >
                    发送
                  </button>
                </form>
                <div class="mt-2 text-xs text-gray-500 flex items-center gap-1">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                  </svg>
                  <span>您的对话内容受到隐私保护</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Wave Divider -->
    <div class="w-full h-24 relative overflow-hidden">
      <svg viewBox="0 0 1200 120" preserveAspectRatio="none" class="absolute bottom-0 w-full h-full">
        <path
            d="M0,0 C150,90 350,0 500,80 C650,160 750,40 900,100 C1050,160 1200,80 1200,80 V120 H0 V0 Z"
            class="fill-blue-50"
        ></path>
      </svg>
    </div>

    <!-- Features Section -->
    <section id="features" class="py-16 bg-blue-50">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-[#2A5C8A] mb-4">三维功能展示</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">
            我们提供全方位的心理健康管理功能，满足员工和管理者的不同需求
          </p>
        </div>

        <div class="grid md:grid-cols-2 gap-8">
          <!-- Employee Services Card -->
          <div class="group perspective-card">
            <div class="relative h-80 transition-all duration-500 transform-style-3d hover:rotate-y-180">
              <!-- Front -->
              <div class="absolute inset-0 backface-hidden bg-white rounded-xl shadow-lg p-6 border border-blue-100">
                <h3 class="text-xl font-bold text-[#2A5C8A] mb-4">员工服务</h3>
                <div class="flex justify-center items-center h-48">
                  <div class="relative w-48 h-48">
                    <div class="absolute inset-0 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-24 w-24 text-[#2A5C8A] opacity-10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                        <circle cx="9" cy="7" r="4"></circle>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                      </svg>
                    </div>
                    <div class="absolute top-0 left-0 bg-blue-100 rounded-full p-3">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                      </svg>
                    </div>
                    <div class="absolute top-1/4 right-0 bg-green-100 rounded-full p-3">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
                      </svg>
                    </div>
                    <div class="absolute bottom-0 left-1/4 bg-orange-100 rounded-full p-3">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-orange-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                      </svg>
                    </div>
                  </div>
                </div>
                <p class="text-gray-600 text-center">
                  为员工提供情绪管理、自助资源和专业支持
                </p>
              </div>

              <!-- Back -->
              <div class="absolute inset-0 backface-hidden rotate-y-180 bg-[#2A5C8A] rounded-xl shadow-lg p-6 text-white">
                <h3 class="text-xl font-bold mb-4">核心功能</h3>
                <ul class="space-y-4">
                  <li class="flex items-center gap-3">
                    <div class="bg-white/20 rounded-full p-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                      </svg>
                    </div>
                    <div>
                      <h4 class="font-medium">心理测评</h4>
                      <p class="text-sm text-blue-100">通过问卷了解你的心理状态与变化趋势</p>
                    </div>
                  </li>
                  <li class="flex items-center gap-3">
                    <div class="bg-white/20 rounded-full p-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <line x1="16" y1="13" x2="8" y2="13"></line>
                        <line x1="16" y1="17" x2="8" y2="17"></line>
                        <polyline points="10 9 9 9 8 9"></polyline>
                      </svg>
                    </div>
                    <div>
                      <h4 class="font-medium">评估报告</h4>
                      <p class="text-sm text-blue-100">获取专业的心理健康评估</p>
                    </div>
                  </li>
                  <li class="flex items-center gap-3">
                    <div class="bg-white/20 rounded-full p-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                      </svg>
                    </div>
                    <div>
                      <h4 class="font-medium">自助资源</h4>
                      <p class="text-sm text-blue-100">丰富的心理健康学习资料</p>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <!-- Management Card -->
          <div class="group perspective-card">
            <div class="relative h-80 transition-all duration-500 transform-style-3d hover:rotate-y-180">
              <!-- Front -->
              <div class="absolute inset-0 backface-hidden bg-white rounded-xl shadow-lg p-6 border border-blue-100">
                <h3 class="text-xl font-bold text-[#2A5C8A] mb-4">管理端</h3>
                <div class="bg-gradient-to-br from-blue-50 to-white rounded-lg p-4 h-48">
                  <div class="grid grid-cols-2 gap-3 h-full">
                    <div class="bg-white rounded-lg shadow-sm p-3 flex flex-col">
                      <div class="text-xs text-gray-500 mb-1">整体健康指数</div>
                      <div class="text-2xl font-bold text-[#2A5C8A]">87.5</div>
                      <div class="mt-auto">
                        <div class="text-xs text-gray-500 mb-1">较上月</div>
                        <div class="text-sm text-green-500 flex items-center">
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 rotate-90" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="9 18 15 12 9 6"></polyline>
                          </svg>
                          +2.3%
                        </div>
                      </div>
                    </div>
                    <div class="bg-white rounded-lg shadow-sm p-3 flex flex-col">
                      <div class="text-xs text-gray-500 mb-1">预警人数</div>
                      <div class="text-2xl font-bold text-orange-500">12</div>
                      <div class="mt-auto">
                        <div class="text-xs text-gray-500 mb-1">较上月</div>
                        <div class="text-sm text-green-500 flex items-center">
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 -rotate-90" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="9 18 15 12 9 6"></polyline>
                          </svg>
                          -5
                        </div>
                      </div>
                    </div>
                    <div class="col-span-2 bg-white rounded-lg shadow-sm p-3">
                      <div class="text-xs text-gray-500 mb-1">部门健康分布</div>
                      <div class="flex justify-between items-end h-12">
                        <div class="w-1/5 bg-blue-200 rounded-t h-6"></div>
                        <div class="w-1/5 bg-blue-300 rounded-t h-8"></div>
                        <div class="w-1/5 bg-blue-400 rounded-t h-10"></div>
                        <div class="w-1/5 bg-blue-500 rounded-t h-4"></div>
                        <div class="w-1/5 bg-blue-600 rounded-t h-7"></div>
                      </div>
                    </div>
                  </div>
                </div>
                <p class="text-gray-600 text-center mt-4">
                  为管理者提供数据驾驶舱和团队健康管理工具
                </p>
              </div>

              <!-- Back -->
              <div class="absolute inset-0 backface-hidden rotate-y-180 bg-[#2A5C8A] rounded-xl shadow-lg p-6 text-white">
                <h3 class="text-xl font-bold mb-4">管理功能</h3>
                <ul class="space-y-4">
                  <li class="flex items-center gap-3">
                    <div class="bg-white/20 rounded-full p-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="3" y1="9" x2="21" y2="9"></line>
                        <line x1="9" y1="21" x2="9" y2="9"></line>
                      </svg>
                    </div>
                    <div>
                      <h4 class="font-medium">数据分析</h4>
                      <p class="text-sm text-blue-100">全面的团队心理健康数据</p>
                    </div>
                  </li>
                  <li class="flex items-center gap-3">
                    <div class="bg-white/20 rounded-full p-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
                      </svg>
                    </div>
                    <div>
                      <h4 class="font-medium">高危预警</h4>
                      <p class="text-sm text-blue-100">及时发现并干预高风险情况</p>
                    </div>
                  </li>
                  <li class="flex items-center gap-3">
                    <div class="bg-white/20 rounded-full p-2">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                        <circle cx="12" cy="10" r="3"></circle>
                      </svg>
                    </div>
                    <div>
                      <h4 class="font-medium">部门分析</h4>
                      <p class="text-sm text-blue-100">不同部门的心理健康对比</p>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Data Visualization Section -->
    <section id="data" class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-[#2A5C8A] mb-4">数据可视化展示墙</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">
            直观展示企业心理健康数据，帮助管理者做出明智决策
          </p>
        </div>

        <div class="grid md:grid-cols-3 gap-6">
          <!-- Health Distribution -->
          <div class="bg-white rounded-lg shadow-sm overflow-hidden">
            <div class="p-6">
              <h3 class="text-lg font-medium text-[#2A5C8A] mb-4">企业整体心理健康分布</h3>
              <div class="relative h-48 flex items-center justify-center">
                <div class="w-36 h-36 rounded-full border-8 border-blue-100 relative">
                  <div
                      class="absolute top-0 left-0 w-full h-full rounded-full border-8 border-transparent border-t-[#2A5C8A]"
                      style="transform: rotate(45deg)"
                  ></div>
                  <div
                      class="absolute top-0 left-0 w-full h-full rounded-full border-8 border-transparent border-r-orange-400"
                      style="transform: rotate(45deg)"
                  ></div>
                  <div class="absolute inset-0 flex items-center justify-center flex-col">
                    <span class="text-2xl font-bold text-[#2A5C8A]">87%</span>
                    <span class="text-xs text-gray-500">健康状态</span>
                  </div>
                </div>
                <div class="absolute bottom-0 w-full flex justify-between text-xs text-gray-500 px-4">
                  <div class="flex items-center">
                    <div class="w-3 h-3 rounded-full bg-[#2A5C8A] mr-1"></div>
                    <span>健康 (87%)</span>
                  </div>
                  <div class="flex items-center">
                    <div class="w-3 h-3 rounded-full bg-orange-400 mr-1"></div>
                    <span>亚健康 (11%)</span>
                  </div>
                  <div class="flex items-center">
                    <div class="w-3 h-3 rounded-full bg-red-400 mr-1"></div>
                    <span>预警 (2%)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Department Heatmap -->
          <div class="bg-white rounded-lg shadow-sm overflow-hidden">
            <div class="p-6">
              <h3 class="text-lg font-medium text-[#2A5C8A] mb-4">部门健康指数热力图</h3>
              <div class="h-48 grid grid-cols-3 grid-rows-3 gap-2">
                <div
                    v-for="(dept, index) in departments"
                    :key="index"
                    :class="dept.color"
                    class="rounded-lg p-2 flex flex-col justify-between text-white"
                >
                  <div class="text-xs">{{ dept.name }}</div>
                  <div class="text-lg font-bold">{{ dept.value }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Recent Assessments -->
          <div class="bg-white rounded-lg shadow-sm overflow-hidden">
            <div class="p-6">
              <h3 class="text-lg font-medium text-[#2A5C8A] mb-4">最新评估完成情况</h3>
              <div class="h-48 overflow-y-auto space-y-3">
                <div
                    v-for="item in 6"
                    :key="item"
                    class="flex items-center gap-3 p-2 bg-gray-50 rounded-lg"
                >
                  <div class="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden">
                    <img
                        :src="`https://randomuser.me/api/portraits/thumb/men/${item + 10}.jpg`"
                        alt="User avatar"
                        class="object-cover"
                    />
                  </div>
                  <div class="flex-1">
                    <div class="flex justify-between">
                      <span class="text-sm font-medium">用户{{ item + 100 }}</span>
                      <span class="text-xs text-gray-500">刚刚</span>
                    </div>
                    <div class="text-xs text-gray-500">完成了每周情绪评估</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-8 text-center">
          <div class="inline-flex items-center gap-2 text-sm text-gray-500">
          </div>
        </div>
      </div>
    </section>

    <!-- Resource Center -->
    <section id="resources" class="py-16 bg-gradient-to-b from-white to-blue-50">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-[#2A5C8A] mb-4">情景化资源中心</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">
            根据不同角色和需求，提供个性化的心理健康资源
          </p>
        </div>

        <div class="w-full">
          <div class="grid w-full max-w-md mx-auto grid-cols-2 mb-8">
            <button
                @click="activeTab = 'employee'"
                class="py-2 px-4 text-sm font-medium transition-colors rounded-l-md"
                :class="activeTab === 'employee' ? 'bg-[#2A5C8A] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            >
              员工资源
            </button>
            <button
                @click="activeTab = 'manager'"
                class="py-2 px-4 text-sm font-medium transition-colors rounded-r-md"
                :class="activeTab === 'manager' ? 'bg-[#2A5C8A] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
            >
              管理者资源
            </button>
          </div>

          <div v-if="activeTab === 'employee'" class="space-y-4">
            <div class="grid md:grid-cols-3 gap-6">
              <div
                  v-for="(resource, index) in employeeResources"
                  :key="index"
                  class="bg-white rounded-lg shadow-sm overflow-hidden group"
              >
                <div class="p-0">
                  <div class="h-40 bg-blue-100 relative overflow-hidden">
                    <div v-if="resource.type === 'video'" class="absolute inset-0 flex items-center justify-center">
                      <div class="w-12 h-12 rounded-full bg-white/80 flex items-center justify-center">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <polygon points="5 3 19 12 5 21 5 3"></polygon>
                        </svg>
                      </div>
                    </div>
                    <div v-if="resource.type === 'article'" class="absolute inset-0 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-[#2A5C8A]/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <line x1="16" y1="13" x2="8" y2="13"></line>
                        <line x1="16" y1="17" x2="8" y2="17"></line>
                        <polyline points="10 9 9 9 8 9"></polyline>
                      </svg>
                    </div>
                    <div v-if="resource.type === 'audio'" class="absolute inset-0 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-[#2A5C8A]/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
                      </svg>
                    </div>
                  </div>
                  <div class="p-4">
                    <div class="flex justify-between items-start mb-2">
                      <h3 class="font-medium text-[#2A5C8A] group-hover:underline">
                        {{ resource.title }}
                      </h3>
                      <span class="text-xs bg-blue-100 text-[#2A5C8A] px-2 py-0.5 rounded">
                        {{ resource.type === 'article' ? '文章' :
                          resource.type === 'video' ? '视频' : '音频' }}
                      </span>
                    </div>
                    <p class="text-sm text-gray-600 mb-3">{{ resource.desc }}</p>
                    <div v-if="resource.progress > 0" class="space-y-1">
                      <div class="flex justify-between text-xs">
                        <span>阅读进度</span>
                        <span>{{ resource.progress }}%</span>
                      </div>
                      <div class="w-full bg-gray-200 rounded-full h-1">
                        <div
                            class="bg-[#2A5C8A] h-1 rounded-full"
                            :style="{ width: resource.progress + '%' }"
                        ></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="text-center mt-8">
              <button class="px-4 py-2 rounded-md border border-gray-300 hover:bg-gray-50 transition-colors">
                查看更多资源
              </button>
            </div>
          </div>

          <div v-if="activeTab === 'manager'" class="space-y-4">
            <div class="grid md:grid-cols-3 gap-6">
              <div
                  v-for="(resource, index) in managerResources"
                  :key="index"
                  class="bg-white rounded-lg shadow-sm overflow-hidden group"
              >
                <div class="p-0">
                  <div class="h-40 bg-blue-100 relative overflow-hidden">
                    <div v-if="resource.type === 'video'" class="absolute inset-0 flex items-center justify-center">
                      <div class="w-12 h-12 rounded-full bg-white/80 flex items-center justify-center">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <polygon points="5 3 19 12 5 21 5 3"></polygon>
                        </svg>
                      </div>
                    </div>
                    <div v-if="resource.type === 'article'" class="absolute inset-0 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-[#2A5C8A]/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <line x1="16" y1="13" x2="8" y2="13"></line>
                        <line x1="16" y1="17" x2="8" y2="17"></line>
                        <polyline points="10 9 9 9 8 9"></polyline>
                      </svg>
                    </div>
                    <div v-if="resource.type === 'report'" class="absolute inset-0 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-[#2A5C8A]/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="3" y1="9" x2="21" y2="9"></line>
                        <line x1="9" y1="21" x2="9" y2="9"></line>
                      </svg>
                    </div>
                  </div>
                  <div class="p-4">
                    <div class="flex justify-between items-start mb-2">
                      <h3 class="font-medium text-[#2A5C8A] group-hover:underline">
                        {{ resource.title }}
                      </h3>
                      <span class="text-xs bg-blue-100 text-[#2A5C8A] px-2 py-0.5 rounded">
                        {{ resource.type === 'article' ? '指南' :
                          resource.type === 'video' ? '视频' : '报告' }}
                      </span>
                    </div>
                    <p class="text-sm text-gray-600 mb-3">{{ resource.desc }}</p>
                    <div v-if="resource.progress > 0" class="space-y-1">
                      <div class="flex justify-between text-xs">
                        <span>阅读进度</span>
                        <span>{{ resource.progress }}%</span>
                      </div>
                      <div class="w-full bg-gray-200 rounded-full h-1">
                        <div
                            class="bg-[#2A5C8A] h-1 rounded-full"
                            :style="{ width: resource.progress + '%' }"
                        ></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="text-center mt-8">
              <button class="px-4 py-2 rounded-md border border-gray-300 hover:bg-gray-50 transition-colors">
                查看更多资源
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Partners Section -->
    <section id="about" class="py-16 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-[#2A5C8A] mb-4">合作伙伴</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">
            我们与行业领先的机构合作，提供专业的心理健康服务
          </p>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-8">
          <div
              v-for="partner in 4"
              :key="partner"
              class="flex items-center justify-center grayscale hover:grayscale-0 transition-all duration-300"
          >
            <img
                :src="`https://via.placeholder.com/120x60?text=Partner+${partner}`"
                :alt="`Partner ${partner}`"
                class="object-contain"
            />
          </div>
        </div>

        <div class="mt-12 text-center">
          <div class="inline-flex items-center gap-2 text-sm text-gray-500 bg-gray-100 px-4 py-2 rounded-full">
            <span>已服务</span>
            <span class="text-[#2A5C8A] font-bold">0</span>
            <span>家企业，完成</span>
            <span class="text-[#2A5C8A] font-bold">0</span>
            <span>次评估</span>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="py-16 bg-[#2A5C8A] text-white">
      <div class="container mx-auto px-4">
        <div class="grid md:grid-cols-2 gap-8 items-center">
          <div>
            <h2 class="text-3xl font-bold mb-4">开始构建健康职场生态</h2>
            <p class="text-blue-100 mb-6">
              立即体验我们的心理健康管理系统，为您的企业和员工带来积极改变
            </p>
            <div class="flex flex-wrap gap-4">
              <button class="px-4 py-2 rounded-md border border-white text-white hover:bg-[#3a6c9a] transition-colors">
                免费试用
              </button>
              <button class="px-4 py-2 rounded-md border border-white text-white hover:bg-[#3a6c9a] transition-colors">
                联系我们
              </button>
            </div>
          </div>
          <div class="bg-[#3a6c9a] rounded-xl p-6">
            <h3 class="text-xl font-medium mb-4">预约演示</h3>
            <div class="space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="text-sm text-blue-100 block mb-1">姓名</label>
                  <input
                      v-model="formData.name"
                      type="text"
                      class="w-full px-3 py-2 rounded bg-white/10 border border-blue-400 text-white placeholder-blue-200"
                      placeholder="您的姓名"
                  />
                </div>
                <div>
                  <label class="text-sm text-blue-100 block mb-1">公司</label>
                  <input
                      v-model="formData.company"
                      type="text"
                      class="w-full px-3 py-2 rounded bg-white/10 border border-blue-400 text-white placeholder-blue-200"
                      placeholder="公司名称"
                  />
                </div>
              </div>
              <div>
                <label class="text-sm text-blue-100 block mb-1">邮箱</label>
                <input
                    v-model="formData.email"
                    type="email"
                    class="w-full px-3 py-2 rounded bg-white/10 border border-blue-400 text-white placeholder-blue-200"
                    placeholder="您的邮箱地址"
                />
              </div>
              <div>
                <label class="text-sm text-blue-100 block mb-1">需求描述</label>
                <textarea
                    v-model="formData.description"
                    class="w-full px-3 py-2 rounded bg-white/10 border border-blue-400 text-white placeholder-blue-200 h-24 resize-none"
                    placeholder="请简要描述您的需求"
                ></textarea>
              </div>
              <button
                  @click="submitForm"
                  class="w-full px-4 py-2 rounded-md border border-white text-white hover:bg-[#3a6c9a] transition-colors"
              >
                提交预约
              </button>
              <div v-if="formSubmitted" class="text-center text-sm text-blue-200">
                感谢您的预约！我们将尽快与您联系。
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-gray-900 text-white py-12">
      <div class="container mx-auto px-4">
        <div class="grid md:grid-cols-4 gap-8">
          <div>
            <div class="flex items-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="m8 3 4 8 5-5 5 15H2L8 3z"/>
              </svg>
              <span class="ml-2 font-bold">心理健康管理系统</span>
            </div>
            <p class="text-gray-400 text-sm">
              为企业提供全方位的员工心理健康管理服务，打造积极健康的职场环境
            </p>
          </div>
          <div>
            <h3 class="font-medium mb-4">产品</h3>
            <ul class="space-y-2 text-sm text-gray-400">
              <li><a href="#" class="hover:text-white">员工服务</a></li>
              <li><a href="#" class="hover:text-white">管理端</a></li>
              <li><a href="#" class="hover:text-white">数据分析</a></li>
              <li><a href="#" class="hover:text-white">资源中心</a></li>
            </ul>
          </div>
          <div>
            <h3 class="font-medium mb-4">关于我们</h3>
            <ul class="space-y-2 text-sm text-gray-400">
              <li><a href="#" class="hover:text-white">公司介绍</a></li>
              <li><a href="#" class="hover:text-white">专家团队</a></li>
              <li><a href="#" class="hover:text-white">合作伙伴</a></li>
              <li><a href="#" class="hover:text-white">联系我们</a></li>
            </ul>
          </div>
          <div>
            <h3 class="font-medium mb-4">联系方式</h3>
            <ul class="space-y-2 text-sm text-gray-400">
              <li>邮箱：contact@example.com</li>
              <li>电话：400-123-4567</li>
              <li>地址：北京市朝阳区科技园区</li>
            </ul>
          </div>
        </div>

        <div class="mt-8 pt-8 border-t border-gray-800 flex flex-col md:flex-row justify-between items-center">
          <div class="text-sm text-gray-500">
            © {{ new Date().getFullYear() }} 心理健康管理系统. 保留所有权利.
          </div>
          <div class="flex items-center mt-4 md:mt-0">
            <div class="flex items-center text-sm text-gray-500 mr-6">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
              </svg>
              <span>数据安全保障</span>
            </div>
            <div class="flex space-x-4">
              <a href="#" class="text-gray-500 hover:text-white">
                隐私政策
              </a>
              <a href="#" class="text-gray-500 hover:text-white">
                使用条款
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>

    <!-- Emotion Support Button -->
    <div class="fixed bottom-6 right-6 z-40">
      <button
          class="rounded-full h-14 w-14 bg-orange-500 hover:bg-orange-600 shadow-lg p-0 animate-pulse"
          @click="showEmotionSupport = true"
          aria-label="情绪急救"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white mx-auto" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
        </svg>
        <span class="sr-only">情绪急救</span>
      </button>
    </div>

    <!-- Emotion Support Dialog -->
    <div
        v-if="showEmotionSupport"
        class="fixed inset-0 bg-black/30 z-50 flex items-end sm:items-center justify-center p-4"
        @click.self="showEmotionSupport = false"
    >
      <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-auto overflow-hidden">
        <div class="p-4 bg-orange-500 text-white flex justify-between items-center">
          <div class="flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
            </svg>
            <span class="font-medium">情绪急救</span>
          </div>
          <button
              @click="showEmotionSupport = false"
              class="text-white/80 hover:text-white"
              aria-label="关闭对话框"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
        <div class="p-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">您当前的情绪状态是？</h3>
          <div class="grid grid-cols-3 gap-4 mb-6">
            <button
                v-for="(emotion, index) in emotions"
                :key="index"
                class="flex flex-col items-center p-3 rounded-lg border transition-colors"
                :class="selectedEmotion === emotion.name ? 'border-orange-500 bg-orange-50' : 'border-gray-200 hover:bg-gray-50'"
                @click="selectedEmotion = emotion.name"
            >
              <div class="text-2xl mb-1">{{ emotion.emoji }}</div>
              <div class="text-sm">{{ emotion.name }}</div>
            </button>
          </div>
          <div class="space-y-4">
            <p class="text-gray-600">
              {{ getEmotionMessage() }}
            </p>
            <div class="flex justify-between">
              <button
                  class="px-4 py-2 rounded-md border border-gray-300 text-gray-700 hover:bg-gray-50 transition-colors"
                  @click="showEmotionSupport = false"
              >
                稍后再说
              </button>
              <button
                  class="px-4 py-2 rounded-md bg-orange-500 text-white hover:bg-orange-600 transition-colors"
                  @click="connectToSupport"
              >
                立即联系专家
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Tour Guide -->
    <div v-if="showGuide" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center">
      <div class="bg-white rounded-lg shadow-lg w-full max-w-md">
        <div class="p-6">
          <div class="flex justify-between items-start mb-4">
            <h3 class="text-xl font-bold text-[#2A5C8A]">系统导览</h3>
            <button
                @click="showGuide = false"
                class="text-sm px-2 py-1 rounded hover:bg-gray-100"
                aria-label="跳过导览"
            >
              跳过
            </button>
          </div>

          <div v-if="guideStep === 0" class="space-y-4">
            <div class="flex items-center gap-3">
              <div class="bg-blue-100 rounded-full p-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="m8 3 4 8 5-5 5 15H2L8 3z"/>
                </svg>
              </div>
              <div>
                <h4 class="font-medium">欢迎使用心理健康管理系统</h4>
                <p class="text-sm text-gray-600">我们将带您了解系统的核心功能</p>
              </div>
            </div>
            <img
                src="https://via.placeholder.com/400x200?text=Welcome"
                alt="Welcome"
                class="rounded-lg"
            />
          </div>

          <div v-if="guideStep === 1" class="space-y-4">
            <div class="flex items-center gap-3">
              <div class="bg-blue-100 rounded-full p-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                  <circle cx="9" cy="7" r="4"></circle>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                  <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                </svg>
              </div>
              <div>
                <h4 class="font-medium">员工服务</h4>
                <p class="text-sm text-gray-600">情绪记录、自助资源和专业支持</p>
              </div>
            </div>
            <img
                src="https://via.placeholder.com/400x200?text=Employee+Services"
                alt="Employee Services"
                class="rounded-lg"
            />
          </div>

          <div v-if="guideStep === 2" class="space-y-4">
            <div class="flex items-center gap-3">
              <div class="bg-blue-100 rounded-full p-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="3" y1="9" x2="21" y2="9"></line>
                  <line x1="9" y1="21" x2="9" y2="9"></line>
                </svg>
              </div>
              <div>
                <h4 class="font-medium">管理端功能</h4>
                <p class="text-sm text-gray-600">数据分析、团队管理和预警系统</p>
              </div>
            </div>
            <img
                src="https://via.placeholder.com/400x200?text=Management+Features"
                alt="Management Features"
                class="rounded-lg"
            />
          </div>

          <div v-if="guideStep === 3" class="space-y-4">
            <div class="flex items-center gap-3">
              <div class="bg-blue-100 rounded-full p-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                </svg>
              </div>
              <div>
                <h4 class="font-medium">情绪急救</h4>
                <p class="text-sm text-gray-600">随时获取情绪支持和专业帮助</p>
              </div>
            </div>
            <img
                src="https://via.placeholder.com/400x200?text=Emotional+Support"
                alt="Emotional Support"
                class="rounded-lg"
            />
          </div>

          <div class="flex justify-between mt-6">
            <button
                class="px-4 py-2 rounded-md border border-gray-300 hover:bg-gray-50 transition-colors"
                @click="guideStep = Math.max(0, guideStep - 1)"
                :disabled="guideStep === 0"
            >
              上一步
            </button>
            <button
                class="px-4 py-2 rounded-md bg-[#2A5C8A] text-white hover:bg-[#1e4266] transition-colors"
                @click="nextGuideStep"
            >
              {{ guideStep < 3 ? "下一步" : "开始使用" }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed, watch } from 'vue';

// 状态变量
const scrolled = ref(false);
const currentTime = ref(new Date().toLocaleTimeString());
const isWorkHours = ref(true);
const showGuide = ref(true);
const guideStep = ref(0);
const typedText = ref("");
const currentTextIndex = ref(0);
const subtitles = ["情绪识别", "预防干预", "数据分析"];
const activeTab = ref('employee');
const mobileMenuOpen = ref(false);
const showAIAssistant = ref(false);
const showEmotionSupport = ref(false);
const selectedEmotion = ref('');
const formSubmitted = ref(false);
const aiInput = ref('');
const aiMessages = ref([]);

const currentTipIndex = ref(0); // 当前显示的贴士索引
const tipsCount = 3; // 贴士总数

// 动态点位置
const dotPosition = reactive({
  x: 0,
  y: 50
});

// 表单数据
const formData = reactive({
  name: '',
  company: '',
  email: '',
  description: ''
});

// 部门数据
const departments = [
  { name: "技术部", value: 92, color: "bg-blue-500" },
  { name: "市场部", value: 85, color: "bg-blue-400" },
  { name: "销售部", value: 78, color: "bg-blue-300" },
  { name: "人力资源", value: 90, color: "bg-blue-500" },
  { name: "财务部", value: 88, color: "bg-blue-400" },
  { name: "客服部", value: 75, color: "bg-blue-300" },
  { name: "产品部", value: 89, color: "bg-blue-400" },
  { name: "行政部", value: 86, color: "bg-blue-400" },
  { name: "法务部", value: 91, color: "bg-blue-500" },
];

// 员工资源
const employeeResources = [
  {
    title: "情绪管理技巧",
    desc: "学习如何识别和管理负面情绪",
    type: "article",
    progress: 30
  },
  {
    title: "工作压力缓解指南",
    desc: "实用的工作压力应对方法",
    type: "video",
    progress: 0
  },
  {
    title: "正念冥想练习",
    desc: "10分钟快速减压冥想音频",
    type: "audio",
    progress: 75
  },
];

// 管理者资源
const managerResources = [
  {
    title: "团队心理健康管理指南",
    desc: "如何建立健康的团队心理环境",
    type: "article",
    progress: 60
  },
  {
    title: "识别员工心理压力信号",
    desc: "早期干预的关键指标解析",
    type: "video",
    progress: 0
  },
  {
    title: "季度心理健康报告解读",
    desc: "专业解读数据背后的含义",
    type: "report",
    progress: 45
  },
];

// 情绪选项
const emotions = [
  { name: "平静", emoji: "😌" },
  { name: "焦虑", emoji: "😰" },
  { name: "压力", emoji: "😓" },
  { name: "沮丧", emoji: "😔" },
  { name: "愤怒", emoji: "😠" },
  { name: "困惑", emoji: "🤔" },
];

// 处理滚动效果
const handleScroll = () => {
  scrolled.value = window.scrollY > 50;
};

// 检查工作时间
const checkWorkHours = () => {
  const now = new Date();
  currentTime.value = now.toLocaleTimeString();
  const hours = now.getHours();
  isWorkHours.value = hours >= 9 && hours < 18;
};

// 打字效果
const typeNextSubtitle = () => {
  const currentSubtitle = subtitles[currentTextIndex.value];
  let i = 0;

  const typeChar = () => {
    if (i < currentSubtitle.length) {
      typedText.value = currentSubtitle.substring(0, i + 1);
      i++;
      setTimeout(typeChar, 150);
    } else {
      // 等待一段时间后开始下一个字幕
      setTimeout(() => {
        typedText.value = "";
        currentTextIndex.value = (currentTextIndex.value + 1) % subtitles.length;
        typeNextSubtitle();
      }, 2000);
    }
  };

  typeChar();
};

// 导览下一步
const nextGuideStep = () => {
  if (guideStep.value < 3) {
    guideStep.value++;
  } else {
    showGuide.value = false;
  }
};

// 提交表单
const submitForm = () => {
  // 这里可以添加表单验证逻辑
  console.log('表单提交:', formData);
  formSubmitted.value = true;

  // 重置表单
  setTimeout(() => {
    formData.name = '';
    formData.company = '';
    formData.email = '';
    formData.description = '';
    formSubmitted.value = false;
  }, 3000);
};

// 发送AI消息
const sendAIMessage = () => {
  if (!aiInput.value.trim()) return;

  // 添加用户消息
  aiMessages.value.push({
    type: 'user',
    text: aiInput.value
  });

  // 模拟AI回复
  setTimeout(() => {
    aiMessages.value.push({
      type: 'ai',
      text: getAIResponse(aiInput.value)
    });
  }, 1000);

  aiInput.value = '';
};

// 获取AI回复
const getAIResponse = (message) => {
  const responses = [
    "我理解您的感受，这种情况确实会让人感到压力。",
    "您可以尝试一些深呼吸练习来缓解当前的情绪。",
    "建议您可以找一个安静的环境，给自己一些放松的时间。",
    "这是很常见的情绪反应，我们可以一起找到应对方法。",
    "您的感受是重要的，我们可以一起探讨如何改善这种状况。"
  ];

  return responses[Math.floor(Math.random() * responses.length)];
};

// 获取情绪消息
const getEmotionMessage = () => {
  if (!selectedEmotion.value) {
    return "请选择您当前的情绪状态，我们将为您提供相应的支持和建议。";
  }

  const messages = {
    "平静": "很高兴您现在感到平静。保持这种状态对心理健康非常有益。您可以继续浏览我们的资源，或者记录下是什么让您感到平静的。",
    "焦虑": "感到焦虑是很常见的。深呼吸可能会有所帮助：吸气4秒，屏息4秒，呼气6秒。我们有专业人员可以帮助您应对焦虑情绪。",
    "压力": "面对压力时，尝试识别压力源并思考解决方案可能会有所帮助。适当的休息和放松也很重要。我们的专家可以提供更多建议。",
    "沮丧": "感到沮丧时，与人交流可能会有所帮助。请记住，这种感觉是暂时的。我们的专业人员随时准备倾听和支持您。",
    "愤怒": "愤怒是一种强烈但正常的情绪。尝试暂时离开引起愤怒的情境，给自己一些冷静的时间。我们的专家可以帮助您管理这种情绪。",
    "困惑": "感到困惑是正常的，特别是在面对复杂情况时。整理思绪或与他人讨论可能会有所帮助。我们的专家可以提供更多指导。"
  };

  return messages[selectedEmotion.value] || "我们的专业人员可以帮助您处理当前的情绪状态。";
};

// 连接到支持
const connectToSupport = () => {
  alert(`正在连接专业支持，您选择的情绪是：${selectedEmotion.value}`);
  showEmotionSupport.value = false;
};

// 动画曲线上的点移动
const animateDot = () => {
  let t = 0;
  const animateFrame = () => {
    t += 0.005;
    if (t > 1) t = 0;

    // 使用贝塞尔曲线计算点的位置
    const bezierPoints = [
      { x: 0, y: 50 },
      { x: 30, y: 40 },
      { x: 60, y: 70 },
      { x: 90, y: 50 },
      { x: 120, y: 30 },
      { x: 150, y: 60 },
      { x: 180, y: 50 },
      { x: 210, y: 40 },
      { x: 240, y: 60 },
      { x: 270, y: 45 },
      { x: 290, y: 35 },
      { x: 300, y: 50 }
    ];

    // 简化的贝塞尔曲线计算
    const segment = Math.floor(t * (bezierPoints.length - 1));
    const segmentT = (t * (bezierPoints.length - 1)) - segment;

    if (segment < bezierPoints.length - 1) {
      const p0 = bezierPoints[segment];
      const p1 = bezierPoints[segment + 1];

      dotPosition.x = p0.x + (p1.x - p0.x) * segmentT;
      dotPosition.y = p0.y + (p1.y - p0.y) * segmentT;
    }

    requestAnimationFrame(animateFrame);
  };

  animateFrame();
};

// 自动轮播功能
const startAutoCarousel = () => {
  setInterval(() => {
    currentTipIndex.value = (currentTipIndex.value + 1) % tipsCount; // 循环切换贴士
  }, 3000); // 每2秒切换
};

// 生命周期钩子
onMounted(() => {
  // 处理滚动效果
  window.addEventListener('scroll', handleScroll);

  // 检查工作时间
  checkWorkHours();
  const timeInterval = setInterval(checkWorkHours, 60000);

  // 开始打字效果
  typeNextSubtitle();

  // 设置默认标签页
  activeTab.value = isWorkHours.value ? 'manager' : 'employee';

  // 开始点动画
  animateDot();

  // 清理函数
  onBeforeUnmount(() => {
    window.removeEventListener('scroll', handleScroll);
    clearInterval(timeInterval);
  });

  // 在组件挂载时启动自动轮播
  startAutoCarousel();
});

// 监听移动菜单状态
watch(mobileMenuOpen, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
});

// 基础轮播逻辑（纯CSS实现可删除此部分）
document.addEventListener('DOMContentLoaded', () => {
    const track = document.querySelector('.transition-transform');
    const indicators = document.querySelectorAll('.active-indicator');
    let currentIndex = 0;

    function updateCarousel() {
      track.style.transform = `translateX(-${currentIndex * 33.33}%)`;
      indicators.forEach((ind, i) => {
        ind.classList.toggle('bg-blue-400', i === currentIndex);
        ind.classList.toggle('w-10', i === currentIndex);
      });
    }

    indicators.forEach((ind, i) => {
      ind.addEventListener('click', () => {
        currentIndex = i;
        updateCarousel();
      });
    });
  });
</script>

<style>
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

.animate-bounce-slow {
  animation: bounce 3s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-blink {
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  from, to {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

/* 3D 翻转卡片相关样式 */
.perspective-card {
  perspective: 1000px;
}

.transform-style-3d {
  transform-style: preserve-3d;
}

.backface-hidden {
  backface-visibility: hidden;
}

.rotate-y-180 {
  transform: rotateY(180deg);
}

.hover\:rotate-y-180:hover {
  transform: rotateY(180deg);
}

/* 波纹动画修正 */
@keyframes ripple {
  0% { transform: translateY(-10px); }
  100% { transform: translateY(10px); }
}
.animate-ripple {
  animation: ripple 3s ease-in-out infinite alternate;
}

/* 按钮悬停效果 */
.hover\:scale-110:hover {
  transform: scale(1.1);
}

/* 轮播激活指示器 */
.active-indicator:hover {
  transform: scaleY(1.5);
  transition: all 0.3s ease;
}
</style>

<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">