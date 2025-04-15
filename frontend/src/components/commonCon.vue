<link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet"></link>
<template>
  <div class="flex h-screen bg-gray-50 p-5 gap-5 relative">
    <!-- 左侧对话列表 -->
    <div
      class="flex flex-col bg-white rounded-xl border border-gray-200 shadow-sm transition-all duration-300 h-[calc(100vh-40px)] overflow-hidden"
      :class="collapsed ? 'w-20' : 'w-64'"
    >
      <div class="p-4 border-b border-gray-100 flex-shrink-0">
        <div class="flex justify-between items-center mb-3">
          <span v-if="!collapsed" class="text-base font-semibold text-gray-900">对话列表</span>
          <button
            @click="toggleCollapse"
            class="text-gray-500 hover:text-blue-500 transition-colors p-1"
          >
            <MenuFoldOutlined v-if="!collapsed" class="text-lg" />
            <MenuUnfoldOutlined v-else class="text-lg" />
          </button>
        </div>
        <button
          @click="addChart"
          class="w-full h-10 rounded-md bg-blue-500 hover:bg-blue-600 active:bg-blue-700 transition-colors flex items-center justify-center gap-2 text-white font-medium"
          :disabled="creatingConversation"
        >
          <PlusOutlined class="text-lg" />
          <span v-if="!collapsed">{{ creatingConversation ? '创建中...' : '新增对话' }}</span>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto p-2">
        <template v-if="loadingConversations">
          <!-- 骨架屏加载状态 -->
          <div class="space-y-2">
            <div v-for="i in 3" :key="'skeleton-'+i" class="h-12 bg-gray-100 rounded-md animate-pulse"></div>
          </div>
        </template>
        <template v-else>
          <a-empty
            v-if="!safeConversations.length"
            description="暂无对话"
            :image="Empty.PRESENTED_IMAGE_SIMPLE"
            class="my-auto"
          />
          <div v-else class="space-y-1">
            <div
              v-for="item in safeConversations"
              :key="item.id"
              @click="goPath(item)"
              class="relative rounded-md transition-colors cursor-pointer p-3 flex items-center gap-2 group conversation-item"
              :class="{
                'bg-blue-50': item.id === currentConversationId,
                'hover:bg-gray-50': item.id !== currentConversationId
              }"
            >
              <MessageOutlined class="text-gray-500 flex-shrink-0 text-base" />
              <div class="flex-1 min-w-0 flex items-center gap-2 overflow-hidden">
                <span
                  class="truncate text-sm"
                  :class="{
                    'text-blue-500': item.id === currentConversationId,
                    'text-gray-800': item.id !== currentConversationId,
                    'font-medium': item.unreadCount > 0
                  }"
                >
                  {{ getChatTitle(item) }}
                </span>

                <!-- 操作按钮 -->
                <div
                  class="flex items-center gap-1 ml-2"
                  :class="{
                    'opacity-0 group-hover:opacity-100': !collapsed,
                    'flex': collapsed
                  }"
                >
                  <button
                    @click.stop="openEditModal(item, $event)"
                    class="text-gray-400 hover:text-blue-600 p-1 transition-colors"
                    title="重命名"
                  >
                    <FormOutlined class="text-sm" />
                  </button>
                  <button
                    @click.stop="showDeleteConfirm(item.id, $event)"
                    class="text-gray-400 hover:text-red-500 p-1 transition-colors"
                    title="删除"
                  >
                    <DeleteOutlined class="text-sm" />
                  </button>
                </div>
              </div>

              <!-- 右侧时间信息 -->
              <div v-if="!collapsed" class="flex items-center gap-2 ml-2">
                <span v-if="item.updateTime" class="text-xs text-gray-400 whitespace-nowrap">
                  {{ formatTime(item.updateTime) }}
                </span>
                <span
                  v-if="item.unreadCount > 0"
                  class="bg-red-500 text-white rounded-full min-w-[18px] h-[18px] flex items-center justify-center text-xs px-1"
                >
                  {{ item.unreadCount }}
                </span>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="flex-1 overflow-hidden bg-white rounded-xl border border-gray-200 shadow-sm h-[calc(100vh-40px)]">
      <router-view v-if="isRouterViewReady && currentConversationId" />
      <div v-else class="h-full flex items-center justify-center">
        <a-empty description="请选择或创建一个对话" />
      </div>
    </div>

    <!-- 重命名弹窗 - 修复问题 -->
    <a-modal
      v-model:visible="editModalVisible"
      title="重命名对话"
      :maskClosable="true"
      :destroyOnClose="true"
      @cancel="closeModal"
      :width="400"
      :okButtonProps="{ loading: updatingTitle,
                         style: { background: '#1890ff', borderColor: '#1890ff' }}"
      :okText="'确定'"
      :cancelText="'取消'"
      @ok="updateTitle"
    >
      <div class="p-2">
        <a-form layout="vertical">
          <a-form-item
            label="对话名称"
            :validateStatus="titleError ? 'error' : ''"
            :help="titleError"
          >
            <a-input
              v-model:value="editTitleValue"
              ref="titleInput"
              placeholder="请输入对话名称(最多20个字符)"
              :maxLength="20"
              @pressEnter="updateTitle"
              allowClear
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { mapGetters, mapState, mapActions, mapMutations } from 'vuex'
import { addChatLog } from '@/api/business/chatLog'
import { generateRandomString } from "@/utils/Obj"
import { message, Modal, Form, Input, Button } from 'ant-design-vue'
import {
  DeleteOutlined,
  FormOutlined,
  PlusOutlined,
  MessageOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  ExclamationCircleFilled
} from '@ant-design/icons-vue'
import { Empty } from 'ant-design-vue'
import { nextTick, createVNode } from 'vue'
import dayjs from 'dayjs'

