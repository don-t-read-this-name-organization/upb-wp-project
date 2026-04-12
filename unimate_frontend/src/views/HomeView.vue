<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/appStore'
import { parseMarkdown } from '@/utils/markdown'
import WeatherWidget from '@/components/WeatherWidget.vue'
import QuoteWidget from '@/components/QuoteWidget.vue'
import BaseModal from '@/components/BaseModal.vue'

const router = useRouter()
const { locale, t } = useI18n()
const store = useAppStore()

interface NewsTranslation {
  language: string
  title: string
  body: string
}

interface NewsItem {
  id: number
  publishDate: string
  translations: NewsTranslation[]
}

interface NewsCard {
  id: number
  date: string
  title: string
  body: string
  truncatedBody: string
}

const isPanelCollapsed = ref(true)
const newsData = ref<NewsItem[]>([])
const loading = ref(true)
const selectedNews = ref<NewsCard | null>(null)

const languageMap: Record<string, string> = {
  en: 'en',
  de: 'de',
  fr: 'fr',
  ro: 'ro',
}

const currentLang = computed(() => languageMap[locale.value] ?? 'en')

const showMap = computed(() => !store.isAdmin)
const showNews = computed(() => !store.isAdmin)
const showQuotes = computed(() => !store.isAdmin)
const showWelcome = computed(() => true)

const news = computed<NewsCard[]>(() => {
  return newsData.value.map((item) => {
    const translation = item.translations?.find(
      (t) => t.language === currentLang.value
    ) || item.translations?.[0]
    
    const body = translation?.body || ''
    const truncatedBody = body.length > 150 ? body.substring(0, 150) + '...' : body
    
    return {
      id: item.id,
      date: new Date(item.publishDate).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      }),
      title: translation?.title || 'Untitled',
      body: body,
      truncatedBody: truncatedBody,
    }
  })
})

const latestNews = computed(() => news.value.slice(0, 6))

const showViewMore = computed(() => news.value.length >= 6)

async function fetchNews() {
  try {
    const response = await fetch('/api/news/latest?limit=10')
    if (response.ok) {
      newsData.value = await response.json()
    }
  } catch (error) {
    console.error('Failed to fetch news:', error)
  } finally {
    loading.value = false
  }
}

function openNewsModal(item: NewsCard) {
  selectedNews.value = item
}

function closeNewsModal() {
  selectedNews.value = null
}

onMounted(() => {
  const collapsed = localStorage.getItem('bottomPanelCollapsed')
  if (collapsed !== null) {
    isPanelCollapsed.value = collapsed === 'true'
  }
  fetchNews()
})

const togglePanel = () => {
  isPanelCollapsed.value = !isPanelCollapsed.value
  localStorage.setItem('bottomPanelCollapsed', String(isPanelCollapsed.value))
}

const maps = [
  { name: 'Campus Central', src: '/src/assets/images/campus-map.png' },
  { name: 'Polizu', src: '/src/assets/images/polizu.jpg' },
  { name: 'Leu', src: '/src/assets/images/leu.jpg' },
  { name: 'Regie', src: '/src/assets/images/regie.jpg' },
  { name: 'Pitesti 1', src: '/src/assets/images/pitesti_1_campus.jpg' },
  { name: 'Pitesti 2', src: '/src/assets/images/pitesti_2_campus.jpg' },
]

const currentMapIndex = ref(0)
const currentMapSrc = computed(() => maps[currentMapIndex.value]?.src ?? '')
const currentMapName = computed(() => maps[currentMapIndex.value]?.name ?? '')

const prevMap = () => {
  currentMapIndex.value = currentMapIndex.value === 0 ? maps.length - 1 : currentMapIndex.value - 1
}

const nextMap = () => {
  currentMapIndex.value = currentMapIndex.value === maps.length - 1 ? 0 : currentMapIndex.value + 1
}
</script>

