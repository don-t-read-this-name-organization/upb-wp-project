<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/appStore'
import PdfViewer from '@/components/PdfViewer.vue'
import SpreadsheetViewer from '@/components/SpreadsheetViewer.vue'

const { t } = useI18n()
const store = useAppStore()

interface FileItem {
  id: number
  filename: string
  displayName: string
  filePath: string
  fileType: string
  fileSize: number
  folderId: number | null
  createdAt: string
}

interface Folder {
  id: number
  name: string
  parentId: number | null
  createdAt: string
}

const files = ref<FileItem[]>([])
const folders = ref<Folder[]>([])
const loading = ref(true)
const uploading = ref(false)
const activeFilter = ref('all')
const searchQuery = ref('')
const editingFileId = ref<number | null>(null)
const editingName = ref('')
const currentFolderId = ref<number | null>(null)
const folderBreadcrumb = ref<Folder[]>([])
const showNewFolderModal = ref(false)
const newFolderName = ref('')

const showPreviewModal = ref(false)
const previewFile = ref<FileItem | null>(null)
const previewType = ref('')

const showConfirmModal = ref(false)
const confirmMessage = ref('')
const confirmCallback = ref<(() => void) | null>(null)

const showAlertModal = ref(false)
const alertMessage = ref('')

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

function showAlert(message: string) {
  alertMessage.value = message
  showAlertModal.value = true
}

const FILE_TYPES = [
  { value: 'all', label: 'All Files', icon: 'fa-folder' },
  { value: 'pdf', label: 'PDF', icon: 'fa-file-pdf' },
  { value: 'image', label: 'Images', icon: 'fa-image' },
  { value: 'audio', label: 'Audio', icon: 'fa-file-audio' },
  { value: 'video', label: 'Video', icon: 'fa-file-video' },
  { value: 'spreadsheet', label: 'Spreadsheets', icon: 'fa-file-excel' },
  { value: 'other', label: 'Other', icon: 'fa-file' },
]

const filteredFiles = computed(() => {
  let result = files.value.filter(f => f.folderId === currentFolderId.value)

  if (activeFilter.value !== 'all') {
    if (activeFilter.value === 'other') {
      const otherTypes = ['pdf', 'png', 'jpg', 'jpeg', 'gif', 'webp', 'mp3', 'flac', 'wav', 'mp4', 'webm', 'xlsx', 'xls', 'csv', 'ppt', 'pptx']
      result = result.filter(f => !otherTypes.includes(f.fileType || ''))
    } else {
      result = result.filter(f => f.fileType === activeFilter.value)
    }
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(f => 
      f.filename.toLowerCase().includes(query) || 
      (f.displayName && f.displayName.toLowerCase().includes(query))
    )
  }

  return result
})

function getFileIcon(fileType: string): string {
  const type = (fileType || '').toLowerCase()
  if (type === 'pdf') return 'fa-file-pdf'
  if (['png', 'jpg', 'jpeg', 'gif', 'webp', 'svg'].includes(type)) return 'fa-image'
  if (['mp3', 'flac', 'wav', 'ogg', 'm4a'].includes(type)) return 'fa-file-audio'
  if (['mp4', 'webm', 'mov', 'avi'].includes(type)) return 'fa-file-video'
  if (['xlsx', 'xls', 'csv', 'ods'].includes(type)) return 'fa-file-excel'
  if (['doc', 'docx'].includes(type)) return 'fa-file-word'
  if (['ppt', 'pptx'].includes(type)) return 'fa-file-powerpoint'
  return 'fa-file'
}

function formatFileSize(bytes: number): string {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString()
}

async function fetchFiles() {
  const userId = store.user?.id
  if (!userId) {
    loading.value = false
    return
  }

  loading.value = true
  try {
    let url = `/api/files?userId=${userId}`
    if (currentFolderId.value !== null) {
      url += `&folderId=${currentFolderId.value}`
    }
    const response = await fetch(url)
    if (response.ok) {
      files.value = await response.json()
    }
  } catch (error) {
    console.error('Failed to fetch files:', error)
  } finally {
    loading.value = false
  }
}

