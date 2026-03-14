<script setup lang="ts">
import { ref } from 'vue'

interface User {
  id: number
  username: string
  email: string
  role: 'admin' | 'sef' | 'student'
  group?: string
}

const users = ref<User[]>([
  { id: 1, username: 'admin', email: 'admin@unimate.ro', role: 'admin' },
  { id: 2, username: 'student', email: 'student@unimate.ro', role: 'student', group: '1231EB' },
  { id: 3, username: 'sef', email: 'sef@unimate.ro', role: 'sef', group: '1231EB' },
])

const getRoleBadgeClass = (role: string) => {
  switch (role) {
    case 'admin':
      return 'badge-admin'
    case 'sef':
      return 'badge-sef'
    default:
      return 'badge-student'
  }
}
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <h2 class="card-title"><i class="fas fa-user-shield"></i> Admin Panel</h2>
      <p class="admin-subtitle">Manage users and system settings</p>

      <div class="admin-section">
        <h3 class="section-title">User Management</h3>
        <table class="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Group</th>
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
                  {{ user.role }}
                </span>
              </td>
              <td>{{ user.group || '—' }}</td>
              <td>
                <button class="btn-icon" title="Edit">
                  <i class="fas fa-edit"></i>
                </button>
                <button class="btn-icon delete" title="Delete">
                  <i class="fas fa-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="admin-section">
        <h3 class="section-title">System Statistics</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <i class="fas fa-users stat-icon"></i>
            <div class="stat-value">3</div>
            <div class="stat-label">Total Users</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-user-graduate stat-icon"></i>
            <div class="stat-value">1</div>
            <div class="stat-label">Students</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-clipboard-list stat-icon"></i>
            <div class="stat-value">12</div>
            <div class="stat-label">Active Tasks</div>
          </div>
          <div class="stat-card">
            <i class="fas fa-sticky-note stat-icon"></i>
            <div class="stat-value">8</div>
            <div class="stat-label">Notes</div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.admin-subtitle {
  color: var(--text-muted);
  margin-bottom: 2rem;
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
  text-transform: capitalize;
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
</style>
