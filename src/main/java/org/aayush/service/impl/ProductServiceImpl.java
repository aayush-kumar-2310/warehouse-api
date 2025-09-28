package org.aayush.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.aayush.models.Product;
import org.aayush.models.dto.ProductCreateRequest;
import org.aayush.models.dto.ProductUpdateRequest;
import org.aayush.repository.ProductRepo;
import org.aayush.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;

    public ProductServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    @Transactional
    public Product createAndSaveProduct(ProductCreateRequest request) {
        validateProductRequest(request.getProductName(), request.getProductDesc(), request.getAvailableQty(), request.getLowStockThreshold());

        Product existingProduct = findProductByName(request.getProductName());
        if (existingProduct != null) {
            log.warn("Attempted to create duplicate product with name: {}", request.getProductName());
            throw new IllegalArgumentException("Product with the same name already exists");
        }

        Product product = Product.builder()
                .productName(request.getProductName())
                .productDesc(request.getProductDesc())
                .availableQty(request.getAvailableQty())
                .enableLowStockThreshold(request.getEnableLowStockThreshold())
                .lowStockThreshold(request.getLowStockThreshold())
                .build();

        log.debug("Saving new product to database");
        Product savedProduct = productRepo.saveProduct(product);
        log.debug("Product saved successfully");
        return savedProduct;
    }

    @Override
    public Product findProductById(String productId) {
        if (productId == null || productId.isEmpty()) {
            log.warn("Invalid product ID provided");
            throw new IllegalArgumentException("Invalid product ID");
        }
        Product product = productRepo.findProductById(productId);
        if (product == null) {
            log.warn("Product not found: {}", productId);
            throw new RuntimeException("Product not found: " + productId);
        }
        return product;
    }

    @Override
    @Transactional
    public void deleteProductById(String productId) {
        if (productId == null || productId.isEmpty()) {
            log.warn("Attempted to delete product with null or empty ID");
            throw new IllegalArgumentException("Invalid product ID");
        }

        boolean deleted = productRepo.deleteByProductId(productId);
        if (!deleted) {
            log.warn("Failed to delete product by ID: {}", productId);
            throw new RuntimeException("Product not found: " + productId);
        }
        log.info("Product deleted successfully by ID: {}", productId);
    }

    @Override
    @Transactional
    public Product updateProductDetails(ProductUpdateRequest request) {
        if (request.getProductId() == null || request.getProductId().isEmpty()) {
            log.warn("Attempted to update with null or empty ID");
            throw new IllegalArgumentException("Invalid product ID");
        }
        validateProductRequest(request.getProductName(), request.getProductDesc(), request.getAvailableQty(), request.getLowStockThreshold());

        Product product = Product.builder()
                .productId(request.getProductId())
                .productName(request.getProductName())
                .productDesc(request.getProductDesc())
                .availableQty(request.getAvailableQty())
                .enableLowStockThreshold(request.getEnableLowStockThreshold())
                .lowStockThreshold(request.getLowStockThreshold())
                .build();

        Product updatedProduct = productRepo.updateProductDetails(product);
        if (updatedProduct == null) {
            log.warn("Product update failed or product not found. ID: {}", request.getProductId());
            throw new RuntimeException("Product not found: " + request.getProductId());
        }

        log.info("Product updated successfully. ID: {}", request.getProductId());
        return updatedProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.getAllProducts();
    }

    @Override
    public List<Product> findProductsBelowThreshold() {
        return productRepo.findProductsBelowThreshold();
    }

    private void validateProductRequest(String name, String desc, Integer qty, Integer threshold) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required and cannot be blank");
        }
        if (desc == null || desc.isBlank()) {
            throw new IllegalArgumentException("Product description is required and cannot be blank");
        }
        if (qty == null || qty < 0) {
            throw new IllegalArgumentException("Available quantity cannot be null or negative");
        }
        if (threshold != null && threshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
    }

    @Override
    public Product findProductByName(String productName) {
        return productRepo.findProductByName(productName);
    }
}