// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
// ... (你的其他 import 保持不变)
import LoginView from '../views/LoginView.vue'
import MainLayout from '../views/MainLayout.vue'
import BackupView from '../views/BackupView.vue'
import BackupStrategyView from '../views/backup/BackupStrategyView.vue'
import BackupHistoryView from '../views/backup/BackupHistoryView.vue'
import DataModuleView from '../views/DataModuleView.vue' // <--- 新增：导入数据模块视图
import WarningView from '../views/WarningView.vue'
import SystemModuleView from '@/views/SystemModuleView.vue' // 新增
import TargetServerManagement from '@/views/system/TargetServerManagement.vue' // 新增
import DashboardView from '@/views/DashboardView.vue'
import MetricsView from '@/views/MetricsView.vue'
import CapacityPredictionView from '@/views/CapacityPredictionView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/',
      component: MainLayout,
      redirect: '/backup', // 访问根路径时，自动去 /backup
      children: [
        {
          path: 'dashboard', // <--- 新增监控大屏路由
          name: 'Dashboard',
          component: DashboardView,
          meta: { title: '监控大屏' },
        },
        {
          path: 'backup',
          name: 'backup',
          component: BackupView,
          redirect: { name: 'backup-strategy' }, // 访问 /backup 时，自动去策略页
          children: [
            {
              path: 'strategy',
              name: 'backup-strategy',
              component: BackupStrategyView,
            },
            {
              path: 'history',
              name: 'backup-history',
              component: BackupHistoryView,
            },
          ],
        },
        {
          path: 'system',
          component: SystemModuleView, // **** 新增：系统模块容器 ****
          redirect: '/system/target-server', // **** 默认重定向到目标服务器 ****
          children: [
            {
              path: 'target-server',
              name: 'TargetServerManagement',
              component: TargetServerManagement, // **** 新增：目标服务器管理组件 ****
            },
            // { path: 'other-system-feature', component: () => import('@/views/system/OtherSystemFeature.vue') } // 其他系统模块子路由占位
          ],
        },
        { path: 'warning', name: 'warning', component: WarningView },
        { path: 'data', name: 'data', component: DataModuleView },
        {
          path: '/ai-assistant',
          name: 'AiAssistant',
          component: () =>
            import(/* webpackChunkName: "ai-assistant" */ '../views/AiAssistantView.vue'),
          meta: { requiresAuth: true }, // 确保也需要认证
        },
        {
          path: '/metrics', // 新增路由路径
          name: 'Metrics',
          component: MetricsView,
        },
        {
          path: '/capacity',
          name: 'CapacityPrediction',
          component: CapacityPredictionView,
          meta: { title: '容量预测', icon: 'el-icon-data-analysis' },
        },
      ],
    },
    // 可能还有其他路由...
  ],
})

// --- 这是路由守卫（路由跳转前的检查站） ---
router.beforeEach((to, from, next) => {
  console.log(`导航检查: 从 ${from.fullPath} 到 ${to.fullPath}`)

  // **** 修改这里: 从 sessionStorage 获取标记 ****
  const isLoggedIn = sessionStorage.getItem('fake_token') === 'admin_logged_in'
  console.log(`检查结果: 是否已登录 (isLoggedIn) = ${isLoggedIn}`)

  if (to.name !== 'login' && !isLoggedIn) {
    console.log('决策: 未登录，跳转到 /login')
    next({ name: 'login' })
  } else if (to.name === 'login' && isLoggedIn) {
    console.log('决策: 已登录，尝试访问登录页，跳转到 /')
    next({ path: '/' })
  } else {
    console.log('决策: 允许导航')
    next()
  }
})

export default router
