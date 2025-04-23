<template>
    <div class="propagate-container">
      <!-- Search and Action Card -->
      <div class="bg-white rounded-lg shadow-md p-4 mb-4">
        <!-- Search Row -->
        <div class="flex flex-wrap items-center gap-3 mb-4 pb-3 border-b border-gray-100">
          <!-- 标题搜索框 -->
          <div class="relative flex-grow max-w-xs">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </span>
            <el-input 
              v-model="data.title" 
              placeholder="请输入宣传标题" 
              class="pl-10"
            />
          </div>
          
          <!-- 医生姓名搜索框 -->
          <div class="relative flex-grow max-w-xs" v-if="data.user.role === 'ADMIN'">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </span>
            <el-input 
              v-model="data.doctorName" 
              placeholder="请输入医生姓名" 
              class="pl-10"
            />
          </div>
          
          <!-- 状态筛选下拉框 -->
          <div class="relative flex-grow max-w-xs">
            <el-select 
              v-model="data.searchStatus" 
              placeholder="审核状态" 
              clearable
              class="w-full"
            >
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
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
            v-if="data.user.role === 'DOCTOR'"
            class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            新增宣传
          </el-button>
          
          <!-- 新增宣传类型管理按钮 -->
          <el-button 
            type="success" 
            @click="openTagManager"
            v-if="data.user.role === 'ADMIN'"
            class="bg-green-500 hover:bg-green-600 border-green-500 hover:border-green-600 text-white"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
              <line x1="7" y1="7" x2="7.01" y2="7"></line>
            </svg>
            宣传类型管理
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
          <el-table-column prop="img" label="宣传封面" width="130" align="center">
            <template #default="scope">
              <el-image 
                v-if="scope.row.img"
                class="w-24 h-20 rounded-md object-cover mx-auto"
                :src="scope.row.img" 
                :preview-src-list="[scope.row.img]" 
                preview-teleported
                fit="cover"
              />
              <div v-else class="w-24 h-20 rounded-md bg-gray-200 flex items-center justify-center mx-auto">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <circle cx="8.5" cy="8.5" r="1.5"></circle>
                  <polyline points="21 15 16 10 5 21"></polyline>
                </svg>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="宣传标题" min-width="150" show-overflow-tooltip />
          <el-table-column prop="content" label="宣传内容" min-width="120" align="center">
            <template #default="scope">
              <el-button 
                type="success" 
                @click="viewContent(scope.row.content)"
                class="bg-green-500 hover:bg-green-600 border-green-500 hover:border-green-600 text-white"
              >
                点击查看
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="发布时间" min-width="150" />
          <el-table-column prop="doctorName" label="医生姓名" min-width="120" />
          <el-table-column prop="num" label="浏览量" width="100" align="center">
            <template #default="scope">
              <div class="flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
                <span>{{ scope.row.num || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="scope">
              <el-tag 
                :type="getStatusType(scope.row.status)"
                class="rounded-full px-2 py-1 text-xs"
              >
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="scope">
              <div class="flex items-center space-x-2">
                <el-button 
                  type="primary" 
                  circle 
                  @click="handleEdit(scope.row)"
                  v-if="data.user.role === 'DOCTOR' && scope.row.status !== 'APPROVED' || data.user.role === 'ADMIN'"
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
                  v-if="data.user.role === 'DOCTOR' || data.user.role === 'ADMIN'"
                  class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 6h18"></path>
                    <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                    <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                  </svg>
                </el-button>
                <el-button 
                  v-if="data.user.role === 'ADMIN' && scope.row.status === 'PENDING'"
                  type="success" 
                  circle 
                  @click="handleAudit(scope.row)"
                  class="bg-green-500 hover:bg-green-600 border-green-500 hover:border-green-600"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 6L9 17l-5-5"></path>
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
          <p class="mt-2 text-gray-500">暂无宣传数据</p>
          <el-button 
            v-if="data.user.role === 'DOCTOR'"
            type="primary" 
            @click="handleAdd" 
            class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            添加第一条宣传
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
          :page-sizes="[5, 10, 20, 50]"
          :page-size="data.pageSize" 
          v-model:current-page="data.pageNum" 
          :total="data.total" 
        />
      </div>
  
      <!-- Form Dialog -->
      <el-dialog 
        v-model="data.formVisible" 
        :title="data.form.id ? '审核宣传资料' : '新增宣传资料'" 
        width="70%" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <!-- 根据角色显示不同的表单内容 -->
        <template v-if="data.user.role === 'DOCTOR'">
          <!-- 医生的完整表单，保持原有内容不变 -->
          <el-form 
            ref="formRef" 
            :model="data.form" 
            :rules="rules" 
            label-width="80px" 
            class="p-4"
          >
            <el-form-item prop="title" label="宣传标题" required>
              <el-input 
                v-model="data.form.title" 
                placeholder="请输入宣传标题"
                maxlength="100"
                show-word-limit
              ></el-input>
            </el-form-item>
            
            <!-- 新增标签选择 -->
            <el-form-item prop="tagId" label="宣传类型" required>
              <el-select 
                v-model="data.form.tagId" 
                placeholder="请选择宣传类型"
                class="w-full"
                filterable
                clearable
              >
                <el-option 
                  v-for="tag in data.tagOptions" 
                  :key="tag.id" 
                  :label="tag.tag" 
                  :value="tag.id" 
                />
              </el-select>
            </el-form-item>
            
            <el-form-item prop="img" label="宣传封面" required>
              <div class="flex items-center">
                <el-upload
                  :action="uploadUrl"
                  :headers="headers"
                  :on-success="handleFileUpload"
                  :on-error="handleUploadError"
                  :before-upload="beforeImageUpload"
                  list-type="picture-card"
                  :limit="1"
                  :file-list="fileList"
                >
                  <div class="flex flex-col items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-400 mb-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                      <polyline points="17 8 12 3 7 8"></polyline>
                      <line x1="12" y1="3" x2="12" y2="15"></line>
                    </svg>
                    <span class="text-xs text-gray-500">上传封面</span>
                  </div>
                </el-upload>
                <div v-if="data.form.img" class="ml-4 flex items-center text-sm text-gray-500">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-500 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                  </svg>
                  封面已上传
                </div>
              </div>
            </el-form-item>
            
            <el-form-item prop="content" label="宣传内容" required>
              <div class="border border-gray-300 rounded-md">
                <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="editorRef"
                  :mode="mode"
                  class="border-b border-gray-200"
                />
                <Editor
                  style="height: 400px; overflow-y: hidden;"
                  v-model="data.form.content"
                  :mode="mode"
                  :defaultConfig="editorConfig"
                  @onCreated="handleCreated"
                  class="w-full"
                />
              </div>
            </el-form-item>
          </el-form>
        </template>
  
        <!-- 管理员只能看到状态修改 -->
        <template v-else-if="data.user.role === 'ADMIN'">
          <el-form 
            ref="formRef" 
            :model="data.form" 
            :rules="adminRules" 
            label-width="80px" 
            class="p-4"
          >
            <!-- 显示基本信息（只读） -->
            <div class="mb-6">
              <h3 class="text-lg font-medium text-gray-900 mb-4">宣传资料信息</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="text-sm font-medium text-gray-700">宣传标题</label>
                  <p class="mt-1 text-gray-900">{{ data.form.title }}</p>
                </div>
                <div>
                  <label class="text-sm font-medium text-gray-700">发布医生</label>
                  <p class="mt-1 text-gray-900">{{ data.form.doctorName }}</p>
                </div>
                <div>
                  <label class="text-sm font-medium text-gray-700">发布时间</label>
                  <p class="mt-1 text-gray-900">{{ data.form.time }}</p>
                </div>
                <div>
                  <label class="text-sm font-medium text-gray-700">浏览量</label>
                  <p class="mt-1 text-gray-900">{{ data.form.num || 0 }}</p>
                </div>
              </div>
            </div>
  
            <!-- 状态修改 -->
            <el-form-item prop="status" label="审核状态" required>
              <el-radio-group v-model="data.form.status" class="flex flex-wrap gap-2">
                <el-radio-button label="PENDING">待审核</el-radio-button>
                <el-radio-button label="APPROVED">已通过</el-radio-button>
                <el-radio-button label="REJECTED">已拒绝</el-radio-button>
              </el-radio-group>
            </el-form-item>
  
            <!-- 拒绝原因 -->
            <el-form-item 
              v-if="data.form.status === 'REJECTED'" 
              prop="rejectReason" 
              label="拒绝原因"
              :rules="[{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]"
            >
              <el-input
                v-model="data.form.rejectReason"
                type="textarea"
                :rows="3"
                placeholder="请输入拒绝原因，将通知给发布医生"
              />
            </el-form-item>
          </el-form>
        </template>
  
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
  
      <!-- View Content Dialog -->
      <el-dialog 
        v-model="data.viewVisible" 
        title="心理健康宣传内容" 
        width="70%" 
        destroy-on-close
        :close-on-click-modal="true"
      >
        <div class="p-6 bg-white rounded-lg">
          <div v-html="data.viewContent" class="prose max-w-none"></div>
        </div>
        <template #footer>
          <div class="flex justify-end">
            <el-button @click="data.viewVisible = false">关 闭</el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- 审核对话框 -->
      <el-dialog 
        v-model="data.auditVisible" 
        title="审核宣传资料" 
        width="700px"
        destroy-on-close
        :close-on-click-modal="false"
      >
        <div class="p-4">
          <!-- 宣传基本信息 -->
          <div class="mb-6 bg-blue-50 p-4 rounded-md">
            <h3 class="text-lg font-medium mb-3 text-blue-700">宣传基本信息</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex items-center">
                <span class="text-gray-500 w-20">标题：</span>
                <span class="font-medium">{{ data.currentAudit?.title }}</span>
              </div>
              <div class="flex items-center">
                <span class="text-gray-500 w-20">发布医生：</span>
                <span class="font-medium">{{ data.currentAudit?.doctorName }}</span>
              </div>
              <div class="flex items-center">
                <span class="text-gray-500 w-20">发布时间：</span>
                <span class="font-medium">{{ data.currentAudit?.time }}</span>
              </div>
              <div class="flex items-center">
                <span class="text-gray-500 w-20">浏览量：</span>
                <span class="font-medium">{{ data.currentAudit?.num || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- 宣传内容预览 -->
          <div class="mb-6">
            <h3 class="text-lg font-medium mb-2">宣传内容预览</h3>
            <div class="p-3 bg-gray-50 rounded-md max-h-[300px] overflow-auto">
              <div v-html="data.currentAudit?.content" class="prose max-w-none"></div>
            </div>
          </div>

          <!-- 审核结果 -->
          <div class="mt-8">
            <h3 class="text-lg font-medium mb-3">审核结果</h3>
            <el-radio-group v-model="data.auditForm.status" class="flex flex-wrap gap-3">
              <el-radio-button label="APPROVED" class="rounded-md bg-green-50">
                <span class="flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-green-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                    <polyline points="22 4 12 14.01 9 11.01"></polyline>
                  </svg>
                  审批通过
                </span>
              </el-radio-button>
              <el-radio-button label="REJECTED" class="rounded-md bg-red-50">
                <span class="flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="15" y1="9" x2="9" y2="15"></line>
                    <line x1="9" y1="9" x2="15" y2="15"></line>
                  </svg>
                  审批拒绝
                </span>
              </el-radio-button>
              <el-radio-button label="PENDING" class="rounded-md bg-yellow-50">
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
          </div>
          
          <div v-if="data.auditForm.status === 'REJECTED'" class="mt-4">
            <h3 class="text-lg font-medium mb-2">拒绝原因</h3>
            <el-input
              v-model="data.auditForm.rejectReason"
              type="textarea"
              :rows="3"
              placeholder="请输入拒绝原因，将通知给发布医生"
              class="w-full"
            />
          </div>
        </div>
        
        <template #footer>
          <div class="flex justify-end gap-3">
            <el-button @click="data.auditVisible = false">取 消</el-button>
            <el-button 
              type="primary" 
              @click="submitAudit"
              :loading="data.submitting"
              class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
            >
              提交审核结果
            </el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 宣传类型管理对话框 -->
      <el-dialog 
        v-model="data.tagManagerVisible" 
        title="宣传类型管理" 
        width="600px"
        destroy-on-close
        :close-on-click-modal="false"
      >
        <div class="p-4">
          <!-- 类型列表 -->
          <div class="mb-6">
            <div class="flex justify-between items-center mb-4">
              <h3 class="text-lg font-medium text-gray-900">当前宣传类型</h3>
              <el-button 
                type="primary" 
                size="small" 
                @click="handleAddTag"
                class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                新增类型
              </el-button>
            </div>
            
            <!-- 加载状态 -->
            <div v-if="data.tagsLoading" class="py-8 flex justify-center">
              <el-loading></el-loading>
            </div>
            
            <!-- 空状态 -->
            <div v-else-if="data.tags.length === 0" class="py-8 text-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
                <line x1="7" y1="7" x2="7.01" y2="7"></line>
              </svg>
              <p class="mt-2 text-gray-500">暂无宣传类型</p>
              <el-button 
                type="primary" 
                @click="handleAddTag" 
                class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
              >
                添加第一个宣传类型
              </el-button>
            </div>
            
            <!-- 类型列表 -->
            <div v-else class="border rounded-lg overflow-hidden">
              <el-table 
                :data="data.tags" 
                border 
                stripe
                style="width: 100%"
              >
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="tag" label="类型名称" />
                <el-table-column label="操作" width="150" align="center">
                  <template #default="scope">
                    <div class="flex items-center justify-center space-x-2">
                      <el-button 
                        type="primary" 
                        circle 
                        size="small"
                        @click="handleEditTag(scope.row)"
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
                        size="small"
                        @click="handleDeleteTag(scope.row.id)"
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
            </div>
          </div>
        </div>
        
        <template #footer>
          <div class="flex justify-end">
            <el-button @click="data.tagManagerVisible = false">关 闭</el-button>
          </div>
        </template>
      </el-dialog>
      
      <!-- 新增/编辑标签对话框 -->
      <el-dialog 
        v-model="data.tagFormVisible" 
        :title="data.tagForm.id ? '编辑宣传类型' : '新增宣传类型'" 
        width="400px"
        destroy-on-close
        append-to-body
      >
        <el-form 
          ref="tagFormRef" 
          :model="data.tagForm" 
          :rules="tagRules" 
          label-width="80px" 
          class="p-4"
        >
          <el-form-item prop="tag" label="类型名称" required>
            <el-input 
              v-model="data.tagForm.tag" 
              placeholder="请输入宣传类型名称"
              maxlength="50"
              show-word-limit
            ></el-input>
          </el-form-item>
        </el-form>
        
        <template #footer>
          <div class="flex justify-end gap-2">
            <el-button @click="data.tagFormVisible = false">取 消</el-button>
            <el-button 
              type="primary" 
              @click="saveTag"
              :loading="data.tagSubmitting"
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
  import { onBeforeUnmount, reactive, ref, shallowRef, computed } from "vue";
  import request from "@/utils/request.js";
  import { ElMessage, ElMessageBox } from "element-plus";
  import '@wangeditor/editor/dist/css/style.css' // 引入 css
  import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
  
  const formRef = ref(null);
  const tagFormRef = ref(null);
  const baseUrl = import.meta.env.VITE_APP_BASE_API || 'http://localhost:9090';
  const fileList = ref([]);
  
  // 获取当前用户信息
  const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  const headers = { token: user.token };
  
  const rules = {
    title: [
      { required: true, message: '请输入宣传标题', trigger: 'blur' },
      { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
    ],
    tagId: [
      { required: true, message: '请选择宣传类型', trigger: 'change' }
    ],
    img: [
      { required: true, message: '请上传宣传封面', trigger: 'change' }
    ],
    content: [
      { required: true, message: '请输入宣传内容', trigger: 'blur' }
    ]
  };
  
  const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    formVisible: false,
    form: {},
    tableData: [],
    pageNum: 1,
    pageSize: 5,
    total: 0,
    title: '',
    doctorName: '',
    ids: [],
    viewVisible: false,
    viewContent: null,
    loading: false,
    submitting: false,
    auditVisible: false,
    currentAudit: null,
    auditForm: {
      status: 'APPROVED',
      rejectReason: ''
    },
    searchStatus: null,
    tagManagerVisible: false,
    tagFormVisible: false,
    tagsLoading: false,
    tagSubmitting: false,
    tags: [],
    tagForm: {},
    tagOptions: []
  });
  
  /* wangEditor5 初始化开始 */
  const editorRef = shallowRef();  // 编辑器实例，必须用 shallowRef
  const mode = 'default';
  const editorConfig = { MENU_CONF: {} };
  // 图片上传配置
  editorConfig.MENU_CONF['uploadImage'] = {
    headers: {
      token: data.user.token,
    },
    server: baseUrl + '/files/wang/upload',  // 修改这里的URL拼接方式
    fieldName: 'file',  // 服务端图片上传接口参数
    maxFileSize: 5 * 1024 * 1024, // 5MB
    maxNumberOfFiles: 10,
    allowedFileTypes: ['image/*'],
    customInsert(res, insertFn) {  // 自定义插入图片
      if (res.code === '200') {
        insertFn(res.data, '', res.data);
      } else {
        ElMessage.error(res.msg || '图片上传失败');
      }
    },
    onError(file, err, res) {
      console.error('wangEditor image upload error:', err, res);
      ElMessage.error('图片上传失败: ' + (res?.msg || err.message || '未知错误'));
    }
  };
  // 组件销毁时，也及时销毁编辑器，否则可能会造成内存泄漏
  onBeforeUnmount(() => {
    const editor = editorRef.value;
    if (editor == null) return;
    editor.destroy();
  });
  // 记录 editor 实例，重要！
  const handleCreated = (editor) => {
    editorRef.value = editor;
  };
  /* wangEditor5 初始化结束 */
  
  const load = async () => {
    data.loading = true;
    try {
      const res = await request.get('/propagate/selectPage', {
        params: {
          pageNum: data.pageNum,
          pageSize: data.pageSize,
          title: data.title || undefined,
          doctorName: data.doctorName || undefined,
          status: data.searchStatus || undefined
        }
      });
      
      if (res.code === '200') {
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
      } else {
        ElMessage.error(res.msg || '加载宣传数据失败');
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
    
    // 加载标签选项
    loadTagOptions();
    
    // 确保编辑器正确初始化
    setTimeout(() => {
      if (editorRef.value) {
        editorRef.value.focus();
      }
    }, 300);
  };
  
  const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row));
    
    // 如果有图片，设置文件列表
    fileList.value = data.form.img ? [{
      name: '封面图片',
      url: data.form.img
    }] : [];
    
    // 加载标签选项
    loadTagOptions();
    
    data.formVisible = true;
    
    // 确保编辑器正确初始化
    setTimeout(() => {
      if (editorRef.value) {
        editorRef.value.focus();
      }
    }, 300);
  };
  
  const beforeImageUpload = (file) => {
    const isImage = file.type.startsWith('image/');
    const isLt2M = file.size / 1024 / 1024 < 2;
  
    if (!isImage) {
      ElMessage.error('上传封面图片只能是图片格式!');
      return false;
    }
    if (!isLt2M) {
      ElMessage.error('上传封面图片大小不能超过 2MB!');
      return false;
    }
    return true;
  };
  
  const handleFileUpload = (res, file, fileList) => {
    console.log('Upload response:', res);
    console.log('Upload URL:', `${baseUrl}/files/upload`); // 调试用
    if (res.code === '200') {
      // 只保存图片URL，而不是整个对象
      if (typeof res.data === 'string') {
        data.form.img = res.data;
      } else if (res.data && typeof res.data.url === 'string') {
        // 如果返回的是对象且包含url属性
        data.form.img = res.data.url;
      } else if (res.data && typeof res.data.path === 'string') {
        // 如果返回的是对象且包含path属性
        data.form.img = res.data.path;
      } else {
        // 如果无法提取URL，则记录错误
        console.error('无法从响应中提取图片URL:', res.data);
        ElMessage.warning('图片上传成功，但URL格式异常');
        // 尝试提取任何可能的字符串属性作为URL
        const extractedUrl = extractUrlFromObject(res.data);
        if (extractedUrl) {
          data.form.img = extractedUrl;
        } else {
          // 最后的备选方案，但可能会导致字段过长
          data.form.img = JSON.stringify(res.data).substring(0, 255); // 限制长度
        }
      }
      ElMessage.success('封面上传成功');
    } else {
      ElMessage.error(res.msg || '封面上传失败');
      fileList.value = fileList.value.filter(item => item.uid !== file.uid);
    }
  };
  
  // 辅助函数：尝试从对象中提取URL
  const extractUrlFromObject = (obj) => {
    if (!obj || typeof obj !== 'object') return null;
    
    // 常见的URL字段名
    const urlFields = ['url', 'path', 'src', 'link', 'href', 'location', 'address'];
    
    for (const field of urlFields) {
      if (typeof obj[field] === 'string' && obj[field].length > 0) {
        return obj[field];
      }
    }
    
    // 递归检查嵌套对象
    for (const key in obj) {
      if (typeof obj[key] === 'object' && obj[key] !== null) {
        const nestedUrl = extractUrlFromObject(obj[key]);
        if (nestedUrl) return nestedUrl;
      }
    }
    
    return null;
  };
  
  const handleUploadError = (error) => {
    ElMessage.error('上传失败：' + (error.message || '服务器错误'));
    console.error('Upload error:', error);
  };
  
  // 添加管理员表单验证规则
  const adminRules = {
    status: [
      { required: true, message: '请选择审核状态', trigger: 'change' }
    ]
  };
  
  // 标签表单验证规则
  const tagRules = {
    tag: [
      { required: true, message: '请输入宣传类型名称', trigger: 'blur' },
      { min: 2, max: 50, message: '类型名称长度在 2 到 50 个字符', trigger: 'blur' }
    ]
  };
  
  // 修改保存方法
  const save = async () => {
    if (!formRef.value) return;
    
    try {
      await formRef.value.validate();
      
      data.submitting = true;
      
      if (data.user.role === 'ADMIN') {
        // 管理员只更新状态
        const res = await request.put(`/propagate/audit/${data.form.id}`, null, {
          params: {
            status: data.form.status,
            rejectReason: data.form.status === 'REJECTED' ? data.form.rejectReason : undefined
          }
        });
        
        if (res.code === '200') {
          ElMessage.success('审核状态更新成功');
          data.formVisible = false;
          load();
        } else {
          ElMessage.error(res.msg || '审核失败');
        }
      } else {
        // 医生的保存逻辑保持不变
        const isUpdate = !!data.form.id;
        const api = isUpdate ? '/propagate/update' : '/propagate/add';
        const method = isUpdate ? 'put' : 'post';
        
        const res = await request[method](api, data.form);
        
        if (res.code === '200') {
          ElMessage({
            type: 'success',
            message: isUpdate ? '宣传信息更新成功' : '宣传信息添加成功',
            duration: 2000
          });
          data.formVisible = false;
          load();
        } else {
          ElMessage.error(res.msg || '操作失败');
        }
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
      
      const res = await request.delete('/propagate/delete/' + id);
      
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
        `确定要删除选中的 ${data.ids.length} 条宣传信息吗？删除后数据无法恢复。`, 
        '批量删除确认', 
        { 
          type: 'warning',
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          confirmButtonClass: 'el-button--danger'
        }
      );
      
      const res = await request.delete("/propagate/delete/batch", { data: data.ids });
      
      if (res.code === '200') {
        ElMessage.success(`成功删除 ${data.ids.length} 条宣传信息`);
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
    data.doctorName = '';
    data.searchStatus = null;
    data.pageNum = 1;
    load();
  };
  
  const viewContent = (content) => {
    data.viewContent = content;
    data.viewVisible = true;
  };
  
  // 确保baseUrl正确设置
  const uploadUrl = computed(() => `${baseUrl}/files/upload`);
  
  console.log('Propagate component baseUrl:', baseUrl);
  console.log('Upload URL:', uploadUrl.value);
  
  // 获取状态类型
  const getStatusType = (status) => {
    const statusMap = {
      'PENDING': 'warning',
      'APPROVED': 'success',
      'REJECTED': 'danger'
    };
    return statusMap[status] || 'info';
  };
  
  // 获取状态文本
  const getStatusText = (status) => {
    const statusMap = {
      'PENDING': '待审核',
      'APPROVED': '已通过',
      'REJECTED': '已拒绝'
    };
    return statusMap[status] || '未知';
  };
  
  // 处理审核
  const handleAudit = (row) => {
    data.currentAudit = row;
    data.auditForm.status = 'APPROVED';
    data.auditForm.rejectReason = '';
    data.auditVisible = true;
  };
  
  // 提交审核
  const submitAudit = async () => {
    try {
      if (data.auditForm.status === 'REJECTED' && !data.auditForm.rejectReason) {
        ElMessage.warning('请输入拒绝原因');
        return;
      }
      
      data.submitting = true;
      const res = await request.put(`/propagate/audit/${data.currentAudit.id}`, null, {
        params: {
          status: data.auditForm.status,
          rejectReason: data.auditForm.rejectReason
        }
      });
      
      if (res.code === '200') {
        ElMessage.success('审核成功');
        data.auditVisible = false;
        load(); // 重新加载列表
      } else {
        ElMessage.error(res.msg || '审核失败');
      }
    } catch (error) {
      console.error('Audit error:', error);
      ElMessage.error('审核失败: ' + error.message);
    } finally {
      data.submitting = false;
    }
  };
  
  // 打开宣传类型管理对话框
  const openTagManager = () => {
    data.tagManagerVisible = true;
    loadTags();
  };
  
  // 加载宣传类型列表
  const loadTags = async () => {
    data.tagsLoading = true;
    try {
      const res = await request.get('/propagate/tags/list');
      if (res.code === '200') {
        data.tags = res.data || [];
        data.tagOptions = res.data || [];
      } else {
        ElMessage.error(res.msg || '加载宣传类型失败');
      }
    } catch (error) {
      console.error('Failed to load tags:', error);
      ElMessage.error('加载宣传类型失败，请检查网络连接');
    } finally {
      data.tagsLoading = false;
    }
  };
  
  // 处理添加宣传类型
  const handleAddTag = () => {
    data.tagForm = {};
    data.tagFormVisible = true;
  };
  
  // 处理编辑宣传类型
  const handleEditTag = (row) => {
    data.tagForm = JSON.parse(JSON.stringify(row));
    data.tagFormVisible = true;
  };
  
  // 保存宣传类型
  const saveTag = async () => {
    if (!tagFormRef.value) return;
    
    try {
      await tagFormRef.value.validate();
      
      data.tagSubmitting = true;
      
      const isUpdate = !!data.tagForm.id;
      const api = isUpdate ? '/propagate/tags/update' : '/propagate/tags/add';
      const method = isUpdate ? 'put' : 'post';
      
      const res = await request[method](api, data.tagForm);
      
      if (res.code === '200') {
        ElMessage({
          type: 'success',
          message: isUpdate ? '宣传类型更新成功' : '宣传类型添加成功',
          duration: 2000
        });
        data.tagFormVisible = false;
        loadTags(); // 重新加载标签列表
      } else {
        ElMessage.error(res.msg || '操作失败');
      }
    } catch (error) {
      console.error('Form validation or submission error:', error);
      if (error?.message) {
        ElMessage.error(error.message);
      }
    } finally {
      data.tagSubmitting = false;
    }
  };
  
  // 处理删除宣传类型
  const handleDeleteTag = async (id) => {
    try {
      await ElMessageBox.confirm(
        '删除后数据无法恢复，您确定删除该宣传类型吗？', 
        '删除确认', 
        { 
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          confirmButtonClass: 'el-button--danger'
        }
      );
      
      const res = await request.delete('/propagate/tags/delete/' + id);
      
      if (res.code === '200') {
        ElMessage.success("删除成功");
        loadTags(); // 重新加载标签列表
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
  
  // 加载标签选项
  const loadTagOptions = async () => {
    try {
      const res = await request.get('/propagate/tags/list');
      if (res.code === '200') {
        data.tagOptions = res.data || [];
      } else {
        ElMessage.error(res.msg || '加载宣传类型失败');
      }
    } catch (error) {
      console.error('Failed to load tag options:', error);
      ElMessage.error('加载宣传类型失败，请检查网络连接');
    }
  };
  
  // Load data when component is mounted
  load();
  </script>
  
  <style scoped>
  .propagate-container {
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
  
  /* Editor styles */
  :deep(.w-e-text-container) {
    background-color: white;
  }
  
  :deep(.w-e-toolbar) {
    background-color: #f9fafb;
    border-bottom: 1px solid #e5e7eb;
  }
  
  /* Rich text content view styles */
  :deep(.prose) {
    font-family: system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
    line-height: 1.6;
  }
  
  :deep(.prose img) {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
  }
  
  :deep(.prose h1, .prose h2, .prose h3) {
    color: #2A5C8A;
    margin-top: 1.5em;
    margin-bottom: 0.5em;
  }
  </style>