FROM gcr.io/distroless/java21-debian12:nonroot
LABEL maintainer="PO Familie - Enslig forsørger"

ENV LANG='nb_NO.UTF-8'
ENV LANGUAGE='nb_NO:nb'
ENV LC_ALL='nb_NO.UTF-8'
ENV TZ='Europe/Oslo'
ENV APP_NAME=familie-ef-proxy
ENV JDK_JAVA_OPTIONS="-Xms270M -XX:MaxRAMPercentage=75"
COPY ./build/libs/familie-ef-infotrygd-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]