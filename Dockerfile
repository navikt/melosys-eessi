# === Stage 1: Build a minimal base with nb_NO.UTF-8 ===
FROM --platform=$BUILDPLATFORM debian:12 AS locale-builder
RUN apt-get update && apt-get install -y locales && \
    echo "nb_NO.UTF-8 UTF-8" >> /etc/locale.gen && \
    locale-gen && \
    apt-get clean

# === Stage 2: Use the distroless base and copy the locale ===
FROM --platform=$TARGETPLATFORM gcr.io/distroless/java17-debian12:nonroot
LABEL maintainer="Team Melosys"

WORKDIR /app

# Copy generated locales from the builder stage
COPY --from=locale-builder /usr/lib/locale /usr/lib/locale
COPY --from=locale-builder /etc/default/locale /etc/default/locale
COPY --from=locale-builder /etc/locale.alias /etc/locale.alias

# Copy application files
COPY melosys-eessi-app/target/melosys-eessi-exec.jar app.jar

# Set Norwegian locale (now supported)
ENV LANG='nb_NO.UTF-8' \
    LANGUAGE='nb_NO:nb' \
    LC_ALL='nb_NO.UTF-8' \
    TZ='Europe/Oslo'

CMD ["/app/app.jar"]
