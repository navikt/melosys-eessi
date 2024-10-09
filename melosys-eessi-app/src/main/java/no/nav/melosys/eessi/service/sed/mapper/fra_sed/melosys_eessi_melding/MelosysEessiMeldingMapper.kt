package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.*
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper

interface MelosysEessiMeldingMapper {
    fun map(
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
        sedErEndring: Boolean?, // må være non-null pga. med mock som. TODO: fiks dette
        sedVersjon: String? // samme som over
    ): MelosysEessiMelding {
        return MelosysEessiMelding().apply {
            sedId = rinaDokumentID
            this.rinaSaksnummer = rinaSaksnummer
            avsender = Avsender(avsenderID, LandkodeMapper.mapTilNavLandkode(landkode))
            journalpostId = journalpostID
            dokumentId = dokumentID
            this.gsakSaksnummer = gsakSaksnummer?.toLongOrNull()
            this.aktoerId = aktoerId
            ytterligereInformasjon = sed?.nav?.ytterligereinformasjon

            this.sedType = sedType
            this.bucType = bucType

            if (inneholderStatsborgerskap(sed)) {
                statsborgerskap = mapStatsborgerskap(sed?.nav?.bruker?.person?.hentStatsborgerksapsliste() ?: emptyList())
            }

            sed?.nav?.let { nav ->
                nav.arbeidssted?.let { arbeidsstedList ->
                    arbeidssteder = arbeidsstedList.map(::Arbeidssted)
                }
                nav.arbeidsland?.let { arbeidslandList ->
                    arbeidsland = arbeidslandList.map(::Arbeidsland)
                }
            }
            this.isErEndring = sedErEndring!!
            this.sedVersjon = sedVersjon
        }
    }

    fun inneholderStatsborgerskap(sed: SED?): Boolean =
        sed?.nav?.bruker?.person?.statsborgerskap != null

    fun mapStatsborgerskap(statsborgerskapListe: Collection<String>): List<Statsborgerskap> =
        statsborgerskapListe.map(::Statsborgerskap)
}
