package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding
import no.nav.melosys.eessi.kafka.producers.model.Periode
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.models.sed.medlemskap.Medlemskap
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.NyttLovvalgSedMapper

interface NyttLovvalgEessiMeldingMapper<T : Medlemskap> : NyttLovvalgSedMapper<T>, MelosysEessiMeldingMapper {
    override fun map(
        aktoerId: String?,
        sed: SED?,
        rinaDokumentID: String?,
        rinaSaksnummer: String?,
        sedType: String?,
        bucType: String?,
        avsenderID: String?,
        landkode: String?,
        journalpostID: String?,
        dokumentID: String?,
        gsakSaksnummer: String?,
        sedErEndring: Boolean,
        sedVersjon: String?
    ): MelosysEessiMelding {
        val melosysEessiMelding = super.map(
            aktoerId, sed, rinaDokumentID,
            rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID, dokumentID, gsakSaksnummer,
            sedErEndring, sedVersjon
        )

        val medlemskap = hentMedlemskap(sed!!)

        melosysEessiMelding.periode = mapPeriode(medlemskap)

        melosysEessiMelding.lovvalgsland = hentLovvalgsland(medlemskap)
        melosysEessiMelding.artikkel = hentLovvalgsbestemmelse(medlemskap)
        melosysEessiMelding.isErEndring = sedErEndring || sedErEndring(medlemskap)
        melosysEessiMelding.isMidlertidigBestemmelse = erMidlertidigBestemmelse(medlemskap)
        melosysEessiMelding.anmodningUnntak = hentAnmodningUnntak(medlemskap)

        return melosysEessiMelding
    }

    fun mapPeriode(medlemskap: T): Periode
}
