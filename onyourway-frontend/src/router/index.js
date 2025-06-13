import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import KakaoCallback from '../components/KakaoCallback.vue'
import MyScheduleView from '../views/MyScheduleView.vue'
import ProfileView from '../views/ProfileView.vue'
import CreateScheduleView from "../views/CreateScheduleView.vue";
import ScheduleDetailView from "../views/ScheduleDetailView.vue";

const routes = [
    { path: '/', component: HomeView },
    { path: '/oauth/kakao/callback', component: KakaoCallback },

    {
        path: '/schedule/mine',
        component: MyScheduleView,
        meta: { requiresAuth: true },
    },
    {
        path: '/schedule/new',
        component: CreateScheduleView,
        meta: { requiresAuth: true },
    },
    {
        path: '/profile',
        component: ProfileView,
        meta: { requiresAuth: true },
    },
    {
        path: '/schedule/:id',
        name: 'ScheduleDetail',
        component: ScheduleDetailView,
        props: true
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')

    if (to.meta.requiresAuth && !token) {
        alert('로그인이 필요합니다.')
        next('/')
    } else {
        next()
    }
})

export default router
