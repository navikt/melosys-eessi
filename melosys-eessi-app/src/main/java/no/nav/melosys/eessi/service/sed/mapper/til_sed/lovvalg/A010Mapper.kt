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

class A010Mapper : LovvalgSedMapper<MedlemskapA010> {
    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA010 {
        val medlemskap = MedlemskapA010()
        val lovvalgsperiode = sedData.finnLovvalgsperiode()

        lovvalgsperiode.ifPresent { value: Lovvalgsperiode -> medlemskap.meldingomlovvalg = hentMeldingOmLovvalg(value) }

        medlemskap.vedtak = hentVedtak(sedData)
        medlemskap.andreland = getAndreland(sedData)
        return medlemskap
    }

    private fun hentVedtak(sedDataDto: SedDataDto): VedtakA010 {
        val vedtak = VedtakA010()
        val lovvalgsperiode = sedDataDto.finnLovvalgsperiode()
        if (lovvalgsperiode.isPresent) {
            vedtak.gjelderperiode = hentPeriode(lovvalgsperiode.get())
            vedtak.land = lovvalgsperiode.get().lovvalgsland
        }

        setVedtaksdata(vedtak, sedDataDto.vedtakDto)
        vedtak.gjeldervarighetyrkesaktivitet = "ja"
        return vedtak
    }

    private fun getAndreland(sedData: SedDataDto): Utsendingsland {
        val lovvalgsland = sedData.finnLovvalgslandDefaultNO()
        val utsendingsland = Utsendingsland()
        utsendingsland.arbeidsgiver = hentArbeidsgivereIkkeILand(sedData.arbeidsgivendeVirksomheter!!, lovvalgsland)
        return utsendingsland
    }

    private fun hentMeldingOmLovvalg(lovvalgsperiode: Lovvalgsperiode): MeldingOmLovvalg {
        val meldingOmLovvalg = MeldingOmLovvalg()
        meldingOmLovvalg.artikkel = tilA010Bestemmelse(lovvalgsperiode)
        return meldingOmLovvalg
    }

    private fun tilA010Bestemmelse(lovvalgsperiode: Lovvalgsperiode): String {
        if (LOVLIGE_BESTEMMELSER_A010.contains(lovvalgsperiode.bestemmelse)) {
            return lovvalgsperiode.bestemmelse!!.value
        } else if (lovvalgsperiode.harTilleggsbestemmelse() && LOVLIGE_BESTEMMELSER_A010.contains(lovvalgsperiode.tilleggsBestemmelse)) {
            return lovvalgsperiode.tilleggsBestemmelse!!.value
        }

        throw MappingException("Kan ikke mappe til bestemmelse i A010 for lovvalgsperiode {}")
    }

    private fun hentPeriode(lovvalgsperiode: Lovvalgsperiode): PeriodeA010 {
        val periode = PeriodeA010()
        periode.startdato = formaterDato(lovvalgsperiode.fom!!)
        periode.sluttdato = formaterDato(lovvalgsperiode.tom!!)
        return periode
    }

    override fun getSedType() = SedType.A010


    companion object {
        private val LOVLIGE_BESTEMMELSER_A010: Set<Bestemmelse?> = java.util.Set.of(
            Bestemmelse.ART_11_3_b,
            Bestemmelse.ART_11_3_c,
            Bestemmelse.ART_11_3_d,
            Bestemmelse.ART_11_4,
            Bestemmelse.ART_11_5,
            Bestemmelse.ART_15
        )
    }
}
