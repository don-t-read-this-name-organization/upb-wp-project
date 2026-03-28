<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const weatherData = ref({ temp: null, description: t('common.loading'), icon: 'fa-cloud' })
const updatedTime = ref(`${t('weather.updated')}: --`)

const weatherIcons: Record<number, { icon: string; descKey: string }> = {
  0: { icon: 'fa-sun', descKey: 'weather.clearSky' },
  1: { icon: 'fa-sun', descKey: 'weather.mainlyClear' },
  2: { icon: 'fa-cloud-sun', descKey: 'weather.partlyCloudy' },
  3: { icon: 'fa-cloud', descKey: 'weather.overcast' },
  45: { icon: 'fa-smog', descKey: 'weather.foggy' },
  48: { icon: 'fa-smog', descKey: 'weather.depositingRimeFog' },
  51: { icon: 'fa-cloud-rain', descKey: 'weather.drizzle' },
  53: { icon: 'fa-cloud-rain', descKey: 'weather.drizzle' },
  55: { icon: 'fa-cloud-rain', descKey: 'weather.drizzle' },
  61: { icon: 'fa-cloud-showers-heavy', descKey: 'weather.rain' },
  63: { icon: 'fa-cloud-showers-heavy', descKey: 'weather.rain' },
  65: { icon: 'fa-cloud-showers-heavy', descKey: 'weather.rain' },
  71: { icon: 'fa-snowflake', descKey: 'weather.snow' },
  73: { icon: 'fa-snowflake', descKey: 'weather.snow' },
  75: { icon: 'fa-snowflake', descKey: 'weather.snow' },
  77: { icon: 'fa-snowflake', descKey: 'weather.snowGrains' },
  80: { icon: 'fa-cloud-rain', descKey: 'weather.rainShowers' },
  81: { icon: 'fa-cloud-rain', descKey: 'weather.rainShowers' },
  82: { icon: 'fa-cloud-rain', descKey: 'weather.rainShowers' },
  85: { icon: 'fa-snowflake', descKey: 'weather.snowShowers' },
  86: { icon: 'fa-snowflake', descKey: 'weather.snowShowers' },
  95: { icon: 'fa-bolt', descKey: 'weather.thunderstorm' },
  96: { icon: 'fa-bolt', descKey: 'weather.thunderstorm' },
  99: { icon: 'fa-bolt', descKey: 'weather.thunderstorm' },
}

async function fetchWeather() {
  try {
    const response = await fetch(
      'https://api.open-meteo.com/v1/forecast?latitude=44.4268&longitude=26.1025&current_weather=true'
    )
    const data = await response.json()
    if (data.current_weather) {
      const code = data.current_weather.weathercode ?? data.current_weather.weatherCode
      const weatherInfo = weatherIcons[code] || { icon: 'fa-cloud', descKey: 'weather.unavailable' }
      weatherData.value = {
        temp: data.current_weather.temperature,
        description: t(weatherInfo.descKey),
        icon: weatherInfo.icon,
      }
      const now = new Date()
      updatedTime.value = `${t('weather.updated')}: ${now.toLocaleTimeString()}`
    }
  } catch (error) {
    console.error('Weather fetch failed', error)
    weatherData.value.description = t('weather.unavailable')
  }
}

onMounted(() => {
  fetchWeather()
  setInterval(fetchWeather, 600000)
})
</script>

<template>
  <div class="weather-widget">
    <i :class="['fas', weatherData.icon, 'widget-icon']"></i>
    <h3>{{ t('weather.title') }}</h3>
    <p>{{ t('weather.bucharest') }}: {{ weatherData.temp !== null ? weatherData.temp : '--' }}°C - {{ weatherData.description }}</p>
    <small>{{ updatedTime }}</small>
  </div>
</template>

<style scoped>
.weather-widget {
  flex: 1;
  min-width: 240px;
  padding: 1.25rem;
  background-color: var(--bg-secondary);
  border-radius: var(--radius);
  text-align: center;
  border: 1px solid var(--border-light);
}

.widget-icon {
  font-size: 1.75rem;
  color: var(--primary-color);
  margin-bottom: 0.25rem;
  display: block;
  opacity: 0.7;
}

.weather-widget h3 {
  margin: 0.5rem 0;
  font-size: 1rem;
}

.weather-widget p {
  color: var(--text-muted);
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.weather-widget small {
  color: var(--text-muted);
  font-size: 0.8rem;
}
</style>
