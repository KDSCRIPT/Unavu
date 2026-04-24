package com.unavu.feed.service.client;

import com.unavu.common.web.enums.EntityType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "social-graph",url="http://social-graph:8083",path = "/api/v1")
public interface SocialGraphFeignClient {

    @GetMapping("/internal/social-graph/followers/{targetId}")
    ResponseEntity<List<String>> findFollowerIds(
            @PathVariable("targetId") String targetId,
            @RequestParam(name = "entityType", defaultValue = "USER") EntityType entityType
    );
}