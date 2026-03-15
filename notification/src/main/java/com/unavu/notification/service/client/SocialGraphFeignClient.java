package com.unavu.notification.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "social-graph",path="/api/v1")
public interface SocialGraphFeignClient {
    @GetMapping(value="/internal/social-graph/followers/{userId}")
    ResponseEntity<List<String>> findFollowerIds(@PathVariable String userId);
}
