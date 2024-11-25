package com.ims.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


@Service
public class EmailNotificationService {
    @Autowired
    private JavaMailSender javaMailSender;

    private static final Logger logger =  LoggerFactory.getLogger(EmailNotificationService.class);

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void sendLowStockAlert(Long productId , int stockQuantity) {
  try{
      String recipientEmail = "kanukuntlaprashanth1@gmail.com";
      String subject = "Low Stock Alert: Product ID " + productId;
      String message = "The stock for Product ID " + productId + " has dropped to " + stockQuantity + " units.";

      SimpleMailMessage email = new SimpleMailMessage();
      email.setTo(recipientEmail);
      email.setSubject(subject);
      email.setText(message);
      email.setFrom("kanukuntlaprashanth1@gmail.com");
      javaMailSender.send(email);
      logger.info("Low stock alert email sent for Product ID: " +productId);
  } catch (Exception e) {

      logger.info("Failed to send low stock alert email: "+e.getMessage());
  }
  }
}
