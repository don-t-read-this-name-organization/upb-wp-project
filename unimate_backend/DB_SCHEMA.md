Database schema and Liquibase changelogs

This document explains the tables created by Liquibase, the purpose of each column, and the constraints applied. It also documents the soft-delete strategy using an `active` flag.

Tables

- roles
  - id: INT, PK, auto-increment
  - name: VARCHAR(50), NOT NULL
  - notes: Roles are static and do not use the `active` flag here.

 - users
  - id: INT, PK, auto-increment
  - username: VARCHAR(50), NOT NULL, UNIQUE (`uq_users_username`)
  - email: VARCHAR(255), NOT NULL, UNIQUE (`uq_users_email`)
  - password_hash: VARCHAR(255), NOT NULL
  - role_id: INT, FK -> `roles(id)` (`fk_users_role`), ON DELETE RESTRICT
  - created_at: TIMESTAMP (set by application)
  - active: BOOLEAN, NOT NULL, default TRUE (soft-delete flag)

Rationale / constraints:
- Users have unique username/email to prevent duplicates.
- `role_id` references `roles.id`; ON DELETE is RESTRICT to avoid accidental cascades since we soft-delete users.

 - professors
  - id: INT, PK, auto-increment
  - name: VARCHAR(255), NOT NULL
  - department: VARCHAR(255)
  - faculty: VARCHAR(255)
  - active: BOOLEAN, NOT NULL, default TRUE

Rationale:
- Professors can be soft-deleted by setting `active=false` so historical reviews remain.

 - notes
  - id: INT, PK, auto-increment
  - user_id: INT, NOT NULL, FK -> `users(id)` (`fk_notes_user`), ON DELETE RESTRICT
  - title: VARCHAR(255)
  - content: CLOB
  - created_at: TIMESTAMP (set by application)
  - active: BOOLEAN, NOT NULL, default TRUE

Rationale:
- Notes are user-owned; soft-delete prevents data loss.

 - tasks
  - id: INT, PK, auto-increment
  - user_id: INT, NOT NULL, FK -> `users(id)` (`fk_tasks_user`), ON DELETE RESTRICT
  - title: VARCHAR(255)
  - status: VARCHAR(50)
  - priority: INT
  - kanban_column: VARCHAR(100)
  - created_at: TIMESTAMP (set by application)
  - active: BOOLEAN, NOT NULL, default TRUE

Rationale:
- Use `kanban_column` instead of reserved word `column`.
- Soft-delete is useful for audit/history and potential restore.

 - faculty_links
  - id: INT, PK, auto-increment
  - title: VARCHAR(255)
  - url: VARCHAR(2000)
  - category: VARCHAR(255)
  - created_at: TIMESTAMP (set by application)
  - active: BOOLEAN, NOT NULL, default TRUE

Rationale:
- Links can be temporarily disabled without losing data.

 - quotes
  - id: INT, PK, auto-increment
  - text: CLOB
  - author: VARCHAR(255)
  - active: BOOLEAN, NOT NULL, default TRUE

Rationale:
- Quotes can be retired via `active=false`.


-Soft-delete strategy

- Every content table (users, professors, notes, reviews, tasks, links, quotes) includes an `active` BOOLEAN column with default TRUE and NOT NULL.
- Application layer should treat `active = false` as soft-deleted and exclude those rows from normal queries.
- For final/physical deletes (purge), we can add scheduled jobs or an admin endpoint to remove rows where `active = false` older than a threshold.

Suggestions / further improvements

- Add indexes on commonly filtered columns, e.g. `users(active)`, `professors(active)`, `reviews(professor_id, active)`.
- Consider audit tables (created_by, updated_at, updated_by) if tracking changes is required.
- If GDPR/PII rules require true deletion, implement a purge pipeline and/or anonymization steps.
- For consistency, you may want to add `created_by` where appropriate.

Files changed

- [src/main/resources/db/changelog/db.changelog-master.yaml](src/main/resources/db/changelog/db.changelog-master.yaml)
- Added per-table changelogs in [src/main/resources/db/changelog/master/](src/main/resources/db/changelog/master/)

Next steps

- I can add indexes and `created_by/updated_at` columns if you want. Also can add an example repository query that filters `active = true`.

