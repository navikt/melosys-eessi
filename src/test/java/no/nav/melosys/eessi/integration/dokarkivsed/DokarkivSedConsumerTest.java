package no.nav.melosys.eessi.integration.dokarkivsed;


import java.util.Collections;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dokarkivsed.api.v1.ArkivSak;
import no.nav.dokarkivsed.api.v1.ArkiverUtgaaendeSed;
import no.nav.dokarkivsed.api.v1.DokumentInfoHoveddokument;
import no.nav.dokarkivsed.api.v1.ForsendelsesInformasjon;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class DokarkivSedConsumerTest {


    private RestTemplate restTemplate = new RestTemplate();
    private MockRestServiceServer server;
    private DokarkivSedConsumer dokarkivSedConsumer;

    @Before
    public void setup() {
        dokarkivSedConsumer = new DokarkivSedConsumer(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void arkiverSed_verifiserKallMotDokarkivSed() throws Exception {

        OpprettUtgaaendeJournalpostResponse responseObj = lagResponse();

        server.expect(requestTo("/dokarkivsed"))
                .andRespond(
                        withSuccess(new ObjectMapper().writeValueAsString(responseObj), MediaType.APPLICATION_JSON));

        ArkiverUtgaaendeSed arkiverUtgaaendeSed = lagRequest();

        OpprettUtgaaendeJournalpostResponse response = dokarkivSedConsumer.create(arkiverUtgaaendeSed);

        assertThat(response).isEqualToComparingFieldByField(responseObj);
    }

    @Test(expected = IntegrationException.class)
    public void arkiverSed_kasterException_verifiserIntegrationException() throws Exception {

        server.expect(requestTo("/dokarkivsed"))
                .andRespond(withBadRequest());

        dokarkivSedConsumer.create(lagRequest());
    }

    private ArkiverUtgaaendeSed lagRequest() {
        return ArkiverUtgaaendeSed.builder()
                .dokumentInfoHoveddokument(
                        DokumentInfoHoveddokument.builder()
                                .sedType("")
                                .filinfoListe(Collections.emptyList()).build()
                )
                .forsendelsesinformasjon(
                        ForsendelsesInformasjon.builder().kanalreferanseId("")
                                .arkivSak(
                                        ArkivSak.builder().arkivSakSystem("").arkivSakId("32").build()
                                ).build()
                ).build();
    }

    private OpprettUtgaaendeJournalpostResponse lagResponse() {
        OpprettUtgaaendeJournalpostResponse response = new OpprettUtgaaendeJournalpostResponse();
        response.setJournalfoeringStatus(OpprettUtgaaendeJournalpostResponse.JournalTilstand.ENDELIG_JOURNALFOERT);
        response.setJournalpostId("1");
        response.setKanalreferanseId("kanal");
        return response;
    }
}