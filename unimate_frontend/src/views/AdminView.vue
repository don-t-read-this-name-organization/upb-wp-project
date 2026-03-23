<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

interface User {
  id: number
  username: string
  email: string
  role: 'ADMIN' | 'CHIEF' | 'STUDENT' | 'VISITOR'
}

interface UserForm {
  username: string
  email: string
  password: string
  role: 'ADMIN' | 'CHIEF' | 'STUDENT' | 'VISITOR'
}

const users = ref<User[]>([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const editingUser = ref<User | null>(null)
const form: UserForm = ref({
  username: '',
  email: '',
  password: '',
  role: 'STUDENT',
})

const hasAdmin = computed(() => users.value.some(u => u.role === 'ADMIN'))

const stats = computed(() => ({
  total: users.value.length,
  students: users.value.filter((u) => u.role === 'STUDENT').length,
  chiefs: users.value.filter((u) => u.role === 'CHIEF').length,
  admins: users.value.filter((u) => u.role === 'ADMIN').length,
}))

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
      return 'Admin'
    case 'CHIEF':
      return 'Sef de Grupa'
    case 'STUDENT':
      return 'Student'
    default:
      return role
  }
}

const fetchUsers = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const response = await fetch('/api/users', {
      headers: {
        Authorization: `Basic ${token}`,
      },
    })
    if (!response.ok) throw new Error('Failed to fetch users')
    users.value = await response.json()
  } catch {
    error.value = 'Failed to load users. Is the backend running?'
  } finally {
    loading.value = false
  }
}

const openAddModal = () => {
  editingUser.value = null
  form.value = { username: '', email: '', password: '', role: 'STUDENT' }
  showModal.value = true
}

const openEditModal = (user: User) => {
  editingUser.value = user
  form.value = {
    username: user.username,
    email: user.email,
    password: '',
    role: user.role,
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingUser.value = null
  form.value = { username: '', email: '', password: '', role: 'STUDENT' }
}

const handleSubmit = async () => {
  error.value = ''
  const token = localStorage.getItem('token')
  const url = editingUser.value ? `/api/users/${editingUser.value.id}` : '/api/users'
  const method = editingUser.value ? 'PUT' : 'POST'

  try {
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Basic ${token}`,
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
    error.value = err instanceof Error ? err.message : 'Operation failed'
  }
}

const deleteUser = async (user: User) => {
  if (!confirm(`Are you sure you want to delete user "${user.username}"?`)) return
  error.value = ''
  const token = localStorage.getItem('token')
  try {
    const response = await fetch(`/api/users/${user.id}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Basic ${token}`,
      },
    })
    if (!response.ok) throw new Error('Delete failed')
    await fetchUsers()
  } catch {
    error.value = 'Failed to delete user'
  }
}

onMounted(fetchUsers)
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <div class="card-header">
        <div>
          <h2 class="card-title"><i class="fas fa-user-shield"></i> Admin Panel</h2>
          <p class="admin-subtitle">Manage users and system settings</p>
        </div>
        <button class="btn btn-primary" @click="openAddModal">
          <i class="fas fa-plus"></i> Add User
        </button>
      </div>

      <div v-if="error" class="error-alert">
        <i class="fas fa-exclamation-circle"></i> {{ error }}
      </div>

      <div class="admin-section">
        <h3 class="section-title">User Management</h3>
        <div v-if="loading" class="loading-state">
          <i class="fas fa-spinner fa-spin"></i> Loading users...
        </div>
        <table v-else class="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>
                <span :class="['user-badge', getRoleBadgeClass(user.role)]">
                  {{ getRoleLabel(user.role) }}
                </span>
              </td>
              <td class="actions-cell">
                <button class="btn-icon" title="Edit" @click="openEditModal(user)">
                  <i class="fas fa-edit"></i>
                </button>
                <button class="btn-icon delete" title="Delete" @click="deleteUser(user)">
                  <i class="fas fa-trash"></i>
                </button>
              </td>
            </tr>
            <tr v-if="users.length === 0">
              <td colspan="5" class="empty-state">No users found</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="admin-section">
        <h3 class="section-title">System Statistics</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <i class="fas fa-users stat-icon"></i>
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">Total Users</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-user-graduate stat-icon"></i>
            <div class="stat-value">{{ stats.students }}</div>
            <div class="stat-label">Students</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-clipboard-list stat-icon"></i>
            <div class="stat-value">{{ stats.chiefs }}</div>
            <div class="stat-label">Sefi de Grupa</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-user-shield stat-icon"></i>
            <div class="stat-value">{{ stats.admins }}</div>
            <div class="stat-label">Admins</div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingUser ? 'Edit User' : 'Add New User' }}</h3>
          <button class="modal-close" @click="closeModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="username">Username</label>
            <input
              v-model="form.username"
              type="text"
              id="username"
              class="form-control"
              required
            />
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input
              v-model="form.email"
              type="email"
              id="email"
              class="form-control"
              required
            />
          </div>
          <div class="form-group">
            <label for="password">{{ editingUser ? 'New Password (leave blank to keep)' : 'Password' }}</label>
            <input
              v-model="form.password"
              type="password"
              id="password"
              class="form-control"
              :required="!editingUser"
            />
          </div>
          <div class="form-group">
            <label for="role">Role</label>
            <select v-model="form.role" id="role" class="form-control">
              <option value="STUDENT">Student</option>
              <option value="CHIEF">Sef de Grupa</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">Cancel</button>
            <button type="submit" class="btn btn-primary">
              {{ editingUser ? 'Save Changes' : 'Add User' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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
  margin-bottom: 1.5rem;
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

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.form-hint {
  display: block;
  color: var(--text-muted);
  font-size: 0.8rem;
  margin-top: 0.25rem;
}

</style>
