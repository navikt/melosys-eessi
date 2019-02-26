package no.nav.melosys.eessi.service.joark;

import java.util.Objects;
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseResponse;
import no.nav.eessi.basis.SedMottatt;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.dokmotinngaaende.DokmotInngaaendeConsumer;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.repository.CaseRelationRepository;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpprettInngaaendeJournalpostServiceTest {

    @Mock
    private DokmotInngaaendeConsumer dokmotInngaaendeConsumer;

    @Mock
    private CaseRelationRepository caseRelationRepository;

    @Mock
    private AktoerConsumer aktoerConsumer;

    @Mock
    private DokkatService dokkatService;

    @Mock
    private GsakService gsakService;

    @Mock
    private EuxConsumer euxConsumer;

    @InjectMocks
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedMottatt sedMottatt;

    @Before
    public void setup() throws Exception {
        sedMottatt = enhancedRandom.nextObject(SedMottatt.class);

        MottaInngaaendeForsendelseResponse response = enhancedRandom.nextObject(MottaInngaaendeForsendelseResponse.class);
        response.setJournalpostId("11223344");
        when(dokmotInngaaendeConsumer.create(any()))
                .thenReturn(response);

        CaseRelation caseRelation = enhancedRandom.nextObject(CaseRelation.class);
        when(caseRelationRepository.findByRinaId(anyString()))
                .thenReturn(Optional.ofNullable(caseRelation));

        when(aktoerConsumer.getAktoerId(anyString()))
                .thenReturn("123456789");

        DokkatSedInfo dokkatSedInfo = enhancedRandom.nextObject(DokkatSedInfo.class);
        when(dokkatService.hentMetadataFraDokkat(anyString()))
                .thenReturn(dokkatSedInfo);

        Sak sak = enhancedRandom.nextObject(Sak.class);
        sak.setId("1234"); // Må kunne bli parset til Long
        when(gsakService.getSak(anyLong()))
                .thenReturn(sak);

        when(gsakService.createSak(anyString()))
                .thenReturn(sak);

        JsonNode deltagereJson = new ObjectMapper().readTree(Objects.requireNonNull(getClass().getClassLoader().getResource("buc_participants.json")));
        when(euxConsumer.hentDeltagere(anyString()))
                .thenReturn(deltagereJson);
    }

    @Test
    public void arkiverInngaaendeSed_expectId() throws Exception {
        String journalpostId = opprettInngaaendeJournalpostService.arkiverInngaaendeSed(sedMottatt);

        assertThat(journalpostId, not(nullValue()));
        assertThat(journalpostId, is("11223344"));

        verify(dokmotInngaaendeConsumer, times(1)).create(any());
        verify(caseRelationRepository, times(1)).findByRinaId(anyString());
        verify(aktoerConsumer, times(1)).getAktoerId(anyString());
        verify(dokkatService, times(1)).hentMetadataFraDokkat(anyString());
        verify(gsakService, times(1)).getSak(anyLong());
        verify(gsakService, times(0)).createSak(any());
        verify(euxConsumer, times(1)).hentDeltagere(anyString());
    }

    @Test
    public void arkiverInngaaendeSed_expectCreateSak() throws Exception {
        when(caseRelationRepository.findByRinaId(anyString()))
                .thenReturn(Optional.empty());

        String journalpostId = opprettInngaaendeJournalpostService.arkiverInngaaendeSed(sedMottatt);

        assertThat(journalpostId, not(nullValue()));
        assertThat(journalpostId, is("11223344"));

        verify(gsakService, times(0)).getSak(anyLong());
        verify(gsakService, times(1)).createSak(any());
    }

    @Test(expected = IntegrationException.class)
    public void arkiverInngaaendeSed_expectIntegrationException() throws Exception {
        when(caseRelationRepository.findByRinaId(anyString()))
                .thenReturn(Optional.empty());

        when(gsakService.createSak(any()))
                .thenReturn(null);

        opprettInngaaendeJournalpostService.arkiverInngaaendeSed(sedMottatt);
    }
}