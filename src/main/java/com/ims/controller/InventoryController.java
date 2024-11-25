package com.ims.controller;

import com.ims.entity.Inventory;
import com.ims.entity.InventoryRequest;
import com.ims.exception.ProductNotFoundException;
import com.ims.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public String updateInventory(@PathVariable Long productId, @RequestParam Integer quantity) {
        inventoryService.updateInventory(productId, quantity);
        return "Inventory update process initiated for product " + productId;
    }

    @GetMapping("/{productId}/low-stock")
    public ResponseEntity<Boolean> checkLowStock(@PathVariable Long productId) {
        boolean isLowStock = inventoryService.isLowStock(productId);
        return ResponseEntity.ok(isLowStock);
    }

    @PostMapping("/reduce")
    public ResponseEntity<String> reduceStock(@RequestBody InventoryRequest request) {
        try {
            // Call service to reduce stock
            inventoryService.reduceStock(request.getProductId(), request.getQuantity());

            // Return success message
            return ResponseEntity.ok("Inventory update process initiated for product " + request.getProductId());
        } catch (Exception e) {
            // Return error message in case of failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
