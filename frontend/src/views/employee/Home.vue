<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>
<template>
  <div class="bg-gray-50">
    <!-- 轮播图信息 -->
    <div class="mb-10 max-w-6xl mx-auto">
      <div class="rounded-xl overflow-hidden shadow-lg">
        <!-- 自定义轮播图组件 -->
        <div class="custom-carousel group relative rounded-xl overflow-hidden h-[400px]">
          <!-- 轮播图内容 -->
          <div class="carousel-container h-full">
            <div 
              v-for="(item, index) in data.sideshowData" 
              :key="item.id" 
              class="carousel-item absolute w-full h-full transition-all duration-700"
              :class="{ 
                'opacity-100 z-10': index === currentIndex, 
                'opacity-0 z-0': index !== currentIndex,
                'translate-x-0': index === currentIndex,
                'translate-x-full': index > currentIndex,
                '-translate-x-full': index < currentIndex
              }"
            >
              <div class="relative h-full w-full group cursor-pointer" @click="navTo('/employee/propagateDetail?id=' + item.id)">
                <img :src="item.img" alt="" class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105">
                <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/70 to-transparent p-6">
                  <h3 class="text-white text-xl font-bold">{{ item.propagateTitle || '心理健康宣传' }}</h3>
                  <p class="text-white/80 mt-2 transform transition-transform duration-500 translate-y-0 group-hover:translate-y-[-5px]">点击查看详情</p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 左右导航按钮 -->
          <button 
            @click.stop="prevSlide" 
            class="carousel-nav-btn left-4 opacity-0 group-hover:opacity-70 hover:opacity-100 transform transition-transform duration-300 hover:scale-110"
            aria-label="上一张"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          
          <button 
            @click.stop="nextSlide" 
            class="carousel-nav-btn right-4 opacity-0 group-hover:opacity-70 hover:opacity-100 transform transition-transform duration-300 hover:scale-110"
            aria-label="下一张"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
          
          <!-- 底部指示器 -->
          <div class="absolute bottom-4 left-0 right-0 flex justify-center space-x-3 z-20">
            <button
              v-for="(_, index) in data.sideshowData"
              :key="index"
              @click.stop="goToSlide(index)"
              class="carousel-indicator transition-all duration-300 hover:scale-125"
              :class="{ 
                'bg-white scale-125 w-[12px] h-[12px]': index === currentIndex, 
                'bg-white/50 hover:bg-white/80': index !== currentIndex 
              }"
              :aria-label="`转到第 ${index + 1} 张幻灯片`"
            ></button>
          </div>
          
          <!-- 进度条 -->
          <div class="absolute bottom-0 left-0 right-0 h-1 bg-white/20">
            <div 
              class="h-full bg-white/70 transition-all duration-300 ease-linear"
              :style="{ width: `${progressPercentage}%` }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 心理健康宣传 -->
    <div class="py-10 bg-white">
      <div class="max-w-6xl mx-auto px-4">
        <div class="flex flex-wrap items-center justify-between mb-6">
          <div class="flex items-center mb-4 md:mb-0">
            <h2 class="text-2xl font-bold text-[#2A5C8A]">心理健康宣传</h2>
            <div class="ml-4 text-gray-500">心理健康宣传 必不可少</div>
          </div>
          <button 
            @click="navTo('/employee/propagate')" 
            class="text-[#2A5C8A] hover:text-[#1e4266] flex items-center text-sm font-medium transition-colors"
          >
            更多健康宣传
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 ml-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M5 12h14"></path>
              <path d="m12 5 7 7-7 7"></path>
            </svg>
          </button>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- 文章列表 -->
          <div class="lg:col-span-2 space-y-4">
            <div 
              v-for="item in data.propagateData" 
              :key="item.id" 
              class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow duration-300 cursor-pointer"
              @click="navTo('/employee/propagateDetail?id=' + item.id)"
            >
              <div class="flex flex-col md:flex-row p-4">
                <div class="md:w-48 flex-shrink-0 mb-4 md:mb-0">
                  <img :src="item.img" alt="" class="w-full h-32 object-cover rounded-md">
                </div>
                <div class="md:ml-4 flex-1">
                  <h3 class="text-lg font-semibold text-gray-800 mb-2">{{ item.title }}</h3>
                  <p class="text-gray-600 text-sm line-clamp-3 mb-3">{{ item.content }}</p>
                  <div class="flex items-center text-sm text-gray-500">
                    <div class="flex items-center">
                      <img :src="item.doctorAvatar" alt="" class="w-6 h-6 rounded-full">
                      <span class="ml-2">{{ item.doctorName }}</span>
                    </div>
                    <div class="ml-4 flex items-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"></path>
                        <circle cx="12" cy="12" r="3"></circle>
                      </svg>
                      <span>{{ item.num }} 次浏览</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 公告信息 -->
          <div class="lg:col-span-1">
            <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-5">
              <h3 class="text-xl font-bold text-gray-800 mb-4 pb-2 border-b border-gray-100 text-center">公告信息</h3>
              <div class="space-y-4">
                <el-popover 
                  v-for="item in data.noticeData" 
                  :key="item.id"
                  placement="left"
                  :title="item.title"
                  :width="300"
                  trigger="hover"
                  :content="item.content"
                  popper-class="notice-popover"
                >
                  <template #reference>
                    <div class="cursor-pointer hover:bg-gray-50 p-2 rounded-md transition-colors">
                      <h4 class="font-medium text-gray-800 truncate">·【{{ item.title }}】</h4>
                      <p class="text-gray-600 text-sm mt-1 ml-3 truncate">{{ item.content }}</p>
                    </div>
                  </template>
                </el-popover>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 心理测试信息 -->
    <div class="py-10 bg-gray-50">
      <div class="max-w-6xl mx-auto px-4">
        <div class="flex flex-wrap items-center justify-between mb-6">
          <div class="flex items-center mb-4 md:mb-0">
            <h2 class="text-2xl font-bold text-[#2A5C8A]">心理测试</h2>
            <div class="ml-4 text-gray-500">认识自己，了解自己</div>
          </div>
          <button 
            @click="navTo('/employee/testPaper')" 
            class="text-[#2A5C8A] hover:text-[#1e4266] flex items-center text-sm font-medium transition-colors"
          >
            更多心理测试
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 ml-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M5 12h14"></path>
              <path d="m12 5 7 7-7 7"></path>
            </svg>
          </button>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div 
            v-for="item in data.testPaperData" 
            :key="item.id" 
            class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-all duration-300 hover:translate-y-[-5px]"
          >
            <div class="relative overflow-hidden">
              <img :src="item.img" alt="" class="w-full h-48 object-cover">
              <div class="absolute inset-0 bg-gradient-to-t from-black/40 to-transparent"></div>
            </div>
            <div class="p-4">
              <h3 class="text-lg font-semibold text-gray-800 mb-3 line-clamp-2 h-14">{{ item.title }}</h3>
              <div class="flex items-center justify-between">
                <div class="text-sm text-gray-500">
                  <span>{{ item.testNum }}</span> 人测试过
                </div>
                <button 
                  @click="navTo('/employee/testPaperDetail?id=' + item.id)" 
                  class="px-4 py-2 bg-[#2A5C8A] text-white text-sm rounded-md hover:bg-[#1e4266] transition-colors"
                >
                  去测试
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onUnmounted } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const formRef = ref();
const currentIndex = ref(0);
const autoPlayTimer = ref(null);
const autoPlayInterval = 3000; // 自动播放间隔，与原来保持一致
const progressPercentage = ref(0);
const progressInterval = ref(null);

