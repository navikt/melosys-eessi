package no.nav.melosys.eessi.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UtpekingAvvisDto {

    private String nyttLovvalgsland;
    private String begrunnelseUtenlandskMyndighet;
    private boolean vilSendeAnmodningOmMerInformasjon;
}
