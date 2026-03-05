<script setup lang="ts">
defineProps<{
  prof: {
    name: string;
    title: string;
    email: string;
    phone?: string;
    hours: string;
    room: string;
    color: string;
    initials: string;
  }
}>()
</script>

<template>
  <div class="prof-card">
    <img 
      :src="`https://via.placeholder.com/100/${prof.color}/ffffff?text=${prof.initials}`" 
      class="prof-photo" 
      :alt="prof.name"
    >
    <div class="prof-name">{{ prof.name }}</div>
    <div class="prof-title">{{ prof.title }}</div>
    <div class="prof-contact">
      <a :href="`mailto:${prof.email}`">
        <i class="fas fa-envelope"></i> {{ prof.email }}
      </a>
      <a v-if="prof.phone" :href="`tel:${prof.phone.replace(/\s/g, '')}`">
        <i class="fas fa-phone"></i> {{ prof.phone }}
      </a>
      <div class="office-hours">
        🕐 {{ prof.hours }}<br>
        📍 {{ prof.room }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.prof-card {
  min-width: 220px;
  max-width: 220px;
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  box-shadow: var(--shadow);
  transition: var(--transition);
  border: 2px solid transparent;
}

.prof-card:hover {
  transform: translateY(-5px);
  border-color: var(--primary-color);
}

.prof-photo {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 auto 1rem;
  border: 3px solid var(--primary-color);
  background: var(--bg-secondary);
}

.prof-name {
  font-weight: 600;
  margin: 0.5rem 0;
  color: var(--text-color);
}

.prof-title {
  font-size: 0.85rem;
  color: var(--text-muted);
  margin-bottom: 0.75rem;
}

.prof-contact {
  font-size: 0.85rem;
}

.prof-contact a {
  color: var(--primary-color);
  text-decoration: none;
  display: block;
  margin: 0.25rem 0;
}

.office-hours {
  font-size: 0.85rem;
  color: var(--text-muted);
  background: var(--bg-secondary);
  padding: 0.75rem;
  border-radius: 5px;
  margin-top: 0.5rem;
}
</style>