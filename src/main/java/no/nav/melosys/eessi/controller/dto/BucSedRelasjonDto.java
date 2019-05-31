package no.nav.melosys.eessi.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucSedRelasjonDto {

    private String buc;
    private String forsteSed;
    private String fagomrade;
}
