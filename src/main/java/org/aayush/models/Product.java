package org.aayush.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "products")
public class Product {

    @Id
    private String productId;
    private String productName;
    private String productDesc;
    private Integer availableQty;
    private Boolean enableLowStockThreshold;
    private Integer lowStockThreshold;
}
