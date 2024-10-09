package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.kafka.producers.model.SvarAnmodningUnntak
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning

abstract class SvarAnmodningUnntakEessiMeldingMapper<T : Medlemskap?> : MelosysEessiMeldingMapper {
    override fun map(
        aktoerId: String?, sed: SED?, rinaDokumentID: String?, rinaSaksnummer: String?,
        sedType: String?, bucType: String?, avsenderID: String?,
        landkode: String?, journalpostID: String?, dokumentID: String?, gsakSaksnummer: String?,
        sedErEndring: Boolean?, sedVersjon: String?
    ): MelosysEessiMelding {
        val melosysEessiMelding = super.map(
            aktoerId, sed, rinaDokumentID,
            rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, dokumentID, gsakSaksnummer,
            sedErEndring, sedVersjon
        )

        val medlemskap = hentMedlemskap(sed)

        val svarAnmodningUnntak = SvarAnmodningUnntak()
        svarAnmodningUnntak.beslutning = hentBeslutning(medlemskap)
        svarAnmodningUnntak.begrunnelse = hentBegrunnelse(medlemskap)
        svarAnmodningUnntak.delvisInnvilgetPeriode = hentDelvisInnvilgetPeriode(medlemskap)

        melosysEessiMelding.svarAnmodningUnntak = svarAnmodningUnntak
        return melosysEessiMelding
    }

    abstract fun hentMedlemskap(sed: SED?): T

    abstract fun hentBeslutning(medlemskap: T): SvarAnmodningUnntakBeslutning?

    abstract fun hentBegrunnelse(medlemskap: T): String?

    abstract fun hentDelvisInnvilgetPeriode(medlemskap: T): Periode?
}
