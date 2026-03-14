import { defineStore } from 'pinia'
import { i18n } from '@/i18n'

export const useAppStore = defineStore('app', {
  state: () => ({
    isDark: false,
    user: (() => {
      const stored = localStorage.getItem('user')
      return stored ? JSON.parse(stored) : null
    })() as { name: string; role: string } | null,
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
      document.documentElement.setAttribute('lang', lang)
    },
    logout() {
      this.user = null
      localStorage.removeItem('user')
      window.location.href = '/login' 
    },
  },
})
