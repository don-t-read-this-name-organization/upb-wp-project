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
.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.25rem;
  margin-top: 1.5rem;
}

.link-card {
  background-color: var(--card-bg);
  border-radius: var(--radius);
  padding: 1.4rem;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
  text-decoration: none;
  color: var(--text-color);
  min-height: 120px;
  height: auto;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  overflow: hidden;
}

.link-card h3 {
  overflow-wrap: break-word;
  word-wrap: break-word;
  hyphens: auto;
  line-height: 1.2;
  margin-bottom: 4px;
}

.link-card p {
  overflow-wrap: break-word;
  word-wrap: break-word;
  hyphens: auto;
}

.link-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.link-content {
  flex: 1;
  min-width: 0;
}

.link-icon {
  flex-shrink: 0;
  width: 2.5rem;
  font-size: 1.6rem;
  opacity: 0.75;
  display: flex;
  justify-content: center;
}

.link-icon-globe {
  color: #7b9cbe;
}
.link-icon-university {
  color: #8db5a0;
}
.link-icon-tasks {
  color: #c8ad7f;
}
.link-icon-users {
  color: #c49090;
}
.link-icon-concierge {
  color: #a093c4;
}
.link-icon-calendar {
  color: #c49a72;
}
.link-icon-clipboard {
  color: #7ab8a4;
}
.link-icon-idcard {
  color: #7ab4c8;
}
.link-icon-laptop {
  color: #c47a99;
}
.link-icon-ticket {
  color: #9ea0a4;
}
</style>
