package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedOppholdsadresse {
    private String gyldigFraOgMed = "";
    private PDLSedNorskAddresse norskVegadresse = new PDLSedNorskAddresse();
    private PDLSedUtenlandskVegadresse utenlandskVegadresse = new PDLSedUtenlandskVegadresse();

    public static class Builder {
        private PDLSedOppholdsadresse pdlSedOppholdsadresse = new PDLSedOppholdsadresse();

        public Builder medGyldigFraOgMed(String gyldigFraOgMed) {
            pdlSedOppholdsadresse.gyldigFraOgMed = gyldigFraOgMed;
            return this;
        }

        public Builder medNorskVegadresse(PDLSedNorskAddresse norskVegadresse) {
            pdlSedOppholdsadresse.norskVegadresse = norskVegadresse;
            return this;
        }

        public Builder medUtenlandskVegadresse(PDLSedUtenlandskVegadresse utenlandskVegadresse) {
            pdlSedOppholdsadresse.utenlandskVegadresse = utenlandskVegadresse;
            return this;
        }

        public PDLSedOppholdsadresse build() {
            return pdlSedOppholdsadresse;
        }
    }
}
