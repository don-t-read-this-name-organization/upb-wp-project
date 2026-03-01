<script setup>
defineProps(['task']);
defineEmits(['edit', 'delete']);
</script>

<template>
  <div :class="['task-card', `priority-${task.priority}`]">
    <div class="task-actions">
      <button @click="$emit('edit', task.id)" title="Edit task"><i class="fas fa-edit"></i></button>
      <button @click="$emit('delete', task.id)" class="delete" title="Delete task"><i class="fas fa-trash"></i></button>
    </div>
    <div class="task-title">{{ task.title }}</div>
    <div class="task-description">{{ task.description }}</div>
    <div class="task-meta">
      <span class="task-date"><i class="fas fa-calendar"></i> {{ task.date }}</span>
      <span class="task-priority">
        {{ task.priority === 'high' ? '🔴' : task.priority === 'medium' ? '🟡' : '🟢' }} 
        {{ task.priority.charAt(0).toUpperCase() + task.priority.slice(1) }}
      </span>
    </div>
  </div>
</template>

<style scoped>
/* --- Task Card Base --- */
.task-card {
  background-color: var(--card-bg);
  border-radius: var(--radius-sm);
  padding: 1.25rem;
  margin-bottom: 1rem;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  position: relative;
  transition: var(--transition);
  border-left: 5px solid var(--primary-color); /* Default accent */
  cursor: grab;
}

.task-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
  border-color: var(--border-color);
}

/* --- Priority Indicators --- */
.task-card.priority-high { border-left-color: #d4a5a5; }   /* Using your --accent-blush */
.task-card.priority-medium { border-left-color: #c8ad7f; } /* Muted Gold */
.task-card.priority-low { border-left-color: var(--secondary-color); } /* Sage */

/* --- Typography --- */
.task-title {
  font-family: var(--font-body);
  font-weight: 600;
  font-size: 1rem;
  color: var(--text-color);
  margin-bottom: 0.5rem;
}

.task-description {
  font-size: 0.875rem;
  color: var(--text-muted);
  line-height: 1.5;
  margin-bottom: 1rem;
}

/* --- Meta & Footer --- */
.task-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.75rem;
  color: var(--text-subtle);
  border-top: 1px solid var(--border-light);
  padding-top: 0.75rem;
  margin-top: 0.5rem;
}

.task-date i {
  margin-right: 4px;
}

.task-priority {
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* --- Actions (Hover Only) --- */
.task-actions {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  display: flex;
  gap: 0.5rem;
  opacity: 0;
  transition: var(--transition);
}

.task-card:hover .task-actions {
  opacity: 1;
}

.task-actions button {
  background: var(--bg-secondary);
  border: none;
  border-radius: 4px;
  width: 26px;
  height: 26px;
  cursor: pointer;
  color: var(--text-muted);
  transition: var(--transition);
}

.task-actions button:hover {
  color: var(--primary-color);
  background: var(--bg-tertiary);
}

.task-actions button.delete:hover {
  color: var(--accent-blush);
}
</style>