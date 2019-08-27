package no.nav.melosys.eessi.service.sed.helpers;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.models.exception.MappingException;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class A1GrunnlagMapperTest {

    @Test
    public void mapFromBestemmelse_expectString12_r() throws MappingException {
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_12_1)).isEqualTo("12_r");
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_12_2)).isEqualTo("12_r");
    }

    @Test
    public void mapFromBestemmelse_expectString16_R() throws MappingException {
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_16_1)).isEqualTo("16_R");
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_16_2)).isEqualTo("16_R");
    }

    @Test
    public void mapFromBestemmelse_expectStringAnnet() throws MappingException {
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_11_1)).isEqualTo("annet");
        assertThat(A1GrunnlagMapper.mapFromBestemmelse(Bestemmelse.ART_13_1_a)).isEqualTo("annet");
    }
}