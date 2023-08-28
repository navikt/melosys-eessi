package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonOppholdsadresse {
    private String gyldigFraOgMed = "";
    private PDLSedNorskAddresse norskVegadresse = new PDLSedNorskAddresse();
    private PDLSedUtenlandskVegadresse utenlandskVegadresse = new PDLSedUtenlandskVegadresse();

    public static class Builder {
        private DnummerRekvisisjonOppholdsadresse dnummerRekvisjonOppholdsadresse = new DnummerRekvisisjonOppholdsadresse();

        public Builder medGyldigFraOgMed(String gyldigFraOgMed) {
            dnummerRekvisjonOppholdsadresse.gyldigFraOgMed = gyldigFraOgMed;
            return this;
        }

        public Builder medNorskVegadresse(PDLSedNorskAddresse norskVegadresse) {
            dnummerRekvisjonOppholdsadresse.norskVegadresse = norskVegadresse;
            return this;
        }

        public Builder medUtenlandskVegadresse(PDLSedUtenlandskVegadresse utenlandskVegadresse) {
            dnummerRekvisjonOppholdsadresse.utenlandskVegadresse = utenlandskVegadresse;
            return this;
        }

        public DnummerRekvisisjonOppholdsadresse build() {
            return dnummerRekvisjonOppholdsadresse;
        }
    }
}
