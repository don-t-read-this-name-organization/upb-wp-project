<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/appStore'
<<<<<<< Updated upstream
=======
import BaseModal from '@/components/BaseModal.vue'
>>>>>>> Stashed changes

const router = useRouter()
const { t } = useI18n()
const store = useAppStore()

interface User {
  id: number
  username: string
  email: string
  role: 'ADMIN' | 'CHIEF' | 'STUDENT' | 'VISITOR'
  firstName?: string
  lastName?: string
  faculty?: { name: string; shortName?: string }
  group?: { name: string }
  createdAt?: string
}

interface UserForm {
  username: string
  email: string
  password: string
  role: 'ADMIN' | 'CHIEF' | 'STUDENT' | 'VISITOR'
  firstName?: string
  lastName?: string
}

interface Quote {
  id: number
  text: string
  author: string
}

const users = ref<User[]>([])
const pendingQuotes = ref<Quote[]>([])
const pendingRegistrations = ref<User[]>([])
const loading = ref(false)
const pendingLoading = ref(false)
const searchQuery = ref('')
const quotesLoading = ref(false)
const error = ref('')
const showModal = ref(false)
const editingUser = ref<User | null>(null)
const form = ref<UserForm>({
  username: '',
  email: '',
  password: '',
  role: 'STUDENT',
  firstName: '',
  lastName: '',
})

const showConfirmModal = ref(false)
const confirmMessage = ref('')
const confirmCallback = ref<(() => void) | null>(null)

const showAlertModal = ref(false)
const alertMessage = ref('')

function showConfirm(message: string, callback: () => void) {
  confirmMessage.value = message
  confirmCallback.value = callback
  showConfirmModal.value = true
}

function handleConfirm() {
  if (confirmCallback.value) {
    confirmCallback.value()
  }
  showConfirmModal.value = false
  confirmCallback.value = null
}

function handleConfirmCancel() {
  showConfirmModal.value = false
  confirmCallback.value = null
}

function showAlert(message: string) {
  alertMessage.value = message
  showAlertModal.value = true
}

const stats = computed(() => ({
  total: users.value.length,
  students: users.value.filter((u) => u.role === 'STUDENT').length,
  chiefs: users.value.filter((u) => u.role === 'CHIEF').length,
  admins: users.value.filter((u) => u.role === 'ADMIN').length,
}))

const filteredUsers = computed(() => {
  const currentUserId = store.user?.id
  let result = users.value.filter((u) => u.id !== currentUserId)
  
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter((u) => 
      u.username.toLowerCase().includes(query) ||
      u.email.toLowerCase().includes(query) ||
      (u.firstName && u.firstName.toLowerCase().includes(query)) ||
      (u.lastName && u.lastName.toLowerCase().includes(query)) ||
      (u.faculty?.name && u.faculty.name.toLowerCase().includes(query)) ||
      (u.group?.name && u.group.name.toLowerCase().includes(query))
    )
  }
  
  return result
})

const getRoleBadgeClass = (role: string) => {
  switch (role) {
    case 'ADMIN':
      return 'badge-admin'
    case 'CHIEF':
      return 'badge-sef'
    default:
      return 'badge-student'
  }
}

const getRoleLabel = (role: string) => {
  switch (role) {
    case 'ADMIN':
      return t('admin.roles.admin')
    case 'CHIEF':
      return t('admin.roles.chief')
    case 'STUDENT':
      return t('admin.roles.student')
    default:
      return role
  }
}

const fetchUsers = async () => {
  loading.value = true
  error.value = ''
  try {
    // const response = await fetch('/api/users', {
    //   headers: {
    //     Authorization: `Basic ${token}`,
    //   },
    // })
    const response = await fetch('/api/users')
    if (!response.ok) throw new Error('Failed to fetch users')
    users.value = await response.json()
  } catch {
    error.value = t('common.error')
  } finally {
    loading.value = false
  }
}

const openAddModal = () => {
  editingUser.value = null
  form.value = { username: '', email: '', password: '', role: 'STUDENT', firstName: '', lastName: '' }
  showModal.value = true
}

