package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedUtenlandskPostboksadresse {
    private String postboksNummerNavn = "";
    private String regionDistriktOmraade = "";
    private String postkode = "";
    private String bySted = "";
    private String landkode = "";

    public static class Builder {
        private PDLSedUtenlandskPostboksadresse pdlSedUtenlandskPostboksadresse = new PDLSedUtenlandskPostboksadresse();

        public Builder medPostboksNummerNavn(String postboksNummerNavn) {
            pdlSedUtenlandskPostboksadresse.postboksNummerNavn = postboksNummerNavn;
            return this;
        }

        public Builder medRegionDistriktOmraade(String regionDistriktOmraade) {
            pdlSedUtenlandskPostboksadresse.regionDistriktOmraade = regionDistriktOmraade;
            return this;
        }

        public Builder medPostkode(String postkode) {
            pdlSedUtenlandskPostboksadresse.postkode = postkode;
            return this;
        }

        public Builder medBySted(String bySted) {
            pdlSedUtenlandskPostboksadresse.bySted = bySted;
            return this;
        }

        public Builder medLandkode(String landkode) {
            pdlSedUtenlandskPostboksadresse.landkode = landkode;
            return this;
        }

        public PDLSedUtenlandskPostboksadresse build() {
            return pdlSedUtenlandskPostboksadresse;
        }
    }
}
