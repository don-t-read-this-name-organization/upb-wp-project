<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/appStore'
import BaseModal from '@/components/BaseModal.vue'

interface ScheduleEntry {
  id: number
  courseName: string
  dayOfWeek: string
  startTime: string
  endTime: string
  location: string
}

const { t } = useI18n()
const store = useAppStore()

const dayKeys = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY']
const dayLabelKey: Record<string, string> = {
  MONDAY: 'monday',
  TUESDAY: 'tuesday',
  WEDNESDAY: 'wednesday',
  THURSDAY: 'thursday',
  FRIDAY: 'friday',
}

const entries = ref<ScheduleEntry[]>([])
const loading = ref(true)
const error = ref('')

const showScheduleModal = ref(false)
const editingEntry = ref<ScheduleEntry | null>(null)
const saving = ref(false)

const formCourseName = ref('')
const formDayOfWeek = ref('MONDAY')
const formStartTime = ref('08:00')
const formEndTime = ref('10:00')
const formLocation = ref('')

const timetableLoading = ref(false)
const timetableError = ref('')

const group = computed(() => store.userGroup || '-')

async function extractErrorMessage(response: Response, fallback: string): Promise<string> {
  const data = await response.json().catch(() => null)
  if (data?.message && typeof data.message === 'string') {
    return data.message
  }
  if (data?.error && typeof data.error === 'string') {
    return data.error
  }
  if (data?.fieldErrors && typeof data.fieldErrors === 'object') {
    const firstFieldError = Object.values(data.fieldErrors)[0]
    if (typeof firstFieldError === 'string' && firstFieldError.trim().length > 0) {
      return firstFieldError
    }
  }
  return fallback
}

const uniqueSlots = computed(() => {
  const slots = new Set(entries.value.map((e) => `${e.startTime}-${e.endTime}`))
  return Array.from(slots).sort()
})

const tableMatrix = computed(() => {
  const matrix: Record<string, Record<string, ScheduleEntry | null>> = {}
  uniqueSlots.value.forEach((slot) => {
    const row: Record<string, ScheduleEntry | null> = {}
    dayKeys.forEach((day) => {
      row[day] = null
    })
    matrix[slot] = row
  })

  entries.value.forEach((entry) => {
    const slot = `${entry.startTime}-${entry.endTime}`
    if (!matrix[slot]) {
      const row: Record<string, ScheduleEntry | null> = {}
      dayKeys.forEach((day) => {
        row[day] = null
      })
      matrix[slot] = row
    }
    const row = matrix[slot]
    if (row && dayKeys.includes(entry.dayOfWeek)) {
      row[entry.dayOfWeek] = entry
    }
  })

  return matrix
})

async function fetchSchedule() {
  loading.value = true
  error.value = ''
  try {
    const response = await fetch('/api/schedule')
    if (!response.ok) {
      throw new Error(await extractErrorMessage(response, t('common.error')))
    }
    entries.value = await response.json()
  } catch (err: unknown) {
    error.value = err instanceof Error ? err.message : t('common.error')
  } finally {
    loading.value = false
  }
}

function openCreateModal() {
  editingEntry.value = null
  formCourseName.value = ''
  formDayOfWeek.value = 'MONDAY'
  formStartTime.value = '08:00'
  formEndTime.value = '10:00'
  formLocation.value = ''
  showScheduleModal.value = true
}

function openEditModal(entry: ScheduleEntry) {
  editingEntry.value = entry
  formCourseName.value = entry.courseName
  formDayOfWeek.value = entry.dayOfWeek
  formStartTime.value = entry.startTime.slice(0, 5)
  formEndTime.value = entry.endTime.slice(0, 5)
  formLocation.value = entry.location
  showScheduleModal.value = true
}

function closeScheduleModal() {
  showScheduleModal.value = false
  editingEntry.value = null
}

async function saveScheduleEntry() {
  if (!formCourseName.value.trim() || !formLocation.value.trim()) {
    return
  }
  saving.value = true
  error.value = ''

  try {
    const payload = {
      courseName: formCourseName.value.trim(),
      dayOfWeek: formDayOfWeek.value,
      startTime: formStartTime.value,
      endTime: formEndTime.value,
      location: formLocation.value.trim(),
    }

    const url = editingEntry.value ? `/api/schedule/${editingEntry.value.id}` : '/api/schedule'
    const method = editingEntry.value ? 'PUT' : 'POST'

    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })

    if (!response.ok) {
      throw new Error(await extractErrorMessage(response, t('common.error')))
    }

    await fetchSchedule()
    closeScheduleModal()
  } catch (err: unknown) {
    error.value = err instanceof Error ? err.message : t('common.error')
  } finally {
    saving.value = false
  }
}

