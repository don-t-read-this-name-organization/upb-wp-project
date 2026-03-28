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
    })() as { name: string; role: string; id?: number; email?: string; firstName?: string; lastName?: string; faculty?: { id?: number; name: string; shortName?: string; website?: string }; group?: { name: string } } | null,
    language: getInitialLanguage(),
  }),
  getters: {
    isLoggedIn: (state) => !!state.user,
    isVisitor: (state) => !state.user || state.user.role === 'VISITOR',
    isStudent: (state) => state.user?.role === 'STUDENT',
    isChief: (state) => state.user?.role === 'CHIEF',
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isStudentOrChief: (state) => state.user?.role === 'STUDENT' || state.user?.role === 'CHIEF',
    userRole: (state) => state.user?.role || 'VISITOR',
    userName: (state) => state.user?.name || '',
    userFirstName: (state) => state.user?.firstName || '',
    userLastName: (state) => state.user?.lastName || '',
    userEmail: (state) => state.user?.email || '',
    userFaculty: (state) => state.user?.faculty?.name || '',
    userFacultyShortName: (state) => state.user?.faculty?.shortName || '',
    userFacultyWebsite: (state) => state.user?.faculty?.website || '',
    userGroup: (state) => state.user?.group?.name || '',
  },
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
