package no.nav.melosys.eessi.service.identifisering;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.MetrikkerRegistrering;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.tps.TpsService;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonsokSokTest {

    private final String PERSON = "01058312345";

    @Mock
    private TpsService tpsService;
    @Mock
    private MetrikkerRegistrering metrikkerRegistrering;

    private PersonsokSok personsokSok;

    @Before
    public void setup() throws Exception {
        personsokSok = new PersonsokSok(tpsService, metrikkerRegistrering);
        PersonSoekResponse response = new PersonSoekResponse();
        response.setIdent(PERSON);
        when(tpsService.hentPerson("1234")).thenThrow(NotFoundException.class);
        when(tpsService.hentPerson(PERSON)).thenReturn(lagTpsPerson());
        when(tpsService.soekEtterPerson(any())).thenReturn(Collections.singletonList(response));
    }

    @Test
    public void finnNorskIdent_medGyldigIdent_forventGyldigIdent() throws Exception {
        Optional<String> ident = personsokSok.finnNorskIdent(lagSedHendelse(), lagSed());

        verify(tpsService).hentPerson(anyString());
        assertThat(ident).isPresent();
        assertThat(ident.get()).isEqualTo(PERSON);
    }

    @Test
    public void finnNorskIdent_medTomIdent_forventGyldigIdent() throws Exception {
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker(null);

        Optional<String> ident = personsokSok.finnNorskIdent(sedHendelse, lagSed());

        verify(tpsService).hentPerson(anyString());
        verify(tpsService).soekEtterPerson(any());

        assertThat(ident).isPresent();
        assertThat(ident.get()).isEqualTo(PERSON);
    }

    @Test
    public void finnNorskIdent_medIdentSomIkkeBlirFunnetITPS_forventGyldigIdent() throws Exception {
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker("1234");

        Optional<String> ident = personsokSok.finnNorskIdent(sedHendelse, lagSed());

        verify(tpsService, times(2)).hentPerson(anyString());
        verify(tpsService).soekEtterPerson(any());

        assertThat(ident).isPresent();
        assertThat(ident.get()).isEqualTo(PERSON);
    }

    @Test
    public void finnNorskIdent_medFeilFoedselsdato_forventIdentLikNull() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().setFoedselsdato("1999-01-01");

        Optional<String> ident = personsokSok.finnNorskIdent(lagSedHendelse(), sed);

        assertThat(ident).isNotPresent();
    }

    @Test
    public void finnNorskIdent_medFeilStatsborgerskap_forventIdentLikNull() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().getStatsborgerskap().clear();

        Optional<String> ident = personsokSok.finnNorskIdent(lagSedHendelse(), sed);

        assertThat(ident).isNotPresent();
    }

    @Test
    public void finnNorskIdent_medIngenIdentOgIngenTreffPaaSoek_forventIdentLikNull() throws Exception {
        when(tpsService.soekEtterPerson(any())).thenReturn(Collections.emptyList());
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker(null);

        Optional<String> ident = personsokSok.finnNorskIdent(sedHendelse, lagSed());

        assertThat(ident).isNotPresent();
    }

    @Test
    public void finnNorskIdent_medIngenTreffITPS_forventIdentLikNull() throws Exception {
        when(tpsService.hentPerson(anyString())).thenThrow(NotFoundException.class);

        Optional<String> ident = personsokSok.finnNorskIdent(lagSedHendelse(), lagSed());

        assertThat(ident).isNotPresent();
    }

    @Test
    public void finnNorskIdent_medOpphoertPerson_forventIdentLikNull() throws Exception {
        Person person = lagTpsPerson().withPersonstatus(new Personstatus()
                .withPersonstatus(new Personstatuser().withValue("UTAN")));

        when(tpsService.hentPerson(anyString())).thenReturn(person);
        Optional<String> ident = personsokSok.finnNorskIdent(lagSedHendelse(), lagSed());

        assertThat(ident).isNotPresent();
    }

    private SedHendelse lagSedHendelse() {
        return SedHendelse.builder()
                .rinaDokumentId("abcd1234")
                .rinaSakId("3232")
                .navBruker(PERSON)
                .sedType("A009")
                .build();
    }

    private SED lagSed() throws IOException {
        URL jsonUrl = getClass().getClassLoader().getResource("mock/sedA009.json");
        ObjectMapper mapper = new ObjectMapper();
        SED sed = mapper.readValue(jsonUrl, SED.class);

        val statsborgerskap = new no.nav.melosys.eessi.models.sed.nav.Statsborgerskap();
        statsborgerskap.setLand("NO");
        sed.getNav().getBruker().getPerson().getStatsborgerskap().add(statsborgerskap);

        return sed;
    }

    private Person lagTpsPerson() throws DatatypeConfigurationException {
        return new Person()
                .withAktoer(new AktoerId()
                        .withAktoerId(PERSON))
                .withStatsborgerskap(new Statsborgerskap()
                        .withLand(new Landkoder().withValue("NOR")))
                .withFoedselsdato(new Foedselsdato()
                        .withFoedselsdato(lagXmlDato("1983-05-01")))
                .withPersonstatus(new Personstatus()
                        .withPersonstatus(new Personstatuser().withValue("BOSA")));
    }

    /**
     * @param dato format: yyyy-MM-dd
     */
    private XMLGregorianCalendar lagXmlDato(String dato) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(dato);
    }
}
