<template>
  <div class="bg-gray-50 py-10 min-h-screen">
    <div class="max-w-6xl mx-auto px-4">
      <div class="mb-8 text-center">
        <h2 class="text-3xl font-bold text-[#2A5C8A] mb-2">提交您的反馈信息</h2>
        <p class="text-gray-600">我们重视您的每一条建议</p>
      </div>
      
      <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-100">
        <!-- 反馈类型选择 -->
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            反馈类型 <span class="text-red-500">*</span>
          </label>
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <div 
              v-for="(type, index) in feedbackTypes" 
              :key="index"
              @click="selectFeedbackType(type.value)"
              :class="[
                'cursor-pointer border rounded-md p-3 text-center transition-all duration-200',
                data.form.type === type.value 
                  ? 'border-blue-500 bg-blue-50 text-blue-700 shadow-sm' 
                  : 'border-gray-200 hover:border-blue-300 hover:bg-blue-50'
              ]"
            >
              <div class="flex flex-col items-center">
                <i :class="type.icon" class="text-xl mb-1"></i>
                <span class="text-sm">{{ type.label }}</span>
              </div>
            </div>
          </div>
          <p v-if="errors.type" class="mt-1 text-sm text-red-600">{{ errors.type }}</p>
        </div>

        <form @submit.prevent="submit" class="space-y-6">
          <!-- 反馈问题 -->
          <div>
            <label for="question" class="block text-sm font-medium text-gray-700 mb-1">
              反馈问题 <span class="text-red-500">*</span>
            </label>
            <textarea
              id="question"
              v-model="data.form.question"
              rows="4"
              maxlength="100"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent transition duration-150"
              placeholder="请简要描述您遇到的问题或建议"
            ></textarea>
            <div class="text-xs text-gray-500 text-right mt-1">
              {{ data.form.question ? data.form.question.length : 0 }}/100
            </div>
            <p v-if="errors.question" class="mt-1 text-sm text-red-600">{{ errors.question }}</p>
          </div>

          <!-- 我的想法 -->
          <div>
            <label for="content" class="block text-sm font-medium text-gray-700 mb-1">
              我的想法 <span class="text-red-500">*</span>
            </label>
            <textarea
              id="content"
              v-model="data.form.content"
              rows="5"
              maxlength="300"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent transition duration-150"
              placeholder="请详细描述您对上述问题的想法或解决方案"
            ></textarea>
            <div class="text-xs text-gray-500 text-right mt-1">
              {{ data.form.content ? data.form.content.length : 0 }}/300
            </div>
            <p v-if="errors.content" class="mt-1 text-sm text-red-600">{{ errors.content }}</p>
          </div>

          <!-- 上传图片 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              上传截图 <span class="text-gray-500 text-xs">(可选，最多3张)</span>
            </label>
            <div class="flex flex-wrap gap-3">
              <!-- 已上传图片预览 -->
              <div v-for="(img, index) in data.form.images" :key="index" class="relative w-24 h-24">
                <img 
                  v-if="!img.loadError" 
                  :src="img.previewUrl || img.base64 || img.url" 
                  class="w-full h-full object-cover rounded-md border border-gray-300" 
                  @error="handleImageError(index)" 
                  alt="上传图片"
                />
                <button 
                  type="button" 
                  @click="removeImage(index)" 
                  class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs"
                >
                  ×
                </button>
              </div>
              
              <!-- 上传按钮 -->
              <div 
                v-if="data.form.images.length < 3" 
                @click="triggerImageUpload"
                class="w-24 h-24 border-2 border-dashed border-gray-300 rounded-md flex flex-col items-center justify-center cursor-pointer hover:border-[#2A5C8A] transition-colors"
              >
                <i class="el-icon-plus text-gray-400 text-xl"></i>
                <span class="text-xs text-gray-500 mt-1">添加图片</span>
              </div>
              <input 
                ref="fileInput" 
                type="file" 
                accept="image/*" 
                class="hidden" 
                @change="handleImageUpload" 
              />
            </div>
          </div>

          <!-- 紧急程度 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">紧急程度</label>
            <div class="flex items-center">
              <span class="text-sm text-gray-500 mr-2">一般</span>
              <input 
                type="range" 
                v-model="data.form.urgency" 
                min="0" 
                max="100" 
                step="10"
                class="w-full mt-2 appearance-none h-2 bg-gray-200 rounded-lg cursor-pointer"
                @input="updateUrgencyLabel"
              />
              <span class="text-sm text-gray-500 ml-2">{{ data.form.urgency }}%</span>
              <span class="text-sm text-gray-500 ml-2">紧急</span>
            </div>
          </div>

          <!-- 提交按钮 -->
          <div class="flex justify-center pt-4">
            <button
              type="submit"
              :disabled="isSubmitting"
              class="py-3 px-6 bg-[#4a9be6] text-white rounded-md hover:bg-[#7c97ea] transition-all duration-200 shadow-lg transform hover:scale-105 hover:-translate-y-0.5 disabled:opacity-70 disabled:cursor-not-allowed"
            >
              <span v-if="isSubmitting" class="flex items-center">
                <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                提交中...
              </span>
              <span v-else>提交反馈</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const errors = reactive({
  type: "",
  question: "",
  content: ""
});

