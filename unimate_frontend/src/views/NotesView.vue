<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/appStore'
import { parseMarkdown } from '@/utils/markdown'
import { MdEditor } from 'md-editor-v3'
<<<<<<< Updated upstream
=======
import BaseModal from '@/components/BaseModal.vue'
>>>>>>> Stashed changes
import 'md-editor-v3/lib/style.css'

const { t } = useI18n()
const store = useAppStore()

interface Note {
  id: number
  title: string
  content: string
  description: string
  collection: string
  createdAt: string
}

const notes = ref<Note[]>([])
const loading = ref(true)
const collections = ref<string[]>([])
const activeFilter = ref('all')
const searchQuery = ref('')

const showEditModal = ref(false)
const showFullModal = ref(false)

const editingNote = ref<Note | null>(null)
const viewingNote = ref<Note | null>(null)

const formTitle = ref('')
const formCollection = ref('Math')
const formContent = ref('')
const formDescription = ref('')
const uploading = ref(false)

const editorRef = ref<InstanceType<typeof MdEditor> | null>(null)

const showConfirmModal = ref(false)
const confirmMessage = ref('')
const confirmCallback = ref<(() => void) | null>(null)

function showConfirm(message: string, callback: () => void) {
  confirmMessage.value = message
  confirmCallback.value = callback
  showConfirmModal.value = true
}

function handleConfirm() {
  if (confirmCallback.value) {
    confirmCallback.value()
  }
  showConfirmModal.value = false
  confirmCallback.value = null
}

