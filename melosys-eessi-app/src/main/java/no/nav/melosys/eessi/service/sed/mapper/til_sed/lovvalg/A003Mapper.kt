package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.Andreland
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010
import no.nav.melosys.eessi.models.sed.nav.VedtakA003

class A003Mapper : LovvalgSedMapper<MedlemskapA003> {
    override fun getSedType() = SedType.A003

    override fun getMedlemskap(sedData: SedDataDto) = MedlemskapA003(
        vedtak = getVedtak(sedData),
        andreland = getAndreLand(sedData),
        relevantartikkelfor8832004eller9872009 = sedData.lovvalgsperioder
            ?.firstOrNull()?.bestemmelse?.value
    )

    private fun getVedtak(sedData: SedDataDto): VedtakA003 {
        val lovvalgsperiode = sedData.finnLovvalgsperiode().orElse(null)

        return VedtakA003(
            land = lovvalgsperiode?.lovvalgsland,
            gjelderperiode = lovvalgsperiode?.let { getPeriode(it) },
            gjeldervarighetyrkesaktivitet = "ja"
        ).also {
            setVedtaksdata(it, sedData.vedtakDto)
        }
    }

    private fun getPeriode(lovvalgsperiode: Lovvalgsperiode) = PeriodeA010(
        startdato = formaterDato(lovvalgsperiode.fom ?: throw IllegalArgumentException("lovvalgsperiode.fom kan ikke være null")),
        sluttdato = formaterDato(lovvalgsperiode.tom ?: throw IllegalArgumentException("lovvalgsperiode.tom kan ikke være null"))
    )

    private fun getAndreLand(sedData: SedDataDto) = Andreland(
        arbeidsgiver = hentArbeidsgivereIkkeILand(
            virksomheter = sedData.arbeidsgivendeVirksomheter.orEmpty(),
            landkode = sedData.finnLovvalgslandDefaultNO()
        )
    )
}
