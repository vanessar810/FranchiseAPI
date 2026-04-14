# Franchise API

REST API built with **Spring Boot 3**, **WebFlux** (functional style), **JPA**, and **MySQL** to manage franchises, branch offices, and their products.

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Web layer | Spring WebFlux (functional router) |
| Database access | Spring Data JPA |
| Database | MySQL 8 / Aurora MySQL (AWS) |
| Containerization | Docker + Docker Compose |
| Infrastructure | Terraform |
| Build tool | Maven |

---

## Prerequisites

Make sure you have the following installed before running the project:

| Tool | Version | Download |

| Java JDK | 21 LTS | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org |
| Docker Desktop | Latest | https://www.docker.com/products/docker-desktop |
| Git | Any | https://git-scm.com |

> MySQL does **not** need to be installed locally — Docker handles it.

---

## Project structure

```
franchise-api/
├── src/
│   └── main/
│       ├── java/com/franchise/
│       │   ├── FranchiseApiApplication.java
│       │   ├── config/
│       │   │   ├── RouterConfig.java
│       │   │   └── GlobalErrorHandler.java
│       │   ├── handler/
│       │   │   ├── FranchiseHandler.java
│       │   │   ├── BranchOfficeHandler.java
│       │   │   └── ProductHandler.java
│       │   ├── service/
│       │   │   ├── FranchiseService.java
│       │   │   ├── BranchOfficeService.java
│       │   │   └── ProductService.java
│       │   ├── repository/
│       │   │   ├── FranchiseRepository.java
│       │   │   ├── BranchOfficeRepository.java
│       │   │   └── ProductRepository.java
│       │   ├── model/
│       │   │   ├── Franchise.java
│       │   │   ├── BranchOffice.java
│       │   │   └── Product.java
│       │   └── dto/
│       │       ├── request/
│       │       └── response/
│       └── resources/
│           └── application.yml
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   └── outputs.tf
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## Running locally

There are two ways to run the application locally. Option A is recommended during development.

---

### Option A — IntelliJ + Docker (recommended for development)

This option runs only the MySQL database in Docker and the Spring Boot app directly from IntelliJ. It gives you faster restarts and easier debugging.

**Step 1 — Clone the repository**

```bash
git clone <https://github.com/vanessar810/FranchiseAPI.git>
cd franchise-api
```

**Step 2 — Start the MySQL container**

```bash
docker-compose up mysql
```

This starts a MySQL 8 container on port `3307` of your machine (to avoid conflicts with any local MySQL on `3306`). The database `franchisedb` is created automatically.

**Step 3 — Run the app from IntelliJ**

Open the project in IntelliJ IDEA, wait for Maven to download dependencies, then run `FranchiseApiApplication.java`.

The app connects to:
```
jdbc:mysql://localhost:3307/franchisedb
```

On startup, Hibernate auto-creates the three tables (`franchise`, `branch_office`, `product`).

**Step 4 — Verify**

Open your browser or Postman and hit:
```
GET http://localhost:8080/api/franchises/1/top-stock
```

You should get a `404` with `{"error": "Franchise not found: 1"}` — this confirms the app is running and connected to the database correctly.

---

### Option B — Full Docker Compose (everything in containers)

This option runs both the app and MySQL inside Docker. Useful to test the full containerized setup.

**Step 1 — Clone the repository**

```bash
git clone <your-repository-url>
cd franchise-api
```

**Step 2 — Build and start all containers**

```bash
docker-compose up --build
```

Docker will:
1. Build the Spring Boot app into a `.jar` using a Maven container
2. Start a MySQL 8 container and wait until it's healthy
3. Start the app container and connect it to MySQL

**Step 3 — Verify**

```bash
curl http://localhost:8080/api/franchises/1/top-stock
```

**Step 4 — Stop everything**

```bash
# Stop containers but keep database data
docker-compose down

# Stop containers and delete database data (fresh start)
docker-compose down -v
```

---

## Environment variables

The app reads configuration from environment variables with fallback defaults for local development:

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | Database host |
| `DB_PORT` | `3306` | Database port |
| `DB_NAME` | `franchisedb` | Database name |
| `DB_USER` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `SERVER_PORT` | `8080` | App port |

When running with Docker Compose, these are set automatically in `docker-compose.yml`. When running from IntelliJ, the defaults are used.

---

## API endpoints

Base URL: `http://localhost:8080`

### Franchises

| Method | Endpoint | Description | Body |

| `POST` | `/api/franchises` | Create a franchise | `{"name": "string"}` |
| `PATCH` | `/api/franchises/{id}/name` | Update franchise name | `{"name": "string"}` |

### Branch offices

| Method | Endpoint | Description | Body |

| `POST` | `/api/franchises/{franchiseId}/branches` | Add branch to franchise | `{"name": "string"}` |
| `PATCH` | `/api/branches/{id}/name` | Update branch name | `{"name": "string"}` |

### Products

| Method | Endpoint | Description | Body |

| `POST` | `/api/branches/{branchOfficeId}/products` | Add product to branch | `{"name": "string", "stock": 0}` |
| `DELETE` | `/api/products/{productId}` | Delete a product | — |
| `PATCH` | `/api/products/{productId}/stock` | Update product stock | `{"stock": 0}` |
| `PATCH` | `/api/products/{productId}/name` | Update product name | `{"name": "string"}` |

### Reports

| Method | Endpoint | Description |

| `GET` | `/api/franchises/{franchiseId}/top-stock` | Product with most stock per branch for a franchise |

---

## Usage example

```bash
# 1. Create a franchise
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Burger House"}'

# 2. Add a branch office (franchiseId = 1)
curl -X POST http://localhost:8080/api/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Downtown Branch"}'

# 3. Add products to the branch (branchOfficeId = 1)
curl -X POST http://localhost:8080/api/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Burger", "stock": 50}'

curl -X POST http://localhost:8080/api/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Fries", "stock": 120}'

# 4. Update stock
curl -X PATCH http://localhost:8080/api/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 75}'

# 5. Get product with most stock per branch for franchise 1
curl http://localhost:8080/api/franchises/1/top-stock
```

Expected response for step 5:
```json
[
  {
    "productId": 2,
    "productName": "Fries",
    "stock": 120,
    "branchOfficeId": 1,
    "branchOfficeName": "Downtown Branch"
  }
]
```

---

## Troubleshooting

**Port 3306 already in use**

Your local MySQL is running on port 3306. The `docker-compose.yml` maps MySQL to port `3307` on your machine to avoid this conflict. If you still see the error, stop your local MySQL service:

- Windows: open Services → find MySQL → Stop
- Or change `3307:3306` to any free port like `3308:3306` in `docker-compose.yml`

**Docker Desktop not running**

The error `open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified` means Docker Desktop is not started. Open Docker Desktop from the Start menu and wait for the whale icon in the taskbar to stop animating.

**WSL 2 error on Windows**

Run this in PowerShell as administrator and restart Docker Desktop:
```powershell
wsl --update
```

**App starts but cannot connect to database**

Make sure the MySQL container is healthy before starting the app:
```bash
docker ps
```
The `franchise-mysql` container should show `healthy` in the status column. If it shows `starting`, wait a few more seconds.

---

## Infrastructure (AWS)

Terraform scripts to provision Aurora MySQL on AWS are located in the `terraform/` folder. See the infrastructure section for deployment instructions once you have an AWS account configured.
