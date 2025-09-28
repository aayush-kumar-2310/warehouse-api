package org.aayush.service.impl;

import org.aayush.models.Product;
import org.aayush.repository.InventoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepo inventoryRepo;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .productId("1")
                .productName("iPhone")
                .productDesc("iPhone 99")
                .availableQty(10)
                .enableLowStockThreshold(true)
                .lowStockThreshold(5)
                .build();
    }

    @Test
    void addStock_SuccessfulAddition_ReturnsUpdatedProduct() {
        String productId = "1";
        Integer stockAmount = 5;
        Product updatedProduct = Product.builder()
                .productId("1")
                .productName("iPhone")
                .productDesc("iPhone 99")
                .availableQty(15)
                .enableLowStockThreshold(true)
                .lowStockThreshold(5)
                .build();
        when(inventoryRepo.findProductById(productId)).thenReturn(product);
        when(inventoryRepo.addStock(productId, stockAmount)).thenReturn(updatedProduct);

        Product result = inventoryService.addStock(productId, stockAmount);

        assertNotNull(result);
        assertEquals(15, result.getAvailableQty());
        verify(inventoryRepo).findProductById(productId);
        verify(inventoryRepo).addStock(productId, stockAmount);
    }

    @Test
    void addStock_NullProductId_ThrowsIllegalArgumentException() {
        String productId = null;
        Integer stockAmount = 5;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addStock(productId, stockAmount));
        assertEquals("Invalid product ID", exception.getMessage());
        verify(inventoryRepo, never()).findProductById(anyString());
        verify(inventoryRepo, never()).addStock(anyString(), anyInt());
    }

    @Test
    void addStock_EmptyProductId_ThrowsIllegalArgumentException() {
        String productId = "";
        Integer stockAmount = 5;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addStock(productId, stockAmount));
        assertEquals("Invalid product ID", exception.getMessage());
        verify(inventoryRepo, never()).findProductById(anyString());
        verify(inventoryRepo, never()).addStock(anyString(), anyInt());
    }

    @Test
    void addStock_NullStockAmount_ThrowsIllegalArgumentException() {
        String productId = "1";
        Integer stockAmount = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addStock(productId, stockAmount));
        assertEquals("Stock amount must be positive and non-null", exception.getMessage());
        verify(inventoryRepo, never()).findProductById(anyString());
        verify(inventoryRepo, never()).addStock(anyString(), anyInt());
    }

    @Test
    void addStock_NonPositiveStockAmount_ThrowsIllegalArgumentException() {
        String productId = "1";
        Integer stockAmount = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addStock(productId, stockAmount));
        assertEquals("Stock amount must be positive and non-null", exception.getMessage());
        verify(inventoryRepo, never()).findProductById(anyString());
        verify(inventoryRepo, never()).addStock(anyString(), anyInt());
    }

    @Test
    void addStock_ProductNotFound_ThrowsRuntimeException() {
        String productId = "1";
        Integer stockAmount = 5;
        when(inventoryRepo.findProductById(productId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.addStock(productId, stockAmount));
        assertEquals("Product not found: " + productId, exception.getMessage());
        verify(inventoryRepo).findProductById(productId);
        verify(inventoryRepo, never()).addStock(anyString(), anyInt());
    }

    @Test
    void addStock_ExceedsMaxInteger_ThrowsIllegalArgumentException() {
        String productId = "1";
        Integer stockAmount = Integer.MAX_VALUE;
        product.setAvailableQty(Integer.MAX_VALUE - 1);
        when(inventoryRepo.findProductById(productId)).thenReturn(product);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addStock(productId, stockAmount));
        assertEquals("Stock addition would exceed maximum allowed quantity", exception.getMessage());
        verify(inventoryRepo).findProductById(productId);
        verify(inventoryRepo, never()).addStock(anyString(), anyInt());
    }

    @Test
    void decreaseStock_SuccessfulRemoval_ReturnsUpdatedProduct() {
        String productId = "1";
        Integer stockAmount = 5;
        Product updatedProduct = Product.builder()
                .productId("1")
                .productName("iPhone")
                .productDesc("iPhone 99")
                .availableQty(5)
                .enableLowStockThreshold(true)
                .lowStockThreshold(5)
                .build();
        when(inventoryRepo.findProductById(productId)).thenReturn(product);
        when(inventoryRepo.decreaseStock(productId, stockAmount)).thenReturn(updatedProduct);

        Product result = inventoryService.decreaseStock(productId, stockAmount);

        assertNotNull(result);
        assertEquals(5, result.getAvailableQty());
        verify(inventoryRepo).findProductById(productId);
        verify(inventoryRepo).decreaseStock(productId, stockAmount);
    }

    @Test
    void decreaseStock_NullProductId_ThrowsIllegalArgumentException() {
        String productId = null;
        Integer stockAmount = 5;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.decreaseStock(productId, stockAmount));
        assertEquals("Invalid product ID", exception.getMessage());
        verify(inventoryRepo, never()).findProductById(anyString());
        verify(inventoryRepo, never()).decreaseStock(anyString(), anyInt());
    }

    @Test
    void decreaseStock_EmptyProductId_ThrowsIllegalArgumentException() {
        String productId = "";
        Integer stockAmount = 5;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.decreaseStock(productId, stockAmount));
        assertEquals("Invalid product ID", exception.getMessage());
        verify(inventoryRepo, never()).findProductById(anyString());
        verify(inventoryRepo, never()).decreaseStock(anyString(), anyInt());
    }

    @Test
    void decreaseStock_NullStockAmount_ThrowsIllegalArgumentException() {
        String productId = "1";
        Integer stockAmount = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.decreaseStock(productId, stockAmount));
        assertEquals("Stock amount must be positive and non-null", exception.getMessage());
        verify(inventoryRepo, never()).findProductById(anyString());
        verify(inventoryRepo, never()).decreaseStock(anyString(), anyInt());
    }

    @Test
    void decreaseStock_NonPositiveStockAmount_ThrowsIllegalArgumentException() {
        String productId = "1";
        Integer stockAmount = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.decreaseStock(productId, stockAmount));
        assertEquals("Stock amount must be positive and non-null", exception.getMessage());
        verify(inventoryRepo, never()).findProductById(anyString());
        verify(inventoryRepo, never()).decreaseStock(anyString(), anyInt());
    }

    @Test
    void decreaseStock_ProductNotFound_ThrowsRuntimeException() {
        String productId = "1";
        Integer stockAmount = 5;
        when(inventoryRepo.findProductById(productId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> inventoryService.decreaseStock(productId, stockAmount));
        assertEquals("Product not found: " + productId, exception.getMessage());
        verify(inventoryRepo).findProductById(productId);
        verify(inventoryRepo, never()).decreaseStock(anyString(), anyInt());
    }

    @Test
    void decreaseStock_InsufficientStock_ThrowsIllegalArgumentException() {
        String productId = "1";
        Integer stockAmount = 15;
        when(inventoryRepo.findProductById(productId)).thenReturn(product);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.decreaseStock(productId, stockAmount));
        assertEquals("Insufficient stock available. Requested: 15, Available: 10", exception.getMessage());
        verify(inventoryRepo).findProductById(productId);
        verify(inventoryRepo, never()).decreaseStock(anyString(), anyInt());
    }
}