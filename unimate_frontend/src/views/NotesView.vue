<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

interface Note {
  id: number
  title: string
  content: string
  collection: string
  date: string
}

const notes = ref<Note[]>([
  {
    id: 1,
    title: 'Integration Techniques',
    content:
      'Key methods: substitution, integration by parts, partial fractions. Remember: ∫u dv = uv - ∫v du...',
    collection: 'Math',
    date: 'Feb 24, 2026',
  },
  {
    id: 2,
    title: 'Lab Report: Optics',
    content:
      'Experiment: Measuring focal length of converging lens. Results: f = 15.2 ± 0.3 cm. Sources of error: parallax, alignment...',
    collection: 'Physics',
    date: 'Feb 20, 2026',
  },
  {
    id: 3,
    title: 'Spring Boot Cheat Sheet',
    content:
      '@SpringBootApplication, @RestController, @Autowired, @Repository. Dependency injection tips and common annotations...',
    collection: 'Programming',
    date: 'Feb 18, 2026',
  },
])

const collections = ['Math', 'Physics', 'Programming', 'English', 'Other']
const activeFilter = ref('all')
const showModal = ref(false)
const editingNote = ref<Note | null>(null)

const formTitle = ref('')
const formContent = ref('')
const formCollection = ref('Math')

const filteredNotes = computed(() => {
  if (activeFilter.value === 'all') return notes.value
  return notes.value.filter((n) => n.collection === activeFilter.value)
})

const filterNotes = (collection: string, btn: EventTarget) => {
  activeFilter.value = collection
  ;(btn as HTMLElement).parentElement
    ?.querySelectorAll('.collection-btn')
    .forEach((b) => b.classList.remove('active'))
  ;(btn as HTMLElement).classList.add('active')
}

const openModal = (note?: Note) => {
  if (note) {
    editingNote.value = note
    formTitle.value = note.title
    formContent.value = note.content
    formCollection.value = note.collection
  } else {
    editingNote.value = null
    formTitle.value = ''
    formContent.value = ''
    formCollection.value = 'Math'
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingNote.value = null
}

const saveNote = () => {
  if (!formTitle.value.trim() || !formContent.value.trim()) return

  if (editingNote.value) {
    const idx = notes.value.findIndex((n) => n.id === editingNote.value?.id)
    if (idx !== -1) {
      notes.value[idx] = {
        ...notes.value[idx],
        title: formTitle.value,
        content: formContent.value,
        collection: formCollection.value,
      }
    }
  } else {
    notes.value.unshift({
      id: Date.now(),
      title: formTitle.value,
      content: formContent.value,
      collection: formCollection.value,
      date: new Date().toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      }),
    })
  }
  closeModal()
}

const deleteNote = (id: number) => {
  if (confirm('Delete this note permanently?')) {
    notes.value = notes.value.filter((n) => n.id !== id)
  }
}

const viewNote = (note: Note) => {
  openModal(note)
}
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <div class="notes-header">
        <h2 class="card-title card-title-no-margin"><i class="fas fa-sticky-note"></i> {{ t('notes.title') }}</h2>
        <div class="notes-actions">
          <button class="btn btn-secondary" @click="openModal()">
            <i class="fas fa-plus"></i> {{ t('notes.newNote') }}
          </button>
        </div>
      </div>

      <div class="collection-filter">
        <button class="collection-btn active" @click="filterNotes('all', $event)">All</button>
        <button
          v-for="col in collections"
          :key="col"
          class="collection-btn"
          @click="filterNotes(col, $event)"
        >
          {{ col }}
        </button>
      </div>

      <div v-if="filteredNotes.length > 0" class="notes-grid">
        <div v-for="note in filteredNotes" :key="note.id" class="note-card" @click="viewNote(note)">
          <div class="note-card-header">
            <h3 class="note-title">{{ note.title }}</h3>
            <div class="note-actions">
              <button @click.stop="openModal(note)" title="Edit note">
                <i class="fas fa-edit"></i>
              </button>
              <button class="delete" @click.stop="deleteNote(note.id)" title="Delete note">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
          <div class="note-preview">{{ note.content }}</div>
          <div class="note-meta">
            <span class="note-collection">{{ note.collection }}</span>
            <span class="note-date">{{ note.date }}</span>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <i class="fas fa-clipboard-list"></i>
        <h3>{{ t('notes.noNotes') }}</h3>
        <p>{{ t('notes.createFirst') }}</p>
        <button class="btn btn-primary add-button-margin" @click="openModal()">
          <i class="fas fa-plus"></i> Create Note
        </button>
      </div>
    </div>
  </main>

  <div v-if="showModal" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ editingNote ? t('notes.editNote') : t('notes.newNote') }}</h3>
        <button class="modal-close" @click="closeModal">&times;</button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label for="noteTitle">Title</label>
          <input
            v-model="formTitle"
            type="text"
            id="noteTitle"
            class="form-control"
            placeholder="Note title..."
          />
        </div>
        <div class="form-group">
          <label for="noteCollection">Collection</label>
          <select v-model="formCollection" id="noteCollection" class="form-control">
            <option v-for="col in collections" :key="col" :value="col">{{ col }}</option>
          </select>
        </div>
        <div class="form-group">
          <label for="noteContent">Content</label>
          <textarea
            v-model="formContent"
            id="noteContent"
            class="note-editor"
            placeholder="Start typing your note..."
          ></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="closeModal">Cancel</button>
        <button class="btn btn-primary" @click="saveNote">Save Note</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}
.notes-actions {
  display: flex;
  gap: 0.75rem;
}
.collection-filter {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1.5rem;
}
.collection-btn {
  padding: 0.4rem 1rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: var(--transition);
}
.collection-btn.active,
.collection-btn:hover {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}
.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}
.note-card {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: var(--shadow);
  transition: var(--transition);
  position: relative;
  cursor: pointer;
  border: 2px solid transparent;
}
.note-card:hover {
  transform: translateY(-5px);
  border-color: var(--primary-color);
}
.note-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}
.note-title {
  font-weight: 600;
  font-size: 1.1rem;
  color: var(--primary-color);
  margin: 0;
  flex: 1;
}
.note-actions {
  display: flex;
  gap: 0.25rem;
  opacity: 0;
  transition: var(--transition);
}
.note-card:hover .note-actions {
  opacity: 1;
}
.note-actions button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  font-size: 1rem;
  color: var(--text-muted);
  transition: var(--transition);
}
.note-actions button:hover {
  color: var(--primary-color);
}
.note-actions button.delete:hover {
  color: #dc3545;
}
.note-preview {
  color: var(--text-color);
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.note-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: var(--text-muted);
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}
.note-collection {
  background: var(--bg-secondary);
  padding: 0.2rem 0.75rem;
  border-radius: 12px;
  font-weight: 500;
}
.empty-state {
  text-align: center;
  padding: 3rem;
  color: var(--text-muted);
}
.empty-state i {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}
.modal-content {
  background: var(--card-bg);
  border-radius: 15px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}
.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-header h3 {
  margin: 0;
  color: var(--primary-color);
}
.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--text-muted);
}
.modal-body {
  padding: 1.5rem;
}
.modal-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}
.note-editor {
  width: 100%;
  min-height: 200px;
  padding: 1rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-color);
  color: var(--text-color);
  font-family: inherit;
  font-size: 1rem;
  line-height: 1.6;
  resize: vertical;
}
.note-editor:focus {
  outline: none;
  border-color: var(--primary-color);
}
.add-button-margin {
  margin-top: 1rem;
}
</style>
