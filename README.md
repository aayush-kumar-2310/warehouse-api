# Warehouse API

## Project Description

The Warehouse API is a RESTful service built with Spring Boot and MongoDB to manage products in a warehouse inventory system. It supports CRUD operations for products, stock updates (add/decrease), and retrieval of products below their low stock threshold. The application is containerized with Docker, includes automatic data seeding for development, and provides comprehensive error handling and logging. Key features include:

- Create, read, update, and delete products.
- Add or decrease stock for products.
- Retrieve products with low stock based on configurable thresholds.
- Input validation and proper HTTP status codes (e.g., 400 for invalid requests, 404 for not found).
- Automatic seeding of sample data in the development environment.

The project is designed for reliability, maintainability, and ease of testing, with a focus on clean code and REST API best practices.

## Setup and Run Instructions

### Prerequisites
- **Docker**: Ensure Docker and Docker Compose are installed (`docker --version` and `docker-compose --version`).
- **Java 17**: Required for building the project locally (optional if using Docker).
- **Gradle**: Required for building the project locally (optional if using Docker).
- **Git**: To clone the repository.

### Steps to Run Locally
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/aayush-kumar-2310/warehouse-api.git
   cd warehouse-api
   ```

2. **Run with Docker Compose** (Recommended):
    - Ensure Docker is running.
    - Build and start the application and MongoDB:
      ```bash
      docker-compose up -d
      ```
    - Verify services are running:
      ```bash
      docker-compose ps
      ```
      Ensure `warehouse-api_app_1` and `warehouse-api_mongo_1` are in the `Up` state.
    - The application runs on `http://localhost:8080`.

3. **Verify Application**:
    - Check the health endpoint:
      ```bash
      curl http://localhost:8080/actuator/health
      ```
      Expected: `{"status":"UP"}`
    - View logs:
      ```bash
      docker-compose logs app
      ```

4. **Stop the Application**:
    - With Docker:
      ```bash
      docker-compose down
      ```
      To clear MongoDB data:
      ```bash
      docker-compose down -v
      ```

## Running Test Cases

### Unit Tests
The project includes unit tests for controllers, services, and repositories using JUnit and Mockito.

1. **Run Unit Tests**:
   ```bash
   ./gradlew clean test
   ```
2. **View Results**:
    - Test reports are generated in `build/reports/tests/test/index.html`.
    - Tests cover:
        - Product creation with validation (e.g., duplicate names, invalid fields).
        - Stock operations (add/decrease, insufficient stock).
        - Low stock queries.
        - Error handling (400, 404 responses).

### Manual Testing with curl
The application seeds 5 sample products in the `dev` profile (see Sample Data below). Use the following `curl` commands to test all endpoints. Replace `{id1}`, `{id2}`, etc., with `productId` values from:
```bash
curl http://localhost:8080/product/all
```

#### Sample Data
The `dev` profile seeds:
1. **Laptop Pro**: 50 units, threshold enabled (20).
2. **Smartphone X**: 5 units, threshold enabled (10).
3. **Wireless Headphones**: 0 units, threshold enabled (5).
4. **Monitor 4K**: 100 units, threshold disabled.
5. **Keyboard Mechanical**: 25 units, threshold disabled.

#### Quick Setup with Postman Collection
A complete Postman collection is available at: `Warehouse-API-Verto.postman_collection.json`

**To use the collection:**
1. Copy the contents of `Warehouse-API-Verto.postman_collection.json`
2. Open Postman → Import → Raw text
3. Paste the collection JSON and import
4. All endpoints will be available with pre-configured requests

This saves you from manually copying and pasting each curl command individually.


#### Test Commands
1. **GET /product/{id}** (Retrieve a product):
   ```bash
   curl http://localhost:8080/product/{id1}
   ```
   **Expected**: 200 OK, JSON for "Laptop Pro".
    - Non-existent ID:
      ```bash
      curl http://localhost:8080/product/invalid123
      ```
      **Expected**: 404 Not Found, `{"errorCode":"PRODUCT_NOT_FOUND","message":"Product not found: invalid123"}`.

2. **POST /product/create** (Create a product):
   ```bash
   curl -X POST http://localhost:8080/product/create \
     -H "Content-Type: application/json" \
     -d '{"productName":"Tablet","productDesc":"Portable tablet","availableQty":30,"enableLowStockThreshold":true,"lowStockThreshold":10}'
   ```
   **Expected**: 200 OK, new product JSON.
    - Duplicate name:
      ```bash
      curl -X POST http://localhost:8080/product/create \
        -H "Content-Type: application/json" \
        -d '{"productName":"Laptop Pro","productDesc":"Duplicate","availableQty":10}'
      ```
      **Expected**: 400 Bad Request, `{"errorCode":"INVALID_PRODUCT","message":"Product with the same name already exists"}`.

