CREATE TABLE CASE_RELATION (
  id          SERIAL    NOT NULL,
  rina_sakid  VARCHAR   NOT NULL,
  gsak_id     VARCHAR   NOT NULL
);

CREATE UNIQUE INDEX rina_sakid_idx ON CASE_RELATION(rina_sakid);
CREATE UNIQUE INDEX gsak_id_idx ON CASE_RELATION(gsak_id);