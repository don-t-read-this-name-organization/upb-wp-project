import './assets/styles.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { i18n } from '@/i18n'
import { setupApiClient } from '@/utils/apiClient'

import App from './App.vue'
import router from './router'

const app = createApp(App)
setupApiClient()

app.use(createPinia())
app.use(router)
app.use(i18n)

app.mount('#app')
