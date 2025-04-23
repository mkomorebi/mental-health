<template>
    <div class="reservation-container">
      <!-- Search Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4">
        <div class="flex flex-wrap items-center gap-3">
          <!-- 员工姓名搜索框 -->
          <div class="relative w-[180px]">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </span>
            <el-input 
              v-model="data.userName" 
              placeholder="请输入员工姓名" 
              class="pl-10"
              clearable
              @keyup.enter="load"
            />
          </div>
          
          <!-- 预约状态选择 -->
          <div class="w-[150px]">
            <el-select
              v-model="data.status"
              placeholder="预约状态"
              class="w-full"
              clearable
            >
              <el-option label="待审核" value="待审核" />
              <el-option label="审核通过" value="审核通过" />
              <el-option label="审核拒绝" value="审核拒绝" />
              <el-option label="已结束" value="已结束" />
            </el-select>
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
              :shortcuts="dateShortcuts"
            />
          </div>
          
          <!-- 操作按钮 -->
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
          
          <!-- 批量删除按钮 (放置在最右侧) -->
          <div class="ml-auto" v-if="data.user.role === 'ADMIN'">
            <el-button 
              type="danger" 
              @click="delBatch"
              :disabled="!data.ids.length"
              class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600 text-white disabled:bg-gray-300 disabled:border-gray-300"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 6h18"></path>
                <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                <line x1="10" y1="11" x2="10" y2="17"></line>
                <line x1="14" y1="11" x2="14" y2="17"></line>
              </svg>
              批量删除
            </el-button>
          </div>
        </div>
      </div>
  
      <!-- Table Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4 overflow-hidden">
        <el-table 
          stripe 
          border
          :data="data.tableData" 
          @selection-change="handleSelectionChange"
          v-loading="data.loading"
          element-loading-text="加载中..."
          element-loading-background="rgba(255, 255, 255, 0.8)"
          class="w-full"
        >
          <el-table-column type="selection" width="55" v-if="data.user.role === 'ADMIN'"/>
          <el-table-column prop="userName" label="用户姓名" min-width="120" />
          <el-table-column prop="doctorName" label="HR姓名" min-width="120" v-if="data.user.role === 'ADMIN'"/>
          <el-table-column prop="start" label="开始时间" width="180" />
          <el-table-column prop="end" label="结束时间" width="180" />
          <el-table-column prop="time" label="预约时间" width="180" />
          <el-table-column prop="question" label="问题描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="status" label="预约状态" width="120">
            <template v-slot="scope">
              <el-tag 
                :type="statusTagType(scope.row.status)"
                effect="light"
                class="w-16 text-center !rounded-full"
              >
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="拒绝理由" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="120" fixed="right">
            <template v-slot="scope">
              <div class="flex items-center space-x-2">
                <el-button 
                  type="primary" 
                  circle 
                  :icon="Edit" 
                  @click="handleEdit(scope.row)" 
                  v-if="data.user.role === 'DOCTOR'" 
                  :disabled="scope.row.status === '审核拒绝' || scope.row.status === '已结束'"
                  class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
                />
                <el-button 
                  type="danger" 
                  circle 
                  :icon="Delete" 
                  @click="del(scope.row.id)" 
                  v-if="data.user.role === 'ADMIN'"
                  class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600"
                />
              </div>
            </template>
          </el-table-column>
        </el-table>
  
        <!-- Empty State -->
        <div v-if="!data.loading && data.tableData.length === 0" class="py-8 text-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          <p class="mt-2 text-gray-500">暂无预约数据</p>
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
        title="预约审核" 
        width="600px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <el-form ref="formRef" :model="data.form" label-width="100px" class="p-4">
          <div class="mb-6 bg-blue-50 p-4 rounded-md">
            <h3 class="text-lg font-medium mb-3 text-blue-700">预约基本信息</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex items-center">
                <span class="text-gray-500 w-20">用户姓名：</span>
                <span class="font-medium">{{ data.form.userName }}</span>
              </div>
              <div class="flex items-center">
                <span class="text-gray-500 w-20">预约时间：</span>
                <span class="font-medium">{{ data.form.time }}</span>
              </div>
              <div class="flex items-center">
                <span class="text-gray-500 w-20">开始时间：</span>
                <span class="font-medium">{{ data.form.start }}</span>
              </div>
              <div class="flex items-center">
                <span class="text-gray-500 w-20">结束时间：</span>
                <span class="font-medium">{{ data.form.end }}</span>
              </div>
            </div>
          </div>
          
          <!-- 问题描述 -->
          <div class="mb-6">
            <h3 class="text-lg font-medium mb-2">问题描述</h3>
            <p class="p-3 bg-gray-50 rounded-md text-gray-700">
              {{ data.form.question || '无问题描述' }}
            </p>
          </div>
          
          <!-- 审核结果 -->
          <div class="mt-8">
            <h3 class="text-lg font-medium mb-3">审核结果</h3>
            <el-form-item prop="status" label="审核状态" required>
              <el-radio-group v-model="data.form.status" class="flex flex-wrap gap-3">
                <el-radio-button label="待审核" class="rounded-md bg-yellow-50">
                  <span class="flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-yellow-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <line x1="12" y1="8" x2="12" y2="12"></line>
                      <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                    待审核
                  </span>
                </el-radio-button>
                <el-radio-button label="审核通过" class="rounded-md bg-green-50">
                  <span class="flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-green-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                      <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                    审核通过
                  </span>
                </el-radio-button>
                <el-radio-button label="审核拒绝" class="rounded-md bg-red-50">
                  <span class="flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <line x1="15" y1="9" x2="9" y2="15"></line>
                      <line x1="9" y1="9" x2="15" y2="15"></line>
                    </svg>
                    审核拒绝
                  </span>
                </el-radio-button>
                <el-radio-button label="已结束" class="rounded-md bg-gray-50">
                  <span class="flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-gray-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                      <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                    已结束
                  </span>
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item prop="reason" label="拒绝理由" v-if="data.form.status === '审核拒绝'">
              <el-input 
                type="textarea" 
                :rows="4" 
                v-model="data.form.reason" 
                placeholder="请输入拒绝理由"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </div>
        </el-form>
        <template #footer>
          <div class="flex justify-end gap-3">
            <el-button @click="data.formVisible = false">取 消</el-button>
            <el-button 
              type="primary" 
              @click="update"
              :loading="data.submitting"
              class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
            >
              提交审核结果
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { reactive, ref, onMounted } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage, ElMessageBox } from "element-plus";
  import { Delete, Edit } from "@element-plus/icons-vue";
  
  const formRef = ref(null);
  
  const statusTagType = (status) => {
    const map = {
      '待审核': 'warning',
      '审核通过': 'success',
      '审核拒绝': 'danger',
      '已结束': 'primary'
    }
    return map[status] || 'info'
  }
  
  const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    formVisible: false,
    form: {},
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    status: null,
    userName: '',
    dateRange: [],
    ids: [],
    loading: false,
    submitting: false
  })
  
  // 日期快捷选项
  const dateShortcuts = [
    {
      text: '最近一周',
      value: () => {
        const end = new Date()
        const start = new Date()
        start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
        return [start, end]
      },
    },
    {
      text: '最近一个月',
      value: () => {
        const end = new Date()
        const start = new Date()
        start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
        return [start, end]
      },
    },
    {
      text: '最近三个月',
      value: () => {
        const end = new Date()
        const start = new Date()
        start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
        return [start, end]
      },
    },
  ]
  
  const load = () => {
    data.loading = true
    
    // 构建查询参数
    const params = {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      status: data.status || undefined // 确保空值不会被发送
    }
    
    // 添加员工姓名搜索条件
    if (data.userName) {
      params.userName = data.userName
    }
    
    // 添加日期范围搜索条件
    if (data.dateRange && data.dateRange.length === 2) {
      params.startDate = data.dateRange[0]
      params.endDate = data.dateRange[1]
    }
    
    console.log('查询参数:', params)
    
    request.get('/reservation/selectPage', {
      params: params
    }).then(res => {
      if (res.code === '200') {
        data.tableData = res.data?.list || []
        data.total = res.data?.total || 0
        
        // 调试信息
        console.log('预约数据加载成功:', data.tableData)
      } else {
        ElMessage.error(res.msg || '加载数据失败')
        console.error('加载预约数据失败:', res)
      }
    }).catch(err => {
      console.error('请求预约数据出错:', err)
      ElMessage.error('网络错误，请稍后重试')
    }).finally(() => {
      data.loading = false
    })
  }
  
  const handlePageChange = (page) => {
    data.pageNum = page;
    load();
  };
  
  const handleSizeChange = (size) => {
    data.pageSize = size;
    data.pageNum = 1; // 切换每页显示数量时，重置为第一页
    load();
  };
  
  const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row))
    data.formVisible = true
    console.log('编辑预约:', data.form)
  }
  
  const update = () => {
    if (!formRef.value) return;
    
    formRef.value.validate((valid) => {
      if (valid) {
        data.submitting = true
        
        // 确保只发送需要的字段
        const updateData = {
          id: data.form.id,
          status: data.form.status
        }
        
        // 只有在拒绝时才发送理由
        if (data.form.status === '审核拒绝' && data.form.reason) {
          updateData.reason = data.form.reason
        }
        
        console.log('提交更新数据:', updateData)
        
        request.put('/reservation/update', updateData).then(res => {
          if (res.code === '200') {
            ElMessage.success('预约状态更新成功')
            data.formVisible = false
            load()
          } else {
            ElMessage.error(res.msg || '操作失败')
            console.error('更新预约失败:', res)
          }
        }).catch(err => {
          console.error('更新预约请求出错:', err)
          ElMessage.error('网络错误，请稍后重试')
        }).finally(() => {
          data.submitting = false
        })
      } else {
        ElMessage.warning('请完成必填项')
        return false
      }
    })
  }
  
  const del = (id) => {
    ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { 
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    }).then(() => {
      console.log('删除预约ID:', id)
      
      request.delete('/reservation/delete/' + id).then(res => {
        if (res.code === '200') {
          ElMessage.success("删除成功")
          // 如果删除的是当前页的最后一条数据且不是第一页，则返回上一页
          if (data.tableData.length === 1 && data.pageNum > 1) {
            data.pageNum--;
          }
          load()
        } else {
          ElMessage.error(res.msg || '删除失败')
          console.error('删除预约失败:', res)
        }
      }).catch(err => {
        console.error('删除预约请求出错:', err)
        ElMessage.error('网络错误，请稍后重试')
      })
    }).catch(() => {
      // 用户取消删除，不做任何操作
    })
  }
  
  const delBatch = () => {
    if (!data.ids.length) {
      ElMessage.warning("请选择要删除的数据")
      return
    }
    
    ElMessageBox.confirm(`确定要删除选中的 ${data.ids.length} 条预约记录吗？删除后数据无法恢复。`, '批量删除确认', { 
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      confirmButtonClass: 'el-button--danger'
    }).then(() => {
      console.log('批量删除预约IDs:', data.ids)
      
      request.delete("/reservation/delete/batch", {data: data.ids}).then(res => {
        if (res.code === '200') {
          ElMessage.success(`成功删除 ${data.ids.length} 条预约记录`)
          // 如果可能删除了当前页的所有数据，则重置到第一页
          if (data.ids.length >= data.tableData.length) {
            data.pageNum = 1;
          }
          load()
        } else {
          ElMessage.error(res.msg || '批量删除失败')
          console.error('批量删除预约失败:', res)
        }
      }).catch(err => {
        console.error('批量删除预约请求出错:', err)
        ElMessage.error('网络错误，请稍后重试')
      })
    }).catch(() => {
      // 用户取消删除，不做任何操作
    })
  }
  
  const handleSelectionChange = (rows) => {
    data.ids = rows.map(v => v.id)
    console.log('选中的预约IDs:', data.ids)
  }
  
  const reset = () => {
    data.status = null
    data.userName = ''
    data.dateRange = []
    data.pageNum = 1
    load()
  }
  
  // 组件挂载时加载数据
  onMounted(() => {
    console.log('Reservation组件已挂载，当前用户角色:', data.user.role)
    load()
  })
  </script>
  
  <style scoped>
  .reservation-container {
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
  
  /* Override Element Plus styles to match our design */
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
  
  :deep(.el-radio-button__inner) {
    display: flex;
    justify-content: center;
  }
  
  /* Card hover effects */
  .bg-white {
    transition: all 0.3s ease;
  }
  
  .bg-white:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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