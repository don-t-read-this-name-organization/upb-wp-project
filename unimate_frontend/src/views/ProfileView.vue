<script setup lang="ts">
import { ref } from 'vue'
import { useAppStore } from '@/stores/appStore'
import { useI18n } from 'vue-i18n'

const store = useAppStore()
const { t } = useI18n()

const showPasswordModal = ref(false)
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const passwordError = ref('')
const passwordLoading = ref(false)

const openPasswordModal = () => {
  oldPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  passwordError.value = ''
  showPasswordModal.value = true
}

const closePasswordModal = () => {
  showPasswordModal.value = false
}

const changePassword = async () => {
  passwordError.value = ''
  
  if (!oldPassword.value || !newPassword.value || !confirmPassword.value) {
    passwordError.value = t('profile.passwordIncorrect')
    return
  }
  
  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = t('profile.passwordMismatch')
    return
  }
  
  passwordLoading.value = true
  
  try {
    const response = await fetch(`/api/users/${store.user?.id}/change-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        oldPassword: oldPassword.value,
        newPassword: newPassword.value,
      }),
    })
    
    if (response.ok) {
      alert(t('profile.passwordChanged'))
      closePasswordModal()
    } else {
      const error = await response.json()
      passwordError.value = error.error || t('profile.passwordIncorrect')
    }
  } catch (error) {
    passwordError.value = t('profile.passwordIncorrect')
  } finally {
    passwordLoading.value = false
  }
}

const getAuthHeaders = () => {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  }
}
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <h2 class="card-title">
        <i class="fas fa-user-circle"></i> {{ t('menu.myProfile') }}
      </h2>
      
      <div class="profile-info">
        <div class="info-row">
          <span class="info-label">{{ t('profile.firstName') }}:</span>
          <span class="info-value">{{ store.userFirstName || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">{{ t('profile.lastName') }}:</span>
          <span class="info-value">{{ store.userLastName || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">{{ t('profile.email') }}:</span>
          <span class="info-value">{{ store.userEmail || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">{{ t('profile.username') }}:</span>
          <span class="info-value">{{ store.userName || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">{{ t('profile.role') }}:</span>
          <span class="info-value">{{ store.userRole || '-' }}</span>
        </div>
        <div v-if="store.userFaculty" class="info-row">
          <span class="info-label">{{ t('profile.faculty') }}:</span>
          <span class="info-value">{{ store.userFaculty }}</span>
        </div>
        <div v-if="store.userGroup" class="info-row">
          <span class="info-label">{{ t('profile.group') }}:</span>
          <span class="info-value">{{ store.userGroup }}</span>
        </div>
      </div>
      
      <button class="btn btn-secondary" @click="openPasswordModal" style="margin-top: 1.5rem;">
        <i class="fas fa-key"></i> {{ t('profile.changePassword') }}
      </button>
    </div>
    
    <div v-if="showPasswordModal" class="modal-overlay" @click.self="closePasswordModal">
      <div class="modal-content">
        <h3>{{ t('profile.changePassword') }}</h3>
        
        <div class="form-group">
          <label>{{ t('profile.oldPassword') }}</label>
          <input type="password" v-model="oldPassword" class="form-input" />
        </div>
        
        <div class="form-group">
          <label>{{ t('profile.newPassword') }}</label>
          <input type="password" v-model="newPassword" class="form-input" />
        </div>
        
        <div class="form-group">
          <label>{{ t('profile.confirmPassword') }}</label>
          <input type="password" v-model="confirmPassword" class="form-input" />
        </div>
        
        <p v-if="passwordError" class="error-text">{{ passwordError }}</p>
        
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="closePasswordModal" :disabled="passwordLoading">
            {{ t('common.cancel') }}
          </button>
          <button class="btn btn-primary" @click="changePassword" :disabled="passwordLoading">
            {{ passwordLoading ? '...' : t('profile.changePassword') }}
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.profile-info {
  margin-top: 1.5rem;
}

.info-row {
  display: flex;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--border-light);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 600;
  color: var(--text-muted);
  min-width: 150px;
}

.info-value {
  color: var(--text-color);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: var(--bg-color);
  padding: 2rem;
  border-radius: 8px;
  min-width: 400px;
  max-width: 90%;
}

.modal-content h3 {
  margin-bottom: 1.5rem;
  color: var(--text-color);
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: var(--text-color);
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background: var(--bg-color);
  color: var(--text-color);
  font-size: 1rem;
}

.form-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.error-text {
  color: #e74c3c;
  margin: 1rem 0;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}
</style>
