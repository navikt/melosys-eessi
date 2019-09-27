CREATE TABLE sed_mottatt
(
    id serial NOT NULL PRIMARY KEY,
    sed_hendelse jsonb NOT NULL,
    sed_kontekst jsonb NOT NULL,
    versjon integer NOT NULL,
    ferdig boolean DEFAULT false,
    mottatt_dato timestamp,
    endret_dato timestamp,
    feilede_forsok integer not null default 0
);

CREATE INDEX sed_mottatt_ferdig_idx ON sed_mottatt(ferdig);
