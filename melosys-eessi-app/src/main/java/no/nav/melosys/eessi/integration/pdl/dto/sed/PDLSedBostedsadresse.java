package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedBostedsadresse {
    private String gyldigFraOgMed = "";
    private PDLSedUtenlandskVegadresse utenlandskVegadresse = new PDLSedUtenlandskVegadresse();

    public static class Builder {
        private PDLSedBostedsadresse pdlSedBostedsadresse = new PDLSedBostedsadresse();

        public Builder medGyldigFraOgMed(String gyldigFraOgMed) {
            pdlSedBostedsadresse.gyldigFraOgMed = gyldigFraOgMed;
            return this;
        }

        public Builder medUtenlandskVegadresse(PDLSedUtenlandskVegadresse utenlandskVegadresse) {
            pdlSedBostedsadresse.utenlandskVegadresse = utenlandskVegadresse;
            return this;
        }

        public PDLSedBostedsadresse build() {
            return pdlSedBostedsadresse;
        }
    }
}
