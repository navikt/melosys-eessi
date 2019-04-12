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
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.tps.TpsService;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
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
        when(tpsService.hentPerson("1234")).thenThrow(HentPersonPersonIkkeFunnet.class);
        when(tpsService.hentPerson(PERSON)).thenReturn(lagTpsPerson());
        when(tpsService.soekEtterPerson(any())).thenReturn(PERSON);
    }

    @Test
    public void vurderPerson_medGyldigIdent() throws Exception {
        personvurdering.vurderPerson(lagSedHendelse(), lagSed());

        verify(tpsService).hentPerson(anyString());
    }

    @Test
    public void vurderPerson_medTomIdent() throws Exception {
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker(null);

        personvurdering.vurderPerson(sedHendelse, lagSed());

        verify(tpsService).hentPerson(anyString());
        verify(tpsService).soekEtterPerson(any());

        assertThat(sedHendelse.getNavBruker()).isEqualTo(PERSON);
    }

    @Test
    public void vurderPerson_medIdentSomIkkeBlirFunnet() throws Exception {
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker("1234");

        personvurdering.vurderPerson(sedHendelse, lagSed());

        verify(tpsService, times(2)).hentPerson(anyString());
        verify(tpsService).soekEtterPerson(any());

        assertThat(sedHendelse.getNavBruker()).isEqualTo(PERSON);
    }

    @Test(expected = ValidationException.class)
    public void vurderPerson_medFeilFoedselsdato_forventException() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().setFoedselsdato("1999-01-01");

        personvurdering.vurderPerson(lagSedHendelse(), sed);
    }

    @Test(expected = ValidationException.class)
    public void vurderPerson_medFeilStatsborgerskap_forventException() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().getStatsborgerskap().clear();

        personvurdering.vurderPerson(lagSedHendelse(), sed);
    }

    @Test(expected = NotFoundException.class)
    public void vurderPerson_medIngenIdentOgIngenTreffPaaSoek_forventException() throws Exception {
        when(tpsService.soekEtterPerson(any())).thenReturn("");
        SedHendelse sedHendelse = lagSedHendelse();
        sedHendelse.setNavBruker(null);

        personvurdering.vurderPerson(sedHendelse, lagSed());
    }

    @Test(expected = NotFoundException.class)
    public void vurderPerson_medIngenTreffITPS_forventException() throws Exception {
        when(tpsService.hentPerson(anyString())).thenThrow(HentPersonPersonIkkeFunnet.class);

        personvurdering.vurderPerson(lagSedHendelse(), lagSed());
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
                        .withFoedselsdato(lagXmlDato("1983-05-01")));
    }

    /**
     * @param dato format: yyyy-MM-dd
     */
    private XMLGregorianCalendar lagXmlDato(String dato) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(dato);
    }
}
