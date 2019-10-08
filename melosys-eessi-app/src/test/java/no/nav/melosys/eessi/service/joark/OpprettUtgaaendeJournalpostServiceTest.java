package no.nav.melosys.eessi.service.joark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.gsak.GsakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpprettUtgaaendeJournalpostServiceTest {

    private static final String JOURNALPOST_ID = "123";

    @Mock
    private GsakService gsakService;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private JournalpostService journalpostService;
    @Mock
    private EuxService euxService;

    private OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;

    private SedHendelse sedSendt;
    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    @Before
    public void setup() throws Exception {
        opprettUtgaaendeJournalpostService = new OpprettUtgaaendeJournalpostService(
                gsakService, saksrelasjonService, journalpostService, euxService
        );

        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, new ArrayList<>(), "123", null);

        when(euxService.hentSedMedVedlegg(anyString(), anyString())).thenReturn(sedMedVedlegg(new byte[0]));

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
        assertThat(result).isEqualTo(JOURNALPOST_ID);
    }

    @Test(expected = NotFoundException.class)
    public void journalfoer_noCaseRelation_expectNotFoundException() throws Exception {
        when(saksrelasjonService.finnVedRinaId(anyString())).thenReturn(Optional.empty());
        opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
    }

    private SedMedVedlegg sedMedVedlegg(byte[] innhold) {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("","", innhold), Collections.emptyList());
    }
}