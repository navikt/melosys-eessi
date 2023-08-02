package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLSedRelasjon {
    private String minRolleForPerson = "";
    private String relatertPersonsRolle = "";
    private String relatertPersonsIdent = "";

    public static class Builder {
        private PDLSedRelasjon pdlSedRelasjon = new PDLSedRelasjon();

        public Builder medMinRolleForPerson(String minRolleForPerson) {
            pdlSedRelasjon.minRolleForPerson = minRolleForPerson;
            return this;
        }

        public Builder medRelatertPersonsRolle(String relatertPersonsRolle) {
            pdlSedRelasjon.relatertPersonsRolle = relatertPersonsRolle;
            return this;
        }

        public Builder medRelatertPersonsIdent(String relatertPersonsIdent) {
            pdlSedRelasjon.relatertPersonsIdent = relatertPersonsIdent;
            return this;
        }

        public PDLSedRelasjon build() {
            return pdlSedRelasjon;
        }
    }
}
