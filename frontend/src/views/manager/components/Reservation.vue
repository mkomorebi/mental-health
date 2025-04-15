<template>
    <div class="reservation-container">
      <!-- Search Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4">
        <div class="flex flex-wrap items-center gap-3">
          <!-- 搜索区域重新布局 -->
          <div class="relative flex-grow max-w-xs">
            <el-select
              v-model="data.status"
              placeholder="请选择预约状态"
              class="w-full"
              clearable
            >
              <el-option label="待审核" value="待审核" />
              <el-option label="审核通过" value="审核通过" />
              <el-option label="审核拒绝" value="审核拒绝" />
              <el-option label="已结束" value="已结束" />
            </el-select>
          </div>
          
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
        <div class="flex flex-wrap items-center gap-3 mt-4 pt-3 border-t border-gray-100" v-if="data.user.role === 'ADMIN'">
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
        width="500px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <el-form ref="formRef" :model="data.form" label-width="80px" class="p-4">
          <el-form-item prop="status" label="审核状态" required>
            <el-radio-group v-model="data.form.status" class="flex flex-wrap gap-2">
              <el-radio-button 
                v-for="status in ['待审核', '审核通过', '审核拒绝', '已结束']"
                :key="status"
                :label="status"
                :value="status"
                class="flex-1 min-w-[100px]"
              />
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
        </el-form>
        <template #footer>
          <div class="flex justify-end gap-2">
            <el-button @click="data.formVisible = false">取 消</el-button>
            <el-button 
              type="primary" 
              @click="update"
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
    ids: [],
    loading: false,
    submitting: false
  })
  
  const load = () => {
    data.loading = true
    request.get('/reservation/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        status: data.status || undefined // 确保空值不会被发送
      }
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
  
  /* Override Element Plus styles to match our design */
  :deep(.el-button--primary) {
    --el-button-bg-color: #2A5C8A;
    --el-button-border-color: #2A5C8A;
    --el-button-hover-bg-color: #1e4266;
    --el-button-hover-border-color: #1e4266;
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
  
  /* Button hover animations */
  .el-button {
    transition: transform 0.2s ease;
  }
  
  .el-button:hover:not(:disabled) {
    transform: translateY(-2px);
  }
  </style>