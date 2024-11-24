package com.ims.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FailedOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private int quantity;
    private String errorMessage;
    private LocalDateTime timestamp;
    private int retryCount;
    private LocalDateTime lastRetry;

    public FailedOperation(Long productId, int quantity, String errorMessage) {
        this.productId = productId;
        this.quantity = quantity;
        this.errorMessage = errorMessage;
        this.timestamp = LocalDateTime.now();
        this.retryCount = 0;
        this.lastRetry = null;
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.lastRetry = LocalDateTime.now();
    }
}
