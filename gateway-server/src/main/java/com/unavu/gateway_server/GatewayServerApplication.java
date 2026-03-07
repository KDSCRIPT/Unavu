package com.unavu.gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@SpringBootApplication
public class GatewayServerApplication {

    private static final Logger log = LoggerFactory.getLogger(GatewayServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    // -------------------------------------------------------------------------
    // DEBUG FILTER — runs first, logs raw incoming request
    // Remove this once the issue is resolved
    // -------------------------------------------------------------------------
    @Bean
    @Order(-2)
    public GlobalFilter debugIncomingRequestFilter() {
        return (exchange, chain) -> {
            log.info("========================================");
            log.info("[DEBUG] Incoming request: {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
            log.info("[DEBUG] Authorization header present: {}", exchange.getRequest().getHeaders().containsKey("Authorization"));

            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null) {
                // Log only the first 40 chars to avoid exposing the full token in logs
                log.info("[DEBUG] Authorization header (truncated): {}...", authHeader.substring(0, Math.min(40, authHeader.length())));
            } else {
                log.warn("[DEBUG] NO Authorization header found — JWT will not be parsed!");
            }

            log.info("[DEBUG] All headers: {}", exchange.getRequest().getHeaders().toSingleValueMap());
            log.info("========================================");

            return chain.filter(exchange);
        };
    }

    // -------------------------------------------------------------------------
    // DEBUG FILTER — runs after security, logs security context state
    // Remove this once the issue is resolved
    // -------------------------------------------------------------------------
    @Bean
    @Order(-1)
    public GlobalFilter debugSecurityContextFilter() {
        return (exchange, chain) -> {
            return ReactiveSecurityContextHolder.getContext()
                    .doOnNext(ctx -> {
                        log.info("[DEBUG] Security context found: {}", ctx);
                        log.info("[DEBUG] Authentication type: {}", ctx.getAuthentication() != null ? ctx.getAuthentication().getClass().getSimpleName() : "null");
                        log.info("[DEBUG] Is authenticated: {}", ctx.getAuthentication() != null && ctx.getAuthentication().isAuthenticated());
                        if (ctx.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
                            log.info("[DEBUG] JWT subject: {}", jwtAuth.getToken().getSubject());
                            log.info("[DEBUG] JWT authorities: {}", jwtAuth.getAuthorities());
                        }
                    })
                    .then(chain.filter(exchange));
        };
    }

    // -------------------------------------------------------------------------
    // USER HEADER FILTER — injects X-User-Id for downstream services
    // -------------------------------------------------------------------------
    @Bean
    @Order(0)
    public GlobalFilter userHeaderFilter() {
        return (exchange, chain) -> {
            log.info("[FILTER] userHeaderFilter triggered for: {}", exchange.getRequest().getURI());

            return ReactiveSecurityContextHolder.getContext()
                    .doOnNext(ctx -> log.info("[FILTER] Security context obtained"))
                    .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
                    .doOnNext(auth -> log.info("[FILTER] JWT subject: {}", auth.getToken().getSubject()))
                    .map(auth -> auth.getToken().getSubject())
                    .onErrorResume(e -> {
                        // Handles ClassCastException or any other error
                        log.error("[FILTER] Error extracting JWT subject: {}", e.getMessage());
                        return reactor.core.publisher.Mono.just("anonymous");
                    })
                    .defaultIfEmpty("anonymous")
                    .flatMap(userId -> {
                        if ("anonymous".equals(userId)) {
                            log.warn("[FILTER] No authenticated user found — injecting X-User-Id: anonymous");
                        } else {
                            log.info("[FILTER] Injecting X-User-Id: {}", userId);
                        }

                        var request = exchange.getRequest()
                                .mutate()
                                .header("X-User-Id", userId)
                                .build();

                        var mutatedExchange = exchange.mutate()
                                .request(request)
                                .build();

                        return chain.filter(mutatedExchange);
                    });
        };
    }

    // -------------------------------------------------------------------------
    // ROUTES
    // -------------------------------------------------------------------------
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
                .build();
    }

    // -------------------------------------------------------------------------
    // RATE LIMITER
    // -------------------------------------------------------------------------
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