export default {
  name: "ConversationLayout",
  components: {
    DeleteOutlined,
    FormOutlined,
    PlusOutlined,
    MessageOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    ExclamationCircleFilled,
    AForm: Form,
    AFormItem: Form.Item,
    AInput: Input,
    AButton: Button,
    AModal: Modal,
    AEmpty: Empty
  },
  data() {
    return {
      collapsed: localStorage.getItem('collapsed') === 'true',
      editModalVisible: false,
      editTitleValue: '',
      editTitleId: '',
      isRouterViewReady: false,
      Empty,
      creatingConversation: false,
      updatingTitle: false,
      loadingConversations: false,
      titleError: ''
    };
  },
  watch: {
    collapsed(val) {
      localStorage.setItem('collapsed', val);
    },
    currentConversationId(newId) {
      if (newId && this.$route.params.id !== newId) {
        this.isRouterViewReady = false;
        this.$router.push({
          name: 'ai',
          params: { id: newId.toString() }
        })
          .then(() => {
            this.$nextTick(() => {
              this.isRouterViewReady = true;
            });
          })
          .catch(err => {
            if (!err.name || !err.name.includes('NavigationDuplicated')) {
              console.error('路由跳转失败:', err);
            } else {
              this.isRouterViewReady = true;
            }
          });
      }
    },
    '$route.params.id': {
      immediate: true,
      handler(newId) {
        if (newId && newId !== this.currentConversationId) {
          this.SET_CURRENT_CONVERSATION(newId);
          this.$nextTick(() => {
            this.isRouterViewReady = true;
          });
        }
      }
    }
  },
  computed: {
    ...mapGetters(['getAllConversations']),
    ...mapState(['currentConversationId']),
    safeConversations() {
      return this.getAllConversations || [];
    }
  },
  methods: {
    ...mapMutations(['SET_CURRENT_CONVERSATION']),
    ...mapActions(['updateConversationTitle', 'loadConversations', 'deleteConversation']),

    toggleCollapse() {
      this.collapsed = !this.collapsed;
    },

    goPath(item) {
      if (item.id !== this.currentConversationId) {
        this.SET_CURRENT_CONVERSATION(item.id);
      }
    },

    getChatTitle(item) {
      return this.collapsed ? '' : item.title || `会话 ${dayjs(item.createTime).format('MM-DD')}`;
    },

    formatTime(time) {
      return dayjs(time).format('MM-DD HH:mm');
    },

    async addChart() {
      if (this.creatingConversation) return;

      try {
        this.creatingConversation = true;
        message.loading({ content: '创建对话中...', key: 'createConversation' });

        // 获取用户ID
        const userId = this.getUserId();
        const newConversation = {
          id: generateRandomString(8),
          title: `对话 ${dayjs().format('MM-DD HH:mm')}`,
          conversation: [],
          userId: userId,
          createTime: new Date().toISOString(),
          updateTime: new Date().toISOString()
        };

        await addChatLog(newConversation);
        await this.loadConversations();

        // 设置新对话为当前对话
        this.SET_CURRENT_CONVERSATION(newConversation.id);

        message.success({ content: "创建成功", key: 'createConversation' });
      } catch (error) {
        console.error('创建对话失败:', error);
        message.error({
          content: error.message || "创建失败，请重试",
          key: 'createConversation'
        });
      } finally {
        this.creatingConversation = false;
      }
    },

    getUserId() {
      try {
        const userInfo = JSON.parse(localStorage.getItem('xm-user'));
        return userInfo?.id || 'temp_user';
      } catch (e) {
        console.error('解析用户信息失败:', e);
        return 'temp_user';
      }
    },

    openEditModal(item, event) {
      if (event) {
        event.stopPropagation();
        // 添加点击动画
        const button = event.currentTarget;
        button.classList.add('animate-pulse');
        setTimeout(() => button.classList.remove('animate-pulse'), 300);
      }

      this.editTitleId = item.id;
      this.editTitleValue = item.title || '';
      this.titleError = '';
      this.editModalVisible = true;

      this.$nextTick(() => {
        if (this.$refs.titleInput) {
          this.$refs.titleInput.focus();
        }
      });
    },

    async updateTitle() {
      if (!this.editTitleValue.trim()) {
        this.titleError = '对话名称不能为空';
        return;
      }

      if (this.editTitleValue.length > 20) {
        this.titleError = '对话名称不能超过20个字符';
        return;
      }

      this.updatingTitle = true;

      try {
        await this.updateConversationTitle({
          id: this.editTitleId,
          title: this.editTitleValue.trim()
        });

        message.success('重命名成功');
        this.closeModal();
      } catch (error) {
        console.error('更新对话标题失败:', error);
        this.titleError = '操作失败，请重试';
      } finally {
        this.updatingTitle = false;
      }
    },

    closeModal() {
      this.editModalVisible = false;
      this.editTitleValue = '';
      this.editTitleId = '';
      this.titleError = '';
    },

    async initConversations() {
      try {
        this.loadingConversations = true;
        await this.loadConversations();

        // 如果没有当前对话，自动选择第一个
        if (!this.currentConversationId && this.safeConversations.length > 0) {
          this.SET_CURRENT_CONVERSATION(this.safeConversations[0].id);
          this.$nextTick(() => {
            this.isRouterViewReady = true;
          });
        } else if (this.currentConversationId) {
          this.$nextTick(() => {
            this.isRouterViewReady = true;
          });
        }
      } catch (error) {
        console.error('初始化对话列表失败:', error);
        message.error("加载对话列表失败，请刷新页面重试");
      } finally {
        this.loadingConversations = false;
      }
    },

    showDeleteConfirm(id, event) {
      if (event) event.stopPropagation();

      Modal.confirm({
        title: '确定要删除这个对话吗？',
        icon: createVNode(ExclamationCircleFilled),
        content: '删除后将无法恢复，请谨慎操作。',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => this.confirmDelete(id),
        onCancel() {},
      });
    },

    async confirmDelete(id) {
      try {
        await this.deleteConversation(id);
        message.success("删除成功");

        // 如果删除的是当前对话，清空当前对话ID
        if (id === this.currentConversationId) {
          this.SET_CURRENT_CONVERSATION(null);
        }
      } catch (error) {
        console.error('删除对话失败:', error);
        message.error(error.message || "删除失败");
      }
    },

    handleKeyDown(e) {
      if (this.editModalVisible && e.key === 'Escape') {
        this.closeModal();
      }
    }
  },
  mounted() {
    // 初始化对话列表
    this.initConversations();

    // 添加键盘事件监听
    window.addEventListener('keydown', this.handleKeyDown);
  },
  beforeUnmount() {
    // 移除键盘事件监听
    window.removeEventListener('keydown', this.handleKeyDown);
  }
};
</script>

<style>
/* 保留必要的 ant-design 组件样式 */
.context-menu .ant-dropdown-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
}

.context-menu .ant-dropdown-menu-item.delete-option {
  color: #ff4d4f;
}

/* 点击动画 */
.animate-pulse {
  animation: pulse 0.3s ease;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 移动端响应式 */
@media (max-width: 640px) {
  .flex.h-screen {
    flex-direction: column;
    height: auto;
    min-height: 100vh;
  }

  .flex.h-screen > div {
    width: 100% !important;
    height: auto !important;
  }

  .conversation-item {
    padding: 0.75rem 0.5rem;
  }
}

/* 对话项悬停效果 */
.conversation-item {
  transition: all 0.2s ease;
}

.conversation-item:hover .text-gray-400 {
  opacity: 1;
}

/* 弹窗样式增强 */
.modal-content {
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}
</style>