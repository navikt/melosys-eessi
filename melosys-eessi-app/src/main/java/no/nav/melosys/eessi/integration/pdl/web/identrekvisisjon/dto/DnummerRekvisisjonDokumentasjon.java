package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonDokumentasjon {

    private String dokumentasjonstype;
    private String ident;
}
