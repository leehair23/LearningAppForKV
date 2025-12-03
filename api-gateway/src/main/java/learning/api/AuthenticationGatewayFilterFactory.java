package learning.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationGatewayFilterFactory.class);

    private final ReactiveJwtDecoder jwtDecoder;

    public AuthenticationGatewayFilterFactory(ReactiveJwtDecoder jwtDecoder) {
        super(Config.class);
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("requiredRole");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
                return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7).trim();

            return jwtDecoder.decode(token)
                    .flatMap(jwt -> handleValidJwt(exchange, chain, jwt, config))
                    .onErrorResume(ex -> {
                        log.warn("JWT validation failed: {}", ex.getMessage());
                        return onError(exchange, "Unauthorized Access", HttpStatus.UNAUTHORIZED);
                    });
        };
    }

    private Mono<Void> handleValidJwt(ServerWebExchange exchange, GatewayFilterChain chain, Jwt jwt, Config config) {
        String username = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String role = jwt.getClaimAsString("role");

        log.debug("User: {}, Role: {}", username, role);

        if (config.getRequiredRole() != null) {
            if (role == null || !role.equals(config.getRequiredRole())) {
                log.warn("Access Denied for user: {}. Required: {}, Actual: {}", username, config.getRequiredRole(), role);
                return onError(exchange, "Forbidden: You don't have permission", HttpStatus.FORBIDDEN);
            }
        }

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-Auth-User", username != null ? username : "")
                .header("X-Auth-Email", email != null ? email : "")
                .header("X-Auth-Role", role != null ? role : "USER")
                .build();

        ServerWebExchange mutated = exchange.mutate().request(request).build();
        return chain.filter(mutated);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        private String requiredRole;

        public String getRequiredRole() {
            return requiredRole;
        }

        public void setRequiredRole(String requiredRole) {
            this.requiredRole = requiredRole;
        }
    }
}