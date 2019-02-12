package no.nav.melosys.eessi.service.helpers;

import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.sed.helpers.LandkodeMapper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LandkodeMapperTest {

    @Test
    public void getIso3_expectIso2() throws NotFoundException {
        assertThat(LandkodeMapper.getLandkodeIso2("NOR"), is("NO"));
        assertThat(LandkodeMapper.getLandkodeIso2("SWE"), is("SE"));
        assertThat(LandkodeMapper.getLandkodeIso2("DNK"), is("DK"));
    }

    @Test
    public void getIso3_withIso2_expectIso2() throws NotFoundException {
        assertThat(LandkodeMapper.getLandkodeIso2("NO"), is("NO"));
        assertThat(LandkodeMapper.getLandkodeIso2("SE"), is("SE"));
        assertThat(LandkodeMapper.getLandkodeIso2("DK"), is("DK"));
    }

    @Test(expected = NotFoundException.class)
    public void getIso3_expectNotFoundException() throws NotFoundException {
        LandkodeMapper.getLandkodeIso2("ABC");
    }
}
