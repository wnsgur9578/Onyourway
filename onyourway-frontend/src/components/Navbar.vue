<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom shadow-sm fixed-top">
    <div class="container">
      <RouterLink class="navbar-brand fw-bold" to="/">OnYourWay</RouterLink>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
              aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
        <ul class="navbar-nav">
          <li class="nav-item"><RouterLink class="nav-link" to="/">홈</RouterLink></li>

          <li v-if="!token" class="nav-item">
            <a class="nav-link" href="#" @click="redirectToKakao">로그인</a>
          </li>

          <li v-if="token" class="nav-item"><RouterLink class="nav-link" to="/schedule/new">일정 추가</RouterLink></li>
          <li v-if="token" class="nav-item"><RouterLink class="nav-link" to="/schedule/mine">내 일정</RouterLink></li>
          <li v-if="token" class="nav-item"><RouterLink class="nav-link" to="/profile">내 정보</RouterLink></li>

          <li v-if="token" class="nav-item">
            <a class="nav-link" href="#" @click="logout">로그아웃</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { token } from '../utils/auth.js'
import { RouterLink } from 'vue-router'
import { useAuth } from '../utils/auth.js'
const { redirectToKakao, logout } = useAuth()
</script>

<style scoped>
.navbar-brand {
  color: #00BFA6;
}

.navbar-toggler {
  border: none;
}
.navbar-toggler:focus {
  box-shadow: none !important;
  outline: none !important;
}
</style>
