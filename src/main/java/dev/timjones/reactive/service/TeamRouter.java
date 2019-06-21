package dev.timjones.reactive.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TeamRouter {

    @Bean
    public RouterFunction<ServerResponse> route(TeamHandler teamHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/teams"), teamHandler::getTeams)
                .andRoute(RequestPredicates.GET("/teams/watch"), teamHandler::watchTeams)
                .andRoute(RequestPredicates.GET("/update/{name}/{scoreChange}"), teamHandler::updatePlayerScore)
                .andRoute(RequestPredicates.GET("/team/{name}"), teamHandler::watchTeam);
    }

    @Bean
    CorsWebFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        // Possibly...
        // config.applyPermitDefaultValues()

        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
