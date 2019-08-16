package no.nav.melosys.eessi.service.tps;

import java.time.LocalDate;
import java.util.List;
import lombok.val;
import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.person.PersonConsumer;
import no.nav.melosys.eessi.integration.tps.personsok.PersonsokConsumer;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsoekKriterier;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bostedsadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Gateadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TpsServiceTest {

    @Mock
    private PersonConsumer personConsumer;

    @Mock
    private AktoerConsumer aktoerConsumer;

    @Mock
    private PersonsokConsumer personsokConsumer;

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

        when(personsokConsumer.finnPerson(any())).thenReturn(lagFinnPersonResponseMedEnPerson());
    }

    @Test
    public void hentPerson_expectPerson() throws Exception {
        Person person = tpsService.hentPerson("11223344556");
        assertThat(person).isNotNull();
    }

    @Test
    public void hentPersonMedAdresse_expectPersonMedAdresse() throws Exception {
        Person person = tpsService.hentPersonMedAdresse("11223344556");
        assertThat(person).isNotNull();

        Gateadresse gateadresse = (Gateadresse) person.getBostedsadresse().getStrukturertAdresse();
        assertThat(gateadresse.getGatenavn()).isEqualTo("Gateveien");
        assertThat(gateadresse.getHusnummer()).isEqualTo(1);
        assertThat(gateadresse.getKommunenummer()).isEqualTo("0301");
    }

    @Test
    public void hentAktoerId_expectAktoerId() throws Exception {
        String aktoerId = tpsService.hentAktoerId("11223344556");
        assertThat(aktoerId).isNotNull();
        assertThat(aktoerId).isEqualTo("998877665544");
    }

    @Test
    public void soekEtterPerson_forventIdent() throws Exception {
        List<PersonSoekResponse> response = tpsService.soekEtterPerson(lagPersonsoekKriterier());
        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getIdent()).isEqualTo("04127811111");
    }

    private PersonsoekKriterier lagPersonsoekKriterier() {
        return PersonsoekKriterier.builder()
                .fornavn("Talentfull")
                .etternavn("Knott")
                .foedselsdato(LocalDate.of(1978, 12, 4))
                .build();
    }

    private FinnPersonResponse lagFinnPersonResponseMedEnPerson() {
        FinnPersonResponse response = new FinnPersonResponse();
        response.setTotaltAntallTreff(1);
        response.getPersonListe().add(lagPerson());
        return response;
    }

    private no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person lagPerson() {
        val person = new no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person();
        val ident = new NorskIdent();
        ident.setIdent("04127811111");
        person.setIdent(ident);

        return person;
    }
}

