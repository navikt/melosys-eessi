package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010
import no.nav.melosys.eessi.models.sed.nav.MeldingOmLovvalg
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland
import no.nav.melosys.eessi.models.sed.nav.VedtakA010
import kotlin.jvm.optionals.getOrNull

class A010Mapper : LovvalgSedMapper<MedlemskapA010> {

    override fun getSedType() = SedType.A010

    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA010 {
        val lovvalgsland = sedData.finnLovvalgslandDefaultNO()
        val lovvalgsperiode = sedData.finnLovvalgsperiode().getOrNull()

        return MedlemskapA010(
            meldingomlovvalg = sedData.finnLovvalgsperiode().getOrNull()?.let {
                MeldingOmLovvalg(artikkel = tilA010Bestemmelse(it))
            },
            vedtak = VedtakA010(
                gjelderperiode = lovvalgsperiode?.let { hentPeriode(it) },
                land = lovvalgsperiode?.lovvalgsland,
                gjeldervarighetyrkesaktivitet = "ja"
            ).also {
                setVedtaksdata(it, sedData.vedtakDto)
            },
            andreland = Utsendingsland(
                arbeidsgiver = hentArbeidsgivereIkkeILand(sedData.arbeidsgivendeVirksomheter.orEmpty(), lovvalgsland)
            )
        )
    }

    private fun tilA010Bestemmelse(lovvalgsperiode: Lovvalgsperiode): String {
        val bestemmelse = lovvalgsperiode.bestemmelse ?: throw IllegalArgumentException("lovvalgsperiode.bestemmelse kan ikke være null")

        return when {
            LOVLIGE_BESTEMMELSER_A010.contains(bestemmelse) ->
                bestemmelse.value

            lovvalgsperiode.harTilleggsbestemmelse() &&
                LOVLIGE_BESTEMMELSER_A010.contains(lovvalgsperiode.tilleggsBestemmelse) ->
                lovvalgsperiode.tilleggsBestemmelse!!.value

            else -> throw MappingException("Kan ikke mappe til bestemmelse i A010 for lovvalgsperiode $lovvalgsperiode")
        }
    }

    private fun hentPeriode(lovvalgsperiode: Lovvalgsperiode) = PeriodeA010(
        startdato = formaterDato(lovvalgsperiode.fom ?: throw IllegalArgumentException("lovvalgsperiode.fom kan ikke være null")),
        sluttdato = formaterDato(lovvalgsperiode.tom ?: throw IllegalArgumentException("lovvalgsperiode.fom kan ikke være null"))
    )

    companion object {
        private val LOVLIGE_BESTEMMELSER_A010 = setOf(
            Bestemmelse.ART_11_3_b,
            Bestemmelse.ART_11_3_c,
            Bestemmelse.ART_11_3_d,
            Bestemmelse.ART_11_4,
            Bestemmelse.ART_11_5,
            Bestemmelse.ART_15
        )
    }
}
