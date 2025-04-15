<template>
    <div class="user-container">
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
              v-model="data.name" 
              placeholder="请输入姓名查询" 
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
            新增用户
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
          <el-button 
            type="success" 
            @click="downLoad"
           class="bg-green-500 hover:bg-green-600 border-green-500 hover:border-green-600 text-white disabled:bg-green-300 disabled:border-green-300"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7 10 12 15 17 10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            导出
          </el-button>
          <el-upload
            class="inline-block"
            action="http://localhost:9090/user/import"
            :headers="headers"
            :limit="1"
            :file-list="fileList"
            :on-success="handleImportSuccess"
            :on-error="handleImportError"
            :show-file-list="false"
          >
            <el-button 
              type="primary"
              class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="17 8 12 3 7 8"></polyline>
                <line x1="12" y1="3" x2="12" y2="15"></line>
              </svg>
              导入
            </el-button>
          </el-upload>
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
          <el-table-column prop="username" label="账号" min-width="120" />
          <el-table-column prop="avatar" label="头像" width="80" align="center">
            <template #default="scope">
              <el-image 
                v-if="scope.row.avatar"
                class="w-10 h-10 rounded-full object-cover mx-auto"
                :src="scope.row.avatar" 
                :preview-src-list="[scope.row.avatar]" 
                preview-teleported
                fit="cover"
              />
              <div v-else class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center mx-auto">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="姓名" min-width="100" />
          <el-table-column prop="role" label="角色" min-width="100">
            <template #default="scope">
              <el-tag 
                v-if="scope.row.role === 'ADMIN'" 
                type="danger" 
                effect="plain"
              >
                管理员
              </el-tag>
              <el-tag 
                v-else-if="scope.row.role === 'USER'" 
                type="primary" 
                effect="plain"
              >
                普通用户
              </el-tag>
              <el-tag 
                v-else 
                type="info" 
                effect="plain"
              >
                {{ scope.row.role }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="电话" min-width="120" />
          <el-table-column prop="departmentName" label="所属部门" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
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
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
          <p class="mt-2 text-gray-500">暂无用户数据</p>
          <el-button 
            type="primary" 
            @click="handleAdd" 
            class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            添加第一个用户
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
        :title="data.form.id ? '编辑用户' : '新增用户'" 
        width="500px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <el-form 
          ref="formRef" 
          :model="data.form" 
          :rules="rules" 
          label-width="80px" 
          class="p-4"
        >
          <div class="mb-6 flex justify-center">
            <el-upload
              class="avatar-uploader"
              :action="baseUrl + '/files/upload'"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleFileUpload"
              :before-upload="beforeAvatarUpload"
            >
              <div class="relative group">
                <div v-if="data.form.avatar" class="w-24 h-24 rounded-full overflow-hidden border-2 border-gray-200">
                  <img :src="data.form.avatar" class="w-full h-full object-cover" />
                </div>
                <div v-else class="w-24 h-24 rounded-full bg-gray-100 flex items-center justify-center border-2 border-dashed border-gray-300">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                  </svg>
                </div>
                <div class="absolute inset-0 bg-black bg-opacity-40 rounded-full opacity-0 group-hover:opacity-100 flex items-center justify-center transition-opacity duration-200">
                  <span class="text-white text-xs">点击上传头像</span>
                </div>
              </div>
            </el-upload>
          </div>
          
          <el-form-item prop="username" label="用户名" required>
            <el-input 
              v-model="data.form.username" 
              placeholder="请输入用户名"
              maxlength="20"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="name" label="姓名" required>
            <el-input 
              v-model="data.form.name" 
              placeholder="请输入姓名"
              maxlength="20"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="phone" label="电话">
            <el-input 
              v-model="data.form.phone" 
              placeholder="请输入电话"
              maxlength="11"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="email" label="邮箱">
            <el-input 
              v-model="data.form.email" 
              placeholder="请输入邮箱"
              maxlength="50"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="departmentId" label="部门" required>
            <el-select 
              v-model="data.form.departmentId" 
              placeholder="请选择部门"
              class="w-full"
              filterable
            >
              <el-option 
                v-for="item in data.departmentList" 
                :key="item.id" 
                :label="item.name" 
                :value="item.id"
              ></el-option>
            </el-select>
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
  import { saveAs } from 'file-saver';
  import { reactive, ref, nextTick } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage, ElMessageBox } from "element-plus";
  import zhCn from 'element-plus/es/locale/lang/zh-cn';
  
  const formRef = ref(null);
  
  const rules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    name: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
      { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    phone: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ],
    email: [
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    departmentId: [
      { required: true, message: '请选择部门', trigger: 'change' }
    ]
  };
  
  let user = JSON.parse(localStorage.getItem("xm-user") || '{}');
  const headers = { token: user.token };
  const baseUrl = import.meta.env.VITE_BASE_URL;
  const fileList = reactive([]);
  
  const data = reactive({
    formVisible: false,
    form: {},
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    name: '',
    ids: [],
    departmentList: [],
    loading: false,
    submitting: false
  });
  
  const load = async () => {
    data.loading = true;
    try {
      const [userRes, deptRes] = await Promise.all([
        request.get('/user/selectPage', {
          params: {
            pageNum: data.pageNum,
            pageSize: data.pageSize,
            name: data.name || undefined
          }
        }),
        request.get('/departments/selectAll')
      ]);
      
      if (userRes.code === '200') {
        data.tableData = userRes.data?.list || [];
        data.total = userRes.data?.total || 0;
      } else {
        ElMessage.error(userRes.msg || '加载用户数据失败');
      }
      
      if (deptRes.code === '200') {
        data.departmentList = deptRes.data?.list || [];
      } else {
        ElMessage.error(deptRes.msg || '加载部门数据失败');
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
  
  const downLoad = async () => {
    try {
      ElMessage.info('正在导出数据，请稍候...');
      
      const response = await request.get('/user/export', {
        timeout: 60000,
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        responseType: 'blob'
      });
      
      const blob = new Blob([response]);
      saveAs(blob, '用户信息.xlsx');
      
      ElMessage.success('导出成功');
    } catch (error) {
      console.error('Export error:', error);
      ElMessage.error('导出失败，请重试');
    }
  };
  
  const handleImportSuccess = (res) => {
    if (res.code === '200') {
      ElMessage.success('导入成功');
      load();
    } else {
      ElMessage.error(res.msg || '导入失败');
    }
  };
  
  const handleImportError = () => {
    ElMessage.error('导入失败，请检查文件格式');
  };
  
  const handleAdd = () => {
    data.form = {};
    data.formVisible = true;
    
    // Focus on first input after dialog is shown
    nextTick(() => {
      const firstInput = document.querySelector('.el-dialog input');
      if (firstInput) firstInput.focus();
    });
  };
  
  const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row));
    // Ensure departmentId is a number
    if (data.form.departmentId) {
      data.form.departmentId = parseInt(data.form.departmentId);
    }
    data.formVisible = true;
  };
  
  const beforeAvatarUpload = (file) => {
    const isImage = file.type.startsWith('image/');
    const isLt2M = file.size / 1024 / 1024 < 2;
  
    if (!isImage) {
      ElMessage.error('上传头像图片只能是图片格式!');
      return false;
    }
    if (!isLt2M) {
      ElMessage.error('上传头像图片大小不能超过 2MB!');
      return false;
    }
    return true;
  };
  
  const handleFileUpload = (res) => {
    if (res.code === '200') {
      data.form.avatar = res.data;
      ElMessage.success('头像上传成功');
    } else {
      ElMessage.error(res.msg || '头像上传失败');
    }
  };
  
  const save = async () => {
    if (!formRef.value) return;
    
    try {
      await formRef.value.validate();
      
      data.submitting = true;
      
      const isUpdate = !!data.form.id;
      const api = isUpdate ? '/user/update' : '/user/add';
      const method = isUpdate ? 'put' : 'post';
      
      const res = await request[method](api, data.form);
      
      if (res.code === '200') {
        ElMessage({
          type: 'success',
          message: isUpdate ? '用户更新成功' : '用户添加成功',
          duration: 2000
        });
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
      
      const res = await request.delete('/user/delete/' + id);
      
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
        `确定要删除选中的 ${data.ids.length} 个用户吗？删除后数据无法恢复。`, 
        '批量删除确认', 
        { 
          type: 'warning',
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          confirmButtonClass: 'el-button--danger'
        }
      );
      
      const res = await request.delete("/user/delete/batch", { data: data.ids });
      
      if (res.code === '200') {
        ElMessage.success(`成功删除 ${data.ids.length} 个用户`);
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
    data.name = '';
    data.pageNum = 1;
    load();
  };
  
  // Load data when component is mounted
  load();
  </script>
  
  <style scoped>
  .user-container {
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
  
  /* Ensure content area doesn't affect sidebar */
  :deep(.el-main) {
    overflow-y: hidden;
  }
  
  /* Avatar uploader styles */
  :deep(.avatar-uploader .el-upload) {
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  
  :deep(.avatar-uploader .el-upload:hover) {
    border-color: #2A5C8A;
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
  </style>