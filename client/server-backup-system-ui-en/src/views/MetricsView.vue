<template>
  <div class="metrics-view">
    <!-- Page Title -->
    <h2 class="page-title">System Server Metrics Monitoring</h2>
    <!-- Top Control Panel -->
    <el-card class="control-panel">
      <el-form :inline="true" :model="controls">
        <el-form-item label="Select Server">
          <el-select
            v-model="controls.selectedServer"
            placeholder="Please select a server"
            style="width: 300px"
            filterable
            @change="handleServerChange"
          >
            <el-option
              v-for="server in serverList"
              :key="server.id"
              :label="`${server.name} (${server.ipv4})`"
              :value="server.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Charts Area -->
    <div class="charts-wrapper">
      <el-row :gutter="20" v-loading="loading">
        <el-col :span="12">
          <el-card class="chart-card">
            <v-chart class="chart" :option="cpuOption" autoresize />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <v-chart class="chart" :option="memoryOption" autoresize />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <v-chart class="chart" :option="diskOption" autoresize />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <v-chart class="chart" :option="networkOption" autoresize />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, provide, reactive } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent,
  ToolboxComponent,
} from 'echarts/components'
import VChart, { THEME_KEY } from 'vue-echarts'
import { getServerListApi, getMetricsDataApi } from '@/api/metrics'
import { ElMessage } from 'element-plus'

// --- ECharts Initialization ---
use([
  CanvasRenderer,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DataZoomComponent,
  ToolboxComponent,
])
provide(THEME_KEY, 'light') // 'light' or 'dark'

// --- State Management ---
const loading = ref(false)
const serverList = ref([])
const controls = reactive({
  selectedServer: '',
})

// --- ECharts Option Definitions ---
const createBaseOption = (title, yAxisName) => ({
  title: { text: title, left: 'center' },
  tooltip: {
    trigger: 'axis',
    formatter: (params) => {
      const date = new Date(params[0].axisValue).toLocaleString()
      let content = `${date}<br/>`
      params.forEach((item) => {
        content += `${item.marker} ${item.seriesName}: ${item.value[1].toFixed(2)} ${yAxisName}<br/>`
      })
      return content
    },
  },
  grid: { left: '10%', right: '10%', bottom: '15%' },
  xAxis: { type: 'time', boundaryGap: false },
  yAxis: { type: 'value', name: yAxisName, axisLabel: { formatter: `{value} ${yAxisName}` } },
  legend: { data: [], bottom: 10, left: 'center' },
  dataZoom: [
    { type: 'inside', start: 0, end: 100 },
    { type: 'slider', start: 0, end: 100 },
  ],
  series: [],
})

const cpuOption = ref(createBaseOption('CPU Usage', '%'))
const memoryOption = ref(createBaseOption('Memory Usage', '%'))
const diskOption = ref(createBaseOption('Disk I/O', 'MB/s'))
const networkOption = ref(createBaseOption('Network Traffic', 'Mbps'))

// --- API Calls and Logic ---

// Fetch the list of servers
const fetchServerList = async () => {
  try {
    const res = await getServerListApi()
    if (res.meta.statusCode === 200 && res.data) {
      serverList.value = res.data
      // If the list is not empty, select the first one by default and load its data
      if (serverList.value.length > 0) {
        controls.selectedServer = serverList.value[0].id
        fetchMetricsData(controls.selectedServer)
      }
    }
  } catch (error) {
    ElMessage.error('Failed to fetch server list.')
  }
}

// Fetch metrics data for a given server
// const fetchMetricsData = async (serverId) => {
//   if (!serverId) return
//   loading.value = true
//   try {
//     const res = await getMetricsDataApi(serverId)
//     if (res.meta.statusCode === 200 && res.data) {
//       updateCharts(res.data)
//     }
//   } catch (error) {
//     ElMessage.error('Failed to fetch metrics data.')
//   } finally {
//     loading.value = false
//   }
// }
// 请用这段新代码替换上面的函数
const fetchMetricsData = async (serverId) => {
  if (!serverId) return
  loading.value = true // 立即开始加载动画

  // 新增：创建一个至少持续 2.5 秒的延迟 Promise
  const minimumWait = new Promise((resolve) => setTimeout(resolve, 2500))

  try {
    // 核心改动：同时等待 API 调用和我们的最小延迟
    // Promise.all 会等待数组中所有的 Promise 都完成
    // 我们用数组解构 `[res]` 来获取 getMetricsDataApi 的返回结果
    const [res] = await Promise.all([getMetricsDataApi(serverId), minimumWait])

    if (res.meta.statusCode === 200 && res.data) {
      updateCharts(res.data)
    }
  } catch (error) {
    // 即使出错，也要等最小延迟结束，避免错误提示一闪而过
    await minimumWait
    ElMessage.error('Failed to fetch metrics data.')
  } finally {
    // 无论成功或失败，这个 finally 块都会在至少 2.5 秒后执行
    loading.value = false // 结束加载动画
  }
}

// Update chart data
const updateCharts = (data) => {
  cpuOption.value.series = [
    { name: 'CPU', type: 'line', smooth: true, data: data.cpuUsage, areaStyle: {} },
  ]
  cpuOption.value.legend.data = ['CPU']

  memoryOption.value.series = [
    {
      name: 'Memory',
      type: 'line',
      smooth: true,
      data: data.memoryUsage,
      areaStyle: { color: '#91cc75' },
      lineStyle: { color: '#91cc75' },
      itemStyle: { color: '#91cc75' },
    },
  ]
  memoryOption.value.legend.data = ['Memory']

  diskOption.value.series = [
    {
      name: 'Read/Write',
      type: 'line',
      smooth: true,
      data: data.diskIO,
      areaStyle: { color: '#fac858' },
      lineStyle: { color: '#fac858' },
      itemStyle: { color: '#fac858' },
    },
  ]
  diskOption.value.legend.data = ['Read/Write']

  networkOption.value.series = [
    {
      name: 'Inbound',
      type: 'line',
      smooth: true,
      data: data.networkIn,
      areaStyle: { color: '#ee6666' },
      lineStyle: { color: '#ee6666' },
      itemStyle: { color: '#ee6666' },
    },
    {
      name: 'Outbound',
      type: 'line',
      smooth: true,
      data: data.networkOut,
      areaStyle: { color: '#5470c6' },
      lineStyle: { color: '#5470c6' },
      itemStyle: { color: '#5470c6' },
    },
  ]
  networkOption.value.legend.data = ['Inbound', 'Outbound']
}

// Handle server selection change
const handleServerChange = (newServerId) => {
  fetchMetricsData(newServerId)
}

// Execute on component mount
onMounted(() => {
  fetchServerList()
})
</script>

<style scoped>
.page-title {
  font-size: 24px;
  color: #303133;
  margin: 0 0 20px 0;
  font-weight: 500;
  flex-shrink: 0;
}
.metrics-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  box-sizing: border-box;
  padding: 20px;
}

.control-panel {
  flex-shrink: 0;
  margin-bottom: 20px;
}

.charts-wrapper {
  flex-grow: 1;
  overflow-y: auto;
}

.chart-card {
  margin-bottom: 20px;
}

.chart {
  height: 350px;
}
</style>
