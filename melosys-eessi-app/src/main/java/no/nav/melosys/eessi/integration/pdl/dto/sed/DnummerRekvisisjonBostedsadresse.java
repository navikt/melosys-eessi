package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonBostedsadresse {
    private String gyldigFraOgMed = "";
    private PDLSedUtenlandskVegadresse utenlandskVegadresse = new PDLSedUtenlandskVegadresse();

    public static class Builder {
        private DnummerRekvisisjonBostedsadresse dnummerRekvisisjonBostedsadresse = new DnummerRekvisisjonBostedsadresse();

        public Builder medGyldigFraOgMed(String gyldigFraOgMed) {
            dnummerRekvisisjonBostedsadresse.gyldigFraOgMed = gyldigFraOgMed;
            return this;
        }

        public Builder medUtenlandskVegadresse(PDLSedUtenlandskVegadresse utenlandskVegadresse) {
            dnummerRekvisisjonBostedsadresse.utenlandskVegadresse = utenlandskVegadresse;
            return this;
        }

        public DnummerRekvisisjonBostedsadresse build() {
            return dnummerRekvisisjonBostedsadresse;
        }
    }
}
