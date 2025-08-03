<template>
  <div class="backup-strategy-view">
    <!-- Toolbar -->
    <div class="toolbar">
      <el-input
        v-model="searchParams.title"
        placeholder="Search by backup name..."
        clearable
        style="width: 240px; margin-right: 10px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
      <el-button type="primary" :icon="Plus" @click="handleAdd">Add Backup Policy</el-button>
      <el-button
        type="danger"
        :icon="Delete"
        @click="handleBatchDelete"
        :disabled="selectedStrategies.length === 0"
      >
        Batch Delete
      </el-button>
    </div>

    <!-- Table -->
    <el-table
      :data="tableData"
      v-loading="loading"
      style="width: 100%; margin-top: 15px"
      border
      row-key="id"
      @selection-change="handleSelectionChange"
      @filter-change="handleFilterChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="title" label="Backup Name" min-width="180" show-overflow-tooltip />
      <!-- Backup Method -->
      <el-table-column
        prop="totalMethod"
        label="Backup Method"
        width="150"
        column-key="totalMethod"
        :filters="backupMethodFilters"
      >
        <template #default="{ row }">
          {{ formatBackupMethod(row.totalMethod) }}
        </template>
      </el-table-column>
      <!-- Creation Time -->
      <el-table-column prop="backupTime" label="Creation Time" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.backupTime) }}
        </template>
      </el-table-column>
      <!-- Last Backup Status -->
      <!-- <el-table-column
        prop="lastStatus"
        label="Last Backup Status"
        width="150"
        align="center"
        column-key="lastStatus"
      >
        <template #default="{ row }">
          <component :is="formatStrategyLastStatus(row.lastStatus)" />
        </template>
      </el-table-column> -->
      <el-table-column
        prop="lastStatus"
        label="Last Backup Status"
        width="150"
        align="center"
        column-key="lastStatus"
      >
        <template #default="{ row }">
          <!-- 如果是模拟的进度条，则显示进度条 -->
          <el-progress
            v-if="fakeBackupProgress[row.id]?.status === 'running'"
            :percentage="fakeBackupProgress[row.id].progress"
            :stroke-width="8"
            striped
            striped-flow
          />
          <!-- 否则，按我们的新规则显示状态 -->
          <component v-else :is="formatStrategyLastStatus(row.lastStatus, row.taskMode)" />
        </template>
      </el-table-column>

      <!-- **** Execution Mode **** -->
      <el-table-column prop="taskMode" label="Execution Mode" width="120" align="center">
        <template #default="{ row }">
          {{ formatTaskMode(row.taskMode) }}
        </template>
      </el-table-column>

      <!-- **** Enabled Status (Switch) **** -->
      <el-table-column label="Enabled" width="100" align="center">
        <template #default="{ row }">
          <el-switch
            v-model="row.enable"
            :active-value="'1'"
            :inactive-value="'0'"
            size="small"
            inline-prompt
            active-text="On"
            inactive-text="Off"
            :loading="enableLoading[row.id]"
            :disabled="row.taskMode === '1' || enableLoading[row.id] || row.enable === '2'"
            @change="toggleStrategyEnable(row.id)"
          />
          <!-- Show a tag for paused state -->
          <el-tag
            v-if="row.enable === '2'"
            type="warning"
            size="small"
            effect="light"
            style="margin-left: 5px"
          >
            Paused
          </el-tag>
        </template>
      </el-table-column>
      <!-- **** End of modification **** -->

      <el-table-column label="Actions" width="280" fixed="right" align="center">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            size="small"
            @click="handleStartBackup(row)"
            :disabled="row.lastStatus === '0'"
          >
            Start Backup
          </el-button>
          <el-button link type="primary" size="small" @click="handleViewHistory(row)">
            View History
          </el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">Edit</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">Delete</el-button>
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

    <!-- Add/Edit Modal -->
    <StrategyAddEditModal
      v-if="addEditModalVisible"
      v-model="addEditModalVisible"
      :strategy-data="currentEditingStrategy"
      @submit-success="handleModalSubmitSuccess"
    />
  </div>
