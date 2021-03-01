package no.nav.melosys.eessi.service.identifisering;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.val;
import no.nav.melosys.eessi.integration.pdl.PDLService;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.person.PersonModell;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.service.tps.personsok.PersonSokResponse;
import no.nav.melosys.eessi.service.tps.personsok.PersonsokKriterier;
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
    private final LocalDate defaultFødselsdato = LocalDate.of(2000, 1, 1);
    private final Collection<String> defaultStatsborgerskap = Set.of("NO");

    @Mock
    private PDLService personFasade;

    private PersonSok personSok;

    @BeforeEach
    public void setup() throws Exception {
        personSok = new PDLPersonSok(personFasade);
    }

    private PersonSokResponse lagPersonSøkResponse() {
        PersonSokResponse response = new PersonSokResponse();
        response.setIdent(IDENT);
        return response;
    }

    @Test
    void søkEtterPerson_ettTreffKorrekteOpplysninger_forventIdentIdentifisert() {
        when(personFasade.hentPerson(IDENT)).thenReturn(lagPersonModell(false));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        PersonSokResultat sokResultat = personSok.søkEtterPerson(personsoekKriterier());

        assertThat(sokResultat.personIdentifisert()).isTrue();
        assertThat(sokResultat.getIdent()).isEqualTo(IDENT);
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.IDENTIFISERT);
    }

    @Test
    void søkEtterPerson_feilFødselsdato_forventIngenIdentFeilFødselsdato() {
        when(personFasade.hentPerson(IDENT)).thenReturn(lagPersonModell(false));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        PersonSokResultat sokResultat = personSok.søkEtterPerson(personsoekKriterier(LocalDate.of(2000, 1, 2)));

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FEIL_FOEDSELSDATO);
    }

    @Test
    void søkEtterPerson_feilStatsborgerskap_forventIngenIdentFeilStatsborgerskap() {
        when(personFasade.hentPerson(IDENT)).thenReturn(lagPersonModell(false));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        PersonSokResultat sokResultat = personSok.søkEtterPerson(personsoekKriterier(Set.of()));

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FEIL_STATSBORGERSKAP);
    }

    @Test
    void søkEtterPerson_ingenTreff_forventIngenIdentIngenTreff() {
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.emptyList());

        PersonSokResultat sokResultat = personSok.søkEtterPerson(personsoekKriterier());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.INGEN_TREFF);
    }

    @Test
    void søkEtterPerson_flereTreff_forventIngenIdentFlereTreff() {
        when(personFasade.soekEtterPerson(any())).thenReturn(Lists.newArrayList(new PersonSokResponse(), new PersonSokResponse()));

        PersonSokResultat sokResultat = personSok.søkEtterPerson(personsoekKriterier());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getIdent()).isNull();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FLERE_TREFF);
    }

    @Test
    void søkEtterPerson_finnerIkkeITPS_forventIngenIdentFnrIkkeFunnet() {
        when(personFasade.hentPerson(anyString())).thenThrow(NotFoundException.class);
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        PersonSokResultat sokResultat = personSok.søkEtterPerson(personsoekKriterier());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.FNR_IKKE_FUNNET);
    }

    @Test
    void søkEtterPerson_personFunnetOpphørt_forventIngenIdentPersonOpphørt() {
        when(personFasade.hentPerson(anyString())).thenReturn(lagPersonModell(true));
        when(personFasade.soekEtterPerson(any())).thenReturn(Collections.singletonList(lagPersonSøkResponse()));

        PersonSokResultat sokResultat = personSok.søkEtterPerson(personsoekKriterier());

        assertThat(sokResultat.personIdentifisert()).isFalse();
        assertThat(sokResultat.getBegrunnelse()).isEqualTo(SoekBegrunnelse.PERSON_OPPHORT);
    }


    private PersonsokKriterier personsoekKriterier() {
        return personsoekKriterier(defaultFødselsdato, defaultStatsborgerskap);
    }

    private PersonsokKriterier personsoekKriterier(LocalDate fødselsdato) {
        return personsoekKriterier(fødselsdato, defaultStatsborgerskap);
    }

    private PersonsokKriterier personsoekKriterier(Collection<String> statsborgerskap) {
        return personsoekKriterier(defaultFødselsdato, statsborgerskap);
    }

    private PersonsokKriterier personsoekKriterier(LocalDate fødselsdato, Collection<String> statsborgerskap) {
        return PersonsokKriterier.builder()
                .fornavn("Fornavn")
                .etternavn("Etternavn")
                .foedselsdato(fødselsdato)
                .statsborgerskapISO2(statsborgerskap)
                .build();
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
                .fødselsdato(defaultFødselsdato)
                .statsborgerskapLandkodeISO2(defaultStatsborgerskap)
                .erOpphørt(erOpphørt)
                .build();
    }
}