3. **PUT /product/update** (Update a product):
   ```bash
   curl -X PUT http://localhost:8080/product/update \
     -H "Content-Type: application/json" \
     -d '{"productId":"{id4}","productName":"Monitor 4K Ultra","productDesc":"Updated Ultra HD monitor","availableQty":90,"enableLowStockThreshold":false}'
   ```
   **Expected**: 200 OK, updated product.
    - Non-existent ID:
      ```bash
      curl -X PUT http://localhost:8080/product/update \
        -H "Content-Type: application/json" \
        -d '{"productId":"invalid123","productName":"Invalid","productDesc":"Desc","availableQty":10}'
      ```
      **Expected**: 404 Not Found.

4. **DELETE /product/{id}** (Delete a product):
   ```bash
   curl -X DELETE http://localhost:8080/product/{id5}
   ```
   **Expected**: 204 No Content.
    - Non-existent ID:
      ```bash
      curl -X DELETE http://localhost:8080/product/invalid123
      ```
      **Expected**: 404 Not Found.

5. **GET /product/all** (List all products):
   ```bash
   curl http://localhost:8080/product/all
   ```
   **Expected**: 200 OK, array of products.

6. **POST /inventory/{productId}/add-stock** (Add stock):
   ```bash
   curl -X POST http://localhost:8080/inventory/{id2}/add-stock \
     -H "Content-Type: application/json" \
     -d '{"amount":10}'
   ```
   **Expected**: 200 OK, `availableQty`: 15.
    - Non-existent ID:
      ```bash
      curl -X POST http://localhost:8080/inventory/invalid123/add-stock \
        -H "Content-Type: application/json" \
        -d '{"amount":5}'
      ```
      **Expected**: 404 Not Found.

7. **POST /inventory/{productId}/decrease-stock** (Decrease stock):
   ```bash
   curl -X POST http://localhost:8080/inventory/{id1}/decrease-stock \
     -H "Content-Type: application/json" \
     -d '{"amount":10}'
   ```
   **Expected**: 200 OK, `availableQty`: 40.
    - Insufficient stock:
      ```bash
      curl -X POST http://localhost:8080/inventory/{id2}/decrease-stock \
        -H "Content-Type: application/json" \
        -d '{"amount":10}'
      ```
      **Expected**: 400 Bad Request, `{"errorCode":"INSUFFICIENT_STOCK","message":"Insufficient stock available. Requested: 10, Available: 5"}`.

8. **GET /product/low-stock** (List low stock products):
   ```bash
   curl http://localhost:8080/product/low-stock
   ```
   **Expected**: 200 OK, array with `Smartphone X` and `Wireless Headphones`.

## Assumptions and Design Choices

- **MongoDB**: Used as the database for its flexibility with unstructured data and scalability. The `products` collection stores product details with fields: `productId`, `productName`, `productDesc`, `availableQty`, `enableLowStockThreshold`, `lowStockThreshold`.
- **Error Handling**: Uses standard HTTP status codes:
    - `200 OK`: Successful operations.
    - `204 No Content`: Successful deletion.
    - `400 Bad Request`: Invalid inputs (e.g., empty ID, duplicate name, insufficient stock).
    - `404 Not Found`: Non-existent product IDs.
    - Error responses include `errorCode` (e.g., `PRODUCT_NOT_FOUND`, `INVALID_REQUEST`, `INSUFFICIENT_STOCK`) and `message`.
- **Data Seeding**: In the `dev` profile, a `CommandLineRunner` (`DataInitializer`) clears the database and seeds 5 sample products to simplify testing. Disabled in other profiles to avoid affecting production data.
- **Low Stock Threshold**: The `enableLowStockThreshold` field controls whether a product is checked for low stock. When `false`, `lowStockThreshold` is ignored, and the field is optional in requests for clarity.
- **Low Stock Query**: The `/product/low-stock` endpoint uses MongoDB’s `$expr` to compare `availableQty` with `lowStockThreshold` for products with `enableLowStockThreshold: true`.
- **No Custom Exceptions**: Relies on `RuntimeException` and `IllegalArgumentException` with message checks for simplicity, avoiding custom exception classes.
- **Docker**: Uses `docker-compose.yml` to run the application and MongoDB with health checks for reliability.
- **Logging**: SLF4J with Logback provides detailed logs at the `DEBUG` level for troubleshooting.
- **Validation**: Uses Bean Validation (`@Valid`) for request DTOs to enforce constraints (e.g., non-null fields, positive stock amounts).

## Known Limitations
- The `/product/low-stock` query uses `$expr`, which may have performance implications for very large datasets. A future improvement could index relevant fields.
- No authentication/authorization, as it’s a demo API. Production systems would require security (e.g., OAuth2).
- Data seeding clears the database in `dev` mode, which may not suit all testing scenarios. If it is not required, it can be safely removed from ```DataInitializer``` class.

## Future Improvements
- Add pagination to `/product/all` and `/product/low-stock` for scalability.
- Implement global exception handling with for consistency.
- Add more robust input validation (e.g., regex for `productId` format).

---

**GitHub Repository**: [https://github.com/yourusername/warehouse-api](https://github.com/aayush-kumar-2310/warehouse-api)