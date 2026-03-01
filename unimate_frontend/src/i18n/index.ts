import { createI18n } from 'vue-i18n'

const messages = {
  en: {
    welcome: 'Welcome to UniMate',
    description: 'Your all-in-one student assistant',
    locations: {
      fils: '📍 FILS Building',
      library: '📍 Library',
    },
    menu: {
      home: 'Home',
      links: 'Links',
      timetable: 'Timetable',
      notes: 'Notes',
      professors: 'Professors',
      kanban: 'Kanban',
      login: 'Login',
      admin: 'Admin'
    },
    map: 'Campus Map',
  },
  de: {
    welcome: 'Willkommen bei UniMate',
    description: 'Ihr All-in-One-Studentenassistent',
    locations: {
      fils: '📍 FILS-Gebäude',
      library: '📍 Bibliothek',
    },
    menu: {
      home: 'Startseite',
      links: 'Links',
      timetable: 'Stundenplan',
      notes: 'Notizen',
      professors: 'Professoren',
      kanban: 'Kanban',
      login: 'Anmelden',
      admin: 'Admin'
    },
    map: 'Campus Karte',
  },
  fr: {
    welcome: 'Bienvenue sur UniMate',
    description: 'Votre assistant étudiant tout-en-un',
    locations: {
      fils: '📍 Bâtiment FILS',
      library: '📍 Bibliothèque',
    },
    menu: {
      home: 'Accueil',
      links: 'Liens',
      timetable: 'Emploi du temps',
      notes: 'Notes',
      professors: 'Professeurs',
      kanban: 'Kanban',
      login: 'Connexion',
      admin: 'Admin'
    },
    map: 'Carte du campus',
  }
}

export const i18n = createI18n({
  legacy: false, // Use Composition API mode
  locale: 'en',  // Default language
  fallbackLocale: 'en',
  messages,
})