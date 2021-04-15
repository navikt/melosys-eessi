CREATE TABLE sed_mottatt_hendelse
(
    id serial NOT NULL PRIMARY KEY,
    sed_hendelse jsonb NOT NULL,
    journalpost_id VARCHAR(20),
    publisert_kafka boolean DEFAULT false,
    mottatt_dato timestamp,
    endret_dato timestamp
);

CREATE TABLE buc_identifisert
(
    id serial NOT NULL PRIMARY KEY,
    aktoer_id VARCHAR(20) NOT NULL,
    rina_saksnummer VARCHAR(20) NOT NULL,
    identifisert_av VARCHAR(20) NOT NULL,
    identifisering_tidspunkt TIMESTAMP NOT NULL
);

create index idx_buc_id_aktoer ON buc_identifisert(aktoer_id);
create unique index idx_buc_id_rina_saksnummer ON buc_identifisert(rina_saksnummer);