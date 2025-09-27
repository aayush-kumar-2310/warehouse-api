markdown
# Warehouse API

## Project Description
The Warehouse API is a Spring Boot application for managing products in a warehouse. It provides RESTful endpoints for creating, reading, updating, and deleting products, as well as managing inventory by increasing or decreasing stock quantities. The application uses MongoDB as the database and includes input validation, error handling, and unit tests for stock operations. A bonus feature allows querying products below a low stock threshold.

Key features:
- **Product Management**: CRUD operations for products (name, description, stock quantity, low stock threshold).
- **Inventory Management**: Endpoints to add or remove stock, preventing negative quantities and handling overflow.
- **List Products with low stock**: Endpoint to list products with stock below a defined threshold when enabled.
- **Unit Tests**: Comprehensive tests for stock addition and removal, covering edge cases like insufficient stock or invalid inputs.

## Setup and Run Instructions
### Prerequisites
- **Java 17**: Ensure JDK 17 is installed.
- **Docker**: Required for containerized deployment.
- **MongoDB**: Can be run via Docker Compose or as a standalone instance.
- **Gradle** (or Maven): For building the project.

### Steps to Set Up and Run Locally
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/warehouse-api.git
   cd warehouse-api
   ```

2. **Build the Project**:
   - Using Maven:
     ```bash
     mvn clean package
     ```
   - Using Gradle:
     ```bash
     ./gradlew build
     ```

3. **Run with Docker Compose** (recommended, includes MongoDB):
   ```bash
   docker-compose up -d
   ```
   - This starts the application on `http://localhost:8080` and MongoDB on `localhost:27017`.
   - Verify the application is running:
     ```bash
     curl http://localhost:8080/actuator/health
     ```
     Expected output: `{"status": "UP"}`

4. **Run Without Docker** (requires a local MongoDB instance):
   - Start MongoDB locally (e.g., `mongod --dbpath /path/to/db`).
   - Configure `src/main/resources/application.properties` with your MongoDB URI:
     ```properties
     spring.data.mongodb.uri=mongodb://localhost:27017/warehouse
     ```
   - Run the application:
     - Maven: `mvn spring-boot:run`
     - Gradle: `./gradlew bootRun`

5. **Test Endpoints**:
   Use Postman or curl to interact with the API. Examples:
   - **Create Product**:
     ```bash
     curl -X POST http://localhost:8080/product/create \
       -H "Content-Type: application/json" \
       -d '{"productName":"Laptop","productDesc":"High-performance laptop","availableQty":10,"enableLowStockThreshold":true,"lowStockThreshold":5}'
     ```
   - **Get Product**: `curl http://localhost:8080/product/{id}`
   - **Update Product**:
     ```bash
     curl -X PUT http://localhost:8080/product/update \
       -H "Content-Type: application/json" \
       -d '{"productId":"{id}","productName":"Updated Laptop","productDesc":"Updated description","availableQty":15,"enableLowStockThreshold":true,"lowStockThreshold":5}'
     ```
   - **Delete Product**: `curl -X DELETE http://localhost:8080/product/{id}`
   - **Get All Products**: `curl http://localhost:8080/product/all`
   - **Add Stock**:
     ```bash
     curl -X POST http://localhost:8080/inventory/{productId}/add-stock \
       -H "Content-Type: application/json" \
       -d '{"amount":5}'
     ```
   - **Decrease Stock**:
     ```bash
     curl -X POST http://localhost:8080/inventory/{productId}/decrease-stock \
       -H "Content-Type: application/json" \
       -d '{"amount":3}'
     ```
   - **Get Low Stock Products**: `curl http://localhost:8080/product/low-stock`

6. **Stop Containers** (if using Docker Compose):
   ```bash
   docker-compose down
   ```

## Running Test Cases
Unit tests are provided for the inventory logic (`InventoryServiceImpl`), covering stock addition and removal, including edge cases like insufficient stock or invalid inputs.

### Prerequisites
- Gradle installed.
- JUnit 5 and Mockito dependencies (included in `pom.xml` or `build.gradle`).

### Steps to Run Tests
1. **Build and Run Tests**:
   - Using Gradle:
     ```bash
     ./gradlew test
     ```

2. **Verify Test Results**:
   - Tests are located in `src/test/java/org/aayush/service/impl/InventoryServiceImplTest.java`.
   - The test suite includes:
     - Successful stock addition/removal.
     - Handling of null/empty product IDs.
     - Handling of null/non-positive stock amounts.
     - Edge cases: exceeding `Integer.MAX_VALUE` for stock addition, removing more stock than available.
   - Check the console or test report (e.g., `target/surefire-reports` for Maven) for results.

## Assumptions and Design Choices
- **Architecture**:
  - The application follows a layered architecture (controllers, services, repositories) for separation of concerns.
  - Controllers handle input validation and HTTP responses, while services contain all business logic.
  - Repositories interact with MongoDB using Spring Data MongoDB.
- **Database**:
  - MongoDB is used for its flexibility with document-based data, suitable for product information.
  - The database is assumed to be available at `mongodb://localhost:27017/verto` or via Docker Compose.
- **Error Handling**:
  - Standard Java exceptions (`IllegalArgumentException`, `RuntimeException`) are used instead of custom exceptions for simplicity.
  - Controllers return consistent error responses with HTTP status codes (400 for invalid inputs, 404 for not found).
- **Security**:
  - No authentication/authorization is implemented, as per requirements.
  - The Dockerfile runs as a non-root user for security.
- **Dockerization**:
  - A multi-stage Dockerfile is used to build the JAR and create a lightweight runtime image.
  - Docker Compose simplifies running the application with MongoDB.
  - The Spring Boot Actuator `/actuator/health` endpoint is included for health checks.
- **Testing**:
  - Unit tests focus on `InventoryServiceImpl` to verify stock logic, using Mockito to mock the repository layer.
  - Edge cases (e.g., insufficient stock, integer overflow) are explicitly tested.
- **Assumptions**:
  - The evaluator has Docker and a build tool (Maven/Gradle) installed.
  - MongoDB is accessible, either via Docker Compose or a local instance.
  - The application runs on port 8080 by default, configurable via environment variables.
  - Input validation assumes non-null, non-negative quantities and valid strings for product names/descriptions.

## Dependencies
- Spring Boot (Web, Data MongoDB, Actuator)
- Lombok (for boilerplate reduction)
- Jakarta Validation API (for input validation)
- JUnit 5 and Mockito (for unit tests)
- MongoDB
- Docker
```
