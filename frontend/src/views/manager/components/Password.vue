<template>
    <div class="card-container">
      <div class="card">
        <el-form ref="formRef" :rules="data.rules" :model="data.user" label-width="90px" class="form">
          <el-form-item label="原密码" prop="password">
            <el-input v-model="data.user.password" placeholder="请输入原密码" show-password clearable />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="data.user.newPassword" 
              placeholder="请输入新密码" 
              show-password 
              clearable 
              @input="evaluateStrength"
            />
            <div v-if="passwordStrength" class="password-strength">
              <span>密码强度：</span>
              <span :class="['strength-label', strengthColor]">{{ passwordStrength }}</span>
            </div>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="data.user.confirmPassword" placeholder="请确认新密码" show-password clearable />
          </el-form-item>
          <div class="form-footer">
            <el-button type="primary" :loading="loading" @click="updatePassword">保存</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { reactive, ref, computed } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage } from "element-plus";
  import router from "@/router/index.js";
  
  const formRef = ref();
  const loading = ref(false);
  const passwordStrength = ref("");
  const strengthColor = computed(() => {
    switch (passwordStrength.value) {
      case "弱": return "weak";
      case "中": return "medium";
      case "强": return "strong";
      case "非常强": return "very-strong";
      default: return "";
    }
  });
  
  const validatePass = (rule, value, callback) => {
    if (!value) {
      callback(new Error("请确认密码"));
    } else {
      if (value !== data.user.newPassword) {
        callback(new Error("确认密码跟新密码不一致!"));
      } else {
        callback();
      }
    }
  };
  
  const data = reactive({
    user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
    rules: {
      password: [
        { required: true, message: "请输入原密码", trigger: "blur" },
      ],
      newPassword: [
        { required: true, message: "请输入新密码", trigger: "blur" },
      ],
      confirmPassword: [
        { validator: validatePass, trigger: "blur" },
      ],
    },
  });
  
  const updatePassword = () => {
    formRef.value.validate((valid) => {
      if (valid) {
        loading.value = true;
        request.put("/updatePassword", data.user).then((res) => {
          loading.value = false;
          if (res.code === "200") {
            ElMessage.success("保存成功");
            logout();
          } else {
            ElMessage.error(res.msg);
          }
        }).catch(() => {
          loading.value = false;
        });
      }
    });
  };
  
  const logout = () => {
    localStorage.removeItem("xm-user");
    router.push("/login");
  };
  
  const evaluateStrength = (val) => {
    if (!val) {
      passwordStrength.value = "";
      return;
    }
    let score = 0;
    if (val.length >= 8) score++;
    if (/[A-Z]/.test(val)) score++;
    if (/[0-9]/.test(val)) score++;
    if (/[^A-Za-z0-9]/.test(val)) score++;
  
    passwordStrength.value = ["弱", "中", "强", "非常强"][score - 1] || "弱";
  };
  </script>
  
  <style scoped>
  .card-container {
    display: flex;
    justify-content: center;
    margin-top: 40px;
  }
  
  .card {
    width: 100%;
    max-width: 500px;
    background-color: #fff;
    padding: 30px;
    border-radius: 16px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }
  
  .form-footer {
    text-align: center;
    margin-top: 20px;
  }
  
  .form :deep(.el-form-item) {
    margin-bottom: 20px;
  }
  
  .password-strength {
    margin-top: 6px;
    font-size: 13px;
  }
  
  .strength-label {
    font-weight: bold;
    padding-left: 4px;
  }
  
  .weak {
    color: #f56c6c;
  }
  
  .medium {
    color: #e6a23c;
  }
  
  .strong {
    color: #67c23a;
  }
  
  .very-strong {
    color: #409eff;
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
  