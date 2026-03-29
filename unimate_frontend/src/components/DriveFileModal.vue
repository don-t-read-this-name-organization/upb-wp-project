<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  url: string
  title?: string
  show: boolean
}>()

const emit = defineEmits<{
  close: []
  download: [url: string]
}>()

const fileName = computed(() => {
  if (props.title) return props.title
  const parts = props.url.split('/')
  return parts[parts.length - 1] || 'File'
})

const fileExtension = computed(() => {
  const match = fileName.value.match(/\.[^.]+$/)
  return match ? match[0].toLowerCase() : ''
})

const fileIcon = computed(() => {
  const ext = fileExtension.value
  if (['.doc', '.docx'].includes(ext)) return 'fa-file-word'
  if (['.xls', '.xlsx'].includes(ext)) return 'fa-file-excel'
  if (['.ppt', '.pptx'].includes(ext)) return 'fa-file-powerpoint'
  if (['.zip', '.rar', '.7z'].includes(ext)) return 'fa-file-archive'
  if (['.txt', '.md'].includes(ext)) return 'fa-file-alt'
  if (['.mp3', '.wav', '.flac'].includes(ext)) return 'fa-file-audio'
  if (['.mp4', '.avi', '.mov'].includes(ext)) return 'fa-file-video'
  return 'fa-file'
})

function handleDownload() {
  emit('download', props.url)
}
</script>

<template>
  <Teleport to="body">
    <div v-if="show" class="modal-overlay" @click.self="emit('close')">
      <div class="drive-modal">
        <div class="modal-header">
          <h3><i class="fas" :class="fileIcon"></i> {{ fileName }}</h3>
          <button class="modal-close" @click="emit('close')">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="file-preview">
            <i class="fas large-icon" :class="fileIcon"></i>
            <p class="file-type">{{ fileExtension.replace('.', '').toUpperCase() }} File</p>
          </div>
          <div class="file-actions">
            <a :href="url" target="_blank" class="btn btn-primary">
              <i class="fas fa-external-link-alt"></i> Open
            </a>
            <button class="btn btn-secondary" @click="handleDownload">
              <i class="fas fa-download"></i> Download
            </button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
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

.drive-modal {
  background: var(--card-bg);
  border-radius: var(--radius);
  width: 90%;
  max-width: 450px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--border-light);
}

.modal-header h3 {
  margin: 0;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.modal-close {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.25rem;
  color: var(--text-muted);
}

.modal-close:hover {
  color: var(--text-color);
}

.modal-body {
  padding: 2rem;
  text-align: center;
}

.file-preview {
  margin-bottom: 1.5rem;
}

.large-icon {
  font-size: 4rem;
  color: var(--primary-color);
  opacity: 0.7;
}

.file-type {
  color: var(--text-muted);
  margin-top: 0.5rem;
  font-size: 0.9rem;
}

.file-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
}
</style>