async function deleteScheduleEntry(entryId: number) {
  try {
    const response = await fetch(`/api/schedule/${entryId}`, { method: 'DELETE' })
    if (!response.ok) {
      throw new Error(await extractErrorMessage(response, t('common.error')))
    }
    await fetchSchedule()
  } catch (err: unknown) {
    error.value = err instanceof Error ? err.message : t('common.error')
  }
}

async function uploadTimetableFile(event: Event) {
  const input = event.target as HTMLInputElement
  if (!input.files?.length) {
    return
  }

  const file = input.files[0]
  input.value = ''
  if (!file) {
    return
  }

  timetableLoading.value = true
  timetableError.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await fetch('/api/timetable', {
      method: 'POST',
      body: formData,
    })

    if (!response.ok) {
      throw new Error(await extractErrorMessage(response, t('common.error')))
    }
  } catch (err: unknown) {
    timetableError.value = err instanceof Error ? err.message : t('common.error')
  } finally {
    timetableLoading.value = false
  }
}

async function downloadTimetable() {
  timetableLoading.value = true
  timetableError.value = ''
  try {
    const response = await fetch('/api/timetable')
    if (!response.ok) {
      throw new Error(await extractErrorMessage(response, t('common.error')))
    }

    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition') || ''
    const filenameMatch = contentDisposition.match(/filename="(.+)"/)
    const filename = filenameMatch?.[1] || 'timetable.pdf'

    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(url)
  } catch (err: unknown) {
    timetableError.value = err instanceof Error ? err.message : t('common.error')
  } finally {
    timetableLoading.value = false
  }
}

async function deleteTimetable() {
  timetableLoading.value = true
  timetableError.value = ''
  try {
    const response = await fetch('/api/timetable', {
      method: 'DELETE',
    })
    if (!response.ok) {
      throw new Error(await extractErrorMessage(response, t('common.error')))
    }
  } catch (err: unknown) {
    timetableError.value = err instanceof Error ? err.message : t('common.error')
  } finally {
    timetableLoading.value = false
  }
}

