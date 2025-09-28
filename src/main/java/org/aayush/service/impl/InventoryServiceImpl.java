package org.aayush.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.aayush.models.Product;
import org.aayush.repository.InventoryRepo;
import org.aayush.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo inventoryRepo;

    public InventoryServiceImpl(InventoryRepo inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    @Override
    @Transactional
    public Product addStock(String productId, Integer stockAmount) {
        if (productId == null || productId.isEmpty()) {
            log.warn("Invalid product ID provided for adding stock");
            throw new IllegalArgumentException("Invalid product ID");
        }
        validateStockAmount(stockAmount);

        Product product = inventoryRepo.findProductById(productId);
        if (product == null) {
            log.warn("Product not found: {}", productId);
            throw new RuntimeException("Product not found: " + productId);
        }

        Integer currentQty = product.getAvailableQty() != null ? product.getAvailableQty() : 0;
        if (stockAmount > Integer.MAX_VALUE - currentQty) {
            log.warn("Stock addition would exceed maximum allowed quantity for product ID: {}", productId);
            throw new IllegalArgumentException("Stock addition would exceed maximum allowed quantity");
        }

        Product updatedProduct = inventoryRepo.addStock(productId, stockAmount);
        log.info("Stock added successfully. Product ID: {}, Added Amount: {}, New Quantity: {}",
                productId, stockAmount, updatedProduct.getAvailableQty());
        return updatedProduct;
    }

    @Override
    @Transactional
    public Product decreaseStock(String productId, Integer stockAmount) {
        if (productId == null || productId.isEmpty()) {
            log.warn("Invalid product ID provided for decreasing stock");
            throw new IllegalArgumentException("Invalid product ID");
        }
        validateStockAmount(stockAmount);

        Product product = inventoryRepo.findProductById(productId);
        if (product == null) {
            log.warn("Product not found: {}", productId);
            throw new RuntimeException("Product not found: " + productId);
        }

        Integer currentQty = product.getAvailableQty() != null ? product.getAvailableQty() : 0;
        if (currentQty < stockAmount) {
            log.warn("Insufficient stock. Available: {}, Requested: {}", currentQty, stockAmount);
            throw new IllegalArgumentException("Insufficient stock available. Requested: " + stockAmount + ", Available: " + currentQty);
        }

        Product updatedProduct = inventoryRepo.decreaseStock(productId, stockAmount);
        log.info("Stock decreased successfully. Product ID: {}, Decreased Amount: {}, New Quantity: {}",
                productId, stockAmount, updatedProduct.getAvailableQty());
        return updatedProduct;
    }

    private void validateStockAmount(Integer stockAmount) {
        if (stockAmount == null || stockAmount <= 0) {
            log.warn("Invalid stock amount: {}", stockAmount);
            throw new IllegalArgumentException("Stock amount must be positive and non-null");
        }
    }
}