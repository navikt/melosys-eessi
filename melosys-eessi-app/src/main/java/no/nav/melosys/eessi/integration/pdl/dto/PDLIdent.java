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
}
