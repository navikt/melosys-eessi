package no.nav.melosys.eessi.integration.aad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.net.URISyntaxException;

public class WebClientProxyConfig {
    private final Logger log = LoggerFactory.getLogger(WebClientProxyConfig.class);

    public ReactorClientHttpConnector clientHttpConnector(String proxyUri) throws URISyntaxException {
        var uri = new URI(proxyUri);
        log.info("URI: Host: '{}'. Port: {}.", uri.getHost(), uri.getPort());

        var httpClient = HttpClient
            .create()
            .proxy(p -> proxySpec(p, uri.getHost(), uri.getPort()));

        return new ReactorClientHttpConnector(httpClient);
    }

    private ProxyProvider.Builder proxySpec(ProxyProvider.TypeSpec proxy, String host, int port) {
        return proxy
            .type(ProxyProvider.Proxy.HTTP)
            .host(host)
            .port(port);
    }
}
