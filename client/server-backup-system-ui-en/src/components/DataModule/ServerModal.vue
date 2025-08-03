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
      ref="serverFormRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
      v-loading="loading"
      scroll-to-error
    >
      <el-form-item label="Server Name" prop="name">
        <el-input v-model="formData.name" placeholder="Enter server name" />
      </el-form-item>
      <el-form-item label="IP Address" prop="ipv4">
        <el-input v-model="formData.ipv4" placeholder="Enter IPv4 address" />
      </el-form-item>
      <el-form-item label="Port" prop="port">
        <el-input
          v-model.number="formData.port"
          type="number"
          placeholder="Enter port number (e.g., 22)"
          :min="1"
          :max="65535"
          controls-position="right"
        />
      </el-form-item>
      <el-form-item label="Username" prop="user">
        <el-input v-model="formData.user" placeholder="Enter SSH/login username" />
      </el-form-item>
      <el-form-item label="Password" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="Enter SSH/login password"
          show-password
        />
      </el-form-item>
      <!-- Additional Oracle fields required for server creation -->
      <el-form-item label="Oracle Username" prop="oracleUser">
        <el-input v-model="formData.oracleUser" placeholder="Enter Oracle username (optional)" />
      </el-form-item>
      <el-form-item label="Oracle Password" prop="oraclePassword">
        <el-input
          v-model="formData.oraclePassword"
          type="password"
          placeholder="Enter Oracle password (optional)"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">Cancel</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        {{ submitButtonText }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, nextTick } from 'vue'
import { ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus'
import { addServer, updateServer } from '@/api/server'

const props = defineProps({
  modelValue: Boolean,
  serverData: { type: Object, default: null },
})

const emit = defineEmits(['update:modelValue', 'submit-success'])

const serverFormRef = ref(null)
const loading = ref(false)

const initialFormData = () => ({
  id: null,
  name: '',
  ipv4: '',
  port: null,
  user: '',
  password: '',
  oracleUser: '',
  oraclePassword: '',
})

const formData = reactive(initialFormData())

const validatePort = (rule, value, callback) => {
  if (value === null || value === '') {
    callback(new Error('Please enter a port number'))
  } else if (!Number.isInteger(value)) {
    callback(new Error('Port must be an integer'))
  } else if (value < 1 || value > 65535) {
    callback(new Error('Port must be between 1 and 65535'))
  } else {
    callback()
  }
}

const formRules = reactive({
  name: [{ required: true, message: 'Please enter a server name', trigger: 'blur' }],
  ipv4: [
    { required: true, message: 'Please enter an IPv4 address', trigger: 'blur' },
    {
      pattern:
        /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
      message: 'Please enter a valid IPv4 address',
      trigger: 'blur',
    },
  ],
  port: [{ required: true, validator: validatePort, trigger: 'blur' }],
  user: [{ required: true, message: 'Please enter the SSH/login username', trigger: 'blur' }],
  password: [{ required: true, message: 'Please enter the SSH/login password', trigger: 'blur' }],
  // oracleUser and oraclePassword are optional, no required rule
})

// --- Computed Properties ---
const isEditMode = computed(() => !!props.serverData?.id)
const modalTitle = computed(() => (isEditMode.value ? 'Edit Server' : 'Add Server'))
const submitButtonText = computed(() => (isEditMode.value ? 'Save Changes' : 'Create'))

// --- Watchers ---
watch(
  () => props.serverData,
  (newData) => {
    if (props.modelValue) {
      nextTick(() => {
        resetFormValidation()
        if (newData && newData.id) {
          // Edit mode
          Object.keys(formData).forEach((key) => {
            formData[key] =
              newData[key] !== undefined && newData[key] !== null
                ? newData[key]
                : initialFormData()[key]
          })
        } else {
          // Add mode
          Object.assign(formData, initialFormData())
        }
      })
    }
  },
  { immediate: true, deep: true },
)

// --- Methods ---
const resetFormValidation = () => {
  serverFormRef.value?.clearValidate()
}

const resetForm = () => {
  Object.assign(formData, initialFormData())
  resetFormValidation()
}

const handleSubmit = async () => {
  if (!serverFormRef.value) return

  try {
    const isValid = await serverFormRef.value.validate()
    if (!isValid) {
      ElMessage.error('Form validation failed. Please check your input.')
      return
    }
    loading.value = true

    const dataToSubmit = { ...formData }
    let res = null

    if (isEditMode.value) {
      res = await updateServer(dataToSubmit)
    } else {
      res = await addServer(dataToSubmit)
    }

    const isSuccess = res?.meta?.success || res?.meta?.statusCode === 200

    if (isSuccess) {
      ElMessage.success(
        res?.meta?.message ||
          (isEditMode.value ? 'Server updated successfully.' : 'Server created successfully.'),
      )
      emit('submit-success')
      emit('update:modelValue', false)
    } else {
      ElMessage.error(res?.meta?.message || 'Operation failed. Please try again.')
    }
  } catch (error) {
    console.error('Server submission failed:', error)
    const errorMsg =
      error?.response?.data?.meta?.message ||
      error?.response?.data?.message ||
      error?.message ||
      'Request exception'
    ElMessage.error(`Operation failed: ${errorMsg}`)
  } finally {
    loading.value = false
  }
}

const handleDialogClosed = () => {
  resetForm()
}
</script>

<style scoped>
/* Scoped styles can be added here if needed */
</style>
