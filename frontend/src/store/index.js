import { createStore } from 'vuex'
import { getAllChatLogs, addChatLog, deleteChatLog, updateChatLog } from '@/api/business/chatLog'
import { generateRandomString } from "@/utils/Obj"

// 自动保存插件 - 负责在内容变更后自动保存到后端
const autoSavePlugin = (store) => {
  // 初始化 autoSaveTimers 对象
  if (!store.state.autoSaveTimers) {
    store.state.autoSaveTimers = {}
  }

  // 监听所有 mutation
  store.subscribe((mutation, state) => {
    // 只处理添加对话内容的 mutation
    if (mutation.type === 'ADD_CONVERSATION_CONTENT') {
      const conversationId = mutation.payload.id

      // 清除之前的定时器，避免重复保存
      if (state.autoSaveTimers && state.autoSaveTimers[conversationId]) {
        clearTimeout(state.autoSaveTimers[conversationId])
        delete state.autoSaveTimers[conversationId]
      }

      // 设置新的定时器，2秒后自动保存（防抖）
      const timer = setTimeout(() => {
        store.dispatch('saveConversation', conversationId)
            .then(() => {
              console.log(`对话 ${conversationId} 自动保存成功`)
            })
            .catch(error => {
              console.error(`对话 ${conversationId} 自动保存失败:`, error)
            })
            .finally(() => {
              // 保存完成后清除定时器引用
              if (state.autoSaveTimers) {
                delete state.autoSaveTimers[conversationId]
              }
            })
      }, 2000) // 2秒延迟

      // 确保 autoSaveTimers 已初始化
      if (!state.autoSaveTimers) {
        state.autoSaveTimers = {}
      }
      state.autoSaveTimers[conversationId] = timer
    }
  })
}

