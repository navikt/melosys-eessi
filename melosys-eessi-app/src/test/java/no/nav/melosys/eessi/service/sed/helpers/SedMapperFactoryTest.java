package no.nav.melosys.eessi.service.sed.helpers;

import java.util.List;

import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.SedMapper;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg.A001Mapper;
import no.nav.melosys.eessi.service.sed.mapper.til_sed.lovvalg.A009Mapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SedMapperFactoryTest {

    @Test
    public void oppslagavSedA001GirKorrektMapper() throws Exception {
        SedMapper sedMapper = SedMapperFactory.sedMapper(SedType.A001);
        assertThat(sedMapper).isInstanceOf(A001Mapper.class);
    }

    @Test
    public void oppslagavSedA009GirKorrektMapper() throws Exception {
        SedMapper sedMapper = SedMapperFactory.sedMapper(SedType.A009);
        assertThat(sedMapper).isInstanceOf(A009Mapper.class);
    }

    @Test
    public void oppslagAvAlleSeder_girKorrektMapper() throws MappingException {
        var sedTyperMedMapper = List.of(
                SedType.A001,
                SedType.A002,
                SedType.A003,
                SedType.A004,
                SedType.A005,
                SedType.A008,
                SedType.A009,
                SedType.A010,
                SedType.A011,
                SedType.A012,

                SedType.H001,
                SedType.H003,
                SedType.H004,
                SedType.H005,
                SedType.H010,
                SedType.H011,
                SedType.H020,
                SedType.H061,
                SedType.H065,
                SedType.H070,
                SedType.H120,
                SedType.H121,
                SedType.H130
        );

        for (var sedType : sedTyperMedMapper) {
            SedMapper sedMapper = SedMapperFactory.sedMapper(sedType);
            assertThat(sedMapper.getSedType()).isEqualTo(sedType);
        }
    }
}

