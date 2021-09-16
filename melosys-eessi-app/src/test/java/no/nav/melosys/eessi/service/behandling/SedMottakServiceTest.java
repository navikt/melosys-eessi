package no.nav.melosys.eessi.service.behandling;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import no.nav.melosys.eessi.identifisering.BucIdentifisertService;
import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.integration.oppgave.HentOppgaveDto;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucIdentifiseringOppg;
import no.nav.melosys.eessi.models.SedMottattHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.repository.BucIdentifiseringOppgRepository;
import no.nav.melosys.eessi.repository.SedMottattHendelseRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.mottak.SedMottakService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private SedMottakService sedMottakService;

    private static final String IDENT = "1122334455";
    private static final String RINA_SAKSNUMMER = "12313213";

    @BeforeEach
    public void setup() throws Exception {
        sedMottakService = new SedMottakService(
                euxService, personIdentifisering, opprettInngaaendeJournalpostService,
                oppgaveService, sedMottattHendelseRepository,
                bucIdentifiseringOppgRepository, bucIdentifisertService
        );

        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()))
                .thenReturn("9988776655");

        when(euxService.hentSed(anyString(), anyString()))
                .thenReturn(opprettSED());

    }

    @Test
    void behandleSed_finnerIkkePerson_journalpostOgOppgaveOpprettes() {
        when(personIdentifisering.identifiserPerson(any(), any())).thenReturn(Optional.empty());

        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottattHendelse sedMottattHendelse = SedMottattHendelse.builder().sedHendelse(sedHendelse).build();

        sedMottakService.behandleSed(sedMottattHendelse);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(sedMottattHendelseRepository, times(2)).save(any());
        verify(bucIdentifisertService, never()).lagreIdentifisertPerson(anyString(), anyString());
    }

    @Test
    void behandleSed_finnerPerson_forventPersonIdentifisertEvent() {
        when(personIdentifisering.identifiserPerson(any(), any())).thenReturn(Optional.of(IDENT));
        SedHendelse sedHendelse = sedHendelseMedBruker();

        sedMottakService.behandleSed(SedMottattHendelse.builder().sedHendelse(sedHendelse).build());

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(sedMottattHendelseRepository, times(2)).save(any());
        verify(bucIdentifisertService).lagreIdentifisertPerson(sedHendelse.getRinaSakId(), IDENT);
    }

    @Test
    void behandleSed_ikkeIdentifisertÅpenOppgaveFinnes_oppretterIkkeNyOppgave() {
        final var oppgaveID = "5555";
        var bucIdentifiseringOppg = new BucIdentifiseringOppg(1L, RINA_SAKSNUMMER, oppgaveID);
        when(bucIdentifiseringOppgRepository.findByRinaSaksnummer(RINA_SAKSNUMMER)).thenReturn(Set.of(bucIdentifiseringOppg));

        final var oppgave = new HentOppgaveDto();
        oppgave.setStatus("OPPRETTET");
        when(oppgaveService.hentOppgave(oppgaveID)).thenReturn(oppgave);
        SedHendelse sedHendelse = sedHendelseMedBruker();

        sedMottakService.behandleSed(SedMottattHendelse.builder().sedHendelse(sedHendelse).build());

        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(bucIdentifisertService, never()).lagreIdentifisertPerson(anyString(), anyString());
    }

    @Test
    void behandleSed_sedAlleredeJournalført_behandlerIkkeVidere() {
        SedHendelse sedHendelse = sedHendelseMedBruker();
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()))
                .thenThrow(new SedAlleredeJournalførtException("Allerede journalført", "123"));

        sedMottakService.behandleSed(SedMottattHendelse.builder().sedHendelse(sedHendelse).build());

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering, never()).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(any(), any(), any());
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
        sedHendelse.setRinaSakId(RINA_SAKSNUMMER);
        return sedHendelse;
    }

    private SedHendelse sedHendelseUtenBruker() {
        SedHendelse sedHendelse = new SedHendelse();
        sedHendelse.setNavBruker("ukjent");
        sedHendelse.setRinaSakId(RINA_SAKSNUMMER);
        sedHendelse.setRinaDokumentId("456");
        sedHendelse.setSedType("A009");
        return sedHendelse;
    }
}