const openEditModal = (user: User) => {
  editingUser.value = user
  form.value = {
    username: user.username,
    email: user.email,
    password: '',
    role: user.role,
    firstName: user.firstName || '',
    lastName: user.lastName || '',
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingUser.value = null
  form.value = { username: '', email: '', password: '', role: 'STUDENT', firstName: '', lastName: '' }
}

const handleSubmit = async () => {
  error.value = ''
<<<<<<< Updated upstream
  const token = localStorage.getItem('token')
=======
>>>>>>> Stashed changes
  const url = editingUser.value ? `/api/users/${editingUser.value.id}` : '/api/users'
  const method = editingUser.value ? 'PUT' : 'POST'

  try {
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
<<<<<<< Updated upstream
        Authorization: `Basic ${token}`,
=======
>>>>>>> Stashed changes
      },
      body: JSON.stringify(form.value),
    })
    if (!response.ok) {
      const data = await response.json()
      throw new Error(data.message || 'Operation failed')
    }
    closeModal()
    await fetchUsers()
  } catch (err: unknown) {
    error.value = err instanceof Error ? err.message : t('common.error')
  }
}

const deleteUser = async (user: User) => {
  showConfirm(t('common.confirm') + ` "${user.username}"?`, async () => {
    error.value = ''
<<<<<<< Updated upstream
    const token = localStorage.getItem('token')
    try {
      const response = await fetch(`/api/users/${user.id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Basic ${token}`,
        },
=======
    try {
      const response = await fetch(`/api/users/${user.id}`, {
        method: 'DELETE',
>>>>>>> Stashed changes
      })
      if (!response.ok) throw new Error('Delete failed')
      await fetchUsers()
    } catch {
      error.value = t('common.error')
    }
  })
}

const fetchPendingQuotes = async () => {
  quotesLoading.value = true
  try {
    const response = await fetch('/api/quotes/pending')
    if (!response.ok) throw new Error('Failed to fetch pending quotes')
    pendingQuotes.value = await response.json()
  } catch (e) {
    console.error('Failed to fetch pending quotes:', e)
  } finally {
    quotesLoading.value = false
  }
}

const handleApproveQuote = async (quote: Quote) => {
  showConfirm(t('admin.approveConfirm', { author: quote.author }), async () => {
    try {
      const response = await fetch(`/api/quotes/${quote.id}/approve`, {
        method: 'POST',
      })
      if (!response.ok) throw new Error('Failed to approve quote')
      await fetchPendingQuotes()
    } catch {
      showAlert(t('common.error'))
    }
  })
}

const handleRejectQuote = async (quote: Quote) => {
  showConfirm(t('admin.rejectConfirm', { author: quote.author }), async () => {
    try {
      const response = await fetch(`/api/quotes/${quote.id}/reject`, {
        method: 'POST',
      })
      if (!response.ok) throw new Error('Failed to reject quote')
      await fetchPendingQuotes()
    } catch {
      showAlert(t('common.error'))
    }
  })
}

const fetchPendingRegistrations = async () => {
  pendingLoading.value = true
  try {
    const response = await fetch('/api/users/pending')
    if (!response.ok) throw new Error('Failed to fetch pending registrations')
    pendingRegistrations.value = await response.json()
  } catch (e) {
    console.error('Failed to fetch pending registrations:', e)
  } finally {
    pendingLoading.value = false
  }
}

const handleApproveRegistration = async (user: User) => {
  showConfirm(t('admin.approveRegistrationConfirm', { username: user.username }), async () => {
    try {
      const response = await fetch(`/api/users/${user.id}/approve`, {
        method: 'POST',
      })
      if (!response.ok) throw new Error('Failed to approve registration')
      await fetchPendingRegistrations()
      showAlert(t('admin.registrationApproved'))
    } catch {
      showAlert(t('common.error'))
    }
  })
}

const handleRejectRegistration = async (user: User) => {
  showConfirm(t('admin.rejectRegistrationConfirm', { username: user.username }), async () => {
    try {
      const response = await fetch(`/api/users/${user.id}/reject`, {
        method: 'POST',
      })
      if (!response.ok) throw new Error('Failed to reject registration')
      await fetchPendingRegistrations()
    } catch {
      showAlert(t('common.error'))
    }
  })
}

onMounted(() => {
  fetchUsers()
  fetchPendingQuotes()
  fetchPendingRegistrations()
})
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <div class="card-header">
        <div>
          <h2 class="card-title"><i class="fas fa-user-shield"></i> {{ t('admin.title') }}</h2>
          <p class="admin-subtitle">{{ t('admin.subtitle') }}</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-secondary" @click="router.push('/admin/news')">
            <i class="fas fa-newspaper"></i> {{ t('admin.manageNews') }}
          </button>
          <button class="btn btn-secondary" @click="router.push('/admin/quotes')">
            <i class="fas fa-quote-left"></i> {{ t('admin.manageQuotes') }}
          </button>
          <button class="btn btn-primary" @click="openAddModal">
            <i class="fas fa-plus"></i> {{ t('admin.addUser') }}
          </button>
        </div>
      </div>

      <div v-if="error" class="error-alert">
        <i class="fas fa-exclamation-circle"></i> {{ error }}
      </div>

      <div class="admin-section">
        <div class="section-header">
          <h3 class="section-title">{{ t('admin.userManagement') }}</h3>
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search users..."
            />
          </div>
        </div>
        <div v-if="loading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i> {{ t('admin.loadingUsers') }}
        </div>
        <table v-else class="admin-table">
          <thead>
            <tr>
              <th>{{ t('admin.id') }}</th>
              <th>{{ t('admin.username') }}</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>{{ t('admin.email') }}</th>
              <th>Faculty</th>
              <th>Group</th>
              <th>{{ t('admin.role') }}</th>
              <th>{{ t('admin.actions') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in filteredUsers" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.firstName || '-' }}</td>
              <td>{{ user.lastName || '-' }}</td>
              <td>{{ user.email }}</td>
              <td>{{ user.faculty?.shortName || user.faculty?.name || '-' }}</td>
              <td>{{ user.group?.name || '-' }}</td>
              <td>
                <span :class="['user-badge', getRoleBadgeClass(user.role)]">
                  {{ getRoleLabel(user.role) }}
                </span>
              </td>
              <td class="actions-cell">
                <button class="btn-icon" :title="t('common.edit')" @click="openEditModal(user)">
                  <i class="fas fa-edit"></i>
                </button>
                <button class="btn-icon delete" :title="t('common.delete')" @click="deleteUser(user)">
                  <i class="fas fa-trash"></i>
                </button>
              </td>
            </tr>
            <tr v-if="filteredUsers.length === 0">
              <td colspan="9" class="empty-state">{{ t('admin.noUsersFound') }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="admin-section">
        <h3 class="section-title">{{ t('admin.systemStats') }}</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <i class="fas fa-users stat-icon"></i>
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">{{ t('admin.totalUsers') }}</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-user-graduate stat-icon"></i>
            <div class="stat-value">{{ stats.students }}</div>
            <div class="stat-label">{{ t('admin.students') }}</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-clipboard-list stat-icon"></i>
            <div class="stat-value">{{ stats.chiefs }}</div>
            <div class="stat-label">{{ t('admin.chiefs') }}</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-user-shield stat-icon"></i>
            <div class="stat-value">{{ stats.admins }}</div>
            <div class="stat-label">{{ t('admin.admins') }}</div>
          </div>
        </div>
      </div>

      <div class="admin-section">
        <h3 class="section-title">{{ t('admin.pendingQuotes') }}</h3>
        <div v-if="quotesLoading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i> {{ t('admin.loadingQuotes') }}
        </div>
        <div v-else-if="pendingQuotes.length === 0" class="empty-state">
          {{ t('admin.noPendingQuotes') }}
        </div>
        <div v-else class="quotes-grid">
          <div v-for="quote in pendingQuotes" :key="quote.id" class="quote-card">
            <div class="quote-text">{{ quote.text }}</div>
            <div class="quote-author">- {{ quote.author }}</div>
            <div class="quote-actions">
              <button class="btn btn-success btn-sm" @click="handleApproveQuote(quote)">
                <i class="fas fa-check"></i> {{ t('admin.approve') }}
              </button>
              <button class="btn btn-danger btn-sm" @click="handleRejectQuote(quote)">
                <i class="fas fa-times"></i> {{ t('admin.reject') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="admin-section">
        <h3 class="section-title">{{ t('admin.pendingRegistrations') }}</h3>
        <div v-if="pendingLoading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i> {{ t('admin.loadingRegistrations') }}
        </div>
        <div v-else-if="pendingRegistrations.length === 0" class="empty-state">
          {{ t('admin.noPendingRegistrations') }}
        </div>
        <div v-else class="quotes-grid">
          <div v-for="user in pendingRegistrations" :key="user.id" class="quote-card">
            <div class="quote-text">{{ user.firstName }} {{ user.lastName }}</div>
            <div class="quote-author">@{{ user.username }} - {{ user.email }}</div>
            <div class="quote-actions">
              <button class="btn btn-success btn-sm" @click="handleApproveRegistration(user)">
                <i class="fas fa-check"></i> {{ t('admin.approve') }}
              </button>
              <button class="btn btn-danger btn-sm" @click="handleRejectRegistration(user)">
                <i class="fas fa-times"></i> {{ t('admin.reject') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

<<<<<<< Updated upstream
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
=======
    <BaseModal v-model="showModal" size="md" :show-close="false" @close="closeModal">
>>>>>>> Stashed changes
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingUser ? t('admin.editUser') : t('admin.addNewUser') }}</h3>
          <button class="modal-close" @click="closeModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="form-row">
            <div class="form-group">
              <label for="firstName">First Name</label>
              <input
                v-model="form.firstName"
                type="text"
                id="firstName"
                class="form-control"
              />
            </div>
            <div class="form-group">
              <label for="lastName">Last Name</label>
              <input
                v-model="form.lastName"
                type="text"
                id="lastName"
                class="form-control"
              />
            </div>
          </div>
          <div class="form-group">
            <label for="username">{{ t('admin.username') }}</label>
            <input
              v-model="form.username"
              type="text"
              id="username"
              class="form-control"
              required
            />
          </div>
          <div class="form-group">
            <label for="email">{{ t('admin.email') }}</label>
            <input
              v-model="form.email"
              type="email"
              id="email"
              class="form-control"
              required
            />
          </div>
          <div class="form-group">
            <label for="password">{{ editingUser ? t('admin.newPassword') : t('admin.password') }}</label>
            <input
              v-model="form.password"
              type="password"
              id="password"
              class="form-control"
              :required="!editingUser"
            />
          </div>
          <div class="form-group">
            <label for="role">{{ t('admin.role') }}</label>
            <select v-model="form.role" id="role" class="form-control">
              <option value="STUDENT">{{ t('admin.roles.student') }}</option>
              <option value="CHIEF">{{ t('admin.roles.chief') }}</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">{{ t('common.cancel') }}</button>
            <button type="submit" class="btn btn-primary">
              {{ editingUser ? t('admin.saveChanges') : t('admin.addUser') }}
            </button>
          </div>
        </form>
      </div>
<<<<<<< Updated upstream
    </div>

    <div v-if="showConfirmModal" class="modal-overlay" @click.self="handleConfirmCancel">
=======
    </BaseModal>

    <BaseModal v-model="showConfirmModal" size="sm" :show-close="false" @close="handleConfirmCancel">
>>>>>>> Stashed changes
      <div class="modal">
        <div class="modal-header">
          <h3><i class="fas fa-question-circle"></i> {{ t('common.confirm') }}</h3>
          <button class="modal-close" @click="handleConfirmCancel">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>{{ confirmMessage }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="handleConfirmCancel">
            {{ t('common.cancel') }}
          </button>
          <button class="btn btn-danger" @click="handleConfirm">
            {{ t('common.confirm') }}
          </button>
        </div>
      </div>
<<<<<<< Updated upstream
    </div>

    <div v-if="showAlertModal" class="modal-overlay" @click.self="showAlertModal = false">
=======
    </BaseModal>

    <BaseModal v-model="showAlertModal" size="sm" :show-close="false">
>>>>>>> Stashed changes
      <div class="modal">
        <div class="modal-header">
          <h3><i class="fas fa-info-circle"></i> {{ t('common.error') }}</h3>
          <button class="modal-close" @click="showAlertModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>{{ alertMessage }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="showAlertModal = false">
            OK
          </button>
        </div>
      </div>
<<<<<<< Updated upstream
    </div>
=======
    </BaseModal>
>>>>>>> Stashed changes
  </main>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
}

.admin-subtitle {
  color: var(--text-muted);
  margin-top: 0.25rem;
}

.error-alert {
  background: rgba(220, 53, 69, 0.1);
  color: #dc3545;
  padding: 0.75rem 1rem;
  border-radius: var(--radius);
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.loading-state {
  text-align: center;
  padding: 2rem;
  color: var(--text-muted);
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: var(--text-muted);
}

.admin-section {
  margin-bottom: 2.5rem;
}

.section-title {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-light);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-light);
}

.section-header .section-title {
  margin-bottom: 0;
  border-bottom: none;
  padding-bottom: 0;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  background: var(--bg-color);
}

.search-box i {
  color: var(--text-muted);
}

.search-box input {
  border: none;
  background: transparent;
  outline: none;
  width: 200px;
  color: var(--text-color);
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
  background-color: var(--card-bg);
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
}

.admin-table th,
.admin-table td {
  padding: 0.9rem 1.1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
}

.admin-table th {
  background-color: var(--bg-secondary);
  color: var(--text-muted);
  font-weight: 600;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.admin-table tr:last-child td {
  border-bottom: none;
}

.admin-table tr:hover td {
  background-color: var(--bg-secondary);
}

.user-badge {
  display: inline-block;
  padding: 0.2rem 0.7rem;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.03em;
}

.badge-admin {
  background-color: #f5d6d6;
  color: #8b3a3a;
}

.badge-sef {
  background-color: #f5ead6;
  color: #7a5a1a;
}

.badge-student {
  background-color: #d6eadf;
  color: #2a5c3f;
}

.actions-cell {
  white-space: nowrap;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.4rem;
  color: var(--text-muted);
  transition: var(--transition);
}

.btn-icon:hover {
  color: var(--primary-color);
}

.btn-icon.delete:hover {
  color: #dc3545;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1.25rem;
  margin-top: 1rem;
}

.stat-card {
  background: var(--bg-secondary);
  border-radius: var(--radius);
  padding: 1.5rem;
  text-align: center;
  border: 1px solid var(--border-light);
  transition: var(--transition);
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow);
}

.stat-icon {
  font-size: 1.75rem;
  color: var(--primary-color);
  margin-bottom: 0.75rem;
}

.stat-value {
  font-size: 2rem;
  font-weight: 600;
  color: var(--text-color);
}

.stat-label {
  font-size: 0.85rem;
  color: var(--text-muted);
  margin-top: 0.25rem;
}

<<<<<<< Updated upstream
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

=======
>>>>>>> Stashed changes
.modal {
  background: var(--card-bg);
  border-radius: var(--radius);
  width: 90%;
  max-width: 450px;
  box-shadow: var(--shadow);
}

.modal-content {
  background: var(--card-bg);
  border-radius: var(--radius);
  padding: 1.5rem;
  width: 100%;
  max-width: 450px;
  box-shadow: var(--shadow);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--border-light);
}

.modal-header h3 {
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-muted);
  font-size: 1.25rem;
}

.modal-close:hover {
  color: var(--text-color);
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--border-light);
}

.form-hint {
  display: block;
  color: var(--text-muted);
  font-size: 0.8rem;
  margin-top: 0.25rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  background: var(--bg-color);
  color: var(--text-color);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.quotes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.quote-card {
  background: var(--bg-secondary);
  border-radius: var(--radius);
  padding: 1.25rem;
  border: 1px solid var(--border-light);
}

.quote-text {
  font-style: italic;
  color: var(--text-color);
  margin-bottom: 0.75rem;
  line-height: 1.5;
}

.quote-author {
  color: var(--text-muted);
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.quote-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-sm {
  padding: 0.4rem 0.75rem;
  font-size: 0.8rem;
}

.btn-success {
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition);
}

.btn-success:hover {
  background-color: #73a08d;
}

.btn-danger {
  background-color: var(--accent-blush);
  color: white;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition);
}

.btn-danger:hover {
  background-color: #c08080;
}

</style>
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
