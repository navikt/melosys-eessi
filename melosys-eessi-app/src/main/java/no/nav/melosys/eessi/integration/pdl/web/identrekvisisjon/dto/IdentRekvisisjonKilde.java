package no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentRekvisisjonKilde {

    private String institusjon;
    private String landkode;
}
