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
| Database | MySQL 8 /  |
| Containerization | Docker + Docker Compose |
| Build tool | Maven |

---

## Prerequisites

Make sure you have the following installed before running the project if you want to run it locally:

| Tool | Version |
|---|---|
| Docker  | 28.5.2 |
| Docker Compose | 2.40.3 |
| Git | Any |


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
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## Running locally

---

This option runs both the app and MySQL inside Docker. Useful to test the full containerized setup.

**Step 1 — Clone the repository**

```bash
git clone <https://github.com/vanessar810/FranchiseAPI.git>
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
* This information only pretend to facilitate development purpose not for production environments

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | Database host |
| `DB_PORT` | `3306` | Database port |
| `DB_NAME` | `franchisedb` | Database name |
| `DB_USER` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `SERVER_PORT` | `8080` | App port |

When running with Docker Compose, these are set automatically in `docker-compose.yml`.

---

## API endpoints

Base URL: `http://localhost:8080`
Swagger URL: `http://localhost:8080/webjars/swagger-ui/index.html`

---

## Usage example

```bash
# 1. Create a franchise
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Medellin}'

# 2. Add a branch office (franchiseId = 1)
curl -X POST http://localhost:8080/api/franchises/1/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Centro"}'

# 3. Add products to the branch (branchOfficeId = 1)
curl -X POST http://localhost:8080/api/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Savings account", "stock": 50}'

curl -X POST http://localhost:8080/api/branches/1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "credit cards", "stock": 120}'

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
    "productName": "credit cards",
    "stock": 120,
    "branchOfficeId": 1,
    "branchOfficeName": "Centro"
  }
]
```
---
