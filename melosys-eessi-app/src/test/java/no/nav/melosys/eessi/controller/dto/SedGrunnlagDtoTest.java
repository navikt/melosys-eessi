package no.nav.melosys.eessi.controller.dto;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class SedGrunnlagDtoTest {

    @Test
    public void av_medUtfyltNav_forventVerdier() throws IOException {
        SedGrunnlagDto sedGrunnlagDto = SedGrunnlagDto.av(hentSed().getNav());

        assertThat(sedGrunnlagDto).isNotNull();
        assertThat(sedGrunnlagDto.getUtenlandskIdent())
                .as("Utenlandsk ident har rett felt")
                .extracting(Ident::getIdent, Ident::getLandkode, Ident::erUtenlandsk)
                .containsExactlyInAnyOrder(tuple("15225345345", "BG", true));

        assertThat(sedGrunnlagDto.getBostedsadresse())
                .as("Bostedsadresse har rett felt")
                .extracting(Adresse::getAdressetype, Adresse::getLand, Adresse::getGateadresse)
                .containsExactlyInAnyOrder(Adressetype.BOSTEDSADRESSE, "BE", "Testgate Testbyggnavn");

        assertThat(sedGrunnlagDto.getArbeidssteder())
                .as("Arbeidssteder har rett info")
                .extracting(Arbeidssted::getNavn, Arbeidssted::isFysisk, Arbeidssted::getHjemmebase)
                .containsExactlyInAnyOrder(
                        tuple("Testarbeidsstednavn", false, "Testarbeidsstedbase"),
                        tuple("Testarbeidsstednavn2", true, "Testarbeidsstedbase2")
                );

        assertThat(sedGrunnlagDto.getArbeidssteder())
                .as("Arbeidssteder har rette adresser")
                .extracting(Arbeidssted::getAdresse)
                .extracting(Adresse::getLand, Adresse::getPostnr, Adresse::getPoststed, Adresse::getRegion, Adresse::getGateadresse)
                .containsExactlyInAnyOrder(
                        tuple("EE", "Testarbeidsstedpostkode", "Testarbeidsstedby", "Testarbeidsstedregion", "Testarbeidsstedgate Testarbeidsstedbygning"),
                        tuple("CY", null, "Testarbeidsstedby2", null, "Testarbeidsstedgate2 Testarbeidsstedbygning2")
                );

        assertThat(sedGrunnlagDto.getArbeidsgivendeVirksomheter())
                .as("Arbeidsgivende virksomheter har rett info")
                .extracting(Virksomhet::getNavn, Virksomhet::getOrgnr)
                .containsExactlyInAnyOrder(tuple("Testarbeidsgiver", "TestOrgnummer"));

        assertThat(sedGrunnlagDto.getArbeidsgivendeVirksomheter())
                .as("Arbeidsgivende virksomheter har rette adresser")
                .extracting(Virksomhet::getAdresse)
                .extracting(Adresse::getLand, Adresse::getPostnr, Adresse::getPoststed, Adresse::getRegion, Adresse::getGateadresse)
                .containsExactlyInAnyOrder(
                        tuple("CY", "Testarbeidsgiverpostkode", "Testarbeidsgiverby", "Testarbeidsgiverregion", "Testarbeidsgivergate Testarbeidsgiverbygning")
                );

        assertThat(sedGrunnlagDto.getSelvstendigeVirksomheter())
                .as("Selvstendige virksomheter har rett info")
                .extracting(Virksomhet::getNavn, Virksomhet::getOrgnr)
                .containsExactlyInAnyOrder(tuple("Testselvstendignavn", "Testselvstendignummer"));

        assertThat(sedGrunnlagDto.getSelvstendigeVirksomheter())
                .as("Selvstendige virksomheter har rette adresser")
                .extracting(Virksomhet::getAdresse)
                .extracting(Adresse::getLand, Adresse::getPostnr, Adresse::getPoststed, Adresse::getRegion, Adresse::getGateadresse)
                .containsExactlyInAnyOrder(
                        tuple("BG", "Testselvstendigpostkode", "Testselvstendigby", "Testselvstendigregion", "Testselvstendiggate Testselvstendigbygning")
                );
    }

    @Test
    public void av_ingenBostedsadresse_forventPostadresse() throws IOException {
        SED sed = hentSed();
        var adresse = sed.getNav().getBruker().getAdresse().get(0);
        adresse.setType(Adressetype.POSTADRESSE.getAdressetypeRina());
        sed.getNav().getBruker().setAdresse(List.of(adresse));

        assertThat(SedGrunnlagDto.av(sed.getNav()).getBostedsadresse())
                .extracting(Adresse::getAdressetype, Adresse::getLand, Adresse::getGateadresse)
                .containsExactlyInAnyOrder(Adressetype.POSTADRESSE, "BE", "Testgate Testbyggnavn");
    }

    @Test
    public void av_ingenAdresse_forventTomAdresse() throws IOException {
        SED sed = hentSed();
        sed.getNav().getBruker().setAdresse(List.of());

        assertThat(SedGrunnlagDto.av(sed.getNav()).getBostedsadresse()).isEqualToComparingFieldByField(new Adresse());
    }

    @Test
    public void av_kunNorskIdent_forventTomListeAvUtenlandskeIdenter() throws IOException {
        SED sed = hentSed();
        Pin pin = sed.getNav().getBruker().getPerson().getPin().get(0);
        pin.setLand("NO");
        sed.getNav().getBruker().getPerson().setPin(List.of(pin));

        assertThat(SedGrunnlagDto.av(sed.getNav()).getUtenlandskIdent()).isEmpty();
    }

    @Test
    public void av_ingenGate_forventKunBygning() throws IOException {
        SED sed = hentSed();
        var adresse = sed.getNav().getBruker().getAdresse().get(0);
        adresse.setGate(null);
        sed.getNav().getBruker().setAdresse(List.of(adresse));

        assertThat(SedGrunnlagDto.av(sed.getNav()).getBostedsadresse().getGateadresse()).isEqualTo("Testbyggnavn");
    }

    @Test
    public void av_ingenBygning_forventKunGate() throws IOException {
        SED sed = hentSed();
        var adresse = sed.getNav().getBruker().getAdresse().get(0);
        adresse.setBygning(null);
        sed.getNav().getBruker().setAdresse(List.of(adresse));

        assertThat(SedGrunnlagDto.av(sed.getNav()).getBostedsadresse().getGateadresse()).isEqualTo("Testgate");
    }

    private static SED hentSed() throws IOException {
        URL jsonUrl = SedGrunnlagDtoTest.class.getClassLoader().getResource("mock/sedA001.json");
        return new ObjectMapper().readValue(jsonUrl, SED.class);
    }
}
