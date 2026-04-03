<script setup lang="ts">
<<<<<<< Updated upstream
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
=======
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/appStore'
>>>>>>> Stashed changes
import ProfessorCard from '@/components/ProfessorCard.vue'
import BaseModal from '@/components/BaseModal.vue'

<<<<<<< Updated upstream
const { t } = useI18n()

const decanatStaff = ref([
  {
    name: 'Name1',
    title: 'Dean, FILS',
    email: 'mail1@example.com',
    phone: '+40 21 000 0001',
    hours: 'Mon, Wed 14:00-16:00',
    room: 'Bldg X, Rm 101',
    color: '4a90e2',
    initials: 'N1',
  },
  {
    name: 'Name2',
    title: 'Vice-Dean',
    email: 'mail2@example.com',
    phone: '+40 21 000 0002',
    hours: 'Tue, Thu 10:00-12:00',
    room: 'Bldg X, Rm 102',
    color: '50c878',
    initials: 'N2',
  },
])

const teachingStaff = ref([
  {
    name: 'Name5',
    title: 'Computer Science',
    email: 'mail5@example.com',
    hours: 'Wed 15:00-17:00',
    room: 'FILS, Lab 201',
    color: '6f42c1',
    initials: 'P1',
  },
  {
    name: 'Name6',
    title: 'Mathematics',
    email: 'mail6@example.com',
    hours: 'Tue 10:00-12:00',
    room: 'FILS, Rm 105',
    color: '20c997',
    initials: 'P2',
  },
])

const scrollCarousel = (id: string, distance: number) => {
  const el = document.getElementById(id)
  if (el) el.scrollBy({ left: distance, behavior: 'smooth' })
=======
interface Professor {
  id: number
  name: string
  department: string | null
  faculty: string | null
>>>>>>> Stashed changes
}

interface RatingStats {
  average: number
  count: number
  distribution: Record<number, number>
}

interface Review {
  id: number
  professorId: number
  userId: number
  username: string
  rating: number
  comment: string
  createdAt: string
}

const { t } = useI18n()
const store = useAppStore()

const loading = ref(true)
const error = ref('')
const professors = ref<Professor[]>([])
const ratings = ref<Record<number, RatingStats>>({})

const showReviewsModal = ref(false)
const selectedProfessor = ref<Professor | null>(null)
const reviews = ref<Review[]>([])
const reviewsLoading = ref(false)
const submittingReview = ref(false)
const reviewError = ref('')
const reviewComment = ref('')
const reviewRating = ref(5)

const isLoggedIn = computed(() => store.isLoggedIn)
const currentUserId = computed(() => store.user?.id || null)

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

function professorRating(professorId: number): RatingStats {
  return ratings.value[professorId] || { average: 0, count: 0, distribution: {} }
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

    const ratingResponses = await Promise.all(
      professors.value.map(async (prof) => {
        const ratingResponse = await fetch(`/api/reviews/professor/${prof.id}/rating`)
        if (!ratingResponse.ok) {
          return [prof.id, { average: 0, count: 0, distribution: {} }] as const
        }
        const data = await ratingResponse.json()
        return [prof.id, data] as const
      }),
    )

    ratings.value = Object.fromEntries(ratingResponses)
  } catch {
    error.value = t('common.error')
  } finally {
    loading.value = false
  }
}

async function openReviewsModal(professor: Professor) {
  selectedProfessor.value = professor
  showReviewsModal.value = true
  reviewsLoading.value = true
  reviewError.value = ''
  reviewComment.value = ''
  reviewRating.value = 5

  try {
    const response = await fetch(`/api/reviews/professor/${professor.id}`)
    if (!response.ok) {
      throw new Error('Failed to load reviews')
    }
    reviews.value = await response.json()
  } catch {
    reviewError.value = t('common.error')
  } finally {
    reviewsLoading.value = false
  }
}

