package no.nav.melosys.eessi.service.saksrelasjon;

import java.util.Collections;
import java.util.Optional;

import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import no.nav.melosys.eessi.service.sak.SakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaksrelasjonServiceTest {

    @Mock
    private FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;
    @Mock
    private CaseStoreConsumer caseStoreConsumer;
    @Mock
    private SakService sakService;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private SaksrelasjonService saksrelasjonService;

    @BeforeEach
    public void setup() {
        saksrelasjonService = new SaksrelasjonService(fagsakRinasakKoblingRepository, caseStoreConsumer, sakService, applicationEventPublisher);
    }

    private final String RINA_ID = "321";

    @Test
    void lagre_bucErIkkeLovvalg_oppdatertEuxCaseStore() {
        when(caseStoreConsumer.finnVedRinaSaksnummer(anyString())).thenReturn(Collections.emptyList());
        saksrelasjonService.lagreKobling(123L, "321", BucType.H_BUC_01);
        verify(caseStoreConsumer).lagre("123", "321");
    }

    @Test
    void lagreKobling_verifiserRepositoryKall() {
        saksrelasjonService.lagreKobling(123L, RINA_ID, BucType.LA_BUC_04);
        verify(fagsakRinasakKoblingRepository).save(any(FagsakRinasakKobling.class));
    }

    @Test
    void lagreKobling_ikkeLovvalgBucSakEksistererICaseStore_oppdaterCaseStore() {
        CaseStoreDto caseStoreDto = new CaseStoreDto(1L, "bucid", "saksnummer", "rinasaksnummer", "journalpostid", "tema");
        when(sakService.hentsak(anyLong())).thenReturn(new Sak());
        when(caseStoreConsumer.finnVedRinaSaksnummer(anyString())).thenReturn(Collections.singletonList(caseStoreDto));
        saksrelasjonService.lagreKobling(123L, "321", BucType.H_BUC_01);
        verify(caseStoreConsumer).lagre(any(CaseStoreDto.class));
    }

    @Test
    void finnVedRinaId_verifiserRepositoryKall() {
        saksrelasjonService.finnVedRinaSaksnummer(RINA_ID);
        verify(fagsakRinasakKoblingRepository).findByRinaSaksnummer(RINA_ID);
    }

    @Test
    void slettRinaId_verifiserRepositoryKall() {
        saksrelasjonService.slettVedRinaId(RINA_ID);
        verify(fagsakRinasakKoblingRepository).deleteByRinaSaksnummer(RINA_ID);
    }

    @Test
    void finnVedGsakSaksnummer_verifiserRepositoryKall() {
        saksrelasjonService.finnVedGsakSaksnummer(123L);
        verify(fagsakRinasakKoblingRepository).findAllByGsakSaksnummer(123L);
    }

    @Test
    void søkEtterSaksnummerFraRinaSaksnummer_finnesIEuxCaseStore_forventSaksnummer() {
        final String rinaSaksnummer = "1231232";
        when(caseStoreConsumer.finnVedRinaSaksnummer(rinaSaksnummer))
                .thenReturn(Collections.singletonList(new CaseStoreDto("123", rinaSaksnummer)));
        Optional<Long> saksnummer = saksrelasjonService.søkEtterSaksnummerFraRinaSaksnummer(rinaSaksnummer);
        assertThat(saksnummer).contains(123L);
    }

    @Test
    void finnArkivsakForRinaSaksnummer_finnesIkke_tomReturverdi() {
        assertThat(saksrelasjonService.finnAktørIDTilhørendeRinasak("123")).isEmpty();
    }

    @Test
    void finnArkivsakForRinaSaksnummer_saksrelasjonFinnes_returnererArkivsak() {
        final var fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setGsakSaksnummer(321L);
        when(fagsakRinasakKoblingRepository.findByRinaSaksnummer("123")).thenReturn(Optional.of(fagsakRinasakKobling));

        final var arkivsak = new Sak();
        arkivsak.setId("3333333");
        when(sakService.hentsak(fagsakRinasakKobling.getGsakSaksnummer())).thenReturn(arkivsak);

        assertThat(saksrelasjonService.finnArkivsakForRinaSaksnummer("123")).contains(arkivsak);
    }

    @Test
    void finnAktørIDTilhørendeRinasak_finnesIkke_tomReturverdi() {
        assertThat(saksrelasjonService.finnAktørIDTilhørendeRinasak("123")).isEmpty();
    }

    @Test
    void finnAktørIDTilhørendeRinasak_saksrelasjonFinnes_returnererAktørID() {
        final var aktørID = "1111";
        final var fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setGsakSaksnummer(321L);
        when(fagsakRinasakKoblingRepository.findByRinaSaksnummer("123")).thenReturn(Optional.of(fagsakRinasakKobling));

        final var arkivsak = new Sak();
        arkivsak.setAktoerId(aktørID);
        when(sakService.hentsak(fagsakRinasakKobling.getGsakSaksnummer())).thenReturn(arkivsak);


        assertThat(saksrelasjonService.finnAktørIDTilhørendeRinasak("123")).contains(aktørID);
    }
}
