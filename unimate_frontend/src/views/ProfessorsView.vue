<script setup lang="ts">
import { ref } from 'vue'
import ProfessorCard from '@/components/ProfessorCard.vue'

const decanatStaff = ref([
  { name: 'Name1', title: 'Dean, FILS', email: 'mail1@example.com', phone: '+40 21 000 0001', hours: 'Mon, Wed 14:00-16:00', room: 'Bldg X, Rm 101', color: '4a90e2', initials: 'N1' },
  { name: 'Name2', title: 'Vice-Dean', email: 'mail2@example.com', phone: '+40 21 000 0002', hours: 'Tue, Thu 10:00-12:00', room: 'Bldg X, Rm 102', color: '50c878', initials: 'N2' }
])

const teachingStaff = ref([
  { name: 'Name5', title: 'Computer Science', email: 'mail5@example.com', hours: 'Wed 15:00-17:00', room: 'FILS, Lab 201', color: '6f42c1', initials: 'P1' },
  { name: 'Name6', title: 'Mathematics', email: 'mail6@example.com', hours: 'Tue 10:00-12:00', room: 'FILS, Rm 105', color: '20c997', initials: 'P2' }
])

const scrollCarousel = (id: string, distance: number) => {
  const el = document.getElementById(id)
  if (el) el.scrollBy({ left: distance, behavior: 'smooth' })
}
</script>

<template>
  <main class="main-content">
    <div class="prof-layout">
      
      <div class="carousels-container">
        <h3 class="section-title">🎓 Decanat (Dean's Office)</h3>
        <div class="carousel-wrapper">
          <button class="carousel-nav prev" @click="scrollCarousel('decanat-carousel', -300)"><i class="fas fa-chevron-left"></i></button>
          <div class="carousel" id="decanat-carousel">
            <ProfessorCard v-for="prof in decanatStaff" :key="prof.name" :prof="prof" />
          </div>
          <button class="carousel-nav next" @click="scrollCarousel('decanat-carousel', 300)"><i class="fas fa-chevron-right"></i></button>
        </div>

        <h3 class="section-title">Professors & Teaching Staff</h3>
        <div class="carousel-wrapper">
          <button class="carousel-nav prev" @click="scrollCarousel('prof-carousel', -300)"><i class="fas fa-chevron-left"></i></button>
          <div class="carousel" id="prof-carousel">
            <ProfessorCard v-for="prof in teachingStaff" :key="prof.name" :prof="prof" />
          </div>
          <button class="carousel-nav next" @click="scrollCarousel('prof-carousel', 300)"><i class="fas fa-chevron-right"></i></button>
        </div>
      </div>

      <aside class="prof-sidebar">
        <div class="card">
            <!-- The Dean's Office vs. Deanery? -->
          <h3 class="card-title">Secretariat & Deanery</h3>
          
          <div class="info-box">
            <h4><i class="fas fa-map-marker-alt"></i> Location</h4>
            <p>FILS Building, Politehnica University<br>313 Splaiul Independenței, Bucharest</p>
          </div>

          <div class="info-box">
            <h4><i class="fas fa-clock"></i> Working Hours</h4>
            <div class="office-hours-list">
              <strong>Secretariat:</strong> Mon-Thu 9:00-16:00<br>
              <strong>Deanery:</strong> Mon-Fri 10:00-15:00
            </div>
          </div>

          <div class="info-box">
            <h4><i class="fas fa-headset"></i> Contact</h4>
            <div class="info-item"><i class="fas fa-phone"></i> +40 21 345 1000</div>
            <div class="info-item"><i class="fas fa-envelope"></i> <a href="mailto:secretariat@upb.ro">secretariat@upb.ro</a></div>
          </div>
        </div>
      </aside>

    </div>
  </main>
</template>

<style scoped>
.prof-layout {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 2rem;
  padding: 2rem;
}

.section-title {
  font-size: 1.3rem;
  margin: 2rem 0 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid var(--border-color);
  color: var(--primary-color);
}

.carousel-wrapper { position: relative; }
.carousel {
  display: flex;
  gap: 1.5rem;
  overflow-x: auto;
  scroll-behavior: smooth;
  padding: 1rem 0.5rem;
}
.carousel::-webkit-scrollbar { height: 6px; }
.carousel::-webkit-scrollbar-thumb { 
  background: var(--primary-color); 
  border-radius: 3px; 
}

.carousel-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: var(--primary-color);
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  z-index: 10;
}
.carousel-nav.prev { left: -20px; }
.carousel-nav.next { right: -20px; }


.info-box {
  background: var(--bg-secondary);
  border-radius: 10px;
  padding: 1rem;
  margin-bottom: 1rem;
}
.info-box h4 { color: var(--primary-color); margin-bottom: 0.5rem; }

@media (max-width: 992px) {
  .prof-layout { grid-template-columns: 1fr; }
}
</style>