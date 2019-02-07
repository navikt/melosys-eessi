package no.nav.melosys.eessi.integration.eux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EuxConsumerConfig {

  private final String uri;

  public EuxConsumerConfig(@Value("${melosys.integrations.euxapp-url}") String uri) {
    this.uri = uri;
  }

  @Bean(name = "euxRestTemplate")
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .rootUri(uri)
        .build();
  }
}
