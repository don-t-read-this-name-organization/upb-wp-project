<script setup>
import { useAppStore } from '@/stores/appStore'
const store = useAppStore()
</script>

<template>
  <header class="header">
    <div class="header-content">
      <button class="menu-toggle" @click="$emit('toggle-menu')">
        <i class="fas fa-bars"></i>
        <span class="menu-text">Menu</span>
      </button>

      <router-link to="/" class="logo"> <i class="fas fa-graduation-cap"></i> UniMate </router-link>

      <div class="header-controls">
        <a href="https://fils.upb.ro/ro/home/" target="_blank" class="faculty-link">FILS</a>

        <span v-if="store.user" class="user-display">{{ store.user.name }}</span>

        <button class="theme-toggle" @click="store.toggleTheme">
          <i :class="store.isDark ? 'fas fa-sun' : 'fas fa-moon'"></i>
        </button>

        <select
          :value="store.language"
          @change="(e) => store.setLanguage(e.target.value)"
          class="lang-select"
        >
          <option value="en">EN</option>
          <option value="de">DE</option>
          <option value="fr">FR</option>
        </select>

        <router-link v-if="!store.user" to="/login" class="btn btn-primary">
          <i class="fas fa-sign-in-alt"></i> Login
        </router-link>
      </div>
    </div>
  </header>
</template>
