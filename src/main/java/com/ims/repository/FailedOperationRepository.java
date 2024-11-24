package com.ims.repository;

import com.ims.entity.FailedOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedOperationRepository extends JpaRepository<FailedOperation, Long> {
    // Custom query using @Query
    @Query("SELECT f FROM FailedOperation f WHERE f.retryCount < :maxRetries")
    List<FailedOperation> findEligibleForRetry(@Param("maxRetries") int maxRetries);
}
