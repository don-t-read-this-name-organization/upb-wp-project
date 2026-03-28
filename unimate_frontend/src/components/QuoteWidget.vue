<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAppStore } from '@/stores/appStore'

interface Quote {
  id: number
  text: string
  author: string
}

const store = useAppStore()
const quotes = ref<Quote[]>([])
const currentQuote = ref('')
const isLoading = ref(true)
const showModal = ref(false)
const submitText = ref('')
const submitAuthor = ref('')
const submitError = ref('')
const submitLoading = ref(false)
const submitSuccess = ref(false)

const isLoggedIn = computed(() => store.user !== null)

async function fetchQuotes() {
  try {
    const response = await fetch('/api/quotes/random?number=10')
    if (response.ok) {
      quotes.value = await response.json()
      rotateQuote()
    } else {
      throw new Error('Failed to fetch quotes')
    }
  } catch (error) {
    console.error('Failed to fetch quotes:', error)
    currentQuote.value = 'Quote unavailable'
    isLoading.value = false
  }
}

function rotateQuote() {
  if (quotes.value.length === 0) return
  const randomIndex = Math.floor(Math.random() * quotes.value.length)
  const quote = quotes.value[randomIndex]
  currentQuote.value = `${quote.text} - ${quote.author}`
  isLoading.value = false
}

function openSubmitModal() {
  showModal.value = true
  submitText.value = ''
  submitAuthor.value = ''
  submitError.value = ''
  submitSuccess.value = false
}

function closeSubmitModal() {
  showModal.value = false
}

async function submitQuote() {
  submitError.value = ''
  submitLoading.value = true

  try {
    const response = await fetch('/api/quotes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text: submitText.value, author: submitAuthor.value }),
    })

    if (!response.ok) {
      const data = await response.json()
      throw new Error(data.message || 'Failed to submit quote')
    }

    submitSuccess.value = true
    setTimeout(() => {
      closeSubmitModal()
    }, 1500)
  } catch (error) {
    submitError.value = error instanceof Error ? error.message : 'Failed to submit quote'
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchQuotes()
  setInterval(rotateQuote, 10000)
})
</script>

<template>
  <div class="quote-widget">
    <i class="fas fa-quote-left widget-icon-quote"></i>
    <h3>Quote of the Day</h3>
    <p class="quote-text">{{ currentQuote }}</p>
    <button v-if="isLoggedIn" class="btn-submit-quote" @click="openSubmitModal">
      <i class="fas fa-plus"></i> Submit Quote
    </button>
  </div>

  <div v-if="showModal" class="modal-overlay" @click.self="closeSubmitModal">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Submit Quote</h3>
        <button class="modal-close" @click="closeSubmitModal">
          <i class="fas fa-times"></i>
        </button>
      </div>
      <div v-if="submitSuccess" class="success-message">
        <i class="fas fa-check-circle"></i> Quote submitted for approval!
      </div>
      <form v-else @submit.prevent="submitQuote">
        <div class="form-group">
          <label for="quote-text">Quote Text</label>
          <textarea
            v-model="submitText"
            id="quote-text"
            class="form-control"
            rows="3"
            required
            minlength="6"
            maxlength="256"
            placeholder="Enter your quote"
          ></textarea>
        </div>
        <div class="form-group">
          <label for="quote-author">Author</label>
          <input
            v-model="submitAuthor"
            type="text"
            id="quote-author"
            class="form-control"
            required
            minlength="6"
            maxlength="256"
            placeholder="Enter author name"
          />
        </div>
        <p v-if="submitError" class="error-message">{{ submitError }}</p>
        <div class="modal-actions">
          <button type="button" class="btn btn-secondary" @click="closeSubmitModal">Cancel</button>
          <button type="submit" class="btn btn-primary" :disabled="submitLoading">
            {{ submitLoading ? 'Submitting...' : 'Submit' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.quote-widget {
  flex: 1;
  min-width: 240px;
  padding: 1.25rem;
  background-color: var(--bg-secondary);
  border-radius: var(--radius);
  text-align: center;
  border: 1px solid var(--border-light);
}

.widget-icon-quote {
  font-size: 1.75rem;
  color: var(--secondary-color);
  margin-bottom: 0.25rem;
  display: block;
  opacity: 0.7;
}

.quote-widget h3 {
  margin: 0.5rem 0;
  font-size: 1rem;
}

.quote-text {
  color: var(--text-muted);
  font-style: italic;
  font-size: 0.9rem;
  line-height: 1.6;
  margin: 0.75rem 0;
}

.btn-submit-quote {
  margin-top: 0.75rem;
  padding: 0.4rem 0.75rem;
  font-size: 0.75rem;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 50px;
  color: var(--text-muted);
  cursor: pointer;
  transition: var(--transition);
}

.btn-submit-quote:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
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
  z-index: 1001;
}

.modal-content {
  background: var(--card-bg);
  border-radius: var(--radius);
  padding: 1.5rem;
  width: 100%;
  max-width: 400px;
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

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.error-message {
  color: #dc3545;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.success-message {
  color: #28a745;
  text-align: center;
  padding: 1rem;
  font-size: 1rem;
}

.success-message i {
  display: block;
  font-size: 2rem;
  margin-bottom: 0.5rem;
}
</style>
