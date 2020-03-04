package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.controller.dto.Periode;
import no.nav.melosys.eessi.controller.dto.SedGrunnlagDto;
import no.nav.melosys.eessi.controller.dto.Virksomhet;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.FraSedA003Mapper;

public class SedGrunnlagMapperA003 extends FraSedA003Mapper implements NyttLovvalgSedGrunnlagMapper<MedlemskapA003> {
    @Override
    public SedGrunnlagDto map(SED sed) {
        SedGrunnlagDto sedGrunnlagDto = NyttLovvalgSedGrunnlagMapper.super.map(sed);
        MedlemskapA003 medlemskap = hentMedlemskap(sed);

        sedGrunnlagDto.setLovvalgsperioder(List.of(hentLovvalgsperiode(medlemskap)));
        sedGrunnlagDto.setOvergangsregelbestemmelse(Bestemmelse.fraString(medlemskap.getGjeldendereglerEC883())); // todo mulig denne skal være en liste

        /* TODO: Arbeidsgivere A003
         * EmployerInMemberStateWhichLegislationApplies -> nav.arbeidsgiver                     -> dto.arbeidsgivendeVirksomheter   -> bg.foretakUtland
         *                                                                                          kan også være marginal ^
         * EmployerInOtherMemberStateConcerned          -> medlemskap.andreland.arbeidsgiver    -> dto.marginaltArbeid(?)           -> bg.marginaltArbeid(?)
         * PlaceWork                                    -> nav.arbeidssted                      -> dto.arbeidssteder                -> bg.arbeidUtland
         * SelfEmployment                               -> nav.selvstendig                      -> dto.selvstendigeVirksomheter     -> bg.foretakUtland
         */

        // Per dags dato er det ikke mulig å hente marginalt arbeid siden det ikke er knyttet til en enkelt arbeidsgiver
        // todo kan skille mellom norske/utenlandske i melosys i stedet
        List<Virksomhet> norskeVirksomheter = medlemskap.getAndreland().getArbeidsgiver().stream() // todo npe
                .filter(SedGrunnlagMapperA003::erNorskArbeidsgiver)
                .map(Virksomhet::av)
                .collect(Collectors.toList());
        sedGrunnlagDto.setNorskeArbeidsgivendeVirksomheter(norskeVirksomheter);

        List<Virksomhet> utenlandskeVirksomheter = medlemskap.getAndreland().getArbeidsgiver().stream() // todo npe (nullableStream()?)
                .filter(SedGrunnlagMapperA003::erUtenlandskArbeidsgiver)
                .map(Virksomhet::av)
                .collect(Collectors.toList());

        sedGrunnlagDto.setArbeidsgivendeVirksomheter(Stream.concat(
                sedGrunnlagDto.getArbeidsgivendeVirksomheter().stream(),
                utenlandskeVirksomheter.stream()
        ).collect(Collectors.toList()));

        return sedGrunnlagDto;
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
