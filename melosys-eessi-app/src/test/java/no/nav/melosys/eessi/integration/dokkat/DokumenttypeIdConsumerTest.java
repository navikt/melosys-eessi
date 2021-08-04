package no.nav.melosys.eessi.integration.dokkat;

import no.nav.melosys.eessi.integration.dokkat.dto.DokumenttypeIdDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class DokumenttypeIdConsumerTest {


    @Spy
    private RestTemplate restTemplate;

    private DokumenttypeIdConsumer dokumenttypeIdConsumer;

    private MockRestServiceServer server;

    @BeforeEach
    public void setup() {
        dokumenttypeIdConsumer = new DokumenttypeIdConsumer(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void hentDokumenttypeId_expectValidJson() {

        String responseJson = "{\"dokumenttypeId\":\"123\"}";
        server.expect(requestTo("/sed/type"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        DokumenttypeIdDto dokumenttypeIdDto = dokumenttypeIdConsumer.hentDokumenttypeId("sed", "type");
        assertThat(dokumenttypeIdDto).isNotNull();
        assertThat(dokumenttypeIdDto.getDokumenttypeId()).isEqualTo("123");
    }

    @Test
    void hentDokumenttypeId_expectException() {
        server.expect(requestTo("/sed/type"))
                .andRespond(withServerError());
        assertThatExceptionOfType(IntegrationException.class)
                .isThrownBy(() -> dokumenttypeIdConsumer.hentDokumenttypeId("sed", "type"))
                .withMessageContaining("Feil ved integrasjon mot dokkat");
    }
}
