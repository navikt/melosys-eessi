package no.nav.melosys.eessi.controller.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SedGrunnlagA003Dto extends SedGrunnlagDto {
    private List<Bestemmelse> overgangsregelbestemmelse;
    private List<Virksomhet> norskeArbeidsgivendeVirksomheter;
}
