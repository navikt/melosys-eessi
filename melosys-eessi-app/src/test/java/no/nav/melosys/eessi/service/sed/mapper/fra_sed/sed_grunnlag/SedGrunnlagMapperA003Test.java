package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.controller.dto.*;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA003;
import no.nav.melosys.eessi.models.sed.nav.Arbeidsgiver;
import no.nav.melosys.eessi.models.sed.nav.Pin;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class SedGrunnlagMapperA003Test {
    @Test
    void map_medUtfyltNav_forventVerdier() throws IOException {
        SedGrunnlagDto sedGrunnlagDto = new SedGrunnlagMapperA003().map(hentSed());

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
                .containsExactlyInAnyOrder(
                        tuple("EQUINOR ASA", "923609016"),
                        tuple("adf", "123321"),
                        tuple("swe", "123")
                );

        assertThat(sedGrunnlagDto.getArbeidsgivendeVirksomheter())
                .as("Arbeidsgivende virksomheter har rette adresser")
                .extracting(Virksomhet::getAdresse)
                .extracting(Adresse::getLand, Adresse::getPostnr, Adresse::getPoststed, Adresse::getGateadresse)
                .containsExactlyInAnyOrder(
                        tuple("BE", "4035", "STAVANGER", "Forusbeen 50"),
                        tuple("BE", null, "by", ""),
                        tuple("SE", null, "stck", "")
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
    void map_ingenBostedsadresse_forventPostadresse() throws IOException {
        SED sed = hentSed();
        var adresse = sed.getNav().getBruker().getAdresse().get(0);
        adresse.setType(Adressetype.POSTADRESSE.getAdressetypeRina());
        sed.getNav().getBruker().setAdresse(List.of(adresse));

        assertThat(new SedGrunnlagMapperA003().map(sed).getBostedsadresse())
                .extracting(Adresse::getAdressetype, Adresse::getLand, Adresse::getGateadresse)
                .containsExactlyInAnyOrder(Adressetype.POSTADRESSE, "BE", "Testgate Testbyggnavn");
    }

    @Test
    void map_ingenAdresse_forventTomAdresse() throws IOException {
        SED sed = hentSed();
        sed.getNav().getBruker().setAdresse(List.of());

        assertThat(new SedGrunnlagMapperA003().map(sed).getBostedsadresse()).isEqualTo(new Adresse());
    }

    @Test
    void map_kunNorskIdent_forventTomListeAvUtenlandskeIdenter() throws IOException {
        SED sed = hentSed();
        Pin pin = sed.getNav().getBruker().getPerson().getPin().iterator().next();
        pin.setLand("NO");
        sed.getNav().getBruker().getPerson().setPin(List.of(pin));

        assertThat(new SedGrunnlagMapperA003().map(sed).getUtenlandskIdent()).isEmpty();
    }

    @Test
    void map_ingenGate_forventKunBygning() throws IOException {
        SED sed = hentSed();
        var adresse = sed.getNav().getBruker().getAdresse().get(0);
        adresse.setGate(null);
        sed.getNav().getBruker().setAdresse(List.of(adresse));

        assertThat(new SedGrunnlagMapperA003().map(sed).getBostedsadresse().getGateadresse()).isEqualTo("Testbyggnavn");
    }

    @Test
    void map_ingenBygning_forventKunGate() throws IOException {
        SED sed = hentSed();
        var adresse = sed.getNav().getBruker().getAdresse().get(0);
        adresse.setBygning(null);
        sed.getNav().getBruker().setAdresse(List.of(adresse));

        assertThat(new SedGrunnlagMapperA003().map(sed).getBostedsadresse().getGateadresse()).isEqualTo("Testgate");
    }

    @Test
    void map_ingenArbeidsgiverAdresse_forventIkkeNorskArbeidsgiver() throws IOException {
        Consumer<Arbeidsgiver> settTomAdresse = (Arbeidsgiver arbeidsgiver) -> arbeidsgiver.setAdresse(null);

        SED sed = hentSed();
        sed.getNav().getArbeidsgiver().forEach(settTomAdresse);
        ((MedlemskapA003) sed.getMedlemskap()).getAndreland().getArbeidsgiver().forEach(settTomAdresse);

        assertThat(new SedGrunnlagMapperA003().map(sed).getNorskeArbeidsgivendeVirksomheter()).isEmpty();
    }

    @Test
    void map_norskArbeidsgiverNullIdentifikator_forventIngenIdentifikator() throws IOException {
        SED sed = hentSed();
        ((MedlemskapA003) sed.getMedlemskap()).getAndreland().getArbeidsgiver().iterator().next().setIdentifikator(null);

        assertThat(new SedGrunnlagMapperA003().map(sed).getNorskeArbeidsgivendeVirksomheter().iterator().next().getOrgnr()).isNull();
    }

    private static SED hentSed() throws IOException {
        URL jsonUrl = SedGrunnlagMapperA003Test.class.getClassLoader().getResource("mock/sedA003.json");
        return new ObjectMapper().readValue(jsonUrl, SED.class);
    }
}
