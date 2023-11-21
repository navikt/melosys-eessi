package no.nav.melosys.eessi.service.journalfoering;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getunleash.FakeUnleash;
import no.nav.melosys.eessi.models.SedType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class JournalpostMetadataServiceTest {

    private final FakeUnleash unleash = new FakeUnleash();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JournalpostMetadataService journalpostMetadataService = new JournalpostMetadataService(unleash);

    @Test
    void hentJournalpostMetadata_ugyldigSedType_kasterFeilNårToggleEnabled() {
        unleash.enable("melosys.eessi.erstatte_dokkat");
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> journalpostMetadataService.hentJournalpostMetadata("ugyldig SedType"))
            .withMessageContaining("No enum constant");
    }

    @Deprecated(since = "Denne kan slettes når toggle melosys.eessi.erstatte_dokkat fjernes")
    @Test
    void hentJournalpostMetadata_ugyldigSedType_kasterIkkeFeilNårToggleDisabled() {
        unleash.disable("melosys.eessi.erstatte_dokkat");
        assertThat(journalpostMetadataService.hentJournalpostMetadata("ugyldig SedType"))
            .isNotNull()
            .extracting(JournalpostMetadata::dokumentTittel, JournalpostMetadata::behandlingstema)
            .containsExactly("", "");
    }

    @Deprecated(since = "Denne og filene innenfor mock/dokkat kan slettes når toggle melosys.eessi.erstatte_dokkat fjernes")
    @Test
    void journalpostMetadataErLikDenFraDokkat() throws IOException {
        var alleDokkatFiler = new File(getClass().getClassLoader().getResource("mock/dokkat").getFile()).listFiles();
        var informasjonFraDokkat = new HashMap<String, JournalpostMetadata>();

        for (File file : alleDokkatFiler) {
            JsonNode json = objectMapper.readTree(file);
            informasjonFraDokkat.put(
                json.path("dokumentMottakInfo").path("eksternDokumentTyper").get(0).path("eksternDokumentTypeId").asText(),
                new JournalpostMetadata(
                    json.path("dokumentTittel").asText(),
                    json.path("behandlingstema").asText()));
        }

        for (SedType sedType : SedType.values()) {
            assertThat(journalpostMetadataService.hentJournalpostMetadata(sedType.name()))
                .isEqualTo(informasjonFraDokkat.get(sedType.name()));
        }
    }
}
