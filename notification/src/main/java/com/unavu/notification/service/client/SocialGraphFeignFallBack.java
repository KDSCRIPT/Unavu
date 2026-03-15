package com.unavu.notification.service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SocialGraphFeignFallBack implements SocialGraphFeignClient {
    @Override
    public ResponseEntity<List<String>> findFollowerIds(String userId) {
        return ResponseEntity.ok(List.of());
    }
}
