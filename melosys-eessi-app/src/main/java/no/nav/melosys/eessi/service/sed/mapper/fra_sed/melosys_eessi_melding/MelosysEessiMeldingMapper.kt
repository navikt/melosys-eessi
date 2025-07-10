package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding

import no.nav.melosys.eessi.kafka.producers.model.*
import no.nav.melosys.eessi.models.sed.SED
import no.nav.melosys.eessi.service.sed.LandkodeMapper

interface MelosysEessiMeldingMapper {
    fun map(eessiMeldingParams: EessiMeldingParams) = MelosysEessiMelding(
        sedId = eessiMeldingParams.rinaDokumentID,
        rinaSaksnummer = eessiMeldingParams.rinaSaksnummer,
        avsender = Avsender(eessiMeldingParams.avsenderID, LandkodeMapper.mapTilNavLandkode(eessiMeldingParams.landkode)),
        journalpostId = eessiMeldingParams.journalpostID,
        dokumentId = eessiMeldingParams.dokumentID,
        gsakSaksnummer = eessiMeldingParams.gsakSaksnummer?.toLongOrNull(),
        aktoerId = eessiMeldingParams.aktoerId,
        ytterligereInformasjon = eessiMeldingParams.sed.nav?.ytterligereinformasjon,
        sedType = eessiMeldingParams.sedType,
        bucType = eessiMeldingParams.bucType,
        erEndring = eessiMeldingParams.sedErEndring,
        sedVersjon = eessiMeldingParams.sedVersjon
    ).apply {
        if (inneholderStatsborgerskap(eessiMeldingParams.sed)) {
            statsborgerskap = mapStatsborgerskap(
                eessiMeldingParams.sed.nav?.bruker?.person?.hentStatsborgerksapsliste() ?: emptyList()
            )
        }
        eessiMeldingParams.sed.nav?.let { nav ->
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