function closeReviewsModal() {
  showReviewsModal.value = false
  selectedProfessor.value = null
  reviews.value = []
  reviewComment.value = ''
  reviewError.value = ''
}

async function submitReview() {
  if (!selectedProfessor.value || !reviewComment.value.trim()) {
    return
  }
  submittingReview.value = true
  reviewError.value = ''

  try {
    const response = await fetch('/api/reviews', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        professorId: selectedProfessor.value.id,
        rating: reviewRating.value,
        comment: reviewComment.value.trim(),
      }),
    })
    if (!response.ok) {
      const data = await response.json().catch(() => null)
      throw new Error(data?.message || 'Failed to submit review')
    }

    await openReviewsModal(selectedProfessor.value)
    await fetchProfessors()
  } catch (err: unknown) {
    reviewError.value = err instanceof Error ? err.message : t('common.error')
  } finally {
    submittingReview.value = false
  }
}

async function deleteReview(review: Review) {
  try {
    const response = await fetch(`/api/reviews/${review.id}`, {
      method: 'DELETE',
    })
    if (!response.ok) {
      throw new Error('Failed to delete review')
    }
    if (selectedProfessor.value) {
      await openReviewsModal(selectedProfessor.value)
      await fetchProfessors()
    }
  } catch {
    reviewError.value = t('common.error')
  }
}

onMounted(fetchProfessors)
</script>

<template>
  <main class="main-content">
    <div class="prof-layout">
      <div class="carousels-container">
<<<<<<< Updated upstream
        <h3 class="section-title">🎓 {{ t('professors.deansOffice') }}</h3>
        <div class="carousel-wrapper">
          <button class="carousel-nav prev" @click="scrollCarousel('decanat-carousel', -300)">
            <i class="fas fa-chevron-left"></i>
          </button>
          <div class="carousel" id="decanat-carousel">
            <ProfessorCard v-for="prof in decanatStaff" :key="prof.name" :prof="prof" />
          </div>
          <button class="carousel-nav next" @click="scrollCarousel('decanat-carousel', 300)">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>

        <h3 class="section-title">{{ t('professors.professorsStaff') }}</h3>
        <div class="carousel-wrapper">
          <button class="carousel-nav prev" @click="scrollCarousel('prof-carousel', -300)">
            <i class="fas fa-chevron-left"></i>
          </button>
          <div class="carousel" id="prof-carousel">
            <ProfessorCard v-for="prof in teachingStaff" :key="prof.name" :prof="prof" />
          </div>
          <button class="carousel-nav next" @click="scrollCarousel('prof-carousel', 300)">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
=======
        <div v-if="error" class="error-alert">{{ error }}</div>
        <div v-if="loading" class="loading">{{ t('common.loading') }}</div>

        <template v-else>
          <h3 class="section-title">{{ t('professors.deansOffice') }}</h3>
          <div class="carousel-wrapper">
            <div class="carousel">
              <div v-for="professor in decanatStaff" :key="professor.id" class="prof-wrap">
                <ProfessorCard :prof="mapProfessorCard(professor)" />
                <button class="btn btn-secondary review-btn" @click="openReviewsModal(professor)">
                  Reviews ({{ professorRating(professor.id).count }})
                </button>
              </div>
            </div>
          </div>

          <h3 class="section-title">{{ t('professors.professorsStaff') }}</h3>
          <div class="carousel-wrapper">
            <div class="carousel">
              <div v-for="professor in teachingStaff" :key="professor.id" class="prof-wrap">
                <ProfessorCard :prof="mapProfessorCard(professor)" />
                <div class="rating-line">
                  <span class="rating-value">Rating: {{ professorRating(professor.id).average?.toFixed(1) || '0.0' }}/5</span>
                  <button class="btn btn-secondary review-btn" @click="openReviewsModal(professor)">
                    Reviews ({{ professorRating(professor.id).count }})
                  </button>
                </div>
              </div>
            </div>
          </div>
        </template>
