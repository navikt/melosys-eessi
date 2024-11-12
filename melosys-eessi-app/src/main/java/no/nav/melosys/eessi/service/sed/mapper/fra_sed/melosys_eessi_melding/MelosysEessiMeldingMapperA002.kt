package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.DatoUtils.tilLocalDate
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA002
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning.Companion.fraRinaKode

internal class MelosysEessiMeldingMapperA002 : SvarAnmodningUnntakEessiMeldingMapper<MedlemskapA002?>() {
    override fun hentMedlemskap(sed: SED): MedlemskapA002? = sed.medlemskap as MedlemskapA002?

    override fun hentBeslutning(medlemskap: MedlemskapA002?): SvarAnmodningUnntakBeslutning? {
        val resultat = medlemskap?.unntak!!.vedtak!!.resultat

        return fraRinaKode(resultat)
    }

    override fun hentBegrunnelse(medlemskap: MedlemskapA002?): String? = medlemskap?.unntak!!.vedtak!!.begrunnelse

    override fun hentDelvisInnvilgetPeriode(medlemskap: MedlemskapA002?): Periode? {
        if (fraRinaKode(medlemskap?.unntak!!.vedtak!!.resultat) == SvarAnmodningUnntakBeslutning.AVSLAG) {
            return null
        }

        val periode = medlemskap.unntak!!.vedtak!!.annenperiode
        return Periode(tilLocalDate(periode!!.fastperiode!!.startdato!!), tilLocalDate(periode.fastperiode!!.sluttdato!!))
    }
}
