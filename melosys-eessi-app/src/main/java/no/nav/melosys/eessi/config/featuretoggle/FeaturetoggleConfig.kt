package no.nav.melosys.eessi.config.featuretoggle

import io.getunleash.DefaultUnleash
import io.getunleash.FakeUnleash
import io.getunleash.Unleash
import io.getunleash.util.UnleashConfig
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

private val log = KotlinLogging.logger {}

@Configuration
class FeatureToggleConfig {

    private val APP_NAME = "Melosys-eessi"

    @Bean
    @Profile("nais", "local-mock", "local-rina")
    fun unleashConfig(
        @Value("\${unleash.url}") url: String,
        @Value("\${unleash.token}") token: String
    ): UnleashConfig {
        log.info { "Configuring Unleash with URL: $url" }
        return UnleashConfig.builder()
            .apiKey(token)
            .appName(APP_NAME)
            .unleashAPI(url)
            .build()
    }

    @Bean
    @Profile("nais")
    fun unleash(config: UnleashConfig): Unleash {
        log.info { "Creating DefaultUnleash for nais profile" }
        return DefaultUnleash(config)
    }

    @Bean
    @Profile("local-mock", "local-rina")
    fun localUnleash(config: UnleashConfig): Unleash {
        log.info { "Creating DefaultEnabledUnleash for local-mock profile" }
        return DefaultEnabledUnleash(DefaultUnleash(config))
    }

    @Bean
    @Profile("local", "local-q2")
    fun fakeUnleash(): Unleash {
        log.info { "Creating FakeUnleash with all toggles enabled" }
        return FakeUnleash().apply { enableAll() }
    }
}
