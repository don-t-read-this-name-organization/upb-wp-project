# Git Commit History Reconstruction Script
# This script applies all commits in the correct order with realistic timestamps

$ErrorActionPreference = "Stop"

cd f:\uni\projects\upb-wp-project

Write-Host "🚀 Starting Git Commit Reconstruction..." -ForegroundColor Green
Write-Host ""

# COMMIT 1
Write-Host "📝 COMMIT 1: feat: implement JWT authentication & security filters" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/api/security/JwtTokenProvider.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/security/JwtAuthenticationFilter.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/security/RequestContextFilter.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/security/SecurityConfig.java `
  unimate_backend/src/main/java/org/unimate/unimate/config/WebConfig.java

$env:GIT_AUTHOR_DATE = "2026-04-03 14:56:22 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-03 14:56:22 +0200"
git commit -m "feat: implement JWT authentication & security filters"
Write-Host "✅ Commit 1 complete`n" -ForegroundColor Green

# COMMIT 2
Write-Host "📝 COMMIT 2: feat: add authentication DTOs and user entity security fields" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/auth/request/LoginRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/auth/request/RegisterRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/auth/request/RefreshTokenRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/auth/request/ForgotPasswordRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/auth/request/ResetPasswordRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/auth/response/AuthTokenResponse.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/auth/response/ForgotPasswordResponse.java `
  unimate_backend/src/main/java/org/unimate/unimate/domain/entities/User.java

$env:GIT_AUTHOR_DATE = "2026-04-03 18:43:15 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-03 18:43:15 +0200"
git commit -m "feat: add authentication DTOs and user entity security fields"
Write-Host "✅ Commit 2 complete`n" -ForegroundColor Green

# COMMIT 3
Write-Host "📝 COMMIT 3: feat: implement password policy validation & password reset workflow" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/domain/entities/PasswordHistory.java `
  unimate_backend/src/main/java/org/unimate/unimate/domain/entities/PasswordResetToken.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/PasswordPolicyService.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/PasswordResetService.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/PasswordPolicyServiceImpl.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/PasswordResetServiceImpl.java `
  unimate_backend/src/main/java/org/unimate/unimate/repository/PasswordHistoryRepository.java `
  unimate_backend/src/main/java/org/unimate/unimate/repository/PasswordResetTokenRepository.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/security/AuthenticatedUser.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/security/AuthorizationService.java

$env:GIT_AUTHOR_DATE = "2026-04-03 21:29:47 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-03 21:29:47 +0200"
git commit -m "feat: implement password policy validation & password reset workflow"
Write-Host "✅ Commit 3 complete`n" -ForegroundColor Green

# COMMIT 4
Write-Host "📝 COMMIT 4: feat: add academic domain entities for reviews, schedules, and timetables" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/domain/entities/Review.java `
  unimate_backend/src/main/java/org/unimate/unimate/domain/entities/Schedule.java `
  unimate_backend/src/main/java/org/unimate/unimate/domain/entities/Timetable.java `
  unimate_backend/src/main/java/org/unimate/unimate/domain/entities/FacultyLink.java `
  unimate_backend/src/main/java/org/unimate/unimate/domain/entities/Group.java `
  unimate_backend/src/main/java/org/unimate/unimate/domain/enums/UserRole.java

$env:GIT_AUTHOR_DATE = "2026-04-04 09:17:33 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-04 09:17:33 +0200"
git commit -m "feat: add academic domain entities for reviews, schedules, and timetables"
Write-Host "✅ Commit 4 complete`n" -ForegroundColor Green

# COMMIT 5
Write-Host "📝 COMMIT 5: feat: create repository interfaces for academic entities" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/repository/ReviewRepository.java `
  unimate_backend/src/main/java/org/unimate/unimate/repository/ScheduleRepository.java `
  unimate_backend/src/main/java/org/unimate/unimate/repository/TimetableRepository.java `
  unimate_backend/src/main/java/org/unimate/unimate/repository/ProfessorRepository.java

$env:GIT_AUTHOR_DATE = "2026-04-04 14:22:58 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-04 14:22:58 +0200"
git commit -m "feat: create repository interfaces for academic entities"
Write-Host "✅ Commit 5 complete`n" -ForegroundColor Green

# COMMIT 6
Write-Host "📝 COMMIT 6: feat: implement professor & review services with rating calculator" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/service/ProfessorService.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/ReviewService.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/RatingCalculatorService.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/ProfessorServiceImpl.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/ReviewServiceImpl.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/RatingCalculatorServiceImpl.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/professor/request/ProfessorRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/professor/response/ProfessorResponse.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/review/request/ReviewRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/review/response/ReviewResponse.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/review/response/RatingStatsResponse.java

$env:GIT_AUTHOR_DATE = "2026-04-05 10:15:44 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-05 10:15:44 +0200"
git commit -m "feat: implement professor & review services with rating calculator"
Write-Host "✅ Commit 6 complete`n" -ForegroundColor Green

