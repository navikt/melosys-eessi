package no.nav.melosys.eessi.service.eux;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableMap;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.dto.Institusjon;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Conversation;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.joark.ParticipantInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EuxServiceTest {

    private final String RINA_MOCK_URL = "https://rina-host-url.local";

    @Mock
    private EuxConsumer euxConsumer;

    @InjectMocks
    private EuxService euxService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
            .collectionSizeRange(3, 10).build();

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
        institusjon.setLandkode("SE");
        when(euxConsumer.hentInstitusjoner(anyString(), any()))
                .thenReturn(Collections.singletonList(institusjon));

        when(euxConsumer.opprettSed(anyString(), any(), any())).thenReturn("12345");

        ReflectionTestUtils.setField(euxService, "rinaHostUrl", RINA_MOCK_URL);
    }

    @Test
    public void hentMottaker_expectParticipantInfo() throws Exception {
        ParticipantInfo receiver = euxService.hentMottaker("123123123");

        assertThat(receiver).isNotNull();
        assertThat(receiver.getId()).isEqualTo("NO:NAVT003");
        assertThat(receiver.getName()).isEqualTo("NAVT003");
    }

    @Test
    public void hentUtsender_expectParticipantInfo() throws Exception {
        ParticipantInfo sender = euxService.hentUtsender("123123123");

        assertThat(sender).isNotNull();
        assertThat(sender.getId()).isEqualTo("NO:NAVT002");
        assertThat(sender.getName()).isEqualTo("NAVT002");
    }

    @Test
    public void hentUtsender_expectNull() throws Exception {
        when(euxConsumer.hentDeltagere(anyString())).thenReturn(JsonNodeFactory.instance.arrayNode());
        ParticipantInfo sender = euxService.hentUtsender("123123123");

        assertThat(sender).isNull();
    }

    @Test
    public void hentSed_expectConsumerCalls() throws IntegrationException {
        SED sed = euxService.hentSed("123123123", "12345");

        verify(euxConsumer).hentSed(eq("123123123"), eq("12345"));
    }

    @Test
    public void hentBucer_expectConsumerCalls() throws IntegrationException {
        BucSearch bucSearch = BucSearch.builder()
                .bucType(BucType.LA_BUC_01.name())
                .fnr("12345678910")
                .build();

        euxService.hentBucer(bucSearch);

        verify(euxConsumer).finnRinaSaker(
                eq("12345678910"),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(BucType.LA_BUC_01.name()),
                eq(null)
        );
    }

    @Test
    public void hentBuc_expectConsumerCalls() throws IntegrationException {
        euxService.hentBuc("123123123");

        verify(euxConsumer).hentBuC(eq("123123123"));
    }

    @Test
    public void hentSedPdf_expectConsumerCalls() throws IntegrationException {
        euxService.hentSedPdf("123123123", "12345");

        verify(euxConsumer).hentSedPdf(eq("123123123"), eq("12345"));
    }

    @Test
    public void opprettBucOgSed_expectRinaCaseId() throws NotFoundException, IntegrationException {
        String bucType = BucType.LA_BUC_01.name();
        String mottakerLand = "SE";
        String mottakerId = null;
        SED sed = new SED();

        OpprettBucOgSedResponse opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType, mottakerLand, mottakerId, sed);

        assertThat(opprettBucOgSedResponse.getRinaSaksnummer()).isEqualTo("1122334455");

        verify(euxConsumer).opprettBucOgSed(anyString(), anyString(), any());
        verify(euxConsumer).hentInstitusjoner(eq(bucType), eq(null));
    }

    @Test
    public void opprettBucOgSed_expectException() throws Exception {
        String bucType = BucType.LA_BUC_01.name();
        String mottakerLand = "SE";
        String mottakerId = null;
        SED sed = new SED();

        doThrow(IntegrationException.class).when(euxConsumer).opprettBucOgSed(anyString(), anyString(), any());

        expectedException.expect(IntegrationException.class);
        euxService.opprettBucOgSed(bucType, mottakerLand, mottakerId, sed);

        verify(euxConsumer).opprettBucOgSed(anyString(), any(), any());
    }

    @Test
    public void opprettOgSendSed_withRinaId_expectConsumerCalls() throws IntegrationException {
        SED sed = new SED();

        euxService.opprettOgSendSed(sed, "123123123");

        verify(euxConsumer).opprettSed(eq("123123123"), eq(null), eq(sed));
        verify(euxConsumer).sendSed(eq("123123123"), eq(null), eq("12345"));
    }

    @Test
    public void opprettSed_withRinaId_expectSedId() throws IntegrationException {
        SED sed = new SED();

        String sedId = euxService.opprettSed(sed, "123123123");

        verify(euxConsumer).opprettSed(eq("123123123"), eq(null), eq(sed));
        assertThat(sedId).isEqualTo("12345");
    }

    @Test
    public void hentRinaUrl_withRinaCaseId_expectUrl() {
        String expectedUrl = RINA_MOCK_URL + "/portal/#/caseManagement/12345";
        String resultUrl = euxService.hentRinaUrl("12345");

        assertThat(resultUrl).isEqualTo(expectedUrl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hentRinaUrl_withNoRinaCaseId_expectException() {
        euxService.hentRinaUrl(null);
    }

    @Test
    public void sedKanOpprettesPaaBuc_withRinaCaseId_expectTrue() throws IntegrationException {
        when(euxConsumer.hentTilgjengeligeSedTyper(anyString())).thenReturn(Arrays.asList("X001", "H001", "A008"));
        boolean result = euxService.sedKanOpprettesPaaBuc("123123123", SedType.A008);

        verify(euxConsumer).hentTilgjengeligeSedTyper(anyString());
        assertThat(result).isTrue();
    }

    @Test
    public void sedKanOpprettesPaaBuc_withRinaCaseId_expectFalse() throws IntegrationException {
        when(euxConsumer.hentTilgjengeligeSedTyper(anyString())).thenReturn(Arrays.asList("X001", "H001", "A001"));
        boolean result = euxService.sedKanOpprettesPaaBuc("123123123", SedType.A008);

        verify(euxConsumer).hentTilgjengeligeSedTyper(anyString());
        assertThat(result).isFalse();
    }

    @Test
    public void sendSed_verifiserConsumerKall() throws IntegrationException {
        euxService.sendSed("123", "123");
        verify(euxConsumer).sendSed(anyString(), any(), anyString());
    }

    @Test
    public void hentMottakerinstitusjoner_verifiserConsumerKall() throws IntegrationException {
        euxService.hentMottakerinstitusjoner(BucType.LA_BUC_01.name(), null);
        verify(euxConsumer).hentInstitusjoner(eq(BucType.LA_BUC_01.name()), eq(null));
    }

    @Test
    public void sedErEndring_medFlereConversations_forventTrue() throws IntegrationException, NotFoundException {
        when(euxConsumer.hentBuC(anyString())).thenReturn(lagBuc());

        boolean erEndring = euxService.sedErEndring("1", "123");

        verify(euxConsumer).hentBuC(eq("123"));
        assertThat(erEndring).isTrue();
    }

    @Test
    public void sedErEndring_utenNoenConversations_forventFalse() throws IntegrationException, NotFoundException {
        BUC buc = lagBuc();
        buc.getDocuments().get(0).setConversations(Collections.singletonList(new Conversation()));
        when(euxConsumer.hentBuC(anyString())).thenReturn(buc);

        boolean erEndring = euxService.sedErEndring("1", "123");

        verify(euxConsumer).hentBuC(eq("123"));
        assertThat(erEndring).isFalse();
    }

    @Test(expected = NotFoundException.class)
    public void sedErEndring_utenSederForBuc_forventException() throws IntegrationException, NotFoundException {
        when(euxConsumer.hentBuC(anyString())).thenReturn(enhancedRandom.nextObject(BUC.class));

        boolean erEndring = euxService.sedErEndring("1", "123");

        verify(euxConsumer).hentBuC(eq("123"));
        assertThat(erEndring).isTrue();
    }

    private BUC lagBuc() {
        BUC buc = enhancedRandom.nextObject(BUC.class);

        buc.getDocuments().get(0).setId("1");
        buc.setId("123");

        return buc;
    }
}
