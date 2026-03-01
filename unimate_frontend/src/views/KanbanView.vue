<script setup>
import { ref, computed } from 'vue';
import draggable from 'vuedraggable';
import KanbanTask from '@/components/KanbanTask.vue';

const tasks = ref([
  { id: 1, title: 'Study for Math', description: 'Review chapters 5-7', date: 'Mar 15', priority: 'high', status: 'todo' },
  { id: 2, title: 'CTI Proposal', description: 'Write user stories', date: 'Mar 10', priority: 'medium', status: 'todo' },
  { id: 3, title: 'Build Vue Frontend', description: 'Implement drag-and-drop', date: 'Mar 20', priority: 'medium', status: 'inprogress' },
]);

const todoTasks = computed({
  get: () => tasks.value.filter(t => t.status === 'todo'),
  set: (val) => updateTaskStatus(val, 'todo')
});

const inProgressTasks = computed({
  get: () => tasks.value.filter(t => t.status === 'inprogress'),
  set: (val) => updateTaskStatus(val, 'inprogress')
});

const doneTasks = computed({
  get: () => tasks.value.filter(t => t.status === 'done'),
  set: (val) => updateTaskStatus(val, 'done')
});

const updateTaskStatus = (newTasks, newStatus) => {
  newTasks.forEach(task => {
    const originalTask = tasks.value.find(t => t.id === task.id);
    if (originalTask) originalTask.status = newStatus;
  });
};

const deleteTask = (id) => {
  if (confirm('Delete this task?')) {
    tasks.value = tasks.value.filter(t => t.id !== id);
  }
};
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <div class="kanban-header">
        <h2 class="card-title"><i class="fas fa-columns"></i> Task Board</h2>
        <button class="btn btn-primary" @click="showModal = true">
          <i class="fas fa-plus"></i> Add Task
        </button>
      </div>

      <div class="kanban-board">
        <div class="kanban-column">
          <div class="column-header">
            <span class="column-title todo"><i class="fas fa-list"></i> To Do</span>
            <span class="task-count">{{ todoTasks.length }}</span>
          </div>
          <draggable 
            v-model="todoTasks" 
            group="tasks" 
            item-key="id" 
            class="tasks-container"
            ghost-class="dragging"
          >
            <template #item="{ element }">
              <KanbanTask :task="element" @delete="deleteTask" />
            </template>
          </draggable>
        </div>

        <div class="kanban-column">
          <div class="column-header">
            <span class="column-title inprogress"><i class="fas fa-spinner"></i> In Progress</span>
            <span class="task-count">{{ inProgressTasks.length }}</span>
          </div>
          <draggable v-model="inProgressTasks" group="tasks" item-key="id" class="tasks-container">
            <template #item="{ element }">
              <KanbanTask :task="element" @delete="deleteTask" />
            </template>
          </draggable>
        </div>

        <div class="kanban-column">
          <div class="column-header">
            <span class="column-title done"><i class="fas fa-check-circle"></i> Done</span>
            <span class="task-count">{{ doneTasks.length }}</span>
          </div>
          <draggable v-model="doneTasks" group="tasks" item-key="id" class="tasks-container">
            <template #item="{ element }">
              <KanbanTask :task="element" @delete="deleteTask" />
            </template>
          </draggable>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.kanban-board {
  display: flex;
  gap: 1.5rem;
  padding: 1rem;
  align-items: flex-start;
  overflow-x: auto;
}

.kanban-column {
  flex: 1;
  min-width: 300px;
  background-color: var(--bg-secondary); 
  border-radius: var(--radius);
  padding: 1.25rem;
  border: 1px solid var(--border-light);
}

.column-header {
  margin-bottom: 1.25rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid var(--border-color);
}

.column-title {
  font-family: var(--font-display);
  font-size: 1.1rem;
  color: var(--text-color);
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
</style>