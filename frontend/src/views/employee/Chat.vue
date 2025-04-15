<template>
  <div class="chat-wrapper h-full">
    <!-- 集成系统头部 -->
    <header class="bg-[#2A5C8A] text-white shadow-md">
      <div class="container mx-auto px-4">
        <div class="flex items-center h-16">
          <button @click="router.back()" class="mr-2 lg:hidden">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
            </svg>
          </button>
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m8 3 4 8 5-5 5 15H2L8 3z"/>
            </svg>
            <span class="ml-2 font-bold text-xl">AI心理咨询</span>
          </div>
        </div>
      </div>
    </header>

    <!-- 主聊天区域 -->
    <main class="flex-1 bg-gray-50 overflow-hidden h-[calc(100vh-64px)]" @click.stop>
      <div class="h-full flex flex-col">
        <!-- 聊天内容区域 -->
        <div ref="chatContainer" class="flex-1 overflow-y-auto p-4 space-y-6">
          <!-- 欢迎消息 -->
          <div class="welcome-message bg-blue-50 border border-blue-100 rounded-lg p-6 mb-6 max-w-3xl mx-auto shadow-sm">
            <div class="flex items-start gap-4">
              <a-avatar :size="48" src="/src/assets/imgs/logo.png" class="flex-shrink-0" />
              <div v-html="configInfo.notice_html_wdms" class="prose"></div>
            </div>
          </div>

          <!-- 聊天记录 -->
          <div v-for="(conv, idx) in getCurrentConversation" 
               :key="idx"
               :class="['message-wrapper', { 'justify-end': conv.speaker === 'human' }]">
            <div :class="['message-bubble', conv.speaker, { 'ml-auto': conv.speaker === 'human' }]">
              <div class="avatar">
                <a-avatar
                  :size="40"
                  :src="conv.speaker === 'ai' ? '/src/assets/imgs/logo.png' : '/src/assets/imgs/human1.png'"
                />
              </div>
              <div class="message-content">
                <div class="message-body">
                  <!-- 添加调试信息 -->
                  <div v-if="conv.speaker === 'ai'" class="ai-message">
                    <!-- 添加原始文本显示，用于调试 -->
                    <div class="debug-text text-xs text-gray-400 mb-1" v-if="false">
                      原始: {{ getMessageText(conv) }}
                    </div>
                    
                    <!-- 正常渲染 -->
                    <div v-html="mdToHtml(getMessageText(conv))" class="prose max-w-none"></div>
                  </div>
                  <div v-else>{{ conv.speech }}</div>
                </div>
                <div class="message-actions">
                  <a-tooltip title="复制" placement="bottom">
                    <CopyOutlined class="action-icon" @click="copyMessage(conv)" />
                  </a-tooltip>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="convLoading" class="message-wrapper">
            <div class="message-bubble ai loading">
              <div class="avatar">
                <a-avatar :size="40" src="/src/assets/imgs/logo.png" />
              </div>
              <div class="message-content">
                <div class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部输入区域 -->
        <div class="bg-white border-t p-4 shadow-inner">
          <!-- 录音预览 -->
          <div v-if="audioUrl" class="audio-preview bg-gray-50 rounded-lg p-3 mb-3 flex items-center">
            <span class="text-sm text-gray-600 mr-3">🎧 录音预览</span>
            <audio :src="audioUrl" controls class="flex-1"></audio>
            <button @click="clearRecording" class="ml-2 text-gray-500 hover:text-gray-700 focus:outline-none">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </button>
          </div>

          <!-- 输入框和发送按钮 -->
          <div class="input-container">
            <textarea
              v-model="chatMsg"
              @keydown="judgeInput"
              @input="adjustTextareaHeight"
              @focus="adjustTextareaHeight"
              ref="inputChat"
              placeholder="向AI心理咨询师倾诉您的想法，Shift+Enter换行，Enter发送"
              class="chat-input w-full resize-none border border-gray-300 rounded-xl py-3 px-4 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
              :maxlength="1000"
              rows="1"
            ></textarea>
            <button 
              class="send-button flex-shrink-0 w-12 h-12 bg-[#4a9be6] hover:bg-[#7c97ea] text-white rounded-xl flex items-center justify-center transition-all duration-200 disabled:bg-gray-300 disabled:cursor-not-allowed transform hover:-translate-y-0.5"
              @click="send"
              :disabled="convLoading || audioUpload"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
              </svg>
            </button>
          </div>

          <!-- 工具按钮 -->
          <div class="tools-container mt-3">
            <div class="tool-buttons flex flex-wrap gap-2">
              <button 
                type="button" 
                @click="toggleRecording"
                :class="[
                  'tool-button flex items-center gap-1.5 px-3 py-2 rounded-lg border transition-all duration-200',
                  isRecording 
                    ? 'bg-red-50 border-red-200 text-red-600' 
                    : 'bg-white border-gray-300 hover:bg-gray-50 hover:-translate-y-0.5'
                ]"
                :disabled="audioUpload"
              >
                <span class="tool-icon">
                  <svg v-if="isRecording" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 10a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1v-4z" />
                  </svg>
                  <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z" />
                  </svg>
                </span>
                <span class="tool-text text-sm">{{ isRecording ? '停止录音' : '开始录音' }}</span>
                <span v-if="isRecording" class="recording-indicator w-2 h-2 bg-red-600 rounded-full animate-pulse"></span>
              </button>
              
              <button 
                type="button"
                @click="uploadAudio"
                :disabled="!audioBlob || audioUpload"
                :class="[
                  'tool-button flex items-center gap-1.5 px-3 py-2 rounded-lg border transition-all',
                  audioUpload 
                    ? 'bg-blue-50 border-blue-200 text-blue-600' 
                    : 'bg-white border-gray-300 hover:bg-gray-50',
                  (!audioBlob || audioUpload) ? 'opacity-60 cursor-not-allowed' : ''
                ]"
              >
                <span class="tool-icon">
                  <svg v-if="audioUpload" class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                  <svg v-else class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                  </svg>
                </span>
                <span class="tool-text text-sm">{{ audioUpload ? '识别中...' : '识别录音' }}</span>
              </button>
              
              <button 
                type="button"
                @click="analyzeEmotion"
                :disabled="convLoading"
                :class="[
                  'tool-button flex items-center gap-1.5 px-3 py-2 rounded-lg border transition-all',
                  'bg-blue-50 border-blue-200 text-blue-500 hover:bg-blue-100',
                  convLoading ? 'opacity-60 cursor-not-allowed' : ''
                ]"
              >
                <span class="tool-icon">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </span>
                <span class="tool-text text-sm">情绪分析</span>
              </button>
            </div>
          </div>

          <!-- 在工具按钮区域下方添加 -->
          <div v-if="lastVoiceEmotion && isDevelopment" class="debug-panel mt-2 p-2 bg-gray-100 rounded-lg text-xs text-gray-600">
            <div class="font-bold">情绪分析调试信息:</div>
            <div>检测到情绪: {{ lastVoiceEmotion.emotion }}</div>
            <div>置信度: {{ lastVoiceEmotion.confidence }}</div>
            <div>音频质量: {{ lastVoiceEmotion.quality }}</div>
            <div>时间戳: {{ new Date(lastVoiceEmotion.timestamp).toLocaleTimeString() }}</div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { marked } from 'marked';
