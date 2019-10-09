package no.nav.melosys.eessi.service.eux;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Conversation;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EuxServiceTest {

    private final String RINA_MOCK_URL = "https://rina-host-url.local";

    @Mock
    private EuxConsumer euxConsumer;
    @Mock
    private BucMetrikker bucMetrikker;

    private EuxService euxService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() throws IOException, IntegrationException {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        euxService = new EuxService(euxConsumer, bucMetrikker, RINA_MOCK_URL, null);

        when(euxConsumer.opprettBucOgSed(anyString(), anyString(), any()))
                .thenReturn(ImmutableMap.of(
                        "caseId", "1122334455",
                        "documentId", "9988776655"
                ));

        URL institusjonerJsonUrl = getClass().getClassLoader().getResource("institusjoner.json");
        List<Institusjon> institusjoner = objectMapper.readValue(institusjonerJsonUrl, new TypeReference<List<Institusjon>>(){});
        when(euxConsumer.hentInstitusjoner(anyString(), any()))
                .thenReturn(institusjoner);

        when(euxConsumer.opprettSed(anyString(), any(), any())).thenReturn("12345");
    }

    @Test
    public void hentSed_forventKonsumentKall() throws IntegrationException {
        euxService.hentSed("123123123", "12345");
        verify(euxConsumer).hentSed(eq("123123123"), eq("12345"));
    }

    @Test
    public void hentBucer_forventKonsumentKall() throws IntegrationException {
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
    public void hentBuc_forventKonsumentKall() throws IntegrationException {
        euxService.hentBuc("123123123");

        verify(euxConsumer).hentBuC(eq("123123123"));
    }

    @Test
    public void hentSedPdf_forventKonsumentKall() throws IntegrationException {
        euxService.hentSedPdf("123123123", "12345");

        verify(euxConsumer).hentSedPdf(eq("123123123"), eq("12345"));
    }

    @Test
    public void genererPdfFraSed_forventKonsumentkall() throws IntegrationException {
        euxService.genererPdfFraSed(new SED());
        verify(euxConsumer).genererPdfFraSed(any());
    }

    @Test
    public void opprettBucOgSed_forventRinaSaksnummer() throws NotFoundException, IntegrationException {
        String bucType = BucType.LA_BUC_01.name();
        String mottakerLand = "SE";
        String mottakerId = null;
        SED sed = new SED();

        OpprettBucOgSedResponse opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType, mottakerLand, mottakerId, sed, null);

        assertThat(opprettBucOgSedResponse.getRinaSaksnummer()).isEqualTo("1122334455");

        verify(euxConsumer).opprettBucOgSed(anyString(), anyString(), any());
        verify(euxConsumer).hentInstitusjoner(eq(bucType), eq(null));
    }

    @Test
    public void opprettBucOgSed_forventException() throws Exception {
        String bucType = BucType.LA_BUC_01.name();
        String mottakerLand = "SE";
        String mottakerId = null;
        SED sed = new SED();

        doThrow(IntegrationException.class).when(euxConsumer).opprettBucOgSed(anyString(), anyString(), any());

        expectedException.expect(IntegrationException.class);
        euxService.opprettBucOgSed(bucType, mottakerLand, mottakerId, sed, null);

        verify(euxConsumer).opprettBucOgSed(anyString(), any(), any());
    }

    @Test
    public void opprettOgSendSed_medRinaSaksnummer_forventKonsumentKall() throws IntegrationException {
        SED sed = new SED();
        euxService.opprettOgSendSed(sed, "123123123");

        verify(euxConsumer).opprettSed(eq("123123123"), eq(null), eq(sed));
        verify(euxConsumer).sendSed(eq("123123123"), eq(null), eq("12345"));
    }

    @Test
    public void hentRinaUrl_medRinaSaksnummer_forventUrl() {
        String expectedUrl = RINA_MOCK_URL + "/portal/#/caseManagement/12345";
        String resultUrl = euxService.hentRinaUrl("12345");

        assertThat(resultUrl).isEqualTo(expectedUrl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hentRinaUrl_utenRinaSaksnummer_forventException() {
        euxService.hentRinaUrl(null);
    }

    @Test
    public void sendSed_forventKonsumentKall() throws IntegrationException {
        String rinaSaksnummer = "123";
        String dokumentId = "332211";
        euxService.sendSed(rinaSaksnummer, dokumentId);
        verify(euxConsumer).sendSed(eq(rinaSaksnummer), any(), eq(dokumentId));
    }

    @Test
    public void hentMottakerinstitusjoner_laBuc04LandSverige_forventEnInstitusjon() throws IntegrationException {
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner(BucType.LA_BUC_04.name(), "SE");
        assertThat(institusjoner).hasSize(1);
        assertThat(institusjoner.get(0).getAkronym()).isEqualTo("FK Sverige-TS70");
        verify(euxConsumer).hentInstitusjoner(eq(BucType.LA_BUC_04.name()), eq(null));
    }

    @Test
    public void hentMottakerinstitusjoner_sBuc18LandSverige_forventIngenInstitusjoner() throws IntegrationException {
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner("S_BUC_18", "SE");
        assertThat(institusjoner).isEmpty();
    }

    @Test
    public void sedErEndring_medFlereConversations_forventTrue() throws IntegrationException {
        String sedID = "3333";
        String rinaSaksnummer = "333222111";
        BUC buc = lagBucMedDocument(rinaSaksnummer, sedID);
        buc.getDocuments().get(0).setConversations(Arrays.asList(new Conversation(), new Conversation()));
        when(euxConsumer.hentBuC(eq(rinaSaksnummer))).thenReturn(buc);

        boolean erEndring = euxService.sedErEndring(sedID, rinaSaksnummer);

        verify(euxConsumer).hentBuC(eq(rinaSaksnummer));
        assertThat(erEndring).isTrue();
    }

    @Test
    public void sedErEndring_utenNoenConversations_forventFalse() throws IntegrationException {
        final String sedID = "3556";
        final String rinaSaksnummer = "54368";
        BUC buc = lagBucMedDocument(rinaSaksnummer, sedID);
        buc.getDocuments().get(0).setConversations(Collections.singletonList(new Conversation()));
        when(euxConsumer.hentBuC(eq(rinaSaksnummer))).thenReturn(buc);

        boolean erEndring = euxService.sedErEndring(sedID, rinaSaksnummer);

        verify(euxConsumer).hentBuC(eq(rinaSaksnummer));
        assertThat(erEndring).isFalse();
    }

    @Test
    public void sedErEndring_utenSederForBuc_forventFalse() throws IntegrationException {
        final String sedID = "33322";
        BUC buc = new BUC();
        Document document = new Document();
        document.setId(sedID);
        document.setConversations(Collections.singletonList(new Conversation()));
        buc.setDocuments(Arrays.asList(document, document, document));

        when(euxConsumer.hentBuC(anyString())).thenReturn(buc);

        boolean erEndring = euxService.sedErEndring(sedID, "123");
        verify(euxConsumer).hentBuC(eq("123"));
        assertThat(erEndring).isFalse();
    }

    @Test
    public void hentRinaUrlPrefix_forventRettString() {
        String rinaUrlPrefix = euxService.hentRinaUrlPrefix();
        assertThat(rinaUrlPrefix).isEqualTo(RINA_MOCK_URL + "/portal/#/caseManagement/");
    }

    private BUC lagBucMedDocument(String rinaSaksnummer, String sedID) {

        BUC buc = new BUC();
        buc.setId(rinaSaksnummer);
        Document document = new Document();
        document.setId(sedID);
        document.setConversations(Collections.singletonList(new Conversation()));
        buc.setDocuments(Collections.singletonList(document));

        return buc;
    }
}
