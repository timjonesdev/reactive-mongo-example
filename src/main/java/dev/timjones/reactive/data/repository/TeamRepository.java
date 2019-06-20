package dev.timjones.reactive.data.repository;

import dev.timjones.reactive.data.model.Team;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TeamRepository extends ReactiveMongoRepository<Team, ObjectId> {

    @Query(value = "{ 'players.name' : ?0 }")
    Mono<Team> findDistinctByPlayerName(String playerName);
}
