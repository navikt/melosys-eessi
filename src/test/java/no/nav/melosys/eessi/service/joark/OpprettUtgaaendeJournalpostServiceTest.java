package no.nav.melosys.eessi.service.joark;

import java.net.URL;
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.dokarkivsed.api.v1.ArkiverUtgaaendeSed;
import no.nav.eessi.basis.SedSendt;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.dokarkivsed.DokarkivSedConsumer;
import no.nav.melosys.eessi.integration.dokarkivsed.OpprettUtgaaendeJournalpostResponse;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.repository.CaseRelationRepository;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.apache.commons.io.IOUtils;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpprettUtgaaendeJournalpostServiceTest {

    private static final String JOURNALPOST_ID = "123";

    @Mock
    private DokarkivSedConsumer dokarkivSedConsumer;
    @Mock
    private GsakService gsakService;
    @Mock
    private EuxConsumer euxConsumer;
    @Mock
    private CaseRelationRepository caseRelationRepository;

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


        URL jsonUrl = getClass().getClassLoader().getResource("buc_receivers.json");
        assertThat(jsonUrl, not(nullValue()));
        JsonNode receiverInfo = new ObjectMapper().readValue(IOUtils.toString(jsonUrl), JsonNode.class);
        when(euxConsumer.hentDeltagere(anyString())).thenReturn(receiverInfo);

        CaseRelation caseRelation = enhancedRandom.nextObject(CaseRelation.class);
        when(caseRelationRepository.findByRinaId(anyString())).thenReturn(Optional.of(caseRelation));

        when(dokarkivSedConsumer.create(any(ArkiverUtgaaendeSed.class))).thenReturn(response);

        Sak sak = enhancedRandom.nextObject(Sak.class);
        sedSendt = enhancedRandom.nextObject(SedSendt.class);

        when(gsakService.getSak(anyLong())).thenReturn(sak);

    }

    @Test
    public void journalfoer_expectId() throws Exception {
        String result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
        assertThat(result, is(JOURNALPOST_ID));
    }

    @Test(expected = NotFoundException.class)
    public void journalfoer_noCaseRelation_expectNotFoundException() throws Exception {
        when(caseRelationRepository.findByRinaId(anyString())).thenReturn(Optional.empty());
        opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
    }
}