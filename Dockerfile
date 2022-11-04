FROM ghcr.io/navikt/baseimages/temurin:17

COPY init.sh /init-scripts/init.sh

ENV JAVA_OPTS="${JAVA_OPTS} -Xms270M -XX:MaxRAMPercentage=75"

COPY build/libs/*.jar app.jar
EXPOSE 8080

