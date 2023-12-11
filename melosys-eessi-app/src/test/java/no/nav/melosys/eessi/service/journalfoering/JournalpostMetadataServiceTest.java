package no.nav.melosys.eessi.service.journalfoering;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class JournalpostMetadataServiceTest {
    private final JournalpostMetadataService journalpostMetadataService = new JournalpostMetadataService();

    @Test
    void hentJournalpostMetadata_ugyldigSedType_kasterFeil() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> journalpostMetadataService.hentJournalpostMetadata("ugyldig SedType"))
            .withMessageContaining("No enum constant");
    }
}
