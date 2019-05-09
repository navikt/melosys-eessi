package no.nav.melosys.eessi.service.joark;

import java.util.Optional;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseResponse;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.dokmotinngaaende.DokmotInngaaendeConsumer;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.RinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.caserelation.SaksrelasjonService;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpprettInngaaendeJournalpostServiceTest {

    @Mock
    private DokmotInngaaendeConsumer dokmotInngaaendeConsumer;

    @Mock
    private SaksrelasjonService saksrelasjonService;

    @Mock
    private DokkatService dokkatService;

    @Mock
    private GsakService gsakService;

    @Mock
    private EuxService euxService;

    @InjectMocks
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedHendelse sedMottatt;

    @Before
    public void setup() throws Exception {
        sedMottatt = enhancedRandom.nextObject(SedHendelse.class);
        sedMottatt.setBucType(BucType.LA_BUC_01.name());

        MottaInngaaendeForsendelseResponse response = enhancedRandom.nextObject(MottaInngaaendeForsendelseResponse.class);
        response.setJournalpostId("11223344");
        when(dokmotInngaaendeConsumer.create(any()))
                .thenReturn(response);

        RinasakKobling caseRelation = enhancedRandom.nextObject(RinasakKobling.class);
        when(saksrelasjonService.finnVedRinaId(anyString()))
                .thenReturn(Optional.ofNullable(caseRelation));

        DokkatSedInfo dokkatSedInfo = enhancedRandom.nextObject(DokkatSedInfo.class);
        when(dokkatService.hentMetadataFraDokkat(anyString()))
                .thenReturn(dokkatSedInfo);

        Sak sak = enhancedRandom.nextObject(Sak.class);
        sak.setId("1234"); // Må kunne bli parset til Long
        when(gsakService.getSak(anyLong()))
                .thenReturn(sak);

        when(gsakService.createSak(anyString()))
                .thenReturn(sak);

        ParticipantInfo sender = ParticipantInfo.builder()
                .name("NAVT002")
                .id("NO:NAVT002")
                .build();
        when(euxService.hentUtsender(anyString()))
                .thenReturn(sender);
    }

    @Test
    public void arkiverInngaaendeSed_expectId() throws Exception {
        SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, "123123");

        assertThat(sakInformasjon, not(nullValue()));
        assertThat(sakInformasjon.getJournalpostId(), is("11223344"));

        verify(dokmotInngaaendeConsumer, times(1)).create(any());
        verify(saksrelasjonService, times(1)).finnVedRinaId(anyString());
        verify(dokkatService, times(1)).hentMetadataFraDokkat(anyString());
        verify(gsakService, times(1)).getSak(anyLong());
        verify(gsakService, times(0)).createSak(any());
        verify(euxService, times(1)).hentUtsender(anyString());
    }

    @Test
    public void arkiverInngaaendeSed_expectCreateSak() throws Exception {
        when(saksrelasjonService.finnVedRinaId(anyString()))
                .thenReturn(Optional.empty());

        SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, "123123");

        assertThat(sakInformasjon, not(nullValue()));
        assertThat(sakInformasjon.getJournalpostId(), is("11223344"));

        verify(gsakService, times(0)).getSak(anyLong());
        verify(gsakService, times(1)).createSak(any());
    }

    @Test(expected = IntegrationException.class)
    public void arkiverInngaaendeSed_expectIntegrationException() throws Exception {
        when(saksrelasjonService.finnVedRinaId(anyString()))
                .thenReturn(Optional.empty());

        when(gsakService.createSak(any()))
                .thenReturn(null);

        opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, "123123");
    }
}