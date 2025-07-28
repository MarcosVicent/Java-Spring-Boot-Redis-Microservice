package com.example.redis.service;

import com.example.redis.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, String, Product> hashOperations;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void testSaveProduct() {
        Product product = new Product("1", "Test Product", 100.0);
        productService.save(product);

        verify(hashOperations, times(1)).put("PRODUCT", product.getId(), product);
    }

    @Test
    void testFindProductById() {
        Product product = new Product("2", "Another Product", 200.0);
        when(hashOperations.get("PRODUCT", product.getId())).thenReturn(product);

        Optional<Product> foundProduct = productService.findById(product.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals(product.getName(), foundProduct.get().getName());
        verify(hashOperations, times(1)).get("PRODUCT", product.getId());
    }

    @Test
    void testFindProductByIdNotFound() {
        String nonExistentId = "99";
        when(hashOperations.get("PRODUCT", nonExistentId)).thenReturn(null);

        Optional<Product> foundProduct = productService.findById(nonExistentId);

        assertFalse(foundProduct.isPresent());
        verify(hashOperations, times(1)).get("PRODUCT", nonExistentId);
    }

    @Test
    void testDeleteProduct() {
        String productId = "3";
        productService.delete(productId);

        verify(hashOperations, times(1)).delete("PRODUCT", productId);
    }
}