package com.gatewayserver.Filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path=exchange.getRequest().getURI().getPath();
        //获取请求头中的token
        String token=exchange.getRequest().getHeaders().getFirst("Authorization");
        //放行登录，注册接口
        if(path.startsWith("/auth/")){
            return chain.filter(exchange);
        }
        if(token==null ||token.isEmpty()){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        //只检查是否存在token，不检查token的合法性，交给auth模块检查
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
