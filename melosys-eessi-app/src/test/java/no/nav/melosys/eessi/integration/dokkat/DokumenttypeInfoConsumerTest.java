package no.nav.melosys.eessi.integration.dokkat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.dokkat.api.tkat020.v4.DokumentTypeInfoToV4;
import no.nav.melosys.eessi.EnhancedRandomCreator;
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
public class DokumenttypeInfoConsumerTest {

    @Spy
    private RestTemplate restTemplate;

    @InjectMocks
    private DokumenttypeInfoConsumer dokumenttypeInfoConsumer;

    private MockRestServiceServer server;

    private EnhancedRandom enhancedRandom;

    private final String dokumentyypeId = "123";

    @Before
    public void setup() {
        server = MockRestServiceServer.createServer(restTemplate);
        enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();
    }

    @Test
    public void hentDokumenttypeInfo_expectValidJson() throws Exception {
        DokumentTypeInfoToV4 dokumentTypeInfoToV4 = enhancedRandom.nextObject(DokumentTypeInfoToV4.class);
        String responseJson = new ObjectMapper().writeValueAsString(dokumentTypeInfoToV4);

        server.expect(requestTo("/" + dokumentyypeId))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        DokumentTypeInfoToV4 responseObject = dokumenttypeInfoConsumer.hentDokumenttypeInfo(dokumentyypeId);

        assertThat(responseObject).isNotNull();
        assertThat(responseObject.getBehandlingstema()).isEqualTo(dokumentTypeInfoToV4.getBehandlingstema());
        assertThat(responseObject.getTema()).isEqualTo(dokumentTypeInfoToV4.getTema());
        assertThat(responseObject.getDokumentKategori()).isEqualTo(dokumentTypeInfoToV4.getDokumentKategori());
    }

    @Test(expected = IntegrationException.class)
    public void hentDokumenttypeId_expectException() throws Exception {
        server.expect(requestTo("/" + dokumentyypeId))
                .andRespond(withServerError());
        dokumenttypeInfoConsumer.hentDokumenttypeInfo(dokumentyypeId);
    }
}