</template>

<script setup>
// Imports remain the same
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  ElTable,
  ElTableColumn,
  ElInput,
  ElButton,
  ElPagination,
  ElMessage,
  ElMessageBox,
  ElProgress,
  ElSwitch,
  ElTag,
} from 'element-plus'
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import {
  getStrategyList,
  deleteStrategies,
  startBackupNow,
  stopBackupProgress,
  toggleStrategyEnable,
} from '@/api/backup'
import { getHistoryDetail } from '@/api/backupHistory'
import {
  formatBackupMethod,
  formatStrategyLastStatus,
  formatStrategyEnable,
  formatDateTime,
  formatTaskMode,
  backupMethodFilters,
  strategyLastStatusFilters,
  taskModeFilters,
} from '@/utils/formatters'
import StrategyAddEditModal from '@/components/backup/StrategyAddEditModal.vue'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const selectedStrategies = ref([])
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const searchParams = reactive({ title: '' })
const activeFilters = reactive({ totalMethod: null, lastStatus: null, taskMode: null })
const addEditModalVisible = ref(false)
const currentEditingStrategy = ref(null)
const activeBackups = reactive({})
const enableLoading = reactive({})
const pollingIntervals = new Set()

const handleAddOrEditSuccess = (isFake = false, fakeData = null) => {
  if (isFake && fakeData) {
    tableData.value.unshift(fakeData)
    ElMessage({
      message: `Simulated policy "${fakeData.title}" has been added to the list.`,
      type: 'info',
      duration: 2000,
    })
  } else if (!isFake) {
    fetchStrategies()
  } else {
    console.warn('Received fake success signal but no fake data provided.')
    fetchStrategies()
  }
}

