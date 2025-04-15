<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>
<template>
  <div class="bg-gray-50 py-10 min-h-screen">
    <div class="max-w-3xl mx-auto px-4">
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
        <!-- 测试标题 -->
        <div class="p-6 border-b border-gray-100 bg-gradient-to-r from-[#2A5C8A]/5 to-[#2A5C8A]/10">
          <div class="text-center">
            <h1 class="text-2xl font-bold text-gray-800 mb-2">{{ data.testPaper.title }}</h1>
            <div class="flex items-center justify-center text-sm text-gray-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
              <span>满分：{{ data.testPaper.score }} 分</span>
            </div>
          </div>
        </div>
        
        <!-- 测试题目列表 -->
        <div class="p-6">
          <div v-if="!data.testPaper.topicList || data.testPaper.topicList.length === 0" class="text-center py-10">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M8 15h8M9 9h.01M15 9h.01"></path>
            </svg>
            <h3 class="text-lg font-medium text-gray-700 mb-1">暂无题目</h3>
            <p class="text-gray-500">该测试暂无题目内容</p>
          </div>
          
          <div v-else class="space-y-6">
            <div 
              v-for="(item, index) in data.testPaper.topicList" 
              :key="index"
              class="rounded-lg border border-gray-200 overflow-hidden"
            >
              <!-- 题目标题 -->
              <div class="p-4 bg-[#2A5C8A]/10 border-b border-gray-200">
                <h3 class="font-medium text-gray-800 flex items-start">
                  <span class="flex-shrink-0 flex items-center justify-center w-6 h-6 rounded-full bg-[#2A5C8A] text-white text-sm mr-2">
                    {{ index + 1 }}
                  </span>
                  <span>{{ item.title }}</span>
                </h3>
              </div>
              
              <!-- 选项 -->
              <div class="p-4">
                <div class="space-y-3">
                  <label 
                    v-for="option in [
                      { label: 'A', value: item.aName },
                      { label: 'B', value: item.bName },
                      { label: 'C', value: item.cName },
                      { label: 'D', value: item.dName }
                    ]" 
                    :key="option.label"
                    class="flex items-start p-3 rounded-md cursor-pointer transition-colors"
                    :class="item.userAnswer === option.value ? 'bg-[#2A5C8A]/10 border border-[#2A5C8A]/30' : 'hover:bg-gray-50 border border-transparent'"
                  >
                    <input 
                      type="radio" 
                      :name="`question-${index}`" 
                      :value="option.value"
                      v-model="item.userAnswer"
                      @change="() => handleAnswerChange(index, option.value)"
                      class="mt-0.5 mr-3 accent-[#2A5C8A]"
                    />
                    <div>
                      <span class="font-medium text-gray-800">{{ option.label }}.</span>
                      <span class="ml-1 text-gray-700">{{ option.value }}</span>
                    </div>
                  </label>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 提交按钮 -->
          <div class="mt-8 flex justify-center">
            <button 
              @click="submit" 
              class="px-8 py-3 bg-[#2A5C8A] text-white rounded-md hover:bg-[#1e4266] transition-all duration-200 transform hover:-translate-y-0.5 shadow-lg flex items-center justify-center font-medium"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
              提交测试
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import router from "@/router/index.js";

const data = reactive({
  testPaperId: router.currentRoute.value.query.id,
  testPaper: {}
});

const loadTestPaper = () => {
  data.testPaperId = router.currentRoute.value.query.id;
  request.get('/testPaper/selectById/' + data.testPaperId).then(res => {
    if (res.code === '200') {
      data.testPaper = res.data;
      console.log('试卷数据：', data.testPaper);
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const submit = () => {
  // 检查是否所有题目都已回答
  const unansweredQuestions = data.testPaper.topicList.filter(topic => !topic.userAnswer);
  
  if (unansweredQuestions.length > 0) {
    ElMessage.warning(`您还有 ${unansweredQuestions.length} 道题目未回答，请检查后再提交`);
    return;
  }
  
  // 直接提交整个 testPaper 对象
  // 后端会从中提取需要的信息，包括题目列表和用户答案
  request.post('/testRecord/add', data.testPaper).then(res => {
    if (res.code === '200') {
      ElMessage.success('提交成功，可以在测试记录里查看测试结果');
      setTimeout(() => {
        window.location.href = '/employee/testRecord';
      }, 1500);
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const handleAnswerChange = (index, value) => {
  console.log(`题目 ${index + 1} 选择了:`, value);
};

onMounted(() => {
  loadTestPaper();
});
</script>

<style scoped>
/* 自定义单选按钮样式 */
input[type="radio"] {
  width: 18px;
  height: 18px;
}
</style>