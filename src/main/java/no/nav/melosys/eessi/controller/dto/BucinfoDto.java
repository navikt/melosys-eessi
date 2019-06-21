package no.nav.melosys.eessi.controller.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucinfoDto {

    private String id;
    private String bucType;
    private LocalDate opprettetDato;
    private List<SedinfoDto> seder;
}
