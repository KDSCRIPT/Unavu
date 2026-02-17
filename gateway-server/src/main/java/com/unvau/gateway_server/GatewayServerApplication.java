package com.unvau.gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;

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
                        .filters(f->f
                                .circuitBreaker(config -> config
                                        .setName("restaurantCircuitBreaker")
                                                .setFallbackUri("forward:/fallback/restaurants")
                                ).retry(retryConfig -> retryConfig.setRetries(3)//.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                                )
                        .uri("lb://RESTAURANT"))
                .route("reviews", r -> r
                        .path("/api/v1/reviews/**")
                        .filters(f->f
                                .circuitBreaker(config -> config
                                        .setName("reviewCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/reviews")
                                ).retry(retryConfig -> retryConfig.setRetries(3)//.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://REVIEW"))
                .route("lists", r -> r
                        .path("/api/v1/lists/**")
                        .filters(f->f
                                .circuitBreaker(config -> config
                                        .setName("listCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/lists")
                                ).retry(retryConfig -> retryConfig.setRetries(3)//.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://LIST"))
                .route("social-graph", r -> r
                        .path("/api/v1/social-graph/**")
                        .filters(f->f
                                .circuitBreaker(config -> config
                                        .setName("socialGraphCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/social-graph")
                                ).retry(retryConfig -> retryConfig.setRetries(3)//.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://SOCIAL-GRAPH"))
                .route("users", r -> r
                        .path("/api/v1/users/**")
                        .filters(f->f
                                .circuitBreaker(config -> config
                                        .setName("userCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/users")
                                ).retry(retryConfig -> retryConfig.setRetries(3)//.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://USER"))
                .build();
    }
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
                .defaultIfEmpty("anonymous");
    }


}
