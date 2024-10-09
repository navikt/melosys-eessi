package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA010
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class MelosysEessiMeldingMapperA010 : NyttLovvalgEessiMeldingMapper<MedlemskapA010> {
    override fun mapPeriode(medlemskap: MedlemskapA010?): Periode {
        val periode = hentPeriode(medlemskap!!.vedtak!!.gjelderperiode!!)
        return Periode(periode.fom, periode.tom)
    }

    override fun hentLovvalgsland(medlemskap: MedlemskapA010): String? {
        return medlemskap.vedtak!!.land
    }

    override fun hentLovvalgsbestemmelse(medlemskap: MedlemskapA010): String? {
        return medlemskap.meldingomlovvalg!!.artikkel
    }

    override fun sedErEndring(medlemskap: MedlemskapA010): Boolean {
        val erEndring = "nei".equals(medlemskap.vedtak!!.eropprinneligvedtak, ignoreCase = true)
        log.info(
            "sedErEndring i A010 er {}, med erendringsvedtak: {} og eropprinneligvedtak: {}",
            erEndring,
            medlemskap.vedtak!!.erendringsvedtak,
            medlemskap.vedtak!!.eropprinneligvedtak
        )
        return erEndring
    }

    override fun hentMedlemskap(sed: SED): MedlemskapA010 {
        return sed.medlemskap as MedlemskapA010
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(MelosysEessiMeldingMapperA010::class.java)
    }
}
