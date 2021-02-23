package no.nav.melosys.eessi.service.identifisering;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.val;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.tps.personsok.PersonSoekResponse;
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
class PersonSokTest {

    private final String IDENT = "01058312345";

    @Mock
    private PersonFasade personFasade;

    private PersonSok personSok;

    @BeforeEach
    public void setup() throws Exception {
        personSok = new PersonSok(personFasade);
    }

    private PersonSoekResponse lagPersonSøkResponse() {
        PersonSoekResponse response = new PersonSoekResponse();
        response.setIdent(IDENT);
        return response;
    }

    @Test
    void søkPersonFraSed_ettTreffKorrekteOpplysninger_forventIdentIdentifisert() throws Exception {
        when(personFasade.hentPerson(IDENT)).thenReturn(lagPersonModell(false));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        SED sed = lagSed();

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isTrue();
        assertThat(sokResultat.getIdent()).isEqualTo(IDENT);
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.IDENTIFISERT);
    }

    @Test
    void søkPersonFraSed_feilFødselsdato_forventIngenIdentFeilFødselsdato() throws Exception {
        when(personFasade.hentPerson(IDENT)).thenReturn(lagPersonModell(false));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().setFoedselsdato("1999-01-01");

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FEIL_FOEDSELSDATO);
    }

    @Test
    void søkPersonFraSed_feilStatsborgerskap_forventIngenIdentFeilStatsborgerskap() throws Exception {
        when(personFasade.hentPerson(IDENT)).thenReturn(lagPersonModell(false));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().getStatsborgerskap().clear();

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FEIL_STATSBORGERSKAP);
    }

    @Test
    void søkPersonFraSed_ingenTreff_forventIngenIdentIngenTreff() throws Exception {
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.emptyList());

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(lagSed());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.INGEN_TREFF);
    }

    @Test
    void søkPersonFraSed_flereTreff_forventIngenIdentFlereTreff() throws Exception {
        when(personFasade.soekEtterPerson(any())).thenReturn(Lists.newArrayList(new PersonSoekResponse(), new PersonSoekResponse()));

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(lagSed());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FLERE_TREFF);
    }

    @Test
    void søkPersonFraSed_finnerIkkeITPS_forventIngenIdentFnrIkkeFunnet() throws Exception {
        when(personFasade.hentPerson(anyString())).thenThrow(NotFoundException.class);
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(lagSed());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FNR_IKKE_FUNNET);
    }

    @Test
    void søkPersonFraSed_personFunnetOpphørt_forventIngenIdentPersonOpphørt() throws Exception {
        when(personFasade.hentPerson(anyString())).thenReturn(lagPersonModell(true));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(lagSed());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.PERSON_OPPHORT);
    }


    @Test
    void søkPersonFraSed_tidssoneForDatoEttTreffKorrekteOpplysninger_forventIdentIdentifisert() throws Exception {
        when(personFasade.hentPerson(IDENT)).thenReturn(lagPersonModell(false));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        SED sed = lagSed();
        sed.getNav().getBruker().getPerson().setFoedselsdato(sed.getNav().getBruker().getPerson().getFoedselsdato() + "+02:00");

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isTrue();
        assertThat(sokResultat.getIdent()).isEqualTo(IDENT);
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.IDENTIFISERT);
    }

    @Test
    void søkPersonFraSed_finnerIkkePersonFraSedStruktur_forventIngenTreff() throws Exception {
        SED sed = lagSed();
        sed.setSedType(SedType.X007.name());

        PersonSokResultat sokResultat = personSok.søkPersonFraSed(sed);

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.INGEN_TREFF);
    }

    @Test
    void søkPersonFraSed_x001UtenStatsborgerskap_statsborgerskapSjekkesIkkeEttTreff() throws IOException {
        when(personFasade.hentPerson(IDENT)).thenReturn(lagPersonModell(false));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

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

    private PersonModell lagPersonModell(boolean erOpphørt) {
        return PersonModell.builder()
                .ident(IDENT)
                .fornavn("Fornavn")
                .etternavn("Etternavn")
                .fødselsdato(LocalDate.parse("1983-05-01"))
                .statsborgerskapLandkodeISO2(List.of("NO"))
                .erOpphørt(erOpphørt)
                .build();
    }
}
