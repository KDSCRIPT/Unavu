package com.unavu.notification.service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserFeignFallBack implements UserFeignClient {

    @Override
    public ResponseEntity<String> getUserEmail(String keycloakId) {
        return ResponseEntity.ok("");
    }
}
