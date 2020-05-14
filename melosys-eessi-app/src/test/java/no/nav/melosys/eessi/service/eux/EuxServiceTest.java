package no.nav.melosys.eessi.service.eux;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.Vedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Conversation;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
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

    private final String opprettetBucID = "114433";
    private final String opprettetSedID = "12222";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() throws IOException, IntegrationException {
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        euxService = new EuxService(euxConsumer, bucMetrikker, RINA_MOCK_URL, "false");

        when(euxConsumer.opprettBuC(anyString())).thenReturn(opprettetBucID);
        when(euxConsumer.opprettSed(eq(opprettetBucID), any(SED.class))).thenReturn(opprettetSedID);

        URL institusjonerJsonUrl = getClass().getClassLoader().getResource("institusjoner.json");
        List<Institusjon> institusjoner = objectMapper.readValue(institusjonerJsonUrl, new TypeReference<List<Institusjon>>(){});
        when(euxConsumer.hentInstitusjoner(anyString(), any()))
                .thenReturn(institusjoner);
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
    public void genererPdfFraSed_forventKonsumentkall() throws IntegrationException {
        euxService.genererPdfFraSed(new SED());
        verify(euxConsumer).genererPdfFraSed(any());
    }

    @Test
    public void opprettBucOgSed_forventRinaSaksnummer() throws IntegrationException {
        BucType bucType = BucType.LA_BUC_01;
        Collection<String> mottakere = List.of("SE:123");
        SED sed = new SED();

        OpprettBucOgSedResponse opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType, mottakere, sed, new Vedlegg("filen min", "pdf".getBytes()));

        verify(euxConsumer).opprettBuC(eq(bucType.name()));
        verify(euxConsumer).opprettSed(eq(opprettetBucID), eq(sed));
        verify(euxConsumer).leggTilVedlegg(eq(opprettetBucID), eq(opprettetSedID), eq("pdf"), any(Vedlegg.class));

        assertThat(opprettBucOgSedResponse.getRinaSaksnummer()).isEqualTo(opprettetBucID);
    }

    @Test
    public void opprettOgSendSed_medRinaSaksnummer_forventKonsumentKall() throws IntegrationException {
        SED sed = new SED();
        euxService.opprettOgSendSed(sed, opprettetBucID);

        verify(euxConsumer).sendSed(eq(opprettetBucID), eq(null), eq(opprettetSedID));
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
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner("S_BUC_24", "SE");
        assertThat(institusjoner).isEmpty();
    }

    @Test
    public void hentMottakerinstitusjoner_laBuc04LandGB_forventEnInstitusjon() throws IntegrationException {
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner("LA_BUC_04", "GB");
        assertThat(institusjoner).hasSize(1);

        Institusjon institusjon = institusjoner.get(0);
        assertThat(institusjon.getAkronym()).isEqualTo("FK UK-TITTEI");
        assertThat(institusjon.getLandkode()).isEqualTo("GB");
    }

    @Test
    public void hentMottakerinstitusjoner_laBuc04LandGR_forventEnInstitusjon() throws IntegrationException {
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner("LA_BUC_04", "GR");
        assertThat(institusjoner).hasSize(1);

        Institusjon institusjon = institusjoner.get(0);
        assertThat(institusjon.getAkronym()).isEqualTo("FK EL-TITTEI");
        assertThat(institusjon.getLandkode()).isEqualTo("GR");
    }

    @Test
    public void hentMottakerinstitusjoner_laBuc02LandGRNorgeIkkePåkoblet_forventTomListe() throws IntegrationException {
        euxService = new EuxService(euxConsumer, bucMetrikker, "url", "true");
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner("LA_BUC_02", "GR");
        assertThat(institusjoner).hasSize(0);

        verify(euxConsumer, never()).hentInstitusjoner(any(), any());
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
