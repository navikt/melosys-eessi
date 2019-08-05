package no.nav.melosys.eessi.security;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.security.SecurityConstants;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StsConfigUtilTest {

    @Test
    public void configureStsRequestSamlToken() {
        Endpoint endpoint = mock(Endpoint.class);
        when(endpoint.getEndpointInfo()).thenReturn(new EndpointInfo());
        Bus bus = BusFactory.getDefaultBus();
        Client client = new ClientImpl(bus, endpoint);

        String stsUrl = "https://some-sts.provider/with/url/path";
        String username = "user";
        String password = "pass";

        StsConfigUtil.configureStsRequestSamlToken(client, stsUrl, username, password);

        assertThat(client.getRequestContext().get(SecurityConstants.STS_CLIENT), not(nullValue()));
    }
}