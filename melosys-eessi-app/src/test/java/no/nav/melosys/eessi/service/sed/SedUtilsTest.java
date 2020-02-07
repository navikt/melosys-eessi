package no.nav.melosys.eessi.service.sed;

import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SedUtilsTest {

    @Test
    public void hentFørsteLovligeSedPåBuc_verifiserTyper() {
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.LA_BUC_01)).isEqualTo(SedType.A001);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.LA_BUC_02)).isEqualTo(SedType.A003);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.LA_BUC_03)).isEqualTo(SedType.A008);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.LA_BUC_04)).isEqualTo(SedType.A009);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.LA_BUC_05)).isEqualTo(SedType.A010);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.LA_BUC_06)).isEqualTo(SedType.A005);

        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_01)).isEqualTo(SedType.H001);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_02a)).isEqualTo(SedType.H005);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_02b)).isEqualTo(SedType.H004);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_02c)).isEqualTo(SedType.H003);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_03a)).isEqualTo(SedType.H010);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_03b)).isEqualTo(SedType.H011);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_04)).isEqualTo(SedType.H020);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_05)).isEqualTo(SedType.H061);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_06)).isEqualTo(SedType.H065);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_07)).isEqualTo(SedType.H070);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_08)).isEqualTo(SedType.H120);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_09)).isEqualTo(SedType.H121);
        assertThat(SedUtils.hentFørsteLovligeSedPåBuc(BucType.H_BUC_10)).isEqualTo(SedType.H130);
    }
}