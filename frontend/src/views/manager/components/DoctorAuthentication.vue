<template>
  <div class="doctor-auth-container">
    <!-- Authentication Card -->
    <div class="bg-white rounded-lg shadow-md p-6 max-w-2xl mx-auto">
      <!-- Warning Message -->
      <div class="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
        <div class="flex">
          <div class="flex-shrink-0">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
          </div>
          <div class="ml-3">
            <p class="text-sm text-red-700">
              一旦认证信息修改，您的状态就会变为待审批状态，管理员审批通过前将无法使用医生功能
            </p>
          </div>
        </div>
      </div>

      <!-- Authentication Form -->
      <el-form 
        ref="userRef" 
        :rules="data.rules" 
        :model="data.user" 
        label-width="100px"
        class="p-2"
        v-loading="data.loading"
      >
        <el-form-item prop="avatar" label="你的头像" required>
          <div class="flex flex-col items-center">
            <el-upload
              :action="baseUrl + '/files/upload'"
              :headers="headers"
              :on-success="handleFileUpload"
              :before-upload="beforeAvatarUpload"
              :show-file-list="false"
              class="avatar-uploader"
            >
              <div v-if="data.user.avatar" class="relative">
                <img :src="data.user.avatar" class="avatar object-cover" />
                <div class="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity duration-200 rounded-md">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                    <polyline points="17 8 12 3 7 8"></polyline>
                    <line x1="12" y1="3" x2="12" y2="15"></line>
                  </svg>
                </div>
              </div>
              <div v-else class="avatar-uploader-icon flex flex-col items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400 mb-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <span class="text-xs text-gray-500">点击上传头像</span>
              </div>
            </el-upload>
            <p class="text-xs text-gray-500 mt-2">
              建议尺寸: 120x120 像素，支持 JPG、PNG 格式
            </p>
          </div>
        </el-form-item>

        <el-form-item prop="name" label="真实姓名" required>
          <el-input 
            v-model="data.user.name" 
            placeholder="请输入真实姓名"
            maxlength="20"
            show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item prop="code" label="身份证号" required>
          <el-input 
            v-model="data.user.code" 
            placeholder="请输入身份证号"
            maxlength="18"
            show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item prop="content" label="你的简介" required>
          <el-input 
            type="textarea" 
            :rows="5" 
            v-model="data.user.content" 
            placeholder="请输入你的简介，包括专业背景、工作经验等"
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>

          <el-form-item prop="seniority" label="你的工龄" required>
          <el-input-number  
            v-model="data.user.seniority" 
            :min="1" 
            :max="50"
            placeholder="你的工龄" 
            class="w-40"
          />
          <span class="ml-2 text-gray-500 text-sm">年</span>
        </el-form-item>

        <el-form-item prop="status" label="审批状态">
          <el-tag 
            v-if="data.user.status === '待审批'" 
            type="warning"
            effect="light"
            class="px-3 py-1"
          >
            {{ data.user.status }}
          </el-tag>
          <el-tag 
            v-else-if="data.user.status === '审批通过'" 
            type="success"
            effect="light"
            class="px-3 py-1"
          >
            {{ data.user.status }}
          </el-tag>
          <el-tag 
            v-else-if="data.user.status === '审批拒绝'" 
            type="danger"
            effect="light"
            class="px-3 py-1"
          >
            {{ data.user.status }}
          </el-tag>
          <el-tag 
            v-else 
            type="info"
            effect="light"
            class="px-3 py-1"
          >
            未提交
          </el-tag>
        </el-form-item>

        <div class="flex justify-center mt-6">
          <el-button 
            type="primary" 
            @click="update"
            :loading="data.submitting"
            class="w-32 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            提 交
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const baseUrl = import.meta.env.VITE_APP_BASE_API || '';
const userRef = ref();

