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
      ref="databaseFormRef"
      :model="formData"
      :rules="formRules"
      label-width="150px"
      v-loading="loading"
      scroll-to-error
    >
      <el-form-item label="Name" prop="dataSourceName">
        <el-input v-model="formData.dataSourceName" placeholder="Enter database name" />
      </el-form-item>
      <el-form-item label="Type" prop="sourceType">
        <el-select v-model="formData.sourceType" placeholder="Select database type">
          <el-option label="Oracle" value="1"></el-option>
          <el-option label="MySQL" value="2"></el-option>
          <el-option label="Mongo" value="6"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="IP Address" prop="ip">
        <el-input v-model="formData.ip" placeholder="Enter IPv4 address" />
      </el-form-item>
      <el-form-item label="Port" prop="port">
        <el-input v-model="formData.port" placeholder="Enter port number" />
      </el-form-item>
      <el-form-item label="Database/Instance Name" prop="dateBaseName">
        <el-input
          v-model="formData.dateBaseName"
          placeholder="Enter database, instance, or service name"
        />
      </el-form-item>
      <el-form-item label="Username" prop="user">
        <el-input v-model="formData.user" placeholder="Enter database username" />
      </el-form-item>
      <el-form-item label="Password" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="Enter database password"
          show-password
        />
      </el-form-item>
      <el-form-item label="URL" prop="url">
        <el-input v-model="formData.url" placeholder="Enter connection URL" />
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
import {
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption,
  ElButton,
  ElMessage,
} from 'element-plus'
import { addDatabase, updateDatabase } from '@/api/database'

const props = defineProps({
  modelValue: Boolean,
  databaseData: { type: Object, default: null },
})

const emit = defineEmits(['update:modelValue', 'submit-success'])

const databaseFormRef = ref(null)
const loading = ref(false)

const initialFormData = () => ({
  id: null,
  dataSourceName: '',
  sourceType: null,
  ip: '',
  port: '',
  dateBaseName: '',
  user: '',
  password: '',
  driver: '',
  url: '',
  whetherMonitoring: '1',
})

const formData = reactive(initialFormData())

const formRules = reactive({
  dataSourceName: [{ required: true, message: 'Please enter a database name', trigger: 'blur' }],
  sourceType: [{ required: true, message: 'Please select a database type', trigger: 'change' }],
  ip: [
    { required: true, message: 'Please enter an IPv4 address', trigger: 'blur' },
    {
      pattern:
        /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/,
      message: 'Please enter a valid IPv4 address',
      trigger: 'blur',
    },
  ],
  port: [
    { required: true, message: 'Please enter a port number', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value === '' || value === null) {
          callback(new Error('Please enter a port number'))
        } else {
          const portNum = Number(value)
          if (!Number.isInteger(portNum) || portNum < 0 || portNum > 65535) {
            callback(new Error('The port must be an integer between 0 and 65535'))
          } else {
            callback()
          }
        }
      },
      trigger: 'blur',
    },
  ],
  user: [{ required: true, message: 'Please enter a username', trigger: 'blur' }],
  password: [{ required: true, message: 'Please enter a password', trigger: 'blur' }],
  dateBaseName: [
    {
      required: true,
      message: 'Please enter the database, instance, or service name',
      trigger: 'blur',
    },
  ],
  url: [{ required: true, message: 'Please enter the connection URL', trigger: 'blur' }],
})

// --- Computed Properties ---
const isEditMode = computed(() => !!props.databaseData?.id)
const modalTitle = computed(() => (isEditMode.value ? 'Edit Database' : 'Add Database'))
const submitButtonText = computed(() => (isEditMode.value ? 'Save Changes' : 'Create'))

// --- Watchers ---
watch(
  () => props.databaseData,
  (newData) => {
    if (props.modelValue) {
      resetFormValidation()
      if (newData && newData.id) {
        // Edit mode
        nextTick(() => {
          Object.keys(formData).forEach((key) => {
            if (
              key !== 'whetherMonitoring' &&
              newData[key] !== undefined &&
              newData[key] !== null
            ) {
              formData[key] = newData[key]
            } else if (key !== 'whetherMonitoring') {
              formData[key] = initialFormData()[key]
            }
          })
          formData.whetherMonitoring = '1'
          updateDriverBasedOnSourceType(formData.sourceType)
        })
      } else {
        // Add mode
        Object.assign(formData, initialFormData())
      }
    }
  },
  { immediate: true, deep: true },
)

watch(
  () => formData.sourceType,
  (newType) => {
    updateDriverBasedOnSourceType(newType)
  },
)

// --- Methods ---
const resetFormValidation = () => {
  databaseFormRef.value?.clearValidate()
}

const updateDriverBasedOnSourceType = (type) => {
  if (type === '1') {
    // Oracle
    formData.driver = 'oracle.jdbc.driver.OracleDriver'
  } else if (type === '2') {
    // MySQL
    formData.driver = 'com.mysql.jdbc.Driver'
  } else if (type === '6') {
    // Mongo
    formData.driver = 'com.mongodb.client.MongoDriver' // Corrected driver for Mongo
  } else {
    formData.driver = ''
  }
  console.log('Driver updated based on sourceType:', formData.driver)
}

const resetForm = () => {
  Object.assign(formData, initialFormData())
  resetFormValidation()
}

const handleSubmit = async () => {
  if (!databaseFormRef.value) return

  try {
    const isValid = await databaseFormRef.value.validate()
    if (!isValid) {
      ElMessage.error('Form validation failed. Please check your input.')
      return
    }
    loading.value = true

    const dataToSubmit = {
      id: formData.id,
      sourceType: formData.sourceType,
      port: formData.port,
      user: formData.user,
      password: formData.password,
      driver: formData.driver,
      url: formData.url,
      whetherMonitoring: formData.whetherMonitoring,
      name: formData.dataSourceName, // Map to 'name'
      ipv4: formData.ip, // Map to 'ipv4'
      databaseName: formData.dateBaseName, // Map to 'databaseName'
    }

    console.log(
      'Data to be submitted to the backend (mapped):',
      JSON.parse(JSON.stringify(dataToSubmit)),
    )

    if (isEditMode.value) {
      await updateDatabase(dataToSubmit)
      ElMessage.success('Database updated successfully.')
    } else {
      await addDatabase(dataToSubmit)
      ElMessage.success('Database created successfully.')
    }
    emit('submit-success')
    emit('update:modelValue', false)
  } catch (error) {
    console.error('Database submission failed:', error)
  } finally {
    loading.value = false
  }
}

const handleDialogClosed = () => {
  resetForm()
  console.log('Database dialog closed and form reset.')
}
</script>

<style scoped>
.el-select {
  width: 100%;
}
</style>
