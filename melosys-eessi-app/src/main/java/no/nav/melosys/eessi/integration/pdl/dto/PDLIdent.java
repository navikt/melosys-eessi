package no.nav.melosys.eessi.integration.pdl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe.AKTORID;
import static no.nav.melosys.eessi.integration.pdl.dto.PDLIdentGruppe.FOLKEREGISTERIDENT;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDLIdent {
    private PDLIdentGruppe gruppe;
    private String ident;

    public boolean erFolkeregisterIdent() {
        return gruppe == FOLKEREGISTERIDENT;
    }

    public boolean erAktørID() {
        return gruppe == AKTORID;
    }
}
