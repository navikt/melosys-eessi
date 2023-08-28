package no.nav.melosys.eessi.integration.pdl.dto.sed;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnummerRekvisisjonUtenlandskIdentifikasjon {

    private String utstederland;
    private String utenlandskId;
}
