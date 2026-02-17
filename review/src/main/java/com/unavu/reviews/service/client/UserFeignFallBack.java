package com.unavu.reviews.service.client;

import org.springframework.stereotype.Component;

@Component
public class UserFeignFallBack implements UserFeignClient {
    @Override
    public Boolean doesUserExist(Long userId) {
        return false;
    }
}
