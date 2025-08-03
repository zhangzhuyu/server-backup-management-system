<template>
  <div class="login-page-wrapper">
    <div class="login-box">
      <h2>Server Backup Management System</h2>
      <p class="subtitle">Please Sign In</p>
      <div class="login-form">
        <div class="form-item">
          <label for="username">Username:</label>
          <input
            type="text"
            id="username"
            v-model="usernameInput"
            placeholder="Enter your username"
            :disabled="isLoading"
          />
        </div>
        <div class="form-item">
          <label for="password">Password:</label>
          <input
            type="password"
            id="password"
            v-model="passwordInput"
            placeholder="Enter your password"
            @keyup.enter="handleLogin"
            :disabled="isLoading"
          />
        </div>
        <!-- Loading or error message -->
        <p v-if="isLoading" class="loading-message">Signing in, please wait...</p>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <button @click="handleLogin" :disabled="isLoading">
          {{ isLoading ? 'Signing In...' : 'Sign In' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const usernameInput = ref('')
const passwordInput = ref('')
const isLoading = ref(false)
const errorMessage = ref('')
const authStore = useAuthStore()
const router = useRouter()

const handleLogin = () => {
  errorMessage.value = ''

  if (usernameInput.value !== 'admin' || passwordInput.value !== '123456') {
    errorMessage.value = 'Invalid username or password!'
    passwordInput.value = ''
    return
  }

  isLoading.value = true

  setTimeout(() => {
    console.log('Simulated loading complete.')

    sessionStorage.setItem('fake_token', 'admin_logged_in')
    console.log('Login token (fake_token) has been set in sessionStorage.')

    authStore.login(usernameInput.value)
    console.log('Pinia store state has been updated.')

    isLoading.value = false
    router.push('/')
    console.log('Redirecting to homepage (/).')
  }, 1000)
}
</script>

<style scoped>
/* Styles remain unchanged */
.login-page-wrapper {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #e6f7f4 0%, #b9e7d5 100%);
  padding: 0 10%;
  box-sizing: border-box;
}

.login-box {
  background-color: white;
  padding: 40px 50px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  width: 400px;
  text-align: center;
  transition: transform 0.3s ease-out;
}

.login-box:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

h2 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
  font-size: 1.8em;
}

.subtitle {
  margin-bottom: 30px;
  color: #666;
  font-size: 1.1em;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  text-align: left;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

label {
  font-weight: bold;
  color: #555;
  font-size: 0.9em;
}

input[type='text'],
input[type='password'] {
  padding: 12px 15px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1em;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

input[type='text']:focus,
input[type='password']:focus {
  border-color: #4caf50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.2);
  outline: none;
}

button {
  padding: 12px 20px;
  background: linear-gradient(to right, #66bb6a, #43a047);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1.1em;
  font-weight: bold;
  transition:
    background 0.3s ease,
    box-shadow 0.3s ease;
  margin-top: 10px;
}

button:hover:not(:disabled) {
  background: linear-gradient(to right, #57a85a, #3a8d3d);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

button:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.loading-message,
.error-message {
  text-align: center;
  margin: 10px 0 0 0;
  padding: 8px;
  border-radius: 4px;
}

.loading-message {
  color: #333;
  background-color: #e0e0e0;
}

.error-message {
  color: #d32f2f;
  background-color: #ffcdd2;
  font-weight: bold;
}
</style>
