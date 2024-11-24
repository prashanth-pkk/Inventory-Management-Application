package com.ims.service;

import com.ims.entity.Inventory;
import com.ims.entity.Product;
import com.ims.exception.ProductNotFoundException;
import com.ims.repository.InventoryRepository;
import com.ims.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private InventoryRepository inventoryRepository;
    private ProductRepository productRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    // add inventory for a product
    @Transactional
    public Inventory addInventory(Long productId, Integer quantity) throws ProductNotFoundException {
        // Check if quantity is valid
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Retrieve the product, throw a custom exception if not found
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found"));

        // Find existing inventory record for the product
        Inventory inventory = inventoryRepository.findByProductId(productId);

        // If no inventory exists for this product, create a new one
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setQuantity(quantity);
            inventory.setLowStockThreshold(10);  // You might want to get this from a config or another source
        } else {
            // If inventory exists, add the quantity to the existing inventory
            inventory.setQuantity(inventory.getQuantity() + quantity);
        }

        // Save and return the inventory object
        return inventoryRepository.save(inventory);
    }


    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    // update inventory quantity
    @Transactional
    public Inventory updateInventory(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (inventory == null) {
            throw new RuntimeException("Inventory not found for the given product");
        }
        inventory.setQuantity(quantity);
        return inventoryRepository.save(inventory);
    }

    public boolean isLowStock(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        return inventory != null && inventory.getQuantity() <= inventory.getLowStockThreshold();
    }

}
