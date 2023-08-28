package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonSivilstand {

    private String sivilstand;
    private String sivilstandsdato;
    private String bekreftelsesdato;
    private String relatertVedSivilstand;
}
