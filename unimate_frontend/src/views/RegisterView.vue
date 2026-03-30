<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t, locale } = useI18n()

interface Faculty {
  id: number
  name: string
  shortName: string
}

interface Group {
  id: number
  name: string
  year: number
}

interface ExistingUser {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
}

interface RegisterForm {
  username: string
  email: string
  password: string
  confirmPassword: string
  firstName: string
  lastName: string
  facultyId: number | null
  groupId: number | null
}

const form = ref<RegisterForm>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  firstName: '',
  lastName: '',
  facultyId: null,
  groupId: null,
})

const faculties = ref<Faculty[]>([])
const groups = ref<Group[]>([])
const groupsLoading = ref(false)

const error = ref('')
const loading = ref(false)
const success = ref(false)

const fieldErrors = ref<Record<string, string>>({})
const touched = ref<Record<string, boolean>>({})
const existingUsers = ref<ExistingUser[]>([])

const filteredGroups = computed(() => groups.value)
const facultiesLoading = ref(false)

const fetchFaculties = async () => {
  facultiesLoading.value = true
  try {
    const response = await fetch('/api/faculties')
    if (response.ok) {
      faculties.value = await response.json()
    }
  } catch (e) {
    console.error('Failed to fetch faculties:', e)
  } finally {
    facultiesLoading.value = false
  }
}

const fetchGroups = async () => {
  if (!form.value.facultyId) {
    groups.value = []
    form.value.groupId = null
    return
  }
  
  groupsLoading.value = true
  try {
    const response = await fetch(`/api/groups/faculty/${form.value.facultyId}`)
    if (response.ok) {
      groups.value = await response.json()
    }
  } catch (e) {
    console.error('Failed to fetch groups:', e)
  } finally {
    groupsLoading.value = false
  }
}

const onFacultyChange = () => {
  form.value.groupId = null
  groups.value = []
  fetchGroups()
  if (form.value.facultyId) {
    validateField('facultyId')
  }
}

const generateUsername = (force: boolean = false) => {
  if (form.value.firstName && form.value.lastName && (!form.value.username || force)) {
    const first = form.value.firstName.toLowerCase().replace(/[^a-zăâîșț]/g, '')
    const last = form.value.lastName.toLowerCase().replace(/[^a-zăâîșț]/g, '')
    let generated = first + '.' + last
    
    let counter = 1
    while (existingUsers.value.some(u => u.username && u.username.toLowerCase() === generated)) {
      generated = first + '.' + last + counter
      counter++
    }
    
    form.value.username = generated
  }
}

const fetchUsers = async () => {
  try {
    const response = await fetch('/api/users')
    if (response.ok) {
      existingUsers.value = await response.json()
      console.log('Users fetched:', existingUsers.value.length)
    }
  } catch (e) {
    console.error('Failed to fetch users:', e)
  }
}

const markTouched = (field: string) => {
  touched.value[field] = true
}

const validateEmpty = (field: string): string | null => {
  const fieldMap: Record<string, string> = {
    faculty: 'facultyId',
    group: 'groupId'
  }
  const formField = field.endsWith('Id') ? field : (fieldMap[field] || field)
  const value = form.value[formField as keyof RegisterForm]
  
  if (value === null || value === undefined || (typeof value === 'string' && !value.trim())) {
    const errorKey = field.endsWith('Id') ? field.replace('Id', '') : field
    return t(`register.${errorKey}Required`)
  }
  return null
}

const validateFormat = (field: string): string | null => {
  const value = form.value[field as keyof RegisterForm]
  if (!value || typeof value !== 'string') return null

  switch (field) {
    case 'firstName':
      if (value.length < 2) return t('register.firstNameTooShort')
      if (value.length > 50) return t('register.firstNameTooLong')
      break
    case 'lastName':
      if (value.length < 2) return t('register.lastNameTooShort')
      if (value.length > 50) return t('register.lastNameTooLong')
      break
    case 'email':
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(value)) return t('register.emailInvalid')
      break
  }
  return null
}

