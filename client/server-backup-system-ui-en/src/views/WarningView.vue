<template>
  <div class="warning-view">
    <h2>Notification Settings</h2>

    <!-- Email Notification Settings -->
    <el-card class="box-card email-settings-card">
      <template #header>
        <div class="card-header">
          <span>Email Notification Settings</span>
        </div>
      </template>
      <el-form :model="emailSettings" label-width="180px" v-loading="loading">
        <el-form-item label="Enable Failure Alerts">
          <el-switch
            v-model="emailSettings.EMAIL_NOTIFICATION_ENABLED"
            active-value="true"
            inactive-value="false"
            @change="saveSettings(emailSettings)"
          />
        </el-form-item>
        <el-form-item label="Recipient Email" prop="NOTIFICATION_RECIPIENT">
          <el-input
            v-model="emailSettings.NOTIFICATION_RECIPIENT"
            placeholder="Email address for receiving alerts"
          />
        </el-form-item>
        <el-form-item>
          <template #label>
            <span>SMTP Server Address</span>
            <el-tooltip content="e.g., smtp.gmail.com or smtp.office365.com" placement="top">
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="emailSettings.SPRING_MAIL_HOST" placeholder="e.g., smtp.gmail.com" />
        </el-form-item>
        <el-form-item label="SMTP Server Port" prop="SPRING_MAIL_PORT">
          <el-input
            v-model="emailSettings.SPRING_MAIL_PORT"
            placeholder="Usually 465 (SSL) or 587 (TLS)"
          />
        </el-form-item>
        <el-form-item label="Sender Email Account" prop="SPRING_MAIL_USERNAME">
          <el-input
            v-model="emailSettings.SPRING_MAIL_USERNAME"
            placeholder="The email account for SMTP server authentication"
          />
        </el-form-item>
        <el-form-item label="App Password / Auth Code" prop="SPRING_MAIL_PASSWORD">
          <el-input
            v-model="emailSettings.SPRING_MAIL_PASSWORD"
            type="password"
            show-password
            placeholder="Note: This is the auth code, not your email password"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings(emailSettings)"
            >Save Email Configuration</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>

    <!-- SMS Notification Settings -->
    <el-card class="box-card phone-settings-card">
      <template #header>
        <div class="card-header">
          <span>SMS Notification Settings</span>
        </div>
      </template>
      <el-form :model="phoneSettings" label-width="180px" v-loading="loading">
        <el-form-item label="Enable Failure Alerts">
          <el-switch
            v-model="phoneSettings.PHONE_NOTIFICATION_ENABLED"
            active-value="true"
            inactive-value="false"
            @change="saveSettings(phoneSettings)"
          />
        </el-form-item>
        <el-form-item label="Recipient Phone Number">
          <el-input
            v-model="phoneSettings.NOTIFICATION_PHONE_NUMBER"
            placeholder="Enter the phone number for receiving alerts"
            clearable
          >
            <template #prepend>+852</template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings(phoneSettings)"
            >Save SMS Configuration</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Feature Toggles (Simulated) -->
    <el-card class="box-card warning-switch-card">
      <template #header>
        <div class="card-header">
          <span>Feature Toggles</span>
        </div>
      </template>
      <div class="warning-item">
        <span>Database Backup Failure Alerts</span>
        <el-switch
          v-model="databaseWarningEnabled"
          active-text="On"
          inactive-text="Off"
          @change="handleDatabaseWarningChange"
        />
      </div>
      <el-divider />
      <div class="warning-item">
        <span>Server Backup Failure Alerts</span>
        <el-switch
          v-model="serverWarningEnabled"
          active-text="On"
          inactive-text="Off"
          @change="handleServerWarningChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  ElMessage,
  ElCard,
  ElInput,
  ElButton,
  ElSwitch,
  ElForm,
  ElFormItem,
  ElIcon,
  ElTooltip,
  ElDivider,
} from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'
import { getSystemSettingsApi, saveSystemSettingsApi } from '@/api/system'

// --- Reactive State ---
const databaseWarningEnabled = ref(false)
const serverWarningEnabled = ref(false)

// --- Configuration State ---
const loading = ref(false)

// Email settings
const emailSettings = ref({
  EMAIL_NOTIFICATION_ENABLED: 'false',
  NOTIFICATION_RECIPIENT: '',
  SPRING_MAIL_HOST: '',
  SPRING_MAIL_PORT: '465',
  SPRING_MAIL_USERNAME: '',
  SPRING_MAIL_PASSWORD: '',
})

// SMS settings
const phoneSettings = ref({
  PHONE_NOTIFICATION_ENABLED: 'false',
  NOTIFICATION_PHONE_NUMBER: '',
})

onMounted(() => {
  fetchSystemSettings()
})

const fetchSystemSettings = async () => {
  loading.value = true
  try {
    const res = await getSystemSettingsApi()
    if (res.meta.statusCode === 200 && res.data) {
      Object.assign(emailSettings.value, res.data)
      Object.assign(phoneSettings.value, res.data)
    }
  } catch (error) {
    ElMessage.error('Failed to fetch system settings.')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const saveSettings = async (settingsToSave) => {
  if (
    settingsToSave.NOTIFICATION_PHONE_NUMBER !== undefined &&
    !settingsToSave.NOTIFICATION_PHONE_NUMBER
  ) {
    ElMessage.warning('Please enter a phone number.')
    return
  }

  loading.value = true
  try {
    await saveSystemSettingsApi(settingsToSave)
    ElMessage.success('Settings saved successfully!')
  } catch (error) {
    ElMessage.error('Failed to save settings.')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// --- Simulated feature handlers ---
const handleDatabaseWarningChange = (newValue) => {
  console.log(`Database backup failure alert status changed to: ${newValue ? 'On' : 'Off'}`)
  ElMessage.info(`Database backup alerts have been ${newValue ? 'enabled' : 'disabled'}.`)
}

const handleServerWarningChange = (newValue) => {
  console.log(`Server backup failure alert status changed to: ${newValue ? 'On' : 'Off'}`)
  ElMessage.info(`Server backup alerts have been ${newValue ? 'enabled' : 'disabled'}.`)
}
</script>

<style scoped>
/* Styles remain the same */
.warning-view {
  padding: 20px;
}
h2 {
  margin-bottom: 25px;
  font-size: 1.5em;
  color: #303133;
}
.box-card {
  margin-bottom: 25px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
.warning-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  font-size: 14px;
  color: #606266;
}
.el-divider--horizontal {
  margin: 0;
}
:deep(.el-switch__label) {
  color: #606266;
}
:deep(.el-switch__label.is-active) {
  color: var(--el-color-primary);
}
</style>
