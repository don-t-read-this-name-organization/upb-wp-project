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
      admin: 'Admin',
    },
    map: 'Campus Map',
    linksPage: {
      title: 'Useful Links for Students',
      intl: {
        title: 'International Students',
        desc: 'Information for international students at FILS',
      },
      portal: { title: 'Student Portal', desc: 'General information and student services' },
      activities: { title: 'Student Activities', desc: 'Activities and events for students' },
      associations: { title: 'Student Associations', desc: 'Join student organizations and clubs' },
      facilities: { title: 'Facilities', desc: 'Campus facilities and resources' },
      calendar: { title: 'Academic Calendar', desc: 'Important dates and deadlines' },
      exams: { title: 'Exam Session', desc: 'Exam schedules and information' },
      myupb: { title: 'MyUPB Portal', desc: 'University management system' },
      courses: { title: 'Online Courses', desc: 'E-learning platform and course materials' },
      ticketing: { title: 'Ticketing System', desc: 'IT support and help desk' },
    },
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
      admin: 'Admin',
    },
    map: 'Campus Karte',
    linksPage: {
      title: 'Nützliche Links für Studierende',
      intl: {
        title: 'Internationale Studierende',
        desc: 'Informationen für internationale Studierende an der FILS',
      },
      portal: {
        title: 'Studierendenportal',
        desc: 'Allgemeine Informationen und Dienste für Studierende',
      },
      activities: {
        title: 'Studentische Aktivitäten',
        desc: 'Aktivitäten und Veranstaltungen für Studierende',
      },
      associations: {
        title: 'Studierendenvereinigungen',
        desc: 'Treten Sie studentischen Organisationen und Clubs bei',
      },
      facilities: { title: 'Einrichtungen', desc: 'Campuseinrichtungen und Ressourcen' },
      calendar: { title: 'Akademischer Kalender', desc: 'Wichtige Termine und Fristen' },
      exams: { title: 'Prüfungsperiode', desc: 'Prüfungspläne und Informationen' },
      myupb: { title: 'MyUPB Portal', desc: 'Universitätsverwaltungssystem' },
      courses: { title: 'Online-Kurse', desc: 'E-Learning-Plattform und Kursmaterialien' },
      ticketing: { title: 'Ticketing-System', desc: 'IT-Support und Helpdesk' },
    },
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
      admin: 'Admin',
    },
    map: 'Carte du campus',
    linksPage: {
      title: 'Liens Utiles pour les Étudiants',
      intl: {
        title: 'Étudiants Internationaux',
        desc: 'Informations pour les étudiants internationaux à la FILS',
      },
      portal: {
        title: 'Portail Étudiant',
        desc: 'Informations générales et services aux étudiants',
      },
      activities: {
        title: 'Activités Étudiantes',
        desc: 'Activités et événements pour les étudiants',
      },
      associations: {
        title: 'Associations Étudiantes',
        desc: 'Rejoignez des organisations et clubs étudiants',
      },
      facilities: { title: 'Installations', desc: 'Installations et ressources du campus' },
      calendar: { title: 'Calendrier Académique', desc: 'Dates importantes et échéances' },
      exams: { title: "Session d'examens", desc: "Horaires d'examens et informations" },
      myupb: { title: 'Portail MyUPB', desc: 'Système de gestion universitaire' },
      courses: {
        title: 'Cours en ligne',
        desc: "Plateforme d'apprentissage en ligne et supports de cours",
      },
      ticketing: { title: 'Système de Tickets', desc: 'Support informatique et assistance' },
    },
  },
}

export const i18n = createI18n({
  legacy: false, // Use Composition API mode
  locale: 'en', // Default language
  fallbackLocale: 'en',
  messages,
})
