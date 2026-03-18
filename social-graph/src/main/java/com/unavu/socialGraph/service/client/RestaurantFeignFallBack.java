package com.unavu.socialGraph.service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RestaurantFeignFallBack implements RestaurantFeignClient {
    @Override
    public Boolean doesRestaurantExist(Long restaurantId) {
        return false;
    }

    @Override
    public ResponseEntity<String> getRestaurantName(Long restaurantId) {
        return ResponseEntity.ok("");
    }
}
