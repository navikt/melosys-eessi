CREATE TABLE sed_mottatt
(
    id serial NOT NULL PRIMARY KEY,
    sed_hendelse jsonb NOT NULL,
    sed_kontekst jsonb NOT NULL,
    versjon integer NOT NULL,
    mottatt_dato timestamp,
    endret_dato timestamp,
    feilede_forsok integer not null default 0,
    feilet boolean DEFAULT false,
    ferdig boolean DEFAULT false
);

CREATE INDEX sed_mottatt_feilet_idx ON sed_mottatt(feilet);
CREATE INDEX sed_mottatt_ferdig_idx ON sed_mottatt(ferdig);
