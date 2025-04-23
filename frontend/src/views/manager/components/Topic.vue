<template>
    <div class="topic-container">
      <!-- Search and Action Card -->
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
              v-model="data.title" 
              placeholder="请输入题目名称查询" 
              class="pl-10"
              clearable
              @keyup.enter="load"
            />
          </div>
          
          <div class="w-32">
            <el-select
              v-model="data.typeName"
              placeholder="心理分类"
              class="w-full"
              clearable
            >
              <el-option
                v-for="item in data.typeData"
                :key="item.id"
                :label="item.title"
                :value="item.title"
              />
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
        <div class="flex flex-wrap items-center gap-3 mt-4 pt-3 border-t border-gray-100" v-if="data.user.role === 'ADMIN' || data.user.role === 'DOCTOR'">
          <el-button 
            type="primary" 
            @click="handleAdd"
            class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            新增题目
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
          
          <!-- 新增类型管理按钮 -->
          <el-button 
            type="success" 
            @click="handleTypeManage"
            class="bg-emerald-500 hover:bg-emerald-600 border-emerald-500 hover:border-emerald-600 text-white"
            v-if="data.user.role === 'ADMIN'"
            >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 20h9"></path>
              <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path>
            </svg>
            管理题目类型
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
          <el-table-column type="selection" width="55" v-if="data.user.role === 'ADMIN' || data.user.role === 'DOCTOR'" />
          <el-table-column prop="title" label="题目名称" min-width="200" show-overflow-tooltip />
          <el-table-column prop="typeName" label="题目分类" min-width="120" />
          <el-table-column label="选项A" min-width="150">
            <template #default="scope">
              <div class="flex items-center">
                <span class="w-6 h-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center mr-2 font-medium">A</span>
                <span class="truncate">{{ scope.row.aName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="aScore" label="A分数" width="80" align="center" />
          <el-table-column label="选项B" min-width="150">
            <template #default="scope">
              <div class="flex items-center">
                <span class="w-6 h-6 rounded-full bg-green-100 text-green-600 flex items-center justify-center mr-2 font-medium">B</span>
                <span class="truncate">{{ scope.row.bName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="bScore" label="B分数" width="80" align="center" />
          <el-table-column label="选项C" min-width="150">
            <template #default="scope">
              <div class="flex items-center">
                <span class="w-6 h-6 rounded-full bg-amber-100 text-amber-600 flex items-center justify-center mr-2 font-medium">C</span>
                <span class="truncate">{{ scope.row.cName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="cScore" label="C分数" width="80" align="center" />
          <el-table-column label="选项D" min-width="150">
            <template #default="scope">
              <div class="flex items-center">
                <span class="w-6 h-6 rounded-full bg-purple-100 text-purple-600 flex items-center justify-center mr-2 font-medium">D</span>
                <span class="truncate">{{ scope.row.dName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="dScore" label="D分数" width="80" align="center" />
          <el-table-column prop="score" label="最高分数" width="100" align="center">
            <template #default="scope">
              <div class="flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-amber-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                </svg>
                <span class="font-medium">{{ scope.row.score || 10 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" v-if="data.user.role === 'ADMIN' || data.user.role === 'DOCTOR'">
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
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
          <p class="mt-2 text-gray-500">暂无题目数据</p>
          <el-button 
            v-if="data.user.role === 'ADMIN' || data.user.role === 'DOCTOR'"
            type="primary" 
            @click="handleAdd" 
            class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            添加第一个题目
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
        title="题目信息" 
        width="600px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <el-form 
          ref="formRef" 
          :rules="data.rules" 
          :model="data.form" 
          label-width="100px" 
          class="p-4"
        >
          <div class="bg-blue-50 p-3 rounded-md mb-4 flex items-start">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-500 mr-2 mt-0.5 flex-shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="16" x2="12" y2="12"></line>
              <line x1="12" y1="8" x2="12.01" y2="8"></line>
            </svg>
            <div class="text-sm text-blue-700">
              <div class="font-medium">分数说明</div>
              <div>每个选项最高 10 分，请合理设置各选项分数</div>
            </div>
          </div>
          
          <el-form-item prop="title" label="题目名称" required>
            <el-input 
              v-model="data.form.title" 
              placeholder="请输入题目名称"
              maxlength="100"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="typeId" label="题目分类" required>
            <el-select
              v-model="data.form.typeId"
              placeholder="请选择心理分类"
              class="w-full"
              filterable
            >
              <el-option
                v-for="item in data.typeData"
                :key="item.id"
                :label="item.title"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="bg-blue-50 p-4 rounded-md">
              <div class="text-sm font-medium text-blue-700 mb-2 flex items-center">
                <span class="w-6 h-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center mr-2 font-medium">A</span>
                选项A
              </div>
              <el-form-item prop="aName" label="选项内容" required>
                <el-input 
                  v-model="data.form.aName" 
                  placeholder="请输入选项A内容"
                  maxlength="100"
                  show-word-limit
                ></el-input>
              </el-form-item>
              <el-form-item prop="aScore" label="分数" required>
                <el-input-number 
                  v-model="data.form.aScore" 
                  :min="0" 
                  :max="10" 
                  placeholder="请输入选项A分数" 
                  class="w-full"
                />
              </el-form-item>
            </div>
            
            <div class="bg-green-50 p-4 rounded-md">
              <div class="text-sm font-medium text-green-700 mb-2 flex items-center">
                <span class="w-6 h-6 rounded-full bg-green-100 text-green-600 flex items-center justify-center mr-2 font-medium">B</span>
                选项B
              </div>
              <el-form-item prop="bName" label="选项内容" required>
                <el-input 
                  v-model="data.form.bName" 
                  placeholder="请输入选项B内容"
                  maxlength="100"
                  show-word-limit
                ></el-input>
              </el-form-item>
              <el-form-item prop="bScore" label="分数" required>
                <el-input-number 
                  v-model="data.form.bScore" 
                  :min="0" 
                  :max="10" 
                  placeholder="请输入选项B分数" 
                  class="w-full"
                />
              </el-form-item>
            </div>
            
            <div class="bg-amber-50 p-4 rounded-md">
              <div class="text-sm font-medium text-amber-700 mb-2 flex items-center">
                <span class="w-6 h-6 rounded-full bg-amber-100 text-amber-600 flex items-center justify-center mr-2 font-medium">C</span>
                选项C
              </div>
              <el-form-item prop="cName" label="选项内容" required>
                <el-input 
                  v-model="data.form.cName" 
                  placeholder="请输入选项C内容"
                  maxlength="100"
                  show-word-limit
                ></el-input>
              </el-form-item>
              <el-form-item prop="cScore" label="分数" required>
                <el-input-number 
                  v-model="data.form.cScore" 
                  :min="0" 
                  :max="10" 
                  placeholder="请输入选项C分数" 
                  class="w-full"
                />
              </el-form-item>
            </div>
            
            <div class="bg-purple-50 p-4 rounded-md">
              <div class="text-sm font-medium text-purple-700 mb-2 flex items-center">
                <span class="w-6 h-6 rounded-full bg-purple-100 text-purple-600 flex items-center justify-center mr-2 font-medium">D</span>
                选项D
              </div>
              <el-form-item prop="dName" label="选项内容" required>
                <el-input 
                  v-model="data.form.dName" 
                  placeholder="请输入选项D内容"
                  maxlength="100"
                  show-word-limit
                ></el-input>
              </el-form-item>
              <el-form-item prop="dScore" label="分数" required>
                <el-input-number 
                  v-model="data.form.dScore" 
                  :min="0" 
                  :max="10" 
                  placeholder="请输入选项D分数" 
                  class="w-full"
                />
              </el-form-item>
            </div>
          </div>
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
  
      <!-- 添加类型管理弹窗 -->
      <el-dialog 
        v-model="data.typeFormVisible" 
        title="题目类型管理" 
        width="600px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <div class="mb-4 flex justify-between items-center">
          <div class="text-sm text-gray-500">管理题目的分类，添加或修改类型</div>
          <el-button 
            type="primary" 
            @click="handleAddType"
            class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            新增类型
          </el-button>
        </div>
        
        <el-table 
          :data="data.typeData" 
          stripe
          border
          class="w-full"
          v-loading="data.typeLoading"
          element-loading-text="加载中..."
          element-loading-background="rgba(255, 255, 255, 0.8)"
        >
          <el-table-column prop="title" label="分类标题" min-width="200">
            <template #default="scope">
              <div class="flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-[#2A5C8A]" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                  <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                </svg>
                <span class="font-medium">{{ scope.row.title }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <div class="flex items-center space-x-2">
                <el-button 
                  type="primary" 
                  circle 
                  @click="handleEditType(scope.row)"
                  class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                  </svg>
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- Empty State for Type Table -->
        <div v-if="!data.typeLoading && data.typeData.length === 0" class="py-8 text-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
          </svg>
          <p class="mt-2 text-gray-500">暂无分类数据</p>
          <el-button 
            type="primary" 
            @click="handleAddType" 
            class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            添加第一个分类
          </el-button>
        </div>
      </el-dialog>
  
      <!-- 类型表单弹窗 -->
      <el-dialog 
        v-model="data.typeEditVisible" 
        :title="data.typeForm.id ? '编辑类型' : '新增类型'" 
        width="500px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <el-form 
          ref="typeFormRef" 
          :model="data.typeForm" 
          :rules="data.typeRules" 
          label-width="100px" 
          class="p-4"
        >
          <el-form-item prop="title" label="分类标题" required>
            <el-input 
              v-model="data.typeForm.title" 
              placeholder="请输入分类标题"
              maxlength="50"
              show-word-limit
            ></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="flex justify-end gap-2">
            <el-button @click="data.typeEditVisible = false">取 消</el-button>
            <el-button 
              type="primary" 
              @click="saveType"
              :loading="data.typeSubmitting"
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
  
  const formRef = ref();
  const typeFormRef = ref();
  
  const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    formVisible: false,
    form: {},
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    title: '',
    typeName: '',
    ids: [],
    typeData: [],
    loading: false,
    submitting: false,
    
    typeFormVisible: false,
    typeEditVisible: false,
    typeForm: {},
    typeLoading: false,
    typeSubmitting: false,
    
    rules: {
      title: [
        { required: true, message: '请输入题目名称', trigger: 'blur' },
        { min: 2, max: 100, message: '题目名称长度在 2 到 100 个字符', trigger: 'blur' }
      ],
      typeId: [
        { required: true, message: '请选择心理分类', trigger: 'change' }
      ],
      aName: [
        { required: true, message: '请输入A选项内容', trigger: 'blur' },
        { min: 1, max: 100, message: '选项内容长度在 1 到 100 个字符', trigger: 'blur' }
      ],
      bName: [
        { required: true, message: '请输入B选项内容', trigger: 'blur' },
        { min: 1, max: 100, message: '选项内容长度在 1 到 100 个字符', trigger: 'blur' }
      ],
      cName: [
        { required: true, message: '请输入C选项内容', trigger: 'blur' },
        { min: 1, max: 100, message: '选项内容长度在 1 到 100 个字符', trigger: 'blur' }
      ],
      dName: [
        { required: true, message: '请输入D选项内容', trigger: 'blur' },
        { min: 1, max: 100, message: '选项内容长度在 1 到 100 个字符', trigger: 'blur' }
      ],
      aScore: [
        { required: true, message: '请输入A选项分数', trigger: 'change' },
        { type: 'number', min: 0, max: 10, message: '分数范围为 0-10', trigger: 'change' }
      ],
      bScore: [
        { required: true, message: '请输入B选项分数', trigger: 'change' },
        { type: 'number', min: 0, max: 10, message: '分数范围为 0-10', trigger: 'change' }
      ],
      cScore: [
        { required: true, message: '请输入C选项分数', trigger: 'change' },
        { type: 'number', min: 0, max: 10, message: '分数范围为 0-10', trigger: 'change' }
      ],
      dScore: [
        { required: true, message: '请输入D选项分数', trigger: 'change' },
        { type: 'number', min: 0, max: 10, message: '分数范围为 0-10', trigger: 'change' }
      ],
    },
    
    typeRules: {
      title: [
        { required: true, message: '请输入分类标题', trigger: 'blur' },
        { min: 2, max: 50, message: '分类标题长度在 2 到 50 个字符', trigger: 'blur' }
      ]
    }
  });
  
  const loadType = async () => {
    data.typeLoading = true;
    try {
      const res = await request.get('/type/selectAll');
      
      if (res.code === '200') {
        data.typeData = res.data || [];
      } else {
        ElMessage.error(res.msg || '加载分类数据失败');
      }
    } catch (error) {
      console.error('Failed to load type data:', error);
      ElMessage.error('加载分类数据失败，请检查网络连接');
    } finally {
      data.typeLoading = false;
    }
  };
  
  const load = async () => {
    data.loading = true;
    try {
      const res = await request.get('/topic/selectPage', {
        params: {
          pageNum: data.pageNum,
          pageSize: data.pageSize,
          title: data.title || undefined,
          typeName: data.typeName || undefined
        }
      });
      
      if (res.code === '200') {
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
      } else {
        ElMessage.error(res.msg || '加载题目数据失败');
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
    data.form = {
      aScore: 0,
      bScore: 0,
      cScore: 0,
      dScore: 0
    };
    data.formVisible = true;
  };
  
  const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row));
    data.formVisible = true;
  };
  
  const save = async () => {
    if (!formRef.value) return;
    
    try {
      await formRef.value.validate();
      
      data.submitting = true;
      
      const isUpdate = !!data.form.id;
      const api = isUpdate ? '/topic/update' : '/topic/add';
      const method = isUpdate ? 'put' : 'post';
      
      const res = await request[method](api, data.form);
      
      if (res.code === '200') {
        ElMessage({
          type: 'success',
          message: isUpdate ? '题目更新成功' : '题目添加成功',
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
      
      const res = await request.delete('/topic/delete/' + id);
      
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
        `确定要删除选中的 ${data.ids.length} 个题目吗？删除后数据无法恢复。`, 
        '批量删除确认', 
        { 
          type: 'warning',
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          confirmButtonClass: 'el-button--danger'
        }
      );
      
      const res = await request.delete("/topic/delete/batch", { data: data.ids });
      
      if (res.code === '200') {
        ElMessage.success(`成功删除 ${data.ids.length} 个题目`);
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
    data.title = '';
    data.typeName = '';
    data.pageNum = 1;
    load();
  };
  
  const handleTypeManage = () => {
    data.typeFormVisible = true;
    loadType();
  };
  
  const handleAddType = () => {
    data.typeForm = {};
    data.typeEditVisible = true;
    
    setTimeout(() => {
      const firstInput = document.querySelector('.el-dialog input');
      if (firstInput) firstInput.focus();
    }, 300);
  };
  
  const handleEditType = (row) => {
    data.typeForm = JSON.parse(JSON.stringify(row));
    data.typeEditVisible = true;
    
    setTimeout(() => {
      const firstInput = document.querySelector('.el-dialog input');
      if (firstInput) firstInput.focus();
    }, 300);
  };
  
  const saveType = async () => {
    if (!typeFormRef.value) return;
    
    try {
      await typeFormRef.value.validate();
      
      data.typeSubmitting = true;
      
      const isUpdate = !!data.typeForm.id;
      const api = isUpdate ? '/type/update' : '/type/add';
      const method = isUpdate ? 'put' : 'post';
      
      const res = await request[method](api, data.typeForm);
      
      if (res.code === '200') {
        ElMessage({
          type: 'success',
          message: isUpdate ? '分类更新成功' : '分类添加成功',
          duration: 2000
        });
        data.typeEditVisible = false;
        loadType();
      } else {
        ElMessage.error(res.msg || '操作失败');
      }
    } catch (error) {
      console.error('Form validation or submission error:', error);
      if (error?.message) {
        ElMessage.error(error.message);
      }
    } finally {
      data.typeSubmitting = false;
    }
  };
  
  // Load data when component is mounted
  onMounted(() => {
    load();
    loadType();
  });
  </script>
  
  <style scoped>
  .topic-container {
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