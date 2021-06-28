package no.nav.melosys.eessi.service.sts;

import java.util.Map;

import com.google.common.collect.Maps;
import no.nav.melosys.eessi.security.BasicAuthClientRequestInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestStsServiceTest {

    private RestStsService restStsService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BasicAuthClientRequestInterceptor basicAuthClientRequestInterceptor;

    @BeforeEach
    public void setUp() {
        restStsService = spy(new RestStsService(restTemplate, basicAuthClientRequestInterceptor));
    }

    //Tester at token blir hentet på nytt ved kort expires_in, og ikke ved lengre expires_in
    @Test
    @SuppressWarnings("unchecked")
    void testCollectToken() {

        Map<String, Object> body = Maps.newHashMap();
        body.put("access_token", "123abc");
        body.put("expires_in", 30L);

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(
                ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        String token = restStsService.collectToken();
        verify(restTemplate, times(1))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        assertThat(token).isNotEmpty();

        body.put("access_token", "cba321");
        body.put("expires_in", 3600L);

        String secondToken = restStsService.collectToken();
        verify(restTemplate, times(2))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        assertThat(token).isNotEqualTo(secondToken);

        body.put("access_token", "abccba");

        String thirdToken = restStsService.collectToken();
        verify(restTemplate, times(2))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        assertThat(secondToken).isEqualTo(thirdToken);
    }
}
