package com.unavu.reviews.service.client;

import org.springframework.stereotype.Component;

@Component
public class RestaurantFeignFallBack implements RestaurantFeignClient {
    @Override
    public Boolean doesRestaurantExist(Long restaurantId) {
        return false;
    }
}
