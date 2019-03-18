package no.nav.melosys.eessi.integration.tps;

import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonConsumerTest {

    @Mock
    private PersonV3 port;

    private PersonConsumer personConsumer;

    @Before
    public void setup() throws Exception {
        personConsumer = new PersonConsumer(port);

        HentPersonResponse response = new HentPersonResponse();
        when(port.hentPerson(any())).thenReturn(response);
    }

    @Test
    public void hentPerson_expectHentPersonResponse() throws Exception {
        HentPersonRequest request = new HentPersonRequest()
                .withAktoer(new AktoerId()
                        .withAktoerId("12345"));

        HentPersonResponse response = personConsumer.hentPerson(request);

        assertThat(response, not(nullValue()));
    }
}