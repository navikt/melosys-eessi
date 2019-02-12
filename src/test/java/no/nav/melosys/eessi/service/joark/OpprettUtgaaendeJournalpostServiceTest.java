package no.nav.melosys.eessi.service.joark;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.dokarkivsed.api.v1.ArkiverUtgaaendeSed;
import no.nav.eessi.basis.SedSendt;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.dokarkivsed.DokarkivSedConsumer;
import no.nav.melosys.eessi.integration.dokarkivsed.OpprettUtgaaendeJournalpostResponse;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
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
    private DokarkivSedConsumer dokarkivSedConsumer;
    @Mock
    private DokkatService dokkatService;
    @Mock
    private GsakService gsakService;

    @InjectMocks
    private OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;

    private SedSendt sedSendt;
    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    @Before
    public void setup() throws Exception {

        OpprettUtgaaendeJournalpostResponse response = new OpprettUtgaaendeJournalpostResponse();
        response.setJournalpostId(JOURNALPOST_ID);
        response.setJournalfoeringStatus(OpprettUtgaaendeJournalpostResponse.JournalTilstand.ENDELIG_JOURNALFOERT);
        response.setKanalreferanseId("123");

        when(dokarkivSedConsumer.create(any(ArkiverUtgaaendeSed.class))).thenReturn(response);

        Sak sak = enhancedRandom.nextObject(Sak.class);
        DokkatSedInfo dokkatSedInfo = enhancedRandom.nextObject(DokkatSedInfo.class);
        sedSendt = enhancedRandom.nextObject(SedSendt.class);

        when(gsakService.getSak(anyLong())).thenReturn(sak);
        when(dokkatService.hentMetadataFraDokkat(anyString())).thenReturn(dokkatSedInfo);

    }

    @Test
    public void journalfoer_expectId() throws Exception {
        String result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
        assertThat(result, is(JOURNALPOST_ID));
    }
}