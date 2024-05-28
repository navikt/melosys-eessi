package no.nav.melosys.eessi.service.journalfoering;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.SedType;
import org.springframework.stereotype.Component;

@Component
public class JournalpostMetadataService {
    private static final String UTLAND = "ab0313";
    private static final String MEDLEMSKAP = "ab0269";

    public JournalpostMetadata hentJournalpostMetadata(String sedType) {
        return switch (SedType.valueOf(sedType)) {
            case X001 -> new JournalpostMetadata("Anmodning om avslutning", UTLAND);
            case X002 -> new JournalpostMetadata("Anmodning om gjenåpning av avsluttet sak", UTLAND);
            case X003 -> new JournalpostMetadata("Svar på anmodning om gjenåpning av avsluttet sak", UTLAND);
            case X004 -> new JournalpostMetadata("Gjenåpne saken", UTLAND);
            case X005 -> new JournalpostMetadata("Legg til ny institusjon", UTLAND);
            case X006 -> new JournalpostMetadata("Fjern institusjon", UTLAND);
            case X007 -> new JournalpostMetadata("Videresende sak", UTLAND);
            case X008 -> new JournalpostMetadata("Ugyldiggjøre SED", UTLAND);
            case X009 -> new JournalpostMetadata("Påminnelse", UTLAND);
            case X010 -> new JournalpostMetadata("Svar på påminnelse", UTLAND);
            case X011 -> new JournalpostMetadata("Avvis SED", UTLAND);
            case X012 -> new JournalpostMetadata("Klargjør innhold", UTLAND);
            case X013 -> new JournalpostMetadata("Svar på anmodning om klargjøring", UTLAND);
            case X050 -> new JournalpostMetadata("Unntaksfeil", UTLAND);
            case X100 -> new JournalpostMetadata("Endring av institusjon", UTLAND);


            case A001 -> new JournalpostMetadata("Søknad om unntak", MEDLEMSKAP);
            case A002 -> new JournalpostMetadata("Delvis eller fullt avslag på søknad om unntak", MEDLEMSKAP);
            case A003 -> new JournalpostMetadata("Beslutning om lovvalg", MEDLEMSKAP);
            case A004 -> new JournalpostMetadata("Uenighet om beslutning om lovvalg", MEDLEMSKAP);
            case A005 -> new JournalpostMetadata("Anmodning om mer informasjon", MEDLEMSKAP);
            case A006 -> new JournalpostMetadata("Svar på anmodning om mer informasjon", MEDLEMSKAP);
            case A007 -> new JournalpostMetadata("Midlertidig beslutning om lovvalg", MEDLEMSKAP);
            case A008 -> new JournalpostMetadata("Melding om relevant informasjon", MEDLEMSKAP);
            case A009 -> new JournalpostMetadata("Melding om utstasjonering", MEDLEMSKAP);
            case A010 -> new JournalpostMetadata("Melding om lovvalg", MEDLEMSKAP);
            case A011 -> new JournalpostMetadata("Innvilgelse av søknad om unntak", MEDLEMSKAP);
            case A012 -> new JournalpostMetadata("Godkjenning av lovvalgsbeslutning", MEDLEMSKAP);


            case H001 -> new JournalpostMetadata("Melding/anmodning om informasjon", UTLAND);
            case H002 -> new JournalpostMetadata("Svar på anmodning om informasjon", UTLAND);
            case H003 -> new JournalpostMetadata("Fremlegg/melding om bostedsland", UTLAND);
            case H004 ->
                new JournalpostMetadata("Svar på fremlegg om bostedsland/uenighet med vedtak om bostedsland", UTLAND);
            case H005 -> new JournalpostMetadata("Anmodning om informasjon om bosted", UTLAND);
            case H006 -> new JournalpostMetadata("Svar på anmodning om informasjon om bosted", UTLAND);
            case H010 -> new JournalpostMetadata("Melding om endring av lovvalg", MEDLEMSKAP);
            case H011 -> new JournalpostMetadata("Anmodning om dato for endring av lovvalg", MEDLEMSKAP);
            case H012 -> new JournalpostMetadata("Svar på anmodning for endring av lovvalg", MEDLEMSKAP);
            case H020 ->
                new JournalpostMetadata("Krav om refusjon - administrativ kontroll/medisinsk refusjon", UTLAND);
            case H021 -> new JournalpostMetadata("Svar på krav om refusjon – administrativ kontroll/medisinsk informasjon", UTLAND);
            case H061 -> new JournalpostMetadata("Melding/anmodning om personnummer", UTLAND);
            case H065 -> new JournalpostMetadata("Overføring av krav/dokument/informasjon", UTLAND);
            case H066 -> new JournalpostMetadata("Svar på overføring av krav/dokument/informasjon", UTLAND);
            case H070 -> new JournalpostMetadata("Melding om dødsfall", UTLAND);
            case H120 -> new JournalpostMetadata("Anmodning om medisinsk informasjon", UTLAND);
            case H121 -> new JournalpostMetadata("Svar på anmodning om medisinsk informasjon", UTLAND);
            case H130 ->
                new JournalpostMetadata("Anmodning om kostnadsestimat/anmodning om medisinsk kontroll", UTLAND);


            case S040 ->
                new JournalpostMetadata("Forespørsel om perioder - trygdeytelse: sykdom, svangerskap og fødsel", UTLAND);
            case S041 ->
                new JournalpostMetadata("Svar på forespørsel om perioder - trygdeytelse: sykdom, svangerskap og fødsel", UTLAND);
        };
    }
}
