CREATE TABLE FAGSAK_RINASAK_KOBLING
(
    rina_saksnummer VARCHAR(20) PRIMARY KEY,
    gsak_saksnummer INTEGER NOT NULL,
    buc_type        VARCHAR(10) NOT NULL
);

CREATE INDEX gsak_saksnummer_idx ON FAGSAK_RINASAK_KOBLING(gsak_saksnummer);