package com.ims.controller;

import com.ims.entity.Inventory;
import com.ims.exception.ProductNotFoundException;
import com.ims.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventors")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Inventory> addInventory(@PathVariable Long productId, @RequestParam Integer quantity) throws ProductNotFoundException {
        Inventory inventory = inventoryService.addInventory(productId, quantity);
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Long productId, @RequestParam Integer quantity) {
        Inventory inventory = inventoryService.updateInventory(productId, quantity);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/{productId}/low-stock")
    public ResponseEntity<Boolean> checkLowStock(@PathVariable Long productId) {
        boolean isLowStock = inventoryService.isLowStock(productId);
        return ResponseEntity.ok(isLowStock);
    }
}
