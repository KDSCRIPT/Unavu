package com.unavu.notification.service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user",url="http://user:8082",path = "/api/v1")
public interface UserFeignClient {

    @GetMapping("/internal/users/{keycloakId}/email")
    ResponseEntity<String> getUserEmail(@PathVariable String keycloakId);
}