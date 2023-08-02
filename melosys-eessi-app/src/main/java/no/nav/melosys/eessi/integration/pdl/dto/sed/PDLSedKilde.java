package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedKilde {
    private String institusjon = "";
    private String landkode = "";

    public static class Builder {
        private PDLSedKilde kilde = new PDLSedKilde();

        public Builder medInstitusjon(String institusjon) {
            kilde.institusjon = institusjon;
            return this;
        }

        public Builder medLandkode(String landkode) {
            kilde.landkode = landkode;
            return this;
        }

        public PDLSedKilde build() {
            return kilde;
        }
    }
}
