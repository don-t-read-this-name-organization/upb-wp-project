<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import draggable from 'vuedraggable'
import { useAppStore } from '@/stores/appStore'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

interface Subtask {
  id: number
  taskId: number
  title: string
  completed: boolean
}

interface Task {
  id: number
  title: string
  description: string | null
  status: string | null
  priority: number | null
  kanbanColumn: string | null
  deadline: string | null
  userId: number | null
  createdAt: string | null
  active: boolean | null
  subtasks: Subtask[]
}

const store = useAppStore()
const tasks = ref<Task[]>([])
const showModal = ref(false)
const editingTask = ref<Task | null>(null)

const formTitle = ref('')
const formDescription = ref('')
const formPriority = ref<'high' | 'medium' | 'low'>('medium')
const formStatus = ref<'TODO' | 'IN_PROGRESS' | 'DONE'>('TODO')
const formKanbanColumn = ref('TODO')
const formDeadline = ref('')
const formSubtasks = ref<string[]>([])

const getAuthHeaders = () => ({
  'Content-Type': 'application/json',
})

const fetchTasks = async () => {
  if (!store.user?.id) return
  try {
    const response = await fetch(`/api/tasks/user/${store.user.id}`, {
      headers: getAuthHeaders(),
    })
    if (response.ok) {
      tasks.value = await response.json()
    }
  } catch (error) {
    console.error('Failed to fetch tasks:', error)
  }
}

const createTask = async () => {
  if (!formTitle.value.trim()) {
    alert(t('kanban.pleaseEnterTitle'))
    return
  }
  if (!store.user?.id) {
    alert(t('kanban.youMustBeLoggedIn'))
    return
  }

  const priorityMap = { high: 3, medium: 2, low: 1 }
  const statusMap = { TODO: 'TODO', IN_PROGRESS: 'IN_PROGRESS', DONE: 'DONE' }

  const subtasks = formSubtasks.value
    .filter(s => s.trim())
    .map(title => ({ title, completed: false }))

  try {
    const response = await fetch('/api/tasks', {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({
        title: formTitle.value,
        description: formDescription.value || null,
        priority: priorityMap[formPriority.value],
        status: statusMap[formStatus.value],
        kanbanColumn: formKanbanColumn.value,
        deadline: formDeadline.value || null,
        userId: store.user.id,
        subtasks: subtasks.length > 0 ? subtasks : null,
      }),
    })

    if (response.ok) {
      const newTask = await response.json()
      tasks.value.push(newTask)
      closeModal()
    } else {
      const error = await response.json()
      console.error('Failed to create task:', error)
      alert('Failed to create task: ' + (error.error || 'Unknown error'))
    }
  } catch (error) {
    console.error('Failed to create task:', error)
    alert('Failed to create task. Check console for details.')
  }
}

const updateTask = async () => {
  if (!editingTask.value) return
  if (!formTitle.value.trim()) {
    alert(t('kanban.pleaseEnterTitle'))
    return
  }

  const priorityMap = { high: 3, medium: 2, low: 1 }
  const statusMap = { TODO: 'TODO', IN_PROGRESS: 'IN_PROGRESS', DONE: 'DONE' }

  const subtasks = formSubtasks.value
    .filter(s => s.trim())
    .map(title => ({ title, completed: false }))

  try {
    const response = await fetch(`/api/tasks/${editingTask.value.id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify({
        title: formTitle.value,
        description: formDescription.value || null,
        priority: priorityMap[formPriority.value],
        status: statusMap[formStatus.value],
        kanbanColumn: formKanbanColumn.value,
        deadline: formDeadline.value || null,
        subtasks: subtasks.length > 0 ? subtasks : null,
      }),
    })

    if (response.ok) {
      const updatedTask = await response.json()
      const idx = tasks.value.findIndex((t) => t.id === editingTask.value?.id)
      if (idx !== -1) {
        tasks.value[idx] = updatedTask
      }
      closeModal()
    } else {
      const error = await response.json()
      console.error('Failed to update task:', error)
    }
  } catch (error) {
    console.error('Failed to update task:', error)
  }
}

const toggleSubtask = async (subtask: Subtask) => {
  try {
    await fetch(`/api/tasks/subtasks/${subtask.id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify({
        completed: !subtask.completed
      }),
    })
    subtask.completed = !subtask.completed
  } catch (error) {
    console.error('Failed to toggle subtask:', error)
  }
}

