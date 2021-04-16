CREATE TABLE sed_mottatt_hendelse
(
    id serial NOT NULL PRIMARY KEY,
    sed_hendelse jsonb NOT NULL,
    journalpost_id VARCHAR(20),
    publisert_kafka boolean DEFAULT false,
    mottatt_dato timestamp,
    endret_dato timestamp
);

CREATE TABLE buc_identifisering_oppg
(
    id serial NOT NULL PRIMARY KEY,
    rina_saksnummer VARCHAR(20) NOT NULL,
    oppgave_id VARCHAR(20) NOT NULL
);

create index idx_buc_oppg_rina_saksnr on buc_identifisering_oppg(rina_saksnummer);
create index idx_buc_oppg_oppgave_id on buc_identifisering_oppg(oppgave_id);
