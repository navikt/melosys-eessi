package no.nav.melosys.eessi.service.sed;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import no.nav.melosys.eessi.controller.dto.OpprettSedDto;
import no.nav.melosys.eessi.controller.dto.Periode;
import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.SvarAnmodningUnntakDto;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.Action;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.SvarAnmodningUnntakBeslutning;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.eux.OpprettBucOgSedResponse;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SedServiceTest {

    @Mock
    private EuxService euxService;
    @Mock
    private SaksrelasjonService saksrelasjonService;

    @InjectMocks
    private SedService sendSedService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Captor
    public ArgumentCaptor<SED> sedArgumentCaptor;

    private final String RINA_ID = "aabbcc";

    @Before
    public void setup() throws Exception {

        OpprettBucOgSedResponse opprettBucOgSedResponse = new OpprettBucOgSedResponse(RINA_ID, "123");

        when(euxService.opprettBucOgSed(anyString(), anyString(), any(), any(SED.class)))
                .thenReturn(opprettBucOgSedResponse);

        when(euxService.hentRinaUrl(anyString())).thenReturn("URL");
    }

    @Test
    public void createAndSend_forventRinacaseId() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        String rinaId = sendSedService.createAndSend(sedData);
        assertThat(rinaId).isEqualTo(RINA_ID);
    }

    @Test
    public void createAndSend_sendSedKasterException_forventSlettBucOgSakrelasjon() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        doThrow(IntegrationException.class).when(euxService).sendSed(anyString(), anyString());

        Exception exception = null;
        try {
            sendSedService.createAndSend(sedData);
        } catch (IntegrationException e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(IntegrationException.class);
        verify(euxService).slettBuC(eq(RINA_ID));
        verify(saksrelasjonService).slettVedRinaId(eq(RINA_ID));
    }

    @Test
    public void createAndSend_sedEksistererPaaBuc_forventOppdaterEksisterendeSed() throws Exception {
        Long gsakSaksnummer = 123L;
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setGsakSaksnummer(gsakSaksnummer);

        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setBucType(BucType.LA_BUC_04);
        fagsakRinasakKobling.setRinaSaksnummer(RINA_ID);
        fagsakRinasakKobling.setGsakSaksnummer(gsakSaksnummer);
        when(saksrelasjonService.finnVedGsakSaksnummerOgBucType(eq(gsakSaksnummer), eq(BucType.LA_BUC_04)))
                .thenReturn(Collections.singletonList(fagsakRinasakKobling));

        String documentId = "docid12314";
        BUC buc = new BUC();
        buc.setId(RINA_ID);
        buc.setStatus("open");
        Document document = new Document();
        document.setStatus("open");
        document.setId(documentId);
        document.setType(SedType.A009.name());
        buc.setDocuments(Collections.singletonList(document));

        Action action = new Action();
        action.setDocumentId(documentId);
        action.setOperation("update");
        buc.setActions(Collections.singletonList(action));

        when(euxService.hentBuc(eq(RINA_ID)))
                .thenReturn(buc);

        sendSedService.createAndSend(sedDataDto);

        verify(euxService).oppdaterSed(eq(RINA_ID), eq(documentId), any(SED.class));
        verify(euxService, never()).opprettBucOgSed(any(), any(), any(), any());
    }

    @Test(expected = MappingException.class)
    public void createAndSend_ingenGsakSaksnummer_forventMappingException() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        sedData.setGsakSaksnummer(null);
        sendSedService.createAndSend(sedData);
    }

    @Test
    public void createSed_A003_forventOpprettNyBucOgSedMedUrl() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        OpprettSedDto response = sendSedService.createSed(sedData, BucType.LA_BUC_03);

        verify(euxService).opprettBucOgSed(anyString(), anyString(), any(), any());
        verify(euxService).hentRinaUrl(eq(RINA_ID));
        assertThat(response.getBucId()).isEqualTo(RINA_ID);
        assertThat(response.getRinaUrl()).isEqualTo("URL");
    }

    @Test
    public void anmodningUnntakSvar_innvilgelse_sendA011() throws NotFoundException, IntegrationException {
        SvarAnmodningUnntakDto svarAnmodningUnntakDto = lagSvarAnmodningUnntakDto(SvarAnmodningUnntakBeslutning.INNVILGELSE);

        when(euxService.hentBuc(anyString())).thenReturn(lagBuc());
        when(euxService.hentSed(anyString(), anyString())).thenReturn(new SED());

        sendSedService.anmodningUnntakSvar(svarAnmodningUnntakDto, "123");

        verify(euxService).hentBuc(eq("123"));
        verify(euxService).hentSed(eq("123"), anyString());
        verify(euxService).opprettOgSendSed(sedArgumentCaptor.capture(), anyString());

        SED sendtSed = sedArgumentCaptor.getValue();
        assertThat(sendtSed.getSed()).isEqualTo(SedType.A011.toString());
    }

    @Test
    public void anmodningUnntakSvar_avslag_sendA002() throws IntegrationException, NotFoundException {
        SvarAnmodningUnntakDto svarAnmodningUnntakDto = lagSvarAnmodningUnntakDto(SvarAnmodningUnntakBeslutning.AVSLAG);

        when(euxService.hentBuc(anyString())).thenReturn(lagBuc());
        when(euxService.hentSed(anyString(), anyString())).thenReturn(new SED());

        sendSedService.anmodningUnntakSvar(svarAnmodningUnntakDto, "123");

        verify(euxService).hentBuc(eq("123"));
        verify(euxService).hentSed(eq("123"), anyString());
        verify(euxService).opprettOgSendSed(sedArgumentCaptor.capture(), anyString());

        SED sendtSed = sedArgumentCaptor.getValue();
        assertThat(sendtSed.getSed()).isEqualTo(SedType.A002.toString());
    }

    @Test
    public void genererPdfFraSed_forventKall() throws IOException, URISyntaxException, IntegrationException, MappingException, NotFoundException {
        SedDataDto sedDataDto = SedDataStub.getStub();
        final byte[] MOCK_PDF = "vi later som om dette er en pdf".getBytes();

        when(euxService.genererPdfFraSed(any(SED.class))).thenReturn(MOCK_PDF);
        byte[] pdf = sendSedService.genererPdfFraSed(sedDataDto, SedType.A001);

        verify(euxService).genererPdfFraSed(any(SED.class));
        assertThat(pdf).isEqualTo(MOCK_PDF);
    }

    private SvarAnmodningUnntakDto lagSvarAnmodningUnntakDto(SvarAnmodningUnntakBeslutning beslutning) {
        return new SvarAnmodningUnntakDto(
                beslutning,
                "begrunnelse",
                new Periode(LocalDate.now(), LocalDate.now().plusDays(1L))
        );
    }

    private BUC lagBuc() {
        BUC buc = new BUC();
        buc.setId("123");

        List<Document> documents = Arrays.asList(
                lagDocument("A001", "1"),
                lagDocument("A005", "2"),
                lagDocument("H003", "3")
        );

        buc.setDocuments(documents);
        return buc;
    }

    private Document lagDocument(String type, String id) {
        Document document = new Document();
        document.setType(type);
        document.setId(id);
        return document;
    }
}
