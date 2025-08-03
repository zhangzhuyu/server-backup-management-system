<template>
  <div class="ai-assistant-view">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span
            ><el-icon><ChatDotRound /></el-icon> AI Backup Assistant</span
          >
        </div>
      </template>

      <div class="chat-history" ref="chatHistoryRef">
        <div
          v-for="(message, index) in messages"
          :key="index"
          :class="['message-row', message.sender]"
        >
          <div :class="['message-bubble', message.sender]">
            <span v-if="message.sender === 'ai' && message.isLoading"
              ><el-icon class="is-loading"><Loading /></el-icon> Thinking...</span
            >
            <span v-else>{{ message.text }}</span>
          </div>
        </div>
      </div>

      <div class="chat-input-area">
        <el-input
          v-model="userInput"
          placeholder="Ask a question about backups..."
          @keyup.enter="sendMessage"
          :disabled="isLoading"
          clearable
        >
          <template #append>
            <el-button
              type="primary"
              @click="sendMessage"
              :disabled="isLoading || !userInput.trim()"
            >
              Send <el-icon><Promotion /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElCard, ElInput, ElButton, ElIcon, ElMessage } from 'element-plus'
import { ChatDotRound, Promotion, Loading } from '@element-plus/icons-vue'

// --- 1. 本地知识库 (预设的问题和答案) ---
// 你可以在这里添加或修改更多的问题和答案
const knowledgeBase = {
  'What is incremental backup?':
    'Incremental Backup is an efficient data backup strategy. It only backs up files that have changed or been newly added since the last backup (whether a full backup or an incremental backup).' +
    'Its main advantages are fast backup speed and small storage space occupation. However, the drawback is that when restoring data, you need to first restore the most recent full backup and then apply all subsequent incremental backups in sequence. The process is relatively complex.',

  // 键不包含问号，使其更具通用性
  'Why is backing up files important?':
    'Backup files are of vital importance. They are like your "insurance policy" in the digital world. The main reasons are as follows:\n' +
    '1. ** Data Recovery ** : In the event of hardware failure, software error, virus attack or accidental deletion by humans, backup is the only way to recover lost data.\n' +
    '2. ** Business continuity ** : For enterprises, system downtime means huge economic losses. Rapid backup recovery can minimize downtime and ensure business continuity.\n' +
    '3. ** Regulatory Compliance ** : Many industry regulations require enterprises to regularly back up and store data for a long time.\n' +
    '4. ** Resisting ransomware ** : If you encounter a ransomware attack, having a clean backup means you can choose not to pay the ransom and directly restore the system from the backup.',
}

// --- 2. 模拟配置 ---
const MIN_THINKING_TIME_MS = 3000 // 最小思考时间 (毫秒)
const MAX_THINKING_TIME_MS = 5000 // 最大思考时间 (毫秒)
const TYPING_SPEED_MS = 50 // 打字速度 (毫秒/字)

const messages = ref([])
const userInput = ref('')
const isLoading = ref(false)
const chatHistoryRef = ref(null)

// --- 3. 创建一个假的AI API调用 ---
const fetchFakeAiResponse = (userQuery) => {
  return new Promise((resolve) => {
    const thinkingTime =
      Math.floor(Math.random() * (MAX_THINKING_TIME_MS - MIN_THINKING_TIME_MS + 1)) +
      MIN_THINKING_TIME_MS

    setTimeout(() => {
      // 查找知识库 (忽略首尾空格和大小写，并尝试去除末尾的标点)
      const queryKey = Object.keys(knowledgeBase).find((key) => {
        const normalizedKey = key.replace(/[？?]/g, '').toLowerCase()
        const normalizedQuery = userQuery.trim().replace(/[？?]/g, '').toLowerCase()
        return normalizedKey === normalizedQuery
      })

      if (queryKey) {
        resolve(knowledgeBase[queryKey]) // 找到了答案
      } else {
        // 未找到答案时的默认回复
        resolve(
          'Sorry, I am currently mainly focused on answering questions about backup strategies. You can try asking me: "What is incremental backup?"',
        )
      }
    }, thinkingTime)
  })
}

// --- 4. 实现打字机流式输出效果 ---
const streamAiResponse = (fullText, messageIndex) => {
  let charIndex = 0
  const typingInterval = setInterval(() => {
    if (charIndex < fullText.length) {
      messages.value[messageIndex].text += fullText[charIndex]
      charIndex++
      scrollToBottom() // 持续滚动，确保新打出的字在视野内
    } else {
      clearInterval(typingInterval)
      isLoading.value = false // 在打字结束后才解除禁用状态
    }
  }, TYPING_SPEED_MS)
}

// 滚动到聊天记录底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatHistoryRef.value) {
      chatHistoryRef.value.scrollTop = chatHistoryRef.value.scrollHeight
    }
  })
}

// 发送消息的主函数 (已重构)
const sendMessage = async () => {
  const text = userInput.value.trim()
  if (!text || isLoading.value) return

  // 1. 显示用户消息
  messages.value.push({ sender: 'user', text })
  userInput.value = ''
  scrollToBottom()

  // 2. 显示AI“思考中”的加载状态
  isLoading.value = true
  const aiLoadingMessageIndex = messages.value.length
  messages.value.push({ sender: 'ai', text: '', isLoading: true })
  scrollToBottom()

  // 3. 调用假的API并获取完整回复
  try {
    const fullAiResponseText = await fetchFakeAiResponse(text)

    // 4. 准备开始打字：移除“思考中”，替换为一个空的消息泡
    messages.value[aiLoadingMessageIndex] = {
      sender: 'ai',
      text: '', // 清空，准备接收打字机内容
      isLoading: false,
    }

    // 5. 开始流式输出打字机效果
    streamAiResponse(fullAiResponseText, aiLoadingMessageIndex)
  } catch (error) {
    // 尽管是假的，但保留错误处理框架以防万一
    console.error('Fake AI Error:', error)
    ElMessage.error('An internal error occurred in the AI assistant.')
    messages.value[aiLoadingMessageIndex] = {
      sender: 'ai',
      text: 'Sorry, it seems I have a bit of a problem...',
      isLoading: false,
    }
    isLoading.value = false
    scrollToBottom()
  }
}

// 组件挂载时显示欢迎语
onMounted(() => {
  messages.value.push({
    sender: 'ai',
    text: 'Hello! I am your AI backup assistant. You can ask me questions, for example: "What is incremental backup?"',
  })
  scrollToBottom()
})
</script>

<style scoped>
/* Styles remain the same, with one addition for newlines */
.ai-assistant-view {
  padding: 20px;
}

.chat-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
}
.card-header .el-icon {
  margin-right: 8px;
}

.chat-history {
  height: calc(70vh - 150px);
  min-height: 300px;
  overflow-y: auto;
  border: 1px solid #eee;
  padding: 15px;
  margin-bottom: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.message-row {
  display: flex;
  margin-bottom: 10px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.ai {
  justify-content: flex-start;
}

.message-bubble {
  padding: 10px 15px;
  border-radius: 15px;
  max-width: 70%;
  word-wrap: break-word;
  white-space: pre-wrap; /*  <-- 新增：让 \n 换行符生效 */
}

.message-bubble.user {
  background-color: #409eff;
  color: white;
  border-bottom-right-radius: 5px;
}

.message-bubble.ai {
  background-color: #e4e4e4;
  color: #333;
  border-bottom-left-radius: 5px;
}
.message-bubble.ai .el-icon {
  vertical-align: middle;
  margin-right: 5px;
}

.chat-input-area {
  display: flex;
}
</style>
