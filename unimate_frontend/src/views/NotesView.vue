<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/appStore'
import { parseMarkdown } from '@/utils/markdown'

const { t } = useI18n()
const store = useAppStore()

interface NoteContent {
  id?: number
  contentType: 'MARKDOWN' | 'AUDIO' | 'PDF'
  content: string
  filePath: string
  sortOrder: number
}

interface Note {
  id: number
  title: string
  collection: string
  contents: NoteContent[]
  createdAt: string
}

const notes = ref<Note[]>([])
const loading = ref(true)
const collections = ref<string[]>([])
const activeFilter = ref('all')
const showModal = ref(false)
const editingNote = ref<Note | null>(null)

const formTitle = ref('')
const formCollection = ref('Math')
const formContents = ref<NoteContent[]>([])
const uploading = ref(false)

const noteContents = ref<{ [key: number]: NoteContent[] }>({})

const filteredNotes = computed(() => {
  if (activeFilter.value === 'all') return notes.value
  return notes.value.filter((n) => n.collection === activeFilter.value)
})

const allCollections = computed(() => {
  const cols = new Set(notes.value.map((n) => n.collection).filter(Boolean))
  return Array.from(cols)
})

async function fetchNotes() {
  const userId = store.user?.id
  if (!userId) return

  loading.value = true
  try {
    const response = await fetch(`/api/notes?userId=${userId}`)
    if (response.ok) {
      notes.value = await response.json()
      updateCollections()
    }
  } catch (error) {
    console.error('Failed to fetch notes:', error)
  } finally {
    loading.value = false
  }
}

function updateCollections() {
  const cols = new Set(notes.value.map((n) => n.collection).filter(Boolean))
  collections.value = Array.from(cols)
}

function filterNotes(collection: string, btn: Event) {
  activeFilter.value = collection
  const target = btn.target as HTMLElement
  target.parentElement
    ?.querySelectorAll('.collection-btn')
    .forEach((b) => b.classList.remove('active'))
  target.classList.add('active')
}