const data = reactive({
  form: {
    type: "",
    question: "",
    content: "",
    images: [],
    imageUrls: null,
    urgency: 50
  }
});

const isSubmitting = ref(false);
const fileInput = ref(null);

const feedbackTypes = [
  { label: "功能建议", value: "feature", icon: "el-icon-star-on" },
  { label: "问题反馈", value: "bug", icon: "el-icon-warning" },
  { label: "使用咨询", value: "question", icon: "el-icon-question" },
  { label: "其他", value: "other", icon: "el-icon-more" }
];

const selectFeedbackType = (type) => {
  data.form.type = type;
  errors.type = "";
};

const triggerImageUpload = () => {
  fileInput.value.click();
};

const handleImageUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/jpg'];
  if (!validTypes.includes(file.type)) {
    ElMessage.error("只支持JPG、PNG和GIF格式的图片");
    return;
  }
  
  // 验证文件大小 (5MB限制)
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error("图片大小不能超过5MB");
    return;
  }
  
  // 创建FormData对象
  const formData = new FormData();
  formData.append('file', file);
  
  // 生成唯一ID来标识这次上传
  const loadingImg = {
    uploadId: Date.now().toString(),
    loading: true
  };
  
  try {
    // 显示上传中状态
    data.form.images.push(loadingImg);
    
    const res = await request.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    
    console.log("Upload response:", res);
    
    // 检查响应状态和数据
    if (res.code === '200') {
      const uploadedUrl = res.data?.url || res.data; // 兼容不同的响应格式
      
      if (!uploadedUrl) {
        throw new Error('上传成功但未获取到图片URL');
      }
      
      // 找到loading状态的图片并更新
      const index = data.form.images.findIndex(img => img.uploadId === loadingImg.uploadId);
      if (index !== -1) {
        data.form.images[index] = {
          uploadId: loadingImg.uploadId,
          url: uploadedUrl,
          loading: false
        };
        
        // 更新form中的imageUrls字段，将所有图片URL用逗号连接
        data.form.imageUrls = data.form.images
          .filter(img => img.url && !img.loading)
          .map(img => img.url)
          .join(',');
          
        console.log("Updated imageUrls:", data.form.imageUrls);
      }
    } else {
      throw new Error(res.msg || '上传失败');
    }
  } catch (error) {
    console.error("图片上传错误:", error);
    ElMessage.error(error.message || "图片上传失败，请稍后重试");
    
    // 移除加载中的图片
    const index = data.form.images.findIndex(img => img.uploadId === loadingImg.uploadId);
    if (index !== -1) {
      data.form.images.splice(index, 1);
    }
  } finally {
    // 清空input，允许重复选择同一文件
    event.target.value = '';
  }
};

const removeImage = (index) => {
  // 释放本地预览URL
  if (data.form.images[index].previewUrl) {
    URL.revokeObjectURL(data.form.images[index].previewUrl);
  }
  data.form.images.splice(index, 1);
  
  // 更新form中的imageUrls字段
  data.form.imageUrls = data.form.images
    .filter(img => img.url && !img.loading)
    .map(img => img.url)
    .join(',');
    
  console.log("Updated imageUrls after removal:", data.form.imageUrls);
};

