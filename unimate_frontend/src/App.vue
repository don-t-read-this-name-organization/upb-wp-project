<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { useAppStore } from '@/stores/appStore'
import { useI18n } from 'vue-i18n'

const store = useAppStore()
const { t } = useI18n()
const isSidebarOpen = ref(false)

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}

const showMenu = computed(() => !store.isVisitor)
const showFacultyLink = computed(() => store.isLoggedIn && store.userFaculty)
const showUserProfile = computed(() => store.isLoggedIn)
</script>

<template>
  <header class="header">
    <div class="header-content">
      <button v-if="showMenu" class="menu-toggle" type="button" @click="toggleSidebar">
        <i class="fas fa-bars"></i>
        <span class="menu-text">Menu</span>
      </button>

      <RouterLink to="/" class="logo"> <i class="fas fa-graduation-cap"></i> UniMate </RouterLink>

      <div class="header-controls">
        <a v-if="showFacultyLink && store.userFacultyWebsite" :href="store.userFacultyWebsite" target="_blank" class="faculty-link">
          {{ store.userFacultyShortName || store.userFaculty }}
        </a>

        <RouterLink v-if="showUserProfile" to="/profile" class="user-profile-link">
          <i class="fas fa-user-circle"></i> {{ t('menu.myProfile') }}
        </RouterLink>

        <button class="theme-toggle" @click="store.toggleTheme">
          <i :class="store.isDark ? 'fas fa-sun' : 'fas fa-moon'"></i>
        </button>

        <select
          class="lang-select"
          :value="store.language"
          @change="store.setLanguage(($event.target as HTMLSelectElement).value)"
        >
          <option value="en">EN</option>
          <option value="ro">RO</option>
          <option value="de">DE</option>
          <option value="fr">FR</option>
        </select>

        <RouterLink v-if="!store.isLoggedIn" to="/login" class="btn btn-primary">
          <i class="fas fa-sign-in-alt"></i> {{ t('menu.login') }}
        </RouterLink>
      </div>
    </div>
  </header>

  <div v-if="showMenu" :class="['sidebar-overlay', { active: isSidebarOpen }]" @click="toggleSidebar"></div>
  <nav v-if="showMenu" :class="['sidebar', { active: isSidebarOpen }]">
    <div class="sidebar-header">
      <h3>Menu</h3>
      <button type="button" class="sidebar-close" @click="toggleSidebar">
        <i class="fas fa-times"></i>
      </button>
    </div>
    <ul class="sidebar-menu">
      <li>
        <RouterLink to="/" @click="toggleSidebar"><i class="fas fa-home"></i> {{ t('menu.home') }}</RouterLink>
      </li>

      <template v-if="store.isStudentOrChief">
        <li>
          <RouterLink to="/links" @click="toggleSidebar"
            ><i class="fas fa-link"></i> {{ t('menu.links') }}</RouterLink
          >
        </li>
        <li>
          <RouterLink to="/professors" @click="toggleSidebar"
            ><i class="fas fa-chalkboard-teacher"></i> {{ t('menu.professors') }}</RouterLink
          >
        </li>
        <li>
          <RouterLink to="/timetable" @click="toggleSidebar"
            ><i class="fas fa-calendar-alt"></i> {{ t('menu.timetable') }}</RouterLink
          >
        </li>
        <li>
          <RouterLink to="/kanban" @click="toggleSidebar"
            ><i class="fas fa-columns"></i> {{ t('menu.kanban') }}</RouterLink
          >
        </li>
        <li>
          <RouterLink to="/notes" @click="toggleSidebar"
            ><i class="fas fa-sticky-note"></i> {{ t('menu.notes') }}</RouterLink
          >
        </li>
        <li>
          <RouterLink to="/files" @click="toggleSidebar"
            ><i class="fas fa-hdd"></i> {{ t('menu.files') || 'Files' }}</RouterLink
          >
        </li>
      </template>

      <li v-if="store.isAdmin">
        <RouterLink to="/admin" @click="toggleSidebar"
          ><i class="fas fa-user-shield"></i> {{ t('menu.admin') }}</RouterLink
        >
      </li>

      <li v-if="store.isLoggedIn" class="auth-only">
        <a href="#" @click.prevent="store.logout"><i class="fas fa-sign-out-alt"></i> {{ t('menu.logout') }}</a>
      </li>
    </ul>
  </nav>

  <main class="main-content">
    <RouterView />
  </main>

  <footer class="footer">
    <div class="footer-content">
      <p>&copy; 2026 UniMate. All rights reserved.</p>
      <p>Created by Gladkykh Daria, Bachynskyi Roi, Huzhan Sofiia</p>
      <p class="footer-joke">
        "Built because we needed it ourselves - procrastination is the mother of invention!" 😊
      </p>
    </div>
  </footer>
</template>

<style>
@import '@/assets/styles.css';

.sidebar {
  transition: transform 0.3s ease;
  transform: translateX(-100%);
}
.sidebar.open {
  transform: translateX(0);
}
.sidebar-overlay.active {
  display: block;
  opacity: 1;
}
</style>
