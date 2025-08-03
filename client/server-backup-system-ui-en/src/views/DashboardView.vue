<template>
  <div class="dashboard-view">
    <!-- 增强的动态背景 -->
    <div class="dashboard-bg">
      <div class="stars"></div>
      <div class="twinkling"></div>
      <div class="clouds"></div>
    </div>

    <!-- 标题 -->
    <h1 class="dashboard-title">Server Backup System - Operations Command Center</h1>

    <!-- 顶部统计栏 -->
    <el-row :gutter="20" class="top-stats">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon">
            <el-icon><Coin /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summaryStats.databases }}</div>
            <div class="stat-label">Databases Monitored</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon">
            <el-icon><Platform /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summaryStats.servers }}</div>
            <div class="stat-label">Servers Monitored</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card success">
          <div class="stat-icon">
            <el-icon><SuccessFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summaryStats.backupsToday }}</div>
            <div class="stat-label">Backups Completed Today</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card error">
          <div class="stat-icon">
            <el-icon><WarningFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ summaryStats.failuresToday }}</div>
            <div class="stat-label">Failures Today</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容网格 -->
    <div class="main-grid">
      <!-- 左侧服务器状态 (带仪表盘) -->
      <el-card class="grid-item server-status-panel" shadow="always">
        <template #header>
          <div class="panel-header">
            <el-icon><Platform /></el-icon><span>Server Load Status</span>
          </div>
        </template>
        <div v-loading="loadingServers" class="panel-body server-list-body">
          <div v-if="serverList.length > 0" class="server-grid">
            <div v-for="(server, index) in serverList" :key="server.id" class="server-item-gauge">
              <div :ref="(el) => (serverGaugeRefs[index] = el)" class="echarts-gauge"></div>
              <div class="server-gauge-name">
                {{ server.serverName || server.name || 'Unnamed Server' }}
              </div>
              <div class="server-gauge-ip">{{ server.ip }}</div>
            </div>
          </div>
          <el-empty v-else description="No Servers Monitored" :image-size="50"></el-empty>
        </div>
      </el-card>

      <!-- 中间核心状态 (可以放整体健康度、关键预警等) -->
      <el-card class="grid-item core-status-panel" shadow="always">
        <template #header>
          <div class="panel-header">
            <el-icon><Odometer /></el-icon><span>System Health Overview</span>
          </div>
        </template>
        <div class="panel-body core-body">
          <!-- 示例：放一个大的健康度仪表盘 -->
          <div ref="overallHealthGaugeRef" class="echarts-gauge-large"></div>
          <div class="health-description">
            Overall system stability is
            <span :class="healthStatus.class">{{ healthStatus.text }}</span
            >.
          </div>
        </div>
      </el-card>

      <!-- 右侧数据库状态列表 -->
      <el-card class="grid-item database-status-panel" shadow="always">
        <template #header>
          <div class="panel-header">
            <el-icon><Coin /></el-icon><span>Database Connectivity</span>
          </div>
        </template>
        <div v-loading="loadingDatabases" class="panel-body database-list-body">
          <ul v-if="databaseList.length > 0" class="data-list">
            <li v-for="db in databaseList" :key="db.id" class="data-item db-item">
              <span :class="['status-icon', db.status === 'online' ? 'online' : 'offline']">
                <el-icon><Link /></el-icon>
              </span>
              <span class="item-name db-name">{{ db.dateBaseName || 'Unnamed DB' }}</span>
              <span class="item-detail db-ip">{{ db.ip || 'Unknown IP' }}</span>
              <span class="item-detail db-type">{{ db.type || 'DB' }}</span>
            </li>
          </ul>
          <el-empty v-else description="No Databases Connected" :image-size="50"></el-empty>
        </div>
      </el-card>

      <!-- 底部活动日志 -->
      <el-card class="grid-item activity-log-panel" shadow="always">
        <template #header>
          <div class="panel-header">
            <el-icon><Document /></el-icon><span>Recent Activity Log</span>
          </div>
        </template>
        <div class="panel-body log-body">
          <ul v-if="activityLog.length > 0" class="log-list">
            <li v-for="(log, index) in activityLog" :key="index" :class="['log-item', log.type]">
              <span class="log-time">{{ log.time }}</span>
              <span class="log-message">{{ log.message }}</span>
            </li>
          </ul>
          <el-empty v-else description="No recent activity" :image-size="40"></el-empty>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed, watch } from 'vue'
