CREATE TABLE BUC_IDENTIFISERT
(
    id serial NOT NULL PRIMARY KEY,
    rina_saksnummer VARCHAR(20) NOT NULL,
    folkeregisterident VARCHAR(20) NOT NULL
);

CREATE INDEX idx_buc_identifisert_rina_saksnummer ON BUC_IDENTIFISERT(rina_saksnummer);
