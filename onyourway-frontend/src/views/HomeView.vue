<template>
  <div class="home-container">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content" data-aos="fade-up" data-aos-duration="1000">
        <h1 class="hero-title">가던길 <span>OnYourWay</span></h1>

        <div v-if="!token">
          <button @click="redirectToKakao" class="cta-button">카카오로 시작하기</button>
        </div>
        <div v-else>
          <p class="user-welcome">{{ nickname }}님, 환영합니다!</p>
          <RouterLink to="/schedule/new">
            <button class="cta-button alt">시작하기</button>
          </RouterLink>
        </div>
      </div>
    </section>

    <section class="feature-section" data-aos="fade-up" data-aos-duration="1000">
      <div class="feature-card">
        <h2>📩 여행 일정 알림</h2>
        <p>여행이 끝나는 그날까지<br />
          <b>원하는 시간</b>에 <b>하루 일정</b>을 알려드립니다.</p>
      </div>
    </section>

    <section class="feature-section alt" data-aos="fade-up" data-aos-duration="1000">
      <div class="feature-card">
        <h2>🗓️ 편리한 일정 생성</h2>
        <p>장소와 시간을 골라<br />
          일정을 <b>편리하게 생성</b>해 보세요.</p>
      </div>
    </section>

    <section class="feature-section" data-aos="fade-up" data-aos-duration="1000">
      <div class="feature-card">
        <h2>🤝 AI 요약</h2>
        <p>생성한 일정을 <b>AI가 요약</b>해<br />
          한눈에 알아볼 수 있습니다.</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { token, nickname, useAuth } from '../utils/auth.js'
import { onMounted } from 'vue'
const { redirectToKakao } = useAuth()

onMounted(() => {
  const hero = document.querySelector('.hero')
  if (hero) {
    setTimeout(() => {
      hero.classList.add('active')
    }, 100)
  }
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=SUIT&display=swap');

.home-container {
  font-family: 'SUIT', sans-serif;
  background-color: #f5f7fa;
  color: #222;
}

.hero {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(to bottom right, #e9f5f3, #ffffff);
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 2rem;
  overflow: hidden;
}

.hero::before {
  content: '';
  position: absolute;
  top: 0; left: 0;
  width: 100%;
  height: 100%;
  background-image: url('../assets/test2.png');
  background-size: cover;
  background-position: center;
  z-index: 0;
  opacity: 0;
  transform: translateY(20px) scale(1.05);
  filter: blur(10px);
  transition: opacity 1.8s ease, transform 1.8s ease;
  border-radius: inherit;
}

.hero.active::before {
  opacity: 0.8;
  transform: translateY(0) scale(1);
}

.hero-content {
  max-width: 720px;
  z-index: 1;
  position: relative;
  padding-bottom: 150px;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 1rem;
  color: #222;
}
.hero-title span {
  color: #00BFA6;
}

.user-welcome {
  font-size: 1.125rem;
  margin-bottom: 1rem;
  color: #444;
}

.cta-button {
  background-color: #00BFA6;
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  font-size: 1.1rem;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.cta-button:hover {
  background-color: #009e90;
}
.cta-button.alt {
  background-color: transparent;
  color: #00BFA6;
  border: 2px solid #00BFA6;
}
.cta-button.alt:hover {
  background-color: #00BFA6;
  color: white;
}

.feature-section {
  padding: 6rem 1rem;
  background-color: #fff;
  display: flex;
  justify-content: center;
}
.feature-section.alt {
  background-color: #f0f4f6;
}

.feature-card {
  max-width: 700px;
  text-align: center;
}

.feature-card h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #00BFA6;
  margin-bottom: 1rem;
}

.feature-card p {
  font-size: 1.1rem;
  color: #555;
  line-height: 1.7;
}

</style>
