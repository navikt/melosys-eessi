package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import org.springframework.util.StringUtils;
import static no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest.*;

public class OpprettJournalpostRequestMapper {

    private OpprettJournalpostRequestMapper() {
    }

    public static OpprettJournalpostRequest opprettInngaaendeJournalpost(final SedHendelse sedHendelse,
            final byte[] sedPdf,
            final Sak sak,
            final DokkatSedInfo dokkatSedInfo) {
        return opprettJournalpostRequest(JournalpostType.INNGAAENDE, sedHendelse, sedPdf, sak, dokkatSedInfo);
    }

    public static OpprettJournalpostRequest opprettUtgaaendeJournalpost(final SedHendelse sedHendelse,
            final byte[] sedPdf,
            final Sak sak,
            final DokkatSedInfo dokkatSedInfo) {
        return opprettJournalpostRequest(JournalpostType.UTGAAENDE, sedHendelse, sedPdf, sak, dokkatSedInfo);
    }


    private static OpprettJournalpostRequest opprettJournalpostRequest(final JournalpostType journalpostType,
            final SedHendelse sedHendelse,
            final byte[] sedPdf,
            final Sak sak,
            final DokkatSedInfo dokkatSedInfo) {

        return OpprettJournalpostRequest.builder()
                .avsenderMottaker(getAvsenderMottaker(journalpostType, sedHendelse))
                .behandlingstema(dokkatSedInfo.getBehandlingstema())
                .bruker(!StringUtils.isEmpty(sedHendelse.getNavBruker()) ? lagBruker(sedHendelse.getNavBruker()) : null)
                .dokumenter(dokumenter(sedHendelse.getSedType(), sedPdf, dokkatSedInfo))
                .eksternReferanseId(sedHendelse.getSedId())
                .journalfoerendeEnhet("4530")
                .journalpostType(journalpostType)
                .kanal("EESSI")
                .sak(sak != null ? OpprettJournalpostRequest.Sak.builder().arkivsaksnummer(sak.getId()).build() : null)
                .tema(sak != null ? sak.getTema() : "MED") //fixme: MED, null eller UFM?
                .tittel(dokkatSedInfo.getDokumentTittel())
                .tilleggsopplysninger(Collections.singletonList(Tilleggsopplysning.builder()
                        .nokkel("rinaSakId")
                        .verdi(sedHendelse.getRinaSakId())
                        .build()))
                .build();
    }

    private static Bruker lagBruker(final String fnr) {
        return Bruker.builder()
                .id(fnr)
                .idType(BrukerIdType.FNR)
                .build();
    }

    private static AvsenderMottaker getAvsenderMottaker(final JournalpostType type,
            final SedHendelse sedHendelse) {

        return AvsenderMottaker.builder()
                .id(type == JournalpostType.UTGAAENDE ? sedHendelse.getMottakerId() : sedHendelse.getAvsenderId())
                .navn(type == JournalpostType.UTGAAENDE ? sedHendelse.getMottakerNavn() : sedHendelse.getAvsenderNavn())
                .idType(AvsenderMottaker.IdType.UTL_ORG)
                .build();
    }

    private static List<Dokument> dokumenter(final String sedType, final byte[] sedPdf,
            final DokkatSedInfo dokkatSedInfo) {
        final List<Dokument> dokumenter = new ArrayList<>();
        dokumenter.add(
                Dokument.builder()
                        .dokumentvarianter(Collections.singletonList(DokumentVariant.builder()
                                .filtype(JournalpostFiltype.PDFA)
                                .fysiskDokument(sedPdf)
                                .variantformat("ARKIV")
                                .build()))
                        .sedType(sedType)
                        .tittel(dokkatSedInfo.getDokumentTittel())
                        .build());
        return dokumenter;
    }
}
