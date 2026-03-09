<div align="center">
  <h1>Dispatch</h1>
  <p><strong>A Pen-Pal & Letter-Writing Platform</strong></p>

  <!-- Badges -->
  <p>
    <img alt="Java Version" src="https://img.shields.io/badge/Java-21-orange">
    <img alt="Spring Boot Version" src="https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen">
    <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16-blue">
    <img alt="Docker" src="https://img.shields.io/badge/Docker-Enabled-2496ED">
    <img alt="License" src="https://img.shields.io/badge/License-MIT-yellow">
  </p>
</div>

<br/>

## Description

**Dispatch** is a project built to bring back the feeling of having a pen-pal. It is a communication platform focused on writing the equivalent of letters, encouraging longer and more thoughtful messages instead of quick chats. I built the backend with Spring Boot 4 to keep it secure, fast, and scalable. It handles user registration, exchanging letters, and managing profiles.

### The Problem it Solves
People spend a lot of time sending short texts and have generally lost the habit of writing full letters. Dispatch provides a quiet space where users can take their time to write to others. The backend takes care of security, protecting user data and verifying file uploads so you can focus entirely on connecting with your pen-pals.

---

## Tech Stack

- **Backend Framework:** Java 21, Spring Boot 4.x
- **Database / ORM:** PostgreSQL, Spring Data JPA, Hibernate
- **Security:** Spring Security, JWT (JSON Web Tokens)
- **API Documentation:** OpenAPI / Swagger (Springdoc)
- **Object Mapping:** MapStruct
- **Performance:** Spring Boot Cache
- **Infrastructure:** Docker, Docker Compose
- **Build Tool:** Maven

---

## Features

- **User Authentication & Authorization:** Secure registration and login flows protected by JWT. Built-in token revocation and user banning implementations.
- **Letter Exchange System:** Core functionalities for users to write, send, and receive digital letters.
- **Profile Management:** Users can customize their profiles, including uploading profile pictures (with built-in file sanitization).
- **Interactive API Documentation:** Ready-to-use Swagger UI for easy endpoint testing and exploration.
- **Dockerized Setup:** Fully containerized for rapid local deployment of both the application and the PostgreSQL database.
- **Secure File Uploads:** Strict file size limits and security checks against path traversal attacks.

---

## Installation

### Prerequisites
- [Java 21](https://jdk.java.net/21/)
- [Maven](https://maven.apache.org/) (or use the provided Maven wrapper `mvnw`)
- [Docker](https://www.docker.com/) and Docker Compose (if running via containers)
- [PostgreSQL](https://www.postgresql.org/) (if running bare-metal)

### Option 1: Running with Docker (Recommended)
This approach automatically sets up the Dispatch application along with its PostgreSQL database.

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/dispatch.git
   cd dispatch
   ```

2. **Start the containers:**
   ```bash
   docker-compose up -d
   ```
   *The application will be accessible at `http://localhost:8080`.*

### Option 2: Local Development (Bare-metal)

1. **Configure the Database:**
   Ensure PostgreSQL is running locally on port `5432`. Create a database named `dispatch` or configure the connection using environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).

2. **Set required Environment Variables:**
   ```bash
   export DB_PASSWORD=your_secure_password
   export JWT_SECRET=your_base64_encoded_jwt_secret_key_here
   ```

3. **Run the Application using Maven Wrapper:**
   ```bash
   ./mvnw spring-boot:run
   ```

---

## Usage

Once the application is running, you can explore the API using the built-in Swagger UI:

- **Swagger Documentation:** Navigate to `http://localhost:8080/swagger-ui.html` in your browser.
- **Authentication:** Use the `/auth/register` and `/auth/login` endpoints to create a user and receive a JWT. Include this token in the `Authorization: Bearer <token>` header for protected endpoints.

---

## Project Structure

```text
Dispatch/
├── .github/                 # GitHub Actions & workflows
├── docs/                    # Additional application documentation
├── security-audit/          # Security reports and fixes logs
├── src/
│   ├── main/java/com/clark/roper/Dispatch/ # Core application source code
│   │   ├── controller/      # REST Controllers
│   │   ├── service/         # Business Logic
│   │   ├── repository/      # Spring Data JPA Repositories
│   │   └── ...              # Models, DTOs, Security filters, etc.
│   └── main/resources/      # Configuration (application.properties)
├── Dockerfile               # Production & local Docker image definition
├── docker-compose.yml       # Stack configuration (App + DB)
└── pom.xml                  # Maven dependencies configuration
```

---

## Core API Endpoints

The API is fully documented and interactive via Swagger UI (accessible at `http://localhost:8080/swagger-ui.html` when running locally). Here's a quick overview of the main endpoints and what they do:

### Authentication Area (`/auth`)
- **`POST /auth/register`**: Used to create a brand new user account. It hashes your password securely and saves your details to the database.
- **`POST /auth/login`**: Takes your credentials, verifies them against the database, and hands back a JWT (JSON Web Token). You'll need to include this token in the `Authorization: Bearer <token>` header for almost every other request.

### Letters Area (`/api/v1/specific-letter`)
- **`POST /api/v1/specific-letter/send`**: This is where you actually send a specific letter to a user. It requires the recipient's information and your letter's content.
- **`GET /api/v1/specific-letter/view/received`**: Fetches all the letters you've received, neatly formatted and ready to read.

### General Letters Area (`/api/v1/general-letter`)
- **`POST /api/v1/general-letter/create`**: Create a public general open letter.
- **`GET /api/v1/general-letter/list`**: Fetches community-wide open letters.

### Profile Area (`/user/me`)
- **`POST /user/me/profile-picture`**: Allows you to upload a profile picture. The backend handles making sure the file isn't too large (limited to 5MB by default) and ensures it's actually a safe image file, preventing malicious software from being uploaded.
- **`GET /user/me/profile`**: Allows you to view your own profile details.

---


