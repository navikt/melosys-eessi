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

    override fun getSedType() = SedType.A009

    override fun getMedlemskap(sedData: SedDataDto) = MedlemskapA009(
        vedtak = getVedtak(sedData),
        andreland = getAndreland(sedData),
        utsendingsland = getUtsendingsland(sedData)
    )

    private fun getVedtak(sedDataDto: SedDataDto): VedtakA009 {
        val lovvalgsperiode = sedDataDto.finnLovvalgsperiode()

        val gjelderperiode = Periode(
            //Vil alltid være fast periode
            fastperiode = lovvalgsperiode?.let { lagFastPeriodeFraLovvalgsPeriode(it) }
        )

        lovvalgsperiode?.let {
            if (!erGyldigLovvalgbestemmelse(it.bestemmelse)) {
                throw MappingException("Lovvalgsbestemmelse er ikke av artikkel 12!")
            }
        }

        return VedtakA009(
            land = lovvalgsperiode?.lovvalgsland?.let { mapTilLandkodeIso2(it) },
            artikkelforordning = lovvalgsperiode?.bestemmelse?.value,
            gjelderperiode = gjelderperiode,
            gjeldervarighetyrkesaktivitet = "nei"
        ).also {
            setVedtaksdata(it, sedDataDto.vedtakDto)
        }
    }

    private fun lagFastPeriodeFraLovvalgsPeriode(lovvalgsperiode: Lovvalgsperiode) = Fastperiode(
        startdato = lovvalgsperiode.fom.formater(),
        sluttdato = lovvalgsperiode.tom.formater()
    )

    private fun erGyldigLovvalgbestemmelse(bestemmelse: Bestemmelse?): Boolean =
        bestemmelse in listOf(Bestemmelse.ART_12_1, Bestemmelse.ART_12_2)

    private fun getUtsendingsland(sedData: SedDataDto) = Utsendingsland(
        arbeidsgiver = hentArbeidsgivereILand(
            sedData.arbeidsgivendeVirksomheter.orEmpty(),
            landkode = sedData.finnLovvalgslandDefaultNO()
        )
    )

    private fun getAndreland(sedData: SedDataDto) = Utsendingsland(
        arbeidsgiver = hentArbeidsgivereIkkeILand(
            sedData.arbeidsgivendeVirksomheter.orEmpty(),
            landkode = sedData.finnLovvalgslandDefaultNO()
        )
    )
}
