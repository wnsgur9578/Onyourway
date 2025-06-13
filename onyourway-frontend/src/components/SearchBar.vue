<template>
  <div class="search-bar shadow d-flex flex-wrap align-items-center p-3">
    <select v-model="areaCode" class="form-select me-2" style="width: 120px">
      <option disabled value="">지역</option>
      <option value="1">서울</option>
      <option value="2">인천</option>
      <option value="3">대전</option>
      <option value="4">대구</option>
      <option value="5">광주</option>
      <option value="6">부산</option>
      <option value="7">울산</option>
      <option value="8">세종</option>
      <option value="31">경기</option>
      <option value="32">강원</option>
      <option value="33">충북</option>
      <option value="34">충남</option>
      <option value="35">경북</option>
      <option value="36">경남</option>
      <option value="37">전북</option>
      <option value="38">전남</option>
      <option value="39">제주</option>
    </select>

    <select v-model="contentTypeId" class="form-select me-2" style="width: 160px">
      <option disabled value="">관광 타입</option>
      <option value="12">관광지</option>
      <option value="14">문화시설</option>
      <option value="15">축제/공연/행사</option>
      <option value="25">여행코스</option>
      <option value="28">레포츠</option>
      <option value="32">숙박</option>
      <option value="38">쇼핑</option>
      <option value="39">음식점</option>
    </select>

    <input
        v-model="keyword"
        class="form-control me-2"
        type="text"
        @keyup.enter="search"
        placeholder="검색어 입력"
        style="width: 200px"
    />

    <button class="btn btn-success" @click="search">검색</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const emit = defineEmits(['search'])
const areaCode = ref('1')
const contentTypeId = ref('12')
const keyword = ref('')

const search = () => {
  emit('search', {
    areaCode: areaCode.value,
    contentTypeId: contentTypeId.value,
    keyword: keyword.value.trim(),
  })
}
</script>

<style scoped>
.search-bar {
  position: absolute;
  top: 1%;
  left:25%;
  z-index: 10;

  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);

  border-radius: 1rem;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.btn-success {
  border: none;
  background: #00BFA6;
  font-weight: 600;
  border-radius: 30px;
}

.btn-success:hover {
  background: #07adb7;
  transition-duration: 0.3s;
}
</style>
