<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/appStore'

const router = useRouter()
const store = useAppStore()
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true

  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value, password: password.value }),
    })

    const data = await response.json()

    if (response.ok) {
      const token = btoa(`${email.value}:${password.value}`)
      localStorage.setItem('token', token)
      store.user = {
        name: data.user.username,
        role: data.user.role,
        id: data.user.id,
      }
      localStorage.setItem('user', JSON.stringify(store.user))
      router.push('/')
    } else {
      error.value = data.error || 'Login failed'
    }
  } catch {
    error.value = 'Connection error. Is the backend running?'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="main-content flex-center">
    <div class="card login-container fade-in">
      <h2 class="card-title text-center">
        <i class="fas fa-sign-in-alt"></i>
        <span>Login</span>
      </h2>

      <form id="loginForm" @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">Email</label>
          <input
            v-model="email"
            type="email"
            id="email"
            class="form-control"
            required
            placeholder="Enter your email"
          />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input
            v-model="password"
            type="password"
            id="password"
            class="form-control"
            required
            placeholder="Enter your password"
          />
        </div>

        <button type="submit" class="btn btn-primary btn-full-width" :disabled="loading">
          <i class="fas fa-sign-in-alt"></i>
          <span>{{ loading ? 'Logging in...' : 'Login' }}</span>
        </button>

        <p v-if="error" class="error-message">{{ error }}</p>
      </form>

      <div class="demo-accounts-divider">
        <p class="demo-accounts-title">
          <strong>Demo Accounts:</strong>
        </p>
        <div class="demo-accounts-box">
          <p><strong>Admin:</strong> admin@unimate.ro / admin123</p>
          <p><strong>Student:</strong> student@unimate.ro / student123</p>
          <p><strong>Sef de Grupa:</strong> sef@unimate.ro / sef123</p>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.login-container {
  max-width: 450px;
  margin: 2rem auto;
  padding: 2.5rem;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 150px);
}

.demo-accounts-divider {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px dashed var(--border-color);
}

.demo-accounts-box {
  background: var(--bg-secondary);
  padding: 1rem;
  border-radius: 8px;
  font-size: 0.85rem;
  line-height: 1.6;
}

.btn-full-width {
  width: 100%;
  margin-top: 1rem;
}

.fade-in {
  animation: fadeIn 0.5s ease-in-out;
}

.error-message {
  color: var(--color-error, #dc3545);
  text-align: center;
  margin-top: 1rem;
  padding: 0.5rem;
  background: rgba(220, 53, 69, 0.1);
  border-radius: 4px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
