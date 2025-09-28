package org.aayush.service;

import org.aayush.models.Product;
import org.aayush.models.dto.ProductCreateRequest;
import org.aayush.models.dto.ProductUpdateRequest;

import java.util.List;

public interface ProductService {
    Product createAndSaveProduct(ProductCreateRequest request);
    Product findProductById(String productId);
    void deleteProductById(String productId);
    Product updateProductDetails(ProductUpdateRequest request);
    List<Product> getAllProducts();
    List<Product> findProductsBelowThreshold();
    Product findProductByName(String productName);
}