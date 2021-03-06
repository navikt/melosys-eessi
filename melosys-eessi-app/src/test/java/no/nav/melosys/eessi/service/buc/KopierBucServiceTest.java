package no.nav.melosys.eessi.service.buc;

import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KopierBucServiceTest {

    @Mock
    private EuxService euxService;
    @Mock
    private SaksrelasjonService saksrelasjonService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private KopierBucService kopierBucService;

    private BUC buc;
    private SED sedA001;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        kopierBucService = new KopierBucService(euxService, saksrelasjonService);

        buc = lesObjekt(BUC.class, "mock/buc.json");
        sedA001 = lesObjekt(SED.class, "mock/sedA001.json");
    }

    @Test
    void kopierBUC_laBuc01A001Finnes_oppretterNyBucMedYtterligereInfoOgOppdatererSaksrelasjon() {
        final var rinaSaksnummer = "12423";
        final var forventetNyRinasaksnummer = "222223";

        buc.getDocuments()
                .stream()
                .filter(d -> SedType.A001.name().equals(d.getType()))
                .findFirst()
                .ifPresent(d -> d.setStatus("SENT"));

        when(euxService.hentBuc(rinaSaksnummer)).thenReturn(buc);
        when(euxService.hentSed(eq(rinaSaksnummer), anyString())).thenReturn(sedA001);
        when(euxService.opprettBucOgSed(eq(BucType.LA_BUC_01), anyCollection(), eq(sedA001), anyCollection()))
                .thenReturn(new OpprettBucOgSedResponse(forventetNyRinasaksnummer, ""));

        assertThat(kopierBucService.kopierBUC(rinaSaksnummer)).isEqualTo(forventetNyRinasaksnummer);
        assertThat(sedA001.getNav().getYtterligereinformasjon()).isNotEmpty()
                .contains(SedType.A001.name(), buc.getInternationalId())
                .hasSizeLessThan(500); //Maks-lengde i RINA
    }

    @Test
    void kopierBUC_laBuc01A001FinnesIkke_kasterFeil() {
        final var rinaSaksnummer = "12423";
        when(euxService.hentBuc(rinaSaksnummer)).thenReturn(buc);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> kopierBucService.kopierBUC(rinaSaksnummer))
                .withMessageContaining("Finner ikke første");
    }

    @SneakyThrows
    private <T> T lesObjekt(Class<T> clazz, String fil) {
        return objectMapper.readValue(getClass().getClassLoader().getResource(fil), clazz);
    }
}