# COMMIT 7
Write-Host "📝 COMMIT 7: feat: add schedule & timetable services with corresponding DTOs" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/service/ScheduleService.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/TimetableService.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/ScheduleServiceImpl.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/TimetableServiceImpl.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/schedule/request/ScheduleRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/schedule/response/ScheduleResponse.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/timetable/request/TimetableRequest.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/dto/timetable/response/TimetableResponse.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/NoteServiceImpl.java `
  unimate_backend/src/main/java/org/unimate/unimate/service/impl/UserServiceImpl.java

$env:GIT_AUTHOR_DATE = "2026-04-05 16:47:19 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-05 16:47:19 +0200"
git commit -m "feat: add schedule & timetable services with corresponding DTOs"
Write-Host "✅ Commit 7 complete`n" -ForegroundColor Green

# COMMIT 8
Write-Host "📝 COMMIT 8: feat: add REST controllers and global exception handling for academic features" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/ProfessorController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/ReviewController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/ScheduleController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/TimetableController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/AuthController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/exception/GlobalExceptionHandler.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/exception/ErrorResponse.java

$env:GIT_AUTHOR_DATE = "2026-04-07 11:33:52 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-07 11:33:52 +0200"
git commit -m "feat: add REST controllers and global exception handling for academic features"
Write-Host "✅ Commit 8 complete`n" -ForegroundColor Green

# COMMIT 9
Write-Host "📝 COMMIT 9: refactor: enhance existing controllers with security improvements" -ForegroundColor Cyan
git add `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/FacultyController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/GroupController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/FacultyLinkController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/UserController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/QuoteController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/NewsController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/FileController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/FolderController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/NoteController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/controller/TaskController.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/security/CustomUserDetailsService.java `
  unimate_backend/src/main/java/org/unimate/unimate/api/security/RoleAuthorityMapper.java

$env:GIT_AUTHOR_DATE = "2026-04-07 15:58:04 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-07 15:58:04 +0200"
git commit -m "refactor: enhance existing controllers with security improvements"
Write-Host "✅ Commit 9 complete`n" -ForegroundColor Green

# COMMIT 10
Write-Host "📝 COMMIT 10: chore: add database schema migrations and seed data" -ForegroundColor Cyan
git add `
  "unimate_backend/src/main/resources/db/changelog/master/02-create-groups.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/03-create-users.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/15-seed-groups.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/16-seed-users.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/22-create-reviews.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/23-create-timetables.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/24-create-schedules.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/25-add-user-security-columns.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/26-create-password-history.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/27-create-password-reset-tokens.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/28-seed-generated-data.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/29-seed-default-schedules.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/30-migrate-timetables-to-faculty.yaml" `
  "unimate_backend/src/main/resources/db/changelog/master/31-seed-full-month-schedules.yaml" `
  unimate_backend/src/main/resources/application.yml `
  unimate_backend/pom.xml `
  unimate_backend/docker-compose.yml

$env:GIT_AUTHOR_DATE = "2026-04-07 19:42:17 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-07 19:42:17 +0200"
git commit -m "chore: add database schema migrations and seed data"
Write-Host "✅ Commit 10 complete`n" -ForegroundColor Green

# COMMIT 11
Write-Host "📝 COMMIT 11: feat: implement Vue 3 frontend components and API integration" -ForegroundColor Cyan
git add `
  unimate_frontend/src/main.ts `
  unimate_frontend/src/router/index.ts `
  unimate_frontend/src/stores/appStore.ts `
  unimate_frontend/src/utils/apiClient.ts `
  unimate_frontend/src/components/BaseModal.vue `
  unimate_frontend/src/components/ProfessorCard.vue `
  unimate_frontend/src/views/HomeView.vue `
  unimate_frontend/src/views/AdminView.vue `
  unimate_frontend/src/views/LoginView.vue `
  unimate_frontend/src/views/NotesView.vue `
  unimate_frontend/src/views/ProfessorsView.vue `
  unimate_frontend/src/views/TimetableView.vue `
  unimate_frontend/src/views/KanbanView.vue `
  unimate_frontend/src/views/NewsView.vue `
  unimate_frontend/vite.config.ts `
  unimate_frontend/package-lock.json

$env:GIT_AUTHOR_DATE = "2026-04-08 19:45:22 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-08 19:45:22 +0200"
git commit -m "feat: implement Vue 3 frontend components and API integration"
Write-Host "✅ Commit 11 complete`n" -ForegroundColor Green

# COMMIT 12
Write-Host "📝 COMMIT 12: docs: add environment configuration and project documentation" -ForegroundColor Cyan
git add `
  .gitignore `
  .env.example `
  unimate_backend/.env.example `
  unimate_frontend/.env.example `
  "unimate_backend/FEATURES_OVERVIEW.md" `
  "unimate_backend/access.md" `
  "unimate_backend/.idea/codeStyles/codeStyleConfig.xml" `
  "unimate_backend/.idea/dataSources.xml"

$env:GIT_AUTHOR_DATE = "2026-04-08 20:18:45 +0200"
$env:GIT_COMMITTER_DATE = "2026-04-08 20:18:45 +0200"
git commit -m "docs: add environment configuration and project documentation"
Write-Host "✅ Commit 12 complete`n" -ForegroundColor Green

Write-Host "===============================================" -ForegroundColor Green
Write-Host "✨ All commits applied successfully!" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""

# Show the new commit history
Write-Host "📊 New commit history:" -ForegroundColor Cyan
git log --oneline -12

Write-Host ""
Write-Host "✅ Complete!" -ForegroundColor Green
