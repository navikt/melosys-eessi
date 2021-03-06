package no.nav.melosys.eessi.service.behandling;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import no.nav.melosys.eessi.identifisering.PersonIdentifisering;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.models.SedMottatt;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BehandleSedMottattServiceTest {

    @Mock
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    @Mock
    private EuxService euxService;
    @Mock
    private PersonFasade personFasade;
    @Mock
    private PersonIdentifisering personIdentifisering;
    @Mock
    private MelosysEessiProducer melosysEessiProducer;
    @Mock
    private OppgaveService oppgaveService;

    private final MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory = new MelosysEessiMeldingMapperFactory("dummy");

    private BehandleSedMottattService behandleSedMottattService;

    private static final String IDENT = "1122334455";

    @BeforeEach
    public void setup() throws Exception {
        behandleSedMottattService = new BehandleSedMottattService(
                opprettInngaaendeJournalpostService, euxService, personFasade,
                melosysEessiProducer, personIdentifisering, oppgaveService,
                melosysEessiMeldingMapperFactory);


        when(euxService.hentSed(anyString(), anyString()))
                .thenReturn(opprettSED());

    }

    @Test
    void behandleSed_finnerIkkePerson_forventJournalpostOgOppgaveOpprettes() {
        when(personIdentifisering.identifiserPerson(any(), any())).thenReturn(Optional.empty());

        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(melosysEessiProducer, never()).publiserMelding(any());
    }

    @Test
    void behandleSed_personIkkeIdentifisert_forventJournalpostOgOppgaveOpprettes() {
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(any(), any(), any()))
                .thenReturn("9988776655");

        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering, never()).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(melosysEessiProducer, never()).publiserMelding(any());
    }

    @Test
    void behandleSed_personIkkeIdentifisertJournalpostOpprettet_forventOppgaveOpprettes() {
        SedHendelse sedHendelse = sedHendelseUtenBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setJournalpostID("123");

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering, never()).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService).opprettOppgaveTilIdOgFordeling(anyString(), anyString(), anyString());
        verify(melosysEessiProducer, never()).publiserMelding(any());
    }

    @Test
    void behandleSed_finnerPerson_forventPubliserKafkaMelding() {
        final String aktoerID = "12312312312";
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(any(), any(), any()))
                .thenReturn(SakInformasjon.builder().gsakSaksnummer("123").journalpostId("9988776655").build());
        when(personIdentifisering.identifiserPerson(any(), any())).thenReturn(Optional.of(IDENT));
        when(personFasade.hentAktoerId(IDENT)).thenReturn(aktoerID);
        SedHendelse sedHendelse = sedHendelseMedBruker();

        behandleSedMottattService.behandleSed(SedMottatt.av(sedHendelse));

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering).identifiserPerson(any(), any());
        verify(personFasade).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(melosysEessiProducer).publiserMelding(any());
    }

    @Test
    void behandleSed_personAlleredeFunnet_forventIkkeIdentifiserOgPubliserKafkaMelding() {
        final String aktoerID = "12312312312";
        when(personFasade.hentAktoerId(IDENT)).thenReturn(aktoerID);
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(any(), any(), any()))
                .thenReturn(SakInformasjon.builder().gsakSaksnummer("123").journalpostId("9988776655").build());

        SedHendelse sedHendelse = sedHendelseMedBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setNavIdent(IDENT);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering, never()).identifiserPerson(any(), any());
        verify(personFasade).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(melosysEessiProducer).publiserMelding(any());
    }

    @Test
    void behandleSed_personFunnetJournalpostOpprettet_forventKunPubliserKafka() {
        final String aktoerID = "12312312312";
        when(personFasade.hentAktoerId(IDENT)).thenReturn(aktoerID);

        SedHendelse sedHendelse = sedHendelseMedBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setNavIdent(IDENT);
        sedMottatt.getSedKontekst().setJournalpostID("123");

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering, never()).identifiserPerson(any(), any());
        verify(personFasade).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(melosysEessiProducer).publiserMelding(any());
    }

    @Test
    void behandleSed_sedAlleredeJournalført_behandlerIkkeVidere() {
        when(personIdentifisering.identifiserPerson(any(), any())).thenReturn(Optional.of(IDENT));

        SedHendelse sedHendelse = sedHendelseMedBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(any(), any(), any()))
                .thenThrow(new SedAlleredeJournalførtException("Allerede journalført", "123"));

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering).identifiserPerson(any(), any());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), any(), any());
        verify(opprettInngaaendeJournalpostService, never()).arkiverInngaaendeSedUtenBruker(any(), any(), any());
        verify(oppgaveService, never()).opprettOppgaveTilIdOgFordeling(any(), any(), any());
        verify(melosysEessiProducer, never()).publiserMelding(any());

        assertThat(sedMottatt.isFerdig()).isTrue();
    }

    @Test
    void behandleSed_alleredeUtført_forventIngenVidereKall() {
        SedHendelse sedHendelse = sedHendelseMedBruker();
        SedMottatt sedMottatt = SedMottatt.av(sedHendelse);
        sedMottatt.getSedKontekst().setForsoktIdentifisert(true);
        sedMottatt.getSedKontekst().setNavIdent(IDENT);
        sedMottatt.getSedKontekst().setJournalpostID("123");
        sedMottatt.getSedKontekst().setPublisertKafka(true);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personIdentifisering, never()).identifiserPerson(any(), any());
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
