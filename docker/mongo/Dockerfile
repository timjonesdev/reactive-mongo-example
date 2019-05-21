FROM mongo:4.0

# files used to initialize the database
COPY ./init-db.d/ /docker-entrypoint-initdb.d

# add this command to a js file in the init directory
RUN echo "rs.initiate({_id: 'rs0', members: [{_id: 0, host: 'localhost:27017'}]}); rs.slaveOk();" > /docker-entrypoint-initdb.d/replica-init.js

# the important bits are calling out the replicaset, binding the IP to 0.0.0.0, and setting the oplogSize to something manageable
# these settings are for local development only, and would likely be significantly different in a deployment
CMD ["--replSet", "rs0", "--bind_ip", "0.0.0.0", "--oplogSize", "100"]