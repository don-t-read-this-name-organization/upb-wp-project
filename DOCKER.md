# Docker Setup Guide for UniMate

This guide explains how to run the UniMate application using Docker Compose.

## Prerequisites

- Docker and Docker Compose installed
- `.env` file in the project root (copy from `.env.example`)

## Project Structure

```
unimate_backend/
├── Dockerfile
├── .dockerignore
├── pom.xml
├── mvnw
└── src/

unimate_frontend/
├── Dockerfile
├── .dockerignore
├── package.json
└── src/

docker-compose.yml  # Multi-service orchestration
.env.example        # Environment configuration template
```

## Services

The Docker setup includes 3 services:

1. **MySQL Database** (port 3306)
   - Database for the application
   - Health check enabled
   - Data persisted in `mysql_data` volume

2. **Backend** (port 8080)
   - Spring Boot REST API
   - Built with Maven from source
   - Health check enabled
   - Depends on MySQL

3. **Frontend** (port 3000)
   - Vue.js web application
   - Built with Vite
   - Served with `serve`
   - Depends on Backend

## Quick Start

### 1. Setup Environment Variables
```bash
cp .env.example .env
```

Edit `.env` and set your values if needed (defaults are provided).

### 2. Build and Start Services
```bash
docker-compose up --build
```

This will:
- Build both backend and frontend images
- Start all 3 services
- Create persistent volumes
- Wait for health checks to pass

**First run may take 5-10 minutes** (building images, downloading dependencies, MySQL initialization).

### 3. Access the Application
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- MySQL: localhost:3306 (username: unimate_admin, password: admin123)

## Common Commands

### Start services (no rebuild)
```bash
docker-compose up
```

### Start in background
```bash
docker-compose up -d
```

### View logs
```bash
docker-compose logs -f
# Or specific service:
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### Stop services
```bash
docker-compose down
```

### Remove volumes (deletes database data)
```bash
docker-compose down -v
```

### Rebuild without cache
```bash
docker-compose up --build --no-cache
```

## Development Workflow

### During Development (Local)
```bash
# Keep using local development
cd unimate_backend
./mvnw spring-boot:run

# In another terminal
cd unimate_frontend
npm run dev
```

### Using Docker for Database Only
```bash
# Start only MySQL
docker-compose up mysql -d

# Run backend/frontend locally against the containerized database
```

## Troubleshooting

### Containers stuck on startup?
```bash
# View logs
docker-compose logs

# Restart services
docker-compose restart

# Full reset
docker-compose down -v
docker-compose up --build
```

### Backend can't connect to MySQL?
```bash
# Check MySQL service
docker-compose logs mysql

# Verify network communication
docker-compose exec backend ping mysql
```

### Frontend can't reach backend?
```bash
# Check backend is running
curl http://localhost:8080/actuator/health

# Check CORS headers in backend logs
docker-compose logs backend
```

### Port already in use?
Edit `docker-compose.yml` and change the port mappings:
```yaml
ports:
  - "3001:3000"  # Change 3000 to different port
```

## Environment Variables

Key variables (see `.env.example` for complete list):

| Variable | Default | Description |
|----------|---------|-------------|
| `MYSQL_ROOT_PASSWORD` | root | MySQL root password |
| `MYSQL_DATABASE` | unimate | Database name |
| `SPRING_PROFILES_ACTIVE` | dev | Spring profile (dev/prod/test) |
| `CORS_ALLOWED_ORIGINS` | http://localhost:3000,http://localhost:5173 | CORS allowed origins |
| `VITE_API_BASE_URL` | http://localhost:8080 | Frontend API URL |

## Performance Tips

1. **First run optimization**: Pre-pull images
   ```bash
   docker pull mysql:8.3
   docker pull eclipse-temurin:21-jre-alpine
   docker pull node:20-alpine
   ```

2. **Speed up builds**: Use BuildKit
   ```bash
   DOCKER_BUILDKIT=1 docker-compose build
   ```

3. **Reduce image size**: Dockerfiles use multi-stage builds for smaller final images

4. **Skip tests during build**: Disable in pom.xml or use Maven skip flag

## Production Deployment

For production, consider:

1. Use `.env.prod` with proper secrets
2. Set `SPRING_PROFILES_ACTIVE=prod`
3. Use a proper secrets manager (AWS Secrets Manager, Kubernetes Secrets, etc.)
4. Enable HTTPS/SSL
5. Use a reverse proxy (Nginx)
6. Setup automated backups for MySQL volume
7. Configure monitoring and logging

## Network Communication

All services run on the `unimate-network` bridge network:
- Backend can reach: `mysql:3306`
- Frontend can reach backend at: `http://backend:8080`
- Frontend is accessed at: `http://localhost:3000`

## Additional Resources

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Vue.js in Docker](https://vuejs.org/guide/scaling-up/deployment.html)
