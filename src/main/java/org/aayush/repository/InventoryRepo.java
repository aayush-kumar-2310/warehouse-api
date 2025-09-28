package org.aayush.repository;

import org.aayush.models.Product;

public interface InventoryRepo {
    Product addStock(String productId, Integer stockAmount);
    Product decreaseStock(String productId, Integer stockAmount);
    Product findProductById(String productId);
}