const validateConfirmPassword = () => {
  if (!touched.value.confirmPassword) return
  
  if (form.value.confirmPassword && form.value.password !== form.value.confirmPassword) {
    fieldErrors.value.confirmPassword = t('register.passwordMismatch')
  } else {
    delete fieldErrors.value.confirmPassword
  }
}

const validateDuplicates = (field: string): string | null => {
  console.log('Validating duplicates for:', field, 'users count:', existingUsers.value.length)
  
  if (!existingUsers.value.length) return null

  switch (field) {
    case 'email':
      if (form.value.email && form.value.email.trim()) {
        const exists = existingUsers.value.some(u => 
          u.email && u.email.toLowerCase() === form.value.email.toLowerCase()
        )
        console.log('Email check:', exists)
        if (exists) return t('register.emailExists')
      }
      break
    case 'username':
      if (form.value.username && form.value.username.trim()) {
        const exists = existingUsers.value.some(u => 
          u.username && u.username.toLowerCase() === form.value.username.toLowerCase()
        )
        console.log('Username check:', exists, 'form username:', form.value.username)
        if (exists) return t('register.usernameExists')
      }
      break
    case 'firstName':
    case 'lastName':
      if (form.value.firstName && form.value.lastName && 
          form.value.firstName.trim() && form.value.lastName.trim()) {
        const exists = existingUsers.value.some(u => 
          u.firstName && u.lastName &&
          u.firstName.toLowerCase() === form.value.firstName.toLowerCase() &&
          u.lastName.toLowerCase() === form.value.lastName.toLowerCase()
        )
        if (exists) return t('register.userAlreadyExists')
      }
      break
  }
  return null
}

const validateField = (field: string) => {
  if (!touched.value[field]) return

  const displayKey = field.replace('Id', '')
  
  const emptyError = validateEmpty(field)
  const formatError = validateFormat(field)
  const duplicateError = validateDuplicates(field)

  if (emptyError) {
    fieldErrors.value[displayKey] = emptyError
  } else if (formatError) {
    fieldErrors.value[displayKey] = formatError
  } else if (duplicateError) {
    fieldErrors.value[displayKey] = duplicateError
  } else {
    delete fieldErrors.value[displayKey]
  }

  if (field === 'firstName' || field === 'lastName') {
    const nameError = validateDuplicates('firstName')
    if (nameError) {
      fieldErrors.value['firstName'] = nameError
      fieldErrors.value['lastName'] = nameError
    } else if (!validateEmpty('firstName') && !validateEmpty('lastName') && !validateFormat('firstName') && !validateFormat('lastName')) {
      delete fieldErrors.value['firstName']
      delete fieldErrors.value['lastName']
    }
  }
}

watch(() => form.value.facultyId, () => {
  if (touched.value.facultyId) {
    validateField('facultyId')
  }
})

watch(() => form.value.groupId, () => {
  if (touched.value.groupId) {
    validateField('groupId')
  }
})

watch(() => form.value.confirmPassword, () => {
  if (touched.value.confirmPassword && form.value.confirmPassword) {
    if (form.value.password !== form.value.confirmPassword) {
      fieldErrors.value.confirmPassword = t('register.passwordMismatch')
    } else {
      delete fieldErrors.value.confirmPassword
    }
  }
})

watch(() => form.value.firstName, () => {
  generateUsername(true)
})

watch(() => form.value.lastName, () => {
  generateUsername(true)
})

watch(() => form.value.password, () => {
  if (touched.value.confirmPassword && form.value.confirmPassword) {
    if (form.value.password !== form.value.confirmPassword) {
      fieldErrors.value.confirmPassword = t('register.passwordMismatch')
    } else {
      delete fieldErrors.value.confirmPassword
    }
  }
})

onMounted(() => {
  fetchFaculties()
  fetchUsers()
})

