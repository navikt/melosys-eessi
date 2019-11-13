package no.nav.melosys.eessi.integration.journalpostapi;


import java.util.Optional;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class JournalpostFiltypeTest {

    @Test
    public void filnavn_pdfEndelse_forventPDF() {
        String filnavn = "titteipådeg.pdf";
        assertFiltype(filnavn, JournalpostFiltype.PDF);
    }

    @Test
    public void filnavn_jpsEndelse_forventJPEG() {
        String filnavn = "titteipådeg.jpg";
        assertFiltype(filnavn, JournalpostFiltype.JPEG);
    }

    @Test
    public void filnavn_filnavnErTomt_forventOptionalEmpty() {
        String filnavn = "";
        Optional<JournalpostFiltype> journalpostFiltype = JournalpostFiltype.filnavn(filnavn);
        assertThat(journalpostFiltype).isNotPresent();
    }

    private void assertFiltype(String filnavn, JournalpostFiltype forventetFiltype) {
        Optional<JournalpostFiltype> journalpostFiltype = JournalpostFiltype.filnavn(filnavn);
        assertThat(journalpostFiltype).isPresent();
        assertThat(journalpostFiltype.get()).isEqualTo(forventetFiltype);
    }
}