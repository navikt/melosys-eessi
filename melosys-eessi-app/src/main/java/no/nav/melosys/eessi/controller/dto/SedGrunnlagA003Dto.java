package no.nav.melosys.eessi.controller.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SedGrunnlagA003Dto extends SedGrunnlagDto {
    private List<Bestemmelse> overgangsregelbestemmelser;
    private List<Virksomhet> norskeArbeidsgivendeVirksomheter;

    public SedGrunnlagA003Dto(SedGrunnlagDto sedGrunnlagDto) {
        setBostedsadresse(sedGrunnlagDto.getBostedsadresse());
        setUtenlandskIdent(sedGrunnlagDto.getUtenlandskIdent());
        setArbeidssteder(sedGrunnlagDto.getArbeidssteder());
        setArbeidsgivendeVirksomheter(sedGrunnlagDto.getArbeidsgivendeVirksomheter());
        setSelvstendigeVirksomheter(sedGrunnlagDto.getSelvstendigeVirksomheter());
        setYtterligereInformasjon(sedGrunnlagDto.getYtterligereInformasjon());
    }
}
