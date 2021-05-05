FROM navikt/java:15
LABEL maintainer="Team Melosys"

COPY docker-init-scripts/*.sh /init-scripts/

COPY melosys-eessi-app/target/melosys-eessi-exec.jar /app/app.jar
