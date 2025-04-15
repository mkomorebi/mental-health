<template>
    <div class="max-w-2xl mx-auto p-6 bg-white rounded-2xl shadow-md">
      <el-form ref="user" :model="data.user" label-width="80px">
        <el-form-item prop="avatar" label="头像">
          <el-upload
            :action="baseUrl + '/files/upload'"
            :on-success="handleFileUpload"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            class="avatar-uploader"
          >
            <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar rounded-full" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="mt-2 text-xs text-gray-500">
            点击上传头像，支持JPG、PNG格式，大小不超过2MB
          </div>
        </el-form-item>
  
        <el-form-item prop="username" label="用户名">
          <el-input disabled v-model="data.user.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
  
        <el-form-item prop="name" label="姓名">
          <el-input v-model="data.user.name" placeholder="请输入姓名" clearable></el-input>
        </el-form-item>
  
        <el-form-item prop="phone" label="电话">
          <el-input v-model="data.user.phone" placeholder="请输入电话" clearable></el-input>
        </el-form-item>
  
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="data.user.email" placeholder="请输入邮箱" clearable></el-input>
        </el-form-item>
  
        <div class="text-center mt-6">
          <el-button type="primary" @click="update" class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]">保 存</el-button>
        </div>
      </el-form>
    </div>
  </template>
  
  <script setup>
  import { reactive } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage } from "element-plus";
  import { Plus } from '@element-plus/icons-vue';
  
  const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
  
  const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}')
  })
  
  const beforeAvatarUpload = (file) => {
    const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
    const isLt2M = file.size / 1024 / 1024 < 2;

    if (!isImage) {
      ElMessage.error('头像只能是JPG或PNG格式!');
      return false;
    }
    if (!isLt2M) {
      ElMessage.error('头像大小不能超过2MB!');
      return false;
    }
    return true;
  }
  
  const handleFileUpload = (res) => {
    if (res.code === '200') {
      data.user.avatar = typeof res.data === 'string' ? res.data : res.data.url || res.data.path || '';
      ElMessage.success('头像上传成功');
    } else {
      ElMessage.error(res.msg || '头像上传失败');
    }
  }
  
  const emit = defineEmits(['updateUser'])
  
  const update = () => {
    const urlMap = {
      'ADMIN': '/admin/update',
      'DOCTOR': '/doctor/update'
    }
    const url = urlMap[data.user.role]
    if (url) {
      const userData = {...data.user};
      if (userData.avatar && typeof userData.avatar !== 'string') {
        userData.avatar = userData.avatar.url || userData.avatar.path || '';
      }
      
      request.put(url, userData).then(res => {
        if (res.code === '200') {
          ElMessage.success('保存成功')
          localStorage.setItem('xm-user', JSON.stringify(userData))
          emit('updateUser')
        } else {
          ElMessage.error(res.msg)
        }
      }).catch(err => {
        console.error('更新用户信息失败:', err);
        ElMessage.error('更新失败，请检查网络连接')
      })
    }
  }
  </script>
  
  <style scoped>
  .avatar-uploader {
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .avatar-uploader .avatar {
    width: 120px;
    height: 120px;
    display: block;
    object-fit: cover;
  }
  .avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 50%;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
  }
  
  .el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
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