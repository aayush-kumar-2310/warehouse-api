package org.aayush.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.aayush.models.Product;
import org.aayush.models.dto.ErrorResponse;
import org.aayush.models.dto.ProductCreateRequest;
import org.aayush.models.dto.ProductUpdateRequest;
import org.aayush.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        log.info("Received request to create product");
        try {
            Product savedProduct = productService.createAndSaveProduct(request);
            return ResponseEntity.ok(savedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_PRODUCT")
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") String productId) {
        log.info("Received request to get product with ID: {}", productId);
        if (productId == null || productId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_REQUEST")
                            .message("Product ID cannot be null or empty")
                            .build());
        }
        try {
            Product product = productService.findProductById(productId);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("Product not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponse.builder()
                                .errorCode("PRODUCT_NOT_FOUND")
                                .message(e.getMessage())
                                .build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_REQUEST")
                            .message("Invalid product ID format")
                            .build());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductUpdateRequest request) {
        log.info("Received request to update product with ID: {}", request.getProductId());
        try {
            Product updatedProduct = productService.updateProductDetails(request);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_PRODUCT")
                            .message(e.getMessage())
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .errorCode("PRODUCT_NOT_FOUND")
                            .message(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable("id") String productId) {
        log.info("Received request to delete product with ID: {}", productId);
        if (productId == null || productId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_REQUEST")
                            .message("Product ID cannot be null or empty")
                            .build());
        }
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("Product not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponse.builder()
                                .errorCode("PRODUCT_NOT_FOUND")
                                .message(e.getMessage())
                                .build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_REQUEST")
                            .message("Invalid product ID format")
                            .build());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> fetchAllProducts() {
        log.info("Fetching all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        log.info("Fetching products below stock threshold");
        List<Product> result = productService.findProductsBelowThreshold();
        return ResponseEntity.ok(result);
    }
}