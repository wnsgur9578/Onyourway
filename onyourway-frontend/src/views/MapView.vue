<template>
  <div id="map-wrapper">
    <div id="map-container">
      <SearchBar @search="handleSearch" />
      <div id="map" ref="mapEl"></div>

      <div v-if="selectedPlace" class="place-popup glass animate__animated animate__fadeInUp">
        <div class="popup-header">
          <h5 class="title">{{ selectedPlace.title }}</h5>
          <button @click="selectedPlace = null" class="btn-close">&times;</button>
        </div>
        <p class="addr">{{ selectedPlace.addr1 }}</p>
        <button @click="addPlaceToSchedule" class="btn btn-success w-100 mt-2">일정에 추가</button>
      </div>
    </div>
    <ChatbotFloating />
  </div>
</template>

<script>
import axios from 'axios'
import ChatbotFloating from '../views/ChatbotFloating.vue'
import SearchBar from '../components/SearchBar.vue'

export default {
  name: 'MapView',
  components: { ChatbotFloating, SearchBar },
  props: {
    places: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      map: null,
      markers: [],
      polylines: [],
      selectedPlace: null,
      selectedMarker: null,
    }
  },
  watch: {
    places: {
      handler(newPlaces) {
        if (this.map && Array.isArray(newPlaces)) {
          this.displayScheduleMarkers(newPlaces)
        }
      },
      deep: true,
      immediate: true
    }
  },

  mounted() {
    const script = document.createElement('script')
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=${import.meta.env.VITE_KAKAO_JAVASCRIPT_API_KEY}`
    script.async = true

    script.onload = () => {
      window.kakao.maps.load(() => {
        this.initMap()
        window.addEventListener('resize', this.relayoutMap)
        this.displayScheduleMarkers(this.places)  // 초기 마커 표시
      })
    }

    document.head.appendChild(script)
  },

  beforeUnmount() {
    window.removeEventListener('resize', this.relayoutMap)
  },

  methods: {
    initMap() {
      const container = this.$refs.mapEl
      const options = {
        center: new kakao.maps.LatLng(36.2665, 127.5780),
        level: 12,
      }
      this.map = new kakao.maps.Map(container, options)
      setTimeout(() => this.map.relayout(), 100)
    },

    relayoutMap() {
      if (this.map) this.map.relayout()
    },

    displayScheduleMarkers(places) {
      if (!this.map) return

      this.markers.forEach(m => m.setMap(null))
      if (this.polylines) this.polylines.forEach(p => p.setMap(null))
      this.markers = []
      this.polylines = []

      const bounds = new kakao.maps.LatLngBounds()
      const dayGroups = {}

      const startDate = this.$root.startDate || this.startDate || places[0]?.day || ''

      places.forEach(place => {
        if (!place.day) return
        const dayIndex = this.getDayIndex(place.day, startDate)
        if (!dayGroups[dayIndex]) dayGroups[dayIndex] = []
        dayGroups[dayIndex].push(place)
      })

      const sortedDays = Object.keys(dayGroups).map(Number).sort((a, b) => a - b)

      const colors = ['#e74c3c', '#3498db', '#2ecc71', '#f1c40f', '#9b59b6', '#1abc9c']

      sortedDays.forEach((dayKey, idx) => {
        const path = []
        const dayList = [...dayGroups[dayKey]].sort((a, b) => a.order - b.order)

        dayList.forEach(place => {
          const x = Number(place.longitude)
          const y = Number(place.latitude)
          if (!x || !y || isNaN(x) || isNaN(y)) return

          const pos = new kakao.maps.LatLng(y, x)
          path.push(pos)

          const marker = new kakao.maps.Marker({ map: this.map, position: pos })

          kakao.maps.event.addListener(marker, 'click', () => {
            this.selectedPlace = place
            if (this.selectedMarker) this.selectedMarker.setImage(null)
            const imageSrc = '/images/marker_red.png'
            const imageSize = new kakao.maps.Size(28, 41)
            marker.setImage(new kakao.maps.MarkerImage(imageSrc, imageSize))
            this.selectedMarker = marker
          })

          this.markers.push(marker)
          bounds.extend(pos)
        })

        if (path.length >= 2) {
          const polyline = new kakao.maps.Polyline({
            path,
            strokeWeight: 4,
            strokeColor: colors[idx % colors.length],
            strokeOpacity: 0.9,
            strokeStyle: 'solid',
            map: this.map
          })
          this.polylines.push(polyline)
        }
      })

      for (let i = 0; i < sortedDays.length - 1; i++) {
        const current = sortedDays[i]
        const next = sortedDays[i + 1]

        const lastPlace = dayGroups[current].sort((a, b) => b.order - a.order)[0]
        const firstPlace = dayGroups[next].sort((a, b) => a.order - b.order)[0]

        const pos1 = new kakao.maps.LatLng(Number(lastPlace.latitude), Number(lastPlace.longitude))
        const pos2 = new kakao.maps.LatLng(Number(firstPlace.latitude), Number(firstPlace.longitude))

        const blackLine = new kakao.maps.Polyline({
          path: [pos1, pos2],
          strokeWeight: 3,
          strokeColor: '#000000',
          strokeOpacity: 0.8,
          strokeStyle: 'shortdot',
          map: this.map
        })
        this.polylines.push(blackLine)
      }

      if (this.markers.length > 0) this.map.setBounds(bounds)
    },

    displayTourMarkers(items) {
      if (!this.map) return

      // 기존 마커 제거
      this.markers.forEach(m => m.setMap(null))
      this.markers = []

      const bounds = new kakao.maps.LatLngBounds()

      items.forEach((place) => {
        const x = Number(place.mapx)
        const y = Number(place.mapy)
        if (!x || !y || isNaN(x) || isNaN(y)) return
        if (x < 120 || x > 130 || y < 30 || y > 40) return

        const position = new kakao.maps.LatLng(y, x)

        const marker = new kakao.maps.Marker({
          map: this.map,
          position,
        })

        // 마커 클릭 시 색 변환
        kakao.maps.event.addListener(marker, 'click', () => {
          this.selectedPlace = place

          if (this.selectedMarker) {
            this.selectedMarker.setImage(null)
          }

          const imageSrc = '/images/marker_red.png';
          const imageSize = new kakao.maps.Size(28, 41);
          const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize)
          marker.setImage(markerImage)

          this.selectedMarker = marker
        })

        this.markers.push(marker)
        bounds.extend(position)
      })

      if (this.markers.length > 0) this.map.setBounds(bounds)
    },


    async fetchTourSpots(params = {}) {
      const endpoint = params.keyword
          ? 'https://apis.data.go.kr/B551011/KorService1/searchKeyword1'
          : 'https://apis.data.go.kr/B551011/KorService1/areaBasedList1'

      const queryParams = {
        serviceKey: `${import.meta.env.VITE_APIS_SERVICE_KEY}`,
        numOfRows: 200,
        pageNo: 1,
        MobileOS: 'ETC',
        MobileApp: 'OnYourWay',
        _type: 'json',
        areaCode: params.areaCode || '1',
        contentTypeId: params.contentTypeId || '',
      }
      if (params.keyword) queryParams.keyword = params.keyword

      try {
        const res = await axios.get(endpoint, { params: queryParams })
        const items = res.data?.response?.body?.items?.item
        if (!items) return alert('관광지 목록을 불러오지 못했습니다.')
        this.displayTourMarkers(items)
      } catch (err) {
        console.error('관광지 API 실패:', err)
        alert('관광지 검색 중 오류가 발생했습니다.')
      }
    },

    async addPlaceToSchedule() {
      if (!this.selectedPlace) return
      const place = this.selectedPlace
      this.$emit('place-selected', place)
      this.selectedPlace = null
    },

    handleSearch({ areaCode, contentTypeId, keyword }) {
      this.fetchTourSpots({ areaCode, contentTypeId, keyword })
    },

    getDayIndex(dateStr, startDateStr) {
      const oneDay = 1000 * 60 * 60 * 24
      const start = new Date(startDateStr)
      const current = new Date(dateStr)
      return Math.floor((current - start) / oneDay) + 1
    },
  }
}
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css');

#map-wrapper {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
#map-container {
  width: 100%;
  height: 100%;
  position: relative;
}
#map {
  width: 100%;
  height: 100%;
}
.place-popup {
  position: absolute;
  bottom: 100px;
  left: 50px;
  background: white;
  padding: 12px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  width: 280px;
  z-index: 10;
}

.place-popup {
  position: absolute;
  bottom: 100px;
  left: 40px;
  width: 280px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  color: #222;
  z-index: 10;
  animation-duration: 0.5s;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.popup-header .title {
  font-weight: 700;
  font-size: 1.1rem;
  margin: 0;
}

.popup-header .btn-close {
  background: none;
  border: none;
  font-size: 1.2rem;
  color: #444;
  cursor: pointer;
}

.addr {
  font-size: 0.95rem;
  color: #555;
  margin: 8px 0 0 0;
  word-break: break-word;
}

.btn-success {
  border: none;
  background-color: #00BFA6;
  border-radius: 12px;
  font-weight: 600;
  transition: 0.3s ease;
}

.btn-success:hover {
  background-color: #00a891;
}

</style>
