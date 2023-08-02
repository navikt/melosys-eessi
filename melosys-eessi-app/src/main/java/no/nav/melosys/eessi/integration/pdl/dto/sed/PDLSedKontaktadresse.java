package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedKontaktadresse {
    private PDLSedNorskAddresse norskVegadresse = new PDLSedNorskAddresse();
    private PDLSedNorskPostboksadresse norskPostboksadresse = new PDLSedNorskPostboksadresse();
    private PDLSedUtenlandskVegadresse utenlandskVegadresse = new PDLSedUtenlandskVegadresse();
    private PDLSedUtenlandskPostboksadresse utenlandskPostboksadresse = new PDLSedUtenlandskPostboksadresse();

    public static class Builder {
        private PDLSedKontaktadresse pdlSedKontaktadresse = new PDLSedKontaktadresse();

        public Builder medNorskVegadresse(PDLSedNorskAddresse norskVegadresse) {
            pdlSedKontaktadresse.norskVegadresse = norskVegadresse;
            return this;
        }

        public Builder medNorskPostboksadresse(PDLSedNorskPostboksadresse norskPostboksadresse) {
            pdlSedKontaktadresse.norskPostboksadresse = norskPostboksadresse;
            return this;
        }

        public Builder medUtenlandskVegadresse(PDLSedUtenlandskVegadresse utenlandskVegadresse) {
            pdlSedKontaktadresse.utenlandskVegadresse = utenlandskVegadresse;
            return this;
        }

        public Builder medUtenlandskPostboksadresse(PDLSedUtenlandskPostboksadresse utenlandskPostboksadresse) {
            pdlSedKontaktadresse.utenlandskPostboksadresse = utenlandskPostboksadresse;
            return this;
        }

        public PDLSedKontaktadresse build() {
            return pdlSedKontaktadresse;
        }
    }
}
