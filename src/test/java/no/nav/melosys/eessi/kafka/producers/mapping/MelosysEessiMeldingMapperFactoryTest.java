package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.models.SedType;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperFactoryTest {

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
}