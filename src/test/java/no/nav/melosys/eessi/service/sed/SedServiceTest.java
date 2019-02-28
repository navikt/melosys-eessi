package no.nav.melosys.eessi.service.sed;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SedServiceTest {

    @Mock
    private EuxService euxService;

    @InjectMocks
    private SedService sedService;

    private final String RINAID = "aabbcc";

    @Before
    public void setup() throws Exception {
        when(euxService.opprettOgSendBucOgSed(anyLong(), anyString(), anyString(),any()))
                .thenReturn(RINAID);
    }

    @Test
    public void createAndSend_expectRinacaseId() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        String rinaId = sedService.createAndSend(sedData);
        assertThat(rinaId, is(RINAID));
    }

    @Test(expected = MappingException.class)
    public void createAndSend_withNoGsakSaksnummer_expectMappingException() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        sedData.setGsakSaksnummer(null);
        String rinaId = sedService.createAndSend(sedData);
    }
}