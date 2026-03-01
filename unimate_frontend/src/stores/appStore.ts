import { defineStore } from 'pinia'
import { i18n } from '@/i18n'

export const useAppStore = defineStore('app', {
  state: () => ({
    isDark: false,
    user: null as { name: string; role: string } | null,
    language: 'en',
  }),
  actions: {
    toggleTheme() {
      this.isDark = !this.isDark
      document.documentElement.setAttribute('data-theme', this.isDark ? 'dark' : 'light')
    },
    setLanguage(lang: string) {
      this.language = lang
      i18n.global.locale.value = lang as any
    },
    logout() {
      this.user = null
      // window.location.href = '/login' 
    },
  },
})
