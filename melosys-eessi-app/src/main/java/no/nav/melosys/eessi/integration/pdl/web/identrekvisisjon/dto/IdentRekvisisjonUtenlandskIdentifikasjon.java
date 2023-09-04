package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentRekvisisjonUtenlandskIdentifikasjon {

    private String utstederland;
    private String utenlandskId;
}
