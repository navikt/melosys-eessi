package no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding;

import no.nav.melosys.eessi.models.SedType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperFactoryTest {

    @Test
    public void hentA002Mapper() {
        MelosysEessiMeldingMapper mapper = MelosysEessiMeldingMapperFactory.getMapper(SedType.A002);
        assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA002.class);
    }

    @Test
    public void hentA003Mapper() {
        MelosysEessiMeldingMapper mapper = MelosysEessiMeldingMapperFactory.getMapper(SedType.A003);
        assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA003.class);
    }

    @Test
    public void hentA009Mapper() {
        MelosysEessiMeldingMapper mapper = MelosysEessiMeldingMapperFactory.getMapper(SedType.A009);
        assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA009.class);
    }

    @Test
    public void hentA010Mapper() {
        MelosysEessiMeldingMapper mapper = MelosysEessiMeldingMapperFactory.getMapper(SedType.A010);
        assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA010.class);
    }

    @Test
    public void hentA011Mapper() {
        MelosysEessiMeldingMapper mapper = MelosysEessiMeldingMapperFactory.getMapper(SedType.A011);
        assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA011.class);
    }
}