<template>
  <div class="doctor-container">
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
        
        <!-- 添加状态筛选下拉框 -->
        <div class="relative flex-grow max-w-xs">
          <el-select 
            v-model="data.status" 
            placeholder="审批状态" 
            clearable
            class="w-full"
          >
            <el-option label="待审批" value="待审批" />
            <el-option label="审批通过" value="审批通过" />
            <el-option label="审批拒绝" value="审批拒绝" />
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
          新增医生
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
        <el-table-column prop="username" label="账号" min-width="100" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="scope">
            <el-image 
              v-if="scope.row.avatar"
              style="width: 40px; height: 40px; border-radius: 50%;" 
              :src="scope.row.avatar" 
              :preview-src-list="[scope.row.avatar]" 
              preview-teleported
              fit="cover"
              class="mx-auto border border-gray-200"
            />
            <div v-else class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center mx-auto">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" min-width="80" />
        <el-table-column prop="seniority" label="工龄" width="80" />
        <el-table-column prop="content" label="简介" min-width="150" show-overflow-tooltip />
        <el-table-column prop="code" label="身份证号" min-width="150" show-overflow-tooltip />
        <el-table-column prop="role" label="角色" min-width="100" />
        <el-table-column prop="phone" label="电话" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="审批状态" min-width="100">
          <template #default="scope">
            <el-tag 
              v-if="scope.row.status === '待审批'" 
              type="warning"
              class="rounded-full px-2 py-1 text-xs"
            >
              {{ scope.row.status }}
            </el-tag>
            <el-tag 
              v-if="scope.row.status === '审批通过'" 
              type="success"
              class="rounded-full px-2 py-1 text-xs"
            >
              {{ scope.row.status }}
            </el-tag>
            <el-tag 
              v-if="scope.row.status === '审批拒绝'" 
              type="danger"
              class="rounded-full px-2 py-1 text-xs"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <div class="flex items-center space-x-2">
              <!-- 对于待审批的医生，显示审核按钮 -->
              <el-button 
                v-if="scope.row.status === '待审批'"
                type="primary" 
                circle 
                @click="handleApproval(scope.row)"
                class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
              >
                <el-tooltip content="审核医生资质" placement="top">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                  </svg>
                </el-tooltip>
              </el-button>
              
              <!-- 对于已审核的医生，显示查看详情按钮 -->
              <el-button 
                v-else
                type="info" 
                circle 
                @click="viewDoctorDetails(scope.row)"
                class="bg-gray-500 hover:bg-gray-600 border-gray-500 hover:border-gray-600"
              >
                <el-tooltip content="查看医生详情" placement="top">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="12" y1="8" x2="12" y2="16"></line>
                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                  </svg>
                </el-tooltip>
              </el-button>
              
              <!-- 修改基本信息按钮 -->
              <el-button 
                type="warning" 
                circle 
                @click="handleEditBasic(scope.row)"
                class="bg-amber-500 hover:bg-amber-600 border-amber-500 hover:border-amber-600"
              >
                <el-tooltip content="修改基本信息" placement="top">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                  </svg>
                </el-tooltip>
              </el-button>
              
              <!-- 删除按钮 -->
              <el-button 
                type="danger" 
                circle 
                @click="del(scope.row.id)"
                class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600"
              >
                <el-tooltip content="删除医生" placement="top">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 6h18"></path>
                    <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                    <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                  </svg>
                </el-tooltip>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- Empty State -->
      <div v-if="!data.loading && data.tableData.length === 0" class="py-8 text-center">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
          <circle cx="8.5" cy="7" r="4"></circle>
          <line x1="20" y1="8" x2="20" y2="14"></line>
          <line x1="23" y1="11" x2="17" y2="11"></line>
        </svg>
        <p class="mt-2 text-gray-500">暂无医生数据</p>
        <el-button 
          type="primary" 
          @click="handleAdd" 
          class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
        >
          添加第一位医生
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
      :title="data.form.id ? '编辑医生信息' : '新增医生'" 
      width="600px" 
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form 
        ref="formRef" 
        :model="data.form" 
        :rules="data.rules" 
        label-width="80px" 
        class="p-4"
      >
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <el-form-item prop="username" label="账号" required>
            <el-input 
              v-model="data.form.username" 
              placeholder="请输入账号"
              maxlength="20"
              show-word-limit
              :disabled="data.editMode === 'basic'"
            ></el-input>
          </el-form-item>
          <el-form-item prop="name" label="姓名" required>
            <el-input 
              v-model="data.form.name" 
              placeholder="请输入姓名"
              maxlength="20"
              show-word-limit
              :disabled="data.editMode === 'basic'"
            ></el-input>
          </el-form-item>
          <el-form-item prop="seniority" label="工龄">
            <el-input-number 
              v-model="data.form.seniority" 
              :min="1" 
              placeholder="工龄" 
              class="w-full"
            />
          </el-form-item>
          <el-form-item prop="phone" label="电话">
            <el-input 
              v-model="data.form.phone" 
              placeholder="请输入电话"
              maxlength="15"
            ></el-input>
          </el-form-item>
          <el-form-item prop="email" label="邮箱">
            <el-input 
              v-model="data.form.email" 
              placeholder="请输入邮箱"
              maxlength="50"
            ></el-input>
          </el-form-item>
          <el-form-item prop="code" label="身份证号">
            <el-input 
              v-model="data.form.code" 
              placeholder="请输入身份证号"
              maxlength="18"
            ></el-input>
          </el-form-item>
        </div>
        
        <el-form-item prop="content" label="简介">
          <el-input 
            type="textarea" 
            :rows="4" 
            v-model="data.form.content" 
            placeholder="请输入简介"
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="status" label="审核状态" v-if="data.editMode !== 'basic'">
          <el-radio-group v-model="data.form.status" class="flex flex-wrap gap-2">
            <el-radio-button label="待审批" class="rounded-md" />
            <el-radio-button label="审批通过" class="rounded-md" />
            <el-radio-button label="审批拒绝" class="rounded-md" />
          </el-radio-group>
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

    <!-- 证书查看对话框 -->
    <el-dialog 
      v-model="data.certificateVisible" 
      title="查看证书" 
      width="700px" 
      destroy-on-close
      :close-on-click-modal="false"
    >
      <doctor-approval
        :selected-doctor="data.selectedDoctor"
        @close="data.certificateVisible = false"
      />
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="data.approvalVisible"
      title="医生资质审核"
      width="700px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <div class="p-4">
        <!-- 医生基本信息 -->
        <div class="mb-6 bg-blue-50 p-4 rounded-md">
          <h3 class="text-lg font-medium mb-3 text-blue-700">医生基本信息</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex items-center">
              <span class="text-gray-500 w-20">姓名：</span>
              <span class="font-medium">{{ data.selectedDoctor?.name }}</span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-500 w-20">工龄：</span>
              <span class="font-medium">{{ data.selectedDoctor?.seniority }} 年</span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-500 w-20">身份证号：</span>
              <span class="font-medium">{{ data.selectedDoctor?.code }}</span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-500 w-20">联系电话：</span>
              <span class="font-medium">{{ data.selectedDoctor?.phone }}</span>
            </div>
          </div>
        </div>

        <!-- 医生简介 -->
        <div class="mb-6">
          <h3 class="text-lg font-medium mb-2">医生简介</h3>
          <p class="p-3 bg-gray-50 rounded-md text-gray-700">
            {{ data.selectedDoctor?.content || '暂无简介' }}
          </p>
        </div>

        <!-- 证书文件 -->
        <div class="mb-6">
          <h3 class="text-lg font-medium mb-3">证书文件</h3>
          <doctor-approval
            :selected-doctor="data.selectedDoctor"
            @close="() => {}"
          />
        </div>

        <!-- 审核结果 -->
        <div class="mt-8">
          <h3 class="text-lg font-medium mb-3">审核结果</h3>
          <el-form :model="data.approvalForm" label-width="80px">
            <el-form-item label="审核状态" required>
              <el-radio-group v-model="data.approvalForm.status" class="flex flex-wrap gap-3">
                <el-radio-button label="审批通过" class="rounded-md bg-green-50">
                  <span class="flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-green-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                      <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                    审批通过
                  </span>
                </el-radio-button>
                <el-radio-button label="审批拒绝" class="rounded-md bg-red-50">
                  <span class="flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <line x1="15" y1="9" x2="9" y2="15"></line>
                      <line x1="9" y1="9" x2="15" y2="15"></line>
                    </svg>
                    审批拒绝
                  </span>
                </el-radio-button>
                <el-radio-button label="待审批" class="rounded-md bg-yellow-50">
                  <span class="flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-yellow-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <line x1="12" y1="8" x2="12" y2="12"></line>
                      <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                    待审批
                  </span>
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="data.approvalVisible = false">取 消</el-button>
          <el-button 
            type="primary" 
            @click="submitApproval"
            :loading="data.submitting"
            class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            提交审核结果
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 医生详情对话框 -->
    <el-dialog 
      v-model="data.detailsVisible" 
      :title="`${data.selectedDoctor?.name} 的详细信息`" 
      width="700px" 
      destroy-on-close
      :close-on-click-modal="false"
    >
      <div class="p-4">
        <!-- 医生基本信息 -->
        <div class="mb-6 bg-blue-50 p-4 rounded-md">
          <h3 class="text-lg font-medium mb-3 text-blue-700">基本信息</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex items-center">
              <span class="text-gray-500 w-20">姓名：</span>
              <span class="font-medium">{{ data.selectedDoctor?.name }}</span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-500 w-20">工龄：</span>
              <span class="font-medium">{{ data.selectedDoctor?.seniority }} 年</span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-500 w-20">身份证号：</span>
              <span class="font-medium">{{ data.selectedDoctor?.code }}</span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-500 w-20">联系电话：</span>
              <span class="font-medium">{{ data.selectedDoctor?.phone }}</span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-500 w-20">邮箱：</span>
              <span class="font-medium">{{ data.selectedDoctor?.email }}</span>
            </div>
            <div class="flex items-center">
              <span class="text-gray-500 w-20">审核状态：</span>
              <el-tag 
                :type="getStatusType(data.selectedDoctor?.status)"
                class="rounded-full px-2 py-1 text-xs"
              >
                {{ data.selectedDoctor?.status }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 医生简介 -->
        <div class="mb-6">
          <h3 class="text-lg font-medium mb-2">医生简介</h3>
          <p class="p-3 bg-gray-50 rounded-md text-gray-700">
            {{ data.selectedDoctor?.content || '暂无简介' }}
          </p>
        </div>

        <!-- 证书文件 -->
        <div class="mb-6">
          <h3 class="text-lg font-medium mb-3">资质证书</h3>
          <doctor-approval
            :selected-doctor="data.selectedDoctor"
            @close="() => {}"
          />
        </div>
      </div>
      
      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="data.detailsVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, nextTick } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import DoctorApproval from './DoctorApproval.vue';

const baseUrl = import.meta.env.VITE_BASE_URL;
const formRef = ref(null);

const data = reactive({
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  name: null,
  ids: [],
  loading: false,
  submitting: false,
  status: null,
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
      { min: 2, max: 20, message: '账号长度在 2 到 20 个字符', trigger: 'blur' }
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
    code: [
      { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码', trigger: 'blur' }
    ]
  },
  selectedDoctor: null,
  editMode: 'full',
  certificateVisible: false,
  approvalVisible: false,
  approvalForm: {
    status: '审批通过'
  },
  detailsVisible: false,
});

const load = async () => {
  data.loading = true;
  try {
    const res = await request.get('/doctor/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        name: data.name || undefined,
        status: data.status || undefined
      }
    });
    
    if (res.code === '200') {
      data.tableData = res.data?.list || [];
      data.total = res.data?.total || 0;
    } else {
      ElMessage.error(res.msg || '加载数据失败');
    }
  } catch (error) {
    console.error('Failed to load doctors:', error);
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
  data.form = {
    status: '待审批' // 默认状态
  };
  data.formVisible = true;
  
  // Focus on first input after dialog is shown
  nextTick(() => {
    const firstInput = document.querySelector('.el-dialog input');
    if (firstInput) firstInput.focus();
  });
};

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  // 不允许编辑公司ID
  delete data.form.companyId;
  data.formVisible = true;
};

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!');
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
    ElMessage.error(res.msg || '上传失败');
  }
};

