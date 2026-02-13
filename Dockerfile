FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
COPY --chown=nonroot:nonroot ./build/libs/familie-ef-infotrygd-0.0.1-SNAPSHOT.jar /app/app.jar
# COPY init.sh /init-scripts/init.sh
WORKDIR /app
ENV APP_NAME=familie-ef-infotrygd
ENV TZ="Europe/Oslo"
# TLS Config works around an issue in OpenJDK... See: https://github.com/kubernetes-client/java/issues/854
ENTRYPOINT [ "java", "-Djdk.tls.client.protocols=TLSv1.2", "-jar", "/app/app.jar" ]

#FROM ghcr.io/navikt/baseimages/temurin:25
#
#COPY init.sh /init-scripts/init.sh
#
#ENV JAVA_OPTS="${JAVA_OPTS} -Xms270M -XX:MaxRAMPercentage=75"
#
#COPY build/libs/familie-ef-infotrygd-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 8080
