FROM navikt/java:15
LABEL maintainer="Team Melosys"

COPY melosys-eessi-app/target/melosys-eessi-exec.jar /app/app.jar
