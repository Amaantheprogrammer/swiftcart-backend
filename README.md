<div align="center">

# 🛒 Swift Cart: E-Commerce & Marketplace Backend

[![Java 21](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-Caching_&_Auth-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Multi_Stage-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Render-Live_Deployment-46E3B7?style=for-the-badge&logo=render&logoColor=black)](https://e-commercebackendproject.onrender.com)

A high-performance, production-deployed **Spring Boot 3.5** backend powering a multi-tier e-commerce marketplace. Featuring stateless **JWT security with Refresh Token Rotation**, **Redis caching policies**, atomic order workflows with stock management, and external service communication via **Spring `RestClient`**.

[🚀 Live Demo App](https://e-commercebackendproject.onrender.com) • [📖 Interactive Swagger API Docs](https://e-commercebackendproject.onrender.com/swagger-ui/index.html) • [🐙 GitHub Repository](https://github.com/Amaantheprogrammer/swiftcart-backend)

</div>

---

## ⚡ Executive Summary

**Swift Cart** exposes **44 robust REST endpoints** handling the complete transactional lifecycle of a modern online marketplace. Built with strict architectural boundaries, zero entity leakage, and enterprise-grade security, it seamlessly orchestrates buyers, sellers, and platform administrators.

- **⚡ High Performance:** Integrated Redis multi-region caching layer slashing query latency.
- **🔒 Production Security:** Dual-token JWT authentication with Redis-backed Refresh Token Rotation.
- **💳 Banking Integration:** Outgoing inter-service communication via Spring `RestClient` with JWT header propagation.
- **📦 Scalable Architecture:** Multi-stage Docker deployment optimized for cloud execution.

---

## 🏗️ System Architecture

```
                       ┌──────────────────────────────────────────┐
                       │           HTTP Client / Frontend         │
                       └────────────────────┬─────────────────────┘
                                            │
                                            ▼
                       ┌──────────────────────────────────────────┐
                       │        Spring Security Gateway           │
                       │   (JwtAuthFilter & Method Security)     │
                       └────────────────────┬─────────────────────┘
                                            │
               ┌────────────────────────────┼────────────────────────────┐
               ▼                            ▼                            ▼
  ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
  │  Auth & Token Service   │  │   Marketplace Services  │  │  External Banking Client│
  │ (Sign-In, Refresh, RBAC)│  │ (Catalog, Cart, Orders) │  │  (Spring RestClient)    │
  └────────────┬────────────┘  └────────────┬────────────┘  └────────────┬────────────┘
               │                            │                            │
               ▼                            ▼                            ▼
  ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
  │      Redis Store        │  │     MySQL Database      │  │ Digital Banking API Core│
  │ (Tokens + Cache TTLs)   │  │ (JPA / Hibernate ORM)   │  │    (External Service)   │
  └─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
```

---

## 🛠️ Technology Stack & Key Libraries

| Domain | Technology | Purpose / Details |
| :--- | :--- | :--- |
| **Language** | **Java 21** | Utilizing LTS performance, virtual thread compatibility, and pattern matching. |
| **Framework** | **Spring Boot 3.5** | Core framework driving the web layer, dependency injection, and bean management. |
| **Security** | **Spring Security & JJWT** | Stateless authentication, BCrypt hashing, and role-based access control. |
| **Persistence** | **Spring Data JPA & Hibernate** | MySQL ORM mapping, dynamic queries, and optimized fetch strategies. |
| **Caching** | **Redis (Spring Cache)** | High-speed cache for queries and HttpOnly refresh token validation. |
| **Integration** | **Spring `RestClient`** | Microservice-style communication forwarding auth contexts to Digital Banking APIs. |
| **DevOps** | **Docker & Maven** | Two-stage lightweight builds running on `eclipse-temurin:21-jre-alpine`. |
| **Documentation** | **Springdoc OpenAPI 2.x** | Automated, interactive Swagger UI generation. |

---

## 🔥 Key Engineering Highlights

### 1. 🛡️ Authentication & Refresh Token Rotation (RTR)
- **Short-Lived Access Tokens:** Delivered via `Authorization: Bearer <token>` (15-min TTL).
- **Refresh Token Rotation:** Stored in Redis with a 7-day TTL and delivered via secure `HttpOnly` cookies.
- Every invocation of `/auth/refresh` revokes the incoming token in Redis and issues a fresh key-value pair, neutralizing replay attacks.

```http
POST /auth/sign-up     -> Register new account
POST /auth/sign-in     -> Authenticate & receive access token + HttpOnly cookie
POST /auth/refresh     -> Rotate refresh token & issue new access token
POST /auth/sign-out    -> Revoke token in Redis & clear client cookie
```

### 2. 🔐 Fine-Grained Role-Based Access Control (RBAC)
Role restrictions are declaratively enforced using `@PreAuthorize("hasRole('ROLE_...')")` at the controller layer:

```
  ┌──────────────┐     ┌─────────────────────────────────────────────────────────┐
  │  ROLE_ADMIN  │ ──► │ Platform-wide management: All users, orders, categories.│
  └──────────────┘     └─────────────────────────────────────────────────────────┘
  ┌──────────────┐     ┌─────────────────────────────────────────────────────────┐
  │ ROLE_SELLER  │ ──► │ Own product lifecycle: Create, edit, upload product image│
  └──────────────┘     └─────────────────────────────────────────────────────────┘
  ┌──────────────┐     ┌─────────────────────────────────────────────────────────┐
  │  ROLE_USER   │ ──► │ Customer workflows: Shopping cart, wishlist, checkout.  │
  └──────────────┘     └─────────────────────────────────────────────────────────┘
```

### 3. 🚀 Multi-Region Redis Caching Strategy
Custom cache regions configured in `RedisConfig` with declarative write-through eviction (`@CacheEvict`) on mutations to ensure strict data consistency:

| Cache Region / Namespace | TTL Policy | Eviction Triggers |
| :--- | :--- | :--- |
| `categories` | **12 Hours** | On category create/update/delete |
| `products`, `productsById`, `productsByName...` | **30 Minutes** | On product update, stock alteration, image upload |
| `users`, `usersById`, `usersByEmail` | **10 Minutes** | On profile update or deletion |

---

## 🚦 Core API Endpoints Overview

<details>
<summary><b>📋 Click to expand 44 REST Endpoints Details</b></summary>

### 🛒 Product & Catalog Management
```http
GET    /products                             -> Paginated product listing
GET    /products/{id}                        -> Fetch product details by ID (Cached)
GET    /products/me                          -> Fetch products listed by current seller
GET    /products/search?name={}&price={}     -> Search by name with optional max price
GET    /products/category/{id}?price={}      -> Category browse with price filters
POST   /products                             -> Create product (ROLE_SELLER)
PATCH  /products/{id}                        -> Update product details (Product owner only)
POST   /products/{id}/image                  -> Upload product image (ROLE_SELLER)
PUT    /products/{id}/image/{imageId}        -> Replace product image
DELETE /products/{id}/image/{imageId}        -> Remove product image
DELETE /products/{id}                        -> Delete product (Product owner only)
```

### 🛍️ Shopping Cart & Checkout Workflows
```http
GET    /carts/my-cart                        -> Retrieve active user cart & dynamic totals
POST   /carts/items                          -> Add item or adjust quantity (+/- delta)
DELETE /carts/items/{productId}              -> Remove item from cart
DELETE /carts/clear                          -> Flush active cart
POST   /orders/place-order                   -> Instant single-product purchase
POST   /orders/cart/place-order              -> Full cart checkout (Grouped by seller)
```

### 📦 Order & Fulfillment Operations
```http
GET    /orders                               -> List all system orders (ROLE_ADMIN)
GET    /orders/me                            -> Fetch customer's personal order history
PATCH  /orders/{orderId}/cancel              -> Cancel order, restore stock & issue refund
PATCH  /orders/{orderId}/order-status        -> Update fulfillment status (ROLE_SELLER)
PATCH  /orders/{orderId}/payment-status      -> Update payment status (ROLE_ADMIN)
```

### 📁 Category & Account Administration
```http
GET    /categories                           -> List marketplace categories (Cached)
POST   /categories                           -> Create category (ROLE_ADMIN)
DELETE /categories/{id}                      -> Delete category (Blocks if items present)
GET    /users/me                             -> Get authenticated profile details
PATCH  /users                                -> Update authenticated profile
```
</details>

---

## 🏛️ Domain Package Structure

```
src/main/java/com/myProject/
├── ⚙️ config/        # Spring beans — Security, Redis, Cors, RestClient, Mapper
├── 🎮 controller/    # REST HTTP endpoints & request validation
├── 📦 dto/           # Strongly-typed request payloads & response schemas
├── 🗄️ entity/        # JPA Database Entities & Relationships
├── 🏷️ enums/         # Role, OrderStatus, PaymentStatus, PaymentMethod
├── ⚠️ exception/     # Global @RestControllerAdvice & custom exception hierarchy
├── 🛡️ filter/        # JwtAuthFilter & Security Interceptors
├── 💾 repository/    # Spring Data JPA interfaces with custom JPQL queries
├── 🧱 seeder/        # Idempotent database initialization scripts (Admin bootstrap)
├── 💼 service/       # Core business logic & transactional orchestration
└── 🛠️ util/          # SecurityContext & authentication helper utilities
```

---

## 💻 Local Setup & Installation

### Prerequisites
- **JDK 21+** installed
- **MySQL 8.0+** running on `localhost:3306`
- **Redis** running on `localhost:6379`
- **Maven** (optional, wrapper included)

### Quick Start
```bash
# 1. Clone the repository
git clone https://github.com/Amaantheprogrammer/swiftcart-backend.git
cd swiftcart-backend

# 2. Configure Environment Variables in application.yml
# Create src/main/resources/application.yml (This file is gitignored for safety)
```

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/ecommerceDb?createDatabaseIfNotExist=true}
    username: ${SPRING_DATASOURCE_USERNAME:root}
    password: ${SPRING_DATASOURCE_PASSWORD:root}
  data:
    redis:
      url: ${REDIS_URL:redis://localhost:6379}

jwt:
  secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
  expiration: 900000          # 15 minutes
  refresh-expiration: 604800000 # 7 days
```

```bash
# 3. Build & Run locally
./mvnw spring-boot:run

# 4. Open Swagger UI
# Navigate to http://localhost:8080/swagger-ui/index.html
```

---

## 🐳 Docker Deployment

To run using a multi-stage Docker build:

```bash
# Build Docker image
docker build -t swift-cart-backend .

# Run container with environment bindings
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://<host>:3306/<dbname>" \
  -e SPRING_DATASOURCE_USERNAME="<user>" \
  -e SPRING_DATASOURCE_PASSWORD="<password>" \
  -e REDIS_URL="redis://<redis-host>:6379" \
  -e JWT_SECRET="<your-32-byte-secret>" \
  swift-cart-backend
```

---

## 🗺️ Engineering Roadmap

- [x] Multi-tier JWT & Redis Refresh Token Architecture
- [x] Multi-region Redis caching strategy
- [x] Production Docker containerization & Render deployment
- [ ] **AI-Powered Product Search & RAG Assistant:**
  - Introduce semantic vector search over product catalogs using embeddings.
  - Integrate a dedicated Python/FastAPI microservice for LLM-grounded shopping recommendations.
- [ ] Expand integration test coverage for full cart purchase transactions (`OrderServiceTest`).

---

## 👤 Author

**Amaan Shakil Coatwala**  
*Computer Engineering Graduate | Specialized in Backend Architecture & Java Microservices*

- **LinkedIn:** [linkedin.com/in/amaan-coatwala](https://www.linkedin.com/in/amaan-coatwala/)
- **GitHub:** [github.com/Amaantheprogrammer](https://github.com/Amaantheprogrammer)
