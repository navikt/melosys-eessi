CREATE TABLE JOURNALPOST_SED_KOBLING
(
    journalpost_id  VARCHAR(20) PRIMARY KEY,
    rina_saksnummer VARCHAR(20) NOT NULL,
    sed_id          VARCHAR(40) NOT NULL,
    sed_versjon     INTEGER     NOT NULL
);

