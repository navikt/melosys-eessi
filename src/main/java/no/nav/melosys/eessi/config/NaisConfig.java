package no.nav.melosys.eessi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("nais")
@Configuration
@PropertySource("file:/var/run/secrets/nais.io/vault/secrets.properties")
public class NaisConfig {}