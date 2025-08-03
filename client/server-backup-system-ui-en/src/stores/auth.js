// src/stores/auth.js
import { ref } from 'vue'
import { defineStore } from 'pinia'
import router from '@/router' // 引入 router

export const useAuthStore = defineStore('auth', () => {
  // 使用 ref 定义响应式状态
  const isLoggedIn = ref(false) // 初始状态为未登录
  const username = ref('')

  // Actions (方法)
  function login(user) {
    // 简单的假登录逻辑
    if (user === 'admin') {
      isLoggedIn.value = true
      username.value = user
      console.log('登录成功，跳转到备份模块')
      // 登录成功后跳转到备份模块
      router.push('/backup') // 使用 router 进行跳转
      return true
    } else {
      isLoggedIn.value = false
      username.value = ''
      console.log('登录失败，用户名需为 admin')
      alert('登录失败，请输入 admin') // 简单提示
      return false
    }
  }

  function logout() {
    isLoggedIn.value = false
    username.value = ''
    console.log('已登出，跳转到登录页')
    router.push('/login') // 登出后跳转回登录页
  }

  // 返回 state 和 actions
  return { isLoggedIn, username, login, logout }
})
