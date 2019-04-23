package no.nav.melosys.eessi.kafka.producers.mapping;

import no.nav.melosys.eessi.models.sed.SedType;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MelosysEessiMeldingMapperTest {

    private MelosysEessiMeldingMapperFactory factory = new MelosysEessiMeldingMapperFactory();

    @Test
    public void mapA009_fastPeriode_verifiserPeriode() {
        MelosysEessiMeldingMapper mapper = factory.getMapper(SedType.A009);
        assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA009.class);
    }

    @Test
    public void hentA010Mapper() {
        MelosysEessiMeldingMapper mapper = factory.getMapper(SedType.A010);
        assertThat(mapper).isInstanceOf(MelosysEessiMeldingMapperA010.class);
    }
}