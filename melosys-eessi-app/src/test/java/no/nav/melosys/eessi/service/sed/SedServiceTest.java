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
import no.nav.melosys.eessi.models.SedVedlegg;
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

    private final String RINA_ID = "aabbcc";

    @Before
    public void setup() throws Exception {

        OpprettBucOgSedResponse opprettBucOgSedResponse = new OpprettBucOgSedResponse(RINA_ID, "123");

        when(euxService.opprettBucOgSed(any(BucType.class), anyCollection(), any(SED.class), any()))
                .thenReturn(opprettBucOgSedResponse);

        when(euxService.hentRinaUrl(anyString())).thenReturn("URL");
    }

    @Test
    public void opprettBucOgSed_forventRinacaseId() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        final BucType bucType = BucType.LA_BUC_01;
        final SedVedlegg vedlegg = new SedVedlegg("tittei", "pdf".getBytes());
        OpprettSedDto sedDto = sendSedService.opprettBucOgSed(sedData, vedlegg, BucType.LA_BUC_01, true);
        verify(euxService).opprettBucOgSed(eq(bucType), eq(sedData.getMottakerIder()), any(SED.class), eq(vedlegg));
        assertThat(sedDto.getRinaSaksnummer()).isEqualTo(RINA_ID);
    }

    @Test
    public void opprettBucOgSed_sendSedKasterException_forventSlettBucOgSakrelasjon() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        doThrow(IntegrationException.class).when(euxService).sendSed(anyString(), anyString());

        Exception exception = null;
        try {
            sendSedService.opprettBucOgSed(sedData, null, BucType.LA_BUC_02, true);
        } catch (IntegrationException e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(IntegrationException.class);
        verify(euxService).slettBuC(eq(RINA_ID));
        verify(saksrelasjonService).slettVedRinaId(eq(RINA_ID));
    }

    @Test
    public void opprettBucOgSed_brukerMedSensitiveOpplysninger_forventSettSakSensitiv() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        sedData.getBruker().setHarSensitiveOpplysninger(true);
        OpprettSedDto sedDto = sendSedService.opprettBucOgSed(sedData, null, BucType.LA_BUC_02, true);

        assertThat(sedDto.getRinaSaksnummer()).isEqualTo(RINA_ID);
        verify(euxService).settSakSensitiv(RINA_ID);
    }

    @Test
    public void createAndSend_sedEksistererPaaBuc_forventOppdaterEksisterendeSed() throws Exception {
        Long gsakSaksnummer = 123L;
        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setGsakSaksnummer(gsakSaksnummer);

        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setBucType(BucType.LA_BUC_02);
        fagsakRinasakKobling.setRinaSaksnummer(RINA_ID);
        fagsakRinasakKobling.setGsakSaksnummer(gsakSaksnummer);
        when(saksrelasjonService.finnVedGsakSaksnummerOgBucType(eq(gsakSaksnummer), eq(BucType.LA_BUC_02)))
                .thenReturn(Collections.singletonList(fagsakRinasakKobling));

        BUC buc = new BUC();
        buc.setId(RINA_ID);
        buc.setStatus("open");

        String emptyDocumentId = "docid12314";
        Document emptyDocument = new Document();
        emptyDocument.setStatus("empty");
        emptyDocument.setId(emptyDocumentId);
        emptyDocument.setType(SedType.A003.name());

        String sentDocumentId = "docid321";
        Document sentDocument = new Document();
        sentDocument.setStatus("sent");
        sentDocument.setId(sentDocumentId);
        sentDocument.setType(SedType.A003.name());

        buc.setDocuments(List.of(emptyDocument, sentDocument));

        Action emptyDocAction = new Action();
        emptyDocAction.setDocumentId(emptyDocumentId);
        emptyDocAction.setOperation("update");

        Action sentDocAction = new Action();
        sentDocAction.setDocumentId(sentDocumentId);
        sentDocAction.setOperation("update");

        buc.setActions(List.of(emptyDocAction, sentDocAction));

        when(euxService.hentBuc(eq(RINA_ID)))
                .thenReturn(buc);

        sendSedService.opprettBucOgSed(sedDataDto, null, BucType.LA_BUC_02, true);

        verify(euxService).oppdaterSed(eq(RINA_ID), eq(sentDocumentId), any(SED.class));
        verify(euxService, never()).opprettBucOgSed(any(), any(), any(), any());
        verify(euxService).sendSed(anyString(), anyString());
    }

    @Test(expected = MappingException.class)
    public void opprettBucOgSed_ingenGsakSaksnummer_forventMappingException() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        sedData.setGsakSaksnummer(null);
        sendSedService.opprettBucOgSed(sedData, null, BucType.LA_BUC_04, true);
    }

    @Test
    public void opprettBucOgSed_LABUC01_forventOpprettNyBucOgSedMedUrl() throws Exception {
        SedDataDto sedData = SedDataStub.getStub();
        OpprettSedDto response = sendSedService.opprettBucOgSed(sedData, null, BucType.LA_BUC_01, false);

        verify(euxService).opprettBucOgSed(any(BucType.class), anyCollection(), any(), any());
        verify(euxService).hentRinaUrl(eq(RINA_ID));
        verify(euxService, never()).sendSed(anyString(), anyString());
        assertThat(response.getRinaSaksnummer()).isEqualTo(RINA_ID);
        assertThat(response.getRinaUrl()).isEqualTo("URL");
    }

    @Test
    public void sendPåEksisterendeBuc_forventMetodekall() throws IOException, URISyntaxException, IntegrationException, NotFoundException, MappingException {
        BUC buc = new BUC();
        buc.setActions(Arrays.asList(
                new Action("A001", "A001", "111", "Read"),
                new Action("A009", "A009", "222", "Create")
        ));
        when(euxService.hentBuc(anyString())).thenReturn(buc);

        SedDataDto sedDataDto = SedDataStub.getStub();
        sendSedService.sendPåEksisterendeBuc(sedDataDto, "123", SedType.A009);

        verify(euxService).hentBuc(anyString());
        verify(euxService).opprettOgSendSed(any(SED.class), anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendPåEksisterendeBuc_kanIkkeOpprettesPåBuc_forventException() throws IntegrationException, NotFoundException, MappingException, IOException, URISyntaxException {
        BUC buc = new BUC();
        buc.setActions(Collections.singletonList(new Action("A001", "A001", "111", "Read")));
        when(euxService.hentBuc(anyString())).thenReturn(buc);

        SedDataDto sedDataDto = SedDataStub.getStub();
        sedDataDto.setSvarAnmodningUnntak(lagSvarAnmodningUnntakDto(SvarAnmodningUnntakBeslutning.INNVILGELSE));

        sendSedService.sendPåEksisterendeBuc(sedDataDto, "123", SedType.A011);
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
}
