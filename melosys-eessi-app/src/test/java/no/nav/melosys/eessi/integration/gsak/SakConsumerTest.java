package no.nav.melosys.eessi.integration.gsak;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.integration.gsak.sak.SakConsumer;
import no.nav.melosys.eessi.integration.gsak.sak.SakDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class SakConsumerTest {

    private SakConsumer sakConsumer;

    @Spy
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @Before
    public void setup() {
        sakConsumer = new SakConsumer(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void getSak_expectSak() throws Exception {
        String responseJson = "{\"id\":\"11\",\"tema\":\"MED\",\"applikasjon\":\"melsoys\","
                + "\"aktoerId\":\"123\",\"orgnr\":\"12312312\",\"fagsakNr\":\"fag123\","
                + "\"opprettetAv\":\"srvmelosys\",\"opprettetTidspunkt\":\"2019-02-11T08:33:38.964Z\"}";
        long sakId = 11L;
        server.expect(requestTo("/" + sakId))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        Sak response = sakConsumer.getSak(sakId);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(Long.toString(sakId));

    }

    @Test
    public void createSak_expectSak() throws Exception {
        String responseJson = "{\"id\":\"11\",\"tema\":\"MED\",\"applikasjon\":\"melsoys\","
                + "\"aktoerId\":\"123\",\"orgnr\":\"12312312\",\"fagsakNr\":\"fag123\","
                + "\"opprettetAv\":\"srvmelosys\",\"opprettetTidspunkt\":\"2019-02-11T08:33:38.964Z\"}";
        long sakId = 11L;

        SakDto expectedSakDto = new SakDto();
        expectedSakDto.setAktoerId("123");
        expectedSakDto.setApplikasjon("FS38");
        expectedSakDto.setTema("MED");
        String expectedSakDtoJson = new ObjectMapper().writeValueAsString(expectedSakDto);

        server.expect(requestTo("/"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(expectedSakDtoJson, true))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        Sak response = sakConsumer.opprettSak("123");
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(Long.toString(sakId));
    }
}