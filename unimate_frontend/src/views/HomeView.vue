<script setup>
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
</script>

<template>
  <div class="home-container">
    <section class="card fade-in">
      <h1 class="card-title">{{ $t('welcome') }}</h1>
      <p class="welcome-text">{{ $t('description') }}</p>
    </section>

    <section class="card map-container fade-in">
      <h2 class="card-title">{{ $t('map') }}</h2>
      <img src="@/assets/images/campus-map.png" class="map-image" alt="Campus Map" />
      <div class="map-locations">
        <span class="location-tag">{{ $t('locations.fils') }}</span>
        <span class="location-tag">{{ $t('locations.library') }}</span>
        <span class="location-tag">{{ $t('locations.canteen') }}</span>
        <span class="location-tag">{{ $t('locations.sports') }}</span>
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

.map-image {
  max-width: 100%;
  height: auto;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  border: 1px solid var(--border-color);
}

.map-locations {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 0.6rem;
  margin-top: 1.25rem;
}

.location-tag {
  background-color: var(--bg-secondary);
  padding: 0.3rem 0.9rem;
  border-radius: 50px;
  border: 1px solid var(--border-color);
  font-size: 0.8rem;
  color: var(--text-muted);
  font-weight: 500;
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
