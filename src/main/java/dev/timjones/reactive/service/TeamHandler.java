package dev.timjones.reactive.service;

import dev.timjones.reactive.data.model.Team;
import dev.timjones.reactive.data.repository.TeamRepository;
import dev.timjones.reactive.service.watch.TeamWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TeamHandler {

    private TeamRepository teamRepository;

    private TeamWatcher teamWatcher;

    @Autowired
    public TeamHandler(TeamRepository teamRepository, TeamWatcher teamWatcher) {
        this.teamRepository = teamRepository;
        this.teamWatcher = teamWatcher;
    }

    public Mono<ServerResponse> getTeams(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(this.teamRepository.findAll(), Team.class));
    }

    public Mono<ServerResponse> updatePlayerScore(ServerRequest request) {
        String playerName = request.pathVariable("name");
        String scoreChangeString = request.pathVariable("scoreChange");
        Double scoreChange = Double.valueOf(scoreChangeString);


        Mono<Team> teamMono = this.teamRepository.findDistinctByPlayerName(playerName)
                .log("find by player")
                .onErrorReturn(new Team())
                .map(team -> {
                    // find the correct player, and update the score
                    team.getPlayers().stream()
                            .filter(player -> player.getName().equals(playerName))
                            .forEach(player -> player.setScore(player.getScore() + scoreChange));
                    return team;
                })
                .flatMap(teamRepository::save);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(teamMono, Team.class);
    }

    public Mono<ServerResponse> watchTeam(ServerRequest request) {
        String teamName = request.pathVariable("name");

        Flux<ServerSentEvent<Team>> sse = this.teamWatcher.watchForTeamChanges(teamName)
                .map(team -> ServerSentEvent.<Team>builder()
                        .id(team.getId().toHexString())
                        .event("team-update")
                        .data(team)
                        .build());

        return ServerResponse.ok().body(BodyInserters.fromServerSentEvents(sse));
    }
}
