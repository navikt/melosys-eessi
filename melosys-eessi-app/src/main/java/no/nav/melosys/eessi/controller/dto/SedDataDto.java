package no.nav.melosys.eessi.controller.dto;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SedDataDto extends SedGrunnlagDto {

    private Bruker bruker;
    private Adresse kontaktadresse;
    private Adresse oppholdsadresse;
    private List<FamilieMedlem> familieMedlem;
    private Periode søknadsperiode;

    //A008 spesifikt
    private String avklartBostedsland;

    //A010, A009, A003 spesifikt
    private VedtakDto vedtakDto;

    //X008 spesifikt
    private InvalideringSedDto invalideringSedDto;

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
            .filter(Objects::nonNull)
            .findFirst();
    }

    public String finnLovvalgslandDefaultNO() {
        return finnLovvalgsland().orElse("NO");
    }

    public Optional<Lovvalgsperiode> finnLovvalgsperiode() {
        return getLovvalgsperioder().stream()
            .max(Comparator.comparing(Lovvalgsperiode::getFom));
    }

    public boolean manglerAdresser() {
        return getBostedsadresse() == null
            && getKontaktadresse() == null
            && getOppholdsadresse() == null;
    }
}
