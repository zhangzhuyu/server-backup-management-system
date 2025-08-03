<template>
  <div class="data-module-view">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <!-- Database Area -->
      <el-tab-pane label="Database Management" name="databases">
        <div class="resource-section">
          <!-- Action Bar -->
          <div class="actions-bar">
            <el-input
              v-model="databaseSearchTerm"
              placeholder="Search databases by name"
              clearable
              @clear="fetchDatabases"
              @keyup.enter="handleDatabaseSearch"
              style="width: 240px; margin-right: 10px"
            >
              <template #append>
                <el-button :icon="Search" @click="handleDatabaseSearch" />
              </template>
            </el-input>
            <el-button type="primary" :icon="Plus" @click="openAddDatabaseModal">
              Add Database
            </el-button>
          </div>

          <!-- Card List -->
          <el-row :gutter="16" v-loading="databaseLoading">
            <el-col
              :xs="24"
              :sm="12"
              :md="8"
              :lg="6"
              :xl="4"
              v-for="db in databaseList"
              :key="db.id"
              style="margin-bottom: 16px"
            >
              <DatabaseCard
                :database="db"
                @edit="openEditDatabaseModal"
                @delete="handleDeleteDatabase"
                @test-connection="handleTestConnection"
                :testing-id="currentlyTestingId"
              />
            </el-col>
            <el-empty
              v-if="!databaseLoading && databaseList.length === 0"
              description="No database data found."
            />
          </el-row>

          <!-- Pagination -->
          <el-pagination
            v-if="databaseTotal > 0"
            background
            layout="prev, pager, next, total, jumper"
            :total="databaseTotal"
            :page-size="databasePagination.pageSize"
            :current-page="databasePagination.pageNum"
            @current-change="handleDatabasePageChange"
            style="margin-top: 20px; display: flex; justify-content: flex-end"
          />
        </div>
      </el-tab-pane>

      <!-- Server Area -->
      <el-tab-pane label="Server Management" name="servers">
        <div class="resource-section">
          <!-- Action Bar -->
          <div class="actions-bar">
            <el-input
              v-model="serverSearchTerm"
              placeholder="Search servers by name"
              clearable
              @clear="fetchServers"
              @keyup.enter="handleServerSearch"
              style="width: 240px; margin-right: 10px"
            >
              <template #append>
                <el-button :icon="Search" @click="handleServerSearch" />
              </template>
            </el-input>
            <el-button type="primary" :icon="Plus" @click="openAddServerModal">
              Add Server
            </el-button>
          </div>

          <!-- Card List -->
          <el-row :gutter="16" v-loading="serverLoading">
            <el-col
              :xs="24"
              :sm="12"
              :md="8"
              :lg="6"
              :xl="4"
              v-for="server in serverList"
              :key="server.id"
              style="margin-bottom: 16px"
            >
              <ServerCard
                :server="server"
                :is-testing="testingServerConnection[server.id]"
                @edit="openEditServerModal"
                @delete="handleDeleteServer"
                @test-connection="handleTestServerConnection"
              />
            </el-col>
            <el-empty
              v-if="!serverLoading && serverList.length === 0"
              description="No server data found."
            />
          </el-row>

          <!-- Pagination -->
          <el-pagination
            v-if="serverTotal > 0"
            background
            layout="prev, pager, next, total, jumper"
            :total="serverTotal"
            :page-size="serverPagination.pageSize"
            :current-page="serverPagination.page"
            @current-change="handleServerPageChange"
            style="margin-top: 20px; display: flex; justify-content: flex-end"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- Add/Edit Database Modal -->
    <DatabaseModal
      v-model="databaseModalVisible"
      :database-data="editingDatabase"
      @submit-success="handleDatabaseSubmitSuccess"
    />

    <!-- Add/Edit Server Modal -->
    <ServerModal
      v-model="serverModalVisible"
      :server-data="editingServer"
      @submit-success="handleServerSubmitSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  ElTabs,
  ElTabPane,
  ElInput,
  ElButton,
  ElRow,
  ElCol,
  ElPagination,
  ElEmpty,
  ElMessage,
  ElMessageBox,
} from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getDatabaseList, deleteDatabase, testDatabaseConnection } from '@/api/database'
import { queryServers, deleteServer, testServerConnection } from '@/api/server'
import DatabaseCard from '@/components/DataModule/DatabaseCard.vue'
import ServerCard from '@/components/DataModule/ServerCard.vue'
import DatabaseModal from '@/components/DataModule/DatabaseModal.vue'
import ServerModal from '@/components/DataModule/ServerModal.vue'