async function fetchFolders() {
  const userId = store.user?.id
  if (!userId) return

  try {
    let url = `/api/folders?userId=${userId}`
    if (currentFolderId.value !== null) {
      url += `&parentId=${currentFolderId.value}`
    }
    const response = await fetch(url)
    if (response.ok) {
      folders.value = await response.json()
    }
  } catch (error) {
    console.error('Failed to fetch folders:', error)
  }
}

async function createFolder() {
  if (!newFolderName.value.trim()) return

  const userId = store.user?.id
  if (!userId) return

  try {
    const response = await fetch(`/api/folders?userId=${userId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: newFolderName.value.trim(),
        parentId: currentFolderId.value
      })
    })

    if (response.ok) {
      const newFolder = await response.json()
      folders.value.push(newFolder)
      newFolderName.value = ''
      showNewFolderModal.value = false
    }
  } catch (error) {
    console.error('Failed to create folder:', error)
  }
}

function openFolder(folder: Folder) {
  folderBreadcrumb.value.push(folder)
  currentFolderId.value = folder.id
  fetchFiles()
  fetchFolders()
}

function navigateToFolder(index: number) {
  if (index === -1) {
    folderBreadcrumb.value = []
    currentFolderId.value = null
  } else {
    folderBreadcrumb.value = folderBreadcrumb.value.slice(0, index + 1)
    const lastFolder = folderBreadcrumb.value[folderBreadcrumb.value.length - 1]
    currentFolderId.value = lastFolder ? lastFolder.id : null
  }
  fetchFiles()
  fetchFolders()
}

async function deleteFolder(folder: Folder) {
  showConfirm(t('files.deleteFolderConfirm'), async () => {
    const userId = store.user?.id
    if (!userId) return

    try {
      await fetch(`/api/folders/${folder.id}?userId=${userId}`, { method: 'DELETE' })
      folders.value = folders.value.filter(f => f.id !== folder.id)
      if (currentFolderId.value === folder.id) {
        navigateToFolder(folderBreadcrumb.value.length - 2)
      }
    } catch (error) {
      console.error('Failed to delete folder:', error)
    }
  })
}

async function handleUpload(event: Event) {
  const input = event.target as HTMLInputElement
  if (!input.files?.length) return

  const file = input.files[0]
  if (file) {
    await uploadFile(file)
  }
  input.value = ''
}

async function handleDrop(event: DragEvent) {
  event.preventDefault()
  if (!event.dataTransfer?.files.length) return

  const file = event.dataTransfer.files[0]
  if (file) {
    await uploadFile(file)
  }
}

async function uploadFile(file: File) {
  const userId = store.user?.id
  if (!userId) return

  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    showAlert(t('files.fileTooLarge'))
    return
  }

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId.toString())
    if (currentFolderId.value !== null) {
      formData.append('folderId', currentFolderId.value.toString())
    }

    const response = await fetch('/api/files', {
      method: 'POST',
      body: formData,
    })

    if (response.ok) {
      const newFile = await response.json()
      files.value.unshift(newFile)
    } else {
      showAlert(t('files.uploadFailed'))
    }
  } catch (error) {
    console.error('Upload failed:', error)
    showAlert(t('files.uploadFailed'))
  } finally {
    uploading.value = false
  }
}

async function deleteFile(file: FileItem) {
  showConfirm(t('files.deleteFileConfirm'), async () => {
    try {
      await fetch(`/api/files/${file.id}`, { method: 'DELETE' })
      files.value = files.value.filter(f => f.id !== file.id)
    } catch (error) {
      console.error('Failed to delete file:', error)
    }
  })
}

function getPreviewType(fileType: string): string {
  const type = (fileType || '').toLowerCase()
  if (['png', 'jpg', 'jpeg', 'gif', 'webp', 'svg', 'bmp'].includes(type)) return 'image'
  if (type === 'pdf') return 'pdf'
  if (['mp3', 'flac', 'wav', 'ogg', 'm4a', 'aac'].includes(type)) return 'audio'
  if (['mp4', 'webm', 'mov', 'avi'].includes(type)) return 'video'
  if (['xlsx', 'xls', 'csv', 'ods'].includes(type)) return 'spreadsheet'
  return ''
}

function openFile(file: FileItem) {
  const type = getPreviewType(file.fileType || '')
  if (type) {
    previewFile.value = file
    previewType.value = type
    showPreviewModal.value = true
  } else {
    window.open(`/api/files/${file.id}/download`, '_blank')
  }
}

function closePreviewModal() {
  showPreviewModal.value = false
  previewFile.value = null
  previewType.value = ''
}

function getDisplayName(file: FileItem): string {
  return file.displayName || file.filename
}

function startEditName(file: FileItem) {
  editingFileId.value = file.id
  editingName.value = file.displayName || file.filename
}

function cancelEditName() {
  editingFileId.value = null
  editingName.value = ''
}

async function saveEditName(file: FileItem) {
  if (!editingName.value.trim()) {
    cancelEditName()
    return
  }

  try {
    const response = await fetch(`/api/files/${file.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ displayName: editingName.value.trim() })
    })

    if (response.ok) {
      const updatedFile = await response.json()
      const index = files.value.findIndex(f => f.id === file.id)
      if (index !== -1) {
        files.value[index] = updatedFile
      }
    }
  } catch (error) {
    console.error('Failed to update file name:', error)
  }

  editingFileId.value = null
  editingName.value = ''
}

