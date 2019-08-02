package no.nav.melosys.eessi.service.joark;

import java.util.ArrayList;
import java.util.Optional;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.caserelation.SaksrelasjonService;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpprettUtgaaendeJournalpostServiceTest {

    private static final String JOURNALPOST_ID = "123";

    @Mock
    private GsakService gsakService;
    @Mock
    private EuxService euxService;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private JournalpostService journalpostService;

    @InjectMocks
    private OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;

    private SedHendelse sedSendt;
    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    @Before
    public void setup() throws Exception {

        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, new ArrayList<>(), "123", null);

        when(euxService.hentSedPdf(anyString(), anyString())).thenReturn(new byte[0]);

        FagsakRinasakKobling fagsakRinasakKobling = enhancedRandom.nextObject(FagsakRinasakKobling.class);
        when(saksrelasjonService.finnVedRinaId(anyString())).thenReturn(Optional.of(fagsakRinasakKobling));

        when(journalpostService.opprettUtgaaendeJournalpost(any(SedHendelse.class), any(), any())).thenReturn(response);

        Sak sak = enhancedRandom.nextObject(Sak.class);
        sedSendt = enhancedRandom.nextObject(SedHendelse.class);

        when(gsakService.hentsak(anyLong())).thenReturn(sak);
    }

    @Test
    public void journalfoer_expectId() throws Exception {
        String result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
        assertThat(result, is(JOURNALPOST_ID));
    }

    @Test(expected = NotFoundException.class)
    public void journalfoer_noCaseRelation_expectNotFoundException() throws Exception {
        when(saksrelasjonService.finnVedRinaId(anyString())).thenReturn(Optional.empty());
        opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
    }
}