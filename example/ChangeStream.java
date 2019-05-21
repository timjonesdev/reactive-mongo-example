// uses Spring Boot 2, Spring Webflux, Reactive Mongo Repository
public class ChangeStream {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<SampleDao> watchChanges() {
        // set changestream options to watch for any changes to the sample collection
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(Aggregation.newAggregation(SampleDao.class,
                        Aggregation.match(
                                Criteria.where("operationType").is("replace")
                        )
                )).returnFullDocumentOnUpdate().build();

        // return a flux that watches the changestream and returns the full document
        return reactiveMongoTemplate.changeStream("samples", options, SampleDao.class)
                .map(ChangeStreamEvent::getBody)
                .doOnError(throwable -> log.error("Error with the samples changestream event: " + throwable.getMessage(), throwable));
    }
}
