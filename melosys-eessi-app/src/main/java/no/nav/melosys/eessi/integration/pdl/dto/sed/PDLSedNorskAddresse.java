package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedNorskAddresse {
    private String adressenavn = "";
    private String husnummer = "";
    private String husbokstav = "";
    private String postnummer = "";
    private String bruksenhetsnummer = "";
    private String tilleggsnavn = "";
    private String matrikkelId = "";

    public static class Builder {
        private PDLSedNorskAddresse pdlSedNorskAddresse = new PDLSedNorskAddresse();

        public Builder medAdressenavn(String adressenavn) {
            pdlSedNorskAddresse.adressenavn = adressenavn;
            return this;
        }

        public Builder medHusnummer(String husnummer) {
            pdlSedNorskAddresse.husnummer = husnummer;
            return this;
        }

        public Builder medHusbokstav(String husbokstav) {
            pdlSedNorskAddresse.husbokstav = husbokstav;
            return this;
        }

        public Builder medPostnummer(String postnummer) {
            pdlSedNorskAddresse.postnummer = postnummer;
            return this;
        }

        public Builder medBruksenhetsnummer(String bruksenhetsnummer) {
            pdlSedNorskAddresse.bruksenhetsnummer = bruksenhetsnummer;
            return this;
        }

        public Builder medTilleggsnavn(String tilleggsnavn) {
            pdlSedNorskAddresse.tilleggsnavn = tilleggsnavn;
            return this;
        }

        public Builder medMatrikkelId(String matrikkelId) {
            pdlSedNorskAddresse.matrikkelId = matrikkelId;
            return this;
        }

        public PDLSedNorskAddresse build() {
            return pdlSedNorskAddresse;
        }
    }
}
