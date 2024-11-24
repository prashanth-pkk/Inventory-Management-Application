package com.ims.service;

import com.ims.entity.FailedOperation;
import com.ims.entity.Inventory;
import com.ims.entity.Product;
import com.ims.exception.ProductNotFoundException;
import com.ims.repository.FailedOperationRepository;
import com.ims.repository.InventoryRepository;
import com.ims.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


@Service
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private InventoryRepository inventoryRepository;
    private ProductRepository productRepository;
    private final FailedOperationRepository failedOperationRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository, FailedOperationRepository failedOperationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.failedOperationRepository = failedOperationRepository;
    }

    // add inventory for a product
    @Transactional
    public Inventory addInventory(Long productId, Integer quantity) throws ProductNotFoundException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found"));
        Inventory inventory = inventoryRepository.findByProductId(productId);

        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setQuantity(quantity);
            inventory.setLowStockThreshold(10);
        } else {
            inventory.setQuantity(inventory.getQuantity() + quantity);
        }
        return inventoryRepository.save(inventory);
    }


    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 1.5)
    )
    // update inventory quantity
    @Transactional
    public Inventory updateInventory(Long productId, Integer quantity) {
        logger.info("Attempting to update inventory...");
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (Math.random() > 0.5) {
            throw new RuntimeException("Transient error occurred while updating inventory");
        }
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

    @Recover
    public void recoverFromFailure(RuntimeException e, Long productId, int quantity) {
        logger.info("Failed to update inventory after retries for product: {}. Quantity: {}", productId, quantity, e);
        sendFailureNotification(productId, quantity, e.getMessage());
        saveFailedOperation(productId, quantity, e.getMessage());
    }

    // Simulated method to send notifications
    private void sendFailureNotification(Long productId, int quantity, String errorMessage) {
        logger.info("NOTIFY ADMIN: Inventory update failed for Product ID: ", productId, quantity, errorMessage);

    }

    // Simulated method to save the failed operation for later retry
    private void saveFailedOperation(Long productId, int quantity, String errorMessage) {
        logger.info("Saving failed operation to database...");
        logger.info("Product ID: "+productId+", Quantity: "+quantity+", Error: "+errorMessage);
        failedOperationRepository.save(new FailedOperation(productId, quantity, errorMessage));
        logger.info("Failed operation saved for manual intervention.");
    }

}
