package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag

import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.controller.dto.Periode
import no.nav.melosys.eessi.controller.dto.SedGrunnlagA003Dto
import no.nav.melosys.eessi.controller.dto.Virksomhet
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver
import no.nav.melosys.eessi.models.sed.nav.Nav
import no.nav.melosys.eessi.service.sed.helpers.StreamUtils
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.FraSedA003Mapper
import org.springframework.util.CollectionUtils
import java.util.stream.Collectors
import java.util.stream.Stream

class SedGrunnlagMapperA003 : FraSedA003Mapper(), NyttLovvalgSedGrunnlagMapper<MedlemskapA003> {
    override fun map(sed: SED): SedGrunnlagA003Dto {
        val medlemskap = hentMedlemskap(sed)

        val sedGrunnlagDto = SedGrunnlagA003Dto(super<NyttLovvalgSedGrunnlagMapper>.map(sed))
        sedGrunnlagDto.sedType = SedType.A003.name
        sedGrunnlagDto.lovvalgsperioder = java.util.List.of(hentLovvalgsperiode(medlemskap))
        sedGrunnlagDto.overgangsregelbestemmelser = mapOvergangsregelbestemmelse(medlemskap)

        val arbeidsgivere = hentArbeidsgivere(sed)
        val norskeVirksomheter = arbeidsgivere.stream()
            .filter { arbeidsgiver: Arbeidsgiver -> erNorskArbeidsgiver(arbeidsgiver) }
            .map { arbeidsgiver: Arbeidsgiver? -> Virksomhet.av(arbeidsgiver) }
            .collect(Collectors.toList())
        sedGrunnlagDto.norskeArbeidsgivendeVirksomheter = norskeVirksomheter

        val utenlandskeVirksomheter = arbeidsgivere.stream()
            .filter { arbeidsgiver: Arbeidsgiver -> erUtenlandskArbeidsgiver(arbeidsgiver) }
            .map { arbeidsgiver: Arbeidsgiver? -> Virksomhet.av(arbeidsgiver) }
            .collect(Collectors.toList())
        sedGrunnlagDto.arbeidsgivendeVirksomheter = utenlandskeVirksomheter

        return sedGrunnlagDto
    }

    private fun mapOvergangsregelbestemmelse(medlemskap: MedlemskapA003): List<Bestemmelse> {
        return StreamUtils.nullableStream(medlemskap.gjeldendereglerEC883)
            .map { bestemmelse: String? -> Bestemmelse.fraString(bestemmelse) }.collect(Collectors.toList())
    }

    private fun hentArbeidsgivere(sed: SED): List<Arbeidsgiver> {
        return Stream.concat(
            hentArbeidsgivere(sed.nav!!)!!.stream(),
            hentAndrelandArbeidsgivere(hentMedlemskap(sed))!!.stream()
        ).collect(Collectors.toList())
    }

    override fun hentPeriode(medlemskap: MedlemskapA003): Periode {
        return hentPeriode(medlemskap.vedtak!!.gjelderperiode!!)
    }

    companion object {
        private fun hentArbeidsgivere(nav: Nav): List<Arbeidsgiver>? {
            if (nav.arbeidsgiver != null) {
                return nav.arbeidsgiver
            }
            return emptyList()
        }

        private fun hentAndrelandArbeidsgivere(medlemskap: MedlemskapA003): List<Arbeidsgiver>? {
            if (medlemskap.andreland != null && !CollectionUtils.isEmpty(medlemskap.andreland!!.arbeidsgiver)) {
                return medlemskap.andreland!!.arbeidsgiver
            }
            return emptyList()
        }

        private fun erNorskArbeidsgiver(arbeidsgiver: Arbeidsgiver): Boolean {
            return "NO".equals(hentArbeidsgiverLand(arbeidsgiver), ignoreCase = true)
        }

        private fun erUtenlandskArbeidsgiver(arbeidsgiver: Arbeidsgiver): Boolean {
            return !erNorskArbeidsgiver(arbeidsgiver)
        }

        private fun hentArbeidsgiverLand(arbeidsgiver: Arbeidsgiver?): String? {
            if (arbeidsgiver?.adresse == null) {
                return null
            }

            return arbeidsgiver.adresse!!.land
        }
    }
}
