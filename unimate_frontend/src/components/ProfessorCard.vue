<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  prof: {
    name: string
    title: string
    email?: string
    phone?: string
    hours?: string
    room?: string
    imageUrl?: string
    color: string
    initials: string
  }
}>()

const DEFAULT_AVATAR_SVG = `data:image/svg+xml;utf8,${encodeURIComponent(`
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#dbeafe" />
      <stop offset="100%" stop-color="#bfdbfe" />
    </linearGradient>
  </defs>
  <rect width="100" height="100" fill="url(#g)" />
  <circle cx="50" cy="38" r="16" fill="#1e40af" opacity="0.9" />
  <path d="M20 86c2-17 14-28 30-28s28 11 30 28" fill="#1e40af" opacity="0.9" />
</svg>
`)}` 

function initialsAvatarSvg(color: string, initials: string): string {
  return `data:image/svg+xml;utf8,${encodeURIComponent(`
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">
  <rect width="100" height="100" fill="#${color}" />
  <text
    x="50"
    y="55"
    text-anchor="middle"
    dominant-baseline="middle"
    fill="#ffffff"
    font-size="34"
    font-family="Arial, sans-serif"
    font-weight="700"
  >
    ${initials || 'P'}
  </text>
</svg>
`)}` 
}

const imageSrc = ref(DEFAULT_AVATAR_SVG)

watch(
  () => props.prof,
  (prof) => {
    imageSrc.value =
      prof.imageUrl && prof.imageUrl.trim().length > 0
        ? prof.imageUrl
        : initialsAvatarSvg(prof.color, prof.initials)
  },
  { immediate: true, deep: true },
)

function onImageError() {
  if (imageSrc.value !== DEFAULT_AVATAR_SVG) {
    imageSrc.value = DEFAULT_AVATAR_SVG
  }
}
</script>

<template>
  <div class="prof-card">
    <img
      :src="imageSrc"
      class="prof-photo"
      :alt="prof.name"
      @error="onImageError"
    />
    <div class="prof-name">{{ prof.name }}</div>
    <div class="prof-title">{{ prof.title }}</div>
    <div class="prof-contact">
      <a v-if="prof.email" :href="`mailto:${prof.email}`"> <i class="fas fa-envelope"></i> {{ prof.email }} </a>
      <a v-if="prof.phone" :href="`tel:${prof.phone.replace(/\s/g, '')}`">
        <i class="fas fa-phone"></i> {{ prof.phone }}
      </a>
      <div v-if="prof.hours || prof.room" class="office-hours">
        <span v-if="prof.hours">Hours: {{ prof.hours }}</span><br v-if="prof.hours && prof.room" />
        <span v-if="prof.room">Room: {{ prof.room }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.prof-card {
  width: 100%;
  max-width: 220px;
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  box-shadow: var(--shadow);
  transition: var(--transition);
  border: 2px solid transparent;
  margin: 0 auto;
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
