<template>
    <div class="feedback-container">
      <!-- Search and Filter Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4">
        <div class="flex flex-wrap items-center gap-3">
          <!-- 搜索区域重新布局 -->
          <div class="relative flex-grow max-w-xs">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </span>
            <el-input 
              v-model="data.question" 
              placeholder="请输入反馈问题" 
              class="pl-10"
              clearable
              @keyup.enter="load"
            />
          </div>
          
          <div class="w-32">
            <el-select
              v-model="data.status"
              placeholder="反馈状态"
              class="w-full"
              clearable
            >
              <el-option label="待回复" value="待回复" />
              <el-option label="已回复" value="已回复" />
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
          <el-table-column prop="userName" label="用户姓名" min-width="100" />
          <el-table-column prop="question" label="反馈问题" min-width="150" show-overflow-tooltip />
          <el-table-column prop="content" label="用户想法" min-width="200" show-overflow-tooltip />
          <el-table-column prop="time" label="提交时间" min-width="150" />
          <el-table-column prop="status" label="回复状态" width="100" align="center">
            <template #default="scope">
              <el-tag 
                v-if="scope.row.status === '待回复'" 
                type="warning"
                effect="light"
                class="w-16 text-center"
              >
                {{ scope.row.status }}
              </el-tag>
              <el-tag 
                v-else-if="scope.row.status === '已回复'" 
                type="success"
                effect="light"
                class="w-16 text-center"
              >
                {{ scope.row.status }}
              </el-tag>
              <el-tag 
                v-else 
                type="info"
                effect="light"
                class="w-16 text-center"
              >
                {{ scope.row.status || '未知' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="replyName" label="回复人" min-width="100" show-overflow-tooltip />
          <el-table-column prop="replyContent" label="回复内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="replyTime" label="回复时间" min-width="150" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <div class="flex items-center space-x-2">
                <el-button 
                  type="primary" 
                  circle 
                  @click="handleEdit(scope.row)"
                  :disabled="scope.row.status === '已回复'"
                  class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266] disabled:bg-gray-300 disabled:border-gray-300"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                  </svg>
                </el-button>
                <el-button 
                  type="danger" 
                  circle 
                  @click="del(scope.row.id)"
                  class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 6h18"></path>
                    <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                    <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                  </svg>
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- Empty State -->
        <div v-if="!data.loading && data.tableData.length === 0" class="py-8 text-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
          <p class="mt-2 text-gray-500">暂无反馈数据</p>
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
  
      <!-- Reply Dialog -->
      <el-dialog 
        v-model="data.formVisible" 
        title="反馈回复" 
        width="500px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <div class="p-4 mb-4 bg-gray-50 rounded-lg">
          <div class="mb-3">
            <span class="text-gray-500 text-sm">用户姓名：</span>
            <span class="font-medium">{{ data.form.userName }}</span>
          </div>
          <div class="mb-3">
            <span class="text-gray-500 text-sm">反馈问题：</span>
            <span class="font-medium">{{ data.form.question }}</span>
          </div>
          <div class="mb-3">
            <span class="text-gray-500 text-sm">用户想法：</span>
            <div class="mt-1 p-2 bg-white rounded border border-gray-200 text-sm">
              {{ data.form.content }}
            </div>
          </div>
          <div>
            <span class="text-gray-500 text-sm">提交时间：</span>
            <span class="font-medium">{{ data.form.time }}</span>
          </div>
        </div>
        
        <el-form 
          ref="formRef" 
          :rules="data.rules" 
          :model="data.form" 
          label-width="80px" 
          class="p-4"
        >
          <el-form-item prop="replyContent" label="回复内容" required>
            <el-input 
              type="textarea" 
              :rows="5" 
              v-model="data.form.replyContent" 
              placeholder="请输入回复内容"
              maxlength="500"
              show-word-limit
            ></el-input>
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
  import { reactive, ref, nextTick } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage, ElMessageBox } from "element-plus";
  
  const formRef = ref();
  
  const data = reactive({
    formVisible: false,
    form: {},
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    question: '',
    status: '',
    loading: false,
    submitting: false,
    rules: {
      replyContent: [
        { required: true, message: '请输入回复内容', trigger: 'blur' },
        { min: 5, max: 500, message: '回复内容长度在 5 到 500 个字符', trigger: 'blur' }
      ],
    }
  });
  
  const load = async () => {
    data.loading = true;
    try {
      const res = await request.get('/feedback/selectPage', {
        params: {
          pageNum: data.pageNum,
          pageSize: data.pageSize,
          question: data.question || undefined,
          status: data.status || undefined,
        }
      });
      
      if (res.code === '200') {
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
      } else {
        ElMessage.error(res.msg || '加载数据失败');
      }
    } catch (error) {
      console.error('Failed to load feedback data:', error);
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
  
  const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row));
    data.formVisible = true;
    
    // Focus on textarea after dialog is shown
    nextTick(() => {
      const textarea = document.querySelector('.el-dialog textarea');
      if (textarea) textarea.focus();
    });
  };
  
  const update = async () => {
    if (!formRef.value) return;
    
    try {
      await formRef.value.validate();
      
      data.submitting = true;
      
      // Add current user as replier if not already set
      if (!data.form.replyName) {
        const user = JSON.parse(localStorage.getItem("xm-user") || '{}');
        data.form.replyName = user.name || '管理员';
      }
      
      // Update status to "已回复"
      data.form.status = '已回复';
      
      const res = await request.put('/feedback/update', data.form);
      
      if (res.code === '200') {
        ElMessage({
          type: 'success',
          message: '回复成功',
          duration: 2000
        });
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.msg || '回复失败');
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
  
  const del = async (id) => {
    try {
      await ElMessageBox.confirm(
        '删除后数据无法恢复，您确定删除吗？', 
        '删除确认', 
        { 
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          confirmButtonClass: 'el-button--danger'
        }
      );
      
      const res = await request.delete('/feedback/delete/' + id);
      
      if (res.code === '200') {
        ElMessage.success("删除成功");
        // If we're on the last page and delete the last item, go to previous page
        if (data.tableData.length === 1 && data.pageNum > 1) {
          data.pageNum--;
        }
        load();
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    } catch (error) {
      // User canceled the deletion
      if (error !== 'cancel' && error?.message) {
        console.error('Delete error:', error);
        ElMessage.error('删除失败: ' + error.message);
      }
    }
  };
  
  const reset = () => {
    data.question = '';
    data.status = '';
    data.pageNum = 1;
    load();
  };
  
  // Load data when component is mounted
  load();
  </script>
  
  <style scoped>
  .feedback-container {
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
  
  /* Ensure content area doesn't affect sidebar */
  :deep(.el-main) {
    overflow-y: hidden;
  }
  
  /* Tag colors */
  :deep(.el-tag--light.el-tag--warning) {
    --el-tag-bg-color: rgba(250, 173, 20, 0.1);
    --el-tag-border-color: rgba(250, 173, 20, 0.2);
    --el-tag-hover-color: var(--el-color-warning);
  }
  
  :deep(.el-tag--light.el-tag--success) {
    --el-tag-bg-color: rgba(82, 196, 26, 0.1);
    --el-tag-border-color: rgba(82, 196, 26, 0.2);
    --el-tag-hover-color: var(--el-color-success);
  }
  
  /* Card hover effects */
  .bg-white {
    transition: all 0.3s ease;
  }
  
  .bg-white:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
  
  :deep(.el-button--default) {
    --el-button-hover-bg-color: #f3f4f6;
    --el-button-hover-border-color: #d1d5db;
  }
  
  :deep(.el-button.is-disabled) {
    opacity: 0.6;
    cursor: not-allowed;
  }
  </style>