package no.nav.melosys.eessi.models.buc;


import java.time.ZonedDateTime;

import com.google.common.collect.Lists;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BUCTest {

    @Test
    void hentSistOppdaterteDocument() {
        BUC buc = new BUC();

        Document document1 = new Document();
        document1.setStatus(SedStatus.MOTTATT.getEngelskStatus());
        document1.setLastUpdate(ZonedDateTime.now());

        Document document2 = new Document();
        document2.setStatus(SedStatus.SENDT.getEngelskStatus());
        document2.setLastUpdate(ZonedDateTime.now().plusWeeks(1));

        buc.setDocuments(Lists.newArrayList(document1, document2));
        assertThat(buc.hentSistOppdaterteDocument()).contains(document2);
    }

}
