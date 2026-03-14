<script setup lang="ts">
import { ref, computed } from 'vue'
import draggable from 'vuedraggable'

interface Task {
  id: number
  title: string
  description: string
  date: string
  priority: 'high' | 'medium' | 'low'
  status: 'todo' | 'inprogress' | 'done'
}

const tasks = ref<Task[]>([
  {
    id: 1,
    title: 'Study for Math',
    description: 'Review chapters 5-7',
    date: 'Mar 15',
    priority: 'high',
    status: 'todo',
  },
  {
    id: 2,
    title: 'CTI Proposal',
    description: 'Write user stories',
    date: 'Mar 10',
    priority: 'medium',
    status: 'todo',
  },
  {
    id: 3,
    title: 'Build Vue Frontend',
    description: 'Implement drag-and-drop',
    date: 'Mar 20',
    priority: 'medium',
    status: 'inprogress',
  },
])

const showModal = ref(false)
const editingTask = ref<Task | null>(null)

const formTitle = ref('')
const formDescription = ref('')
const formDate = ref('')
const formPriority = ref<'high' | 'medium' | 'low'>('medium')

const todoTasks = computed({
  get: () => tasks.value.filter((t) => t.status === 'todo'),
  set: (val) => updateTaskStatus(val, 'todo'),
})

const inProgressTasks = computed({
  get: () => tasks.value.filter((t) => t.status === 'inprogress'),
  set: (val) => updateTaskStatus(val, 'inprogress'),
})

const doneTasks = computed({
  get: () => tasks.value.filter((t) => t.status === 'done'),
  set: (val) => updateTaskStatus(val, 'done'),
})

const updateTaskStatus = (newTasks: Task[], newStatus: string) => {
  newTasks.forEach((task) => {
    const originalTask = tasks.value.find((t) => t.id === task.id)
    if (originalTask) originalTask.status = newStatus as Task['status']
  })
}

