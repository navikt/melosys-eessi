package no.nav.melosys.eessi.service.tps;

import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.person.PersonConsumer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bostedsadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Gateadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TpsServiceTest {

    @Mock
    private PersonConsumer personConsumer;

    @Mock
    private AktoerConsumer aktoerConsumer;

    @InjectMocks
    private TpsService tpsService;

    @Before
    public void setup() throws Exception {
        Person person = new Person()
                .withBostedsadresse(new Bostedsadresse()
                        .withStrukturertAdresse(new Gateadresse()
                                .withGatenavn("Gateveien")
                                .withHusnummer(1)
                                .withKommunenummer("0301")))
                .withAktoer(new AktoerId()
                        .withAktoerId("11223344556"));

        HentPersonResponse response = new HentPersonResponse();
        response.setPerson(person);

        when(personConsumer.hentPerson(any())).thenReturn(response);

        when(aktoerConsumer.getAktoerId(anyString())).thenReturn("998877665544");
    }

    @Test
    public void hentPerson_expectPerson() throws Exception {
        Person person = tpsService.hentPerson("11223344556");
        assertThat(person, not(nullValue()));
    }

    @Test
    public void hentPersonMedAdresse_expectPersonMedAdresse() throws Exception {
        Person person = tpsService.hentPersonMedAdresse("11223344556");
        assertThat(person, not(nullValue()));

        Gateadresse gateadresse = (Gateadresse) person.getBostedsadresse().getStrukturertAdresse();
        assertThat(gateadresse.getGatenavn(), is("Gateveien"));
        assertThat(gateadresse.getHusnummer(), is(1));
        assertThat(gateadresse.getKommunenummer(), is("0301"));
    }

    @Test
    public void hentAktoerId_expectAktoerId() throws Exception {
        String aktoerId = tpsService.hentAktoerId("11223344556");
        assertThat(aktoerId, not(nullValue()));
        assertThat(aktoerId, is("998877665544"));
    }
}

