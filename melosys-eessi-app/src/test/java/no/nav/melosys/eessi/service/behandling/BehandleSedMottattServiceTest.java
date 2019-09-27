package no.nav.melosys.eessi.service.behandling;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BehandleSedMottattServiceTest {
//
//    @Mock
//    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
//
//    @Mock
//    private EuxService euxService;
//
//    @Mock
//    private TpsService tpsService;
//
//    @Mock
//    private PersonIdentifiseringService personIdentifiseringService;
//
//    @Mock
//    private MelosysEessiProducer melosysEessiProducer;
//
//    @Mock
//    private OppgaveService oppgaveService;
//
//    private BehandleSedMottattService behandleSedMottattService;
//
//    private static final String IDENT = "1122334455";
//
//    @Before
//    public void setup() throws Exception {
//        behandleSedMottattService = new BehandleSedMottattService(
//                opprettInngaaendeJournalpostService, euxService, tpsService,
//                melosysEessiProducer, personIdentifiseringService, oppgaveService
//        );
//
//        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(any(), any()))
//                .thenReturn(SakInformasjon.builder().gsakSaksnummer("123").journalpostId("9988776655").build());
//
//        when(euxService.hentSed(anyString(), anyString()))
//                .thenReturn(opprettSED());
//
//    }
//
//    @Test
//    public void behandleSed_FinnerPerson_forventPubliserKafkaMelding() throws Exception {
//        final String aktoerID = "12312312312";
//        when(personIdentifiseringService.identifiserPerson(any(), any())).thenReturn(Optional.of(IDENT));
//        when(tpsService.hentAktoerId(eq(IDENT))).thenReturn(aktoerID);
//
//        SedHendelse sedHendelse = new SedHendelse();
//        sedHendelse.setNavBruker(IDENT);
//        sedHendelse.setRinaSakId("123");
//        sedHendelse.setRinaDokumentId("456");
//        sedHendelse.setSedType("A009");
//
//        behandleSedMottattService.behandleSed(sedHendelse);
//
//        verify(euxService).hentSed(anyString(), anyString());
//        verify(personIdentifiseringService).identifiserPerson(any(), any());
//        verify(tpsService).hentAktoerId(anyString());
//        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedHentSakinformasjon(any(), any());
//        verify(melosysEessiProducer).publiserMelding(any());
//    }
//
//    @Test
//    public void behandleSed_finnerIkkePerson_forventJournalpostOgOppgaveOpprettes() throws Exception {
//        when(personIdentifiseringService.identifiserPerson(any(), any())).thenReturn(Optional.empty());
//
//        SedHendelse sedHendelse = new SedHendelse();
//        sedHendelse.setNavBruker("ukjent");
//        sedHendelse.setRinaSakId("123");
//        sedHendelse.setRinaDokumentId("456");
//        sedHendelse.setSedType("A009");
//
//        behandleSedMottattService.behandleSed(sedHendelse);
//
//        verify(euxService).hentSed(anyString(), anyString());
//        verify(personIdentifiseringService).identifiserPerson(any(), any());
//        verify(opprettInngaaendeJournalpostService).arkiverInngaaendeSedUtenBruker(any(), any());
//
//        verify(melosysEessiProducer, never()).publiserMelding(any());
//    }
//
//    private SED opprettSED() {
//        Statsborgerskap norge = new Statsborgerskap();
//        norge.setLand("NO");
//
//        Statsborgerskap sverige = new Statsborgerskap();
//        sverige.setLand("SE");
//
//        List<Statsborgerskap> statsborgerskap = Arrays.asList(norge, sverige);
//
//        Person person = new Person();
//        person.setStatsborgerskap(statsborgerskap);
//        person.setFoedselsdato("1990-01-01");
//
//        Bruker bruker = new Bruker();
//        bruker.setPerson(person);
//
//        Nav nav = new Nav();
//        nav.setBruker(bruker);
//
//        SED sed = new SED();
//        sed.setNav(nav);
//        sed.setSed("A009");
//
//        MedlemskapA009 medlemskap = new MedlemskapA009();
//        medlemskap.setVedtak(new VedtakA009());
//        medlemskap.getVedtak().setGjelderperiode(new Periode());
//        Fastperiode fastperiode = new Fastperiode();
//        fastperiode.setStartdato("2019-05-01");
//        fastperiode.setSluttdato("2019-12-01");
//        medlemskap.getVedtak().getGjelderperiode().setFastperiode(fastperiode);
//        sed.setMedlemskap(medlemskap);
//
//        return sed;
//    }
}
