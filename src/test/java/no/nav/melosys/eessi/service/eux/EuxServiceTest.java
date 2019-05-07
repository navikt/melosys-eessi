package no.nav.melosys.eessi.service.eux;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.dto.Institusjon;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.caserelation.CaseRelationService;
import no.nav.melosys.eessi.service.joark.ParticipantInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

        Institusjon institusjon = new Institusjon();
        institusjon.setId("NO:321");
        when(euxConsumer.hentInstitusjoner(anyString(), anyString()))
                .thenReturn(Collections.singletonList(institusjon));
    }

    @Test
    public void hentMottaker_expectParticipantInfo() throws Exception {
        ParticipantInfo receiver = euxService.hentMottaker("123123123");

        assertThat(receiver, not(nullValue()));
        assertThat(receiver.getId(), is("NO:NAVT003"));
        assertThat(receiver.getName(), is("NAVT003"));
    }

    @Test
    public void hentUtsender_expectParticipantInfo() throws Exception {
        ParticipantInfo sender = euxService.hentUtsender("123123123");

        assertThat(sender, not(nullValue()));
        assertThat(sender.getId(), is("NO:NAVT002"));
        assertThat(sender.getName(), is("NAVT002"));
    }

    @Test
    public void opprettOgSendBucOgSed_expectRinaCaseId() throws Exception {
        Long gsakSaksnummer = 12345L;
        String bucType = BucType.LA_BUC_01.name();
        String mottakerLand = "SE";
        SED sed = new SED();

        String rinaCaseId = euxService.opprettOgSendBucOgSed(gsakSaksnummer, bucType, mottakerLand, sed);

        assertThat(rinaCaseId, is("1122334455"));

        verify(euxConsumer).opprettBucOgSed(anyString(), anyString(), any());
        verify(euxConsumer).sendSed(anyString(), anyString(), anyString());
        verify(euxConsumer).hentInstitusjoner(eq(bucType), eq(mottakerLand));
        verify(caseRelationService).save(anyLong(), anyString());
    }

    @Test
    public void opprettOgSendBucOgSed_expectExceptionDeleteCaseAndBuc() throws Exception {
        Long gsakSaksnummer = 12345L;
        String bucType = BucType.LA_BUC_01.name();
        String mottakerLand = "SE";
        SED sed = new SED();

        doThrow(IntegrationException.class).when(euxConsumer).sendSed(anyString(), anyString(), anyString());

        expectedException.expect(IntegrationException.class);
        euxService.opprettOgSendBucOgSed(gsakSaksnummer, bucType, mottakerLand, sed);

        verify(euxConsumer).slettBuC(anyString());
        verify(caseRelationService).deleteByRinaId(anyString());
    }
}