const fetchStrategies = async () => {
  loading.value = true
  enableLoading.value = {}
  try {
    const params = {
      page: pagination.currentPage,
      pageSize: pagination.pageSize,
      title: searchParams.title || undefined,
      totalMethod: activeFilters.totalMethod ? activeFilters.totalMethod.join(',') : undefined,
      lastStatus: activeFilters.lastStatus ? activeFilters.lastStatus.join(',') : undefined,
      taskMode: activeFilters.taskMode ? activeFilters.taskMode.join(',') : undefined,
    }

    const res = await getStrategyList(params)
    console.log('Original response from backend:', res)

    if (res && res.data) {
      tableData.value = (res.data.records || []).map((item) => ({
        ...item,
        enable: String(item.enable ?? ''),
        taskMode: String(item.taskMode ?? ''),
        lastStatus: String(item.lastStatus ?? ''),
      }))
      pagination.total = res.data.total || 0
    } else {
      console.warn(
        'Backend response format is not as expected (might be missing the data field):',
        res,
      )
      tableData.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('Failed to fetch strategy list:', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleFilterChange = (filters) => {
  Object.keys(filters).forEach((key) => {
    activeFilters[key] = filters[key].length > 0 ? filters[key] : null
  })
  pagination.currentPage = 1
  fetchStrategies()
}

const handleSearch = () => {
  pagination.currentPage = 1
  fetchStrategies()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchStrategies()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchStrategies()
}

const handleSelectionChange = (selection) => {
  selectedStrategies.value = selection
}

const handleAdd = () => {
  currentEditingStrategy.value = null
  addEditModalVisible.value = true
}

const handleEdit = (row) => {
  currentEditingStrategy.value = { ...row }
  addEditModalVisible.value = true
}

const handleDelete = (row) => {
  handleBatchDelete([row])
}

const handleBatchDelete = async (strategiesToDelete = selectedStrategies.value) => {
  if (!strategiesToDelete || strategiesToDelete.length === 0) {
    ElMessage.warning('Please select at least one policy.')
    return
  }
  const idsToDelete = strategiesToDelete.map((s) => s.id)
  const titles = strategiesToDelete.map((s) => `"${s.title}"`).join(', ')

  try {
    await ElMessageBox.confirm(
      `Are you sure you want to delete the backup policy ${titles}? Associated backup history will not be removed.`,
      'Confirm Deletion',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning',
      },
    )
    loading.value = true
    await deleteStrategies(idsToDelete)
    ElMessage.success('Deletion successful.')
    selectedStrategies.value = []
    fetchStrategies()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete policies:', error)
    }
  } finally {
    loading.value = false
  }
}

const fakeBackupProgress = reactive({})

const handleStartBackup = async (row) => {
  const isFakeStart = row.title.includes('localhost') || row.title.includes('127.0.0.1')
  if (isFakeStart) {
    if (fakeBackupProgress[row.id]?.status === 'running') {
      ElMessage.warning(`A simulated backup for policy "${row.title}" is already in progress.`)
      return
    }
    ElMessage.info(`Starting simulated backup for policy "${row.title}" (127.0.0.1)...`)
    const currentStrategy = tableData.value.find((item) => item.id === row.id)
    if (currentStrategy) {
      fakeBackupProgress[row.id] = { progress: 0, intervalId: null, status: 'running' }
    } else {
      console.error('Cannot find the policy to simulate in tableData:', row.id)
      return
    }
    const intervalId = setInterval(() => {
      if (fakeBackupProgress[row.id]) {
        fakeBackupProgress[row.id].progress += Math.floor(Math.random() * 15) + 10
        if (fakeBackupProgress[row.id].progress >= 100) {
          fakeBackupProgress[row.id].progress = 100
          clearInterval(fakeBackupProgress[row.id].intervalId)
          fakeBackupProgress[row.id].intervalId = null
          fakeBackupProgress[row.id].status = 'done'
          setTimeout(() => {
            delete fakeBackupProgress[row.id]
          }, 1000)
          ElMessage.success(
            `Simulated backup for policy "${row.title}" completed successfully (127.0.0.1).`,
          )
        }
      } else {
        clearInterval(intervalId)
      }
    }, 300)
    if (fakeBackupProgress[row.id]) {
      fakeBackupProgress[row.id].intervalId = intervalId
    }
    return
  }

  if (row.lastStatus === '0') {
    ElMessage.warning(
      `Policy "${row.title}" may already be backing up. Please refresh later to check the status.`,
    )
    return
  }

  try {
    await ElMessageBox.confirm(
      `Are you sure you want to run the backup policy "${row.title}" now?`,
      'Run Backup',
      {
        confirmButtonText: 'Run Now',
        cancelButtonText: 'Cancel',
        type: 'info',
      },
    )
    await startBackupNow(row.id)
    ElMessage.success(`Execution command sent to policy "${row.title}".`)
    fetchStrategies()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to run backup operation:', error)
      fetchStrategies()
    }
  }
}

const handleViewHistory = (row) => {
  router.push({
    name: 'backup-history',
    query: { strategyId: row.id },
  })
}

const toggleEnable = async (row) => {
  const targetState = row.enable
  const originalState = targetState === '1' ? '0' : '1'
  const actionText = targetState === '1' ? 'enable' : 'disable'
  enableLoading[row.id] = true
  try {
    await toggleEnableApi(row.id) // Assuming a generic API call
    ElMessage.success(`Policy "${row.title}" has been ${actionText}d.`)
    fetchStrategies()
  } catch (error) {
    console.error(`Failed to ${actionText} policy:`, error)
    row.enable = originalState
  } finally {
    setTimeout(() => {
      if (enableLoading[row.id]) {
        delete enableLoading[row.id]
      }
    }, 300)
  }
}

const handleModalSubmitSuccess = () => {
  addEditModalVisible.value = false
  fetchStrategies()
}

onMounted(() => {
  fetchStrategies()
})
</script>

<style scoped>
.backup-strategy-view {
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
.el-table-column .el-button + .el-button {
  margin-left: 8px;
}
</style>
