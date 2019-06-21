####################
### Java Builder ###
####################
FROM maven:3.6.0-jdk-8 as java-builder

RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build

RUN mvn -B dependency:resolve dependency:resolve-plugins

# copy all but Angular source in
COPY src/main/java /build/src/main/java
COPY src/main/resources /build/src/main/resources
COPY src/test /build/src/test

RUN mvn package

####################
### Java Runtime ###
####################
FROM openjdk:8-jdk-slim as runtime

ENV APP_HOME /app
ENV JAVA_OPTS=""

RUN mkdir $APP_HOME &&\
    mkdir $APP_HOME/config &&\
    mkdir $APP_HOME/log

WORKDIR $APP_HOME

RUN echo $JAVA_OPTS

COPY --from=java-builder /build/target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]