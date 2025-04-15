<template>
    <div class="diagnosis-container">
      <!-- Search and Action Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4">
        <!-- Search Row -->
        <div class="flex flex-wrap items-center gap-3 mb-4 pb-3 border-b border-gray-100">
          <div class="relative flex-grow max-w-xs">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </span>
            <el-input 
              v-model="data.patientName"
              placeholder="请输入患者姓名查询"
              class="pl-10"
            />
          </div>
          <div class="relative flex-grow max-w-xs">
            <el-input 
              v-model="data.symptoms" 
              placeholder="请输入症状关键词查询" 
            />
          </div>
          <el-date-picker
            v-model="data.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
          <el-button 
            type="primary" 
            @click="load"
            class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            查询
          </el-button>
          <el-button 
            @click="reset"
            class="border-gray-300 text-gray-700"
          >
            重置
          </el-button>
        </div>
  
        <!-- Action Buttons Row -->
        <div class="flex flex-wrap items-center gap-3">
          <el-button 
            type="primary" 
            @click="handleAdd"
            class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            新增咨询记录
          </el-button>
        </div>
      </div>
  
      <!-- Table Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4 overflow-hidden">
        <el-table 
          :data="data.tableData" 
          stripe
          border
          class="w-full"
          v-loading="data.loading"
          element-loading-text="加载中..."
          element-loading-background="rgba(255, 255, 255, 0.8)"
        >
          <el-table-column prop="diagnosis_id" label="记录ID" width="100" />
          <el-table-column prop="patient_name" label="患者姓名" min-width="120" />
          <el-table-column prop="symptoms" label="症状描述" min-width="180" show-overflow-tooltip />
          <el-table-column prop="health_score" label="健康评分" width="100" align="center">
            <template #default="scope">
              <el-tag 
                :type="getScoreTagType(scope.row.health_score)" 
                effect="plain"
              >
                {{ scope.row.health_score }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="diagnosis_date" label="咨询日期" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.diagnosis_date) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <div class="flex items-center space-x-2">
                <el-button 
                  type="primary" 
                  circle 
                  @click="handleView(scope.row)"
                  class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- Empty State -->
        <div v-if="!data.loading && data.tableData.length === 0" class="py-8 text-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
          <p class="mt-2 text-gray-500">暂无咨询记录</p>
          <el-button 
            type="primary" 
            @click="handleAdd" 
            class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            添加第一条记录
          </el-button>
        </div>
      </div>
  
      <!-- Pagination Card -->
      <div v-if="data.total > 0" class="bg-white rounded-lg shadow-md p-4 flex justify-center">
        <el-pagination 
          @current-change="handlePageChange" 
          @size-change="handleSizeChange"
          background 
          layout="total, sizes, prev, pager, next, jumper" 
          :page-sizes="[10, 20, 50, 100]"
          :page-size="data.pageSize" 
          v-model:current-page="data.pageNum" 
          :total="data.total" 
        />
      </div>
  
      <!-- Form Dialog -->
      <el-dialog 
        v-model="data.formVisible" 
        :title="data.form.diagnosis_id ? '咨询记录详情' : '新增咨询记录'" 
        width="600px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <el-form 
          ref="formRef" 
          :model="data.form" 
          :rules="rules" 
          label-width="100px" 
          class="p-4"
        >
          <el-form-item prop="patient_id" label="患者" required>
            <el-select 
              v-model="data.form.patient_id" 
              placeholder="请选择已预约的患者"
              class="w-full"
              filterable
              :disabled="!!data.form.diagnosis_id"
            >
              <el-option 
                v-for="item in data.patientList" 
                :key="item.id" 
                :label="`${item.name} (${item.phone || '无电话'})`" 
                :value="item.id"
              >
                <div class="flex flex-col">
                  <span>{{ item.name }}</span>
                  <small class="text-gray-500">预约时间: {{ formatDate(item.reservation_time) }}</small>
                </div>
              </el-option>
            </el-select>
            <div class="text-xs text-gray-500 mt-1">
              只显示已预约并通过审核的患者
            </div>
          </el-form-item>
          
          <el-form-item prop="symptoms" label="症状描述" required>
            <el-input 
              v-model="data.form.symptoms" 
              placeholder="请输入患者症状描述"
              type="textarea"
              :rows="3"
              maxlength="255"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="diagnosis_details" label="诊断详情" required>
            <el-input 
              v-model="data.form.diagnosis_details" 
              placeholder="请输入诊断详情和建议"
              type="textarea"
              :rows="5"
              maxlength="1000"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="health_score" label="健康评分" required>
            <el-slider
              v-model="data.form.health_score"
              :min="0"
              :max="100"
              :step="1"
              show-input
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="flex justify-end gap-2">
            <el-button @click="data.formVisible = false">取 消</el-button>
            <el-button 
              v-if="!data.form.diagnosis_id"
              type="primary" 
              @click="save"
              :loading="data.submitting"
              class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
            >
              确 定
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { reactive, ref, onMounted } from "vue";
  import { ElMessage } from "element-plus";
  import request from "@/utils/request.js";
  import { formatDate } from "@/utils/date.js";
  
  const formRef = ref(null);
  
  const rules = {
    patient_id: [
      { required: true, message: '请选择患者', trigger: 'change' }
    ],
    symptoms: [
      { required: true, message: '请输入症状描述', trigger: 'blur' },
      { min: 5, max: 255, message: '长度在 5 到 255 个字符', trigger: 'blur' }
    ],
    diagnosis_details: [
      { required: true, message: '请输入诊断详情', trigger: 'blur' },
      { min: 10, max: 1000, message: '长度在 10 到 1000 个字符', trigger: 'blur' }
    ],
    health_score: [
      { required: true, message: '请给出健康评分', trigger: 'blur' }
    ]
  };
  
  let user = JSON.parse(localStorage.getItem("xm-user") || '{}');
  const headers = { token: user.token };
  
  const data = reactive({
    formVisible: false,
    form: {
      doctor_id: user.id,
      patient_id: null,
      symptoms: '',
      diagnosis_details: '',
      health_score: 50
    },
    tableData: [],
    patientList: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    patientName: '',
    symptoms: '',
    dateRange: [],
    loading: false,
    submitting: false
  });
  
  const getScoreTagType = (score) => {
    if (score >= 80) return 'success';
    if (score >= 60) return 'primary';
    if (score >= 40) return 'warning';
    return 'danger';
  };
  
  const loadApprovedPatients = async () => {
    try {
      const patientRes = await request.get('/reservation/getApprovedPatients', {
        params: {
          doctor_id: user.id
        }
      });
      
      if (patientRes.code === '200') {
        data.patientList = patientRes.data || [];
        console.log('已审核通过的预约患者列表:', data.patientList);
        
        if (data.patientList.length === 0) {
          ElMessage.info('当前没有已审核通过的预约患者');
        }
      } else {
        ElMessage.warning('获取预约患者列表失败');
      }
    } catch (error) {
      console.error('Failed to load approved patients:', error);
      ElMessage.error('加载预约患者列表失败');
    }
  };
  
  const load = async () => {
    data.loading = true;
    try {
      // 加载咨询记录
      const res = await request.get('/diagnoses/selectPage', {
        params: {
          pageNum: data.pageNum,
          pageSize: data.pageSize,
          patientName: data.patientName || undefined,
          symptoms: data.symptoms || undefined,
          startDate: data.dateRange?.[0] || undefined,
          endDate: data.dateRange?.[1] || undefined,
          doctor_id: user.id
        }
      });
      
      if (res.code === '200') {
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
      } else {
        ElMessage.error(res.msg || '加载咨询记录失败');
      }
      
      // 加载已预约并通过审核的患者列表
      if (!data.patientList.length) {
        await loadApprovedPatients();
      }
      
    } catch (error) {
      console.error('Failed to load data:', error);
      ElMessage.error('加载数据失败，请检查网络连接');
    } finally {
      data.loading = false;
    }
  };
  
  const handlePageChange = (page) => {
    data.pageNum = page;
    load();
  };
  
  const handleSizeChange = (size) => {
    data.pageSize = size;
    data.pageNum = 1;
    load();
  };
  
  const handleAdd = () => {
    // 确保先加载患者列表
    if (data.patientList.length === 0) {
      loadApprovedPatients().then(() => {
        if (data.patientList.length === 0) {
          ElMessage.warning('当前没有已预约并通过审核的患者，无法添加咨询记录');
          return;
        }
        
        data.form = {
          doctor_id: user.id,
          patient_id: null,
          symptoms: '',
          diagnosis_details: '',
          health_score: 50
        };
        data.formVisible = true;
      });
    } else {
      if (data.patientList.length === 0) {
        ElMessage.warning('当前没有已预约并通过审核的患者，无法添加咨询记录');
        return;
      }
      
      data.form = {
        doctor_id: user.id,
        patient_id: null,
        symptoms: '',
        diagnosis_details: '',
        health_score: 50
      };
      data.formVisible = true;
    }
  };
  
  const handleView = (row) => {
    data.form = JSON.parse(JSON.stringify(row));
    data.formVisible = true;
  };
  
  const save = async () => {
    if (!formRef.value) return;
    
    try {
      await formRef.value.validate();
      
      data.submitting = true;
      
      const res = await request.post('/diagnoses/add', data.form);
      
      if (res.code === '200') {
        ElMessage.success('咨询记录添加成功');
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.msg || '操作失败');
      }
    } catch (error) {
      console.error('Form validation or submission error:', error);
      if (error?.message) {
        ElMessage.error(error.message);
      }
    } finally {
      data.submitting = false;
    }
  };
  
  const reset = () => {
    data.patientName = '';
    data.symptoms = '';
    data.dateRange = [];
    data.pageNum = 1;
    load();
  };
  
  // Load data when component is mounted
  onMounted(() => {
    load();
  });
  </script>
  
  <style scoped>
  .diagnosis-container {
    width: 100%;
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    padding: 1rem;
    background-color: #f5f7fa;
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
  
  :deep(.el-button--default) {
    --el-button-hover-bg-color: #f3f4f6;
    --el-button-hover-border-color: #d1d5db;
  }
  
  :deep(.el-button.is-disabled) {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
    background-color: #2A5C8A;
  }
  
  :deep(.el-table .el-table__header-wrapper th) {
    background-color: #f5f7fa;
    font-weight: 600;
  }
  
  :deep(.el-dialog__header) {
    border-bottom: 1px solid #e4e7ed;
    padding: 15px 20px;
    margin-right: 0;
  }
  
  :deep(.el-dialog__footer) {
    border-top: 1px solid #e4e7ed;
    padding: 15px 20px;
  }
  
  /* Table row hover effect */
  :deep(.el-table__row) {
    transition: background-color 0.2s ease;
  }
  
  :deep(.el-table__row:hover) {
    background-color: #f0f7ff !important;
  }
  </style>