<!-- src/views/system/TargetServerManagement.vue -->
<template>
  <div class="target-server-management">
    <!-- Toolbar -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAddTargetServer">Add Target Server</el-button>
    </div>

    <!-- Server Card List -->
    <div v-loading="loading" class="server-card-list">
      <el-row :gutter="20">
        <el-col
          v-for="server in targetServerList"
          :key="server.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          :xl="4"
          style="margin-bottom: 20px"
        >
          <el-card class="server-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>{{ server.name || 'Unnamed Server' }}</span>
                <el-tag
                  :type="
                    server.testStatus === '1'
                      ? 'success'
                      : server.testStatus === '0'
                        ? 'danger'
                        : 'info'
                  "
                  size="small"
                >
                  {{ formatTestStatus(server.testStatus) }}
                </el-tag>
              </div>
            </template>
            <div class="card-body">
              <p>IP: {{ server.ipv4 || 'N/A' }}</p>
            </div>
            <div class="card-footer">
              <el-button type="primary" link size="small" @click="handleTestConnection(server)"
                >Test Connection</el-button
              >
              <el-button type="primary" link size="small" @click="handleEditTargetServer(server)"
                >Edit</el-button
              >
              <el-button type="danger" link size="small" @click="handleDeleteTargetServer(server)"
                >Delete</el-button
              >
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty
        v-if="!loading && targetServerList.length === 0"
        description="No target server data found."
      />
    </div>

    <!-- Add/Edit Modal -->
    <TargetServerModal
      v-model="dialogVisible"
      :server-data="currentTargetServer"
      @submit-success="fetchTargetServers"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  ElButton,
  ElRow,
  ElCol,
  ElCard,
  ElTag,
  ElEmpty,
  ElMessageBox,
  ElMessage,
} from 'element-plus'
import TargetServerModal from '@/components/system/TargetServerModal.vue'
import { getTargetServerList, testTargetConnection, deleteTargetServer } from '@/api/targetServer'

const loading = ref(false)
const dialogVisible = ref(false)
const currentTargetServer = ref(null)
const targetServerList = ref([])

// --- API Calls ---
const fetchTargetServers = async () => {
  loading.value = true
  try {
    const res = await getTargetServerList()
    if (res && res.data && Array.isArray(res.data)) {
      targetServerList.value = res.data
    } else {
      console.error('Failed to fetch target server list or response format is incorrect:', res)
      targetServerList.value = []
    }
  } catch (error) {
    console.error('An error occurred while fetching the target server list:', error)
    targetServerList.value = []
  } finally {
    loading.value = false
  }
}

const handleTestConnection = async (server) => {
  ElMessage.info(`Testing connection to [${server.name || server.ipv4}]...`)
  try {
    const res = await testTargetConnection({ id: server.id })
    if (res?.meta?.success || res?.meta?.statusCode === 200) {
      ElMessage.success(`Connection to [${server.name || server.ipv4}] successful!`)
      await fetchTargetServers()
    } else {
      ElMessage.error(
        `Connection to [${server.name || server.ipv4}] failed: ${res?.meta?.message || 'Unknown error'}`,
      )
      await fetchTargetServers()
    }
  } catch (error) {
    console.error('Test connection exception:', error)
    ElMessage.error(
      `Request error while testing connection to [${server.name || server.ipv4}]: ${error?.response?.data?.message || error?.message || 'Unknown error'}`,
    )
  }
}

const handleDeleteTargetServer = (server) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete the target server "${server.name || server.ipv4}"? This action cannot be undone.`,
    'Warning',
    {
      confirmButtonText: 'Confirm Delete',
      cancelButtonText: 'Cancel',
      type: 'warning',
    },
  )
    .then(async () => {
      try {
        const res = await deleteTargetServer([server.id])
        if (res?.meta?.success || res?.meta?.statusCode === 200) {
          ElMessage.success('Target server deleted successfully!')
          fetchTargetServers()
        } else {
          ElMessage.error(
            `Failed to delete target server: ${res?.meta?.message || 'Unknown error'}`,
          )
        }
      } catch (error) {
        console.error('Delete target server exception:', error)
        ElMessage.error('An error occurred while deleting the target server.')
      }
    })
    .catch(() => {
      ElMessage.info('Delete canceled.')
    })
}

// --- Modal Control ---
const handleAddTargetServer = () => {
  currentTargetServer.value = null
  dialogVisible.value = true
}

const handleEditTargetServer = (server) => {
  currentTargetServer.value = { ...server }
  dialogVisible.value = true
}

// --- Formatting ---
const formatTestStatus = (status) => {
  switch (status) {
    case '1':
      return 'Connected'
    case '0':
      return 'Failed'
    default:
      return 'Unknown'
  }
}

// --- Lifecycle Hooks ---
onMounted(() => {
  fetchTargetServers()
})
</script>

<style scoped>
.target-server-management {
  padding: 15px;
}
.toolbar {
  margin-bottom: 15px;
  display: flex;
  justify-content: flex-end;
}
.server-card-list {
  min-height: 200px;
}
.server-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.server-card .card-body p {
  margin: 5px 0;
  font-size: 14px;
  color: #606266;
}
.server-card .card-footer {
  border-top: 1px solid #ebeef5;
  padding-top: 10px;
  margin-top: 10px;
  text-align: right;
}
.el-button + .el-button {
  margin-left: 8px;
}
</style>
