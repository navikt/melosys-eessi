package no.nav.melosys.eessi.service.caserelation;

import no.nav.melosys.eessi.models.CaseRelation;
import no.nav.melosys.eessi.repository.CaseRelationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CaseRelationServiceTest {

    @Mock
    private CaseRelationRepository caseRelationRepository;
    @InjectMocks
    private CaseRelationService caseRelationService;

    @Captor
    private ArgumentCaptor<CaseRelation> captor;

    @Test
    public void save_rinaOgGsakId_verifiserKallMotRepo() {
        String rinaCaseId = "123";
        Long gsakSaksnummer = 321L;

        caseRelationService.save(gsakSaksnummer, rinaCaseId);
        verify(caseRelationRepository).save(captor.capture());

        CaseRelation caseRelation = captor.getValue();
        assertThat(rinaCaseId).isEqualTo(caseRelation.getRinaId());
        assertThat(gsakSaksnummer).isEqualTo(caseRelation.getGsakSaksnummer());
    }

    @Test
    public void findByRinaId() {
        caseRelationService.findByRinaId("123");
        verify(caseRelationRepository).findByRinaId(eq("123"));
    }

    @Test
    public void deleteByRinaId() {
        caseRelationService.deleteByRinaId("123");
        verify(caseRelationRepository).deleteByRinaId("123");
    }

    @Test
    public void findByGsakSaksnummer() {
        caseRelationService.findByGsakSaksnummer(123L);
        verify(caseRelationRepository).findByGsakSaksnummer(123L);
    }
}