FROM navikt/java:11
LABEL maintainer="Team Melosys"

COPY melosys-eessi-app/target/melosys-eessi-exec.jar /app/app.jar