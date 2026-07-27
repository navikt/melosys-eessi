FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jdk:openjdk-21
LABEL maintainer="Team Melosys"
WORKDIR /app
# Copy application files
COPY melosys-eessi-app/target/melosys-eessi-exec.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Duser.language=no -Duser.country=NO -Duser.timezone=Europe/Oslo"
CMD ["java", "-jar", "/app/app.jar"]
