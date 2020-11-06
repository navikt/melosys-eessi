package no.nav.melosys.eessi.service.identifisering;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.val;
import no.nav.melosys.eessi.models.SedType;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonSokTest {

    private final String IDENT = "01058312345";

    @Mock
    private TpsService tpsService;

    private PersonSok personSok;

    @Before
    public void setup() throws Exception {
        personSok = new PersonSok(tpsService);
        PersonSoekResponse response = new PersonSoekResponse();
        response.setIdent(IDENT);
        when(tpsService.hentPerson(IDENT)).thenReturn(lagTpsPerson());
        when(tpsService.soekEtterPerson(any())).thenReturn(Collections.singletonList(response));
    }

    @Test
    public void søkPersonFraSed_ettTreffKorrekteOpplysninger_forventIdentIdentifisert() throws Exception {
        SED sed = lagSed();

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isTrue();
        assertThat(sokResultat.getIdent()).isEqualTo(IDENT);
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.IDENTIFISERT);
    }

    @Test
    public void søkPersonFraSed_feilFødselsdato_forventIngenIdentFeilFødselsdato() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().setFoedselsdato("1999-01-01");

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FEIL_FOEDSELSDATO);
    }

    @Test
    public void søkPersonFraSed_feilStatsborgerskap_forventIngenIdentFeilStatsborgerskap() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().getStatsborgerskap().clear();

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FEIL_STATSBORGERSKAP);
    }

    @Test
    public void søkPersonFraSed_ingenTreff_forventIngenIdentIngenTreff() throws Exception {
        when(tpsService.soekEtterPerson(any())).thenReturn(Collections.emptyList());

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(lagSed());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.INGEN_TREFF);
    }

    @Test
    public void søkPersonFraSed_flereTreff_forventIngenIdentFlereTreff() throws Exception {
        when(tpsService.soekEtterPerson(any())).thenReturn(Lists.newArrayList(new PersonSoekResponse(), new PersonSoekResponse()));

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(lagSed());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FLERE_TREFF);
    }

    @Test
    public void søkPersonFraSed_finnerIkkeITPS_forventIngenIdentFnrIkkeFunnet() throws Exception {
        when(tpsService.hentPerson(anyString())).thenThrow(NotFoundException.class);

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(lagSed());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FNR_IKKE_FUNNET);
    }

    @Test
    public void søkPersonFraSed_personFunnetOpphørt_forventIngenIdentPersonOpphørt() throws Exception {
        Person person = lagTpsPerson().withPersonstatus(new Personstatus()
                .withPersonstatus(new Personstatuser().withValue("UTAN")));

        when(tpsService.hentPerson(anyString())).thenReturn(person);
        PersonSokResultat sokResultat = personSok.søkPersonFraSed(lagSed());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.PERSON_OPPHORT);
    }


    @Test
    public void søkPersonFraSed_tidssoneForDatoEttTreffKorrekteOpplysninger_forventIdentIdentifisert() throws Exception {
        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().setFoedselsdato(sed.getNav().getBruker().getPerson().getFoedselsdato() + "+02:00");

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isTrue();
        assertThat(sokResultat.getIdent()).isEqualTo(IDENT);
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.IDENTIFISERT);
    }

    @Test
    public void søkPersonFraSed_finnerIkkePersonFraSedStruktur_forventIngenTreff() throws Exception {
        SED sed = lagSed();
        sed.setSedType(SedType.X007.name());

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.INGEN_TREFF);
    }

    @Test
    public void søkPersonFraSed_x001UtenStatsborgerskap_statsborgerskapSjekkesIkkeEttTreff() throws IOException {
        SED sed = sedX001();
        sed.finnPerson().ifPresent(p -> p.setFoedselsdato("1983-05-01"));

        assertThat(personSok.søkPersonFraSed(sed))
                .extracting(PersonSokResultat::getIdent, PersonSokResultat::getBegrunnelse)
                .containsExactly(IDENT, SoekBegrunnelse.IDENTIFISERT);
    }

    private SED lagSed() throws IOException {
        SED sed = sedA009();

        val statsborgerskap = new no.nav.melosys.eessi.models.sed.nav.Statsborgerskap();
        statsborgerskap.setLand("NO");
        sed.getNav().getBruker().getPerson().getStatsborgerskap().add(statsborgerskap);

        return sed;
    }

    private SED sedA009() throws IOException {
        return hentSedFil("mock/sedA009.json");
    }

    private SED sedX001() throws IOException {
        return hentSedFil("mock/sedX001.json");
    }

    private SED hentSedFil(String filnavn) throws IOException {
        URL jsonUrl = getClass().getClassLoader().getResource(filnavn);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
        return mapper.readValue(jsonUrl, SED.class);
    }

    private Person lagTpsPerson() throws DatatypeConfigurationException {
        return new Person()
                .withAktoer(new AktoerId()
                        .withAktoerId(IDENT))
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