import { ElCard, ElIcon, ElProgress, ElEmpty, ElRow, ElCol } from 'element-plus'
import {
  Coin,
  Platform,
  SuccessFilled,
  WarningFilled,
  Link,
  Document,
  Odometer,
} from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { GaugeChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'

// **使用你实际的 API 函数名**
import { getDatabaseList } from '@/api/database' // 确认这是你用来获取列表的函数
import { queryServers } from '@/api/server' // 确认这是你用来获取列表的函数
import { getDashboardStats } from '@/api/dashboard'

// 注册 ECharts 组件
echarts.use([GaugeChart, CanvasRenderer, TitleComponent, TooltipComponent, LegendComponent])

// --- 状态 ---
const databaseList = ref([])
const serverList = ref([])
const loadingDatabases = ref(false)
const loadingServers = ref(false)
const summaryStats = ref({ databases: 0, servers: 0, backupsToday: 0, failuresToday: 0 })
const activityLog = ref([])
const healthStatus = ref({ value: 95, text: 'Stable', class: 'stable' }) // 整体健康度

// ECharts 实例引用
const serverGaugeRefs = ref([]) // 用于服务器仪表盘 DOM 引用
const serverChartInstances = ref([]) // 用于存储服务器 ECharts 实例
const overallHealthGaugeRef = ref(null) // 整体健康度仪表盘 DOM 引用
let overallHealthChart = null // 整体健康度 ECharts 实例

let intervalId = null // 用于模拟动态数据更新的定时器

const fetchSummaryStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res && res.data) {
      // 用后端返回的真实数据更新 summaryStats
      summaryStats.value.databases = res.data.databasesMonitored ?? 0
      summaryStats.value.servers = res.data.serversMonitored ?? 0
      summaryStats.value.backupsToday = res.data.backupsToday ?? 0
      summaryStats.value.failuresToday = res.data.failuresToday ?? 0
    }
  } catch (error) {
    console.error('Error fetching summary stats:', error)
    // 出错时可以保持假数据或清零
    summaryStats.value = { databases: 0, servers: 0, backupsToday: 0, failuresToday: 0 }
  }
}

// --- 获取数据 ---
const fetchDatabases = async () => {
  loadingDatabases.value = true
  try {
    const res = await getDatabaseList({ pageNum: 1, pageSize: 10 }, {}) // 获取稍多数据
    if (res && res.data && res.data.records) {
      databaseList.value = res.data.records.map((db) => ({
        ...db,
        status: Math.random() > 0.1 ? 'online' : 'offline', // 随机模拟在线状态
        type: db.type || guessDbType(db.name), // 尝试获取类型或猜测
      }))
      // summaryStats.value.databases = res.data.total || res.data.records.length // 更新统计
    } else {
      databaseList.value = createFakeDatabases(7)
      // summaryStats.value.databases = databaseList.value.length
    }
  } catch (error) {
    console.error('Error fetching databases:', error)
    databaseList.value = createFakeDatabases(7)
    // summaryStats.value.databases = databaseList.value.length
  } finally {
    loadingDatabases.value = false
  }
}

