<script setup lang="ts">
import { ref, onMounted } from 'vue'

const weatherData = ref({ temp: null, description: 'Loading...', icon: 'fa-cloud' })
const updatedTime = ref('Updated: Just now')

const weatherIcons = {
  0: { icon: 'fa-sun', desc: 'Clear sky' },
  1: { icon: 'fa-sun', desc: 'Mainly clear' },
  2: { icon: 'fa-cloud-sun', desc: 'Partly cloudy' },
  3: { icon: 'fa-cloud', desc: 'Overcast' },
  45: { icon: 'fa-smog', desc: 'Foggy' },
  48: { icon: 'fa-smog', desc: 'Depositing rime fog' },
  51: { icon: 'fa-cloud-rain', desc: 'Drizzle' },
  53: { icon: 'fa-cloud-rain', desc: 'Drizzle' },
  55: { icon: 'fa-cloud-rain', desc: 'Drizzle' },
  61: { icon: 'fa-cloud-showers-heavy', desc: 'Rain' },
  63: { icon: 'fa-cloud-showers-heavy', desc: 'Rain' },
  65: { icon: 'fa-cloud-showers-heavy', desc: 'Rain' },
  71: { icon: 'fa-snowflake', desc: 'Snow' },
  73: { icon: 'fa-snowflake', desc: 'Snow' },
  75: { icon: 'fa-snowflake', desc: 'Snow' },
  77: { icon: 'fa-snowflake', desc: 'Snow grains' },
  80: { icon: 'fa-cloud-rain', desc: 'Rain showers' },
  81: { icon: 'fa-cloud-rain', desc: 'Rain showers' },
  82: { icon: 'fa-cloud-rain', desc: 'Rain showers' },
  85: { icon: 'fa-snowflake', desc: 'Snow showers' },
  86: { icon: 'fa-snowflake', desc: 'Snow showers' },
  95: { icon: 'fa-bolt', desc: 'Thunderstorm' },
  96: { icon: 'fa-bolt', desc: 'Thunderstorm' },
  99: { icon: 'fa-bolt', desc: 'Thunderstorm' },
}

async function fetchWeather() {
  try {
    const response = await fetch(
      'https://api.open-meteo.com/v1/forecast?latitude=44.4268&longitude=26.1025&current_weather=true'
    )
    const data = await response.json()
    if (data.current_weather) {
      const code = data.current_weather.weathercode ?? data.current_weather.weatherCode
      const weatherInfo = weatherIcons[code] || { icon: 'fa-cloud', desc: 'Unknown' }
      weatherData.value = {
        temp: data.current_weather.temperature,
        description: weatherInfo.desc,
        icon: weatherInfo.icon,
      }
      const now = new Date()
      updatedTime.value = `Updated: ${now.toLocaleTimeString()}`
    }
  } catch (error) {
    console.error('Weather fetch failed', error)
    weatherData.value.description = 'Unavailable'
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
    <h3>Weather</h3>
    <p>Bucharest: {{ weatherData.temp !== null ? weatherData.temp : '--' }}°C - {{ weatherData.description }}</p>
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
