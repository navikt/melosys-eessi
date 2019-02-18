package no.nav.melosys.eessi.service.joark;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class SedDocumentStub {

    public static byte[] getPdfStub() {
        try {
            URI pdfUri = (Objects.requireNonNull(SedDocumentStub.class.getClassLoader().getResource("sedDokument.pdf"))).toURI();
            return Files.readAllBytes(Paths.get(pdfUri));
        } catch (Exception e) {
            return new byte[0];
        }
    }

}