const deleteTask = async (id: number) => {
  if (!confirm(t('kanban.deleteTask'))) return

  try {
    const response = await fetch(`/api/tasks/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    })

    if (response.ok) {
      tasks.value = tasks.value.filter((t) => t.id !== id)
    }
  } catch (error) {
    console.error('Failed to delete task:', error)
  }
}

const saveTask = () => {
  if (editingTask.value) {
    updateTask()
  } else {
    createTask()
  }
}

const getTaskStatus = (task: Task): string => {
  return task.kanbanColumn || task.status || 'TODO'
}

const setTaskStatus = async (task: Task, newStatus: string) => {
  task.kanbanColumn = newStatus

  try {
    await fetch(`/api/tasks/${task.id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify({
        kanbanColumn: newStatus,
        status: newStatus,
      }),
    })
  } catch (error) {
    console.error('Failed to update task status:', error)
  }
}

const todoTasks = computed({
  get: () => tasks.value.filter((t) => getTaskStatus(t) === 'TODO'),
  set: (val) => {
    val.forEach((task) => setTaskStatus(task, 'TODO'))
  },
})

const inProgressTasks = computed({
  get: () => tasks.value.filter((t) => getTaskStatus(t) === 'IN_PROGRESS'),
  set: (val) => {
    val.forEach((task) => setTaskStatus(task, 'IN_PROGRESS'))
  },
})

const doneTasks = computed({
  get: () => tasks.value.filter((t) => getTaskStatus(t) === 'DONE'),
  set: (val) => {
    val.forEach((task) => setTaskStatus(task, 'DONE'))
  },
})

const priorityToLabel = (p: number | null): 'high' | 'medium' | 'low' => {
  if (p === 3) return 'high'
  if (p === 2) return 'medium'
  return 'low'
}

const completedSubtasks = (task: Task): number => {
  return task.subtasks?.filter(s => s.completed).length || 0
}

const totalSubtasks = (task: Task): number => {
  return task.subtasks?.length || 0
}

