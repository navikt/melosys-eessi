package no.nav.melosys.eessi.service.sed;

import no.nav.melosys.eessi.controller.dto.Bestemmelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SedUtilsTest {

    @Test
    public void hentSedTypeFraLovvalgsBestemmelse_forventA009() {
        SedType sedType = SedUtils.getSedTypeFromBestemmelse(Bestemmelse.ART_12_1);
        assertThat(sedType, is(SedType.A009));
        sedType = SedUtils.getSedTypeFromBestemmelse(Bestemmelse.ART_12_2);
        assertThat(sedType, is(SedType.A009));
    }

    @Test
    public void hentBucFraLovvalgsBestemmelse_forventLABUC04() {
        BucType bucType = SedUtils.getBucTypeFromBestemmelse(Bestemmelse.ART_12_1);
        assertThat(bucType, is(BucType.LA_BUC_04));
        bucType = SedUtils.getBucTypeFromBestemmelse(Bestemmelse.ART_12_2);
        assertThat(bucType, is(BucType.LA_BUC_04));
    }
}