const fetchServers = async () => {
  loadingServers.value = true
  serverChartInstances.value.forEach((chart) => chart?.dispose()) // 清理旧实例
  serverChartInstances.value = []
  serverGaugeRefs.value = [] // 重置引用数组
  try {
    const res = await queryServers({ page: 1, pageSize: 6 }) // 获取服务器数据
    if (res && res.data && res.data.records) {
      serverList.value = res.data.records.map((server) => ({
        ...server,
        // 随机生成 CPU 和内存使用率 (因为后端没有)
        cpuUsage: Math.floor(Math.random() * (90 - 10 + 1)) + 10,
        memoryUsage: Math.floor(Math.random() * (95 - 30 + 1)) + 30,
        ip:
          server.ip || `10.0.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}`, // 补充假IP
      }))
      // summaryStats.value.servers = res.data.total || res.data.records.length
    } else {
      serverList.value = createFakeServers(6)
      // summaryStats.value.servers = serverList.value.length
    }
    // 等待 DOM 更新后再初始化 ECharts
    await nextTick()
    initServerGauges()
  } catch (error) {
    console.error('Error fetching servers:', error)
    serverList.value = createFakeServers(6)
    summaryStats.value.servers = serverList.value.length
    await nextTick()
    initServerGauges() // 出错也尝试用假数据初始化
  } finally {
    loadingServers.value = false
  }
}

// 模拟更新数据
const simulateUpdates = () => {
  // 更新服务器负载
  serverList.value = serverList.value.map((server, index) => {
    const newCpu = Math.max(5, Math.min(98, server.cpuUsage + Math.floor(Math.random() * 11) - 5))
    // 更新 ECharts 图表
    serverChartInstances.value[index]?.setOption({
      series: [{ data: [{ value: newCpu }] }],
    })
    return { ...server, cpuUsage: newCpu }
  })

  // 更新整体健康度
  const newHealth = Math.max(
    40,
    Math.min(100, healthStatus.value.value + Math.floor(Math.random() * 7) - 3),
  )
  let healthText = 'Stable'
  let healthClass = 'stable'
  if (newHealth < 60) {
    healthText = 'Critical'
    healthClass = 'critical'
  } else if (newHealth < 85) {
    healthText = 'Warning'
    healthClass = 'warning'
  }
  healthStatus.value = { value: newHealth, text: healthText, class: healthClass }
  overallHealthChart?.setOption({
    series: [{ data: [{ value: newHealth }] }],
  })

  // 更新统计数据
  // summaryStats.value.backupsToday = Math.min(
  //   500,
  //   summaryStats.value.backupsToday + Math.floor(Math.random() * 3),
  // )
  // if (Math.random() < 0.05) {
  //   // 小概率增加失败次数
  //   summaryStats.value.failuresToday++
  // }

  // 添加活动日志
  if (Math.random() < 0.3) {
    addActivityLog(Math.random() > 0.15 ? 'success' : 'error')
  }
}

