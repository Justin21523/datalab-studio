# DataLab Studio

DataLab Studio is a personal data analysis, visualization, and reporting platform. It provides a full-stack solution for importing, profiling, and previewing datasets with a modern Spring Boot backend and a JavaFX desktop client.

## 🚀 Features

- **Multi-Module Architecture**: Built with a clean separation of concerns using a shared core package, a robust REST API, and a desktop client.
- **Dataset Management**: Import CSV files with automatic column-type inference.
- **Data Profiling**: Comprehensive data analysis including:
  - Numeric statistics (Min, Max, Mean, Median, StdDev).
  - Category frequency analysis.
  - Duplicate row detection.
  - Missing value identification.
- **Desktop Preview**: Interactive preview of large datasets with optimized loading.
- **Robust Tech Stack**: Modern Java 21, Spring Boot 3, JavaFX, PostgreSQL, and Flyway migrations.

## 🏗 Project Structure

- `apps/api`: Spring Boot backend handling data storage, metadata management, and profiling logic.
- `apps/desktop`: JavaFX-based desktop application providing an intuitive UI for data analysis.
- `packages/shared`: Common Data Transfer Objects (DTOs) and enums shared between the API and Desktop app.
- `docs/`: Technical documentation including API design, architecture, and database schema.

## 🛠 Prerequisites

- **Java**: JDK 21 or higher.
- **Database**: PostgreSQL (for metadata).
- **Maven**: To build the project.
- **Docker** (Optional): For running integration tests with Testcontainers.

## 🚦 Getting Started

### 1. Configure Environment
Copy the `.env.example` (if available) or set the following environment variables:
- `DATALAB_DB_URL`: JDBC URL for PostgreSQL.
- `DATALAB_DB_USERNAME`: Database username.
- `DATALAB_DB_PASSWORD`: Database password.
- `DATALAB_DATA_DIR`: Directory to store raw dataset files.

### 2. Build the Project
```bash
./mvnw clean install
```

### 3. Run the API
```bash
cd apps/api
../../mvnw spring-boot:run
```

### 4. Run the Desktop App
```bash
cd apps/desktop
../../mvnw javafx:run
```

## 🧪 Testing
The project includes unit and integration tests. Integration tests use Testcontainers to spin up a PostgreSQL instance.
```bash
./mvnw test
```

## 📜 License
This project is for personal use and internal data analysis.
