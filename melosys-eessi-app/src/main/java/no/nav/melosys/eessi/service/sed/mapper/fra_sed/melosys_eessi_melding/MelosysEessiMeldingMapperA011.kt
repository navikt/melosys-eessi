package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA011
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning

internal class MelosysEessiMeldingMapperA011 : SvarAnmodningUnntakEessiMeldingMapper<MedlemskapA011?>() {
    override fun hentMedlemskap(sed: SED?): MedlemskapA011? = sed?.medlemskap as MedlemskapA011?

    override fun hentBeslutning(medlemskap: MedlemskapA011?): SvarAnmodningUnntakBeslutning = SvarAnmodningUnntakBeslutning.INNVILGELSE

    override fun hentBegrunnelse(medlemskap: MedlemskapA011?): String? = null

    override fun hentDelvisInnvilgetPeriode(medlemskap: MedlemskapA011?): Periode? = null
}