const handleRegister = async () => {
  touched.value = {
    firstName: true,
    lastName: true,
    username: true,
    email: true,
    password: true,
    confirmPassword: true,
    facultyId: true,
    groupId: true
  }

  const emptyFields = ['firstName', 'lastName', 'username', 'email', 'password', 'confirmPassword', 'facultyId', 'groupId']
  for (const field of emptyFields) {
    const err = validateEmpty(field)
    if (err) {
      const key = field.replace('Id', '')
      fieldErrors.value[key] = err
    }
  }

  validateDuplicates('email')
  validateDuplicates('username')
  validateDuplicates('firstName')

  if (form.value.password !== form.value.confirmPassword) {
    error.value = t('register.passwordMismatch')
    return
  }

  if (form.value.password.length < 6) {
    error.value = t('register.passwordTooShort')
    return
  }

  if (Object.keys(fieldErrors.value).length > 0) {
    return
  }

  loading.value = true

  try {
    const response = await fetch('/api/users/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: form.value.username,
        email: form.value.email,
        password: form.value.password,
        firstName: form.value.firstName,
        lastName: form.value.lastName,
        role: 'STUDENT',
        facultyId: form.value.facultyId,
        groupId: form.value.groupId,
      }),
    })

    const data = await response.json()

    if (response.ok) {
      success.value = true
    } else {
      const message = data.message || data.error || t('register.failed')
      if (message.toLowerCase().includes('name')) {
        error.value = t('register.userAlreadyExists')
      } else {
        error.value = message
      }
    }
  } catch {
    error.value = t('register.connectionError')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="main-content flex-center">
    <div class="card register-container fade-in">
      <template v-if="!success">
        <h2 class="card-title text-center">
          <i class="fas fa-user-plus"></i>
          <span>{{ t('register.title') }}</span>
        </h2>

        <p class="register-subtitle">{{ t('register.subtitle') }}</p>

        <form id="registerForm" @submit.prevent="handleRegister">
          <div class="form-row">
            <div class="form-group">
              <label for="firstName">{{ t('register.firstName') }}</label>
              <input
                v-model="form.firstName"
                type="text"
                id="firstName"
                class="form-control"
                :class="{ 'input-error': fieldErrors.firstName }"
                :placeholder="t('register.firstNamePlaceholder')"
                @blur="markTouched('firstName')"
                @input="validateField('firstName')"
              />
              <span v-if="fieldErrors.firstName" class="field-error">{{ fieldErrors.firstName }}</span>
            </div>

            <div class="form-group">
              <label for="lastName">{{ t('register.lastName') }}</label>
              <input
                v-model="form.lastName"
                type="text"
                id="lastName"
                class="form-control"
                :class="{ 'input-error': fieldErrors.lastName }"
                :placeholder="t('register.lastNamePlaceholder')"
                @blur="markTouched('lastName')"
                @input="validateField('lastName')"
              />
              <span v-if="fieldErrors.lastName" class="field-error">{{ fieldErrors.lastName }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="username">{{ t('register.username') }}</label>
            <input
              v-model="form.username"
              type="text"
              id="username"
              class="form-control"
              :class="{ 'input-error': fieldErrors.username }"
              :placeholder="t('register.usernamePlaceholder')"
              @blur="markTouched('username')"
              @input="validateField('username')"
            />
            <span v-if="fieldErrors.username" class="field-error">{{ fieldErrors.username }}</span>
          </div>

          <div class="form-group">
            <label for="email">{{ t('register.email') }}</label>
            <input
              v-model="form.email"
              type="email"
              id="email"
              class="form-control"
              :class="{ 'input-error': fieldErrors.email }"
              :placeholder="t('register.emailPlaceholder')"
              @blur="markTouched('email')"
              @input="validateField('email')"
            />
            <span v-if="fieldErrors.email" class="field-error">{{ fieldErrors.email }}</span>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="faculty">{{ t('register.faculty') }}</label>
              <select
                v-model="form.facultyId"
                id="faculty"
                class="form-control"
                :class="{ 'input-error': fieldErrors.faculty }"
                :disabled="facultiesLoading"
                @change="onFacultyChange"
                @blur="markTouched('facultyId')"
              >
                <option :value="null" disabled>
                  {{ facultiesLoading ? t('common.loading') : t('register.selectFaculty') }}
                </option>
                <option v-for="faculty in faculties" :key="faculty.id" :value="faculty.id">
                  {{ faculty.shortName || faculty.name }}
                </option>
              </select>
              <span v-if="fieldErrors.faculty" class="field-error">{{ fieldErrors.faculty }}</span>
            </div>

            <div class="form-group">
              <label for="group">{{ t('register.group') }}</label>
              <select
                v-model="form.groupId"
                id="group"
                class="form-control"
                :class="{ 'input-error': fieldErrors.group }"
                :disabled="!form.facultyId || groupsLoading"
                @blur="markTouched('groupId')"
              >
                <option :value="null" disabled>
                  {{ groupsLoading ? t('register.loadingGroups') : t('register.selectGroup') }}
                </option>
                <option v-for="group in filteredGroups" :key="group.id" :value="group.id">
                  {{ group.name }} ({{ group.year }})
                </option>
              </select>
              <span v-if="fieldErrors.group" class="field-error">{{ fieldErrors.group }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="password">{{ t('register.password') }}</label>
            <input
              v-model="form.password"
              type="password"
              id="password"
              class="form-control"
              :class="{ 'input-error': fieldErrors.password }"
              :placeholder="t('register.passwordPlaceholder')"
              @blur="markTouched('password')"
              @input="validateField('password')"
            />
            <span v-if="fieldErrors.password" class="field-error">{{ fieldErrors.password }}</span>
          </div>

          <div class="form-group">
            <label for="confirmPassword">{{ t('register.confirmPassword') }}</label>
            <input
              v-model="form.confirmPassword"
              type="password"
              id="confirmPassword"
              class="form-control"
              :class="{ 'input-error': fieldErrors.confirmPassword }"
              :placeholder="t('register.confirmPasswordPlaceholder')"
              @blur="markTouched('confirmPassword')"
              @input="validateConfirmPassword"
            />
            <span v-if="fieldErrors.confirmPassword" class="field-error">{{ fieldErrors.confirmPassword }}</span>
          </div>

          <button type="submit" class="btn btn-primary btn-full-width" :disabled="loading">
            <i class="fas fa-user-plus"></i>
            <span>{{ loading ? t('register.registering') : t('register.submit') }}</span>
          </button>

          <p v-if="error" class="error-message">{{ error }}</p>
        </form>

        <div class="login-link">
          <p>
            {{ t('register.alreadyHaveAccount') }}
            <router-link to="/login">{{ t('register.loginLink') }}</router-link>
          </p>
        </div>
      </template>

      <template v-else>
        <div class="success-message">
          <i class="fas fa-check-circle"></i>
          <h3>{{ t('register.successTitle') }}</h3>
          <p>{{ t('register.successMessage') }}</p>
          <router-link to="/login" class="btn btn-primary">
            {{ t('register.goToLogin') }}
          </router-link>
        </div>
      </template>
    </div>
  </main>
</template>

<style scoped>
.register-container {
  max-width: 550px;
  margin: 2rem auto;
  padding: 2.5rem;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 150px);
}

.register-subtitle {
  color: var(--text-muted);
  text-align: center;
  margin-bottom: 1.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--text-color);
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  background: var(--bg-color);
  color: var(--text-color);
  font-size: 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
}

.form-control:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-control.input-error {
  border-color: #dc3545;
}

.form-control.input-error:focus {
  border-color: #dc3545;
  box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.1);
}

.field-error {
  display: block;
  color: #dc3545;
  font-size: 0.8rem;
  margin-top: 0.25rem;
}

.btn-full-width {
  width: 100%;
  margin-top: 1rem;
}

.fade-in {
  animation: fadeIn 0.5s ease-in-out;
}

.error-message {
  color: var(--color-error, #dc3545);
  text-align: center;
  margin-top: 1rem;
  padding: 0.5rem;
  background: rgba(220, 53, 69, 0.1);
  border-radius: 4px;
}

.login-link {
  text-align: center;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px dashed var(--border-color);
}

.login-link a {
  color: var(--primary-color);
  font-weight: 500;
}

.success-message {
  text-align: center;
  padding: 2rem 0;
}

.success-message i {
  font-size: 4rem;
  color: var(--secondary-color);
  margin-bottom: 1rem;
}

.success-message h3 {
  margin-bottom: 0.5rem;
}

.success-message p {
  color: var(--text-muted);
  margin-bottom: 1.5rem;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
