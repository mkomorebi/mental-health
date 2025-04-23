<template>
    <div class="sideshow-container">
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
              v-model="data.propagateTitle" 
              placeholder="请输入宣传标题查询" 
              class="pl-10"
            />
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
            新增轮播图
          </el-button>
          <el-button 
            type="danger" 
            @click="delBatch"
            :disabled="!data.ids.length"
            class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600 text-white disabled:bg-red-300 disabled:border-red-300"
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
          :data="data.tableData" 
          @selection-change="handleSelectionChange"
          stripe
          border
          class="w-full"
          v-loading="data.loading"
          element-loading-text="加载中..."
          element-loading-background="rgba(255, 255, 255, 0.8)"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="img" label="轮播图封面" min-width="200" align="center">
            <template #default="scope">
              <el-image 
                v-if="scope.row.img"
                class="w-40 h-12 rounded-md object-cover mx-auto"
                :src="scope.row.img" 
                :preview-src-list="[scope.row.img]" 
                preview-teleported
                fit="cover"
              />
              <div v-else class="w-40 h-12 rounded-md bg-gray-200 flex items-center justify-center mx-auto">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <circle cx="8.5" cy="8.5" r="1.5"></circle>
                  <polyline points="21 15 16 10 5 21"></polyline>
                </svg>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="propagateTitle" label="健康宣传标题" min-width="200" show-overflow-tooltip />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <div class="flex items-center space-x-2">
                <el-button 
                  type="primary" 
                  circle 
                  @click="handleEdit(scope.row)"
                  class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
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
            <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
            <line x1="8" y1="21" x2="16" y2="21"></line>
            <line x1="12" y1="17" x2="12" y2="21"></line>
          </svg>
          <p class="mt-2 text-gray-500">暂无轮播图数据</p>
          <el-button 
            type="primary" 
            @click="handleAdd" 
            class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            添加第一张轮播图
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
        :title="data.form.id ? '编辑轮播图' : '新增轮播图'" 
        width="500px" 
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
          <el-form-item prop="propagateId" label="宣传标题" required>
            <el-select
              v-model="data.form.propagateId"
              placeholder="请选择健康宣传标题"
              class="w-full"
              filterable
            >
              <el-option
                v-for="item in data.propagateData"
                :key="item.id"
                :label="item.title"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item prop="img" label="轮播图" required>
            <div class="flex items-center">
              <Upload v-model="data.form.img" />
              
              <div v-if="data.form.img" class="ml-4 flex items-center text-sm text-gray-500">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-500 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                  <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
                图片已上传
              </div>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="flex justify-end gap-2">
            <el-button @click="data.formVisible = false">取 消</el-button>
            <el-button 
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
  import request from "@/utils/request.js";
  import { ElMessage, ElMessageBox } from "element-plus";
  import Upload from "@/components/Upload.vue";
  
  const formRef = ref(null);
  const baseUrl = import.meta.env.VITE_APP_BASE_API || 'http://localhost:9090';
  console.log('Sideshow component baseUrl:', baseUrl);
  const fileList = ref([]);
  
  // 获取当前用户信息
  const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  const headers = { token: user.token };
  
  const rules = {
    propagateId: [
      { required: true, message: '请选择健康宣传标题', trigger: 'change' }
    ],
    img: [
      { required: true, message: '请上传轮播图封面', trigger: 'change' }
    ]
  };
  
  const data = reactive({
    formVisible: false,
    form: {},
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    propagateTitle: '',
    ids: [],
    propagateData: [],
    loading: false,
    submitting: false
  });
  
  const loadPropagateData = async () => {
    try {
      console.log('加载宣传数据...');
      // 修改为使用带有公司ID过滤的接口
      const res = await request.get('/propagate/selectByCompany');
      
      console.log('宣传数据响应:', res);
      
      if (res.code === '200') {
        data.propagateData = res.data || [];
        console.log('加载到宣传数据:', data.propagateData.length, '条');
      } else {
        ElMessage.error(res.msg || '加载宣传数据失败');
      }
    } catch (error) {
      console.error('Failed to load propagate data:', error);
      ElMessage.error('加载宣传数据失败，请检查网络连接');
    }
  };
  
  const load = async () => {
    data.loading = true;
    try {
      console.log('加载轮播图数据，参数:', {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        propagateTitle: data.propagateTitle || undefined
      });
      
      const res = await request.get('/sideshow/selectPage', {
        params: {
          pageNum: data.pageNum,
          pageSize: data.pageSize,
          propagateTitle: data.propagateTitle || undefined
        }
      });
      
      console.log('轮播图数据响应:', res);
      
      if (res.code === '200') {
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
        
        console.log('加载到轮播图数据:', data.tableData.length, '条');
        if (data.tableData.length > 0) {
          console.log('第一条数据:', data.tableData[0]);
        }
      } else {
        ElMessage.error(res.msg || '加载轮播图数据失败');
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
    data.pageNum = 1; // 切换每页显示数量时，重置为第一页
    load();
  };
  
  const handleAdd = () => {
    data.form = {};
    fileList.value = [];
    data.formVisible = true;
  };
  
  const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row));
    
    // 如果有图片，设置文件列表
    fileList.value = data.form.img ? [{
      name: '轮播图',
      url: data.form.img
    }] : [];
    
    data.formVisible = true;
  };
  
  const beforeImageUpload = (file) => {
    const isImage = file.type.startsWith('image/');
    const isLt2M = file.size / 1024 / 1024 < 2;
  
    if (!isImage) {
      ElMessage.error('上传轮播图只能是图片格式!');
      return false;
    }
    if (!isLt2M) {
      ElMessage.error('上传轮播图大小不能超过 2MB!');
      return false;
    }
    return true;
  };
  
  const handleFileUpload = (res) => {
    console.log('Upload response:', res);
    if (res.code === '200') {
      data.form.img = res.data;
      ElMessage.success('图片上传成功');
    } else {
      ElMessage.error(res.msg || '图片上传失败');
    }
  };
  
  const handleUploadError = (err) => {
    console.error('Upload error:', err);
    ElMessage.error('上传失败: ' + (err.message || '未知错误'));
  };
  
  const save = async () => {
    formRef.value.validate(async (valid) => {
      if (valid) {
        data.submitting = true;
        try {
          // 确保图片URL已正确设置
          if (!data.form.img) {
            ElMessage.error('请上传轮播图');
            data.submitting = false;
            return;
          }
          
          // 打印表单数据，用于调试
          console.log('保存轮播图数据:', {
            id: data.form.id || '新增',
            propagateId: data.form.propagateId,
            propagateTitle: data.propagateData.find(p => p.id === data.form.propagateId)?.title || '未知',
            img: data.form.img
          });
          
          const url = data.form.id ? '/sideshow/update' : '/sideshow/add';
          const method = data.form.id ? 'put' : 'post';
          
          const res = await request[method](url, data.form);
          console.log('保存响应:', res);
          
          if (res.code === '200') {
            ElMessage.success('保存成功');
            data.formVisible = false;
            
            // 重新加载数据，确保显示最新结果
            console.log('重新加载数据...');
            await load();
          } else {
            ElMessage.error(res.msg || '保存失败');
          }
        } catch (error) {
          console.error('Save error:', error);
          ElMessage.error('保存失败: ' + (error.message || '未知错误'));
        } finally {
          data.submitting = false;
        }
      }
    });
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
      
      const res = await request.delete('/sideshow/delete/' + id);
      
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
  
  const delBatch = async () => {
    if (!data.ids.length) {
      ElMessage.warning("请选择要删除的数据");
      return;
    }
    
    try {
      await ElMessageBox.confirm(
        `确定要删除选中的 ${data.ids.length} 张轮播图吗？删除后数据无法恢复。`, 
        '批量删除确认', 
        { 
          type: 'warning',
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          confirmButtonClass: 'el-button--danger'
        }
      );
      
      const res = await request.delete("/sideshow/delete/batch", { data: data.ids });
      
      if (res.code === '200') {
        ElMessage.success(`成功删除 ${data.ids.length} 张轮播图`);
        // If we might have deleted all items on current page, reset to page 1
        if (data.ids.length >= data.tableData.length) {
          data.pageNum = 1;
        }
        load();
      } else {
        ElMessage.error(res.msg || '批量删除失败');
      }
    } catch (error) {
      // User canceled the deletion
      if (error !== 'cancel' && error?.message) {
        console.error('Batch delete error:', error);
        ElMessage.error('批量删除失败: ' + error.message);
      }
    }
  };
  
  const handleSelectionChange = (rows) => {
    data.ids = rows.map(v => v.id);
  };
  
  const reset = () => {
    data.propagateTitle = '';
    data.pageNum = 1;
    load();
  };
  
  // Load data when component is mounted
  onMounted(() => {
    // 测试上传URL是否正确
    console.log('Testing upload URL:', `${baseUrl}/files/upload`);
    
    // 测试API连接
    request.get('/test/company-info')
      .then(res => {
        console.log('API connection test:', res);
      })
      .catch(err => {
        console.error('API connection test failed:', err);
      });
    
    // 加载数据
    load();
    loadPropagateData();
  });
  </script>
  
  <style scoped>
  .sideshow-container {
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
  
  /* Ensure content area doesn't affect sidebar */
  :deep(.el-main) {
    overflow-y: hidden;
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