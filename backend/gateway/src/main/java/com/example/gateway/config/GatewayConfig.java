package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    /**
     * Configure gateway routes.
     *
     * <p>Currently, we route all user service related requests to USERSERVICE, and
     * all chat service related requests to CHATSERVICE. All other requests are
     * routed to a default route, which returns a 404 status.
     *
     * @param builder the route builder
     * @return the route locator
     */
    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // Route all user service related requests to USERSERVICE
                .route("USERSERVICE", r -> r
                        .path("/api/v1/user/**")
                        .uri("lb://USERSERVICE"))

                // Route all chat service related requests to CHATSERVICE
                .route("CHATSERVICE", r -> r
                        .path("/api/v1/chat/**")
                        .uri("lb://USERSERVICE"))

                // Default route that returns a 404 status
                .route("DEFAULT_ROUTE", r -> r
                        .path("/**")
                        .filters(f -> f.setStatus(404))
                        .uri("no://op"))
                .build();
    }

}
