package com.unavu.socialGraph.service.client;

import org.springframework.stereotype.Component;

@Component
public class UserFeignFallBack implements UserFeignClient {
    @Override
    public Boolean userWithKeycloakIdExists(String keycloakId) {
        return false;
    }
}
