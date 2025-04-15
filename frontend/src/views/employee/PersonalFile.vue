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
                <div class="text-gray-600">测试次数</div>
              </div>
              <div class="bg-green-50 rounded-lg p-4 text-center shadow-sm">
                <div class="text-3xl font-bold text-green-600 mb-2">{{ avgScore }}</div>
                <div class="text-gray-600">平均分数</div>
              </div>
              <div class="rounded-lg p-4 text-center shadow-sm" :class="healthStatusClass">
                <div class="text-3xl font-bold mb-2" :class="healthTextClass">{{ healthStatus }}</div>
                <div class="text-gray-600">健康状态</div>
              </div>
            </div>
          </section>
  
          <!-- 测试记录 -->
          <section>
            <h2 class="text-xl font-semibold text-[#2A5C8A] mb-4 pb-2 border-b border-gray-100">心理测试记录</h2>
            <div v-if="user.tests.length === 0" class="text-center py-10 text-gray-500">
              暂无测试记录
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
                  <tr v-for="(test, index) in user.tests" :key="index" class="hover:bg-gray-50">
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
                      {{ formatDate(test.time) }}
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
  
  const loading = ref(true);
  const user = reactive({
    name: '',
    phone: '',
    email: '',
    department: '',
    departmentId: null,
    tests: []
  });
  
  const userInfo = ref(JSON.parse(localStorage.getItem('xm-user')));
  
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
    
    request.get('/testRecord/selectRecentWeekPage', { 
      params: {
        pageNum: 1,
        pageSize: 99999,
        userId: userInfo.value.id
      }
    })
    .then(res => {
      if (res.code === '200') {
        if (res.data && Array.isArray(res.data.list)) {
          // 处理测试记录数据
          user.tests = res.data.list.map(test => ({
            ...test,
            // 确保使用正确的字段名
            testPaperId: test.testPaperId,
            testPaperName: test.testPaperName || '未知测试',
            score: test.score || 0,
            result: test.result || '暂无评估',
            time: test.time || test.createTime || test.testDate
          }));
          
          // 按时间排序
          user.tests.sort((a, b) => new Date(b.time) - new Date(a.time));
          
          console.log('获取到测试记录数量:', user.tests.length);
          if (user.tests.length > 0) {
            console.log('第一条记录示例:', user.tests[0]);
          }
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
      ElMessage.error('加载测试记录失败');
    })
    .finally(() => {
      loading.value = false;
    });
  };
  
  const testCount = computed(() => user.tests.length);
  
  const avgScore = computed(() => {
    if (testCount.value === 0) return '0.00';
    const total = user.tests.reduce((sum, test) => sum + (parseFloat(test.score) || 0), 0);
    return (total / testCount.value).toFixed(2);
  });
  
  const healthStatus = computed(() => {
    const avg = parseFloat(avgScore.value);
    if (avg >= 80) return '健康 🟢';
    if (avg >= 60) return '亚健康 🟡';
    return '需关注 🔴';
  });
  
  const healthStatusClass = computed(() => {
    const avg = parseFloat(avgScore.value);
    if (avg >= 80) return 'bg-green-50';
    if (avg >= 60) return 'bg-yellow-50';
    return 'bg-red-50';
  });
  
  const healthTextClass = computed(() => {
    const avg = parseFloat(avgScore.value);
    if (avg >= 80) return 'text-green-600';
    if (avg >= 60) return 'text-yellow-600';
    return 'text-red-600';
  });
  
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
  
  const formatDate = (dateString) => {
    if (!dateString) return '';
    
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).replace(/\//g, '-');
    } catch (e) {
      return dateString;
    }
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
            <div style="color: #666;">测试次数</div>
          </div>
          <div style="padding: 15px; background-color: #f0fdf4; border-radius: 5px; width: 30%; text-align: center;">
            <div style="font-size: 24px; font-weight: bold; color: #16a34a; margin-bottom: 5px;">${avgScore.value}</div>
            <div style="color: #666;">平均分数</div>
          </div>
          <div style="padding: 15px; background-color: ${
            parseFloat(avgScore.value) >= 80 ? '#f0fdf4' : 
            parseFloat(avgScore.value) >= 60 ? '#fefce8' : 
            '#fef2f2'
          }; border-radius: 5px; width: 30%; text-align: center;">
            <div style="font-size: 24px; font-weight: bold; color: ${
              parseFloat(avgScore.value) >= 80 ? '#16a34a' : 
              parseFloat(avgScore.value) >= 60 ? '#ca8a04' : 
              '#dc2626'
            }; margin-bottom: 5px;">${healthStatus.value}</div>
            <div style="color: #666;">健康状态</div>
          </div>
        </div>
        
        <h2 style="color: #2A5C8A; border-bottom: 1px solid #eee; padding-bottom: 10px;">心理测试记录</h2>
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
            ${user.tests.length === 0 ? 
              '<tr><td colspan="4" style="border: 1px solid #000; text-align: center; padding: 20px;">暂无测试记录</td></tr>' :
              user.tests.map(test => `
                <tr>
                  <td style="border: 1px solid #000; padding: 8px;">${test.testPaperName}</td>
                  <td style="border: 1px solid #000; padding: 8px;">${test.score}</td>
                  <td style="border: 1px solid #000; padding: 8px;">${test.result}</td>
                  <td style="border: 1px solid #000; padding: 8px;">${formatDate(test.time)}</td>
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