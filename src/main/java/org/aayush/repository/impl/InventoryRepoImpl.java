package org.aayush.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.aayush.models.Product;
import org.aayush.repository.InventoryRepo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class InventoryRepoImpl implements InventoryRepo {

    private final MongoTemplate mongoTemplate;

    public InventoryRepoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Product findProductById(String productId) {
        log.info("Finding product with ID: {}", productId);
        return mongoTemplate.findById(productId, Product.class);
    }

    @Override
    public Product addStock(String productId, Integer stockAmount) {
        log.info("Adding stock for product ID: {}, Amount: {}", productId, stockAmount);
        Product product = mongoTemplate.findById(productId, Product.class);
        if (product == null) {
            return null;
        }

        Integer currentQty = product.getAvailableQty() != null ? product.getAvailableQty() : 0;
        product.setAvailableQty(currentQty + stockAmount);
        return mongoTemplate.save(product);
    }

    @Override
    public Product decreaseStock(String productId, Integer stockAmount) {
        log.info("Decreasing stock for product ID: {}, Amount: {}", productId, stockAmount);
        Product product = mongoTemplate.findById(productId, Product.class);
        if (product == null) {
            return null;
        }

        Integer currentQty = product.getAvailableQty() != null ? product.getAvailableQty() : 0;
        product.setAvailableQty(currentQty - stockAmount);
        return mongoTemplate.save(product);
    }
}