<template>
    <div class="diagnosis-container">
      <!-- Search and Action Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4">
        <div class="flex flex-wrap items-center gap-3">
          <!-- 患者姓名搜索框 -->
          <div class="relative w-[180px]">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </span>
            <el-input 
              v-model="data.patientName"
              placeholder="请输入患者姓名"
              class="pl-10"
              clearable
            />
          </div>
          
          <!-- 症状关键词搜索框 -->
          <div class="w-[180px]">
            <el-input 
              v-model="data.symptoms" 
              placeholder="请输入症状关键词" 
              clearable
            />
          </div>
          
          <!-- 日期范围选择器 -->
          <div class="date-range-wrapper">
            <el-date-picker
              v-model="data.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </div>
          
          <!-- 操作按钮组 -->
          <div class="flex gap-2">
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
                <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
                <path d="M3 3v5h5"></path>
              </svg>
              重置
            </el-button>
          </div>
          
          <!-- 新增按钮 (放置在最左侧) -->
          <div class="mr-auto">
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
      </div>
  
      <!-- Table Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4 overflow-hidden">
        <el-table 
          :data="data.tableData" 
          stripe
          border
          style="width: 100%"
          v-loading="data.loading"
          element-loading-text="加载中..."
          element-loading-background="rgba(255, 255, 255, 0.8)"
        >
          <el-table-column prop="diagnosisId" label="ID" width="80" />
          <el-table-column label="患者" min-width="120">
            <template #default="scope">
              {{ scope.row.patient?.name || (scope.row.patient ? scope.row.patient.name : '未知患者') }}
            </template>
          </el-table-column>
          <el-table-column label="症状" min-width="180">
            <template #default="scope">
              {{ scope.row.symptoms || '无症状描述' }}
            </template>
          </el-table-column>
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
              {{ formatDateDisplay(scope.row.diagnosis_date) }}
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
                :key="item.id || 'no-id'" 
                :label="`${item.name} (${item.phone || '无电话'})`" 
                :value="Number(item.id) || 0" 
              >
                <div class="flex flex-col">
                  <span>{{ item.name }}</span>
                  <small class="text-gray-500">预约时间: {{ formatDateDisplay(item.reservation_time) }}</small>
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
              :disabled="!!data.form.diagnosis_id"
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
              :disabled="!!data.form.diagnosis_id"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="health_score" label="健康评分" required>
            <el-slider
              v-model="data.form.health_score"
              :min="0"
              :max="100"
              :step="1"
              show-input
              :disabled="!!data.form.diagnosis_id"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="flex justify-end gap-2">
            <el-button @click="data.formVisible = false">{{ data.form.diagnosis_id ? '关 闭' : '取 消' }}</el-button>
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
      { required: true, type: 'number', message: '请选择患者', trigger: 'change' }
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
  
  // 确保用户信息存在
  const user = reactive(JSON.parse(localStorage.getItem("xm-user") || '{"id":0,"token":""}'));
  const headers = { token: user.token || "" };
  
  const data = reactive({
    formVisible: false,
    form: {
      doctor_id: user.id || 0,
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
      console.log('开始加载已审核通过的预约患者列表，医生ID:', user.id);
      
      const patientRes = await request.get('/reservation/getApprovedPatients', {
        params: {
          doctor_id: user.id  // 确保参数名与后端一致
        }
      });
      
      console.log('获取预约患者API响应:', patientRes);
      
      if (patientRes.code === '200') {
        // 打印原始数据，用于调试
        console.log('原始患者数据:', JSON.stringify(patientRes.data));
        
        if (!patientRes.data || patientRes.data.length === 0) {
          console.log('没有找到已审核通过的预约患者');
          data.patientList = [];
          ElMessage.info('当前没有已审核通过的预约患者');
          return;
        }
        
        data.patientList = patientRes.data.map(patient => {
          // 确保ID是数字类型
          const patientId = Number(patient.id) || 0;
          
          console.log(`处理患者数据: ID=${patientId}, Name=${patient.name}`);
          
          return {
            id: patientId,
            name: patient.name || '未知患者',
            phone: patient.phone || '无电话',
            reservation_time: patient.reservation_time || ''
          };
        });
        
        console.log('处理后的预约患者列表:', data.patientList);
      } else {
        console.error('获取预约患者列表失败:', patientRes.msg);
        ElMessage.warning(patientRes.msg || '获取预约患者列表失败');
      }
    } catch (error) {
      console.error('Failed to load approved patients:', error);
      ElMessage.error('加载预约患者列表失败');
    }
  };
  
  // 添加统一的数据格式化函数
  const formatDiagnosisData = (diagnosis) => {
    return {
      diagnosisId: diagnosis.diagnosisId,
      doctorId: diagnosis.doctorId,
      patientId: diagnosis.patientId,
      symptoms: diagnosis.symptoms,
      diagnosisDetails: diagnosis.diagnosisDetails || diagnosis.diagnosis_details,
      healthScore: diagnosis.healthScore || diagnosis.health_score,
      diagnosisDate: diagnosis.diagnosisDate || diagnosis.diagnosis_date,
      // 保留下划线格式的字段，以兼容现有模板
      diagnosis_id: diagnosis.diagnosisId,
      doctor_id: diagnosis.doctorId,
      patient_id: diagnosis.patientId,
      diagnosis_details: diagnosis.diagnosisDetails || diagnosis.diagnosis_details,
      health_score: diagnosis.healthScore || diagnosis.health_score,
      diagnosis_date: diagnosis.diagnosisDate || diagnosis.diagnosis_date,
      // 保留关联对象
      doctor: diagnosis.doctor,
      patient: diagnosis.patient
    };
  };
  
  const load = async () => {
    try {
      data.loading = true;
      
      // 构建查询参数
      const params = {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        doctorId: user.id
      };
      
      if (data.patientName) {
        params.patientName = data.patientName;
      }
      
      if (data.symptoms) {
        params.symptoms = data.symptoms;
      }
      
      if (data.dateRange && data.dateRange.length === 2) {
        params.startDate = data.dateRange[0];
        params.endDate = data.dateRange[1];
      }
      
      console.log('查询参数:', params);
      
      const res = await request.get('/diagnoses/page', { params });
      
      console.log('API响应:', res);
      
      if (res.code === '200') {
        console.log('原始返回数据:', JSON.stringify(res.data));
        
        // 确保数据结构正确
        if (res.data.list) {
          // 格式化每条记录，确保字段名称一致
          data.tableData = res.data.list.map(formatDiagnosisData);
          data.total = res.data.total || 0;
        } else if (Array.isArray(res.data)) {
          // 兼容旧格式
          data.tableData = res.data.map(formatDiagnosisData);
          data.total = res.data.length;
        } else {
          data.tableData = [];
          data.total = 0;
        }
        
        console.log('处理后的表格数据:', data.tableData);
      } else {
        console.error('查询失败:', res.msg);
        ElMessage.warning(res.msg || '查询失败');
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
    // 确保用户已登录
    if (!user.id) {
      ElMessage.error('请先登录');
      return;
    }
    
    console.log('当前登录医生ID:', user.id); // 添加日志检查医生ID
    
    // 确保先加载患者列表
    if (data.patientList.length === 0) {
      loadApprovedPatients().then(() => {
        if (data.patientList.length === 0) {
          ElMessage.warning('当前没有已预约并通过审核的患者，无法添加咨询记录');
          return;
        }
        
        data.form = {
          doctor_id: Number(user.id) || 0,
          patient_id: 0,
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
        doctor_id: Number(user.id) || 0,
        patient_id: 0,
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
      
      // 确保用户已登录
      if (!user.id) {
        throw new Error('请先登录');
      }
      
      console.log('保存时的医生ID:', user.id);
      
      // 确保 patientId 是有效的数字
      const patientId = Number(data.form.patient_id);
      if (!patientId || isNaN(patientId) || patientId <= 0) {
        throw new Error('请选择有效的患者');
      }
      
      // 确保 doctorId 是有效的数字
      const doctorId = Number(user.id);
      if (!doctorId || isNaN(doctorId) || doctorId <= 0) {
        throw new Error('医生ID无效，请重新登录');
      }
      
      // 创建一个新对象，确保字段名称与后端实体类匹配
      const formData = {
        doctorId: doctorId,
        patientId: patientId,
        symptoms: data.form.symptoms,
        diagnosisDetails: data.form.diagnosis_details,
        healthScore: data.form.health_score,
        diagnosisDate: new Date().toISOString()
      };
      
      // 同时添加下划线格式的字段，以防后端期望这种格式
      formData.doctor_id = doctorId;
      formData.patient_id = patientId;
      formData.diagnosis_details = data.form.diagnosis_details;
      formData.health_score = data.form.health_score;
      formData.diagnosis_date = formData.diagnosisDate;
      
      console.log('提交的诊断记录数据:', formData);
      
      const res = await request.post('/diagnoses/add', formData, { headers });
      
      if (res.code === '200') {
        ElMessage.success('咨询记录添加成功');
        data.formVisible = false;
        
        // 更新预约状态为已结束
        try {
          // 查找该患者的预约记录
          const reservationRes = await request.get('/reservation/selectAll', {
            params: {
              userId: patientId,  // 使用userId作为查询参数，对应employee_id
              doctorId: doctorId,
              status: '审核通过'  // 只查询已通过的预约
            }
          });
          
          console.log('查询预约记录结果:', reservationRes);
          
          if (reservationRes.code === '200' && Array.isArray(reservationRes.data) && reservationRes.data.length > 0) {
            // 获取最新的预约记录
            const latestReservation = reservationRes.data[0];
            
            console.log('找到预约记录:', latestReservation);
            
            // 更新预约状态为已结束
            const updateRes = await request.put('/reservation/update', {
              id: latestReservation.id,
              status: '已结束'
            });
            
            if (updateRes.code === '200') {
              console.log('预约状态已更新为已结束');
              ElMessage.success('咨询记录添加成功，预约状态已更新为已结束');
            } else {
              console.warn('更新预约状态失败:', updateRes.msg);
              ElMessage.success('咨询记录添加成功，但预约状态更新失败');
            }
          } else {
            console.log('未找到该患者的审核通过预约记录');
            ElMessage.success('咨询记录添加成功');
          }
        } catch (error) {
          console.error('更新预约状态时出错:', error);
          ElMessage.success('咨询记录添加成功，但更新预约状态时出错');
        }
        
        // 重新加载数据
        load();
        loadApprovedPatients();
      } else {
        ElMessage.error(res.msg || '操作失败');
      }
    } catch (error) {
      console.error('Form validation or submission error:', error);
      if (error?.message) {
        ElMessage.error(error.message);
      } else {
        ElMessage.error('提交失败，请检查表单数据');
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
    loadApprovedPatients();
  });
  
  // 使用导入的日期格式化函数
  const formatDateDisplay = (dateStr) => {
    if (!dateStr) return '未知日期';
    
    try {
      return formatDate(dateStr);
    } catch (error) {
      console.error('日期格式化错误:', error);
      return '日期错误';
    }
  };
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
  
  /* 控制日期选择器宽度 */
  .date-range-wrapper {
    width: 320px;
  }
  
  .date-range-wrapper :deep(.el-date-editor.el-input__wrapper) {
    width: 100%;
  }
  
  .date-range-wrapper :deep(.el-date-editor--daterange) {
    width: 100%;
  }
  </style>