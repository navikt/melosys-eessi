package no.nav.melosys.eessi.service.sed;

import java.util.List;
import no.nav.melosys.eessi.controller.dto.BucSedRelasjonDto;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class BucServiceTest {

    private BucService bucService = new BucService();

    @Test
    public void hentBucSedRelasjoner() {
        List<BucSedRelasjonDto> relasjoner = bucService.hentBucSedRelasjoner();

        assertThat(relasjoner)
                .extracting(
                        BucSedRelasjonDto::getBuc,
                        BucSedRelasjonDto::getForsteSed,
                        BucSedRelasjonDto::getFagomrade
                )
                .contains(
                        tuple("LA_BUC_01", "A001", "LOVVALG"),
                        tuple("LA_BUC_03", "A008", "LOVVALG"),
                        tuple("LA_BUC_06", "A005", "LOVVALG")
                );
    }
}
