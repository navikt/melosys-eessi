package no.nav.melosys.eessi.controller.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SedDataDto extends SedGrunnlagDto {
    //Persondok.
    private List<FamilieMedlem> familieMedlem;
    private Bruker bruker;

    //Andre medlemsvariabler
    private List<Virksomhet> utenlandskeVirksomheter;

    //A008 spesifikt
    private String avklartBostedsland;

    //Lovvalg
    private List<Lovvalgsperiode> tidligereLovvalgsperioder;

    private Long gsakSaksnummer;

    private List<String> mottakerIder;

    private SvarAnmodningUnntakDto svarAnmodningUnntak;
}
