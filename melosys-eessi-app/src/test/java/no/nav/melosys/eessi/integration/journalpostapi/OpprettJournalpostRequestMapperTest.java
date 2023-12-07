package no.nav.melosys.eessi.integration.journalpostapi;

import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpprettJournalpostRequestMapperTest {

    private final String ident = "123123123123";

    @Test
    void opprettInngaaendeJournalpost_medPdfVedlegg_validerFelterSatt() {
        final var vedlegg = new SedMedVedlegg.BinaerFil("vedlegg123.pdf", null, new byte[0]);
        final var sedHendelse = sedHendelse();
        final var dokkatSedInfo = new DokkatSedInfo();
        dokkatSedInfo.setDokumentTittel("titteipådeg");

        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse,
            sedMedVedlegg(List.of(vedlegg)),
            null,
            dokkatSedInfo.getDokumentTittel(),
            dokkatSedInfo.getBehandlingstema(),
            ident,
            false
        );

        assertThat(request.getDokumenter()).hasSize(2)
            .flatExtracting(OpprettJournalpostRequest.Dokument::getTittel)
            .containsExactly(dokkatSedInfo.getDokumentTittel(), vedlegg.getFilnavn());

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

    @Test
    void opprettInngaaendeJournalpost_medDOCXVedlegg_validerFelterSatt() throws IOException {
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.createStyles();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("Hello, this is a simple DOCX file.");
        document.write(out);
        byte[] bytes = out.toByteArray();
        out.close();

        final var vedlegg = new SedMedVedlegg.BinaerFil("vedlegg123.docx", "DOCX", bytes);
        final var sedHendelse = sedHendelse();
        final var dokkatSedInfo = new DokkatSedInfo();
        dokkatSedInfo.setDokumentTittel("titteipådeg");

        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettInngaaendeJournalpost(
            sedHendelse,
            sedMedVedlegg(List.of(vedlegg)),
            null,
            dokkatSedInfo.getDokumentTittel(),
            dokkatSedInfo.getBehandlingstema(),
            ident,
            true
        );

        assertThat(request.getDokumenter()).hasSize(2)
            .flatExtracting(OpprettJournalpostRequest.Dokument::getTittel)
            .containsExactly(dokkatSedInfo.getDokumentTittel(), vedlegg.getFilnavn());

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


    @Test
    @Disabled
    void opprettUtgaaendeJournalpost_medJPGVedlegg_vedleggSettesIkke() {
        final var vedlegg = new SedMedVedlegg.BinaerFil("vedlegg123.jpeg", null, new byte[0]);
        final var sedHendelse = sedHendelse();
        final var dokkatSedInfo = new DokkatSedInfo();
        dokkatSedInfo.setDokumentTittel("titteipådeg");

        OpprettJournalpostRequest request = OpprettJournalpostRequestMapper.opprettUtgaaendeJournalpost(
            sedHendelse,
            sedMedVedlegg(List.of(vedlegg)),
            null,
            dokkatSedInfo.getDokumentTittel(),
            dokkatSedInfo.getBehandlingstema(),
            ident,
            false
        );

        assertThat(request.getDokumenter()).hasSize(1)
            .flatExtracting(OpprettJournalpostRequest.Dokument::getTittel)
            .containsExactly(dokkatSedInfo.getDokumentTittel());

        assertThat(request).extracting(
            OpprettJournalpostRequest::getKanal,
            OpprettJournalpostRequest::getJournalpostType,
            OpprettJournalpostRequest::getTema,
            OpprettJournalpostRequest::getEksternReferanseId
        ).containsExactly(
            "EESSI",
            OpprettJournalpostRequest.JournalpostType.UTGAAENDE,
            "MED",
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
