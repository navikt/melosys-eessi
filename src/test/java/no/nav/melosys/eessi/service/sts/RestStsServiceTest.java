package no.nav.melosys.eessi.service.sts;

import com.google.common.collect.Maps;
import java.util.Map;
import no.nav.melosys.eessi.config.EnvironmentHandler;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestStsServiceTest {

  private RestStsService restStsService;

  @Mock
  private RestTemplate restTemplate;

  @Before
  public void setUp() {
    restStsService = spy(new RestStsService(restTemplate));

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
    verify(restStsService, times(1)).basicAuth();
    assertNotNull(token);
    assertFalse(token.isEmpty());

    body.put("access_token", "cba321");
    body.put("expires_in", 3600L);

    String secondToken = restStsService.collectToken();
    verify(restStsService, times(2)).basicAuth();
    assertNotEquals(token, secondToken);

    body.put("access_token", "abccba");

    String thirdToken = restStsService.collectToken();
    verify(restStsService, times(2)).basicAuth();
    assertEquals(secondToken, thirdToken);
  }
}