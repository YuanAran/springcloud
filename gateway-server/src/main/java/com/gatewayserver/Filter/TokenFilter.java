package com.gatewayserver.Filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenFilter implements GlobalFilter, Ordered {
    private final WebClient webClient;
    public TokenFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path=exchange.getRequest().getURI().getPath();
        //获取请求头中的token
        String token=exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println(token);
        //放行登录，注册接口
        if(path.startsWith("/auth/")){
            return chain.filter(exchange);
        }
        if (token == null || token.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/isTokenValid")
                        .queryParam("token", token) // token 放到 query 参数
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(valid -> {
                    if (valid) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
