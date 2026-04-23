<script setup lang="ts">
</script>

<template>
  <main class="main-content">
    <div class="card fade-in getting-started">
      <h2 class="card-title">
        <i class="fas fa-compass"></i>
        <span>{{ $t('gettingStarted.title') }}</span>
      </h2>

      <p class="subtitle">{{ $t('gettingStarted.subtitle') }}</p>

      <div class="steps">
        <h3 class="steps-title">{{ $t('gettingStarted.stepsTitle') }}</h3>
        <ol class="steps-list">
          <li>{{ $t('gettingStarted.step1') }}</li>
          <li>{{ $t('gettingStarted.step2') }}</li>
          <li>{{ $t('gettingStarted.step3') }}</li>
        </ol>
      </div>

      <div class="actions">
        <router-link to="/register" class="btn btn-primary">
          <i class="fas fa-user-plus"></i>
          {{ $t('gettingStarted.register') }}
        </router-link>
        <router-link to="/login" class="btn btn-secondary">
          <i class="fas fa-sign-in-alt"></i>
          {{ $t('gettingStarted.login') }}
        </router-link>
      </div>
    </div>
  </main>
</template>

<style scoped>
.getting-started {
  max-width: 780px;
  margin: 2rem auto;
  padding: 2.25rem;
}

.subtitle {
  color: var(--text-muted);
  margin-top: 0.75rem;
  line-height: 1.7;
}

.steps {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-light);
}

.steps-title {
  margin: 0 0 0.75rem;
  font-family: var(--font-display);
  color: var(--text-color);
}

.steps-list {
  margin: 0;
  padding-left: 1.25rem;
  color: var(--text-color);
  line-height: 1.8;
}

.actions {
  margin-top: 1.75rem;
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.actions .btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}
</style>
