<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as XLSX from 'xlsx'

const props = defineProps<{
  url: string
  title?: string
}>()

const loading = ref(true)
const error = ref<string | null>(null)
const tableHtml = ref('')

async function loadSpreadsheet() {
  loading.value = true
  error.value = null

  try {
    const response = await fetch(props.url)
    if (!response.ok) throw new Error('Failed to load file')
    
    const arrayBuffer = await response.arrayBuffer()
    const workbook = XLSX.read(arrayBuffer, { type: 'array' })
    
    const firstSheetName = workbook.SheetNames[0]
    if (!firstSheetName) throw new Error('No sheets found')
    const worksheet = workbook.Sheets[firstSheetName]
    if (!worksheet) throw new Error('Sheet not found')
    
    const html = XLSX.utils.sheet_to_html(worksheet, {
      id: 'spreadsheet-table',
      editable: false,
    })
    
    tableHtml.value = html
  } catch (e) {
    error.value = 'Could not load spreadsheet'
    console.error('Spreadsheet error:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSpreadsheet()
})
</script>

<template>
  <div class="spreadsheet-viewer">
    <div v-if="loading" class="spreadsheet-loading">
      <i class="fas fa-spinner fa-spin"></i> Loading spreadsheet...
    </div>
    <div v-else-if="error" class="spreadsheet-error">
      <i class="fas fa-exclamation-triangle"></i> {{ error }}
      <p>File: {{ title || url }}</p>
    </div>
    <div v-else class="spreadsheet-container" v-html="tableHtml"></div>
  </div>
</template>

<style scoped>
.spreadsheet-viewer {
  width: 100%;
  max-height: 600px;
  overflow: auto;
  background: var(--bg-color);
  border-radius: var(--radius);
}

.spreadsheet-loading,
.spreadsheet-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  color: var(--text-muted);
  gap: 0.5rem;
}

.spreadsheet-error {
  color: var(--accent-blush);
}

.spreadsheet-error p {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.spreadsheet-container {
  padding: 1rem;
}

.spreadsheet-container :deep(table) {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.85rem;
}

.spreadsheet-container :deep(th),
.spreadsheet-container :deep(td) {
  border: 1px solid var(--border-color);
  padding: 0.5rem;
  text-align: left;
}

.spreadsheet-container :deep(th) {
  background: var(--bg-secondary);
  font-weight: 600;
}

.spreadsheet-container :deep(tr:nth-child(even)) {
  background: var(--bg-secondary);
}
</style>
