package no.nav.melosys.eessi.controller.dto;

import lombok.*;

@Value
@Builder
public class BucSedRelasjonDto {

    private String buc;
    private String forsteSed;
    private String fagomrade;
}
