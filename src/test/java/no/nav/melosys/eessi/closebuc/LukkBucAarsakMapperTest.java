package no.nav.melosys.eessi.closebuc;

import java.util.Collections;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LukkBucAarsakMapperTest {

    private BUC buc;

    @Before
    public void setup() {
        buc = new BUC();
    }

    @Test(expected = IllegalArgumentException.class)
    public void hentAarsakForLukking_LABUC01_kasterException() {
        buc.setBucType(BucType.LA_BUC_01.name());
        LukkBucAarsakMapper.hentAarsakForLukking(buc);
    }

    @Test
    public void hentAarsakForLukking_LABUC02InneholderA009_validerTekst() {
        buc.setBucType(BucType.LA_BUC_02.name());

        Document document = new Document();
        document.setType(SedType.A012.name());
        buc.setDocuments(Collections.singletonList(document));

        String aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc);
        assertThat(aarsak).isEqualTo("gjeldende_lovgivning_det_ble_oppnådd_enighet_om_anmodningen_om_unntak");
    }

    @Test
    public void hentAarsakForLukking_LABUC02UtenA009_validerTekst() {
        buc.setBucType(BucType.LA_BUC_02.name());
        buc.setDocuments(Collections.emptyList());
        String aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc);
        assertThat(aarsak).isEqualTo("gjeldende_lovgivning_fastsettelsen_ble_endelig_ingen_reaksjon_innen_2_måneder");
    }

    @Test
    public void hentAarsakForLukking_LABUC03_validerTekst() {
        buc.setBucType(BucType.LA_BUC_03.name());
        String aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc);
        assertThat(aarsak).isEqualTo("lovvalg_30_dager_siden_melding_om_relevant_informasjon");
    }

    @Test
    public void hentAarsakForLukking_LABUC04_validerTekst() {
        buc.setBucType(BucType.LA_BUC_04.name());
        String aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc);
        assertThat(aarsak).isEqualTo("lovvalg_30_dager_siden_melding_om_utstasjonering");
    }

    @Test
    public void hentAarsakForLukking_LABUC05_validerTekst() {
        buc.setBucType(BucType.LA_BUC_05.name());
        String aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc);
        assertThat(aarsak).isEqualTo("lovvalg_30_dager_siden_melding_om_utstasjonering");
    }

    @Test
    public void hentAarsakForLukking_LABUC06_validerTekst() {
        buc.setBucType(BucType.LA_BUC_06.name());
        String aarsak = LukkBucAarsakMapper.hentAarsakForLukking(buc);
        assertThat(aarsak).isEqualTo("gjeldende_lovgivning_30_dager_siden_svar_på_anmodning_om_mer_informasjon");
    }
}