function handleConfirmCancel() {
  showConfirmModal.value = false
  confirmCallback.value = null
}

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
  if (!userId) {
    loading.value = false
    return
  }

  loading.value = true
  try {
    let url = `/api/notes?userId=${userId}`
    if (searchQuery.value.trim()) {
      url += `&search=${encodeURIComponent(searchQuery.value.trim())}`
    }
    const response = await fetch(url)
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

function openEditModal(note?: Note) {
  if (note) {
    editingNote.value = note
    formTitle.value = note.title
    formCollection.value = note.collection || 'Math'
    formContent.value = note.content || ''
    formDescription.value = note.description || ''
  } else {
    editingNote.value = null
    formTitle.value = ''
    formCollection.value = 'Math'
    formContent.value = ''
    formDescription.value = ''
  }
  showEditModal.value = true
}

function closeEditModal() {
  showEditModal.value = false
  editingNote.value = null
}

function openFullModal(note: Note) {
  viewingNote.value = note
  showFullModal.value = true
}

function closeFullModal() {
  showFullModal.value = false
  viewingNote.value = null
}

async function uploadFile(file: File): Promise<string> {
  const userId = store.user?.id
  if (!userId) throw new Error('Not logged in')

  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', userId.toString())

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

const IMAGE_EXTENSIONS = ['.png', '.jpg', '.jpeg', '.gif', '.webp', '.svg']

function isImage(file: File): boolean {
  const ext = '.' + file.name.split('.').pop()?.toLowerCase()
  return IMAGE_EXTENSIONS.includes(ext)
}

async function handleDrop(dt: DataTransfer | null) {
  if (!dt) return
  
  const files = dt.files
  if (!files || !files.length) return

  const file = files[0]
  if (!file) return

  if (!isImage(file)) {
    console.warn('Only image files are supported')
    return
  }
  
  const url = await uploadFile(file)

  const markdown = `![${file.name}](${url})`

  const textarea = document.querySelector('.md-editor-textarea') as HTMLTextAreaElement
  if (textarea) {
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    formContent.value = formContent.value.slice(0, start) + markdown + formContent.value.slice(end)
    
    setTimeout(() => {
      textarea.focus()
      textarea.setSelectionRange(start + markdown.length, start + markdown.length)
    }, 0)
  } else {
    formContent.value += '\n' + markdown
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
    content: formContent.value,
    description: formDescription.value,
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
    closeEditModal()
  } catch (error) {
    console.error('Failed to save note:', error)
  }
}

async function deleteNote(note: Note) {
  showConfirm(t('common.confirm') + '?', async () => {
    try {
      const imageUrls = extractImageUrls(note.content)
      const userId = store.user?.id
      await fetch(`/api/notes/${note.id}`, { method: 'DELETE' })
      for (const url of imageUrls) {
        try {
          const filename = url.split('/').pop()
          if (filename && userId) {
            await fetch(`/api/files/by-filename?userId=${userId}&filename=${encodeURIComponent(filename)}`, { method: 'DELETE' })
          }
        } catch {
          console.warn('Failed to delete image:', url)
        }
      }
      await fetchNotes()
    } catch (error) {
      console.error('Failed to delete note:', error)
    }
  })
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
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input
              v-model="searchQuery"
              type="text"
              :placeholder="t('notes.search') || 'Search notes...'"
              @keyup.enter="fetchNotes"
            />
          </div>
          <button class="btn btn-secondary" @click="openEditModal()">
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
            <h3 @click="openFullModal(note)" class="note-title-link">
              {{ note.title }}
            </h3>
            <div class="note-actions">
              <button class="btn-icon" @click="openEditModal(note)" :title="t('common.edit')">
                <i class="fas fa-edit"></i>
              </button>
              <button class="btn-icon danger" @click="deleteNote(note)" :title="t('common.delete')">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
          <div class="note-collection">{{ note.collection }}</div>
          <div class="note-description">{{ note.description }}</div>
          <div class="note-date">
            {{ new Date(note.createdAt).toLocaleDateString() }}
          </div>
        </div>
      </div>
    </div>

<<<<<<< Updated upstream
    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
=======
    <BaseModal v-model="showEditModal" size="xl" :show-close="false" @close="closeEditModal">
>>>>>>> Stashed changes
      <div class="modal edit-modal">
        <div class="modal-header">
          <h3>{{ editingNote ? t('notes.editNote') : t('notes.newNote') }}</h3>
          <button class="modal-close" @click="closeEditModal">
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
          <div class="form-group">
            <label>Description</label>
            <textarea v-model="formDescription" class="form-input" rows="3" placeholder="Short description..."></textarea>
          </div>
          <div class="form-group">
            <label>Content</label>
            <div class="editor-hint">
              <span>Drag & drop images to embed, or use markdown syntax</span>
              <span class="hint-formats">Supports: images, markdown, formulas ($...$)</span>
            </div>
            <MdEditor
              ref="editorRef"
              v-model="formContent"
              editor-id="note-editor"
              language="en-US"
              preview
              :toolbars-exclude="['github']"
              class="markdown-editor"
              @drop.prevent="handleDrop($event.dataTransfer!)"
              @dragover.prevent
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeEditModal">
            {{ t('common.cancel') }}
          </button>
          <button class="btn btn-primary" @click="saveNote" :disabled="uploading">
            {{ uploading ? t('common.loading') : t('notes.saveNote') }}
          </button>
        </div>
      </div>
<<<<<<< Updated upstream
    </div>

    <div v-if="showFullModal && viewingNote" class="modal-overlay" @click.self="closeFullModal">
=======
    </BaseModal>

    <BaseModal v-if="viewingNote" v-model="showFullModal" size="xl" :show-close="false" @close="closeFullModal">
>>>>>>> Stashed changes
      <div class="modal full-modal">
        <div class="modal-header">
          <h3>{{ viewingNote.title }}</h3>
          <button class="modal-close" @click="closeFullModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body full-content">
          <div class="markdown-rendered" v-html="parseMarkdown(viewingNote.content)"></div>
        </div>
      </div>
<<<<<<< Updated upstream
    </div>

    <div v-if="showConfirmModal" class="modal-overlay" @click.self="handleConfirmCancel">
=======
    </BaseModal>

    <BaseModal v-model="showConfirmModal" size="sm" :show-close="false" @close="handleConfirmCancel">
>>>>>>> Stashed changes
      <div class="modal">
        <div class="modal-header">
          <h3><i class="fas fa-question-circle"></i> {{ t('common.confirm') }}</h3>
          <button class="modal-close" @click="handleConfirmCancel">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>{{ confirmMessage }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="handleConfirmCancel">
            {{ t('common.cancel') }}
          </button>
          <button class="btn btn-danger" @click="handleConfirm">
            {{ t('common.confirm') }}
          </button>
        </div>
      </div>
<<<<<<< Updated upstream
    </div>
=======
    </BaseModal>
>>>>>>> Stashed changes
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

.notes-actions {
  display: flex;
  gap: 10px;
  align-items: center;
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
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.note-title-link {
  cursor: pointer;
  margin: 0;
  font-size: 1.1rem;
}

.note-title-link:hover {
  color: var(--primary-color);
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

.note-description {
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 0.75rem;
  color: var(--text-color);
  white-space: pre-wrap;
  word-break: break-word;
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

<<<<<<< Updated upstream
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

=======
>>>>>>> Stashed changes
.modal {
  background: var(--card-bg);
  border-radius: var(--radius);
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.edit-modal {
  max-width: 900px;
}

.full-modal {
  max-width: 900px;
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

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  background: var(--bg-color);
  color: var(--text-color);
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

.markdown-editor {
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
}

.full-content {
  max-height: 70vh;
  overflow-y: auto;
}

.markdown-rendered {
  line-height: 1.6;
}

.markdown-rendered :deep(.math-block) {
  margin: 1rem 0;
  overflow-x: auto;
}

.markdown-rendered :deep(.markdown-image) {
  max-width: 100%;
  border-radius: var(--radius);
}

.markdown-rendered :deep(.drive-link) {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--primary-color);
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-box i {
  position: absolute;
  left: 12px;
  color: #888;
  font-size: 14px;
}

.search-box input {
  padding: 8px 12px 8px 36px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  width: 200px;
  transition: border-color 0.3s, width 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: var(--primary-color);
  width: 250px;
}
</style>
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
