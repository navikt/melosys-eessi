package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonKilde {
    private String institusjon = "";
    private String landkode = "";

    public static class Builder {
        private DnummerRekvisisjonKilde kilde = new DnummerRekvisisjonKilde();

        public Builder medInstitusjon(String institusjon) {
            kilde.institusjon = institusjon;
            return this;
        }

        public Builder medLandkode(String landkode) {
            kilde.landkode = landkode;
            return this;
        }

        public DnummerRekvisisjonKilde build() {
            return kilde;
        }
    }
}
