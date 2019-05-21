package no.nav.melosys.eessi.controller.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SedinfoDto {

    private String bucId;
    private String sedId;
    private LocalDate opprettetDato;
    private LocalDate sistOppdatert;
    private String sedType;
    private String status;
    private String rinaUrl;
}
