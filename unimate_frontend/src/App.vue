<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, RouterView } from 'vue-router'
import { useAppStore } from '@/stores/appStore'

const store = useAppStore()
const isSidebarOpen = ref(false)

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}
</script>

<template>
  <header class="header">
    <div class="header-content">
      <button class="menu-toggle" type="button" @click="toggleSidebar">
        <i class="fas fa-bars"></i>
        <span class="menu-text">Menu</span>
      </button>

      <RouterLink to="/" class="logo"> <i class="fas fa-graduation-cap"></i> UniMate </RouterLink>

      <div class="header-controls">
        <a href="https://fils.upb.ro/ro/home/" target="_blank" class="faculty-link">FILS</a>

        <span v-if="store.user" class="user-display">{{ store.user.name }}</span>

        <button class="theme-toggle" @click="store.toggleTheme">
          <i :class="store.isDark ? 'fas fa-sun' : 'fas fa-moon'"></i>
        </button>

        <select
          class="lang-select"
          :value="store.language"
          @change="store.setLanguage(($event.target as HTMLSelectElement).value)"
        >
          <option value="en">ENG</option>
          <option value="de">DE</option>
          <option value="fr">FR</option>
        </select>

        <RouterLink v-if="!store.user" to="/login" class="btn btn-primary">
          <i class="fas fa-sign-in-alt"></i> Login
        </RouterLink>
      </div>
    </div>
  </header>

  <div :class="['sidebar-overlay', { active: isSidebarOpen }]" @click="toggleSidebar"></div>
  <nav :class="['sidebar', { active: isSidebarOpen }]">
    <div class="sidebar-header">
      <h3>Menu</h3>
      <button type="button" class="sidebar-close" @click="toggleSidebar">
        <i class="fas fa-times"></i>
      </button>
    </div>
    <ul class="sidebar-menu">
      <li>
        <RouterLink to="/" @click="toggleSidebar"><i class="fas fa-home"></i> Home</RouterLink>
      </li>
      <li>
        <RouterLink to="/links" @click="toggleSidebar"
          ><i class="fas fa-link"></i> Links</RouterLink
        >
      </li>
      <li>
        <RouterLink to="/professors" @click="toggleSidebar"
          ><i class="fas fa-chalkboard-teacher"></i> Professors</RouterLink
        >
      </li>
      <li>
        <RouterLink to="/kanban" @click="toggleSidebar"
          ><i class="fas fa-columns"></i> Kanban</RouterLink
        >
      </li>

      <template v-if="!store.user">
        <li>
          <RouterLink to="/timetable" @click="toggleSidebar"
            ><i class="fas fa-calendar-alt"></i> Timetable</RouterLink
          >
        </li>
        <li>
          <RouterLink to="/notes" @click="toggleSidebar"
            ><i class="fas fa-sticky-note"></i> Notes</RouterLink
          >
        </li>
      </template>

      <li v-if="store.user?.role === 'admin'">
        <RouterLink to="/admin" @click="toggleSidebar"
          ><i class="fas fa-user-shield"></i> Admin Panel</RouterLink
        >
      </li>

      <li v-if="store.user" class="auth-only">
        <a href="#" @click.prevent="store.logout"><i class="fas fa-sign-out-alt"></i> Logout</a>
      </li>
    </ul>
  </nav>

  <main class="main-content">
    <RouterView />
  </main>

  <footer class="footer">
    <div class="footer-content">
      <p>&copy; 2026 UniMate. All rights reserved.</p>
      <p>Created by Gladkykh Daria, Bachynskyi Roi, Huzhn Sofiia</p>
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