const data = reactive({
  sideshowData: [],
  propagateData: [],
  noticeData: [],
  doctorData: [],
  testPaperData: [],

  form: {},
  formVisible: false,
  rules: {
    timeRange: [
      { required: true, message: '请选择预约时间', trigger: 'blur' }
    ],
    question: [
      { required: true, message: '请选描述问题', trigger: 'blur' }
    ],
  }
});

// 轮播图控制函数
const nextSlide = () => {
  if (data.sideshowData.length === 0) return;
  currentIndex.value = (currentIndex.value + 1) % data.sideshowData.length;
  resetAutoPlay();
};

const prevSlide = () => {
  if (data.sideshowData.length === 0) return;
  currentIndex.value = (currentIndex.value - 1 + data.sideshowData.length) % data.sideshowData.length;
  resetAutoPlay();
};

const goToSlide = (index) => {
  currentIndex.value = index;
  resetAutoPlay();
};

const startAutoPlay = () => {
  stopAutoPlay();
  resetProgress();
  
  // 启动进度条
  progressInterval.value = setInterval(() => {
    progressPercentage.value += (100 / (autoPlayInterval / 100));
    if (progressPercentage.value >= 100) {
      progressPercentage.value = 0;
    }
  }, 100);
  
  // 启动轮播
  autoPlayTimer.value = setInterval(() => {
    nextSlide();
  }, autoPlayInterval);
};

