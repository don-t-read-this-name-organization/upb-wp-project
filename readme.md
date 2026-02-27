#  UniMate  
> **Your university survival kit**  
> *Because 'I'll do it later' is not a study strategy*

[![Status](https://img.shields.io/badge/status-in%20development-yellow)]()
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)]()
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)]()

---

##  Team Information

| Role | Name | Email | GitHub Profile |
|------|------|-------|----------------|
| **Member1** | `[Gladkykh Daria]` | `[daria.gladkyh@gmail.com]` | [@dariagladkykh](https://github.com/dariagladkykh) |
| **Member2** | `[Bachynskyi Roi]` | `[roi.bachynskyi@stud.fils.upb.ro]` | [@frihetselsker](https://github.com/frihetselsker) |
| **Member3** | `[Huzhan Sofiia]` | `[guzhansofia@gmail.com]` | [@demesup](https://github.com/demesup) |

- **Team Name:** `Team UniMate`  
- **Repository:** `[https://github.com/don-t-read-this-name-organization/upb-wp-project]`  
- **Course:** WP Internet Programming Technologies 

Our team operates without rigid role division. Tasks are assigned dynamically based on availability and learning goals, ensuring cross-functional skills and balanced contribution.

---

##  Project Description

**UniMate** is a Single-Page Application (SPA) designed to streamline student life. It provides a secure platform where students can manage academic resources, track tasks, view timetables, and navigate the campus. The system emphasizes security (Spring Security), usability (Responsive Bootstrap), and deployability (Docker).

Unlike generic platforms, UniMate focuses on **role-based customization** (e.g., Șef de Grupă privileges) and **orientation support** for exchange students.

###  Core Objectives
- Centralize student resources (notes, links, timetables).
- Empower Group Leaders (Șef de Grupă) to manage specific group schedules.
- Support Erasmus students with orientation tools and easy access to help.
- Ensure accessibility via responsive design and Docker deployment.

---

##  Key Features

-  **Secure Authentication:** Registration & Login with encrypted passwords (BCrypt).
-  **Course Notes:** Private text-based notes organized by course.
-  **Group Timetables:** Specific schedules uploaded by Șef de Grupă (not global PDFs).
-  **Professor Directory:** Contact info, office locations, and consultation hours.
-  **Task Board:** Kanban-style exam tracker (To Do → In Progress → Done).
-  **Campus Map:** Static map with key locations for easy orientation.
-  **Widgets:** Live weather forecast & random motivational quotes.
-  **Deployment:** Fully containerized with Docker & docker-compose.

---

## User Stories (Agile)

> These stories define the core scope agreed upon for the WP project.

### 1. Regular Student Management
> *"As a **regular student**, I want to **view my group timetable, access professor contact info, and manage my private notes/tasks**, so that I can **stay organized without searching through multiple channels**."*  
> **Acceptance:** Timetable is read-only for students, notes are private, professor info is searchable.

### 2. Șef de Grupă (Group Leader) Privileges
> *"As a **Șef de Grupă**, I want to **upload and update the timetable specifically for my group**, so that **my colleagues always have the correct schedule without unnecessary global clutter**."*  
> **Acceptance:** Role-based access control (RBAC), upload restricted to assigned group, version history optional.

### 3. Erasmus Student Orientation
> *"As an **Erasmus student**, I want to **easily orient myself via campus maps, and curated links**, so that I can **manage the new university system without needing to ask for help constantly**."*  


---

##  Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 3.x, Spring Security, Spring Data JPA |
| **Frontend** |React / NextJS |
| **Database** | **MariaDB** |
| **DevOps** | Docker, docker-compose, GitHub Actions |
| **APIs** | OpenWeatherMap (Weather), Internal (Quotes) |

---

##  Deployment & Setup

This project is designed for **one-command deployment** using Docker.

### Prerequisites
- Docker & Docker Compose installed
- Java 17+ (for local dev)
- Node.js 18+ (for local frontend dev)

###  Quick Start
> **Note:** This section will be **fully updated once we finish our project and we are ready for release**.  
> Currently, the repository contains the source code and development structure. Final deployment instructions, including environment variables and production-ready Docker scripts, will be added upon project completion.

---

##  Project Timeline (Sprints)

| Sprint | Focus | Deliverable |
|--------|-------|-------------|
| **Sprint 0** | Setup | Repo, README, Team Validation |
| **Sprint 1** | Auth & DB | Spring Security, Users, Roles (MariaDB) |
| **Sprint 2** | Core Features | Notes, Profile, Responsive UI |
| **Sprint 3** | Roles | Șef de Grupă timetable management |
| **Sprint 4** | Orientation | Erasmus section, Map, Links |
| **Sprint 5** | DevOps | Docker, Testing, Final Polish |

---

##  License

This project is licensed under the **GPL v3.0** for educational purposes.

---

