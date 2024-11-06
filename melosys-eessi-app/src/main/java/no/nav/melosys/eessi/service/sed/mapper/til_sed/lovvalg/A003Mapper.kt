package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.models.sed.nav.Andreland
import no.nav.melosys.eessi.models.sed.nav.PeriodeA010
import no.nav.melosys.eessi.models.sed.nav.VedtakA003

class A003Mapper : LovvalgSedMapper<MedlemskapA003> {
    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA003 {
        val medlemskap = MedlemskapA003()

        medlemskap.vedtak = getVedtak(sedData)
        medlemskap.andreland = getAndreLand(sedData)

        if (!sedData.lovvalgsperioder!!.isEmpty()) {
            medlemskap.relevantartikkelfor8832004eller9872009 = sedData.lovvalgsperioder!![0].bestemmelse!!.value
        }

        return medlemskap
    }

    private fun getVedtak(sedData: SedDataDto): VedtakA003 {
        val vedtak = VedtakA003()
        val lovvalgsperiode = sedData.finnLovvalgsperiode()

        if (lovvalgsperiode.isPresent) {
            vedtak.land = lovvalgsperiode.get().lovvalgsland
            vedtak.gjelderperiode = getPeriode(lovvalgsperiode.get())
        }

        vedtak.gjeldervarighetyrkesaktivitet = "ja"
        setVedtaksdata(vedtak, sedData.vedtakDto)

        return vedtak
    }

    private fun getPeriode(lovvalgsperiode: Lovvalgsperiode): PeriodeA010 {
        val periode = PeriodeA010()
        periode.startdato = formaterDato(lovvalgsperiode.fom!!)
        periode.sluttdato = formaterDato(lovvalgsperiode.tom!!)
        return periode
    }


    private fun getAndreLand(sedData: SedDataDto): Andreland {
        val lovvalgsland = sedData.finnLovvalgslandDefaultNO()
        val andreland = Andreland()
        andreland.arbeidsgiver = hentArbeidsgivereIkkeILand(sedData.arbeidsgivendeVirksomheter!!, lovvalgsland)
        return andreland
    }

    override fun getSedType() = SedType.A003
}
