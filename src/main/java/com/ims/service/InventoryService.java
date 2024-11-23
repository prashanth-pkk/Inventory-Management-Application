package com.ims.service;

import com.ims.entity.Inventory;
import com.ims.entity.Product;
import com.ims.repository.InventoryRepository;
import com.ims.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Inventory addInventory(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("The product is not found"));
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
