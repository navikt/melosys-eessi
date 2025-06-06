package no.nav.melosys.eessi.integration.journalpostapi;

import java.util.List;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

class OpprettJournalpostRequestMapperTest {

    @Mock
    private SedMetrikker sedMetrikker;
    private final String ident = "123123123123";

    @Test
    void opprettInngaaendeJournalpost_medTomtVedlegg_vedleggIkkeJournalfoert() {
        final var tomtVedlegg = new SedMedVedlegg.BinaerFil("tomtVedlegg.pdf", "application/pdf", new byte[0]);
        final var sedHendelse = sedHendelse();


        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse,
            sedMedVedlegg(List.of(tomtVedlegg)),
            null,
            "dokumenttittel",
            "behandlingstema",
            ident,
            sedMetrikker
        );


        assertThat(request.getDokumenter()).hasSize(1)
            .flatExtracting(OpprettJournalpostRequest.Dokument::getTittel)
            .containsExactly("dokumenttittel");
    }

    @Test
    void opprettInngaaendeJournalpost_medPdfVedlegg_validerFelterSatt() {
        final var vedlegg = new SedMedVedlegg.BinaerFil("vedlegg123.pdf", null, new byte[1]);
        final var sedHendelse = sedHendelse();


        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse,
            sedMedVedlegg(List.of(vedlegg)),
            null,
            "dokumenttittel",
            "behandlingstema",
            ident,
            sedMetrikker
        );


        assertThat(request.getDokumenter()).hasSize(2)
            .flatExtracting(OpprettJournalpostRequest.Dokument::getTittel)
            .containsExactly("dokumenttittel", vedlegg.getFilnavn());

        assertThat(request).extracting(
            OpprettJournalpostRequest::getKanal,
            OpprettJournalpostRequest::getJournalpostType,
            OpprettJournalpostRequest::getTema,
            OpprettJournalpostRequest::getEksternReferanseId
        ).containsExactly(
            "EESSI",
            OpprettJournalpostRequest.JournalpostType.INNGAAENDE,
            "UFM",
            sedHendelse.getSedId()
        );
    }

    private SedMedVedlegg sedMedVedlegg(List<SedMedVedlegg.BinaerFil> vedlegg) {
        return new SedMedVedlegg(
            new SedMedVedlegg.BinaerFil("sed123", "application/pdf", new byte[0]),
            vedlegg
        );
    }

    private SedHendelse sedHendelse() {
        return new SedHendelse(
            123,
            "sedid11111",
            "LA",
            "LA_BUC_01",
            "2222",
            "DK:123",
            "avsendernavn",
            "NO:123",
            "NAV",
            "abc123",
            "1",
            "A001",
            ident
        );
    }
}
