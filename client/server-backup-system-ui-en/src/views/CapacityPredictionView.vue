<template>
  <div class="capacity-prediction-view">
    <!-- Top Control Panel -->
    <el-card class="control-panel">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="Select Server for Prediction">
          <el-select
            v-model="selectedServerId"
            placeholder="Please select a server"
            filterable
            clearable
            style="width: 300px"
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

    <!-- Results Area -->
    <div v-loading="loading" class="result-content">
      <div v-if="!selectedServerId" class="placeholder">
        <el-empty description="Please select a server above to start the analysis" />
      </div>
      <div v-else-if="!predictionData.predictionAvailable" class="placeholder">
        <el-empty :description="predictionData.message || 'No data available for this server'" />
      </div>
      <div v-else class="data-grid">
        <!-- Left: Summary and Gauge -->
        <el-card class="summary-card">
          <template #header>
            <div class="card-header">
              <span>Capacity Overview & Prediction</span>
              <el-tag type="success">Prediction Successful</el-tag>
            </div>
          </template>
          <div class="summary-content">
            <div class="gauge-chart" ref="gaugeChartRef"></div>
            <div class="prediction-text">
              <p>
                Server:
                <strong
                  >{{ predictionData.serverInfo.serverName }} ({{
                    predictionData.serverInfo.serverIp
                  }})</strong
                >
              </p>
              <p>
                Current daily growth rate is approx.
                <span class="highlight-green">
                  {{ predictionData.predictionResult.dailyGrowthRateMB }} MB/day
                </span>
              </p>
              <div class="prediction-final-result">
                The
                <span class="highlight-blue"
                  >{{ predictionData.serverInfo.totalCapacityGB }} GB</span
                >
                capacity limit is predicted to be reached in
                <br />
                <span class="highlight-red">{{
                  predictionData.predictionResult.daysRemaining
                }}</span>
                days (around
                <strong>{{ predictionData.predictionResult.predictedFullDate }}</strong
                >).
              </div>
            </div>
          </div>
        </el-card>

        <!-- Right: Historical Data Trend Chart -->
        <el-card class="history-card">
          <template #header>
            <div class="card-header">
              <span>Backup Growth Trend (Last 2 Years)</span>
            </div>
          </template>
          <div class="history-chart" ref="historyChartRef"></div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getServerListForCapacityApi, getPredictionDataForServerApi } from '@/api/capacity.js'

// --- Reactive State ---
const loading = ref(false)
const serverList = ref([])
const selectedServerId = ref(null)
const predictionData = ref({})

// ECharts Instances
const gaugeChartRef = ref(null)
const historyChartRef = ref(null)
let gaugeChartInstance = null
let historyChartInstance = null

// --- Data Fetching ---
const fetchServerList = async () => {
  try {
    const res = await getServerListForCapacityApi()
    if (res.meta.statusCode === 200 && res.data) {
      serverList.value = res.data
      if (serverList.value.length > 0) {
        selectedServerId.value = serverList.value[0].id
        fetchPredictionData()
      }
    } else {
      ElMessage.error('Failed to fetch server list.')
    }
  } catch (error) {
    ElMessage.error('Error fetching server list: ' + error.message)
  }
}

const fetchPredictionData = async () => {
  if (!selectedServerId.value) return
  loading.value = true
  try {
    const res = await getPredictionDataForServerApi(selectedServerId.value)
    if (res.meta.statusCode === 200) {
      predictionData.value = res.data
      await nextTick() // Wait for DOM update
      if (predictionData.value.predictionAvailable) {
        initOrUpdateCharts()
      }
    } else {
      ElMessage.error(res.meta.message || 'Failed to fetch prediction data.')
      predictionData.value = { predictionAvailable: false, message: res.meta.message }
    }
  } catch (error) {
    ElMessage.error('Error fetching prediction data: ' + error.message)
    predictionData.value = { predictionAvailable: false, message: 'Network or server error.' }
  } finally {
    loading.value = false
  }
}

