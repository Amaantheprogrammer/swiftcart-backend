# 🛒 Swift Cart: E-Commerce Backend Platform

**A Java and Spring Boot backend for marketplace workflows, including
product discovery, carts, wishlists, order management, and role-based
access.**

Swift Cart exposes REST APIs for the core operations of an online
shopping platform. It uses Spring Security and JWT-based authentication,
persists application data with MySQL and Spring Data JPA, and uses Redis
for caching. The project also demonstrates communication with an
external digital banking service through Spring's `RestClient`.

🌐 **Live application:** https://e-commercebackendproject.onrender.com\
📚 **Live API documentation:**
https://e-commercebackendproject.onrender.com/swagger-ui/index.html\
💻 **Source code:**
https://github.com/Amaantheprogrammer/Swift-Cart-Backend-Project

> **API count note:** The source archive currently contains 44
> controller mappings. The live Swagger listing shared separately shows
> a different set of operations, so this README uses "40+ REST API
> operations" rather than implying that the deployed and repository
> versions are identical.

------------------------------------------------------------------------

## ✨ Highlights

-   🔐 **Authentication:** JWT access tokens, refresh-token workflows,
    BCrypt password hashing, sign-in, sign-up, sign-out, and token
    refresh.
-   👥 **Role-based access:** Role-aware access for administrators,
    users, and sellers.
-   🛍️ **Marketplace workflows:** Product and category management,
    product images, carts, wishlists, and order operations.
-   🔎 **Product discovery:** Search by product name, optional price
    filtering, category-based browsing, and paginated product retrieval.
-   ⚡ **Redis caching:** Configured cache regions and expiry policies
    for selected product, category, and user reads.
-   🔗 **Service integration:** A `RestClient` integration for supported
    operations against a separate digital banking service.
-   🧱 **Layered design:** Controllers, services, repositories,
    entities, request/response DTOs, and centralized exception handling.
-   📖 **Interactive documentation:** OpenAPI documentation through
    Swagger UI.
-   ☁️ **Deployment:** Publicly deployed application and a Dockerfile
    for container builds.
-   🤖 **AI roadmap:** Planned exploration of semantic product discovery
    and Retrieval-Augmented Generation (RAG); these AI capabilities are
    not implemented in the current backend.

------------------------------------------------------------------------

## 🚀 Features

### 1. Authentication and security

The application uses Spring Security with a custom JWT authentication
filter and method-level authorization.

Implemented flows include:

-   User registration and sign-in.
-   JWT access-token generation and validation.
-   Refresh-token handling through a refresh-token cookie.
-   Sign-out and refresh-token invalidation.
-   BCrypt password hashing.
-   Role-based authorization for protected operations.

Authentication routes in the source include:

  -----------------------------------------------------------------------
  Method                  Endpoint                Purpose
  ----------------------- ----------------------- -----------------------
  `POST`                  `/auth/sign-up`         Register a user

  `POST`                  `/auth/sign-in`         Authenticate and obtain
                                                  tokens

  `POST`                  `/auth/sign-out`        Sign out and clear the
                                                  refresh-token cookie

  `POST`                  `/auth/refresh`         Refresh the access
                                                  token
  -----------------------------------------------------------------------

For protected API calls, send the access token as a bearer token:

``` http
Authorization: Bearer <access-token>
```

The refresh-token flow also relies on the cookie returned by the
authentication endpoints.

### 2. User management

User operations include:

-   Retrieve users and individual user records.
-   Retrieve the authenticated user's profile.
-   Update the current user's details.
-   Delete a user.
-   Apply configured access controls to user operations.

### 3. Product management and discovery

Product functionality includes:

-   Create, retrieve, update, and delete products.
-   Retrieve products with pagination.
-   Search products by name.
-   Apply an optional maximum-price filter to search results.
-   Browse products by category, with optional price filtering.
-   Retrieve products belonging to the authenticated seller.
-   Add, update, and delete product images.
-   Restrict product creation and modification to authorized sellers and
    validate ownership for seller-managed products.

Example requests (check Swagger for the exact parameters and schemas):

