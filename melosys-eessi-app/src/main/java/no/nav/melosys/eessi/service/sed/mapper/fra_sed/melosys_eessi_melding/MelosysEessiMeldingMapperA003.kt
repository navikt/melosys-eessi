package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.FraSedA003Mapper

internal class MelosysEessiMeldingMapperA003 : FraSedA003Mapper(), NyttLovvalgEessiMeldingMapper<MedlemskapA003> {
    override fun mapPeriode(medlemskap: MedlemskapA003?): Periode {
        val periode = hentPeriode(medlemskap?.vedtak!!.gjelderperiode!!)
        return Periode(periode.fom, periode.tom)
    }
}
