package no.nav.melosys.eessi.kafka.producers;


import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.avro.MelosysEessiMelding;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.service.joark.SakInformasjon;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;

public class MelosysEessiMeldingMapperTest {

    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    @Test
    public void testMapping_sedA009_expectValidEessiMelding() {
        MelosysEessiMelding res = MelosysEessiMeldingMapper.map("123",
                createSed(SedType.A009), new SedHendelse(), SakInformasjon.builder().gsakSaksnummer("123321").build());

        assertThat(res, not(nullValue()));
        assertThat(res.getPeriode(), not(nullValue()));
        assertThat(res.getAktoerId(), not(nullValue()));
        assertThat(res.getArtikkel(), not(nullValue()));
        assertThat(res.getGsakSaksnummer(), not(nullValue()));
    }

    private SED createSed(SedType sedType) {
        SED sed = new SED();
        sed.setSed(sedType.name());
        sed.setNav(enhancedRandom.nextObject(Nav.class));
        sed.setMedlemskap(enhancedRandom.nextObject(MedlemskapA009.class));
        return sed;
    }
}