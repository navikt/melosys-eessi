package no.nav.melosys.eessi.service.sed;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
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

    @Test
    public void hentSedTypeFraLovvalgsBestemmelse_forventA009() {
        SedType sedType = SedUtils.getSedTypeFromBestemmelse(Bestemmelse.ART_12_1);
        assertThat(sedType).isEqualTo(SedType.A009);
        sedType = SedUtils.getSedTypeFromBestemmelse(Bestemmelse.ART_12_2);
        assertThat(sedType).isEqualTo(SedType.A009);
    }

    @Test
    public void hentBucFraLovvalgsBestemmelse_forventLABUC04() {
        BucType bucType = SedUtils.getBucTypeFromBestemmelse(Bestemmelse.ART_12_1);
        assertThat(bucType).isEqualTo(BucType.LA_BUC_04);
        bucType = SedUtils.getBucTypeFromBestemmelse(Bestemmelse.ART_12_2);
        assertThat(bucType).isEqualTo(BucType.LA_BUC_04);
    }
}