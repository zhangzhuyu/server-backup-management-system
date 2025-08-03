<template>
  <el-dialog
    :model-value="modelValue"
    @update:modelValue="$emit('update:modelValue', $event)"
    :title="modalTitle"
    width="70%"
    :close-on-click-modal="false"
    @closed="handleDialogClosed"
    top="5vh"
    append-to-body
    destroy-on-close
  >
    <el-form
      ref="strategyFormRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="
        formLoading ||
        loading.targetServerOptions || // 修改: targetServerOptions
        loading.databaseOptions || // 修改: databaseOptions
        loading.tables || // **** 添加回 tables loading ****
        loading.sourceServers
      "
      scroll-to-error
    >
      <el-form-item label="Policy Name" prop="title">
        <el-input v-model="formData.title" placeholder="Enter policy name" />
      </el-form-item>

      <!-- 1. 备份方式 -->
      <el-form-item label="Backup Type" prop="backupWay">
        <el-select
          v-model="formData.backupWay"
          placeholder="Select Backup Type"
          @change="handleBackupWayChange"
          clearable
        >
          <el-option label="Database Backup" value="1"></el-option>
          <el-option label="Server Backup" value="2"></el-option>
          <!-- CDC 和 核心表 已移除 -->
        </el-select>
      </el-form-item>

      <!-- ==================== 数据库备份配置 (backupWay === '1') ==================== -->
      <template v-if="formData.backupWay === '1'">
        <!-- 1.1 数据源类型 -->
        <el-form-item label="Data Source Type" prop="dataSourceType">
          <el-select
            v-model="formData.dataSourceType"
            placeholder="Select Data Source Type"
            @change="handleDataSourceTypeChange"
            clearable
          >
            <el-option label="MySQL" value="2"></el-option>
            <el-option label="Oracle" value="1"></el-option>
            <el-option label="Mongo" value="6"></el-option>
          </el-select>
        </el-form-item>

        <!-- 1.2 具体方法 (根据数据源类型联动) -->
        <el-form-item label="specific method" prop="totalMethod">
          <!-- Oracle: 可选 exp/expdb -->
          <el-select
            v-if="formData.dataSourceType === '1'"
            v-model="formData.totalMethod"
            placeholder="Please select the specific method"
            clearable
          >
            <el-option label="exp" value="2"></el-option>
            <el-option label="expdb" value="3"></el-option>
          </el-select>
          <!-- MySQL: 固定 mysqldump -->
          <el-input v-if="formData.dataSourceType === '2'" value="mysqldump" disabled />
          <el-input v-if="formData.dataSourceType === '6'" value="mongodump" disabled />
          <!-- MongoDB 移除 -->
        </el-form-item>

        <!-- **** 1.3 备份数据库 (调用新 API) **** -->
        <el-form-item label="backup database" prop="databases">
          <el-select
            v-model="formData.databases"
            placeholder="Select a database to back up"
            filterable
            clearable
            :loading="loading.databaseOptions"
            @visible-change="(visible) => visible && fetchDatabaseListForBackupIfNeeded()"
            @change="handleDatabaseChange"
            style="width: 100%"
          >
            <!-- **** 修改：循环 databaseListOptions **** -->
            <el-option
              v-for="item in databaseListOptions"
              :key="item.id"
              :label="`${item.dataSourceName} (${item.ip})`"
              :value="item.id"
            />
          </el-select>
          <!-- 将测试连接按钮放在这里 -->
          <el-button
            type="primary"
            link
            @click="handleTestConnection"
            :loading="testingConnection"
            :disabled="!formData.databases || testingConnection"
            style="margin-left: 10px"
          >
            test connection
          </el-button>
        </el-form-item>

        <!-- 1.4 选择表格 (仅 Oracle/MySQL?) -->
        <el-form-item
          label="Select Tables"
          prop="tables"
          v-if="formData.dataSourceType === '1' || formData.dataSourceType === '2'"
        >
          <el-select
            v-model="formData.tables"
            placeholder="Please select the backup database first"
            :disabled="!formData.databases"
            multiple
            filterable
            clearable
            collapse-tags
            collapse-tags-tooltip
            :max-collapse-tags="3"
            :loading="loading.tables"
            @visible-change="fetchTablesIfNeeded"
            style="width: 100%"
          >
            <el-option
              v-for="tableName in options.tables"
              :key="tableName"
              :label="tableName"
              :value="tableName"
            />
          </el-select>
        </el-form-item>
        <!-- **** 1.5 目标服务器 (移动到这里) **** -->
        <!-- **** 1.4 目标服务器 (调用新 API) **** -->
        <el-form-item label="Target Server" prop="targetServerId">
          <el-select
            v-model="formData.targetServerId"
            placeholder="Select a target server to execute the backup"
            filterable
            clearable
            :loading="loading.targetServerOptions"
            @visible-change="fetchTargetServersForBackupIfNeeded"
            @change="handleTargetServerChange"
            style="width: 100%"
          >
            <!-- 注意：API 返回的 data 直接是列表，value 和 label 需要从 item 中提取 -->
            <el-option
              v-for="item in options.targetServerOptions"
              :key="item.id"
              :label="`${item.name || 'Unknown name'} (${item.ipv4 || 'Unknown IP'})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </template>

      <!-- ==================== 服务器目录备份配置 (backupWay === '2') ==================== -->
      <template v-if="formData.backupWay === '2'">
        <!-- 2.1 具体方法 -->
        <el-form-item label="specific method" prop="totalMethod">
          <el-select
            v-model="formData.totalMethod"
            placeholder="Please select the server backup method"
            clearable
            @change="handleServerMethodChange"
          >
            <!-- http 已移除 -->
            <el-option label="ssh" value="6"></el-option>
            <el-option label="ftp" value="7"></el-option>
          </el-select>
        </el-form-item>

        <!-- 2.2 选择源服务器 (调用新参数的 API) -->
        <el-form-item label="Select the source server" prop="sourceServerId">
          <el-select
            v-model="formData.sourceServerId"
            placeholder="Please select the server to be backed up"
            filterable
            clearable
            :loading="loading.sourceServers"
            @visible-change="fetchSourceServers"
            @change="handleSourceServerChange"
            style="width: 100%"
          >
            <el-option
              v-for="item in options.sourceServers"
              :key="item.id"
              :label="`${item.name} (${item.ipv4})`"
              :value="item.id"
            />
          </el-select>
          <!-- 显示选中的服务器信息 (使用新字段名) -->
          <div
            v-if="selectedSourceServerInfo"
            style="font-size: 12px; color: #909399; margin-top: 5px"
          >
            IP: {{ selectedSourceServerInfo.ipv4 }}, 用户名:
            {{ selectedSourceServerInfo.user }}
          </div>
        </el-form-item>

        <!-- 2.3 备份路径 -->
        <el-form-item label="backup path" prop="backupTargetInput">
          <el-input v-model="formData.backupTargetInput" placeholder="/path/to/backup/directory" />
        </el-form-item>

        <!-- 2.4 目标服务器 (调用新 API) -->
        <el-form-item label="Target server" prop="targetServerId">
          <el-select
            v-model="formData.targetServerId"
            placeholder="Please select the target server to backup"
            filterable
            clearable
            :loading="loading.targetServerOptions"
            @visible-change="fetchTargetServersForBackupIfNeeded"
            @change="handleTargetServerChange"
            style="width: 100%"
          >
            <el-option
              v-for="item in options.targetServerOptions"
              :key="item.id"
              :label="`${item.name || 'Unknown name'} (${item.ipv4 || item.serverId || 'Unknown IP'})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </template>

      <!-- 任务模式 -->
      <el-form-item label="task model" prop="taskMode">
        <el-radio-group v-model="formData.taskMode" @change="handleTaskModeChange">
          <el-radio label="1">Execute lmmediately</el-radio>
          <el-radio label="2">Scheduled Execution</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- ==================== 定时设置 (只用 v-show 控制整个区域) ==================== -->
      <div v-show="formData.taskMode === '2'">
        <el-row :gutter="10" style="margin-bottom: 18px">
          <el-col :span="8">
            <el-form-item label="Frequency" prop="operatingCycle">
              <el-select
                v-model="formData.operatingCycle"
                placeholder="selection cycle"
                @change="handleOperatingCycleChange"
              >
                <el-option label="Daily" value="1"></el-option>
                <el-option label="Weekly" value="2"></el-option>
                <el-option label="Monthly" value="3"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- **** 修改：v-if -> v-show **** -->
            <el-form-item
              label="operationDate"
              prop="operationDate"
              v-show="formData.operatingCycle === '2' || formData.operatingCycle === '3'"
              :key="'date-select-' + formData.operatingCycle"
            >
              <el-select
                v-model="formData.operationDate"
                multiple
                placeholder="Select the date/week"
                style="width: 100%"
                clearable
                collapse-tags
                collapse-tags-tooltip
                :max-collapse-tags="3"
              >
                <el-option
                  v-for="item in dateOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="runTime" prop="runTime">
              <!-- **** 添加 key **** -->
              <el-time-picker
                :key="'time-picker-' + formData.taskMode"
                v-model="runTimeValue"
                placeholder="select time"
                format="HH:mm"
                style="width: 100%"
                @change="handleTimePickerChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 启用状态 (也放在 v-show='taskMode==="2"' 的 div 内) -->
        <el-form-item label="state" prop="enable">
          <el-radio-group v-model="formData.enable">
            <el-radio label="1">enable</el-radio>
            <el-radio label="0">forbidden</el-radio>
          </el-radio-group>
        </el-form-item>
      </div>
      <!-- ==================== 定时设置 End ==================== -->
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">cancel</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
        {{ submitButtonText }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, nextTick } from 'vue'