import hljs from "highlight.js";
import 'highlight.js/styles/github.css';
import { mapGetters, mapActions, mapState } from 'vuex'
import { post } from '@/utils/request'
import Recorder from 'recorder-core'
import 'recorder-core/src/engine/mp3'
import 'recorder-core/src/engine/mp3-engine'
import 'recorder-core/src/engine/wav'
import 'recorder-core/src/extensions/waveview'
import { CopyOutlined } from '@ant-design/icons-vue'
import router from '@/router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 懒加载 Recorder 实例，只在需要时创建
let recorder = null;
const initRecorder = () => {
  if (!recorder) {
    recorder = Recorder({
      type: 'wav',
      sampleRate: 16000,  // 确保与Python服务匹配
      bitRate: 16,        // 16位深度
      numChannels: 1,     // 单声道，更适合语音处理
      onProcess: (buffers, powerLevel, bufferDuration, bufferSampleRate) => {
        // 可以在这里处理实时音频数据
        console.log("录音处理中，音量:", powerLevel, "持续时间:", bufferDuration);
      }
    });
  }
  return recorder;
};

// 优化 marked 渲染器，使用缓存提高性能
const rendererCache = new Map();
const renderer = {
  code(code, infostring, escaped) {
    const cacheKey = `${code}-${infostring}`;
    if (rendererCache.has(cacheKey)) {
      return rendererCache.get(cacheKey);
    }
    
    const codeHtml = infostring ? hljs.highlightAuto(code).value : code;
    const result = `<div class="code-block">
      <div class="code-header">
        <span>${infostring || ''}</span>
        <button class="copy-btn" data-code="${encodeURIComponent(code)}">
          <svg stroke="currentColor" fill="none" viewBox="0 0 24 24">
            <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"></path>
            <rect x="8" y="2" width="8" height="4" rx="1" ry="1"></rect>
          </svg>
          <span>复制代码</span>
        </button>
      </div>
      <pre><code class="hljs language-${infostring}">${codeHtml}</code></pre>
    </div>`;
    
    rendererCache.set(cacheKey, result);
    return result;
  },
  paragraph(text) {
    return `<p class="message-paragraph">${text}</p>`;
  }
};

// 限制缓存大小，避免内存泄漏
const MAX_CACHE_SIZE = 100;
const mdCache = new Map();

marked.use({ renderer });

