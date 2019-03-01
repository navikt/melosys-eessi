package no.nav.melosys.eessi.service.joark;

import java.io.InputStream;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;

@Slf4j
public class SedDocumentStub {

    public static byte[] getPdfStub() {
        try {
            InputStream is = (Objects.requireNonNull(SedDocumentStub.class.getClassLoader().getResource("sedDokument.pdf"))).openStream();
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            log.info("Kunne ikke hente mock-pdf");
            return new byte[0];
        }
    }

}
