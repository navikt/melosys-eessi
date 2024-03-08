package no.nav.melosys.eessi.service.journalpostkobling;

import java.util.Collections;
import java.util.Optional;

import io.getunleash.FakeUnleash;
import io.getunleash.Unleash;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.controller.dto.SedStatus;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.integration.saf.SafConsumer;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.JournalpostSedKobling;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Creator;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.buc.Organisation;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.models.sed.nav.Nav;
import no.nav.melosys.eessi.repository.JournalpostSedKoblingRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalpostSedKoblingServiceTest {
    @Mock
    private JournalpostSedKoblingRepository journalpostSedKoblingRepository;
    @Mock
    private CaseStoreConsumer caseStoreConsumer;
    @Mock
    private EuxService euxService;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private SafConsumer safConsumer;

    private final MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory = new MelosysEessiMeldingMapperFactory("dummy");

    private JournalpostSedKoblingService journalpostSedKoblingService;

    private BUC buc;
    private SED sed;
    private Document document;
    private Organisation organisation;
    private JournalpostSedKobling journalpostSedKobling;

    @BeforeEach
    public void setup() {
        journalpostSedKoblingService = new JournalpostSedKoblingService(
                journalpostSedKoblingRepository, caseStoreConsumer, euxService, saksrelasjonService,
                safConsumer, melosysEessiMeldingMapperFactory, new FakeUnleash());

        EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();
        buc = enhancedRandom.nextObject(BUC.class);
        document = buc.getDocuments().get(0);
        document.setId("sedID");
        document.setType("A008");
        document.setStatus(SedStatus.MOTTATT.getEngelskStatus());
        Creator creator = new Creator();
        organisation = new Organisation("org1", "DK", "mnb");
        creator.setOrganisation(organisation);
        document.setCreator(creator);
        buc.setDocuments(Collections.singletonList(document));
        sed = new SED();
        sed.setNav(new Nav());
        journalpostSedKobling = new JournalpostSedKobling("123", "321", "sedID", "1", "LA_BUC_03", "A008");
    }

    @Test
    void finnVedJournalpostIDOpprettMelosysEessiMelding_sakEksistererIDB_forventMelosysEessiMelding() {
        when(journalpostSedKoblingRepository.findByJournalpostID(anyString()))
                .thenReturn(Optional.of(journalpostSedKobling));
        when(euxService.hentBuc(anyString())).thenReturn(buc);
        when(euxService.hentSed(anyString(), anyString())).thenReturn(sed);

        Optional<MelosysEessiMelding> melosysEessiMelding = journalpostSedKoblingService
                .finnVedJournalpostIDOpprettMelosysEessiMelding("123");

        assertThat(melosysEessiMelding).isPresent();
        assertThat(melosysEessiMelding.get().getSedType()).isEqualTo("A008");
        assertThat(melosysEessiMelding.get().getAvsender().getAvsenderID()).isEqualTo(organisation.getId());
    }

    @Test
    void finnVedJournalpostIDOpprettMelosysEessiMelding_sakEksistererIEuxCaseStore_forventMelosysEessiMelding() {
        when(journalpostSedKoblingRepository.findByJournalpostID(anyString()))
                .thenReturn(Optional.empty());
        when(caseStoreConsumer.finnVedJournalpostID(anyString()))
                .thenReturn(Collections.singletonList(new CaseStoreDto("123", "321")));
        when(euxService.hentBuc(anyString())).thenReturn(buc);
        when(euxService.hentSed(anyString(), anyString())).thenReturn(sed);

        Optional<MelosysEessiMelding> melosysEessiMelding = journalpostSedKoblingService
                .finnVedJournalpostIDOpprettMelosysEessiMelding("123");

        assertThat(melosysEessiMelding).isPresent();
        assertThat(melosysEessiMelding.get().getSedType()).isEqualTo(document.getType());
        assertThat(melosysEessiMelding.get().getAvsender().getAvsenderID()).isEqualTo(organisation.getId());
    }
}
