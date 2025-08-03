<template>
  <div class="backup-history-view">
    <!-- Toolbar (Optional) -->
    <div class="toolbar">
      <el-alert
        v-if="filterStrategyId"
        type="info"
        show-icon
        :closable="false"
        style="margin-bottom: 15px"
      >
        Currently viewing backup history for Policy ID: {{ filterStrategyId }}.
        <el-button link type="primary" @click="clearFilter" style="margin-left: 10px">
          View All History
        </el-button>
      </el-alert>
      <el-input
        v-model="searchParams.title"
        placeholder="Search by backup or policy name..."
        clearable
        style="width: 280px; margin-right: 10px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
      <!-- Other filters like a download button could be added here -->
    </div>

    <!-- Table -->
    <el-table
      :data="tableData"
      v-loading="loading"
      style="width: 100%; margin-top: 15px"
      border
      row-key="id"
    >
      <el-table-column prop="title" label="Backup Name" min-width="180" show-overflow-tooltip />
      <el-table-column prop="totalMethod" label="Backup Method" width="150">
        <template #default="{ row }">
          {{ formatBackupMethod(row.totalMethod) }}
        </template>
      </el-table-column>
      <el-table-column prop="backupTime" label="Start Time" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.backupTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="operationTime" label="End Time" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.operationTime) }}
        </template>
      </el-table-column>
      <el-table-column label="Status & Progress" width="180" align="center">
        <template #default="{ row }">
          <el-progress
            :percentage="String(row.backupStatus) === '3' ? 100 : Number(row.proportion || 0)"
            :status="getProgressBarStatus(row.backupStatus)"
            :stroke-width="10"
          />
        </template>
      </el-table-column>
      <el-table-column prop="size" label="File Size" width="120" />
      <!-- Actions Column -->
      <el-table-column label="Actions" width="200" fixed="right" align="center">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            size="small"
            @click="handleDownload(row)"
            :disabled="row.backupStatus !== '1'"
          >
            Download
          </el-button>
          <el-button link type="primary" size="small" @click="handleViewLog(row)">
            View Log
          </el-button>
          <!-- Show different actions based on status -->
          <el-button
            v-if="row.backupStatus === '2' || row.backupStatus === '3'"
            link
            type="warning"
            size="small"
            @click="handleRestart(row)"
          >
            Restart Backup
          </el-button>
          <el-button
            v-if="row.backupStatus === '0'"
            link
            type="danger"
            size="small"
            @click="handleStop(row)"
          >
            Stop
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Pagination -->
    <el-pagination
      v-if="pagination.total > 0"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      v-model:current-page="pagination.currentPage"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 15px; justify-content: flex-end"
    />

    <!-- View Log Dialog -->
    <el-dialog v-model="logDialogVisible" title="Backup Log" width="70%" top="5vh">
      <pre v-if="currentLog" class="log-content">{{ currentLog }}</pre>
      <el-empty v-else description="No log content available"></el-empty>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ElTable,
  ElTableColumn,
  ElInput,
  ElButton,
  ElPagination,
  ElMessage,
  ElMessageBox,
  ElDialog,
  ElEmpty,
  ElAlert,
  ElProgress,
} from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getHistoryList, downloadBackupFile, getHistoryDetail } from '@/api/backupHistory'
import { stopBackupProgress, restartBackup } from '@/api/backup'
import { formatBackupMethod, formatHistoryStatus, formatDateTime } from '@/utils/formatters'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const searchParams = reactive({ title: '' })
const filterStrategyId = ref(route.query.strategyId || null)
const logDialogVisible = ref(false)
const currentLog = ref('')

const getProgressBarStatus = (statusCode) => {
  const statusStr = String(statusCode ?? '')
  // 0: In Progress(blue/primary), 1: Success(green/success), 2: Failed(red/exception), 3: Stopped(yellow/warning), 4: Waiting(gray/info)
  if (statusStr === '1') return 'success'
  if (statusStr === '2') return 'exception'
  if (statusStr === '3') return 'warning'
  return undefined // Default blue for '0' (In Progress), '4' (Waiting) and others
}

