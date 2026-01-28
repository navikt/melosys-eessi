package no.nav.melosys.eessi.config.featuretoggle;

import io.getunleash.DefaultUnleash;
import io.getunleash.FakeUnleash;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
public class FeaturetoggleConfig {

    private static final String APP_NAME = "Melosys-eessi";

    @Bean
    @Profile({"nais", "local-mock"})
    public UnleashConfig unleashConfig(@Value("${unleash.url}") String url,
                                        @Value("${unleash.token}") String token) {
        log.info("Configuring Unleash with URL: {}", url);
        return UnleashConfig.builder()
            .apiKey(token)
            .appName(APP_NAME)
            .unleashAPI(url)
            .build();
    }

    @Bean
    @Profile("nais")
    public Unleash unleash(UnleashConfig config) {
        log.info("Creating DefaultUnleash for nais profile");
        return new DefaultUnleash(config);
    }

    @Bean
    @Profile("local-mock")
    public Unleash localUnleash(UnleashConfig config) {
        log.info("Creating DefaultEnabledUnleash for local-mock profile");
        return new DefaultEnabledUnleash(new DefaultUnleash(config));
    }

    @Bean
    @Profile({"local", "local-q2", "test"})
    public Unleash fakeUnleash() {
        log.info("Creating FakeUnleash with all toggles enabled");
        var fakeUnleash = new FakeUnleash();
        fakeUnleash.enableAll();
        return fakeUnleash;
    }
}
