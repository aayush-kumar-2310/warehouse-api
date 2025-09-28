package org.aayush.config;

import lombok.extern.slf4j.Slf4j;
import org.aayush.models.dto.ProductCreateRequest;
import org.aayush.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    @Profile("dev")
    CommandLineRunner initDatabase(ProductService productService) {
        return args -> {
            log.info("Initializing database with sample products");

            //remove if previous data is required, currently deleting all prev docs from DB
            productService.getAllProducts().forEach(product -> {
                productService.deleteProductById(product.getProductId());
                log.debug("Deleted existing product: {}", product.getProductName());
            });

            try {
                productService.createAndSaveProduct(new ProductCreateRequest(
                        "Laptop Pro", "High-end laptop with 16GB RAM", 50, true, 20));
                log.info("Seeded product: Laptop Pro");

                productService.createAndSaveProduct(new ProductCreateRequest(
                        "Smartphone X", "Latest smartphone model", 5, true, 10));
                log.info("Seeded product: Smartphone X");

                productService.createAndSaveProduct(new ProductCreateRequest(
                        "Wireless Headphones", "Noise-cancelling headphones", 0, true, 5));
                log.info("Seeded product: Wireless Headphones");

                productService.createAndSaveProduct(new ProductCreateRequest(
                        "Monitor 4K", "Ultra HD monitor", 100, false, null));
                log.info("Seeded product: Monitor 4K");

                productService.createAndSaveProduct(new ProductCreateRequest(
                        "Keyboard Mechanical", "RGB mechanical keyboard", 25, false, null));
                log.info("Seeded product: Keyboard Mechanical");
            } catch (IllegalArgumentException e) {
                log.error("Failed to seed product: {}", e.getMessage());
            }

            log.info("Database initialization completed");
        };
    }
}
