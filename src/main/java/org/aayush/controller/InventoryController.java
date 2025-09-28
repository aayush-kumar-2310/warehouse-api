package org.aayush.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.aayush.models.Product;
import org.aayush.models.dto.ErrorResponse;
import org.aayush.models.dto.StockUpdateRequest;
import org.aayush.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}/add-stock")
    public ResponseEntity<?> addStock(@PathVariable("productId") String productId, @Valid @RequestBody StockUpdateRequest request) {
        log.info("Request to add stock: Product ID = {}, Amount = {}", productId, request.getAmount());
        if (productId == null || productId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_REQUEST")
                            .message("Product ID cannot be null or empty")
                            .build());
        }
        try {
            Product updatedProduct = inventoryService.addStock(productId, request.getAmount());
            return ResponseEntity.ok(updatedProduct);
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
                            .errorCode("INVALID_STOCK_OPERATION")
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/{productId}/decrease-stock")
    public ResponseEntity<?> decreaseStock(@PathVariable("productId") String productId, @Valid @RequestBody StockUpdateRequest request) {
        log.info("Request to decrease stock: Product ID = {}, Amount = {}", productId, request.getAmount());
        if (productId == null || productId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_REQUEST")
                            .message("Product ID cannot be null or empty")
                            .build());
        }
        try {
            Product updatedProduct = inventoryService.decreaseStock(productId, request.getAmount());
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("Product not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponse.builder()
                                .errorCode("PRODUCT_NOT_FOUND")
                                .message(e.getMessage())
                                .build());
            }
            if (e.getMessage().startsWith("Insufficient stock")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.builder()
                                .errorCode("INSUFFICIENT_STOCK")
                                .message(e.getMessage())
                                .build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .errorCode("INVALID_STOCK_OPERATION")
                            .message(e.getMessage())
                            .build());
        }
    }
}