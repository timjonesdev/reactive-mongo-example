package dev.timjones.reactive.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .andRoute(RequestPredicates.GET("/update/{name}/{scoreChange}"), teamHandler::updatePlayerScore)
                .andRoute(RequestPredicates.GET("/team/{name}"), teamHandler::watchTeam);
    }
}