``` http
GET /products?page=0&size=10
GET /products/1
GET /products/search?name=phone
GET /products/search?name=phone&price=50000
GET /products/category/1
GET /products/me
```

### 4. Category management

Category operations support creating, listing, retrieving, updating, and
deleting categories. Category search is also defined in the source
controller. Products can be associated with categories and retrieved by
category.

### 5. Shopping cart

Cart APIs support the current user's cart:

  Method     Endpoint                     Purpose
  ---------- ---------------------------- --------------------------------------
  `GET`      `/carts/my-cart`             Retrieve the current user's cart
  `POST`     `/carts/items`               Add a product or update its quantity
  `DELETE`   `/carts/items/{productId}`   Remove a product
  `DELETE`   `/carts/clear`               Clear the cart

### 6. Wishlists

Customers can maintain a list of products they want to revisit:

  Method     Endpoint                   Purpose
  ---------- -------------------------- --------------------------------------
  `GET`      `/wishlists`               Retrieve the current user's wishlist
  `POST`     `/wishlists/{productId}`   Add a product
  `DELETE`   `/wishlists/{productId}`   Remove a product
  `DELETE`   `/wishlists`               Clear the wishlist

### 7. Order management

Order workflows include placing orders directly or from the cart,
retrieving orders, cancelling orders, and updating order and payment
statuses.

  Method    Endpoint                             Purpose
  --------- ------------------------------------ ------------------------------------
  `GET`     `/orders`                            Retrieve orders
  `GET`     `/orders/{orderId}`                  Retrieve an order by ID
  `GET`     `/orders/user/{userId}`              Retrieve orders for a user
  `GET`     `/orders/me`                         Retrieve the current user's orders
  `POST`    `/orders/place-order`                Place an order
  `POST`    `/orders/cart/place-order`           Place an order from the cart
  `PATCH`   `/orders/{orderId}/cancel`           Cancel an order
  `PATCH`   `/orders/{orderId}/order-status`     Update order status
  `PATCH`   `/orders/{orderId}/payment-status`   Update payment status

Access to these operations depends on the authorization rules and
business logic implemented in the application.

### 8. External digital banking service

A dedicated `RestClient` is configured to communicate with a separate
digital banking backend. The integration includes supported account and
transaction requests and forwards authentication credentials for
applicable calls.

This demonstrates service-to-service REST communication. It should
**not** be interpreted as a claim that Swift Cart integrates with a
production payment gateway. The banking service must be running and its
base URL and authentication requirements must match the environment.

### 9. Redis caching

Redis is configured as the Spring cache provider. Cache regions in the
source include selected product lists and lookups, product search
results, category data, and user lookups.

Configured time-to-live policies include:

-   Product-related cache regions: **30 minutes**
-   Category cache region: **12 hours**
-   User-related cache regions: **10 minutes**

The code also uses cache eviction for selected product mutations. Actual
performance gains depend on workload, cache hit rate, invalidation
behavior, and deployment configuration; no performance improvement is
claimed here without a reproducible benchmark.

### 10. Validation and error handling

The application uses Jakarta Bean Validation and centralized exception
handling through `@RestControllerAdvice`. Custom exception types cover
scenarios such as bad requests, missing resources, duplicate resources,
insufficient resources, and invalid refresh tokens.

------------------------------------------------------------------------

## 🧰 Technology stack

  Technology                       Role
  -------------------------------- -----------------------------------
  Java 21                          Programming language
  Spring Boot 3                    Application framework
  Spring Web                       REST API development
  Spring Security                  Authentication and authorization
  JWT (JJWT)                       Access-token handling
  BCrypt                           Password hashing
  Spring Data JPA                  Persistence layer
  Hibernate ORM                    Object-relational mapping
  MySQL                            Relational database
  Redis                            Caching and refresh-token support
  Spring `RestClient`              External service communication
  Spring Boot Actuator             Application monitoring support
  Springdoc OpenAPI / Swagger UI   API documentation
  Maven                            Dependency management and build
  Lombok                           Boilerplate reduction
  ModelMapper                      DTO/entity mapping
  JUnit, Spring Boot Test          Testing dependencies

------------------------------------------------------------------------

