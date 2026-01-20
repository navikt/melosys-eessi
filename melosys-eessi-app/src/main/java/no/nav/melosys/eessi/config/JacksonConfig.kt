package no.nav.melosys.eessi.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.cfg.DateTimeFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.KotlinModule

@Configuration
class JacksonConfig {

    /**
     * Hoved JsonMapper med KotlinModule og NullIsSameAsDefault.
     */
    @Bean
    @Primary
    fun jsonMapper(): JsonMapper {
        return JsonMapper.builder()
            .addModule(
                KotlinModule.Builder()
                    .enable(KotlinFeature.NullIsSameAsDefault)
                    .build()
            )
            .build()
    }

    /**
     * JsonMapper konfigurert for EUX/RINA API-integrasjoner.
     * Brukes av EuxConsumer, EuxRinasakerConsumer og EuxKotlinConsumer.
     * - FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY disabled: For SED'er uten 'medlemskap' objekt (f.eks. X001)
     * - WRITE_DATES_AS_TIMESTAMPS enabled: For riktig datoformat mot EUX API
     * 
     * Merk: Krever eksplisitt @Qualifier("euxJsonMapper") for å unngå utilsiktet bruk.
     */
    @Bean
    @Qualifier("euxJsonMapper")
    fun euxJsonMapper(jsonMapper: JsonMapper): JsonMapper {
        return jsonMapper.rebuild()
            .disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
    }
}
