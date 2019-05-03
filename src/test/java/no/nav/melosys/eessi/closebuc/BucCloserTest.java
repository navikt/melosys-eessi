package no.nav.melosys.eessi.closebuc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.models.buc.*;
import no.nav.melosys.eessi.models.bucinfo.BucInfo;
import no.nav.melosys.eessi.models.sed.BucType;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.SedType;
import no.nav.melosys.eessi.models.sed.medlemskap.impl.MedlemskapA009;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.service.eux.BucSearch;
import no.nav.melosys.eessi.service.eux.EuxService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BucCloserTest {
    @Mock
    private EuxService euxService;
    @InjectMocks
    private BucCloser bucCloser;

    private EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();


    @Test
    public void closeBucsByType_ingenKanLukkes_ingenEuxKall() throws Exception {
        BucInfo bucInfo = new BucInfo();
        bucInfo.setId("123jfpw");

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

    private BUC lagBuc() {
        BUC buc = new BUC();
        buc.setId("ffff");
        buc.setBucType("LA_BUC_04");

        Creator creator = new Creator();
        creator.setOrganisation(new Organisation());
        creator.getOrganisation().setCountryCode("NO");
        buc.setCreator(creator);

        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setDocumentType(SedType.X001.name());
        actions.add(action);
        buc.setActions(actions);

        List<Document> documents = new ArrayList<>();
        Document document = new Document();
        document.setType(SedType.A009.name());
        document.setCreationDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        document.setStatus("sent");
        document.setId("gjrieogroei");
        documents.add(document);

        buc.setDocuments(documents);

        return buc;
    }
}