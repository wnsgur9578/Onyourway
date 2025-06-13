<template>
  <div class="container mt-5 pt-5 pb-5">
    <div class="text-center mb-5">
      <h2 class="myschedule-title fw-bold">📅 내 일정</h2>
    </div>

    <div class="d-flex flex-column gap-4 align-items-center">
      <div
          v-for="schedule in schedules"
          :key="schedule.id"
          class="schedule-card"
          @click="goToSchedule(schedule.id)"
      >
        <button class="btn-summary" @click.stop="showSummary(schedule.id)">
          AI 요약
        </button>

        <div class="fw-bold fs-5 text-center mb-2">{{ schedule.title }}</div>
        <div class="text-muted text-center">📅 {{ schedule.startDate }} ~ {{ schedule.endDate }}
        </div>

        <div style="position: absolute; top: 3.5rem; right: 1rem;" @click.stop>
          <AutoSendToggle
              :schedule-id="schedule.id"
              :schedule-date="schedule.startDate"
              @updated="handleAutoSend"
          />
        </div>
      </div>
    </div>

    <div v-if="hasMore" class="mt-4 text-center">
      <button class="btn btn-outline-success" @click="fetchSchedules" :disabled="loadingMore">
        {{ loadingMore ? '불러오는 중...' : '더보기 +' }}
      </button>
    </div>

    <Transition name="popup-fade">
      <div v-if="showSummaryPopup" class="summary-popup">
        <div class="summary-content">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <strong>🧐 AI 일정 요약</strong>
            <button @click="showSummaryPopup = false" class="btn-close"></button>
          </div>

          <div v-if="loading">요약 중입니다...</div>
          <div v-else>
            <div class="summary-section">
              <strong>📄 요약</strong>
              <p>{{ summaryText }}</p>
            </div>
            <hr />
            <div class="summary-section">
              <strong>💡 한줄평 및 참고</strong>
              <p>{{ prosConsText }}</p>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import AutoSendToggle from '@/components/AutoSendToggle.vue'

const schedules = ref([])
const currentPage = ref(0)
const pageSize = 6
const hasMore = ref(true)
const loadingMore = ref(false)

const router = useRouter()

const summaryText = ref('')
const prosConsText = ref('')
const showSummaryPopup = ref(false)
const loading = ref(false)

const goToSchedule = (id) => {
  router.push(`/schedule/${id}`)
}

const fetchSchedules = async () => {
  if (!hasMore.value || loadingMore.value) return
  loadingMore.value = true

  try {
    const token = localStorage.getItem('token')
    const res = await axios.get(`/api/schedules/mine?page=${currentPage.value}&size=${pageSize}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    const data = res.data || []
    if (data.length < pageSize) hasMore.value = false

    schedules.value.push(...data)
    currentPage.value++
  } catch (err) {
    console.error('❌ 일정 로드 실패:', err)
  } finally {
    loadingMore.value = false
  }
}

const showSummary = async (id) => {
  try {
    showSummaryPopup.value = true
    loading.value = true

    const token = localStorage.getItem('token')
    const res = await axios.get(`/api/schedules/${id}/summary`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    const full = res.data.summary
    const parts = full.split('한줄평 및 추천:')
    summaryText.value = parts[0].replace('요약:', '').trim()
    prosConsText.value = (parts[1] || '').trim()
  } catch (err) {
    summaryText.value = '요약을 불러오지 못했습니다.'
    prosConsText.value = ''
  } finally {
    loading.value = false
  }
}

const handleAutoSend = ({ scheduleId, time }) => {
  if (time) {
    console.log(`✅ ${scheduleId}번 자동 전송 설정: ${time}`)
    // TODO: 서버 저장 or 예약 등록
  } else {
    console.log(`❌ ${scheduleId}번 자동 전송 해제`)
    // TODO: 예약 취소
  }
}

onMounted(() => {
  fetchSchedules()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=SUIT&display=swap');

.container {
  font-family: 'SUIT', sans-serif;
  background-color: #fff;
  min-height: 100vh;
}

.myschedule-title {
  color: #00BFA6;
}

.schedule-card {
  position: relative;
  background: #fff;
  border-radius: 20px;
  padding: 2rem 1.5rem;
  width: 100%;
  max-width: 600px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.schedule-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 14px 36px rgba(0, 0, 0, 0.12);
}

.btn-summary {
  position: absolute;
  top: 1rem;
  right: 1rem;
  font-size: 0.8rem;
  padding: 0.4rem 0.8rem;
  border-radius: 50px;
  border: 1px solid #00bfa6;
  background-color: white;
  color: #00bfa6;
  transition: 0.3s ease;
}
.btn-summary:hover {
  background-color: #00bfa6;
  color: white;
}

.popup-fade-enter-active {
  animation: popupFadeIn 0.3s ease forwards;
}
.popup-fade-leave-active {
  animation: popupFadeOut 0.2s ease forwards;
}
@keyframes popupFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
@keyframes popupFadeOut {
  to {
    opacity: 0;
    transform: translateY(-20px);
  }
}

.summary-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.summary-content {
  background: white;
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  max-width: 480px;
  width: 90%;
}

.summary-section {
  margin-bottom: 1rem;
  font-size: 0.95rem;
  line-height: 1.5;
}

.btn-outline-success {
  border-radius: 50px;
  padding: 0.6rem 1.5rem;
  font-weight: 600;
  transition: all 0.3s ease;
}
.btn-outline-success:hover {
  background-color: #00BFA6;
  color: white;
  border-color: #00BFA6;
}
</style>
