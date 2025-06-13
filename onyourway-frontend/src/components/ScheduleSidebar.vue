<template>
  <div class="sidebar glass-panel">
    <h4 class="sidebar-title">📝 일정 구성</h4>

    <div class="form-block">
      <label class="form-label">제목</label>
      <input :value="title" @input="$emit('update:title', $event.target.value)" class="form-control styled-input" placeholder="여행 제목 입력" />
    </div>

    <div class="form-block">
      <label class="form-label">시작일</label>
      <input :value="startDate" @input="$emit('update:startDate', $event.target.value)" class="form-control styled-input" type="date" />
    </div>

    <div class="form-block">
      <label class="form-label">종료일</label>
      <input :value="endDate" @input="$emit('update:endDate', $event.target.value)" class="form-control styled-input" type="date" />
    </div>

    <hr class="divider" />

    <div v-if="Object.keys(scheduleItemsByDate).length === 0" class="text-muted empty-message">
      날짜를 선택하면 일정 그룹이 생성됩니다.
    </div>

    <div v-for="(items, day, index) in sortedScheduleItemsByDate" :key="day" class="day-section">
      <h5 class="day-title">📅 {{ index + 1 }}일차 ({{ day }})</h5>
      <button class="btn btn-sm mb-2" :class="selectedDay === day ? 'btn-primary' : 'btn-outline-primary'" @click="$emit('set-day', day)">
        이 날짜에 일정 추가
      </button>
      <ul class="list-group">
        <li v-for="(item, idx) in items" :key="idx" class="list-group-item schedule-item">
          <strong>{{ item.title }}</strong>
          <div class="text-muted small">📍 {{ item.locationName }}</div>
          <input v-model="item.time" type="time" class="form-control mt-2 styled-input" />
          <textarea v-model="item.memo" class="form-control mt-2 styled-input" rows="1" placeholder="메모"></textarea>
          <button class="btn btn-sm btn-outline-danger mt-2" @click="$emit('remove-item', day, idx)">삭제</button>
        </li>
      </ul>
    </div>

    <div class="d-grid gap-2 mt-4">
      <button v-if="!showUpdate" class="btn btn-success action-button" @click="$emit('save-schedule')">저장</button>
      <button v-if="showUpdate" class="btn btn-success action-button" @click="$emit('update-schedule')">수정</button>
      <button v-if="showDelete" class="btn btn-danger action-button" @click="$emit('delete-schedule')">삭제</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ScheduleSidebar',
  props: {
    title: String,
    startDate: String,
    endDate: String,
    scheduleItemsByDate: Object,
    selectedDay: String,
    showUpdate: { type: Boolean, default: false },
    showDelete: { type: Boolean, default: false },
    showShare: { type: Boolean, default: false }
  },
  computed: {
    sortedScheduleItemsByDate() {
      const sorted = {}
      for (const [day, items] of Object.entries(this.scheduleItemsByDate)) {
        sorted[day] = items.slice().sort((a, b) => {
          if (!a.time) return 1
          if (!b.time) return -1
          return a.time.localeCompare(b.time)
        })
      }
      return sorted
    }
  }
}
</script>

<style scoped>
.sidebar {
  width: 340px;
  flex-shrink: 0;
  padding: 24px;
  height: 100vh;
  overflow-y: auto;
  border-right: 1px solid #ddd;
  background: #ffffff88;
}

.glass-panel {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.4);
  border-right: 1px solid rgba(255, 255, 255, 0.25);
}

.sidebar-title {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  color: #00BFA6;
}

.form-block {
  margin-bottom: 1.2rem;
}

.styled-input {
  border-radius: 0.75rem;
  border: 1px solid #ddd;
  padding: 0.5rem 0.75rem;
  font-size: 0.95rem;
}

.divider {
  margin: 1.5rem 0;
  border-color: #ddd;
}

.day-section {
  margin-bottom: 2rem;
}

.day-title {
  font-weight: 540;
  color: #444;
  margin-bottom: 0.5rem;
}

.schedule-item {
  border-radius: 0.75rem;
  padding: 0.75rem;
  margin-top: 0.5rem;
}

.action-button {
  border-radius: 1.5rem;
  font-weight: 600;
  font-size: 1rem;
}

.empty-message {
  font-size: 0.9rem;
  text-align: center;
  padding: 1rem 0;
}

.btn-success {
  border: none;
  background: #00BFA6;
}

.btn-success:hover {
  background: #07adb7;
  transition-duration: 0.3s;
}
</style>
