package com.unavu.notification.service.client;

import com.unavu.common.web.enums.EntityType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SocialGraphFeignFallBack implements SocialGraphFeignClient {
    @Override
    public ResponseEntity<List<String>> findFollowerIds(String targetId, EntityType entityType) {
        return ResponseEntity.ok(List.of());

    }
}
