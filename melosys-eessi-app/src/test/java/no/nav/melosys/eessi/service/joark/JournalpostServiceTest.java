package no.nav.melosys.eessi.service.joark;

import java.util.Collections;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalpostServiceTest {

    @Mock
    private DokkatService dokkatService;
    @Mock
    private JournalpostapiConsumer journalpostapiConsumer;

    private JournalpostService journalpostService;

    private EnhancedRandom random = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedHendelse sedHendelse;
    private Sak sak;
    private DokkatSedInfo dokkatSedInfo;

    @BeforeEach
    public void setUp() throws Exception {
        journalpostService = new JournalpostService(dokkatService, journalpostapiConsumer);

        sedHendelse = random.nextObject(SedHendelse.class);
        sak = random.nextObject(Sak.class);
        dokkatSedInfo = random.nextObject(DokkatSedInfo.class);

        when(dokkatService.hentMetadataFraDokkat(anyString())).thenReturn(dokkatSedInfo);
    }

    @Test
    void opprettInngaaendeJournalpost_verifiserEndeligJfr() {
        journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(new byte[0]), "123321");
        verify(journalpostapiConsumer).opprettJournalpost(any(OpprettJournalpostRequest.class), eq(false));
    }

    @Test
    void opprettUtgaaendeJournalpost_verifiserEndeligJfr() {
        journalpostService.opprettUtgaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(new byte[0]), "123321");
        verify(journalpostapiConsumer).opprettJournalpost(any(OpprettJournalpostRequest.class), eq(true));
    }

    private SedMedVedlegg sedMedVedlegg(byte[] innhold) {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("","", innhold), Collections.emptyList());
    }
}
