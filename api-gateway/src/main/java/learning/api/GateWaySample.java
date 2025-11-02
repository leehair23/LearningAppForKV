package learning.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.method;

@Configuration
public class GateWaySample {
    @Bean
    public RouterFunction<ServerResponse> gatewayRouterFunctionMethod(){
        return route("method_route")
                .route(method(HttpMethod.GET, HttpMethod.POST), http())
                .before(uri("https://exmample.org"))
                .build();
    }
}
