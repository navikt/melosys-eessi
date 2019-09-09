package no.nav.melosys.eessi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstitusjonDto {

    private String id;
    private String navn;
    private String landkode;
}
