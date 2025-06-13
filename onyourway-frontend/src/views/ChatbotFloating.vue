<template>
  <div class="chatbot-wrapper">
    <button v-if="showToggle" class="chatbot-button" @click="toggleChat">💬</button>

    <Transition name="chatbot-slide" @after-leave="afterChatClose">
      <div v-if="showChat" class="chatbot-window glass shadow-lg">
        <div class="chatbot-header">
          <strong>AI 챗봇</strong>
          <button class="close-btn" @click="toggleChat">✖</button>
        </div>

        <div class="chatbot-body" ref="chatBody">
          <div v-for="(msg, index) in messages" :key="index" :class="['msg', msg.from]">
            {{ msg.text }}
          </div>
        </div>

        <div class="chatbot-input">
          <input
              v-model="input"
              type="text"
              placeholder="메시지를 입력하세요"
              @keyup.enter.prevent="sendMessage"
          />
        </div>
      </div>
    </Transition>
  </div>
</template>


<script setup>
import { ref, nextTick } from 'vue'
import api from '../utils/api'

const input = ref('')
const messages = ref([])
const showChat = ref(false)
const showToggle = ref(true)
const chatBody = ref(null)

const toggleChat = () => {
  if (showChat.value) {
    showChat.value = false
    showToggle.value = false // 일단 숨김
  } else {
    showToggle.value = false
    showChat.value = true
  }

  if (messages.value.length === 0) {
    messages.value.push({ from: 'bot', text: '여행일정 도우미입니다. 필요한 사항이 있으면 말씀해주세요 :)' })
    nextTick(scrollToBottom)
  }
}

// 챗봇이 완전히 닫힌 후 버튼 다시 표시
const afterChatClose = () => {
  setTimeout(() => {
    showToggle.value = true
  }, 50) // animation 끝나고 렌더링 지연
}

const sendMessage = async () => {
  const trimmed = input.value.trim()
  if (!trimmed) return

  messages.value.push({ from: 'user', text: trimmed })
  input.value = ''
  scrollToBottom()

  try {
    const response = await api.post('/api/chat', {
      message: trimmed,
    })

    const reply = response.data.reply || '응답을 불러올 수 없습니다.'
    messages.value.push({ from: 'bot', text: reply })
  } catch (error) {
    console.error('❌ 챗봇 응답 실패:', error)
    messages.value.push({ from: 'bot', text: '서버와 연결되지 않았습니다.' })
  }

  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.chatbot-wrapper {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 9999;
}

.chatbot-button {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(0, 191, 166, 0.7);
  color: white;
  font-size: 24px;
  border: none;
  cursor: pointer;
  backdrop-filter: blur(6px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  transition: all 0.3s ease;
}
.chatbot-button:hover {
  background: rgba(0, 191, 166, 1);
}

.chatbot-window {
  width: 320px;
  height: 460px;
  display: flex;
  flex-direction: column;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}

.glass {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
}

.chatbot-header {
  background: rgba(0, 191, 166, 0.9);
  color: white;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.chatbot-body {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.msg {
  padding: 8px 12px;
  border-radius: 16px;
  font-size: 14px;
  max-width: 80%;
  word-break: break-word;
  display: inline-block;
  white-space: pre-wrap;
  text-align: left; /* ✅ 왼쪽 정렬 */
}

.msg.user {
  background: rgba(0, 191, 166, 0.2);
  color: #222;
  align-self: flex-end;
  margin-left: auto;
}

.msg.bot {
  background: rgba(255, 255, 255, 0.6);
  color: #111;
  align-self: flex-start;
  margin-right: auto;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.chatbot-input {
  padding: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.25);
}

.chatbot-input input {
  width: 100%;
  padding: 10px;
  border-radius: 20px;
  border: none;
  font-size: 14px;
  outline: none;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: inset 0 0 4px rgba(0, 0, 0, 0.05);
}

.chatbot-slide-enter-active {
  animation: slideUpFade 0.3s ease-out forwards;
}
.chatbot-slide-leave-active {
  animation: slideDownFade 0.2s ease-in forwards;
}

@keyframes slideUpFade {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideDownFade {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(40px);
  }
}
</style>
