package no.nav.melosys.eessi.security;

import java.util.HashMap;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.policy.EndpointPolicy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.ReferenceResolver;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;

public class StsConfigUtil {

    private StsConfigUtil() {}

    private static final String STS_REQUEST_SAML_POLICY = "classpath:soap/policy/stsPolicy.xml";
    private static final String STS_CLIENT_AUTHENTICATION_POLICY = "classpath:soap/policy/untPolicy.xml";

    public static void configureStsRequestSamlToken(Client client, String stsUrl, String username, String password) {

        STSClient stsClient = new STSClient(client.getBus());
        configureSTSClient(stsClient, stsUrl, username, password);

        client.getRequestContext().put(SecurityConstants.STS_CLIENT, stsClient);
        client.getRequestContext().put(SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT, true);

        setClientEndpointPolicy(client, resolvePolicyReference(client));
    }

    private static void configureSTSClient(STSClient stsClient, String location, String username, String password) {

        stsClient.setEnableAppliesTo(false);
        stsClient.setAllowRenewing(false);
        stsClient.setLocation(location);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put(SecurityConstants.USERNAME, username);
        properties.put(SecurityConstants.PASSWORD, password);

        stsClient.setProperties(properties);

        stsClient.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY);
    }

    private static Policy resolvePolicyReference(Client client) {
        PolicyBuilder policyBuilder = client.getBus().getExtension(PolicyBuilder.class);
        ReferenceResolver resolver = new RemoteReferenceResolver("", policyBuilder);
        return resolver.resolveReference(STS_REQUEST_SAML_POLICY);
    }

    private static void setClientEndpointPolicy(Client client, Policy policy) {
        Endpoint endpoint = client.getEndpoint();
        EndpointInfo endpointInfo = endpoint.getEndpointInfo();

        PolicyEngine policyEngine = client.getBus().getExtension(PolicyEngine.class);
        SoapMessage message = new SoapMessage(Soap12.getInstance());
        EndpointPolicy endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message);
        policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message));
    }
}
