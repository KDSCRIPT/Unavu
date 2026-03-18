package com.unavu.gateway_server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public ResponseEntity<String> usersFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User service is currently unavailable.");
    }

    @GetMapping("/restaurants")
    public ResponseEntity<String> restaurantsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Restaurant service is currently unavailable.");
    }

    @GetMapping("/reviews")
    public ResponseEntity<String> reviewsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Review service is currently unavailable.");
    }

    @GetMapping("/social-graph")
    public ResponseEntity<String> socialFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Social Graph service is currently unavailable.");
    }

    @GetMapping("/lists")
    public ResponseEntity<String> listsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("List service is currently unavailable.");
    }

    @GetMapping("/notifications")
    public ResponseEntity<String> notificationsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Notification service is currently unavailable.");
    }

    @GetMapping("/feed")
    public ResponseEntity<String> feedFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Feed service is currently unavailable.");
    }

    @GetMapping("/activity")
    public ResponseEntity<String> activityFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Activity service is currently unavailable.");
    }
}

