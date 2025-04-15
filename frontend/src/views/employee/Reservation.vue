<template>
    <div class="bg-gray-50 py-10 min-h-screen">
      <div class="max-w-6xl mx-auto px-4">
        <!-- 标题和描述 -->
        <div class="mb-8 text-center">
          <h1 class="text-3xl font-bold text-[#2A5C8A] mb-2">心理咨询预约</h1>
          <p class="text-gray-600">管理您的心理咨询预约记录</p>
        </div>
        
        <!-- 搜索和筛选区域 -->
        <div class="mb-8">
          <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-100">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <!-- 状态筛选 -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">预约状态</label>
                <div class="relative">
                  <select
                    v-model="data.status"
                    class="block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-[#2A5C8A] focus:border-[#2A5C8A] rounded-md appearance-none"
                    @change="load"
                  >
                    <option :value="null">全部状态</option>
                    <option value="待审核">待审核</option>
                    <option value="审核通过">审核通过</option>
                    <option value="审核拒绝">审核拒绝</option>
                    <option value="已结束">已结束</option>
                  </select>
                  <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                  </div>
                </div>
              </div>
              
              <!-- 操作按钮 -->
              <div class="flex items-end space-x-3">
                <el-button 
                  type="primary" 
                  @click="load" 
                  class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"></circle>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                  </svg>
                  查询
                </el-button>
                <el-button 
                  @click="reset"
                  class="border-gray-300 text-gray-700"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
                    <path d="M3 3v5h5"></path>
                    <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"></path>
                    <path d="M16 21h5v-5"></path>
                  </svg>
                  重置
                </el-button>
                <el-button 
                  type="success" 
                  @click="openAddDialog" 
                  class="bg-green-500 hover:bg-green-600 border-green-500 hover:border-green-600 text-white"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                  </svg>
                  新增预约
                </el-button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 预约记录表格 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
          <el-table 
            :data="data.tableData" 
            stripe 
            class="w-full"
            :header-cell-style="{ backgroundColor: '#f9fafb', color: '#374151' }"
          >
            <el-table-column prop="doctorName" label="咨询师" width="120" />
            <el-table-column prop="start" label="开始时间" width="180">
              <template #default="{row}">
                {{ formatDateTime(row.start) }}
              </template>
            </el-table-column>
            <el-table-column prop="end" label="结束时间" width="180">
              <template #default="{row}">
                {{ formatDateTime(row.end) }}
              </template>
            </el-table-column>
            <el-table-column prop="time" label="预约时间" width="180">
              <template #default="{row}">
                {{ formatDateTime(row.time) }}
              </template>
            </el-table-column>
            <el-table-column prop="question" label="问题描述" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{row}">
                <span :class="getStatusClass(row.status)">
                  {{ row.status }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="拒绝理由" show-overflow-tooltip />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{row}">
                <div class="flex space-x-2">
                  <button 
                    v-if="row.status === '待审核' || row.status === '审核通过'"
                    @click="cancelReservation(row.id)"
                    class="px-2 py-1 bg-white text-red-500 border border-red-300 rounded hover:bg-red-50 transition-colors text-xs whitespace-nowrap"
                  >
                    取消预约
                  </button>
                  <button 
                    @click="viewDetails(row)"
                    class="px-2 py-1 bg-white text-[#2A5C8A] border border-[#2A5C8A] rounded hover:bg-blue-50 transition-colors text-xs whitespace-nowrap"
                  >
                    查看详情
                  </button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 空状态 -->
          <div v-if="data.tableData.length === 0" class="py-12 text-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto text-gray-300 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <h3 class="text-lg font-medium text-gray-700 mb-1">暂无预约记录</h3>
            <p class="text-gray-500">您还没有任何心理咨询预约记录</p>
          </div>
        </div>
        
        <!-- 分页 -->
        <div class="mt-6" v-if="data.total > 0">
          <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-4 flex flex-col items-center">
            <div class="flex items-center justify-center space-x-1">
              <!-- 每页显示数量选择器 -->
              <div class="mr-4">
                <select 
                  v-model="data.pageSize" 
                  @change="handleSizeChange(data.pageSize)"
                  class="border border-gray-300 rounded-md text-sm py-1 px-2 focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
                >
                  <option v-for="size in [5, 10, 20, 50]" :key="size" :value="size">{{ size }}条/页</option>
                </select>
              </div>
              
              <!-- 上一页按钮 -->
              <button 
                @click="handlePageChange(data.pageNum - 1)" 
                :disabled="data.pageNum <= 1"
                class="w-9 h-9 flex items-center justify-center rounded-md border"
                :class="data.pageNum <= 1 ? 'border-gray-200 text-gray-400 cursor-not-allowed' : 'border-gray-300 text-gray-700 hover:bg-gray-50'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="15 18 9 12 15 6"></polyline>
                </svg>
              </button>
              
              <!-- 页码按钮 -->
              <div class="flex space-x-1">
                <template v-for="page in displayedPages" :key="page">
                  <button
                    v-if="page !== '...'"
                    @click="handlePageChange(page)" 
                    class="w-9 h-9 flex items-center justify-center rounded-md text-sm"
                    :class="page === data.pageNum ? 'bg-[#2A5C8A] text-white' : 'border border-gray-300 text-gray-700 hover:bg-gray-50'"
                  >
                    {{ page }}
                  </button>
                  <span 
                    v-else
                    class="w-9 h-9 flex items-center justify-center text-gray-500"
                  >
                    ...
                  </span>
                </template>
              </div>
              
              <!-- 下一页按钮 -->
              <button 
                @click="handlePageChange(data.pageNum + 1)" 
                :disabled="data.pageNum >= totalPages"
                class="w-9 h-9 flex items-center justify-center rounded-md border"
                :class="data.pageNum >= totalPages ? 'border-gray-200 text-gray-400 cursor-not-allowed' : 'border-gray-300 text-gray-700 hover:bg-gray-50'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="9 18 15 12 9 6"></polyline>
                </svg>
              </button>
              
              <!-- 跳转到指定页 -->
              <div class="ml-4 flex items-center">
                <span class="text-sm text-gray-600 mr-2">跳至</span>
                <input 
                  v-model="jumpToPage" 
                  type="number" 
                  min="1" 
                  :max="totalPages" 
                  class="w-12 border border-gray-300 rounded-md text-sm py-1 px-2 text-center focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
                  @keyup.enter="handleJumpToPage"
                />
                <span class="text-sm text-gray-600 ml-2">页</span>
              </div>
              
              <!-- 总条数显示 -->
              <div class="ml-4 text-sm text-gray-600">
                共 {{ data.total }} 条
              </div>
            </div>
            
            <!-- 当前页信息 -->
            <div class="mt-2 text-sm text-gray-500">
              当前显示 {{ (data.pageNum - 1) * data.pageSize + 1 }} - {{ Math.min(data.pageNum * data.pageSize, data.total) }} 条
            </div>
          </div>
        </div>
        
        <!-- 新增预约对话框 -->
        <el-dialog
          v-model="data.dialogVisible"
          title="新增预约"
          width="600px"
          :close-on-click-modal="false"
        >
          <el-form :model="data.form" label-width="100px" :rules="rules" ref="formRef">
            <el-form-item label="咨询师" prop="doctorId">
              <el-select v-model="data.form.doctorId" placeholder="请选择咨询师" class="w-full" @change="handleDoctorChange">
                <el-option 
                  v-for="doctor in data.doctors" 
                  :key="doctor.id" 
                  :label="doctor.name" 
                  :value="doctor.id"
                />
              </el-select>
            </el-form-item>
            
            <!-- 显示医生信息 -->
            <div v-if="data.selectedDoctor" class="doctor-info mb-4">
              <h3 class="text-lg font-semibold">{{ data.selectedDoctor.name }}</h3>
              <p class="text-gray-600">{{ data.selectedDoctor.content || '暂无简介' }}</p>
              <p class="text-gray-600">电话: {{ data.selectedDoctor.phone }}</p>
              <p class="text-gray-600">邮箱: {{ data.selectedDoctor.email }}</p>
            </div>
            
            <el-form-item label="可选时间段" prop="timeRange" v-if="data.form.doctorId">
              <el-select v-model="data.form.timeRange" placeholder="请选择时间段" class="w-full">
                <el-option 
                  v-for="(time, index) in data.availableTimes" 
                  :key="index" 
                  :label="formatTimeDisplay(time)" 
                  :value="time"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="问题描述" prop="question">
              <el-input 
                v-model="data.form.question" 
                type="textarea" 
                :rows="4" 
                placeholder="请描述您的问题或咨询需求"
              />
            </el-form-item>
          </el-form>
          
          <template #footer>
            <div class="flex justify-end space-x-3">
              <el-button @click="data.dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="submitForm" :loading="data.loading">提交预约</el-button>
            </div>
          </template>
        </el-dialog>
        
        <!-- 医生详情对话框 -->
        <el-dialog
          v-model="data.doctorDetailsVisible"
          title="医生详情"
          width="600px"
        >
          <div v-if="data.selectedDoctor" class="doctor-details">
            <div class="flex items-start mb-6">
              <div class="w-20 h-20 rounded-full overflow-hidden mr-4 flex-shrink-0">
                <img 
                  :src="data.selectedDoctor.avatar || '/default-avatar.png'" 
                  :alt="data.selectedDoctor.name" 
                  class="w-full h-full object-cover"
                  @error="e => e.target.src = '/default-avatar.png'"
                />
              </div>
              <div>
                <h3 class="text-xl font-medium text-gray-900 mb-1">{{ data.selectedDoctor.name }}</h3>
                <p class="text-gray-600 mb-1">{{ data.selectedDoctor.seniority || 0 }}年工作经验</p>
                <p class="text-gray-600">{{ data.selectedDoctor.status }}</p>
              </div>
            </div>
            
            <div class="space-y-4">
              <div>
                <h4 class="text-base font-medium text-gray-800 mb-2">医生简介</h4>
                <p class="text-gray-700">{{ data.selectedDoctor.content || '暂无简介' }}</p>
              </div>
              
              <div>
                <h4 class="text-base font-medium text-gray-800 mb-2">联系方式</h4>
                <p class="text-gray-700 mb-1" v-if="data.selectedDoctor.phone">电话: {{ data.selectedDoctor.phone }}</p>
                <p class="text-gray-700" v-if="data.selectedDoctor.email">邮箱: {{ data.selectedDoctor.email }}</p>
              </div>
              
              <div v-if="data.selectedDoctor.certificate">
                <h4 class="text-base font-medium text-gray-800 mb-2">资格证书</h4>
                <p class="text-gray-700">{{ data.selectedDoctor.certificate }}</p>
              </div>
            </div>
          </div>
        </el-dialog>
      </div>
    </div>
  </template>
  
  <script setup>
  import { reactive, onMounted, ref, computed } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage, ElMessageBox } from "element-plus";
  import router from "@/router/index.js";
  
  const formRef = ref(null);
  const jumpToPage = ref(1);
  
  const data = reactive({
    status: null,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    tableData: [],
    dialogVisible: false,
    doctorDetailsVisible: false,
    loading: false,
    doctors: [],
    availableTimes: [],
    selectedDoctor: null,
    form: {
      doctorId: null,
      timeRange: null,
      question: '',
      status: '待审核'
    }
  });
  
  const totalPages = computed(() => {
    return Math.ceil(data.total / data.pageSize);
  });
  
  const displayedPages = computed(() => {
    const current = data.pageNum;
    const total = totalPages.value;
    const pages = [];
    
    if (total <= 7) {
      for (let i = 1; i <= total; i++) {
        pages.push(i);
      }
    } else {
      pages.push(1);
      
      if (current <= 3) {
        pages.push(2, 3, 4, '...', total);
      } 
      else if (current >= total - 2) {
        pages.push('...', total - 3, total - 2, total - 1, total);
      } 
      else {
        pages.push('...', current - 1, current, current + 1, '...', total);
      }
    }
    
    return pages;
  });
  
  // 表单验证规则
  const rules = {
    doctorId: [{ required: true, message: '请选择咨询师', trigger: 'change' }],
    timeRange: [{ required: true, message: '请选择时间段', trigger: 'change' }],
    question: [{ required: true, message: '请描述您的问题', trigger: 'blur' }]
  };
  
  // 加载预约记录
  const load = () => {
    request.get('/reservation/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        status: data.status
      }
    }).then(res => {
      if (res.code === '200') {
        data.tableData = res.data.list;
        data.total = res.data.total;
      } else {
        ElMessage.error(res.msg || '获取预约记录失败');
      }
    });
  };
  
  // 重置筛选条件
  const reset = () => {
    data.status = null;
    data.pageNum = 1;
    load();
  };
  
  // 取消预约
  const cancelReservation = (id) => {
    ElMessageBox.confirm('确定要取消此预约吗？', '取消确认', { 
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }).then(() => {
      request.delete('/reservation/delete/' + id).then(res => {
        if (res.code === '200') {
          ElMessage.success("预约已取消");
          load();
        } else {
          ElMessage.error(res.msg);
        }
      });
    }).catch(() => {});
  };
  
  // 查看详情
  const viewDetails = (row) => {
    // 这里可以根据实际需求跳转到详情页或显示模态框
    ElMessageBox.alert(
      `<div class="space-y-2">
        <p><strong>咨询师：</strong>${row.doctorName}</p>
        <p><strong>开始时间：</strong>${formatDateTime(row.start)}</p>
        <p><strong>结束时间：</strong>${formatDateTime(row.end)}</p>
        <p><strong>预约时间：</strong>${formatDateTime(row.time)}</p>
        <p><strong>问题描述：</strong>${row.question}</p>
        <p><strong>当前状态：</strong><span class="${getStatusClass(row.status)}">${row.status}</span></p>
        ${row.reason ? `<p><strong>拒绝理由：</strong>${row.reason}</p>` : ''}
      </div>`,
      '预约详情',
      {
        dangerouslyUseHTMLString: true,
        customClass: 'reservation-detail-modal'
      }
    );
  };
  
  // 打开新增预约对话框
  const openAddDialog = () => {
    data.dialogVisible = true;
    // 重置表单
    data.form = {
      doctorId: null,
      timeRange: null,
      question: '',
      status: '待审核'
    };
    // 加载医生列表
    loadDoctors();
  };
  
  // 加载医生列表
  const loadDoctors = () => {
    request.get('/doctor/selectAll').then(res => {
      if (res.code === '200') {
        data.doctors = res.data;
      } else {
        ElMessage.error(res.msg || '获取医生列表失败');
      }
    });
  };
  
  // 处理医生选择变化
  const handleDoctorChange = () => {
    if (data.form.doctorId) {
      // 获取所选医生的可用时间段
      request.get('/doctor/availableTimes', {
        params: { doctorId: data.form.doctorId }
      }).then(res => {
        if (res.code === '200') {
          data.availableTimes = res.data;
          data.form.timeRange = null;
        } else {
          ElMessage.error(res.msg || '获取可用时间段失败');
        }
      });
      
      // 获取医生详细信息
      request.get(`/doctor/selectById/${data.form.doctorId}`).then(res => {
        if (res.code === '200') {
          data.selectedDoctor = res.data; // 保存医生信息
        } else {
          ElMessage.error(res.msg || '获取医生信息失败');
        }
      });
    } else {
      data.availableTimes = [];
      data.form.timeRange = null;
      data.selectedDoctor = null; // 清空医生信息
    }
  };
  
  // 查看医生详情
  const viewDoctorDetails = () => {
    if (data.selectedDoctor) {
      data.doctorDetailsVisible = true;
    } else {
      ElMessage.warning('请先选择一位医生');
    }
  };
  
  // 格式化时间显示
  const formatTimeDisplay = (timeRange) => {
    if (!timeRange) return '';
    
    const [start, end] = timeRange.split(' - ');
    const startDate = new Date(start);
    const endDate = new Date(end);
    
    return `${startDate.toLocaleDateString()} ${startDate.toLocaleTimeString()} - ${endDate.toLocaleTimeString()}`;
  };
  
  // 提交预约表单
  const submitForm = () => {
    formRef.value.validate((valid) => {
      if (valid) {
        data.loading = true;
        
        // 创建一个新对象，不包含 timeRange 字段
        const formData = {
          doctorId: data.form.doctorId,
          question: data.form.question,
          status: data.form.status || '待审核'
        };
        
        // 解析时间范围
        if (data.form.timeRange) {
          const [start, end] = data.form.timeRange.split(' - ');
          formData.start = start;
          formData.end = end;
          formData.time = new Date().toISOString();
        }
        
        request.post('/reservation/add', formData).then(res => {
          data.loading = false;
          if (res.code === '200') {
            ElMessage.success('预约提交成功，请等待审核');
            data.dialogVisible = false;
            load(); // 重新加载列表
          } else {
            ElMessage.error(res.msg || '预约提交失败');
          }
        }).catch(() => {
          data.loading = false;
        });
      }
    });
  };
  
  // 格式化日期时间
  const formatDateTime = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };
  
  // 获取状态对应的样式类
  const getStatusClass = (status) => {
    switch (status) {
      case '待审核':
        return 'text-yellow-600 bg-yellow-100 px-2 py-1 rounded text-sm';
      case '审核通过':
        return 'text-green-600 bg-green-100 px-2 py-1 rounded text-sm';
      case '审核拒绝':
        return 'text-red-600 bg-red-100 px-2 py-1 rounded text-sm';
      case '已结束':
        return 'text-blue-600 bg-blue-100 px-2 py-1 rounded text-sm';
      default:
        return 'text-gray-600 bg-gray-100 px-2 py-1 rounded text-sm';
    }
  };
  
  // 处理每页显示数量变化
  const handleSizeChange = (size) => {
    data.pageSize = size;
    data.pageNum = 1; // 重置到第一页
    jumpToPage.value = 1;
    load();
  };
  
  // 处理页码变化
  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages.value) return;
    data.pageNum = page;
    jumpToPage.value = page;
    load();
  };
  
  // 添加处理跳转到指定页的函数
  const handleJumpToPage = () => {
    let page = parseInt(jumpToPage.value);
    if (isNaN(page)) {
      page = 1;
    } else if (page < 1) {
      page = 1;
    } else if (page > totalPages.value) {
      page = totalPages.value;
    }
    
    jumpToPage.value = page;
    handlePageChange(page);
  };
  
  onMounted(() => {
    load();
    jumpToPage.value = data.pageNum; // 初始化跳转页
  });
  </script>
  
  <style>
  /* 表格行悬停效果 */
  .el-table__body tr:hover > td {
    background-color: #f8fafc !important;
  }
  
  /* 模态框自定义样式 */
  .reservation-detail-modal {
    width: 80%;
    max-width: 600px;
  }
  
  .reservation-detail-modal .el-message-box__content {
    padding: 20px;
  }
  
  .reservation-detail-modal .el-message-box__btns {
    padding: 10px 20px 20px;
  }
  
  /* 分页样式调整 */
  .el-pagination.is-background .el-pager li:not(.disabled).active {
    background-color: #2A5C8A;
    color: white;
  }
  
  .el-pagination.is-background .el-pager li:not(.disabled):hover {
    color: #2A5C8A;
  }
  
  .el-pagination.is-background .el-pager li:not(.disabled).active:hover {
    color: white;
  }
  
  /* 新增样式 */
  .el-select {
    width: 100%;
  }
  
  .el-textarea__inner {
    min-height: 100px;
  }
  
  /* 医生详情样式 */
  .doctor-details {
    max-height: 70vh;
    overflow-y: auto;
  }
  
  /* 统一按钮样式 */
  :deep(.el-button) {
    transition: all 0.2s ease;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.25rem;
  }
  
  :deep(.el-button:hover:not(:disabled)) {
    transform: translateY(-2px);
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }
  
  :deep(.el-button--primary) {
    --el-button-bg-color: #2A5C8A;
    --el-button-border-color: #2A5C8A;
    --el-button-hover-bg-color: #1e4266;
    --el-button-hover-border-color: #1e4266;
  }
  
  :deep(.el-button--success) {
    --el-button-bg-color: #10b981;
    --el-button-border-color: #10b981;
    --el-button-hover-bg-color: #059669;
    --el-button-hover-border-color: #059669;
  }
  
  :deep(.el-button--danger) {
    --el-button-bg-color: #ef4444;
    --el-button-border-color: #ef4444;
    --el-button-hover-bg-color: #dc2626;
    --el-button-hover-border-color: #dc2626;
  }
  
  :deep(.el-button--default) {
    --el-button-hover-bg-color: #f3f4f6;
    --el-button-hover-border-color: #d1d5db;
  }
  
  :deep(.el-button.is-disabled) {
    opacity: 0.6;
    cursor: not-allowed;
  }
  </style>