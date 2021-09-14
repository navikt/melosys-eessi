package no.nav.melosys.eessi.config;

import java.util.Map;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.strategy.Strategy;
import no.finn.unleash.util.UnleashConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FeaturetoggleConfig {

    @Bean
    public Unleash unleash(Environment environment) {
        var unleashConfig = UnleashConfig.builder()
                .appName("melosys")
                .unleashAPI("https://unleash.nais.io/api/")
                .build();

        return new DefaultUnleash(
                unleashConfig,
                new IsTestStrategy(environment.getProperty("APP_ENVIRONMENT"))
        );
    }

    static class IsTestStrategy implements Strategy {

        private final String env;

        IsTestStrategy(String env) {
            this.env = env;
        }

        @Override
        public String getName() {
            return "isTest";
        }

        @Override
        public boolean isEnabled(Map<String, String> map) {
            return "dev".equalsIgnoreCase(env);
        }
    }
}
