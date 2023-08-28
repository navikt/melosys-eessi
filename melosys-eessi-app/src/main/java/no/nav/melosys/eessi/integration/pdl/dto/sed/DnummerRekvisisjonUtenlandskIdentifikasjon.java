package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonUtenlandskIdentifikasjon {
    private String utstederland = "";
    private String utenlandskId = "";

    public static class Builder {
        private DnummerRekvisisjonUtenlandskIdentifikasjon dnummerRekvisisjonUtenlandskIdentifikasjon = new DnummerRekvisisjonUtenlandskIdentifikasjon();

        public Builder medUtstederland(String utstederland) {
            dnummerRekvisisjonUtenlandskIdentifikasjon.utstederland = utstederland;
            return this;
        }

        public Builder medUtenlandskId(String utenlandskId) {
            dnummerRekvisisjonUtenlandskIdentifikasjon.utenlandskId = utenlandskId;
            return this;
        }

        public DnummerRekvisisjonUtenlandskIdentifikasjon build() {
            return dnummerRekvisisjonUtenlandskIdentifikasjon;
        }
    }
}
