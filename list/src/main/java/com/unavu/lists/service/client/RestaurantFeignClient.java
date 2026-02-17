package com.unavu.lists.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant",path="/api/v1",fallback = RestaurantFeignFallBack.class)
public interface RestaurantFeignClient {
    @GetMapping("/internal/restaurants/{restaurantId}/exists")
    Boolean doesRestaurantExist(@PathVariable("restaurantId") Long restaurantId);
}
