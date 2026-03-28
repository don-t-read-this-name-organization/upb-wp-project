<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useAppStore } from '@/stores/appStore'
import { useI18n } from 'vue-i18n'

interface Link {
  key: string
  title: string
  description: string
  url: string
  icon: string
  colorClass: string
}

const store = useAppStore()
const { locale } = useI18n()
const facultyLinks = ref<Link[]>([])

const defaultLinks: Link[] = [
  {
    key: 'myupb',
    title: '',
    description: '',
    url: 'https://my.upb.ro/login',
    icon: 'fas fa-id-card',
    colorClass: 'link-icon-idcard',
  },
  {
    key: 'courses',
    title: '',
    description: '',
    url: 'https://curs.upb.ro/2025/',
    icon: 'fas fa-laptop-code',
    colorClass: 'link-icon-laptop',
  },
  {
    key: 'ticketing',
    title: '',
    description: '',
    url: 'https://ticketing.upb.ro/',
    icon: 'fas fa-ticket-alt',
    colorClass: 'link-icon-ticket',
  },
]

const usefulLinks = computed(() => [...facultyLinks.value, ...defaultLinks])

async function fetchFacultyLinks() {
  const facultyId = store.user?.faculty?.id
  if (!facultyId) return
  
  try {
    const response = await fetch(`/api/faculty-links/${facultyId}?lang=${locale.value}`)
    if (response.ok) {
      const data = await response.json()
      facultyLinks.value = data.map((link: any) => ({
        key: link.key,
        title: link.title,
        description: link.description || '',
        url: link.url,
        icon: link.icon || 'fas fa-link',
        colorClass: link.colorClass || 'link-icon-default',
      }))
    }
  } catch (error) {
    console.error('Failed to fetch faculty links:', error)
  }
}

onMounted(() => {
  fetchFacultyLinks()
})

watch(locale, () => {
  fetchFacultyLinks()
})
</script>

<template>
  <div class="card fade-in">
    <h2 class="card-title">
      <i class="fas fa-link"></i>
      {{ $t ? $t('linksPage.title') : 'Useful Links for Students' }}
    </h2>

    <div class="links-grid">
      <a
        v-for="link in usefulLinks"
        :key="link.url"
        :href="link.url"
        target="_blank"
        rel="noopener"
        class="link-card"
      >
        <i :class="[link.icon, 'link-icon', link.colorClass]"></i>
        <div class="link-content">
          <h3>{{ link.title || $t(`linksPage.${link.key}.title`) }}</h3>
          <p>{{ link.description || $t(`linksPage.${link.key}.desc`) }}</p>
        </div>
      </a>
    </div>
  </div>
</template>

<style scoped>
.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.25rem;
  margin-top: 1.5rem;
}

.link-card {
  background-color: var(--card-bg);
  border-radius: var(--radius);
  padding: 1.4rem;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
  text-decoration: none;
  color: var(--text-color);
  min-height: 120px;
  height: auto;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  overflow: hidden;
}

.link-card h3 {
  overflow-wrap: break-word;
  word-wrap: break-word;
  hyphens: auto;
  line-height: 1.2;
  margin-bottom: 4px;
}

.link-card p {
  overflow-wrap: break-word;
  word-wrap: break-word;
  hyphens: auto;
}

.link-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.link-content {
  flex: 1;
  min-width: 0;
}

.link-icon {
  flex-shrink: 0;
  width: 2.5rem;
  font-size: 1.6rem;
  opacity: 0.75;
  display: flex;
  justify-content: center;
}

.link-icon-globe {
  color: #7b9cbe;
}
.link-icon-university {
  color: #8db5a0;
}
.link-icon-tasks {
  color: #c8ad7f;
}
.link-icon-users {
  color: #c49090;
}
.link-icon-concierge {
  color: #a093c4;
}
.link-icon-calendar {
  color: #c49a72;
}
.link-icon-clipboard {
  color: #7ab8a4;
}
.link-icon-idcard {
  color: #7ab4c8;
}
.link-icon-laptop {
  color: #c47a99;
}
.link-icon-ticket {
  color: #9ea0a4;
}
</style>
