package no.nav.melosys.eessi.service.behandling;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.behandling.event.BucIdentifisertEvent;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.identifisering.PersonIdentifiseringService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SedMottattBehandleServiceTest {

    @Mock
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    @Mock
    private EuxService euxService;

    @Mock
    private PersonIdentifiseringService personIdentifiseringService;

    @Mock
    private OppgaveService oppgaveService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private SedMottattHendelseRepository sedMottattHendelseRepository;

    @Mock
    private BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;

    @Mock
    private PersonFasade personFasade;

    private SedMottattBehandleService sedMottattBehandleService;

    private static final String IDENT = "1122334455";

    @BeforeEach
    public void setup() throws Exception {
        sedMottattBehandleService = new SedMottattBehandleService(
                euxService, personIdentifiseringService, opprettInngaaendeJournalpostService,
                oppgaveService, applicationEventPublisher, sedMottattHendelseRepository,
                bucIdentifiseringOppgRepository, personFasade
        );

        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()))
                .thenReturn("9988776655");

        when(euxService.hentSed(anyString(), anyString()))
                .thenReturn(opprettSED());

    }

    @Test
    void behandleSed_finnerIkkePerson_journalpostOgOppgaveOpprettes() {
        when(personIdentifiseringService.identifiserPerson(any(), any())).thenReturn(Optional.empty());

        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottattHendelse sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build();

        sedMottattBehandleService.behandleSed(sedMottattHendelse);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(sedMottattHendelseRepository).save(any());
        verify(applicationEventPublisher, never()).publishEvent(BucIdentifisertEvent.class);
    }

    @Test
    void behandleSed_finnerIkkePersonOppgaveLagetTidligere_journalpostOpprettesMenIkkeOppgave() {
        when(personIdentifiseringService.identifiserPerson(any(), any())).thenReturn(Optional.empty());
        when(bucIdentifiseringOppgRepository.findByRinaSaksnummer(any())).thenReturn(Optional.of(new BucIdentifiseringOppg()));

        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottattHendelse sedMottatt = SedMottattHendelse.builder().sedHendelse(sedHendelse).build();

        sedMottattBehandleService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(sedMottattHendelseRepository).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void behandleSed_finnerPerson_forventPersonIdentifisertEvent() {
        when(personIdentifiseringService.identifiserPerson(any(), any())).thenReturn(Optional.of(IDENT));
        SedHendelse sedHendelse = sedHendelseMedBruker();

        sedMottattBehandleService.behandleSed(SedMottattHendelse.builder().sedHendelse(sedHendelse).build());

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(sedMottattHendelseRepository).save(any());
        verify(applicationEventPublisher).publishEvent(any(BucIdentifisertEvent.class));
    }

    private SED opprettSED() {
        Statsborgerskap norge = new Statsborgerskap();
        norge.setLand("NO");

        Statsborgerskap sverige = new Statsborgerskap();
        sverige.setLand("SE");

        List<Statsborgerskap> statsborgerskap = Arrays.asList(norge, sverige);

        Person person = new Person();
        person.setStatsborgerskap(statsborgerskap);
        person.setFoedselsdato("1990-01-01");

        Bruker bruker = new Bruker();
        bruker.setPerson(person);

        Nav nav = new Nav();
        nav.setBruker(bruker);

        SED sed = new SED();
        sed.setNav(nav);
        sed.setSedType("A009");

        MedlemskapA009 medlemskap = new MedlemskapA009();
        medlemskap.setVedtak(new VedtakA009());
        medlemskap.getVedtak().setGjelderperiode(new Periode());
        Fastperiode fastperiode = new Fastperiode();
        fastperiode.setStartdato("2019-05-01");
        fastperiode.setSluttdato("2019-12-01");
        medlemskap.getVedtak().getGjelderperiode().setFastperiode(fastperiode);
        sed.setMedlemskap(medlemskap);

        return sed;
    }

    private SedHendelse sedHendelseMedBruker() {
        SedHendelse sedHendelse = sedHendelseUtenBruker();
        sedHendelse.setAvsenderId("SE:12345");
        sedHendelse.setNavBruker(IDENT);
        return sedHendelse;
    }

    private SedHendelse sedHendelseUtenBruker() {
        SedHendelse sedHendelse = new SedHendelse();
        sedHendelse.setNavBruker("ukjent");
        sedHendelse.setRinaSakId("123");
        sedHendelse.setRinaDokumentId("456");
        sedHendelse.setSedType("A009");
        return sedHendelse;
    }
}
