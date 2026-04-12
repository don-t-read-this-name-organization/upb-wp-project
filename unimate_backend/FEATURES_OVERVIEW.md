# Unimate Backend Features Overview

This document describes the currently implemented backend features in `unimate_backend`.

## Core Platform

- Spring Boot 4 API with MySQL persistence.
- Liquibase-managed schema and seed data.
- Soft-delete pattern (`active` flag) across most domain entities.
- Global JSON error handling with consistent response format.

## Authentication and Users

- JWT-based auth:
  - `POST /api/auth/login`
  - `POST /api/auth/register`
  - `POST /api/auth/refresh`
  - `POST /api/auth/logout`
- User management:
  - Registration approval flow for pending users.
  - Admin approval/rejection endpoints.
  - Profile/user CRUD with role/ownership checks.
  - Password change endpoint.

## Academic and Content Modules

- Faculties:
  - Public read endpoints.
  - Admin write endpoints (create/update/delete).
  - Translation-aware read behavior.
- Groups:
  - Public read endpoints.
  - Admin write endpoints (create/update/delete).
  - Faculty-scoped group listing.
- Faculty Links:
  - Public read endpoints.
  - Admin write endpoints (create/update/delete).
  - Translation-aware response DTO.
- Professors:
  - Public read endpoints.
  - Admin write endpoints (create/update/delete).
- News:
  - Public read endpoints (`list`, `latest`, `by id`).
  - Admin write endpoints.
- Quotes:
  - Public read/random endpoints.
  - User quote submission as pending.
  - Admin approve/reject/update/delete flows.

## Productivity Modules

- Tasks and Subtasks:
  - User-owned task CRUD with ownership enforcement.
  - Subtask add/update/delete flows.
- Notes:
  - User-owned note CRUD with ownership enforcement.
- Files and Folders:
  - User-owned file/folder management with ownership checks.

## New Feature Modules

- Reviews:
  - `POST /api/reviews` for authenticated users.
  - Public professor review listing and rating statistics endpoints.
  - Ownership/admin-controlled delete behavior.
- Timetable:
  - Upload/download/delete own timetable.
  - PDF-only, max 10MB validation.
  - One active timetable per user.
- Schedule:
  - Create/read/update/delete weekly schedule entries.
  - Per-user schedule ownership enforcement.
  - Time-range validation and overlap prevention.

## Current Status Notes

- Phase 7 (environment/profile polish) was intentionally skipped.
- Security/account-hardening work from Phase 8 is implemented (see `SECURITY_IMPLEMENTATION.md`).
- Test suite currently includes basic application-context coverage and build verification.
