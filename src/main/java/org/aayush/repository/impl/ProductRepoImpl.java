package org.aayush.repository.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.aayush.models.Product;
import org.aayush.repository.ProductRepo;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@Slf4j
public class ProductRepoImpl implements ProductRepo {

    private final MongoTemplate mongoTemplate;

    public ProductRepoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Product saveProduct(Product product) {
        log.debug("Saving product to database");
        return mongoTemplate.save(product);
    }

    @Override
    public Product findProductById(String productId) {
        log.info("Finding product with ID: {}", productId);
        return mongoTemplate.findById(productId, Product.class);
    }

    @Override
    public boolean deleteProduct(Product product) {
        log.info("Deleting product: {}", product.getProductId());
        return mongoTemplate.remove(product).wasAcknowledged();
    }

    @Override
    public boolean deleteByProductId(String productId) {
        log.info("Deleting product by ID: {}", productId);
        Query query = new Query(Criteria.where("_id").is(productId));
        DeleteResult result = mongoTemplate.remove(query, Product.class);
        return result.wasAcknowledged() && result.getDeletedCount() > 0;
    }

    @Override
    public Product updateProductDetails(Product product) {
        log.info("Updating product: {}", product.getProductId());
        Query query = new Query(Criteria.where("_id").is(product.getProductId()));

        Update update = new Update()
                .set("productName", product.getProductName())
                .set("productDesc", product.getProductDesc())
                .set("availableQty", product.getAvailableQty())
                .set("enableLowStockThreshold", product.getEnableLowStockThreshold())
                .set("lowStockThreshold", product.getLowStockThreshold());

        UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);

        if (result.getMatchedCount() == 0) {
            return null;
        }
        return mongoTemplate.findById(product.getProductId(), Product.class);
    }

    @Override
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return mongoTemplate.findAll(Product.class);
    }

    @Override
    public List<Product> findProductsBelowThreshold() {
        log.info("Fetching products below low stock threshold");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("enableLowStockThreshold").is(true)),
                Aggregation.match(Criteria.expr(
                        ComparisonOperators.Lt.valueOf("$availableQty").lessThan("$lowStockThreshold")
                ))
        );
        return mongoTemplate.aggregate(aggregation, Product.class, Product.class)
                .getMappedResults();
    }

    @Override
    public Product findProductByName(String productName) {
        Query query = new Query(Criteria.where("productName").is(productName));
        return mongoTemplate.findOne(query, Product.class);
    }
}