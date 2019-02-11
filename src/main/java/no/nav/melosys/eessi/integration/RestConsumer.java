package no.nav.melosys.eessi.integration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import no.nav.melosys.eessi.config.EnvironmentHandler;
import org.springframework.core.env.Environment;

public interface RestConsumer {

    String SYSTEM_USERNAME = "melosys.systemuser.username";
    String SYSTEM_PASSWORD = "melosys.systemuser.password";

    default String basicAuth() {
        return "Basic " + Base64.getEncoder().encodeToString(
                String.format("%s:%s", getEnv().getRequiredProperty(SYSTEM_USERNAME),
                        getEnv().getRequiredProperty(SYSTEM_PASSWORD))
                        .getBytes(StandardCharsets.UTF_8));
    }

    default Environment getEnv() {
        return EnvironmentHandler.getInstance().getEnv();
    }
}
