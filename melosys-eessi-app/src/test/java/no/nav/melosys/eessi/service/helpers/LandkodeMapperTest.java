package no.nav.melosys.eessi.service.helpers;

import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class LandkodeMapperTest {

    @Test
    void getIso2_expectIso2() throws NotFoundException {
        assertThat(LandkodeMapper.getLandkodeIso2("NOR")).isEqualTo("NO");
        assertThat(LandkodeMapper.getLandkodeIso2("SWE")).isEqualTo("SE");
        assertThat(LandkodeMapper.getLandkodeIso2("DNK")).isEqualTo("DK");
    }

    @Test
    void getIso2_withIso2_expectIso2() throws NotFoundException {
        assertThat(LandkodeMapper.getLandkodeIso2("NO")).isEqualTo("NO");
        assertThat(LandkodeMapper.getLandkodeIso2("SE")).isEqualTo("SE");
        assertThat(LandkodeMapper.getLandkodeIso2("DK")).isEqualTo("DK");
    }

    @Test
    void getIso3_expectNotFoundException() throws NotFoundException {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> LandkodeMapper.getLandkodeIso2("ABC"))
                .withMessageContaining("ikke funnet");
    }

    @Test
    void getIso2_medIkkeISOStandardKoder_forventSammeKodeTilbake() throws NotFoundException {
        assertThat(LandkodeMapper.getLandkodeIso2("???")).isEqualTo("???");
        assertThat(LandkodeMapper.getLandkodeIso2("XXX")).isEqualTo("XS");
    }
}