// --- 假数据 ---
const createFakeDatabases = (count) => {
  /* ... (同上一个版本) ... */
  const fakes = []
  const types = ['MySQL', 'Oracle', 'SQLServer', 'PostgreSQL', 'MongoDB']
  for (let i = 1; i <= count; i++) {
    fakes.push({
      id: `db-fake-${i}`,
      name: `${types[i % types.length]}-DB-${i}`,
      ip: `192.168.${i}.${100 + i}`,
      status: Math.random() > 0.1 ? 'online' : 'offline',
      type: types[i % types.length],
    })
  }
  return fakes
}
const createFakeServers = (count) => {
  /* ... (同上一个版本, 加 CPU 和 IP) ... */
  const fakes = []
  for (let i = 1; i <= count; i++) {
    fakes.push({
      id: `srv-fake-${i}`,
      serverName: `Compute-${String.fromCharCode(65 + i)}-${i}`,
      ip: `10.0.${i}.${50 + i}`,
      cpuUsage: Math.floor(Math.random() * 81) + 10, // 10-90
      memoryUsage: Math.floor(Math.random() * 71) + 20, // 20-90
    })
  }
  return fakes
}
const generateInitialActivityLog = () => {
  activityLog.value = [
    {
      time: '10:35:12',
      message: 'Backup task "Daily Full Backup" completed successfully.',
      type: 'success',
    },
    { time: '10:28:05', message: 'Connection failed to server "DB-Slave-02".', type: 'error' },
    { time: '10:15:00', message: 'System maintenance window started.', type: 'info' },
    { time: '09:55:41', message: 'Backup started for "WebServer-Prod-1".', type: 'info' },
  ]
}
const addActivityLog = (type = 'info') => {
  const now = new Date()
  const timeString = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`
  let message = ''
  if (type === 'success') {
    message = `Backup for "Database-${Math.floor(Math.random() * 5) + 1}" succeeded.`
  } else if (type === 'error') {
    message = `Failed to backup "Server-${Math.floor(Math.random() * 3) + 1}": Disk space low.`
  } else {
    message = `User 'admin' logged in from 192.168.1.10.`
  }
  activityLog.value.unshift({ time: timeString, message: message, type: type })
  if (activityLog.value.length > 20) {
    // 限制日志数量
    activityLog.value.pop()
  }
}

// --- ECharts 初始化 ---
const initServerGauges = () => {
  serverList.value.forEach((server, index) => {
    const chartDom = serverGaugeRefs.value[index]
    if (chartDom) {
      const myChart = echarts.init(chartDom)
      const option = getServerGaugeOption(server.cpuUsage) // 使用 CPU 数据
      myChart.setOption(option)
      serverChartInstances.value[index] = myChart
    }
  })
}

const initOverallHealthGauge = () => {
  if (overallHealthGaugeRef.value) {
    overallHealthChart = echarts.init(overallHealthGaugeRef.value)
    const option = getOverallHealthGaugeOption(healthStatus.value.value)
    overallHealthChart.setOption(option)
  }
}

// --- ECharts 配置 ---
const getServerGaugeOption = (value) => ({
  series: [
    {
      type: 'gauge',
      center: ['50%', '60%'], // 仪表盘位置
      radius: '95%', // 仪表盘大小
      startAngle: 200,
      endAngle: -20,
      min: 0,
      max: 100,
      splitNumber: 5,
      progress: {
        show: true,
        width: 10,
        itemStyle: {
          // 根据数值改变颜色
          color: value < 60 ? '#58D9F9' : value < 85 ? '#FDDD60' : '#FF6E76',
        },
      },
      axisLine: {
        lineStyle: { width: 10, color: [[1, '#2c3e50']] }, // 底色
      },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false },
      pointer: { show: false }, // 不显示指针
      anchor: { show: false },
      title: { show: false }, // 不显示标题
      detail: {
        // 显示数值
        valueAnimation: true,
        fontSize: 14,
        offsetCenter: [0, '0%'],
        color: '#fff',
        formatter: '{value}%',
      },
      data: [{ value: value, name: 'CPU' }],
    },
  ],
})

const getOverallHealthGaugeOption = (value) => ({
  series: [
    {
      type: 'gauge',
      startAngle: 180,
      endAngle: 0,
      min: 0,
      max: 100,
      splitNumber: 4,
      center: ['50%', '75%'], // 调整中心以适应半圆
      radius: '100%',
      axisLine: {
        lineStyle: {
          width: 25,
          color: [
            // 分段颜色
            [0.6, '#ff6e76'], // 0-60% 红色
            [0.85, '#FDDD60'], // 60-85% 黄色
            [1, '#91cc75'], // 85-100% 绿色
          ],
        },
      },
      progress: {
        // 当前进度条
        show: true,
        width: 25,
        itemStyle: {
          color: '#313a46', // 使用深色表示当前值
        },
      },
      pointer: {
        // 指针
        icon: 'path://M12.8,0.7l12,40.1H0.7L12.8,0.7z', // 三角形指针
        length: '60%',
        width: 8,
        offsetCenter: [0, '-55%'], // 指针位置
        itemStyle: { color: '#auto' }, // 颜色跟随区域
      },
      axisTick: {
        // 刻度
        length: 10,
        lineStyle: { color: '#auto', width: 1 },
      },
      splitLine: {
        // 分隔线
        length: 15,
        lineStyle: { color: '#auto', width: 3 },
      },
      axisLabel: {
        // 刻度标签
        color: '#ddd',
        fontSize: 12,
        distance: -45, // 标签与刻度距离
        formatter: function (value) {
          if (value === 0) return 'Crit'
          if (value === 25) return 'Warn'
          if (value === 50) return 'Ok'
          if (value === 75) return 'Good'
          if (value === 100) return 'Exc'
          return ''
        },
      },
      title: { show: false },
      detail: {
        // 显示数值
        fontSize: 24,
        offsetCenter: [0, '-25%'],
        valueAnimation: true,
        formatter: '{value}%',
        color: '#auto', // 跟随区域颜色
      },
      data: [{ value: value }],
    },
  ],
})

// --- 辅助函数 ---
const guessDbType = (name = '') => {
  name = name.toLowerCase()
  if (name.includes('mysql')) return 'MySQL'
  if (name.includes('oracle')) return 'Oracle'
  if (name.includes('sql server') || name.includes('mssql')) return 'SQLServer'
  if (name.includes('postgre')) return 'PostgreSQL'
  if (name.includes('mongo')) return 'MongoDB'
  return 'DB'
}

// --- 生命周期 ---
onMounted(async () => {
  // await Promise.all([fetchDatabases(), fetchServers()]) // 并行获取数据
  // generateInitialActivityLog() // 生成初始日志
  // initOverallHealthGauge() // 初始化整体健康度仪表盘

  // // 启动模拟数据更新
  // intervalId = setInterval(simulateUpdates, 3000) // 每 3 秒更新一次

  // // 监听窗口大小变化，重新绘制图表
  // window.addEventListener('resize', handleResize)

  // 新的获取逻辑
  await Promise.all([
    fetchDatabases(), // 这个函数内部更新数据库列表，但不再更新总数
    fetchServers(), // 这个函数内部更新服务器列表，但不再更新总数
    fetchSummaryStats(), // <-- 新增调用：专门获取顶部统计数据
  ])

  generateInitialActivityLog()
  initOverallHealthGauge()

  // 启动模拟数据更新
  intervalId = setInterval(simulateUpdates, 3000)

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId) // 清除定时器
  // 销毁 ECharts 实例
  serverChartInstances.value.forEach((chart) => chart?.dispose())
  overallHealthChart?.dispose()
  window.removeEventListener('resize', handleResize)
})

// 窗口大小调整处理
const handleResize = () => {
  serverChartInstances.value.forEach((chart) => chart?.resize())
  overallHealthChart?.resize()
}
</script>

<style scoped>
/* -------- Soft Light Blue Dashboard Theme -------- */

/* -------- 基础和背景 -------- */
.dashboard-view {
  position: relative;
  width: 100%;
  height: 100%; /* 确保占满容器 */
  overflow: hidden;
  /* 主背景色 - 非常柔和的淡蓝到更淡的蓝色渐变 */
  background: linear-gradient(180deg, #f0f8ff 0%, #e6f7ff 100%); /* AliceBlue to Lighter Sky Blue */
  color: #303133; /* 主要文字颜色 - Element Plus Default Text */
  font-family:
    'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑',
    Arial, sans-serif;
}

.dashboard-bg {
  /* 如果使用了背景层，确保它是透明的或与主背景协调 */
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none; /* 不阻挡交互 */
}

/* 如果需要背景特效（如星星），在这里添加，并确保它们在浅色背景下可见 */
/* .stars { background: transparent url('/path/to/stars_light.png') ...; opacity: 0.7; } */

/* -------- 标题 -------- */
.dashboard-title {
  font-size: 1.8em; /* 调整大小 */
  text-align: center;
  padding: 18px 0;
  margin-bottom: 15px;
  color: #2c3e50; /* 更深的蓝灰色，增加对比 */
  font-weight: 600;
  z-index: 10;
  position: relative;
  letter-spacing: 1px;
  border-bottom: 1px solid #dcdfe6; /* 添加一个细分隔线 */
  background-color: rgba(255, 255, 255, 0.5); /* 半透明白色背景，提升标题区 */
}

/* -------- 顶部统计 -------- */
.top-stats {
  padding: 0 25px; /* 增加左右内边距 */
  margin-bottom: 25px;
  z-index: 10;
  position: relative;
}
.stat-card {
  background-color: #fff !important; /* 卡片使用纯白背景 */
  border: 1px solid #e4e7ed; /* Element Plus Light Border */
  color: #606266; /* Element Plus Secondary Text */
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px; /* 更圆润的边角 */
  transition: box-shadow 0.3s ease-in-out;
}
.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-card.success {
  border-left: 5px solid var(--el-color-success);
}
.stat-card.error {
  border-left: 5px solid var(--el-color-error);
}

.stat-icon {
  font-size: 2.8em;
  margin-right: 20px;
  color: var(--el-color-primary); /* 使用 Element Plus 主色调 */
  flex-shrink: 0;
}
.stat-card.success .stat-icon {
  color: var(--el-color-success);
}
.stat-card.error .stat-icon {
  color: var(--el-color-error);
}

.stat-content {
  display: flex;
  flex-direction: column;
  min-width: 0; /* 防止内容过长撑开 */
}
.stat-value {
  font-size: 2em;
  font-weight: 600; /* 加粗数字 */
  color: #303133; /* 主要文字色 */
  line-height: 1.2;
  white-space: nowrap;
}
.stat-label {
  font-size: 0.95em;
  color: #909399; /* Element Plus Info Text */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* -------- 主要网格布局 -------- */
.main-grid {
  flex-grow: 1; /* 占据剩余空间 */
  display: grid;
  grid-template-columns: 1fr 1.2fr 1fr; /* 三列布局 */
  grid-template-rows: auto auto; /* 两行 */
  gap: 25px; /* 增加间隙 */
  padding: 0 25px 25px 25px; /* 调整内边距 */
  z-index: 10;
  position: relative;
  grid-template-areas:
    'server core db'
    'log log log';
}

.grid-item {
  background-color: #fff; /* 面板也用白色背景 */
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); /* 添加细微阴影 */
}
/* 分配区域 */
.server-status-panel {
  grid-area: server;
}
.core-status-panel {
  grid-area: core;
}
.database-status-panel {
  grid-area: db;
}
.activity-log-panel {
  grid-area: log;
  max-height: 220px; /* 日志区域高度 */
}

/* 覆盖 Element Plus 卡片头部样式 */
:deep(.el-card__header) {
  background-color: #f8faff; /* 非常浅的蓝色头部背景 */
  border-bottom: 1px solid #e4e7ed;
  color: #303133;
  padding: 12px 20px; /* 调整内边距 */
  font-weight: 500; /* 中等粗细 */
}
.panel-header {
  display: flex;
  align-items: center;
  font-size: 1.1em; /* 稍微增大标题 */
}
.panel-header .el-icon {
  margin-right: 10px;
  font-size: 1.25em;
  color: var(--el-color-primary); /* 头部图标颜色 */
}

.panel-body {
  padding: 20px; /* 统一内边距 */
  flex-grow: 1;
  overflow: hidden; /* 隐藏溢出，内部滚动 */
  position: relative;
}

/* -------- 服务器状态 (左侧) -------- */
.server-list-body {
  overflow-y: auto; /* 允许滚动 */
}
.server-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); /* 调整最小宽度 */
  gap: 20px; /* 调整间隙 */
}
.server-item-gauge {
  text-align: center;
  border: 1px solid #ebeef5; /* Element Plus Border */
  border-radius: 6px;
  padding: 15px 10px;
  background-color: #fafcff; /* 非常浅的背景 */
}
.echarts-gauge {
  height: 110px; /* 调整图表高度 */
  width: 100%;
  margin-bottom: 8px;
}
.server-gauge-name {
  font-size: 0.95em;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 5px;
}
.server-gauge-ip {
  font-size: 0.85em;
  color: #909399;
}

/* -------- 核心状态 (中间) -------- */
.core-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.echarts-gauge-large {
  height: 220px; /* 大仪表盘高度 */
  width: 100%;
  max-width: 320px;
  margin-bottom: 20px;
}
.health-description {
  font-size: 1.15em;
  text-align: center;
  color: #606266;
}
.health-description span {
  /* 状态文字加粗 */
  font-weight: 600;
}
.health-description .stable {
  color: var(--el-color-success);
}
.health-description .warning {
  color: var(--el-color-warning);
}
.health-description .critical {
  color: var(--el-color-error);
}

/* -------- 数据库状态 (右侧) -------- */
.database-list-body {
  overflow-y: auto; /* 允许滚动 */
}
.data-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.data-item {
  display: flex;
  align-items: center;
  padding: 10px 5px; /* 调整列表项内边距 */
  border-bottom: 1px solid #f2f6fc; /* 更浅的分隔线 */
  font-size: 0.95em;
}
.data-item:last-child {
  border-bottom: none;
}

.db-item .status-icon {
  width: 18px; /* 稍微缩小图标背景 */
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 50%;
  margin-right: 10px;
  flex-shrink: 0;
  color: #fff; /* 图标颜色设为白色 */
}
.db-item .status-icon.online {
  background-color: var(--el-color-success);
}
.db-item .status-icon.offline {
  background-color: var(--el-color-error);
}
.db-item .status-icon .el-icon {
  font-size: 0.75em; /* 调整图标大小 */
  vertical-align: middle;
}

.db-name {
  flex-grow: 1;
  margin-right: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #303133;
}
.db-ip {
  color: #909399;
  margin-right: 15px;
  font-size: 0.9em;
  flex-shrink: 0;
}
.db-type {
  color: #a6a9ad;
  font-size: 0.85em;
  flex-shrink: 0;
  background-color: #f4f4f5; /* 浅灰色背景 */
  padding: 2px 6px;
  border-radius: 4px;
}

/* -------- 活动日志 (底部) -------- */
.log-body {
  overflow-y: auto;
  padding: 15px 20px; /* 调整日志内边距 */
}
.log-list {
  list-style: none;
  padding: 0;
  margin: 0;
  font-size: 0.9em; /* 日志字体大小 */
}
.log-item {
  padding: 6px 0;
  border-bottom: 1px dotted #e4e7ed;
  display: flex;
}
.log-item:last-child {
  border-bottom: none;
}
.log-time {
  color: #c0c4cc; /* Element Plus Placeholder Text */
  margin-right: 12px;
  width: 70px;
  flex-shrink: 0;
}
.log-message {
  flex-grow: 1;
  color: #606266; /* 日志消息颜色 */
}
/* 根据类型给消息添加颜色 */
.log-item.success .log-message {
  color: var(--el-color-success-dark-2);
}
.log-item.error .log-message {
  color: var(--el-color-error-dark-2);
}
.log-item.info .log-message {
  color: var(--el-color-info-dark-2);
}

/* -------- 通用样式 & 覆盖 -------- */
/* 自定义滚动条 (浅色主题) */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Element Plus Empty (浅色) */
:deep(.el-empty__description p) {
  color: #909399;
}
:deep(.el-empty__image) {
  /* filter: grayscale(50%) opacity(0.6); /* 可以稍微调整 */
  opacity: 0.7;
}

/* Loading (浅色) */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.8); /* 白色半透明遮罩 */
}
:deep(.el-loading-spinner .path) {
  stroke: var(--el-color-primary); /* 使用主色调 */
}

/* 让卡片阴影更柔和 */
.el-card {
  box-shadow: var(--el-box-shadow-light);
}
</style>
