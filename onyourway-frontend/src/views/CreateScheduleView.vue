<template>
  <div id="create-schedule-view" class="d-flex">
    <ScheduleSidebar
        :title="title"
        :startDate="startDate"
        :endDate="endDate"
        :scheduleItemsByDate="scheduleItemsByDate"
        :selectedDay="selectedDay"
        @update:title="title = $event"
        @update:startDate="handleStartDateChange"
        @update:endDate="handleEndDateChange"
        @set-day="selectedDay = $event"
        @remove-item="removeScheduleItem"
        @save-schedule="saveSchedule"
    />
    <MapView @place-selected="handlePlaceSelect" />
  </div>
</template>

<script>
import ScheduleSidebar from '../components/ScheduleSidebar.vue'
import MapView from './MapView.vue'
import { reactive } from "vue";

export default {
  name: 'CreateScheduleView',
  components: { ScheduleSidebar, MapView },
  data() {
    return {
      title: '',
      startDate: '',
      endDate: '',
      selectedDay: '',
      scheduleItemsByDate: reactive({}),
    }
  },
  watch: {
    startDate() {
      this.generateScheduleDateGroups()
    },
    endDate() {
      this.generateScheduleDateGroups()
    }
  },

  methods: {
    handleStartDateChange(date) {
      this.startDate = date
      if (this.endDate && this.endDate < this.startDate) {
        this.endDate = this.startDate
      }
    },
    handleEndDateChange(date) {
      if (this.startDate && date < this.startDate) {
        alert('종료일은 시작일보다 빠를 수 없습니다.')
        date = null;
        this.endDate = null;
        return
      }
      this.endDate = date
    },
    generateScheduleDateGroups() {
      if (!this.startDate || !this.endDate) return
      const start = new Date(this.startDate)
      const end = new Date(this.endDate)
      if (end < start) return

      const dateMap = {}
      while (start <= end) {
        const iso = start.toISOString().split('T')[0]
        if (!this.scheduleItemsByDate[iso]) {
          dateMap[iso] = []
        } else {
          dateMap[iso] = this.scheduleItemsByDate[iso]
        }
        start.setDate(start.getDate() + 1)
      }
      this.scheduleItemsByDate = dateMap
    },
    handlePlaceSelect(place) {
      if (!this.selectedDay) {
        alert('⛔ 날짜를 먼저 선택해주세요 (1일차 버튼 클릭)')
        return
      }
      const item = {
        title: place.title,
        locationName: place.addr1,
        latitude: place.mapy,
        longitude: place.mapx,
        time: null,
        memo: '',
      }

      const updatedItems = [...(this.scheduleItemsByDate[this.selectedDay] || []), item]
      const sortedItems = this.sortItemsByTime(updatedItems)

      this.scheduleItemsByDate = {
        ...this.scheduleItemsByDate,
        [this.selectedDay]: sortedItems
      }
    },

    removeScheduleItem(day, index) {
      const items = [...this.scheduleItemsByDate[day]]
      items.splice(index, 1)
      this.scheduleItemsByDate[day] = this.sortItemsByTime(items) // ✅
    },
    async saveSchedule() {
      try {
        const token = localStorage.getItem('token')
        if (!token || token === 'null' || token === 'undefined') {
          alert('로그인 정보가 만료되었습니다. 다시 로그인해주세요.')
          return
        }
        const schedule = {
          title: this.title,
          startDate: this.startDate,
          endDate: this.endDate,
        }

        const res = await fetch('/api/schedules', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(schedule),
        })
        if (!res.ok) throw new Error('일정 생성 실패')
        const scheduleId = await res.json()

        // flat
        const items = []
        for (const [day, list] of Object.entries(this.scheduleItemsByDate)) {
          for (const item of list) {
            items.push({ ...item, day })
          }
        }

        await fetch(`/api/schedules/${scheduleId}/items/bulk`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(items),
        })
        alert('일정 생성 완료!')
        this.$router.push(`/schedule/mine`)

      } catch (err) {
        console.error('❌ 일정 저장 실패:', err)
        alert('일정 저장 중 오류가 발생했습니다.')
      }
    },

    sortItemsByTime(items) {
      return items.slice().sort((a, b) => {
        if (!a.time) return 1
        if (!b.time) return -1
        return a.time.localeCompare(b.time)
      })
    },

  },
}
</script>

<style scoped>
#create-schedule-view {
  display: flex;
  flex-grow: 1;
  width: 100%;
  overflow: hidden;
  margin-top: 25px;
}
</style>
