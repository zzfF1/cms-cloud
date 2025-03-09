package com.sinosoft.gateway.filter;

import com.sinosoft.gateway.utils.WebFluxUtils;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 全局缓存获取body请求数据（解决流不能重复读取问题）
 *
 * @author: zzf
 * @create: 2025-02-12 10:05
 */
@Component
public class WebCacheRequestFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 只缓存json类型请求
        if (!WebFluxUtils.isJsonRequest(exchange)) {
            return chain.filter(exchange);
        }
        return ServerWebExchangeUtils.cacheRequestBody(exchange, (serverHttpRequest) -> {
            if (serverHttpRequest == exchange.getRequest()) {
                return chain.filter(exchange);
            }
            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
