<template>
  <div class="auto-toggle-container">
    <button :class="['toggle-btn', isOn ? 'on' : 'off']" @click="toggle">
      {{ isOn ? 'ON' : 'OFF' }}
    </button>

    <!-- Floating 팝업 -->
    <div v-if="showPopup" class="popup">
      <div class="popup-inner">
        <label>보낼 시간 설정:</label>
        <input type="time" v-model="selectedTime" />
        <div class="popup-buttons">
          <button class="confirm" @click="confirmTime">확인</button>
          <button class="cancel" @click="cancel">취소</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import dayjs from 'dayjs'
import { token } from '@/utils/auth'

const props = defineProps({
  scheduleDate: String,
  scheduleId: Number
})

const emit = defineEmits(['updated'])

const isOn = ref(false)
const selectedTime = ref('')
const showPopup = ref(false)

const toggle = () => {
  if (isOn.value) {
    // OFF 처리
    isOn.value = false
    selectedTime.value = ''
    saveToggleState(props.scheduleId, false, null)
    emit('updated', { scheduleId: props.scheduleId, time: null })
  } else {
    // ON → 시간 선택창 표시
    showPopup.value = true
  }
}

const confirmTime = () => {
  if (!selectedTime.value) return

  const now = dayjs()
  const targetDateTime = dayjs(`${props.scheduleDate}T${selectedTime.value}`)

  if (targetDateTime.isBefore(now)) {
    alert('❌ 이미 지난 시간입니다.')
    return
  }

  isOn.value = true
  showPopup.value = false
  saveToggleState(props.scheduleId, true, selectedTime.value)
  emit('updated', { scheduleId: props.scheduleId, time: selectedTime.value })
}

const cancel = () => {
  showPopup.value = false
}

const fetchToggleState = async (scheduleId) => {
  try {
    const res = await axios.get(`/api/toggle/${scheduleId}`, {
      headers: {
        Authorization: `Bearer ${token.value}`
      },
    })
    if (res.data) {
      isOn.value = res.data.enabled
      selectedTime.value = res.data.time || ''
    }
  } catch (e) {
    console.error('❌ 상태 불러오기 실패', e)
  }
}

const saveToggleState = async (scheduleId, enabled, time) => {
  try {
    await axios.post(`/api/toggle/${scheduleId}`, {
      enabled,
      time,
    }, {
      headers: {
        Authorization: `Bearer ${token.value}` // ✅ 수정됨
      },
    })
  } catch (e) {
    console.error('❌ 저장 실패:', e)
    alert('토글 상태 저장에 실패했습니다. 다시 시도해 주세요.')
  }
}

onMounted(() => {
  fetchToggleState(props.scheduleId)
})
</script>
<style scoped>
.auto-toggle-container {
  position: relative;
  display: inline-block;
  margin-top: 0.5rem;
}

.toggle-btn {
  padding: 0.3rem 1rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
  cursor: pointer;
  border: 2px solid #00bfa6;
  background-color: white;
  color: #00bfa6;
  transition: 0.3s ease;
}
.toggle-btn.on {
  background-color: #00bfa6;
  color: white;
}

.popup {
  position: absolute;
  top: 50%;
  left: 120%;
  transform: translateY(-50%);
  background: white;
  border: 1px solid #ccc;
  padding: 1rem;
  border-radius: 12px;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 180px;
}

.popup-inner {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

input[type="time"] {
  padding: 0.3rem;
  font-size: 0.9rem;
  border-radius: 6px;
  border: 1px solid #ddd;
  width: 100%;
}

.popup-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}
.popup-buttons button {
  padding: 0.3rem 0.8rem;
  border: none;
  border-radius: 6px;
  font-size: 0.8rem;
  cursor: pointer;
}
.popup-buttons .confirm {
  background-color: #00bfa6;
  color: white;
}
.popup-buttons .cancel {
  background-color: #eee;
  color: #333;
}
</style>