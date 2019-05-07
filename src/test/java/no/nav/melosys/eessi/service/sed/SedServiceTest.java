package no.nav.melosys.eessi.service.sed;

import java.util.Map;
import java.util.Optional;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.service.caserelation.CaseRelationService;
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

    @Mock
    private CaseRelationService caseRelationService;

    @InjectMocks
    private SedService sendSedService;

    private final String RINA_ID = "aabbcc";
    private final String SED_ID = "12345";

    @Before
    public void setup() throws Exception {
        when(euxService.opprettOgSendBucOgSed(anyLong(), anyString(), anyString(), any()))
                .thenReturn(RINA_ID);

        when(euxService.opprettBucOgSed(anyLong(), anyString(), anyString(), any()))
                .thenReturn(RINA_ID);

        when(euxService.opprettSed(any(), anyString())).thenReturn(SED_ID);

        when(euxService.hentRinaUrl(anyString(), any())).thenReturn("URL");
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
        String rinaId = sendSedService.createAndSend(sedData);
    }

    @Test
    public void createA008_withRinaId_expectRinaIdAndUrl() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        Map<String, String> map = sendSedService.createA008(sedData, RINA_ID);

        verify(euxService).opprettSed(any(), eq(RINA_ID));
        verify(euxService).hentRinaUrl(eq(RINA_ID), eq(SED_ID));
        assertThat(map.get("rinaCaseId")).isEqualTo(RINA_ID);
        assertThat(map.get("rinaUrl")).isEqualTo("URL");
    }

    @Test
    public void createA008_withNoRinaId_expectRinaIdAndUrl() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        CaseRelation caseRelation = new CaseRelation();
        caseRelation.setGsakSaksnummer(123L);
        caseRelation.setRinaId(RINA_ID);
        when(caseRelationService.findByGsakSaksnummer(anyLong())).thenReturn(Optional.of(caseRelation));

        Map<String, String> map = sendSedService.createA008(sedData, null);

        verify(caseRelationService).findByGsakSaksnummer(eq(123L));
        verify(euxService).opprettSed(any(), eq(RINA_ID));
        verify(euxService).hentRinaUrl(eq(RINA_ID), eq(SED_ID));
        assertThat(map.get("rinaCaseId")).isEqualTo(RINA_ID);
        assertThat(map.get("rinaUrl")).isEqualTo("URL");
    }

    @Test
    public void createA008_withNoRinaId_expectCreateNewBucAndSed() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        when(caseRelationService.findByGsakSaksnummer(anyLong())).thenReturn(Optional.empty());

        Map<String, String> map = sendSedService.createA008(sedData, null);

        verify(caseRelationService).findByGsakSaksnummer(eq(123L));
        verify(euxService).opprettBucOgSed(eq(123L), eq(BucType.LA_BUC_03.name()), any(), any());
        verify(euxService).hentRinaUrl(eq(RINA_ID), eq(null));
        assertThat(map.get("rinaCaseId")).isEqualTo(RINA_ID);
        assertThat(map.get("rinaUrl")).isEqualTo("URL");
    }
}
