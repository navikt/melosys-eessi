package no.nav.melosys.eessi.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpprettSedDto {
    private String rinaSaksnummer;
    private String rinaUrl;
}
