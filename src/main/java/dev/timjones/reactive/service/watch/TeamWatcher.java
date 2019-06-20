package dev.timjones.reactive.service.watch;

import dev.timjones.reactive.data.model.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TeamWatcher {

    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    public TeamWatcher(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    /**
     * Watch the MongoDB change stream for updates to the fantasy_db.teams collection
     *
     * @param teamName - the name of the team to watch
     * @return a subscription to the change stream
     */
    public Flux<Team> watchForTeamChanges(String teamName) {
        // set changestream options to watch for any changes to the businesses collection
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(Aggregation.newAggregation(Team.class,
                        Aggregation.match(
                                Criteria.where("operationType").is("replace")
                        )
                )).returnFullDocumentOnUpdate().build();

        // return a flux that watches the changestream and returns the full document
        return reactiveMongoTemplate.changeStream("teams", options, Team.class)
                .map(ChangeStreamEvent::getBody)
                .filter(team -> team.getName().equals(teamName))
                .doOnError(throwable -> log.error("Error with the teams changestream event: " + throwable.getMessage(), throwable));

    }
}
