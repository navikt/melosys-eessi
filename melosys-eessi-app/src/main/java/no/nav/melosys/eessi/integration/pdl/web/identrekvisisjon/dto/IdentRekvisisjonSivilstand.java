package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentRekvisisjonSivilstand {

    private String sivilstand;
    private String sivilstandsdato;
    private String bekreftelsesdato;
    private String relatertVedSivilstand;
}
