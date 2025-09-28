package org.aayush.repository;

import org.aayush.models.Product;

import java.util.List;

public interface ProductRepo {
    Product saveProduct(Product product);
    Product findProductById(String productId);
    boolean deleteProduct(Product product);
    boolean deleteByProductId(String productId);
    Product updateProductDetails(Product product);
    List<Product> getAllProducts();
    List<Product> findProductsBelowThreshold();
    Product findProductByName(String productName);
}