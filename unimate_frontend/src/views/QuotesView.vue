<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()

interface Quote {
  id: number
  text: string
  author: string
  active: boolean
}

interface QuoteForm {
  text: string
  author: string
  active: boolean
}

const quotes = ref<Quote[]>([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const editingQuote = ref<Quote | null>(null)
const filter = ref<'all' | 'active' | 'inactive'>('all')
const searchQuery = ref('')
const sortBy = ref<'id' | 'author' | 'text'>('id')
const sortOrder = ref<'asc' | 'desc'>('desc')
const form = ref<QuoteForm>({
  text: '',
  author: '',
  active: true,
})

const filteredQuotes = computed(() => {
  let result = [...quotes.value]

  if (filter.value === 'active') {
    result = result.filter(q => q.active)
  } else if (filter.value === 'inactive') {
    result = result.filter(q => !q.active)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(q => 
      q.text.toLowerCase().includes(query) || 
      q.author.toLowerCase().includes(query)
    )
  }

  result.sort((a, b) => {
    let comparison = 0
    if (sortBy.value === 'author') {
      comparison = a.author.localeCompare(b.author)
    } else if (sortBy.value === 'text') {
      comparison = a.text.localeCompare(b.text)
    } else {
      comparison = a.id - b.id
    }
    return sortOrder.value === 'asc' ? comparison : -comparison
  })

  return result
})

const stats = computed(() => ({
  total: quotes.value.length,
  active: quotes.value.filter(q => q.active).length,
  inactive: quotes.value.filter(q => !q.active).length,
}))

async function fetchQuotes() {
  loading.value = true
  error.value = ''
  try {
    const response = await fetch('/api/quotes')
    if (!response.ok) throw new Error(t('quotes.failedToLoad'))
    quotes.value = await response.json()
  } catch {
    error.value = t('quotes.failedToLoad')
  } finally {
    loading.value = false
  }
}

function openAddModal() {
  editingQuote.value = null
  form.value = { text: '', author: '', active: true }
  showModal.value = true
}

function openEditModal(quote: Quote) {
  editingQuote.value = quote
  form.value = { text: quote.text, author: quote.author, active: quote.active }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingQuote.value = null
}

async function handleSubmit() {
  error.value = ''
  try {
    if (editingQuote.value) {
      const response = await fetch(`/api/quotes/${editingQuote.value.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form.value),
      })
      if (!response.ok) throw new Error(t('quotes.failedToUpdate'))
    } else {
      const response = await fetch('/api/quotes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form.value),
      })
      if (!response.ok) throw new Error(t('quotes.failedToCreate'))
    }
    closeModal()
    await fetchQuotes()
  } catch (e) {
    error.value = e instanceof Error ? e.message : t('quotes.failedToCreate')
  }
}

async function deleteQuote(quote: Quote) {
  if (!confirm(t('quotes.deleteConfirm', { author: quote.author }))) return
  error.value = ''
  try {
    const response = await fetch(`/api/quotes/${quote.id}`, {
      method: 'DELETE',
    })
    if (!response.ok) throw new Error('Failed to delete quote')
    await fetchQuotes()
  } catch {
    error.value = t('quotes.failedToDelete')
  }
}

function goBack() {
  router.push('/admin')
}

onMounted(fetchQuotes)
</script>

<template>
  <main class="main-content">
    <div class="page-header">
      <div class="header-left">
        <button class="btn btn-secondary back-btn" @click="goBack">
          <i class="fas fa-arrow-left"></i> {{ t('quotes.back') }}
        </button>
        <div>
          <h1 class="page-title"><i class="fas fa-quote-left"></i> {{ t('quotes.title') }}</h1>
          <p class="page-subtitle">{{ t('quotes.subtitle') }}</p>
        </div>
      </div>
      <button class="btn btn-primary" @click="openAddModal">
        <i class="fas fa-plus"></i> {{ t('quotes.addQuote') }}
      </button>
    </div>

    <div v-if="error" class="error-alert">
      <i class="fas fa-exclamation-circle"></i> {{ error }}
    </div>

    <div class="card">
      <div class="filters-row">
        <div class="filters">
          <button 
            class="filter-btn" 
            :class="{ active: filter === 'all' }"
            @click="filter = 'all'"
          >
            {{ t('quotes.all') }} ({{ stats.total }})
          </button>
          <button 
            class="filter-btn" 
            :class="{ active: filter === 'active' }"
            @click="filter = 'active'"
          >
            {{ t('quotes.active') }} ({{ stats.active }})
          </button>
          <button 
            class="filter-btn" 
            :class="{ active: filter === 'inactive' }"
            @click="filter = 'inactive'"
          >
            {{ t('quotes.inactive') }} ({{ stats.inactive }})
          </button>
        </div>
        
        <div class="search-sort">
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input 
              v-model="searchQuery" 
              type="text" 
              :placeholder="t('quotes.search')" 
              class="search-input"
            />
          </div>
          
          <select v-model="sortBy" class="sort-select">
            <option value="id">{{ t('quotes.sortById') }}</option>
            <option value="author">{{ t('quotes.sortByAuthor') }}</option>
            <option value="text">{{ t('quotes.sortByText') }}</option>
          </select>
          
          <button class="sort-order-btn" @click="sortOrder = sortOrder === 'asc' ? 'desc' : 'asc'">
            <i :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i> Loading quotes...
      </div>

      <table v-else class="quotes-table">
        <thead>
          <tr>
            <th>{{ t('admin.id') }}</th>
            <th>{{ t('quotes.quoteText') }}</th>
            <th>{{ t('quotes.author') }}</th>
            <th>{{ t('quotes.status') }}</th>
            <th>{{ t('admin.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="quote in filteredQuotes" :key="quote.id">
            <td>{{ quote.id }}</td>
            <td class="quote-cell">{{ quote.text }}</td>
            <td>{{ quote.author }}</td>
            <td>
              <span :class="['status-badge', quote.active ? 'active' : 'inactive']">
                {{ quote.active ? t('quotes.active') : t('quotes.inactive') }}
              </span>
            </td>
            <td class="actions-cell">
              <button class="btn-icon" :title="t('common.edit')" @click="openEditModal(quote)">
                <i class="fas fa-edit"></i>
              </button>
              <button class="btn-icon delete" :title="t('common.delete')" @click="deleteQuote(quote)">
                <i class="fas fa-trash"></i>
              </button>
            </td>
          </tr>
          <tr v-if="filteredQuotes.length === 0">
            <td colspan="5" class="empty-state">{{ t('quotes.noQuotes') }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingQuote ? t('quotes.editQuote') : t('quotes.addNewQuote') }}</h3>
          <button class="modal-close" @click="closeModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="quote-text">{{ t('quotes.quoteText') }}</label>
            <textarea
              v-model="form.text"
              id="quote-text"
              class="form-control"
              rows="3"
              required
              minlength="6"
              maxlength="256"
            ></textarea>
          </div>
          <div class="form-group">
            <label for="quote-author">{{ t('quotes.author') }}</label>
            <input
              v-model="form.author"
              type="text"
              id="quote-author"
              class="form-control"
              required
              minlength="6"
              maxlength="256"
            />
          </div>
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.active" />
              <span>{{ t('quotes.active') }}</span>
            </label>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">{{ t('common.cancel') }}</button>
            <button type="submit" class="btn btn-primary">
              {{ editingQuote ? t('admin.saveChanges') : t('quotes.addQuote') }}
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

.filters-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.filters {
  display: flex;
  gap: 0.5rem;
}

.filter-btn {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 50px;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.85rem;
  transition: var(--transition);
}

.filter-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.filter-btn.active {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: white;
}

.search-sort {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-box i {
  position: absolute;
  left: 0.75rem;
  color: var(--text-muted);
  font-size: 0.85rem;
}

.search-input {
  padding: 0.5rem 1rem 0.5rem 2rem;
  border: 1px solid var(--border-color);
  border-radius: 50px;
  background: var(--bg-color);
  color: var(--text-color);
  font-size: 0.85rem;
  width: 200px;
  transition: var(--transition);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.search-input::placeholder {
  color: var(--text-subtle);
}

.sort-select {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  background: var(--bg-color);
  color: var(--text-color);
  font-size: 0.85rem;
  cursor: pointer;
  transition: var(--transition);
}

.sort-select:focus {
  outline: none;
  border-color: var(--primary-color);
}

.sort-order-btn {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  background: var(--bg-color);
  color: var(--text-muted);
  cursor: pointer;
  transition: var(--transition);
}

.sort-order-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.loading-state {
  text-align: center;
  padding: 2rem;
  color: var(--text-muted);
}

.quotes-table {
  width: 100%;
  border-collapse: collapse;
}

.quotes-table th,
.quotes-table td {
  padding: 0.9rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
}

.quotes-table th {
  background-color: var(--bg-secondary);
  color: var(--text-muted);
  font-weight: 600;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.quotes-table tr:last-child td {
  border-bottom: none;
}

.quotes-table tr:hover td {
  background-color: var(--bg-secondary);
}

.quote-cell {
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.status-badge {
  display: inline-block;
  padding: 0.2rem 0.6rem;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.active {
  background-color: #d6eadf;
  color: #2a5c3f;
}

.status-badge.inactive {
  background-color: #f5d6d6;
  color: #8b3a3a;
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

.empty-state {
  text-align: center;
  padding: 2rem;
  color: var(--text-muted);
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

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.45rem;
  font-weight: 500;
  font-size: 0.875rem;
  color: var(--text-muted);
}

.form-control {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-family: var(--font-body);
  font-size: 0.9rem;
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
  min-height: 80px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}
</style>
