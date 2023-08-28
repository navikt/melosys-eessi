package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonSivilstand {
    private String sivilstand = "";
    private String sivilstandsdato = "";
    private String bekreftelsesdato = "";
    private String relatertVedSivilstand = "";

    public static class Builder {
        private final DnummerRekvisisjonSivilstand dnummerRekvisisjonSivilstand = new DnummerRekvisisjonSivilstand();

        public Builder medSivilstand(String sivilstand) {
            dnummerRekvisisjonSivilstand.sivilstand = sivilstand;
            return this;
        }

        public Builder medSivilstandsdato(String sivilstandsdato) {
            dnummerRekvisisjonSivilstand.sivilstandsdato = sivilstandsdato;
            return this;
        }

        public Builder medBekreftelsesdato(String bekreftelsesdato) {
            dnummerRekvisisjonSivilstand.bekreftelsesdato = bekreftelsesdato;
            return this;
        }

        public Builder medRelatertVedSivilstand(String relatertVedSivilstand) {
            dnummerRekvisisjonSivilstand.relatertVedSivilstand = relatertVedSivilstand;
            return this;
        }

        public DnummerRekvisisjonSivilstand build() {
            return dnummerRekvisisjonSivilstand;
        }
    }
}
