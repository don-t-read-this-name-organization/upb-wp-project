<script setup lang="ts">
import { ref, computed } from 'vue'

export interface Toast {
  id: string
  type: 'success' | 'error' | 'info' | 'warning'
  title: string
  message?: string
  duration?: number
}

const toasts = ref<Toast[]>([])
let toastCounter = 0

const activeToasts = computed(() => toasts.value.filter(t => t.id))

function addToast(type: Toast['type'], title: string, message?: string, duration = 3000) {
  const id = `toast-${++toastCounter}`
  const toast: Toast = { id, type, title, message, duration }

  toasts.value.push(toast)

  if (duration > 0) {
    setTimeout(() => removeToast(id), duration)
  }

  return id
}

function removeToast(id: string) {
  const index = toasts.value.findIndex(t => t.id === id)
  if (index > -1) {
    toasts.value.splice(index, 1)
  }
}

function clearAll() {
  toasts.value = []
}

defineExpose({
  addToast,
  removeToast,
  clearAll,
})
</script>

<template>
  <Teleport to="body">
    <div class="toast-container">
      <transition-group name="toast-fade" tag="div">
        <div v-for="toast in activeToasts" :key="toast.id" :class="['toast', `toast-${toast.type}`]">
          <div class="toast-content">
            <strong class="toast-title">{{ toast.title }}</strong>
            <p v-if="toast.message" class="toast-message">{{ toast.message }}</p>
          </div>
          <button class="toast-close" @click="removeToast(toast.id)">
            <i class="fas fa-times"></i>
          </button>
        </div>
      </transition-group>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 400px;
}

.toast {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 16px;
  border-radius: var(--radius);
  background-color: var(--card-bg);
  border-left: 4px solid;
  box-shadow: var(--shadow-md);
  animation: slideIn 0.3s ease;
}

.toast-success {
  border-left-color: #4caf50;
  background: linear-gradient(to right, rgba(76, 175, 80, 0.1), transparent);
}

.toast-error {
  border-left-color: #f44336;
  background: linear-gradient(to right, rgba(244, 67, 54, 0.1), transparent);
}

.toast-info {
  border-left-color: #2196f3;
  background: linear-gradient(to right, rgba(33, 150, 243, 0.1), transparent);
}

.toast-warning {
  border-left-color: #ff9800;
  background: linear-gradient(to right, rgba(255, 152, 0, 0.1), transparent);
}

.toast-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.toast-title {
  font-size: 0.95rem;
  color: var(--text-color);
  margin: 0;
}

.toast-message {
  font-size: 0.85rem;
  color: var(--text-muted);
  margin: 0;
}

.toast-close {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
}

.toast-close:hover {
  color: var(--text-color);
}

@keyframes slideIn {
  from {
    transform: translateX(400px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: var(--transition);
}

.toast-fade-enter-from {
  transform: translateX(400px);
  opacity: 0;
}

.toast-fade-leave-to {
  transform: translateX(400px);
  opacity: 0;
}
</style>
