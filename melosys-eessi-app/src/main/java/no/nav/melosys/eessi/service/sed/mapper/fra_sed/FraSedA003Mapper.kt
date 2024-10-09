package no.nav.melosys.eessi.service.sed.mapper.fra_sed

import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper.mapTilNavLandkode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class FraSedA003Mapper : NyttLovvalgSedMapper<MedlemskapA003> {
    override fun hentLovvalgsland(medlemskap: MedlemskapA003): String? {
        return mapTilNavLandkode(medlemskap.vedtak!!.land)
    }

    override fun hentLovvalgsbestemmelse(medlemskap: MedlemskapA003): String? {
        return medlemskap.relevantartikkelfor8832004eller9872009
    }

    override fun sedErEndring(medlemskap: MedlemskapA003): Boolean {
        val erEndring = !"ja".equals(medlemskap.vedtak!!.eropprinneligvedtak, ignoreCase = true)
        log.info(
            "sedErEndring i A003 er {}, med erendringsvedtak: {} og eropprinneligvedtak: {}",
            erEndring,
            medlemskap.vedtak!!.erendringsvedtak,
            medlemskap.vedtak!!.eropprinneligvedtak
        )
        return erEndring
    }

    override fun hentMedlemskap(sed: SED): MedlemskapA003 {
        return sed.medlemskap as MedlemskapA003
    }

    override fun erMidlertidigBestemmelse(medlemskap: MedlemskapA003): Boolean {
        return "ja" == medlemskap.isDeterminationProvisional
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(FraSedA003Mapper::class.java)
    }
}
