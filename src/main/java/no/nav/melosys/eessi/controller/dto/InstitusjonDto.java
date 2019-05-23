package no.nav.melosys.eessi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InstitusjonDto {

    private String id;
    private String navn;
    private String landkode;
}
