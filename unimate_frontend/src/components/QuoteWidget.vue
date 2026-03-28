<script setup lang="ts">
import { ref, onMounted } from 'vue'

const quotes = ref([
  "The only way to do great work is to love what you do. - Steve Jobs",
  "Success is not final, failure is not fatal: it is the courage to continue that counts. - Winston Churchill",
  "Education is the most powerful weapon which you can use to change the world. - Nelson Mandela",
  "The beautiful thing about learning is that no one can take it away from you. - B.B. King",
  "Live as if you were to die tomorrow. Learn as if you were to live forever. - Mahatma Gandhi",
  "The expert in anything was once a beginner. - Helen Hayes",
  "It does not matter how slowly you go as long as you do not stop. - Confucius",
  "The only person who is educated is the one who has learned how to learn and change. - Carl Rogers",
  "Success is the sum of small efforts, repeated day in and day out. - Robert Collier",
  "Believe you can and you're halfway there. - Theodore Roosevelt",
])

const currentQuote = ref('')

function rotateQuote() {
  const randomIndex = Math.floor(Math.random() * quotes.value.length)
  currentQuote.value = quotes.value[randomIndex]
}

onMounted(() => {
  rotateQuote()
  setInterval(rotateQuote, 10000)
})
</script>

<template>
  <div class="quote-widget">
    <i class="fas fa-quote-left widget-icon-quote"></i>
    <h3>Quote of the Day</h3>
    <p class="quote-text">{{ currentQuote }}</p>
  </div>
</template>

<style scoped>
.quote-widget {
  flex: 1;
  min-width: 240px;
  padding: 1.25rem;
  background-color: var(--bg-secondary);
  border-radius: var(--radius);
  text-align: center;
  border: 1px solid var(--border-light);
}

.widget-icon-quote {
  font-size: 1.75rem;
  color: var(--secondary-color);
  margin-bottom: 0.25rem;
  display: block;
  opacity: 0.7;
}

.quote-widget h3 {
  margin: 0.5rem 0;
  font-size: 1rem;
}

.quote-text {
  color: var(--text-muted);
  font-style: italic;
  font-size: 0.9rem;
  line-height: 1.6;
  margin: 0.75rem 0;
}
</style>