function filterByType(type: string, btn: Event) {
  activeFilter.value = type
  const target = btn.target as HTMLElement
  target.parentElement
    ?.querySelectorAll('.filter-btn')
    .forEach((b) => b.classList.remove('active'))
  target.classList.add('active')
}

onMounted(() => {
  fetchFiles()
  fetchFolders()
})

watch(
  () => store.user,
  () => {
    fetchFiles()
    fetchFolders()
  }
)
</script>

<template>
  <div class="files-page">
    <div class="card fade-in">
      <div class="files-header">
        <h2 class="card-title card-title-no-margin">
          <i class="fas fa-hdd"></i> {{ t('files.title') || 'My Files' }}
        </h2>
        <div class="header-actions">
          <button class="btn btn-secondary" @click="showNewFolderModal = true">
            <i class="fas fa-folder-plus"></i> {{ t('files.newFolder') }}
          </button>
          <label class="btn btn-primary upload-btn">
            <i class="fas fa-upload"></i> {{ t('files.upload') || 'Upload' }}
            <input
              type="file"
              class="hidden-input"
              @change="handleUpload"
            />
          </label>
        </div>
      </div>

      <div class="folder-breadcrumb">
        <span class="breadcrumb-item" @click="navigateToFolder(-1)">
          <i class="fas fa-home"></i> {{ t('files.home') }}
        </span>
        <template v-for="(folder, index) in folderBreadcrumb" :key="folder.id">
          <span class="breadcrumb-separator">/</span>
          <span class="breadcrumb-item" @click="navigateToFolder(index)">
            {{ folder.name }}
          </span>
        </template>
      </div>

      <div class="files-toolbar">
        <div class="filter-buttons">
          <button
            v-for="ft in FILE_TYPES"
            :key="ft.value"
            class="filter-btn"
            :class="{ active: activeFilter === ft.value }"
            @click="filterByType(ft.value, $event)"
          >
            <i class="fas" :class="ft.icon"></i> {{ ft.label }}
          </button>
        </div>
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input
            v-model="searchQuery"
            type="text"
            :placeholder="t('files.search') || 'Search files...'"
          />
        </div>
      </div>

      <div
        class="upload-zone"
        @drop.prevent="handleDrop"
        @dragover.prevent
        @dragenter.prevent
      >
        <div v-if="uploading" class="upload-progress">
          <i class="fas fa-spinner fa-spin"></i> {{ t('files.uploading') }}
        </div>
        <div v-else class="upload-hint">
          <i class="fas fa-cloud-upload-alt"></i>
          <p>{{ t('files.dragDropHint') }}</p>
        </div>
      </div>

      <div v-if="loading" class="loading">{{ t('common.loading') }}</div>

      <div v-else-if="folders.length === 0 && filteredFiles.length === 0" class="no-files">
        <i class="fas fa-folder-open"></i>
        <p>{{ t('files.noFiles') || 'No files yet. Upload some!' }}</p>
      </div>

      <div v-else class="files-grid">
        <div v-for="folder in folders" :key="folder.id" class="file-card folder-card" @dblclick="openFolder(folder)">
          <div class="file-icon folder-icon">
            <i class="fas fa-folder"></i>
          </div>
          <div class="file-info">
            <h4 class="file-name">{{ folder.name }}</h4>
            <div class="file-meta">
              <span>{{ t('files.folder') }}</span>
            </div>
          </div>
          <div class="file-actions">
            <button class="btn-icon danger" @click.stop="deleteFolder(folder)" :title="t('common.delete')">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
        <div v-for="file in filteredFiles" :key="file.id" class="file-card">
          <div class="file-icon" @click="openFile(file)">
            <i class="fas" :class="getFileIcon(file.fileType)"></i>
          </div>
          <div class="file-info">
            <template v-if="editingFileId === file.id">
              <input 
                v-model="editingName" 
                class="edit-name-input"
                @keyup.enter="saveEditName(file)"
                @keyup.escape="cancelEditName"
                @blur="saveEditName(file)"
                autofocus
              />
            </template>
            <template v-else>
              <h4 class="file-name" :title="file.filename" @dblclick="startEditName(file)">
                {{ getDisplayName(file) }}
              </h4>
            </template>
            <div class="file-meta">
              <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
              <span class="file-date">{{ formatDate(file.createdAt) }}</span>
            </div>
          </div>
          <div class="file-actions">
            <button class="btn-icon" @click="openFile(file)" :title="t('files.download')">
              <i class="fas fa-download"></i>
            </button>
            <button class="btn-icon" @click="startEditName(file)" :title="t('files.rename')">
              <i class="fas fa-edit"></i>
            </button>
            <button class="btn-icon danger" @click="deleteFile(file)" :title="t('common.delete')">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showNewFolderModal" class="modal-overlay" @click.self="showNewFolderModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ t('files.newFolder') }}</h3>
          <button class="modal-close" @click="showNewFolderModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>{{ t('files.folderName') }}</label>
            <input 
              v-model="newFolderName" 
              type="text" 
              class="form-input"
              :placeholder="t('files.enterFolderName')"
              @keyup.enter="createFolder"
              autofocus
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showNewFolderModal = false">
            {{ t('common.cancel') }}
          </button>
          <button class="btn btn-primary" @click="createFolder">
            {{ t('files.create') }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showPreviewModal && previewFile" class="modal-overlay" @click.self="closePreviewModal">
      <div class="modal preview-modal">
        <div class="modal-header">
          <h3><i class="fas" :class="getFileIcon(previewFile.fileType || '')"></i> {{ previewFile.displayName || previewFile.filename }}</h3>
          <button class="modal-close" @click="closePreviewModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body preview-content">
          <template v-if="previewType === 'image'">
            <img :src="previewFile.filePath" :alt="previewFile.filename" class="preview-image" />
          </template>
          <template v-else-if="previewType === 'pdf'">
            <PdfViewer :url="previewFile.filePath" :title="previewFile.displayName || previewFile.filename" />
          </template>
          <template v-else-if="previewType === 'audio'">
            <audio controls class="preview-audio">
              <source :src="previewFile.filePath" />
              Your browser does not support audio.
            </audio>
          </template>
          <template v-else-if="previewType === 'video'">
            <video controls class="preview-video">
              <source :src="previewFile.filePath" />
              Your browser does not support video.
            </video>
          </template>
          <template v-else-if="previewType === 'spreadsheet'">
            <SpreadsheetViewer :url="previewFile.filePath" />
          </template>
        </div>
      </div>
    </div>

    <div v-if="showConfirmModal" class="modal-overlay" @click.self="handleConfirmCancel">
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
    </div>

    <div v-if="showAlertModal" class="modal-overlay" @click.self="showAlertModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3><i class="fas fa-info-circle"></i> {{ t('common.error') }}</h3>
          <button class="modal-close" @click="showAlertModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>{{ alertMessage }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="showAlertModal = false">
            OK
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.files-page {
  max-width: 1200px;
  margin: 0 auto;
}

.files-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.header-actions {
  display: flex;
  gap: 0.5rem;
}

.folder-breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.breadcrumb-item {
  cursor: pointer;
  color: var(--primary-color);
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.breadcrumb-item:hover {
  text-decoration: underline;
}

.breadcrumb-separator {
  color: var(--text-muted);
}

.upload-btn {
  cursor: pointer;
}

.hidden-input {
  display: none;
}

.files-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.filter-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  background: var(--card-bg);
  border-radius: var(--radius);
  cursor: pointer;
  transition: var(--transition);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
}

.filter-btn:hover,
.filter-btn.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.search-box {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  background: var(--bg-color);
}

.search-box input {
  border: none;
  background: transparent;
  outline: none;
  width: 200px;
  color: var(--text-color);
}

.upload-zone {
  border: 2px dashed var(--border-color);
  border-radius: var(--radius);
  padding: 2rem;
  margin-bottom: 1.5rem;
  text-align: center;
  transition: var(--transition);
}

.upload-zone:hover {
  border-color: var(--primary-color);
  background: var(--bg-secondary);
}

.upload-hint {
  color: var(--text-muted);
}

.upload-hint i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.upload-progress {
  color: var(--primary-color);
}

.loading {
  text-align: center;
  padding: 2rem;
}

.no-files {
  text-align: center;
  padding: 3rem;
  color: var(--text-muted);
}

.no-files i {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.files-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}

.file-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: var(--bg-secondary);
  border-radius: var(--radius);
  transition: var(--transition);
}

