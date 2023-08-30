package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonRelasjon {

    private String minRolleForPerson;
    private String relatertPersonsRolle;
    private String relatertPersonsIdent;
}
