package no.nav.melosys.eessi.models.buc;


import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
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

    @Test
    void kanLukkesAutomatisk_LABUC06_30dagerSidenA006() {
        BUC buc06 = new BUC();
        buc06.setBucType(BucType.LA_BUC_06.name());
        buc06.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc06.setDocuments(Collections.singletonList(
            createDocument(SedType.A006, SedStatus.MOTTATT, 30, "")
        ));

        assertThat(buc06.kanLukkesAutomatisk()).isTrue();
    }

    @Test
    void kanIkkeLukkesAutomatisk_LABUC06_30dagerSidenA006OperationREAD() {
        BUC buc06 = new BUC();
        buc06.setBucType(BucType.LA_BUC_06.name());
        buc06.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "READ")));
        buc06.setDocuments(Collections.singletonList(
            createDocument(SedType.A006, SedStatus.MOTTATT, 30, "")
        ));

        assertThat(buc06.kanLukkesAutomatisk()).isFalse();
    }

    @Test
    void kanLukkesAutomatisk_LABUC01_60dagerSidenA011A002() {
        BUC buc01 = new BUC();
        buc01.setBucType(BucType.LA_BUC_01.name());
        buc01.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc01.setDocuments(Arrays.asList(
            createDocument(SedType.A002, SedStatus.MOTTATT, 60, "IN"),
            createDocument(SedType.A011, SedStatus.MOTTATT, 60, "IN")
        ));

        assertThat(buc01.kanLukkesAutomatisk()).isTrue();
    }

    @Test
    void kanIkkeLukkesAutomatisk_LABUC03_20dagerSidenX013() {
        BUC buc03 = new BUC();
        buc03.setBucType(BucType.LA_BUC_03.name());
        buc03.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc03.setDocuments(Arrays.asList(
            createDocument(SedType.X012, SedStatus.MOTTATT, 30, ""),
            createDocument(SedType.X013, SedStatus.SENDT, 20, ""),
            createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
        ));

        assertThat(buc03.kanLukkesAutomatisk()).isFalse();
    }

    @Test
    void kanIkkeLukkesAutomatisk_LABUC03_20dagerSidenX012() {
        BUC buc03 = new BUC();
        buc03.setBucType(BucType.LA_BUC_03.name());
        buc03.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc03.setDocuments(Arrays.asList(
            createDocument(SedType.X012, SedStatus.MOTTATT, 20, ""),
            createDocument(SedType.X013, SedStatus.SENDT, 30, ""),
            createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
        ));

        assertThat(buc03.kanLukkesAutomatisk()).isFalse();
    }

    @Test
    void kanIkkeLukkesAutomatisk_LABUC03_30dagerSidenX013og25dagerSidenA008() {
        BUC buc03 = new BUC();
        buc03.setBucType(BucType.LA_BUC_03.name());
        buc03.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc03.setDocuments(Arrays.asList(
            createDocument(SedType.X012, SedStatus.MOTTATT, 20, ""),
            createDocument(SedType.X013, SedStatus.SENDT, 30, ""),
            createDocument(SedType.A008, SedStatus.SENDT, 25, "IN")
        ));

        assertThat(buc03.kanLukkesAutomatisk()).isFalse();
    }

    @Test
    void kanIkkeLukkesAutomatisk_LABUC03_30dagerSidenA008ogX01300dagerSidenX012() {
        BUC buc03 = new BUC();
        buc03.setBucType(BucType.LA_BUC_03.name());
        buc03.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc03.setDocuments(Arrays.asList(
            createDocument(SedType.X012, SedStatus.MOTTATT, 10, ""),
            createDocument(SedType.X013, SedStatus.SENDT, 30, ""),
            createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
        ));
        assertThat(buc03.kanLukkesAutomatisk()).isFalse();
    }

    @Test
    void kanIkkeLukkesAutomatisk_LABUC03_30dagerSidenA008ogX01210dagerSidenX013() {
        BUC buc03 = new BUC();
        buc03.setBucType(BucType.LA_BUC_03.name());
        buc03.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc03.setDocuments(Arrays.asList(
            createDocument(SedType.X012, SedStatus.MOTTATT, 30, ""),
            createDocument(SedType.X013, SedStatus.SENDT, 10, ""),
            createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
        ));
        assertThat(buc03.kanLukkesAutomatisk()).isFalse();
    }

    @Test
    void kanLukkesAutomatisk_LABUC03_30dagerSidenA008ogX012() {
        BUC buc03 = new BUC();
        buc03.setBucType(BucType.LA_BUC_03.name());
        buc03.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc03.setDocuments(Arrays.asList(
            createDocument(SedType.X012, SedStatus.MOTTATT, 30, ""),
            createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
        ));
        assertThat(buc03.kanLukkesAutomatisk()).isTrue();
    }

    @Test
    void kanLukkesAutomatisk_LABUC03_30dagerSidenA008ogX013() {
        BUC buc03 = new BUC();
        buc03.setBucType(BucType.LA_BUC_03.name());
        buc03.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc03.setDocuments(Arrays.asList(
            createDocument(SedType.X013, SedStatus.MOTTATT, 30, ""),
            createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
        ));
        assertThat(buc03.kanLukkesAutomatisk()).isTrue();
    }

    @Test
    void kanLukkesAutomatisk_LABUC03_30dagerSidenA008() {
        BUC buc03 = new BUC();
        buc03.setBucType(BucType.LA_BUC_03.name());
        buc03.setActions(Collections.singletonList(new Action("name", SedType.X001.name(), "1", "UPDATE")));
        buc03.setDocuments(List.of(
            createDocument(SedType.A008, SedStatus.SENDT, 30, "IN")
        ));
        assertThat(buc03.kanLukkesAutomatisk()).isTrue();
    }


    private Document createDocument(SedType sedType, SedStatus sedStatus, long days, String direction) {
        Document document = new Document();
        document.setType(sedType.name());
        document.setStatus(sedStatus.getEngelskStatus());
        document.setLastUpdate(ZonedDateTime.now().minusDays(days).minusSeconds(1));
        document.setDirection(direction);

        return document;
    }
}
