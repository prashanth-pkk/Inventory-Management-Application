package com.ims.service;

import com.ims.entity.FailedOperation;
import com.ims.repository.FailedOperationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetryScheduler {
    private static final Logger logger = LoggerFactory.getLogger(RetryScheduler.class);
    private final FailedOperationRepository failedOperationRepository;
    private final InventoryService inventoryService;

    @Autowired
    public RetryScheduler(FailedOperationRepository failedOperationRepository, InventoryService inventoryService) {
        this.failedOperationRepository = failedOperationRepository;
        this.inventoryService = inventoryService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void retryFailedOperations() {
        List<FailedOperation> failedOperations = failedOperationRepository.findAll();
        for (FailedOperation foperation : failedOperations) {
            try {

                logger.info("Retrying operation for Product ID: {}, Quantity: {}", foperation.getProductId(), foperation.getQuantity());
                inventoryService.updateInventory(foperation.getProductId(), foperation.getQuantity());
                failedOperationRepository.delete(foperation);
            } catch (Exception e) {
                logger.info("Retry failed for Product ID: {}. Error: {}", foperation.getProductId(), e.getMessage());
                foperation.incrementRetryCount();
                failedOperationRepository.save(foperation);
            }
        }
    }
}