export default createStore({
  state: {
    conversations: [],        // 所有对话列表
    currentConversationId: null, // 当前选中的对话ID
    deepseekTime: 0,          // 深度搜索时间戳
    isLoading: false,         // 加载状态
    autoSaveTimers: {}        // 自动保存定时器记录
  },

  getters: {
    // 获取所有对话
    getAllConversations(state) {
      return state.conversations
    },

    // 获取当前对话ID
    getCurrentConversationId(state) {
      return state.currentConversationId
    },

    // 获取当前对话内容
    getCurrentConversation(state) {
      const currentConversation = state.conversations.find(
          conv => conv.id === state.currentConversationId
      )
      return currentConversation ? currentConversation.conversation || [] : []
    },

    // 获取加载状态
    isLoading(state) {
      return state.isLoading
    }
  },

  mutations: {
    // 设置加载状态
    SET_LOADING(state, isLoading) {
      state.isLoading = isLoading
    },

    // 设置对话列表
    SET_CONVERSATIONS(state, conversations) {
      state.conversations = conversations
    },

    // 设置当前对话
    SET_CURRENT_CONVERSATION(state, id) {
      state.currentConversationId = id
    },

    // 添加新对话
    ADD_CONVERSATION(state, conversation) {
      state.conversations.unshift(conversation)
      state.currentConversationId = conversation.id
    },

    // 删除对话
    REMOVE_CONVERSATION(state, id) {
      state.conversations = state.conversations.filter(conv => conv.id !== id)
      // 清除相关自动保存定时器
      if (state.autoSaveTimers && state.autoSaveTimers[id]) {
        clearTimeout(state.autoSaveTimers[id])
        delete state.autoSaveTimers[id]
      }
      // 如果删除的是当前对话，则切换到第一个对话或清空当前对话ID
      if (state.currentConversationId === id) {
        state.currentConversationId = state.conversations.length > 0
            ? state.conversations[0].id
            : null
      }
    },

    // 更新对话标题
    UPDATE_CONVERSATION_TITLE(state, { id, title, heartAnalysis }) {
      const conversation = state.conversations.find(conv => conv.id === id)
      if (conversation) {
        conversation.title = title
        if (heartAnalysis !== undefined) {
          conversation.heartAnalysis = heartAnalysis
        }
      }
    },

    // 添加对话内容（触发自动保存的关键 mutation）
    ADD_CONVERSATION_CONTENT(state, { id, content }) {
      const conversation = state.conversations.find(conv => conv.id === id)
      if (conversation) {
        if (!conversation.conversation) {
          conversation.conversation = []
        }
        conversation.conversation.push(content)
      }
    }
  },

  actions: {
    // 加载所有对话
    async loadConversations({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await getAllChatLogs()
        if (response.code === '200') {
          commit('SET_CONVERSATIONS', response.data || [])
          // 如果有对话但没有选中的对话，则选中第一个
          if (response.data && response.data.length > 0 && !this.state.currentConversationId) {
            commit('SET_CURRENT_CONVERSATION', response.data[0].id)
          }
          return { data: response.data || [] }
        } else {
          console.error('加载对话失败:', response.msg)
          return { data: [] }
        }
      } catch (error) {
        console.error('加载对话失败:', error)
        return { data: [] }
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // 创建新对话
    async createConversation({ commit }, userId) {
      commit('SET_LOADING', true)
      try {
        const newConversation = {
          id: generateRandomString(8),
          title: "新会话",
          conversation: [],
          userId: userId || ''
        }

        const response = await addChatLog(newConversation)
        if (response.code === '200') {
          commit('ADD_CONVERSATION', newConversation)
          return { success: true, conversation: newConversation }
        } else {
          console.error('创建对话失败:', response.msg)
          return { success: false, error: response.msg }
        }
      } catch (error) {
        console.error('创建对话失败:', error)
        return { success: false, error: error.message }
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // 删除对话
    async deleteConversation({ commit }, id) {
      commit('SET_LOADING', true)
      try {
        const response = await deleteChatLog(id)
        if (response.code === '200') {
          commit('REMOVE_CONVERSATION', id)
          return { success: true }
        } else {
          console.error('删除对话失败:', response.msg)
          return { success: false, error: response.msg }
        }
      } catch (error) {
        console.error('删除对话失败:', error)
        return { success: false, error: error.message }
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // 更新对话标题
    async updateConversationTitle({ commit }, data) {
      commit('SET_LOADING', true)
      try {
        const response = await updateChatLog(data)
        if (response.code === '200') {
          commit('UPDATE_CONVERSATION_TITLE', data)
          return { success: true }
        } else {
          console.error('更新对话标题失败:', response.msg)
          return { success: false, error: response.msg }
        }
      } catch (error) {
        console.error('更新对话标题失败:', error)
        return { success: false, error: error.message }
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // 添加对话内容（会自动触发保存）
    addConversationContent({ commit }, data) {
      commit('ADD_CONVERSATION_CONTENT', data)
    },

    // 保存对话到后端（被自动保存插件调用）
    async saveConversation({ state, dispatch }, conversationId) {
      try {
        const conversation = state.conversations.find(c => c.id === conversationId)
        if (!conversation) {
          console.warn(`未找到对话 ${conversationId}`)
          return { success: false, error: 'Conversation not found' }
        }

        // 获取最后一条消息
        const lastMessage = await dispatch('getLastMessage', conversation)
        console.log('准备保存对话:', conversationId, '最后消息:', lastMessage)

        const response = await updateChatLog({
          id: conversationId,
          conversation: conversation.conversation,
          lastMessage: lastMessage,
          updateTime: new Date().toISOString()
        })

        if (response.code === '200') {
          return { success: true }
        } else {
          console.error('保存对话失败:', response.msg)
          return { success: false, error: response.msg }
        }
      } catch (error) {
        console.error('保存对话出错:', error)
        return { success: false, error: error.message }
      }
    },

    // 获取最后一条消息内容
    async getLastMessage({ dispatch }, conversation) {
      if (!conversation.conversation || conversation.conversation.length === 0) return ''
      const lastMsg = conversation.conversation[conversation.conversation.length - 1]
      return lastMsg.speaker === 'human'
          ? lastMsg.speech
          : await dispatch('getMessageText', lastMsg)
    },

    // 从消息对象中提取文本
    async getMessageText({ state }, msg) {
      if (!msg) return ''

      // 优先从 speeches 数组获取
      if (Array.isArray(msg.speeches) && msg.speeches.length > 0) {
        return typeof msg.speeches[0] === 'string'
            ? msg.speeches[0]
            : JSON.stringify(msg.speeches[0])
      }

      // 然后尝试 speech 字段
      if (msg.speech && typeof msg.speech === 'string') {
        return msg.speech
      }

      // 最后尝试 content 字段
      if (msg.content && typeof msg.content === 'string') {
        return msg.content
      }

      return ''
    }
  },

  // 注册自动保存插件
  plugins: [autoSavePlugin]
})