onMounted(() => {
  fetchSchedule()
})
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <div class="timetable-header">
        <h2 class="card-title card-title-no-margin">
          <i class="fas fa-calendar-week"></i> {{ t('timetable.weeklyTimetable') }}
        </h2>
        <div class="header-actions">
          <span class="group-badge">{{ t('timetable.group') }}: {{ group }}</span>
          <button v-if="store.isChief || store.isAdmin" class="btn btn-secondary" @click="openCreateModal">
            <i class="fas fa-plus"></i> {{ t('timetable.addEntry') }}
          </button>
        </div>
      </div>

      <div v-if="error" class="error-alert">{{ error }}</div>

      <div class="timetable-wrapper">
        <table class="timetable-table">
          <thead>
            <tr>
              <th>{{ t('timetable.time') }}</th>
              <th v-for="day in dayKeys" :key="day">{{ t(`timetable.days.${dayLabelKey[day]}`) }}</th>
            </tr>
          </thead>
          <tbody v-if="!loading && uniqueSlots.length > 0">
            <tr v-for="slot in uniqueSlots" :key="slot">
              <td class="time-cell">{{ slot }}</td>
              <td v-for="day in dayKeys" :key="`${slot}-${day}`">
                <div v-if="tableMatrix[slot]?.[day]" class="course-cell">
                  <strong>{{ tableMatrix[slot][day]?.courseName }}</strong>
                  <small>{{ tableMatrix[slot][day]?.location }}</small>
                  <div v-if="store.isChief || store.isAdmin" class="entry-actions">
                    <button class="btn-icon" @click="openEditModal(tableMatrix[slot][day]!)">
                      <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn-icon danger" @click="deleteScheduleEntry(tableMatrix[slot][day]!.id)">
                      <i class="fas fa-trash"></i>
                    </button>
                  </div>
                </div>
                <span v-else class="empty-cell">-</span>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr>
              <td :colspan="dayKeys.length + 1" class="empty-row">
                {{ loading ? t('common.loading') : t('timetable.noEntries') }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card timetable-file-card">
        <h3 class="section-title"><i class="fas fa-file-pdf"></i> {{ t('timetable.viewFullTimetable') }}</h3>
        <p class="section-subtitle">Upload, download or replace your own timetable PDF.</p>

        <div v-if="timetableError" class="error-alert">{{ timetableError }}</div>

        <div class="timetable-file-actions">
          <label v-if="store.isAdmin" class="btn btn-primary upload-btn">
            <i class="fas fa-upload"></i> Upload PDF
            <input type="file" accept="application/pdf" class="hidden-input" @change="uploadTimetableFile" />
          </label>
          <button class="btn btn-secondary" :disabled="timetableLoading" @click="downloadTimetable">
            <i class="fas fa-download"></i> Download PDF
          </button>
          <button v-if="store.isAdmin" class="btn btn-danger" :disabled="timetableLoading" @click="deleteTimetable">
            <i class="fas fa-trash"></i> Delete PDF
          </button>
        </div>
      </div>
    </div>

    <BaseModal
      v-model="showScheduleModal"
      :title="editingEntry ? 'Edit Schedule Entry' : 'Add Schedule Entry'"
      size="md"
      @close="closeScheduleModal"
    >
      <div class="form-group">
        <label>Course</label>
        <input v-model="formCourseName" class="form-control" type="text" />
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>Day</label>
          <select v-model="formDayOfWeek" class="form-control">
            <option v-for="day in dayKeys" :key="day" :value="day">
              {{ t(`timetable.days.${dayLabelKey[day]}`) }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>Location</label>
          <input v-model="formLocation" class="form-control" type="text" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>Start</label>
          <input v-model="formStartTime" class="form-control" type="time" />
        </div>
        <div class="form-group">
          <label>End</label>
          <input v-model="formEndTime" class="form-control" type="time" />
        </div>
      </div>
      <template #footer>
        <button class="btn btn-secondary" @click="closeScheduleModal">{{ t('common.cancel') }}</button>
        <button class="btn btn-primary" :disabled="saving" @click="saveScheduleEntry">
          {{ saving ? t('common.loading') : t('admin.saveChanges') }}
        </button>
      </template>
    </BaseModal>
  </main>
</template>

<style scoped>
.timetable-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.group-badge {
  background: var(--primary-color);
  color: white;
  padding: 0.45rem 1rem;
  border-radius: 25px;
  font-weight: 600;
  font-size: 0.9rem;
}

.error-alert {
  background: rgba(220, 53, 69, 0.1);
  color: #dc3545;
  padding: 0.75rem 1rem;
  border-radius: var(--radius);
  margin-bottom: 1rem;
}

.timetable-wrapper {
  overflow-x: auto;
}

.timetable-table {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
  background-color: var(--card-bg);
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
}

.timetable-table th,
.timetable-table td {
  padding: 0.9rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
  vertical-align: top;
}

.timetable-table th {
  background-color: var(--bg-secondary);
  color: var(--text-muted);
  font-weight: 600;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.time-cell {
  font-weight: 600;
  white-space: nowrap;
}

.course-cell strong {
  display: block;
  color: var(--text-color);
}

.course-cell small {
  color: var(--text-muted);
}

.entry-actions {
  margin-top: 0.35rem;
  display: flex;
  gap: 0.25rem;
}

.btn-icon {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
}

.btn-icon:hover {
  color: var(--primary-color);
}

.btn-icon.danger:hover {
  color: #dc3545;
}

.empty-cell,
.empty-row {
  color: var(--text-muted);
}

.timetable-file-card {
  margin-top: 1.5rem;
}

.section-title {
  margin: 0 0 0.25rem 0;
}

.section-subtitle {
  color: var(--text-muted);
  margin-bottom: 1rem;
}

.timetable-file-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.upload-btn {
  cursor: pointer;
}

.hidden-input {
  display: none;
}

.btn-danger {
  background: #dc3545;
  color: white;
  border: none;
}

.btn-danger:hover {
  background: #c82333;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.form-group {
  margin-bottom: 0.75rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.4rem;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.65rem 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  background: var(--bg-color);
  color: var(--text-color);
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
