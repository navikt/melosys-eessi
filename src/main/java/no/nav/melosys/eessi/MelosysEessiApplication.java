package no.nav.melosys.eessi;

import no.nav.melosys.eessi.config.AppCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppCredentials.class)
public class MelosysEessiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MelosysEessiApplication.class, args);
    }

}