// --- Event Handlers ---
const handleServerChange = () => {
  if (selectedServerId.value) {
    fetchPredictionData()
  } else {
    // Clear data
    predictionData.value = {}
  }
}

// --- ECharts ---
const initOrUpdateCharts = () => {
  // Gauge Chart
  const gaugeOption = {
    series: [
      {
        type: 'gauge',
        startAngle: 180,
        endAngle: 0,
        center: ['50%', '75%'],
        radius: '90%',
        min: 0,
        max: 100,
        splitNumber: 8,
        axisLine: {
          lineStyle: {
            width: 6,
            color: [
              [0.6, '#67C23A'],
              [0.8, '#E6A23C'],
              [1, '#F56C6C'],
            ],
          },
        },
        pointer: {
          icon: 'path://M12.8,0.7l12,40.1H0.7L12.8,0.7z',
          length: '12%',
          width: 20,
          offsetCenter: [0, '-60%'],
          itemStyle: {
            color: 'auto',
          },
        },
        axisTick: { show: false },
        splitLine: { show: false },
        axisLabel: { show: false },
        title: {
          offsetCenter: [0, '-20%'],
          fontSize: 20,
        },
        detail: {
          fontSize: 25,
          offsetCenter: [0, '0%'],
          valueAnimation: true,
          formatter: function (value) {
            return Math.round(value) + ' %'
          },
          color: 'auto',
        },
        data: [
          {
            value: predictionData.value.serverInfo.usagePercentage,
            name: 'Current Capacity Usage',
          },
        ],
      },
    ],
  }
  if (!gaugeChartInstance) {
    gaugeChartInstance = echarts.init(gaugeChartRef.value)
  }
  gaugeChartInstance.setOption(gaugeOption)

  // History Trend Chart
  const historyOption = {
    tooltip: {
      trigger: 'axis',
      formatter: 'Date: {b}<br/>Backup Size: {c} MB',
    },
    xAxis: {
      type: 'category',
      data: predictionData.value.historicalData.map((item) => item.date),
    },
    yAxis: {
      type: 'value',
      name: 'Backup Size (MB)',
    },
    grid: { top: '15%', left: '12%', right: '5%', bottom: '10%' },
    series: [
      {
        data: predictionData.value.historicalData.map((item) => item.size),
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(58, 132, 255, 0.5)' },
            { offset: 1, color: 'rgba(58, 132, 255, 0)' },
          ]),
        },
        lineStyle: { color: '#3A84FF' },
        itemStyle: { color: '#3A84FF' },
      },
    ],
  }
  if (!historyChartInstance) {
    historyChartInstance = echarts.init(historyChartRef.value)
  }
  historyChartInstance.setOption(historyOption)
}

// --- Lifecycle Hooks ---
onMounted(() => {
  fetchServerList()
})
</script>

<style scoped>
/* Styles remain the same */
.capacity-prediction-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 15px;
  background-color: #f0f2f5;
}

.control-panel {
  flex-shrink: 0;
  margin-bottom: 15px;
}

.result-content {
  flex-grow: 1;
  min-height: 0;
  position: relative;
}

.placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.data-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 15px;
  height: 100%;
}

.summary-card,
.history-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  flex-grow: 1;
  min-height: 0;
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.gauge-chart,
.history-chart {
  width: 100%;
  height: 100%;
  min-height: 250px;
}

.prediction-text {
  text-align: center;
  font-size: 16px;
  padding: 10px;
  line-height: 1.8;
}

.prediction-final-result {
  margin-top: 15px;
  font-size: 18px;
  font-weight: bold;
  background-color: #ecf5ff;
  border-left: 5px solid #409eff;
  padding: 15px;
}

.highlight-green {
  color: #67c23a;
  font-weight: bold;
}
.highlight-red {
  color: #f56c6c;
  font-weight: bold;
}
.highlight-blue {
  color: #409eff;
  font-weight: bold;
}
</style>
