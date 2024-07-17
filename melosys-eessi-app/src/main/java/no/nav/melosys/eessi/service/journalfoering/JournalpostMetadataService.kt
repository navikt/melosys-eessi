package no.nav.melosys.eessi.service.journalfoering

import no.nav.melosys.eessi.models.SedType
import org.springframework.stereotype.Component

@Component
class JournalpostMetadataService {
    fun hentJournalpostMetadata(sedType: String?): JournalpostMetadata {
        return when (SedType.valueOf(sedType!!)) {
            SedType.X001 -> JournalpostMetadata("Anmodning om avslutning", UTLAND)
            SedType.X002 -> JournalpostMetadata("Anmodning om gjenåpning av avsluttet sak", UTLAND)
            SedType.X003 -> JournalpostMetadata("Svar på anmodning om gjenåpning av avsluttet sak", UTLAND)
            SedType.X004 -> JournalpostMetadata("Gjenåpne saken", UTLAND)
            SedType.X005 -> JournalpostMetadata("Legg til ny institusjon", UTLAND)
            SedType.X006 -> JournalpostMetadata("Fjern institusjon", UTLAND)
            SedType.X007 -> JournalpostMetadata("Videresende sak", UTLAND)
            SedType.X008 -> JournalpostMetadata("Ugyldiggjøre SED", UTLAND)
            SedType.X009 -> JournalpostMetadata("Påminnelse", UTLAND)
            SedType.X010 -> JournalpostMetadata("Svar på påminnelse", UTLAND)
            SedType.X011 -> JournalpostMetadata("Avvis SED", UTLAND)
            SedType.X012 -> JournalpostMetadata("Klargjør innhold", UTLAND)
            SedType.X013 -> JournalpostMetadata("Svar på anmodning om klargjøring", UTLAND)
            SedType.X050 -> JournalpostMetadata("Unntaksfeil", UTLAND)
            SedType.X100 -> JournalpostMetadata("Endring av institusjon", UTLAND)
            SedType.A001 -> JournalpostMetadata("Søknad om unntak", MEDLEMSKAP)
            SedType.A002 -> JournalpostMetadata("Delvis eller fullt avslag på søknad om unntak", MEDLEMSKAP)
            SedType.A003 -> JournalpostMetadata("Beslutning om lovvalg", MEDLEMSKAP)
            SedType.A004 -> JournalpostMetadata("Uenighet om beslutning om lovvalg", MEDLEMSKAP)
            SedType.A005 -> JournalpostMetadata("Anmodning om mer informasjon", MEDLEMSKAP)
            SedType.A006 -> JournalpostMetadata("Svar på anmodning om mer informasjon", MEDLEMSKAP)
            SedType.A007 -> JournalpostMetadata("Midlertidig beslutning om lovvalg", MEDLEMSKAP)
            SedType.A008 -> JournalpostMetadata("Melding om relevant informasjon", MEDLEMSKAP)
            SedType.A009 -> JournalpostMetadata("Melding om utstasjonering", MEDLEMSKAP)
            SedType.A010 -> JournalpostMetadata("Melding om lovvalg", MEDLEMSKAP)
            SedType.A011 -> JournalpostMetadata("Innvilgelse av søknad om unntak", MEDLEMSKAP)
            SedType.A012 -> JournalpostMetadata("Godkjenning av lovvalgsbeslutning", MEDLEMSKAP)
            SedType.H001 -> JournalpostMetadata("Melding/anmodning om informasjon", UTLAND)
            SedType.H002 -> JournalpostMetadata("Svar på anmodning om informasjon", UTLAND)
            SedType.H003 -> JournalpostMetadata("Fremlegg/melding om bostedsland", UTLAND)
            SedType.H004 -> JournalpostMetadata("Svar på fremlegg om bostedsland/uenighet med vedtak om bostedsland", UTLAND)
            SedType.H005 -> JournalpostMetadata("Anmodning om informasjon om bosted", UTLAND)
            SedType.H006 -> JournalpostMetadata("Svar på anmodning om informasjon om bosted", UTLAND)
            SedType.H010 -> JournalpostMetadata("Melding om endring av lovvalg", MEDLEMSKAP)
            SedType.H011 -> JournalpostMetadata("Anmodning om dato for endring av lovvalg", MEDLEMSKAP)
            SedType.H012 -> JournalpostMetadata("Svar på anmodning for endring av lovvalg", MEDLEMSKAP)
            SedType.H020 -> JournalpostMetadata("Krav om refusjon - administrativ kontroll/medisinsk refusjon", UTLAND)
            SedType.H021 -> JournalpostMetadata("Svar på krav om refusjon – administrativ kontroll/medisinsk informasjon", UTLAND)
            SedType.H061 -> JournalpostMetadata("Melding/anmodning om personnummer", UTLAND)
            SedType.H062 -> JournalpostMetadata("Bekreftelse/svar på anmodning om personlig identifikasjonsnummer", UTLAND)
            SedType.H065 -> JournalpostMetadata("Overføring av krav/dokument/informasjon", UTLAND)
            SedType.H066 -> JournalpostMetadata("Svar på overføring av krav/dokument/informasjon", UTLAND)
            SedType.H070 -> JournalpostMetadata("Melding om dødsfall", UTLAND)
            SedType.H120 -> JournalpostMetadata("Anmodning om medisinsk informasjon", UTLAND)
            SedType.H121 -> JournalpostMetadata("Svar på anmodning om medisinsk informasjon", UTLAND)
            SedType.H130 -> JournalpostMetadata("Anmodning om kostnadsestimat/anmodning om medisinsk kontroll", UTLAND)
            SedType.S040 -> JournalpostMetadata("Forespørsel om perioder - trygdeytelse: sykdom, svangerskap og fødsel", UTLAND)
            SedType.S041 -> JournalpostMetadata("Svar på forespørsel om perioder - trygdeytelse: sykdom, svangerskap og fødsel", UTLAND)
        }
    }

    companion object {
        private const val UTLAND = "ab0313"
        private const val MEDLEMSKAP = "ab0269"
    }
}
