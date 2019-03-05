package no.nav.melosys.eessi.service.behandling;

import java.util.Arrays;
import java.util.List;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import lombok.val;
import no.nav.eessi.basis.SedMottatt;
import no.nav.melosys.eessi.integration.eux.EuxConsumer;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Bruker;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.tps.TpsService;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.feil.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Foedselsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Landkoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BehandleSedMottattServiceTest {

    @Mock
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    @Mock
    private EuxConsumer euxConsumer;

    @Mock
    private TpsService tpsService;

    @InjectMocks
    private BehandleSedMottattService behandleSedMottattService;

    @Before
    public void setup() throws Exception {
        when(tpsService.hentAktoerId(anyString()))
                .thenReturn("44332211");

        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSed(any(), anyString()))
                .thenReturn("9988776655");

        when(euxConsumer.hentSed(anyString(), anyString()))
                .thenReturn(opprettSED());

        when(tpsService.hentPerson(anyString()))
                .thenReturn(opprettPerson());
    }

    @Test
    public void behandleSed_expectServiceCalls() throws Exception {
        SedMottatt sedMottatt = new SedMottatt();
        sedMottatt.setNavBruker("11223344");
        sedMottatt.setRinaSakId("123");
        sedMottatt.setRinaDokumentId("456");

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxConsumer, times(1)).hentSed(anyString(), anyString());
        verify(tpsService, times(1)).hentPerson(anyString());
        verify(tpsService, times(1)).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService, times(1)).arkiverInngaaendeSed(any(), anyString());
    }

    @Test
    public void behandleSed_expectIngenNorskIdent() throws Exception {
        SedMottatt sedMottatt = new SedMottatt();
        sedMottatt.setRinaSakId("123");
        sedMottatt.setRinaDokumentId("456");

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxConsumer, times(1)).hentSed(anyString(), anyString());
        verify(tpsService, times(0)).hentPerson(anyString());
        verify(tpsService, times(0)).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService, times(0)).arkiverInngaaendeSed(any(), anyString());
    }

    @Test
    public void behandleSed_expectIkkeValiderbarPerson() throws Exception {
        SedMottatt sedMottatt = new SedMottatt();
        sedMottatt.setNavBruker("11223344");
        sedMottatt.setRinaSakId("123");
        sedMottatt.setRinaDokumentId("456");

        val person = opprettPerson();
        person.setStatsborgerskap(new no.nav.tjeneste.virksomhet.person.v3.informasjon.Statsborgerskap()
                .withLand(new Landkoder()
                        .withValue("DNK")));

        when(tpsService.hentPerson(anyString())).thenReturn(person);

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxConsumer, times(1)).hentSed(anyString(), anyString());
        verify(tpsService, times(1)).hentPerson(anyString());
        verify(tpsService, times(0)).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService, times(0)).arkiverInngaaendeSed(any(), anyString());
    }

    @Test
    public void behandleSed_expectIngenPersonFunnet() throws Exception {
        SedMottatt sedMottatt = new SedMottatt();
        sedMottatt.setNavBruker("11223344");
        sedMottatt.setRinaSakId("123");
        sedMottatt.setRinaDokumentId("456");

        when(tpsService.hentPerson(anyString()))
                .thenThrow(new HentPersonPersonIkkeFunnet("Person ikke funnet" , new PersonIkkeFunnet()));

        behandleSedMottattService.behandleSed(sedMottatt);

        verify(euxConsumer, times(1)).hentSed(anyString(), anyString());
        verify(tpsService, times(1)).hentPerson(anyString());
        verify(tpsService, times(0)).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService, times(0)).arkiverInngaaendeSed(any(), anyString());
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

        return sed;
    }

    private no.nav.tjeneste.virksomhet.person.v3.informasjon.Person opprettPerson() {
        val person = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Person();

        person.setFoedselsdato(new Foedselsdato()
                .withFoedselsdato(XMLGregorianCalendarImpl
                        .createDateTime(1990, 1, 1, 17, 50, 0)));

        person.setStatsborgerskap(new no.nav.tjeneste.virksomhet.person.v3.informasjon.Statsborgerskap()
                .withLand(new Landkoder()
                        .withValue("NOR")));

        return person;
    }
}