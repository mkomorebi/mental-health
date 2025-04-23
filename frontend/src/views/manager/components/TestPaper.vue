<template>
    <div class="test-paper-container">
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
              placeholder="请输入试卷名称查询" 
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
          
          <!-- 添加试卷状态筛选 -->
          <div class="w-32">
            <el-select
              v-model="data.status"
              placeholder="试卷状态"
              class="w-full"
              clearable
            >
              <el-option label="待审核" value="待审核" />
              <el-option label="审核通过" value="审核通过" />
              <el-option label="审核拒绝" value="审核拒绝" />
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
        <div class="flex flex-wrap items-center gap-3 mt-4 pt-3 border-t border-gray-100" v-if="data.user.role === 'DOCTOR'">
          <el-button 
            type="primary" 
            @click="handleAdd(1)"
            class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            手动出题
          </el-button>
          <el-button 
            type="success" 
            @click="handleAdd(2)"
            class="bg-green-500 hover:bg-green-600 border-green-500 hover:border-green-600 text-white"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 inline-block" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z"></path>
              <path d="m9 12 2 2 4-4"></path>
            </svg>
            自动出题
          </el-button>
          <el-button 
            type="danger" 
            @click="delBatch"
            :disabled="!data.ids.length"
            class="bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600 text-white disabled:bg-gray-300 disabled:border-gray-300"
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
          <el-table-column prop="img" label="试卷封面" width="100" align="center">
            <template #default="scope">
              <el-image 
                v-if="scope.row.img"
                class="w-20 h-20 rounded-md object-cover mx-auto"
                :src="scope.row.img" 
                :preview-src-list="[scope.row.img]" 
                preview-teleported
                fit="cover"
              />
              <div v-else class="w-20 h-20 rounded-md bg-gray-200 flex items-center justify-center mx-auto">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                  <polyline points="14 2 14 8 20 8"></polyline>
                  <line x1="16" y1="13" x2="8" y2="13"></line>
                  <line x1="16" y1="17" x2="8" y2="17"></line>
                  <polyline points="10 9 9 9 8 9"></polyline>
                </svg>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="试卷名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="content" label="试卷简介" min-width="150" show-overflow-tooltip />
          <el-table-column prop="typeName" label="试卷分类" min-width="120" />
          <el-table-column prop="doctorName" label="医生姓名" min-width="120" />
          <el-table-column prop="doctorAvatar" label="医生头像" width="80" align="center">
            <template #default="scope">
              <el-image 
                v-if="scope.row.doctorAvatar"
                class="w-10 h-10 rounded-full object-cover mx-auto"
                :src="scope.row.doctorAvatar" 
                :preview-src-list="[scope.row.doctorAvatar]" 
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
          <el-table-column prop="num" label="题目数量" width="100" align="center" />
          <el-table-column prop="score" label="试卷总分" width="100" align="center" />
          <el-table-column prop="time" label="发布时间" min-width="150" />
          <el-table-column prop="testNum" label="测试人数" width="100" align="center" />
          <el-table-column prop="status" label="试卷状态" width="100" align="center">
            <template #default="scope">
              <el-tag 
                v-if="scope.row.status === '待审核'" 
                type="warning"
                effect="light"
                class="w-16 text-center"
              >
                {{ scope.row.status }}
              </el-tag>
              <el-tag 
                v-else-if="scope.row.status === '审核通过'" 
                type="success"
                effect="light"
                class="w-16 text-center"
              >
                {{ scope.row.status }}
              </el-tag>
              <el-tag 
                v-else-if="scope.row.status === '审核拒绝'" 
                type="danger"
                effect="light"
                class="w-16 text-center"
              >
                {{ scope.row.status }}
              </el-tag>
              <el-tag 
                v-else 
                type="info"
                effect="light"
                class="w-16 text-center"
              >
                未知
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ids" label="题目信息" width="150" align="center">
            <template #default="scope">
              <el-button 
                @click="viewTopics(scope.row.topicList)" 
                type="primary"
                class="bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
              >
                查看试卷题目
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <div class="flex items-center space-x-2">
                <el-button 
                  type="primary" 
                  circle 
                  @click="handleEdit(scope.row)"
                  v-if="data.user.role === 'ADMIN'"
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
          <p class="mt-2 text-gray-500">暂无试卷数据</p>
          <el-button 
            v-if="data.user.role === 'DOCTOR'"
            type="primary" 
            @click="handleAdd(1)" 
            class="mt-4 bg-[#2A5C8A] hover:bg-[#1e4266] border-[#2A5C8A] hover:border-[#1e4266]"
          >
            添加第一份试卷
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
        title="试卷信息" 
        width="70%" 
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
          <el-form-item prop="title" label="试卷名称" required>
            <el-input 
              v-model="data.form.title" 
              placeholder="请输入试卷名称"
              maxlength="100"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="img" label="试卷封面" required>
            <div class="flex items-center">
              <Upload v-model="data.form.img" />
              
              <div v-if="data.form.img" class="ml-4 flex items-center text-sm text-gray-500">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-500 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                  <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
                封面已上传
              </div>
            </div>
          </el-form-item>
          
          <el-form-item prop="content" label="试卷介绍" required>
            <el-input 
              type="textarea" 
              :rows="4" 
              v-model="data.form.content" 
              placeholder="请输入试卷介绍"
              maxlength="500"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="typeId" label="试卷分类" required>
            <el-select 
              v-model="data.form.typeId" 
              placeholder="请选择心理分类" 
              class="w-full" 
              @change="loadTopic"
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
          
          <el-form-item prop="idList" label="试卷题目" v-if="data.form.flag === 1" required>
            <el-select 
              v-model="data.form.idList" 
              multiple 
              placeholder="请选择题目" 
              class="w-full" 
              @change="calculateNum"
              filterable
            >
              <el-option 
                v-for="item in data.topicData" 
                :key="item.id" 
                :label="item.title" 
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item prop="num" label="题目数量" v-if="data.form.flag === 1">
            <el-input 
              v-model="data.form.num" 
              placeholder="0" 
              disabled
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="num" label="题目数量" v-if="data.form.flag === 2" required>
            <el-input-number 
              v-model="data.form.num" 
              :min="1"
              :max="50"
              @change="calculateTotal"
              class="w-48"
            />
          </el-form-item>
          
          <el-form-item prop="score" label="试卷总分">
            <el-input 
              v-model="data.form.score" 
              placeholder="0" 
              disabled
            ></el-input>
          </el-form-item>
          
          <div class="bg-gray-50 p-4 rounded-lg mb-4">
            <h3 class="text-lg font-medium text-gray-800 mb-3">分数区间设置</h3>
            
            <el-form-item prop="aLeft" label="底部区间左边界" required>
              <el-input-number 
                v-model="data.form.aLeft" 
                class="w-32" 
                :min="0"
                :max="data.form.aRight ? data.form.aRight - 1 : undefined"
                @change="validateRanges"
              />
            </el-form-item>
            
            <el-form-item prop="aRight" label="底部区间右边界" required>
              <el-input-number 
                v-model="data.form.aRight" 
                class="w-32" 
                :min="data.form.aLeft ? data.form.aLeft + 1 : 1"
                :max="data.form.bLeft ? data.form.bLeft - 1 : undefined"
                @change="validateRanges"
              />
            </el-form-item>
            
            <el-form-item prop="aAnswer" label="底部区间解答" required>
              <el-input 
                type="textarea" 
                :rows="3" 
                v-model="data.form.aAnswer" 
                placeholder="请输入底部区间解答"
                maxlength="500"
                show-word-limit
              ></el-input>
            </el-form-item>
            
            <el-form-item prop="bLeft" label="中部区间左边界" required>
              <el-input-number 
                v-model="data.form.bLeft" 
                class="w-32" 
                :min="data.form.aRight ? data.form.aRight + 1 : 1"
                :max="data.form.bRight ? data.form.bRight - 1 : undefined"
                @change="validateRanges"
              />
            </el-form-item>
            
            <el-form-item prop="bRight" label="中部区间右边界" required>
              <el-input-number 
                v-model="data.form.bRight" 
                class="w-32" 
                :min="data.form.bLeft ? data.form.bLeft + 1 : 2"
                :max="data.form.cLeft ? data.form.cLeft - 1 : undefined"
                @change="validateRanges"
              />
            </el-form-item>
            
            <el-form-item prop="bAnswer" label="中部区间解答" required>
              <el-input 
                type="textarea" 
                :rows="3" 
                v-model="data.form.bAnswer" 
                placeholder="请输入中部区间解答"
                maxlength="500"
                show-word-limit
              ></el-input>
            </el-form-item>
            
            <el-form-item prop="cLeft" label="顶部区间左边界" required>
              <el-input-number 
                v-model="data.form.cLeft" 
                class="w-32" 
                :min="data.form.bRight ? data.form.bRight + 1 : 2"
                :max="data.form.cRight ? data.form.cRight - 1 : undefined"
                @change="validateRanges"
              />
            </el-form-item>
            
            <el-form-item prop="cRight" label="顶部区间右边界" required>
              <el-input-number 
                v-model="data.form.cRight" 
                class="w-32" 
                :min="data.form.cLeft ? data.form.cLeft + 1 : 3"
                :max="data.form.score"
                @change="validateRanges"
              />
            </el-form-item>
            
            <el-form-item prop="cAnswer" label="顶部区间解答" required>
              <el-input 
                type="textarea" 
                :rows="3" 
                v-model="data.form.cAnswer" 
                placeholder="请输入顶部区间解答"
                maxlength="500"
                show-word-limit
              ></el-input>
            </el-form-item>
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
  
      <!-- Topics Dialog -->
      <el-dialog 
        v-model="data.topicVisible" 
        title="试卷题目信息" 
        width="80%" 
        destroy-on-close
        :close-on-click-modal="true"
      >
        <el-table 
          :data="data.topicList"
          stripe
          border
          class="w-full"
        >
          <el-table-column prop="title" label="题目名称" min-width="250" show-overflow-tooltip />
          <el-table-column prop="aName" label="选项A" min-width="150" show-overflow-tooltip />
          <el-table-column prop="aScore" label="A分数" width="80" align="center" />
          <el-table-column prop="bName" label="选项B" min-width="150" show-overflow-tooltip />
          <el-table-column prop="bScore" label="B分数" width="80" align="center" />
          <el-table-column prop="cName" label="选项C" min-width="150" show-overflow-tooltip />
          <el-table-column prop="cScore" label="C分数" width="80" align="center" />
          <el-table-column prop="dName" label="选项D" min-width="150" show-overflow-tooltip />
          <el-table-column prop="dScore" label="D分数" width="80" align="center" />
        </el-table>
        <template #footer>
          <div class="flex justify-end">
            <el-button @click="data.topicVisible = false">关 闭</el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- Audit Dialog -->
      <el-dialog 
        v-model="data.formVisible2" 
        title="试卷审核" 
        width="500px" 
        destroy-on-close
        :close-on-click-modal="false"
      >
        <el-form 
          ref="auditFormRef" 
          :model="data.form" 
          :rules="auditRules" 
          label-width="100px" 
          class="p-4"
        >
          <el-form-item prop="status" label="审核状态" required>
            <el-radio-group v-model="data.form.status">
              <el-radio-button label="待审核">待审核</el-radio-button>
              <el-radio-button label="审核通过">审核通过</el-radio-button>
              <el-radio-button label="审核拒绝">审核拒绝</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="flex justify-end gap-2">
            <el-button @click="data.formVisible2 = false">取 消</el-button>
            <el-button 
              type="primary" 
              @click="update"
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
  const auditFormRef = ref(null);
  const baseUrl = import.meta.env.VITE_BASE_URL;
  
  // 获取当前用户信息
  const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  const headers = { token: user.token };
  
  const rules = {
    title: [
      { required: true, message: '请输入试卷名称', trigger: 'blur' },
      { min: 2, max: 100, message: '试卷名称长度在 2 到 100 个字符', trigger: 'blur' }
    ],
    img: [
      { required: true, message: '请上传试卷封面', trigger: 'change' }
    ],
    content: [
      { required: true, message: '请输入试卷介绍', trigger: 'blur' },
      { min: 10, max: 500, message: '试卷介绍长度在 10 到 500 个字符', trigger: 'blur' }
    ],
    typeId: [
      { required: true, message: '请选择试卷分类', trigger: 'change' }
    ],
    idList: [
      { required: true, message: '请选择试卷题目', trigger: 'change' }
    ],
    num: [
      { required: true, message: '请输入题目数量', trigger: 'blur' }
    ],
    aLeft: [
      { required: true, message: '请输入底部区间左边界', trigger: 'blur' }
    ],
    aRight: [
      { required: true, message: '请输入底部区间右边界', trigger: 'blur' }
    ],
    bLeft: [
      { required: true, message: '请输入中部区间左边界', trigger: 'blur' }
    ],
    bRight: [
      { required: true, message: '请输入中部区间右边界', trigger: 'blur' }
    ],
    cLeft: [
      { required: true, message: '请输入顶部区间左边界', trigger: 'blur' }
    ],
    cRight: [
      { required: true, message: '请输入顶部区间右边界', trigger: 'blur' }
    ],
    aAnswer: [
      { required: true, message: '请输入底部区间解答', trigger: 'blur' }
    ],
    bAnswer: [
      { required: true, message: '请输入中部区间解答', trigger: 'blur' }
    ],
    cAnswer: [
      { required: true, message: '请输入顶部区间解答', trigger: 'blur' }
    ]
  };
  
  const auditRules = {
    status: [
      { required: true, message: '请选择审核状态', trigger: 'change' }
    ]
  };
  
  const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    formVisible: false,
    formVisible2: false,
    topicVisible: false,
    form: {},
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    title: '',
    ids: [],
    typeName: '',
    status: '',
    typeData: [],
    topicData: [],
    bRightMax: 0,
    topicList: [],
    loading: false,
    submitting: false
  });
  
  const viewTopics = (list) => {
    data.topicList = list || [];
    data.topicVisible = true;
  };
  
  const calculateTotal = (num) => {
    if (!num) return;
    
    data.form.score = num * 10;
    // 重置分数区间，使用默认的60%和80%作为分界点
    data.form.aLeft = 0;
    data.form.aRight = Math.floor(data.form.score * 0.6); // 60%
    data.form.bLeft = data.form.aRight + 1;
    data.form.bRight = Math.floor(data.form.score * 0.8); // 80%
    data.form.cLeft = data.form.bRight + 1;
    data.form.cRight = data.form.score;
  };
  
  const calculateNum = (arr) => {
    if (!arr || !arr.length) return;
    
    data.form.num = arr.length;
    data.form.score = arr.length * 10;
    // 重置分数区间
    data.form.aLeft = 0;
    data.form.aRight = Math.floor(data.form.score * 0.6);
    data.form.bLeft = data.form.aRight + 1;
    data.form.bRight = Math.floor(data.form.score * 0.8);
    data.form.cLeft = data.form.bRight + 1;
    data.form.cRight = data.form.score;
  };
  
  const validateRanges = () => {
    try {
        // 确保所有值都存在
        if (!data.form.score) return;
        
        // 确保区间连续且不重叠
        if (data.form.aRight !== undefined) {
            data.form.bLeft = data.form.aRight + 1;
        }
        if (data.form.bRight !== undefined) {
            data.form.cLeft = data.form.bRight + 1;
        }
        
        // 确保最高分区间右边界等于总分
        data.form.cRight = data.form.score;
        
        // 验证区间的有效性
        if (data.form.aLeft >= data.form.aRight ||
            data.form.bLeft >= data.form.bRight ||
            data.form.cLeft >= data.form.cRight) {
            ElMessage.warning('分数区间设置错误：右边界必须大于左边界');
            return false;
        }
        
        return true;
    } catch (error) {
        console.error('验证分数区间时发生错误:', error);
        return false;
    }
  };
  
  const loadTopic = async (typeId) => {
    try {
      const res = await request.get('/topic/selectAll', {
        params: {
          typeId: typeId
        }
      });
      
      if (res.code === '200') {
        data.topicData = res.data || [];
      } else {
        ElMessage.error(res.msg || '加载题目数据失败');
      }
    } catch (error) {
      console.error('Failed to load topic data:', error);
      ElMessage.error('加载题目数据失败，请检查网络连接');
    }
  };
  
  const loadType = async () => {
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
    }
  };
  
  const load = async () => {
    data.loading = true;
    try {
      const res = await request.get('/testPaper/selectPage', {
        params: {
          pageNum: data.pageNum,
          pageSize: data.pageSize,
          title: data.title || undefined,
          typeName: data.typeName || undefined,
          status: data.status || undefined
        }
      });
      
      if (res.code === '200') {
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
      } else {
        ElMessage.error(res.msg || '加载试卷数据失败');
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
  
  const handleAdd = (flag) => {
    data.form = {
      flag: flag,
      aLeft: 0
    };
    data.formVisible = true;
  };
  
  const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row));
    data.formVisible2 = true;
  };
  
  const save = async () => {
    if (!formRef.value) return;
    
    try {
        await formRef.value.validate();
        
        // 验证分数区间
        if (!validateRanges()) {
            return;
        }
        
        // 设置分数区间字符串
        data.form.aRange = `${data.form.aLeft}~${data.form.aRight}`;
        data.form.bRange = `${data.form.bLeft}~${data.form.bRight}`;
        data.form.cRange = `${data.form.cLeft}~${data.form.cRight}`;
        
        data.submitting = true;
        
        const isUpdate = !!data.form.id;
        const api = isUpdate ? '/testPaper/update' : '/testPaper/add';
        const method = isUpdate ? 'put' : 'post';
        
        const res = await request[method](api, data.form);
        
        if (res.code === '200') {
            ElMessage({
                type: 'success',
                message: isUpdate ? '试卷更新成功' : '试卷添加成功',
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
  
  const update = async () => {
    if (!auditFormRef.value) return;
    
    try {
      await auditFormRef.value.validate();
      
      data.submitting = true;
      
      const res = await request.put('/testPaper/update', data.form);
      
      if (res.code === '200') {
        ElMessage({
          type: 'success',
          message: '试卷审核状态更新成功',
          duration: 2000
        });
        data.formVisible2 = false;
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
      
      const res = await request.delete('/testPaper/delete/' + id);
      
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
        `确定要删除选中的 ${data.ids.length} 份试卷吗？删除后数据无法恢复。`, 
        '批量删除确认', 
        { 
          type: 'warning',
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          confirmButtonClass: 'el-button--danger'
        }
      );
      
      const res = await request.delete("/testPaper/delete/batch", { data: data.ids });
      
      if (res.code === '200') {
        ElMessage.success(`成功删除 ${data.ids.length} 份试卷`);
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
    data.status = '';
    data.pageNum = 1;
    load();
  };
  
  // Load data when component is mounted
  onMounted(() => {
    load();
    loadType();
  });
  </script>
  
  <style scoped>
  .test-paper-container {
    width: 100%;
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    padding: 1rem;
    background-color: #f5f7fa;
  }
  
  /* Override Element Plus styles to match our design */
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