package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import mu.KotlinLogging
import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.DatoUtils.tilLocalDate
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009
import java.time.LocalDate

private val log = KotlinLogging.logger { }

internal class MelosysEessiMeldingMapperA009 : NyttLovvalgEessiMeldingMapper<MedlemskapA009> {
    override fun sedErEndring(medlemskap: MedlemskapA009): Boolean {
        val erEndring = !"ja".equals(medlemskap.vedtak!!.eropprinneligvedtak, ignoreCase = true)
        log.info(
            "sedErEndring i A009 er $erEndring, med erendringsvedtak: ${
                medlemskap.vedtak!!.erendringsvedtak
            } og eropprinneligvedtak: ${medlemskap.vedtak!!.eropprinneligvedtak}",
        )
        return erEndring
    }

    override fun hentMedlemskap(sed: SED): MedlemskapA009 = sed.medlemskap as MedlemskapA009

    override fun hentLovvalgsbestemmelse(medlemskap: MedlemskapA009): String? = medlemskap.vedtak!!.artikkelforordning

    override fun hentLovvalgsland(medlemskap: MedlemskapA009): String? = medlemskap.vedtak!!.land

    override fun mapPeriode(medlemskap: MedlemskapA009): Periode {
        val fom: LocalDate
        val tom: LocalDate?
        val periode = medlemskap.vedtak!!.gjelderperiode
        if (periode!!.erAapenPeriode()) {
            val aapenPeriode = periode.aapenperiode
            fom = tilLocalDate(aapenPeriode!!.startdato!!)
            tom = null
        } else {
            val fastperiode = periode.fastperiode
            fom = tilLocalDate(fastperiode!!.startdato!!)
            tom = tilLocalDate(fastperiode.sluttdato!!)
        }
        return Periode(fom, tom)
    }
}
