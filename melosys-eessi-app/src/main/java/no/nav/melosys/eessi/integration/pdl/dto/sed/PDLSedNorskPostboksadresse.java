package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedNorskPostboksadresse {
    private String postboks = "";
    private String postnummer = "";

    public static class Builder {
        private PDLSedNorskPostboksadresse pdlSedNorskPostboksadresse = new PDLSedNorskPostboksadresse();

        public Builder medPostboks(String postboks) {
            pdlSedNorskPostboksadresse.postboks = postboks;
            return this;
        }

        public Builder medPostnummer(String postnummer) {
            pdlSedNorskPostboksadresse.postnummer = postnummer;
            return this;
        }

        public PDLSedNorskPostboksadresse build() {
            return pdlSedNorskPostboksadresse;
        }
    }
}
