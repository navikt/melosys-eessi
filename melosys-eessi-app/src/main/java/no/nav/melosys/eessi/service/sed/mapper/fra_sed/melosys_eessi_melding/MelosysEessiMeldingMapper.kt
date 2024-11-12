package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.*
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper

interface MelosysEessiMeldingMapper {
    fun map(eessiMeldingQuery: EessiMeldingQuery) = MelosysEessiMelding(
        sedId = eessiMeldingQuery.rinaDokumentID,
        rinaSaksnummer = eessiMeldingQuery.rinaSaksnummer,
        avsender = Avsender(eessiMeldingQuery.avsenderID, LandkodeMapper.mapTilNavLandkode(eessiMeldingQuery.landkode)),
        journalpostId = eessiMeldingQuery.journalpostID,
        dokumentId = eessiMeldingQuery.dokumentID,
        gsakSaksnummer = eessiMeldingQuery.gsakSaksnummer?.toLongOrNull(),
        aktoerId = eessiMeldingQuery.aktoerId,
        ytterligereInformasjon = eessiMeldingQuery.sed.nav?.ytterligereinformasjon,
        sedType = eessiMeldingQuery.sedType,
        bucType = eessiMeldingQuery.bucType,
        erEndring = eessiMeldingQuery.sedErEndring,
        sedVersjon = eessiMeldingQuery.sedVersjon
    ).apply {
        if (inneholderStatsborgerskap(eessiMeldingQuery.sed)) {
            statsborgerskap = mapStatsborgerskap(
                eessiMeldingQuery.sed.nav?.bruker?.person?.hentStatsborgerksapsliste() ?: emptyList()
            )
        }
        eessiMeldingQuery.sed.nav?.let { nav ->
            nav.arbeidssted?.let { arbeidsstedList ->
                arbeidssteder = arbeidsstedList.map(::Arbeidssted)
            }
            nav.arbeidsland?.let { arbeidslandList ->
                arbeidsland = arbeidslandList.map(::Arbeidsland)
            }
        }
    }

    fun inneholderStatsborgerskap(sed: SED?): Boolean =
        sed?.nav?.bruker?.person?.statsborgerskap != null

    fun mapStatsborgerskap(statsborgerskapListe: Collection<String>): List<Statsborgerskap> =
        statsborgerskapListe.map(::Statsborgerskap)
}