const openModal = (task?: Task) => {
  if (task) {
    editingTask.value = task
    formTitle.value = task.title
    formDescription.value = task.description
    formDate.value = task.date
    formPriority.value = task.priority
  } else {
    editingTask.value = null
    formTitle.value = ''
    formDescription.value = ''
    formDate.value = ''
    formPriority.value = 'medium'
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingTask.value = null
}

const saveTask = () => {
  if (!formTitle.value.trim()) return

  if (editingTask.value) {
    const idx = tasks.value.findIndex((t) => t.id === editingTask.value?.id)
    if (idx !== -1) {
      tasks.value[idx] = {
        ...tasks.value[idx],
        title: formTitle.value,
        description: formDescription.value,
        date: formDate.value,
        priority: formPriority.value,
      }
    }
  } else {
    tasks.value.push({
      id: Date.now(),
      title: formTitle.value,
      description: formDescription.value,
      date: formDate.value || 'TBD',
      priority: formPriority.value,
      status: 'todo',
    })
  }
  closeModal()
}

const deleteTask = (id: number) => {
  if (confirm('Delete this task?')) {
    tasks.value = tasks.value.filter((t) => t.id !== id)
  }
}
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <div class="kanban-header">
        <h2 class="card-title"><i class="fas fa-columns"></i> Task Board</h2>
        <button class="btn btn-primary" @click="openModal()">
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
              <div
                :class="['task-card', `priority-${element.priority}`]"
                @click="openModal(element)"
              >
                <div class="task-actions">
                  <button @click.stop="deleteTask(element.id)" class="delete" title="Delete task">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
                <div class="task-title">{{ element.title }}</div>
                <div class="task-description">{{ element.description }}</div>
                <div class="task-meta">
                  <span class="task-date"><i class="fas fa-calendar"></i> {{ element.date }}</span>
                  <span class="task-priority"
                    >{{
                      element.priority === 'high'
                        ? '🔴'
                        : element.priority === 'medium'
                          ? '🟡'
                          : '🟢'
                    }}
                    {{ element.priority.charAt(0).toUpperCase() + element.priority.slice(1) }}</span
                  >
                </div>
              </div>
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
              <div
                :class="['task-card', `priority-${element.priority}`]"
                @click="openModal(element)"
              >
                <div class="task-actions">
                  <button @click.stop="deleteTask(element.id)" class="delete">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
                <div class="task-title">{{ element.title }}</div>
                <div class="task-description">{{ element.description }}</div>
                <div class="task-meta">
                  <span class="task-date"><i class="fas fa-calendar"></i> {{ element.date }}</span>
                  <span class="task-priority"
                    >{{
                      element.priority === 'high'
                        ? '🔴'
                        : element.priority === 'medium'
                          ? '🟡'
                          : '🟢'
                    }}
                    {{ element.priority.charAt(0).toUpperCase() + element.priority.slice(1) }}</span
                  >
                </div>
              </div>
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
              <div
                :class="['task-card', `priority-${element.priority}`]"
                @click="openModal(element)"
              >
                <div class="task-actions">
                  <button @click.stop="deleteTask(element.id)" class="delete">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
                <div class="task-title">{{ element.title }}</div>
                <div class="task-description">{{ element.description }}</div>
                <div class="task-meta">
                  <span class="task-date"><i class="fas fa-calendar"></i> {{ element.date }}</span>
                  <span class="task-priority"
                    >{{
                      element.priority === 'high'
                        ? '🔴'
                        : element.priority === 'medium'
                          ? '🟡'
                          : '🟢'
                    }}
                    {{ element.priority.charAt(0).toUpperCase() + element.priority.slice(1) }}</span
                  >
                </div>
              </div>
            </template>
          </draggable>
        </div>
      </div>
    </div>
  </main>

  <div v-if="showModal" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ editingTask ? 'Edit Task' : 'New Task' }}</h3>
        <button class="modal-close" @click="closeModal">&times;</button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label for="taskTitle">Title</label>
          <input
            v-model="formTitle"
            type="text"
            id="taskTitle"
            class="form-control"
            placeholder="Task title..."
          />
        </div>
        <div class="form-group">
          <label for="taskDescription">Description</label>
          <textarea
            v-model="formDescription"
            id="taskDescription"
            class="form-control"
            placeholder="Task description..."
          ></textarea>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label for="taskDate">Due Date</label>
            <input
              v-model="formDate"
              type="text"
              id="taskDate"
              class="form-control"
              placeholder="Mar 20"
            />
          </div>
          <div class="form-group">
            <label for="taskPriority">Priority</label>
            <select v-model="formPriority" id="taskPriority" class="form-control">
              <option value="high">High</option>
              <option value="medium">Medium</option>
              <option value="low">Low</option>
            </select>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="closeModal">Cancel</button>
        <button class="btn btn-primary" @click="saveTask">
          {{ editingTask ? 'Update' : 'Create' }} Task
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.kanban-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}
.kanban-board {
  display: flex;
  gap: 1.5rem;
  padding: 1rem;
  align-items: flex-start;
  overflow-x: auto;
}
.kanban-column {
  flex: 1;
  min-width: 280px;
  background: var(--bg-secondary);
  border-radius: var(--radius);
  padding: 1.25rem;
  border: 1px solid var(--border-light);
}
.column-header {
  margin-bottom: 1.25rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.column-title {
  font-family: var(--font-display);
  font-size: 1.1rem;
  color: var(--text-color);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.task-count {
  background: var(--primary-color);
  color: white;
  padding: 0.2rem 0.6rem;
  border-radius: 10px;
  font-size: 0.8rem;
}
.tasks-container {
  min-height: 200px;
}
.task-card {
  background: var(--card-bg);
  border-radius: var(--radius-sm);
  padding: 1rem;
  margin-bottom: 0.75rem;
  border: 1px solid var(--border-light);
  cursor: grab;
  border-left: 4px solid var(--primary-color);
}
.task-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}
.task-card.priority-high {
  border-left-color: #d4a5a5;
}
.task-card.priority-medium {
  border-left-color: #c8ad7f;
}
.task-card.priority-low {
  border-left-color: var(--secondary-color);
}
.task-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 0.5rem;
  opacity: 0;
  transition: opacity 0.2s;
}
.task-card:hover .task-actions {
  opacity: 1;
}
.task-actions button {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-muted);
}
.task-actions button.delete:hover {
  color: #dc3545;
}
.task-title {
  font-weight: 600;
  margin-bottom: 0.25rem;
}
.task-description {
  font-size: 0.85rem;
  color: var(--text-muted);
  margin-bottom: 0.5rem;
}
.task-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.75rem;
  color: var(--text-subtle);
  border-top: 1px solid var(--border-light);
  padding-top: 0.5rem;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-content {
  background: var(--card-bg);
  border-radius: 15px;
  width: 100%;
  max-width: 500px;
}
.modal-header {
  padding: 1.25rem;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-header h3 {
  margin: 0;
  color: var(--primary-color);
}
.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--text-muted);
}
.modal-body {
  padding: 1.25rem;
}
.modal-footer {
  padding: 1rem 1.25rem;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}
.form-row {
  display: flex;
  gap: 1rem;
}
.form-row .form-group {
  flex: 1;
}
</style>
