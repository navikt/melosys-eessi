package no.nav.melosys.eessi.config;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableConfigurationProperties(AppCredentials.class)
@EnableJwtTokenValidation(ignore={"org.springframework", "springfox.documentation"})
@EnableRetry
@EnableJpaAuditing
public class ApplicationConfig {
}
