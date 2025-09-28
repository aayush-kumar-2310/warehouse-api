package org.aayush.service;

import org.aayush.models.Product;

public interface InventoryService {
    Product addStock(String productId, Integer stockAmount);
    Product decreaseStock(String productId, Integer stockAmount);
}