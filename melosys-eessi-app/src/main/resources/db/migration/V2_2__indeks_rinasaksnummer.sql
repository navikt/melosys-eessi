CREATE INDEX idx_sed_mottatt_rina_saksnummer ON sed_mottatt_hendelse ((sed_hendelse -> 'rinaSakId'));
CREATE INDEX idx_sed_mottatt_dato ON sed_mottatt_hendelse(mottatt_dato);
