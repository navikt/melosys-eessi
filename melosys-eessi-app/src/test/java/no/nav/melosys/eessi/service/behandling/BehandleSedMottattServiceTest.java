package no.nav.melosys.eessi.service.behandling;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.identifisering.PersonIdentifiseringService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BehandleSedMottattServiceTest {

    @Mock
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    @Mock
    private EuxService euxService;

    @Mock
    private PersonFasade personFasade;

    @Mock
    private PersonIdentifiseringService personIdentifiseringService;

    @Mock
    private MelosysEessiProducer melosysEessiProducer;

    @Mock
    private OppgaveService oppgaveService;

    private BehandleSedMottattService behandleSedMottattService;

    private static final String IDENT = "1122334455";

    @Before
    public void setup() throws Exception {
        behandleSedMottattService = new BehandleSedMottattService(
                opprettInngaaendeJournalpostService, euxService, personFasade,
                melosysEessiProducer, personIdentifiseringService, oppgaveService
        );

        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(any(), any(), any()))
                .thenReturn(SakInformasjon.builder().gsakSaksnummer("123").journalpostId("9988776655").build());
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()))
                .thenReturn("9988776655");

        when(euxService.hentSed(anyString(), anyString()))
                .thenReturn(opprettSED());

    }

    @Test
    public void behandleSed_finnerIkkePerson_forventJournalpostOgOppgaveOpprettes() {
        when(personIdentifiseringService.identifiserPerson(any(), any())).thenReturn(Optional.empty());

        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(melosysEessiProducer, never()).publiserMelding(any());
    }

    @Test
    public void behandleSed_personIkkeIdentifisert_forventJournalpostOgOppgaveOpprettes() {
        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService, never()).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(melosysEessiProducer, never()).publiserMelding(any());
    }

    @Test
    public void behandleSed_personIkkeIdentifisertJournalpostOpprettet_forventOppgaveOpprettes() {
        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setJournalpostID("123");

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService, never()).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(melosysEessiProducer, never()).publiserMelding(any());
    }

    @Test
    public void behandleSed_finnerPerson_forventPubliserKafkaMelding() {
        final String aktoerID = "12312312312";
        when(personIdentifiseringService.identifiserPerson(any(), any())).thenReturn(Optional.of(IDENT));
        when(personFasade.hentAktoerId(eq(IDENT))).thenReturn(aktoerID);
        SedHendelse sedHendelse = sedHendelseMedBruker();

        behandleSedMottattService.behandleSed(SedMottatt.av(sedHendelse));

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService).identifiserPerson(any(), any());
        verify(personFasade).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(melosysEessiProducer).publiserMelding(any());
    }

    @Test
    public void behandleSed_personAlleredeFunnet_forventIkkeIdentifiserOgPubliserKafkaMelding() {
        final String aktoerID = "12312312312";
        when(personFasade.hentAktoerId(eq(IDENT))).thenReturn(aktoerID);

        SedHendelse sedHendelse = sedHendelseMedBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setNavIdent(IDENT);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService, never()).identifiserPerson(any(), any());
        verify(personFasade).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(melosysEessiProducer).publiserMelding(any());
    }

    @Test
    public void behandleSed_personFunnetJournalpostOpprettet_forventKunPubliserKafka() {
        final String aktoerID = "12312312312";
        when(personFasade.hentAktoerId(eq(IDENT))).thenReturn(aktoerID);

        SedHendelse sedHendelse = sedHendelseMedBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setNavIdent(IDENT);
        sedMottatt.getSedKontekst().setJournalpostID("123");

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService, never()).identifiserPerson(any(), any());
        verify(personFasade).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(melosysEessiProducer).publiserMelding(any());
    }

    @Test
    public void behandleSed_sedAlleredeJournalført_behandlerIkkeVidere() {
        final String aktoerID = "12312312312";
        when(personFasade.hentAktoerId(eq(IDENT))).thenReturn(aktoerID);
        when(personIdentifiseringService.identifiserPerson(any(), any())).thenReturn(Optional.of(IDENT));

        SedHendelse sedHendelse = sedHendelseMedBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(any(), any(), any()))
                .thenThrow(new SedAlleredeJournalførtException("Allerede journalført", "123"));

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(any(), any(), any());
        verify(melosysEessiProducer, never()).publiserMelding(any());

        assertThat(sedMottatt.isFerdig()).isTrue();
    }

    @Test
    public void behandleSed_alleredeUtført_forventIngenVidereKall() {
        SedHendelse sedHendelse = sedHendelseMedBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setNavIdent(IDENT);
        sedMottatt.getSedKontekst().setJournalpostID("123");
        sedMottatt.getSedKontekst().setPublisertKafka(true);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifiseringService, never()).identifiserPerson(any(), any());
        verify(personFasade, never()).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(melosysEessiProducer, never()).publiserMelding(any());
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