// 获取当前用户信息和token
const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
const headers = { token: user.token };

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  loading: false, // 是否显示加载动画
  submitting: false, // 是否显示提交按钮的加载动画
  rules: {
    avatar: [
      { required: true, message: '请上传医生头像', trigger: 'change' },
    ],
    name: [
      { required: true, message: '请填写真实姓名', trigger: 'blur' },
      { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    code: [
      { required: true, message: '请填写身份证号', trigger: 'blur' },
      { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码', trigger: 'blur' }
    ],
    content: [
      { required: true, message: '请填写医生简介', trigger: 'blur' },
      { min: 10, max: 500, message: '简介长度在 10 到 500 个字符', trigger: 'blur' }
    ],
    seniority: [
      { required: true, message: '请填写医生工龄', trigger: 'change' },
      { type: 'number', min: 1, message: '工龄必须大于0', trigger: 'change' }
    ],
  }
});

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('头像只能是图片格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!');
    return false;
  }
  
  // 检查并裁剪图片
  return new Promise((resolve) => {
    const img = new Image();
    img.onload = () => {
      // 如果图片尺寸超过建议尺寸，进行裁剪
      if (img.width > 120 || img.height > 120) {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        
        // 设置画布尺寸为目标尺寸
        canvas.width = 120;
        canvas.height = 120;
        
        // 计算裁剪区域（居中裁剪）
        const size = Math.min(img.width, img.height);
        const startX = (img.width - size) / 2;
        const startY = (img.height - size) / 2;
        
        // 在画布上绘制裁剪后的图片
        ctx.drawImage(
          img,
          startX, startY, size, size,  // 源图像的裁剪区域
          0, 0, 120, 120               // 目标画布区域
        );
        
        // 将画布转换为Blob对象
        canvas.toBlob((blob) => {
          // 创建新的File对象
          const croppedFile = new File([blob], file.name, {
            type: file.type,
            lastModified: Date.now()
          });
          
          ElMessage.info('图片已自动裁剪至合适尺寸');
          resolve(croppedFile);
        }, file.type);
      } else {
        // 如果图片尺寸已经合适，直接使用原图
        resolve(file);
      }
    };
    
    img.src = URL.createObjectURL(file);
  });
};

const handleFileUpload = (res) => {
  if (res.code === '200') {
    // 确保avatar是字符串类型
    if (typeof res.data === 'object') {
      // 如果返回的是对象，尝试获取URL字符串
      data.user.avatar = res.data.url || res.data.path || '';
      console.log('Avatar URL extracted from object:', data.user.avatar);
    } else {
      // 如果已经是字符串，直接使用
      data.user.avatar = res.data;
    }
    ElMessage.success('头像上传成功');
  } else {
    ElMessage.error(res.msg || '头像上传失败');
  }
};

const emit = defineEmits(['updateUser']);

const update = async () => {
  if (data.user.role !== 'DOCTOR') {
    ElMessage.warning('只有医生角色可以提交认证信息');
    return;
  }
  
  try {
    await userRef.value.validate();
    
    data.submitting = true;
    
    // 确保avatar是字符串
    if (typeof data.user.avatar === 'object') {
      data.user.avatar = data.user.avatar.url || data.user.avatar.path || '';
    }
    
    const res = await request.put('/doctor/submit', data.user);
    
    if (res.code === '200') {
      ElMessage({
        type: 'success',
        message: '提交成功，等待管理员审批',
        duration: 3000
      });
      
      // 更新本地存储的用户信息
      localStorage.setItem('xm-user', JSON.stringify(data.user));
      emit('updateUser', data.user);
    } else {
      ElMessage.error(res.msg || '提交失败');
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
</script>

<style scoped>
.doctor-auth-container {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 1rem;
  background-color: #f5f7fa;
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  margin: 0 auto;
}

.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 6px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader .el-upload:hover {
  border-color: #2A5C8A;
}

.avatar-uploader-icon {
  width: 120px;
  height: 120px;
  background-color: #f5f7fa;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  transition: all 0.3s;
}

.avatar-uploader-icon:hover {
  border-color: #2A5C8A;
  background-color: #f0f7ff;
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