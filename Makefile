# ----------------------------------------------------------
# Setup
# ----------------------------------------------------------
DOCKER_COMPOSE_FILE="./docker/docker-compose.yml"
DOCKER_COMPOSE=docker-compose -f $(DOCKER_COMPOSE_FILE)

# ----------------------------------------------------------
# Main Commands
# ----------------------------------------------------------
all:	up-db-local

up-db-local:	rebuild-mongo
	$(DOCKER_COMPOSE) up -d mongo

rebuild-mongo:
	$(DOCKER_COMPOSE) build mongo-seed &&\
	$(DOCKER_COMPOSE) up mongo-seed

# Alternate main commands if you have pushd/popd available, for reference
all-alt:	up-db-local-alt

up-db-local-alt:	rebuild-mongo-alt
	pushd docker &&\
	docker-compose up -d mongo &&\
	popd

rebuild-mongo-alt:
	pushd docker &&\
	docker-compose build mongo-seed &&\
	docker-compose up mongo-seed &&\
	popd

# ----------------------------------------------------------
# Cleanup Helper Commands
# ----------------------------------------------------------
clean:
	$(DOCKER_COMPOSE) down
	docker volume rm docker_mongodata

# ----------------------------------------------------------
# Container Helper Commands
# ----------------------------------------------------------

# exec into mongo container
mongo-terminal:
	docker exec -it docker_mongo_1 mongo
	
# tail the logs for the mongo instance
mongo-logs:
	docker logs -f docker_mongo_1