const stopAutoPlay = () => {
  if (autoPlayTimer.value) {
    clearInterval(autoPlayTimer.value);
    autoPlayTimer.value = null;
  }
  
  if (progressInterval.value) {
    clearInterval(progressInterval.value);
    progressInterval.value = null;
  }
};

const resetAutoPlay = () => {
  stopAutoPlay();
  resetProgress();
  startAutoPlay();
};

const loadTestPaper = () => {
  request.get('/testPaper/selectAllDesc').then(res => {
    if (res.code === '200') {
      data.testPaperData = res.data;
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const loadSideshow = () => {
  request.get('/sideshow/selectAll').then(res => {
    if (res.code === '200') {
      data.sideshowData = res.data;
      // 数据加载后启动自动播放
      if (data.sideshowData.length > 0) {
        startAutoPlay();
      }
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const loadPropagate = () => {
  request.get('/propagate/selectTop3').then(res => {
    if (res.code === '200') {
      data.propagateData = res.data;
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const loadNotice = () => {
  request.get('/notice/selectAll').then(res => {
    if (res.code === '200') {
      data.noticeData = res.data;
      if (data.noticeData.length > 6) {
        data.noticeData = data.noticeData.slice(0, 6);
      }
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const loadDoctor = () => {
  request.get('/doctor/top4').then(res => {
    if (res.code === '200') {
      data.doctorData = res.data;
      if (data.doctorData.length > 4) {
        data.doctorData = data.doctorData.slice(0, 4);
      }
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const navTo = (url) => {
  location.href = url;
};

const reservationInit = (item) => {
  data.form = JSON.parse(JSON.stringify(item));
  data.formVisible = true;
};

const submit = () => {
  formRef.value.validate(valid => {
    if (valid) {
      let reservationData = {
        doctorId: data.form.id,
        timeRange: data.form.timeRange,
        question: data.form.question
      };
      request.post('/reservation/add', reservationData).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功，等待医生审核');
          data.formVisible = false;
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  });
};

// 添加重置进度条函数
const resetProgress = () => {
  progressPercentage.value = 0;
  if (progressInterval.value) {
    clearInterval(progressInterval.value);
    progressInterval.value = null;
  }
};

// 添加键盘导航支持
const handleKeyDown = (e) => {
  if (e.key === 'ArrowLeft') {
    prevSlide();
  } else if (e.key === 'ArrowRight') {
    nextSlide();
  }
};

onMounted(() => {
  loadSideshow();
  loadPropagate();
  loadNotice();
  loadDoctor();
  loadTestPaper();
  
  // 添加键盘事件监听
  window.addEventListener('keydown', handleKeyDown);
});

// 组件卸载时清除定时器
onUnmounted(() => {
  stopAutoPlay();
  resetProgress();
  
  // 移除键盘事件监听
  window.removeEventListener('keydown', handleKeyDown);
});
</script>

<style>
.custom-carousel {
  position: relative;
  overflow: hidden;
}

.carousel-container {
  position: relative;
  height: 100%;
  width: 100%;
}

.carousel-item {
  position: absolute;
  width: 100%;
  height: 100%;
  transition: all 0.7s ease;
}

.carousel-nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 20;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.carousel-nav-btn:hover {
  background-color: rgba(0, 0, 0, 0.7);
  transform: translateY(-50%) scale(1.1);
}

.carousel-nav-btn:active {
  transform: translateY(-50%) scale(0.95);
}

.custom-carousel:hover .carousel-nav-btn {
  opacity: 0.7;
}

.carousel-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.carousel-indicator:hover {
  background-color: rgba(255, 255, 255, 0.8);
}

.carousel-indicator:active {
  transform: scale(0.9);
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notice-popover {
  max-width: 300px;
}
</style>