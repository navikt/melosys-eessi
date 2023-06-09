package no.nav.melosys.eessi.service.mottak;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SedMottakServiceTest {

    @Mock
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    @Mock
    private EuxService euxService;
    @Mock
    private PersonIdentifisering personIdentifisering;
    @Mock
    private OppgaveService oppgaveService;
    @Mock
    private SedMottattHendelseRepository sedMottattHendelseRepository;
    @Mock
    private BucIdentifiseringOppgRepository bucIdentifiseringOppgRepository;
    @Mock
    private BucIdentifisertService bucIdentifisertService;
    @Mock
    private JournalpostSedKoblingService journalpostSedKoblingService;
    @Mock
    private SedMetrikker sedMetrikker;

    private SedMottakService sedMottakService;

    private static final String IDENT = "1122334455";
    private static final String SED_ID = "555554444";
    private static final String RINA_SAKSNUMMER = "12313213";

    @BeforeEach
    public void setup() throws Exception {
        sedMottakService = new SedMottakService(
            euxService, personIdentifisering, opprettInngaaendeJournalpostService,
            oppgaveService, sedMottattHendelseRepository,
            bucIdentifiseringOppgRepository, bucIdentifisertService, journalpostSedKoblingService,
            sedMetrikker
        );
    }

    @Test
    void behandleSed_finnerIkkePerson_OppgaveOpprettes() {
        when(euxService.hentSedMedRetry(anyString(), anyString()))
            .thenReturn(opprettSED());
        when(sedMottattHendelseRepository.save(any(SedMottattHendelse.class))).then(returnsFirstArg());
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()))
            .thenReturn("9988776655");
        when(personIdentifisering.identifiserPerson(any(), any())).thenReturn(Optional.empty());
        when(euxService.hentSedMedRetry(anyString(), anyString())).thenReturn(opprettSED());

        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottattHendelse sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build();


        sedMottakService.behandleSed(sedMottattHendelse);


        verify(euxService).hentSedMedRetry(anyString(), anyString());
        verify(personIdentifisering).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(sedMottattHendelseRepository, times(2)).save(any());
        verify(bucIdentifisertService, never()).lagreIdentifisertPerson(anyString(), anyString());
    }

    @Test
    void behandleSed_finnerPerson_forventPersonIdentifisertEvent() {
        when(personIdentifisering.identifiserPerson(any(), any())).thenReturn(Optional.of(IDENT));
        when(euxService.hentSedMedRetry(anyString(), anyString()))
            .thenReturn(opprettSED());
        when(sedMottattHendelseRepository.save(any(SedMottattHendelse.class))).then(returnsFirstArg());
        SedHendelse sedHendelse = sedHendelseMedBruker();


        sedMottakService.behandleSed(SedMottattHendelse.builder().sedHendelse(sedHendelse).build());


        verify(euxService).hentSedMedRetry(anyString(), anyString());
        verify(personIdentifisering).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(sedMottattHendelseRepository).save(any());
        verify(bucIdentifisertService).lagreIdentifisertPerson(sedHendelse.getRinaSakId(), IDENT);
    }

    @Test
    void behandleSed_ikkeIdentifisertÅpenOppgaveFinnes_oppretterIkkeNyOppgaveEllerJournalpost() {
        final var oppgaveID = "5555";
        var bucIdentifiseringOppg = new BucIdentifiseringOppg(1L, RINA_SAKSNUMMER, oppgaveID, 1);
        when(bucIdentifiseringOppgRepository.findByRinaSaksnummer(RINA_SAKSNUMMER)).thenReturn(Set.of(bucIdentifiseringOppg));
        when(euxService.hentSedMedRetry(anyString(), anyString()))
            .thenReturn(opprettSED());
        when(sedMottattHendelseRepository.save(any(SedMottattHendelse.class))).then(returnsFirstArg());

        final var oppgave = new HentOppgaveDto();
        oppgave.setStatus("OPPRETTET");
        when(oppgaveService.hentOppgave(oppgaveID)).thenReturn(oppgave);
        SedHendelse sedHendelse = sedHendelseMedBruker();


        sedMottakService.behandleSed(SedMottattHendelse.builder().sedHendelse(sedHendelse).build());


        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(bucIdentifisertService, never()).lagreIdentifisertPerson(anyString(), anyString());
    }

    @Test
    void behandleSed_sedAlleredeBehandlet_behandlerIkkeVidere() {
        SedMottattHendelse sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelseMedBruker()).build();
        when(sedMottattHendelseRepository.findBySedID(SED_ID)).thenReturn(Optional.of(sedMottattHendelse));


        sedMottakService.behandleSed(sedMottattHendelse);


        verify(euxService, never()).hentSedMedRetry(anyString(), anyString());
        verify(personIdentifisering, never()).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(any(), any(), any());
    }

    @Test
    void behandleSed_xSedUtenTilhørendeASed_kasterException() {
        SedHendelse sedHendelse = sedHendelseMedBruker();
        sedHendelse.setSedType("X008");
        SedMottattHendelse sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build();
        when(journalpostSedKoblingService.erASedAlleredeBehandlet(anyString())).thenReturn(false);


        assertThatThrownBy(() -> sedMottakService.behandleSed(sedMottattHendelse))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Mottatt SED 555554444 av type X008 har ikke tilhørende A sed behandlet");
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
        sedHendelse.setSedId(SED_ID);
        sedHendelse.setRinaSakId(RINA_SAKSNUMMER);
        return sedHendelse;
    }

    private SedHendelse sedHendelseUtenBruker() {
        SedHendelse sedHendelse = new SedHendelse();
        sedHendelse.setNavBruker("ukjent");
        sedHendelse.setRinaSakId(RINA_SAKSNUMMER);
        sedHendelse.setRinaDokumentId("456");
        sedHendelse.setSedId(SED_ID);
        sedHendelse.setSedType("A009");
        return sedHendelse;
    }
}