const save = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    data.submitting = true;
    
    const isUpdate = !!data.form.id;
    const api = isUpdate ? '/doctor/update' : '/doctor/add';
    const method = isUpdate ? 'put' : 'post';
    
    const res = await request[method](api, data.form);
    
    if (res.code === '200') {
      ElMessage({
        type: 'success',
        message: isUpdate ? '医生信息更新成功' : '医生添加成功',
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
    
    const res = await request.delete('/doctor/delete/' + id);
    
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
      `确定要删除选中的 ${data.ids.length} 位医生吗？删除后数据无法恢复。`, 
      '批量删除确认', 
      { 
        type: 'warning',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'el-button--danger'
      }
    );
    
    const res = await request.delete("/doctor/delete/batch", { data: data.ids });
    
    if (res.code === '200') {
      ElMessage.success(`成功删除 ${data.ids.length} 位医生`);
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
  data.status = null;
  data.pageNum = 1;
  load();
};

// 查看证书
const viewCertificates = (row) => {
  data.selectedDoctor = JSON.parse(JSON.stringify(row));
  data.certificateVisible = true;
};

// 处理修改基本信息（仅联系方式等）
const handleEditBasic = (row) => {
  // 浅拷贝医生信息，避免修改原始数据
  data.form = { ...row };
  // 限制可编辑字段
  data.editMode = 'basic';
  data.formVisible = true;
};

// 查看医生详情（包括证书和审核状态）
const viewDoctorDetails = (row) => {
  data.selectedDoctor = JSON.parse(JSON.stringify(row));
  data.detailsVisible = true;
};

// 处理审核
const handleApproval = (row) => {
  data.selectedDoctor = JSON.parse(JSON.stringify(row));
  data.approvalForm.status = row.status || '待审批';
  data.approvalVisible = true;
};

// 提交审核结果
const submitApproval = async () => {
  if (!data.selectedDoctor) return;
  
  try {
    data.submitting = true;
    
    // 创建要提交的数据对象
    const approvalData = {
      id: data.selectedDoctor.id,
      status: data.approvalForm.status
    };
    
    const res = await request.put('/doctor/update', approvalData);
    
    if (res.code === '200') {
      ElMessage({
        type: 'success',
        message: '审核操作成功',
        duration: 2000
      });
      data.approvalVisible = false;
      // 刷新列表
      load();
    } else {
      ElMessage.error(res.msg || '审核失败');
    }
  } catch (error) {
    console.error('审核提交错误:', error);
    if (error?.message) {
      ElMessage.error(error.message);
    }
  } finally {
    data.submitting = false;
  }
};

// Load data when component is mounted
load();

// 获取状态对应的tag类型
const getStatusType = (status) => {
  if (status === '审批通过') return 'success';
  if (status === '审批拒绝') return 'danger';
  if (status === '待审批') return 'warning';
  return 'info';
};
</script>

<style scoped>
.doctor-container {
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