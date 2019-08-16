package no.nav.melosys.eessi.service.behandling;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.kafka.producers.MelosysEessiProducer;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.*;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @InjectMocks
    private BehandleSedMottattService behandleSedMottattService;

    @Before
    public void setup() throws Exception {
        when(tpsService.hentAktoerId(anyString()))
                .thenReturn("44332211");

        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(any(), anyString(), any()))
                .thenReturn(SakInformasjon.builder().gsakSaksnummer("123").journalpostId("9988776655").build());

        when(euxService.hentSed(anyString(), anyString()))
                .thenReturn(opprettSED());

        when(personvurdering.hentNorskIdent(any(), any())).thenReturn(Optional.of("12312312312"));
    }

    @Test
    public void behandleSed_expectServiceCalls() throws Exception {
        SedHendelse sedHendelse = new SedHendelse();
        sedHendelse.setNavBruker("11223344");
        sedHendelse.setRinaSakId("123");
        sedHendelse.setRinaDokumentId("456");
        sedHendelse.setSedType("A009");

        behandleSedMottattService.behandleSed(sedHendelse);

        verify(euxService).hentSed(anyString(), anyString());
        verify(personvurdering).hentNorskIdent(any(), any());
        verify(tpsService).hentAktoerId(anyString());
        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), anyString(), any());
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
        sed.setSed("A009");

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
}
