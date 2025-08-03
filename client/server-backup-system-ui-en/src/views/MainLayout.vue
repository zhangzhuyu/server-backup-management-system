<template>
  <el-container class="main-layout">
    <el-header>
      <div class="header-content">
        <span>Server Backup Management System</span>
        <div>
          <span style="margin-right: 15px">Welcome, {{ authStore.username || 'Admin' }}</span>
          <el-button type="danger" size="small" @click="handleLogout">Logout</el-button>
        </div>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <!-- 你的侧边导航 -->
        <el-menu router :default-active="activeMenu">
          <el-menu-item index="/dashboard">Dashboard</el-menu-item>
          <el-menu-item index="/backup">Backup</el-menu-item>
          <el-menu-item index="/data">Data</el-menu-item>
          <el-menu-item index="/system">System</el-menu-item>
          <el-menu-item index="/warning">Alerts</el-menu-item>
          <el-menu-item index="/metrics">Metrics</el-menu-item>
          <el-menu-item index="/capacity">Capacity Forecast</el-menu-item>
          <el-menu-item index="/ai-assistant">AI Assistant</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
        <!-- 子路由出口 -->
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElContainer, ElHeader, ElAside, ElMain, ElMenu, ElMenuItem, ElButton } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 计算当前激活的菜单项
const activeMenu = computed(() => {
  // 根据路由路径判断主菜单激活状态
  if (route.path.startsWith('/dashboard')) return '/dashboard'
  if (route.path.startsWith('/backup')) return '/backup'
  if (route.path.startsWith('/data')) return '/data'
  if (route.path.startsWith('/system')) return '/system'
  if (route.path.startsWith('/warning')) return '/warning'
  if (route.path.startsWith('/metrics')) return '/metrics'
  if (route.path.startsWith('/capacity')) return '/capacity'
  if (route.path.startsWith('/ai-assistant')) return '/ai-assistant'
  return '/dashboard' // Default
})

const handleLogout = () => {
  // 1. 清除 localStorage 中的标记
  localStorage.removeItem('fake_token')
  console.log('Login token (fake_token) has been removed from localStorage.')

  // 2. (如果使用了 Pinia Store) 调用 store 的 logout action 来重置状态
  authStore.logout()
  console.log('Pinia Store state has been reset.')

  // 3. 跳转到登录页面
  router.push('/login')
  console.log('Redirected to /login.')
}
</script>

<style scoped>
.main-layout,
.el-container {
  height: 100vh;
}
.el-header {
  background-color: #409eff;
  color: #fff;
  line-height: 60px;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-aside {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
}
.el-menu {
  border-right: none; /* 移除 el-menu 默认的右边框 */
}
.el-main {
  background-color: #f4f4f5;
  padding: 15px; /* 给主内容区一点内边距 */
}
</style>
