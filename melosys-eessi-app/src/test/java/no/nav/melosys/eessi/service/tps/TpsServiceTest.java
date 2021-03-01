package no.nav.melosys.eessi.service.tps;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;

import lombok.SneakyThrows;
import no.nav.melosys.eessi.integration.tps.TpsService;
import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.integration.tps.person.PersonConsumer;
import no.nav.melosys.eessi.integration.tps.personsok.PersonsokConsumer;
import no.nav.melosys.eessi.metrikker.PersonSokMetrikker;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.service.tps.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsokKriterier;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault1;
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TpsServiceTest {

    @Mock
    private PersonConsumer personConsumer;

    @Mock
    private AktoerConsumer aktoerConsumer;

    @Mock
    private PersonsokConsumer personsokConsumer;

    @Mock
    private PersonSokMetrikker personSokMetrikker;

    private TpsService tpsService;

    private HentPersonResponse hentPersonResponse;

    @BeforeEach
    public void setup() throws Exception {
        tpsService = new TpsService(personConsumer, aktoerConsumer, personsokConsumer, personSokMetrikker);

        hentPersonResponse = new HentPersonResponse().withPerson(lagPerson());
    }

    @Test
    void hentPerson_expectPerson() throws HentPersonPersonIkkeFunnet, HentPersonSikkerhetsbegrensning {
        when(personConsumer.hentPerson(any())).thenReturn(hentPersonResponse);
        PersonModell person = tpsService.hentPerson("11223344556");
        assertThat(person).isEqualTo(
                PersonModell.builder()
                        .ident("11223344556")
                        .fornavn("Fornavn")
                        .etternavn("Etternavn")
                        .fødselsdato(LocalDate.parse("2000-01-01"))
                        .statsborgerskapLandkodeISO2(Collections.singleton("SE"))
                        .erOpphørt(false)
                        .build()
        );
    }

    @Test
    void hentAktoerId_expectAktoerId() {
        when(aktoerConsumer.hentAktoerId(anyString())).thenReturn("998877665544");
        String aktoerId = tpsService.hentAktoerId("11223344556");
        assertThat(aktoerId).isEqualTo("998877665544");
    }

    @Test
    void soekEtterPerson_forventIdent() throws FinnPersonFault, FinnPersonFault1 {
        when(personsokConsumer.finnPerson(any())).thenReturn(lagFinnPersonResponseMedEnPerson());
        List<PersonSokResponse> response = tpsService.soekEtterPerson(lagPersonsoekKriterier());
        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getIdent()).isEqualTo("04127811111");
    }

    private PersonsokKriterier lagPersonsoekKriterier() {
        return PersonsokKriterier.builder()
                .fornavn("Talentfull")
                .etternavn("Knott")
                .foedselsdato(LocalDate.of(1978, 12, 4))
                .build();
    }

    private FinnPersonResponse lagFinnPersonResponseMedEnPerson() {
        FinnPersonResponse response = new FinnPersonResponse();
        response.setTotaltAntallTreff(1);
        response.getPersonListe().add(lagPersonsøkPerson());
        return response;
    }

    private no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person lagPersonsøkPerson() {
        var person = new no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.Person();
        var ident = new NorskIdent();
        ident.setIdent("04127811111");
        person.setIdent(ident);

        return person;
    }

    @SneakyThrows
    private Person lagPerson() {
        return new Person()
                .withPersonnavn(new Personnavn()
                        .withFornavn("Fornavn")
                        .withEtternavn("Etternavn"))
                .withFoedselsdato(new Foedselsdato()
                        .withFoedselsdato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2000-01-01")))
                .withStatsborgerskap(new Statsborgerskap().withLand(new Landkoder().withValue("SWE")))
                .withAktoer(new AktoerId().withAktoerId("11223344556"))
                .withPersonstatus(new Personstatus().withPersonstatus(new Personstatuser().withValue("BOSA")));
    }
}

