<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const group = '1231EB'

interface Course {
  name: string
  type: string
  room: string
}

interface DaySchedule {
  [timeSlot: string]: Course | null
}

const timetable: Record<string, DaySchedule> = {
  '08:00-10:00': {
    monday: null,
    tuesday: null,
    wednesday: { name: 'DCP', type: 'C', room: 'AN024' },
    thursday: null,
    friday: null,
    saturday: null,
  },
  '10:00-12:00': {
    monday: { name: 'SDM', type: 'P', room: 'CJ104' },
    tuesday: { name: 'WP2', type: 'C', room: 'AN024' },
    wednesday: { name: 'AI', type: 'C', room: 'AN024' },
    thursday: null,
    friday: null,
    saturday: null,
  },
  '12:00-14:00': {
    monday: { name: 'SDM', type: 'L', room: 'CJ201' },
    tuesday: { name: 'SDM', type: 'C', room: 'AN024' },
    wednesday: { name: 'HCI', type: 'C', room: 'CB205' },
    thursday: null,
    friday: { name: 'NN&GA', type: 'L', room: 'CJ102' },
    saturday: null,
  },
  '14:00-16:00': {
    monday: { name: 'HCI', type: 'P', room: 'CJ201' },
    tuesday: { name: 'FP', type: 'C', room: 'EC102' },
    wednesday: null,
    thursday: { name: 'WP2', type: 'P', room: 'CB105' },
    friday: { name: 'FP', type: 'L', room: 'cantiCTI' },
    saturday: null,
  },
  '16:00-18:00': {
    monday: null,
    tuesday: null,
    wednesday: { name: 'DCP', type: 'L', room: 'CJ205' },
    thursday: { name: 'BA', type: 'S', room: 'CB205' },
    friday: null,
    saturday: null,
  },
  '18:00-20:00': {
    monday: null,
    tuesday: null,
    wednesday: { name: 'WP2', type: 'L', room: 'CJ101' },
    thursday: null,
    friday: null,
    saturday: null,
  },
}

const timeSlots = Object.keys(timetable)
const days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday']
const dayKeys = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday']
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <div class="timetable-header">
        <h2 class="card-title card-title-no-margin">
          <i class="fas fa-calendar-week"></i> {{ t('timetable.weeklyTimetable') }}
        </h2>
        <span class="group-badge">{{ t('timetable.group') }}: {{ group }}</span>
      </div>

      <div class="timetable-wrapper">
        <table class="timetable-table">
          <thead>
            <tr>
              <th>{{ t('timetable.time') }}</th>
              <th v-for="day in dayKeys" :key="day">{{ t(`timetable.days.${day}`) }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="slot in timeSlots" :key="slot">
              <td class="time-cell">{{ slot }}</td>
              <td v-for="day in days" :key="day">
                <div v-if="timetable[slot][day]" class="course-cell">
                  <strong>{{ timetable[slot][day]?.name }}</strong>
                  <small>{{ timetable[slot][day]?.type }} - {{ timetable[slot][day]?.room }}</small>
                </div>
                <span v-else class="empty-cell">—</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

        <div class="sef-info">
        <div class="sef-avatar">S</div>
        <div class="sef-details">
          <h4>{{ t('timetable.yourSef') }}</h4>
          <p><strong>Maria Sef</strong> - Group {{ group }}</p>
          <p>
            <a href="mailto:sef@unimate.ro"><i class="fas fa-envelope"></i> sef@unimate.ro</a>
          </p>
          <p class="sef-office-hours">
            {{ t('timetable.contactSef') }}
          </p>
        </div>
      </div>

      <a href="https://fils.upb.ro/ro/orar/" target="_blank" class="pdf-link">
        <i class="fas fa-file-pdf"></i>
        {{ t('timetable.viewFullTimetable') }}
      </a>
    </div>
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
.group-badge {
  background: var(--primary-color);
  color: white;
  padding: 0.5rem 1.5rem;
  border-radius: 25px;
  font-weight: 600;
  font-size: 1.1rem;
}
.timetable-wrapper {
  overflow-x: auto;
}
.timetable-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--card-bg);
  border-radius: 10px;
  overflow: hidden;
  box-shadow: var(--shadow);
  font-size: 0.9rem;
}
.timetable-table th {
  background: var(--primary-color);
  color: white;
  padding: 1rem;
  text-align: center;
  font-weight: 600;
}
.timetable-table td {
  padding: 1rem;
  text-align: center;
  border-bottom: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);
  min-height: 80px;
  vertical-align: top;
}
.timetable-table td:last-child {
  border-right: none;
}
.time-cell {
  font-weight: 600;
  background: var(--bg-secondary);
  border-right: 2px solid var(--border-color) !important;
}
.course-cell {
  background: var(--bg-secondary);
  padding: 0.5rem;
  border-radius: 5px;
  font-size: 0.85rem;
}
.course-cell strong {
  display: block;
  color: var(--primary-color);
  margin-bottom: 0.25rem;
}
.course-cell small {
  color: var(--text-muted);
}
.empty-cell {
  color: var(--text-muted);
  font-style: italic;
  font-size: 0.85rem;
}
.sef-info {
  background: var(--bg-secondary);
  border-radius: 10px;
  padding: 1.5rem;
  margin: 2rem 0;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}
.sef-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 1.5rem;
}
.sef-details h4 {
  margin-bottom: 0.25rem;
}
.sef-details a {
  color: var(--primary-color);
  text-decoration: none;
}
.sef-details a:hover {
  text-decoration: underline;
}
.pdf-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  padding: 0.75rem 1.5rem;
  background: var(--bg-secondary);
  border-radius: 8px;
  transition: var(--transition);
  margin-top: 1rem;
}
.pdf-link:hover {
  background: var(--primary-color);
  color: white;
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
  padding: 0.9rem 1.1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
}

.timetable-table th {
  background-color: var(--bg-secondary);
  color: var(--text-muted);
  font-weight: 600;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.timetable-table tr:last-child td {
  border-bottom: none;
}

.timetable-table tr:hover td {
  background-color: var(--bg-secondary);
}
</style>
