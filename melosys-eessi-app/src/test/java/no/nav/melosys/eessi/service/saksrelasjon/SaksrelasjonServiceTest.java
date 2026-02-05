package no.nav.melosys.eessi.service.saksrelasjon;

import java.util.Optional;

import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.repository.FagsakRinasakKoblingRepository;
import no.nav.melosys.eessi.service.sak.ArkivsakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.melosys.eessi.models.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class SaksrelasjonServiceTest {

    @Mock
    private FagsakRinasakKoblingRepository fagsakRinasakKoblingRepository;
    @Mock
    private ArkivsakService arkivsakService;

    private SaksrelasjonService saksrelasjonService;

    @BeforeEach
    public void setup() {
        saksrelasjonService = new SaksrelasjonService(fagsakRinasakKoblingRepository, arkivsakService);
    }

    private final String RINA_ID = "321";

    @Test
    void lagreKobling_verifiserRepositoryKall() {
        saksrelasjonService.lagreKobling(123L, RINA_ID, BucType.LA_BUC_04);
        verify(fagsakRinasakKoblingRepository).save(any(FagsakRinasakKobling.class));
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
        when(arkivsakService.hentsak(fagsakRinasakKobling.getGsakSaksnummer())).thenReturn(arkivsak);

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
        when(arkivsakService.hentsak(fagsakRinasakKobling.getGsakSaksnummer())).thenReturn(arkivsak);


        assertThat(saksrelasjonService.finnAktørIDTilhørendeRinasak("123")).contains(aktørID);
    }

    @Test
    void oppdaterKobling_koblingFinnes_oppdatererGsakSaksnummer() {
        final var gammelGsakSaksnummer = 123L;
        final var nyGsakSaksnummer = 456L;
        final var fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setRinaSaksnummer(RINA_ID);
        fagsakRinasakKobling.setGsakSaksnummer(gammelGsakSaksnummer);
        fagsakRinasakKobling.setBucType(BucType.LA_BUC_04);
        when(fagsakRinasakKoblingRepository.findByRinaSaksnummer(RINA_ID)).thenReturn(Optional.of(fagsakRinasakKobling));

        saksrelasjonService.oppdaterKobling(RINA_ID, nyGsakSaksnummer);

        assertThat(fagsakRinasakKobling.getGsakSaksnummer()).isEqualTo(nyGsakSaksnummer);
        verify(fagsakRinasakKoblingRepository).save(fagsakRinasakKobling);
    }

    @Test
    void oppdaterKobling_koblingFinnesIkke_kasterNotFoundException() {
        when(fagsakRinasakKoblingRepository.findByRinaSaksnummer(RINA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saksrelasjonService.oppdaterKobling(RINA_ID, 456L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(RINA_ID);
    }
}
