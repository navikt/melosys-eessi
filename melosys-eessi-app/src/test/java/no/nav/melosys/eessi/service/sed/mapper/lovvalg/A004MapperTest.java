package no.nav.melosys.eessi.service.sed.mapper.lovvalg;

import java.io.IOException;
import java.net.URISyntaxException;

import no.nav.melosys.eessi.controller.dto.SedDataDto;
import no.nav.melosys.eessi.controller.dto.UtpekingAvvisDto;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA004;
import no.nav.melosys.eessi.service.sed.SedDataStub;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class A004MapperTest {
    private A004Mapper a004Mapper = new A004Mapper();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void mapTilSed_forventSed() throws IOException, URISyntaxException, MappingException, NotFoundException {
        SedDataDto sedData = SedDataStub.getStub();
        UtpekingAvvisDto utpekingAvvisDto = new UtpekingAvvisDto(
            "DK",
            "begrunnelse",
            false
        );
        sedData.setUtpekingAvvis(utpekingAvvisDto);

        SED a004 = a004Mapper.mapTilSed(sedData);
        assertThat(a004).isNotNull();
        assertThat(a004.getMedlemskap()).isInstanceOf(MedlemskapA004.class);
    }

    @Test
    public void mapTilSed_utenUtpekingAvvis_forventException() throws IOException, URISyntaxException, MappingException, NotFoundException {
        SedDataDto sedData = SedDataStub.getStub();

        expectedException.expect(MappingException.class);
        expectedException.expectMessage("Trenger UtpekingAvvis for å opprette A004");
        a004Mapper.mapTilSed(sedData);
    }}
