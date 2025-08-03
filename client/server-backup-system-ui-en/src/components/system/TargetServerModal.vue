<!-- src/components/system/TargetServerModal.vue -->
<template>
  <el-dialog
    :model-value="modelValue"
    @update:modelValue="$emit('update:modelValue', $event)"
    :title="modalTitle"
    width="600px"
    :close-on-click-modal="false"
    @closed="handleDialogClosed"
    append-to-body
    destroy-on-close
  >
    <el-form
      ref="targetServerFormRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="loading"
      scroll-to-error
    >
      <!-- 根据 BackupManagementDto 调整字段 -->
      <el-form-item label="目标服务器名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入目标服务器名称" />
      </el-form-item>
      <el-form-item label="IP 地址" prop="ipv4">
        <el-input v-model="formData.ipv4" placeholder="请输入 IPv4 地址" />
      </el-form-item>
      <el-form-item label="端口号" prop="port">
        <el-input
          v-model.number="formData.port"
          type="number"
          placeholder="请输入端口号 (例如: 22)"
          :min="1"
          :max="65535"
          controls-position="right"
        />
      </el-form-item>
      <el-form-item label="用户名" prop="user">
        <el-input v-model="formData.user" placeholder="请输入 SSH/登录 用户名" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="请输入 SSH/登录 密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="Oracle 用户名" prop="oracleUser">
        <el-input v-model="formData.oracleUser" placeholder="请输入 Oracle 用户名 (可选)" />
      </el-form-item>
      <el-form-item label="Oracle 密码" prop="oraclePassword">
        <el-input
          v-model="formData.oraclePassword"
          type="password"
          placeholder="请输入 Oracle 密码 (可选)"
          show-password
        />
      </el-form-item>
      <!-- BackupManagementDto 中的其他必填或常用字段也应在此添加 -->
      <!-- 例如：备份方式、数据源类型等，如果新增时需要的话 -->
      <!-- <el-form-item label="备份方式" prop="backupWay"> ... </el-form-item> -->
      <!-- <el-form-item label="数据源类型" prop="dataSourceType"> ... </el-form-item> -->
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        {{ submitButtonText }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, nextTick } from 'vue'
import { ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus'
// **** 导入新的目标服务器 API ****
import { addTargetServer, updateTargetServer } from '@/api/targetServer'

const props = defineProps({
  modelValue: Boolean,
  serverData: { type: Object, default: null },
})

const emit = defineEmits(['update:modelValue', 'submit-success'])

const targetServerFormRef = ref(null)
const loading = ref(false)

// 表单数据模型 (基于 BackupManagementDto 核心字段)
const initialFormData = () => ({
  id: null,
  name: '',
  ipv4: '',
  port: null,
  user: '',
  password: '',
  oracleUser: '',
  oraclePassword: '',
  // 如果新增/编辑时需要其他 BackupManagementDto 字段，在此添加
  // backupWay: null,
  // dataSourceType: null,
  // ...
})

const formData = reactive(initialFormData())

// 端口号验证函数 (复用)
const validatePort = (rule, value, callback) => {
  if (value === null || value === '') {
    callback(new Error('请输入端口号'))
  } else if (!Number.isInteger(value)) {
    callback(new Error('端口号必须是整数'))
  } else if (value < 1 || value > 65535) {
    callback(new Error('端口号必须在 1 到 65535 之间'))
  } else {
    callback()
  }
}

// 表单校验规则 (基于需要的字段)
const formRules = reactive({
  name: [{ required: true, message: '请输入目标服务器名称', trigger: 'blur' }],
  ipv4: [
    { required: true, message: '请输入 IPv4 地址', trigger: 'blur' },
    {
      pattern:
        /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
      message: '请输入有效的 IPv4 地址',
      trigger: 'blur',
    },
  ],
  port: [{ required: true, validator: validatePort, trigger: 'blur' }],
  user: [{ required: true, message: '请输入 SSH/登录 用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入 SSH/登录 密码', trigger: 'blur' }],
  // Oracle 字段通常可选
  // backupWay, dataSourceType 等根据后端是否要求必填来添加规则
})

// --- Computed Properties ---
const isEditMode = computed(() => !!props.serverData?.id)
const modalTitle = computed(() => (isEditMode.value ? '编辑目标服务器' : '新增目标服务器'))
const submitButtonText = computed(() => (isEditMode.value ? '保存更新' : '确认新增'))

// --- Watchers ---
watch(
  () => props.serverData,
  (newData) => {
    if (props.modelValue) {
      nextTick(() => {
        targetServerFormRef.value?.clearValidate()
        if (newData && newData.id) {
          // 编辑模式
          Object.keys(formData).forEach((key) => {
            if (newData[key] !== undefined && newData[key] !== null) {
              formData[key] = newData[key]
            } else {
              formData[key] = initialFormData()[key]
            }
          })
        } else {
          // 新增模式
          Object.assign(formData, initialFormData())
        }
      })
    }
  },
  { immediate: true, deep: true },
)

// --- Methods ---
const resetForm = () => {
  Object.assign(formData, initialFormData())
  targetServerFormRef.value?.clearValidate()
}

const handleSubmit = async () => {
  if (!targetServerFormRef.value) return

  try {
    const isValid = await targetServerFormRef.value.validate()
    if (!isValid) {
      ElMessage.warning('请检查表单输入项')
      return
    }
    loading.value = true

    const dataToSubmit = { ...formData }
    let res = null

    // **注意：后端新增和修改接口都接收 BackupManagementDto **
    // **但是新增时可能只需要部分字段，修改时需要 id **
    // **这里假设两个 API 都已正确处理这些情况**
    if (isEditMode.value) {
      res = await updateTargetServer(dataToSubmit) // **** 调用新的 API ****
    } else {
      // 新增时不需要传 id
      delete dataToSubmit.id
      res = await addTargetServer(dataToSubmit) // **** 调用新的 API ****
    }

    // 后端接口返回 { meta: { success, statusCode, message } }
    if (res?.meta?.success || res?.meta?.statusCode === 200) {
      ElMessage.success(
        res?.meta?.message || (isEditMode.value ? '目标服务器更新成功' : '目标服务器新增成功'),
      )
      emit('submit-success')
      emit('update:modelValue', false)
    } else {
      ElMessage.error(res?.meta?.message || '操作失败')
    }
  } catch (error) {
    console.error('目标服务器提交失败:', error)
    const errorMsg =
      error?.response?.data?.meta?.message ||
      error?.response?.data?.message ||
      error?.message ||
      '请求异常'
    ElMessage.error(`操作失败: ${errorMsg}`)
  } finally {
    loading.value = false
  }
}

// 处理弹窗关闭动画完成后的事件
const handleDialogClosed = () => {
  resetForm()
}
</script>

<style scoped>
/* 可以添加特定样式 */
</style>
