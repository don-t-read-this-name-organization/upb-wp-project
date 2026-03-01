<script setup>
const news = [
  { id: 1, date: 'Feb 25, 2026', title: 'Exam Schedule', body: 'The summer session is out!' },
  { id: 2, date: 'Feb 20, 2026', title: 'Erasmus', body: 'Applications are open until March.' },
]
</script>

<template>
  <div class="main-content">
    <section class="card animate-fade-in">
      <h1 class="card-title">{{ $t('welcome') }}</h1>
      <p>{{ $t('description') }}</p>
    </section>

    <section class="card map-container">
      <h2 class="card-title">{{ $t('map') }}</h2>
      <img src="@/assets/images/campus-map.png" class="map-image" />
      <div class="map-locations">
        <span class="location-tag">{{ $t('locations.fils') }}</span>
        <span class="location-tag">{{ $t('locations.library') }}</span>
      </div>
    </section>

    <div class="news-grid">
      <div v-for="item in news" :key="item.id" class="news-card">
        <div class="news-date">{{ item.date }}</div>
        <h3>{{ item.title }}</h3>
        <p>{{ item.body }}</p>
      </div>
    </div>
  </div>
</template>
