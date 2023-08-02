package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedSivilstand {
    private String sivilstand = "";
    private String sivilstandsdato = "";
    private String bekreftelsesdato = "";
    private String relatertVedSivilstand = "";

    public static class Builder {
        private PDLSedSivilstand pdlSedSivilstand = new PDLSedSivilstand();

        public Builder medSivilstand(String sivilstand) {
            pdlSedSivilstand.sivilstand = sivilstand;
            return this;
        }

        public Builder medSivilstandsdato(String sivilstandsdato) {
            pdlSedSivilstand.sivilstandsdato = sivilstandsdato;
            return this;
        }

        public Builder medBekreftelsesdato(String bekreftelsesdato) {
            pdlSedSivilstand.bekreftelsesdato = bekreftelsesdato;
            return this;
        }

        public Builder medRelatertVedSivilstand(String relatertVedSivilstand) {
            pdlSedSivilstand.relatertVedSivilstand = relatertVedSivilstand;
            return this;
        }

        public PDLSedSivilstand build() {
            return pdlSedSivilstand;
        }
    }
}
