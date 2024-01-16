FROM ghcr.io/navikt/baseimages/temurin:21

COPY init.sh /init-scripts/init.sh

ENV JAVA_OPTS="${JAVA_OPTS} -Xms270M -XX:MaxRAMPercentage=75"

COPY build/libs/familie-ef-infotrygd-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

