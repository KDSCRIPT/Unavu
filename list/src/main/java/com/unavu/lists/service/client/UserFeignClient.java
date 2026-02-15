package com.unavu.lists.service.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user",path="/api/v1")
public interface UserFeignClient {

    @GetMapping("/internal/users/{userId}/exists")
    Boolean doesUserExist(@PathVariable("userId") Long userId);
}
