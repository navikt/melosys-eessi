package no.nav.melosys.eessi;

import no.nav.melosys.eessi.config.AppCredentials;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableConfigurationProperties(AppCredentials.class)
@EnableJwtTokenValidation(ignore={"org.springframework", "springfox.documentation"})
@EnableRetry
public class MelosysEessiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MelosysEessiApplication.class, args);
    }

}

