package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonRelasjon {
    private String minRolleForPerson = "";
    private String relatertPersonsRolle = "";
    private String relatertPersonsIdent = "";

    public static class Builder {
        private DnummerRekvisisjonRelasjon dnummerRekvisisjonRelasjon = new DnummerRekvisisjonRelasjon();

        public Builder medMinRolleForPerson(String minRolleForPerson) {
            dnummerRekvisisjonRelasjon.minRolleForPerson = minRolleForPerson;
            return this;
        }

        public Builder medRelatertPersonsRolle(String relatertPersonsRolle) {
            dnummerRekvisisjonRelasjon.relatertPersonsRolle = relatertPersonsRolle;
            return this;
        }

        public Builder medRelatertPersonsIdent(String relatertPersonsIdent) {
            dnummerRekvisisjonRelasjon.relatertPersonsIdent = relatertPersonsIdent;
            return this;
        }

        public DnummerRekvisisjonRelasjon build() {
            return dnummerRekvisisjonRelasjon;
        }
    }
}
