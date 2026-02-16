package com.unvau.gateway_server.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ResponseTimeFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long start=System.currentTimeMillis();

        return chain.filter(exchange)
                .then(Mono.fromRunnable(()->{
                    long duration=System.currentTimeMillis()-start;
                    exchange.getResponse().getHeaders().add("X-Response-Time",duration+"ms");
        }));
    }
}
