<template>
    <div class="bg-gray-50 py-10 min-h-screen">
      <div class="max-w-md mx-auto px-4">
        <!-- 标题 -->
        <div class="mb-8 text-center">
          <h1 class="text-3xl font-bold text-[#2A5C8A] mb-2">修改密码</h1>
          <p class="text-gray-600">定期更改密码有助于保护账户安全</p>
        </div>
        
        <!-- 密码修改表单 -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-8">
          <form @submit.prevent="updatePassword" class="space-y-6">
            <!-- 原密码 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">原密码</label>
              <div class="relative">
                <input
                  v-model="data.user.password"
                  :type="showPassword.current ? 'text' : 'password'"
                  placeholder="请输入当前使用的密码"
                  class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
                  required
                />
                <button
                  type="button"
                  @click="togglePasswordVisibility('current')"
                  class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-5 w-5"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path v-if="showPassword.current" d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                    <path v-else d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                    <circle v-if="!showPassword.current" cx="12" cy="12" r="3" />
                  </svg>
                </button>
              </div>
            </div>
            
            <!-- 新密码 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">新密码</label>
              <div class="relative">
                <input
                  v-model="data.user.newPassword"
                  :type="showPassword.new ? 'text' : 'password'"
                  placeholder="请输入新密码"
                  class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
                  required
                  @input="checkPasswordStrength"
                />
                <button
                  type="button"
                  @click="togglePasswordVisibility('new')"
                  class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-5 w-5"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path v-if="showPassword.new" d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                    <path v-else d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                    <circle v-if="!showPassword.new" cx="12" cy="12" r="3" />
                  </svg>
                </button>
              </div>
              <div v-if="data.user.newPassword" class="mt-2">
                <div class="flex items-center">
                  <div class="flex-1 h-2 bg-gray-200 rounded-full overflow-hidden">
                    <div 
                      class="h-full transition-all duration-300" 
                      :class="passwordStrengthClass"
                      :style="{ width: `${(passwordStrength.value / 4) * 100}%` }"
                    ></div>
                  </div>
                  <span class="text-xs ml-3 min-w-[60px]" :class="passwordStrengthTextClass">
                    {{ passwordStrengthText }}
                  </span>
                </div>
                <ul class="mt-2 text-xs text-gray-500 space-y-1">
                  <li class="flex items-center" :class="{'text-green-500': hasMinLength}">
                    <svg v-if="hasMinLength" xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                    </svg>
                    至少8个字符
                  </li>
                  <li class="flex items-center" :class="{'text-green-500': hasNumber}">
                    <svg v-if="hasNumber" xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                    </svg>
                    包含数字
                  </li>
                  <li class="flex items-center" :class="{'text-green-500': hasSpecialChar}">
                    <svg v-if="hasSpecialChar" xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                    </svg>
                    包含特殊字符 (!@#$%等)
                  </li>
                </ul>
              </div>
            </div>
            
            <!-- 确认密码 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">确认新密码</label>
              <div class="relative">
                <input
                  v-model="data.user.confirmPassword"
                  :type="showPassword.confirm ? 'text' : 'password'"
                  placeholder="请再次输入新密码"
                  class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#2A5C8A] focus:border-transparent"
                  required
                />
                <button
                  type="button"
                  @click="togglePasswordVisibility('confirm')"
                  class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-5 w-5"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path v-if="showPassword.confirm" d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                    <path v-else d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                    <circle v-if="!showPassword.confirm" cx="12" cy="12" r="3" />
                  </svg>
                </button>
              </div>
              <p v-if="showMismatchError" class="mt-1 text-sm text-red-600">两次输入的密码不一致</p>
            </div>
            
            <!-- 保存按钮 -->
            <div class="pt-4 flex justify-center">
              <button
                type="submit"
                class="py-3 px-6 bg-[#4a9be6] text-white rounded-md hover:bg-[#7c97ea] transition-all duration-200 shadow-lg transform hover:-translate-y-0.5 flex items-center justify-center w-full sm:w-auto sm:min-w-[200px] disabled:opacity-70 disabled:cursor-not-allowed"
                :disabled="isSaving"
              >
                <svg
                  v-if="isSaving"
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 mr-2 animate-spin"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M21 12a9 9 0 1 1-6.219-8.56" />
                </svg>
                <svg
                  v-else
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 mr-2"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z" />
                  <polyline points="17 21 17 13 7 13 7 21" />
                  <polyline points="7 3 7 8 15 8" />
                </svg>
                {{ isSaving ? '保存中...' : '保存更改' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { reactive, ref, computed } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage } from "element-plus";
  import router from "@/router/index.ts";
  
  const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  });
  
  const showPassword = reactive({
    current: false,
    new: false,
    confirm: false
  });
  
  const isSaving = ref(false);
  const passwordStrength = ref(0);
  
  const togglePasswordVisibility = (field) => {
    showPassword[field] = !showPassword[field];
  };
  
  const checkPasswordStrength = () => {
    const password = data.user.newPassword || '';
    let strength = 0;
    
    // 长度检查
    if (password.length >= 8) strength += 1;
    // 数字检查
    if (/\d/.test(password)) strength += 1;
    // 特殊字符检查
    if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength += 1;
    // 大小写检查
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength += 1;
    
    passwordStrength.value = strength;
  };
  
  const passwordStrengthClass = computed(() => {
    switch (passwordStrength.value) {
      case 0: return 'bg-gray-300';
      case 1: return 'bg-red-500';
      case 2: return 'bg-yellow-500';
      case 3: return 'bg-blue-500';
      case 4: return 'bg-emerald-500';
      default: return 'bg-gray-300';
    }
  });
  
  const passwordStrengthText = computed(() => {
    switch (passwordStrength.value) {
      case 0: return '非常弱';
      case 1: return '弱';
      case 2: return '中等';
      case 3: return '强';
      case 4: return '非常强';
      default: return '';
    }
  });
  
  const passwordStrengthTextClass = computed(() => {
    switch (passwordStrength.value) {
      case 0: return 'text-gray-500';
      case 1: return 'text-red-500';
      case 2: return 'text-yellow-500';
      case 3: return 'text-blue-500';
      case 4: return 'text-emerald-500';
      default: return 'text-gray-500';
    }
  });
  
  const hasMinLength = computed(() => (data.user.newPassword || '').length >= 8);
  const hasNumber = computed(() => /\d/.test(data.user.newPassword || ''));
  const hasSpecialChar = computed(() => /[!@#$%^&*(),.?":{}|<>]/.test(data.user.newPassword || ''));
  
  const showMismatchError = computed(() => {
    return data.user.confirmPassword && 
           data.user.newPassword && 
           data.user.confirmPassword !== data.user.newPassword;
  });
  
  const updatePassword = () => {
    if (!data.user.password) {
      ElMessage.warning('请输入原密码');
      return;
    }
    
    if (!data.user.newPassword) {
      ElMessage.warning('请输入新密码');
      return;
    }
    
    if (data.user.newPassword !== data.user.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致');
      return;
    }
    
    if (data.user.password === data.user.newPassword) {
      ElMessage.warning('新密码不能与原密码相同');
      return;
    }
    
    if (passwordStrength.value < 2) {
      ElMessage.warning('密码强度不足，请设置更复杂的密码');
      return;
    }
    
    isSaving.value = true;
    
    request.put('/updatePassword', data.user)
      .then(res => {
        if (res.code === '200') {
          ElMessage.success('密码修改成功，请重新登录');
          logout();
        } else {
          ElMessage.error(res.msg);
        }
      })
      .catch(err => {
        ElMessage.error('密码修改失败');
        console.error(err);
      })
      .finally(() => {
        isSaving.value = false;
      });
  };
  
  const logout = () => {
    localStorage.removeItem('xm-user');
    router.push('/login');
  };
  </script>
  
  <style>
  /* 禁用输入框的箭头 */
  input[type=number]::-webkit-inner-spin-button, 
  input[type=number]::-webkit-outer-spin-button { 
    -webkit-appearance: none; 
    margin: 0; 
  }
  input[type=number] {
    -moz-appearance: textfield;
  }
  
  /* 添加过渡效果 */
  .transition-all {
    transition-property: all;
    transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
    transition-duration: 300ms;
  }
  
  /* 按钮悬停效果 */
  button:not(:disabled):hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  }
  
  /* 按钮禁用状态 */
  button:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
  
  /* 输入框聚焦效果 */
  input:focus {
    box-shadow: 0 0 0 2px rgba(74, 156, 231, 0.1);
  }
  </style>