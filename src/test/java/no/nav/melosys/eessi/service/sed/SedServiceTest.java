package no.nav.melosys.eessi.service.sed;

import no.nav.melosys.eessi.controller.dto.CreateSedDto;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SedServiceTest {

    @Mock
    private EuxService euxService;

    @InjectMocks
    private SedService sendSedService;

    private final String RINA_ID = "aabbcc";

    @Before
    public void setup() throws Exception {
        when(euxService.opprettOgSendBucOgSed(anyLong(), anyString(), anyString(), any()))
                .thenReturn(RINA_ID);

        when(euxService.opprettBucOgSed(anyLong(), anyString(), anyString(), any()))
                .thenReturn(RINA_ID);

        when(euxService.hentRinaUrl(anyString())).thenReturn("URL");
    }

    @Test
    public void createAndSend_expectRinacaseId() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        String rinaId = sendSedService.createAndSend(sedData);
        assertThat(rinaId).isEqualTo(RINA_ID);
    }

    @Test(expected = MappingException.class)
    public void createAndSend_withNoGsakSaksnummer_expectMappingException() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        sedData.setGsakSaksnummer(null);
        sendSedService.createAndSend(sedData);
    }

    @Test
    public void createSed_withNoExistingBuc_expectCreateNewBucAndSed() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        CreateSedDto response = sendSedService.createSed(sedData, BucType.LA_BUC_03, SedType.A008);

        verify(euxService).opprettBucOgSed(anyLong(), anyString(), anyString(), any());
        verify(euxService).hentRinaUrl(eq(RINA_ID));
        assertThat(response.getBucId()).isEqualTo(RINA_ID);
        assertThat(response.getRinaUrl()).isEqualTo("URL");
    }
}
