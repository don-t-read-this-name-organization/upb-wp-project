# Access Control Overview

This document describes the current role-based access behavior in the backend.

## Current Effective Security

- HTTP-level security currently permits all API routes:
  - `SecurityConfig` has `.requestMatchers("/api/**").permitAll()`.
  - File: `src/main/java/org/unimate/unimate/api/security/SecurityConfig.java`
- Because of that, requests are not blocked at URL level by role.

## Method-Level Role Rules Present in Controllers

The following controller methods define role rules with `@PreAuthorize`:

- `ProfessorController`
  - `permitAll()` for reads
  - `hasRole('ADMIN')` for create/update/delete
- `ReviewController`
  - `hasRole('USER')` for create
  - `permitAll()` for reads
  - `hasRole('ADMIN') or @authorizationService.ownsReview(#id)` for delete
- `ScheduleController`
  - `hasRole('USER')` for create/list
  - `hasRole('ADMIN') or @authorizationService.ownsSchedule(#id)` for update/delete
- `TimetableController`
  - `hasRole('USER')` for upload/get/delete

## Ownership Checks

- `AuthorizationService` implements ownership checks used in SpEL:
  - `ownsReview`, `ownsSchedule`, `ownsTask`, `ownsSubtask`, `ownsNote`, `ownsFile`, `ownsFolder`
- It also exposes:
  - `isCurrentUser`
  - `canAccessUser`
  - `isAdmin`

File: `src/main/java/org/unimate/unimate/api/security/AuthorizationService.java`

## Role Mapping

- Domain role enum (`RoleName`): `STUDENT`, `CHIEF`, `ADMIN`, `VISITOR`
- Spring authorities (`UserRole`): `ROLE_USER`, `ROLE_ADMIN`, `ROLE_PROFESSOR`
- `RoleAuthorityMapper` maps:
  - `ADMIN` -> `ROLE_ADMIN` (+ also `ROLE_USER`)
  - all other roles -> `ROLE_USER`

Files:
- `src/main/java/org/unimate/unimate/domain/enums/RoleName.java`
- `src/main/java/org/unimate/unimate/domain/enums/UserRole.java`
- `src/main/java/org/unimate/unimate/api/security/RoleAuthorityMapper.java`

## Important Note

Role/ownership annotations exist, but URL security currently permits `/api/**`.
If strict role enforcement is desired at runtime, tighten `SecurityConfig` matchers and ensure method security is enabled in configuration.
