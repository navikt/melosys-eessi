FROM ghcr.io/navikt/baseimages/temurin:17
LABEL maintainer="Team Melosys"

ENV JAVA_OPTS="${JAVA_OPTS} -Xms512m -Xmx2048m"

COPY docker-init-scripts/*.sh /init-scripts/

COPY melosys-eessi-app/target/melosys-eessi-exec.jar /app/app.jar