const fetchHistory = async (isPolling = false) => {
  if (!isPolling) {
    loading.value = true
  }
  try {
    const params = {
      page: pagination.currentPage,
      pageSize: pagination.pageSize,
      title: searchParams.title || undefined,
      strategyId: filterStrategyId.value || undefined,
    }
    console.log(`Fetching history (polling: ${isPolling}) with params:`, params)
    const res = await getHistoryList(params)
    console.log('Backend history response:', res)
    if (res && res.data && Array.isArray(res.data.records)) {
      tableData.value = res.data.records.map((item) => ({
        ...item,
        backupStatus: String(item.backupStatus ?? ''),
        proportion: item.proportion ?? 0,
      }))
      pagination.total = res.data.total || 0
    } else {
      console.warn('Backend response format is not as expected:', res)
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('Failed to fetch backup history:', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const clearFilter = () => {
  if (filterStrategyId.value) {
    filterStrategyId.value = null
    router.replace({ query: {} })
    handleSearch()
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  fetchHistory()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchHistory()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchHistory()
}

const handleDownload = async (row) => {
  if (row.backupStatus !== '1') {
    ElMessage.warning('Only successfully completed backups can be downloaded.')
    return
  }
  ElMessage.info('Preparing download, please wait...')
  try {
    await downloadBackupFile({ id: row.id })
  } catch (error) {
    console.error('Download request failed:', error)
  }
}

const handleViewLog = async (row) => {
  currentLog.value = 'Loading log...'
  logDialogVisible.value = true
  try {
    const apiUrl = `/api/backup/checkJournal?id=${row.id}`
    const response = await fetch(apiUrl)
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`)
    }
    const result = await response.json()
    console.log('Log API response:', result)
    if (result.meta && result.meta.success === true) {
      if (result.data && typeof result.data.journal === 'string') {
        currentLog.value = result.data.journal.trim() || 'Log content is empty.'
      } else if (result.data && result.data.journal == null) {
        currentLog.value = 'Log content is empty.'
      } else {
        currentLog.value = 'Could not parse log content (data format might be incorrect).'
      }
    } else {
      currentLog.value = `Failed to load log: ${result.meta?.message || 'Backend did not return a success status.'}`
    }
  } catch (error) {
    console.error('Failed to view log:', error)
    currentLog.value = `An error occurred while loading the log: ${error.message || 'Unknown network or parsing error.'}`
  }
}

const handleRestart = async (row) => {
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to restart this backup task? (Policy: ${row.title || 'Unknown'})`,
      'Restart Backup',
      {
        confirmButtonText: 'Restart',
        cancelButtonText: 'Cancel',
        type: 'warning',
      },
    )
    await restartBackup(row.id)
    ElMessage.success('Restart command has been sent.')
    fetchHistory()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to restart backup:', error)
    } else {
      console.log('User cancelled the restart operation.')
    }
  }
}

const handleStop = async (row) => {
  if (String(row.backupStatus) !== '0') {
    ElMessage.warning('Only a running backup task can be stopped.')
    return
  }
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to stop this running backup task? (Policy: ${row.title || 'Unknown'})`,
      'Stop Backup',
      {
        confirmButtonText: 'Stop',
        cancelButtonText: 'Cancel',
        type: 'danger',
      },
    )
    await stopBackupProgress([row.id])
    ElMessage.success('Stop command has been sent.')
    fetchHistory()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to stop backup:', error)
    } else {
      console.log('User cancelled the stop operation.')
    }
  }
}

watch(
  () => route.query.strategyId,
  (newStrategyId) => {
    const oldStrategyId = filterStrategyId.value
    filterStrategyId.value = newStrategyId || null
    if (oldStrategyId !== filterStrategyId.value) {
      console.log(`Strategy ID filter changed to: ${filterStrategyId.value}`)
      handleSearch()
    }
  },
)

onMounted(() => {
  fetchHistory()
})
</script>

<style scoped>
/* Styles remain the same */
.backup-history-view {
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
  flex-shrink: 0;
}
.el-table {
  flex-grow: 1;
  height: 0;
}
.el-pagination {
  flex-shrink: 0;
}
.log-content {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  max-height: 60vh;
  overflow: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: monospace;
}
</style>