import {
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElButton,
  ElSelect,
  ElOption,
  ElMessage,
  ElRadioGroup,
  ElRadio,
  ElTimePicker,
  ElRow,
  ElCol,
  ElIcon,
  ElLoading,
} from 'element-plus'
import { CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import {
  addStrategy,
  updateStrategy, // 导入 add/update API
  getBackupTargetListApi,
  getDatabasesApi,
  getTablesForDatabaseApi,
  getTargetServerListForBackup,
  testConnectionApi, // 导入新的 API
} from '@/api/backup'
import { getDatabaseList, testDatabaseConnection } from '@/api/database' // 来自 database.js
import { queryServers } from '@/api/server' // 来自 server.js
import dayjs from 'dayjs' // 用于处理时间 runTimeValue

const props = defineProps({
  modelValue: Boolean,
  strategyData: { type: Object, default: null },
})

const emit = defineEmits(['update:modelValue', 'submit-success'])

const strategyFormRef = ref(null)
const timePickerValue = ref(null)
const formLoading = ref(false) // 控制整个表单的加载状态（例如编辑时加载数据）
const submitLoading = ref(false)

// 各下拉框和测试按钮的独立 loading 状态
const loading = reactive({
  databaseOptions: false, // <-- 确认这个 key 存在且被模板使用
  sourceServers: false,
  targetServerOptions: false,
  testConnection: false,
  tables: false,
})

// 下拉框选项
const options = reactive({
  sourceServers: [], // { id, name, ipv4, user, password, ... }
  targetServerOptions: [], // { id, name, ipv4, serverId, ... }
  tables: [],
})

// 测试连接结果
const testResult = ref(null) // null | 'success' | 'error'
const testResultMessage = ref('')

// 表单数据模型
const initialFormData = () => ({
  id: null,
  title: '',
  backupWay: null,
  dataSourceType: null,
  totalMethod: null,
  targetServerId: null,
  databases: null, // 数据库 ID
  tables: [], // **** 添加回 tables 字段 ****
  sourceServerId: null,
  backupTargetInput: '',
  taskMode: '2',
  operatingCycle: '1',
  operationDate: [],
  runTime: '02:00',
  enable: '1',
})

const formData = reactive(initialFormData())

// --- 表单校验规则 ---
// (需要根据 DTO 和业务逻辑调整)
const formRules = reactive({
  title: [{ required: true, message: 'Please enter the strategy name', trigger: 'blur' }],
  backupWay: [{ required: true, message: 'Please select the backup method', trigger: 'change' }],
  // --- 数据库备份校验 ---
  dataSourceType: [
    {
      required: computed(() => formData.backupWay === '1'),
      message: 'Please select the data source type',
      trigger: 'change',
    },
  ],
  totalMethod: [
    {
      required: computed(() => formData.backupWay === '1' || formData.backupWay === '2'), // 两种方式都需要
      message: 'Please select or confirm the specific method',
      trigger: 'change',
    },
  ],
  databases: [
    {
      // 现在是备份数据库 ID
      required: computed(() => formData.backupWay === '1'),
      message: 'Please select the backup database',
      trigger: 'change',
    },
  ],
  tables: [
    {
      type: 'array',
      min: 0,
      message: 'Please select a table',
      trigger: 'change',
    },
  ],
  targetServerId: [
    {
      // 目标服务器ID (两种方式都需要)
      required: computed(() => formData.backupWay === '1' || formData.backupWay === '2'),
      message: 'Please select the target server',
      trigger: 'change',
    },
  ],
  // --- 服务器目录备份校验 ---
  sourceServerId: [
    {
      required: computed(() => formData.backupWay === '2'),
      message: 'Please select the source server',
      trigger: 'change',
    },
  ],
  backupTargetInput: [
    {
      required: computed(() => formData.backupWay === '2'),
      message: 'Please enter the backup path',
      trigger: 'blur',
    },
  ],
  // --- 定时任务校验 ---
  taskMode: [{ required: true, message: 'Please select the task mode', trigger: 'change' }],
  operatingCycle: [
    {
      // required: computed(() => formData.taskMode === '2'), // v-show 下可以一直校验或不校验
      required: true, // 或者如果 taskMode=2 时总是需要，就设为 true
      message: 'Please select the execution cycle',
      trigger: 'change',
    },
  ],
  operationDate: [
    {
      // required 条件保持不变，因为外层有 v-show 控制显隐
      required: computed(
        () => formData.taskMode === '2' && ['2', '3'].includes(formData.operatingCycle),
      ),
      type: 'array',
      min: 0,
      message: 'Please select the execution date/week',
      trigger: 'change',
    },
  ],
  runTime: [
    {
      required: true,
      message: 'Please select the execution time',
      trigger: 'change',
    },
  ],
  enable: [
    {
      required: true,
      message: 'Please select the enabled status',
      trigger: 'change',
    },
  ],
})

// --- Computed Properties ---
const isEditMode = computed(() => !!props.strategyData?.id)
const modalTitle = computed(() => (isEditMode.value ? 'Edit Backup Policy' : 'Add Backup Policy'))
const submitButtonText = computed(() => (isEditMode.value ? 'save' : 'Create'))

// 计算执行日期的选项 (周/月)
const dateOptions = computed(() => {
  // ... (这部分逻辑不变) ...
  let options = []
  if (formData.operatingCycle === '2') {
    // 每周
    const weekDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
    for (let i = 0; i < 7; i++) {
      options.push({ label: `${weekDays[i]}`, value: String(i + 1) }) // 后端期望 1-7
    }
  } else if (formData.operatingCycle === '3') {
    // 每月
    for (let i = 1; i <= 31; i++) {
      options.push({ label: `${i}date`, value: String(i) })
    }
    options.push({ label: 'last day', value: 'L' }) // 后端是否支持 'L'? 假设支持
  }
  return options
})

// 处理 TimePicker 的 v-model
const runTimeValue = computed({
  get() {
    if (
      formData.runTime &&
      typeof formData.runTime === 'string' &&
      /^\d{2}:\d{2}$/.test(formData.runTime)
    ) {
      try {
        return dayjs(`1970-01-01 ${formData.runTime}:00`).toDate()
      } catch (e) {
        console.error('Error parsing runTime:', formData.runTime, e)
        return null // 解析失败返回 null
      }
    }
    // console.warn("runTimeValue get: formData.runTime is invalid or null:", formData.runTime);
    return null // 明确返回 null
  },
  set(newValue) {
    if (newValue instanceof Date) {
      formData.runTime = dayjs(newValue).format('HH:mm')
    } else {
      formData.runTime = null
    }
  },
})

// 获取选中的源服务器信息 (使用新字段名)
const selectedSourceServerInfo = computed(() => {
  if (formData.backupWay === '2' && formData.sourceServerId) {
    return options.sourceServers.find((s) => String(s.id) === String(formData.sourceServerId))
  }
  return null
})

// 判断测试连接按钮是否可用 (依赖更新)
const isTestConnectionReady = computed(() => {
  if (formData.backupWay === '1') {
    // 数据库：需要数据源类型、备份数据库 ID、目标服务器 ID
    return !!formData.dataSourceType && !!formData.databases && !!formData.targetServerId
  } else if (formData.backupWay === '2') {
    // 服务器：需要具体方法、源服务器 ID、目标服务器 ID
    return !!formData.totalMethod && !!formData.sourceServerId && !!formData.targetServerId
  }
  return false
})

// 用于编辑模式强制重新加载下拉框的标志
const editModeForceReload = reactive({
  databaseOptions: false,
  sourceServers: false,
  tables: false,
  targetServerOptions: false,
})

// --- Watchers ---

// 监听编辑数据填充表单
watch(
  () => props.strategyData,
  (newData) => {
    if (newData && newData.id && props.modelValue && isEditMode.value) {
      formLoading.value = true
      // **** 重置所有强制加载标志 ****
      Object.keys(editModeForceReload).forEach((key) => (editModeForceReload[key] = true))

      nextTick(async () => {
        try {
          resetForm()

          // 填充基础字段
          Object.keys(formData).forEach((key) => {
            if (newData[key] !== undefined && newData[key] !== null && key !== 'runTime') {
              if (key === 'operationDate' && typeof newData[key] === 'string') {
                try {
                  // 尝试解析 JSON 字符串为数组
                  formData.operationDate = JSON.parse(newData[key] || '[]') // 提供默认空数组以防 null 或空字符串
                } catch (e) {
                  console.error(`解析 operationDate 失败: '${newData[key]}'`, e)
                  formData.operationDate = [] // 解析失败则设为空数组
                }
              }
              // **** 更新字段映射 (同上一个版本) ****
              else if (key === 'databases' && newData.url && newData.backupWay === '1') {
                formData.databases = newData.url[0]
              } else if (key === 'targetServerId') {
                /* ... (需要确认来源) ... */
                // 数据库备份时，假设从 backupTarget[0] 获取
                if (newData.backupTarget && newData.backupWay === '1') {
                  formData.targetServerId = newData.backupTarget[0]
                }
                // 服务器备份时，假设也从 backupTarget[0] 获取 (极可能需要调整!)
                else if (newData.backupTarget && newData.backupWay === '2') {
                  formData.targetServerId = newData.backupTarget[0]
                }
              } else if (key === 'sourceServerId' && newData.url && newData.backupWay === '2') {
                formData.sourceServerId = newData.url[0]
              } else if (
                key === 'backupTargetInput' &&
                newData.backupTarget &&
                newData.backupWay === '2'
              ) {
                formData.backupTargetInput = newData.backupTarget[0]
              }
              // **** 添加回 tables 回显 ****
              else if (key === 'tables' && Array.isArray(newData[key])) {
                formData.tables = [...newData[key]] // 直接使用 newData 的 tables 字段
              } else if (formData.hasOwnProperty(key)) {
                formData[key] = newData[key]
              }
              formData[key] = newData[key]
            }
          })

          // 处理 taskMode 和 enable
          formData.taskMode = String(formData.taskMode || '2')
          formData.enable = String(formData.enable || '1')
          if (formData.taskMode === '1') formData.enable = '1'

          // 设置 MySQL 的 totalMethod
          handleDataSourceTypeChange(false)

          // 处理时间选择器
          if (formData.runTime) runTimeValue.value = runTimeValue.value // 触发 setter

          // **** 预加载下拉框选项 (顺序可能重要) ****
          await fetchTargetServersForBackupIfNeeded(true) // 1. 加载目标服务器

          if (formData.backupWay === '1') {
            if (formData.dataSourceType) {
              await fetchDatabaseListForBackupIfNeeded(true) // 2. 加载数据库列表
              if (formData.databases) {
                await fetchTablesIfNeeded(true) // 3. 加载表格列表 (依赖数据库)
              }
            }
          } else if (formData.backupWay === '2') {
            await fetchSourceServers(true) // 加载源服务器列表
          }
        } catch (error) {
          console.error('编辑模式加载/回显出错:', error)
          ElMessage.error('加载编辑数据时出错，请检查控制台')
        } finally {
          formLoading.value = false
        }
      })
      // **** 单独处理 runTime ****
      formData.runTime = newData.runTime || initialFormData().runTime
      syncStringToDate() // 将回显的字符串同步到 Date Ref
    }
  },
  { immediate: false, deep: true },
)

// 监听弹窗打开状态，如果是新增，重置表单
watch(
  () => props.modelValue,
  (isVisible) => {
    syncStringToDate()
    if (!isEditMode.value) {
      nextTick(() => {
        resetForm()
      })
    }
  },
)

// 确保在 backupWay 变为 '2' 时调用 fetchSourceServers
watch(
  () => formData.backupWay,
  (newVal, oldVal) => {
    // 添加 oldVal 用于判断是否是从非 '2' 切换过来的
    if (newVal === '2') {
      // 可以在这里调用 fetchSourceServers()，如果希望切换时就加载
      // 或者依赖 @visible-change，用户点击时再加载
      // fetchSourceServers(); // 取决于你的需求
    } else {
      // 从 '2' 切换走时，清空选项和选中值
      if (oldVal === '2') {
        options.sourceServers = [] // <-- 修改这里：清空正确的数组
        formData.sourceServerId = null // 清空已选值
      }
    }
    // 其他联动逻辑...
    resetTestResult() // 切换备份方式时重置测试结果
    // 清空可能不再相关的字段
    if (newVal !== oldVal) {
      // 只有在值确实改变时才清空
      if (newVal === '1') {
        // 切换到数据库
        formData.sourceServerId = null
        formData.backupTargetInput = ''
      } else if (newVal === '2') {
        // 切换到服务器
        formData.dataSourceType = null
        formData.databases = null
        formData.tables = []
        options.databaseOptions = [] // 如果之前加载过
        options.tables = [] // 如果之前加载过
      }
    }
  },
)

// --- Methods ---

// 重置表单和相关状态
const resetForm = () => {
  strategyFormRef.value?.resetFields() // 这个会根据 rules 清空字段
  Object.assign(formData, initialFormData()) // 强制用初始值覆盖
  options.sourceServers = []
  options.targetServerOptions = []
  options.tables = []
  loading.databaseOptions = false
  loading.sourceServers = false
  loading.targetServerOptions = false
  loading.testConnection = false
  loading.tables = false
  testResult.value = null
  testResultMessage.value = ''
  syncStringToDate()
  // **** 移除下面这行 nextTick ****
  // nextTick(() => { if (!formData.runTime) runTimeValue.value = null; });
  console.log('Form reset, formData.runTime:', formData.runTime) // Debug
}

// **** 新增：将 formData.runTime (string) 同步到 timePickerValue (Date) ****
const syncStringToDate = (timeString = formData.runTime) => {
  if (timeString && typeof timeString === 'string' && /^\d{2}:\d{2}$/.test(timeString)) {
    try {
      timePickerValue.value = dayjs(`1970-01-01 ${timeString}:00`).toDate()
    } catch (e) {
      console.error('Error parsing time string:', timeString, e)
      timePickerValue.value = null
    }
  } else {
    // 如果初始值无效或不存在，可以设置一个默认 Date 或保持 null
    // timePickerValue.value = dayjs('1970-01-01 02:00:00').toDate();
    timePickerValue.value = null // 或者保持 null
  }
  console.log(
    'syncStringToDate - formData:',
    formData.runTime,
    'timePickerValue:',
    timePickerValue.value,
  ) // Debug
}

// **** 新增：处理 TimePicker 的 change 事件，将 Date 同步回 formData.runTime ****
const handleTimePickerChange = (newDateValue) => {
  if (newDateValue instanceof Date) {
    formData.runTime = dayjs(newDateValue).format('HH:mm')
  } else {
    formData.runTime = null // 清空时 newDateValue 是 null
  }
  console.log(
    'handleTimePickerChange - timePickerValue:',
    newDateValue,
    'formData:',
    formData.runTime,
  ) // Debug
  // 可能需要手动触发校验
  strategyFormRef.value?.validateField('runTime')
}

// 处理任务模式变化
const handleTaskModeChange = () => {
  resetTestResult() // 这个可能不需要每次都重置
  if (formData.taskMode === '1') {
    formData.enable = '1'
    // 清空定时相关字段可能不是必须的，因为 v-show 会隐藏它们
    // 如果确实需要清空，可以保留：
    // formData.operatingCycle = '1';
    // formData.operationDate = [];
    // formData.runTime = '02:00'; // 重置回默认值
  } else {
    // 切回定时，确保有默认值
    formData.operatingCycle = formData.operatingCycle || '1'
    formData.runTime = formData.runTime || '02:00'
    formData.enable = formData.enable || '1'
  }
  // 如果使用了 v-show, 可能需要手动触发一次校验来更新依赖 v-if 的 required 状态
  // nextTick(() => {
  //   strategyFormRef.value?.validateField(['operatingCycle', 'operationDate', 'runTime', 'enable']);
  // });
}

// 处理数据源类型变化
const handleDataSourceTypeChange = (clearDependencies = true) => {
  if (clearDependencies) {
    formData.totalMethod = null
    formData.databases = null
    formData.tables = [] // **** 清空 tables ****
    options.databaseOptions = []
    options.tables = [] // **** 清空 tables 选项 ****
    resetTestResult()
  }
  if (formData.dataSourceType === '2') {
    // MySQL
    formData.totalMethod = '1' // mysqldump
  }
}

// 处理目标服务器变化
const handleTargetServerChange = () => {
  // 目标服务器变化，现在不直接影响其他下拉框的加载
  resetTestResult()
}

// 处理备份数据库变化 (添加清空表格逻辑)
const handleDatabaseChange = () => {
  formData.tables = [] // **** 切换数据库时清空已选表格 ****
  options.tables = [] // **** 清空表格选项 ****
  resetTestResult()
}

// 处理服务器备份方法变化
const handleServerMethodChange = () => {
  resetTestResult()
}

// 处理源服务器变化
const handleSourceServerChange = () => {
  resetTestResult()
}

// 处理执行周期变化
const handleOperatingCycleChange = () => {
  formData.operationDate = [] // 切换周期时清空已选日期
}

const databaseListOptions = ref([])
const databaseListLoading = ref(false)
// **** 新增：获取备份数据库列表 ****
const fetchDatabaseListForBackupIfNeeded = async (force = false) => {
  console.log(formData)
  // ... (可选的缓存检查) ...
  if (!formData.dataSourceType) {
    console.warn('获取数据库列表需要数据源类型')
    databaseListOptions.value = []
    return
  }

  databaseListLoading.value = true
  loading.databaseOptions = true
  try {
    // **** 准备查询参数 ****
    const queryParams = {
      pageNum: 1, // 固定为 1
      pageSize: 1000, // 固定为一个较大的数，实现假不分页
    }

    // **** 准备请求体 (DatabaseDto) ****
    const dataBody = {
      sourceType: formData.dataSourceType, // 从表单获取
      // 如果 DatabaseDto 还有其他字段需要传 (比如 content)，也在这里添加
      // content: '' // 示例：如果需要 content 字段
    }

    // **** 调用 API ****
    const res = await getDatabaseList(queryParams, dataBody)
    console.log('Raw API response records:', JSON.stringify(res.data?.records, null, 2)) // 确认原始数据

    // **** 处理响应数据 (直接赋值) ****
    if (res && res.data && Array.isArray(res.data.records)) {
      // **** 修改：直接赋值原始 records 数组 ****
      databaseListOptions.value = res.data.records
      console.log(
        'Directly assigned databaseListOptions:',
        JSON.stringify(databaseListOptions.value, null, 2),
      ) // 确认赋值后的数据
    } else {
      databaseListOptions.value = []
      console.warn('No valid records found in response')
      if (res?.meta?.message) ElMessage.error(`加载数据库列表失败: ${res.meta.message}`)
    }
  } catch (error) {
    console.error('获取数据库列表失败:', error)
    databaseListOptions.value = []
  } finally {
    databaseListLoading.value = false
    loading.databaseOptions = false
  }
}

// **** 新增：获取目标服务器列表 ****
const fetchTargetServersForBackupIfNeeded = async (force = false) => {
  // 这个 API 不需要参数，可以直接调用
  if (!force && options.targetServerOptions.length > 0 && !editModeForceReload.targetServerOptions)
    return
  if (loading.targetServerOptions) return

  loading.targetServerOptions = true
  editModeForceReload.targetServerOptions = false

  try {
    console.log('Fetching target server list...') // Debug
    const res = await getTargetServerListForBackup()
    // 假设返回 { data: [{ id, name, ipv4, serverId, ... }] }
    if (res && Array.isArray(res.data)) {
      options.targetServerOptions = res.data
      console.log('Target server options loaded:', options.targetServerOptions) // Debug
    } else {
      options.targetServerOptions = []
      console.warn('获取目标服务器列表响应格式不符:', res)
      if (res?.meta?.message) ElMessage.error(`加载目标服务器失败: ${res.meta.message}`)
    }
  } catch (error) {
    console.error('获取目标服务器列表失败:', error)
    options.targetServerOptions = []
  } finally {
    loading.targetServerOptions = false
  }
}

// 获取数据库列表
const fetchDatabasesIfNeeded = async (force = false) => {
  if (!formData.targetServerId || !formData.dataSourceType) return // 依赖检查
  if (!force && options.databases.length > 0) return
  if (loading.databases) return

  loading.databases = true
  try {
    const params = {
      sourceType: formData.dataSourceType,
      id: formData.targetServerId, // API 需要目标服务器 ID
    }
    const res = await getDatabasesApi(params)
    // 假设返回 { data: ['db1', 'db2'] }
    if (res && res.data && Array.isArray(res.data)) {
      options.databases = res.data
    } else {
      options.databases = []
      console.warn('获取数据库列表响应格式不符:', res)
      if (res?.meta?.message) ElMessage.error(res.meta.message) // 显示后端错误
    }
  } catch (error) {
    console.error('获取数据库列表失败:', error)
    options.databases = []
  } finally {
    loading.databases = false
  }
}

// **** 添加回：获取表格列表 ****
const fetchTablesIfNeeded = async (force = false) => {
  if (!formData.databases) return // 必须选择了数据库
  // 使用 editModeForceReload.tables
  if (!force && options.tables.length > 0 && !editModeForceReload.tables) return
  if (loading.tables) return

  loading.tables = true
  editModeForceReload.tables = false // 重置标志

  try {
    console.log('Fetching tables for database:', formData.databases) // Debug
    // **** 调用新的 API ****
    const res = await getTablesForDatabaseApi(formData.databases)

    // **** 根据实际 API 返回结构调整 ****
    // 假设返回 { "data": ["table1", "table2", ...] }
    if (res && Array.isArray(res.data)) {
      options.tables = res.data
      console.log('Tables loaded:', options.tables) // Debug
      // **** 重要：检查回显的 tables 是否还在新的选项中 ****
      formData.tables = formData.tables.filter((t) => options.tables.includes(t))
    }
    // 或者如果返回 { "data": { "records": [...] } }
    // else if (res && res.data && Array.isArray(res.data.records)) {
    //   options.tables = res.data.records;
    //   console.log("Tables loaded:", options.tables); // Debug
    //   formData.tables = formData.tables.filter(t => options.tables.includes(t));
    // }
    else {
      options.tables = []
      formData.tables = [] // 清空已选
      console.warn('获取表格列表响应格式不符:', res)
      if (res?.meta?.message) ElMessage.error(`加载表格列表失败: ${res.meta.message}`)
    }
  } catch (error) {
    console.error('获取表格列表失败:', error)
    options.tables = []
    formData.tables = [] // 出错时清空
  } finally {
    loading.tables = false
  }
}

// 获取源服务器列表 (修正版本)
const fetchSourceServers = async (force = false) => {
  // 使用 editModeForceReload 标志进行缓存检查 (如果需要)
  if (!force && options.sourceServers.length > 0 && !editModeForceReload.sourceServers) return
  // 使用 loading.sourceServers 控制加载状态
  if (loading.sourceServers) return

  console.log('Fetching source servers...') // 添加日志
  loading.sourceServers = true // <-- 修改这里
  editModeForceReload.sourceServers = false // 重置强制加载标志

  try {
    const params = {
      page: 1,
      pageSize: 1000,
    }
    const res = await queryServers(params)
    console.log('Source Server API Response:', res) // 确认响应

    // **** 关键：处理响应并直接赋值给 options.sourceServers ****
    if (res && res.data && Array.isArray(res.data.records)) {
      options.sourceServers = res.data.records // <-- 修改这里：直接赋值原始数组
      console.log('Source servers loaded into options.sourceServers:', options.sourceServers) // 确认赋值
    } else {
      // 如果响应结构不对或 records 不是数组，清空选项
      options.sourceServers = [] // <-- 修改这里
      console.warn('获取源服务器列表响应格式不符或无数据:', res)
      if (res?.meta?.message && res.meta.success === false) {
        ElMessage.error(`加载源服务器列表失败: ${res.meta.message}`)
      } else if (!res?.meta?.success) {
        ElMessage.warning('加载源服务器列表时响应数据格式异常')
      }
    }
  } catch (error) {
    console.error('获取源服务器列表失败:', error)
    options.sourceServers = [] // <-- 修改这里：出错时清空正确的数组
    ElMessage.error('获取源服务器列表时发生网络或程序错误')
  } finally {
    loading.sourceServers = false // <-- 修改这里
  }
}

// 编辑时根据 ID 获取单个源服务器信息以显示在下拉框中
const fetchSingleSourceServer = async (serverId) => {
  if (!serverId) return
  loading.sourceServers = true // 复用 loading 状态
  try {
    const params = { page: 1, pageSize: 1, id: serverId } // 尝试通过 ID 查询，API 可能不支持
    const res = await queryServers(params)
    if (res?.data?.records?.length > 0) {
      // 如果查询到了，将其放入 options，这样 Select 能显示 label
      options.sourceServers = res.data.records
    } else {
      // 如果 API 不支持 ID 查询，可能需要另一个 API 或在初次加载时获取所有服务器
      console.warn(`无法获取 ID 为 ${serverId} 的服务器信息`)
    }
  } catch (error) {
    console.error('获取单个服务器信息失败:', error)
  } finally {
    loading.sourceServers = false
  }
}

const testingConnection = ref(false) // 用于显示按钮加载状态
// 测试连接 (更新 DTO 构造)
const handleTestConnection = async () => {
  testingConnection.value = true
  let testData = {} // 用于构造发送给后端的数据
  let targetDescription = '' // 用于显示提示信息的目标描述

  try {
    // --- 根据备份方式 (backupWay) 决定测试目标和构造数据 ---
    if (formData.backupWay === '1') {
      // **** 数据库备份模式 ****
      if (!formData.databases) {
        ElMessage.warning('请先选择要测试连接的备份数据库')
        testingConnection.value = false // 提前结束
        return
      }
      if (!formData.dataSourceType) {
        // 数据库模式需要数据源类型
        ElMessage.warning('请选择数据源类型 (Oracle/MySQL)')
        testingConnection.value = false
        return
      }
      targetDescription = `数据库 [ID: ${formData.databases}]`
      testData = {
        backupWay: formData.backupWay, // '1'
        sourceType: formData.dataSourceType, // Oracle '1' 或 MySQL '2'
        urlList: [String(formData.databases)], // 数据库 ID 列表
        // backupMethod 和 backupTarget 在数据库模式下不需要
      }
      ElMessage.info(`正在测试 ${targetDescription} 连接...`)
    } else if (formData.backupWay === '2') {
      // **** 服务器备份模式 ****
      if (!formData.serverSource) {
        ElMessage.warning('请先选择要测试连接的源服务器')
        testingConnection.value = false
        return
      }
      if (!formData.backupMethod) {
        // 服务器模式需要备份方法
        ElMessage.warning('请选择备份方法 (SSH/FTP)')
        testingConnection.value = false
        return
      }
      if (!formData.backupTargetPath) {
        // 服务器模式需要备份路径
        ElMessage.warning('请输入备份路径')
        testingConnection.value = false
        return
      }
      targetDescription = `源服务器 [ID: ${formData.serverSource}]`
      testData = {
        backupWay: formData.backupWay, // '2'
        backupMethod: formData.backupMethod, // SSH '2' 或 FTP '3'
        urlList: [String(formData.serverSource)], // 源服务器 ID 列表
        backupTarget: formData.backupTargetPath, // 备份路径
        // sourceType 在服务器模式下不需要
      }
      ElMessage.info(`正在测试 ${targetDescription} 连接...`)
    } else {
      // 如果 backupWay 不是 '1' 或 '2'，给出错误提示
      ElMessage.error('未知的备份方式，无法测试连接')
      testingConnection.value = false
      return
    }

    console.log('Testing connection with data:', JSON.stringify(testData)) // 打印构造的请求数据

    // --- 调用 API ---
    const res = await testConnectionApi(testData)

    // --- 处理结果 ---
    if (res && (res.meta?.success || res.code === 200)) {
      ElMessage.success(`${targetDescription} 连接成功！`)
    } else {
      const errorMsg = res?.meta?.message || res?.message || '连接失败，请检查配置或联系管理员'
      ElMessage.error(`${targetDescription} 连接失败: ${errorMsg}`)
    }
  } catch (error) {
    console.error(`测试 ${targetDescription || '连接'} 异常:`, error)
    const errorMsg = error?.response?.data?.message || error?.message || '请求异常'
    ElMessage.error(`测试 ${targetDescription || '连接'} 时出错: ${errorMsg}`)
  } finally {
    testingConnection.value = false
  }
}

// 重置测试结果状态
const resetTestResult = () => {
  testResult.value = null
  testResultMessage.value = ''
}

// 在 options 旁边，或者 computed 部分添加一个获取目标服务器 IP 的辅助函数
const getTargetServerIp = (targetServerId) => {
  const server = options.targetServerOptions.find((s) => String(s.id) === String(targetServerId))
  // --- 修改：处理 server 可能不存在的情况，并优先使用 ipv4 ---
  if (server) {
    return server.ipv4 || server.serverId || null // 优先用 ipv4，然后 serverId，最后 null
  }
  return null // 找不到服务器对象，返回 null
}

// / 提交表单 (添加回 tables)
const handleSubmit = async () => {
  if (!strategyFormRef.value) return
  resetTestResult() // 这个可以保留

  try {
    const isValid = await strategyFormRef.value.validate()
    if (!isValid) {
      ElMessage.error('表单校验失败，请检查输入项')
      return
    }
    submitLoading.value = true

    const dataToSubmit = {}
    // ... (填充 title, backupWay, taskMode, totalMethod 等...) ...
    dataToSubmit.title = formData.title
    dataToSubmit.backupWay = formData.backupWay
    dataToSubmit.taskMode = formData.taskMode
    dataToSubmit.totalMethod = formData.totalMethod

    if (formData.backupWay === '1') {
      dataToSubmit.dataSourceType = formData.dataSourceType
      // **** 注意：后端期望 url 和 backupTarget 是数组 ****
      dataToSubmit.url = formData.databases ? [String(formData.databases)] : []
      dataToSubmit.backupTarget = formData.targetServerId ? [String(formData.targetServerId)] : []
      dataToSubmit.tables = formData.tables || []
      dataToSubmit.expdb = formData.totalMethod === '3' ? '1' : '0' // 假设 MySQL(1) 和 Oracle exp(2) 都是 '0'
      // 修正: 如果 totalMethod 不是 '3', expdb 应该为 '0'
      if (formData.totalMethod !== '3') {
        dataToSubmit.expdb = '0'
      }
    } else if (formData.backupWay === '2') {
      // 使用 else if 避免重复判断
      // ... (填充 backupMethod, url, backupTarget, login*) ...
      // 假设服务器备份的 totalMethod 映射到 backupMethod
      dataToSubmit.backupMethod = formData.totalMethod // 确保 totalMethod 的值对应后端 backupMethod 的期望值
      dataToSubmit.url = formData.sourceServerId ? [String(formData.sourceServerId)] : []
      // **** backupTarget 应该是备份路径，也是数组 ****
      dataToSubmit.backupTarget = formData.backupTargetInput ? [formData.backupTargetInput] : []
      const sourceServer = selectedSourceServerInfo.value
      if (sourceServer) {
        // 确保 sourceServer 存在
        // dataToSubmit.loginUrl = sourceServer?.ipv4 || ''
        dataToSubmit.loginUsername = sourceServer?.user || ''
        // **** 密码需要从 options.sourceServers 里的完整对象获取 ****
        const fullSourceServer = options.sourceServers.find(
          (s) => String(s.id) === String(formData.sourceServerId),
        )
        dataToSubmit.loginPassword = fullSourceServer?.password || '' // 注意安全风险，理想情况密码不应存储在前端选项中
      } else {
        // 如果找不到源服务器信息，可能需要报错或给默认值
        console.warn('无法找到选定的源服务器信息')
        // dataToSubmit.loginUrl = ''
        dataToSubmit.loginUsername = ''
        dataToSubmit.loginPassword = ''
      }
      dataToSubmit.loginUrl = String(formData.targetServerId || '') // <-- 修改：使用目标服务器ID
    }

    if (formData.taskMode === '2') {
      // ... (填充 operatingCycle, operationDate, runTime, enable) ...
      dataToSubmit.operatingCycle = formData.operatingCycle
      // 确保 operationDate 是 JSON 字符串
      try {
        dataToSubmit.operationDate = JSON.stringify(formData.operationDate || [])
      } catch (e) {
        console.error('序列化 operationDate 失败:', formData.operationDate, e)
        dataToSubmit.operationDate = '[]' // 出错时给默认空数组字符串
      }
      dataToSubmit.runTime = formData.runTime
      dataToSubmit.enable = formData.enable
    } else {
      // 立即执行模式下，后端可能仍需要这些字段或有默认值，enable 应为 '1'
      dataToSubmit.enable = '1'
      // 清空或设置默认定时任务字段 (根据后端要求决定)
      // dataToSubmit.operatingCycle = null;
      // dataToSubmit.operationDate = null;
      // dataToSubmit.runTime = null;
    }

    console.log('最终提交的数据:', JSON.stringify(dataToSubmit, null, 2)) // 打印清晰的 JSON 格式

    // --- 将 API 调用和结果处理放在单独的 try...catch 中 ---
    try {
      let res = null // 用于存储 API 响应
      if (isEditMode.value) {
        dataToSubmit.id = props.strategyData.id
        res = await updateStrategy(dataToSubmit)
      } else {
        res = await addStrategy(dataToSubmit)
      }

      console.log('API Response:', res) // 打印原始响应以供调试

      // **** 关键：检查 API 响应状态 ****
      if (res && res.meta && res.meta.success === true) {
        // --- 业务逻辑成功 ---
        ElMessage.success(isEditMode.value ? '策略更新成功' : '策略新增成功')
        emit('submit-success', false)
        emit('update:modelValue', false) // 成功时关闭模态框
      } else {
        // --- 业务逻辑失败 (例如：名称重复、验证失败等) ---
        const errorMsg =
          res?.meta?.message || (isEditMode.value ? '策略更新失败，请重试' : '策略新增失败，请重试')
        ElMessage.error(errorMsg) // 显示后端返回的错误信息
        // 注意：失败时不关闭模态框，让用户可以修改
      }
    } catch (apiError) {
      // --- API 调用本身出错 (网络错误、服务器崩溃等，非 2xx 状态码) ---
      console.error('API调用出错:', apiError)
      // 尝试从错误对象中提取更具体的后端错误信息 (如果 Axios Interceptor 配置了的话)
      const errorMsg =
        apiError?.response?.data?.meta?.message ||
        apiError?.response?.data?.message ||
        apiError?.message ||
        '操作失败，请检查网络或联系管理员'
      ElMessage.error(errorMsg)
      // 同样，失败时不关闭模态框
    }
  } catch (validationError) {
    // --- 表单校验失败 ---
    // validate() 已经触发了 Element Plus 的错误提示，这里可以只 log
    console.error('表单校验失败:', validationError)
    // ElMessage.error('表单校验失败，请检查输入项'); // 这句可以省略，因为 validate 失败会自动显示
  } finally {
    submitLoading.value = false // 无论成功失败，结束 loading 状态
  }
}

// 处理弹窗关闭动画完成后的事件
const handleDialogClosed = () => {
  // resetForm() // 确保关闭后彻底重置
  console.log('Dialog closed and form reset')
}
</script>

<style scoped>
.el-form-item {
  margin-bottom: 18px; /* 稍微减小间距 */
}
.el-select,
.el-time-picker {
  width: 100%;
}
/* 微调 Row 抵消 FormItem 的间距 */
.el-row {
  margin-left: -5px !important;
  margin-right: -5px !important;
  margin-bottom: -18px; /* 匹配 el-form-item 的 margin-bottom */
}
.el-col {
  padding-left: 5px !important;
  padding-right: 5px !important;
}
</style>
