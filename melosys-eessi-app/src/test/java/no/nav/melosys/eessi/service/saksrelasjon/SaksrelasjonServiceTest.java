package no.nav.melosys.eessi.service.saksrelasjon;

import java.util.Collections;
import java.util.Optional;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.integration.sak.SakConsumer;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksrelasjonServiceTest {

    @Mock
    private FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;
    @Mock
    private CaseStoreConsumer caseStoreConsumer;
    @Mock
    private SakConsumer sakConsumer;

    private SaksrelasjonService saksrelasjonService;

    @Before
    public void setup() {
        saksrelasjonService = new SaksrelasjonService(fagsakRinasakKoblingRepository, caseStoreConsumer, sakConsumer);
    }

    private final String RINA_ID = "321";

    @Test
    public void lagre_bucErIkkeLovvalg_oppdatertEuxCaseStore() throws Exception {
        when(caseStoreConsumer.finnVedRinaSaksnummer(anyString())).thenReturn(Collections.emptyList());
        saksrelasjonService.lagreKobling(123L, "321", BucType.H_BUC_01);
        verify(caseStoreConsumer).lagre(eq("123"), eq("321"));
    }

    @Test
    public void lagreKobling_verifiserRepositoryKall() throws IntegrationException {
        saksrelasjonService.lagreKobling(123L, RINA_ID, BucType.LA_BUC_04);
        verify(fagsakRinasakKoblingRepository).save(any(FagsakRinasakKobling.class));
    }

    @Test
    public void lagreKobling_ikkeLovvalgBucSakEksistererICaseStore_oppdaterCaseStore() throws IntegrationException {
        CaseStoreDto caseStoreDto = new CaseStoreDto(1L, "bucid", "saksnummer", "rinasaksnummer", "journalpostid", "tema");
        when(sakConsumer.getSak(anyString())).thenReturn(new Sak());
        when(caseStoreConsumer.finnVedRinaSaksnummer(anyString())).thenReturn(Collections.singletonList(caseStoreDto));
        saksrelasjonService.lagreKobling(123L, "321", BucType.H_BUC_01);
        verify(caseStoreConsumer).lagre(any(CaseStoreDto.class));
    }

    @Test
    public void finnVedRinaId_verifiserRepositoryKall() {
        saksrelasjonService.finnVedRinaSaksnummer(RINA_ID);
        verify(fagsakRinasakKoblingRepository).findByRinaSaksnummer(eq(RINA_ID));
    }

    @Test
    public void slettRinaId_verifiserRepositoryKall() {
        saksrelasjonService.slettVedRinaId(RINA_ID);
        verify(fagsakRinasakKoblingRepository).deleteByRinaSaksnummer(eq(RINA_ID));
    }

    @Test
    public void finnVedGsakSaksnummer_verifiserRepositoryKall() {
        saksrelasjonService.finnVedGsakSaksnummer(123L);
        verify(fagsakRinasakKoblingRepository).findAllByGsakSaksnummer(eq(123L));
    }

    @Test
    public void søkEtterSaksnummerFraRinaSaksnummer_finnesIEuxCaseStore_forventSaksnummer() throws IntegrationException {
        final String rinaSaksnummer = "1231232";
        when(caseStoreConsumer.finnVedRinaSaksnummer(rinaSaksnummer))
                .thenReturn(Collections.singletonList(new CaseStoreDto("123", rinaSaksnummer)));
        Optional<Long> saksnummer = saksrelasjonService.søkEtterSaksnummerFraRinaSaksnummer(rinaSaksnummer);
        assertThat(saksnummer).isPresent();
        assertThat(saksnummer.get()).isEqualTo(123L);
    }
}