const activeTab = ref('databases')

// --- Database State ---
const databaseList = ref([])
const databaseLoading = ref(false)
const databaseSearchTerm = ref('')
const databasePagination = reactive({ pageNum: 1, pageSize: 12 })
const databaseTotal = ref(0)
const databaseModalVisible = ref(false)
const editingDatabase = ref(null)
const currentlyTestingId = ref(null)

// --- Server State ---
const serverList = ref([])
const serverLoading = ref(false)
const serverSearchTerm = ref('')
const serverPagination = reactive({ page: 1, pageSize: 12 })
const serverTotal = ref(0)
const serverModalVisible = ref(false)
const editingServer = ref(null)
const testingServerConnection = reactive({})

// --- Methods ---

const fetchDatabases = async () => {
  databaseLoading.value = true
  try {
    const params = { ...databasePagination }
    const data = { content: databaseSearchTerm.value }
    const res = await getDatabaseList(params, data)
    if (res.data && res.data.records) {
      databaseList.value = res.data.records
      databaseTotal.value = res.data.total || 0
    } else {
      databaseList.value = []
      databaseTotal.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch database list:', error)
    databaseList.value = []
    databaseTotal.value = 0
  } finally {
    databaseLoading.value = false
  }
}

const fetchServers = async () => {
  serverLoading.value = true
  try {
    const params = {
      ...serverPagination,
      content: serverSearchTerm.value,
    }
    const res = await queryServers(params)
    if (res.data && res.data.records) {
      serverList.value = res.data.records
      serverTotal.value = res.data.total || 0
      serverList.value.forEach((server) => {
        if (testingServerConnection[server.id] === undefined) {
          testingServerConnection[server.id] = false
        }
      })
    } else {
      serverList.value = []
      serverTotal.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch server list:', error)
    serverList.value = []
    serverTotal.value = 0
  } finally {
    serverLoading.value = false
  }
}

const handleTabClick = (tab) => {
  if (tab.paneName === 'databases' && databaseList.value.length === 0) {
    fetchDatabases()
  } else if (tab.paneName === 'servers' && serverList.value.length === 0) {
    fetchServers()
  }
}

// --- Database Operations ---
const handleDatabaseSearch = () => {
  databasePagination.pageNum = 1
  fetchDatabases()
}

const handleDatabasePageChange = (newPage) => {
  databasePagination.pageNum = newPage
  fetchDatabases()
}

const openAddDatabaseModal = () => {
  editingDatabase.value = null
  databaseModalVisible.value = true
}

const openEditDatabaseModal = (db) => {
  editingDatabase.value = { ...db }
  databaseModalVisible.value = true
}

const handleDatabaseSubmitSuccess = () => {
  databaseModalVisible.value = false
  fetchDatabases()
}

const handleTestConnection = async (databaseId) => {
  currentlyTestingId.value = databaseId
  try {
    const res = await testDatabaseConnection({ id: databaseId })
    const dbIndex = databaseList.value.findIndex((db) => db.id === databaseId)

    if (res && res.data === true) {
      ElMessage.success(`Connection test for database (ID: ${databaseId}) was successful.`)
      if (dbIndex !== -1) {
        databaseList.value[dbIndex].testStatus = '1'
      }
    } else {
      const failureMsg =
        res?.meta?.message && res.meta.message !== 'ok'
          ? res.meta.message
          : 'Connection failed, please check configuration.'
      ElMessage.error(`Connection test for database (ID: ${databaseId}) failed: ${failureMsg}`)
      if (dbIndex !== -1) {
        databaseList.value[dbIndex].testStatus = '0'
      }
    }
  } catch (error) {
    console.error(`Failed to test database connection (ID: ${databaseId}):`, error)
    ElMessage.error(`Request failed for connection test (ID: ${databaseId}).`)
    const dbIndex = databaseList.value.findIndex((db) => db.id === databaseId)
    if (dbIndex !== -1) {
      databaseList.value[dbIndex].testStatus = '0'
    }
  } finally {
    currentlyTestingId.value = null
  }
}

const handleDeleteDatabase = (id) => {
  ElMessageBox.confirm(
    'Are you sure you want to delete this database record? This action cannot be undone.',
    'Warning',
    {
      confirmButtonText: 'Confirm Delete',
      cancelButtonText: 'Cancel',
      type: 'warning',
    },
  )
    .then(async () => {
      try {
        await deleteDatabase([id])
        ElMessage.success('Database deleted successfully!')
        const remaining = databaseList.value.length - 1
        if (remaining === 0 && databasePagination.pageNum > 1) {
          databasePagination.pageNum--
        }
        fetchDatabases()
      } catch (error) {
        console.error('Failed to delete database:', error)
      }
    })
    .catch(() => {
      ElMessage.info('Delete canceled.')
    })
}

// --- Server Operations ---
const handleServerSearch = () => {
  serverPagination.page = 1
  fetchServers()
}

const handleServerPageChange = (newPage) => {
  serverPagination.page = newPage
  fetchServers()
}

const openAddServerModal = () => {
  editingServer.value = null
  serverModalVisible.value = true
}

const openEditServerModal = (server) => {
  editingServer.value = { ...server }
  serverModalVisible.value = true
}

const handleServerSubmitSuccess = () => {
  serverModalVisible.value = false
  fetchServers()
}

const handleTestServerConnection = async (serverId) => {
  testingServerConnection[serverId] = true
  const server = serverList.value.find((s) => s.id === serverId)
  const serverIdentifier = server ? server.name || server.ipv4 : `ID: ${serverId}`
  try {
    const serverDto = { id: serverId }
    const res = await testServerConnection(serverDto)

    if (res?.meta?.success || res?.meta?.statusCode === 200) {
      ElMessage.success(`Connection to server [${serverIdentifier}] successful!`)
    } else {
      ElMessage.error(
        `Connection to server [${serverIdentifier}] failed: ${res?.meta?.message || 'Please check configuration.'}`,
      )
    }
  } catch (error) {
    console.error(`Failed to test server connection [${serverIdentifier}]:`, error)
    const errorMsg =
      error?.response?.data?.meta?.message ||
      error?.response?.data?.message ||
      error?.message ||
      'Request exception'
    ElMessage.error(`Error during connection test for [${serverIdentifier}]: ${errorMsg}`)
  } finally {
    testingServerConnection[serverId] = false
  }
}

const handleDeleteServer = (id) => {
  ElMessageBox.confirm(
    'Are you sure you want to delete this server record? This action cannot be undone.',
    'Warning',
    {
      confirmButtonText: 'Confirm Delete',
      cancelButtonText: 'Cancel',
      type: 'warning',
    },
  )
    .then(async () => {
      try {
        await deleteServer([id])
        ElMessage.success('Server deleted successfully!')
        const remaining = serverList.value.length - 1
        if (remaining === 0 && serverPagination.page > 1) {
          serverPagination.page--
        }
        fetchServers()
      } catch (error) {
        console.error('Failed to delete server:', error)
      }
    })
    .catch(() => {
      ElMessage.info('Delete canceled.')
    })
}

// --- Lifecycle ---
onMounted(() => {
  if (activeTab.value === 'databases') {
    fetchDatabases()
  } else if (activeTab.value === 'servers') {
    fetchServers()
  }
})
</script>

<style scoped>
.data-module-view {
  padding: 15px;
}
.actions-bar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}
</style>
