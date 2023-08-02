package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedUtenlandskVegadresse {
    private String adressenavnNummer = "";
    private String bygningEtasjeLeilighet = "";
    private String postkode = "";
    private String bySted = "";
    private String regionDistriktOmraade = "";
    private String landkode = "";

    public static class Builder {
        private PDLSedUtenlandskVegadresse pdlSedUtenlandskVegadresse = new PDLSedUtenlandskVegadresse();

        public Builder medAdressenavnNummer(String adressenavnNummer) {
            pdlSedUtenlandskVegadresse.adressenavnNummer = adressenavnNummer;
            return this;
        }

        public Builder medBygningEtasjeLeilighet(String bygningEtasjeLeilighet) {
            pdlSedUtenlandskVegadresse.bygningEtasjeLeilighet = bygningEtasjeLeilighet;
            return this;
        }

        public Builder medPostkode(String postkode) {
            pdlSedUtenlandskVegadresse.postkode = postkode;
            return this;
        }

        public Builder medBySted(String bySted) {
            pdlSedUtenlandskVegadresse.bySted = bySted;
            return this;
        }

        public Builder medRegionDistriktOmraade(String regionDistriktOmraade) {
            pdlSedUtenlandskVegadresse.regionDistriktOmraade = regionDistriktOmraade;
            return this;
        }

        public Builder medLandkode(String landkode) {
            pdlSedUtenlandskVegadresse.landkode = landkode;
            return this;
        }

        public PDLSedUtenlandskVegadresse build() {
            return pdlSedUtenlandskVegadresse;
        }
    }
}
