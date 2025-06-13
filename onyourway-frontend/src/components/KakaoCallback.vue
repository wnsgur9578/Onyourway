<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useAuth } from '../utils/auth.js'

const route = useRoute()
const router = useRouter()
const { login } = useAuth()

onMounted(async () => {
  const code = route.query.code
  try {
    console.log("🔍 code:", code)
    const res = await axios.post('/api/auth/kakao/callback', { code })

    // ✅ 여기 수정됨: kakaoAccessToken도 받아옴
    const { token, nickname, kakaoAccessToken } = res.data

    login(token, nickname, kakaoAccessToken)

    alert(`${nickname}님 환영합니다!`)
    router.push('/')
  } catch (err) {
    console.error(err)
    alert('카카오 로그인 실패')
    router.push('/')
  }
})
</script>
