<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    modelValue: boolean
    title?: string
    size?: 'sm' | 'md' | 'lg' | 'xl'
    closeOnOverlay?: boolean
    showClose?: boolean
  }>(),
  {
    size: 'md',
    closeOnOverlay: true,
    showClose: true,
  },
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'close'): void
}>()

function close() {
  emit('update:modelValue', false)
  emit('close')
}

function onOverlayClick() {
  if (props.closeOnOverlay) {
    close()
  }
}
</script>

<template>
  <Teleport to="body">
    <div v-if="modelValue" class="base-modal-overlay" @click="onOverlayClick">
      <div class="base-modal-shell" :class="`size-${size}`" @click.stop>
        <div v-if="$slots.header || title || showClose" class="base-modal-header">
          <slot name="header">
            <h3 v-if="title">{{ title }}</h3>
          </slot>
          <button v-if="showClose" class="base-modal-close" type="button" @click="close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="base-modal-body">
          <slot />
        </div>
        <div v-if="$slots.footer" class="base-modal-footer">
          <slot name="footer" />
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.base-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.base-modal-shell {
  width: 100%;
  background: var(--card-bg);
  border-radius: var(--radius);
  box-shadow: var(--shadow-lg);
  max-height: 90vh;
  overflow-y: auto;
}

.base-modal-shell.size-sm {
  max-width: 420px;
}

.base-modal-shell.size-md {
  max-width: 560px;
}

.base-modal-shell.size-lg {
  max-width: 760px;
}

.base-modal-shell.size-xl {
  max-width: 980px;
}

.base-modal-header {
  padding: 1rem 1.25rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-light);
}

.base-modal-header h3 {
  margin: 0;
}

.base-modal-close {
  background: none;
  border: none;
  font-size: 1.1rem;
  color: var(--text-muted);
  cursor: pointer;
}

.base-modal-body {
  padding: 1rem 1.25rem;
}

.base-modal-footer {
  padding: 1rem 1.25rem;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  border-top: 1px solid var(--border-light);
}
</style>