<template>
  <div class="home-container">
    <section v-if="showWelcome" class="card fade-in">
      <h1 class="card-title">{{ $t('welcome') }}</h1>
      <p class="welcome-text">{{ $t('description') }}</p>
      <p class="welcome-text" style="margin-top: 1rem;"></p>
    </section>

    <section v-if="showMap" class="card map-container fade-in">
      <h2 class="card-title">{{ $t('map') }}</h2>
      <div class="map-carousel">
        <button class="map-arrow map-arrow-left" @click="prevMap" aria-label="Previous map">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
        </button>
        <div class="map-wrapper">
          <img :src="currentMapSrc" class="map-image" :alt="currentMapName" />
        </div>
        <button class="map-arrow map-arrow-right" @click="nextMap" aria-label="Next map">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </button>
      </div>
      <div class="map-dots">
        <span 
          v-for="(map, index) in maps" 
          :key="index" 
          class="map-dot" 
          :class="{ active: index === currentMapIndex }"
          @click="currentMapIndex = index"
        ></span>
      </div>
    </section>

    <section v-if="showNews" class="news-section">
      <h2 class="section-heading">{{ t('latestNews') }}</h2>
      <div class="news-grid">
        <div v-for="item in latestNews" :key="item.id" class="news-card" @click="openNewsModal(item)">
          <div class="news-date">{{ item.date }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.truncatedBody }}</p>
          <span class="read-more">{{ t('readMore') || 'Read more' }} &rarr;</span>
        </div>
      </div>
      <div v-if="showViewMore" class="view-more-container">
        <button class="btn btn-secondary" @click="router.push('/news')">
          <i class="fas fa-search"></i> {{ t('viewMore') }}
        </button>
      </div>
    </section>

    <div v-if="showQuotes" class="bottom-panel" :class="{ collapsed: isPanelCollapsed }">
      <button class="panel-toggle" @click="togglePanel" aria-label="Toggle panel">
        <span class="arrow">{{ isPanelCollapsed ? '▼' : '▲' }}</span>
        <span>{{ t('weatherQuote') }}</span>
      </button>
      <div class="panel-content">
        <WeatherWidget />
        <QuoteWidget />
      </div>
    </div>

    <BaseModal
      v-if="selectedNews"
      :model-value="true"
      size="lg"
      :show-close="false"
      @update:modelValue="closeNewsModal"
      @close="closeNewsModal"
    >
      <template #header>
        <div></div>
      </template>
      <div class="modal-content news-modal">
        <button class="modal-close" @click="closeNewsModal">
          <i class="fas fa-times"></i>
        </button>
        <div class="news-modal-date">{{ selectedNews.date }}</div>
        <h2 class="news-modal-title">{{ selectedNews.title }}</h2>
        <div class="news-modal-body" v-html="parseMarkdown(selectedNews.body)"></div>
      </div>
    </BaseModal>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1100px;
  margin: 0 auto;
}

.welcome-text {
  color: var(--text-muted);
  line-height: 1.7;
  font-size: 1.05rem;
}

.map-container {
  text-align: center;
  margin: 2rem 0;
}

.map-carousel {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.map-wrapper {
  flex: 1;
  max-width: 800px;
  position: relative;
}

.map-image {
  max-width: 100%;
  height: auto;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  border: 1px solid var(--border-color);
}

.map-name {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: var(--radius);
  font-size: 0.9rem;
}

.map-arrow {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 50%;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
  color: var(--text-color);
}

.map-arrow:hover {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.map-dots {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.map-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: var(--border-color);
  cursor: pointer;
  transition: var(--transition);
}

.map-dot:hover,
.map-dot.active {
  background-color: var(--primary-color);
}

.section-heading {
  font-family: var(--font-display);
  font-size: 1.5rem;
  margin: 2rem 0 1rem;
  color: var(--text-color);
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.view-more-container {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

.news-card {
  background-color: var(--card-bg);
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
  padding: 1.5rem;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.news-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.news-card p {
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more {
  color: var(--primary-color);
  font-size: 0.85rem;
  font-weight: 500;
  margin-top: 0.5rem;
}

.news-date {
  color: var(--primary-color);
  font-size: 0.78rem;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.news-card h3 {
  margin-bottom: 0.5rem;
  color: var(--text-color);
}

.news-card p {
  color: var(--text-muted);
  font-size: 0.9rem;
  line-height: 1.6;
}

.bottom-panel {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: var(--card-bg);
  border-top: 1px solid var(--border-color);
  box-shadow: 0 -4px 20px rgba(46, 44, 40, 0.06);
  transition: transform 0.3s ease;
  z-index: 999;
}

.bottom-panel.collapsed {
  transform: translateY(calc(100% - 48px));
}

.panel-toggle {
  width: 100%;
  padding: 0.9rem;
  background-color: transparent;
  border: none;
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  color: var(--text-muted);
  font-family: var(--font-body);
  font-weight: 500;
  font-size: 0.8rem;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  transition: background-color 0.2s, color 0.2s;
}

.panel-toggle:hover {
  color: var(--text-color);
  background-color: var(--bg-secondary);
}

.panel-content {
  padding: 1.5rem 2rem;
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
}

.arrow {
  font-size: 0.7rem;
}

@media (max-width: 768px) {
  .panel-content {
    flex-direction: column;
    padding: 1.25rem;
    gap: 1rem;
  }

  .bottom-panel.collapsed {
    transform: translateY(calc(100% - 48px));
  }
}

.modal-content {
  background: var(--card-bg);
  border-radius: var(--radius);
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  box-shadow: var(--shadow-lg);
}

.modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.25rem;
  color: var(--text-muted);
  cursor: pointer;
  z-index: 1;
}

.modal-close:hover {
  color: var(--text-color);
}

.news-modal {
  padding: 2rem;
}

.news-modal-date {
  color: var(--primary-color);
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.news-modal-title {
  font-family: var(--font-display);
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: var(--text-color);
}

.news-modal-body {
  line-height: 1.7;
  color: var(--text-color);
}

.news-modal-body :deep(img) {
  max-width: 100%;
  border-radius: var(--radius);
}

.news-modal-body :deep(h1),
.news-modal-body :deep(h2),
.news-modal-body :deep(h3) {
  margin-top: 1.5rem;
  margin-bottom: 0.75rem;
}

.news-modal-body :deep(p) {
  margin-bottom: 1rem;
}

.news-modal-body :deep(ul),
.news-modal-body :deep(ol) {
  margin-bottom: 1rem;
  padding-left: 1.5rem;
}

.news-modal-body :deep(a) {
  color: var(--primary-color);
}
</style>
