package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedUtenlandskIdentifikasjon {
    private String utstederland = "";
    private String utenlandskId = "";

    public static class Builder {
        private PDLSedUtenlandskIdentifikasjon pdlSedUtenlandskIdentifikasjon = new PDLSedUtenlandskIdentifikasjon();

        public Builder medUtstederland(String utstederland) {
            pdlSedUtenlandskIdentifikasjon.utstederland = utstederland;
            return this;
        }

        public Builder medUtenlandskId(String utenlandskId) {
            pdlSedUtenlandskIdentifikasjon.utenlandskId = utenlandskId;
            return this;
        }

        public PDLSedUtenlandskIdentifikasjon build() {
            return pdlSedUtenlandskIdentifikasjon;
        }
    }
}
