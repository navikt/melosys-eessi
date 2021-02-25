package no.nav.melosys.eessi.integration.pdl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLIdent {
    private String gruppe;
    private String ident;

    public boolean erFolkeregisterIdent() {
        return "FOLKEREGISTERIDENT".equals(gruppe);
    }

    public boolean erAktørID() {
        return "AKTORID".equals(gruppe);
    }

    public boolean erNPID() { //NAV PersonIdent
        return "NPID".equals(gruppe);
    }
}
