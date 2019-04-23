package no.nav.melosys.eessi.service.behandling;

import java.util.Arrays;
import java.util.List;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapper;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperFactory;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Bruker;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.models.sed.nav.Person;
import no.nav.melosys.eessi.models.sed.nav.Statsborgerskap;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BehandleSedMottattServiceTest {

    @Mock
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    @Mock
    private EuxService euxService;

    @Mock
    private TpsService tpsService;

    @Mock
    private Personvurdering personvurdering;

    @Mock
    private MelosysEessiProducer melosysEessiProducer;

    @Mock
    private MelosysEessiMeldingMapperFactory meldingMapperFactory;

    @InjectMocks
    private BehandleSedMottattService behandleSedMottattService;

    @Before
    public void setup() throws Exception {
        when(tpsService.hentAktoerId(anyString()))
                .thenReturn("44332211");

        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(any(), anyString()))
                .thenReturn(SakInformasjon.builder().journalpostId("9988776655").build());

        when(euxService.hentSed(anyString(), anyString()))
                .thenReturn(opprettSED());

        when(personvurdering.hentNorskIdent(any(), any())).thenReturn("12312312312");
        when(meldingMapperFactory.getMapper(any())).thenReturn(Mockito.mock(MelosysEessiMeldingMapper.class));
    }

    @Test
    public void behandleSed_expectServiceCalls() throws Exception {
        SedHendelse sedHendelse = new SedHendelse();
        sedHendelse.setNavBruker("11223344");
        sedHendelse.setRinaSakId("123");
        sedHendelse.setRinaDokumentId("456");
        sedHendelse.setSedType("A005");

        behandleSedMottattService.behandleSed(sedHendelse);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personvurdering).hentNorskIdent(any(), any());
        verify(tpsService).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), anyString());
        verify(melosysEessiProducer).publiserMelding(any());
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
        sed.setSed("A005");

        return sed;
    }
}
