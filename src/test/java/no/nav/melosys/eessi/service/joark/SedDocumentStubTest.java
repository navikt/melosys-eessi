package no.nav.melosys.eessi.service.joark;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SedDocumentStubTest {

    @Test
    public void getPdfStub() {
        byte[] pdf = SedDocumentStub.getPdfStub();
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }
}