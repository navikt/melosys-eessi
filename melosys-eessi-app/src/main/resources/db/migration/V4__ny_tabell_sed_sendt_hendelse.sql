CREATE TABLE sed_sendt_hendelse
(
    id serial NOT NULL PRIMARY KEY,
    sed_id varchar(20) NOT NULL,
    rina_sak_id varchar(20) NOT NULL,
    journalfoert boolean NOT NULL
);
