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
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.FraSedA003Mapper

class SedGrunnlagMapperA003 : FraSedA003Mapper(), NyttLovvalgSedGrunnlagMapper<MedlemskapA003> {
    override fun map(sed: SED): SedGrunnlagA003Dto {
        val medlemskap = hentMedlemskap(sed)

        val sedGrunnlagDto = SedGrunnlagA003Dto(super.map(sed)).apply {
            sedType = SedType.A003.name
            lovvalgsperioder = listOf(hentLovvalgsperiode(medlemskap))
            overgangsregelbestemmelser = mapOvergangsregelbestemmelse(medlemskap)
            norskeArbeidsgivendeVirksomheter = hentArbeidsgivere(sed).filter { erNorskArbeidsgiver(it) }
                .map { Virksomhet.av(it) }
            arbeidsgivendeVirksomheter = hentArbeidsgivere(sed).filter { erUtenlandskArbeidsgiver(it) }
                .map { Virksomhet.av(it) }
        }

        return sedGrunnlagDto
    }

    private fun mapOvergangsregelbestemmelse(medlemskap: MedlemskapA003): List<Bestemmelse> =
        medlemskap.gjeldendereglerEC883?.map { Bestemmelse.fraString(it)!! } ?: emptyList()

    private fun hentArbeidsgivere(sed: SED): List<Arbeidsgiver> =
        hentArbeidsgivere(sed.nav!!) + hentAndrelandArbeidsgivere(hentMedlemskap(sed))

    override fun hentPeriode(medlemskap: MedlemskapA003): Periode =
        hentPeriode(medlemskap.vedtak!!.gjelderperiode!!)


    companion object {
        private fun hentArbeidsgivere(nav: Nav): List<Arbeidsgiver> =
            nav.arbeidsgiver ?: emptyList()

        private fun hentAndrelandArbeidsgivere(medlemskap: MedlemskapA003): List<Arbeidsgiver> =
            medlemskap.andreland?.arbeidsgiver.orEmpty()

        private fun erNorskArbeidsgiver(arbeidsgiver: Arbeidsgiver): Boolean =
            hentArbeidsgiverLand(arbeidsgiver)?.equals("NO", ignoreCase = true) == true

        private fun erUtenlandskArbeidsgiver(arbeidsgiver: Arbeidsgiver): Boolean =
            !erNorskArbeidsgiver(arbeidsgiver)

        private fun hentArbeidsgiverLand(arbeidsgiver: Arbeidsgiver?): String? =
            arbeidsgiver?.adresse?.land
    }
}
