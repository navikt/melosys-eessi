package no.nav.melosys.eessi.service.journalfoering

import no.nav.melosys.eessi.models.SedType
import org.springframework.stereotype.Component

@Component
class JournalpostMetadataService {

    private val metadataMap = mapOf(
        SedType.X001 to JournalpostMetadata("Anmodning om avslutning", UTLAND),
        SedType.X002 to JournalpostMetadata("Anmodning om gjenåpning av avsluttet sak", UTLAND),
        SedType.X003 to JournalpostMetadata("Svar på anmodning om gjenåpning av avsluttet sak", UTLAND),
        SedType.X004 to JournalpostMetadata("Gjenåpne saken", UTLAND),
        SedType.X005 to JournalpostMetadata("Legg til ny institusjon", UTLAND),
        SedType.X006 to JournalpostMetadata("Fjern institusjon", UTLAND),
        SedType.X007 to JournalpostMetadata("Videresende sak", UTLAND),
        SedType.X008 to JournalpostMetadata("Ugyldiggjøre SED", UTLAND),
        SedType.X009 to JournalpostMetadata("Påminnelse", UTLAND),
        SedType.X010 to JournalpostMetadata("Svar på påminnelse", UTLAND),
        SedType.X011 to JournalpostMetadata("Avvis SED", UTLAND),
        SedType.X012 to JournalpostMetadata("Klargjør innhold", UTLAND),
        SedType.X013 to JournalpostMetadata("Svar på anmodning om klargjøring", UTLAND),
        SedType.X050 to JournalpostMetadata("Unntaksfeil", UTLAND),
        SedType.X100 to JournalpostMetadata("Endring av institusjon", UTLAND),
        SedType.A001 to JournalpostMetadata("Søknad om unntak", MEDLEMSKAP),
        SedType.A002 to JournalpostMetadata("Delvis eller fullt avslag på søknad om unntak", MEDLEMSKAP),
        SedType.A003 to JournalpostMetadata("Beslutning om lovvalg", MEDLEMSKAP),
        SedType.A004 to JournalpostMetadata("Uenighet om beslutning om lovvalg", MEDLEMSKAP),
        SedType.A005 to JournalpostMetadata("Anmodning om mer informasjon", MEDLEMSKAP),
        SedType.A006 to JournalpostMetadata("Svar på anmodning om mer informasjon", MEDLEMSKAP),
        SedType.A007 to JournalpostMetadata("Midlertidig beslutning om lovvalg", MEDLEMSKAP),
        SedType.A008 to JournalpostMetadata("Melding om relevant informasjon", MEDLEMSKAP),
        SedType.A009 to JournalpostMetadata("Melding om utstasjonering", MEDLEMSKAP),
        SedType.A010 to JournalpostMetadata("Melding om lovvalg", MEDLEMSKAP),
        SedType.A011 to JournalpostMetadata("Innvilgelse av søknad om unntak", MEDLEMSKAP),
        SedType.A012 to JournalpostMetadata("Godkjenning av lovvalgsbeslutning", MEDLEMSKAP),
        SedType.H001 to JournalpostMetadata("Melding/anmodning om informasjon", UTLAND),
        SedType.H002 to JournalpostMetadata("Svar på anmodning om informasjon", UTLAND),
        SedType.H003 to JournalpostMetadata("Fremlegg/melding om bostedsland", UTLAND),
        SedType.H004 to JournalpostMetadata("Svar på fremlegg om bostedsland/uenighet med vedtak om bostedsland", UTLAND),
        SedType.H005 to JournalpostMetadata("Anmodning om informasjon om bosted", UTLAND),
        SedType.H006 to JournalpostMetadata("Svar på anmodning om informasjon om bosted", UTLAND),
        SedType.H010 to JournalpostMetadata("Melding om endring av lovvalg", MEDLEMSKAP),
        SedType.H011 to JournalpostMetadata("Anmodning om dato for endring av lovvalg", MEDLEMSKAP),
        SedType.H012 to JournalpostMetadata("Svar på anmodning for endring av lovvalg", MEDLEMSKAP),
        SedType.H020 to JournalpostMetadata("Krav om refusjon - administrativ kontroll/medisinsk refusjon", UTLAND),
        SedType.H021 to JournalpostMetadata("Svar på krav om refusjon – administrativ kontroll/medisinsk informasjon", UTLAND),
        SedType.H061 to JournalpostMetadata("Melding/anmodning om personnummer", UTLAND),
        SedType.H062 to JournalpostMetadata("Bekreftelse/svar på anmodning om personlig identifikasjonsnummer", UTLAND),
        SedType.H065 to JournalpostMetadata("Overføring av krav/dokument/informasjon", UTLAND),
        SedType.H066 to JournalpostMetadata("Svar på overføring av krav/dokument/informasjon", UTLAND),
        SedType.H070 to JournalpostMetadata("Melding om dødsfall", UTLAND),
        SedType.H120 to JournalpostMetadata("Anmodning om medisinsk informasjon", UTLAND),
        SedType.H121 to JournalpostMetadata("Svar på anmodning om medisinsk informasjon", UTLAND),
        SedType.H130 to JournalpostMetadata("Anmodning om kostnadsestimat/anmodning om medisinsk kontroll", UTLAND),
        SedType.S040 to JournalpostMetadata("Forespørsel om perioder - trygdeytelse: sykdom, svangerskap og fødsel", UTLAND),
        SedType.S041 to JournalpostMetadata("Svar på forespørsel om perioder - trygdeytelse: sykdom, svangerskap og fødsel", UTLAND)
    )

    fun hentJournalpostMetadata(sedType: String): JournalpostMetadata =
        metadataMap[SedType.valueOf(sedType)] ?: throw IllegalArgumentException("Unknown SED type: $sedType")

    companion object {
        private const val UTLAND = "ab0313"
        private const val MEDLEMSKAP = "ab0269"
    }
}
