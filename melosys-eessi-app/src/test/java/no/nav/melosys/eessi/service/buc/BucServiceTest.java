package no.nav.melosys.eessi.service.buc;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.melosys.eessi.controller.dto.BucinfoDto;
import no.nav.melosys.eessi.controller.dto.SedinfoDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BucServiceTest {
    private static Long EXPECTED_TIMESTAMP = 1567082435017L;

    @Mock
    private EuxService euxService;

    @Mock
    private SaksrelasjonService saksrelasjonService;

    @InjectMocks
    private BucService bucService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void hentSed_forventSedMedTypeOgId() throws IntegrationException, IOException {
        when(saksrelasjonService.finnVedGsakSaksnummer(1L)).thenReturn(lagFagsakRinasakKobling());
        when(euxService.hentBuc(anyString())).thenReturn(lagBuc());

        List<BucinfoDto> bucinfoDtoList = bucService.hentBucer(1L, "");

        verify(saksrelasjonService).finnVedGsakSaksnummer(anyLong());
        verify(euxService).hentBuc(anyString());

        assertThat(bucinfoDtoList)
                .extracting(BucinfoDto::getId, BucinfoDto::getBucType, BucinfoDto::getOpprettetDato)
                .contains(tuple("100485", "LA_BUC_01", EXPECTED_TIMESTAMP));

       assertThat(bucinfoDtoList.get(0).getSeder())
                .extracting(SedinfoDto::getSedType, SedinfoDto::getSedId, SedinfoDto::getBucId)
                .contains(tuple("A001", "93f022ea50e54c08bbdb85290a5fb23d", "100485"));
    }

    @Test
    public void hentSed_medStatusTom_forventSed() throws IntegrationException, IOException {
        when(saksrelasjonService.finnVedGsakSaksnummer(1L)).thenReturn(lagFagsakRinasakKobling());
        when(euxService.hentBuc(anyString())).thenReturn(lagBuc());

        List<BucinfoDto> bucinfoDtoList = bucService.hentBucer(1L, "tom");

        verify(saksrelasjonService).finnVedGsakSaksnummer(anyLong());
        verify(euxService).hentBuc(anyString());

        assertThat(bucinfoDtoList)
                .extracting(BucinfoDto::getId, BucinfoDto::getBucType, BucinfoDto::getOpprettetDato)
                .contains(tuple("100485", "LA_BUC_01", EXPECTED_TIMESTAMP));

        assertThat(bucinfoDtoList.get(0).getSeder())
                .extracting(SedinfoDto::getSedType, SedinfoDto::getSedId, SedinfoDto::getBucId)
                .contains(tuple("A001", "93f022ea50e54c08bbdb85290a5fb23d", "100485"));
    }

    @Test
    public void hentSed_medIntegrationError_forventIngenSed() throws IntegrationException, IOException {
        when(saksrelasjonService.finnVedGsakSaksnummer(1L)).thenReturn(lagFagsakRinasakKobling());
        when(euxService.hentBuc(anyString())).thenAnswer(invocation -> {
            throw new IntegrationException("");
        });

        List<BucinfoDto> bucinfoDtoList = bucService.hentBucer(1L, "");

        verify(saksrelasjonService).finnVedGsakSaksnummer(anyLong());
        verify(euxService).hentBuc(anyString());

        assertThat(bucinfoDtoList).isEmpty();
    }

    private List<FagsakRinasakKobling> lagFagsakRinasakKobling() {
        FagsakRinasakKobling kobling = new FagsakRinasakKobling();
        kobling.setBucType(BucType.LA_BUC_03);
        kobling.setGsakSaksnummer(1L);
        kobling.setRinaSaksnummer("123");

        return Collections.singletonList(kobling);
    }

    private BUC lagBuc() throws IOException {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/buc.json");
        return new ObjectMapper().registerModule(new JavaTimeModule()).readValue(jsonUrl, BUC.class);
    }
}
