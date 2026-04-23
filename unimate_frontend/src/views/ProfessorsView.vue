<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/appStore'
import ProfessorCard from '@/components/ProfessorCard.vue'
import BaseModal from '@/components/BaseModal.vue'

interface Professor {
  id: number
  name: string
  department: string | null
  faculty: string | null
  phone: string | null
  email: string | null
  officeLocation: string | null
  officeHours: string | null
}

const { t } = useI18n()
const store = useAppStore()

const loading = ref(true)
const error = ref('')
const professors = ref<Professor[]>([])

const showContactModal = ref(false)
const selectedProfessor = ref<Professor | null>(null)

const deanKeywords = ['dean', 'vice-dean', 'decan', 'secretariat', 'secretary']

const decanatStaff = computed(() =>
  professors.value.filter((p) => {
    const dep = (p.department || '').toLowerCase()
    return deanKeywords.some((key) => dep.includes(key))
  }),
)

const teachingStaff = computed(() =>
  professors.value.filter((p) => !decanatStaff.value.some((d) => d.id === p.id)),
)

function mapProfessorCard(professor: Professor) {
  const color = (professor.id * 74123).toString(16).slice(0, 6).padEnd(6, '9')
  const initials = professor.name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((word) => word[0])
    .join('')
    .toUpperCase()
  return {
    name: professor.name,
    title: professor.department || professor.faculty || 'Professor',
    color,
    initials: initials || 'P',
  }
}

async function fetchProfessors() {
  loading.value = true
  error.value = ''
  try {
    const response = await fetch('/api/professors')
    if (!response.ok) {
      throw new Error('Failed to load professors')
    }
    professors.value = await response.json()
  } catch {
    error.value = t('common.error')
  } finally {
    loading.value = false
  }
}

function openContactModal(professor: Professor) {
  selectedProfessor.value = professor
  showContactModal.value = true
}

function closeContactModal() {
  showContactModal.value = false
  selectedProfessor.value = null
}

onMounted(fetchProfessors)
</script>

<template>
  <main class="main-content">
    <div class="prof-layout">
      <div class="carousels-container">
        <div v-if="error" class="error-alert">{{ error }}</div>
        <div v-if="loading" class="loading">{{ t('common.loading') }}</div>

        <template v-else>
          <h3 class="section-title">{{ t('professors.deansOffice') }}</h3>
          <div class="carousel-wrapper">
            <div class="carousel">
              <div v-for="professor in decanatStaff" :key="professor.id" class="prof-wrap">
                <ProfessorCard :prof="mapProfessorCard(professor)" />
                <button class="btn btn-secondary contact-btn" @click="openContactModal(professor)">
                  <i class="fas fa-info-circle"></i> Contact Info
                </button>
              </div>
            </div>
          </div>

          <h3 class="section-title">{{ t('professors.professorsStaff') }}</h3>
          <div class="carousel-wrapper">
            <div class="carousel">
              <div v-for="professor in teachingStaff" :key="professor.id" class="prof-wrap">
                <ProfessorCard :prof="mapProfessorCard(professor)" />
                <button class="btn btn-secondary contact-btn" @click="openContactModal(professor)">
                  <i class="fas fa-info-circle"></i> Contact Info
                </button>
              </div>
            </div>
          </div>
        </template>
      </div>

      <aside class="prof-sidebar">
        <div class="card">
          <h3 class="card-title">{{ t('professors.secretariat') }}</h3>

          <div class="info-box">
            <h4><i class="fas fa-map-marker-alt"></i> {{ t('professors.location') }}</h4>
            <p>FILS Building, Politehnica University<br />313 Splaiul Independentei, Bucharest</p>
          </div>

          <div class="info-box">
            <h4><i class="fas fa-clock"></i> {{ t('professors.workingHours') }}</h4>
            <div class="office-hours-list">
              <strong>Secretariat:</strong> Mon-Thu 9:00-16:00<br />
              <strong>Deanery:</strong> Mon-Fri 10:00-15:00
            </div>
          </div>

          <div class="info-box">
            <h4><i class="fas fa-headset"></i> {{ t('professors.contact') }}</h4>
            <div class="info-item"><i class="fas fa-phone"></i> +40 21 345 1000</div>
            <div class="info-item">
              <i class="fas fa-envelope"></i>
              <a href="mailto:secretariat@upb.ro">secretariat@upb.ro</a>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <BaseModal
      v-if="selectedProfessor"
      v-model="showContactModal"
      :title="`${selectedProfessor.name}`"
      size="md"
      @close="closeContactModal"
    >
      <div class="contact-info">
        <div v-if="selectedProfessor.department" class="info-item">
          <strong><i class="fas fa-briefcase"></i> Department:</strong>
          <span>{{ selectedProfessor.department }}</span>
        </div>
        <div v-if="selectedProfessor.faculty" class="info-item">
          <strong><i class="fas fa-building"></i> Faculty:</strong>
          <span>{{ selectedProfessor.faculty }}</span>
        </div>
        <div v-if="selectedProfessor.phone" class="info-item">
          <strong><i class="fas fa-phone"></i> Phone:</strong>
          <a :href="`tel:${selectedProfessor.phone}`">{{ selectedProfessor.phone }}</a>
        </div>
        <div v-if="selectedProfessor.email" class="info-item">
          <strong><i class="fas fa-envelope"></i> Email:</strong>
          <a :href="`mailto:${selectedProfessor.email}`">{{ selectedProfessor.email }}</a>
        </div>
        <div v-if="selectedProfessor.officeLocation" class="info-item">
          <strong><i class="fas fa-map-marker-alt"></i> Office:</strong>
          <span>{{ selectedProfessor.officeLocation }}</span>
        </div>
        <div v-if="selectedProfessor.officeHours" class="info-item">
          <strong><i class="fas fa-clock"></i> Office Hours:</strong>
          <span>{{ selectedProfessor.officeHours }}</span>
        </div>
      </div>
    </BaseModal>
  </main>
