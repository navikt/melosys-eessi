package no.nav.melosys.eessi.integration.dokkat;

import no.nav.melosys.eessi.integration.dokkat.dto.DokumenttypeIdDto;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class DokumenttypeIdConsumerTest {


    @Spy
    private RestTemplate restTemplate;

    @InjectMocks
    private DokumenttypeIdConsumer dokumenttypeIdConsumer;

    private MockRestServiceServer server;

    @Before
    public void setup() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void hentDokumenttypeId_expectValidJson() {

        String responseJson = "{\"dokumenttypeId\":\"123\"}";
        server.expect(requestTo("/sed/type"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        DokumenttypeIdDto dokumenttypeIdDto = dokumenttypeIdConsumer.hentDokumenttypeId("sed", "type");
        assertThat(dokumenttypeIdDto).isNotNull();
        assertThat(dokumenttypeIdDto.getDokumenttypeId()).isEqualTo("123");
    }

    @Test(expected = IntegrationException.class)
    public void hentDokumenttypeId_expectException() {
        server.expect(requestTo("/sed/type"))
                .andRespond(withServerError());
        dokumenttypeIdConsumer.hentDokumenttypeId("sed", "type");
    }
}
