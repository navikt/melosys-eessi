package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import mu.KotlinLogging
import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010

private val log = KotlinLogging.logger { }

internal class MelosysEessiMeldingMapperA010 : NyttLovvalgEessiMeldingMapper<MedlemskapA010> {
    override fun mapPeriode(medlemskap: MedlemskapA010?): Periode {
        val periode = hentPeriode(medlemskap!!.vedtak!!.gjelderperiode!!)
        return Periode(periode.fom, periode.tom)
    }

    override fun hentLovvalgsland(medlemskap: MedlemskapA010): String? = medlemskap.vedtak!!.land

    override fun hentLovvalgsbestemmelse(medlemskap: MedlemskapA010): String? = medlemskap.meldingomlovvalg!!.artikkel

    override fun sedErEndring(medlemskap: MedlemskapA010): Boolean {
        val erEndring = "nei".equals(medlemskap.vedtak!!.eropprinneligvedtak, ignoreCase = true)
        log.info(
            "sedErEndring i A010 er $erEndring, med erendringsvedtak: ${
                medlemskap.vedtak!!.erendringsvedtak
            } og eropprinneligvedtak: ${medlemskap.vedtak!!.eropprinneligvedtak}",
        )
        return erEndring
    }

    override fun hentMedlemskap(sed: SED): MedlemskapA010 = sed.medlemskap as MedlemskapA010
}
