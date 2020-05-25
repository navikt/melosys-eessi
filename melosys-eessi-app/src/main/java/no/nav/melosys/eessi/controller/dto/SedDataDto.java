package no.nav.melosys.eessi.controller.dto;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SedDataDto extends SedGrunnlagDto {
    private List<FamilieMedlem> familieMedlem;
    private Bruker bruker;

    //A008 spesifikt
    private String avklartBostedsland;

    //Lovvalg
    private List<Lovvalgsperiode> tidligereLovvalgsperioder;

    private Long gsakSaksnummer;

    private List<String> mottakerIder;

    private String ytterligereInformasjon;

    private SvarAnmodningUnntakDto svarAnmodningUnntak;

    private UtpekingAvvisDto utpekingAvvis;

    public Optional<String> finnLovvalgsland() {
        return getLovvalgsperioder().stream()
                .map(Lovvalgsperiode::getLovvalgsland)
                .findFirst();
    }

    public String finnLovvalgslandDefaultNO() {
        return finnLovvalgsland().orElse("NO");
    }
}
