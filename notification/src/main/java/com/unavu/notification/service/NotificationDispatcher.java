package com.unavu.notification.service;

import com.unavu.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationDispatcher {

    private final JavaMailSender mailSender;

    public void sendPush(Notification notification) {

        log.info("Sending PUSH notification to user {} : {}",
                notification.getEntityId(),
                notification.getMessage());
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendRealtime(Notification notification) {

        log.info("Sending WEBSOCKET notification : {}", notification);
    }

}