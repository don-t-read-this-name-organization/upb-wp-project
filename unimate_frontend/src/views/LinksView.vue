<script setup lang="ts">
const usefulLinks = [
  {
    key: 'intl',
    url: 'https://fils.upb.ro/ro/international-students/',
    icon: 'fas fa-globe',
    colorClass: 'link-icon-globe',
  },
  {
    key: 'portal',
    url: 'https://studenti.pub.ro/index.php?page=Informatii&ActiveViewID=tab_infogen',
    icon: 'fas fa-university',
    colorClass: 'link-icon-university',
  },
  {
    key: 'activities',
    url: 'https://fils.upb.ro/ro/activitati-pentru-studenti/',
    icon: 'fas fa-tasks',
    colorClass: 'link-icon-tasks',
  },
  {
    key: 'associations',
    url: 'https://fils.upb.ro/ro/asociatii-studentesti/',
    icon: 'fas fa-users',
    colorClass: 'link-icon-users',
  },
  {
    key: 'facilities',
    url: 'https://fils.upb.ro/ro/facilitati/',
    icon: 'fas fa-concierge-bell',
    colorClass: 'link-icon-concierge',
  },
  {
    key: 'calendar',
    url: 'https://fils.upb.ro/ro/calendar-universitar/',
    icon: 'fas fa-calendar-alt',
    colorClass: 'link-icon-calendar',
  },
  {
    key: 'exams',
    url: 'https://fils.upb.ro/ro/sesiune-examene/',
    icon: 'fas fa-clipboard-list',
    colorClass: 'link-icon-clipboard',
  },
  {
    key: 'myupb',
    url: 'https://my.upb.ro/login',
    icon: 'fas fa-id-card',
    colorClass: 'link-icon-idcard',
  },
  {
    key: 'courses',
    url: 'https://curs.upb.ro/2025/',
    icon: 'fas fa-laptop-code',
    colorClass: 'link-icon-laptop',
  },
  {
    key: 'ticketing',
    url: 'https://ticketing.upb.ro/',
    icon: 'fas fa-ticket-alt',
    colorClass: 'link-icon-ticket',
  },
]
</script>

<template>
  <div class="card fade-in">
    <h2 class="card-title">
      <i class="fas fa-link"></i>
      {{ $t ? $t('linksPage.title') : 'Useful Links for Students' }}
    </h2>

    <div class="links-grid">
      <a
        v-for="link in usefulLinks"
        :key="link.url"
        :href="link.url"
        target="_blank"
        rel="noopener"
        class="link-card"
      >
        <i :class="[link.icon, 'link-icon', link.colorClass]"></i>
        <div class="link-content">
          <h3>{{ $t(`linksPage.${link.key}.title`) }}</h3>
          <p>{{ $t(`linksPage.${link.key}.desc`) }}</p>
        </div>
      </a>
    </div>
  </div>
</template>

<style scoped>
.link-card {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: transform 0.2s ease;
}

.link-card:hover {
  transform: scale(1.02);
}
</style>
