<template>
  <div class="container mt-5 pt-5 d-flex flex-column align-items-center justify-content-center profile-wrapper">
    <!-- ✅ Glass 카드 -->
    <div class="glass-card text-center position-relative">
      <h1 class="myprofile-title fw-bold mb-4">내 정보</h1>

      <img
          :src="user.profileImage"
          alt="프로필 이미지"
          class="profile-img mb-3"
      />
      <h5 class="fw-bold mb-2">👤 {{ user.nickname }}</h5>
      <p class="text-muted fs-5">
        <img src="/kakao-icon.png" alt="카카오" class="kakao-icon" />
        {{ user.email }}
      </p>

      <button class="btn btn-outline-danger mt-4" @click="confirmUnlink">
        탈퇴하기 (카카오 연결 해제)
      </button>
    </div>

    <Transition name="slide-fade">
      <div v-if="showConfirm" class="confirm-overlay">
        <div class="confirm-modal-solid">
          <h5 class="mb-3 fw-bold">정말 탈퇴하시겠습니까?</h5>
          <p class="text-muted mb-4">
            카카오 연결이 해제되고<br />모든 일정 데이터가 삭제됩니다.
          </p>
          <div class="d-flex justify-content-center gap-3">
            <button class="btn btn-danger px-4" @click="unlinkApp">네, 탈퇴합니다</button>
            <button class="btn btn-outline-dark px-4" @click="showConfirm = false">취소</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useAuth, token } from '../utils/auth.js'
const { logout } = useAuth()

const user = ref({
  nickname: '',
  profileImage: '',
  email: '',
})

const showConfirm = ref(false)

const confirmUnlink = () => {
  showConfirm.value = true
}

const unlinkApp = async () => {
  try {
    if (!token.value) {
      alert('로그인 후 이용해주세요.')
      return
    }

    await axios.post('/api/users/unlink', null, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`
      }
    })

    alert('회원 탈퇴가 완료되었습니다.')
    logout()
  } catch (err) {
    console.error('❌ 탈퇴 실패:', err)
    alert('탈퇴에 실패했습니다.')
  }
}

const fetchUserInfo = async () => {
  try {
    if (!token.value) {
      alert('로그인 후 이용해주세요.')
      return
    }

    const res = await axios.get('/api/users/me', {
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
    })

    user.value = res.data
  } catch (err) {
    console.error('❌ 사용자 정보 불러오기 실패:', err)
    alert('사용자 정보를 불러오지 못했습니다.')
  }
}

const loadKakaoSdk = () => {
  return new Promise((resolve, reject) => {
    if (window.Kakao && window.Kakao.isInitialized()) {
      return resolve(window.Kakao)
    }

    const script = document.createElement('script')
    script.src = 'https://t1.kakaocdn.net/kakao_js_sdk/2.7.5/kakao.min.js'
    script.async = true
    script.onload = () => {
      if (window.Kakao && !window.Kakao.isInitialized()) {
        window.Kakao.init(`${import.meta.env.VITE_KAKAO_JAVASCRIPT_API_KEY}`)
        console.log('✅ Kakao SDK initialized')
      }
      resolve(window.Kakao)
    }
    script.onerror = reject
    document.head.appendChild(script)
  })
}

onMounted(() => {
  fetchUserInfo()
  loadKakaoSdk()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=SUIT&display=swap');

.profile-wrapper {
  min-height: 100vh;
  padding: 2rem;
  font-family: 'SUIT', sans-serif;
  background-color: white;
}

.glass-card {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(12px);
  padding: 3rem;
  border-radius: 2rem;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 420px;
  z-index: 1;
}

.myprofile-title {
  color: #00BFA6;
  font-size: 2rem;
}

.profile-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.kakao-icon {
  width: 20px;
  height: 20px;
  vertical-align: middle;
  margin-right: 6px;
}

.btn-outline-danger {
  border-radius: 50px;
  font-weight: 600;
  padding: 0.6rem 1.5rem;
  transition: 0.3s ease;
}
.btn-outline-danger:hover {
  background-color: #dc3545;
  color: #fff;
}

/* ✅ 모달 애니메이션 */
.slide-fade-enter-active {
  transition: all 0.3s ease;
}
.slide-fade-leave-active {
  transition: all 0.2s ease;
}
.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(-30px);
}
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

/* ✅ 오버레이 */
.confirm-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

/* ✅ 모달 카드 */
.confirm-modal-solid {
  background: #ffffff;
  border-radius: 1.5rem;
  padding: 2rem 2.5rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  max-width: 360px;
  width: 90%;
  color: #222;
  text-align: center;
  z-index: 1000;
}
</style>
