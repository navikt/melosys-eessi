FROM navikt/java:11
LABEL maintainer="Team Melosys"

COPY melosys-eessi-app/target/melosys-eessi-exec.jar /app/app.jar

ARG SPRING_PROFILES
RUN echo $SPRING_PROFILES
ENV SPRING_PROFILES_ACTIVE $SPRING_PROFILES