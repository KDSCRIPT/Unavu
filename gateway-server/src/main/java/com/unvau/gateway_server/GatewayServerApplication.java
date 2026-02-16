package com.unvau.gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServerApplication.class, args);
	}

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("restaurants", r -> r
                        .path("/api/v1/restaurants/**")
                        .uri("lb://RESTAURANT"))
                .route("reviews", r -> r
                        .path("/api/v1/reviews/**")
                        .uri("lb://REVIEW"))
                .route("lists", r -> r
                        .path("/api/v1/lists/**")
                        .uri("lb://LIST"))
                .route("social-graph", r -> r
                        .path("/api/v1/social-graph/**")
                        .uri("lb://SOCIAL-GRAPH"))
                .route("users", r -> r
                        .path("/api/v1/users/**")
                        .uri("lb://USER"))
                .build();
    }


}
