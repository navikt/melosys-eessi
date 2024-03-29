CREATE TABLE kafka_dlq
(
    id uuid PRIMARY KEY,
    melding jsonb NOT NULL,
    queue_type varchar(50),
    tid_registrert timestamp DEFAULT CURRENT_TIMESTAMP,
    tid_sist_rekjort timestamp,
    siste_feilmelding text,
    antall_rekjoringer int default 0
);
