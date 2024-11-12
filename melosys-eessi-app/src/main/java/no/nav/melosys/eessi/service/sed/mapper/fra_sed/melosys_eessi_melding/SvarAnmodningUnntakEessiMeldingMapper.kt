package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.kafka.producers.model.SvarAnmodningUnntak
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning

abstract class SvarAnmodningUnntakEessiMeldingMapper<T : Medlemskap?> : MelosysEessiMeldingMapper {
    override fun map(eessiMeldingQuery: EessiMeldingQuery): MelosysEessiMelding =
        super.map(eessiMeldingQuery).apply {
            val medlemskap = hentMedlemskap(eessiMeldingQuery.sed)

            this.svarAnmodningUnntak = SvarAnmodningUnntak(
                beslutning = hentBeslutning(medlemskap),
                begrunnelse = hentBegrunnelse(medlemskap),
                delvisInnvilgetPeriode = hentDelvisInnvilgetPeriode(medlemskap)
            )
        }

    abstract fun hentMedlemskap(sed: SED?): T

    abstract fun hentBeslutning(medlemskap: T): SvarAnmodningUnntakBeslutning?

    abstract fun hentBegrunnelse(medlemskap: T): String?

    abstract fun hentDelvisInnvilgetPeriode(medlemskap: T): Periode?
}
