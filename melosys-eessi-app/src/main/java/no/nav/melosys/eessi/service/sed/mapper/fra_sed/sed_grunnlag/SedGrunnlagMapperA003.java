package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Periode;
import no.nav.melosys.eessi.controller.dto.SedGrunnlagA003Dto;
import no.nav.melosys.eessi.controller.dto.Virksomhet;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.service.sed.helpers.StreamUtils;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.FraSedA003Mapper;

public class SedGrunnlagMapperA003 extends FraSedA003Mapper implements NyttLovvalgSedGrunnlagMapper<MedlemskapA003> {
    @Override
    public SedGrunnlagA003Dto map(SED sed) {
        MedlemskapA003 medlemskap = hentMedlemskap(sed);

        SedGrunnlagA003Dto sedGrunnlagDto = new SedGrunnlagA003Dto(NyttLovvalgSedGrunnlagMapper.super.map(sed));
        sedGrunnlagDto.setSedType(SedType.A003);
        sedGrunnlagDto.setLovvalgsperioder(List.of(hentLovvalgsperiode(medlemskap)));
        sedGrunnlagDto.setOvergangsregelbestemmelser(mapOvergangsregelbestemmelse(medlemskap));

        // Per dags dato er det ikke mulig å hente marginalt arbeid siden det ikke er knyttet til en enkelt arbeidsgiver
        List<Arbeidsgiver> arbeidsgivere = hentArbeidsgivere(sed);
        List<Virksomhet> norskeVirksomheter = arbeidsgivere.stream()
                .filter(SedGrunnlagMapperA003::erNorskArbeidsgiver)
                .map(Virksomhet::av)
                .collect(Collectors.toList());
        sedGrunnlagDto.setNorskeArbeidsgivendeVirksomheter(norskeVirksomheter);

        List<Virksomhet> utenlandskeVirksomheter = arbeidsgivere.stream()
                .filter(SedGrunnlagMapperA003::erUtenlandskArbeidsgiver)
                .map(Virksomhet::av)
                .collect(Collectors.toList());
        sedGrunnlagDto.setArbeidsgivendeVirksomheter(utenlandskeVirksomheter);

        return sedGrunnlagDto;
    }

    private List<Bestemmelse> mapOvergangsregelbestemmelse(MedlemskapA003 medlemskap) {
        return StreamUtils.nullableStream(medlemskap.getGjeldendereglerEC883())
                .map(Bestemmelse::fraString).collect(Collectors.toList());
    }

    private List<Arbeidsgiver> hentArbeidsgivere(SED sed) {
        return Stream.concat(
                hentArbeidsgivere(sed.getNav()).stream(),
                hentAndrelandArbeidsgivere(hentMedlemskap(sed)).stream()
        ).collect(Collectors.toList());
    }

    private static List<Arbeidsgiver> hentArbeidsgivere(Nav nav) {
        if (nav.getArbeidsgiver() != null) {
            return nav.getArbeidsgiver();
        }
        return Collections.emptyList();
    }

    private static List<Arbeidsgiver> hentAndrelandArbeidsgivere(MedlemskapA003 medlemskap) {
        if (medlemskap.getAndreland() != null && CollectionUtils.isNotEmpty(medlemskap.getAndreland().getArbeidsgiver())) {
            return medlemskap.getAndreland().getArbeidsgiver();
        }
        return Collections.emptyList();
    }

    private static boolean erNorskArbeidsgiver(Arbeidsgiver arbeidsgiver) {
        return "NO".equalsIgnoreCase(arbeidsgiver.getAdresse().getLand());
    }

    private static boolean erUtenlandskArbeidsgiver(Arbeidsgiver arbeidsgiver) {
        return !erNorskArbeidsgiver(arbeidsgiver);
    }

    @Override
    public Periode hentPeriode(MedlemskapA003 medlemskap) {
        return hentPeriode(medlemskap.getVedtak().getGjelderperiode());
    }
}
