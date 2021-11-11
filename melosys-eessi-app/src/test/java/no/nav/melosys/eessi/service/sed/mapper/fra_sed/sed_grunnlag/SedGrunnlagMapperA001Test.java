package no.nav.melosys.eessi.service.sed.mapper.fra_sed.sed_grunnlag;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.controller.dto.*;
import no.nav.melosys.eessi.models.sed.SED;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class SedGrunnlagMapperA001Test {

    @Test
    void map_forventetVerdier() throws IOException {
        SedGrunnlagDto sedGrunnlagDto = new SedGrunnlagMapperA001().map(hentSed());

        assertThat(sedGrunnlagDto).isNotNull();
        assertThat(sedGrunnlagDto.getSedType()).isEqualTo("A001");

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


    }

    private static SED hentSed() throws IOException {
        URL jsonUrl = SedGrunnlagMapperA003Test.class.getClassLoader().getResource("mock/sedA001.json");
        return new ObjectMapper().readValue(jsonUrl, SED.class);
    }
}
