package no.nav.melosys.eessi.closebuc;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BucCloserTest {

    @Mock
    private EuxService euxService;
    @Mock
    private BucMetrikker bucMetrikker;

    private BucCloser bucCloser;

    private EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    @Before
    public void setup() {
        bucCloser = new BucCloser(euxService, bucMetrikker);
    }

    @Test
    public void closeBucsByType_enBucKanLukkes_verifiserOpprettOgSend() throws Exception {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");

        List<BucInfo> bucInfos = new ArrayList<>();
        bucInfos.add(bucInfo);

        BUC buc = lagBuc();

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(bucInfos);
        when(euxService.hentBuc(eq(bucInfo.getId()))).thenReturn(buc);

        SED sed = new SED();
        sed.setNav(enhancedRandom.nextObject(Nav.class));
        sed.setMedlemskap(enhancedRandom.nextObject(MedlemskapA009.class));
        when(euxService.hentSed(anyString(), anyString())).thenReturn(sed);

        bucCloser.closeBucsByType(BucType.LA_BUC_04);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(buc.getDocuments().get(0).getId()));
        verify(euxService).opprettOgSendSed(any(SED.class), eq(buc.getId()));
    }

    @Test
    public void closeBucsByType_enBucKanLukkesInneholderUtkastX001_verifiserOppdaterSåSend() throws Exception {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");

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

        bucCloser.closeBucsByType(BucType.LA_BUC_04);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(buc.getDocuments().get(0).getId()));
        verify(euxService).oppdaterSed(eq(buc.getId()), eq(x001Doc.getId()), any(SED.class));
        verify(euxService).sendSed(eq(buc.getId()), eq(x001Doc.getId()));
    }

    @Test
    public void closeBucsByType_feilVedHentingAvBucer_ingenVidereKall() throws Exception {
        when(euxService.hentBucer(any(BucSearch.class))).thenThrow(new IntegrationException(""));
        bucCloser.closeBucsByType(BucType.LA_BUC_04);
        verify(euxService, never()).hentBuc(anyString());
    }

    @Test
    public void closeBucsByType_feilVedHentingAvBuc_ingenVidereKall() throws Exception {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");

        List<BucInfo> bucInfos = new ArrayList<>();
        bucInfos.add(bucInfo);

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(bucInfos);
        when(euxService.hentBuc(any())).thenThrow(new IntegrationException(""));

        bucCloser.closeBucsByType(BucType.LA_BUC_04);

        verify(euxService).hentBuc(anyString());
        verify(euxService, never()).hentSed(anyString(), anyString());
    }

    @Test
    public void closeBucsByType_feilVedHentingAvSed_ingenVidereKall() throws Exception {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");

        List<BucInfo> bucInfos = new ArrayList<>();
        bucInfos.add(bucInfo);

        BUC buc = lagBuc();

        when(euxService.hentBucer(any(BucSearch.class))).thenReturn(bucInfos);
        when(euxService.hentBuc(eq(bucInfo.getId()))).thenReturn(buc);

        SED sed = new SED();
        sed.setNav(enhancedRandom.nextObject(Nav.class));
        sed.setMedlemskap(enhancedRandom.nextObject(MedlemskapA009.class));
        when(euxService.hentSed(anyString(), anyString())).thenThrow(new IntegrationException(""));

        bucCloser.closeBucsByType(BucType.LA_BUC_04);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(buc.getDocuments().get(0).getId()));
        verify(euxService, never()).opprettOgSendSed(any(), any());
    }

    @Test
    public void closeBucsByType_toDokumenter_brukSistOpprettetDokument() throws Exception {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");
        bucInfo.setApplicationRoleId("PO");

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

        bucCloser.closeBucsByType(BucType.LA_BUC_04);

        verify(euxService).hentBucer(any(BucSearch.class));
        verify(euxService).hentBuc(bucInfo.getId());
        verify(euxService).hentSed(eq(buc.getId()), eq(sisteOppdatertDokumentId));
        verify(euxService, never()).opprettOgSendSed(any(), any());
    }

    private BUC lagBuc() {
        BUC buc = new BUC();
        buc.setId("ffff");
        buc.setBucType(BucType.LA_BUC_04.name());

        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setDocumentType(SedType.X001.name());
        actions.add(action);
        buc.setActions(actions);

        Conversation conversation = new Conversation();
        conversation.setVersionId("1");

        List<Document> documents = new ArrayList<>();
        Document document = new Document();
        document.setType(SedType.A009.name());
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
