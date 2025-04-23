<template>
    <div class="bg-gray-50 py-8 min-h-screen">
      <div class="max-w-6xl mx-auto px-4">
        <!-- 标题 -->
        <div class="mb-8 text-center">
          <h1 class="text-3xl font-bold text-[#2A5C8A] mb-2">心理档案</h1>
          <p class="text-gray-600">您的心理健康评估记录与统计</p>
        </div>
  
        <!-- 下载按钮 -->
        <div class="flex justify-end mb-6">
          <button 
            @click="downloadPDF"
            class="px-6 py-2 bg-[#4a9be6] text-white rounded-md hover:bg-[#7c97ea] transition-all duration-200 transform hover:-translate-y-0.5 shadow-sm flex items-center"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7 10 12 15 17 10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            下载 PDF
          </button>
        </div>
  
        <!-- 主要内容区域 -->
        <div v-if="loading" class="flex justify-center items-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-[#4b98e0]"></div>
        </div>

        <div v-else ref="pdfContent" class="bg-white rounded-lg shadow-sm border border-gray-100 p-8">
          <!-- 用户基本信息 -->
          <section class="mb-8">
            <h2 class="text-xl font-semibold text-[#2A5C8A] mb-4 pb-2 border-b border-gray-100">基本信息</h2>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex items-center">
                <span class="w-24 text-gray-700 font-medium">姓名：</span>
                <span class="text-gray-800">{{ user.name }}</span>
              </div>
              <div class="flex items-center">
                <span class="w-24 text-gray-700 font-medium">电话：</span>
                <span class="text-gray-800">{{ user.phone }}</span>
              </div>
              <div class="flex items-center">
                <span class="w-24 text-gray-700 font-medium">邮箱：</span>
                <span class="text-gray-800">{{ user.email }}</span>
              </div>
              <div class="flex items-center">
                <span class="w-24 text-gray-700 font-medium">部门：</span>
                <span class="text-gray-800">{{ user.department }}</span>
              </div>
            </div>
          </section>
  
          <!-- 统计信息 -->
          <section class="mb-8">
            <h2 class="text-xl font-semibold text-[#2A5C8A] mb-4 pb-2 border-b border-gray-100">心理评估统计</h2>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div class="bg-blue-50 rounded-lg p-4 text-center shadow-sm">
                <div class="text-3xl font-bold text-[#2A5C8A] mb-2">{{ testCount }}</div>
                <div class="text-gray-600">近期测试次数</div>
              </div>
              <div class="bg-green-50 rounded-lg p-4 text-center shadow-sm">
                <div class="text-3xl font-bold text-green-600 mb-2">{{ combinedScore }}</div>
                <div class="text-gray-600">综合评分</div>
              </div>
              <div class="rounded-lg p-4 text-center shadow-sm" :class="healthStatusClass">
                <div class="text-3xl font-bold mb-2" :class="healthTextClass">{{ healthStatus }}</div>
                <div class="text-gray-600">健康状态</div>
              </div>
            </div>
            
            <!-- 添加评估趋势图表 -->
            <!--<div class="mt-6 p-4 bg-white rounded-lg shadow-sm">
              <h3 class="text-lg font-medium text-gray-700 mb-4">心理健康趋势</h3>
              <div v-if="healthTrend.length > 1" class="h-64">
                <div class="relative h-full">
                  <div class="absolute inset-0 flex items-end">
                    <div 
                      v-for="(point, index) in healthTrend" 
                      :key="index" 
                      class="flex-1 mx-1 relative"
                      :style="`height: ${point.percentage}%`"
                    >
                      <div 
                        class="w-full absolute bottom-0 rounded-t-sm" 
                        :class="getScoreColorClass(point.score)"
                        :style="`height: 100%`"
                      ></div>
                      <div class="absolute -bottom-6 left-0 right-0 text-xs text-center text-gray-500">
                        {{ formatTrendDate(point.date) }}
                      </div>
                      <div class="absolute -top-6 left-0 right-0 text-xs text-center font-medium">
                        {{ point.score }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="flex justify-center items-center h-32 text-gray-500">
                数据不足，无法生成趋势图
              </div>
            </div>-->
            
            <!-- 添加评估详情 -->
            <div class="mt-6 p-4 bg-white rounded-lg shadow-sm">
              <h3 class="text-lg font-medium text-gray-700 mb-4">评估详情</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="p-3 bg-gray-50 rounded-lg">
                  <div class="font-medium text-gray-700 mb-2">测试评估</div>
                  <div class="text-sm text-gray-600">
                    <p>近期测试平均分: <span class="font-medium">{{ avgScore }}</span></p>
                    <p>测试次数: <span class="font-medium">{{ testCount }}</span></p>
                    <p>最近测试: <span class="font-medium">{{ lastTestDate }}</span></p>
                  </div>
                </div>
                <div class="p-3 bg-gray-50 rounded-lg">
                  <div class="font-medium text-gray-700 mb-2">医生评估</div>
                  <div class="text-sm text-gray-600">
                    <p>医生评分平均: <span class="font-medium">{{ avgDiagnosisScore }}</span></p>
                    <p>咨询次数: <span class="font-medium">{{ diagnoses.length }}</span></p>
                    <p>最近咨询: <span class="font-medium">{{ lastDiagnosisDate }}</span></p>
                  </div>
                </div>
              </div>
              <div class="mt-4 p-3 bg-gray-50 rounded-lg">
                <div class="font-medium text-gray-700 mb-2">健康建议</div>
                <p class="text-sm text-gray-600">{{ healthAdvice }}</p>
              </div>
            </div>
          </section>
          
          <!-- 咨询记录 -->
          <section class="mb-8">
            <h2 class="text-xl font-semibold text-[#2A5C8A] mb-4 pb-2 border-b border-gray-100">咨询记录</h2>
            <div v-if="diagnoses.length === 0" class="text-center py-6 text-gray-500 bg-gray-50 rounded-lg">
              暂无咨询记录
            </div>
            <div v-else>
              <div v-for="(diagnosis, index) in diagnoses" :key="index" class="mb-4 p-4 border border-gray-100 rounded-lg hover:shadow-sm transition-shadow">
                <div class="flex justify-between items-start mb-2">
                  <span class="text-sm text-gray-500">{{ formatCustomDate(diagnosis.diagnosisDate) }}</span>
                  <span class="px-2 py-1 text-xs rounded-full" :class="getHealthScoreClass(diagnosis.healthScore)">
                    健康评分: {{ diagnosis.healthScore }}
                  </span>
                </div>
                <div class="mb-2">
                  <span class="font-medium text-gray-700">症状描述：</span>
                  <p class="text-gray-800 mt-1">{{ diagnosis.symptoms }}</p>
                </div>
                <div>
                  <span class="font-medium text-gray-700">诊断详情：</span>
                  <p class="text-gray-800 mt-1">{{ diagnosis.diagnosisDetails }}</p>
                </div>
              </div>
            </div>
          </section>
  
          <!-- 测试记录 -->
          <section>
            <h2 class="text-xl font-semibold text-[#2A5C8A] mb-4 pb-2 border-b border-gray-100">心理测试记录（最近7天）</h2>
            <div v-if="recentTests.length === 0" class="text-center py-10 text-gray-500">
              最近7天内暂无测试记录
            </div>
            <div v-else class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">测试名称</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">测试分数</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">测试结果</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">测试时间</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="(test, index) in recentTests" :key="index" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">
                      <a 
                        :href="'/employee/testPaperDetail?id=' + test.testPaperId" 
                        target="_blank"
                        class="text-[#2A5C8A] hover:underline font-medium"
                      >
                        {{ test.testPaperName }}
                      </a>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">
                      {{ test.score }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">
                      <span :class="getEvaluationClass(test.result)">
                        {{ test.result }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">
                      {{ formatCustomDate(test.time) }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { computed, reactive, ref, onMounted } from 'vue';
  import html2pdf from 'html2pdf.js';
  import request from '@/utils/request';
  import { ElMessage } from 'element-plus';
  import { formatDate } from "@/utils/date.js";
  
  const loading = ref(true);
  const user = reactive({
    name: '',
    phone: '',
    email: '',
    department: '',
    departmentId: null,
    tests: []
  });
  
  const diagnoses = ref([]);
  const userInfo = ref(JSON.parse(localStorage.getItem('xm-user')));
  const avgDiagnosisScore = ref('无数据');
  const lastDiagnosisDate = ref('无记录');
  
  onMounted(() => {
    if (userInfo.value) {
      user.name = userInfo.value.username || '';
      user.phone = userInfo.value.phone || '';
      user.email = userInfo.value.email || '';
      user.departmentId = userInfo.value.departmentId;
      
      // 获取部门信息
      if (user.departmentId) {
        getDepartmentInfo(user.departmentId);
      } else {
        user.department = '未分配部门';
      }
      
      // 获取测试记录
      loadTestRecords();
      
      // 获取咨询记录
      loadDiagnosisRecords();
    } else {
      ElMessage.warning('用户信息不完整，请重新登录');
      loading.value = false;
    }
  });
  
  const getDepartmentInfo = (departmentId) => {
    request.get('/departments/list', {
      params: {
        pageNum: 1,
        pageSize: 1,
        id: departmentId
      }
    }).then(res => {
      if (res.code === '200' && res.data?.list?.length > 0) {
        user.department = res.data.list[0].name || '未分配部门';
      } else {
        user.department = '未分配部门';
      }
    }).catch(() => {
      user.department = '未分配部门';
    });
  };
  
  const loadTestRecords = () => {
    if (!userInfo.value?.id) {
      loading.value = false;
      return;
    }
    
    console.log('正在加载用户ID的测试记录:', userInfo.value.id);
    
    // 使用更通用的方法获取测试记录
    request.get('/testRecord/selectPage', { 
      params: {
        pageNum: 1,
        pageSize: 100,  // 获取足够多的记录
        userId: userInfo.value.id
      }
    })
    .then(res => {
      if (res.code === '200') {
        if (res.data && Array.isArray(res.data.list)) {
          // 处理测试记录数据
          user.tests = res.data.list.map(test => ({
            ...test,
            testPaperId: test.testPaperId,
            testPaperName: test.testPaperName || '未知测试',
            score: test.score || 0,
            result: test.result || '暂无评估',
            time: test.time || test.createTime || test.testDate
          }));
          
          // 按时间排序
          user.tests.sort((a, b) => new Date(b.time) - new Date(a.time));
          
          console.log('获取到测试记录数量:', user.tests.length);
        } else {
          console.warn('API返回的数据结构不符合预期:', res.data);
          user.tests = [];
          ElMessage.warning('测试记录数据结构异常');
        }
      } else {
        console.error('API返回错误:', res.msg);
        ElMessage.error(res.msg || '获取测试记录失败');
      }
    })
    .catch(err => {
      console.error('加载测试记录失败:', err);
      ElMessage.error('无法加载测试记录，请稍后再试');
      user.tests = [];
    })
    .finally(() => {
      checkLoadingComplete();
    });
  };
  
  const loadDiagnosisRecords = () => {
    if (!userInfo.value || !userInfo.value.id) {
      ElMessage.warning('用户信息不完整，无法加载诊断记录');
      loading.value = false;
      return;
    }

    const patientId = userInfo.value.id;
    console.log('开始加载患者诊断记录，患者ID:', patientId);

    request.get(`/diagnoses/patient/${patientId}`)
      .then(res => {
        console.log('诊断记录API响应:', res);
        
        if (res.code === '200') {
          // 格式化每条记录，确保字段名称一致
          diagnoses.value = Array.isArray(res.data) 
            ? res.data.map(formatDiagnosisData)
            : [];
          
          console.log('格式化后的诊断记录:', diagnoses.value);
          
          // 更新相关计算属性
          calculateDiagnosisStats();
        } else {
          console.error('获取诊断记录失败:', res.msg);
          ElMessage.warning(res.msg || '获取诊断记录失败');
          diagnoses.value = [];
        }
      })
      .catch(error => {
        console.error('加载诊断记录出错:', error);
        ElMessage.error('网络错误，无法加载诊断记录');
        diagnoses.value = [];
      })
      .finally(() => {
        // 如果这是最后一个异步操作，可以在这里设置loading为false
        checkLoadingComplete();
      });
  };
  
  // 添加计算诊断统计信息的函数
  const calculateDiagnosisStats = () => {
    if (diagnoses.value.length === 0) {
      avgDiagnosisScore.value = '无数据';
      lastDiagnosisDate.value = '无记录';
      return;
    }
    
    // 计算平均健康评分
    let totalScore = 0;
    let validScores = 0;
    
    diagnoses.value.forEach(diag => {
      const score = parseFloat(diag.healthScore || diag.health_score);
      if (!isNaN(score)) {
        totalScore += score;
        validScores++;
      }
    });
    
    avgDiagnosisScore.value = validScores > 0 
      ? Math.round(totalScore / validScores) 
      : '无数据';
    
    // 获取最近的诊断日期
    const sortedDiagnoses = [...diagnoses.value].sort((a, b) => {
      const dateA = new Date(a.diagnosisDate || a.diagnosis_date);
      const dateB = new Date(b.diagnosisDate || b.diagnosis_date);
      return dateB - dateA;
    });
    
    lastDiagnosisDate.value = sortedDiagnoses.length > 0 
      ? formatDate(sortedDiagnoses[0].diagnosisDate || sortedDiagnoses[0].diagnosis_date) 
      : '无记录';
  };
  
  // 检查所有异步加载是否完成
  const checkLoadingComplete = () => {
    // 这里可以添加其他异步操作的检查
    const testRecordsLoaded = user.tests !== null;
    const diagnosesLoaded = diagnoses.value !== null;
    
    if (testRecordsLoaded && diagnosesLoaded) {
      loading.value = false;
    }
  };
  
  // 修改recentTests计算属性，用于显示最近的测试记录
  // 但保留所有记录用于评分计算
  const recentTests = computed(() => {
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);
    
    return user.tests
      .filter(test => {
        const testDate = new Date(test.time);
        return testDate >= oneWeekAgo;
      })
      .slice(0, 10); // 最多显示10条
  });
  
  // 添加一个新的计算属性，用于评分计算的测试记录
  // 这将包含更长时间范围的数据
  const allRelevantTests = computed(() => {
    const sixMonthsAgo = new Date();
    sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6);
    
    return user.tests.filter(test => {
      const testDate = new Date(test.time);
      return testDate >= sixMonthsAgo;
    });
  });
  
  const testCount = computed(() => recentTests.value.length);
  
  const avgScore = computed(() => {
    if (testCount.value === 0) return '0.00';
    const total = recentTests.value.reduce((sum, test) => sum + (parseFloat(test.score) || 0), 0);
    return (total / testCount.value).toFixed(2);
  });
  
  // 综合评分 (测试评分和医生评分的加权平均，考虑时间衰减)
  const combinedScore = computed(() => {
    // 如果没有任何数据，返回0
    if (allRelevantTests.value.length === 0 && diagnoses.value.length === 0) return '0.00';
    
    // 设置基础权重 (医生评分权重更高)
    const testBaseWeight = 0.4;
    const diagnosisBaseWeight = 0.6;
    
    // 获取当前时间
    const now = new Date();
    
    // 计算测试记录的加权分数
    let testWeightedSum = 0;
    let testWeightSum = 0;
    
    if (allRelevantTests.value.length > 0) {
      allRelevantTests.value.forEach(test => {
        const testDate = new Date(test.time);
        // 检查日期是否有效
        if (isNaN(testDate.getTime())) {
          console.warn('无效的测试日期:', test.time);
          return; // 跳过这条记录
        }
        
        // 计算天数差
        const daysDiff = Math.floor((now - testDate) / (1000 * 60 * 60 * 24));
        // 时间衰减因子 (最近的测试权重更高)
        const timeFactor = Math.exp(-0.1 * daysDiff); // 指数衰减
        
        const weight = testBaseWeight * timeFactor;
        const score = parseFloat(test.score) || 0;
        testWeightedSum += score * weight;
        testWeightSum += weight;
      });
    }
    
    // 计算医生诊断的加权分数
    let diagnosisWeightedSum = 0;
    let diagnosisWeightSum = 0;
    
    if (diagnoses.value.length > 0) {
      diagnoses.value.forEach(diag => {
        const diagDate = new Date(diag.diagnosisDate || diag.diagnosis_date);
        // 检查日期是否有效
        if (isNaN(diagDate.getTime())) {
          console.warn('无效的诊断日期:', diag.diagnosisDate || diag.diagnosis_date);
          return; // 跳过这条记录
        }
        
        // 计算天数差
        const daysDiff = Math.floor((now - diagDate) / (1000 * 60 * 60 * 24));
        // 时间衰减因子 (最近的诊断权重更高)
        const timeFactor = Math.exp(-0.05 * daysDiff); // 医生诊断的衰减较慢
        
        const weight = diagnosisBaseWeight * timeFactor;
        const score = parseFloat(diag.healthScore || diag.health_score) || 0;
        diagnosisWeightedSum += score * weight;
        diagnosisWeightSum += weight;
      });
    }
    
    // 计算最终的综合评分
    let finalScore;
    if (testWeightSum > 0 && diagnosisWeightSum > 0) {
      // 两种数据都有，综合计算
      finalScore = (testWeightedSum + diagnosisWeightedSum) / (testWeightSum + diagnosisWeightSum);
    } else if (testWeightSum > 0) {
      // 只有测试数据
      finalScore = testWeightedSum / testWeightSum;
    } else if (diagnosisWeightSum > 0) {
      // 只有诊断数据
      finalScore = diagnosisWeightedSum / diagnosisWeightSum;
    } else {
      // 没有任何数据
      finalScore = 0;
    }
    
    // 返回保留两位小数的分数
    return isNaN(finalScore) ? '0.00' : finalScore.toFixed(2);
  });
  
  // 健康状态现在基于综合评分，分为五个等级
  const healthStatus = computed(() => {
    const avg = parseFloat(combinedScore.value);
    if (isNaN(avg) || avg === 0) return '暂无评级';
    if (avg >= 85) return '优秀 🟢';
    if (avg >= 75) return '良好 🟢';
    if (avg >= 65) return '一般 🟡';
    if (avg >= 50) return '需改善 🟠';
    return '需关注 🔴';
  });
  
  const healthStatusClass = computed(() => {
    const avg = parseFloat(combinedScore.value);
    if (isNaN(avg) || avg === 0) return 'bg-gray-50';
    if (avg >= 85) return 'bg-green-50';
    if (avg >= 75) return 'bg-green-50';
    if (avg >= 65) return 'bg-yellow-50';
    if (avg >= 50) return 'bg-orange-50';
    return 'bg-red-50';
  });
  
  const healthTextClass = computed(() => {
    const avg = parseFloat(combinedScore.value);
    if (isNaN(avg) || avg === 0) return 'text-gray-600';
    if (avg >= 85) return 'text-green-600';
    if (avg >= 75) return 'text-green-600';
    if (avg >= 65) return 'text-yellow-600';
    if (avg >= 50) return 'text-orange-600';
    return 'text-red-600';
  });
  
  // 最近测试日期
  const lastTestDate = computed(() => {
    if (recentTests.value.length === 0) return '无记录';
    return formatDate(recentTests.value[0].time);
  });
  
  // 健康建议也应该更新为更细致的版本
  const healthAdvice = computed(() => {
    const score = parseFloat(combinedScore.value);
    
    if (score >= 85) {
      return '您的心理健康状况优秀。建议继续保持积极的生活态度，定期参与心理测评，保持良好的作息习惯和社交活动。';
    } else if (score >= 75) {
      return '您的心理健康状况良好。建议保持健康的生活方式，适当进行放松活动，如运动、阅读或与朋友交流，以维持良好的心理状态。';
    } else if (score >= 65) {
      return '您的心理健康状况一般。建议适当增加休息时间，学习一些减压技巧，如冥想、深呼吸等。保持规律作息，避免过度劳累。如有需要，可以预约心理医生进行咨询。';
    } else if (score >= 50) {
      return '您的心理健康状况需要改善。建议学习并实践压力管理技巧，增加与亲友的交流，减少工作压力，考虑预约心理医生进行专业咨询和指导。';
    } else if (score > 0) {
      return '您的心理健康状况需要关注。建议尽快预约心理医生进行专业咨询，减少工作压力，增加与亲友的交流，保持规律的生活作息，并考虑寻求专业的心理支持。';
    } else {
      return '暂无建议，请完成心理测评或咨询以获取个性化建议。';
    }
  });
  
  // 健康趋势数据
  const healthTrend = computed(() => {
    // 合并测试记录和咨询记录，按日期排序
    const allRecords = [];
    
    // 添加测试记录
    recentTests.value.forEach(test => {
      allRecords.push({
        date: new Date(test.time),
        score: parseFloat(test.score) || 0,
        type: 'test'
      });
    });
    
    // 添加咨询记录
    diagnoses.value.forEach(diag => {
      const date = diag.diagnosisDate || diag.diagnosis_date;
      allRecords.push({
        date: new Date(date),
        score: parseFloat(diag.healthScore || diag.health_score) || 0,
        type: 'diagnosis'
      });
    });
    
    // 按日期排序
    allRecords.sort((a, b) => a.date - b.date);
    
    // 计算百分比高度 (最大值为100%)
    const maxScore = 100;
    return allRecords.map(record => ({
      ...record,
      percentage: (record.score / maxScore) * 100
    }));
  });
  
  // 格式化趋势图日期 (只显示月/日)
  const formatTrendDate = (date) => {
    return `${date.getMonth() + 1}/${date.getDate()}`;
  };
  
  // 获取分数对应的颜色类
  const getScoreColorClass = (score) => {
    if (score >= 80) return 'bg-green-500';
    if (score >= 60) return 'bg-yellow-500';
    return 'bg-red-500';
  };
  
  const getHealthScoreClass = (score) => {
    if (score >= 80) return 'bg-green-100 text-green-800';
    if (score >= 60) return 'bg-yellow-100 text-yellow-800';
    return 'bg-red-100 text-red-800';
  };
  
  const getEvaluationClass = (evaluation) => {
    if (!evaluation) return 'text-gray-800';
    
    const evalText = String(evaluation).toLowerCase();
    
    if (evalText.includes('良好') || evalText.includes('优秀') || evalText.includes('good') || evalText.includes('excellent')) 
      return 'text-green-600';
    if (evalText.includes('一般') || evalText.includes('中等') || evalText.includes('normal') || evalText.includes('average')) 
      return 'text-yellow-600';
    if (evalText.includes('差') || evalText.includes('不佳') || evalText.includes('poor') || evalText.includes('bad')) 
      return 'text-red-600';
    return 'text-gray-800';
  };
  
  // 添加统一的数据格式化函数
  const formatDiagnosisData = (diagnosis) => {
    // 确保无论后端返回什么格式的字段，前端都能正确处理
    return {
      diagnosisId: diagnosis.diagnosisId || diagnosis.diagnosis_id,
      doctorId: diagnosis.doctorId || diagnosis.doctor_id,
      patientId: diagnosis.patientId || diagnosis.patient_id,
      symptoms: diagnosis.symptoms || '',
      diagnosisDetails: diagnosis.diagnosisDetails || diagnosis.diagnosis_details || '',
      healthScore: diagnosis.healthScore || diagnosis.health_score || 0,
      diagnosisDate: diagnosis.diagnosisDate || diagnosis.diagnosis_date || new Date().toISOString(),
      // 保留下划线格式的字段，以兼容现有模板
      diagnosis_id: diagnosis.diagnosisId || diagnosis.diagnosis_id,
      doctor_id: diagnosis.doctorId || diagnosis.doctor_id,
      patient_id: diagnosis.patientId || diagnosis.patient_id,
      diagnosis_details: diagnosis.diagnosisDetails || diagnosis.diagnosis_details || '',
      health_score: diagnosis.healthScore || diagnosis.health_score || 0,
      diagnosis_date: diagnosis.diagnosisDate || diagnosis.diagnosis_date || new Date().toISOString(),
      // 保留关联对象
      doctor: diagnosis.doctor,
      patient: diagnosis.patient
    };
  };
  
  const downloadPDF = () => {
    const element = document.querySelector('.bg-white.rounded-lg');
    if (!element) {
      ElMessage.error('无法生成PDF，请稍后再试');
      return;
    }
    
    ElMessage.info('正在生成PDF，请稍候...');
    
    // PDF配置选项
    const opt = {
      margin: 10,
      filename: `${user.name}的心理档案.pdf`,
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: { 
        scale: 2,
        useCORS: true,
        logging: false
      },
      jsPDF: { 
        unit: 'mm', 
        format: 'a4', 
        orientation: 'portrait' 
      }
    };

    // 构建HTML内容
    const htmlContent = `
      <div style="font-family: Arial, sans-serif; padding: 20px;">
        <h1 style="color: #2A5C8A; text-align: center;">${user.name}的心理档案</h1>
        
        <h2 style="color: #2A5C8A; border-bottom: 1px solid #eee; padding-bottom: 10px;">基本信息</h2>
        <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
          <tr>
            <td style="width: 25%; border: 1px solid #000; padding: 8px;"><strong>姓名</strong></td>
            <td style="width: 25%; border: 1px solid #000; padding: 8px;">${user.name}</td>
            <td style="width: 25%; border: 1px solid #000; padding: 8px;"><strong>电话</strong></td>
            <td style="width: 25%; border: 1px solid #000; padding: 8px;">${user.phone}</td>
          </tr>
          <tr>
            <td style="border: 1px solid #000; padding: 8px;"><strong>邮箱</strong></td>
            <td style="border: 1px solid #000; padding: 8px;">${user.email}</td>
            <td style="border: 1px solid #000; padding: 8px;"><strong>部门</strong></td>
            <td style="border: 1px solid #000; padding: 8px;">${user.department}</td>
          </tr>
        </table>
        
        <h2 style="color: #2A5C8A; border-bottom: 1px solid #eee; padding-bottom: 10px;">心理评估统计</h2>
        <div style="display: flex; justify-content: space-between; margin: 20px 0;">
          <div style="padding: 15px; background-color: #eff6ff; border-radius: 5px; width: 30%; text-align: center;">
            <div style="font-size: 24px; font-weight: bold; color: #2A5C8A; margin-bottom: 5px;">${testCount.value}</div>
            <div style="color: #666;">近期测试次数</div>
          </div>
          <div style="padding: 15px; background-color: #f0fdf4; border-radius: 5px; width: 30%; text-align: center;">
            <div style="font-size: 24px; font-weight: bold; color: #16a34a; margin-bottom: 5px;">${combinedScore.value}</div>
            <div style="color: #666;">综合评分</div>
          </div>
          <div style="padding: 15px; background-color: ${
            parseFloat(combinedScore.value) >= 80 ? '#f0fdf4' : 
            parseFloat(combinedScore.value) >= 60 ? '#fefce8' : 
            '#fef2f2'
          }; border-radius: 5px; width: 30%; text-align: center;">
            <div style="font-size: 24px; font-weight: bold; color: ${
              parseFloat(combinedScore.value) >= 80 ? '#16a34a' : 
              parseFloat(combinedScore.value) >= 60 ? '#ca8a04' : 
              '#dc2626'
            }; margin-bottom: 5px;">${healthStatus.value}</div>
            <div style="color: #666;">健康状态</div>
          </div>
        </div>
        
        <!-- 添加评估详情 -->
        <div style="margin-top: 20px; padding: 15px; background-color: #f9fafb; border-radius: 5px;">
          <h3 style="color: #374151; margin-bottom: 10px;">评估详情</h3>
          <div style="display: flex; justify-content: space-between; margin-bottom: 15px;">
            <div style="width: 48%; padding: 10px; background-color: #ffffff; border-radius: 5px; border: 1px solid #e5e7eb;">
              <div style="font-weight: 500; color: #374151; margin-bottom: 8px;">测试评估</div>
              <div style="font-size: 14px; color: #4b5563;">
                <p>近期测试平均分: <span style="font-weight: 500;">${avgScore.value}</span></p>
                <p>测试次数: <span style="font-weight: 500;">${testCount.value}</span></p>
                <p>最近测试: <span style="font-weight: 500;">${lastTestDate.value}</span></p>
              </div>
            </div>
            <div style="width: 48%; padding: 10px; background-color: #ffffff; border-radius: 5px; border: 1px solid #e5e7eb;">
              <div style="font-weight: 500; color: #374151; margin-bottom: 8px;">医生评估</div>
              <div style="font-size: 14px; color: #4b5563;">
                <p>医生评分平均: <span style="font-weight: 500;">${avgDiagnosisScore.value}</span></p>
                <p>咨询次数: <span style="font-weight: 500;">${diagnoses.value.length}</span></p>
                <p>最近咨询: <span style="font-weight: 500;">${lastDiagnosisDate.value}</span></p>
              </div>
            </div>
          </div>
          <div style="padding: 10px; background-color: #ffffff; border-radius: 5px; border: 1px solid #e5e7eb;">
            <div style="font-weight: 500; color: #374151; margin-bottom: 8px;">健康建议</div>
            <p style="font-size: 14px; color: #4b5563;">${healthAdvice.value}</p>
          </div>
        </div>
        
        <h2 style="color: #2A5C8A; border-bottom: 1px solid #eee; padding-bottom: 10px;">咨询记录</h2>
        ${diagnoses.value.length === 0 ? 
          '<div style="text-align: center; padding: 15px; background-color: #f9fafb; border-radius: 5px; margin-bottom: 20px;">暂无咨询记录</div>' :
          diagnoses.value.map(diagnosis => `
            <div style="border: 1px solid #e5e7eb; border-radius: 5px; padding: 15px; margin-bottom: 15px;">
              <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                <span style="color: #6b7280; font-size: 14px;">${formatCustomDate(diagnosis.diagnosisDate)}</span>
                <span style="background-color: ${
                  (diagnosis.healthScore || diagnosis.health_score) >= 80 ? '#dcfce7' : 
                  (diagnosis.healthScore || diagnosis.health_score) >= 60 ? '#fef9c3' : 
                  '#fee2e2'
                }; color: ${
                  (diagnosis.healthScore || diagnosis.health_score) >= 80 ? '#166534' : 
                  (diagnosis.healthScore || diagnosis.health_score) >= 60 ? '#854d0e' : 
                  '#b91c1c'
                }; padding: 4px 8px; border-radius: 9999px; font-size: 12px;">
                  健康评分: ${diagnosis.healthScore || diagnosis.health_score}
                </span>
              </div>
              <div style="margin-bottom: 10px;">
                <span style="font-weight: 500; color: #374151;">症状描述：</span>
                <p style="margin-top: 5px; color: #1f2937;">${diagnosis.symptoms}</p>
              </div>
              <div>
                <span style="font-weight: 500; color: #374151;">诊断详情：</span>
                <p style="margin-top: 5px; color: #1f2937;">${diagnosis.diagnosisDetails}</p>
              </div>
            </div>
          `).join('')
        }
        
        <h2 style="color: #2A5C8A; border-bottom: 1px solid #eee; padding-bottom: 10px;">心理测试记录（最近7天）</h2>
        <table style="width: 100%; border-collapse: collapse;">
          <thead>
            <tr>
              <th style="border: 1px solid #000; padding: 8px; background-color: #f2f2f2;">测试名称</th>
              <th style="border: 1px solid #000; padding: 8px; background-color: #f2f2f2;">测试分数</th>
              <th style="border: 1px solid #000; padding: 8px; background-color: #f2f2f2;">测试结果</th>
              <th style="border: 1px solid #000; padding: 8px; background-color: #f2f2f2;">测试时间</th>
            </tr>
          </thead>
          <tbody>
            ${recentTests.value.length === 0 ? 
              '<tr><td colspan="4" style="border: 1px solid #000; text-align: center; padding: 20px;">最近7天内暂无测试记录</td></tr>' :
              recentTests.value.map(test => `
                <tr>
                  <td style="border: 1px solid #000; padding: 8px;">${test.testPaperName}</td>
                  <td style="border: 1px solid #000; padding: 8px;">${test.score}</td>
                  <td style="border: 1px solid #000; padding: 8px;">${test.result}</td>
                  <td style="border: 1px solid #000; padding: 8px;">${formatCustomDate(test.time)}</td>
                </tr>
              `).join('')
            }
          </tbody>
        </table>
      </div>
    `;

    // 创建临时容器
    const container = document.createElement('div');
    container.innerHTML = htmlContent;
    document.body.appendChild(container);

    // 使用html2pdf生成并下载PDF
    html2pdf().set(opt).from(container).save().then(() => {
      document.body.removeChild(container);
      ElMessage.success('PDF文件已生成并开始下载');
    }).catch(err => {
      console.error('PDF生成失败:', err);
      document.body.removeChild(container);
      ElMessage.error('PDF生成失败，请稍后重试');
    });
  };

  // 删除自定义的 formatDate 函数，直接使用导入的函数
  // 如果需要自定义格式，可以这样包装:
  const formatCustomDate = (dateString) => {
    if (!dateString) return '未知日期';
    
    try {
      return formatDate(dateString);
    } catch (e) {
      console.error('日期格式化错误:', e);
      return '日期错误';
    }
  };
  </script>
  
  <style>
  /* 确保PDF导出时表格边框可见 */
  table {
    border-collapse: collapse;
    width: 100%;
  }
  
  th, td {
    border: 1px solid #e2e8f0;
    padding: 8px;
  }
  
  th {
    background-color: #f8fafc;
    font-weight: 600;
  }
  
  /* 打印优化 */
  @media print {
    body {
      background: white !important;
      margin: 0;
      padding: 20px;
    }
    
    .bg-white {
      box-shadow: none !important;
      border: none !important;
    }
    
    button {
      display: none !important;
    }
    
    /* 确保表格在打印时有边框 */
    table, th, td {
      border: 1px solid #000 !important;
    }
    
    /* 确保背景色在打印时可见 */
    .bg-blue-50 {
      background-color: #eff6ff !important;
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }
    
    .bg-green-50 {
      background-color: #f0fdf4 !important;
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }
    
    .bg-yellow-50 {
      background-color: #fefce8 !important;
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }
    
    .bg-red-50 {
      background-color: #fef2f2 !important;
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }
  }
  </style>