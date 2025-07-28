package com.example.redis.service;

import com.example.redis.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private static final String PRODUCT_CACHE_KEY = "PRODUCT";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Product> hashOperations;

    @Autowired
    public ProductService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * @param product
     */
    public void save(Product product) {
        hashOperations.put(PRODUCT_CACHE_KEY, product.getId(), product);
        System.out.println("Produto salvo no Redis: " + product.getId());
    }

    /**
     * @param id
     * @return
     */
    public Optional<Product> findById(String id) {
        Product product = hashOperations.get(PRODUCT_CACHE_KEY, id);
        if (product != null) {
            System.out.println("Produto recuperado no Redis: " + product.getId());
            return Optional.of(product);
        }
        System.out.println("Produto não encontrado no Redis: " + id);
        return Optional.empty();
    }

    /**
     * @param id
     */
    public void delete(String id) {
        Long deletedCount = hashOperations.delete(PRODUCT_CACHE_KEY, id);
        if (deletedCount > 0) {
            System.out.println("Produto excluido do Redis: " + id);
        } else {
            System.out.println("Produto não encontrado para exclusão no Redis: " + id);
        }
    }
}