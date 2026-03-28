<script setup>
import { ref } from 'vue'

const news = [
  {
    id: 1,
    date: 'Mar 14, 2026',
    title: 'Exam Schedule',
    body: 'The summer session exam schedule has been published. Check your dates and rooms!',
  },
  {
    id: 2,
    date: 'Mar 10, 2026',
    title: 'Erasmus+',
    body: "Applications for Erasmus+ program are open until March 31st. Don't miss this opportunity!",
  },
  {
    id: 3,
    date: 'Mar 8, 2026',
    title: 'Library Hours',
    body: 'During exam period, FILS library extends opening hours until 10 PM.',
  },
  {
    id: 4,
    date: 'Mar 5, 2026',
    title: 'Career Fair',
    body: 'Annual tech career fair happening next month. Register now for company interviews.',
  },
]

const maps = [
  { name: 'Campus Central', src: '/src/assets/images/campus-map.png' },
  { name: 'Polizu', src: '/src/assets/images/polizu.jpg' },
  { name: 'Leu', src: '/src/assets/images/leu.jpg' },
  { name: 'Regie', src: '/src/assets/images/regie.jpg' },
  { name: 'Pitesti 1', src: '/src/assets/images/pitesti_1_campus.jpg' },
  { name: 'Pitesti 2', src: '/src/assets/images/pitesti_2_campus.jpg' },
]

const currentMapIndex = ref(0)

const prevMap = () => {
  currentMapIndex.value = currentMapIndex.value === 0 ? maps.length - 1 : currentMapIndex.value - 1
}

const nextMap = () => {
  currentMapIndex.value = currentMapIndex.value === maps.length - 1 ? 0 : currentMapIndex.value + 1
}
</script>

<template>
  <div class="home-container">
    <section class="card fade-in">
      <h1 class="card-title">{{ $t('welcome') }}</h1>
      <p class="welcome-text">{{ $t('description') }}</p>
    </section>

    <section class="card map-container fade-in">
      <h2 class="card-title">{{ $t('map') }}</h2>
      <div class="map-carousel">
        <button class="map-arrow map-arrow-left" @click="prevMap" aria-label="Previous map">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
        </button>
        <div class="map-wrapper">
          <img :src="maps[currentMapIndex].src" class="map-image" :alt="maps[currentMapIndex].name" />
        </div>
        <button class="map-arrow map-arrow-right" @click="nextMap" aria-label="Next map">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </button>
      </div>
      <div class="map-dots">
        <span 
          v-for="(map, index) in maps" 
          :key="index" 
          class="map-dot" 
          :class="{ active: index === currentMapIndex }"
          @click="currentMapIndex = index"
        ></span>
      </div>
    </section>

    <section class="news-section">
      <h2 class="section-heading">Latest News</h2>
      <div class="news-grid">
        <div v-for="item in news" :key="item.id" class="news-card">
          <div class="news-date">{{ item.date }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.body }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1100px;
  margin: 0 auto;
}

.welcome-text {
  color: var(--text-muted);
  line-height: 1.7;
  font-size: 1.05rem;
}

.map-container {
  text-align: center;
  margin: 2rem 0;
}

.map-carousel {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.map-wrapper {
  flex: 1;
  max-width: 800px;
  position: relative;
}

.map-image {
  max-width: 100%;
  height: auto;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  border: 1px solid var(--border-color);
}

.map-name {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: var(--radius);
  font-size: 0.9rem;
}

.map-arrow {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 50%;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
  color: var(--text-color);
}

.map-arrow:hover {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.map-dots {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.map-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: var(--border-color);
  cursor: pointer;
  transition: var(--transition);
}

.map-dot:hover,
.map-dot.active {
  background-color: var(--primary-color);
}

.section-heading {
  font-family: var(--font-display);
  font-size: 1.5rem;
  margin: 2rem 0 1rem;
  color: var(--text-color);
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.news-card {
  background-color: var(--card-bg);
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
  padding: 1.5rem;
}

.news-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.news-date {
  color: var(--primary-color);
  font-size: 0.78rem;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.news-card h3 {
  margin-bottom: 0.5rem;
  color: var(--text-color);
}

.news-card p {
  color: var(--text-muted);
  font-size: 0.9rem;
  line-height: 1.6;
}
</style>
