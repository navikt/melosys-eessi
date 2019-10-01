package no.nav.melosys.eessi.service.sed;

import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SedUtilsTest {

    @Test
    public void hentFoersteLovligeSedPaaBuc_verifiserTyper() {
        assertThat(SedUtils.hentFoersteLovligeSedPaaBuc(BucType.LA_BUC_01)).isEqualTo(SedType.A001);
        assertThat(SedUtils.hentFoersteLovligeSedPaaBuc(BucType.LA_BUC_02)).isEqualTo(SedType.A003);
        assertThat(SedUtils.hentFoersteLovligeSedPaaBuc(BucType.LA_BUC_03)).isEqualTo(SedType.A008);
        assertThat(SedUtils.hentFoersteLovligeSedPaaBuc(BucType.LA_BUC_04)).isEqualTo(SedType.A009);
        assertThat(SedUtils.hentFoersteLovligeSedPaaBuc(BucType.LA_BUC_05)).isEqualTo(SedType.A010);
        assertThat(SedUtils.hentFoersteLovligeSedPaaBuc(BucType.LA_BUC_06)).isEqualTo(SedType.A005);
    }
}