# UniMate Project Changelog

## Version 1.0.0 - Initial Release

### Database Migrations (Liquibase)

#### Author: demesup

**Migration 01: Create Faculties Table**
- Creates base faculties table for university departments
- Status: Completed

**Migration 02: Create Groups Table**
- Creates student groups table linked to faculties
- Status: Completed

**Migration 03: Create Users Table**
- Creates user authentication and profile table
- Supports ADMIN, CHIEF, and STUDENT roles
- Status: Completed

**Migration 04: Create Notes Table**
- Creates user notes/notes management table
- Status: Completed

**Migration 05: Create Professors Table**
- Creates professors directory with contact information
- Includes phone, email, office location, and office hours
- Status: Completed

**Migration 06: Create Faculty Links Table**
- Creates useful links for each faculty
- Status: Completed

**Migration 07: Create Tasks Table**
- Creates kanban task management system
- Includes timestamps and user associations
- Status: Completed

**Migration 08: Create Subtasks Table**
- Creates subtask tracking for main tasks
- Includes completion status and timestamps
- Status: Completed

**Migration 09: Create Quotes Table**
- Creates daily quotes/motivational content
- Status: Completed

**Migration 10: Create News Table**
- Creates news/announcements system
- Status: Completed

**Migration 11: Create Faculty Link Translations Table**
- Adds multi-language support for faculty links
- Status: Completed

**Migration 12: Create Faculty Translations Table**
- Adds multi-language support for faculties
- Status: Completed

**Migration 13: Create Folders Table**
- Creates file management folder structure
- Status: Completed

**Migration 14: Seed Faculties**
- Populates initial faculties data
- Includes: ELTH, ENERG, ACS, ETTI, FIMM, FIIR, ISB, TRANS, FILS, and others
- Status: Completed

**Migration 15: Seed Groups**
- Author: demesup
- Populates student groups for FILS and ACS faculties
- FILS Groups: 1211EA, 1211EB, 1221EA, 1231EA, 1241EA (Groups 1-5)
- ACS Groups: 1211AC, 1221AC, 1231AC, 1241AC (Groups 6-9)
- Status: Completed

**Migration 16: Seed Users**
- Author: demesup
- Populates initial user accounts with hashed passwords
- Admin User: admin@unimate.ro
- FILS Students: student, ana.ionescu, mihai.ghitescu, roxana.cosmescu
- FILS Chief: sef (Maria Popescu)
- ACS Students: cristian.stoica, elena.radu, rares.nicolae
- ACS Chief: adrian.vasilescu
- All passwords are properly hashed using bcrypt (rounds: 10)
- Status: Completed

**Migration 24: Create Schedules Table**
- Creates timetable/schedule management
- Status: Completed

**Migration 32: Add Professor Contact Info**
- Adds phone, email, office location, and office hours fields to professors
- Status: Completed

**Migration 33: Drop Reviews Table**
- Removes review/rating system (migration from legacy)
- Properly drops foreign key constraints and unique constraints
- Status: Completed

**Migration 34: Add Subtask Timestamps**
- Adds updated_at timestamps to subtasks
- Auto-updated via Hibernate @UpdateTimestamp
- Status: Completed

**Migration 35: Update Timetable Structure**
- Adds schedule_json, semester, and year fields to timetables
- Schema updated but API implementation pending
- Status: In Progress

**Migration 36: Create Notifications Table**
- Creates toast notification system for user alerts
- Includes notification types, status tracking, and soft-delete
- Status: Completed

### Backend Features

#### Task Management System
- Task CRUD operations with timestamps
- Subtask support with completion tracking
- Subtask updates now return DTO to prevent LazyInitializationException
- Toast notifications on all task operations
- Status: Completed

#### User Management
- Admin dashboard for user CRUD
- Faculty and group assignment with dependent dropdowns
- Password update support (optional for updates, required for creation)
- Password hashing with bcrypt (rounds: 10)
- Status: Completed

#### Professor Directory
- Professor CRUD operations
- Contact information: phone, email, office location, office hours
- Status: Completed

#### Notification System
- Toast notifications for user feedback
- Types: success, error, info, warning
- Auto-dismiss after 3 seconds
- Persistent notification audit trail in database
- Status: Completed

### Frontend Features

#### Admin Dashboard
- User management with Create, Read, Update, Delete
- Faculty and group selection with smart dropdowns (group depends on faculty)
- Password update for existing users
- User list display with search
- Status: Completed

#### Kanban View
- Task board with drag-and-drop
- Subtask creation and management
- Toast notifications for all operations
- Real-time UI updates
- Status: Completed

#### Professor Directory
- Professor listing with contact information modal
- Phone and email as clickable links (tel:, mailto:)
- Office location and hours display
- Status: Completed

### Security & Validation

#### Password Security
- All passwords stored as bcrypt hashes
- Bcrypt round count: 10 (strong security)
- Password validation on user creation
- Optional password update for existing users
- Status: Completed

#### Authorization
- Role-based access control (ADMIN, CHIEF, STUDENT, VISITOR)
- User resource ownership verification
- Admin-only operations protected
- Status: Completed

### Bug Fixes

#### Fixed Issues
1. **LazyInitializationException** - Subtask update now returns DTO instead of entity
2. **Password not updating** - Update endpoint now properly encodes passwords
3. **Hibernate circular references** - @ToString(exclude) annotations added
4. **Group dropdown state** - Groups now dependent on selected faculty
5. **Faculty/Group data loss** - Form now pre-populates and preserves values

### Testing
- Backend Maven compilation: Successful
- Frontend TypeScript type checking: Passing
- API endpoint testing: Verified with curl
- Database migrations: Applied successfully
- Status: Completed

---

## Seed Data Summary

### Users Created:
- **Admin**: admin / admin123 (ADMIN role)
- **FILS Students**: 
  - John Student / student123
  - Ana Ionescu / (hashed password)
  - Mihai Ghitescu / (hashed password)
  - Roxana Cosmescu / (hashed password)
- **FILS Chief**: Maria Popescu (sef) / sef123
- **ACS Students**:
  - Cristian Stoica / (hashed password)
  - Elena Radu / (hashed password)
  - Rares Nicolae / (hashed password)
- **ACS Chief**: Adrian Vasilescu / (hashed password)

### Groups Created:
- **FILS**: 5 groups across 4 years (1211EA, 1211EB, 1221EA, 1231EA, 1241EA)
- **ACS**: 4 groups across 4 years (1211AC, 1221AC, 1231AC, 1241AC)

### Faculties:
- 12 faculties including FILS (Facultatea de Ingineria Softului) and ACS (Facultatea de Automatică și Calculatoare)

---

**Project Author**: demesup  
**Last Updated**: 2026-04-23  
**Status**: Active Development
