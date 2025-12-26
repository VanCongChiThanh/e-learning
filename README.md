
# Coursevo – E-Learning Platform  
**Maven Multi-Module · Docker · CI/CD to AWS EC2**

Coursevo is an **E-Learning platform** built as a **Maven Multi-Module project** following a **Modular Monolith architecture**.  
The system focuses on clean modular design, maintainability, and real-world deployment using **Docker** and **CI/CD pipelines**.

This repository is developed and maintained by the **Coursevo Group** as part of our **software engineering group project** for the semester.

📄 **Full project report and detailed documentation:**  
https://docs.google.com/document/d/16ysBVgDKLP7sDw38MfwE2NgUnGPX6-Ri/edit

---

## 🎯 Project Goals

- Apply **Object-Oriented Analysis & Design (OOAD)**
- Practice **Maven Multi-Module** architecture
- Build a **modular monolith** instead of microservices
- Use **Docker** for consistent deployment
- Implement **CI/CD** with GitHub Actions
- Deploy to **AWS EC2** with HTTPS

---

## 🧱 Architecture Overview

- **Architecture Style**: Modular Monolith  
- **Deployment**: Single application (single JAR)
- **Database**: Shared database
- **Communication**: Direct method calls between modules
- **Networking**: No inter-service network calls

> Each module represents a **business domain**, but all modules are built and deployed together.

---

### Module 

- **common-service**  
  Shared utilities, constants, DTOs, base exceptions, and configurations.

- **security-service**  
  Authentication, authorization, JWT, OAuth2 logic.

- **user-service**  
  User profile and user-related business logic.

- **course-service**  
  Course creation, update, and management.

- **enrollment-service**  
  Student enrollment and course participation.

- **commerce-service**  
  Payment, orders, and transaction handling.

- **notification-service**  
  In-app notification logic.

- **email-service**  
  Email sending (verification, notifications).

- **file-service**  
  File upload and storage handling.

- **web**  
  Application entry point, REST controllers, API exposure.

---

## 🔗 Module Dependency Rules

- `web` depends on feature modules
- Feature modules may depend on `common-service`
- `common-service` has **no dependencies**
- Circular dependencies are strictly avoided

Example:
web → course-service → common-service
web → user-service → common-service


## 🛠️ Technology Stack

- Java 17
- Maven (Multi-Module)
- Spring Boot
- Spring Security
- JPA / Hibernate
- RESTful API
- Docker
- GitHub Actions
- AWS EC2
- Caddy (Reverse Proxy + HTTPS)

---

## 🐳 Docker Support

The application is fully containerized using **Docker multi-stage builds**.

### Docker Build Strategy

**Stage 1 – Build**
- Image: `maven:3.9.2-eclipse-temurin-17`
- Builds all Maven modules
- Skips tests for faster image creation

**Stage 2 – Runtime**
- Image: `eclipse-temurin:17-jdk`
- Runs only the final `web` JAR

### Build Docker Image

```bash
docker build -t coursevo:latest .

docker run -d \
  --name coursevo \
  -p 8105:8105 \
  coursevo:latest
```
##🔄 CI/CD Pipeline
Pipeline Name
CI/CD Docker to EC2

The project uses GitHub Actions to automatically test, build, containerize, and deploy the application.

Trigger
Push to main branch

🧪 Continuous Integration (CI)
Job: Run Unit Tests
Steps:

Checkout source code

Set up JDK 17

Cache Maven dependencies

Run unit tests

Upload test reports (Surefire / Failsafe)

This ensures code quality before deployment.

🚀 Continuous Deployment (CD)
Job: Build & Deploy
Runs only if CI passes.

Steps:

Build Maven project (skip tests)

Build Docker image

Push image to Docker Hub

SSH into AWS EC2

Pull latest Docker image

Stop and remove old container

Run new container in Docker network web

Verify application availability via HTTPS

☁️ Deployment Environment
Cloud Provider: AWS EC2

Container Runtime: Docker

Reverse Proxy: Caddy (pre-configured)

Docker Network: web

HTTPS: Automatically handled by Caddy

The CI/CD pipeline does not modify Caddy configuration.

🔐 Environment Variables & Secrets
Sensitive information is managed via GitHub Secrets, including:

Database credentials

JWT secrets

OAuth (Google, Facebook)

Email credentials

AWS access keys

Payment gateway (PayOS)

Secrets are injected as environment variables at runtime.




