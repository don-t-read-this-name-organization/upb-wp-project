# Unimate Backend

## Overview

This project is built with:

- Spring Boot 4
- Java 25
- MySQL 8 (Dockerized)
- Liquibase for database migrations
- Maven Wrapper

The application runs on:

http://localhost:8888


## Requirements

Before starting, make sure you have:

- Java 25 installed
- Docker running
- Git

## 1. Start the Database (Docker)

The project uses Docker to run MySQL locally.

### Start MySQL

From the `unimate_backend` directory (or use `-f` from the repo root):

docker compose up -d

Or from the repo root:

docker compose -f unimate_backend/docker-compose.yml up -d

This will:

- Start a MySQL 8 container
- Create database: unimate
- Create user: unimate_admin
- Grant privileges on the unimate database

Verify that the container is running:

docker ps

You should see a container named:

unimate-mysql


### Wait Until MySQL Is Ready

Check logs:

docker logs unimate-mysql

Wait until you see:

ready for connections

Do not start the application before this appears.

Note: `docker compose up` (without `-d`) runs in the foreground and will keep streaming logs; it won't "finish". Use `-d` if you want the command to return immediately.

First run can take a few minutes because Docker may need to download the `mysql:8.3` image and initialize the data volume; later runs are typically much faster.

If you already created the container once, you can often use these faster commands:

- Start existing container: `docker compose start`
- Stop without removing: `docker compose stop`
- Recreate only when needed: avoid `docker compose down -v` unless you want a full reset


## 2. Application Configuration

Database configuration is defined in:

src/main/resources/application.yml

Datasource configuration:

spring:
datasource:
url: jdbc:mysql://127.0.0.1:3306/unimate?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
username: unimate_admin
password: admin123

Important:

We use 127.0.0.1 instead of localhost to avoid IPv6 resolution issues on Windows.


## 3. Run the Application

On Windows:

mvnw.cmd spring-boot:run

On macOS/Linux:

./mvnw spring-boot:run

Alternatively, run UnimateApplication directly from your IDE.

The application will start on:

http://localhost:8888


## 4. Database Migrations (Liquibase)

Liquibase is used to manage the database schema.

Main changelog file:

src/main/resources/db/changelog/db.changelog-master.yaml

Liquibase runs automatically on application startup.

Hibernate is configured with:

ddl-auto: validate

This means:

- Liquibase manages schema changes
- Hibernate validates the schema against entities
- Hibernate does not create or update tables automatically


## 5. Reset the Database

If you encounter database or connection issues, reset everything:

docker compose down -v
docker volume prune -f
docker compose up -d

Then wait again until:

ready for connections

After that, restart the Spring Boot application.


## 6. Manual Database Access

To access MySQL inside the container:

docker exec -it unimate-mysql mysql -u unimate_admin -p

Password:

admin123


## 7. Stop the Database

To stop the database container:

docker compose down


## Project Structure

src/main/java        Application source code  
src/main/resources   Configuration and Liquibase files  
docker-compose.yml   MySQL container configuration  
pom.xml              Maven configuration


## Quick Start

From the repo root:

docker compose -f unimate_backend/docker-compose.yml up -d
mvnw.cmd spring-boot:run

Open:
http://localhost:8888