.file-card:hover {
  background: var(--border-color);
}

.file-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  color: white;
  border-radius: var(--radius);
  font-size: 1.25rem;
  cursor: pointer;
  flex-shrink: 0;
}

.file-icon:hover {
  opacity: 0.8;
}

.folder-icon {
  background: #f59e0b;
}

.folder-card {
  cursor: pointer;
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  margin: 0;
  font-size: 0.95rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

.file-name:hover {
  color: var(--primary-color);
}

.edit-name-input {
  width: 100%;
  padding: 0.25rem 0.5rem;
  font-size: 0.95rem;
  border: 1px solid var(--primary-color);
  border-radius: var(--radius-sm);
  background: var(--bg-color);
  color: var(--text-color);
  outline: none;
}

.file-meta {
  display: flex;
  gap: 0.75rem;
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-top: 0.25rem;
}

.file-actions {
  display: flex;
  gap: 0.25rem;
}

.btn-icon {
  padding: 0.4rem 0.6rem;
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--text-muted);
  border-radius: var(--radius-sm);
  transition: var(--transition);
}

.btn-icon:hover {
  background: var(--card-bg);
  color: var(--primary-color);
}

.btn-icon.danger:hover {
  color: #dc3545;
}

.btn-danger {
  background: #dc3545;
  color: white;
  border: none;
}

.btn-danger:hover {
  background: #c82333;
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
  max-width: 400px;
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

.preview-modal {
  max-width: 90vw;
  width: 90vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.preview-modal .modal-body {
  flex: 1;
  overflow: auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-content {
  padding: 1rem;
}

.preview-image {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
  border-radius: var(--radius);
}

.preview-audio {
  width: 100%;
  max-width: 600px;
}

.preview-video {
  width: 100%;
  max-width: 800px;
  max-height: 80vh;
  border-radius: var(--radius);
}
</style>
