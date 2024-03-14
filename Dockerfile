FROM gradle:jdk17 AS build

ENV APP_HOME=/task-tracker-api-app
WORKDIR $APP_HOME
COPY /src $APP_HOME/src
COPY build.gradle.kts settings.gradle.kts gradlew $APP_HOME/
COPY gradle $APP_HOME/gradle
RUN ./gradlew build --no-daemon

FROM openjdk:17-jdk-slim

ENV APP_HOME=/task-tracker-api-app
WORKDIR $APP_HOME
COPY --from=build $APP_HOME/build/libs/*SNAPSHOT.jar task-tracker-api.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "task-tracker-api.jar.jar"]