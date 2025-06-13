<template>
  <div class="schedule-detail-wrapper">
    <div id="schedule-detail-view" class="d-flex">
      <ScheduleSidebar
          :title="title"
          :startDate="startDate"
          :endDate="endDate"
          :scheduleItemsByDate="scheduleItemsByDate"
          :selectedDay="selectedDay"
          :showUpdate="true"
          :showDelete="true"
          :showShare="true"
          @update:title="title = $event"
          @update:startDate="handleStartDateChange"
          @update:endDate="handleEndDateChange"
          @set-day="selectedDay = $event"
          @remove-item="removeScheduleItem"
          @update-schedule="updateSchedule"
          @delete-schedule="deleteSchedule"
      />
      <MapView @place-selected="handlePlaceSelect" :places="allPlaces" />
    </div>
  </div>
</template>

<script>
import ScheduleSidebar from '../components/ScheduleSidebar.vue'
import MapView from './MapView.vue'

export default {
  name: 'ScheduleDetailView',
  components: { ScheduleSidebar, MapView },
  data() {
    return {
      title: '',
      startDate: '',
      endDate: '',
      selectedDay: '',
      scheduleItemsByDate: {},
    }
  },

  computed: {
    allPlaces() {
      // ✅ 모든 날짜의 item에 day 정보도 붙이기
      const result = []
      for (const [day, items] of Object.entries(this.scheduleItemsByDate)) {
        const sorted = items.slice().sort((a, b) => {
          if (!a.time) return 1
          if (!b.time) return -1
          return a.time.localeCompare(b.time)
        })
        sorted.forEach((item, i) => {
          result.push({
            ...item,
            day: day,      // 🔥 날짜 키
            order: i       // 🔥 순서
          })
        })
      }
      return result
    }
  },

  mounted() {
    this.loadSchedule()
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
    async loadSchedule() {
      const token = localStorage.getItem('token')
      const id = this.$route.params.id
      const res = await fetch(`/api/schedules/${id}/full`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = await res.json()
      this.title = data.title
      this.startDate = data.startDate
      this.endDate = data.endDate

      const grouped = {}
      data.items.forEach(item => {
        if (!grouped[item.day]) grouped[item.day] = []
        grouped[item.day].push(item)
      })
      this.scheduleItemsByDate = grouped
    },
    handleStartDateChange(date) {
      this.startDate = date
      if (this.endDate && this.endDate < this.startDate) {
        this.endDate = this.startDate
      }
    },
    handleEndDateChange(date) {
      if (this.startDate && date < this.startDate) {
        alert('⛔ 종료일은 시작일보다 빠를 수 없습니다.')
        return
      }
      this.endDate = date
    },
    removeScheduleItem(day, index) {
      const items = [...this.scheduleItemsByDate[day]]
      items.splice(index, 1)
      this.scheduleItemsByDate[day] = this.sortItemsByTime(items) // ✅
    },
    async updateSchedule() {
      const token = localStorage.getItem('token')
      const scheduleId = this.$route.params.id
      const updateData = {
        title: this.title,
        startDate: this.startDate,
        endDate: this.endDate
      }

      await fetch(`/api/schedules/${scheduleId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updateData),
      })

      const items = []
      for (const [day, list] of Object.entries(this.scheduleItemsByDate)) {
        for (const item of list) {
          items.push({ ...item, day })
        }
      }

      await fetch(`/api/schedules/${scheduleId}/items/bulk`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(items),
      })

      alert('수정 완료!')
    },
    async deleteSchedule() {
      const confirmDelete = confirm('정말 삭제하시겠습니까?')
      if (!confirmDelete) return

      const token = localStorage.getItem('token')
      const id = this.$route.params.id
      await fetch(`/api/schedules/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      alert('삭제 완료')
      this.$router.push('/schedule/mine')
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

      // 🔥 핵심: 전체 객체를 재할당해서 Vue의 반응성 강제 유도
      this.scheduleItemsByDate = {
        ...this.scheduleItemsByDate,
        [this.selectedDay]: sortedItems
      }
    },

    generateScheduleDateGroups() {
      if (!this.startDate || !this.endDate) return
      const start = new Date(this.startDate)
      const end = new Date(this.endDate)
      if (end < start) return

      const newMap = {}
      const toDelete = []

      // 날짜별로 체크
      for (const day in this.scheduleItemsByDate) {
        if (day < this.startDate || day > this.endDate) {
          toDelete.push(day)
        }
      }

      // 삭제 확인
      if (toDelete.length > 0) {
        const confirmDelete = confirm(
            `종료일 변경으로 인해 다음 날짜의 일정이 삭제됩니다:\n${toDelete.join('\n')}\n계속하시겠습니까?`
        )
        if (!confirmDelete) {
          // 변경 취소
          this.endDate = Object.keys(this.scheduleItemsByDate).sort().pop() // 기존 마지막 날짜로 복구
          return
        }
      }

      // 삭제 후 날짜 그룹 재구성
      const cursor = new Date(this.startDate)
      while (cursor <= new Date(this.endDate)) {
        const iso = cursor.toISOString().split('T')[0]
        newMap[iso] = this.scheduleItemsByDate[iso] || []
        cursor.setDate(cursor.getDate() + 1)
      }

      this.scheduleItemsByDate = newMap
    },

    sortItemsByTime(items) {
      return items.slice().sort((a, b) => {
        if (!a.time) return 1
        if (!b.time) return -1
        return a.time.localeCompare(b.time)
      })
    },

  }
}
</script>

<style scoped>
.schedule-detail-wrapper {
  margin-top: 25px;
}

#schedule-detail-view {
  display: flex;
  flex-grow: 1;
  width: 100%;
  overflow: hidden;
}
</style>
