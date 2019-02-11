package no.nav.melosys.eessi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentHandler {

    private static class EnvironmentHandlerHolder {

        private static EnvironmentHandler ENV_HANDLER = null;
    }

    private final Environment env;

    @Autowired
    public EnvironmentHandler(Environment environment) {
        this.env = environment;
        EnvironmentHandlerHolder.ENV_HANDLER = this;
    }

    public Environment getEnv() {
        return env;
    }

    public static EnvironmentHandler getInstance() {
        return EnvironmentHandlerHolder.ENV_HANDLER;
    }
}
