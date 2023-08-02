package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedDokumentasjon {
    private String dokumentasjonstype = "";
    private String ident = "";

    public static class Builder {
        private PDLSedDokumentasjon pdlSedDokumentasjon = new PDLSedDokumentasjon();

        public Builder medDokumentasjonstype(String dokumentasjonstype) {
            pdlSedDokumentasjon.dokumentasjonstype = dokumentasjonstype;
            return this;
        }

        public Builder medIdent(String ident) {
            pdlSedDokumentasjon.ident = ident;
            return this;
        }

        public PDLSedDokumentasjon build() {
            return pdlSedDokumentasjon;
        }
    }
}
