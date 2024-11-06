package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.Bestemmelse
import no.nav.melosys.eessi.controller.dto.Lovvalgsperiode
import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009
import no.nav.melosys.eessi.models.sed.nav.Fastperiode
import no.nav.melosys.eessi.models.sed.nav.Periode
import no.nav.melosys.eessi.models.sed.nav.Utsendingsland
import no.nav.melosys.eessi.models.sed.nav.VedtakA009
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper.mapTilLandkodeIso2

class A009Mapper : LovvalgSedMapper<MedlemskapA009> {
    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA009 {
        val medlemskapA009 = MedlemskapA009()
        medlemskapA009.vedtak = getVedtak(sedData)
        medlemskapA009.andreland = getAndreland(sedData)
        medlemskapA009.utsendingsland = getUtsendingsland(sedData)

        return medlemskapA009
    }

    private fun getVedtak(sedDataDto: SedDataDto): VedtakA009 {
        val vedtak = VedtakA009()
        val lovvalgsperiode = sedDataDto.finnLovvalgsperiode()
        val gjelderperiode = Periode()

        if (lovvalgsperiode.isPresent) {
            vedtak.land = mapTilLandkodeIso2(lovvalgsperiode.get().lovvalgsland)

            //Vil alltid være fast periode
            gjelderperiode.fastperiode = lagFastPeriodeFraLovvalgsPeriode(lovvalgsperiode.get())

            if (!erGyldigLovvalgbestemmelse(lovvalgsperiode.get().bestemmelse)) {
                throw MappingException("Lovvalgsbestemmelse er ikke av artikkel 12!")
            }
            vedtak.artikkelforordning = lovvalgsperiode.get().bestemmelse!!.value
        }

        setVedtaksdata(vedtak, sedDataDto.vedtakDto)
        vedtak.gjeldervarighetyrkesaktivitet = "nei"
        vedtak.gjelderperiode = gjelderperiode
        return vedtak
    }


    private fun lagFastPeriodeFraLovvalgsPeriode(lovvalgsperiode: Lovvalgsperiode): Fastperiode {
        val fastperiode = Fastperiode()
        fastperiode.startdato = formaterDato(lovvalgsperiode.fom!!)
        fastperiode.sluttdato = formaterDato(lovvalgsperiode.tom!!)
        return fastperiode
    }

    private fun erGyldigLovvalgbestemmelse(bestemmelse: Bestemmelse?): Boolean {
        return bestemmelse == Bestemmelse.ART_12_1
                || bestemmelse == Bestemmelse.ART_12_2
    }

    private fun getUtsendingsland(sedData: SedDataDto): Utsendingsland {
        val lovvalgsland = sedData.finnLovvalgslandDefaultNO()
        val utsendingsland = Utsendingsland()
        utsendingsland.arbeidsgiver = hentArbeidsgivereILand(sedData.arbeidsgivendeVirksomheter!!, lovvalgsland)
        return utsendingsland
    }

    private fun getAndreland(sedData: SedDataDto): Utsendingsland {
        val lovvalgsland = sedData.finnLovvalgslandDefaultNO()
        val utsendingsland = Utsendingsland()
        utsendingsland.arbeidsgiver = hentArbeidsgivereIkkeILand(sedData.arbeidsgivendeVirksomheter!!, lovvalgsland)
        return utsendingsland
    }


    override fun getSedType() = SedType.A009

}
