package no.nav.melosys.eessi.closebuc;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.Action;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Conversation;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.service.eux.BucSearch;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucLukkerTest {

    @Mock
    private EuxService euxService;
    @Mock
    private BucMetrikker bucMetrikker;

    private BucLukker bucLukker;

    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    @BeforeEach
    public void setup() {
        bucLukker = new BucLukker(euxService, bucMetrikker);
    }

    @Test
    void lukkBucerAvType_enBucKanLukkes_verifiserOpprettOgSend() {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");
        bucInfo.setStatus("open");

        List<BucInfo> bucInfos = new ArrayList<>();
        bucInfos.add(bucInfo);

        BUC buc = lagBuc();

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(bucInfos);
        when(euxService.hentBuc(eq(bucInfo.getId()))).thenReturn(buc);

        SED sed = new SED();
        sed.setNav(enhancedRandom.nextObject(Nav.class));
        sed.setMedlemskap(enhancedRandom.nextObject(MedlemskapA009.class));
        when(euxService.hentSed(anyString(), anyString())).thenReturn(sed);

        bucLukker.lukkBucerAvType(BucType.LA_BUC_04);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(buc.getDocuments().get(0).getId()));
        verify(euxService).opprettOgSendSed(any(SED.class), eq(buc.getId()));
    }

    @Test
    void lukkBucerAvType_enBucKanLukkesInneholderUtkastX001_verifiserOppdaterSåSend() {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");
        bucInfo.setStatus("open");

        List<BucInfo> bucInfos = new ArrayList<>();
        bucInfos.add(bucInfo);

        BUC buc = lagBuc();

        Document x001Doc = new Document();
        x001Doc.setType(SedType.X001.name());
        x001Doc.setCreationDate(ZonedDateTime.now());
        x001Doc.setConversations(Collections.emptyList());
        x001Doc.setStatus("draft");
        buc.getDocuments().add(x001Doc);

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(bucInfos);
        when(euxService.hentBuc(eq(bucInfo.getId()))).thenReturn(buc);

        SED sed = new SED();
        sed.setNav(enhancedRandom.nextObject(Nav.class));
        sed.setMedlemskap(enhancedRandom.nextObject(MedlemskapA009.class));
        when(euxService.hentSed(anyString(), anyString())).thenReturn(sed);

        bucLukker.lukkBucerAvType(BucType.LA_BUC_04);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(buc.getDocuments().get(0).getId()));
        verify(euxService).oppdaterSed(eq(buc.getId()), eq(x001Doc.getId()), any(SED.class));
        verify(euxService).sendSed(eq(buc.getId()), eq(x001Doc.getId()));
    }

    @Test
    void lukkBucerAvType_feilVedHentingAvBucer_ingenVidereKall() {
        when(euxService.hentBucer(any(BucSearch.class))).thenThrow(new IntegrationException(""));
        bucLukker.lukkBucerAvType(BucType.LA_BUC_04);
        verify(euxService, never()).hentBuc(anyString());
    }

    @Test
    void lukkBucerAvType_feilVedHentingAvBuc_ingenVidereKall() {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");
        bucInfo.setStatus("open");

        List<BucInfo> bucInfos = new ArrayList<>();
        bucInfos.add(bucInfo);

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(bucInfos);
        when(euxService.hentBuc(any())).thenThrow(new IntegrationException(""));

        bucLukker.lukkBucerAvType(BucType.LA_BUC_04);

        verify(euxService).hentBuc(anyString());
        verify(euxService, never()).hentSed(anyString(), anyString());
    }

    @Test
    void lukkBucerAvType_feilVedHentingAvSed_ingenVidereKall() {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");
        bucInfo.setStatus("open");

        List<BucInfo> bucInfos = new ArrayList<>();
        bucInfos.add(bucInfo);

        BUC buc = lagBuc();

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(bucInfos);
        when(euxService.hentBuc(eq(bucInfo.getId()))).thenReturn(buc);
        when(euxService.hentSed(anyString(), anyString())).thenThrow(new IntegrationException(""));

        bucLukker.lukkBucerAvType(BucType.LA_BUC_04);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(buc.getDocuments().get(0).getId()));
        verify(euxService, never()).opprettOgSendSed(any(), any());
    }

    @Test
    void lukkBucerAvType_toDokumenter_brukSistOpprettetDokument() {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");
        bucInfo.setStatus("open");

        List<BucInfo> bucInfos = new ArrayList<>();
        bucInfos.add(bucInfo);

        BUC buc = lagBuc();

        String sisteOppdatertDokumentId = buc.getDocuments().get(0).getId();

        Document document = new Document();
        document.setType(SedType.A008.name());
        document.setCreationDate(ZonedDateTime.now().minusDays(1L));
        document.setStatus("sent");
        document.setId("rrrr");
        document.setConversations(Collections.emptyList());
        buc.getDocuments().add(document);

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(bucInfos);
        when(euxService.hentBuc(eq(bucInfo.getId()))).thenReturn(buc);

        SED sed = new SED();
        sed.setNav(enhancedRandom.nextObject(Nav.class));
        sed.setMedlemskap(enhancedRandom.nextObject(MedlemskapA009.class));
        when(euxService.hentSed(anyString(), anyString())).thenThrow(new IntegrationException(""));

        bucLukker.lukkBucerAvType(BucType.LA_BUC_04);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(sisteOppdatertDokumentId));
        verify(euxService, never()).opprettOgSendSed(any(), any());
    }

    @Test
    void lukkBucerAvType_statusClosed_ingenBlirLukket() {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setStatus("closed");

        when(euxService.hentBucer(any())).thenReturn(List.of(bucInfo));
        bucLukker.lukkBucerAvType(BucType.LA_BUC_02);
        verify(euxService, never()).hentBuc(any());
    }

    @Test
    void lukkBucerAvType_LABUC06ToMndSidenMottattA006_lukkes() throws IntegrationException {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");
        bucInfo.setStatus("open");

        BUC buc = lagBuc(BucType.LA_BUC_06, SedType.A005);

        String sisteOppdatertDokumentId = buc.getDocuments().get(0).getId();

        Document document = new Document();
        document.setType(SedType.A006.name());
        document.setLastUpdate(ZonedDateTime.now().minusMonths(2));
        document.setStatus(SedStatus.MOTTATT.getEngelskStatus());
        document.setId("mottattA006-123");
        document.setConversations(Collections.emptyList());
        buc.getDocuments().add(document);

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(List.of(bucInfo));
        when(euxService.hentBuc(eq(bucInfo.getId()))).thenReturn(buc);

        SED sed = new SED();
        sed.setNav(enhancedRandom.nextObject(Nav.class));
        sed.setMedlemskap(enhancedRandom.nextObject(MedlemskapA009.class));
        when(euxService.hentSed(anyString(), anyString())).thenReturn(sed);

        bucLukker.lukkBucerAvType(BucType.LA_BUC_06);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(sisteOppdatertDokumentId));
        verify(euxService).opprettOgSendSed(any(), eq(buc.getId()));
    }

    @Test
    void lukkBucerAvType_LABUC06A006IkkeMottatt_lukkesIkke() throws IntegrationException {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");
        bucInfo.setStatus("open");

        BUC buc = lagBuc(BucType.LA_BUC_06, SedType.A005);

        String sisteOppdatertDokumentId = buc.getDocuments().get(0).getId();

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(List.of(bucInfo));
        when(euxService.hentBuc(eq(bucInfo.getId()))).thenReturn(buc);

        bucLukker.lukkBucerAvType(BucType.LA_BUC_06);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService, never()).hentSed(eq(buc.getId()), eq(sisteOppdatertDokumentId));
        verify(euxService, never()).opprettOgSendSed(any(), eq(buc.getId()));
    }

    private BUC lagBuc() {
        return lagBuc(BucType.LA_BUC_04, SedType.A009);
    }

    private BUC lagBuc(BucType bucType, SedType sedType) {
        BUC buc = new BUC();
        buc.setId("ffff");
        buc.setBucType(bucType.name());

        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setDocumentType(SedType.X001.name());
        action.setOperation("create");
        actions.add(action);
        buc.setActions(actions);

        Conversation conversation = new Conversation();
        conversation.setVersionId("1");

        List<Document> documents = new ArrayList<>();
        Document document = new Document();
        document.setType(sedType.name());
        document.setCreationDate(ZonedDateTime.now());
        document.setStatus("sent");
        document.setConversations(Collections.singletonList(conversation));
        document.setId("gjrieogroei");
        documents.add(document);

        buc.setDocuments(documents);
        buc.setBucVersjon("4.1");

        return buc;
    }
}
