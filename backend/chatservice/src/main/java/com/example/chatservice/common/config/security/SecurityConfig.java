package com.example.chatservice.common.config.security;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.shaded.gson.JsonParseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationManager jwtAuthenticationManager;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Stateless
            .exceptionHandling(handle -> handle.authenticationEntryPoint(serverAuthenticationEntryPoint()))
            // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authenticationManager(jwtAuthenticationManager)
            .addFilterBefore(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange(exchange -> exchange
                .anyExchange().authenticated()
            )
            .build();
    }

    // @Bean
    // CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     configuration.setAllowedOrigins(List.of("http://127.0.0.1:8080", "http://127.0.0.1:3000", "http://localhost:8080", "http://localhost:3000"));
    //     configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    //     configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    //     configuration.setAllowCredentials(true);
        
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }

    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter()); // JWT 변환
        return authenticationWebFilter;
    }

    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint(){
        return (exchange, authEx) -> {
            String requestPath = exchange.getRequest().getPath().value();

            log.debug("Requested path    : {}", requestPath);
            log.debug("Unauthorized error: {}", authEx.getMessage());

            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            Map<String, Object> errorResponse = Map.of(
                "message", "Unauthorized",
                "errorCode", "UNAUTHORIZED",
                "timestamp", LocalDateTime.now()
            );

            try {
                String jsonMessage = objectMapper.writeValueAsString(errorResponse);
                return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonMessage.getBytes())));
            } catch (Exception e) {
                log.error("Error writing response: ", e);
                return Mono.error(new JsonParseException("Failed to write error response", e));
            }
        };
    }

}
