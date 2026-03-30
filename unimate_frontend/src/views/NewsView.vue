<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const { t } = useI18n()

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

interface NewsForm {
  translations: { language: string; title: string; body: string }[]
}

const router = useRouter()
const news = ref<NewsPiece[]>([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const editingNews = ref<NewsPiece | null>(null)
const searchQuery = ref('')
const uploading = ref(false)
const form = ref<NewsForm>({
  translations: [
    { language: 'en', title: '', body: '' },
    { language: 'ro', title: '', body: '' },
    { language: 'de', title: '', body: '' },
    { language: 'fr', title: '', body: '' },
  ],
})

const languages = ['en', 'ro', 'de', 'fr']
const selectedLanguage = ref('en')

const languageIndex = computed(() => {
  return languages.indexOf(selectedLanguage.value)
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

async function fetchNews() {
  loading.value = true
  error.value = ''
  try {
    const response = await fetch('/api/news')
    if (!response.ok) throw new Error('Failed to fetch news')
    news.value = await response.json()
  } catch {
    error.value = t('newsAdmin.failedToLoad')
  } finally {
    loading.value = false
  }
}

const IMAGE_EXTENSIONS = ['.png', '.jpg', '.jpeg', '.gif', '.webp', '.svg']

function isImage(file: File): boolean {
  const ext = '.' + file.name.split('.').pop()?.toLowerCase()
  return IMAGE_EXTENSIONS.includes(ext)
}

async function uploadFile(file: File): Promise<string> {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', '1')

  uploading.value = true
  try {
    const response = await fetch('/api/files', {
      method: 'POST',
      body: formData,
    })
    if (response.ok) {
      const data = await response.json()
      return data.filePath
    }
    throw new Error('Upload failed')
  } finally {
    uploading.value = false
  }
}

async function handleDrop(lang: string, dt: DataTransfer | null) {
  if (!dt) return
  const files = dt.files
  if (!files?.length) return
  const file = files[0]
  if (!isImage(file)) return

  const url = await uploadFile(file)
  const markdown = `![${file.name}](${url})`

  const trans = form.value.translations.find(t => t.language === lang)
  if (trans) {
    const textarea = document.querySelector(`#body-${lang}`) as HTMLTextAreaElement
    if (textarea) {
      const start = textarea.selectionStart
      const end = textarea.selectionEnd
      trans.body = trans.body.slice(0, start) + markdown + trans.body.slice(end)
    } else {
      trans.body += '\n' + markdown
    }
  }
}

function extractImageUrls(content: string): string[] {
  const regex = /!\[([^\]]*)\]\(([^)]+)\)/g
  const urls: string[] = []
  let match
  while ((match = regex.exec(content)) !== null) {
    const url = match[2]
    if (url && (url.startsWith('/api/files/') || url.startsWith('/uploads/'))) {
      urls.push(url)
    }
  }
  return urls
}

function openAddModal() {
  editingNews.value = null
  selectedLanguage.value = 'en'
  form.value = {
    translations: [
      { language: 'en', title: '', body: '' },
      { language: 'ro', title: '', body: '' },
      { language: 'de', title: '', body: '' },
      { language: 'fr', title: '', body: '' },
    ],
  }
  showModal.value = true
}

function openEditModal(item: NewsPiece) {
  editingNews.value = item
  selectedLanguage.value = 'en'
  form.value = {
    translations: languages.map((lang) => {
      const existing = item.translations.find((t) => t.language === lang)
      return {
        language: lang,
        title: existing?.title || '',
        body: existing?.body || '',
      }
    }),
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingNews.value = null
}

async function handleSubmit() {
  error.value = ''
  const validTranslations = form.value.translations.filter((t) => t.title.trim())
  if (validTranslations.length === 0) {
    error.value = t('newsAdmin.translationRequired')
    return
  }

  try {
    const payload = { translations: validTranslations }
    const url = editingNews.value ? `/api/news/${editingNews.value.id}` : '/api/news'
    const method = editingNews.value ? 'PUT' : 'POST'

    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })

    if (!response.ok) throw new Error('Operation failed')
    closeModal()
    await fetchNews()
  } catch (e) {
    error.value = e instanceof Error ? e.message : t('newsAdmin.operationFailed')
  }
}

async function deleteNews(item: NewsPiece) {
  if (!confirm(t('newsAdmin.deleteConfirm'))) return
  error.value = ''
  try {
    for (const trans of item.translations) {
      const imageUrls = extractImageUrls(trans.body)
      for (const url of imageUrls) {
        try {
          const filename = url.split('/').pop()
          if (filename) {
            await fetch(`/api/files/by-filename?userId=1&filename=${encodeURIComponent(filename)}`, {
              method: 'DELETE'
            })
          }
        } catch {
          console.warn('Failed to delete image:', url)
        }
      }
    }

    const response = await fetch(`/api/news/${item.id}`, {
      method: 'DELETE',
    })
    if (!response.ok) throw new Error('Failed to delete news')
    await fetchNews()
  } catch {
    error.value = t('newsAdmin.failedToDelete')
  }
}

function getTitle(item: NewsPiece, lang: string): string {
  const translation = item.translations.find((t) => t.language === lang)
  return translation?.title || '-'
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

onMounted(fetchNews)
</script>

<template>
  <main class="main-content">
    <div class="page-header">
      <div class="header-left">
        <button class="btn btn-secondary back-btn" @click="router.push('/admin')">
          <i class="fas fa-arrow-left"></i> {{ t('newsAdmin.back') }}
        </button>
        <div>
          <h1 class="page-title"><i class="fas fa-newspaper"></i> {{ t('newsAdmin.title') }}</h1>
          <p class="page-subtitle">{{ t('newsAdmin.subtitle') }}</p>
        </div>
      </div>
      <button class="btn btn-primary" @click="openAddModal">
        <i class="fas fa-plus"></i> {{ t('newsAdmin.addNews') }}
      </button>
    </div>

    <div v-if="error" class="error-alert">
      <i class="fas fa-exclamation-circle"></i> {{ error }}
    </div>

    <div class="card">
      <div class="search-bar">
        <i class="fas fa-search"></i>
        <input
          v-model="searchQuery"
          type="text"
          :placeholder="t('newsAdmin.search')"
          class="search-input"
        />
      </div>

      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i> Loading news...
      </div>

      <div v-else-if="filteredNews.length === 0" class="empty-state">
        No news found
      </div>

      <div v-else class="news-grid">
        <div v-for="item in filteredNews" :key="item.id" class="news-card">
          <div class="news-date">{{ formatDate(item.publishDate) }}</div>
          <div class="news-translations">
            <div v-for="lang in languages" :key="lang" class="translation-row">
              <span class="lang-badge">{{ lang.toUpperCase() }}</span>
              <span class="translation-title">{{ getTitle(item, lang) }}</span>
            </div>
          </div>
          <div class="news-actions">
            <button class="btn-icon" title="Edit" @click="openEditModal(item)">
              <i class="fas fa-edit"></i>
            </button>
            <button class="btn-icon delete" title="Delete" @click="deleteNews(item)">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content modal-lg">
        <div class="modal-header">
          <h3>{{ editingNews ? t('newsAdmin.editNews') : t('newsAdmin.addNewNews') }}</h3>
          <button class="modal-close" @click="closeModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="language-tabs">
            <button
              v-for="lang in languages"
              :key="lang"
              type="button"
              class="lang-tab"
              :class="{ active: selectedLanguage === lang }"
              @click="selectedLanguage = lang"
            >
              {{ lang.toUpperCase() }}
            </button>
          </div>

          <div class="translation-form">
            <div class="form-group">
              <label :for="`title-${selectedLanguage}`">Title</label>
              <input
                v-model="form.translations[languageIndex].title"
                type="text"
                :id="`title-${selectedLanguage}`"
                class="form-control"
                required
              />
            </div>
            <div class="form-group">
              <label>Body</label>
              <div class="editor-hint">
                <span>{{ t('newsAdmin.dragDropImages') }}</span>
                <span class="hint-formats">Supports: images, markdown, formulas ($...$)</span>
              </div>
              <MdEditor
                v-model="form.translations[languageIndex].body"
                :editor-id="`body-${selectedLanguage}`"
                language="en-US"
                :toolbars-exclude="['github']"
                class="markdown-editor"
                @drop.prevent="handleDrop(selectedLanguage, $event.dataTransfer!)"
                @dragover.prevent
              />
            </div>
          </div>
          <p v-if="error" class="error-message">{{ error }}</p>
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">{{ t('common.cancel') }}</button>
            <button type="submit" class="btn btn-primary" :disabled="uploading">
              {{ uploading ? t('newsAdmin.saving') : (editingNews ? t('admin.saveChanges') : t('newsAdmin.addNews')) }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.header-left {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.back-btn {
  margin-top: 0.25rem;
}

.page-title {
  font-family: var(--font-display);
  font-size: 1.75rem;
  margin: 0;
  color: var(--text-color);
}

.page-subtitle {
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

.search-bar {
  position: relative;
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
}

.search-bar i {
  position: absolute;
  left: 1rem;
  color: var(--text-muted);
}

.search-input {
  width: 100%;
  padding: 0.75rem 1rem 0.75rem 2.5rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  background: var(--bg-color);
  color: var(--text-color);
  font-size: 0.9rem;
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 2rem;
  color: var(--text-muted);
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1rem;
}

.news-card {
  background: var(--bg-secondary);
  border-radius: var(--radius);
  padding: 1.25rem;
  border: 1px solid var(--border-light);
}

.news-date {
  font-size: 0.75rem;
  color: var(--primary-color);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.75rem;
}

.news-translations {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.translation-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.lang-badge {
  display: inline-block;
  padding: 0.15rem 0.4rem;
  background: var(--border-color);
  border-radius: 4px;
  font-size: 0.65rem;
  font-weight: 600;
  min-width: 24px;
  text-align: center;
}

.translation-title {
  font-size: 0.85rem;
  color: var(--text-color);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.news-actions {
  display: flex;
  gap: 0.5rem;
  border-top: 1px solid var(--border-light);
  padding-top: 0.75rem;
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
  overflow-y: auto;
  padding: 2rem 0;
}

.modal-content {
  background: var(--card-bg);
  border-radius: var(--radius);
  padding: 1.5rem;
  width: 100%;
  max-width: 600px;
  box-shadow: var(--shadow);
  max-height: 90vh;
  overflow-y: auto;
}

.modal-lg {
  max-width: 1000px;
  max-height: 95vh;
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

.translation-form {
  background: var(--bg-secondary);
  border-radius: var(--radius);
  padding: 1rem;
  margin-bottom: 1rem;
}

.translation-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.lang-badge-lg {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  background: var(--primary-color);
  color: white;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}

.translation-label {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.form-group {
  margin-bottom: 1rem;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.35rem;
  font-weight: 500;
  font-size: 0.85rem;
  color: var(--text-muted);
}

.form-control {
  width: 100%;
  padding: 0.65rem 0.85rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-family: var(--font-body);
  font-size: 0.85rem;
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: var(--transition);
}

.form-control:focus {
  outline: none;
  border-color: var(--primary-color);
}

textarea.form-control {
  resize: vertical;
}

.error-message {
  color: #dc3545;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.markdown-editor {
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  width: 100%;
}

.markdown-editor :deep(.md-editor) {
  min-height: 300px;
  width: 100%;
}

.markdown-editor :deep(.md-editor-preview-wrapper) {
  min-height: 200px;
}

.editor-hint {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
}

.hint-formats {
  color: var(--primary-color);
}

.language-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.lang-tab {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  background: var(--card-bg);
  border-radius: var(--radius);
  cursor: pointer;
  font-weight: 500;
  transition: var(--transition);
}

.lang-tab:hover {
  border-color: var(--primary-color);
}

.lang-tab.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}
</style>
