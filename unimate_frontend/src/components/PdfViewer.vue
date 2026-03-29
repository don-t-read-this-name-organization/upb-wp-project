<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import * as pdfjsLib from 'pdfjs-dist'

const props = defineProps<{
  url: string
  title?: string
}>()

pdfjsLib.GlobalWorkerOptions.workerSrc = new URL(
  'pdfjs-dist/build/pdf.worker.min.mjs',
  import.meta.url
).toString()

const canvasRef = ref<HTMLCanvasElement | null>(null)
const pdfDoc = ref<pdfjsLib.PDFDocumentProxy | null>(null)
const currentPage = ref(1)
const totalPages = ref(0)
const scale = ref(1.0)
const loading = ref(true)
const error = ref('')

const scales = [0.5, 0.75, 1.0, 1.25, 1.5, 2.0]

async function loadPdf() {
  if (!props.url) return
  
  loading.value = true
  error.value = ''
  
  try {
    const loadingTask = pdfjsLib.getDocument(props.url)
    pdfDoc.value = await loadingTask.promise
    totalPages.value = pdfDoc.value.numPages
    currentPage.value = 1
    await renderPage()
  } catch (e) {
    console.error('Error loading PDF:', e)
    error.value = 'Failed to load PDF'
  } finally {
    loading.value = false
  }
}

async function renderPage() {
  if (!pdfDoc.value || !canvasRef.value) return
  
  const page = await pdfDoc.value.getPage(currentPage.value)
  const viewport = page.getViewport({ scale: scale.value })
  
  const canvas = canvasRef.value
  const context = canvas.getContext('2d')
  if (!context) return
  
  canvas.height = viewport.height
  canvas.width = viewport.width
  
  const renderContext = {
    canvasContext: context,
    viewport: viewport
  }
  
  await page.render(renderContext).promise
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
    renderPage()
  }
}

function nextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    renderPage()
  }
}

function changeScale(newScale: number) {
  scale.value = newScale
  renderPage()
}

onMounted(() => {
  loadPdf()
})

watch(() => props.url, () => {
  loadPdf()
})
</script>

<template>
  <div class="pdf-viewer">
    <div v-if="loading" class="pdf-loading">
      <i class="fas fa-spinner fa-spin"></i> Loading PDF...
    </div>
    
    <div v-else-if="error" class="pdf-error">
      <i class="fas fa-exclamation-triangle"></i> {{ error }}
    </div>
    
    <template v-else>
      <div class="pdf-toolbar">
        <div class="pdf-info">
          <span class="pdf-title">{{ title || 'PDF Viewer' }}</span>
          <span class="pdf-page-info">
            Page {{ currentPage }} of {{ totalPages }}
          </span>
        </div>
        
        <div class="pdf-controls">
          <div class="page-controls">
            <button 
              class="pdf-btn" 
              @click="prevPage" 
              :disabled="currentPage <= 1"
              title="Previous page"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <button 
              class="pdf-btn" 
              @click="nextPage" 
              :disabled="currentPage >= totalPages"
              title="Next page"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <div class="scale-controls">
            <button 
              v-for="s in scales" 
              :key="s"
              class="pdf-btn"
              :class="{ active: scale === s }"
              @click="changeScale(s)"
            >
              {{ Math.round(s * 100) }}%
            </button>
          </div>
        </div>
      </div>
      
      <div class="pdf-canvas-container">
        <canvas ref="canvasRef" class="pdf-canvas"></canvas>
      </div>
    </template>
  </div>
</template>

<style scoped>
.pdf-viewer {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 500px;
  background: var(--bg-secondary);
  border-radius: var(--radius);
  overflow: hidden;
}

.pdf-loading,
.pdf-error {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  height: 100%;
  min-height: 500px;
  color: var(--text-muted);
}

.pdf-error {
  color: #dc3545;
}

.pdf-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  flex-wrap: wrap;
  gap: 0.5rem;
}

.pdf-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.pdf-title {
  font-weight: 600;
  font-size: 0.9rem;
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pdf-page-info {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.pdf-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.page-controls,
.scale-controls {
  display: flex;
  gap: 0.25rem;
}

.pdf-btn {
  padding: 0.4rem 0.6rem;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  color: var(--text-color);
  font-size: 0.8rem;
  transition: var(--transition);
}

.pdf-btn:hover:not(:disabled) {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.pdf-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pdf-btn.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.pdf-canvas-container {
  flex: 1;
  overflow: auto;
  display: flex;
  justify-content: center;
  padding: 1rem;
  background: #525659;
}

.pdf-canvas {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}
</style>
