package no.nav.melosys.eessi.service.sed.helpers;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.models.exception.MappingException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class A1GrunnlagMapperTest {

    @Test
    public void mapFromBestemmelse_expectString12_r() throws MappingException {
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_12_1), is("12_r"));
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_12_2), is("12_r"));
    }

    @Test
    public void mapFromBestemmelse_expectString16_R() throws MappingException {
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_16_1), is("16_R"));
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_16_2), is("16_R"));
    }

    @Test
    public void mapFromBestemmelse_expectStringAnnet() throws MappingException {
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_11_1), is("annet"));
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_13_1_a), is("annet"));
    }
}