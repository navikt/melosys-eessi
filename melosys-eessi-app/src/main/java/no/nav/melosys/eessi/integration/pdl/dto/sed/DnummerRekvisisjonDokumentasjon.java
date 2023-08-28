package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonDokumentasjon {
    private String dokumentasjonstype = "";
    private String ident = "";

    public static class Builder {
        private DnummerRekvisisjonDokumentasjon dnummerRekvisisjonDokumentasjon = new DnummerRekvisisjonDokumentasjon();

        public Builder medDokumentasjonstype(String dokumentasjonstype) {
            dnummerRekvisisjonDokumentasjon.dokumentasjonstype = dokumentasjonstype;
            return this;
        }

        public Builder medIdent(String ident) {
            dnummerRekvisisjonDokumentasjon.ident = ident;
            return this;
        }

        public DnummerRekvisisjonDokumentasjon build() {
            return dnummerRekvisisjonDokumentasjon;
        }
    }
}
