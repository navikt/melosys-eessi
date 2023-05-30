CREATE TABLE kafka_dlq
(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    melding jsonb NOT NULL,
    ko_type varchar(50),
    tid_registrert timestamp DEFAULT CURRENT_TIMESTAMP,
    tid_sist_rekjort timestamp,
    siste_feilmelding text,
    antall_rekjoringer int default 0
);
