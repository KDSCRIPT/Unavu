package com.unavu.gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class GatewayServerApplication {

    private static final Logger log = LoggerFactory.getLogger(GatewayServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Bean
    @Order(0)
    public GlobalFilter userHeaderFilter() {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
                .map(AbstractOAuth2TokenAuthenticationToken::getToken)
                .flatMap(token -> {
                    String userId = token != null ? token.getSubject() : "anonymous";
                    String username = token != null ? token.getClaim("name") : "anonymous";

                    var request = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
                            .header("X-Username", username)
                            .build();

                    var mutatedExchange = exchange.mutate()
                            .request(request)
                            .build();

                    return chain.filter(mutatedExchange);
                });
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("restaurants", r -> r
                        .path("/api/v1/restaurants/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("restaurantCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/restaurants")
                                )
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://RESTAURANT"))
                .route("reviews", r -> r
                        .path("/api/v1/reviews/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("reviewCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/reviews")
                                )
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://REVIEW"))
                .route("lists", r -> r
                        .path("/api/v1/lists/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("listCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/lists")
                                )
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://LIST"))
                .route("social-graph", r -> r
                        .path("/api/v1/social-graph/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("socialGraphCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/social-graph")
                                )
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://SOCIAL-GRAPH"))
                .route("users", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("userCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/users")
                                )
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://USER"))
                .route("notifications", r -> r
                        .path("/api/v1/notifications/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("notificationCircuitBreaker")
//                                        .setFallbackUri("forward:/fallback/notifications")
                                )
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                        )
                        .uri("lb://NOTIFICATION"))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1); // Increased from 1,1,1 — was likely throttling all requests
    }

    // -------------------------------------------------------------------------
    // KEY RESOLVER — with fallback to IP to avoid silent rate limiter failures
    // -------------------------------------------------------------------------
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange ->
                ReactiveSecurityContextHolder.getContext()
                        .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
                        .map(auth -> auth.getToken().getSubject())
                        .doOnNext(subject -> log.info("[RATE LIMITER] Resolved key: {}", subject))
                        .onErrorResume(e -> {
                            log.error("[RATE LIMITER] Error resolving key, falling back to IP: {}", e.getMessage());
                            String ip = exchange.getRequest().getRemoteAddress() != null
                                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                                    : "unknown";
                            return reactor.core.publisher.Mono.just(ip);
                        })
                        .switchIfEmpty(reactor.core.publisher.Mono.defer(() -> {
                            log.warn("[RATE LIMITER] Empty security context, falling back to IP");
                            String ip = exchange.getRequest().getRemoteAddress() != null
                                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                                    : "unknown";
                            return reactor.core.publisher.Mono.just(ip);
                        }));
    }
}