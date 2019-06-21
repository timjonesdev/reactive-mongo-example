package dev.timjones.reactive.service;

import dev.timjones.reactive.data.model.Player;
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

    /**
     * Return all teams from the fantasy_db.teams collection
     *
     * @param request - the request (unused in this operation)
     * @return a list of all teams
     */
    public Mono<ServerResponse> getTeams(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(this.teamRepository.findAll(), Team.class));
    }

    /**
     * Update the score for the given player name.
     *
     * @param request - must be of the form /{name}/{scoreChange}
     * @return the team that was updated
     */
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
                            .forEach(player -> {
                                player.setScore(player.getScore() + scoreChange);
                                team.setTotalScore(recalculateScore(team));
                            });
                    return team;
                })
                .flatMap(teamRepository::save);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(teamMono, Team.class);
    }

    /**
     * Subscribe to watch updates to a particular team
     *
     * @param request - must be of the form /{name}
     * @return a subscription to a Server Sent Event which
     * fires every time the requested team is updated in the fantasy_db.teams collection.
     * The subscription is watching a change stream in MongoDB
     */
    public Mono<ServerResponse> watchTeam(ServerRequest request) {
        String teamName = request.pathVariable("name");

        Flux<ServerSentEvent<Team>> sse = this.teamWatcher.watchForTeamChanges(teamName)
                .map(team -> ServerSentEvent.<Team>builder()
                        .data(team)
                        .build());

        return ServerResponse.ok().body(BodyInserters.fromServerSentEvents(sse));
    }

    public Mono<ServerResponse> watchTeams(ServerRequest request) {


        Flux<ServerSentEvent<Team>> sse = this.teamWatcher.watchForTeamCollectionChanges()
                .map(team -> ServerSentEvent.<Team>builder()
                        .data(team)
                        .build());

        return ServerResponse.ok().body(BodyInserters.fromServerSentEvents(sse));
    }

    private Double recalculateScore(Team team) {
        return team.getPlayers().stream()
                .mapToDouble(Player::getScore)
                .sum();
    }
}
