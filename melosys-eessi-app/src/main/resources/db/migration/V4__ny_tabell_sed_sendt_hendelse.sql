CREATE TABLE sed_sendt_hendelse
(
    id serial NOT NULL PRIMARY KEY,
    sed_hendelse jsonb NOT NULL,
    journalpost_id VARCHAR(20)
);
