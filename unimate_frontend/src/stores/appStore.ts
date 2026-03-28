import { defineStore } from 'pinia'
import { i18n } from '@/i18n'

const getInitialLanguage = () => {
  const stored = localStorage.getItem('language')
  return stored || 'en'
}

export const useAppStore = defineStore('app', {
  state: () => ({
    isDark: false,
    user: (() => {
      const stored = localStorage.getItem('user')
      return stored ? JSON.parse(stored) : null
    })() as { name: string; role: string; id?: number } | null,
    language: getInitialLanguage(),
  }),
  actions: {
    toggleTheme() {
      this.isDark = !this.isDark
      document.documentElement.setAttribute('data-theme', this.isDark ? 'dark' : 'light')
    },
    setLanguage(lang: string) {
      this.language = lang
      localStorage.setItem('language', lang)
      i18n.global.locale.value = lang as 'en' | 'de' | 'fr' | 'ro'
      document.documentElement.setAttribute('lang', lang)
    },
    logout() {
      this.user = null
      localStorage.removeItem('user')
      window.location.href = '/login'
    },
  },
})

i18n.global.locale.value = getInitialLanguage() as 'en' | 'de' | 'fr' | 'ro'