</template>

<style scoped>
.prof-layout {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 2rem;
  padding: 2rem;
}

.error-alert {
  background: rgba(220, 53, 69, 0.1);
  color: #dc3545;
  padding: 0.75rem 1rem;
  border-radius: var(--radius);
}

.loading,
.empty {
  color: var(--text-muted);
  padding: 1rem 0;
}

.section-title {
  font-size: 1.3rem;
  margin: 2rem 0 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid var(--border-color);
  color: var(--primary-color);
}

.carousel-wrapper {
  position: relative;
}

.carousel {
  display: grid;
  grid-template-columns: repeat(6, minmax(160px, 1fr));
  gap: 1.5rem;
  padding: 1rem 0.25rem;
}

.prof-wrap {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: stretch;
}

.review-btn {
  font-size: 0.8rem;
}

.rating-line {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.4rem;
}

.rating-value {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.info-box {
  background: var(--bg-secondary);
  border-radius: 10px;
  padding: 1rem;
  margin-bottom: 1rem;
}

.info-box h4 {
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.reviews-list {
  display: grid;
  gap: 0.75rem;
}

.review-card {
  border: 1px solid var(--border-light);
  border-radius: var(--radius);
  padding: 0.75rem;
  position: relative;
}

.review-header {
  display: flex;
  justify-content: space-between;
}

.btn-icon {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
}

.btn-icon.danger:hover {
  color: #dc3545;
}

.review-form {
  margin-top: 1rem;
  border-top: 1px solid var(--border-light);
  padding-top: 1rem;
}

.form-row {
  margin-bottom: 0.75rem;
}

.form-row label {
  display: block;
  margin-bottom: 0.35rem;
}

.form-control {
  width: 100%;
  padding: 0.65rem 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  background: var(--bg-color);
  color: var(--text-color);
}

.error-text {
  color: #dc3545;
  margin-bottom: 0.5rem;
}

@media (max-width: 992px) {
  .prof-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1600px) {
  .carousel {
    grid-template-columns: repeat(5, minmax(160px, 1fr));
  }
}

@media (max-width: 1300px) {
  .carousel {
    grid-template-columns: repeat(4, minmax(160px, 1fr));
  }
}

@media (max-width: 992px) {
  .carousel {
    grid-template-columns: repeat(3, minmax(160px, 1fr));
  }
}

@media (max-width: 700px) {
  .carousel {
    grid-template-columns: repeat(2, minmax(150px, 1fr));
  }
}

@media (max-width: 480px) {
  .carousel {
    grid-template-columns: 1fr;
  }
}
</style>