const openModal = (task?: Task) => {
  if (task) {
    editingTask.value = task
    formTitle.value = task.title
    formDescription.value = task.description || ''
    formPriority.value = priorityToLabel(task.priority)
    formKanbanColumn.value = task.kanbanColumn || 'TODO'
    formStatus.value = (task.status as 'TODO' | 'IN_PROGRESS' | 'DONE') || 'TODO'
    formDeadline.value = task.deadline || ''
    formSubtasks.value = task.subasks?.map(s => s.title) || []
  } else {
    editingTask.value = null
    formTitle.value = ''
    formDescription.value = ''
    formPriority.value = 'medium'
    formKanbanColumn.value = 'TODO'
    formStatus.value = 'TODO'
    formDeadline.value = ''
    formSubtasks.value = []
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingTask.value = null
}

const addSubtaskInput = () => {
  formSubtasks.value.push('')
}

const removeSubtaskInput = (index: number) => {
  formSubtasks.value.splice(index, 1)
}

const formatDeadline = (deadline: string | null): string => {
  if (!deadline) return ''
  const date = new Date(deadline)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

const isOverdue = (deadline: string | null): boolean => {
  if (!deadline) return false
  return new Date(deadline) < new Date()
}

onMounted(() => {
  fetchTasks()
})
</script>

<template>
  <main class="main-content">
    <div class="card fade-in">
      <div class="kanban-header">
        <h2 class="card-title"><i class="fas fa-columns"></i> {{ t('kanban.title') }}</h2>
        <button class="btn btn-primary" @click="openModal()">
          <i class="fas fa-plus"></i> {{ t('kanban.addTask') }}
        </button>
      </div>

      <div class="kanban-board">
        <div class="kanban-column">
          <div class="column-header">
            <span class="column-title todo"><i class="fas fa-list"></i> {{ t('kanban.todo') }}</span>
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
                :class="['task-card', `priority-${priorityToLabel(element.priority)}`, { overdue: isOverdue(element.deadline) }]"
                @click="openModal(element)"
              >
                <div class="task-actions">
                  <button @click.stop="deleteTask(element.id)" class="delete" title="Delete task">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
                <div class="task-title">{{ element.title }}</div>
                <div v-if="element.description" class="task-description">{{ element.description }}</div>
                <div v-if="element.deadline" class="task-deadline" :class="{ 'deadline-overdue': isOverdue(element.deadline) }">
                  <i class="fas fa-calendar"></i> {{ formatDeadline(element.deadline) }}
                </div>
                <div v-if="element.subtasks && element.subtasks.length > 0" class="task-subtasks">
                  <div class="subtask-progress">
                    <span>{{ completedSubtasks(element) }}/{{ totalSubtasks(element) }}</span>
                    <i class="fas fa-check-square"></i>
                  </div>
                </div>
                <div class="task-meta">
                  <span class="task-priority"
                    >{{
                      priorityToLabel(element.priority) === 'high'
                        ? '🔴'
                        : priorityToLabel(element.priority) === 'medium'
                          ? '🟡'
                          : '🟢'
                    }}
                    {{ t(`kanban.${priorityToLabel(element.priority)}`) }}</span
                  >
                </div>
              </div>
            </template>
          </draggable>
        </div>

        <div class="kanban-column">
          <div class="column-header">
            <span class="column-title inprogress"><i class="fas fa-spinner"></i> {{ t('kanban.inProgress') }}</span>
            <span class="task-count">{{ inProgressTasks.length }}</span>
          </div>
          <draggable v-model="inProgressTasks" group="tasks" item-key="id" class="tasks-container">
            <template #item="{ element }">
              <div
                :class="['task-card', `priority-${priorityToLabel(element.priority)}`, { overdue: isOverdue(element.deadline) }]"
                @click="openModal(element)"
              >
                <div class="task-actions">
                  <button @click.stop="deleteTask(element.id)" class="delete">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
                <div class="task-title">{{ element.title }}</div>
                <div v-if="element.description" class="task-description">{{ element.description }}</div>
                <div v-if="element.deadline" class="task-deadline" :class="{ 'deadline-overdue': isOverdue(element.deadline) }">
                  <i class="fas fa-calendar"></i> {{ formatDeadline(element.deadline) }}
                </div>
                <div v-if="element.subtasks && element.subtasks.length > 0" class="task-subtasks">
                  <div class="subtask-progress">
                    <span>{{ completedSubtasks(element) }}/{{ totalSubtasks(element) }}</span>
                    <i class="fas fa-check-square"></i>
                  </div>
                </div>
                <div class="task-meta">
                  <span class="task-priority"
                    >{{
                      priorityToLabel(element.priority) === 'high'
                        ? '🔴'
                        : priorityToLabel(element.priority) === 'medium'
                          ? '🟡'
                          : '🟢'
                    }}
                    {{ t(`kanban.${priorityToLabel(element.priority)}`) }}</span
                  >
                </div>
              </div>
            </template>
          </draggable>
        </div>

        <div class="kanban-column">
          <div class="column-header">
            <span class="column-title done"><i class="fas fa-check-circle"></i> {{ t('kanban.done') }}</span>
            <span class="task-count">{{ doneTasks.length }}</span>
          </div>
          <draggable v-model="doneTasks" group="tasks" item-key="id" class="tasks-container">
            <template #item="{ element }">
              <div
                :class="['task-card', `priority-${priorityToLabel(element.priority)}`, { overdue: isOverdue(element.deadline) }]"
                @click="openModal(element)"
              >
                <div class="task-actions">
                  <button @click.stop="deleteTask(element.id)" class="delete">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
                <div class="task-title">{{ element.title }}</div>
                <div v-if="element.description" class="task-description">{{ element.description }}</div>
                <div v-if="element.deadline" class="task-deadline">
                  <i class="fas fa-calendar"></i> {{ formatDeadline(element.deadline) }}
                </div>
                <div v-if="element.subtasks && element.subtasks.length > 0" class="task-subtasks">
                  <div class="subtask-progress">
                    <span>{{ completedSubtasks(element) }}/{{ totalSubtasks(element) }}</span>
                    <i class="fas fa-check-square"></i>
                  </div>
                </div>
                <div class="task-meta">
                  <span class="task-priority"
                    >{{
                      priorityToLabel(element.priority) === 'high'
                        ? '🔴'
                        : priorityToLabel(element.priority) === 'medium'
                          ? '🟡'
                          : '🟢'
                    }}
                    {{ t(`kanban.${priorityToLabel(element.priority)}`) }}</span
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
    <div class="modal-content modal-large" @click.stop>
      <div class="modal-header">
        <h3>{{ editingTask ? t('kanban.editTask') : t('kanban.addTask') }}</h3>
        <button class="modal-close" @click="closeModal">&times;</button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label for="taskTitle">{{ t('kanban.taskTitle') }}</label>
          <input
            v-model="formTitle"
            type="text"
            id="taskTitle"
            class="form-control"
            :placeholder="t('kanban.taskTitle') + '...'"
          />
        </div>
        <div class="form-group">
          <label for="taskDescription">{{ t('kanban.taskDescription') }}</label>
          <textarea
            v-model="formDescription"
            id="taskDescription"
            class="form-control"
            :placeholder="t('kanban.taskDescription') + '...'"
            rows="3"
          ></textarea>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label for="taskStatus">{{ t('kanban.status') }}</label>
            <select v-model="formStatus" id="taskStatus" class="form-control">
              <option value="TODO">{{ t('kanban.todo') }}</option>
              <option value="IN_PROGRESS">{{ t('kanban.inProgress') }}</option>
              <option value="DONE">{{ t('kanban.done') }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="taskPriority">{{ t('kanban.priority') }}</label>
            <select v-model="formPriority" id="taskPriority" class="form-control">
              <option value="high">{{ t('kanban.high') }}</option>
              <option value="medium">{{ t('kanban.medium') }}</option>
              <option value="low">{{ t('kanban.low') }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="taskDeadline">{{ t('kanban.dueDate') }}</label>
            <input
              v-model="formDeadline"
              type="date"
              id="taskDeadline"
              class="form-control"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="subtasks-header">
            <label>{{ t('kanban.subtasks') }}</label>
            <button type="button" class="btn btn-sm btn-secondary" @click="addSubtaskInput">
              <i class="fas fa-plus"></i> {{ t('kanban.addSubtask') }}
            </button>
          </div>
          <div v-if="editingTask && editingTask.subtasks && editingTask.subtasks.length > 0" class="existing-subtasks">
            <div v-for="subtask in editingTask.subtasks" :key="subtask.id" class="subtask-item">
              <input
                type="checkbox"
                :checked="subtask.completed"
                @change="toggleSubtask(subtask)"
              />
              <span :class="{ completed: subtask.completed }">{{ subtask.title }}</span>
            </div>
          </div>
          <div v-if="formSubtasks.length > 0" class="new-subtasks">
            <div v-for="(subtask, index) in formSubtasks" :key="index" class="subtask-input-row">
              <input
                v-model="formSubtasks[index]"
                type="text"
                class="form-control"
                placeholder="Subtask title..."
              />
              <button type="button" class="btn-remove" @click="removeSubtaskInput(index)">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <p v-else class="no-subtasks">{{ t('kanban.noSubtasks') }}</p>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="closeModal">{{ t('kanban.cancel') }}</button>
        <button class="btn btn-primary" @click="saveTask">
          {{ editingTask ? t('kanban.update') : t('kanban.create') }} {{ t('kanban.title').toLowerCase() }}
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
.task-card.overdue {
  border-left-color: #dc3545;
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
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.task-deadline {
  font-size: 0.75rem;
  color: var(--text-subtle);
  margin-bottom: 0.5rem;
}
.task-deadline.deadline-overdue {
  color: #dc3545;
  font-weight: 600;
}
.task-subtasks {
  margin-bottom: 0.5rem;
}
.subtask-progress {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  color: var(--text-subtle);
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
.modal-large {
  max-width: 600px;
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
.subtasks-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}
.subtasks-header label {
  margin: 0;
}
.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
}
.existing-subtasks {
  margin-bottom: 1rem;
}
.subtask-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  background: var(--bg-secondary);
  border-radius: 4px;
  margin-bottom: 0.25rem;
}
.subtask-item input[type="checkbox"] {
  cursor: pointer;
}
.subtask-item span.completed {
  text-decoration: line-through;
  color: var(--text-muted);
}
.new-subtasks {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.subtask-input-row {
  display: flex;
  gap: 0.5rem;
}
.subtask-input-row input {
  flex: 1;
}
.btn-remove {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0.5rem;
}
.btn-remove:hover {
  color: #dc3545;
}
.no-subtasks {
  color: var(--text-muted);
  font-size: 0.85rem;
  font-style: italic;
}
</style>
