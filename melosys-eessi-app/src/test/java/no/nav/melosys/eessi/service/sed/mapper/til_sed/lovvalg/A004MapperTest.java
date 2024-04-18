package no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.UtpekingAvvisDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA004;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class A004MapperTest {
    private final A004Mapper a004Mapper = new A004Mapper();

    @Test
    void mapTilSed_forventSed() throws IOException, URISyntaxException {
        SedDataDto sedData = SedDataStub.getStub();
        UtpekingAvvisDto utpekingAvvisDto = new UtpekingAvvisDto(
            "DK",
            "begrunnelse",
            false
        );
        sedData.setUtpekingAvvis(utpekingAvvisDto);

        SED a004 = a004Mapper.mapTilSed(sedData, false);
        assertThat(a004).isNotNull();
        assertThat(a004.getMedlemskap()).isInstanceOf(MedlemskapA004.class);
    }

    @Test
    void mapTilSed_utenUtpekingAvvis_forventException() throws IOException, URISyntaxException {
        SedDataDto sedData = SedDataStub.getStub();
        assertThatExceptionOfType(MappingException.class)
                .isThrownBy(() -> a004Mapper.mapTilSed(sedData, false))
                .withMessageContaining("Trenger UtpekingAvvis for å opprette A004");
    }}
