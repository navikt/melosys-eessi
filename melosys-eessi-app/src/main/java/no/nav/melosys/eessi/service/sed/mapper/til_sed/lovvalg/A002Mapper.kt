package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg

import no.nav.melosys.eessi.controller.dto.SedDataDto
import no.nav.melosys.eessi.models.SedType
import no.nav.melosys.eessi.models.exception.MappingException
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning
import no.nav.melosys.eessi.models.sed.medlemskap.impl.UnntakA002
import no.nav.melosys.eessi.models.sed.medlemskap.impl.VedtakA002
import no.nav.melosys.eessi.models.sed.nav.Fastperiode
import no.nav.melosys.eessi.models.sed.nav.Periode
import java.time.LocalDate

class A002Mapper : LovvalgSedMapper<MedlemskapA002> {
    override fun getMedlemskap(sedData: SedDataDto): MedlemskapA002 {
        val svarAnmodningUnntak = sedData.svarAnmodningUnntak
            ?: throw MappingException("Trenger SvarAnmodningUnntak for å opprette A002")

        val medlemskapA002 = MedlemskapA002()
        medlemskapA002.unntak = getUnntak(
            svarAnmodningUnntak.begrunnelse,
            svarAnmodningUnntak.beslutning!!,
            svarAnmodningUnntak.delvisInnvilgetPeriode!!.fom,
            svarAnmodningUnntak.delvisInnvilgetPeriode!!.tom
        )

        return medlemskapA002
    }

    private fun getUnntak(
        begrunnelse: String?,
        resultat: SvarAnmodningUnntakBeslutning,
        delvisInnvilgetFom: LocalDate?,
        delvisInnvilgetTom: LocalDate?
    ): UnntakA002 {
        val unntak = UnntakA002()
        unntak.vedtak = getVedtak(begrunnelse, resultat, delvisInnvilgetFom, delvisInnvilgetTom)
        return unntak
    }

    private fun getVedtak(
        begrunnelse: String?,
        resultat: SvarAnmodningUnntakBeslutning,
        delvisInnvilgetFom: LocalDate?,
        delvisInnvilgetTom: LocalDate?
    ): VedtakA002 {
        val vedtak = VedtakA002()
        vedtak.annenperiode = getPeriode(delvisInnvilgetFom, delvisInnvilgetTom)
        vedtak.begrunnelse = begrunnelse
        vedtak.resultat = resultat.rinaKode
        return vedtak
    }

    private fun getPeriode(delvisInnvilgetFom: LocalDate?, delvisInnvilgetTom: LocalDate?): Periode {
        val periode = Periode()
        periode.fastperiode = getFastperiode(delvisInnvilgetFom, delvisInnvilgetTom)
        return periode
    }

    private fun getFastperiode(delvisInnvilgetFom: LocalDate?, delvisInnvilgetTom: LocalDate?): Fastperiode {
        val fastperiode = Fastperiode()
        fastperiode.startdato = formaterDatoEllerNull(delvisInnvilgetFom)
        fastperiode.sluttdato = formaterDatoEllerNull(delvisInnvilgetTom)
        return fastperiode
    }

    private fun formaterDatoEllerNull(dato: LocalDate?): String? {
        if (dato == null) {
            return null
        }
        return formaterDato(dato)
    }

    override fun getSedType() = SedType.A002
}