export default {
  components: {
    CopyOutlined
  },
  data() {
    return {
      chatMsg: "",
      convLoading: false,
      configInfo: {
        notice_html_wdms: `
        <h3 class="text-lg font-semibold mb-2">欢迎使用AI心理咨询服务</h3>
        <p class="mb-2">我是您的心理健康助手，可以为您提供:</p>
        <ul class="list-disc pl-5 space-y-1">
          <li>情绪支持和心理疏导</li>
          <li>心理健康知识科普</li>
          <li>简单的认知行为疗法练习</li>
          <li>压力管理建议</li>
        </ul>
        <p class="mt-3 text-sm text-blue-600">请注意：AI不能替代专业心理咨询，如有严重困扰请寻求专业帮助。</p>
        `,
      },
      audioBlob: null,
      audioUrl: null,
      isRecording: false,
      audioUpload: false,
      audioStream: null,
      waveView: null,
      recordingStartTime: null,
      recordingDuration: 0,
      recordingTimer: null,
      retryingMessage: false,
      networkStatus: navigator.onLine,
      lastVoiceEmotion: null,
      lastNetworkCheckFailed: false,
      cacheCleanupInterval: null,
      apiBaseUrl: '',
      isDevelopment: import.meta.env.DEV
    }
  },
  computed: {
    ...mapState(['currentConversationId']),
    ...mapGetters(['getCurrentConversation', 'isLoading'])
  },
  methods: {
    ...mapActions(['addConversationContent', 'loadConversations']),
    
    async init() {
      // 添加代码块复制功能
      this.$nextTick(() => {
        document.addEventListener('click', (e) => {
          if (e.target.closest('.copy-btn')) {
            const code = decodeURIComponent(e.target.closest('.copy-btn').dataset.code);
            navigator.clipboard.writeText(code).then(() => {
              ElMessage.success('代码已复制');
            });
          }
        });
      });
    },
    
    async send() {
      const text = this.chatMsg.trim();
      this.chatMsg = '';

      if (!text || this.convLoading || this.audioUpload) return;

      await this.sendMessageWithRetry(text);
    },
    
    async sendMessage(msg) {
      if (!this.currentConversationId) {
        ElMessage.warning("请先选择或创建一个对话");
        return;
      }
      
      // 检查网络状态
      if (!this.checkNetworkStatus()) return;
      
      this.addConversationContent({
        id: this.currentConversationId,
        content: {
          speaker: "human",
          speech: msg,
        }
      });

      let conv = {
        idx: 0,
        loading: true,
        speaker: "ai",
        suitable: [0],
        speeches: [""],
        speech: ""
      };

      this.handleScrollBottom();
      this.convLoading = true;

      try {
        const currentList = this.getCurrentConversation;
        const payload = {
          messages: currentList.slice(-6).map(item => ({
            "role": { ai: "assistant", human: "user" }[item.speaker],
            "content": item.speaker === "human" ? item.speech : (item.speeches && item.speeches[0] || "")
          }))
        };
        
        // 如果有语音情绪分析结果且不超过5分钟，添加到请求中
        if (this.lastVoiceEmotion && 
            (Date.now() - this.lastVoiceEmotion.timestamp < 5 * 60 * 1000)) {
          // 解析置信度字符串，移除百分号并转换为数值
          let confidence = this.lastVoiceEmotion.confidence;
          if (typeof confidence === 'string' && confidence.endsWith('%')) {
            confidence = parseFloat(confidence.replace('%', '')) / 100;
          }
          
          // 确保格式与后端期望的Map<String, Object>匹配
          payload.voiceEmotion = {
            emotion: this.lastVoiceEmotion.emotion,
            confidence: confidence,
            quality: this.lastVoiceEmotion.quality
          };
        }
        
        // 使用更长的超时时间
        const response = await post("/business/chat/aiChat", payload, { timeout: 60000 });

        console.log("AI响应原始数据:", response);

        // 处理响应
        if (response && response.code === '200') {
          // 简化响应处理逻辑
          let aiResponse = "";
          
          if (typeof response.data === 'string') {
            aiResponse = response.data;
          } 
          else if (response.data && typeof response.data === 'object') {
            // 尝试提取常见字段
            if (response.data.content) {
              aiResponse = response.data.content;
            } 
            else if (response.data.message && typeof response.data.message === 'string') {
              aiResponse = response.data.message;
            }
            else if (response.data.message && response.data.message.content) {
              aiResponse = response.data.message.content;
            }
            else if (response.data.choices && response.data.choices.length > 0) {
              const choice = response.data.choices[0];
              if (typeof choice === 'string') {
                aiResponse = choice;
              } 
              else if (choice.message && typeof choice.message === 'string') {
                aiResponse = choice.message;
              }
              else if (choice.message && choice.message.content) {
                aiResponse = choice.message.content;
              }
              else if (choice.text) {
                aiResponse = choice.text;
              }
            }
            else {
              // 如果无法提取，尝试JSON字符串化
              try {
                aiResponse = JSON.stringify(response.data);
              } catch (e) {
                aiResponse = "无法解析的响应格式";
              }
            }
          }
          
          // 确保aiResponse是字符串
          if (typeof aiResponse !== 'string') {
            aiResponse = String(aiResponse);
          }
          
          // 更新对话内容
          conv.speeches[0] = aiResponse;
          conv.speech = aiResponse;
          
          this.addConversationContent({
            id: this.currentConversationId,
            content: {
              ...conv,
              loading: false
            }
          });
          
          this.handleScrollBottom();
        } else {
          // 处理错误响应
          const errorMsg = response?.msg || response?.data?.message || "AI回复失败";
          ElMessage.error(errorMsg);
          
          // 在聊天中显示错误消息
          conv.speeches[0] = `抱歉，遇到问题: ${errorMsg}`;
          conv.speech = `抱歉，遇到问题: ${errorMsg}`;
          this.addConversationContent({
            id: this.currentConversationId,
            content: conv
          });
        }
      } catch (error) {
        console.error("Error fetching chat response:", error);
        
        let errorMessage = "请求失败";
        if (error.message) errorMessage += `: ${error.message}`;
        if (error.response) errorMessage += ` (状态码: ${error.response.status})`;
        
        ElMessage.error(errorMessage);
        
        // 在聊天中显示错误消息
        conv.speeches[0] = "抱歉，我遇到了技术问题，无法回应您的消息。请稍后再试。";
        conv.speech = "抱歉，我遇到了技术问题，无法回应您的消息。请稍后再试。";
        this.addConversationContent({
          id: this.currentConversationId,
          content: conv
        });
      } finally {
        this.convLoading = false;
        conv.loading = false;
      }
    },
    
    stopChat() {
      this.convLoading = false;
    },
    
    mdToHtml(md) {
      console.log("准备渲染Markdown:", md); // 添加日志
      
      if (!md) {
        console.warn("收到空的Markdown内容");
        return "<p>无内容</p>";
      }
      
      // 添加安全过滤
      md = this.sanitizeMarkdown(md);
      
      // 检查是否包含Markdown语法
      const hasMarkdown = /(\*\*|__|\*|_|~~|`|```|#|>|\[.*]\(.*\)|^\d+\.|^-|\+)/.test(md);
      
      // 如果不包含Markdown语法，直接返回纯文本，只处理换行
      if (!hasMarkdown) {
        console.log("内容不包含Markdown，直接返回纯文本");
        // 处理换行和空格
        const htmlText = md
          .replace(/\n/g, '<br>')
          .replace(/\s\s/g, '&nbsp;&nbsp;');
        return `<p class="message-paragraph">${htmlText}</p>`;
      }
      
      // 使用缓存提高性能
      if (mdCache.has(md)) {
        return mdCache.get(md);
      }
      
      try {
        // 确保代码块闭合
        const codeBlockCount = (md.match(/```/g) || []).length;
        if (codeBlockCount % 2 !== 0) {
          md += "\n```";
        }
        
        // 尝试直接使用marked解析
        const result = marked.parse(md.trim());
        
        // 检查结果是否包含[object Object]
        if (result.includes('[object Object]')) {
          console.warn("渲染结果包含[object Object]，使用备用渲染方法");
          // 备用渲染方法：简单地处理常见的Markdown语法
          return this.simpleMarkdownRender(md);
        }
        
        // 限制缓存大小
        if (mdCache.size >= MAX_CACHE_SIZE) {
          const firstKey = mdCache.keys().next().value;
          mdCache.delete(firstKey);
        }
        
        mdCache.set(md, result);
        console.log("Markdown渲染结果:", result.substring(0, 100) + "..."); // 添加日志
        return result;
      } catch (error) {
        console.error("Markdown渲染错误:", error);
        // 如果渲染失败，使用备用渲染方法
        return this.simpleMarkdownRender(md);
      }
    },
    
    // 添加一个简单的Markdown渲染函数作为备用
    simpleMarkdownRender(text) {
      if (!text) return "<p></p>";
      
      // 转义HTML特殊字符
      let html = text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
      
      // 处理代码块
      html = html.replace(/```([\s\S]*?)```/g, function(match, code) {
        return `<pre><code>${code.trim()}</code></pre>`;
      });
      
      // 处理行内代码
      html = html.replace(/`([^`]+)`/g, '<code>$1</code>');
      
      // 处理粗体
      html = html.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
      html = html.replace(/__([^_]+)__/g, '<strong>$1</strong>');
      
      // 处理斜体
      html = html.replace(/\*([^*]+)\*/g, '<em>$1</em>');
      html = html.replace(/_([^_]+)_/g, '<em>$1</em>');
      
      // 处理链接
      html = html.replace(/\[([^\]]+)]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>');
      
      // 处理换行
      html = html.replace(/\n/g, '<br>');
      
      return `<div class="simple-markdown">${html}</div>`;
    },
    
    judgeInput(e) {
      if (!e.shiftKey && e.keyCode === 13) {
        e.preventDefault();
        this.send();
      }
    },
    
    handleScrollBottom() {
      this.$nextTick(() => {
        const container = this.$refs.chatContainer;
        if (container) {
          container.scrollTo({
            top: container.scrollHeight,
            behavior: 'smooth'
          });
        }
      });
    },
    
    copyMessage(conv) {
      const text = this.getMessageText(conv);
      navigator.clipboard.writeText(text).then(() => {
        ElMessage.success('复制成功');
      });
    },
    
    async toggleRecording() {
      if (this.isRecording) {
        await this.stopRecording();
      } else {
        await this.startRecording();
      }
    },
    
    async startRecording() {
      try {
        // 检查浏览器支持
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
          ElMessage.error('您的浏览器不支持录音功能');
          return;
        }

        // 如果已经在录音，先停止
        if (this.isRecording) {
          await this.stopRecording();
        }

        // 请求麦克风权限
        const stream = await navigator.mediaDevices.getUserMedia({ 
          audio: {
            sampleRate: 16000,  // 尝试请求16kHz采样率
            channelCount: 1,    // 单声道
            echoCancellation: true,
            noiseSuppression: true
          } 
        });
        this.audioStream = stream;
        
        // 配置录音 - 懒加载初始化
        const recorder = initRecorder();
        
        // 添加错误处理和超时
        const openPromise = new Promise((resolve, reject) => {
          const timeout = setTimeout(() => {
            reject(new Error('录音设备初始化超时'));
          }, 5000);
          
          recorder.open(() => {
            clearTimeout(timeout);
            console.log('录音设备已就绪');
            resolve();
          }, (err) => {
            clearTimeout(timeout);
            reject(err);
          }, stream); // 传入已获取的流
        });
        
        await openPromise;
        
        // 开始录音
        recorder.start();
        this.isRecording = true;
        ElMessage.info('录音已开始');
        
        // 记录开始时间并启动计时器
        this.recordingStartTime = Date.now();
        this.recordingTimer = setInterval(() => {
          this.recordingDuration = Math.floor((Date.now() - this.recordingStartTime) / 1000);
          // 如果录音超过60秒，自动停止
          if (this.recordingDuration >= 60) {
            this.stopRecording();
            ElMessage.warning('录音已达到最大时长(60秒)');
          }
        }, 1000);
        
      } catch (error) {
        console.error("录音失败:", error);
        let errorMsg = "麦克风访问被拒绝";
        if (error.name === 'NotAllowedError') {
          errorMsg = "麦克风访问权限被拒绝，请允许浏览器使用麦克风";
        } else if (error.name === 'NotFoundError') {
          errorMsg = "未检测到麦克风设备";
        } else if (error.message) {
          errorMsg = error.message;
        }
        ElMessage.error(errorMsg);
        this.closeAudioStream();
      }
    },
    
    async stopRecording() {
      return new Promise((resolve, reject) => {
        // 清除计时器
        if (this.recordingTimer) {
          clearInterval(this.recordingTimer);
          this.recordingTimer = null;
        }
        
        // 如果没有录音实例或未在录音，直接返回
        if (!recorder || !this.isRecording) {
          this.isRecording = false;
          this.closeAudioStream();
          resolve();
          return;
        }
        
        // 添加超时处理
        const timeout = setTimeout(() => {
          this.isRecording = false;
          this.closeAudioStream();
          ElMessage.error('录音停止超时，请重试');
          reject(new Error('录音停止超时'));
        }, 3000);
        
        recorder.stop((blob, duration) => {
          clearTimeout(timeout);
          console.log(`录音结束，时长: ${duration}ms`);
          
          // 验证blob是否有效
          if (!blob || blob.size === 0) {
            ElMessage.error('录音失败，未捕获到音频');
            this.isRecording = false;
            this.closeAudioStream();
            reject(new Error('录音失败，未捕获到音频'));
            return;
          }
          
          this.audioBlob = blob;
          this.audioUrl = URL.createObjectURL(blob);
          this.isRecording = false;
          this.recordingDuration = 0;
          ElMessage.success('录音完成');
          this.closeAudioStream();
          resolve();
        }, (error) => {
          clearTimeout(timeout);
          console.error('录音停止失败:', error);
          ElMessage.error('录音停止失败: ' + (error.message || '未知错误'));
          this.isRecording = false;
          this.closeAudioStream();
          reject(error);
        });
      });
    },
    
    closeAudioStream() {
      if (this.audioStream) {
        this.audioStream.getTracks().forEach(track => track.stop());
        this.audioStream = null;
      }
    },
    
    clearRecording() {
      this.audioBlob = null;
      this.audioUrl = null;
      if (this.audioStream) {
        this.closeAudioStream();
      }
    },
    
    // 修改Token获取方式
    getToken() {
      // 从 xm-user 中获取 token，与 request.js 保持一致
      const user = JSON.parse(localStorage.getItem("xm-user") || '{}');
      const token = user.token || '';
      
      // 检查token是否存在
      if (!token) {
        console.error('未找到有效的token，可能需要重新登录');
        ElMessage.error('登录状态已失效，请重新登录');
        return null;
      }
      
      return token;
    },
    
    async uploadAudio() {
      if (!this.audioBlob) {
        ElMessage.warning("请先录制音频");
        return;
      }

      this.audioUpload = true;

      try {
        // 检查登录状态 - 使用统一的 getToken 方法
        const token = this.getToken();
        if (!token) {
          this.audioUpload = false;
          return;
        }

        // 确保音频格式正确
        let processedBlob = this.audioBlob;
        
        // 检查音频格式，确保是WAV格式
        if (this.audioBlob.type !== 'audio/wav') {
          ElMessage.info("正在处理音频格式...");
          try {
            processedBlob = await this.ensureCorrectAudioFormat(this.audioBlob);
          } catch (error) {
            console.error("音频格式转换失败:", error);
            ElMessage.warning("音频格式转换失败，将使用原始格式");
          }
        }
        
        // 创建FormData对象
        const formData = new FormData();
        formData.append('file', processedBlob, 'recording.wav');

        // 使用统一的请求方式
        try {
          // 步骤1: 先调用音频转写API
          ElMessage.info("正在转写音频...");
          
          const transcriptionRes = await post("/files/transcription", formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
          });
          
          if (transcriptionRes.code === '200' && transcriptionRes.data) {
            // 转写成功，将结果放入输入框
            this.chatMsg = transcriptionRes.data;
            ElMessage.success("语音转写成功");
            
            // 步骤2: 再调用情绪分析API
            ElMessage.info("正在分析情绪...");
            
            const emotionFormData = new FormData();
            emotionFormData.append('file', processedBlob, 'recording.wav');
            
            const emotionRes = await post("/business/emotion/analyze", emotionFormData, {
              headers: { 'Content-Type': 'multipart/form-data' }
            });
            
            if (emotionRes.code === '200' && emotionRes.data) {
              // 保存情绪分析结果
              this.lastVoiceEmotion = {
                emotion: this.translateEmotion(emotionRes.data.emotion || 'neutral'),
                confidence: emotionRes.data.confidence || '70%',
                quality: this.evaluateAudioQuality(emotionRes.data),
                timestamp: Date.now()
              };
              
              console.log("情绪分析结果:", this.lastVoiceEmotion);
            }
          } else {
            ElMessage.warning("语音转写失败: " + (transcriptionRes.msg || "未知错误"));
          }
        } catch (error) {
          console.error("使用统一请求工具失败:", error);
          ElMessage.error("音频处理失败: " + (error.message || "未知错误"));
        }
      } catch (error) {
        console.error("音频上传或处理出错:", error);
        let errorMessage = "音频处理失败";
        if (error.response) {
          errorMessage += `: ${error.response.status} - ${error.response.data?.msg || error.message}`;
        } else if (error.message) {
          errorMessage += ": " + error.message;
        }
        ElMessage.error(errorMessage);
      } finally {
        this.audioUpload = false;
      }
    },
    
    // 添加音频质量评估方法
    evaluateAudioQuality(emotionData) {
      // 检查是否有信噪比或其他音频质量指标
      if (emotionData.snr !== undefined) {
        // 信噪比低于10dB视为低质量
        return emotionData.snr < 10 ? "low" : "good";
      }
      
      // 如果没有明确的质量指标，检查情绪概率分布
      if (emotionData.probabilities) {
        const probs = Object.values(emotionData.probabilities);
        
        // 计算概率分布的标准差
        const avg = probs.reduce((sum, val) => sum + val, 0) / probs.length;
        const variance = probs.reduce((sum, val) => sum + Math.pow(val - avg, 2), 0) / probs.length;
        const stdDev = Math.sqrt(variance);
        
        // 如果标准差很小，说明模型对各情绪的预测接近，可能是低质量音频
        if (stdDev < 0.1) {
          return "low";
        }
      }
      
      // 检查音频时长，过短的音频可能质量不佳
      if (emotionData.duration !== undefined && emotionData.duration < 1.5) {
        return "low";
      }
      
      // 检查音频音量
      if (emotionData.volume !== undefined && emotionData.volume < 0.3) {
        return "low";
      }
      
      // 默认为良好质量
      return "good";
    },
    
    // 翻译情绪标签
    translateEmotion(emotion) {
      const emotionMap = {
        'happy': '开心',
        'sad': '悲伤',
        'angry': '愤怒',
        'fear': '恐惧',
        'disgust': '厌恶',
        'neutral': '平静',
        'surprise': '惊讶',
        'anxiety': '焦虑',
        'depression': '抑郁',
        'confusion': '困惑',
        'stress': '压力'
      };
      
      return emotionMap[emotion] || emotion;
    },
    
    async analyzeEmotion() {
      if (this.convLoading) return;
      
      // 获取最近3条用户消息
      const lastHumanMessages = this.getCurrentConversation
        .filter(msg => msg.speaker === 'human')
        .slice(-3)
        .map(msg => msg.speech)
        .join("\n");
      
      if (!lastHumanMessages) {
        ElMessage.warning("请先与AI对话后再分析情绪");
        return;
      }
      
      // 检查是否有最近的语音情绪分析结果
      let emotionContext = "";
      if (this.lastVoiceEmotion && 
          (Date.now() - this.lastVoiceEmotion.timestamp < 5 * 60 * 1000)) {
        emotionContext = `
        注意：用户最近的语音情绪分析结果为"${this.lastVoiceEmotion.emotion}"，
        置信度为${this.lastVoiceEmotion.confidence}。请将此信息作为重要参考。`;
      }
      
      const prompt = `请根据用户最近的对话内容分析其情绪状态。对话内容如下：
      ${lastHumanMessages}${emotionContext}
      
      请用以下格式回复：
      【情绪分析结果】
      您的当前情绪主要是：{情绪类型}
      情绪强度：{低/中/高}
      【详细分析】
      {简要分析用户情绪状态的原因和表现}
      【建议】
      {根据情绪状态给出1-2条简单的自我调节建议}
      情绪类型请从以下选项中选择：开心、平静、焦虑、愤怒、悲伤、困惑、压力、其他`;
      
      try {
        ElMessage.info("正在分析情绪...");
        await this.sendMessageWithRetry(prompt);
      } catch (error) {
        console.error("情绪分析失败:", error);
        ElMessage.error("情绪分析请求失败，请稍后再试");
      }
    },
    
    adjustTextareaHeight() {
      const textarea = this.$refs.inputChat;
      if (textarea) {
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
      }
    },
    
    focusInput() {
      this.$nextTick(() => {
        const textarea = this.$refs.inputChat;
        if (textarea) {
          textarea.focus();
        }
      });
    },
    
    // 增强网络状态检查方法
    checkNetworkStatus() {
      if (!navigator.onLine) {
        ElMessage.error("网络连接已断开，请检查您的网络设置");
        return false;
      }
      
      // 添加额外的网络检测逻辑
      if (this.lastNetworkCheckFailed) {
        // 如果上次网络检查失败，进行更严格的检查
        return this.performDeepNetworkCheck();
      }
      
      return true;
    },
    
    // 添加深度网络检查方法
    async performDeepNetworkCheck() {
      try {
        // 使用已存在的后端接口进行检查
        const response = await fetch(`${import.meta.env.VITE_APP_BASE_API}/health`, { 
          method: 'HEAD',
          cache: 'no-store',
          timeout: 2000
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        this.lastNetworkCheckFailed = false;
        return true;
      } catch (error) {
        console.error('深度网络检查失败:', error);
        this.lastNetworkCheckFailed = true;
        ElMessage.error('网络连接不稳定，请检查您的网络设置');
        return false;
      }
    },
    
    // 添加重试功能
    async sendMessageWithRetry(msg, retryCount = 0) {
      const maxRetries = 2; // 最大重试次数
      
      try {
        await this.sendMessage(msg);
      } catch (error) {
        console.error(`发送消息失败 (尝试 ${retryCount + 1}/${maxRetries + 1})`, error);
        
        if (retryCount < maxRetries && navigator.onLine) {
          // 如果网络在线且未超过最大重试次数，则重试
          ElMessage.info(`正在重试 (${retryCount + 1}/${maxRetries})...`);
          await new Promise(resolve => setTimeout(resolve, 1000)); // 等待1秒后重试
          await this.sendMessageWithRetry(msg, retryCount + 1);
        } else {
          // 超过重试次数或网络离线，显示最终错误
          ElMessage.error('发送消息失败，请检查网络连接后重试');
        }
      }
    },
    
    extractTextFromResponse(data) {
      // 处理null/undefined
      if (data == null) return "服务器返回了空响应";
      
      // 处理字符串
      if (typeof data === 'string') {
        // 检查是否是JSON字符串
        if (data.trim().startsWith('{') || data.trim().startsWith('[')) {
          try {
            const parsed = JSON.parse(data);
            return this.extractTextFromResponse(parsed);
          } catch {
            return data; // 返回原始字符串
          }
        }
        return data;
      }
      
      // 处理数组
      if (Array.isArray(data)) {
        // 尝试提取第一个元素的content/message/text字段
        if (data.length > 0) {
          const firstItem = data[0];
          if (typeof firstItem === 'object' && firstItem !== null) {
            if (firstItem.content) return firstItem.content;
            if (firstItem.message?.content) return firstItem.message.content;
            if (firstItem.message) return typeof firstItem.message === 'string' ? firstItem.message : JSON.stringify(firstItem.message);
            if (firstItem.text) return firstItem.text;
          } else if (typeof firstItem === 'string') {
            return firstItem;
          }
        }
        return data.map(item => this.extractTextFromResponse(item)).join('\n');
      }
      
      // 处理对象
      if (typeof data === 'object') {
        // DeepSeek标准字段
        if (data.content) return data.content;
        if (data.message?.content) return data.message.content;
        if (data.message && typeof data.message === 'string') return data.message;
        if (data.text) return data.text;
        
        // OpenAI兼容格式
        if (data.choices && Array.isArray(data.choices) && data.choices.length > 0) {
          const choice = data.choices[0];
          if (choice.message?.content) return choice.message.content;
          if (choice.text) return choice.text;
          if (choice.message) return typeof choice.message === 'string' ? choice.message : JSON.stringify(choice.message);
          if (choice.delta?.content) return choice.delta.content;
          return this.extractTextFromResponse(choice);
        }
        
        // 尝试提取第一个属性值
        const keys = Object.keys(data);
        if (keys.length > 0) {
          const firstValue = data[keys[0]];
          if (firstValue !== undefined) {
            return this.extractTextFromResponse(firstValue);
          }
        }
        
        // 最后尝试JSON格式化
        try {
          return "```json\n" + JSON.stringify(data, null, 2) + "\n```";
        } catch {
          return "无法解析的响应数据";
        }
      }
      
      // 其他类型转换为字符串
      return String(data);
    },
    
    handleNetworkChange() {
      this.networkStatus = navigator.onLine;
      if (navigator.onLine) {
        ElMessage.success('网络已连接');
      } else {
        ElMessage.error('网络已断开');
      }
    },
    
    // 在methods中添加调试函数
    debugConversation() {
      //console.log("当前对话数据结构:", JSON.stringify(this.getCurrentConversation, null, 2));
      
      // 检查最后一条消息
      if (this.getCurrentConversation && this.getCurrentConversation.length > 0) {
        const lastMsg = this.getCurrentConversation[this.getCurrentConversation.length - 1];
        //console.log("最后一条消息:", lastMsg);
        //console.log("speeches类型:", lastMsg.speeches ? typeof lastMsg.speeches : "undefined");
        if (lastMsg.speeches) {
          //console.log("speeches[0]类型:", typeof lastMsg.speeches[0]);
          //console.log("speeches[0]值:", lastMsg.speeches[0]);
        }
      }
    },
    
    getMessageText(conv) {
      if (!conv) {
        console.warn("getMessageText: 收到空的对话对象");
        return "";
      }
      
      console.log("getMessageText处理对象:", conv);
      
      // 对于AI消息，尝试多种可能的数据结构
      if (conv.speaker === 'ai') {
        // 首先检查speeches数组
        if (Array.isArray(conv.speeches) && conv.speeches.length > 0) {
         // console.log("使用speeches[0]:", conv.speeches[0]);
          if (typeof conv.speeches[0] === 'string') {
            return conv.speeches[0];
          } else if (conv.speeches[0] && typeof conv.speeches[0] === 'object') {
            const jsonStr = JSON.stringify(conv.speeches[0]);
           // console.log("speeches[0]是对象，转换为JSON:", jsonStr);
            return jsonStr;
          }
        }
        
        // 然后检查speech字段
        if (conv.speech && typeof conv.speech === 'string') {
          //console.log("使用speech字段:", conv.speech);
          return conv.speech;
        }
        
        // 最后尝试其他可能的字段
        if (conv.content && typeof conv.content === 'string') {
          //console.log("使用content字段:", conv.content);
          return conv.content;
        }
        
        console.warn("无法找到有效的消息内容");
        return "无法显示消息内容";
      }
      
      // 对于用户消息，直接返回speech
      return conv.speech || "";
    },
    
    async ensureCorrectAudioFormat(blob) {
      // 如果已经是正确格式，直接返回
      if (blob.type === 'audio/wav') {
        return blob;
      }
      
      // 否则尝试转换
      return new Promise((resolve, reject) => {
        try {
          const fileReader = new FileReader();
          fileReader.onload = (event) => {
            const arrayBuffer = event.target.result;
            
            // 使用AudioContext进行格式转换
            const audioContext = new (window.AudioContext || window.webkitAudioContext)({
              sampleRate: 16000 // 强制使用16kHz采样率
            });
            
            audioContext.decodeAudioData(arrayBuffer, (buffer) => {
              // 创建离线上下文进行重采样
              const offlineContext = new OfflineAudioContext(
                1, // 单声道
                buffer.duration * 16000, // 新的采样率下的样本数
                16000 // 目标采样率
              );
              
              const source = offlineContext.createBufferSource();
              source.buffer = buffer;
              source.connect(offlineContext.destination);
              source.start(0);
              
              offlineContext.startRendering().then((renderedBuffer) => {
                // 将AudioBuffer转换为WAV格式
                const wavBlob = this.bufferToWav(renderedBuffer);
                resolve(new Blob([wavBlob], { type: 'audio/wav' }));
              }).catch((err) => {
                console.error('音频渲染失败:', err);
                reject(err);
              });
            }, (err) => {
              console.error('音频解码失败:', err);
              reject(err);
            });
          };
          
          fileReader.onerror = function(error) {
            reject(error);
          };
          
          fileReader.readAsArrayBuffer(blob);
        } catch (error) {
          console.error('音频格式转换失败:', error);
          reject(error);
        }
      });
    },
    
    // 辅助函数：将AudioBuffer转换为WAV格式
    bufferToWav(buffer) {
      const numOfChan = buffer.numberOfChannels;
      const length = buffer.length * numOfChan * 2;
      const result = new ArrayBuffer(44 + length);
      const view = new DataView(result);
      
      // RIFF标识
      this.writeString(view, 0, 'RIFF');
      // RIFF块长度
      view.setUint32(4, 36 + length, true);
      // WAVE标识
      this.writeString(view, 8, 'WAVE');
      // fmt标识
      this.writeString(view, 12, 'fmt ');
      // fmt块长度
      view.setUint32(16, 16, true);
      // 采样格式（PCM）
      view.setUint16(20, 1, true);
      // 声道数
      view.setUint16(22, numOfChan, true);
      // 采样率
      view.setUint32(24, buffer.sampleRate, true);
      // 每秒字节数
      view.setUint32(28, buffer.sampleRate * 2 * numOfChan, true);
      // 每样本字节数
      view.setUint16(32, numOfChan * 2, true);
      // 位深度
      view.setUint16(34, 16, true);
      // data标识
      this.writeString(view, 36, 'data');
      // data块长度
      view.setUint32(40, length, true);
      
      // 写入PCM数据
      const channelData = [];
      let offset = 44;
      for (let i = 0; i < buffer.numberOfChannels; i++) {
        channelData.push(buffer.getChannelData(i));
      }
      
      // 使用更安全的索引计算方式
      const dataLength = buffer.length;
      for (let i = 0; i < dataLength; i++) {
        for (let channel = 0; channel < numOfChan; channel++) {
          if (offset < result.byteLength) {
            const sample = Math.max(-1, Math.min(1, channelData[channel][i]));
            view.setInt16(offset, sample < 0 ? sample * 0x8000 : sample * 0x7FFF, true);
            offset += 2;
          }
        }
      }
      
      return result;
    },
    
    // 辅助函数：写入字符串
    writeString(view, offset, string) {
      for (let i = 0; i < string.length; i++) {
        view.setUint8(offset + i, string.charCodeAt(i));
      }
    },
    
    // 添加 Markdown 安全过滤方法
    sanitizeMarkdown(md) {
      // 移除潜在的危险HTML标签
      md = md.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
      md = md.replace(/<iframe\b[^<]*(?:(?!<\/iframe>)<[^<]*)*<\/iframe>/gi, '');
      md = md.replace(/<embed\b[^<]*(?:(?!<\/embed>)<[^<]*)*<\/embed>/gi, '');
      md = md.replace(/<object\b[^<]*(?:(?!<\/object>)<[^<]*)*<\/object>/gi, '');
      
      // 过滤可能的XSS攻击向量
      md = md.replace(/javascript:/gi, 'javascript:void(0);');
      md = md.replace(/data:/gi, 'data:void(0);');
      
      return md;
    },
  },
  async mounted() {
    await this.init();
    // 确保代码高亮 - 延迟加载
    setTimeout(() => {
      if (typeof hljs !== 'undefined') {
        hljs.highlightAll();
      }
    }, 100);
    
    // 初始滚动到底部
    this.handleScrollBottom();
    
    // 使用防抖优化窗口大小变化事件
    let resizeTimeout;
    const handleResizeWithDebounce = () => {
      clearTimeout(resizeTimeout);
      resizeTimeout = setTimeout(this.handleScrollBottom, 100);
    };
    
    window.addEventListener('resize', handleResizeWithDebounce);
    
    // 初始化输入框高度
    this.$nextTick(() => {
      this.adjustTextareaHeight();
      
      // 添加点击事件监听器
      document.addEventListener('click', (e) => {
        // 如果点击的是输入框区域，确保输入框获得焦点
        if (e.target.closest('.input-container')) {
          this.focusInput();
        }
      });
    });
    
    // 监听网络状态变化
    window.addEventListener('online', this.handleNetworkChange);
    window.addEventListener('offline', this.handleNetworkChange);
    
    // 添加store监听
    this.$store.subscribe((mutation, state) => {
      if (mutation.type === 'ADD_CONVERSATION_CONTENT') {
        console.log("Store更新:", mutation.payload);
        // 强制重新渲染
        this.$forceUpdate();
      }
    });
    
    // 添加定期清理缓存的逻辑
    this.cacheCleanupInterval = setInterval(() => {
      // 如果缓存过大，清理一半
      if (mdCache.size > MAX_CACHE_SIZE / 2) {
        const keysToDelete = Array.from(mdCache.keys()).slice(0, mdCache.size / 2);
        keysToDelete.forEach(key => mdCache.delete(key));
        console.log(`已清理 ${keysToDelete.length} 个Markdown缓存项`);
      }
      
      if (rendererCache.size > MAX_CACHE_SIZE / 2) {
        const keysToDelete = Array.from(rendererCache.keys()).slice(0, rendererCache.size / 2);
        keysToDelete.forEach(key => rendererCache.delete(key));
        console.log(`已清理 ${keysToDelete.length} 个渲染器缓存项`);
      }
    }, 60000); // 每分钟检查一次
    
    // 初始化 API 基础 URL - 使用环境变量
    this.apiBaseUrl = import.meta.env.VITE_APP_BASE_API || '';
    
    // 如果是生产环境，可以使用空字符串（相对路径）
    if (import.meta.env.PROD) {
      this.apiBaseUrl = ''; // 生产环境使用相对路径
    }
    
    console.log('使用 API 基础 URL:', this.apiBaseUrl);
  },
  beforeUnmount() {
    if (this.isRecording) {
      this.stopRecording();
    }
    this.closeAudioStream();
    
    // 移除事件监听
    window.removeEventListener('resize', this.handleScrollBottom);
    
    // 清理定时器
    if (this.cacheCleanupInterval) {
      clearInterval(this.cacheCleanupInterval);
    }
    
    if (this.recordingTimer) {
      clearInterval(this.recordingTimer);
    }
    
    // 清理缓存
    rendererCache.clear();
    mdCache.clear();
    
    // 移除网络状态监听
    window.removeEventListener('online', this.handleNetworkChange);
    window.removeEventListener('offline', this.handleNetworkChange);
  },
  watch: {
    chatMsg() {
      this.adjustTextareaHeight();
    }
  }
}
</script>

<style scoped lang="scss">
.chat-wrapper {
  display: flex;
  flex-direction: column;
  height: 100% !important;
  width: 100% !important;
  background-color: #f9fafb;
  position: relative;
  
  header {
    flex-shrink: 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
  }
  
  main {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    width: 100%;
    position: relative;
  }
}

.message-wrapper {
  display: flex;
  width: 100%;
  margin-bottom: 16px;
}

.message-bubble {
  display: flex;
  gap: 12px;
  max-width: 85%;
  
  &.ai {
    .message-content {
      background-color: #f0f7ff;
      border: 1px solid #d1e3f8;
      border-radius: 0 16px 16px 16px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
      color: #1e3a5f;
    }
    
    .avatar {
      order: 1;
    }
    
    .message-content {
      order: 2;
    }
  }
  
  &.human {
    margin-left: auto;
    
    .message-content {
      background-color: #edf7ed;
      color: #1e3a1e;
      border: 1px solid #c6e6c6;
      border-radius: 16px 0 16px 16px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    }
    
    .avatar {
      order: 2;
    }
    
    .message-content {
      order: 1;
    }
    
    .message-actions {
      justify-content: flex-end;
    }
  }
  
  .avatar {
    flex-shrink: 0;
    display: flex;
    align-items: flex-start;
    padding-top: 4px;
  }
  
  .message-content {
    flex: 1;
    min-width: 0;
    padding: 12px 16px;
    position: relative;
    transition: all 0.2s ease;
    
    &:hover {
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      
      .message-actions {
        opacity: 1;
      }
    }
  }
  
  .message-body {
    word-break: break-word;
    line-height: 1.6;
    
    pre, code {
      white-space: pre-wrap;
      word-break: break-all;
    }
    
    a {
      word-break: break-all;
    }
  }
  
  .message-actions {
    position: absolute;
    top: 8px;
    right: 8px;
    display: flex;
    gap: 4px;
    opacity: 0;
    transition: opacity 0.2s;
    
    .action-icon {
      color: #6b7280;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 4px;
      padding: 2px;
      cursor: pointer;
      
      &:hover {
        color: #1890ff;
        background: rgba(255, 255, 255, 0.95);
      }
    }
  }
  
  &.loading {
    .message-content {
      background: none;
      border: none;
      box-shadow: none;
    }
  }
}

.input-container {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  position: relative;
  z-index: 10;
  width: 100%;
}

.typing-indicator {
  display: flex;
  gap: 6px;
  padding: 12px 16px;
  background-color: #f0f7ff;
  border-radius: 12px;
  
  span {
    width: 8px;
    height: 8px;
    background-color: #4b83c5;
    border-radius: 50%;
    display: inline-block;
    animation: typing 1s infinite ease-in-out;
    
    &:nth-child(2) {
      animation-delay: 0.2s;
    }
    
    &:nth-child(3) {
      animation-delay: 0.4s;
    }
  }
}

@keyframes typing {
  0%, 100% {
    transform: translateY(0);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-4px);
    opacity: 1;
  }
}

.welcome-message {
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-1px);
  }
}

.audio-preview {
  background-color: #f0f7ff;
  border: 1px solid #d1e3f8;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #e0f2fe;
  }
  
  audio {
    border-radius: 4px;
  }
}

.chat-input {
  min-height: 48px;
  max-height: 120px;
  line-height: 1.5;
  transition: all 0.2s;
  
  &:focus {
    outline: none;
    box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
  }
}

.send-button {
  &:disabled {
    opacity: 0.7;
  }
  
  &:not(:disabled):hover {
    transform: scale(1.05);
  }
}

.tool-button {
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  &:not(:disabled):hover {
    transform: translateY(-1px);
  }
}

.recording-indicator {
  margin-left: 4px;
}

.code-block {
  max-height: 300px;
  overflow-y: auto;
}

/* 全局按钮样式 */
:global(.btn) {
  @apply rounded-md transition-all duration-200 flex items-center justify-center;
  
  &:not(:disabled):hover {
    @apply transform -translate-y-0.5 shadow-md;
  }
  
  &:disabled {
    @apply opacity-70 cursor-not-allowed;
  }
}

:global(.btn-primary) {
  @apply bg-[#4a9be6] hover:bg-[#7c97ea] text-white;
}

:global(.btn-secondary) {
  @apply bg-gray-100 hover:bg-gray-200 text-gray-700;
}

:global(.btn-danger) {
  @apply bg-red-500 hover:bg-red-600 text-white;
}

:global(.btn-success) {
  @apply bg-green-600 hover:bg-green-700 text-white;
}

:global(.btn-icon) {
  @apply p-2 rounded-full transition-all duration-200;
  
  &:hover {
    @apply transform -translate-y-0.5;
  }
}

:global(.btn-icon-danger) {
  @apply text-red-500 hover:bg-red-50;
}

:global(.btn-icon-primary) {
  @apply text-[#2A5C8A] hover:bg-blue-50;
}
</style>