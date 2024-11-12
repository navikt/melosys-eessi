package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.NyttLovvalgSedMapper

interface NyttLovvalgEessiMeldingMapper<T : Medlemskap> : NyttLovvalgSedMapper<T>, MelosysEessiMeldingMapper {
    override fun map(eessiMeldingParams: EessiMeldingParams): MelosysEessiMelding =
        super.map(eessiMeldingParams).apply {
            val medlemskap = hentMedlemskap(eessiMeldingParams.sed)

            periode = mapPeriode(medlemskap)

            lovvalgsland = hentLovvalgsland(medlemskap)
            artikkel = hentLovvalgsbestemmelse(medlemskap)
            erEndring = eessiMeldingParams.sedErEndring || sedErEndring(medlemskap)
            midlertidigBestemmelse = erMidlertidigBestemmelse(medlemskap)
            anmodningUnntak = hentAnmodningUnntak(medlemskap)
        }

    fun mapPeriode(medlemskap: T): Periode
}
