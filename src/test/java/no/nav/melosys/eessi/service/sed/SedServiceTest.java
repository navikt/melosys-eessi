package no.nav.melosys.eessi.service.sed;

import java.util.Map;
import com.google.common.collect.Maps;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.models.sed.SED;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SedServiceTest {

    @Mock
    private EuxConsumer euxConsumer;

    @InjectMocks
    private SedService sedService;

    private final String RINAID = "aabbcc";

    @Before
    public void setup() throws Exception {

        Map<String, String> bucAndSedId = Maps.newHashMap();
        bucAndSedId.put("caseId", RINAID);
        bucAndSedId.put("documentId", "123123123");
        when(euxConsumer.opprettBucOgSed(anyString(), anyString(), any(SED.class)))
                .thenReturn(bucAndSedId);
    }

    @Test
    public void createAndSend_expectRinacaseId() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        String rinaId = sedService.createAndSend(sedData);
        assertThat(rinaId, is(RINAID));
    }
}