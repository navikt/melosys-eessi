package no.nav.melosys.eessi.service.eux;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import no.nav.melosys.eessi.integration.eux.rina_api.Aksjoner;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.EuxRinasakerConsumer;
import no.nav.melosys.eessi.integration.eux.rina_api.dto.Institusjon;
import no.nav.melosys.eessi.metrikker.BucMetrikker;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.SedVedlegg;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Conversation;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.ValidationException;
import no.nav.melosys.eessi.models.sed.SED;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EuxServiceTest {

    @Mock
    private EuxConsumer euxConsumer;

    @Mock
    private EuxRinasakerConsumer euxRinasakerConsumer;
    @Mock
    private BucMetrikker bucMetrikker;

    private EuxService euxService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final String opprettetBucID = "114433";
    private final String opprettetSedID = "12222";

    @BeforeEach
    public void setup() throws IOException, IntegrationException {
        euxService = new EuxService(euxConsumer, bucMetrikker, euxRinasakerConsumer);
    }

    @Test
    void hentSed_forventKonsumentKall() {
        euxService.hentSed("123123123", "12345");
        verify(euxConsumer).hentSed("123123123", "12345");
    }

    @Test
    void hentBucer_forventKonsumentKall() {
        BucSearch bucSearch = BucSearch.builder()
            .bucType(BucType.LA_BUC_01.name())
            .build();

        euxService.hentBucer(bucSearch);

        verify(euxConsumer).finnRinaSaker(eq(BucType.LA_BUC_01.name()), isNull());
    }

    @Test
    void hentBuc_forventKonsumentKall() {
        euxService.hentBuc("123123123");

        verify(euxConsumer).hentBUC("123123123");
    }

    @Test
    void finnBuc_integrasjonsfeil_tomRespons() {
        when(euxConsumer.hentBUC("123123123")).thenThrow(new IntegrationException("err"));
        assertThat(euxService.finnBUC("123123123")).isEmpty();
    }

    @Test
    void genererPdfFraSed_forventKonsumentkall() {
        euxService.genererPdfFraSed(new SED());
        verify(euxConsumer).genererPdfFraSed(any());
    }

    @Test
    void opprettBucOgSed_forventRinaSaksnummer() {
        when(euxConsumer.opprettBUC(anyString())).thenReturn(opprettetBucID);
        when(euxConsumer.opprettSed(eq(opprettetBucID), any(SED.class))).thenReturn(opprettetSedID);

        BucType bucType = BucType.LA_BUC_01;
        Collection<String> mottakere = List.of("SE:123");
        SED sed = new SED();
        var vedlegg = Set.of(new SedVedlegg("filen min", "pdf".getBytes()));

        OpprettBucOgSedResponse opprettBucOgSedResponse = euxService.opprettBucOgSed(bucType, mottakere, sed, vedlegg);

        verify(euxConsumer).opprettBUC(bucType.name());
        verify(euxConsumer).opprettSed(opprettetBucID, sed);
        verify(euxConsumer).leggTilVedlegg(eq(opprettetBucID), eq(opprettetSedID), eq("pdf"), any(SedVedlegg.class));

        assertThat(opprettBucOgSedResponse.getRinaSaksnummer()).isEqualTo(opprettetBucID);
    }

    @Test
    void opprettOgSendSed_medRinaSaksnummer_forventKonsumentKall() {
        when(euxConsumer.opprettSed(eq(opprettetBucID), any(SED.class))).thenReturn(opprettetSedID);
        when(euxConsumer.hentBucHandlinger(opprettetBucID))
            .thenReturn(List.of("SedId SedType " + Aksjoner.CREATE.hentHandling()
            ));
        when(euxConsumer.hentSedHandlinger(opprettetBucID, opprettetSedID))
            .thenReturn(Collections.singleton(Aksjoner.SEND.hentHandling()));

        SED sed = new SED();
        euxService.opprettOgSendSed(sed, opprettetBucID);

        verify(euxConsumer).sendSed(opprettetBucID, opprettetSedID);
    }

    @Test
    void opprettOgSendSedMedHandlingSjekk_medRinaSaksnummer_forventKonsumentKall() {
        when(euxConsumer.opprettSed(eq(opprettetBucID), any(SED.class))).thenReturn(opprettetSedID);
        when(euxConsumer.hentBucHandlinger(opprettetBucID))
            .thenReturn(List.of("SedId SedType " + Aksjoner.CREATE.hentHandling()
            ));
        when(euxConsumer.hentSedHandlinger(opprettetBucID, opprettetSedID))
            .thenReturn(Collections.singleton(Aksjoner.SEND.hentHandling()));

        SED sed = new SED();
        euxService.opprettOgSendSed(sed, opprettetBucID);

        verify(euxConsumer).sendSed(opprettetBucID, opprettetSedID);
    }

    @Test
    void opprettOgSendSedMedHandlingSjekk_medIngenMuligBucHandlingCreate_forventKasterException() {
        when(euxConsumer.hentBucHandlinger(opprettetBucID))
            .thenReturn(List.of("SedId SedType " + Aksjoner.READ.hentHandling()
            ));

        SED sed = new SED();

        assertThatExceptionOfType(ValidationException.class)
            .isThrownBy(() -> euxService.opprettOgSendSed(sed, opprettetBucID))
            .withMessageContaining(String.format("Kan ikke gjøre handling %s på BUC %s, ugyldig handling i Rina",
                Aksjoner.CREATE.hentHandling()
                , opprettetBucID));
    }

    @Test
    void opprettOgSendSedMedHandlingSjekk_medTomListeBucHandling_forventKasterException() {
        when(euxConsumer.hentBucHandlinger(opprettetBucID))
            .thenReturn(List.of("SedId SedType " + Aksjoner.READ.hentHandling()
            ));

        SED sed = new SED();

        assertThatExceptionOfType(ValidationException.class)
            .isThrownBy(() -> euxService.opprettOgSendSed(sed, opprettetBucID))
            .withMessageContaining(String.format("Kan ikke gjøre handling %s på BUC %s, ugyldig handling i Rina",
                Aksjoner.CREATE.hentHandling()
                , opprettetBucID));
    }

    @Test
    void opprettOgSendSedMedHandlingSjekk_medTomtSedHandling_forventKasterException() {
        when(euxConsumer.opprettSed(eq(opprettetBucID), any(SED.class))).thenReturn(opprettetSedID);
        when(euxConsumer.hentBucHandlinger(opprettetBucID))
            .thenReturn(List.of("SedId SedType " + Aksjoner.CREATE.hentHandling()
            ));

        when(euxConsumer.hentSedHandlinger(opprettetBucID, opprettetSedID))
            .thenReturn(Collections.emptyList());

        SED sed = new SED();

        assertThatExceptionOfType(ValidationException.class)
            .isThrownBy(() -> euxService.opprettOgSendSed(sed, opprettetBucID))
            .withMessageContaining(String.format("Kan ikke sende SED på BUC %s, ugyldig handling %s i Rina",
                opprettetBucID,
                Aksjoner.SEND.hentHandling()));
    }


    @Test
    void opprettOgSendSedMedHandlingSjekk_medUgyldigSedHandling_kasterIntegrationException() {
        when(euxConsumer.opprettSed(eq(opprettetBucID), any(SED.class))).thenReturn(opprettetSedID);
        when(euxConsumer.hentBucHandlinger(opprettetBucID))
            .thenReturn(List.of("SedID Sedtype " + Aksjoner.CREATE.hentHandling()
            ));

        when(euxConsumer.hentSedHandlinger(opprettetBucID, opprettetSedID))
            .thenReturn(Collections.singleton(Aksjoner.READ.hentHandling()));
        SED sed = new SED();

        assertThatExceptionOfType(ValidationException.class)
            .isThrownBy(() -> euxService.opprettOgSendSed(sed, opprettetBucID))
            .withMessageContaining(String.format("Kan ikke sende SED på BUC %s, ugyldig handling %s i Rina",
                opprettetBucID,
                Aksjoner.SEND.hentHandling()));

    }

    @Test
    void opprettOgSendSedMedHandlingSjekk_medFlereHandlinger_forventKonsumentKall() {
        when(euxConsumer.opprettSed(eq(opprettetBucID), any(SED.class))).thenReturn(opprettetSedID);
        when(euxConsumer.hentBucHandlinger(opprettetBucID))
            .thenReturn(List.of("test test " + Aksjoner.CREATE.hentHandling()
            ));
        when(euxConsumer.hentSedHandlinger(opprettetBucID, opprettetSedID))
            .thenReturn(List.of(Aksjoner.CLOSE.hentHandling(),
                Aksjoner.SEND.hentHandling(),
                Aksjoner.SEND.hentHandling())
            );

        SED sed = new SED();
        euxService.opprettOgSendSed(sed, opprettetBucID);

        verify(euxConsumer).sendSed(opprettetBucID, opprettetSedID);

    }

    @Test
    void hentRinaUrl_medSaksnummer_verifiserEuxConsumerKall() {
        euxService.hentRinaUrl("1234");
        verify(euxConsumer).hentRinaUrl("1234");
    }

    @Test
    void hentRinaUrl_medRinaSaksnummer_forventUrl() {
        String rinaSak = "12345";
        String RINA_MOCK_URL = "https://rina-host-url.local";
        when(euxConsumer.hentRinaUrl(rinaSak)).thenReturn(RINA_MOCK_URL + "/portal/#/caseManagement/" + rinaSak);

        String expectedUrl = RINA_MOCK_URL + "/portal/#/caseManagement/" + rinaSak;
        String resultUrl = euxService.hentRinaUrl(rinaSak);

        assertThat(resultUrl).isEqualTo(expectedUrl);
    }

    @Test
    void hentRinaUrl_utenRinaSaksnummer_forventException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> euxService.hentRinaUrl(null))
            .withMessageContaining("Trenger rina-saksnummer");
    }

    @Test
    void sendSed_forventKonsumentKall() {
        String rinaSaksnummer = "123";
        String dokumentId = "332211";
        String sedtype = SedType.A003.name();
        when(euxConsumer.hentSedHandlinger(rinaSaksnummer, dokumentId))
            .thenReturn(Collections.singleton(Aksjoner.SEND.hentHandling()));

        euxService.sendSed(rinaSaksnummer, dokumentId, sedtype);


        verify(euxConsumer).sendSed(rinaSaksnummer, dokumentId);
    }

    @Test
    void hentMottakerinstitusjoner_laBuc04LandSverige_forventEnInstitusjon() {
        mockInstitusjoner();
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner(BucType.LA_BUC_04.name(), List.of("SE"));
        assertThat(institusjoner).hasSize(1);
        assertThat(institusjoner.get(0).getAkronym()).isEqualTo("FK Sverige-TS70");
        verify(euxConsumer).hentInstitusjoner(BucType.LA_BUC_04.name(), null);
    }

    @Test
    void hentMottakerinstitusjoner_sBuc18LandSverige_forventIngenInstitusjoner() {
        mockInstitusjoner();
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner("S_BUC_24", List.of("SE"));
        assertThat(institusjoner).isEmpty();
    }

    @Test
    void hentMottakerinstitusjoner_laBuc04LandGB_forventEnInstitusjon() {
        mockInstitusjoner();

        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner("LA_BUC_04", List.of("GB"));
        assertThat(institusjoner).hasSize(1);

        Institusjon institusjon = institusjoner.get(0);
        assertThat(institusjon.getAkronym()).isEqualTo("FK UK-TITTEI");
        assertThat(institusjon.getLandkode()).isEqualTo("GB");
    }

    @Test
    void hentMottakerinstitusjoner_laBuc04LandGR_forventEnInstitusjon() {
        mockInstitusjoner();
        List<Institusjon> institusjoner = euxService.hentMottakerinstitusjoner("LA_BUC_04", List.of("GR"));
        assertThat(institusjoner).hasSize(1);

        Institusjon institusjon = institusjoner.get(0);
        assertThat(institusjon.getAkronym()).isEqualTo("FK EL-TITTEI");
        assertThat(institusjon.getLandkode()).isEqualTo("GR");
    }

    @Test
    void sedErEndring_medFlereConversations_forventTrue() {
        String sedID = "3333";
        String rinaSaksnummer = "333222111";
        BUC buc = lagBucMedDocument(rinaSaksnummer, sedID);
        buc.getDocuments().get(0).setConversations(Arrays.asList(new Conversation(), new Conversation()));
        when(euxConsumer.hentBUC(rinaSaksnummer)).thenReturn(buc);

        boolean erEndring = euxService.sedErEndring(sedID, rinaSaksnummer);

        verify(euxConsumer).hentBUC(rinaSaksnummer);
        assertThat(erEndring).isTrue();
    }

    @Test
    void sedErEndring_utenNoenConversations_forventFalse() {
        final String sedID = "3556";
        final String rinaSaksnummer = "54368";
        BUC buc = lagBucMedDocument(rinaSaksnummer, sedID);
        buc.getDocuments().get(0).setConversations(Collections.singletonList(new Conversation()));
        when(euxConsumer.hentBUC(rinaSaksnummer)).thenReturn(buc);

        boolean erEndring = euxService.sedErEndring(sedID, rinaSaksnummer);

        verify(euxConsumer).hentBUC(rinaSaksnummer);
        assertThat(erEndring).isFalse();
    }

    @Test
    void sedErEndring_utenSederForBuc_forventFalse() {
        final String sedID = "33322";
        BUC buc = new BUC();
        Document document = new Document();
        document.setId(sedID);
        document.setConversations(Collections.singletonList(new Conversation()));
        buc.setDocuments(Arrays.asList(document, document, document));

        when(euxConsumer.hentBUC(anyString())).thenReturn(buc);

        boolean erEndring = euxService.sedErEndring(sedID, "123");
        verify(euxConsumer).hentBUC("123");
        assertThat(erEndring).isFalse();
    }


    @SneakyThrows
    private void mockInstitusjoner() {
        URL institusjonerJsonUrl = getClass().getClassLoader().getResource("institusjoner.json");
        List<Institusjon> institusjoner = objectMapper.readValue(institusjonerJsonUrl, new TypeReference<>() {
        });
        when(euxConsumer.hentInstitusjoner(anyString(), any()))
            .thenReturn(institusjoner);
    }

    private BUC lagBucMedDocument(String rinaSaksnummer, String sedID) {

        BUC buc = new BUC();
        buc.setId(rinaSaksnummer);
        Document document = new Document();
        document.setId(sedID);
        document.setConversations(Collections.singletonList(new Conversation()));
        buc.setDocuments(Collections.singletonList(document));

        return buc;
    }
}