function openModal(note?: Note) {
  if (note) {
    editingNote.value = note
    formTitle.value = note.title
    formCollection.value = note.collection || 'Math'
    formContents.value = note.contents.map((c) => ({ ...c }))
  } else {
    editingNote.value = null
    formTitle.value = ''
    formCollection.value = 'Math'
    formContents.value = [{ contentType: 'MARKDOWN', content: '', filePath: '', sortOrder: 0 }]
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingNote.value = null
}

function addContentBlock() {
  formContents.value.push({
    contentType: 'MARKDOWN',
    content: '',
    filePath: '',
    sortOrder: formContents.value.length,
  })
}

function removeContentBlock(index: number) {
  formContents.value.splice(index, 1)
  formContents.value.forEach((c, i) => (c.sortOrder = i))
}

async function uploadFile(file: File, index: number): Promise<string> {
  const formData = new FormData()
  formData.append('file', file)

  uploading.value = true
  try {
    const response = await fetch('/api/uploads', {
      method: 'POST',
      body: formData,
    })
    if (response.ok) {
      const data = await response.json()
      return data.url
    }
    throw new Error('Upload failed')
  } finally {
    uploading.value = false
  }
}

async function handleFileSelect(event: Event, index: number) {
  const input = event.target as HTMLInputElement
  if (!input.files?.length) return

  const file = input.files[0]
  if (!file) return
  
  const content = formContents.value[index]
  if (!content) return

  if (content.contentType === 'AUDIO') {
    content.filePath = await uploadFile(file, index)
  } else if (content.contentType === 'PDF') {
    content.filePath = await uploadFile(file, index)
  }
}

async function saveNote() {
  if (!formTitle.value.trim()) return

  const userId = store.user?.id
  if (!userId) return

  const noteData = {
    userId,
    title: formTitle.value,
    collection: formCollection.value,
    contents: formContents.value.map((c, i) => ({
      contentType: c.contentType,
      content: c.content,
      filePath: c.filePath,
      sortOrder: i,
    })),
  }

  try {
    if (editingNote.value) {
      await fetch(`/api/notes/${editingNote.value.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(noteData),
      })
    } else {
      await fetch('/api/notes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(noteData),
      })
    }
    await fetchNotes()
    closeModal()
  } catch (error) {
    console.error('Failed to save note:', error)
  }
}

async function deleteNote(note: Note) {
  if (!confirm(t('common.confirm'))) return

  try {
    await fetch(`/api/notes/${note.id}`, { method: 'DELETE' })
    await fetchNotes()
  } catch (error) {
    console.error('Failed to delete note:', error)
  }
}

function getRenderedContent(content: NoteContent): string {
  if (content.contentType === 'MARKDOWN') {
    return parseMarkdown(content.content || '')
  }
  return ''
}

onMounted(() => {
  fetchNotes()
})

watch(
  () => store.user,
  () => {
    fetchNotes()
  }
)
</script>

<template>
  <div class="notes-page">
    <div class="card fade-in">
      <div class="notes-header">
        <h2 class="card-title card-title-no-margin">
          <i class="fas fa-sticky-note"></i> {{ t('notes.title') }}
        </h2>
        <div class="notes-actions">
          <button class="btn btn-secondary" @click="openModal()">
            <i class="fas fa-plus"></i> {{ t('notes.newNote') }}
          </button>
        </div>
      </div>

      <div class="collection-filter">
        <button class="collection-btn active" @click="filterNotes('all', $event)">
          {{ t('notes.all') }}
        </button>
        <button
          v-for="col in allCollections"
          :key="col"
          class="collection-btn"
          @click="filterNotes(col, $event)"
        >
          {{ col }}
        </button>
      </div>

      <div v-if="loading" class="loading">{{ t('common.loading') }}</div>

      <div v-else-if="filteredNotes.length === 0" class="no-notes">
        <p>{{ t('notes.noNotes') }}</p>
        <p>{{ t('notes.createFirst') }}</p>
      </div>

      <div v-else class="notes-grid">
        <div v-for="note in filteredNotes" :key="note.id" class="note-card">
          <div class="note-header">
            <h3>{{ note.title }}</h3>
            <div class="note-actions">
              <button class="btn-icon" @click="openModal(note)" :title="t('common.edit')">
                <i class="fas fa-edit"></i>
              </button>
              <button class="btn-icon danger" @click="deleteNote(note)" :title="t('common.delete')">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
          <div class="note-collection">{{ note.collection }}</div>
          <div class="note-contents">
            <div
              v-for="content in note.contents"
              :key="content.id"
              class="note-content-block"
            >
              <div
                v-if="content.contentType === 'MARKDOWN'"
                class="markdown-content"
                v-html="getRenderedContent(content)"
              ></div>
              <div v-else-if="content.contentType === 'AUDIO'" class="audio-content">
                <audio controls :src="content.filePath"></audio>
              </div>
              <div v-else-if="content.contentType === 'PDF'" class="pdf-content">
                <a :href="content.filePath" target="_blank" class="pdf-link">
                  <i class="fas fa-file-pdf"></i> View PDF
                </a>
              </div>
            </div>
          </div>
          <div class="note-date">
            {{ new Date(note.createdAt).toLocaleDateString() }}
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingNote ? t('notes.editNote') : t('notes.newNote') }}</h3>
          <button class="modal-close" @click="closeModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>{{ t('notes.noteTitle') }}</label>
            <input v-model="formTitle" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label>{{ t('notes.collection') }}</label>
            <input v-model="formCollection" type="text" class="form-input" list="collections" />
            <datalist id="collections">
              <option v-for="col in allCollections" :key="col" :value="col" />
            </datalist>
          </div>

          <div class="contents-section">
            <div v-for="(content, index) in formContents" :key="index" class="content-block">
              <div class="content-block-header">
                <select v-model="content.contentType" class="form-select">
                  <option value="MARKDOWN">Markdown</option>
                  <option value="AUDIO">Audio</option>
                  <option value="PDF">PDF</option>
                </select>
                <button
                  v-if="formContents.length > 1"
                  class="btn-icon danger"
                  @click="removeContentBlock(index)"
                >
                  <i class="fas fa-trash"></i>
                </button>
              </div>

              <div v-if="content.contentType === 'MARKDOWN'" class="markdown-editor">
                <textarea
                  v-model="content.content"
                  class="form-textarea"
                  rows="6"
                  placeholder="Write your note in Markdown... Use $...$ for inline math, $$...$$ for block math"
                ></textarea>
              </div>

              <div v-else-if="content.contentType === 'AUDIO'" class="file-upload">
                <input
                  type="file"
                  accept="audio/*"
                  @change="(e) => handleFileSelect(e, index)"
                />
                <p v-if="content.filePath" class="file-uploaded">
                  <i class="fas fa-check"></i> Audio uploaded
                </p>
              </div>

              <div v-else-if="content.contentType === 'PDF'" class="file-upload">
                <input
                  type="file"
                  accept="application/pdf"
                  @change="(e) => handleFileSelect(e, index)"
                />
                <p v-if="content.filePath" class="file-uploaded">
                  <i class="fas fa-check"></i> PDF uploaded
                </p>
              </div>
            </div>

            <button class="btn btn-secondary" @click="addContentBlock">
              <i class="fas fa-plus"></i> Add Content Block
            </button>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeModal">
            {{ t('common.cancel') }}
          </button>
          <button class="btn btn-primary" @click="saveNote" :disabled="uploading">
            {{ uploading ? t('common.loading') : t('notes.saveNote') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notes-page {
  max-width: 1200px;
  margin: 0 auto;
}

.notes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.collection-filter {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.collection-btn {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  background: var(--card-bg);
  border-radius: var(--radius);
  cursor: pointer;
  transition: var(--transition);
}

.collection-btn:hover,
.collection-btn.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.note-card {
  background: var(--card-bg);
  border: 1px solid var(--border-light);
  border-radius: var(--radius);
  padding: 1.25rem;
  transition: var(--transition);
}

.note-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow);
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.note-header h3 {
  margin: 0;
  font-size: 1.1rem;
}

.note-actions {
  display: flex;
  gap: 0.25rem;
}

.btn-icon {
  padding: 0.25rem 0.5rem;
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--text-muted);
}

.btn-icon:hover {
  color: var(--primary-color);
}

.btn-icon.danger:hover {
  color: #dc3545;
}

.note-collection {
  font-size: 0.8rem;
  color: var(--primary-color);
  margin-bottom: 0.75rem;
}

.note-contents {
  margin-bottom: 0.75rem;
}

.note-content-block {
  margin-bottom: 0.5rem;
}

.markdown-content {
  font-size: 0.9rem;
  line-height: 1.5;
}

.markdown-content :deep(.math-block) {
  margin: 1rem 0;
  overflow-x: auto;
}

.audio-content audio {
  width: 100%;
}

.pdf-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--primary-color);
}

.note-date {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.no-notes {
  text-align: center;
  padding: 3rem;
  color: var(--text-muted);
}

.loading {
  text-align: center;
  padding: 2rem;
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

.modal {
  background: var(--card-bg);
  border-radius: var(--radius);
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--border-light);
}

.modal-close {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.25rem;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--border-light);
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  background: var(--bg-color);
  color: var(--text-color);
}

.contents-section {
  margin-top: 1.5rem;
}

.content-block {
  border: 1px solid var(--border-light);
  border-radius: var(--radius);
  padding: 1rem;
  margin-bottom: 1rem;
}

.content-block-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.markdown-editor .form-textarea {
  font-family: monospace;
}

.file-upload input {
  width: 100%;
}

.file-uploaded {
  color: #28a745;
  margin-top: 0.5rem;
  font-size: 0.9rem;
}
</style>
