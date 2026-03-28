<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'

interface Translation {
  language: string
  title: string
  body: string
}

interface NewsPiece {
  id: number
  publishDate: string
  translations: Translation[]
}

const { locale, t } = useI18n()

const news = ref<NewsPiece[]>([])
const loading = ref(true)
const error = ref('')
const searchQuery = ref('')
const currentPage = ref(1)
const itemsPerPage = 6

const languageMap: Record<string, string> = {
  en: 'en',
  ro: 'ro',
  de: 'de',
  fr: 'fr',
}

const currentLang = computed(() => {
  const lang = locale.value || 'en'
  return languageMap[lang] || lang
})

const filteredNews = computed(() => {
  if (!searchQuery.value) return news.value
  const query = searchQuery.value.toLowerCase()
  return news.value.filter((item) =>
    item.translations.some(
      (t) =>
        t.title.toLowerCase().includes(query) ||
        t.body.toLowerCase().includes(query)
    )
  )
})

const totalPages = computed(() => Math.ceil(filteredNews.value.length / itemsPerPage))

const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return filteredNews.value.slice(start, start + itemsPerPage)
})

const displayedNews = computed(() => {
  return paginatedNews.value.map((item) => {
    const translation = item.translations?.find(
      (t) => t.language === currentLang.value
    ) || item.translations?.[0]

    return {
      id: item.id,
      date: new Date(item.publishDate).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      }),
      title: translation?.title || 'Untitled',
      body: translation?.body || '',
    }
  })
})

async function fetchNews() {
  loading.value = true
  error.value = ''
  try {
    const response = await fetch('/api/news')
    if (!response.ok) throw new Error('Failed to fetch news')
    news.value = await response.json()
  } catch {
    error.value = 'Failed to load news. Is the backend running?'
  } finally {
    loading.value = false
  }
}

function goToPage(page: number) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

watch(searchQuery, () => {
  currentPage.value = 1
})

onMounted(fetchNews)
</script>

<template>
  <main class="main-content">
    <div class="news-page-container">
      <div class="page-header-section">
        <h1 class="page-title"><i class="fas fa-newspaper"></i> News</h1>
        <p class="page-subtitle">{{ t('newsPage.subtitle') }}</p>
      </div>

      <div class="search-section">
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input
            v-model="searchQuery"
            type="text"
            :placeholder="t('newsPage.search')"
            class="search-input"
          />
        </div>
      </div>

      <div v-if="error" class="error-alert">
        <i class="fas fa-exclamation-circle"></i> {{ error }}
      </div>

      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i> Loading news...
      </div>

      <div v-else-if="displayedNews.length === 0" class="empty-state">
        <i class="fas fa-newspaper"></i>
        <p>No news found</p>
      </div>

      <div v-else>
        <div class="news-list">
          <article v-for="item in displayedNews" :key="item.id" class="news-item">
            <div class="news-item-date">{{ item.date }}</div>
            <h2 class="news-item-title">{{ item.title }}</h2>
            <p class="news-item-body">{{ item.body }}</p>
          </article>
        </div>

        <div v-if="totalPages > 1" class="pagination">
          <button
            class="page-btn"
            :disabled="currentPage === 1"
            @click="goToPage(currentPage - 1)"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          
          <template v-for="page in totalPages" :key="page">
            <button
              v-if="
                page === 1 ||
                page === totalPages ||
                (page >= currentPage - 1 && page <= currentPage + 1)
              "
              class="page-btn"
              :class="{ active: page === currentPage }"
              @click="goToPage(page)"
            >
              {{ page }}
            </button>
            <span v-else-if="page === currentPage - 2 || page === currentPage + 2" class="page-ellipsis">
              ...
            </span>
          </template>

          <button
            class="page-btn"
            :disabled="currentPage === totalPages"
            @click="goToPage(currentPage + 1)"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>

        <div class="results-info">
          Showing {{ (currentPage - 1) * itemsPerPage + 1 }} - 
          {{ Math.min(currentPage * itemsPerPage, filteredNews.length) }} 
          of {{ filteredNews.length }} news
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.news-page-container {
  max-width: 800px;
  margin: 0 auto;
}

.page-header-section {
  text-align: center;
  margin-bottom: 2rem;
}

.page-title {
  font-family: var(--font-display);
  font-size: 2rem;
  color: var(--text-color);
  margin: 0;
}

.page-title i {
  margin-right: 0.5rem;
  color: var(--primary-color);
}

.page-subtitle {
  color: var(--text-muted);
  margin-top: 0.5rem;
}

.search-section {
  margin-bottom: 2rem;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-box i {
  position: absolute;
  left: 1rem;
  color: var(--text-muted);
}

.search-input {
  width: 100%;
  padding: 0.85rem 1rem 0.85rem 2.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  background: var(--bg-color);
  color: var(--text-color);
  font-size: 1rem;
  transition: var(--transition);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
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

.loading-state,
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: var(--text-muted);
}

.empty-state i {
  font-size: 3rem;
  display: block;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.news-item {
  background: var(--card-bg);
  border: 1px solid var(--border-light);
  border-radius: var(--radius);
  padding: 1.5rem;
  transition: var(--transition);
}

.news-item:hover {
  box-shadow: var(--shadow);
  transform: translateY(-2px);
}

.news-item-date {
  color: var(--primary-color);
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
}

.news-item-title {
  font-family: var(--font-display);
  font-size: 1.25rem;
  margin: 0 0 0.75rem;
  color: var(--text-color);
}

.news-item-body {
  color: var(--text-muted);
  line-height: 1.6;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 2.5rem;
}

.page-btn {
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  background: var(--card-bg);
  color: var(--text-color);
  cursor: pointer;
  font-size: 0.9rem;
  transition: var(--transition);
}

.page-btn:hover:not(:disabled) {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.page-btn.active {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-ellipsis {
  color: var(--text-muted);
}

.results-info {
  text-align: center;
  color: var(--text-muted);
  font-size: 0.85rem;
  margin-top: 1.5rem;
}
</style>
