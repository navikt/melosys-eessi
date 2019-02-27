package no.nav.melosys.eessi.service.sts;

import java.util.Map;
import com.google.common.collect.Maps;
import no.nav.melosys.eessi.config.EnvironmentHandler;
import no.nav.melosys.eessi.security.BasicAuthClientRequestInterceptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.client.RestTemplate;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestStsServiceTest {

    private RestStsService restStsService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor;

    @Before
    public void setUp() {
        restStsService = spy(new RestStsService(restTemplate, basicAuthClientRequestInterceptor));

        // Setter environment som "singleton"
        MockEnvironment environment = spy(new MockEnvironment());
        environment.setProperty("melosys.systemuser.username", "test");
        environment.setProperty("melosys.systemuser.password", "test");
        new EnvironmentHandler(environment);
    }

    //Tester at token blir hentet på nytt ved kort expires_in, og ikke ved lengre expires_in
    @Test
    @SuppressWarnings("unchecked")
    public void testCollectToken() throws Exception {

        Map<String, Object> body = Maps.newHashMap();
        body.put("access_token", "123abc");
        body.put("expires_in", 30L);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(
                ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        String token = restStsService.collectToken();
        verify(restTemplate, times(1))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        assertNotNull(token);
        assertFalse(token.isEmpty());

        body.put("access_token", "cba321");
        body.put("expires_in", 3600L);

        String secondToken = restStsService.collectToken();
        verify(restTemplate, times(2))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        assertNotEquals(token, secondToken);

        body.put("access_token", "abccba");

        String thirdToken = restStsService.collectToken();
        verify(restTemplate, times(2))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        assertEquals(secondToken, thirdToken);
    }
}