>>>>>>> Stashed changes
      </div>

      <aside class="prof-sidebar">
        <div class="card">
<<<<<<< Updated upstream
          <!-- The Dean's Office vs. Deanery? -->
=======
>>>>>>> Stashed changes
          <h3 class="card-title">{{ t('professors.secretariat') }}</h3>

          <div class="info-box">
            <h4><i class="fas fa-map-marker-alt"></i> {{ t('professors.location') }}</h4>
<<<<<<< Updated upstream
            <p>FILS Building, Politehnica University<br />313 Splaiul Independenței, Bucharest</p>
=======
            <p>FILS Building, Politehnica University<br />313 Splaiul Independentei, Bucharest</p>
>>>>>>> Stashed changes
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
      v-model="showReviewsModal"
      :title="`${selectedProfessor.name} - Reviews`"
      size="lg"
      @close="closeReviewsModal"
    >
      <div v-if="reviewsLoading" class="loading">{{ t('common.loading') }}</div>
      <div v-else-if="reviews.length === 0" class="empty">{{ t('quotes.noQuotes') }}</div>
      <div v-else class="reviews-list">
        <div v-for="review in reviews" :key="review.id" class="review-card">
          <div class="review-header">
            <strong>{{ review.username }}</strong>
            <span>{{ review.rating }}/5</span>
          </div>
          <p>{{ review.comment }}</p>
          <small>{{ new Date(review.createdAt).toLocaleDateString() }}</small>
          <button
            v-if="currentUserId === review.userId || store.isAdmin"
            class="btn-icon danger"
            @click="deleteReview(review)"
          >
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>

      <div v-if="isLoggedIn" class="review-form">
        <h4>Add review</h4>
        <div class="form-row">
          <label>Rating</label>
          <select v-model="reviewRating" class="form-control">
            <option v-for="value in [5, 4, 3, 2, 1]" :key="value" :value="value">{{ value }}</option>
          </select>
        </div>
        <div class="form-row">
          <label>Comment</label>
          <textarea v-model="reviewComment" class="form-control" rows="3"></textarea>
        </div>
        <p v-if="reviewError" class="error-text">{{ reviewError }}</p>
        <button class="btn btn-primary" :disabled="submittingReview" @click="submitReview">
          {{ submittingReview ? t('common.loading') : 'Submit review' }}
        </button>
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
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
.carousel {
  display: grid;
  grid-template-columns: repeat(6, minmax(160px, 1fr));
  gap: 1.5rem;
<<<<<<< Updated upstream
  overflow-x: auto;
  scroll-behavior: smooth;
  padding: 1rem 0.5rem;
}
.carousel::-webkit-scrollbar {
  height: 6px;
}
.carousel::-webkit-scrollbar-thumb {
  background: var(--primary-color);
  border-radius: 3px;
=======
  padding: 1rem 0.25rem;
>>>>>>> Stashed changes
}

.prof-wrap {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: stretch;
}
<<<<<<< Updated upstream
.carousel-nav.prev {
  left: -20px;
}
.carousel-nav.next {
  right: -20px;
=======

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
>>>>>>> Stashed changes
}

.info-box {
  background: var(--bg-secondary);
  border-radius: 10px;
  padding: 1rem;
  margin-bottom: 1rem;
}
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
.info-box h4 {
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

<<<<<<< Updated upstream
.prof-card {
  min-width: 220px;
  background-color: var(--card-bg);
  border-radius: var(--radius);
  padding: 1.5rem;
  text-align: center;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
}

.prof-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow);
}

.prof-photo {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 1rem;
  border: 2px solid var(--border-color);
=======
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
>>>>>>> Stashed changes
}

@media (max-width: 992px) {
  .prof-layout {
    grid-template-columns: 1fr;
  }
}
<<<<<<< Updated upstream
=======

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
>>>>>>> Stashed changes
</style>
