package no.nav.melosys.eessi.service.eux;

import java.io.IOException;
import java.net.URL;
import avro.shaded.com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.caserelation.CaseRelationService;
import no.nav.melosys.eessi.service.joark.ParticipantInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EuxServiceTest {

    @Mock
    private EuxConsumer euxConsumer;

    @Mock
    private CaseRelationService caseRelationService;

    @InjectMocks
    private EuxService euxService;

    @Before
    public void setup() throws IOException, IntegrationException {
        URL jsonUrl = getClass().getClassLoader().getResource("buc_participants.json");
        JsonNode participants = new ObjectMapper().readTree(jsonUrl);

        when(euxConsumer.hentDeltagere(anyString()))
                .thenReturn(participants);

        when(euxConsumer.opprettBucOgSed(anyString(), anyString(), any()))
                .thenReturn(ImmutableMap.of(
                        "caseId", "1122334455",
                        "documentId", "9988776655"
                ));
    }

    @Test
    public void hentMottaker_expectParticipantInfo() throws IntegrationException {
        ParticipantInfo receiver = euxService.hentMottaker("123123123");

        assertThat(receiver, not(nullValue()));
        assertThat(receiver.getId(), is("NO:NAVT003"));
        assertThat(receiver.getName(), is("NAVT003"));
    }

    @Test
    public void hentUtsender_expectParticipantInfo() throws IntegrationException {
        ParticipantInfo sender = euxService.hentUtsender("123123123");

        assertThat(sender, not(nullValue()));
        assertThat(sender.getId(), is("NO:NAVT002"));
        assertThat(sender.getName(), is("NAVT002"));
    }

    @Test
    public void opprettOgSendBucOgSed_expectRinaCaseId() throws IntegrationException {
        Long gsakSaksnummer = 12345L;
        String bucType = BucType.LA_BUC_01.name();
        String mottakerId = "NAVT003";
        SED sed = new SED();

        String rinaCaseId = euxService.opprettOgSendBucOgSed(gsakSaksnummer, bucType, mottakerId, sed);

        assertThat(rinaCaseId, is("1122334455"));

        verify(euxConsumer, times(1))
                .opprettBucOgSed(anyString(), anyString(), any());

        verify(euxConsumer, times(1))
                .sendSed(anyString(), anyString(), anyString());

        verify(caseRelationService, times(1))
                .save(anyLong(), anyString());
    }
}