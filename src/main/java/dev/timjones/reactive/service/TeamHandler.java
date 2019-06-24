package dev.timjones.reactive.service;

import dev.timjones.reactive.data.model.Player;
import dev.timjones.reactive.data.model.Team;
import dev.timjones.reactive.data.repository.TeamRepository;
import dev.timjones.reactive.service.watch.TeamWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.Random;

@Slf4j
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


        Mono<Team> teamMono = this.updateTeam(playerName, scoreChange);

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

    /**
     * Subscribe to watch changes for any team in the collection
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> watchTeams(ServerRequest request) {

        Flux<ServerSentEvent<Team>> sse = this.teamWatcher.watchForTeamCollectionChanges()
                .map(team -> ServerSentEvent.<Team>builder()
                        .data(team)
                        .build());

        return ServerResponse.ok().body(BodyInserters.fromServerSentEvents(sse));
    }

    /**
     * Helper method to zero out the scores of all players for a clean UI.
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> allZero(ServerRequest request) {
        return ServerResponse.ok().body(this.zeroPlayers(), Team.class);
    }


    /**
     * Randomize score updates. Helper function to kick off streams to the UI.
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> randomizeScore(ServerRequest request) {
        String countString = request.pathVariable("count");
        int count = Integer.valueOf(countString);
        
        if (count < 0 || count > 40) {
            return ServerResponse.badRequest().body(BodyInserters.fromObject("Count must be between 0 and 40"));
        }

        Flux<String> playerNames = this.teamRepository.findAll()
                .map(Team::getPlayers)
                .map(players -> players.stream()
                        .map(Player::getName))
                .flatMap(Flux::fromStream)
                .collectList()
                .map(list -> {
                    while (list.size() < count) {
                        // Double the size of the list until it's bigger than the count
                        list.addAll(list);
                    }
                    Collections.shuffle(list);
                    return list;
                })
                .flatMapMany(Flux::fromIterable);

        Flux<Double> doubleFlux = Flux.interval(Duration.ofMillis(1000))
                .map(pulse -> this.randomDouble())
                .take(count);

        Flux<Team> updateFlux = Flux.zip(doubleFlux, playerNames)
                .flatMap(objects -> {
                    Double scoreChange = objects.getT1();
                    String name = objects.getT2();
                    return this.updateTeam(name, scoreChange);
                });

        return ServerResponse.ok().body(BodyInserters.fromPublisher(updateFlux, Team.class));
    }

    /**
     * Helper function to tally total score based on individual player scores
     *
     * @param team - the team to sum scores for
     * @return the total score
     */
    private Double recalculateScore(Team team) {
        return team.getPlayers().stream()
                .mapToDouble(Player::getScore)
                .sum();
    }

    private Double randomDouble() {
        Random random = new Random();
        return random.doubles(1L, -2.0, 10.0).sum();
    }

    private Integer randomInteger(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    private Mono<Team> updateTeam(String playerName, Double scoreChange) {
        log.info("Player: " + playerName + ", Score Change: " + scoreChange);
        return this.teamRepository.findDistinctByPlayerName(playerName)
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
    }

    private Flux<Team> zeroPlayers() {
        return this.teamRepository.findAll()
                .map(team -> {
                    team.getPlayers()
                            .parallelStream()
                            .forEach(player -> player.setScore(0.0));
                    team.setTotalScore(this.recalculateScore(team));
                    return team;
                })
                .flatMap(teamRepository::save);
    }
}