## 🏗️ Architecture

Swift Cart uses a layered structure to separate HTTP handling, business
logic, persistence, and integrations.

``` text
                         Client
                           |
                           v
                  Spring Boot REST API
                           |
                    Spring Security
                   JWT Authentication
                           |
                           v
                      Controllers
                           |
                           v
                        Services
                       /        \
                      v          v
                Repositories   RestClient
                      |          |
                      v          v
                    MySQL   Digital Banking API

                  Redis Cache
                      |
                      v
             Cached application data
```

**Controllers** expose endpoints and handle HTTP requests and
responses.\
**Services** coordinate application workflows and business rules.\
**Repositories** provide database access through Spring Data JPA.\
**DTOs** define API request and response structures.\
**Security components** authenticate requests and apply configured
authorization rules.\
**Redis** provides caching and supports refresh-token functionality.\
**RestClient** communicates with the separate banking service.

------------------------------------------------------------------------

## 👥 Application roles

The application defines three roles:

  -----------------------------------------------------------------------
  Role                                Intended area
  ----------------------------------- -----------------------------------
  `ROLE_ADMIN`                        Authorized administrative
                                      operations

  `ROLE_USER`                         Customer operations such as carts,
                                      wishlists, and orders

  `ROLE_SELLER`                       Seller-owned product creation and
                                      management
  -----------------------------------------------------------------------

Actual access is determined by the endpoint rules and method-level
authorization in the code.

------------------------------------------------------------------------

## 📂 Project structure

``` text
src/
└── main/
    ├── java/com/myProject/
    │   ├── config/
    │   ├── controller/
    │   ├── dto/
    │   │   ├── request/
    │   │   └── response/
    │   ├── entity/
    │   ├── enums/
    │   ├── exception/
    │   ├── filter/
    │   ├── repository/
    │   ├── seeder/
    │   ├── service/
    │   └── ECommerceBackendProjectApplication.java
    └── resources/
```

Feature-specific behavior is implemented across the controller, service,
repository, entity, and DTO layers. Configuration files should be
supplied for the target environment and should not contain committed
production secrets.

------------------------------------------------------------------------

## 📚 API documentation

Use the live Swagger UI to inspect available endpoints, HTTP methods,
parameters, and request/response schemas:

