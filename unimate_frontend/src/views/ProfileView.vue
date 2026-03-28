<script setup lang="ts">
import { useAppStore } from '@/stores/appStore'
import { useI18n } from 'vue-i18n'

const store = useAppStore()
const { t } = useI18n()
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
</style>
