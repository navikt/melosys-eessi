package no.nav.melosys.eessi.service.behandling;

import java.io.IOException;
import java.net.URL;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.tps.TpsService;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonvurderingTest {

    private final String PERSON = "01058312345";

    @Mock
    private TpsService tpsService;

    @InjectMocks
    private Personvurdering personvurdering;

    @Before
    public void setup() throws Exception {
        when(tpsService.hentPerson("1234")).thenThrow(NotFoundException.class);
        when(tpsService.hentPerson(PERSON)).thenReturn(lagTpsPerson());
        when(tpsService.soekEtterPerson(any())).thenReturn(PERSON);
    }

    @Test
    public void hentNorskIdent_medGyldigIdent_forventGyldigIdent() throws Exception {
        String ident = personvurdering.hentNorskIdent(lagSedHendelse(), lagSed());

        verify(tpsService).hentPerson(anyString());
        assertThat(ident).isEqualTo(PERSON);
    }

    @Test
    public void hentNorskIdent_medTomIdent_forventGyldigIdent() throws Exception {
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker(null);

        String ident = personvurdering.hentNorskIdent(sedHendelse, lagSed());

        verify(tpsService).hentPerson(anyString());
        verify(tpsService).soekEtterPerson(any());

        assertThat(ident).isEqualTo(PERSON);
    }

    @Test
    public void hentNorskIdent_medIdentSomIkkeBlirFunnetITPS_forventGyldigIdent() throws Exception {
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker("1234");

        String ident = personvurdering.hentNorskIdent(sedHendelse, lagSed());

        verify(tpsService, times(2)).hentPerson(anyString());
        verify(tpsService).soekEtterPerson(any());

        assertThat(ident).isEqualTo(PERSON);
    }

    @Test
    public void hentNorskIdent_medFeilFoedselsdato_forventIdentLikNull() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().setFoedselsdato("1999-01-01");

        String ident = personvurdering.hentNorskIdent(lagSedHendelse(), sed);

        assertThat(ident).isNull();
    }

    @Test
    public void hentNorskIdent_medFeilStatsborgerskap_forventIdentLikNull() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().getStatsborgerskap().clear();

        String ident = personvurdering.hentNorskIdent(lagSedHendelse(), sed);

        assertThat(ident).isNull();
    }

    @Test
    public void hentNorskIdent_medIngenIdentOgIngenTreffPaaSoek_forventIdentLikNull() throws Exception {
        when(tpsService.soekEtterPerson(any())).thenReturn("");
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker(null);

        String ident = personvurdering.hentNorskIdent(sedHendelse, lagSed());

        assertThat(ident).isNullOrEmpty();
    }

    @Test
    public void hentNorskIdent_medIngenTreffITPS_forventIdentLikNull() throws Exception {
        when(tpsService.hentPerson(anyString())).thenThrow(NotFoundException.class);

        String ident = personvurdering.hentNorskIdent(lagSedHendelse(), lagSed());

        assertThat(ident).isNull();
    }

    @Test
    public void hentNorskIdent_medOpphoertPerson_forventIdentLikNull() throws Exception {
        Person person = lagTpsPerson().withPersonstatus(new Personstatus()
                .withPersonstatus(new Personstatuser().withValue("UTAN")));

        when(tpsService.hentPerson(anyString())).thenReturn(person);
        String ident = personvurdering.hentNorskIdent(lagSedHendelse(), lagSed());

        assertThat(ident).isNull();
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
