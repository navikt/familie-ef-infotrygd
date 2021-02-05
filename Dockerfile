FROM navikt/java:11

COPY init.sh /init-scripts/init.sh

ENV JAVA_OPTS="${JAVA_OPTS} -Xms270M"

COPY build/libs/*.jar app.jar
EXPOSE 8080

