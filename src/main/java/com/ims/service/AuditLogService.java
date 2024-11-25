package com.ims.service;

import com.ims.entity.AuditLog;
import com.ims.entity.User;
import com.ims.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    //create a new audit log
    @Transactional
    public AuditLog createAuditLog(User user, String action) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setTimeStamp(LocalDateTime.now());

        return auditLogRepository.save(auditLog);
    }

    public void logEvent(String eventType, String action) {
        AuditLog log = new AuditLog();
        log.setEventType(eventType);
        log.setAction(action);
        log.setTimeStamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
