package no.nav.melosys.eessi.service.identifisering;

import java.util.Optional;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.PersonSokMetrikker;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonIdentifiseringServiceTest {

    @Mock
    private PersonSok personSok;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private SakService sakService;
    @Mock
    private TpsService tpsService;
    @Mock
    private PersonSokMetrikker personSokMetrikker;

    private PersonIdentifiseringService personIdentifiseringService;

    @Before
    public void setup() {
        personIdentifiseringService = new PersonIdentifiseringService(
                personSok, saksrelasjonService, sakService, tpsService, personSokMetrikker
        );
    }

    @Test
    public void identifiserPerson_sakEksisterer_hentPersonFraSak() throws Exception {
        final String norskIdent = "333333";
        Sak sak = new Sak();
        sak.setAktoerId("32132132");

        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setGsakSaksnummer(123L);

        when(tpsService.hentNorskIdent(anyString())).thenReturn(norskIdent);
        when(sakService.hentsak(anyLong())).thenReturn(sak);
        when(saksrelasjonService.finnVedRinaSaksnummer(anyString())).thenReturn(Optional.of(fagsakRinasakKobling));

        Optional<String> res = personIdentifiseringService.identifiserPerson(lagSedHendelse("123"), new SED());
        assertThat(res).isPresent();
        assertThat(res.get()).isEqualTo(norskIdent);
    }

    @Test
    public void identifiserPerson_ingenSakSedMedNorskIdent_personSuksessfultValidert() throws Exception {
        final String norskIdent = "333333";

        when(personSok.vurderPerson(anyString(), any(SED.class))).thenReturn(PersonSokResultat.identifisert(norskIdent));
        Optional<String> res = personIdentifiseringService.identifiserPerson(lagSedHendelse(norskIdent), new SED());
        assertThat(res).isPresent();
        assertThat(res.get()).isEqualTo(norskIdent);
    }

    @Test
    public void identifiserPerson_ingenSakSedIngenNorskIdent_finnerIkkePersonFraSedFinnerFraSøk() throws Exception {
        final String norskIdent = "33";
        when(personSok.søkPersonFraSed(any(SED.class))).thenReturn(PersonSokResultat.identifisert(norskIdent));
        Optional<String> res = personIdentifiseringService.identifiserPerson(lagSedHendelse(null), new SED());
        assertThat(res).isPresent();
        assertThat(res.get()).isEqualTo(norskIdent);
    }

    @Test
    public void identifiserPerson_ingenSakSedIngenNorskIdent_finnerIkkePersonFraSedFinnerIkkeFraSøk() throws Exception {
        when(personSok.søkPersonFraSed(any(SED.class))).thenReturn(PersonSokResultat.ikkeIdentifisert(SoekBegrunnelse.FLERE_TREFF));
        Optional<String> res = personIdentifiseringService.identifiserPerson(lagSedHendelse(null), new SED());
        assertThat(res).isNotPresent();
    }

    private SedHendelse lagSedHendelse(String ident) {
        return SedHendelse.builder()
                .rinaDokumentId("abcd1234")
                .rinaSakId("3232")
                .navBruker(ident)
                .sedType("A009")
                .build();
    }

}