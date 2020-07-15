package no.nav.melosys.eessi.integration.journalpostapi;


import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class JournalpostFiltypeTest {

    @Test
    public void filnavn_pdfEndelse_forventPDF() {
        String filnavn = "titteipådeg.pdf";
        String mimeType = "123IkkeGyldigMimeType";
        assertFiltype(filnavn, mimeType, JournalpostFiltype.PDF);
    }

    @Test
    public void filnavn_jpsEndelse_forventJPEG() {
        String filnavn = "titteipådeg.jpg";
        String mimeType = "123IkkeGyldigMimeType";
        assertFiltype(filnavn, mimeType, JournalpostFiltype.JPEG);
    }

    @Test
    public void filnavn_filnavnErTomt_forventOptionalEmpty() {
        String filnavn = "abc_234gf_T43T_re4";
        String mimeType = null;
        Optional<JournalpostFiltype> journalpostFiltype = JournalpostFiltype.fraMimeOgFilnavn(mimeType, filnavn);
        assertThat(journalpostFiltype).isNotPresent();
    }

    @Test
    public void filnavn_filnavnErNullMimetypePdf_forventPDFA() {
        String filnavn = "abc_234gf_T43T_re4";
        String mimeType = "application/pdf";
        Optional<JournalpostFiltype> journalpostFiltype = JournalpostFiltype.fraMimeOgFilnavn(mimeType, filnavn);
        assertThat(journalpostFiltype).isPresent();
        assertThat(journalpostFiltype.get()).isEqualTo(JournalpostFiltype.PDF);
    }

    private void assertFiltype(String filnavn, String mimeType, JournalpostFiltype forventetFiltype) {
        Optional<JournalpostFiltype> journalpostFiltype = JournalpostFiltype.fraMimeOgFilnavn(mimeType, filnavn);
        assertThat(journalpostFiltype).isPresent();
        assertThat(journalpostFiltype.get()).isEqualTo(forventetFiltype);
    }
}
