package no.nav.melosys.eessi.identifisering;

import java.util.Optional;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.models.BucIdentifisert;
import no.nav.melosys.eessi.repository.BucIdentifisertRepository;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucIdentifisertServiceTest {

    @Mock
    private BucIdentifisertRepository bucIdentifisertRepository;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private PersonFasade personFasade;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private BucIdentifisertService bucIdentifisertService;

    private final String rinaSaksnummer = "1111";
    private final String aktørID = "123123123";
    private final String norskIdent = "321321321";
    private final Sak arkivsak = new Sak();

    @BeforeEach
    void setup() {
        arkivsak.setAktoerId(aktørID);
        bucIdentifisertService = new BucIdentifisertService(
            bucIdentifisertRepository, saksrelasjonService, personFasade, applicationEventPublisher
        );
    }

    @Test
    void finnIdentifisertPerson_personIkkeIdentifisert_tomReturverdi() {
        assertThat(bucIdentifisertService.finnIdentifisertPerson(rinaSaksnummer)).isEmpty();
    }

    @Test
    void finnIdentifisertPerson_saksrelasjonFinnes_ident() {
        when(saksrelasjonService.finnAktørIDTilhørendeRinasak(rinaSaksnummer)).thenReturn(Optional.of(aktørID));
        when(personFasade.hentNorskIdent(aktørID)).thenReturn(norskIdent);

        assertThat(bucIdentifisertService.finnIdentifisertPerson(rinaSaksnummer)).contains(norskIdent);
        verify(bucIdentifisertRepository, never()).findByRinaSaksnummer(anyString());
    }

    @Test
    void finnIdentifisertPerson_bucIdentifisert_ident() {
        when(bucIdentifisertRepository.findByRinaSaksnummer(rinaSaksnummer))
            .thenReturn(Optional.of(new BucIdentifisert(1L, rinaSaksnummer, norskIdent)));

        assertThat(bucIdentifisertService.finnIdentifisertPerson(rinaSaksnummer)).contains(norskIdent);
    }
}
