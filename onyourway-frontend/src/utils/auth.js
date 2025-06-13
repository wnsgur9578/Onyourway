// src/utils/auth.js

import { ref } from 'vue'
import { useRouter } from 'vue-router'

export const token = ref(localStorage.getItem('token') || '')
export const nickname = ref(localStorage.getItem('nickname') || '')
export const kakaoAccessToken = ref(localStorage.getItem('kakaoAccessToken') || '')

export function useAuth() {
    const REST_API_KEY = import.meta.env.VITE_KAKAO_REST_API_KEY;
    const REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI
    const SCOPE = "profile_nickname profile_image account_email talk_message"
    const router = useRouter()

    const redirectToKakao = () => {
        window.location.href =
            `https://kauth.kakao.com/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=${SCOPE}`
    }

    const login = (newToken, newNickname, newKakaoToken) => {
        token.value = newToken
        nickname.value = newNickname
        kakaoAccessToken.value = newKakaoToken
        localStorage.setItem('token', newToken)
        localStorage.setItem('nickname', newNickname)
        localStorage.setItem('kakaoAccessToken', newKakaoToken)
    }

    const logout = () => {
        token.value = ''
        nickname.value = ''
        kakaoAccessToken.value = ''
        localStorage.removeItem('token')
        localStorage.removeItem('nickname')
        localStorage.removeItem('kakaoAccessToken')
        router.push('/')
    }

    return { redirectToKakao, login, logout }
}