**[Open Swift Cart Swagger
UI](https://e-commercebackendproject.onrender.com/swagger-ui/index.html)**

For a local instance, open:

``` text
http://localhost:8080/swagger-ui/index.html
```

The deployed API documentation may not match the source archive if the
deployed service and repository are on different revisions. Check the
deployment revision before relying on an exact endpoint count.

------------------------------------------------------------------------

## ⚙️ Configuration

Configure the database, Redis, JWT settings, server port, and external
banking-service URL for your environment. Do not commit credentials or
production secrets.

The application uses Hibernate schema validation (`ddl-auto: validate`)
in the configuration described for this project. The database schema
must therefore be compatible with the entity mappings before the
application starts.

Common configuration values used by the project include:

  Setting                        Purpose
  ------------------------------ -----------------------------------------------
  `SPRING_DATASOURCE_URL`        MySQL JDBC URL
  `SPRING_DATASOURCE_USERNAME`   Database username
  `SPRING_DATASOURCE_PASSWORD`   Database password
  `REDIS_URL`                    Redis connection URL
  `JWT_SECRET`                   JWT signing secret
  `JWT_EXPIRATION`               Access-token lifetime in milliseconds
  `PORT`                         HTTP port supplied by the deployment platform

The exact property names and defaults must match the `application.yml`
used by your local checkout/deployment. The uploaded source archive does
not include that YAML file, so verify or add the environment-specific
configuration before running the application.

**Security checklist**

-   Use a unique, strong JWT secret outside local development.
-   Do not use a development fallback secret in production.
-   Keep database credentials out of source control.
-   Use least-privilege database credentials.
-   Review cookie security and CORS settings for the deployed
    environment.
-   Avoid enabling verbose SQL logging in production unless needed.

------------------------------------------------------------------------

## ▶️ Getting started

### Prerequisites

-   JDK 21
-   MySQL
-   Redis
-   Git
-   Access to the external digital banking service if you want to test
    its integration

### 1. Clone the repository

``` bash
git clone https://github.com/Amaantheprogrammer/Swift-Cart-Backend-Project.git
cd Swift-Cart-Backend-Project
```

If your repository's actual name or default branch differs, use the
corresponding clone URL and directory name.

### 2. Configure dependencies

Create/configure the MySQL database and ensure the schema matches the
JPA entities. Start Redis and configure the database, JWT, and external
service settings for your environment.

Because the project uses schema validation, Hibernate will validate
existing tables rather than automatically creating or updating the
schema.

### 3. Run the application

**Windows PowerShell:**

``` powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

``` bash
./mvnw spring-boot:run
```

Build the project with:

``` bash
./mvnw clean package
```

Then open the local Swagger UI at
`http://localhost:8080/swagger-ui/index.html`.

### 4. Container build

A `Dockerfile` is included. To build an image from the repository root:

``` bash
docker build -t swift-cart-backend .
```

Run the image with the required database, Redis, JWT, and
external-service configuration supplied for your environment. A
reachable MySQL and Redis instance are still required.

------------------------------------------------------------------------

## 🧪 Testing

The project includes Spring Boot Test and Spring Security Test
dependencies. Run the test suite with:

**Windows:**

``` powershell
.\mvnw.cmd test
```

**Linux/macOS:**

``` bash
./mvnw test
```

Important areas to cover with automated tests include authentication and
authorization, product ownership rules, cart and wishlist operations,
order placement and cancellation, validation, cache invalidation, and
failures from external service calls.

------------------------------------------------------------------------

## 🤖 Roadmap: AI-powered shopping assistance

Generative AI and RAG are planned extensions, not features of the
current backend.

### Phase 1 --- Product knowledge base

-   Extract product names, descriptions, categories, and specifications.
-   Normalize product data.
-   Generate embeddings.
-   Store vectors in a vector database.

### Phase 2 --- Retrieval-Augmented Generation

Retrieve relevant product records before asking a language model to
answer customer questions, for example:

-   "Find a laptop for programming under ₹60,000."
-   "Compare these two products."
-   "Which products match these requirements?"

Responses should be grounded in retrieved catalogue data and link back
to the relevant products.

### Phase 3 --- Dedicated AI service

A possible future design is to keep Spring Boot responsible for core
business logic and introduce a Python service for retrieval and
generation.

``` text
Customer
   |
   v
Swift Cart Backend (Spring Boot)
   |
   v
AI Service (Python / FastAPI)
   |
   +--> Retrieval pipeline --> Vector database
   |
   +--> Language model
   |
   v
Grounded response with product references
```

Potential tools include LangChain, an embedding model, and a vector
database selected according to evaluation and deployment needs.

### Phase 4 --- Reliability and safety

-   Evaluate retrieval relevance and answer quality.
-   Return product references with grounded answers.
-   Handle cases where the catalogue has no relevant result.
-   Measure latency and cost.
-   Enforce authentication and authorization for customer-specific data.
-   Prevent the AI service from bypassing backend permissions or
    directly performing sensitive business actions.

------------------------------------------------------------------------

## 🎯 Engineering concepts

-   REST API design and layered architecture
-   JWT authentication and role-based authorization
-   JPA entity relationships and relational data modeling
-   Pagination, filtering, and search
-   DTO-based API contracts
-   Centralized validation and exception handling
-   Redis caching and cache eviction
-   External REST service integration
-   Environment-specific configuration
-   OpenAPI documentation
-   Container builds with Docker

------------------------------------------------------------------------

## 👨‍💻 Author

**Amaan Shakil Coatwala**

Interests: Java, Spring Boot, backend engineering, REST APIs, databases,
caching, system design, and Generative AI.

-   **GitHub:** https://github.com/Amaantheprogrammer
-   **LinkedIn:** https://www.linkedin.com/in/amaan-coatwala/

------------------------------------------------------------------------

## ⭐ Support

If you find Swift Cart useful or interesting, consider starring the
repository. Feedback and contributions that improve its security,
architecture, testing, and functionality are welcome.
