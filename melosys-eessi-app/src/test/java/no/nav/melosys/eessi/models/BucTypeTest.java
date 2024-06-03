package no.nav.melosys.eessi.models;

import org.junit.jupiter.api.Test;

import static no.nav.melosys.eessi.models.BucType.H_BUC_01;
import static org.assertj.core.api.Assertions.assertThat;

class BucTypeTest {

    @Test
    void bucType_hBucSomSkalKonsumeres_true() {
        assertThat(BucType.erHBucsomSkalKonsumeres(H_BUC_01.name())).isTrue();
    }

    @Test
    void buctype_ikkeEksisterendeBucType_false() {
        assertThat(BucType.erHBucsomSkalKonsumeres("buc")).isFalse();
    }
}
