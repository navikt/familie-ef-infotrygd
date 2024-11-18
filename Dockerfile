FROM gcr.io/distroless/java21-debian12:nonroot

ENV TZ="Europe/Oslo"

COPY init.sh /init-scripts/init.sh
COPY build/libs/familie-ef-infotrygd-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"
CMD ["-jar", "/app.jar"]