const handleImageError = (index) => {
  console.error("图片加载失败:", data.form.images[index].url);
  data.form.images[index].loadError = true;
};

const validateForm = () => {
  let isValid = true;
  
  // 验证反馈类型
  if (!data.form.type) {
    errors.type = "请选择反馈类型";
    isValid = false;
  } else {
    errors.type = "";
  }
  
  // 验证反馈问题
  if (!data.form.question.trim()) {
    errors.question = "请输入您反馈的问题";
    isValid = false;
  } else {
    errors.question = "";
  }

  // 验证想法内容
  if (!data.form.content.trim()) {
    errors.content = "请输入您对反馈问题的想法";
    isValid = false;
  } else {
    errors.content = "";
  }

  return isValid;
};

const submit = async () => {
  if (!validateForm()) return;

  isSubmitting.value = true;

  try {
    // 确保imageUrls字段已正确设置
    const imageUrls = data.form.images
      .filter(img => img.url && !img.loading)
      .map(img => img.url)
      .join(',');
      
    const formData = {
      ...data.form,
      imageUrls: imageUrls || null // 如果没有图片则设为null
    };
    
    // 删除不需要的字段
    delete formData.images;
    
    console.log("Submitting feedback with images:", formData.imageUrls);
    
    const res = await request.post('/feedback/add', formData);
    if (res.code === '200') {
      ElMessage.success({
        message: '反馈成功，等待管理员回复，您可以在右上角"我的反馈"查看具体内容',
        duration: 5000
      });
      // 重置表单
      resetForm();
    }
  } catch (error) {
    console.error("提交反馈失败:", error);
    ElMessage.error("提交反馈失败，请稍后再试: " + (error.message || ""));
  } finally {
    isSubmitting.value = false;
  }
};

const resetForm = () => {
  data.form = { 
    type: "", 
    question: "", 
    content: "", 
    images: [],
    imageUrls: null,
    urgency: 50
  };
  errors.type = "";
  errors.question = "";
  errors.content = "";
};

const debugImages = () => {
  data.form.images.forEach((img, index) => {
    console.log(`图片 ${index+1}:`);
    console.log("- URL:", img.url);
    console.log("- Base64 (前50字符):", img.base64 ? img.base64.substring(0, 50) + "..." : "无");
    console.log("- 本地预览URL:", img.previewUrl || "无");
    
    // 测试URL可访问性
    if (img.url) {
      fetch(img.url)
        .then(response => {
          console.log(`- URL状态: ${response.status} ${response.statusText}`);
          return response.blob();
        })
        .then(blob => {
          console.log(`- URL内容类型: ${blob.type}`);
        })
        .catch(error => {
          console.error(`- URL访问错误: ${error.message}`);
        });
    }
  });
};

// 在上传开始时设置超时处理
const timeoutId = setTimeout(() => {
  // 查找所有加载中的图片并标记为错误
  data.form.images.forEach((img, idx) => {
    if (img.loading) {
      console.warn("图片上传超时:", idx);
      img.loading = false;
      img.loadError = true;
    }
  });
}, 30000); // 30秒超时

// 在上传完成时清除超时
clearTimeout(timeoutId);

const updateUrgencyLabel = () => {
  // This function can be used to update any additional UI elements based on urgency if needed
};
</script>

<style scoped>
/* 自定义样式 */
.w-full button {
  transition: all 0.2s ease-in-out;
}

.w-full button:hover:not(:disabled) {
  transform: translateY(-1px);
}

.w-full textarea {
  min-height: 100px;
  transition: border-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

/* 自定义滑块样式 */
input[type="range"] {
  -webkit-appearance: none;
  height: 6px;
  background: #e5e7eb; /* Tailwind gray-300 */
  border-radius: 5px;
  outline: none;
}

input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 18px;
  height: 18px;
  background: #3B82F6; /* Tailwind blue-500 */
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

input[type="range"]::-moz-range-thumb {
  width: 18px;
  height: 18px;
  background: #3B82F6; /* Tailwind blue-500 */
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}
</style>